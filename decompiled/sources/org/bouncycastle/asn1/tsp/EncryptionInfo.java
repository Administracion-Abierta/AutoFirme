package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DLSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/tsp/EncryptionInfo.class */
public class EncryptionInfo extends ASN1Object {
    private ASN1ObjectIdentifier encryptionInfoType;
    private ASN1Encodable encryptionInfoValue;

    public static EncryptionInfo getInstance(ASN1Object aSN1Object) {
        return getInstance((Object) aSN1Object);
    }

    public static EncryptionInfo getInstance(Object obj) {
        if (obj instanceof EncryptionInfo) {
            return (EncryptionInfo) obj;
        }
        if (obj != null) {
            return new EncryptionInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static EncryptionInfo getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return getInstance((Object) ASN1Sequence.getInstance(aSN1TaggedObject, z));
    }

    private EncryptionInfo(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("wrong sequence size in constructor: " + aSN1Sequence.size());
        }
        this.encryptionInfoType = ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
        this.encryptionInfoValue = aSN1Sequence.getObjectAt(1);
    }

    public EncryptionInfo(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Encodable aSN1Encodable) {
        this.encryptionInfoType = aSN1ObjectIdentifier;
        this.encryptionInfoValue = aSN1Encodable;
    }

    public ASN1ObjectIdentifier getEncryptionInfoType() {
        return this.encryptionInfoType;
    }

    public ASN1Encodable getEncryptionInfoValue() {
        return this.encryptionInfoValue;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DLSequence(this.encryptionInfoType, this.encryptionInfoValue);
    }
}
