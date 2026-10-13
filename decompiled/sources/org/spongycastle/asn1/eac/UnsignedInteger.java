package org.spongycastle.asn1.eac;

import java.math.BigInteger;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/eac/UnsignedInteger.class */
public class UnsignedInteger extends ASN1Object {
    private int tagNo;
    private BigInteger value;

    public UnsignedInteger(int tagNo, BigInteger value) {
        this.tagNo = tagNo;
        this.value = value;
    }

    private UnsignedInteger(ASN1TaggedObject obj) {
        this.tagNo = obj.getTagNo();
        this.value = new BigInteger(1, ASN1OctetString.getInstance(obj, false).getOctets());
    }

    public static UnsignedInteger getInstance(Object obj) {
        if (obj instanceof UnsignedInteger) {
            return (UnsignedInteger) obj;
        }
        if (obj != null) {
            return new UnsignedInteger(ASN1TaggedObject.getInstance(obj));
        }
        return null;
    }

    private byte[] convertValue() {
        byte[] v = this.value.toByteArray();
        if (v[0] == 0) {
            byte[] tmp = new byte[v.length - 1];
            System.arraycopy(v, 1, tmp, 0, tmp.length);
            return tmp;
        }
        return v;
    }

    public int getTagNo() {
        return this.tagNo;
    }

    public BigInteger getValue() {
        return this.value;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(false, this.tagNo, new DEROctetString(convertValue()));
    }
}
