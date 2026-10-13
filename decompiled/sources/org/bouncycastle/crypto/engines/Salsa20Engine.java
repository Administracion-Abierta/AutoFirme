package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.MaxBytesExceededException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.SkippingStreamCipher;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/Salsa20Engine.class */
public class Salsa20Engine implements SkippingStreamCipher {
    public static final int DEFAULT_ROUNDS = 20;
    private static final int STATE_SIZE = 16;
    private static final int[] TAU_SIGMA = Pack.littleEndianToInt(Strings.toByteArray("expand 16-byte kexpand 32-byte k"), 0, 8);
    protected static final byte[] sigma = Strings.toByteArray("expand 32-byte k");
    protected static final byte[] tau = Strings.toByteArray("expand 16-byte k");
    protected int rounds;
    private int index;
    protected int[] engineState;
    protected int[] x;
    private byte[] keyStream;
    private boolean initialised;
    private int cW0;
    private int cW1;
    private int cW2;

    protected void packTauOrSigma(int i, int[] iArr, int i2) {
        int i3 = (i - 16) / 4;
        iArr[i2] = TAU_SIGMA[i3];
        iArr[i2 + 1] = TAU_SIGMA[i3 + 1];
        iArr[i2 + 2] = TAU_SIGMA[i3 + 2];
        iArr[i2 + 3] = TAU_SIGMA[i3 + 3];
    }

    public Salsa20Engine() {
        this(20);
    }

    public Salsa20Engine(int i) {
        this.index = 0;
        this.engineState = new int[16];
        this.x = new int[16];
        this.keyStream = new byte[64];
        this.initialised = false;
        if (i <= 0 || (i & 1) != 0) {
            throw new IllegalArgumentException("'rounds' must be a positive, even number");
        }
        this.rounds = i;
    }

