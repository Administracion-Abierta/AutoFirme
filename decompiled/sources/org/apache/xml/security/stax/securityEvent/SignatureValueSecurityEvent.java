package org.apache.xml.security.stax.securityEvent;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/securityEvent/SignatureValueSecurityEvent.class */
public class SignatureValueSecurityEvent extends SecurityEvent {
    private byte[] signatureValue;

    public SignatureValueSecurityEvent() {
        super(SecurityEventConstants.SignatureValue);
    }

    public byte[] getSignatureValue() {
        return this.signatureValue;
    }

    public void setSignatureValue(byte[] signatureValue) {
        this.signatureValue = signatureValue;
    }
}
