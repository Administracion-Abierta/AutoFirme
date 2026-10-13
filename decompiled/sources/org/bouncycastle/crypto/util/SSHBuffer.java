package org.bouncycastle.crypto.util;

import java.math.BigInteger;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/util/SSHBuffer.class */
class SSHBuffer {
    private final byte[] buffer;
    private int pos = 0;

    public SSHBuffer(byte[] bArr, byte[] bArr2) {
        this.buffer = bArr2;
        for (int i = 0; i != bArr.length; i++) {
            if (bArr[i] != bArr2[i]) {
                throw new IllegalArgumentException("magic-number incorrect");
            }
        }
        this.pos += bArr.length;
    }

    public SSHBuffer(byte[] bArr) {
        this.buffer = bArr;
    }

    public int readU32() {
        if (this.pos > this.buffer.length - 4) {
            throw new IllegalArgumentException("4 bytes for U32 exceeds buffer.");
        }
        byte[] bArr = this.buffer;
        int i = this.pos;
        this.pos = i + 1;
        int i2 = (bArr[i] & 255) << 24;
        byte[] bArr2 = this.buffer;
        int i3 = this.pos;
        this.pos = i3 + 1;
        int i4 = i2 | ((bArr2[i3] & 255) << 16);
        byte[] bArr3 = this.buffer;
        int i5 = this.pos;
        this.pos = i5 + 1;
        int i6 = i4 | ((bArr3[i5] & 255) << 8);
        byte[] bArr4 = this.buffer;
        int i7 = this.pos;
        this.pos = i7 + 1;
        return i6 | (bArr4[i7] & 255);
    }

    public String readString() {
        return Strings.fromByteArray(readBlock());
    }

    public byte[] readBlock() {
        int u32 = readU32();
        if (u32 == 0) {
            return new byte[0];
        }
        if (this.pos > this.buffer.length - u32) {
            throw new IllegalArgumentException("not enough data for block");
        }
        int i = this.pos;
        this.pos += u32;
        return Arrays.copyOfRange(this.buffer, i, this.pos);
    }

    public void skipBlock() {
        int u32 = readU32();
        if (this.pos > this.buffer.length - u32) {
            throw new IllegalArgumentException("not enough data for block");
        }
        this.pos += u32;
    }

    public byte[] readPaddedBlock() {
        return readPaddedBlock(8);
    }

    public byte[] readPaddedBlock(int i) {
        int i2;
        int u32 = readU32();
        if (u32 == 0) {
            return new byte[0];
        }
        if (this.pos > this.buffer.length - u32) {
            throw new IllegalArgumentException("not enough data for block");
        }
        if (0 != u32 % i) {
            throw new IllegalArgumentException("missing padding");
        }
        int i3 = this.pos;
        this.pos += u32;
        int i4 = this.pos;
        if (u32 > 0 && 0 < (i2 = this.buffer[this.pos - 1] & 255) && i2 < i) {
            i4 -= i2;
            int i5 = 1;
            int i6 = i4;
            while (i5 <= i2) {
                if (i5 != (this.buffer[i6] & 255)) {
                    throw new IllegalArgumentException("incorrect padding");
                }
                i5++;
                i6++;
            }
        }
        return Arrays.copyOfRange(this.buffer, i3, i4);
    }

    public BigInteger readBigNumPositive() {
        int u32 = readU32();
        if (this.pos + u32 > this.buffer.length) {
            throw new IllegalArgumentException("not enough data for big num");
        }
        int i = this.pos;
        this.pos += u32;
        return new BigInteger(1, Arrays.copyOfRange(this.buffer, i, this.pos));
    }

    public byte[] getBuffer() {
        return Arrays.clone(this.buffer);
    }

    public boolean hasRemaining() {
        return this.pos < this.buffer.length;
    }
}
