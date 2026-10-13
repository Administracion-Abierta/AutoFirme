package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ASN1RelativeOID.class */
public class ASN1RelativeOID extends ASN1Primitive {
    private static final int MAX_CONTENTS_LENGTH = 4096;
    private static final int MAX_IDENTIFIER_LENGTH = 16383;
    private static final long LONG_LIMIT = 72057594037927808L;
    private final byte[] contents;
    private String identifier;
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1RelativeOID.class, 13) { // from class: org.bouncycastle.asn1.ASN1RelativeOID.1
        @Override // org.bouncycastle.asn1.ASN1UniversalType
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1RelativeOID.createPrimitive(dEROctetString.getOctets(), false);
        }
    };
    private static final ConcurrentMap<ASN1ObjectIdentifier.OidHandle, ASN1RelativeOID> pool = new ConcurrentHashMap();

    public static ASN1RelativeOID fromContents(byte[] bArr) {
        if (bArr == null) {
            throw new NullPointerException("'contents' cannot be null");
        }
        return createPrimitive(bArr, true);
    }

    public static ASN1RelativeOID getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1RelativeOID)) {
            return (ASN1RelativeOID) obj;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable) obj).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1RelativeOID) {
                return (ASN1RelativeOID) aSN1Primitive;
            }
        } else if (obj instanceof byte[]) {
            try {
                return (ASN1RelativeOID) TYPE.fromByteArray((byte[]) obj);
            } catch (IOException e) {
                throw new IllegalArgumentException("failed to construct relative OID from byte[]: " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1RelativeOID getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return (ASN1RelativeOID) TYPE.getContextInstance(aSN1TaggedObject, z);
    }

    public static ASN1RelativeOID tryFromID(String str) {
        if (str == null) {
            throw new NullPointerException("'identifier' cannot be null");
        }
        if (str.length() > 16383 || !isValidIdentifier(str, 0)) {
            return null;
        }
        byte[] identifier = parseIdentifier(str);
        if (identifier.length <= 4096) {
            return new ASN1RelativeOID(identifier, str);
        }
        return null;
    }

    public ASN1RelativeOID(String str) {
        checkIdentifier(str);
        byte[] identifier = parseIdentifier(str);
        checkContentsLength(identifier.length);
        this.contents = identifier;
        this.identifier = str;
    }

    private ASN1RelativeOID(byte[] bArr, String str) {
        this.contents = bArr;
        this.identifier = str;
    }

    public ASN1RelativeOID branch(String str) {
        byte[] bArrConcatenate;
        checkIdentifier(str);
        if (str.length() <= 2) {
            checkContentsLength(this.contents.length + 1);
            int iCharAt = str.charAt(0) - '0';
            if (str.length() == 2) {
                iCharAt = (iCharAt * 10) + (str.charAt(1) - '0');
            }
            bArrConcatenate = Arrays.append(this.contents, (byte) iCharAt);
        } else {
            byte[] identifier = parseIdentifier(str);
            checkContentsLength(this.contents.length + identifier.length);
            bArrConcatenate = Arrays.concatenate(this.contents, identifier);
        }
        return new ASN1RelativeOID(bArrConcatenate, getId() + "." + str);
    }

    public synchronized String getId() {
        if (this.identifier == null) {
            this.identifier = parseContents(this.contents);
        }
        return this.identifier;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive, org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    public String toString() {
        return getId();
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (this == aSN1Primitive) {
            return true;
        }
        if (aSN1Primitive instanceof ASN1RelativeOID) {
            return Arrays.areEqual(this.contents, ((ASN1RelativeOID) aSN1Primitive).contents);
        }
        return false;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    int encodedLength(boolean z) {
        return ASN1OutputStream.getLengthOfEncodingDL(z, this.contents.length);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    void encode(ASN1OutputStream aSN1OutputStream, boolean z) throws IOException {
        aSN1OutputStream.writeEncodingDL(z, 13, this.contents);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    boolean encodeConstructed() {
        return false;
    }

    static void checkContentsLength(int i) {
        if (i > 4096) {
            throw new IllegalArgumentException("exceeded relative OID contents length limit");
        }
    }

    static void checkIdentifier(String str) {
        if (str == null) {
            throw new NullPointerException("'identifier' cannot be null");
        }
        if (str.length() > 16383) {
            throw new IllegalArgumentException("exceeded relative OID contents length limit");
        }
        if (!isValidIdentifier(str, 0)) {
            throw new IllegalArgumentException("string " + str + " not a valid relative OID");
        }
    }

    static ASN1RelativeOID createPrimitive(byte[] bArr, boolean z) {
        checkContentsLength(bArr.length);
        ASN1RelativeOID aSN1RelativeOID = pool.get(new ASN1ObjectIdentifier.OidHandle(bArr));
        if (aSN1RelativeOID != null) {
            return aSN1RelativeOID;
        }
        if (isValidContents(bArr)) {
            return new ASN1RelativeOID(z ? Arrays.clone(bArr) : bArr, null);
        }
        throw new IllegalArgumentException("invalid relative OID contents");
    }

    static boolean isValidContents(byte[] bArr) {
        if (Properties.isOverrideSet("org.bouncycastle.asn1.allow_wrong_oid_enc")) {
            return true;
        }
        if (bArr.length < 1) {
            return false;
        }
        boolean z = true;
        for (int i = 0; i < bArr.length; i++) {
            if (z && (bArr[i] & 255) == 128) {
                return false;
            }
            z = (bArr[i] & 128) == 0;
        }
        return z;
    }

    static boolean isValidIdentifier(String str, int i) {
        int i2 = 0;
        int length = str.length();
        while (true) {
            length--;
            if (length < i) {
                if (0 != i2) {
                    return i2 <= 1 || str.charAt(length + 1) != '0';
                }
                return false;
            }
            char cCharAt = str.charAt(length);
            if (cCharAt == '.') {
                if (0 == i2) {
                    return false;
                }
                if (i2 > 1 && str.charAt(length + 1) == '0') {
                    return false;
                }
                i2 = 0;
            } else {
                if ('0' > cCharAt || cCharAt > '9') {
                    return false;
                }
                i2++;
            }
        }
    }

    static String parseContents(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        long j = 0;
        BigInteger bigIntegerShiftLeft = null;
        boolean z = true;
        for (int i = 0; i != bArr.length; i++) {
            int i2 = bArr[i] & 255;
            if (j <= LONG_LIMIT) {
                long j2 = j + ((long) (i2 & 127));
                if ((i2 & 128) == 0) {
                    if (z) {
                        z = false;
                    } else {
                        sb.append('.');
                    }
                    sb.append(j2);
                    j = 0;
                } else {
                    j = j2 << 7;
                }
            } else {
                if (bigIntegerShiftLeft == null) {
                    bigIntegerShiftLeft = BigInteger.valueOf(j);
                }
                BigInteger bigIntegerOr = bigIntegerShiftLeft.or(BigInteger.valueOf(i2 & 127));
                if ((i2 & 128) == 0) {
                    if (z) {
                        z = false;
                    } else {
                        sb.append('.');
                    }
                    sb.append(bigIntegerOr);
                    bigIntegerShiftLeft = null;
                    j = 0;
                } else {
                    bigIntegerShiftLeft = bigIntegerOr.shiftLeft(7);
                }
            }
        }
        return sb.toString();
    }

    static byte[] parseIdentifier(String str) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OIDTokenizer oIDTokenizer = new OIDTokenizer(str);
        while (oIDTokenizer.hasMoreTokens()) {
            String strNextToken = oIDTokenizer.nextToken();
            if (strNextToken.length() <= 18) {
                writeField(byteArrayOutputStream, Long.parseLong(strNextToken));
            } else {
                writeField(byteArrayOutputStream, new BigInteger(strNextToken));
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    static void writeField(ByteArrayOutputStream byteArrayOutputStream, long j) {
        byte[] bArr = new byte[9];
        int i = 8;
        bArr[8] = (byte) (((int) j) & 127);
        while (j >= 128) {
            j >>= 7;
            i--;
            bArr[i] = (byte) (((int) j) | 128);
        }
        byteArrayOutputStream.write(bArr, i, 9 - i);
    }

    static void writeField(ByteArrayOutputStream byteArrayOutputStream, BigInteger bigInteger) {
        int iBitLength = (bigInteger.bitLength() + 6) / 7;
        if (iBitLength == 0) {
            byteArrayOutputStream.write(0);
            return;
        }
        BigInteger bigIntegerShiftRight = bigInteger;
        byte[] bArr = new byte[iBitLength];
        for (int i = iBitLength - 1; i >= 0; i--) {
            bArr[i] = (byte) (bigIntegerShiftRight.intValue() | 128);
            bigIntegerShiftRight = bigIntegerShiftRight.shiftRight(7);
        }
        int i2 = iBitLength - 1;
        bArr[i2] = (byte) (bArr[i2] & 127);
        byteArrayOutputStream.write(bArr, 0, bArr.length);
    }
}
