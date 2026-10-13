package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/crmf/PKIArchiveOptions.class */
public class PKIArchiveOptions extends ASN1Object implements ASN1Choice {
    public static final int encryptedPrivKey = 0;
    public static final int keyGenParameters = 1;
    public static final int archiveRemGenPrivKey = 2;
    private ASN1Encodable value;

    public static PKIArchiveOptions getInstance(Object obj) {
        if (obj == null || (obj instanceof PKIArchiveOptions)) {
            return (PKIArchiveOptions) obj;
        }
        if (obj instanceof ASN1TaggedObject) {
            return new PKIArchiveOptions(ASN1TaggedObject.getInstance(obj, 128));
        }
        throw new IllegalArgumentException("unknown object: " + obj);
    }

    private PKIArchiveOptions(ASN1TaggedObject aSN1TaggedObject) {
        switch (aSN1TaggedObject.getTagNo()) {
            case 0:
                this.value = EncryptedKey.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 1:
                this.value = ASN1OctetString.getInstance(aSN1TaggedObject, false);
                return;
            case 2:
                this.value = ASN1Boolean.getInstance(aSN1TaggedObject, false);
                return;
            default:
                throw new IllegalArgumentException("unknown tag number: " + aSN1TaggedObject.getTagNo());
        }
    }

    public PKIArchiveOptions(EncryptedKey encryptedKey) {
        this.value = encryptedKey;
    }

    public PKIArchiveOptions(ASN1OctetString aSN1OctetString) {
        this.value = aSN1OctetString;
    }

    public PKIArchiveOptions(boolean z) {
        this.value = ASN1Boolean.getInstance(z);
    }

    public int getType() {
        if (this.value instanceof EncryptedKey) {
            return 0;
        }
        return this.value instanceof ASN1OctetString ? 1 : 2;
    }

    public ASN1Encodable getValue() {
        return this.value;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        if (this.value instanceof EncryptedKey) {
            return new DERTaggedObject(true, 0, this.value);
        }
        return this.value instanceof ASN1OctetString ? new DERTaggedObject(false, 1, this.value) : new DERTaggedObject(false, 2, this.value);
    }
}
