package org.spongycastle.asn1.oiw;

import java.math.BigInteger;
import java.util.Enumeration;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/oiw/ElGamalParameter.class */
public class ElGamalParameter extends ASN1Object {
    ASN1Integer p;
    ASN1Integer g;

    public ElGamalParameter(BigInteger p, BigInteger g) {
        this.p = new ASN1Integer(p);
        this.g = new ASN1Integer(g);
    }

    private ElGamalParameter(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.p = (ASN1Integer) e.nextElement();
        this.g = (ASN1Integer) e.nextElement();
    }

    public static ElGamalParameter getInstance(Object o) {
        if (o instanceof ElGamalParameter) {
            return (ElGamalParameter) o;
        }
        if (o != null) {
            return new ElGamalParameter(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public BigInteger getP() {
        return this.p.getPositiveValue();
    }

    public BigInteger getG() {
        return this.g.getPositiveValue();
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.p);
        v.add(this.g);
        return new DERSequence(v);
    }
}
