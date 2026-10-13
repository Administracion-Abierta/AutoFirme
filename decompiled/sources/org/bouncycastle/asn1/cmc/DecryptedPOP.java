package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmc/DecryptedPOP.class */
public class DecryptedPOP extends ASN1Object {
    private final BodyPartID bodyPartID;
    private final AlgorithmIdentifier thePOPAlgID;
    private final byte[] thePOP;

    public DecryptedPOP(BodyPartID bodyPartID, AlgorithmIdentifier algorithmIdentifier, byte[] bArr) {
        this.bodyPartID = bodyPartID;
        this.thePOPAlgID = algorithmIdentifier;
        this.thePOP = Arrays.clone(bArr);
    }

    private DecryptedPOP(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.bodyPartID = BodyPartID.getInstance(aSN1Sequence.getObjectAt(0));
        this.thePOPAlgID = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(1));
        this.thePOP = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(2)).getOctets());
    }

    public static DecryptedPOP getInstance(Object obj) {
        if (obj instanceof DecryptedPOP) {
            return (DecryptedPOP) obj;
        }
        if (obj != null) {
            return new DecryptedPOP(ASN1Sequence.getInstance(obj));
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

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.add(this.bodyPartID);
        aSN1EncodableVector.add(this.thePOPAlgID);
        aSN1EncodableVector.add(new DEROctetString(this.thePOP));
        return new DERSequence(aSN1EncodableVector);
    }
}
