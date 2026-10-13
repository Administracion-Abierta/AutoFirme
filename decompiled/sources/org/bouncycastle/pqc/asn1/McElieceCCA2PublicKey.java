package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.legacy.math.linearalgebra.GF2Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/asn1/McElieceCCA2PublicKey.class */
public class McElieceCCA2PublicKey extends ASN1Object {
    private final int n;
    private final int t;
    private final GF2Matrix g;
    private final AlgorithmIdentifier digest;

    public McElieceCCA2PublicKey(int i, int i2, GF2Matrix gF2Matrix, AlgorithmIdentifier algorithmIdentifier) {
        this.n = i;
        this.t = i2;
        this.g = new GF2Matrix(gF2Matrix.getEncoded());
        this.digest = algorithmIdentifier;
    }

    private McElieceCCA2PublicKey(ASN1Sequence aSN1Sequence) {
        this.n = ((ASN1Integer) aSN1Sequence.getObjectAt(0)).intValueExact();
        this.t = ((ASN1Integer) aSN1Sequence.getObjectAt(1)).intValueExact();
        this.g = new GF2Matrix(((ASN1OctetString) aSN1Sequence.getObjectAt(2)).getOctets());
        this.digest = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(3));
    }

    public int getN() {
        return this.n;
    }

    public int getT() {
        return this.t;
    }

    public GF2Matrix getG() {
        return this.g;
    }

    public AlgorithmIdentifier getDigest() {
        return this.digest;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new ASN1Integer(this.n));
        aSN1EncodableVector.add(new ASN1Integer(this.t));
        aSN1EncodableVector.add(new DEROctetString(this.g.getEncoded()));
        aSN1EncodableVector.add(this.digest);
        return new DERSequence(aSN1EncodableVector);
    }

    public static McElieceCCA2PublicKey getInstance(Object obj) {
        if (obj instanceof McElieceCCA2PublicKey) {
            return (McElieceCCA2PublicKey) obj;
        }
        if (obj != null) {
            return new McElieceCCA2PublicKey(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
