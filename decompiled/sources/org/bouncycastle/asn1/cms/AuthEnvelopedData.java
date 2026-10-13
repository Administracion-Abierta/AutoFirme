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

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cms/AuthEnvelopedData.class */
public class AuthEnvelopedData extends ASN1Object {
    private ASN1Integer version;
    private OriginatorInfo originatorInfo;
    private ASN1Set recipientInfos;
    private EncryptedContentInfo authEncryptedContentInfo;
    private ASN1Set authAttrs;
    private ASN1OctetString mac;
    private ASN1Set unauthAttrs;

    public AuthEnvelopedData(OriginatorInfo originatorInfo, ASN1Set aSN1Set, EncryptedContentInfo encryptedContentInfo, ASN1Set aSN1Set2, ASN1OctetString aSN1OctetString, ASN1Set aSN1Set3) {
        this.version = new ASN1Integer(0L);
        this.originatorInfo = originatorInfo;
        this.recipientInfos = aSN1Set;
        if (this.recipientInfos.size() == 0) {
            throw new IllegalArgumentException("AuthEnvelopedData requires at least 1 RecipientInfo");
        }
        this.authEncryptedContentInfo = encryptedContentInfo;
        this.authAttrs = aSN1Set2;
        if (!encryptedContentInfo.getContentType().equals((ASN1Primitive) CMSObjectIdentifiers.data) && (aSN1Set2 == null || aSN1Set2.size() == 0)) {
            throw new IllegalArgumentException("authAttrs must be present with non-data content");
        }
        this.mac = aSN1OctetString;
        this.unauthAttrs = aSN1Set3;
    }

    private AuthEnvelopedData(ASN1Sequence aSN1Sequence) {
        int i = 0 + 1;
        this.version = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0).toASN1Primitive());
        if (!this.version.hasValue(0)) {
            throw new IllegalArgumentException("AuthEnvelopedData version number must be 0");
        }
        int i2 = i + 1;
        ASN1Primitive aSN1Primitive = aSN1Sequence.getObjectAt(i).toASN1Primitive();
        if (aSN1Primitive instanceof ASN1TaggedObject) {
            this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject) aSN1Primitive, false);
            i2++;
            aSN1Primitive = aSN1Sequence.getObjectAt(i2).toASN1Primitive();
        }
        this.recipientInfos = ASN1Set.getInstance(aSN1Primitive);
        if (this.recipientInfos.size() == 0) {
            throw new IllegalArgumentException("AuthEnvelopedData requires at least 1 RecipientInfo");
        }
        int i3 = i2;
        int i4 = i2 + 1;
        this.authEncryptedContentInfo = EncryptedContentInfo.getInstance(aSN1Sequence.getObjectAt(i3).toASN1Primitive());
        int i5 = i4 + 1;
        ASN1Primitive aSN1Primitive2 = aSN1Sequence.getObjectAt(i4).toASN1Primitive();
        if (aSN1Primitive2 instanceof ASN1TaggedObject) {
            this.authAttrs = ASN1Set.getInstance((ASN1TaggedObject) aSN1Primitive2, false);
            i5++;
            aSN1Primitive2 = aSN1Sequence.getObjectAt(i5).toASN1Primitive();
        } else if (!this.authEncryptedContentInfo.getContentType().equals((ASN1Primitive) CMSObjectIdentifiers.data) && (this.authAttrs == null || this.authAttrs.size() == 0)) {
            throw new IllegalArgumentException("authAttrs must be present with non-data content");
        }
        this.mac = ASN1OctetString.getInstance(aSN1Primitive2);
        if (aSN1Sequence.size() > i5) {
            this.unauthAttrs = ASN1Set.getInstance((ASN1TaggedObject) aSN1Sequence.getObjectAt(i5).toASN1Primitive(), false);
        }
    }

    public static AuthEnvelopedData getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public static AuthEnvelopedData getInstance(Object obj) {
        if (obj instanceof AuthEnvelopedData) {
            return (AuthEnvelopedData) obj;
        }
        if (obj != null) {
            return new AuthEnvelopedData(ASN1Sequence.getInstance(obj));
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

    public EncryptedContentInfo getAuthEncryptedContentInfo() {
        return this.authEncryptedContentInfo;
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
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(7);
        aSN1EncodableVector.add(this.version);
        if (this.originatorInfo != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 0, (ASN1Encodable) this.originatorInfo));
        }
        aSN1EncodableVector.add(this.recipientInfos);
        aSN1EncodableVector.add(this.authEncryptedContentInfo);
        if (this.authAttrs != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 1, (ASN1Encodable) this.authAttrs));
        }
        aSN1EncodableVector.add(this.mac);
        if (this.unauthAttrs != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 2, (ASN1Encodable) this.unauthAttrs));
        }
        return new BERSequence(aSN1EncodableVector);
    }
}
