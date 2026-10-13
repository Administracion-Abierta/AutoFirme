package org.spongycastle.asn1.cmc;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmc/DecryptedPOP.class */
public class DecryptedPOP extends ASN1Object {
    private final BodyPartID bodyPartID;
    private final AlgorithmIdentifier thePOPAlgID;
    private final byte[] thePOP;

    public DecryptedPOP(BodyPartID bodyPartID, AlgorithmIdentifier thePOPAlgID, byte[] thePOP) {
        this.bodyPartID = bodyPartID;
        this.thePOPAlgID = thePOPAlgID;
        this.thePOP = Arrays.clone(thePOP);
    }

    private DecryptedPOP(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.bodyPartID = BodyPartID.getInstance(seq.getObjectAt(0));
        this.thePOPAlgID = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
        this.thePOP = Arrays.clone(ASN1OctetString.getInstance(seq.getObjectAt(2)).getOctets());
    }

    public static DecryptedPOP getInstance(Object o) {
        if (o instanceof DecryptedPOP) {
            return (DecryptedPOP) o;
        }
        if (o != null) {
            return new DecryptedPOP(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public BodyPartID getBodyPartID() {
        return this.bodyPartID;
    }

    public AlgorithmIdentifier getThePOPAlgID() {
        return this.thePOPAlgID;
    }

    public byte[] getThePOP() {
        return Arrays.clone(this.thePOP);
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.bodyPartID);
        v.add(this.thePOPAlgID);
        v.add(new DEROctetString(this.thePOP));
        return new DERSequence(v);
    }
}
