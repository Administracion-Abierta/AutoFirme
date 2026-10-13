package org.spongycastle.asn1.cms;

import org.spongycastle.asn1.ASN1Choice;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cms/RecipientInfo.class */
public class RecipientInfo extends ASN1Object implements ASN1Choice {
    ASN1Encodable info;

    public RecipientInfo(KeyTransRecipientInfo info) {
        this.info = info;
    }

    public RecipientInfo(KeyAgreeRecipientInfo info) {
        this.info = new DERTaggedObject(false, 1, info);
    }

    public RecipientInfo(KEKRecipientInfo info) {
        this.info = new DERTaggedObject(false, 2, info);
    }

    public RecipientInfo(PasswordRecipientInfo info) {
        this.info = new DERTaggedObject(false, 3, info);
    }

    public RecipientInfo(OtherRecipientInfo info) {
        this.info = new DERTaggedObject(false, 4, info);
    }

    public RecipientInfo(ASN1Primitive info) {
        this.info = info;
    }

    public static RecipientInfo getInstance(Object o) {
        if (o == null || (o instanceof RecipientInfo)) {
            return (RecipientInfo) o;
        }
        if (o instanceof ASN1Sequence) {
            return new RecipientInfo((ASN1Sequence) o);
        }
        if (o instanceof ASN1TaggedObject) {
            return new RecipientInfo((ASN1TaggedObject) o);
        }
        throw new IllegalArgumentException("unknown object in factory: " + o.getClass().getName());
    }

    public ASN1Integer getVersion() {
        if (this.info instanceof ASN1TaggedObject) {
            ASN1TaggedObject o = (ASN1TaggedObject) this.info;
            switch (o.getTagNo()) {
                case 1:
                    return KeyAgreeRecipientInfo.getInstance(o, false).getVersion();
                case 2:
                    return getKEKInfo(o).getVersion();
                case 3:
                    return PasswordRecipientInfo.getInstance(o, false).getVersion();
                case 4:
                    return new ASN1Integer(0L);
                default:
                    throw new IllegalStateException("unknown tag");
            }
        }
        return KeyTransRecipientInfo.getInstance(this.info).getVersion();
    }

    public boolean isTagged() {
        return this.info instanceof ASN1TaggedObject;
    }

    public ASN1Encodable getInfo() {
        if (this.info instanceof ASN1TaggedObject) {
            ASN1TaggedObject o = (ASN1TaggedObject) this.info;
            switch (o.getTagNo()) {
                case 1:
                    return KeyAgreeRecipientInfo.getInstance(o, false);
                case 2:
                    return getKEKInfo(o);
                case 3:
                    return PasswordRecipientInfo.getInstance(o, false);
                case 4:
                    return OtherRecipientInfo.getInstance(o, false);
                default:
                    throw new IllegalStateException("unknown tag");
            }
        }
        return KeyTransRecipientInfo.getInstance(this.info);
    }

    private KEKRecipientInfo getKEKInfo(ASN1TaggedObject o) {
        if (o.isExplicit()) {
            return KEKRecipientInfo.getInstance(o, true);
        }
        return KEKRecipientInfo.getInstance(o, false);
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.info.toASN1Primitive();
    }
}
