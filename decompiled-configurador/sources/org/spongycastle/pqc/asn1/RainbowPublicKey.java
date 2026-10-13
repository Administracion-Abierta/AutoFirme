package org.spongycastle.pqc.asn1;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.pqc.crypto.rainbow.util.RainbowUtil;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/asn1/RainbowPublicKey.class */
public class RainbowPublicKey extends ASN1Object {
    private ASN1Integer version;
    private ASN1ObjectIdentifier oid;
    private ASN1Integer docLength;
    private byte[][] coeffQuadratic;
    private byte[][] coeffSingular;
    private byte[] coeffScalar;

    private RainbowPublicKey(ASN1Sequence seq) {
        if (seq.getObjectAt(0) instanceof ASN1Integer) {
            this.version = ASN1Integer.getInstance(seq.getObjectAt(0));
        } else {
            this.oid = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0));
        }
        this.docLength = ASN1Integer.getInstance(seq.getObjectAt(1));
        ASN1Sequence asnCoeffQuad = ASN1Sequence.getInstance(seq.getObjectAt(2));
        this.coeffQuadratic = new byte[asnCoeffQuad.size()][];
        for (int quadSize = 0; quadSize < asnCoeffQuad.size(); quadSize++) {
            this.coeffQuadratic[quadSize] = ASN1OctetString.getInstance(asnCoeffQuad.getObjectAt(quadSize)).getOctets();
        }
        ASN1Sequence asnCoeffSing = (ASN1Sequence) seq.getObjectAt(3);
        this.coeffSingular = new byte[asnCoeffSing.size()][];
        for (int singSize = 0; singSize < asnCoeffSing.size(); singSize++) {
            this.coeffSingular[singSize] = ASN1OctetString.getInstance(asnCoeffSing.getObjectAt(singSize)).getOctets();
        }
        ASN1Sequence asnCoeffScalar = (ASN1Sequence) seq.getObjectAt(4);
        this.coeffScalar = ASN1OctetString.getInstance(asnCoeffScalar.getObjectAt(0)).getOctets();
    }

    public RainbowPublicKey(int docLength, short[][] coeffQuadratic, short[][] coeffSingular, short[] coeffScalar) {
        this.version = new ASN1Integer(0L);
        this.docLength = new ASN1Integer(docLength);
        this.coeffQuadratic = RainbowUtil.convertArray(coeffQuadratic);
        this.coeffSingular = RainbowUtil.convertArray(coeffSingular);
        this.coeffScalar = RainbowUtil.convertArray(coeffScalar);
    }

    public static RainbowPublicKey getInstance(Object o) {
        if (o instanceof RainbowPublicKey) {
            return (RainbowPublicKey) o;
        }
        if (o != null) {
            return new RainbowPublicKey(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public int getDocLength() {
        return this.docLength.getValue().intValue();
    }

    public short[][] getCoeffQuadratic() {
        return RainbowUtil.convertArray(this.coeffQuadratic);
    }

    public short[][] getCoeffSingular() {
        return RainbowUtil.convertArray(this.coeffSingular);
    }

    public short[] getCoeffScalar() {
        return RainbowUtil.convertArray(this.coeffScalar);
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (this.version != null) {
            v.add(this.version);
        } else {
            v.add(this.oid);
        }
        v.add(this.docLength);
        ASN1EncodableVector asnCoeffQuad = new ASN1EncodableVector();
        for (int i = 0; i < this.coeffQuadratic.length; i++) {
            asnCoeffQuad.add(new DEROctetString(this.coeffQuadratic[i]));
        }
        v.add(new DERSequence(asnCoeffQuad));
        ASN1EncodableVector asnCoeffSing = new ASN1EncodableVector();
        for (int i2 = 0; i2 < this.coeffSingular.length; i2++) {
            asnCoeffSing.add(new DEROctetString(this.coeffSingular[i2]));
        }
        v.add(new DERSequence(asnCoeffSing));
        ASN1EncodableVector asnCoeffScalar = new ASN1EncodableVector();
        asnCoeffScalar.add(new DEROctetString(this.coeffScalar));
        v.add(new DERSequence(asnCoeffScalar));
        return new DERSequence(v);
    }
}
