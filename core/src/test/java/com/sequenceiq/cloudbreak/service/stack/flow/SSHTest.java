package com.sequenceiq.cloudbreak.service.stack.flow;

import java.io.File;
import java.util.Collections;

import org.junit.Test;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;
import com.sequenceiq.cloudbreak.common.type.CloudPlatform;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.Buffer;
import net.schmizz.sshj.common.SecurityUtils;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.userauth.keyprovider.OpenSSHKeyFile;

/**
 * Created by fschneider on 11/17/15.
 */
public class SSHTest {
    @Test
    public void testSSh() throws Exception {
        SSHClient ssh = new SSHClient();
        String privateKeyLocation = "/Users/msereg/.ssh/new_key.pem";
        HostKeyVerifier hostKeyVerifier = new VerboseHostKeyVerifier(Collections.<String>singleton("53:ae:db:ed:8f:2d:02:d4:d5:6c:24:bc:a4:66:88:79"),
//        HostKeyVerifier hostKeyVerifier = new VerboseHostKeyVerifier(Collections.<String>singleton("35:c6:35:91:2c:7a:8a:37:78:aa:18:5b:f7:18:cd:06"),
                CloudPlatform.GCP);
        ssh.addHostKeyVerifier(hostKeyVerifier);
        ssh.connect("104.155.104.50", TlsSetupService.SSH_PORT);
//        ssh.connect("146.148.30.168", TlsSetupService.SSH_PORT);
        ssh.authPublickey("cloudbreak", privateKeyLocation);
    }

    @Test
    public void testFingerprint() throws Exception {
        SSHClient ssh = new SSHClient();
        // load sshj keyfile
        OpenSSHKeyFile keyFile = new OpenSSHKeyFile();
        keyFile.init(new File("/tmp/test_ecdsa"));

// load jsch keypair
        KeyPair jschKeyPair = KeyPair.load(new JSch(), "/tmp/test_ecdsa", "/tmp/test_ecdsa.pub");

// print fingerprints
        System.out.println(SecurityUtils.getFingerprint(keyFile.getPublic()));
        System.out.println(jschKeyPair.getFingerPrint());
// compare it with `ssh-keygen -lf /tmp/test_ecdsa.pub` - it equals the jsch fingerprint

// compare public key bytes
        byte[] sshjbytes = new Buffer.PlainBuffer().putPublicKey(keyFile.getPublic()).getCompactData();
        byte[] jschbytes = jschKeyPair.getPublicKeyBlob();

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < jschbytes.length; i++) {
            if (i < sshjbytes.length && jschbytes[i] != sshjbytes[i]){
                result.append(i).append(" ")
                        .append(jschbytes[i]).append(" ").append(sshjbytes[i])
                        .append("\n");
            }
        }

        System.out.println(result.toString());
    }
}
