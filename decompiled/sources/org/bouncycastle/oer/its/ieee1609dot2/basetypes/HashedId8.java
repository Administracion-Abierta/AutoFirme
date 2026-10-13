package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/HashedId8.class */
public class HashedId8 extends HashedId {
    public HashedId8(byte[] bArr) {
        super(bArr);
        if (bArr.length != 8) {
            throw new IllegalArgumentException("hash id not 8 bytes");
        }
    }

    public static HashedId8 getInstance(Object obj) {
        if (obj instanceof HashedId8) {
            return (HashedId8) obj;
        }
        if (obj != null) {
            return new HashedId8(ASN1OctetString.getInstance(obj).getOctets());
        }
        return null;
    }
}
