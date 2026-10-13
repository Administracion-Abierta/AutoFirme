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

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/asn1/McEliecePublicKey.class */
public class McEliecePublicKey extends ASN1Object {
    private final int n;
    private final int t;
    private final GF2Matrix g;

    public McEliecePublicKey(int n, int t, GF2Matrix g) {
        this.n = n;
        this.t = t;
        this.g = new GF2Matrix(g);
    }

    private McEliecePublicKey(ASN1Sequence seq) {
        BigInteger bigN = ((ASN1Integer) seq.getObjectAt(0)).getValue();
        this.n = bigN.intValue();
        BigInteger bigT = ((ASN1Integer) seq.getObjectAt(1)).getValue();
        this.t = bigT.intValue();
        this.g = new GF2Matrix(((ASN1OctetString) seq.getObjectAt(2)).getOctets());
    }

    public int getN() {
        return this.n;
    }

    public int getT() {
        return this.t;
    }

    public GF2Matrix getG() {
        return new GF2Matrix(this.g);
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(this.n));
        v.add(new ASN1Integer(this.t));
        v.add(new DEROctetString(this.g.getEncoded()));
        return new DERSequence(v);
    }

    public static McEliecePublicKey getInstance(Object o) {
        if (o instanceof McEliecePublicKey) {
            return (McEliecePublicKey) o;
        }
        if (o != null) {
            return new McEliecePublicKey(ASN1Sequence.getInstance(o));
        }
        return null;
    }
}
