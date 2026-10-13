package es.gob.afirma.core.signers.der;

import com.sun.jna.platform.win32.WinNT;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/signers/der/DerInputBuffer.class */
class DerInputBuffer extends ByteArrayInputStream implements Cloneable {
    boolean allowBER;

    DerInputBuffer(byte[] bArr) {
        this(bArr, true);
    }

    DerInputBuffer(byte[] bArr, boolean z) {
        super(bArr);
        this.allowBER = true;
        this.allowBER = z;
    }

    DerInputBuffer(byte[] bArr, int i, int i2, boolean z) {
        super(bArr, i, i2);
        this.allowBER = true;
        this.allowBER = z;
    }

    DerInputBuffer dup() {
        try {
            DerInputBuffer derInputBuffer = (DerInputBuffer) clone();
            derInputBuffer.mark(WinNT.MAXLONG);
            return derInputBuffer;
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

    byte[] toByteArray() {
        int iAvailable = available();
        if (iAvailable <= 0) {
            return null;
        }
        byte[] bArr = new byte[iAvailable];
        System.arraycopy(this.buf, this.pos, bArr, 0, iAvailable);
        return bArr;
    }

    int peek() throws IOException {
        if (this.pos >= this.count) {
            throw new IOException("out of data");
        }
        return this.buf[this.pos];
    }

    public boolean equals(Object obj) {
        if (obj instanceof DerInputBuffer) {
            return equals((DerInputBuffer) obj);
        }
        return false;
    }

    boolean equals(DerInputBuffer derInputBuffer) {
        if (this == derInputBuffer) {
            return true;
        }
        int iAvailable = available();
        if (derInputBuffer.available() != iAvailable) {
            return false;
        }
        for (int i = 0; i < iAvailable; i++) {
            if (this.buf[this.pos + i] != derInputBuffer.buf[derInputBuffer.pos + i]) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int i = 0;
        int iAvailable = available();
        int i2 = this.pos;
        for (int i3 = 0; i3 < iAvailable; i3++) {
            i += this.buf[i2 + i3] * i3;
        }
        return i;
    }

    void truncate(int i) throws IOException {
        if (i > available()) {
            throw new IOException("insufficient data");
        }
        this.count = this.pos + i;
    }

    BigInteger getBigInteger(int i, boolean z) throws IOException {
        if (i > available()) {
            throw new IOException("short read of integer");
        }
        if (i == 0) {
            throw new IOException("Invalid encoding: zero length Int value");
        }
        byte[] bArr = new byte[i];
        System.arraycopy(this.buf, this.pos, bArr, 0, i);
        skip(i);
        if (!this.allowBER && i >= 2 && bArr[0] == 0 && bArr[1] >= 0) {
            throw new IOException("Invalid encoding: redundant leading 0s");
        }
        if (z) {
            return new BigInteger(1, bArr);
        }
        return new BigInteger(bArr);
    }

    public int getInteger(int i) throws IOException {
        BigInteger bigInteger = getBigInteger(i, false);
        if (bigInteger.compareTo(BigInteger.valueOf(-2147483648L)) < 0) {
            throw new IOException("Integer below minimum valid value");
        }
        if (bigInteger.compareTo(BigInteger.valueOf(2147483647L)) > 0) {
            throw new IOException("Integer exceeds maximum valid value");
        }
        return bigInteger.intValue();
    }

    public byte[] getBitString(int i) throws IOException {
        if (i > available()) {
            throw new IOException("short read of bit string");
        }
        if (i == 0) {
            throw new IOException("Invalid encoding: zero length bit string");
        }
        byte b = this.buf[this.pos];
        if (b < 0 || b > 7) {
            throw new IOException("Invalid number of padding bits");
        }
        byte[] bArr = new byte[i - 1];
        System.arraycopy(this.buf, this.pos + 1, bArr, 0, i - 1);
        if (b != 0) {
            int i2 = i - 2;
            bArr[i2] = (byte) (bArr[i2] & (255 << b));
        }
        skip(i);
        return bArr;
    }

    byte[] getBitString() throws IOException {
        return getBitString(available());
    }

    BitArray getUnalignedBitString() throws IOException {
        if (this.pos >= this.count) {
            return null;
        }
        int iAvailable = available();
        int i = this.buf[this.pos] & 255;
        if (i > 7) {
            throw new IOException("Invalid value for unused bits: " + i);
        }
        byte[] bArr = new byte[iAvailable - 1];
        int length = bArr.length == 0 ? 0 : (bArr.length * 8) - i;
        System.arraycopy(this.buf, this.pos + 1, bArr, 0, iAvailable - 1);
        BitArray bitArray = new BitArray(length, bArr);
        this.pos = this.count;
        return bitArray;
    }
}
