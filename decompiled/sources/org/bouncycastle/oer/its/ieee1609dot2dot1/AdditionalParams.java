package org.bouncycastle.oer.its.ieee1609dot2dot1;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2dot1/AdditionalParams.class */
public class AdditionalParams extends ASN1Object implements ASN1Choice {
    public static final int original = 0;
    public static final int unified = 1;
    public static final int compactUnified = 2;
    public static final int encryptionKey = 3;
    protected final int choice;
    protected final ASN1Encodable additionalParams;

    private AdditionalParams(int i, ASN1Encodable aSN1Encodable) {
        switch (i) {
            case 0:
                this.additionalParams = ButterflyParamsOriginal.getInstance(aSN1Encodable);
                break;
            case 1:
            case 2:
                this.additionalParams = ButterflyExpansion.getInstance(aSN1Encodable);
                break;
            case 3:
                this.additionalParams = PublicEncryptionKey.getInstance(aSN1Encodable);
                break;
            default:
                throw new IllegalArgumentException("invalid choice value " + i);
        }
        this.choice = i;
    }

    private AdditionalParams(ASN1TaggedObject aSN1TaggedObject) {
        this(aSN1TaggedObject.getTagNo(), aSN1TaggedObject.getExplicitBaseObject());
    }

    public static AdditionalParams getInstance(Object obj) {
        if (obj instanceof AdditionalParams) {
            return (AdditionalParams) obj;
        }
        if (obj != null) {
            return new AdditionalParams(ASN1TaggedObject.getInstance(obj, 128));
        }
        return null;
    }

    public static AdditionalParams original(ButterflyParamsOriginal butterflyParamsOriginal) {
        return new AdditionalParams(0, butterflyParamsOriginal);
    }

    public static AdditionalParams unified(ButterflyExpansion butterflyExpansion) {
        return new AdditionalParams(1, butterflyExpansion);
    }

    public static AdditionalParams compactUnified(ButterflyExpansion butterflyExpansion) {
        return new AdditionalParams(2, butterflyExpansion);
    }

    public static AdditionalParams encryptionKey(PublicEncryptionKey publicEncryptionKey) {
        return new AdditionalParams(3, publicEncryptionKey);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getAdditionalParams() {
        return this.additionalParams;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.additionalParams);
    }
}
