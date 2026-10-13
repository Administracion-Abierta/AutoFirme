package es.gob.afirma.core.signers.der;

import com.sun.jna.platform.win32.WinNT;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/signers/der/ObjectIdentifier.class */
public final class ObjectIdentifier implements Serializable {
    private byte[] encoding;
    private volatile transient String stringForm;
    private static final long serialVersionUID = 8697030238860181294L;
    private Object components;
    private int componentLen;
    private transient boolean componentsCalculated;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !ObjectIdentifier.class.desiredAssertionStatus();
    }

    private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        if (this.encoding == null) {
            int[] iArr = (int[]) this.components;
            if (this.componentLen > iArr.length) {
                this.componentLen = iArr.length;
            }
            init(iArr, this.componentLen);
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        if (!this.componentsCalculated) {
            int[] intArray = toIntArray();
            if (intArray != null) {
                this.components = intArray;
                this.componentLen = intArray.length;
            } else {
                this.components = HugeOidNotSupportedByOldJDK.theOne;
            }
            this.componentsCalculated = true;
        }
        objectOutputStream.defaultWriteObject();
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/signers/der/ObjectIdentifier$HugeOidNotSupportedByOldJDK.class */
    static class HugeOidNotSupportedByOldJDK implements Serializable {
        private static final long serialVersionUID = 1;
        static HugeOidNotSupportedByOldJDK theOne = new HugeOidNotSupportedByOldJDK();

        HugeOidNotSupportedByOldJDK() {
        }
    }

    public ObjectIdentifier(String str) throws IOException {
        int iIndexOf;
        String strSubstring;
        int length;
        this.encoding = null;
        this.components = null;
        this.componentLen = -1;
        this.componentsCalculated = false;
        int i = 0;
        int iPack7Oid = 0;
        byte[] bArr = new byte[str.length()];
        int iIntValue = 0;
        int i2 = 0;
        do {
            try {
                iIndexOf = str.indexOf(46, i);
                if (iIndexOf == -1) {
                    strSubstring = str.substring(i);
                    length = str.length() - i;
                } else {
                    strSubstring = str.substring(i, iIndexOf);
                    length = iIndexOf - i;
                }
                if (length > 9) {
                    BigInteger bigInteger = new BigInteger(strSubstring);
                    if (i2 == 0) {
                        checkFirstComponent(bigInteger);
                        iIntValue = bigInteger.intValue();
                    } else {
                        if (i2 == 1) {
                            checkSecondComponent(iIntValue, bigInteger);
                            bigInteger = bigInteger.add(BigInteger.valueOf(40 * iIntValue));
                        } else {
                            checkOtherComponent(i2, bigInteger);
                        }
                        iPack7Oid += pack7Oid(bigInteger, bArr, iPack7Oid);
                    }
                } else {
                    int i3 = Integer.parseInt(strSubstring);
                    if (i2 == 0) {
                        checkFirstComponent(i3);
                        iIntValue = i3;
                    } else {
                        if (i2 == 1) {
                            checkSecondComponent(iIntValue, i3);
                            i3 += 40 * iIntValue;
                        } else {
                            checkOtherComponent(i2, i3);
                        }
                        iPack7Oid += pack7Oid(i3, bArr, iPack7Oid);
                    }
                }
                i = iIndexOf + 1;
                i2++;
            } catch (IOException e) {
                throw e;
            } catch (Exception e2) {
                throw new IOException("ObjectIdentifier() -- Invalid format: " + e2.toString(), e2);
            }
        } while (iIndexOf != -1);
        checkCount(i2);
        this.encoding = new byte[iPack7Oid];
        System.arraycopy(bArr, 0, this.encoding, 0, iPack7Oid);
        this.stringForm = str;
    }

    public ObjectIdentifier(int[] iArr) throws IOException {
        this.encoding = null;
        this.components = null;
        this.componentLen = -1;
        this.componentsCalculated = false;
        checkCount(iArr.length);
        checkFirstComponent(iArr[0]);
        checkSecondComponent(iArr[0], iArr[1]);
        for (int i = 2; i < iArr.length; i++) {
            checkOtherComponent(i, iArr[i]);
        }
        init(iArr, iArr.length);
    }

    public ObjectIdentifier(DerInputStream derInputStream) throws IOException {
        this.encoding = null;
        this.components = null;
        this.componentLen = -1;
        this.componentsCalculated = false;
        byte b = (byte) derInputStream.getByte();
        if (b != 6) {
            throw new IOException("ObjectIdentifier() -- data isn't an object ID (tag = " + ((int) b) + ")");
        }
        int length = derInputStream.getLength();
        if (length > derInputStream.available()) {
            throw new IOException("ObjectIdentifier() -- length exceedsdata available.  Length: " + length + ", Available: " + derInputStream.available());
        }
        this.encoding = new byte[length];
        derInputStream.getBytes(this.encoding);
        check(this.encoding);
    }

    ObjectIdentifier(DerInputBuffer derInputBuffer) throws IOException {
        this.encoding = null;
        this.components = null;
        this.componentLen = -1;
        this.componentsCalculated = false;
        DerInputStream derInputStream = new DerInputStream(derInputBuffer);
        this.encoding = new byte[derInputStream.available()];
        derInputStream.getBytes(this.encoding);
        check(this.encoding);
    }

    private void init(int[] iArr, int i) {
        int iPack7Oid;
        byte[] bArr = new byte[(i * 5) + 1];
        if (iArr[1] < WinNT.MAXLONG - (iArr[0] * 40)) {
            iPack7Oid = 0 + pack7Oid((iArr[0] * 40) + iArr[1], bArr, 0);
        } else {
            iPack7Oid = 0 + pack7Oid(BigInteger.valueOf(iArr[1]).add(BigInteger.valueOf(iArr[0] * 40)), bArr, 0);
        }
        for (int i2 = 2; i2 < i; i2++) {
            iPack7Oid += pack7Oid(iArr[i2], bArr, iPack7Oid);
        }
        this.encoding = new byte[iPack7Oid];
        System.arraycopy(bArr, 0, this.encoding, 0, iPack7Oid);
    }

    public static ObjectIdentifier newInternal(int[] iArr) {
        try {
            return new ObjectIdentifier(iArr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void encode(DerOutputStream derOutputStream) throws IOException {
        derOutputStream.write((byte) 6, this.encoding);
    }

    @Deprecated
    public boolean equals(ObjectIdentifier objectIdentifier) {
        return equals((Object) objectIdentifier);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ObjectIdentifier)) {
            return false;
        }
        return Arrays.equals(this.encoding, ((ObjectIdentifier) obj).encoding);
    }

    public int hashCode() {
        return Arrays.hashCode(this.encoding);
    }

    private int[] toIntArray() {
        int length = this.encoding.length;
        int[] iArrCopyOf = new int[20];
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < length; i3++) {
            if ((this.encoding[i3] & 128) == 0) {
                if ((i3 - i2) + 1 > 4) {
                    BigInteger bigInteger = new BigInteger(pack(this.encoding, i2, (i3 - i2) + 1, 7, 8));
                    if (i2 == 0) {
                        int i4 = i;
                        int i5 = i + 1;
                        iArrCopyOf[i4] = 2;
                        BigInteger bigIntegerSubtract = bigInteger.subtract(BigInteger.valueOf(80L));
                        if (bigIntegerSubtract.compareTo(BigInteger.valueOf(2147483647L)) == 1) {
                            return null;
                        }
                        i = i5 + 1;
                        iArrCopyOf[i5] = bigIntegerSubtract.intValue();
                    } else {
                        if (bigInteger.compareTo(BigInteger.valueOf(2147483647L)) == 1) {
                            return null;
                        }
                        int i6 = i;
                        i++;
                        iArrCopyOf[i6] = bigInteger.intValue();
                    }
                } else {
                    int i7 = 0;
                    for (int i8 = i2; i8 <= i3; i8++) {
                        i7 = (i7 << 7) | (this.encoding[i8] & 127);
                    }
                    if (i2 == 0) {
                        if (i7 < 80) {
                            int i9 = i;
                            int i10 = i + 1;
                            iArrCopyOf[i9] = i7 / 40;
                            i = i10 + 1;
                            iArrCopyOf[i10] = i7 % 40;
                        } else {
                            int i11 = i;
                            int i12 = i + 1;
                            iArrCopyOf[i11] = 2;
                            i = i12 + 1;
                            iArrCopyOf[i12] = i7 - 80;
                        }
                    } else {
                        int i13 = i;
                        i++;
                        iArrCopyOf[i13] = i7;
                    }
                }
                i2 = i3 + 1;
            }
            if (i >= iArrCopyOf.length) {
                iArrCopyOf = Arrays.copyOf(iArrCopyOf, i + 10);
            }
        }
        return Arrays.copyOf(iArrCopyOf, i);
    }

    public String toString() {
        String string = this.stringForm;
        if (string == null) {
            int length = this.encoding.length;
            StringBuffer stringBuffer = new StringBuffer(length * 4);
            int i = 0;
            for (int i2 = 0; i2 < length; i2++) {
                if ((this.encoding[i2] & 128) == 0) {
                    if (i != 0) {
                        stringBuffer.append('.');
                    }
                    if ((i2 - i) + 1 > 4) {
                        BigInteger bigInteger = new BigInteger(pack(this.encoding, i, (i2 - i) + 1, 7, 8));
                        if (i == 0) {
                            stringBuffer.append("2.");
                            stringBuffer.append(bigInteger.subtract(BigInteger.valueOf(80L)));
                        } else {
                            stringBuffer.append(bigInteger);
                        }
                    } else {
                        int i3 = 0;
                        for (int i4 = i; i4 <= i2; i4++) {
                            i3 = (i3 << 7) | (this.encoding[i4] & 127);
                        }
                        if (i == 0) {
                            if (i3 < 80) {
                                stringBuffer.append(i3 / 40);
                                stringBuffer.append('.');
                                stringBuffer.append(i3 % 40);
                            } else {
                                stringBuffer.append("2.");
                                stringBuffer.append(i3 - 80);
                            }
                        } else {
                            stringBuffer.append(i3);
                        }
                    }
                    i = i2 + 1;
                }
            }
            string = stringBuffer.toString();
            this.stringForm = string;
        }
        return string;
    }

    private static byte[] pack(byte[] bArr, int i, int i2, int i3, int i4) {
        if (!$assertionsDisabled && (i3 <= 0 || i3 > 8)) {
            throw new AssertionError("input NUB must be between 1 and 8");
        }
        if (!$assertionsDisabled && (i4 <= 0 || i4 > 8)) {
            throw new AssertionError("output NUB must be between 1 and 8");
        }
        if (i3 == i4) {
            return (byte[]) bArr.clone();
        }
        int i5 = i2 * i3;
        byte[] bArr2 = new byte[((i5 + i4) - 1) / i4];
        int i6 = 0;
        int i7 = ((((i5 + i4) - 1) / i4) * i4) - i5;
        while (true) {
            int i8 = i7;
            if (i6 < i5) {
                int i9 = i3 - (i6 % i3);
                if (i9 > i4 - (i8 % i4)) {
                    i9 = i4 - (i8 % i4);
                }
                int i10 = i8 / i4;
                bArr2[i10] = (byte) (bArr2[i10] | ((((bArr[i + (i6 / i3)] + 256) >> ((i3 - (i6 % i3)) - i9)) & ((1 << i9) - 1)) << ((i4 - (i8 % i4)) - i9)));
                i6 += i9;
                i7 = i8 + i9;
            } else {
                return bArr2;
            }
        }
    }

    private static int pack7Oid(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        byte[] bArrPack = pack(bArr, i, i2, 8, 7);
        int length = bArrPack.length - 1;
        for (int length2 = bArrPack.length - 2; length2 >= 0; length2--) {
            if (bArrPack[length2] != 0) {
                length = length2;
            }
            int i4 = length2;
            bArrPack[i4] = (byte) (bArrPack[i4] | 128);
        }
        System.arraycopy(bArrPack, length, bArr2, i3, bArrPack.length - length);
        return bArrPack.length - length;
    }

    private static int pack8(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        byte[] bArrPack = pack(bArr, i, i2, 7, 8);
        int length = bArrPack.length - 1;
        for (int length2 = bArrPack.length - 2; length2 >= 0; length2--) {
            if (bArrPack[length2] != 0) {
                length = length2;
            }
        }
        System.arraycopy(bArrPack, length, bArr2, i3, bArrPack.length - length);
        return bArrPack.length - length;
    }

    private static int pack7Oid(int i, byte[] bArr, int i2) {
        return pack7Oid(new byte[]{(byte) (i >> 24), (byte) (i >> 16), (byte) (i >> 8), (byte) i}, 0, 4, bArr, i2);
    }

    private static int pack7Oid(BigInteger bigInteger, byte[] bArr, int i) {
        byte[] byteArray = bigInteger.toByteArray();
        return pack7Oid(byteArray, 0, byteArray.length, bArr, i);
    }

    private static void check(byte[] bArr) throws IOException {
        int length = bArr.length;
        if (length < 1 || (bArr[length - 1] & 128) != 0) {
            throw new IOException("ObjectIdentifier() -- Invalid DER encoding, not ended");
        }
        for (int i = 0; i < length; i++) {
            if (bArr[i] == -128 && (i == 0 || (bArr[i - 1] & 128) == 0)) {
                throw new IOException("ObjectIdentifier() -- Invalid DER encoding, useless extra octet detected");
            }
        }
    }

    private static void checkCount(int i) throws IOException {
        if (i < 2) {
            throw new IOException("ObjectIdentifier() -- Must be at least two oid components ");
        }
    }

    private static void checkFirstComponent(int i) throws IOException {
        if (i < 0 || i > 2) {
            throw new IOException("ObjectIdentifier() -- First oid component is invalid ");
        }
    }

    private static void checkFirstComponent(BigInteger bigInteger) throws IOException {
        if (bigInteger.signum() == -1 || bigInteger.compareTo(BigInteger.valueOf(2L)) == 1) {
            throw new IOException("ObjectIdentifier() -- First oid component is invalid ");
        }
    }

    private static void checkSecondComponent(int i, int i2) throws IOException {
        if (i2 < 0 || (i != 2 && i2 > 39)) {
            throw new IOException("ObjectIdentifier() -- Second oid component is invalid ");
        }
    }

    private static void checkSecondComponent(int i, BigInteger bigInteger) throws IOException {
        if (bigInteger.signum() == -1 || (i != 2 && bigInteger.compareTo(BigInteger.valueOf(39L)) == 1)) {
            throw new IOException("ObjectIdentifier() -- Second oid component is invalid ");
        }
    }

    private static void checkOtherComponent(int i, int i2) throws IOException {
        if (i2 < 0) {
            throw new IOException("ObjectIdentifier() -- oid component #" + (i + 1) + " must be non-negative ");
        }
    }

    private static void checkOtherComponent(int i, BigInteger bigInteger) throws IOException {
        if (bigInteger.signum() == -1) {
            throw new IOException("ObjectIdentifier() -- oid component #" + (i + 1) + " must be non-negative ");
        }
    }
}
