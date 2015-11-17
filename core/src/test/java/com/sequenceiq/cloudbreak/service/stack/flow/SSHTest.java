package com.sequenceiq.cloudbreak.service.stack.flow;

import java.util.Collections;

import org.junit.Test;

import com.sequenceiq.cloudbreak.common.type.CloudPlatform;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;

/**
 * Created by fschneider on 11/17/15.
 */
public class SSHTest {
    @Test
    public void testSSh() throws Exception {
        SSHClient ssh = new SSHClient();
        String privateKeyLocation = "/Users/fschneider/credentials/seqiq.pem";
        HostKeyVerifier hostKeyVerifier = new VerboseHostKeyVerifier(Collections.<String>singleton("53:ae:db:ed:8f:2d:02:d4:d5:6c:24:bc:a4:66:88:79"),
                CloudPlatform.GCP);
        ssh.addHostKeyVerifier(hostKeyVerifier);
        ssh.connect("104.155.104.50", TlsSetupService.SSH_PORT);
        ssh.authPublickey("cloudbreak", privateKeyLocation);
    }
}
