package org.bouncycastle.asn1;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/BEROctetString.class */
public class BEROctetString extends ASN1OctetString {
    private static final int DEFAULT_SEGMENT_LIMIT = 1000;
    private final int segmentLimit;
    private final ASN1OctetString[] elements;

    static byte[] flattenOctetStrings(ASN1OctetString[] aSN1OctetStringArr) {
        switch (aSN1OctetStringArr.length) {
            case 0:
                return EMPTY_OCTETS;
            case 1:
                return aSN1OctetStringArr[0].string;
            default:
                int length = 0;
                for (ASN1OctetString aSN1OctetString : aSN1OctetStringArr) {
                    length += aSN1OctetString.string.length;
                }
                byte[] bArr = new byte[length];
                int length2 = 0;
                for (ASN1OctetString aSN1OctetString2 : aSN1OctetStringArr) {
                    byte[] bArr2 = aSN1OctetString2.string;
                    System.arraycopy(bArr2, 0, bArr, length2, bArr2.length);
                    length2 += bArr2.length;
                }
                return bArr;
        }
    }

    public BEROctetString(byte[] bArr) {
        this(bArr, 1000);
    }

    public BEROctetString(ASN1OctetString[] aSN1OctetStringArr) {
        this(aSN1OctetStringArr, 1000);
    }

    public BEROctetString(byte[] bArr, int i) {
        this(bArr, null, i);
    }

    public BEROctetString(ASN1OctetString[] aSN1OctetStringArr, int i) {
        this(flattenOctetStrings(aSN1OctetStringArr), aSN1OctetStringArr, i);
    }

    private BEROctetString(byte[] bArr, ASN1OctetString[] aSN1OctetStringArr, int i) {
        super(bArr);
        this.elements = aSN1OctetStringArr;
        this.segmentLimit = i;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    boolean encodeConstructed() {
        return true;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    int encodedLength(boolean z) throws IOException {
        int iEncodedLength = z ? 4 : 3;
        if (null != this.elements) {
            for (int i = 0; i < this.elements.length; i++) {
                iEncodedLength += this.elements[i].encodedLength(true);
            }
        } else {
            int length = this.string.length / this.segmentLimit;
            iEncodedLength += length * DEROctetString.encodedLength(true, this.segmentLimit);
            int length2 = this.string.length - (length * this.segmentLimit);
            if (length2 > 0) {
                iEncodedLength += DEROctetString.encodedLength(true, length2);
            }
        }
        return iEncodedLength;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    void encode(ASN1OutputStream aSN1OutputStream, boolean z) throws IOException {
        aSN1OutputStream.writeIdentifier(z, 36);
        aSN1OutputStream.write(128);
        if (null == this.elements) {
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= this.string.length) {
                    break;
                }
                int iMin = Math.min(this.string.length - i2, this.segmentLimit);
                DEROctetString.encode(aSN1OutputStream, true, this.string, i2, iMin);
                i = i2 + iMin;
            }
        } else {
            aSN1OutputStream.writePrimitives(this.elements);
        }
        aSN1OutputStream.write(0);
        aSN1OutputStream.write(0);
    }
}
