package org.bouncycastle.asn1.eac;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/eac/EACTagged.class */
class EACTagged {
    EACTagged() {
    }

    static ASN1TaggedObject create(int i, ASN1Sequence aSN1Sequence) {
        return new DERTaggedObject(false, 64, i, (ASN1Encodable) aSN1Sequence);
    }

    static ASN1TaggedObject create(int i, PublicKeyDataObject publicKeyDataObject) {
        return new DERTaggedObject(false, 64, i, (ASN1Encodable) publicKeyDataObject);
    }

    static ASN1TaggedObject create(int i, byte[] bArr) {
        return new DERTaggedObject(false, 64, i, (ASN1Encodable) new DEROctetString(bArr));
    }
}
