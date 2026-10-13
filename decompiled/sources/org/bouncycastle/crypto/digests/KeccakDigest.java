package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/KeccakDigest.class */
public class KeccakDigest implements ExtendedDigest {
    private static long[] KeccakRoundConstants = {1, 32898, -9223372036854742902L, -9223372034707259392L, 32907, 2147483649L, -9223372034707259263L, -9223372036854743031L, 138, 136, 2147516425L, 2147483658L, 2147516555L, -9223372036854775669L, -9223372036854742903L, -9223372036854743037L, -9223372036854743038L, -9223372036854775680L, 32778, -9223372034707292150L, -9223372034707259263L, -9223372036854742912L, 2147483649L, -9223372034707259384L};
    protected final CryptoServicePurpose purpose;
    protected long[] state;
    protected byte[] dataQueue;
    protected int rate;
    protected int bitsInQueue;
    protected int fixedOutputLength;
    protected boolean squeezing;

    public KeccakDigest() {
        this(288, CryptoServicePurpose.ANY);
    }

    public KeccakDigest(CryptoServicePurpose cryptoServicePurpose) {
        this(288, cryptoServicePurpose);
    }

    public KeccakDigest(int i) {
        this(i, CryptoServicePurpose.ANY);
    }

    public KeccakDigest(int i, CryptoServicePurpose cryptoServicePurpose) {
        this.state = new long[25];
        this.dataQueue = new byte[192];
        this.purpose = cryptoServicePurpose;
        init(i);
        CryptoServicesRegistrar.checkConstraints(cryptoServiceProperties());
    }

    public KeccakDigest(KeccakDigest keccakDigest) {
        this.state = new long[25];
        this.dataQueue = new byte[192];
        this.purpose = keccakDigest.purpose;
        System.arraycopy(keccakDigest.state, 0, this.state, 0, keccakDigest.state.length);
        System.arraycopy(keccakDigest.dataQueue, 0, this.dataQueue, 0, keccakDigest.dataQueue.length);
        this.rate = keccakDigest.rate;
        this.bitsInQueue = keccakDigest.bitsInQueue;
        this.fixedOutputLength = keccakDigest.fixedOutputLength;
        this.squeezing = keccakDigest.squeezing;
        CryptoServicesRegistrar.checkConstraints(cryptoServiceProperties());
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "Keccak-" + this.fixedOutputLength;
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return this.fixedOutputLength / 8;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) {
        absorb(b);
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) {
        absorb(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        squeeze(bArr, i, this.fixedOutputLength);
        reset();
        return getDigestSize();
    }

    protected int doFinal(byte[] bArr, int i, byte b, int i2) {
        if (i2 > 0) {
            absorbBits(b, i2);
        }
        squeeze(bArr, i, this.fixedOutputLength);
        reset();
        return getDigestSize();
    }

    @Override // org.bouncycastle.crypto.Digest
    public void reset() {
        init(this.fixedOutputLength);
    }

    @Override // org.bouncycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return this.rate / 8;
    }

    private void init(int i) {
        switch (i) {
            case 128:
            case 224:
            case 256:
            case 288:
            case 384:
            case 512:
                initSponge(1600 - (i << 1));
                return;
            default:
                throw new IllegalArgumentException("bitLength must be one of 128, 224, 256, 288, 384, or 512.");
        }
    }

    private void initSponge(int i) {
        if (i <= 0 || i >= 1600 || i % 64 != 0) {
            throw new IllegalStateException("invalid rate value");
        }
        this.rate = i;
        for (int i2 = 0; i2 < this.state.length; i2++) {
            this.state[i2] = 0;
        }
        Arrays.fill(this.dataQueue, (byte) 0);
        this.bitsInQueue = 0;
        this.squeezing = false;
        this.fixedOutputLength = (1600 - i) / 2;
    }

