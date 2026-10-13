package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/HashedId32.class */
public class HashedId32 extends HashedId {
    public HashedId32(byte[] bArr) {
        super(bArr);
        if (bArr.length != 32) {
            throw new IllegalArgumentException("hash id not 32 bytes");
        }
    }

    public static HashedId32 getInstance(Object obj) {
        if (obj instanceof HashedId32) {
            return (HashedId32) obj;
        }
        if (obj != null) {
            return new HashedId32(ASN1OctetString.getInstance(obj).getOctets());
        }
        return null;
    }
}
