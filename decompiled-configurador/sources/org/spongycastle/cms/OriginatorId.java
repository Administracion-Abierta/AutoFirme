package org.spongycastle.cms;

import java.math.BigInteger;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Selector;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/OriginatorId.class */
class OriginatorId implements Selector {
    private byte[] subjectKeyId;
    private X500Name issuer;
    private BigInteger serialNumber;

    public OriginatorId(byte[] subjectKeyId) {
        setSubjectKeyID(subjectKeyId);
    }

    private void setSubjectKeyID(byte[] subjectKeyId) {
        this.subjectKeyId = subjectKeyId;
    }

    public OriginatorId(X500Name issuer, BigInteger serialNumber) {
        setIssuerAndSerial(issuer, serialNumber);
    }

    private void setIssuerAndSerial(X500Name issuer, BigInteger serialNumber) {
        this.issuer = issuer;
        this.serialNumber = serialNumber;
    }

    public OriginatorId(X500Name issuer, BigInteger serialNumber, byte[] subjectKeyId) {
        setIssuerAndSerial(issuer, serialNumber);
        setSubjectKeyID(subjectKeyId);
    }

    public X500Name getIssuer() {
        return this.issuer;
    }

    @Override // org.spongycastle.util.Selector
    public Object clone() {
        return new OriginatorId(this.issuer, this.serialNumber, this.subjectKeyId);
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
        if (!(o instanceof OriginatorId)) {
            return false;
        }
        OriginatorId id = (OriginatorId) o;
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
        return false;
    }
}
