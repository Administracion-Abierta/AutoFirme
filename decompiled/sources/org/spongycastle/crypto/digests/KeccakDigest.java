package org.spongycastle.crypto.digests;

import org.spongycastle.crypto.ExtendedDigest;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/digests/KeccakDigest.class */
public class KeccakDigest implements ExtendedDigest {
    private static long[] KeccakRoundConstants = keccakInitializeRoundConstants();
    private static int[] KeccakRhoOffsets = keccakInitializeRhoOffsets();
    protected long[] state;
    protected byte[] dataQueue;
    protected int rate;
    protected int bitsInQueue;
    protected int fixedOutputLength;
    protected boolean squeezing;

    private static long[] keccakInitializeRoundConstants() {
        long[] keccakRoundConstants = new long[24];
        byte[] LFSRstate = {1};
        for (int i = 0; i < 24; i++) {
            keccakRoundConstants[i] = 0;
            for (int j = 0; j < 7; j++) {
                int bitPosition = (1 << j) - 1;
                if (LFSR86540(LFSRstate)) {
                    int i2 = i;
                    keccakRoundConstants[i2] = keccakRoundConstants[i2] ^ (1 << bitPosition);
                }
            }
        }
        return keccakRoundConstants;
    }

    private static boolean LFSR86540(byte[] LFSR) {
        boolean result = (LFSR[0] & 1) != 0;
        if ((LFSR[0] & 128) != 0) {
            LFSR[0] = (byte) ((LFSR[0] << 1) ^ 113);
        } else {
            LFSR[0] = (byte) (LFSR[0] << 1);
        }
        return result;
    }

    private static int[] keccakInitializeRhoOffsets() {
        int[] keccakRhoOffsets = new int[25];
        keccakRhoOffsets[0] = 0;
        int x = 1;
        int y = 0;
        for (int t = 0; t < 24; t++) {
            keccakRhoOffsets[(x % 5) + (5 * (y % 5))] = (((t + 1) * (t + 2)) / 2) % 64;
            int newX = ((0 * x) + (1 * y)) % 5;
            int newY = ((2 * x) + (3 * y)) % 5;
            x = newX;
            y = newY;
        }
        return keccakRhoOffsets;
    }

    public KeccakDigest() {
        this(288);
    }

    public KeccakDigest(int bitLength) {
        this.state = new long[25];
        this.dataQueue = new byte[192];
        init(bitLength);
    }

    public KeccakDigest(KeccakDigest source) {
        this.state = new long[25];
        this.dataQueue = new byte[192];
        System.arraycopy(source.state, 0, this.state, 0, source.state.length);
        System.arraycopy(source.dataQueue, 0, this.dataQueue, 0, source.dataQueue.length);
        this.rate = source.rate;
        this.bitsInQueue = source.bitsInQueue;
        this.fixedOutputLength = source.fixedOutputLength;
        this.squeezing = source.squeezing;
    }

