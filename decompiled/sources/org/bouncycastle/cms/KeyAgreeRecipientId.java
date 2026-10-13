package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/KeyAgreeRecipientId.class */
public class KeyAgreeRecipientId extends PKIXRecipientId {
    private KeyAgreeRecipientId(X509CertificateHolderSelector x509CertificateHolderSelector) {
        super(2, x509CertificateHolderSelector);
    }

    public KeyAgreeRecipientId(byte[] bArr) {
        super(2, null, null, bArr);
    }

    public KeyAgreeRecipientId(X500Name x500Name, BigInteger bigInteger) {
        super(2, x500Name, bigInteger, null);
    }

    public KeyAgreeRecipientId(X500Name x500Name, BigInteger bigInteger, byte[] bArr) {
        super(2, x500Name, bigInteger, bArr);
    }

    @Override // org.bouncycastle.cms.PKIXRecipientId
    public X500Name getIssuer() {
        return this.baseSelector.getIssuer();
    }

    @Override // org.bouncycastle.cms.PKIXRecipientId
    public BigInteger getSerialNumber() {
        return this.baseSelector.getSerialNumber();
    }

    @Override // org.bouncycastle.cms.PKIXRecipientId
    public byte[] getSubjectKeyIdentifier() {
        return this.baseSelector.getSubjectKeyIdentifier();
    }

    @Override // org.bouncycastle.cms.PKIXRecipientId
    public int hashCode() {
        return this.baseSelector.hashCode();
    }

    @Override // org.bouncycastle.cms.PKIXRecipientId
    public boolean equals(Object obj) {
        if (obj instanceof KeyAgreeRecipientId) {
            return this.baseSelector.equals(((KeyAgreeRecipientId) obj).baseSelector);
        }
        return false;
    }

    @Override // org.bouncycastle.cms.PKIXRecipientId, org.bouncycastle.cms.RecipientId, org.bouncycastle.util.Selector
    public Object clone() {
        return new KeyAgreeRecipientId(this.baseSelector);
    }

    @Override // org.bouncycastle.cms.PKIXRecipientId, org.bouncycastle.util.Selector
    public boolean match(Object obj) {
        return obj instanceof KeyAgreeRecipientInformation ? ((KeyAgreeRecipientInformation) obj).getRID().equals(this) : this.baseSelector.match(obj);
    }
}
