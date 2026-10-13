package org.spongycastle.asn1.cmp;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmp/PBMParameter.class */
public class PBMParameter extends ASN1Object {
    private ASN1OctetString salt;
    private AlgorithmIdentifier owf;
    private ASN1Integer iterationCount;
    private AlgorithmIdentifier mac;

    private PBMParameter(ASN1Sequence seq) {
        this.salt = ASN1OctetString.getInstance(seq.getObjectAt(0));
        this.owf = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
        this.iterationCount = ASN1Integer.getInstance(seq.getObjectAt(2));
        this.mac = AlgorithmIdentifier.getInstance(seq.getObjectAt(3));
    }

    public static PBMParameter getInstance(Object o) {
        if (o instanceof PBMParameter) {
            return (PBMParameter) o;
        }
        if (o != null) {
            return new PBMParameter(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public PBMParameter(byte[] salt, AlgorithmIdentifier owf, int iterationCount, AlgorithmIdentifier mac) {
        this(new DEROctetString(salt), owf, new ASN1Integer(iterationCount), mac);
    }

    public PBMParameter(ASN1OctetString salt, AlgorithmIdentifier owf, ASN1Integer iterationCount, AlgorithmIdentifier mac) {
        this.salt = salt;
        this.owf = owf;
        this.iterationCount = iterationCount;
        this.mac = mac;
    }

    public ASN1OctetString getSalt() {
        return this.salt;
    }

    public AlgorithmIdentifier getOwf() {
        return this.owf;
    }

    public ASN1Integer getIterationCount() {
        return this.iterationCount;
    }

    public AlgorithmIdentifier getMac() {
        return this.mac;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.salt);
        v.add(this.owf);
        v.add(this.iterationCount);
        v.add(this.mac);
        return new DERSequence(v);
    }
}
