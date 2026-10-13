package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.SparkleEngine;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/SparkleDigest.class */
public class SparkleDigest implements ExtendedDigest {
    private static final int RATE_BYTES = 16;
    private static final int RATE_WORDS = 4;
    private String algorithmName;
    private final int[] state;
    private final int DIGEST_BYTES;
    private final int SPARKLE_STEPS_SLIM;
    private final int SPARKLE_STEPS_BIG;
    private final int STATE_WORDS;
    private final byte[] m_buf = new byte[16];
    private int m_bufPos = 0;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/SparkleDigest$Friend.class */
    public static class Friend {
        private static final Friend INSTANCE = new Friend();

        private Friend() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/SparkleDigest$SparkleParameters.class */
    public enum SparkleParameters {
        ESCH256,
        ESCH384
    }

    public SparkleDigest(SparkleParameters sparkleParameters) {
        switch (sparkleParameters) {
            case ESCH256:
                this.algorithmName = "ESCH-256";
                this.DIGEST_BYTES = 32;
                this.SPARKLE_STEPS_SLIM = 7;
                this.SPARKLE_STEPS_BIG = 11;
                this.STATE_WORDS = 12;
                break;
            case ESCH384:
                this.algorithmName = "ESCH-384";
                this.DIGEST_BYTES = 48;
                this.SPARKLE_STEPS_SLIM = 8;
                this.SPARKLE_STEPS_BIG = 12;
                this.STATE_WORDS = 16;
                break;
            default:
                throw new IllegalArgumentException("Invalid definition of SCHWAEMM instance");
        }
        this.state = new int[this.STATE_WORDS];
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return this.algorithmName;
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return this.DIGEST_BYTES;
    }

    @Override // org.bouncycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return 16;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) {
        if (this.m_bufPos == 16) {
            processBlock(this.m_buf, 0, this.SPARKLE_STEPS_SLIM);
            this.m_bufPos = 0;
        }
        byte[] bArr = this.m_buf;
        int i = this.m_bufPos;
        this.m_bufPos = i + 1;
        bArr[i] = b;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) {
        if (i > bArr.length - i2) {
            throw new DataLengthException(this.algorithmName + " input buffer too short");
        }
        if (i2 < 1) {
            return;
        }
        int i3 = 16 - this.m_bufPos;
        if (i2 <= i3) {
            System.arraycopy(bArr, i, this.m_buf, this.m_bufPos, i2);
            this.m_bufPos += i2;
            return;
        }
        int i4 = 0;
        if (this.m_bufPos > 0) {
            System.arraycopy(bArr, i, this.m_buf, this.m_bufPos, i3);
            processBlock(this.m_buf, 0, this.SPARKLE_STEPS_SLIM);
            i4 = 0 + i3;
        }
        while (true) {
            int i5 = i2 - i4;
            if (i5 <= 16) {
                System.arraycopy(bArr, i + i4, this.m_buf, 0, i5);
                this.m_bufPos = i5;
                return;
            } else {
                processBlock(bArr, i + i4, this.SPARKLE_STEPS_SLIM);
                i4 += 16;
            }
        }
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        if (i > bArr.length - this.DIGEST_BYTES) {
            throw new OutputLengthException(this.algorithmName + " input buffer too short");
        }
        if (this.m_bufPos < 16) {
            int[] iArr = this.state;
            int i2 = (this.STATE_WORDS >> 1) - 1;
            iArr[i2] = iArr[i2] ^ 16777216;
            this.m_buf[this.m_bufPos] = -128;
            while (true) {
                int i3 = this.m_bufPos + 1;
                this.m_bufPos = i3;
                if (i3 >= 16) {
                    break;
                }
                this.m_buf[this.m_bufPos] = 0;
            }
        } else {
            int[] iArr2 = this.state;
            int i4 = (this.STATE_WORDS >> 1) - 1;
            iArr2[i4] = iArr2[i4] ^ 33554432;
        }
        processBlock(this.m_buf, 0, this.SPARKLE_STEPS_BIG);
        Pack.intToLittleEndian(this.state, 0, 4, bArr, i);
        if (this.STATE_WORDS == 16) {
            SparkleEngine.sparkle_opt16(Friend.INSTANCE, this.state, this.SPARKLE_STEPS_SLIM);
            Pack.intToLittleEndian(this.state, 0, 4, bArr, i + 16);
            SparkleEngine.sparkle_opt16(Friend.INSTANCE, this.state, this.SPARKLE_STEPS_SLIM);
            Pack.intToLittleEndian(this.state, 0, 4, bArr, i + 32);
        } else {
            SparkleEngine.sparkle_opt12(Friend.INSTANCE, this.state, this.SPARKLE_STEPS_SLIM);
            Pack.intToLittleEndian(this.state, 0, 4, bArr, i + 16);
        }
        reset();
        return this.DIGEST_BYTES;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void reset() {
        Arrays.fill(this.state, 0);
        Arrays.fill(this.m_buf, (byte) 0);
        this.m_bufPos = 0;
    }

    private void processBlock(byte[] bArr, int i, int i2) {
        int iLittleEndianToInt = Pack.littleEndianToInt(bArr, i);
        int iLittleEndianToInt2 = Pack.littleEndianToInt(bArr, i + 4);
        int iLittleEndianToInt3 = Pack.littleEndianToInt(bArr, i + 8);
        int iLittleEndianToInt4 = Pack.littleEndianToInt(bArr, i + 12);
        int iELL = ELL(iLittleEndianToInt ^ iLittleEndianToInt3);
        int iELL2 = ELL(iLittleEndianToInt2 ^ iLittleEndianToInt4);
        int[] iArr = this.state;
        iArr[0] = iArr[0] ^ (iLittleEndianToInt ^ iELL2);
        int[] iArr2 = this.state;
        iArr2[1] = iArr2[1] ^ (iLittleEndianToInt2 ^ iELL);
        int[] iArr3 = this.state;
        iArr3[2] = iArr3[2] ^ (iLittleEndianToInt3 ^ iELL2);
        int[] iArr4 = this.state;
        iArr4[3] = iArr4[3] ^ (iLittleEndianToInt4 ^ iELL);
        int[] iArr5 = this.state;
        iArr5[4] = iArr5[4] ^ iELL2;
        int[] iArr6 = this.state;
        iArr6[5] = iArr6[5] ^ iELL;
        if (this.STATE_WORDS != 16) {
            SparkleEngine.sparkle_opt12(Friend.INSTANCE, this.state, i2);
            return;
        }
        int[] iArr7 = this.state;
        iArr7[6] = iArr7[6] ^ iELL2;
        int[] iArr8 = this.state;
        iArr8[7] = iArr8[7] ^ iELL;
        SparkleEngine.sparkle_opt16(Friend.INSTANCE, this.state, i2);
    }

    private static int ELL(int i) {
        return Integers.rotateRight(i, 16) ^ (i & 65535);
    }
}