    protected void absorb(byte b) {
        if (this.bitsInQueue % 8 != 0) {
            throw new IllegalStateException("attempt to absorb with odd length queue");
        }
        if (this.squeezing) {
            throw new IllegalStateException("attempt to absorb while squeezing");
        }
        this.dataQueue[this.bitsInQueue >>> 3] = b;
        int i = this.bitsInQueue + 8;
        this.bitsInQueue = i;
        if (i == this.rate) {
            KeccakAbsorb(this.dataQueue, 0);
            this.bitsInQueue = 0;
        }
    }

    protected void absorb(byte[] bArr, int i, int i2) {
        if (this.bitsInQueue % 8 != 0) {
            throw new IllegalStateException("attempt to absorb with odd length queue");
        }
        if (this.squeezing) {
            throw new IllegalStateException("attempt to absorb while squeezing");
        }
        int i3 = this.bitsInQueue >>> 3;
        int i4 = this.rate >>> 3;
        int i5 = i4 - i3;
        if (i2 < i5) {
            System.arraycopy(bArr, i, this.dataQueue, i3, i2);
            this.bitsInQueue += i2 << 3;
            return;
        }
        int i6 = 0;
        if (i3 > 0) {
            System.arraycopy(bArr, i, this.dataQueue, i3, i5);
            i6 = 0 + i5;
            KeccakAbsorb(this.dataQueue, 0);
        }
        while (true) {
            int i7 = i2 - i6;
            if (i7 < i4) {
                System.arraycopy(bArr, i + i6, this.dataQueue, 0, i7);
                this.bitsInQueue = i7 << 3;
                return;
            } else {
                KeccakAbsorb(bArr, i + i6);
                i6 += i4;
            }
        }
    }

    protected void absorbBits(int i, int i2) {
        if (i2 < 1 || i2 > 7) {
            throw new IllegalArgumentException("'bits' must be in the range 1 to 7");
        }
        if (this.bitsInQueue % 8 != 0) {
            throw new IllegalStateException("attempt to absorb with odd length queue");
        }
        if (this.squeezing) {
            throw new IllegalStateException("attempt to absorb while squeezing");
        }
        this.dataQueue[this.bitsInQueue >>> 3] = (byte) (i & ((1 << i2) - 1));
        this.bitsInQueue += i2;
    }

    private void padAndSwitchToSqueezingPhase() {
        byte[] bArr = this.dataQueue;
        int i = this.bitsInQueue >>> 3;
        bArr[i] = (byte) (bArr[i] | ((byte) (1 << (this.bitsInQueue & 7))));
        int i2 = this.bitsInQueue + 1;
        this.bitsInQueue = i2;
        if (i2 == this.rate) {
            KeccakAbsorb(this.dataQueue, 0);
        } else {
            int i3 = this.bitsInQueue >>> 6;
            int i4 = this.bitsInQueue & 63;
            int i5 = 0;
            for (int i6 = 0; i6 < i3; i6++) {
                long[] jArr = this.state;
                int i7 = i6;
                jArr[i7] = jArr[i7] ^ Pack.littleEndianToLong(this.dataQueue, i5);
                i5 += 8;
            }
            if (i4 > 0) {
                long j = (1 << i4) - 1;
                long[] jArr2 = this.state;
                jArr2[i3] = jArr2[i3] ^ (Pack.littleEndianToLong(this.dataQueue, i5) & j);
            }
        }
        long[] jArr3 = this.state;
        int i8 = (this.rate - 1) >>> 6;
        jArr3[i8] = jArr3[i8] ^ Long.MIN_VALUE;
        this.bitsInQueue = 0;
        this.squeezing = true;
    }

    protected void squeeze(byte[] bArr, int i, long j) {
        if (!this.squeezing) {
            padAndSwitchToSqueezingPhase();
        }
        if (j % 8 != 0) {
            throw new IllegalStateException("outputLength not a multiple of 8");
        }
        long j2 = 0;
        while (true) {
            long j3 = j2;
            if (j3 >= j) {
                return;
            }
            if (this.bitsInQueue == 0) {
                KeccakExtract();
            }
            int iMin = (int) Math.min(this.bitsInQueue, j - j3);
            System.arraycopy(this.dataQueue, (this.rate - this.bitsInQueue) / 8, bArr, i + ((int) (j3 / 8)), iMin / 8);
            this.bitsInQueue -= iMin;
            j2 = j3 + ((long) iMin);
        }
    }

