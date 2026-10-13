package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/HashedId.class */
public class HashedId extends ASN1Object {
    private final byte[] id;

    protected HashedId(byte[] bArr) {
        this.id = Arrays.clone(bArr);
    }

    public byte[] getHashBytes() {
        return Arrays.clone(this.id);
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DEROctetString(this.id);
    }

    @Override // org.bouncycastle.asn1.ASN1Object
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass() && super.equals(obj)) {
            return java.util.Arrays.equals(this.id, ((HashedId) obj).id);
        }
        return false;
    }

    @Override // org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        return (31 * super.hashCode()) + java.util.Arrays.hashCode(this.id);
    }
}
