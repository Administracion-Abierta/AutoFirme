package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cms/AuthenticatedData.class */
public class AuthenticatedData extends ASN1Object {
    private ASN1Integer version;
    private OriginatorInfo originatorInfo;
    private ASN1Set recipientInfos;
    private AlgorithmIdentifier macAlgorithm;
    private AlgorithmIdentifier digestAlgorithm;
    private ContentInfo encapsulatedContentInfo;
    private ASN1Set authAttrs;
    private ASN1OctetString mac;
    private ASN1Set unauthAttrs;

    public AuthenticatedData(OriginatorInfo originatorInfo, ASN1Set aSN1Set, AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, ContentInfo contentInfo, ASN1Set aSN1Set2, ASN1OctetString aSN1OctetString, ASN1Set aSN1Set3) {
        if (!(algorithmIdentifier2 == null && aSN1Set2 == null) && (algorithmIdentifier2 == null || aSN1Set2 == null)) {
            throw new IllegalArgumentException("digestAlgorithm and authAttrs must be set together");
        }
        this.version = new ASN1Integer(calculateVersion(originatorInfo));
        this.originatorInfo = originatorInfo;
        this.macAlgorithm = algorithmIdentifier;
        this.digestAlgorithm = algorithmIdentifier2;
        this.recipientInfos = aSN1Set;
        this.encapsulatedContentInfo = contentInfo;
        this.authAttrs = aSN1Set2;
        this.mac = aSN1OctetString;
        this.unauthAttrs = aSN1Set3;
    }

    private AuthenticatedData(ASN1Sequence aSN1Sequence) {
        int i = 0 + 1;
        this.version = (ASN1Integer) aSN1Sequence.getObjectAt(0);
        int i2 = i + 1;
        ASN1Encodable objectAt = aSN1Sequence.getObjectAt(i);
        if (objectAt instanceof ASN1TaggedObject) {
            this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject) objectAt, false);
            i2++;
            objectAt = aSN1Sequence.getObjectAt(i2);
        }
        this.recipientInfos = ASN1Set.getInstance(objectAt);
        int i3 = i2;
        int i4 = i2 + 1;
        this.macAlgorithm = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(i3));
        int i5 = i4 + 1;
        ASN1Encodable objectAt2 = aSN1Sequence.getObjectAt(i4);
        if (objectAt2 instanceof ASN1TaggedObject) {
            this.digestAlgorithm = AlgorithmIdentifier.getInstance((ASN1TaggedObject) objectAt2, false);
            i5++;
            objectAt2 = aSN1Sequence.getObjectAt(i5);
        }
        this.encapsulatedContentInfo = ContentInfo.getInstance(objectAt2);
        int i6 = i5;
        int i7 = i5 + 1;
        ASN1Encodable objectAt3 = aSN1Sequence.getObjectAt(i6);
        if (objectAt3 instanceof ASN1TaggedObject) {
            this.authAttrs = ASN1Set.getInstance((ASN1TaggedObject) objectAt3, false);
            i7++;
            objectAt3 = aSN1Sequence.getObjectAt(i7);
        }
        this.mac = ASN1OctetString.getInstance(objectAt3);
        if (aSN1Sequence.size() > i7) {
            this.unauthAttrs = ASN1Set.getInstance((ASN1TaggedObject) aSN1Sequence.getObjectAt(i7), false);
        }
    }

    public static AuthenticatedData getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public static AuthenticatedData getInstance(Object obj) {
        if (obj instanceof AuthenticatedData) {
            return (AuthenticatedData) obj;
        }
        if (obj != null) {
            return new AuthenticatedData(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public OriginatorInfo getOriginatorInfo() {
        return this.originatorInfo;
    }

    public ASN1Set getRecipientInfos() {
        return this.recipientInfos;
    }

    public AlgorithmIdentifier getMacAlgorithm() {
        return this.macAlgorithm;
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgorithm;
    }

    public ContentInfo getEncapsulatedContentInfo() {
        return this.encapsulatedContentInfo;
    }

    public ASN1Set getAuthAttrs() {
        return this.authAttrs;
    }

    public ASN1OctetString getMac() {
        return this.mac;
    }

    public ASN1Set getUnauthAttrs() {
        return this.unauthAttrs;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(9);
        aSN1EncodableVector.add(this.version);
        if (this.originatorInfo != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 0, (ASN1Encodable) this.originatorInfo));
        }
        aSN1EncodableVector.add(this.recipientInfos);
        aSN1EncodableVector.add(this.macAlgorithm);
        if (this.digestAlgorithm != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 1, (ASN1Encodable) this.digestAlgorithm));
        }
        aSN1EncodableVector.add(this.encapsulatedContentInfo);
        if (this.authAttrs != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 2, (ASN1Encodable) this.authAttrs));
        }
        aSN1EncodableVector.add(this.mac);
        if (this.unauthAttrs != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 3, (ASN1Encodable) this.unauthAttrs));
        }
        return new BERSequence(aSN1EncodableVector);
    }

    public static int calculateVersion(OriginatorInfo originatorInfo) {
        if (originatorInfo == null) {
            return 0;
        }
        ASN1Set cRLs = originatorInfo.getCRLs();
        if (cRLs != null) {
            int size = cRLs.size();
            for (int i = 0; i < size; i++) {
                ASN1Encodable objectAt = cRLs.getObjectAt(i);
                if ((objectAt instanceof ASN1TaggedObject) && ((ASN1TaggedObject) objectAt).hasContextTag(1)) {
                    return 3;
                }
            }
        }
        ASN1Set certificates = originatorInfo.getCertificates();
        if (certificates == null) {
            return 0;
        }
        boolean z = false;
        int size2 = certificates.size();
        for (int i2 = 0; i2 < size2; i2++) {
            ASN1Encodable objectAt2 = certificates.getObjectAt(i2);
            if (objectAt2 instanceof ASN1TaggedObject) {
                ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) objectAt2;
                if (aSN1TaggedObject.hasContextTag(3)) {
                    return 3;
                }
                z = z || aSN1TaggedObject.hasContextTag(2);
            }
        }
        return z ? 1 : 0;
    }
}
