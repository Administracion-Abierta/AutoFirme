package org.spongycastle.pqc.asn1;

import java.math.BigInteger;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.pqc.math.linearalgebra.GF2Matrix;
import org.spongycastle.pqc.math.linearalgebra.GF2mField;
import org.spongycastle.pqc.math.linearalgebra.Permutation;
import org.spongycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/asn1/McEliecePrivateKey.class */
public class McEliecePrivateKey extends ASN1Object {
    private int n;
    private int k;
    private byte[] encField;
    private byte[] encGp;
    private byte[] encSInv;
    private byte[] encP1;
    private byte[] encP2;

    public McEliecePrivateKey(int n, int k, GF2mField field, PolynomialGF2mSmallM goppaPoly, Permutation p1, Permutation p2, GF2Matrix sInv) {
        this.n = n;
        this.k = k;
        this.encField = field.getEncoded();
        this.encGp = goppaPoly.getEncoded();
        this.encSInv = sInv.getEncoded();
        this.encP1 = p1.getEncoded();
        this.encP2 = p2.getEncoded();
    }

    public static McEliecePrivateKey getInstance(Object o) {
        if (o instanceof McEliecePrivateKey) {
            return (McEliecePrivateKey) o;
        }
        if (o != null) {
            return new McEliecePrivateKey(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    private McEliecePrivateKey(ASN1Sequence seq) {
        BigInteger bigN = ((ASN1Integer) seq.getObjectAt(0)).getValue();
        this.n = bigN.intValue();
        BigInteger bigK = ((ASN1Integer) seq.getObjectAt(1)).getValue();
        this.k = bigK.intValue();
        this.encField = ((ASN1OctetString) seq.getObjectAt(2)).getOctets();
        this.encGp = ((ASN1OctetString) seq.getObjectAt(3)).getOctets();
        this.encP1 = ((ASN1OctetString) seq.getObjectAt(4)).getOctets();
        this.encP2 = ((ASN1OctetString) seq.getObjectAt(5)).getOctets();
        this.encSInv = ((ASN1OctetString) seq.getObjectAt(6)).getOctets();
    }

    public int getN() {
        return this.n;
    }

    public int getK() {
        return this.k;
    }

    public GF2mField getField() {
        return new GF2mField(this.encField);
    }

    public PolynomialGF2mSmallM getGoppaPoly() {
        return new PolynomialGF2mSmallM(getField(), this.encGp);
    }

    public GF2Matrix getSInv() {
        return new GF2Matrix(this.encSInv);
    }

    public Permutation getP1() {
        return new Permutation(this.encP1);
    }

    public Permutation getP2() {
        return new Permutation(this.encP2);
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(this.n));
        v.add(new ASN1Integer(this.k));
        v.add(new DEROctetString(this.encField));
        v.add(new DEROctetString(this.encGp));
        v.add(new DEROctetString(this.encP1));
        v.add(new DEROctetString(this.encP2));
        v.add(new DEROctetString(this.encSInv));
        return new DERSequence(v);
    }
}
