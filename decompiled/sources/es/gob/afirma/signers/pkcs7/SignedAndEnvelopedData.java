package es.gob.afirma.signers.pkcs7;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.BERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.cms.EncryptedContentInfo;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pkcs7/SignedAndEnvelopedData.class */
public final class SignedAndEnvelopedData extends ASN1Object {
    private final ASN1Integer version;
    private final ASN1Set recipientInfos;
    private ASN1Primitive digestAlgorithms;
    private final EncryptedContentInfo encryptedContentInfo;
    private ASN1Set certificates;
    private ASN1Set crls;
    private final ASN1Set signerInfos;

    public SignedAndEnvelopedData(ASN1Set aSN1Set, ASN1Primitive aSN1Primitive, EncryptedContentInfo encryptedContentInfo, ASN1Set aSN1Set2, ASN1Set aSN1Set3, ASN1Set aSN1Set4) {
        this.version = new ASN1Integer(1L);
        this.recipientInfos = aSN1Set;
        this.digestAlgorithms = aSN1Primitive;
        this.encryptedContentInfo = encryptedContentInfo;
        this.certificates = aSN1Set2;
        this.crls = aSN1Set3;
        this.signerInfos = aSN1Set4;
    }

    public SignedAndEnvelopedData(ASN1Sequence aSN1Sequence) {
        int i = 0 + 1;
        this.version = (ASN1Integer) aSN1Sequence.getObjectAt(0);
        int i2 = i + 1;
        this.recipientInfos = ASN1Set.getInstance(aSN1Sequence.getObjectAt(i));
        int i3 = i2 + 1;
        ASN1Encodable objectAt = aSN1Sequence.getObjectAt(i2);
        try {
            this.digestAlgorithms = ASN1Set.getInstance(objectAt);
        } catch (IllegalArgumentException e) {
            this.digestAlgorithms = ASN1Sequence.getInstance(objectAt);
        }
        int i4 = i3 + 1;
        this.encryptedContentInfo = EncryptedContentInfo.getInstance(aSN1Sequence.getObjectAt(i3));
        if (aSN1Sequence.size() > 5) {
            if (aSN1Sequence.size() == 6) {
                i4++;
                this.certificates = ASN1Set.getInstance((ASN1TaggedObject) aSN1Sequence.getObjectAt(i4), false);
            } else {
                int i5 = i4 + 1;
                this.certificates = ASN1Set.getInstance((ASN1TaggedObject) aSN1Sequence.getObjectAt(i4), false);
                i4 = i5 + 1;
                this.crls = ASN1Set.getInstance((ASN1TaggedObject) aSN1Sequence.getObjectAt(i5), false);
            }
        }
        int i6 = i4;
        int i7 = i4 + 1;
        this.signerInfos = ASN1Set.getInstance(aSN1Sequence.getObjectAt(i6));
    }

    public static SignedAndEnvelopedData getInstance(Object obj) {
        if (obj == null || (obj instanceof SignedAndEnvelopedData)) {
            return (SignedAndEnvelopedData) obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new SignedAndEnvelopedData((ASN1Sequence) obj);
        }
        throw new IllegalArgumentException("EnvelopedData invalido: " + obj.getClass().getName());
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public ASN1Set getRecipientInfos() {
        return this.recipientInfos;
    }

    public ASN1Primitive getDigestAlgorithms() {
        return this.digestAlgorithms;
    }

    public EncryptedContentInfo getEncryptedContentInfo() {
        return this.encryptedContentInfo;
    }

    public ASN1Set getCertificates() {
        return this.certificates;
    }

    public ASN1Set getSignerInfos() {
        return this.signerInfos;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(this.version);
        aSN1EncodableVector.add(this.recipientInfos);
        aSN1EncodableVector.add(this.digestAlgorithms);
        aSN1EncodableVector.add(this.encryptedContentInfo);
        if (this.certificates != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 1, this.certificates));
        }
        if (this.crls != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 1, this.crls));
        }
        aSN1EncodableVector.add(this.signerInfos);
        return new BERSequence(aSN1EncodableVector);
    }
}
