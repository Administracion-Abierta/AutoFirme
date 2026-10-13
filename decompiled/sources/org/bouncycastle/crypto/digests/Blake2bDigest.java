package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Longs;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/Blake2bDigest.class */
public class Blake2bDigest implements ExtendedDigest {
    private static final long[] blake2b_IV = {7640891576956012808L, -4942790177534073029L, 4354685564936845355L, -6534734903238641935L, 5840696475078001361L, -7276294671716946913L, 2270897969802886507L, 6620516959819538809L};
    private static final byte[][] blake2b_sigma = {new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, new byte[]{14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3}, new byte[]{11, 8, 12, 0, 5, 2, 15, 13, 10, 14, 3, 6, 7, 1, 9, 4}, new byte[]{7, 9, 3, 1, 13, 12, 11, 14, 2, 6, 5, 10, 4, 0, 15, 8}, new byte[]{9, 0, 5, 7, 2, 4, 10, 15, 14, 1, 11, 12, 6, 8, 3, 13}, new byte[]{2, 12, 6, 10, 0, 11, 8, 3, 4, 13, 7, 5, 15, 14, 1, 9}, new byte[]{12, 5, 1, 15, 14, 13, 4, 10, 0, 7, 6, 3, 9, 2, 8, 11}, new byte[]{13, 11, 7, 14, 12, 1, 3, 9, 5, 0, 15, 4, 8, 6, 2, 10}, new byte[]{6, 15, 14, 9, 11, 3, 0, 8, 12, 2, 13, 7, 1, 4, 10, 5}, new byte[]{10, 2, 8, 4, 7, 6, 1, 5, 15, 11, 9, 14, 3, 12, 13, 0}, new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, new byte[]{14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3}};
    private static int ROUNDS = 12;
    private static final int BLOCK_LENGTH_BYTES = 128;
    private int digestLength;
    private int keyLength;
    private byte[] salt;
    private byte[] personalization;
    private byte[] key;
    private int fanout;
    private int depth;
    private int leafLength;
    private long nodeOffset;
    private int nodeDepth;
    private int innerHashLength;
    private boolean isLastNode;
    private byte[] buffer;
    private int bufferPos;
    private long[] internalState;
    private long[] chainValue;
    private long t0;
    private long t1;
    private long f0;
    private long f1;
    private final CryptoServicePurpose purpose;

    public Blake2bDigest() {
        this(512, CryptoServicePurpose.ANY);
    }

    public Blake2bDigest(int i) {
        this(i, CryptoServicePurpose.ANY);
    }

    public Blake2bDigest(Blake2bDigest blake2bDigest) {
        this.digestLength = 64;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.fanout = 1;
        this.depth = 1;
        this.leafLength = 0;
        this.nodeOffset = 0L;
        this.nodeDepth = 0;
        this.innerHashLength = 0;
        this.isLastNode = false;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new long[16];
        this.chainValue = null;
        this.t0 = 0L;
        this.t1 = 0L;
        this.f0 = 0L;
        this.f1 = 0L;
        this.bufferPos = blake2bDigest.bufferPos;
        this.buffer = Arrays.clone(blake2bDigest.buffer);
        this.keyLength = blake2bDigest.keyLength;
        this.key = Arrays.clone(blake2bDigest.key);
        this.digestLength = blake2bDigest.digestLength;
        this.chainValue = Arrays.clone(blake2bDigest.chainValue);
        this.personalization = Arrays.clone(blake2bDigest.personalization);
        this.salt = Arrays.clone(blake2bDigest.salt);
        this.t0 = blake2bDigest.t0;
        this.t1 = blake2bDigest.t1;
        this.f0 = blake2bDigest.f0;
        this.purpose = blake2bDigest.purpose;
    }

