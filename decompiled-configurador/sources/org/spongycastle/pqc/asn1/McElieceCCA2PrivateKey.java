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
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.pqc.math.linearalgebra.GF2mField;
import org.spongycastle.pqc.math.linearalgebra.Permutation;
import org.spongycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/asn1/McElieceCCA2PrivateKey.class */
public class McElieceCCA2PrivateKey extends ASN1Object {
    private int n;
    private int k;
    private byte[] encField;
    private byte[] encGp;
    private byte[] encP;
    private AlgorithmIdentifier digest;

    public McElieceCCA2PrivateKey(int n, int k, GF2mField field, PolynomialGF2mSmallM goppaPoly, Permutation p, AlgorithmIdentifier digest) {
        this.n = n;
        this.k = k;
        this.encField = field.getEncoded();
        this.encGp = goppaPoly.getEncoded();
        this.encP = p.getEncoded();
        this.digest = digest;
    }

    private McElieceCCA2PrivateKey(ASN1Sequence seq) {
        BigInteger bigN = ((ASN1Integer) seq.getObjectAt(0)).getValue();
        this.n = bigN.intValue();
        BigInteger bigK = ((ASN1Integer) seq.getObjectAt(1)).getValue();
        this.k = bigK.intValue();
        this.encField = ((ASN1OctetString) seq.getObjectAt(2)).getOctets();
        this.encGp = ((ASN1OctetString) seq.getObjectAt(3)).getOctets();
        this.encP = ((ASN1OctetString) seq.getObjectAt(4)).getOctets();
        this.digest = AlgorithmIdentifier.getInstance(seq.getObjectAt(5));
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

    public Permutation getP() {
        return new Permutation(this.encP);
    }

    public AlgorithmIdentifier getDigest() {
        return this.digest;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(this.n));
        v.add(new ASN1Integer(this.k));
        v.add(new DEROctetString(this.encField));
        v.add(new DEROctetString(this.encGp));
        v.add(new DEROctetString(this.encP));
        v.add(this.digest);
        return new DERSequence(v);
    }

    public static McElieceCCA2PrivateKey getInstance(Object o) {
        if (o instanceof McElieceCCA2PrivateKey) {
            return (McElieceCCA2PrivateKey) o;
        }
        if (o != null) {
            return new McElieceCCA2PrivateKey(ASN1Sequence.getInstance(o));
        }
        return null;
    }
}
