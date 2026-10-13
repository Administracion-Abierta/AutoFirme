package org.bouncycastle.pqc.legacy.crypto.ntru;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/ntru/IndexGenerator.class */
public class IndexGenerator {
    private byte[] seed;
    private int N;
    private int c;
    private int minCallsR;
    private BitString buf;
    private Digest hashAlg;
    private int hLen;
    private int totLen = 0;
    private int remLen = 0;
    private int counter = 0;
    private boolean initialized = false;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/ntru/IndexGenerator$BitString.class */
    public static class BitString {
        byte[] bytes = new byte[4];
        int numBytes;
        int lastByteBits;

        void appendBits(byte[] bArr) {
            for (int i = 0; i != bArr.length; i++) {
                appendBits(bArr[i]);
            }
        }

        public void appendBits(byte b) {
            if (this.numBytes == this.bytes.length) {
                this.bytes = IndexGenerator.copyOf(this.bytes, 2 * this.bytes.length);
            }
            if (this.numBytes == 0) {
                this.numBytes = 1;
                this.bytes[0] = b;
                this.lastByteBits = 8;
            } else {
                if (this.lastByteBits == 8) {
                    byte[] bArr = this.bytes;
                    int i = this.numBytes;
                    this.numBytes = i + 1;
                    bArr[i] = b;
                    return;
                }
                int i2 = 8 - this.lastByteBits;
                byte[] bArr2 = this.bytes;
                int i3 = this.numBytes - 1;
                bArr2[i3] = (byte) (bArr2[i3] | ((b & 255) << this.lastByteBits));
                byte[] bArr3 = this.bytes;
                int i4 = this.numBytes;
                this.numBytes = i4 + 1;
                bArr3[i4] = (byte) ((b & 255) >> i2);
            }
        }

        public BitString getTrailing(int i) {
            BitString bitString = new BitString();
            bitString.numBytes = (i + 7) / 8;
            bitString.bytes = new byte[bitString.numBytes];
            for (int i2 = 0; i2 < bitString.numBytes; i2++) {
                bitString.bytes[i2] = this.bytes[i2];
            }
            bitString.lastByteBits = i % 8;
            if (bitString.lastByteBits == 0) {
                bitString.lastByteBits = 8;
            } else {
                int i3 = 32 - bitString.lastByteBits;
                bitString.bytes[bitString.numBytes - 1] = (byte) ((bitString.bytes[bitString.numBytes - 1] << i3) >>> i3);
            }
            return bitString;
        }

        public int getLeadingAsInt(int i) {
            int i2 = (((this.numBytes - 1) * 8) + this.lastByteBits) - i;
            int i3 = i2 / 8;
            int i4 = i2 % 8;
            int i5 = (this.bytes[i3] & 255) >>> i4;
            int i6 = 8 - i4;
            for (int i7 = i3 + 1; i7 < this.numBytes; i7++) {
                i5 |= (this.bytes[i7] & 255) << i6;
                i6 += 8;
            }
            return i5;
        }

        public byte[] getBytes() {
            return Arrays.clone(this.bytes);
        }
    }

    IndexGenerator(byte[] bArr, NTRUEncryptionParameters nTRUEncryptionParameters) {
        this.seed = bArr;
        this.N = nTRUEncryptionParameters.N;
        this.c = nTRUEncryptionParameters.c;
        this.minCallsR = nTRUEncryptionParameters.minCallsR;
        this.hashAlg = nTRUEncryptionParameters.hashAlg;
        this.hLen = this.hashAlg.getDigestSize();
    }

    int nextIndex() {
        int leadingAsInt;
        if (!this.initialized) {
            this.buf = new BitString();
            byte[] bArr = new byte[this.hashAlg.getDigestSize()];
            while (this.counter < this.minCallsR) {
                appendHash(this.buf, bArr);
                this.counter++;
            }
            this.totLen = this.minCallsR * 8 * this.hLen;
            this.remLen = this.totLen;
            this.initialized = true;
        }
        do {
            this.totLen += this.c;
            BitString trailing = this.buf.getTrailing(this.remLen);
            if (this.remLen < this.c) {
                int i = this.c - this.remLen;
                int i2 = this.counter + (((i + this.hLen) - 1) / this.hLen);
                byte[] bArr2 = new byte[this.hashAlg.getDigestSize()];
                while (this.counter < i2) {
                    appendHash(trailing, bArr2);
                    this.counter++;
                    if (i > 8 * this.hLen) {
                        i -= 8 * this.hLen;
                    }
                }
                this.remLen = (8 * this.hLen) - i;
                this.buf = new BitString();
                this.buf.appendBits(bArr2);
            } else {
                this.remLen -= this.c;
            }
            leadingAsInt = trailing.getLeadingAsInt(this.c);
        } while (leadingAsInt >= (1 << this.c) - ((1 << this.c) % this.N));
        return leadingAsInt % this.N;
    }

    private void appendHash(BitString bitString, byte[] bArr) {
        this.hashAlg.update(this.seed, 0, this.seed.length);
        putInt(this.hashAlg, this.counter);
        this.hashAlg.doFinal(bArr, 0);
        bitString.appendBits(bArr);
    }

    private void putInt(Digest digest, int i) {
        digest.update((byte) (i >> 24));
        digest.update((byte) (i >> 16));
        digest.update((byte) (i >> 8));
        digest.update((byte) i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] copyOf(byte[] bArr, int i) {
        byte[] bArr2 = new byte[i];
        System.arraycopy(bArr, 0, bArr2, 0, i < bArr.length ? i : bArr.length);
        return bArr2;
    }
}
