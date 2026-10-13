package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ASN1IA5String.class */
public abstract class ASN1IA5String extends ASN1Primitive implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1IA5String.class, 22) { // from class: org.bouncycastle.asn1.ASN1IA5String.1
        @Override // org.bouncycastle.asn1.ASN1UniversalType
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1IA5String.createPrimitive(dEROctetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1IA5String getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1IA5String)) {
            return (ASN1IA5String) obj;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable) obj).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1IA5String) {
                return (ASN1IA5String) aSN1Primitive;
            }
        }
        if (!(obj instanceof byte[])) {
            throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
        }
        try {
            return (ASN1IA5String) TYPE.fromByteArray((byte[]) obj);
        } catch (Exception e) {
            throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
        }
    }

    public static ASN1IA5String getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return (ASN1IA5String) TYPE.getContextInstance(aSN1TaggedObject, z);
    }

    ASN1IA5String(String str, boolean z) {
        if (str == null) {
            throw new NullPointerException("'string' cannot be null");
        }
        if (z && !isIA5String(str)) {
            throw new IllegalArgumentException("'string' contains illegal characters");
        }
        this.contents = Strings.toByteArray(str);
    }

    ASN1IA5String(byte[] bArr, boolean z) {
        this.contents = z ? Arrays.clone(bArr) : bArr;
    }

    @Override // org.bouncycastle.asn1.ASN1String
    public final String getString() {
        return Strings.fromByteArray(this.contents);
    }

    public String toString() {
        return getString();
    }

    public final byte[] getOctets() {
        return Arrays.clone(this.contents);
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
        aSN1OutputStream.writeEncodingDL(z, 22, this.contents);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    final boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (aSN1Primitive instanceof ASN1IA5String) {
            return Arrays.areEqual(this.contents, ((ASN1IA5String) aSN1Primitive).contents);
        }
        return false;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive, org.bouncycastle.asn1.ASN1Object
    public final int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    public static boolean isIA5String(String str) {
        for (int length = str.length() - 1; length >= 0; length--) {
            if (str.charAt(length) > 127) {
                return false;
            }
        }
        return true;
    }

    static ASN1IA5String createPrimitive(byte[] bArr) {
        return new DERIA5String(bArr, false);
    }
}
