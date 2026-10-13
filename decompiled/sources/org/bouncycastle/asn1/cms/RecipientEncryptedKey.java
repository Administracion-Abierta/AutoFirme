package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cms/RecipientEncryptedKey.class */
public class RecipientEncryptedKey extends ASN1Object {
    private KeyAgreeRecipientIdentifier identifier;
    private ASN1OctetString encryptedKey;

    private RecipientEncryptedKey(ASN1Sequence aSN1Sequence) {
        this.identifier = KeyAgreeRecipientIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
        this.encryptedKey = (ASN1OctetString) aSN1Sequence.getObjectAt(1);
    }

    public static RecipientEncryptedKey getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    public static RecipientEncryptedKey getInstance(Object obj) {
        if (obj instanceof RecipientEncryptedKey) {
            return (RecipientEncryptedKey) obj;
        }
        if (obj != null) {
            return new RecipientEncryptedKey(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public RecipientEncryptedKey(KeyAgreeRecipientIdentifier keyAgreeRecipientIdentifier, ASN1OctetString aSN1OctetString) {
        this.identifier = keyAgreeRecipientIdentifier;
        this.encryptedKey = aSN1OctetString;
    }

    public KeyAgreeRecipientIdentifier getIdentifier() {
        return this.identifier;
    }

    public ASN1OctetString getEncryptedKey() {
        return this.encryptedKey;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.identifier, this.encryptedKey);
    }
}
