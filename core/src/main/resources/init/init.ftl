#!/bin/bash
set -x

START_LABEL=${platformDiskStartLabel}
PLATFORM_DISK_PREFIX=${platformDiskPrefix}

<#if gateway>
setup_tmp_ssh() {
  echo "#tmpssh_start" >> /home/${sshUser}/.ssh/authorized_keys
  echo "${tmpSshKey}" >> /home/${sshUser}/.ssh/authorized_keys
  echo "#tmpssh_end" >> /home/${sshUser}/.ssh/authorized_keys
}
</#if>

<#noparse>
get_ip() {
  ifconfig eth0 | awk '/inet addr/{print substr($2,6)}'
}

fix_hostname() {
  if grep -q $(get_ip) /etc/hosts ;then
    sed -i "/$(get_ip)/d" /etc/hosts
  else
    echo OK
  fi
}

extend_rootfs() {
  # Usable on GCP, does not harm anywhere else
  root_fs_device=$(mount | grep ' / ' | cut -d' ' -f 1 | sed s/1//g)
  growpart $root_fs_device 1
  xfs_growfs /
}

format_disks() {
  mkdir /hadoopfs
  for (( i=1; i<=24; i++ )); do
    LABEL=$(printf "\x$(printf %x $((START_LABEL+i)))")
    DEVICE=/dev/${PLATFORM_DISK_PREFIX}${LABEL}
    if [ -e $DEVICE ]; then
      MOUNTPOINT=$(grep $DEVICE /etc/fstab | tr -s ' \t' ' ' | cut -d' ' -f 2)
      if [ -n "$MOUNTPOINT" ]; then
        umount "$MOUNTPOINT"
        sed -i "\|^$DEVICE|d" /etc/fstab
      fi
      if [ $i -eq 1 ]; then
      	pvcreate $DEVICE && \
		vgcreate direct-lvm $DEVICE && \
		lvcreate -n data direct-lvm -L 20G  && \
		lvcreate -n metadata direct-lvm -L 10G  && \
		lvcreate -n hadoop direct-lvm -l 100%FREE  && \
		dd if=/dev/zero of=/dev/direct-lvm/metadata bs=1M count=10
		mkfs -E lazy_itable_init=1 -O uninit_bg -F -t ext4 /dev/direct-lvm/hadoop
        mkdir /hadoopfs/fs${i}
        echo /dev/direct-lvm/hadoop /hadoopfs/fs${i} ext4  defaults,noatime 0 2 >> /etc/fstab
        mount /hadoopfs/fs${i}
        sed -i '/^ExecStart/ s/$/ --storage-opt dm.datadev=\/dev\/direct-lvm\/data --storage-opt dm.metadatadev=\/dev\/direct-lvm\/metadata --storage-opt dm.fs=xfs --storage-opt dm.blocksize=512K/' /usr/lib/systemd/system/docker.service
        service docker stop
        rm -rf /var/lib/docker
        systemctl daemon-reload
        service docker start
        ls -1 /tmp/ | grep tar.gz | while read line; do echo "Import $line"; docker load -i /tmp/$line; done
	  else
	    mkfs -E lazy_itable_init=1 -O uninit_bg -F -t ext4 $DEVICE
        mkdir /hadoopfs/fs${i}
        echo $DEVICE /hadoopfs/fs${i} ext4  defaults,noatime 0 2 >> /etc/fstab
        mount /hadoopfs/fs${i}
      fi
    fi
  done
  cd /hadoopfs/fs1 && mkdir logs logs/ambari-server logs/ambari-agent logs/consul-watch logs/kerberos
}
</#noparse>

<#-- Workaround to reload /etc/sysctl.conf parameters at first boot. Without this the parameters cannot be set properly. -->
reload_sysconf() {
  sysctl -p
}

main() {
  reload_sysconf
  if [[ "$1" == "::" ]]; then
    shift
    eval "$@"
  elif [ ! -f "/var/cb-init-executed" ]; then
    <#if gateway>
    setup_tmp_ssh
    </#if>
    extend_rootfs
    format_disks
    fix_hostname
    touch /var/cb-init-executed
    echo $(date +%Y-%m-%d:%H:%M:%S) >> /var/cb-init-executed
  fi
}

[[ "$0" == "$BASH_SOURCE" ]] && main "$@"