    private void KeccakAbsorb(byte[] bArr, int i) {
        int i2 = this.rate >>> 6;
        for (int i3 = 0; i3 < i2; i3++) {
            long[] jArr = this.state;
            int i4 = i3;
            jArr[i4] = jArr[i4] ^ Pack.littleEndianToLong(bArr, i);
            i += 8;
        }
        KeccakPermutation();
    }

    private void KeccakExtract() {
        KeccakPermutation();
        Pack.longToLittleEndian(this.state, 0, this.rate >>> 6, this.dataQueue, 0);
        this.bitsInQueue = this.rate;
    }

    private void KeccakPermutation() {
        long[] jArr = this.state;
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = jArr[2];
        long j4 = jArr[3];
        long j5 = jArr[4];
        long j6 = jArr[5];
        long j7 = jArr[6];
        long j8 = jArr[7];
        long j9 = jArr[8];
        long j10 = jArr[9];
        long j11 = jArr[10];
        long j12 = jArr[11];
        long j13 = jArr[12];
        long j14 = jArr[13];
        long j15 = jArr[14];
        long j16 = jArr[15];
        long j17 = jArr[16];
        long j18 = jArr[17];
        long j19 = jArr[18];
        long j20 = jArr[19];
        long j21 = jArr[20];
        long j22 = jArr[21];
        long j23 = jArr[22];
        long j24 = jArr[23];
        long j25 = jArr[24];
        for (int i = 0; i < 24; i++) {
            long j26 = (((j ^ j6) ^ j11) ^ j16) ^ j21;
            long j27 = (((j2 ^ j7) ^ j12) ^ j17) ^ j22;
            long j28 = (((j3 ^ j8) ^ j13) ^ j18) ^ j23;
            long j29 = (((j4 ^ j9) ^ j14) ^ j19) ^ j24;
            long j30 = (((j5 ^ j10) ^ j15) ^ j20) ^ j25;
            long j31 = ((j27 << 1) | (j27 >>> (-1))) ^ j30;
            long j32 = ((j28 << 1) | (j28 >>> (-1))) ^ j26;
            long j33 = ((j29 << 1) | (j29 >>> (-1))) ^ j27;
            long j34 = ((j30 << 1) | (j30 >>> (-1))) ^ j28;
            long j35 = ((j26 << 1) | (j26 >>> (-1))) ^ j29;
            long j36 = j ^ j31;
            long j37 = j6 ^ j31;
            long j38 = j11 ^ j31;
            long j39 = j16 ^ j31;
            long j40 = j21 ^ j31;
            long j41 = j2 ^ j32;
            long j42 = j7 ^ j32;
            long j43 = j12 ^ j32;
            long j44 = j17 ^ j32;
            long j45 = j22 ^ j32;
            long j46 = j3 ^ j33;
            long j47 = j8 ^ j33;
            long j48 = j13 ^ j33;
            long j49 = j18 ^ j33;
            long j50 = j23 ^ j33;
            long j51 = j4 ^ j34;
            long j52 = j9 ^ j34;
            long j53 = j14 ^ j34;
            long j54 = j19 ^ j34;
            long j55 = j24 ^ j34;
            long j56 = j5 ^ j35;
            long j57 = j10 ^ j35;
            long j58 = j15 ^ j35;
            long j59 = j20 ^ j35;
            long j60 = j25 ^ j35;
            long j61 = (j41 << 1) | (j41 >>> 63);
            long j62 = (j42 << 44) | (j42 >>> 20);
            long j63 = (j57 << 20) | (j57 >>> 44);
            long j64 = (j50 << 61) | (j50 >>> 3);
            long j65 = (j58 << 39) | (j58 >>> 25);
            long j66 = (j40 << 18) | (j40 >>> 46);
            long j67 = (j46 << 62) | (j46 >>> 2);
            long j68 = (j48 << 43) | (j48 >>> 21);
            long j69 = (j53 << 25) | (j53 >>> 39);
            long j70 = (j59 << 8) | (j59 >>> 56);
            long j71 = (j55 << 56) | (j55 >>> 8);
            long j72 = (j39 << 41) | (j39 >>> 23);
            long j73 = (j56 << 27) | (j56 >>> 37);
            long j74 = (j60 << 14) | (j60 >>> 50);
            long j75 = (j45 << 2) | (j45 >>> 62);
            long j76 = (j52 << 55) | (j52 >>> 9);
            long j77 = (j44 << 45) | (j44 >>> 19);
            long j78 = (j37 << 36) | (j37 >>> 28);
            long j79 = (j51 << 28) | (j51 >>> 36);
            long j80 = (j54 << 21) | (j54 >>> 43);
            long j81 = (j49 << 15) | (j49 >>> 49);
            long j82 = (j43 << 10) | (j43 >>> 54);
            long j83 = (j47 << 6) | (j47 >>> 58);
            long j84 = (j38 << 3) | (j38 >>> 61);
            long j85 = j36 ^ ((j62 ^ (-1)) & j68);
            j3 = j68 ^ ((j80 ^ (-1)) & j74);
            j4 = j80 ^ ((j74 ^ (-1)) & j36);
            j5 = j74 ^ ((j36 ^ (-1)) & j62);
            j2 = j62 ^ ((j68 ^ (-1)) & j80);
            j8 = j84 ^ ((j77 ^ (-1)) & j64);
            j9 = j77 ^ ((j64 ^ (-1)) & j79);
            j10 = j64 ^ ((j79 ^ (-1)) & j63);
            j6 = j79 ^ ((j63 ^ (-1)) & j84);
            j7 = j63 ^ ((j84 ^ (-1)) & j77);
            j13 = j69 ^ ((j70 ^ (-1)) & j66);
            j14 = j70 ^ ((j66 ^ (-1)) & j61);
            j15 = j66 ^ ((j61 ^ (-1)) & j83);
            j11 = j61 ^ ((j83 ^ (-1)) & j69);
            j12 = j83 ^ ((j69 ^ (-1)) & j70);
            j18 = j82 ^ ((j81 ^ (-1)) & j71);
            j19 = j81 ^ ((j71 ^ (-1)) & j73);
            j20 = j71 ^ ((j73 ^ (-1)) & j78);
            j16 = j73 ^ ((j78 ^ (-1)) & j82);
            j17 = j78 ^ ((j82 ^ (-1)) & j81);
            j23 = j65 ^ ((j72 ^ (-1)) & j75);
            j24 = j72 ^ ((j75 ^ (-1)) & j67);
            j25 = j75 ^ ((j67 ^ (-1)) & j76);
            j21 = j67 ^ ((j76 ^ (-1)) & j65);
            j22 = j76 ^ ((j65 ^ (-1)) & j72);
            j = j85 ^ KeccakRoundConstants[i];
        }
        jArr[0] = j;
        jArr[1] = j2;
        jArr[2] = j3;
        jArr[3] = j4;
        jArr[4] = j5;
        jArr[5] = j6;
        jArr[6] = j7;
        jArr[7] = j8;
        jArr[8] = j9;
        jArr[9] = j10;
        jArr[10] = j11;
        jArr[11] = j12;
        jArr[12] = j13;
        jArr[13] = j14;
        jArr[14] = j15;
        jArr[15] = j16;
        jArr[16] = j17;
        jArr[17] = j18;
        jArr[18] = j19;
        jArr[19] = j20;
        jArr[20] = j21;
        jArr[21] = j22;
        jArr[22] = j23;
        jArr[23] = j24;
        jArr[24] = j25;
    }

    protected CryptoServiceProperties cryptoServiceProperties() {
        return Utils.getDefaultProperties(this, getDigestSize() * 8, this.purpose);
    }
}