    @Override // org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        return "Keccak-" + this.fixedOutputLength;
    }

    @Override // org.spongycastle.crypto.Digest
    public int getDigestSize() {
        return this.fixedOutputLength / 8;
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte in) {
        absorb(new byte[]{in}, 0, 1);
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte[] in, int inOff, int len) {
        absorb(in, inOff, len);
    }

    @Override // org.spongycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        squeeze(out, outOff, this.fixedOutputLength);
        reset();
        return getDigestSize();
    }

    protected int doFinal(byte[] out, int outOff, byte partialByte, int partialBits) {
        if (partialBits > 0) {
            absorbBits(partialByte, partialBits);
        }
        squeeze(out, outOff, this.fixedOutputLength);
        reset();
        return getDigestSize();
    }

    @Override // org.spongycastle.crypto.Digest
    public void reset() {
        init(this.fixedOutputLength);
    }

    @Override // org.spongycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return this.rate / 8;
    }

    private void init(int bitLength) {
        switch (bitLength) {
            case 128:
            case 224:
            case 256:
            case 288:
            case 384:
            case 512:
                initSponge(1600 - (bitLength << 1));
                return;
            default:
                throw new IllegalArgumentException("bitLength must be one of 128, 224, 256, 288, 384, or 512.");
        }
    }

    private void initSponge(int rate) {
        if (rate <= 0 || rate >= 1600 || rate % 64 != 0) {
            throw new IllegalStateException("invalid rate value");
        }
        this.rate = rate;
        for (int i = 0; i < this.state.length; i++) {
            this.state[i] = 0;
        }
        Arrays.fill(this.dataQueue, (byte) 0);
        this.bitsInQueue = 0;
        this.squeezing = false;
        this.fixedOutputLength = (1600 - rate) / 2;
    }

    protected void absorb(byte[] data, int off, int len) {
        if (this.bitsInQueue % 8 != 0) {
            throw new IllegalStateException("attempt to absorb with odd length queue");
        }
        if (this.squeezing) {
            throw new IllegalStateException("attempt to absorb while squeezing");
        }
        int bytesInQueue = this.bitsInQueue >> 3;
        int rateBytes = this.rate >> 3;
        int count = 0;
        while (count < len) {
            if (bytesInQueue == 0 && count <= len - rateBytes) {
                do {
                    KeccakAbsorb(data, off + count);
                    count += rateBytes;
                } while (count <= len - rateBytes);
            } else {
                int partialBlock = Math.min(rateBytes - bytesInQueue, len - count);
                System.arraycopy(data, off + count, this.dataQueue, bytesInQueue, partialBlock);
                bytesInQueue += partialBlock;
                count += partialBlock;
                if (bytesInQueue == rateBytes) {
                    KeccakAbsorb(this.dataQueue, 0);
                    bytesInQueue = 0;
                }
            }
        }
        this.bitsInQueue = bytesInQueue << 3;
    }

    protected void absorbBits(int data, int bits) {
        if (bits < 1 || bits > 7) {
            throw new IllegalArgumentException("'bits' must be in the range 1 to 7");
        }
        if (this.bitsInQueue % 8 != 0) {
            throw new IllegalStateException("attempt to absorb with odd length queue");
        }
        if (this.squeezing) {
            throw new IllegalStateException("attempt to absorb while squeezing");
        }
        int mask = (1 << bits) - 1;
        this.dataQueue[this.bitsInQueue >> 3] = (byte) (data & mask);
        this.bitsInQueue += bits;
    }

    private void padAndSwitchToSqueezingPhase() {
        byte[] bArr = this.dataQueue;
        int i = this.bitsInQueue >> 3;
        bArr[i] = (byte) (bArr[i] | ((byte) (1 << (this.bitsInQueue & 7))));
        int i2 = this.bitsInQueue + 1;
        this.bitsInQueue = i2;
        if (i2 == this.rate) {
            KeccakAbsorb(this.dataQueue, 0);
            this.bitsInQueue = 0;
        }
        int full = this.bitsInQueue >> 6;
        int partial = this.bitsInQueue & 63;
        int off = 0;
        for (int i3 = 0; i3 < full; i3++) {
            long[] jArr = this.state;
            int i4 = i3;
            jArr[i4] = jArr[i4] ^ Pack.littleEndianToLong(this.dataQueue, off);
            off += 8;
        }
        if (partial > 0) {
            long mask = (1 << partial) - 1;
            long[] jArr2 = this.state;
            jArr2[full] = jArr2[full] ^ (Pack.littleEndianToLong(this.dataQueue, off) & mask);
        }
        long[] jArr3 = this.state;
        int i5 = (this.rate - 1) >> 6;
        jArr3[i5] = jArr3[i5] ^ Long.MIN_VALUE;
        KeccakPermutation();
        KeccakExtract();
        this.bitsInQueue = this.rate;
        this.squeezing = true;
    }

    protected void squeeze(byte[] output, int offset, long outputLength) {
        if (!this.squeezing) {
            padAndSwitchToSqueezingPhase();
        }
        if (outputLength % 8 != 0) {
            throw new IllegalStateException("outputLength not a multiple of 8");
        }
        long j = 0;
        while (true) {
            long i = j;
            if (i < outputLength) {
                if (this.bitsInQueue == 0) {
                    KeccakPermutation();
                    KeccakExtract();
                    this.bitsInQueue = this.rate;
                }
                int partialBlock = (int) Math.min(this.bitsInQueue, outputLength - i);
                System.arraycopy(this.dataQueue, (this.rate - this.bitsInQueue) / 8, output, offset + ((int) (i / 8)), partialBlock / 8);
                this.bitsInQueue -= partialBlock;
                j = i + ((long) partialBlock);
            } else {
                return;
            }
        }
    }

    private void KeccakAbsorb(byte[] data, int off) {
        int count = this.rate >> 6;
        for (int i = 0; i < count; i++) {
            long[] jArr = this.state;
            int i2 = i;
            jArr[i2] = jArr[i2] ^ Pack.littleEndianToLong(data, off);
            off += 8;
        }
        KeccakPermutation();
    }

    private void KeccakExtract() {
        Pack.longToLittleEndian(this.state, 0, this.rate >> 6, this.dataQueue, 0);
    }

    private void KeccakPermutation() {
        for (int i = 0; i < 24; i++) {
            theta(this.state);
            rho(this.state);
            pi(this.state);
            chi(this.state);
            iota(this.state, i);
        }
    }

    private static long leftRotate(long v, int r) {
        return (v << r) | (v >>> (-r));
    }

    private static void theta(long[] A) {
        long C0 = (((A[0] ^ A[5]) ^ A[10]) ^ A[15]) ^ A[20];
        long C1 = (((A[1] ^ A[6]) ^ A[11]) ^ A[16]) ^ A[21];
        long C2 = (((A[2] ^ A[7]) ^ A[12]) ^ A[17]) ^ A[22];
        long C3 = (((A[3] ^ A[8]) ^ A[13]) ^ A[18]) ^ A[23];
        long C4 = (((A[4] ^ A[9]) ^ A[14]) ^ A[19]) ^ A[24];
        long dX = leftRotate(C1, 1) ^ C4;
        A[0] = A[0] ^ dX;
        A[5] = A[5] ^ dX;
        A[10] = A[10] ^ dX;
        A[15] = A[15] ^ dX;
        A[20] = A[20] ^ dX;
        long dX2 = leftRotate(C2, 1) ^ C0;
        A[1] = A[1] ^ dX2;
        A[6] = A[6] ^ dX2;
        A[11] = A[11] ^ dX2;
        A[16] = A[16] ^ dX2;
        A[21] = A[21] ^ dX2;
        long dX3 = leftRotate(C3, 1) ^ C1;
        A[2] = A[2] ^ dX3;
        A[7] = A[7] ^ dX3;
        A[12] = A[12] ^ dX3;
        A[17] = A[17] ^ dX3;
        A[22] = A[22] ^ dX3;
        long dX4 = leftRotate(C4, 1) ^ C2;
        A[3] = A[3] ^ dX4;
        A[8] = A[8] ^ dX4;
        A[13] = A[13] ^ dX4;
        A[18] = A[18] ^ dX4;
        A[23] = A[23] ^ dX4;
        long dX5 = leftRotate(C0, 1) ^ C3;
        A[4] = A[4] ^ dX5;
        A[9] = A[9] ^ dX5;
        A[14] = A[14] ^ dX5;
        A[19] = A[19] ^ dX5;
        A[24] = A[24] ^ dX5;
    }

    private static void rho(long[] A) {
        for (int x = 1; x < 25; x++) {
            A[x] = leftRotate(A[x], KeccakRhoOffsets[x]);
        }
    }

    private static void pi(long[] A) {
        long a1 = A[1];
        A[1] = A[6];
        A[6] = A[9];
        A[9] = A[22];
        A[22] = A[14];
        A[14] = A[20];
        A[20] = A[2];
        A[2] = A[12];
        A[12] = A[13];
        A[13] = A[19];
        A[19] = A[23];
        A[23] = A[15];
        A[15] = A[4];
        A[4] = A[24];
        A[24] = A[21];
        A[21] = A[8];
        A[8] = A[16];
        A[16] = A[5];
        A[5] = A[3];
        A[3] = A[18];
        A[18] = A[17];
        A[17] = A[11];
        A[11] = A[7];
        A[7] = A[10];
        A[10] = a1;
    }

    private static void chi(long[] A) {
        for (int yBy5 = 0; yBy5 < 25; yBy5 += 5) {
            long chiC0 = A[0 + yBy5] ^ ((A[1 + yBy5] ^ (-1)) & A[2 + yBy5]);
            long chiC1 = A[1 + yBy5] ^ ((A[2 + yBy5] ^ (-1)) & A[3 + yBy5]);
            long chiC2 = A[2 + yBy5] ^ ((A[3 + yBy5] ^ (-1)) & A[4 + yBy5]);
            long chiC3 = A[3 + yBy5] ^ ((A[4 + yBy5] ^ (-1)) & A[0 + yBy5]);
            long chiC4 = A[4 + yBy5] ^ ((A[0 + yBy5] ^ (-1)) & A[1 + yBy5]);
            A[0 + yBy5] = chiC0;
            A[1 + yBy5] = chiC1;
            A[2 + yBy5] = chiC2;
            A[3 + yBy5] = chiC3;
            A[4 + yBy5] = chiC4;
        }
    }

    private static void iota(long[] A, int indexRound) {
        A[0] = A[0] ^ KeccakRoundConstants[indexRound];
    }
}
