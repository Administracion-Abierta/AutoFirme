package org.spongycastle.asn1.cms;

import org.spongycastle.asn1.ASN1Choice;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cms/KeyAgreeRecipientIdentifier.class */
public class KeyAgreeRecipientIdentifier extends ASN1Object implements ASN1Choice {
    private IssuerAndSerialNumber issuerSerial;
    private RecipientKeyIdentifier rKeyID;

    public static KeyAgreeRecipientIdentifier getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static KeyAgreeRecipientIdentifier getInstance(Object obj) {
        if (obj == null || (obj instanceof KeyAgreeRecipientIdentifier)) {
            return (KeyAgreeRecipientIdentifier) obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new KeyAgreeRecipientIdentifier(IssuerAndSerialNumber.getInstance(obj));
        }
        if ((obj instanceof ASN1TaggedObject) && ((ASN1TaggedObject) obj).getTagNo() == 0) {
            return new KeyAgreeRecipientIdentifier(RecipientKeyIdentifier.getInstance((ASN1TaggedObject) obj, false));
        }
        throw new IllegalArgumentException("Invalid KeyAgreeRecipientIdentifier: " + obj.getClass().getName());
    }

    public KeyAgreeRecipientIdentifier(IssuerAndSerialNumber issuerSerial) {
        this.issuerSerial = issuerSerial;
        this.rKeyID = null;
    }

    public KeyAgreeRecipientIdentifier(RecipientKeyIdentifier rKeyID) {
        this.issuerSerial = null;
        this.rKeyID = rKeyID;
    }

    public IssuerAndSerialNumber getIssuerAndSerialNumber() {
        return this.issuerSerial;
    }

    public RecipientKeyIdentifier getRKeyID() {
        return this.rKeyID;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        if (this.issuerSerial != null) {
            return this.issuerSerial.toASN1Primitive();
        }
        return new DERTaggedObject(false, 0, this.rKeyID);
    }
}
