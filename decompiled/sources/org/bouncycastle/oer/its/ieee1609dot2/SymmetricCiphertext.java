package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/SymmetricCiphertext.class */
public class SymmetricCiphertext extends ASN1Object implements ASN1Choice {
    public static final int aes128ccm = 0;
    private final int choice;
    private final ASN1Encodable symmetricCiphertext;

    public SymmetricCiphertext(int i, ASN1Encodable aSN1Encodable) {
        this.choice = i;
        this.symmetricCiphertext = aSN1Encodable;
    }

    private SymmetricCiphertext(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (this.choice) {
            case 0:
                this.symmetricCiphertext = AesCcmCiphertext.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            default:
                throw new IllegalArgumentException("invalid choice value " + this.choice);
        }
    }

    public static SymmetricCiphertext aes128ccm(AesCcmCiphertext aesCcmCiphertext) {
        return new SymmetricCiphertext(0, aesCcmCiphertext);
    }

    public static SymmetricCiphertext getInstance(Object obj) {
        if (obj instanceof SymmetricCiphertext) {
            return (SymmetricCiphertext) obj;
        }
        if (obj != null) {
            return new SymmetricCiphertext(ASN1TaggedObject.getInstance(obj, 128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getSymmetricCiphertext() {
        return this.symmetricCiphertext;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.symmetricCiphertext);
    }
}
