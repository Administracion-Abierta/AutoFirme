package org.spongycastle.cert.selector;

import java.math.BigInteger;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.cms.IssuerAndSerialNumber;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Selector;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/selector/X509CertificateHolderSelector.class */
public class X509CertificateHolderSelector implements Selector {
    private byte[] subjectKeyId;
    private X500Name issuer;
    private BigInteger serialNumber;

    public X509CertificateHolderSelector(byte[] subjectKeyId) {
        this(null, null, subjectKeyId);
    }

    public X509CertificateHolderSelector(X500Name issuer, BigInteger serialNumber) {
        this(issuer, serialNumber, null);
    }

    public X509CertificateHolderSelector(X500Name issuer, BigInteger serialNumber, byte[] subjectKeyId) {
        this.issuer = issuer;
        this.serialNumber = serialNumber;
        this.subjectKeyId = subjectKeyId;
    }

    public X500Name getIssuer() {
        return this.issuer;
    }

    public BigInteger getSerialNumber() {
        return this.serialNumber;
    }

    public byte[] getSubjectKeyIdentifier() {
        return Arrays.clone(this.subjectKeyId);
    }

    public int hashCode() {
        int code = Arrays.hashCode(this.subjectKeyId);
        if (this.serialNumber != null) {
            code ^= this.serialNumber.hashCode();
        }
        if (this.issuer != null) {
            code ^= this.issuer.hashCode();
        }
        return code;
    }

    public boolean equals(Object o) {
        if (!(o instanceof X509CertificateHolderSelector)) {
            return false;
        }
        X509CertificateHolderSelector id = (X509CertificateHolderSelector) o;
        return Arrays.areEqual(this.subjectKeyId, id.subjectKeyId) && equalsObj(this.serialNumber, id.serialNumber) && equalsObj(this.issuer, id.issuer);
    }

    private boolean equalsObj(Object a, Object b) {
        if (a != null) {
            return a.equals(b);
        }
        return b == null;
    }

    @Override // org.spongycastle.util.Selector
    public boolean match(Object obj) {
        if (obj instanceof X509CertificateHolder) {
            X509CertificateHolder certHldr = (X509CertificateHolder) obj;
            if (getSerialNumber() != null) {
                IssuerAndSerialNumber iAndS = new IssuerAndSerialNumber(certHldr.toASN1Structure());
                return iAndS.getName().equals(this.issuer) && iAndS.getSerialNumber().getValue().equals(this.serialNumber);
            }
            if (this.subjectKeyId != null) {
                Extension ext = certHldr.getExtension(Extension.subjectKeyIdentifier);
                if (ext == null) {
                    return Arrays.areEqual(this.subjectKeyId, MSOutlookKeyIdCalculator.calculateKeyId(certHldr.getSubjectPublicKeyInfo()));
                }
                byte[] subKeyID = ASN1OctetString.getInstance(ext.getParsedValue()).getOctets();
                return Arrays.areEqual(this.subjectKeyId, subKeyID);
            }
            return false;
        }
        if (obj instanceof byte[]) {
            return Arrays.areEqual(this.subjectKeyId, (byte[]) obj);
        }
        return false;
    }

    @Override // org.spongycastle.util.Selector
    public Object clone() {
        return new X509CertificateHolderSelector(this.issuer, this.serialNumber, this.subjectKeyId);
    }
}
