package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/LaId.class */
public class LaId extends ASN1Object {
    private final byte[] laId;

    public LaId(byte[] bArr) {
        this.laId = bArr;
        assertLength();
    }

    private LaId(ASN1OctetString aSN1OctetString) {
        this(aSN1OctetString.getOctets());
    }

    public static LaId getInstance(Object obj) {
        if (obj instanceof LaId) {
            return (LaId) obj;
        }
        if (obj != null) {
            return new LaId(DEROctetString.getInstance(obj));
        }
        return null;
    }

    private void assertLength() {
        if (this.laId.length != 2) {
            throw new IllegalArgumentException("laId must be 2 octets");
        }
    }

    public byte[] getLaId() {
        return Arrays.clone(this.laId);
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DEROctetString(this.laId);
    }
}
