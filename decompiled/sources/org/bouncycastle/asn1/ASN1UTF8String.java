package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ASN1UTF8String.class */
public abstract class ASN1UTF8String extends ASN1Primitive implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1UTF8String.class, 12) { // from class: org.bouncycastle.asn1.ASN1UTF8String.1
        @Override // org.bouncycastle.asn1.ASN1UniversalType
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1UTF8String.createPrimitive(dEROctetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1UTF8String getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1UTF8String)) {
            return (ASN1UTF8String) obj;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable) obj).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1UTF8String) {
                return (ASN1UTF8String) aSN1Primitive;
            }
        }
        if (!(obj instanceof byte[])) {
            throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
        }
        try {
            return (ASN1UTF8String) TYPE.fromByteArray((byte[]) obj);
        } catch (Exception e) {
            throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
        }
    }

    public static ASN1UTF8String getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return (ASN1UTF8String) TYPE.getContextInstance(aSN1TaggedObject, z);
    }

    ASN1UTF8String(String str) {
        this(Strings.toUTF8ByteArray(str), false);
    }

    ASN1UTF8String(byte[] bArr, boolean z) {
        this.contents = z ? Arrays.clone(bArr) : bArr;
    }

    @Override // org.bouncycastle.asn1.ASN1String
    public final String getString() {
        return Strings.fromUTF8ByteArray(this.contents);
    }

    public String toString() {
        return getString();
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive, org.bouncycastle.asn1.ASN1Object
    public final int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    final boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (aSN1Primitive instanceof ASN1UTF8String) {
            return Arrays.areEqual(this.contents, ((ASN1UTF8String) aSN1Primitive).contents);
        }
        return false;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    final boolean encodeConstructed() {
        return false;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    final int encodedLength(boolean z) {
        return ASN1OutputStream.getLengthOfEncodingDL(z, this.contents.length);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    final void encode(ASN1OutputStream aSN1OutputStream, boolean z) throws IOException {
        aSN1OutputStream.writeEncodingDL(z, 12, this.contents);
    }

    static ASN1UTF8String createPrimitive(byte[] bArr) {
        return new DERUTF8String(bArr, false);
    }
}
