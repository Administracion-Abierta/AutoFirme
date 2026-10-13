package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SparkleDigest;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/SparkleEngine.class */
public class SparkleEngine extends AEADBufferBaseEngine {
    private static final int[] RCON = {-1209970334, -1083090816, 951376470, 844003128, -1156479509, 1333558103, -809524792, -1028445891};
    private final int[] state;
    private final int[] k;
    private final int[] npub;
    private boolean encrypted;
    private final int m_bufferSizeDecrypt;
    private final int SPARKLE_STEPS_SLIM;
    private final int SPARKLE_STEPS_BIG;
    private final int KEY_WORDS;
    private final int TAG_WORDS;
    private final int STATE_WORDS;
    private final int RATE_WORDS;
    private final int CAP_MASK;
    private final int _A0;
    private final int _A1;
    private final int _M2;
    private final int _M3;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/SparkleEngine$SparkleParameters.class */
    public enum SparkleParameters {
        SCHWAEMM128_128,
        SCHWAEMM256_128,
        SCHWAEMM192_192,
        SCHWAEMM256_256
    }

    public SparkleEngine(SparkleParameters sparkleParameters) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        switch (sparkleParameters) {
            case SCHWAEMM128_128:
                i = 128;
                i2 = 128;
                i3 = 128;
                i4 = 256;
                i5 = 128;
                this.SPARKLE_STEPS_SLIM = 7;
                this.SPARKLE_STEPS_BIG = 10;
                this.algorithmName = "SCHWAEMM128-128";
                break;
            case SCHWAEMM256_128:
                i = 128;
                i2 = 256;
                i3 = 128;
                i4 = 384;
                i5 = 128;
                this.SPARKLE_STEPS_SLIM = 7;
                this.SPARKLE_STEPS_BIG = 11;
                this.algorithmName = "SCHWAEMM256-128";
                break;
            case SCHWAEMM192_192:
                i = 192;
                i2 = 192;
                i3 = 192;
                i4 = 384;
                i5 = 192;
                this.SPARKLE_STEPS_SLIM = 7;
                this.SPARKLE_STEPS_BIG = 11;
                this.algorithmName = "SCHWAEMM192-192";
                break;
            case SCHWAEMM256_256:
                i = 256;
                i2 = 256;
                i3 = 256;
                i4 = 512;
                i5 = 256;
                this.SPARKLE_STEPS_SLIM = 8;
                this.SPARKLE_STEPS_BIG = 12;
                this.algorithmName = "SCHWAEMM256-256";
                break;
            default:
                throw new IllegalArgumentException("Invalid definition of SCHWAEMM instance");
        }
        this.KEY_WORDS = i >>> 5;
        this.KEY_SIZE = i >>> 3;
        this.TAG_WORDS = i3 >>> 5;
        this.MAC_SIZE = i3 >>> 3;
        this.STATE_WORDS = i4 >>> 5;
        this.RATE_WORDS = i2 >>> 5;
        this.IV_SIZE = i2 >>> 3;
        int i6 = i5 >>> 6;
        int i7 = i5 >>> 5;
        this.CAP_MASK = this.RATE_WORDS > i7 ? i7 - 1 : -1;
        this._A0 = (1 << i6) << 24;
        this._A1 = (1 ^ (1 << i6)) << 24;
        this._M2 = (2 ^ (1 << i6)) << 24;
        this._M3 = (3 ^ (1 << i6)) << 24;
        this.state = new int[this.STATE_WORDS];
        this.k = new int[this.KEY_WORDS];
        this.npub = new int[this.RATE_WORDS];
        int i8 = this.IV_SIZE;
        this.BlockSize = i8;
        this.AADBufferSize = i8;
        this.m_bufferSizeDecrypt = this.IV_SIZE + this.MAC_SIZE;
        this.m_buf = new byte[this.m_bufferSizeDecrypt];
        this.m_aad = new byte[this.BlockSize];
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    protected void init(byte[] bArr, byte[] bArr2) throws IllegalArgumentException {
        Pack.littleEndianToInt(bArr, 0, this.k);
        Pack.littleEndianToInt(bArr2, 0, this.npub);
        this.initialised = true;
        this.m_state = this.forEncryption ? AEADBufferBaseEngine.State.EncInit : AEADBufferBaseEngine.State.DecInit;
        reset();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    protected void processFinalBlock(byte[] bArr, int i) {
        if (this.encrypted || this.m_bufPos > 0) {
            int[] iArr = this.state;
            int i2 = this.STATE_WORDS - 1;
            iArr[i2] = iArr[i2] ^ (this.m_bufPos < this.IV_SIZE ? this._M2 : this._M3);
            int[] iArr2 = new int[this.RATE_WORDS];
            for (int i3 = 0; i3 < this.m_bufPos; i3++) {
                int i4 = i3 >>> 2;
                iArr2[i4] = iArr2[i4] | ((this.m_buf[i3] & 255) << ((i3 & 3) << 3));
            }
            if (this.m_bufPos < this.IV_SIZE) {
                if (!this.forEncryption) {
                    int i5 = (this.m_bufPos & 3) << 3;
                    int i6 = this.m_bufPos >>> 2;
                    iArr2[i6] = iArr2[i6] | ((this.state[this.m_bufPos >>> 2] >>> i5) << i5);
                    int i7 = (this.m_bufPos >>> 2) + 1;
                    System.arraycopy(this.state, i7, iArr2, i7, this.RATE_WORDS - i7);
                }
                int i8 = this.m_bufPos >>> 2;
                iArr2[i8] = iArr2[i8] ^ (128 << ((this.m_bufPos & 3) << 3));
            }
            for (int i9 = 0; i9 < this.RATE_WORDS / 2; i9++) {
                int i10 = i9 + (this.RATE_WORDS / 2);
                int i11 = this.state[i9];
                int i12 = this.state[i10];
                if (this.forEncryption) {
                    this.state[i9] = (i12 ^ iArr2[i9]) ^ this.state[this.RATE_WORDS + i9];
                    this.state[i10] = ((i11 ^ i12) ^ iArr2[i10]) ^ this.state[this.RATE_WORDS + (i10 & this.CAP_MASK)];
                } else {
                    this.state[i9] = ((i11 ^ i12) ^ iArr2[i9]) ^ this.state[this.RATE_WORDS + i9];
                    this.state[i10] = (i11 ^ iArr2[i10]) ^ this.state[this.RATE_WORDS + (i10 & this.CAP_MASK)];
                }
                int i13 = i9;
                iArr2[i13] = iArr2[i13] ^ i11;
                iArr2[i10] = iArr2[i10] ^ i12;
            }
            for (int i14 = 0; i14 < this.m_bufPos; i14++) {
                int i15 = i;
                i++;
                bArr[i15] = (byte) (iArr2[i14 >>> 2] >>> ((i14 & 3) << 3));
            }
            sparkle_opt(this.state, this.SPARKLE_STEPS_BIG);
        }
        for (int i16 = 0; i16 < this.KEY_WORDS; i16++) {
            int[] iArr3 = this.state;
            int i17 = this.RATE_WORDS + i16;
            iArr3[i17] = iArr3[i17] ^ this.k[i16];
        }
        this.mac = new byte[this.MAC_SIZE];
        Pack.intToLittleEndian(this.state, this.RATE_WORDS, this.TAG_WORDS, this.mac, 0);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    protected void processBufferAAD(byte[] bArr, int i) {
        for (int i2 = 0; i2 < this.RATE_WORDS / 2; i2++) {
            int i3 = i2 + (this.RATE_WORDS / 2);
            int i4 = this.state[i2];
            int i5 = this.state[i3];
            int iLittleEndianToInt = Pack.littleEndianToInt(bArr, i + (i2 * 4));
            int iLittleEndianToInt2 = Pack.littleEndianToInt(bArr, i + (i3 * 4));
            this.state[i2] = (i5 ^ iLittleEndianToInt) ^ this.state[this.RATE_WORDS + i2];
            this.state[i3] = ((i4 ^ i5) ^ iLittleEndianToInt2) ^ this.state[this.RATE_WORDS + (i3 & this.CAP_MASK)];
        }
        sparkle_opt(this.state, this.SPARKLE_STEPS_SLIM);
    }

    private void processBufferDecrypt(byte[] bArr, int i, byte[] bArr2, int i2) {
        for (int i3 = 0; i3 < this.RATE_WORDS / 2; i3++) {
            int i4 = i3 + (this.RATE_WORDS / 2);
            int i5 = this.state[i3];
            int i6 = this.state[i4];
            int iLittleEndianToInt = Pack.littleEndianToInt(bArr, i + (i3 * 4));
            int iLittleEndianToInt2 = Pack.littleEndianToInt(bArr, i + (i4 * 4));
            this.state[i3] = ((i5 ^ i6) ^ iLittleEndianToInt) ^ this.state[this.RATE_WORDS + i3];
            this.state[i4] = (i5 ^ iLittleEndianToInt2) ^ this.state[this.RATE_WORDS + (i4 & this.CAP_MASK)];
            Pack.intToLittleEndian(iLittleEndianToInt ^ i5, bArr2, i2 + (i3 * 4));
            Pack.intToLittleEndian(iLittleEndianToInt2 ^ i6, bArr2, i2 + (i4 * 4));
        }
        sparkle_opt(this.state, this.SPARKLE_STEPS_SLIM);
        this.encrypted = true;
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    protected void processBuffer(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (this.forEncryption) {
            processBufferEncrypt(bArr, i, bArr2, i2);
        } else {
            processBufferDecrypt(bArr, i, bArr2, i2);
        }
    }

    private void processBufferEncrypt(byte[] bArr, int i, byte[] bArr2, int i2) {
        for (int i3 = 0; i3 < this.RATE_WORDS / 2; i3++) {
            int i4 = i3 + (this.RATE_WORDS / 2);
            int i5 = this.state[i3];
            int i6 = this.state[i4];
            int iLittleEndianToInt = Pack.littleEndianToInt(bArr, i + (i3 * 4));
            int iLittleEndianToInt2 = Pack.littleEndianToInt(bArr, i + (i4 * 4));
            this.state[i3] = (i6 ^ iLittleEndianToInt) ^ this.state[this.RATE_WORDS + i3];
            this.state[i4] = ((i5 ^ i6) ^ iLittleEndianToInt2) ^ this.state[this.RATE_WORDS + (i4 & this.CAP_MASK)];
            Pack.intToLittleEndian(iLittleEndianToInt ^ i5, bArr2, i2 + (i3 * 4));
            Pack.intToLittleEndian(iLittleEndianToInt2 ^ i6, bArr2, i2 + (i4 * 4));
        }
        sparkle_opt(this.state, this.SPARKLE_STEPS_SLIM);
        this.encrypted = true;
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    protected void processFinalAAD() {
        if (this.m_aadPos < this.BlockSize) {
            int[] iArr = this.state;
            int i = this.STATE_WORDS - 1;
            iArr[i] = iArr[i] ^ this._A0;
            this.m_aad[this.m_aadPos] = -128;
            while (true) {
                int i2 = this.m_aadPos + 1;
                this.m_aadPos = i2;
                if (i2 >= this.BlockSize) {
                    break;
                } else {
                    this.m_aad[this.m_aadPos] = 0;
                }
            }
        } else {
            int[] iArr2 = this.state;
            int i3 = this.STATE_WORDS - 1;
            iArr2[i3] = iArr2[i3] ^ this._A1;
        }
        for (int i4 = 0; i4 < this.RATE_WORDS / 2; i4++) {
            int i5 = i4 + (this.RATE_WORDS / 2);
            int i6 = this.state[i4];
            int i7 = this.state[i5];
            int iLittleEndianToInt = Pack.littleEndianToInt(this.m_aad, i4 * 4);
            int iLittleEndianToInt2 = Pack.littleEndianToInt(this.m_aad, i5 * 4);
            this.state[i4] = (i7 ^ iLittleEndianToInt) ^ this.state[this.RATE_WORDS + i4];
            this.state[i5] = ((i6 ^ i7) ^ iLittleEndianToInt2) ^ this.state[this.RATE_WORDS + (i5 & this.CAP_MASK)];
        }
        sparkle_opt(this.state, this.SPARKLE_STEPS_BIG);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    protected void reset(boolean z) {
        bufferReset();
        this.encrypted = false;
        System.arraycopy(this.npub, 0, this.state, 0, this.RATE_WORDS);
        System.arraycopy(this.k, 0, this.state, this.RATE_WORDS, this.KEY_WORDS);
        sparkle_opt(this.state, this.SPARKLE_STEPS_BIG);
        super.reset(z);
    }

    private static int ELL(int i) {
        return Integers.rotateRight(i, 16) ^ (i & 65535);
    }

    private static void sparkle_opt(int[] iArr, int i) {
        switch (iArr.length) {
            case 8:
                sparkle_opt8(iArr, i);
                return;
            case 12:
                sparkle_opt12(iArr, i);
                return;
            case 16:
                sparkle_opt16(iArr, i);
                return;
            default:
                throw new IllegalStateException();
        }
    }

    static void sparkle_opt8(int[] iArr, int i) {
        int i2 = iArr[0];
        int i3 = iArr[1];
        int i4 = iArr[2];
        int i5 = iArr[3];
        int i6 = iArr[4];
        int i7 = iArr[5];
        int i8 = iArr[6];
        int i9 = iArr[7];
        for (int i10 = 0; i10 < i; i10++) {
            int i11 = i3 ^ RCON[i10 & 7];
            int i12 = i5 ^ i10;
            int i13 = RCON[0];
            int iRotateRight = i2 + Integers.rotateRight(i11, 31);
            int iRotateRight2 = i11 ^ Integers.rotateRight(iRotateRight, 24);
            int iRotateRight3 = (iRotateRight ^ i13) + Integers.rotateRight(iRotateRight2, 17);
            int iRotateRight4 = iRotateRight2 ^ Integers.rotateRight(iRotateRight3, 17);
            int i14 = (iRotateRight3 ^ i13) + iRotateRight4;
            int iRotateRight5 = iRotateRight4 ^ Integers.rotateRight(i14, 31);
            int iRotateRight6 = (i14 ^ i13) + Integers.rotateRight(iRotateRight5, 24);
            int iRotateRight7 = iRotateRight5 ^ Integers.rotateRight(iRotateRight6, 16);
            int i15 = iRotateRight6 ^ i13;
            int i16 = RCON[1];
            int iRotateRight8 = i4 + Integers.rotateRight(i12, 31);
            int iRotateRight9 = i12 ^ Integers.rotateRight(iRotateRight8, 24);
            int iRotateRight10 = (iRotateRight8 ^ i16) + Integers.rotateRight(iRotateRight9, 17);
            int iRotateRight11 = iRotateRight9 ^ Integers.rotateRight(iRotateRight10, 17);
            int i17 = (iRotateRight10 ^ i16) + iRotateRight11;
            int iRotateRight12 = iRotateRight11 ^ Integers.rotateRight(i17, 31);
            int iRotateRight13 = (i17 ^ i16) + Integers.rotateRight(iRotateRight12, 24);
            int iRotateRight14 = iRotateRight12 ^ Integers.rotateRight(iRotateRight13, 16);
            int i18 = iRotateRight13 ^ i16;
            int i19 = RCON[2];
            int iRotateRight15 = i6 + Integers.rotateRight(i7, 31);
            int iRotateRight16 = i7 ^ Integers.rotateRight(iRotateRight15, 24);
            int iRotateRight17 = (iRotateRight15 ^ i19) + Integers.rotateRight(iRotateRight16, 17);
            int iRotateRight18 = iRotateRight16 ^ Integers.rotateRight(iRotateRight17, 17);
            int i20 = (iRotateRight17 ^ i19) + iRotateRight18;
            int iRotateRight19 = iRotateRight18 ^ Integers.rotateRight(i20, 31);
            int iRotateRight20 = (i20 ^ i19) + Integers.rotateRight(iRotateRight19, 24);
            int iRotateRight21 = iRotateRight19 ^ Integers.rotateRight(iRotateRight20, 16);
            int i21 = iRotateRight20 ^ i19;
            int i22 = RCON[3];
            int iRotateRight22 = i8 + Integers.rotateRight(i9, 31);
            int iRotateRight23 = i9 ^ Integers.rotateRight(iRotateRight22, 24);
            int iRotateRight24 = (iRotateRight22 ^ i22) + Integers.rotateRight(iRotateRight23, 17);
            int iRotateRight25 = iRotateRight23 ^ Integers.rotateRight(iRotateRight24, 17);
            int i23 = (iRotateRight24 ^ i22) + iRotateRight25;
            int iRotateRight26 = iRotateRight25 ^ Integers.rotateRight(i23, 31);
            int iRotateRight27 = (i23 ^ i22) + Integers.rotateRight(iRotateRight26, 24);
            int iRotateRight28 = iRotateRight26 ^ Integers.rotateRight(iRotateRight27, 16);
            int i24 = iRotateRight27 ^ i22;
            int iELL = ELL(i15 ^ i18);
            int iELL2 = ELL(iRotateRight7 ^ iRotateRight14);
            int i25 = i15 ^ i21;
            int i26 = iRotateRight7 ^ iRotateRight21;
            i6 = i15;
            i7 = iRotateRight7;
            i8 = i18;
            i9 = iRotateRight14;
            i2 = (i18 ^ i24) ^ iELL2;
            i3 = (iRotateRight14 ^ iRotateRight28) ^ iELL;
            i4 = i25 ^ iELL2;
            i5 = i26 ^ iELL;
        }
        iArr[0] = i2;
        iArr[1] = i3;
        iArr[2] = i4;
        iArr[3] = i5;
        iArr[4] = i6;
        iArr[5] = i7;
        iArr[6] = i8;
        iArr[7] = i9;
    }

    static void sparkle_opt12(int[] iArr, int i) {
        int i2 = iArr[0];
        int i3 = iArr[1];
        int i4 = iArr[2];
        int i5 = iArr[3];
        int i6 = iArr[4];
        int i7 = iArr[5];
        int i8 = iArr[6];
        int i9 = iArr[7];
        int i10 = iArr[8];
        int i11 = iArr[9];
        int i12 = iArr[10];
        int i13 = iArr[11];
        for (int i14 = 0; i14 < i; i14++) {
            int i15 = i3 ^ RCON[i14 & 7];
            int i16 = i5 ^ i14;
            int i17 = RCON[0];
            int iRotateRight = i2 + Integers.rotateRight(i15, 31);
            int iRotateRight2 = i15 ^ Integers.rotateRight(iRotateRight, 24);
            int iRotateRight3 = (iRotateRight ^ i17) + Integers.rotateRight(iRotateRight2, 17);
            int iRotateRight4 = iRotateRight2 ^ Integers.rotateRight(iRotateRight3, 17);
            int i18 = (iRotateRight3 ^ i17) + iRotateRight4;
            int iRotateRight5 = iRotateRight4 ^ Integers.rotateRight(i18, 31);
            int iRotateRight6 = (i18 ^ i17) + Integers.rotateRight(iRotateRight5, 24);
            int iRotateRight7 = iRotateRight5 ^ Integers.rotateRight(iRotateRight6, 16);
            int i19 = iRotateRight6 ^ i17;
            int i20 = RCON[1];
            int iRotateRight8 = i4 + Integers.rotateRight(i16, 31);
            int iRotateRight9 = i16 ^ Integers.rotateRight(iRotateRight8, 24);
            int iRotateRight10 = (iRotateRight8 ^ i20) + Integers.rotateRight(iRotateRight9, 17);
            int iRotateRight11 = iRotateRight9 ^ Integers.rotateRight(iRotateRight10, 17);
            int i21 = (iRotateRight10 ^ i20) + iRotateRight11;
            int iRotateRight12 = iRotateRight11 ^ Integers.rotateRight(i21, 31);
            int iRotateRight13 = (i21 ^ i20) + Integers.rotateRight(iRotateRight12, 24);
            int iRotateRight14 = iRotateRight12 ^ Integers.rotateRight(iRotateRight13, 16);
            int i22 = iRotateRight13 ^ i20;
            int i23 = RCON[2];
            int iRotateRight15 = i6 + Integers.rotateRight(i7, 31);
            int iRotateRight16 = i7 ^ Integers.rotateRight(iRotateRight15, 24);
            int iRotateRight17 = (iRotateRight15 ^ i23) + Integers.rotateRight(iRotateRight16, 17);
            int iRotateRight18 = iRotateRight16 ^ Integers.rotateRight(iRotateRight17, 17);
            int i24 = (iRotateRight17 ^ i23) + iRotateRight18;
            int iRotateRight19 = iRotateRight18 ^ Integers.rotateRight(i24, 31);
            int iRotateRight20 = (i24 ^ i23) + Integers.rotateRight(iRotateRight19, 24);
            int iRotateRight21 = iRotateRight19 ^ Integers.rotateRight(iRotateRight20, 16);
            int i25 = iRotateRight20 ^ i23;
            int i26 = RCON[3];
            int iRotateRight22 = i8 + Integers.rotateRight(i9, 31);
            int iRotateRight23 = i9 ^ Integers.rotateRight(iRotateRight22, 24);
            int iRotateRight24 = (iRotateRight22 ^ i26) + Integers.rotateRight(iRotateRight23, 17);
            int iRotateRight25 = iRotateRight23 ^ Integers.rotateRight(iRotateRight24, 17);
            int i27 = (iRotateRight24 ^ i26) + iRotateRight25;
            int iRotateRight26 = iRotateRight25 ^ Integers.rotateRight(i27, 31);
            int iRotateRight27 = (i27 ^ i26) + Integers.rotateRight(iRotateRight26, 24);
            int iRotateRight28 = iRotateRight26 ^ Integers.rotateRight(iRotateRight27, 16);
            int i28 = iRotateRight27 ^ i26;
            int i29 = RCON[4];
            int iRotateRight29 = i10 + Integers.rotateRight(i11, 31);
            int iRotateRight30 = i11 ^ Integers.rotateRight(iRotateRight29, 24);
            int iRotateRight31 = (iRotateRight29 ^ i29) + Integers.rotateRight(iRotateRight30, 17);
            int iRotateRight32 = iRotateRight30 ^ Integers.rotateRight(iRotateRight31, 17);
            int i30 = (iRotateRight31 ^ i29) + iRotateRight32;
            int iRotateRight33 = iRotateRight32 ^ Integers.rotateRight(i30, 31);
            int iRotateRight34 = (i30 ^ i29) + Integers.rotateRight(iRotateRight33, 24);
            int iRotateRight35 = iRotateRight33 ^ Integers.rotateRight(iRotateRight34, 16);
            int i31 = iRotateRight34 ^ i29;
            int i32 = RCON[5];
            int iRotateRight36 = i12 + Integers.rotateRight(i13, 31);
            int iRotateRight37 = i13 ^ Integers.rotateRight(iRotateRight36, 24);
            int iRotateRight38 = (iRotateRight36 ^ i32) + Integers.rotateRight(iRotateRight37, 17);
            int iRotateRight39 = iRotateRight37 ^ Integers.rotateRight(iRotateRight38, 17);
            int i33 = (iRotateRight38 ^ i32) + iRotateRight39;
            int iRotateRight40 = iRotateRight39 ^ Integers.rotateRight(i33, 31);
            int iRotateRight41 = (i33 ^ i32) + Integers.rotateRight(iRotateRight40, 24);
            int iRotateRight42 = iRotateRight40 ^ Integers.rotateRight(iRotateRight41, 16);
            int i34 = iRotateRight41 ^ i32;
            int iELL = ELL((i19 ^ i22) ^ i25);
            int iELL2 = ELL((iRotateRight7 ^ iRotateRight14) ^ iRotateRight21);
            int i35 = i19 ^ i28;
            int i36 = iRotateRight7 ^ iRotateRight28;
            int i37 = i22 ^ i31;
            int i38 = iRotateRight14 ^ iRotateRight35;
            i8 = i19;
            i9 = iRotateRight7;
            i10 = i22;
            i11 = iRotateRight14;
            i12 = i25;
            i13 = iRotateRight21;
            i2 = i37 ^ iELL2;
            i3 = i38 ^ iELL;
            i4 = (i25 ^ i34) ^ iELL2;
            i5 = (iRotateRight21 ^ iRotateRight42) ^ iELL;
            i6 = i35 ^ iELL2;
            i7 = i36 ^ iELL;
        }
        iArr[0] = i2;
        iArr[1] = i3;
        iArr[2] = i4;
        iArr[3] = i5;
        iArr[4] = i6;
        iArr[5] = i7;
        iArr[6] = i8;
        iArr[7] = i9;
        iArr[8] = i10;
        iArr[9] = i11;
        iArr[10] = i12;
        iArr[11] = i13;
    }

    public static void sparkle_opt12(SparkleDigest.Friend friend, int[] iArr, int i) {
        if (null == friend) {
            throw new NullPointerException("This method is only for use by SparkleDigest");
        }
        sparkle_opt12(iArr, i);
    }

    static void sparkle_opt16(int[] iArr, int i) {
        int i2 = iArr[0];
        int i3 = iArr[1];
        int i4 = iArr[2];
        int i5 = iArr[3];
        int i6 = iArr[4];
        int i7 = iArr[5];
        int i8 = iArr[6];
        int i9 = iArr[7];
        int i10 = iArr[8];
        int i11 = iArr[9];
        int i12 = iArr[10];
        int i13 = iArr[11];
        int i14 = iArr[12];
        int i15 = iArr[13];
        int i16 = iArr[14];
        int i17 = iArr[15];
        for (int i18 = 0; i18 < i; i18++) {
            int i19 = i3 ^ RCON[i18 & 7];
            int i20 = i5 ^ i18;
            int i21 = RCON[0];
            int iRotateRight = i2 + Integers.rotateRight(i19, 31);
            int iRotateRight2 = i19 ^ Integers.rotateRight(iRotateRight, 24);
            int iRotateRight3 = (iRotateRight ^ i21) + Integers.rotateRight(iRotateRight2, 17);
            int iRotateRight4 = iRotateRight2 ^ Integers.rotateRight(iRotateRight3, 17);
            int i22 = (iRotateRight3 ^ i21) + iRotateRight4;
            int iRotateRight5 = iRotateRight4 ^ Integers.rotateRight(i22, 31);
            int iRotateRight6 = (i22 ^ i21) + Integers.rotateRight(iRotateRight5, 24);
            int iRotateRight7 = iRotateRight5 ^ Integers.rotateRight(iRotateRight6, 16);
            int i23 = iRotateRight6 ^ i21;
            int i24 = RCON[1];
            int iRotateRight8 = i4 + Integers.rotateRight(i20, 31);
            int iRotateRight9 = i20 ^ Integers.rotateRight(iRotateRight8, 24);
            int iRotateRight10 = (iRotateRight8 ^ i24) + Integers.rotateRight(iRotateRight9, 17);
            int iRotateRight11 = iRotateRight9 ^ Integers.rotateRight(iRotateRight10, 17);
            int i25 = (iRotateRight10 ^ i24) + iRotateRight11;
            int iRotateRight12 = iRotateRight11 ^ Integers.rotateRight(i25, 31);
            int iRotateRight13 = (i25 ^ i24) + Integers.rotateRight(iRotateRight12, 24);
            int iRotateRight14 = iRotateRight12 ^ Integers.rotateRight(iRotateRight13, 16);
            int i26 = iRotateRight13 ^ i24;
            int i27 = RCON[2];
            int iRotateRight15 = i6 + Integers.rotateRight(i7, 31);
            int iRotateRight16 = i7 ^ Integers.rotateRight(iRotateRight15, 24);
            int iRotateRight17 = (iRotateRight15 ^ i27) + Integers.rotateRight(iRotateRight16, 17);
            int iRotateRight18 = iRotateRight16 ^ Integers.rotateRight(iRotateRight17, 17);
            int i28 = (iRotateRight17 ^ i27) + iRotateRight18;
            int iRotateRight19 = iRotateRight18 ^ Integers.rotateRight(i28, 31);
            int iRotateRight20 = (i28 ^ i27) + Integers.rotateRight(iRotateRight19, 24);
            int iRotateRight21 = iRotateRight19 ^ Integers.rotateRight(iRotateRight20, 16);
            int i29 = iRotateRight20 ^ i27;
            int i30 = RCON[3];
            int iRotateRight22 = i8 + Integers.rotateRight(i9, 31);
            int iRotateRight23 = i9 ^ Integers.rotateRight(iRotateRight22, 24);
            int iRotateRight24 = (iRotateRight22 ^ i30) + Integers.rotateRight(iRotateRight23, 17);
            int iRotateRight25 = iRotateRight23 ^ Integers.rotateRight(iRotateRight24, 17);
            int i31 = (iRotateRight24 ^ i30) + iRotateRight25;
            int iRotateRight26 = iRotateRight25 ^ Integers.rotateRight(i31, 31);
            int iRotateRight27 = (i31 ^ i30) + Integers.rotateRight(iRotateRight26, 24);
            int iRotateRight28 = iRotateRight26 ^ Integers.rotateRight(iRotateRight27, 16);
            int i32 = iRotateRight27 ^ i30;
            int i33 = RCON[4];
            int iRotateRight29 = i10 + Integers.rotateRight(i11, 31);
            int iRotateRight30 = i11 ^ Integers.rotateRight(iRotateRight29, 24);
            int iRotateRight31 = (iRotateRight29 ^ i33) + Integers.rotateRight(iRotateRight30, 17);
            int iRotateRight32 = iRotateRight30 ^ Integers.rotateRight(iRotateRight31, 17);
            int i34 = (iRotateRight31 ^ i33) + iRotateRight32;
            int iRotateRight33 = iRotateRight32 ^ Integers.rotateRight(i34, 31);
            int iRotateRight34 = (i34 ^ i33) + Integers.rotateRight(iRotateRight33, 24);
            int iRotateRight35 = iRotateRight33 ^ Integers.rotateRight(iRotateRight34, 16);
            int i35 = iRotateRight34 ^ i33;
            int i36 = RCON[5];
            int iRotateRight36 = i12 + Integers.rotateRight(i13, 31);
            int iRotateRight37 = i13 ^ Integers.rotateRight(iRotateRight36, 24);
            int iRotateRight38 = (iRotateRight36 ^ i36) + Integers.rotateRight(iRotateRight37, 17);
            int iRotateRight39 = iRotateRight37 ^ Integers.rotateRight(iRotateRight38, 17);
            int i37 = (iRotateRight38 ^ i36) + iRotateRight39;
            int iRotateRight40 = iRotateRight39 ^ Integers.rotateRight(i37, 31);
            int iRotateRight41 = (i37 ^ i36) + Integers.rotateRight(iRotateRight40, 24);
            int iRotateRight42 = iRotateRight40 ^ Integers.rotateRight(iRotateRight41, 16);
            int i38 = iRotateRight41 ^ i36;
            int i39 = RCON[6];
            int iRotateRight43 = i14 + Integers.rotateRight(i15, 31);
            int iRotateRight44 = i15 ^ Integers.rotateRight(iRotateRight43, 24);
            int iRotateRight45 = (iRotateRight43 ^ i39) + Integers.rotateRight(iRotateRight44, 17);
            int iRotateRight46 = iRotateRight44 ^ Integers.rotateRight(iRotateRight45, 17);
            int i40 = (iRotateRight45 ^ i39) + iRotateRight46;
            int iRotateRight47 = iRotateRight46 ^ Integers.rotateRight(i40, 31);
            int iRotateRight48 = (i40 ^ i39) + Integers.rotateRight(iRotateRight47, 24);
            int iRotateRight49 = iRotateRight47 ^ Integers.rotateRight(iRotateRight48, 16);
            int i41 = iRotateRight48 ^ i39;
            int i42 = RCON[7];
            int iRotateRight50 = i16 + Integers.rotateRight(i17, 31);
            int iRotateRight51 = i17 ^ Integers.rotateRight(iRotateRight50, 24);
            int iRotateRight52 = (iRotateRight50 ^ i42) + Integers.rotateRight(iRotateRight51, 17);
            int iRotateRight53 = iRotateRight51 ^ Integers.rotateRight(iRotateRight52, 17);
            int i43 = (iRotateRight52 ^ i42) + iRotateRight53;
            int iRotateRight54 = iRotateRight53 ^ Integers.rotateRight(i43, 31);
            int iRotateRight55 = (i43 ^ i42) + Integers.rotateRight(iRotateRight54, 24);
            int iRotateRight56 = iRotateRight54 ^ Integers.rotateRight(iRotateRight55, 16);
            int i44 = iRotateRight55 ^ i42;
            int iELL = ELL(((i23 ^ i26) ^ i29) ^ i32);
            int iELL2 = ELL(((iRotateRight7 ^ iRotateRight14) ^ iRotateRight21) ^ iRotateRight28);
            int i45 = i23 ^ i35;
            int i46 = iRotateRight7 ^ iRotateRight35;
            int i47 = i26 ^ i38;
            int i48 = iRotateRight14 ^ iRotateRight42;
            int i49 = i29 ^ i41;
            int i50 = iRotateRight21 ^ iRotateRight49;
            i10 = i23;
            i11 = iRotateRight7;
            i12 = i26;
            i13 = iRotateRight14;
            i14 = i29;
            i15 = iRotateRight21;
            i16 = i32;
            i17 = iRotateRight28;
            i2 = i47 ^ iELL2;
            i3 = i48 ^ iELL;
            i4 = i49 ^ iELL2;
            i5 = i50 ^ iELL;
            i6 = (i32 ^ i44) ^ iELL2;
            i7 = (iRotateRight28 ^ iRotateRight56) ^ iELL;
            i8 = i45 ^ iELL2;
            i9 = i46 ^ iELL;
        }
        iArr[0] = i2;
        iArr[1] = i3;
        iArr[2] = i4;
        iArr[3] = i5;
        iArr[4] = i6;
        iArr[5] = i7;
        iArr[6] = i8;
        iArr[7] = i9;
        iArr[8] = i10;
        iArr[9] = i11;
        iArr[10] = i12;
        iArr[11] = i13;
        iArr[12] = i14;
        iArr[13] = i15;
        iArr[14] = i16;
        iArr[15] = i17;
    }

    public static void sparkle_opt16(SparkleDigest.Friend friend, int[] iArr, int i) {
        if (null == friend) {
            throw new NullPointerException("This method is only for use by SparkleDigest");
        }
        sparkle_opt16(iArr, i);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int getOutputSize(int i) {
        return super.getOutputSize(i);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int getUpdateOutputSize(int i) {
        return super.getUpdateOutputSize(i);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    public /* bridge */ /* synthetic */ int getBlockSize() {
        return super.getBlockSize();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int doFinal(byte[] bArr, int i) throws InvalidCipherTextException, IllegalStateException {
        return super.doFinal(bArr, i);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException {
        return super.processBytes(bArr, i, i2, bArr2, i3);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ void processAADBytes(byte[] bArr, int i, int i2) {
        super.processAADBytes(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ void processAADByte(byte b) {
        super.processAADByte(b);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ void init(boolean z, CipherParameters cipherParameters) {
        super.init(z, cipherParameters);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int processByte(byte b, byte[] bArr, int i) throws DataLengthException {
        return super.processByte(b, bArr, i);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ void reset() {
        super.reset();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ byte[] getMac() {
        return super.getMac();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    public /* bridge */ /* synthetic */ int getIVBytesSize() {
        return super.getIVBytesSize();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    public /* bridge */ /* synthetic */ int getKeyBytesSize() {
        return super.getKeyBytesSize();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ String getAlgorithmName() {
        return super.getAlgorithmName();
    }
}
