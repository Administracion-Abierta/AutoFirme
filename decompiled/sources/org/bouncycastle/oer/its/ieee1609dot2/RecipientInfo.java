package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/RecipientInfo.class */
public class RecipientInfo extends ASN1Object implements ASN1Choice {
    public static final int pskRecipInfo = 0;
    public static final int symmRecipInfo = 1;
    public static final int certRecipInfo = 2;
    public static final int signedDataRecipInfo = 3;
    public static final int rekRecipInfo = 4;
    private final int choice;
    private final ASN1Encodable recipientInfo;

    public RecipientInfo(int i, ASN1Encodable aSN1Encodable) {
        this.choice = i;
        this.recipientInfo = aSN1Encodable;
    }

    private RecipientInfo(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (this.choice) {
            case 0:
                this.recipientInfo = PreSharedKeyRecipientInfo.getInstance((Object) aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 1:
                this.recipientInfo = SymmRecipientInfo.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 2:
            case 3:
            case 4:
                this.recipientInfo = PKRecipientInfo.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            default:
                throw new IllegalArgumentException("invalid choice value " + this.choice);
        }
    }

    public static RecipientInfo getInstance(Object obj) {
        if (obj instanceof RecipientInfo) {
            return (RecipientInfo) obj;
        }
        if (obj != null) {
            return new RecipientInfo(ASN1TaggedObject.getInstance(obj, 128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getRecipientInfo() {
        return this.recipientInfo;
    }

    public static RecipientInfo pskRecipInfo(PreSharedKeyRecipientInfo preSharedKeyRecipientInfo) {
        return new RecipientInfo(0, preSharedKeyRecipientInfo);
    }

    public static RecipientInfo symmRecipInfo(SymmRecipientInfo symmRecipientInfo) {
        return new RecipientInfo(1, symmRecipientInfo);
    }

    public static RecipientInfo certRecipInfo(PKRecipientInfo pKRecipientInfo) {
        return new RecipientInfo(2, pKRecipientInfo);
    }

    public static RecipientInfo signedDataRecipInfo(PKRecipientInfo pKRecipientInfo) {
        return new RecipientInfo(3, pKRecipientInfo);
    }

    public static RecipientInfo rekRecipInfo(PKRecipientInfo pKRecipientInfo) {
        return new RecipientInfo(4, pKRecipientInfo);
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.recipientInfo);
    }
}
