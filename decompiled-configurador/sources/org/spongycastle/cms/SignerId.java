package org.spongycastle.cms;

import java.math.BigInteger;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.cert.selector.X509CertificateHolderSelector;
import org.spongycastle.util.Selector;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/SignerId.class */
public class SignerId implements Selector {
    private X509CertificateHolderSelector baseSelector;

    private SignerId(X509CertificateHolderSelector baseSelector) {
        this.baseSelector = baseSelector;
    }

    public SignerId(byte[] subjectKeyId) {
        this(null, null, subjectKeyId);
    }

    public SignerId(X500Name issuer, BigInteger serialNumber) {
        this(issuer, serialNumber, null);
    }

    public SignerId(X500Name issuer, BigInteger serialNumber, byte[] subjectKeyId) {
        this(new X509CertificateHolderSelector(issuer, serialNumber, subjectKeyId));
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

    public int hashCode() {
        return this.baseSelector.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof SignerId)) {
            return false;
        }
        SignerId id = (SignerId) o;
        return this.baseSelector.equals(id.baseSelector);
    }

    @Override // org.spongycastle.util.Selector
    public boolean match(Object obj) {
        if (obj instanceof SignerInformation) {
            return ((SignerInformation) obj).getSID().equals(this);
        }
        return this.baseSelector.match(obj);
    }

    @Override // org.spongycastle.util.Selector
    public Object clone() {
        return new SignerId(this.baseSelector);
    }
}
