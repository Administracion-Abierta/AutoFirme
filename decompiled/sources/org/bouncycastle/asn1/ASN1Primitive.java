package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ASN1Primitive.class */
public abstract class ASN1Primitive extends ASN1Object {
    ASN1Primitive() {
    }

    @Override // org.bouncycastle.asn1.ASN1Object
    public void encodeTo(OutputStream outputStream) throws IOException {
        ASN1OutputStream aSN1OutputStreamCreate = ASN1OutputStream.create(outputStream);
        aSN1OutputStreamCreate.writePrimitive(this, true);
        aSN1OutputStreamCreate.flushInternal();
    }

    @Override // org.bouncycastle.asn1.ASN1Object
    public void encodeTo(OutputStream outputStream, String str) throws IOException {
        ASN1OutputStream aSN1OutputStreamCreate = ASN1OutputStream.create(outputStream, str);
        aSN1OutputStreamCreate.writePrimitive(this, true);
        aSN1OutputStreamCreate.flushInternal();
    }

    public static ASN1Primitive fromByteArray(byte[] bArr) throws IOException {
        ASN1InputStream aSN1InputStream = new ASN1InputStream(bArr);
        try {
            ASN1Primitive object = aSN1InputStream.readObject();
            if (aSN1InputStream.available() != 0) {
                throw new IOException("Extra data detected in stream");
            }
            return object;
        } catch (ClassCastException e) {
            throw new IOException("cannot recognise object in stream");
        }
    }

    @Override // org.bouncycastle.asn1.ASN1Object
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof ASN1Encodable) && asn1Equals(((ASN1Encodable) obj).toASN1Primitive());
    }

    public final boolean equals(ASN1Encodable aSN1Encodable) {
        return this == aSN1Encodable || (null != aSN1Encodable && asn1Equals(aSN1Encodable.toASN1Primitive()));
    }

    public final boolean equals(ASN1Primitive aSN1Primitive) {
        return this == aSN1Primitive || asn1Equals(aSN1Primitive);
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public final ASN1Primitive toASN1Primitive() {
        return this;
    }

    ASN1Primitive toDERObject() {
        return this;
    }

    ASN1Primitive toDLObject() {
        return this;
    }

    @Override // org.bouncycastle.asn1.ASN1Object
    public abstract int hashCode();

    abstract boolean encodeConstructed();

    abstract int encodedLength(boolean z) throws IOException;

    abstract void encode(ASN1OutputStream aSN1OutputStream, boolean z) throws IOException;

    abstract boolean asn1Equals(ASN1Primitive aSN1Primitive);
}
