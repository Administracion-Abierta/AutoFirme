package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.EnvelopedData;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/crmf/EncryptedKey.class */
public class EncryptedKey extends ASN1Object implements ASN1Choice {
    private EnvelopedData envelopedData;
    private EncryptedValue encryptedValue;

    public static EncryptedKey getInstance(Object obj) {
        if (obj instanceof EncryptedKey) {
            return (EncryptedKey) obj;
        }
        return obj instanceof ASN1TaggedObject ? new EncryptedKey(EnvelopedData.getInstance((ASN1TaggedObject) obj, false)) : new EncryptedKey(EncryptedValue.getInstance(obj));
    }

    public EncryptedKey(EnvelopedData envelopedData) {
        this.envelopedData = envelopedData;
    }

    public EncryptedKey(EncryptedValue encryptedValue) {
        this.encryptedValue = encryptedValue;
    }

    public boolean isEncryptedValue() {
        return this.encryptedValue != null;
    }

    public ASN1Encodable getValue() {
        return this.encryptedValue != null ? this.encryptedValue : this.envelopedData;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.encryptedValue != null ? this.encryptedValue.toASN1Primitive() : new DERTaggedObject(false, 0, (ASN1Encodable) this.envelopedData);
    }
}
