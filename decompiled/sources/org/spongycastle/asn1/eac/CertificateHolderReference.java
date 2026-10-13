package org.spongycastle.asn1.eac;

import java.io.UnsupportedEncodingException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/eac/CertificateHolderReference.class */
public class CertificateHolderReference {
    private static final String ReferenceEncoding = "ISO-8859-1";
    private String countryCode;
    private String holderMnemonic;
    private String sequenceNumber;

    public CertificateHolderReference(String countryCode, String holderMnemonic, String sequenceNumber) {
        this.countryCode = countryCode;
        this.holderMnemonic = holderMnemonic;
        this.sequenceNumber = sequenceNumber;
    }

    CertificateHolderReference(byte[] contents) {
        try {
            String concat = new String(contents, "ISO-8859-1");
            this.countryCode = concat.substring(0, 2);
            this.holderMnemonic = concat.substring(2, concat.length() - 5);
            this.sequenceNumber = concat.substring(concat.length() - 5);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.toString());
        }
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public String getHolderMnemonic() {
        return this.holderMnemonic;
    }

    public String getSequenceNumber() {
        return this.sequenceNumber;
    }

    public byte[] getEncoded() {
        String ref = this.countryCode + this.holderMnemonic + this.sequenceNumber;
        try {
            return ref.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.toString());
        }
    }
}
