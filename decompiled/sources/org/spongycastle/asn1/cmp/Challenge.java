package org.spongycastle.asn1.cmp;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmp/Challenge.class */
public class Challenge extends ASN1Object {
    private AlgorithmIdentifier owf;
    private ASN1OctetString witness;
    private ASN1OctetString challenge;

    private Challenge(ASN1Sequence seq) {
        int index = 0;
        if (seq.size() == 3) {
            index = 0 + 1;
            this.owf = AlgorithmIdentifier.getInstance(seq.getObjectAt(0));
        }
        this.witness = ASN1OctetString.getInstance(seq.getObjectAt(index));
        this.challenge = ASN1OctetString.getInstance(seq.getObjectAt(index + 1));
    }

    public static Challenge getInstance(Object o) {
        if (o instanceof Challenge) {
            return (Challenge) o;
        }
        if (o != null) {
            return new Challenge(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public Challenge(byte[] witness, byte[] challenge) {
        this(null, witness, challenge);
    }

    public Challenge(AlgorithmIdentifier owf, byte[] witness, byte[] challenge) {
        this.owf = owf;
        this.witness = new DEROctetString(witness);
        this.challenge = new DEROctetString(challenge);
    }

    public AlgorithmIdentifier getOwf() {
        return this.owf;
    }

    public byte[] getWitness() {
        return this.witness.getOctets();
    }

    public byte[] getChallenge() {
        return this.challenge.getOctets();
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        addOptional(v, this.owf);
        v.add(this.witness);
        v.add(this.challenge);
        return new DERSequence(v);
    }

    private void addOptional(ASN1EncodableVector v, ASN1Encodable obj) {
        if (obj != null) {
            v.add(obj);
        }
    }
}
