package org.spongycastle.cms;

import java.math.BigInteger;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.cert.selector.X509CertificateHolderSelector;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/KeyAgreeRecipientId.class */
public class KeyAgreeRecipientId extends RecipientId {
    private X509CertificateHolderSelector baseSelector;

    private KeyAgreeRecipientId(X509CertificateHolderSelector baseSelector) {
        super(2);
        this.baseSelector = baseSelector;
    }

    public KeyAgreeRecipientId(byte[] subjectKeyId) {
        this(null, null, subjectKeyId);
    }

    public KeyAgreeRecipientId(X500Name issuer, BigInteger serialNumber) {
        this(issuer, serialNumber, null);
    }

    public KeyAgreeRecipientId(X500Name issuer, BigInteger serialNumber, byte[] subjectKeyId) {
        this(new X509CertificateHolderSelector(issuer, serialNumber, subjectKeyId));
    }

    public BigInteger getSerialNumber() {
        return this.baseSelector.getSerialNumber();
    }

    public byte[] getSubjectKeyIdentifier() {
        return this.baseSelector.getSubjectKeyIdentifier();
    }

    public int hashCode() {
        return this.baseSelector.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof KeyAgreeRecipientId)) {
            return false;
        }
        KeyAgreeRecipientId id = (KeyAgreeRecipientId) o;
        return this.baseSelector.equals(id.baseSelector);
    }

    @Override // org.spongycastle.cms.RecipientId, org.spongycastle.util.Selector
    public Object clone() {
        return new KeyAgreeRecipientId(this.baseSelector);
    }

    @Override // org.spongycastle.util.Selector
    public boolean match(Object obj) {
        if (obj instanceof KeyAgreeRecipientInformation) {
            return ((KeyAgreeRecipientInformation) obj).getRID().equals(this);
        }
        return this.baseSelector.match(obj);
    }
}
