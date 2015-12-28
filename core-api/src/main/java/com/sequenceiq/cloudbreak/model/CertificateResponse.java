package com.sequenceiq.cloudbreak.model;


import com.sequenceiq.cloudbreak.doc.ModelDescriptions.StackModelDescription;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class CertificateResponse {

    @ApiModelProperty(value = StackModelDescription.CERTIFICATE)
    private byte[] certificate;

    public CertificateResponse(byte[] certificate) {
        this.certificate = certificate;
    }

    public byte[] getCertificate() {
        return certificate;
    }

    public void setCertificate(byte[] certificate) {
        this.certificate = certificate;
    }
}
