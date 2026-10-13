package org.bouncycastle.oer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/BitBuilder.class */
public class BitBuilder {
    private static final byte[] bits = {-128, 64, 32, 16, 8, 4, 2, 1};
    byte[] buf = new byte[1];
    int pos = 0;

    public BitBuilder writeBit(int i) {
        if (this.pos / 8 >= this.buf.length) {
            byte[] bArr = new byte[this.buf.length + 4];
            System.arraycopy(this.buf, 0, bArr, 0, this.pos / 8);
            Arrays.clear(this.buf);
            this.buf = bArr;
        }
        if (i == 0) {
            byte[] bArr2 = this.buf;
            int i2 = this.pos / 8;
            bArr2[i2] = (byte) (bArr2[i2] & (bits[this.pos % 8] ^ (-1)));
        } else {
            byte[] bArr3 = this.buf;
            int i3 = this.pos / 8;
            bArr3[i3] = (byte) (bArr3[i3] | bits[this.pos % 8]);
        }
        this.pos++;
        return this;
    }

    public BitBuilder writeBits(long j, int i) {
        for (int i2 = i - 1; i2 >= 0; i2--) {
            writeBit((j & (1 << i2)) > 0 ? 1 : 0);
        }
        return this;
    }

    public BitBuilder writeBits(long j, int i, int i2) {
        for (int i3 = i - 1; i3 >= i - i2; i3--) {
            writeBit((j & (1 << i3)) != 0 ? 1 : 0);
        }
        return this;
    }

    public int write(OutputStream outputStream) throws IOException {
        int i = (this.pos + (this.pos % 8)) / 8;
        outputStream.write(this.buf, 0, i);
        outputStream.flush();
        return i;
    }

    public int writeAndClear(OutputStream outputStream) throws IOException {
        int i = (this.pos + (this.pos % 8)) / 8;
        outputStream.write(this.buf, 0, i);
        outputStream.flush();
        zero();
        return i;
    }

    public void pad() {
        this.pos += this.pos % 8;
    }

    public void write7BitBytes(int i) {
        boolean z = false;
        for (int i2 = 4; i2 >= 0; i2--) {
            if (!z && (i & (-33554432)) != 0) {
                z = true;
            }
            if (z) {
                writeBit(i2).writeBits(i, 32, 7);
            }
            i <<= 7;
        }
    }

    public void write7BitBytes(BigInteger bigInteger) {
        int iBitLength = (bigInteger.bitLength() + (bigInteger.bitLength() % 8)) / 8;
        BigInteger bigIntegerShiftLeft = BigInteger.valueOf(254L).shiftLeft(iBitLength * 8);
        boolean z = false;
        for (int i = iBitLength; i >= 0; i--) {
            if (!z && bigInteger.and(bigIntegerShiftLeft).compareTo(BigInteger.ZERO) != 0) {
                z = true;
            }
            if (z) {
                writeBit(i).writeBits(bigInteger.and(bigIntegerShiftLeft).shiftRight((8 * iBitLength) - 8).intValue(), 8, 7);
            }
            bigInteger = bigInteger.shiftLeft(7);
        }
    }

    public void zero() {
        Arrays.clear(this.buf);
        this.pos = 0;
    }
}
