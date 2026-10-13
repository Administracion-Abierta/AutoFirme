package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmp/KemOtherInfo.class */
public class KemOtherInfo extends ASN1Object {
    private static final PKIFreeText DEFAULT_staticString = new PKIFreeText("CMP-KEM");
    private final PKIFreeText staticString;
    private final ASN1OctetString transactionID;
    private final ASN1OctetString senderNonce;
    private final ASN1OctetString recipNonce;
    private final ASN1Integer len;
    private final AlgorithmIdentifier mac;
    private final ASN1OctetString ct;

    public KemOtherInfo(ASN1OctetString aSN1OctetString, ASN1OctetString aSN1OctetString2, ASN1OctetString aSN1OctetString3, ASN1Integer aSN1Integer, AlgorithmIdentifier algorithmIdentifier, ASN1OctetString aSN1OctetString4) {
        this.staticString = DEFAULT_staticString;
        this.transactionID = aSN1OctetString;
        this.senderNonce = aSN1OctetString2;
        this.recipNonce = aSN1OctetString3;
        this.len = aSN1Integer;
        this.mac = algorithmIdentifier;
        this.ct = aSN1OctetString4;
    }

    public KemOtherInfo(ASN1OctetString aSN1OctetString, ASN1OctetString aSN1OctetString2, ASN1OctetString aSN1OctetString3, long j, AlgorithmIdentifier algorithmIdentifier, ASN1OctetString aSN1OctetString4) {
        this(aSN1OctetString, aSN1OctetString2, aSN1OctetString3, new ASN1Integer(j), algorithmIdentifier, aSN1OctetString4);
    }

    private KemOtherInfo(ASN1Sequence aSN1Sequence) {
        ASN1Primitive aSN1PrimitiveTryGetContextBaseUniversal;
        ASN1Primitive aSN1PrimitiveTryGetContextBaseUniversal2;
        ASN1Primitive aSN1PrimitiveTryGetContextBaseUniversal3;
        if (aSN1Sequence.size() < 4 || aSN1Sequence.size() > 7) {
            throw new IllegalArgumentException("sequence size should be between 4 and 7 inclusive");
        }
        this.staticString = PKIFreeText.getInstance(aSN1Sequence.getObjectAt(0));
        if (!DEFAULT_staticString.equals(this.staticString)) {
            throw new IllegalArgumentException("staticString field should be " + DEFAULT_staticString);
        }
        ASN1OctetString aSN1OctetString = null;
        ASN1OctetString aSN1OctetString2 = null;
        ASN1OctetString aSN1OctetString3 = null;
        int i = 0 + 1;
        ASN1TaggedObject aSN1TaggedObjectTryGetTagged = tryGetTagged(aSN1Sequence, i);
        if (aSN1TaggedObjectTryGetTagged != null && (aSN1PrimitiveTryGetContextBaseUniversal3 = ASN1Util.tryGetContextBaseUniversal(aSN1TaggedObjectTryGetTagged, 0, true, 4)) != null) {
            aSN1OctetString = (ASN1OctetString) aSN1PrimitiveTryGetContextBaseUniversal3;
            i++;
            aSN1TaggedObjectTryGetTagged = tryGetTagged(aSN1Sequence, i);
        }
        if (aSN1TaggedObjectTryGetTagged != null && (aSN1PrimitiveTryGetContextBaseUniversal2 = ASN1Util.tryGetContextBaseUniversal(aSN1TaggedObjectTryGetTagged, 1, true, 4)) != null) {
            aSN1OctetString2 = (ASN1OctetString) aSN1PrimitiveTryGetContextBaseUniversal2;
            i++;
            aSN1TaggedObjectTryGetTagged = tryGetTagged(aSN1Sequence, i);
        }
        if (aSN1TaggedObjectTryGetTagged != null && (aSN1PrimitiveTryGetContextBaseUniversal = ASN1Util.tryGetContextBaseUniversal(aSN1TaggedObjectTryGetTagged, 2, true, 4)) != null) {
            aSN1OctetString3 = (ASN1OctetString) aSN1PrimitiveTryGetContextBaseUniversal;
            i++;
            aSN1TaggedObjectTryGetTagged = tryGetTagged(aSN1Sequence, i);
        }
        if (aSN1TaggedObjectTryGetTagged != null) {
            throw new IllegalArgumentException("unknown tag: " + ASN1Util.getTagText(aSN1TaggedObjectTryGetTagged));
        }
        this.transactionID = aSN1OctetString;
        this.senderNonce = aSN1OctetString2;
        this.recipNonce = aSN1OctetString3;
        this.len = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(i));
        int i2 = i + 1;
        this.mac = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(i2));
        int i3 = i2 + 1;
        this.ct = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(i3));
        if (i3 + 1 != aSN1Sequence.size()) {
            throw new IllegalArgumentException("unexpected data at end of sequence");
        }
    }

    public static KemOtherInfo getInstance(Object obj) {
        if (obj instanceof KemOtherInfo) {
            return (KemOtherInfo) obj;
        }
        if (obj != null) {
            return new KemOtherInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1OctetString getTransactionID() {
        return this.transactionID;
    }

    public ASN1OctetString getSenderNonce() {
        return this.senderNonce;
    }

    public ASN1OctetString getRecipNonce() {
        return this.recipNonce;
    }

    public ASN1Integer getLen() {
        return this.len;
    }

    public AlgorithmIdentifier getMac() {
        return this.mac;
    }

    public ASN1OctetString getCt() {
        return this.ct;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(7);
        aSN1EncodableVector.add(this.staticString);
        addOptional(aSN1EncodableVector, 0, this.transactionID);
        addOptional(aSN1EncodableVector, 1, this.senderNonce);
        addOptional(aSN1EncodableVector, 2, this.recipNonce);
        aSN1EncodableVector.add(this.len);
        aSN1EncodableVector.add(this.mac);
        aSN1EncodableVector.add(this.ct);
        return new DERSequence(aSN1EncodableVector);
    }

    private static void addOptional(ASN1EncodableVector aSN1EncodableVector, int i, ASN1Encodable aSN1Encodable) {
        if (aSN1Encodable != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, i, aSN1Encodable));
        }
    }

    private static ASN1TaggedObject tryGetTagged(ASN1Sequence aSN1Sequence, int i) {
        ASN1Encodable objectAt = aSN1Sequence.getObjectAt(i);
        if (objectAt instanceof ASN1TaggedObject) {
            return (ASN1TaggedObject) objectAt;
        }
        return null;
    }
}
