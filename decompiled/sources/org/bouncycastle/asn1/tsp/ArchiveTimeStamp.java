package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.Attributes;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/tsp/ArchiveTimeStamp.class */
public class ArchiveTimeStamp extends ASN1Object {
    private final AlgorithmIdentifier digestAlgorithm;
    private final Attributes attributes;
    private final ASN1Sequence reducedHashTree;
    private final ContentInfo timeStamp;

    public static ArchiveTimeStamp getInstance(Object obj) {
        if (obj instanceof ArchiveTimeStamp) {
            return (ArchiveTimeStamp) obj;
        }
        if (obj != null) {
            return new ArchiveTimeStamp(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ArchiveTimeStamp(AlgorithmIdentifier algorithmIdentifier, PartialHashtree[] partialHashtreeArr, ContentInfo contentInfo) {
        this(algorithmIdentifier, null, partialHashtreeArr, contentInfo);
    }

    public ArchiveTimeStamp(ContentInfo contentInfo) {
        this(null, null, null, contentInfo);
    }

    public ArchiveTimeStamp(AlgorithmIdentifier algorithmIdentifier, Attributes attributes, PartialHashtree[] partialHashtreeArr, ContentInfo contentInfo) {
        this.digestAlgorithm = algorithmIdentifier;
        this.attributes = attributes;
        if (partialHashtreeArr != null) {
            this.reducedHashTree = new DERSequence(partialHashtreeArr);
        } else {
            this.reducedHashTree = null;
        }
        this.timeStamp = contentInfo;
    }

    private ArchiveTimeStamp(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() < 1 || aSN1Sequence.size() > 4) {
            throw new IllegalArgumentException("wrong sequence size in constructor: " + aSN1Sequence.size());
        }
        AlgorithmIdentifier algorithmIdentifier = null;
        Attributes attributes = null;
        ASN1Sequence aSN1Sequence2 = null;
        for (int i = 0; i < aSN1Sequence.size() - 1; i++) {
            ASN1Encodable objectAt = aSN1Sequence.getObjectAt(i);
            if (objectAt instanceof ASN1TaggedObject) {
                ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(objectAt);
                switch (aSN1TaggedObject.getTagNo()) {
                    case 0:
                        algorithmIdentifier = AlgorithmIdentifier.getInstance(aSN1TaggedObject, false);
                        break;
                    case 1:
                        attributes = Attributes.getInstance(aSN1TaggedObject, false);
                        break;
                    case 2:
                        aSN1Sequence2 = ASN1Sequence.getInstance(aSN1TaggedObject, false);
                        break;
                    default:
                        throw new IllegalArgumentException("invalid tag no in constructor: " + aSN1TaggedObject.getTagNo());
                }
            }
        }
        this.digestAlgorithm = algorithmIdentifier;
        this.attributes = attributes;
        this.reducedHashTree = aSN1Sequence2;
        this.timeStamp = ContentInfo.getInstance(aSN1Sequence.getObjectAt(aSN1Sequence.size() - 1));
    }

    public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
        return this.digestAlgorithm != null ? this.digestAlgorithm : getTimeStampInfo().getMessageImprint().getHashAlgorithm();
    }

    public byte[] getTimeStampDigestValue() {
        return getTimeStampInfo().getMessageImprint().getHashedMessage();
    }

    private TSTInfo getTimeStampInfo() {
        if (!this.timeStamp.getContentType().equals((ASN1Primitive) CMSObjectIdentifiers.signedData)) {
            throw new IllegalStateException("cannot identify algorithm identifier for digest");
        }
        SignedData signedData = SignedData.getInstance(this.timeStamp.getContent());
        if (signedData.getEncapContentInfo().getContentType().equals((ASN1Primitive) PKCSObjectIdentifiers.id_ct_TSTInfo)) {
            return TSTInfo.getInstance(ASN1OctetString.getInstance(signedData.getEncapContentInfo().getContent()).getOctets());
        }
        throw new IllegalStateException("cannot parse time stamp");
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgorithm;
    }

    public PartialHashtree getHashTreeLeaf() {
        if (this.reducedHashTree == null) {
            return null;
        }
        return PartialHashtree.getInstance(this.reducedHashTree.getObjectAt(0));
    }

    public PartialHashtree[] getReducedHashTree() {
        if (this.reducedHashTree == null) {
            return null;
        }
        PartialHashtree[] partialHashtreeArr = new PartialHashtree[this.reducedHashTree.size()];
        for (int i = 0; i != partialHashtreeArr.length; i++) {
            partialHashtreeArr[i] = PartialHashtree.getInstance(this.reducedHashTree.getObjectAt(i));
        }
        return partialHashtreeArr;
    }

    public ContentInfo getTimeStamp() {
        return this.timeStamp;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(4);
        if (this.digestAlgorithm != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 0, (ASN1Encodable) this.digestAlgorithm));
        }
        if (this.attributes != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 1, (ASN1Encodable) this.attributes));
        }
        if (this.reducedHashTree != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 2, (ASN1Encodable) this.reducedHashTree));
        }
        aSN1EncodableVector.add(this.timeStamp);
        return new DERSequence(aSN1EncodableVector);
    }
}