    public Blake2bDigest(int i, CryptoServicePurpose cryptoServicePurpose) {
        this.digestLength = 64;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.fanout = 1;
        this.depth = 1;
        this.leafLength = 0;
        this.nodeOffset = 0L;
        this.nodeDepth = 0;
        this.innerHashLength = 0;
        this.isLastNode = false;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new long[16];
        this.chainValue = null;
        this.t0 = 0L;
        this.t1 = 0L;
        this.f0 = 0L;
        this.f1 = 0L;
        this.purpose = cryptoServicePurpose;
        if (i < 8 || i > 512 || i % 8 != 0) {
            throw new IllegalArgumentException("BLAKE2b digest bit length must be a multiple of 8 and not greater than 512");
        }
        this.buffer = new byte[128];
        this.keyLength = 0;
        this.digestLength = i / 8;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, i, cryptoServicePurpose));
        init();
    }

    public Blake2bDigest(byte[] bArr) {
        this(bArr, CryptoServicePurpose.ANY);
    }

    public Blake2bDigest(byte[] bArr, CryptoServicePurpose cryptoServicePurpose) {
        this.digestLength = 64;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.fanout = 1;
        this.depth = 1;
        this.leafLength = 0;
        this.nodeOffset = 0L;
        this.nodeDepth = 0;
        this.innerHashLength = 0;
        this.isLastNode = false;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new long[16];
        this.chainValue = null;
        this.t0 = 0L;
        this.t1 = 0L;
        this.f0 = 0L;
        this.f1 = 0L;
        this.buffer = new byte[128];
        if (bArr != null) {
            this.key = new byte[bArr.length];
            System.arraycopy(bArr, 0, this.key, 0, bArr.length);
            if (bArr.length > 64) {
                throw new IllegalArgumentException("Keys > 64 are not supported");
            }
            this.keyLength = bArr.length;
            System.arraycopy(bArr, 0, this.buffer, 0, bArr.length);
            this.bufferPos = 128;
        }
        this.purpose = cryptoServicePurpose;
        this.digestLength = 64;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, this.digestLength * 8, cryptoServicePurpose));
        init();
    }

    public Blake2bDigest(byte[] bArr, int i, byte[] bArr2, byte[] bArr3) {
        this(bArr, i, bArr2, bArr3, CryptoServicePurpose.ANY);
    }

    public Blake2bDigest(byte[] bArr, int i, byte[] bArr2, byte[] bArr3, CryptoServicePurpose cryptoServicePurpose) {
        this.digestLength = 64;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.fanout = 1;
        this.depth = 1;
        this.leafLength = 0;
        this.nodeOffset = 0L;
        this.nodeDepth = 0;
        this.innerHashLength = 0;
        this.isLastNode = false;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new long[16];
        this.chainValue = null;
        this.t0 = 0L;
        this.t1 = 0L;
        this.f0 = 0L;
        this.f1 = 0L;
        this.purpose = cryptoServicePurpose;
        this.buffer = new byte[128];
        if (i < 1 || i > 64) {
            throw new IllegalArgumentException("Invalid digest length (required: 1 - 64)");
        }
        this.digestLength = i;
        if (bArr2 != null) {
            if (bArr2.length != 16) {
                throw new IllegalArgumentException("salt length must be exactly 16 bytes");
            }
            this.salt = new byte[16];
            System.arraycopy(bArr2, 0, this.salt, 0, bArr2.length);
        }
        if (bArr3 != null) {
            if (bArr3.length != 16) {
                throw new IllegalArgumentException("personalization length must be exactly 16 bytes");
            }
            this.personalization = new byte[16];
            System.arraycopy(bArr3, 0, this.personalization, 0, bArr3.length);
        }
        if (bArr != null) {
            this.key = new byte[bArr.length];
            System.arraycopy(bArr, 0, this.key, 0, bArr.length);
            if (bArr.length > 64) {
                throw new IllegalArgumentException("Keys > 64 are not supported");
            }
            this.keyLength = bArr.length;
            System.arraycopy(bArr, 0, this.buffer, 0, bArr.length);
            this.bufferPos = 128;
        }
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, i * 8, cryptoServicePurpose));
        init();
    }

    public Blake2bDigest(byte[] bArr, byte[] bArr2) {
        this.digestLength = 64;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.fanout = 1;
        this.depth = 1;
        this.leafLength = 0;
        this.nodeOffset = 0L;
        this.nodeDepth = 0;
        this.innerHashLength = 0;
        this.isLastNode = false;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new long[16];
        this.chainValue = null;
        this.t0 = 0L;
        this.t1 = 0L;
        this.f0 = 0L;
        this.f1 = 0L;
        this.buffer = new byte[128];
        this.purpose = CryptoServicePurpose.ANY;
        this.digestLength = bArr2[0];
        this.keyLength = bArr2[1];
        this.fanout = bArr2[2];
        this.depth = bArr2[3];
        this.leafLength = Pack.littleEndianToInt(bArr2, 4);
        this.nodeOffset |= (long) Pack.littleEndianToInt(bArr2, 8);
        this.nodeDepth = bArr2[16];
        this.innerHashLength = bArr2[17];
        init();
    }

    private void init() {
        if (this.chainValue == null) {
            this.chainValue = new long[8];
            this.chainValue[0] = blake2b_IV[0] ^ ((long) ((this.digestLength | (this.keyLength << 8)) | (((this.fanout << 16) | (this.depth << 24)) | (this.leafLength << 32))));
            this.chainValue[1] = blake2b_IV[1] ^ this.nodeOffset;
            this.chainValue[2] = blake2b_IV[2] ^ ((long) (this.nodeDepth | (this.innerHashLength << 8)));
            this.chainValue[3] = blake2b_IV[3];
            this.chainValue[4] = blake2b_IV[4];
            this.chainValue[5] = blake2b_IV[5];
            if (this.salt != null) {
                long[] jArr = this.chainValue;
                jArr[4] = jArr[4] ^ Pack.littleEndianToLong(this.salt, 0);
                long[] jArr2 = this.chainValue;
                jArr2[5] = jArr2[5] ^ Pack.littleEndianToLong(this.salt, 8);
            }
            this.chainValue[6] = blake2b_IV[6];
            this.chainValue[7] = blake2b_IV[7];
            if (this.personalization != null) {
                long[] jArr3 = this.chainValue;
                jArr3[6] = jArr3[6] ^ Pack.littleEndianToLong(this.personalization, 0);
                long[] jArr4 = this.chainValue;
                jArr4[7] = jArr4[7] ^ Pack.littleEndianToLong(this.personalization, 8);
            }
        }
    }

    private void initializeInternalState() {
        System.arraycopy(this.chainValue, 0, this.internalState, 0, this.chainValue.length);
        System.arraycopy(blake2b_IV, 0, this.internalState, this.chainValue.length, 4);
        this.internalState[12] = this.t0 ^ blake2b_IV[4];
        this.internalState[13] = this.t1 ^ blake2b_IV[5];
        this.internalState[14] = this.f0 ^ blake2b_IV[6];
        this.internalState[15] = this.f1 ^ blake2b_IV[7];
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) {
        if (128 - this.bufferPos != 0) {
            this.buffer[this.bufferPos] = b;
            this.bufferPos++;
            return;
        }
        this.t0 += 128;
        if (this.t0 == 0) {
            this.t1++;
        }
        compress(this.buffer, 0);
        Arrays.fill(this.buffer, (byte) 0);
        this.buffer[0] = b;
        this.bufferPos = 1;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) {
        if (bArr == null || i2 == 0) {
            return;
        }
        int i3 = 0;
        if (this.bufferPos != 0) {
            i3 = 128 - this.bufferPos;
            if (i3 >= i2) {
                System.arraycopy(bArr, i, this.buffer, this.bufferPos, i2);
                this.bufferPos += i2;
                return;
            }
            System.arraycopy(bArr, i, this.buffer, this.bufferPos, i3);
            this.t0 += 128;
            if (this.t0 == 0) {
                this.t1++;
            }
            compress(this.buffer, 0);
            this.bufferPos = 0;
            Arrays.fill(this.buffer, (byte) 0);
        }
        int i4 = (i + i2) - 128;
        int i5 = i + i3;
        while (i5 < i4) {
            this.t0 += 128;
            if (this.t0 == 0) {
                this.t1++;
            }
            compress(bArr, i5);
            i5 += 128;
        }
        System.arraycopy(bArr, i5, this.buffer, 0, (i + i2) - i5);
        this.bufferPos += (i + i2) - i5;
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        if (i > bArr.length - this.digestLength) {
            throw new OutputLengthException("output buffer too short");
        }
        this.f0 = -1L;
        if (this.isLastNode) {
            this.f1 = -1L;
        }
        this.t0 += (long) this.bufferPos;
        if (this.bufferPos > 0 && this.t0 == 0) {
            this.t1++;
        }
        compress(this.buffer, 0);
        Arrays.fill(this.buffer, (byte) 0);
        Arrays.fill(this.internalState, 0L);
        int i2 = this.digestLength >>> 3;
        int i3 = this.digestLength & 7;
        Pack.longToLittleEndian(this.chainValue, 0, i2, bArr, i);
        if (i3 > 0) {
            byte[] bArr2 = new byte[8];
            Pack.longToLittleEndian(this.chainValue[i2], bArr2, 0);
            System.arraycopy(bArr2, 0, bArr, (i + this.digestLength) - i3, i3);
        }
        Arrays.fill(this.chainValue, 0L);
        reset();
        return this.digestLength;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void reset() {
        this.bufferPos = 0;
        this.f0 = 0L;
        this.f1 = 0L;
        this.t0 = 0L;
        this.t1 = 0L;
        this.isLastNode = false;
        this.chainValue = null;
        Arrays.fill(this.buffer, (byte) 0);
        if (this.key != null) {
            System.arraycopy(this.key, 0, this.buffer, 0, this.key.length);
            this.bufferPos = 128;
        }
        init();
    }

    private void compress(byte[] bArr, int i) {
        initializeInternalState();
        long[] jArr = new long[16];
        Pack.littleEndianToLong(bArr, i, jArr);
        for (int i2 = 0; i2 < ROUNDS; i2++) {
            G(jArr[blake2b_sigma[i2][0]], jArr[blake2b_sigma[i2][1]], 0, 4, 8, 12);
            G(jArr[blake2b_sigma[i2][2]], jArr[blake2b_sigma[i2][3]], 1, 5, 9, 13);
            G(jArr[blake2b_sigma[i2][4]], jArr[blake2b_sigma[i2][5]], 2, 6, 10, 14);
            G(jArr[blake2b_sigma[i2][6]], jArr[blake2b_sigma[i2][7]], 3, 7, 11, 15);
            G(jArr[blake2b_sigma[i2][8]], jArr[blake2b_sigma[i2][9]], 0, 5, 10, 15);
            G(jArr[blake2b_sigma[i2][10]], jArr[blake2b_sigma[i2][11]], 1, 6, 11, 12);
            G(jArr[blake2b_sigma[i2][12]], jArr[blake2b_sigma[i2][13]], 2, 7, 8, 13);
            G(jArr[blake2b_sigma[i2][14]], jArr[blake2b_sigma[i2][15]], 3, 4, 9, 14);
        }
        for (int i3 = 0; i3 < this.chainValue.length; i3++) {
            this.chainValue[i3] = (this.chainValue[i3] ^ this.internalState[i3]) ^ this.internalState[i3 + 8];
        }
    }

    private void G(long j, long j2, int i, int i2, int i3, int i4) {
        this.internalState[i] = this.internalState[i] + this.internalState[i2] + j;
        this.internalState[i4] = Longs.rotateRight(this.internalState[i4] ^ this.internalState[i], 32);
        this.internalState[i3] = this.internalState[i3] + this.internalState[i4];
        this.internalState[i2] = Longs.rotateRight(this.internalState[i2] ^ this.internalState[i3], 24);
        this.internalState[i] = this.internalState[i] + this.internalState[i2] + j2;
        this.internalState[i4] = Longs.rotateRight(this.internalState[i4] ^ this.internalState[i], 16);
        this.internalState[i3] = this.internalState[i3] + this.internalState[i4];
        this.internalState[i2] = Longs.rotateRight(this.internalState[i2] ^ this.internalState[i3], 63);
    }

    protected void setAsLastNode() {
        this.isLastNode = true;
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "BLAKE2b";
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return this.digestLength;
    }

    @Override // org.bouncycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return 128;
    }

    public void clearKey() {
        if (this.key != null) {
            Arrays.fill(this.key, (byte) 0);
            Arrays.fill(this.buffer, (byte) 0);
        }
    }

    public void clearSalt() {
        if (this.salt != null) {
            Arrays.fill(this.salt, (byte) 0);
        }
    }
}
