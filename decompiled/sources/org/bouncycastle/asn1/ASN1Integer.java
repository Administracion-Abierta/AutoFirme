package org.bouncycastle.asn1;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ASN1Integer.class */
public class ASN1Integer extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1Integer.class, 2) { // from class: org.bouncycastle.asn1.ASN1Integer.1
        @Override // org.bouncycastle.asn1.ASN1UniversalType
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1Integer.createPrimitive(dEROctetString.getOctets());
        }
    };
    static final int SIGN_EXT_SIGNED = -1;
    static final int SIGN_EXT_UNSIGNED = 255;
    private final byte[] bytes;
    private final int start;

    public static ASN1Integer getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1Integer)) {
            return (ASN1Integer) obj;
        }
        if (!(obj instanceof byte[])) {
            throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
        }
        try {
            return (ASN1Integer) TYPE.fromByteArray((byte[]) obj);
        } catch (Exception e) {
            throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
        }
    }

    public static ASN1Integer getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        return (ASN1Integer) TYPE.getContextInstance(aSN1TaggedObject, z);
    }

    public ASN1Integer(long j) {
        this.bytes = BigInteger.valueOf(j).toByteArray();
        this.start = 0;
    }

    public ASN1Integer(BigInteger bigInteger) {
        this.bytes = bigInteger.toByteArray();
        this.start = 0;
    }

    public ASN1Integer(byte[] bArr) {
        this(bArr, true);
    }

    ASN1Integer(byte[] bArr, boolean z) {
        if (isMalformed(bArr)) {
            throw new IllegalArgumentException("malformed integer");
        }
        this.bytes = z ? Arrays.clone(bArr) : bArr;
        this.start = signBytesToSkip(bArr);
    }

    public BigInteger getPositiveValue() {
        return new BigInteger(1, this.bytes);
    }

    public BigInteger getValue() {
        return new BigInteger(this.bytes);
    }

    public boolean hasValue(int i) {
        return this.bytes.length - this.start <= 4 && intValue(this.bytes, this.start, -1) == i;
    }

    public boolean hasValue(long j) {
        return this.bytes.length - this.start <= 8 && longValue(this.bytes, this.start, -1) == j;
    }

    public boolean hasValue(BigInteger bigInteger) {
        return null != bigInteger && intValue(this.bytes, this.start, -1) == bigInteger.intValue() && getValue().equals(bigInteger);
    }

    public int intPositiveValueExact() {
        int length = this.bytes.length - this.start;
        if (length > 4 || (length == 4 && 0 != (this.bytes[this.start] & 128))) {
            throw new ArithmeticException("ASN.1 Integer out of positive int range");
        }
        return intValue(this.bytes, this.start, 255);
    }

    public int intValueExact() {
        if (this.bytes.length - this.start > 4) {
            throw new ArithmeticException("ASN.1 Integer out of int range");
        }
        return intValue(this.bytes, this.start, -1);
    }

    public long longValueExact() {
        if (this.bytes.length - this.start > 8) {
            throw new ArithmeticException("ASN.1 Integer out of long range");
        }
        return longValue(this.bytes, this.start, -1);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    boolean encodeConstructed() {
        return false;
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    int encodedLength(boolean z) {
        return ASN1OutputStream.getLengthOfEncodingDL(z, this.bytes.length);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    void encode(ASN1OutputStream aSN1OutputStream, boolean z) throws IOException {
        aSN1OutputStream.writeEncodingDL(z, 2, this.bytes);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive, org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    @Override // org.bouncycastle.asn1.ASN1Primitive
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (aSN1Primitive instanceof ASN1Integer) {
            return Arrays.areEqual(this.bytes, ((ASN1Integer) aSN1Primitive).bytes);
        }
        return false;
    }

    public String toString() {
        return getValue().toString();
    }

    static ASN1Integer createPrimitive(byte[] bArr) {
        return new ASN1Integer(bArr, false);
    }

    static int intValue(byte[] bArr, int i, int i2) {
        int length = bArr.length;
        int iMax = Math.max(i, length - 4);
        int i3 = bArr[iMax] & i2;
        while (true) {
            int i4 = i3;
            iMax++;
            if (iMax >= length) {
                return i4;
            }
            i3 = (i4 << 8) | (bArr[iMax] & 255);
        }
    }

    static long longValue(byte[] bArr, int i, int i2) {
        int length = bArr.length;
        int iMax = Math.max(i, length - 8);
        long j = bArr[iMax] & i2;
        while (true) {
            long j2 = j;
            iMax++;
            if (iMax >= length) {
                return j2;
            }
            j = (j2 << 8) | ((long) (bArr[iMax] & 255));
        }
    }

    static boolean isMalformed(byte[] bArr) {
        switch (bArr.length) {
            case 0:
                return true;
            case 1:
                return false;
            default:
                return bArr[0] == (bArr[1] >> 7) && !Properties.isOverrideSet("org.bouncycastle.asn1.allow_unsafe_integer");
        }
    }

    static int signBytesToSkip(byte[] bArr) {
        int i = 0;
        int length = bArr.length - 1;
        while (i < length && bArr[i] == (bArr[i + 1] >> 7)) {
            i++;
        }
        return i;
    }
}
