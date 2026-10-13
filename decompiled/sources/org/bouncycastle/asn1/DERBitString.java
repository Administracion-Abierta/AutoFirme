package org.bouncycastle.asn1;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/DERBitString.class */
public class DERBitString extends ASN1BitString {
    public static DERBitString convert(ASN1BitString aSN1BitString) {
        return (DERBitString) aSN1BitString.toDERObject();
    }

    public DERBitString(byte[] bArr) {
        this(bArr, 0);
    }

    public DERBitString(byte b, int i) {
        super(b, i);
    }

    public DERBitString(byte[] bArr, int i) {
        super(bArr, i);
    }

    public DERBitString(int i) {
        super(getBytes(i), getPadBits(i));
    }

    public DERBitString(ASN1Encodable aSN1Encodable) throws IOException {
        super(aSN1Encodable.toASN1Primitive().getEncoded("DER"), 0);
    }

    DERBitString(byte[] bArr, boolean z) {
        super(bArr, z);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    boolean encodeConstructed() {
        return false;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    int encodedLength(boolean z) {
        return ASN1OutputStream.getLengthOfEncodingDL(z, this.contents.length);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    void encode(ASN1OutputStream aSN1OutputStream, boolean z) throws IOException {
        int i = this.contents[0] & 255;
        int length = this.contents.length - 1;
        byte b = this.contents[length];
        byte b2 = (byte) (this.contents[length] & (255 << i));
        if (b == b2) {
            aSN1OutputStream.writeEncodingDL(z, 3, this.contents);
        } else {
            aSN1OutputStream.writeEncodingDL(z, 3, this.contents, 0, length, b2);
        }
    }

    @Override // org.bouncycastle.asn1.ASN1BitString, org.bouncycastle.asn1.ASN1Primitive
    ASN1Primitive toDERObject() {
        return this;
    }

    @Override // org.bouncycastle.asn1.ASN1BitString, org.bouncycastle.asn1.ASN1Primitive
    ASN1Primitive toDLObject() {
        return this;
    }

    static DERBitString fromOctetString(ASN1OctetString aSN1OctetString) {
        return new DERBitString(aSN1OctetString.getOctets(), true);
    }
}
