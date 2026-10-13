package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/ObjectDigestInfo.class */
public class ObjectDigestInfo extends ASN1Object {
    public static final int publicKey = 0;
    public static final int publicKeyCert = 1;
    public static final int otherObjectDigest = 2;
    ASN1Enumerated digestedObjectType;
    ASN1ObjectIdentifier otherObjectTypeID;
    AlgorithmIdentifier digestAlgorithm;
    ASN1BitString objectDigest;

    public static ObjectDigestInfo getInstance(Object obj) {
        if (obj instanceof ObjectDigestInfo) {
            return (ObjectDigestInfo) obj;
        }
        if (obj != null) {
            return new ObjectDigestInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static ObjectDigestInfo getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public ObjectDigestInfo(int i, ASN1ObjectIdentifier aSN1ObjectIdentifier, AlgorithmIdentifier algorithmIdentifier, byte[] bArr) {
        this.digestedObjectType = new ASN1Enumerated(i);
        if (i == 2) {
            this.otherObjectTypeID = aSN1ObjectIdentifier;
        }
        this.digestAlgorithm = algorithmIdentifier;
        this.objectDigest = new DERBitString(bArr);
    }

    private ObjectDigestInfo(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() > 4 || aSN1Sequence.size() < 3) {
            throw new IllegalArgumentException("Bad sequence size: " + aSN1Sequence.size());
        }
        this.digestedObjectType = ASN1Enumerated.getInstance(aSN1Sequence.getObjectAt(0));
        int i = 0;
        if (aSN1Sequence.size() == 4) {
            this.otherObjectTypeID = ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(1));
            i = 0 + 1;
        }
        this.digestAlgorithm = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(1 + i));
        this.objectDigest = ASN1BitString.getInstance(aSN1Sequence.getObjectAt(2 + i));
    }

    public ASN1Enumerated getDigestedObjectType() {
        return this.digestedObjectType;
    }

    public ASN1ObjectIdentifier getOtherObjectTypeID() {
        return this.otherObjectTypeID;
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgorithm;
    }

    public ASN1BitString getObjectDigest() {
        return this.objectDigest;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(4);
        aSN1EncodableVector.add(this.digestedObjectType);
        if (this.otherObjectTypeID != null) {
            aSN1EncodableVector.add(this.otherObjectTypeID);
        }
        aSN1EncodableVector.add(this.digestAlgorithm);
        aSN1EncodableVector.add(this.objectDigest);
        return new DERSequence(aSN1EncodableVector);
    }
}
