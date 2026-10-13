package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/SymmetricEncryptionKey.class */
public class SymmetricEncryptionKey extends ASN1Object implements ASN1Choice {
    public static final int aes128ccm = 0;
    private final int choice;
    private final ASN1Encodable symmetricEncryptionKey;

    public SymmetricEncryptionKey(int i, ASN1Encodable aSN1Encodable) {
        this.choice = i;
        this.symmetricEncryptionKey = aSN1Encodable;
    }

    private SymmetricEncryptionKey(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        if (this.choice != 0) {
            throw new IllegalArgumentException("invalid choice value " + this.choice);
        }
        ASN1OctetString dEROctetString = DEROctetString.getInstance(aSN1TaggedObject.getExplicitBaseObject());
        if (dEROctetString.getOctets().length != 16) {
            throw new IllegalArgumentException("aes128ccm string not 16 bytes");
        }
        this.symmetricEncryptionKey = dEROctetString;
    }

    public static SymmetricEncryptionKey getInstance(Object obj) {
        if (obj instanceof SymmetricEncryptionKey) {
            return (SymmetricEncryptionKey) obj;
        }
        if (obj != null) {
            return new SymmetricEncryptionKey(ASN1TaggedObject.getInstance(obj, 128));
        }
        return null;
    }

    public static SymmetricEncryptionKey aes128ccm(byte[] bArr) {
        return new SymmetricEncryptionKey(0, new DEROctetString(bArr));
    }

    public static SymmetricEncryptionKey aes128ccm(ASN1OctetString aSN1OctetString) {
        return new SymmetricEncryptionKey(0, aSN1OctetString);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getSymmetricEncryptionKey() {
        return this.symmetricEncryptionKey;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.symmetricEncryptionKey);
    }
}
