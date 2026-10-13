package org.spongycastle.crypto.engines;

import org.spongycastle.asn1.cmc.BodyPartID;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.MaxBytesExceededException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.SkippingStreamCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.Pack;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/engines/Salsa20Engine.class */
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

    protected void packTauOrSigma(int keyLength, int[] state, int stateOffset) {
        int tsOff = (keyLength - 16) / 4;
        state[stateOffset] = TAU_SIGMA[tsOff];
        state[stateOffset + 1] = TAU_SIGMA[tsOff + 1];
        state[stateOffset + 2] = TAU_SIGMA[tsOff + 2];
        state[stateOffset + 3] = TAU_SIGMA[tsOff + 3];
    }

    public Salsa20Engine() {
        this(20);
    }

    public Salsa20Engine(int rounds) {
        this.index = 0;
        this.engineState = new int[16];
        this.x = new int[16];
        this.keyStream = new byte[64];
        this.initialised = false;
        if (rounds <= 0 || (rounds & 1) != 0) {
            throw new IllegalArgumentException("'rounds' must be a positive, even number");
        }
        this.rounds = rounds;
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public void init(boolean forEncryption, CipherParameters params) {
        if (!(params instanceof ParametersWithIV)) {
            throw new IllegalArgumentException(getAlgorithmName() + " Init parameters must include an IV");
        }
        ParametersWithIV ivParams = (ParametersWithIV) params;
        byte[] iv = ivParams.getIV();
        if (iv == null || iv.length != getNonceSize()) {
            throw new IllegalArgumentException(getAlgorithmName() + " requires exactly " + getNonceSize() + " bytes of IV");
        }
        CipherParameters keyParam = ivParams.getParameters();
        if (keyParam == null) {
            if (!this.initialised) {
                throw new IllegalStateException(getAlgorithmName() + " KeyParameter can not be null for first initialisation");
            }
            setKey(null, iv);
        } else if (keyParam instanceof KeyParameter) {
            setKey(((KeyParameter) keyParam).getKey(), iv);
        } else {
            throw new IllegalArgumentException(getAlgorithmName() + " Init parameters must contain a KeyParameter (or null for re-init)");
        }
        reset();
        this.initialised = true;
    }

    protected int getNonceSize() {
        return 8;
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public String getAlgorithmName() {
        String name = "Salsa20";
        if (this.rounds != 20) {
            name = name + "/" + this.rounds;
        }
        return name;
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public byte returnByte(byte in) {
        if (limitExceeded()) {
            throw new MaxBytesExceededException("2^70 byte limit per IV; Change IV");
        }
        byte out = (byte) (this.keyStream[this.index] ^ in);
        this.index = (this.index + 1) & 63;
        if (this.index == 0) {
            advanceCounter();
            generateKeyStream(this.keyStream);
        }
        return out;
    }

    protected void advanceCounter(long diff) {
        int hi = (int) (diff >>> 32);
        int lo = (int) diff;
        if (hi > 0) {
            int[] iArr = this.engineState;
            iArr[9] = iArr[9] + hi;
        }
        int oldState = this.engineState[8];
        int[] iArr2 = this.engineState;
        iArr2[8] = iArr2[8] + lo;
        if (oldState != 0 && this.engineState[8] < oldState) {
            int[] iArr3 = this.engineState;
            iArr3[9] = iArr3[9] + 1;
        }
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

    protected void retreatCounter(long diff) {
        int hi = (int) (diff >>> 32);
        int lo = (int) diff;
        if (hi != 0) {
            if ((((long) this.engineState[9]) & BodyPartID.bodyIdMax) >= (((long) hi) & BodyPartID.bodyIdMax)) {
                int[] iArr = this.engineState;
                iArr[9] = iArr[9] - hi;
            } else {
                throw new IllegalStateException("attempt to reduce counter past zero.");
            }
        }
        if ((((long) this.engineState[8]) & BodyPartID.bodyIdMax) >= (((long) lo) & BodyPartID.bodyIdMax)) {
            int[] iArr2 = this.engineState;
            iArr2[8] = iArr2[8] - lo;
        } else {
            if (this.engineState[9] != 0) {
                int[] iArr3 = this.engineState;
                iArr3[9] = iArr3[9] - 1;
                int[] iArr4 = this.engineState;
                iArr4[8] = iArr4[8] - lo;
                return;
            }
            throw new IllegalStateException("attempt to reduce counter past zero.");
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

    @Override // org.spongycastle.crypto.StreamCipher
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) {
        if (!this.initialised) {
            throw new IllegalStateException(getAlgorithmName() + " not initialised");
        }
        if (inOff + len > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + len > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (limitExceeded(len)) {
            throw new MaxBytesExceededException("2^70 byte limit per IV would be exceeded; Change IV");
        }
        for (int i = 0; i < len; i++) {
            out[i + outOff] = (byte) (this.keyStream[this.index] ^ in[i + inOff]);
            this.index = (this.index + 1) & 63;
            if (this.index == 0) {
                advanceCounter();
                generateKeyStream(this.keyStream);
            }
        }
        return len;
    }

    @Override // org.spongycastle.crypto.SkippingCipher
    public long skip(long numberOfBytes) {
        if (numberOfBytes >= 0) {
            long remaining = numberOfBytes;
            if (remaining >= 64) {
                long count = remaining / 64;
                advanceCounter(count);
                remaining -= count * 64;
            }
            int oldIndex = this.index;
            this.index = (this.index + ((int) remaining)) & 63;
            if (this.index < oldIndex) {
                advanceCounter();
            }
        } else {
            long remaining2 = -numberOfBytes;
            if (remaining2 >= 64) {
                long count2 = remaining2 / 64;
                retreatCounter(count2);
                remaining2 -= count2 * 64;
            }
            long j = 0;
            while (true) {
                long i = j;
                if (i >= remaining2) {
                    break;
                }
                if (this.index == 0) {
                    retreatCounter();
                }
                this.index = (this.index - 1) & 63;
                j = i + 1;
            }
        }
        generateKeyStream(this.keyStream);
        return numberOfBytes;
    }

    @Override // org.spongycastle.crypto.SkippingCipher
    public long seekTo(long position) {
        reset();
        return skip(position);
    }

    @Override // org.spongycastle.crypto.SkippingCipher
    public long getPosition() {
        return (getCounter() * 64) + ((long) this.index);
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public void reset() {
        this.index = 0;
        resetLimitCounter();
        resetCounter();
        generateKeyStream(this.keyStream);
    }

    protected long getCounter() {
        return (((long) this.engineState[9]) << 32) | (((long) this.engineState[8]) & BodyPartID.bodyIdMax);
    }

    protected void resetCounter() {
        int[] iArr = this.engineState;
        this.engineState[9] = 0;
        iArr[8] = 0;
    }

    protected void setKey(byte[] keyBytes, byte[] ivBytes) {
        if (keyBytes != null) {
            if (keyBytes.length != 16 && keyBytes.length != 32) {
                throw new IllegalArgumentException(getAlgorithmName() + " requires 128 bit or 256 bit key");
            }
            int tsOff = (keyBytes.length - 16) / 4;
            this.engineState[0] = TAU_SIGMA[tsOff];
            this.engineState[5] = TAU_SIGMA[tsOff + 1];
            this.engineState[10] = TAU_SIGMA[tsOff + 2];
            this.engineState[15] = TAU_SIGMA[tsOff + 3];
            Pack.littleEndianToInt(keyBytes, 0, this.engineState, 1, 4);
            Pack.littleEndianToInt(keyBytes, keyBytes.length - 16, this.engineState, 11, 4);
        }
        Pack.littleEndianToInt(ivBytes, 0, this.engineState, 6, 2);
    }

    protected void generateKeyStream(byte[] output) {
        salsaCore(this.rounds, this.engineState, this.x);
        Pack.intToLittleEndian(this.x, output, 0);
    }

    public static void salsaCore(int rounds, int[] input, int[] x) {
        if (input.length != 16) {
            throw new IllegalArgumentException();
        }
        if (x.length != 16) {
            throw new IllegalArgumentException();
        }
        if (rounds % 2 != 0) {
            throw new IllegalArgumentException("Number of rounds must be even");
        }
        int x00 = input[0];
        int x01 = input[1];
        int x02 = input[2];
        int x03 = input[3];
        int x04 = input[4];
        int x05 = input[5];
        int x06 = input[6];
        int x07 = input[7];
        int x08 = input[8];
        int x09 = input[9];
        int x10 = input[10];
        int x11 = input[11];
        int x12 = input[12];
        int x13 = input[13];
        int x14 = input[14];
        int x15 = input[15];
        for (int i = rounds; i > 0; i -= 2) {
            int x010 = x04 ^ rotl(x00 + x12, 7);
            int x011 = x08 ^ rotl(x010 + x00, 9);
            int x16 = x12 ^ rotl(x011 + x010, 13);
            int x012 = x00 ^ rotl(x16 + x011, 18);
            int x013 = x09 ^ rotl(x05 + x01, 7);
            int x17 = x13 ^ rotl(x013 + x05, 9);
            int x014 = x01 ^ rotl(x17 + x013, 13);
            int x015 = x05 ^ rotl(x014 + x17, 18);
            int x18 = x14 ^ rotl(x10 + x06, 7);
            int x016 = x02 ^ rotl(x18 + x10, 9);
            int x017 = x06 ^ rotl(x016 + x18, 13);
            int x19 = x10 ^ rotl(x017 + x016, 18);
            int x018 = x03 ^ rotl(x15 + x11, 7);
            int x019 = x07 ^ rotl(x018 + x15, 9);
            int x110 = x11 ^ rotl(x019 + x018, 13);
            int x111 = x15 ^ rotl(x110 + x019, 18);
            x01 = x014 ^ rotl(x012 + x018, 7);
            x02 = x016 ^ rotl(x01 + x012, 9);
            x03 = x018 ^ rotl(x02 + x01, 13);
            x00 = x012 ^ rotl(x03 + x02, 18);
            x06 = x017 ^ rotl(x015 + x010, 7);
            x07 = x019 ^ rotl(x06 + x015, 9);
            x04 = x010 ^ rotl(x07 + x06, 13);
            x05 = x015 ^ rotl(x04 + x07, 18);
            x11 = x110 ^ rotl(x19 + x013, 7);
            x08 = x011 ^ rotl(x11 + x19, 9);
            x09 = x013 ^ rotl(x08 + x11, 13);
            x10 = x19 ^ rotl(x09 + x08, 18);
            x12 = x16 ^ rotl(x111 + x18, 7);
            x13 = x17 ^ rotl(x12 + x111, 9);
            x14 = x18 ^ rotl(x13 + x12, 13);
            x15 = x111 ^ rotl(x14 + x13, 18);
        }
        x[0] = x00 + input[0];
        x[1] = x01 + input[1];
        x[2] = x02 + input[2];
        x[3] = x03 + input[3];
        x[4] = x04 + input[4];
        x[5] = x05 + input[5];
        x[6] = x06 + input[6];
        x[7] = x07 + input[7];
        x[8] = x08 + input[8];
        x[9] = x09 + input[9];
        x[10] = x10 + input[10];
        x[11] = x11 + input[11];
        x[12] = x12 + input[12];
        x[13] = x13 + input[13];
        x[14] = x14 + input[14];
        x[15] = x15 + input[15];
    }

    protected static int rotl(int x, int y) {
        return (x << y) | (x >>> (-y));
    }

    private void resetLimitCounter() {
        this.cW0 = 0;
        this.cW1 = 0;
        this.cW2 = 0;
    }

    private boolean limitExceeded() {
        int i = this.cW0 + 1;
        this.cW0 = i;
        if (i == 0) {
            int i2 = this.cW1 + 1;
            this.cW1 = i2;
            if (i2 == 0) {
                int i3 = this.cW2 + 1;
                this.cW2 = i3;
                return (i3 & 32) != 0;
            }
            return false;
        }
        return false;
    }

    private boolean limitExceeded(int len) {
        this.cW0 += len;
        if (this.cW0 < len && this.cW0 >= 0) {
            int i = this.cW1 + 1;
            this.cW1 = i;
            if (i == 0) {
                int i2 = this.cW2 + 1;
                this.cW2 = i2;
                return (i2 & 32) != 0;
            }
            return false;
        }
        return false;
    }
}
