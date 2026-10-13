package org.bouncycastle.asn1;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/DERSet.class */
public class DERSet extends ASN1Set {
    private int contentsLength;

    public static DERSet convert(ASN1Set aSN1Set) {
        return (DERSet) aSN1Set.toDERObject();
    }

    public DERSet() {
        this.contentsLength = -1;
    }

    public DERSet(ASN1Encodable aSN1Encodable) {
        super(aSN1Encodable);
        this.contentsLength = -1;
    }

    public DERSet(ASN1EncodableVector aSN1EncodableVector) {
        super(aSN1EncodableVector, true);
        this.contentsLength = -1;
    }

    public DERSet(ASN1Encodable[] aSN1EncodableArr) {
        super(aSN1EncodableArr, true);
        this.contentsLength = -1;
    }

    DERSet(boolean z, ASN1Encodable[] aSN1EncodableArr) {
        super(checkSorted(z), aSN1EncodableArr);
        this.contentsLength = -1;
    }

    private int getContentsLength() throws IOException {
        if (this.contentsLength < 0) {
            int length = this.elements.length;
            int iEncodedLength = 0;
            for (int i = 0; i < length; i++) {
                iEncodedLength += this.elements[i].toASN1Primitive().toDERObject().encodedLength(true);
            }
            this.contentsLength = iEncodedLength;
        }
        return this.contentsLength;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    int encodedLength(boolean z) throws IOException {
        return ASN1OutputStream.getLengthOfEncodingDL(z, getContentsLength());
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    void encode(ASN1OutputStream aSN1OutputStream, boolean z) throws IOException {
        aSN1OutputStream.writeIdentifier(z, 49);
        DEROutputStream dERSubStream = aSN1OutputStream.getDERSubStream();
        int length = this.elements.length;
        if (this.contentsLength >= 0 || length > 16) {
            aSN1OutputStream.writeDL(getContentsLength());
            for (int i = 0; i < length; i++) {
                this.elements[i].toASN1Primitive().toDERObject().encode(dERSubStream, true);
            }
            return;
        }
        int iEncodedLength = 0;
        ASN1Primitive[] aSN1PrimitiveArr = new ASN1Primitive[length];
        for (int i2 = 0; i2 < length; i2++) {
            ASN1Primitive dERObject = this.elements[i2].toASN1Primitive().toDERObject();
            aSN1PrimitiveArr[i2] = dERObject;
            iEncodedLength += dERObject.encodedLength(true);
        }
        this.contentsLength = iEncodedLength;
        aSN1OutputStream.writeDL(iEncodedLength);
        for (int i3 = 0; i3 < length; i3++) {
            aSN1PrimitiveArr[i3].encode(dERSubStream, true);
        }
    }

    @Override // org.bouncycastle.asn1.ASN1Set, org.bouncycastle.asn1.ASN1Primitive
    ASN1Primitive toDERObject() {
        return this.sortedElements != null ? this : super.toDERObject();
    }

    @Override // org.bouncycastle.asn1.ASN1Set, org.bouncycastle.asn1.ASN1Primitive
    ASN1Primitive toDLObject() {
        return this;
    }

    private static boolean checkSorted(boolean z) {
        if (z) {
            return z;
        }
        throw new IllegalStateException("DERSet elements should always be in sorted order");
    }
}
