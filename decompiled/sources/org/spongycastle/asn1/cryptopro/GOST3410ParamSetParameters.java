package org.spongycastle.asn1.cryptopro;

import java.math.BigInteger;
import java.util.Enumeration;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cryptopro/GOST3410ParamSetParameters.class */
public class GOST3410ParamSetParameters extends ASN1Object {
    int keySize;
    ASN1Integer p;
    ASN1Integer q;
    ASN1Integer a;

    public static GOST3410ParamSetParameters getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static GOST3410ParamSetParameters getInstance(Object obj) {
        if (obj == null || (obj instanceof GOST3410ParamSetParameters)) {
            return (GOST3410ParamSetParameters) obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new GOST3410ParamSetParameters((ASN1Sequence) obj);
        }
        throw new IllegalArgumentException("Invalid GOST3410Parameter: " + obj.getClass().getName());
    }

    public GOST3410ParamSetParameters(int keySize, BigInteger p, BigInteger q, BigInteger a) {
        this.keySize = keySize;
        this.p = new ASN1Integer(p);
        this.q = new ASN1Integer(q);
        this.a = new ASN1Integer(a);
    }

    public GOST3410ParamSetParameters(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.keySize = ((ASN1Integer) e.nextElement()).getValue().intValue();
        this.p = (ASN1Integer) e.nextElement();
        this.q = (ASN1Integer) e.nextElement();
        this.a = (ASN1Integer) e.nextElement();
    }

    public int getLKeySize() {
        return this.keySize;
    }

    public int getKeySize() {
        return this.keySize;
    }

    public BigInteger getP() {
        return this.p.getPositiveValue();
    }

    public BigInteger getQ() {
        return this.q.getPositiveValue();
    }

    public BigInteger getA() {
        return this.a.getPositiveValue();
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(this.keySize));
        v.add(this.p);
        v.add(this.q);
        v.add(this.a);
        return new DERSequence(v);
    }
}
