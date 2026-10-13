package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ASN1NumericString.class */
public abstract class ASN1NumericString extends ASN1Primitive implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1NumericString.class, 18) { // from class: org.bouncycastle.asn1.ASN1NumericString.1
        @Override // org.bouncycastle.asn1.ASN1UniversalType
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1NumericString.createPrimitive(dEROctetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1NumericString getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1NumericString)) {
            return (ASN1NumericString) obj;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable) obj).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1NumericString) {
                return (ASN1NumericString) aSN1Primitive;
            }
        }
        if (!(obj instanceof byte[])) {
            throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
        }
        try {
            return (ASN1NumericString) TYPE.fromByteArray((byte[]) obj);
        } catch (Exception e) {
            throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
        }
    }

    public static ASN1NumericString getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return (ASN1NumericString) TYPE.getContextInstance(aSN1TaggedObject, z);
    }

    ASN1NumericString(String str, boolean z) {
        if (z && !isNumericString(str)) {
            throw new IllegalArgumentException("string contains illegal characters");
        }
        this.contents = Strings.toByteArray(str);
    }

    ASN1NumericString(byte[] bArr, boolean z) {
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
        aSN1OutputStream.writeEncodingDL(z, 18, this.contents);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive, org.bouncycastle.asn1.ASN1Object
    public final int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    final boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (aSN1Primitive instanceof ASN1NumericString) {
            return Arrays.areEqual(this.contents, ((ASN1NumericString) aSN1Primitive).contents);
        }
        return false;
    }

    public static boolean isNumericString(String str) {
        for (int length = str.length() - 1; length >= 0; length--) {
            char cCharAt = str.charAt(length);
            if (cCharAt > 127) {
                return false;
            }
            if (('0' > cCharAt || cCharAt > '9') && cCharAt != ' ') {
                return false;
            }
        }
        return true;
    }

    static boolean isNumericString(byte[] bArr) {
        for (byte b : bArr) {
            switch (b) {
                case 32:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                    break;
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                default:
                    return false;
            }
        }
        return true;
    }

    static ASN1NumericString createPrimitive(byte[] bArr) {
        return new DERNumericString(bArr, false);
    }
}
