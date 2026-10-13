package org.bouncycastle.asn1;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/DLTaggedObject.class */
public class DLTaggedObject extends ASN1TaggedObject {
    public DLTaggedObject(int i, ASN1Encodable aSN1Encodable) {
        super(true, i, aSN1Encodable);
    }

    public DLTaggedObject(int i, int i2, ASN1Encodable aSN1Encodable) {
        super(true, i, i2, aSN1Encodable);
    }

    public DLTaggedObject(boolean z, int i, ASN1Encodable aSN1Encodable) {
        super(z, i, aSN1Encodable);
    }

    public DLTaggedObject(boolean z, int i, int i2, ASN1Encodable aSN1Encodable) {
        super(z, i, i2, aSN1Encodable);
    }

    DLTaggedObject(int i, int i2, int i3, ASN1Encodable aSN1Encodable) {
        super(i, i2, i3, aSN1Encodable);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    boolean encodeConstructed() {
        return isExplicit() || this.obj.toASN1Primitive().toDLObject().encodeConstructed();
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    int encodedLength(boolean z) throws IOException {
        ASN1Primitive dLObject = this.obj.toASN1Primitive().toDLObject();
        boolean zIsExplicit = isExplicit();
        int iEncodedLength = dLObject.encodedLength(zIsExplicit);
        if (zIsExplicit) {
            iEncodedLength += ASN1OutputStream.getLengthOfDL(iEncodedLength);
        }
        return iEncodedLength + (z ? ASN1OutputStream.getLengthOfIdentifier(this.tagNo) : 0);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    void encode(ASN1OutputStream aSN1OutputStream, boolean z) throws IOException {
        ASN1Primitive dLObject = this.obj.toASN1Primitive().toDLObject();
        boolean zIsExplicit = isExplicit();
        if (z) {
            int i = this.tagClass;
            if (zIsExplicit || dLObject.encodeConstructed()) {
                i |= 32;
            }
            aSN1OutputStream.writeIdentifier(true, i, this.tagNo);
        }
        if (zIsExplicit) {
            aSN1OutputStream.writeDL(dLObject.encodedLength(true));
        }
        dLObject.encode(aSN1OutputStream.getDLSubStream(), zIsExplicit);
    }

    @Override // org.bouncycastle.asn1.ASN1TaggedObject
    ASN1Sequence rebuildConstructed(ASN1Primitive aSN1Primitive) {
        return new DLSequence(aSN1Primitive);
    }

    @Override // org.bouncycastle.asn1.ASN1TaggedObject
    ASN1TaggedObject replaceTag(int i, int i2) {
        return new DLTaggedObject(this.explicitness, i, i2, this.obj);
    }

    @Override // org.bouncycastle.asn1.ASN1TaggedObject, org.bouncycastle.asn1.ASN1Primitive
    ASN1Primitive toDLObject() {
        return this;
    }
}
