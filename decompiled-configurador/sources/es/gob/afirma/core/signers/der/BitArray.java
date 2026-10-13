package es.gob.afirma.core.signers.der;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/signers/der/BitArray.class */
public class BitArray {
    private final byte[] repn;
    private final int length;
    private static final int BITS_PER_UNIT = 8;
    private static final byte[][] NYBBLE = {new byte[]{48, 48, 48, 48}, new byte[]{48, 48, 48, 49}, new byte[]{48, 48, 49, 48}, new byte[]{48, 48, 49, 49}, new byte[]{48, 49, 48, 48}, new byte[]{48, 49, 48, 49}, new byte[]{48, 49, 49, 48}, new byte[]{48, 49, 49, 49}, new byte[]{49, 48, 48, 48}, new byte[]{49, 48, 48, 49}, new byte[]{49, 48, 49, 48}, new byte[]{49, 48, 49, 49}, new byte[]{49, 49, 48, 48}, new byte[]{49, 49, 48, 49}, new byte[]{49, 49, 49, 48}, new byte[]{49, 49, 49, 49}};
    private static final int BYTES_PER_LINE = 8;

    private static int subscript(int i) {
        return i / 8;
    }

    private static int position(int i) {
        return 1 << (7 - (i % 8));
    }

    public BitArray(int i) throws IllegalArgumentException {
        if (i < 0) {
            throw new IllegalArgumentException("Negative length for BitArray");
        }
        this.length = i;
        this.repn = new byte[((i + 8) - 1) / 8];
    }

    public BitArray(int i, byte[] bArr) throws IllegalArgumentException {
        if (i < 0) {
            throw new IllegalArgumentException("Negative length for BitArray");
        }
        if (bArr.length * 8 < i) {
            throw new IllegalArgumentException("Byte array too short to represent bit array of given length");
        }
        this.length = i;
        int i2 = ((i + 8) - 1) / 8;
        byte b = (byte) (255 << ((i2 * 8) - i));
        this.repn = new byte[i2];
        System.arraycopy(bArr, 0, this.repn, 0, i2);
        if (i2 > 0) {
            byte[] bArr2 = this.repn;
            int i3 = i2 - 1;
            bArr2[i3] = (byte) (bArr2[i3] & b);
        }
    }

    public BitArray(boolean[] zArr) {
        this.length = zArr.length;
        this.repn = new byte[(this.length + 7) / 8];
        for (int i = 0; i < this.length; i++) {
            set(i, zArr[i]);
        }
    }

    private BitArray(BitArray bitArray) {
        this.length = bitArray.length;
        this.repn = (byte[]) bitArray.repn.clone();
    }

    public boolean get(int i) throws ArrayIndexOutOfBoundsException {
        if (i < 0 || i >= this.length) {
            throw new ArrayIndexOutOfBoundsException(Integer.toString(i));
        }
        return (this.repn[subscript(i)] & position(i)) != 0;
    }

    public void set(int i, boolean z) throws ArrayIndexOutOfBoundsException {
        if (i < 0 || i >= this.length) {
            throw new ArrayIndexOutOfBoundsException(Integer.toString(i));
        }
        int iSubscript = subscript(i);
        int iPosition = position(i);
        if (z) {
            byte[] bArr = this.repn;
            bArr[iSubscript] = (byte) (bArr[iSubscript] | iPosition);
        } else {
            byte[] bArr2 = this.repn;
            bArr2[iSubscript] = (byte) (bArr2[iSubscript] & (iPosition ^ (-1)));
        }
    }

    public int length() {
        return this.length;
    }

    public byte[] toByteArray() {
        return (byte[]) this.repn.clone();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !(obj instanceof BitArray)) {
            return false;
        }
        BitArray bitArray = (BitArray) obj;
        if (bitArray.length != this.length) {
            return false;
        }
        for (int i = 0; i < this.repn.length; i++) {
            if (this.repn[i] != bitArray.repn[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean[] toBooleanArray() {
        boolean[] zArr = new boolean[this.length];
        for (int i = 0; i < this.length; i++) {
            zArr[i] = get(i);
        }
        return zArr;
    }

    public int hashCode() {
        int i = 0;
        for (int i2 = 0; i2 < this.repn.length; i2++) {
            i = (31 * i) + this.repn[i2];
        }
        return i ^ this.length;
    }

    public Object clone() {
        return new BitArray(this);
    }

    public String toString() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i < this.repn.length - 1; i++) {
            byteArrayOutputStream.write(NYBBLE[(this.repn[i] >> 4) & 15], 0, 4);
            byteArrayOutputStream.write(NYBBLE[this.repn[i] & 15], 0, 4);
            if (i % 8 == 7) {
                byteArrayOutputStream.write(10);
            } else {
                byteArrayOutputStream.write(32);
            }
        }
        for (int length = 8 * (this.repn.length - 1); length < this.length; length++) {
            byteArrayOutputStream.write(get(length) ? 49 : 48);
        }
        return new String(byteArrayOutputStream.toByteArray());
    }

    public BitArray truncate() {
        for (int i = this.length - 1; i >= 0; i--) {
            if (get(i)) {
                return new BitArray(i + 1, Arrays.copyOf(this.repn, (i + 8) / 8));
            }
        }
        return new BitArray(1);
    }
}
