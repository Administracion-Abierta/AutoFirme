package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/PKIXRecipientId.class */
public class PKIXRecipientId extends RecipientId {
    protected final X509CertificateHolderSelector baseSelector;

    protected PKIXRecipientId(int i, X509CertificateHolderSelector x509CertificateHolderSelector) {
        super(i);
        this.baseSelector = x509CertificateHolderSelector;
    }

    protected PKIXRecipientId(int i, X500Name x500Name, BigInteger bigInteger, byte[] bArr) {
        this(i, new X509CertificateHolderSelector(x500Name, bigInteger, bArr));
    }

    public X500Name getIssuer() {
        return this.baseSelector.getIssuer();
    }

    public BigInteger getSerialNumber() {
        return this.baseSelector.getSerialNumber();
    }

    public byte[] getSubjectKeyIdentifier() {
        return this.baseSelector.getSubjectKeyIdentifier();
    }

    @Override // org.bouncycastle.cms.RecipientId, org.bouncycastle.util.Selector
    public Object clone() {
        return new PKIXRecipientId(getType(), this.baseSelector);
    }

    public int hashCode() {
        return this.baseSelector.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof PKIXRecipientId) {
            return this.baseSelector.equals(((PKIXRecipientId) obj).baseSelector);
        }
        return false;
    }

    @Override // org.bouncycastle.util.Selector
    public boolean match(Object obj) {
        return this.baseSelector.match(obj);
    }
}
