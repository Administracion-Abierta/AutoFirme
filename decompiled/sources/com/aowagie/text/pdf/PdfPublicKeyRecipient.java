package com.aowagie.text.pdf;

import java.security.cert.Certificate;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfPublicKeyRecipient.class */
class PdfPublicKeyRecipient {
    private Certificate certificate;
    private int permission;
    private byte[] cms = null;

    public PdfPublicKeyRecipient(Certificate certificate, int permission) {
        this.certificate = null;
        this.permission = 0;
        this.certificate = certificate;
        this.permission = permission;
    }

    public Certificate getCertificate() {
        return this.certificate;
    }

    public int getPermission() {
        return this.permission;
    }

    protected void setCms(byte[] cms) {
        this.cms = cms;
    }

    protected byte[] getCms() {
        return this.cms;
    }
}
