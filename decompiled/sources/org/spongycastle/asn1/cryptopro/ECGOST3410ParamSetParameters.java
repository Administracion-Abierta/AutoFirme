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

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cryptopro/ECGOST3410ParamSetParameters.class */
public class ECGOST3410ParamSetParameters extends ASN1Object {
    ASN1Integer p;
    ASN1Integer q;
    ASN1Integer a;
    ASN1Integer b;
    ASN1Integer x;
    ASN1Integer y;

    public static ECGOST3410ParamSetParameters getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static ECGOST3410ParamSetParameters getInstance(Object obj) {
        if (obj == null || (obj instanceof ECGOST3410ParamSetParameters)) {
            return (ECGOST3410ParamSetParameters) obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new ECGOST3410ParamSetParameters((ASN1Sequence) obj);
        }
        throw new IllegalArgumentException("Invalid GOST3410Parameter: " + obj.getClass().getName());
    }

    public ECGOST3410ParamSetParameters(BigInteger a, BigInteger b, BigInteger p, BigInteger q, int x, BigInteger y) {
        this.a = new ASN1Integer(a);
        this.b = new ASN1Integer(b);
        this.p = new ASN1Integer(p);
        this.q = new ASN1Integer(q);
        this.x = new ASN1Integer(x);
        this.y = new ASN1Integer(y);
    }

    public ECGOST3410ParamSetParameters(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.a = (ASN1Integer) e.nextElement();
        this.b = (ASN1Integer) e.nextElement();
        this.p = (ASN1Integer) e.nextElement();
        this.q = (ASN1Integer) e.nextElement();
        this.x = (ASN1Integer) e.nextElement();
        this.y = (ASN1Integer) e.nextElement();
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
        v.add(this.a);
        v.add(this.b);
        v.add(this.p);
        v.add(this.q);
        v.add(this.x);
        v.add(this.y);
        return new DERSequence(v);
    }
}
