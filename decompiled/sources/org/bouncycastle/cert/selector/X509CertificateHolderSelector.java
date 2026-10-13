package org.bouncycastle.cert.selector;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/selector/X509CertificateHolderSelector.class */
public class X509CertificateHolderSelector implements Selector {
    private byte[] subjectKeyId;
    private X500Name issuer;
    private BigInteger serialNumber;

    public X509CertificateHolderSelector(byte[] bArr) {
        this(null, null, bArr);
    }

    public X509CertificateHolderSelector(X500Name x500Name, BigInteger bigInteger) {
        this(x500Name, bigInteger, null);
    }

    public X509CertificateHolderSelector(X500Name x500Name, BigInteger bigInteger, byte[] bArr) {
        this.issuer = x500Name;
        this.serialNumber = bigInteger;
        this.subjectKeyId = Arrays.clone(bArr);
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
        int iHashCode = Arrays.hashCode(this.subjectKeyId);
        if (this.serialNumber != null) {
            iHashCode ^= this.serialNumber.hashCode();
        }
        if (this.issuer != null) {
            iHashCode ^= this.issuer.hashCode();
        }
        return iHashCode;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof X509CertificateHolderSelector)) {
            return false;
        }
        X509CertificateHolderSelector x509CertificateHolderSelector = (X509CertificateHolderSelector) obj;
        return Arrays.areEqual(this.subjectKeyId, x509CertificateHolderSelector.subjectKeyId) && equalsObj(this.serialNumber, x509CertificateHolderSelector.serialNumber) && equalsObj(this.issuer, x509CertificateHolderSelector.issuer);
    }

    private boolean equalsObj(Object obj, Object obj2) {
        if (obj != null) {
            return obj.equals(obj2);
        }
        return obj2 == null;
    }

    @Override // org.bouncycastle.util.Selector
    public boolean match(Object obj) {
        if (!(obj instanceof X509CertificateHolder)) {
            if (obj instanceof byte[]) {
                return Arrays.areEqual(this.subjectKeyId, (byte[]) obj);
            }
            return false;
        }
        X509CertificateHolder x509CertificateHolder = (X509CertificateHolder) obj;
        if (getSerialNumber() != null) {
            IssuerAndSerialNumber issuerAndSerialNumber = new IssuerAndSerialNumber(x509CertificateHolder.toASN1Structure());
            return issuerAndSerialNumber.getName().equals(this.issuer) && issuerAndSerialNumber.getSerialNumber().hasValue(this.serialNumber);
        }
        if (this.subjectKeyId == null) {
            return false;
        }
        Extension extension = x509CertificateHolder.getExtension(Extension.subjectKeyIdentifier);
        if (extension == null) {
            return Arrays.areEqual(this.subjectKeyId, MSOutlookKeyIdCalculator.calculateKeyId(x509CertificateHolder.getSubjectPublicKeyInfo()));
        }
        return Arrays.areEqual(this.subjectKeyId, ASN1OctetString.getInstance(extension.getParsedValue()).getOctets());
    }

    @Override // org.bouncycastle.util.Selector
    public Object clone() {
        return new X509CertificateHolderSelector(this.issuer, this.serialNumber, this.subjectKeyId);
    }
}
