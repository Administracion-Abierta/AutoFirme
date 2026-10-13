package org.spongycastle.asn1.ua;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/ua/DSTU4145BinaryField.class */
public class DSTU4145BinaryField extends ASN1Object {
    private int m;
    private int k;
    private int j;
    private int l;

    private DSTU4145BinaryField(ASN1Sequence seq) {
        this.m = ASN1Integer.getInstance(seq.getObjectAt(0)).getPositiveValue().intValue();
        if (seq.getObjectAt(1) instanceof ASN1Integer) {
            this.k = ((ASN1Integer) seq.getObjectAt(1)).getPositiveValue().intValue();
        } else {
            if (seq.getObjectAt(1) instanceof ASN1Sequence) {
                ASN1Sequence coefs = ASN1Sequence.getInstance(seq.getObjectAt(1));
                this.k = ASN1Integer.getInstance(coefs.getObjectAt(0)).getPositiveValue().intValue();
                this.j = ASN1Integer.getInstance(coefs.getObjectAt(1)).getPositiveValue().intValue();
                this.l = ASN1Integer.getInstance(coefs.getObjectAt(2)).getPositiveValue().intValue();
                return;
            }
            throw new IllegalArgumentException("object parse error");
        }
    }

    public static DSTU4145BinaryField getInstance(Object obj) {
        if (obj instanceof DSTU4145BinaryField) {
            return (DSTU4145BinaryField) obj;
        }
        if (obj != null) {
            return new DSTU4145BinaryField(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public DSTU4145BinaryField(int m, int k1, int k2, int k3) {
        this.m = m;
        this.k = k1;
        this.j = k2;
        this.l = k3;
    }

    public int getM() {
        return this.m;
    }

    public int getK1() {
        return this.k;
    }

    public int getK2() {
        return this.j;
    }

    public int getK3() {
        return this.l;
    }

    public DSTU4145BinaryField(int m, int k) {
        this(m, k, 0, 0);
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(this.m));
        if (this.j == 0) {
            v.add(new ASN1Integer(this.k));
        } else {
            ASN1EncodableVector coefs = new ASN1EncodableVector();
            coefs.add(new ASN1Integer(this.k));
            coefs.add(new ASN1Integer(this.j));
            coefs.add(new ASN1Integer(this.l));
            v.add(new DERSequence(coefs));
        }
        return new DERSequence(v);
    }
}
