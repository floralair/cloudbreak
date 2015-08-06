package com.sequenceiq.cloudbreak.cloud.model;

public class CloudVmInstanceStatus {

    private CloudInstance cloudInstance;

    private InstanceStatus status;

    private String statusReason;

    public CloudVmInstanceStatus(CloudInstance cloudInstance, InstanceStatus status, String statusReason) {
        this.cloudInstance = cloudInstance;
        this.status = status;
        this.statusReason = statusReason;
    }

    public CloudVmInstanceStatus(CloudInstance cloudInstance, InstanceStatus status) {
        this.cloudInstance = cloudInstance;
        this.status = status;
    }

    public CloudInstance getCloudInstance() {
        return cloudInstance;
    }

    public InstanceStatus getStatus() {
        return status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    //BEGIN GENERATED CODE
    @Override
    public String toString() {
        return "CloudVmInstanceStatus{" +
                "instance=" + cloudInstance +
                ", status=" + status +
                ", statusReason='" + statusReason + '\'' +
                '}';
    }
    //BEGIN GENERATED CODE
}
