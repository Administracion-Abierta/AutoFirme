package org.bouncycastle.asn1;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ASN1UniversalType.class */
abstract class ASN1UniversalType extends ASN1Type {
    final ASN1Tag tag;

    ASN1UniversalType(Class cls, int i) {
        super(cls);
        this.tag = ASN1Tag.create(0, i);
    }

    final ASN1Primitive checkedCast(ASN1Primitive aSN1Primitive) {
        if (this.javaClass.isInstance(aSN1Primitive)) {
            return aSN1Primitive;
        }
        throw new IllegalStateException("unexpected object: " + aSN1Primitive.getClass().getName());
    }

    ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
        throw new IllegalStateException("unexpected implicit primitive encoding");
    }

    ASN1Primitive fromImplicitConstructed(ASN1Sequence aSN1Sequence) {
        throw new IllegalStateException("unexpected implicit constructed encoding");
    }

    final ASN1Primitive fromByteArray(byte[] bArr) throws IOException {
        return checkedCast(ASN1Primitive.fromByteArray(bArr));
    }

    final ASN1Primitive getContextInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return checkedCast(ASN1Util.checkContextTagClass(aSN1TaggedObject).getBaseUniversal(z, this));
    }

    final ASN1Tag getTag() {
        return this.tag;
    }
}
