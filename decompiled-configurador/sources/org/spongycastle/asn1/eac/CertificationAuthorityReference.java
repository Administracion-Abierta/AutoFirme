package org.spongycastle.asn1.eac;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/eac/CertificationAuthorityReference.class */
public class CertificationAuthorityReference extends CertificateHolderReference {
    public CertificationAuthorityReference(String countryCode, String holderMnemonic, String sequenceNumber) {
        super(countryCode, holderMnemonic, sequenceNumber);
    }

    CertificationAuthorityReference(byte[] contents) {
        super(contents);
    }
}
