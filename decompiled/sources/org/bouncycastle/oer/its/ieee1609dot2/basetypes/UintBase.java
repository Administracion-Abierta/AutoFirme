package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/UintBase.class */
public abstract class UintBase extends ASN1Object {
    protected final BigInteger value;

    public UintBase(BigInteger bigInteger) {
        this.value = bigInteger;
        assertLimit();
    }

    public UintBase(int i) {
        this(BigInteger.valueOf(i));
    }

    public UintBase(long j) {
        this(BigInteger.valueOf(j));
    }

    protected UintBase(ASN1Integer aSN1Integer) {
        this(aSN1Integer.getValue());
    }

    public BigInteger getValue() {
        return this.value;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.value);
    }

    protected abstract void assertLimit();
}