    @Override // org.bouncycastle.crypto.StreamCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof ParametersWithIV)) {
            throw new IllegalArgumentException(getAlgorithmName() + " Init parameters must include an IV");
        }
        ParametersWithIV parametersWithIV = (ParametersWithIV) cipherParameters;
        byte[] iv = parametersWithIV.getIV();
        if (iv == null || iv.length != getNonceSize()) {
            throw new IllegalArgumentException(getAlgorithmName() + " requires exactly " + getNonceSize() + " bytes of IV");
        }
        CipherParameters parameters = parametersWithIV.getParameters();
        if (parameters == null) {
            if (!this.initialised) {
                throw new IllegalStateException(getAlgorithmName() + " KeyParameter can not be null for first initialisation");
            }
            setKey(null, iv);
        } else {
            if (!(parameters instanceof KeyParameter)) {
                throw new IllegalArgumentException(getAlgorithmName() + " Init parameters must contain a KeyParameter (or null for re-init)");
            }
            byte[] key = ((KeyParameter) parameters).getKey();
            setKey(key, iv);
            CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), key.length * 8, cipherParameters, Utils.getPurpose(z)));
        }
        reset();
        this.initialised = true;
    }

    protected int getNonceSize() {
        return 8;
    }

    @Override // org.bouncycastle.crypto.StreamCipher
    public String getAlgorithmName() {
        return this.rounds != 20 ? "Salsa20/" + this.rounds : "Salsa20";
    }

    @Override // org.bouncycastle.crypto.StreamCipher
    public byte returnByte(byte b) {
        if (limitExceeded()) {
            throw new MaxBytesExceededException("2^70 byte limit per IV; Change IV");
        }
        byte b2 = (byte) (this.keyStream[this.index] ^ b);
        this.index = (this.index + 1) & 63;
        if (this.index == 0) {
            advanceCounter();
            generateKeyStream(this.keyStream);
        }
        return b2;
    }

    protected void advanceCounter(long j) {
        int i = (int) (j >>> 32);
        int i2 = (int) j;
        if (i > 0) {
            int[] iArr = this.engineState;
            iArr[9] = iArr[9] + i;
        }
        int i3 = this.engineState[8];
        int[] iArr2 = this.engineState;
        iArr2[8] = iArr2[8] + i2;
        if (i3 == 0 || this.engineState[8] >= i3) {
            return;
        }
        int[] iArr3 = this.engineState;
        iArr3[9] = iArr3[9] + 1;
    }

    protected void advanceCounter() {
        int[] iArr = this.engineState;
        int i = iArr[8] + 1;
        iArr[8] = i;
        if (i == 0) {
            int[] iArr2 = this.engineState;
            iArr2[9] = iArr2[9] + 1;
        }
    }

    protected void retreatCounter(long j) {
        int i = (int) (j >>> 32);
        int i2 = (int) j;
        if (i != 0) {
            if ((((long) this.engineState[9]) & 4294967295L) < (((long) i) & 4294967295L)) {
                throw new IllegalStateException("attempt to reduce counter past zero.");
            }
            int[] iArr = this.engineState;
            iArr[9] = iArr[9] - i;
        }
        if ((((long) this.engineState[8]) & 4294967295L) >= (((long) i2) & 4294967295L)) {
            int[] iArr2 = this.engineState;
            iArr2[8] = iArr2[8] - i2;
        } else {
            if (this.engineState[9] == 0) {
                throw new IllegalStateException("attempt to reduce counter past zero.");
            }
            int[] iArr3 = this.engineState;
            iArr3[9] = iArr3[9] - 1;
            int[] iArr4 = this.engineState;
            iArr4[8] = iArr4[8] - i2;
        }
    }

    protected void retreatCounter() {
        if (this.engineState[8] == 0 && this.engineState[9] == 0) {
            throw new IllegalStateException("attempt to reduce counter past zero.");
        }
        int[] iArr = this.engineState;
        int i = iArr[8] - 1;
        iArr[8] = i;
        if (i == -1) {
            int[] iArr2 = this.engineState;
            iArr2[9] = iArr2[9] - 1;
        }
    }

    @Override // org.bouncycastle.crypto.StreamCipher
    public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        if (!this.initialised) {
            throw new IllegalStateException(getAlgorithmName() + " not initialised");
        }
        if (i + i2 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (i3 + i2 > bArr2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (limitExceeded(i2)) {
            throw new MaxBytesExceededException("2^70 byte limit per IV would be exceeded; Change IV");
        }
        for (int i4 = 0; i4 < i2; i4++) {
            bArr2[i4 + i3] = (byte) (this.keyStream[this.index] ^ bArr[i4 + i]);
            this.index = (this.index + 1) & 63;
            if (this.index == 0) {
                advanceCounter();
                generateKeyStream(this.keyStream);
            }
        }
        return i2;
    }

    @Override // org.bouncycastle.crypto.SkippingCipher
    public long skip(long j) {
        if (j < 0) {
            long j2 = -j;
            if (j2 >= 64) {
                long j3 = j2 / 64;
                retreatCounter(j3);
                j2 -= j3 * 64;
            }
            long j4 = 0;
            while (true) {
                long j5 = j4;
                if (j5 >= j2) {
                    break;
                }
                if (this.index == 0) {
                    retreatCounter();
                }
                this.index = (this.index - 1) & 63;
                j4 = j5 + 1;
            }
        } else {
            long j6 = j;
            if (j6 >= 64) {
                long j7 = j6 / 64;
                advanceCounter(j7);
                j6 -= j7 * 64;
            }
            int i = this.index;
            this.index = (this.index + ((int) j6)) & 63;
            if (this.index < i) {
                advanceCounter();
            }
        }
        generateKeyStream(this.keyStream);
        return j;
    }

    @Override // org.bouncycastle.crypto.SkippingCipher
    public long seekTo(long j) {
        reset();
        return skip(j);
    }

    @Override // org.bouncycastle.crypto.SkippingCipher
    public long getPosition() {
        return (getCounter() * 64) + ((long) this.index);
    }

    @Override // org.bouncycastle.crypto.StreamCipher
    public void reset() {
        this.index = 0;
        resetLimitCounter();
        resetCounter();
        generateKeyStream(this.keyStream);
    }

    protected long getCounter() {
        return (((long) this.engineState[9]) << 32) | (((long) this.engineState[8]) & 4294967295L);
    }

    protected void resetCounter() {
        int[] iArr = this.engineState;
        this.engineState[9] = 0;
        iArr[8] = 0;
    }

    protected void setKey(byte[] bArr, byte[] bArr2) {
        if (bArr != null) {
            if (bArr.length != 16 && bArr.length != 32) {
                throw new IllegalArgumentException(getAlgorithmName() + " requires 128 bit or 256 bit key");
            }
            int length = (bArr.length - 16) / 4;
            this.engineState[0] = TAU_SIGMA[length];
            this.engineState[5] = TAU_SIGMA[length + 1];
            this.engineState[10] = TAU_SIGMA[length + 2];
            this.engineState[15] = TAU_SIGMA[length + 3];
            Pack.littleEndianToInt(bArr, 0, this.engineState, 1, 4);
            Pack.littleEndianToInt(bArr, bArr.length - 16, this.engineState, 11, 4);
        }
        Pack.littleEndianToInt(bArr2, 0, this.engineState, 6, 2);
    }

    protected void generateKeyStream(byte[] bArr) {
        salsaCore(this.rounds, this.engineState, this.x);
        Pack.intToLittleEndian(this.x, bArr, 0);
    }

    public static void salsaCore(int i, int[] iArr, int[] iArr2) {
        if (iArr.length != 16) {
            throw new IllegalArgumentException();
        }
        if (iArr2.length != 16) {
            throw new IllegalArgumentException();
        }
        if (i % 2 != 0) {
            throw new IllegalArgumentException("Number of rounds must be even");
        }
        int iRotateLeft = iArr[0];
        int iRotateLeft2 = iArr[1];
        int iRotateLeft3 = iArr[2];
        int iRotateLeft4 = iArr[3];
        int iRotateLeft5 = iArr[4];
        int iRotateLeft6 = iArr[5];
        int iRotateLeft7 = iArr[6];
        int iRotateLeft8 = iArr[7];
        int iRotateLeft9 = iArr[8];
        int iRotateLeft10 = iArr[9];
        int iRotateLeft11 = iArr[10];
        int iRotateLeft12 = iArr[11];
        int iRotateLeft13 = iArr[12];
        int iRotateLeft14 = iArr[13];
        int iRotateLeft15 = iArr[14];
        int iRotateLeft16 = iArr[15];
        for (int i2 = i; i2 > 0; i2 -= 2) {
            int iRotateLeft17 = iRotateLeft5 ^ Integers.rotateLeft(iRotateLeft + iRotateLeft13, 7);
            int iRotateLeft18 = iRotateLeft9 ^ Integers.rotateLeft(iRotateLeft17 + iRotateLeft, 9);
            int iRotateLeft19 = iRotateLeft13 ^ Integers.rotateLeft(iRotateLeft18 + iRotateLeft17, 13);
            int iRotateLeft20 = iRotateLeft ^ Integers.rotateLeft(iRotateLeft19 + iRotateLeft18, 18);
            int iRotateLeft21 = iRotateLeft10 ^ Integers.rotateLeft(iRotateLeft6 + iRotateLeft2, 7);
            int iRotateLeft22 = iRotateLeft14 ^ Integers.rotateLeft(iRotateLeft21 + iRotateLeft6, 9);
            int iRotateLeft23 = iRotateLeft2 ^ Integers.rotateLeft(iRotateLeft22 + iRotateLeft21, 13);
            int iRotateLeft24 = iRotateLeft6 ^ Integers.rotateLeft(iRotateLeft23 + iRotateLeft22, 18);
            int iRotateLeft25 = iRotateLeft15 ^ Integers.rotateLeft(iRotateLeft11 + iRotateLeft7, 7);
            int iRotateLeft26 = iRotateLeft3 ^ Integers.rotateLeft(iRotateLeft25 + iRotateLeft11, 9);
            int iRotateLeft27 = iRotateLeft7 ^ Integers.rotateLeft(iRotateLeft26 + iRotateLeft25, 13);
            int iRotateLeft28 = iRotateLeft11 ^ Integers.rotateLeft(iRotateLeft27 + iRotateLeft26, 18);
            int iRotateLeft29 = iRotateLeft4 ^ Integers.rotateLeft(iRotateLeft16 + iRotateLeft12, 7);
            int iRotateLeft30 = iRotateLeft8 ^ Integers.rotateLeft(iRotateLeft29 + iRotateLeft16, 9);
            int iRotateLeft31 = iRotateLeft12 ^ Integers.rotateLeft(iRotateLeft30 + iRotateLeft29, 13);
            int iRotateLeft32 = iRotateLeft16 ^ Integers.rotateLeft(iRotateLeft31 + iRotateLeft30, 18);
            iRotateLeft2 = iRotateLeft23 ^ Integers.rotateLeft(iRotateLeft20 + iRotateLeft29, 7);
            iRotateLeft3 = iRotateLeft26 ^ Integers.rotateLeft(iRotateLeft2 + iRotateLeft20, 9);
            iRotateLeft4 = iRotateLeft29 ^ Integers.rotateLeft(iRotateLeft3 + iRotateLeft2, 13);
            iRotateLeft = iRotateLeft20 ^ Integers.rotateLeft(iRotateLeft4 + iRotateLeft3, 18);
            iRotateLeft7 = iRotateLeft27 ^ Integers.rotateLeft(iRotateLeft24 + iRotateLeft17, 7);
            iRotateLeft8 = iRotateLeft30 ^ Integers.rotateLeft(iRotateLeft7 + iRotateLeft24, 9);
            iRotateLeft5 = iRotateLeft17 ^ Integers.rotateLeft(iRotateLeft8 + iRotateLeft7, 13);
            iRotateLeft6 = iRotateLeft24 ^ Integers.rotateLeft(iRotateLeft5 + iRotateLeft8, 18);
            iRotateLeft12 = iRotateLeft31 ^ Integers.rotateLeft(iRotateLeft28 + iRotateLeft21, 7);
            iRotateLeft9 = iRotateLeft18 ^ Integers.rotateLeft(iRotateLeft12 + iRotateLeft28, 9);
            iRotateLeft10 = iRotateLeft21 ^ Integers.rotateLeft(iRotateLeft9 + iRotateLeft12, 13);
            iRotateLeft11 = iRotateLeft28 ^ Integers.rotateLeft(iRotateLeft10 + iRotateLeft9, 18);
            iRotateLeft13 = iRotateLeft19 ^ Integers.rotateLeft(iRotateLeft32 + iRotateLeft25, 7);
            iRotateLeft14 = iRotateLeft22 ^ Integers.rotateLeft(iRotateLeft13 + iRotateLeft32, 9);
            iRotateLeft15 = iRotateLeft25 ^ Integers.rotateLeft(iRotateLeft14 + iRotateLeft13, 13);
            iRotateLeft16 = iRotateLeft32 ^ Integers.rotateLeft(iRotateLeft15 + iRotateLeft14, 18);
        }
        iArr2[0] = iRotateLeft + iArr[0];
        iArr2[1] = iRotateLeft2 + iArr[1];
        iArr2[2] = iRotateLeft3 + iArr[2];
        iArr2[3] = iRotateLeft4 + iArr[3];
        iArr2[4] = iRotateLeft5 + iArr[4];
        iArr2[5] = iRotateLeft6 + iArr[5];
        iArr2[6] = iRotateLeft7 + iArr[6];
        iArr2[7] = iRotateLeft8 + iArr[7];
        iArr2[8] = iRotateLeft9 + iArr[8];
        iArr2[9] = iRotateLeft10 + iArr[9];
        iArr2[10] = iRotateLeft11 + iArr[10];
        iArr2[11] = iRotateLeft12 + iArr[11];
        iArr2[12] = iRotateLeft13 + iArr[12];
        iArr2[13] = iRotateLeft14 + iArr[13];
        iArr2[14] = iRotateLeft15 + iArr[14];
        iArr2[15] = iRotateLeft16 + iArr[15];
    }

    private void resetLimitCounter() {
        this.cW0 = 0;
        this.cW1 = 0;
        this.cW2 = 0;
    }

    private boolean limitExceeded() {
        int i = this.cW0 + 1;
        this.cW0 = i;
        if (i != 0) {
            return false;
        }
        int i2 = this.cW1 + 1;
        this.cW1 = i2;
        if (i2 != 0) {
            return false;
        }
        int i3 = this.cW2 + 1;
        this.cW2 = i3;
        return (i3 & 32) != 0;
    }

    private boolean limitExceeded(int i) {
        this.cW0 += i;
        if (this.cW0 >= i || this.cW0 < 0) {
            return false;
        }
        int i2 = this.cW1 + 1;
        this.cW1 = i2;
        if (i2 != 0) {
            return false;
        }
        int i3 = this.cW2 + 1;
        this.cW2 = i3;
        return (i3 & 32) != 0;
    }
}
