package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/macs/SipHash.class */
public class SipHash implements Mac {
    protected final int c;
    protected final int d;
    protected long k0;
    protected long k1;
    protected long v0;
    protected long v1;
    protected long v2;
    protected long v3;
    protected long m;
    protected int wordPos;
    protected int wordCount;

    public SipHash() {
        this.m = 0L;
        this.wordPos = 0;
        this.wordCount = 0;
        this.c = 2;
        this.d = 4;
    }

    public SipHash(int i, int i2) {
        this.m = 0L;
        this.wordPos = 0;
        this.wordCount = 0;
        this.c = i;
        this.d = i2;
    }

    @Override // org.bouncycastle.crypto.Mac
    public String getAlgorithmName() {
        return "SipHash-" + this.c + "-" + this.d;
    }

    @Override // org.bouncycastle.crypto.Mac
    public int getMacSize() {
        return 8;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void init(CipherParameters cipherParameters) throws IllegalArgumentException {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("'params' must be an instance of KeyParameter");
        }
        byte[] key = ((KeyParameter) cipherParameters).getKey();
        if (key.length != 16) {
            throw new IllegalArgumentException("'params' must be a 128-bit key");
        }
        this.k0 = Pack.littleEndianToLong(key, 0);
        this.k1 = Pack.littleEndianToLong(key, 8);
        reset();
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte b) throws IllegalStateException {
        this.m >>>= 8;
        this.m |= (((long) b) & 255) << 56;
        int i = this.wordPos + 1;
        this.wordPos = i;
        if (i == 8) {
            processMessageWord();
            this.wordPos = 0;
        }
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte[] bArr, int i, int i2) throws IllegalStateException, DataLengthException {
        int i3 = 0;
        int i4 = i2 & (-8);
        if (this.wordPos == 0) {
            while (i3 < i4) {
                this.m = Pack.littleEndianToLong(bArr, i + i3);
                processMessageWord();
                i3 += 8;
            }
            while (i3 < i2) {
                this.m >>>= 8;
                this.m |= (((long) bArr[i + i3]) & 255) << 56;
                i3++;
            }
            this.wordPos = i2 - i4;
            return;
        }
        int i5 = this.wordPos << 3;
        while (i3 < i4) {
            long jLittleEndianToLong = Pack.littleEndianToLong(bArr, i + i3);
            this.m = (jLittleEndianToLong << i5) | (this.m >>> (-i5));
            processMessageWord();
            this.m = jLittleEndianToLong;
            i3 += 8;
        }
        while (i3 < i2) {
            this.m >>>= 8;
            this.m |= (((long) bArr[i + i3]) & 255) << 56;
            int i6 = this.wordPos + 1;
            this.wordPos = i6;
            if (i6 == 8) {
                processMessageWord();
                this.wordPos = 0;
            }
            i3++;
        }
    }

    public long doFinal() throws IllegalStateException, DataLengthException {
        this.m >>>= (7 - this.wordPos) << 3;
        this.m >>>= 8;
        this.m |= (((long) ((this.wordCount << 3) + this.wordPos)) & 255) << 56;
        processMessageWord();
        this.v2 ^= 255;
        applySipRounds(this.d);
        long j = ((this.v0 ^ this.v1) ^ this.v2) ^ this.v3;
        reset();
        return j;
    }

    @Override // org.bouncycastle.crypto.Mac
    public int doFinal(byte[] bArr, int i) throws IllegalStateException, DataLengthException {
        Pack.longToLittleEndian(doFinal(), bArr, i);
        return 8;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void reset() {
        this.v0 = this.k0 ^ 8317987319222330741L;
        this.v1 = this.k1 ^ 7237128888997146477L;
        this.v2 = this.k0 ^ 7816392313619706465L;
        this.v3 = this.k1 ^ 8387220255154660723L;
        this.m = 0L;
        this.wordPos = 0;
        this.wordCount = 0;
    }

    protected void processMessageWord() {
        this.wordCount++;
        this.v3 ^= this.m;
        applySipRounds(this.c);
        this.v0 ^= this.m;
    }

    protected void applySipRounds(int i) {
        long j = this.v0;
        long jRotateLeft = this.v1;
        long jRotateLeft2 = this.v2;
        long jRotateLeft3 = this.v3;
        for (int i2 = 0; i2 < i; i2++) {
            long j2 = j + jRotateLeft;
            long j3 = jRotateLeft2 + jRotateLeft3;
            long jRotateLeft4 = rotateLeft(jRotateLeft, 13) ^ j2;
            long jRotateLeft5 = rotateLeft(jRotateLeft3, 16) ^ j3;
            long jRotateLeft6 = rotateLeft(j2, 32);
            long j4 = j3 + jRotateLeft4;
            j = jRotateLeft6 + jRotateLeft5;
            jRotateLeft = rotateLeft(jRotateLeft4, 17) ^ j4;
            jRotateLeft3 = rotateLeft(jRotateLeft5, 21) ^ j;
            jRotateLeft2 = rotateLeft(j4, 32);
        }
        this.v0 = j;
        this.v1 = jRotateLeft;
        this.v2 = jRotateLeft2;
        this.v3 = jRotateLeft3;
    }

    protected static long rotateLeft(long j, int i) {
        return (j << i) | (j >>> (-i));
    }
}
