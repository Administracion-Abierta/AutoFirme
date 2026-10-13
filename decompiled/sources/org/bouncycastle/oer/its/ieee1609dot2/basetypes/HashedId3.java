package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/HashedId3.class */
public class HashedId3 extends HashedId {
    public HashedId3(byte[] bArr) {
        super(bArr);
        if (bArr.length != 3) {
            throw new IllegalArgumentException("hash id not 3 bytes");
        }
    }

    public static HashedId3 getInstance(Object obj) {
        if (obj instanceof HashedId3) {
            return (HashedId3) obj;
        }
        if (obj != null) {
            return new HashedId3(ASN1OctetString.getInstance(obj).getOctets());
        }
        return null;
    }
}
