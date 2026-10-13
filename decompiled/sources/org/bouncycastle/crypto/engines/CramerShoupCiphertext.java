package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/CramerShoupCiphertext.class */
public class CramerShoupCiphertext {
    BigInteger u1;
    BigInteger u2;
    BigInteger e;
    BigInteger v;

    public CramerShoupCiphertext() {
    }

    public CramerShoupCiphertext(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, BigInteger bigInteger4) {
        this.u1 = bigInteger;
        this.u2 = bigInteger2;
        this.e = bigInteger3;
        this.v = bigInteger4;
    }

    public CramerShoupCiphertext(byte[] bArr) {
        int iBigEndianToInt = Pack.bigEndianToInt(bArr, 0);
        int i = 0 + 4;
        byte[] bArrCopyOfRange = Arrays.copyOfRange(bArr, i, i + iBigEndianToInt);
        int i2 = i + iBigEndianToInt;
        this.u1 = new BigInteger(bArrCopyOfRange);
        int iBigEndianToInt2 = Pack.bigEndianToInt(bArr, i2);
        int i3 = i2 + 4;
        byte[] bArrCopyOfRange2 = Arrays.copyOfRange(bArr, i3, i3 + iBigEndianToInt2);
        int i4 = i3 + iBigEndianToInt2;
        this.u2 = new BigInteger(bArrCopyOfRange2);
        int iBigEndianToInt3 = Pack.bigEndianToInt(bArr, i4);
        int i5 = i4 + 4;
        byte[] bArrCopyOfRange3 = Arrays.copyOfRange(bArr, i5, i5 + iBigEndianToInt3);
        int i6 = i5 + iBigEndianToInt3;
        this.e = new BigInteger(bArrCopyOfRange3);
        int iBigEndianToInt4 = Pack.bigEndianToInt(bArr, i6);
        int i7 = i6 + 4;
        byte[] bArrCopyOfRange4 = Arrays.copyOfRange(bArr, i7, i7 + iBigEndianToInt4);
        int i8 = i7 + iBigEndianToInt4;
        this.v = new BigInteger(bArrCopyOfRange4);
    }

    public BigInteger getU1() {
        return this.u1;
    }

    public void setU1(BigInteger bigInteger) {
        this.u1 = bigInteger;
    }

    public BigInteger getU2() {
        return this.u2;
    }

    public void setU2(BigInteger bigInteger) {
        this.u2 = bigInteger;
    }

    public BigInteger getE() {
        return this.e;
    }

    public void setE(BigInteger bigInteger) {
        this.e = bigInteger;
    }

    public BigInteger getV() {
        return this.v;
    }

    public void setV(BigInteger bigInteger) {
        this.v = bigInteger;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("u1: " + this.u1.toString());
        stringBuffer.append("\nu2: " + this.u2.toString());
        stringBuffer.append("\ne: " + this.e.toString());
        stringBuffer.append("\nv: " + this.v.toString());
        return stringBuffer.toString();
    }

    public byte[] toByteArray() {
        byte[] byteArray = this.u1.toByteArray();
        int length = byteArray.length;
        byte[] byteArray2 = this.u2.toByteArray();
        int length2 = byteArray2.length;
        byte[] byteArray3 = this.e.toByteArray();
        int length3 = byteArray3.length;
        byte[] byteArray4 = this.v.toByteArray();
        int length4 = byteArray4.length;
        byte[] bArr = new byte[length + length2 + length3 + length4 + 16];
        Pack.intToBigEndian(length, bArr, 0);
        int i = 0 + 4;
        System.arraycopy(byteArray, 0, bArr, i, length);
        int i2 = i + length;
        Pack.intToBigEndian(length2, bArr, i2);
        int i3 = i2 + 4;
        System.arraycopy(byteArray2, 0, bArr, i3, length2);
        int i4 = i3 + length2;
        Pack.intToBigEndian(length3, bArr, i4);
        int i5 = i4 + 4;
        System.arraycopy(byteArray3, 0, bArr, i5, length3);
        int i6 = i5 + length3;
        Pack.intToBigEndian(length4, bArr, i6);
        int i7 = i6 + 4;
        System.arraycopy(byteArray4, 0, bArr, i7, length4);
        int i8 = i7 + length4;
        return bArr;
    }
}
