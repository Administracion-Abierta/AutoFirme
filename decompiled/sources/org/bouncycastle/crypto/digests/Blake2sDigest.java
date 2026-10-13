package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/Blake2sDigest.class */
public class Blake2sDigest implements ExtendedDigest {
    private static final int[] blake2s_IV = {1779033703, -1150833019, 1013904242, -1521486534, 1359893119, -1694144372, 528734635, 1541459225};
    private static final byte[][] blake2s_sigma = {new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, new byte[]{14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3}, new byte[]{11, 8, 12, 0, 5, 2, 15, 13, 10, 14, 3, 6, 7, 1, 9, 4}, new byte[]{7, 9, 3, 1, 13, 12, 11, 14, 2, 6, 5, 10, 4, 0, 15, 8}, new byte[]{9, 0, 5, 7, 2, 4, 10, 15, 14, 1, 11, 12, 6, 8, 3, 13}, new byte[]{2, 12, 6, 10, 0, 11, 8, 3, 4, 13, 7, 5, 15, 14, 1, 9}, new byte[]{12, 5, 1, 15, 14, 13, 4, 10, 0, 7, 6, 3, 9, 2, 8, 11}, new byte[]{13, 11, 7, 14, 12, 1, 3, 9, 5, 0, 15, 4, 8, 6, 2, 10}, new byte[]{6, 15, 14, 9, 11, 3, 0, 8, 12, 2, 13, 7, 1, 4, 10, 5}, new byte[]{10, 2, 8, 4, 7, 6, 1, 5, 15, 11, 9, 14, 3, 12, 13, 0}};
    private static final int ROUNDS = 10;
    private static final int BLOCK_LENGTH_BYTES = 64;
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
    private int[] internalState;
    private int[] chainValue;
    private int t0;
    private int t1;
    private int f0;
    private int f1;
    private final CryptoServicePurpose purpose;

    public Blake2sDigest() {
        this(256, CryptoServicePurpose.ANY);
    }

    public Blake2sDigest(int i) {
        this(i, CryptoServicePurpose.ANY);
    }

    public Blake2sDigest(Blake2sDigest blake2sDigest) {
        this.digestLength = 32;
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
        this.internalState = new int[16];
        this.chainValue = null;
        this.t0 = 0;
        this.t1 = 0;
        this.f0 = 0;
        this.f1 = 0;
        this.bufferPos = blake2sDigest.bufferPos;
        this.buffer = Arrays.clone(blake2sDigest.buffer);
        this.keyLength = blake2sDigest.keyLength;
        this.key = Arrays.clone(blake2sDigest.key);
        this.digestLength = blake2sDigest.digestLength;
        this.internalState = Arrays.clone(blake2sDigest.internalState);
        this.chainValue = Arrays.clone(blake2sDigest.chainValue);
        this.t0 = blake2sDigest.t0;
        this.t1 = blake2sDigest.t1;
        this.f0 = blake2sDigest.f0;
        this.salt = Arrays.clone(blake2sDigest.salt);
        this.personalization = Arrays.clone(blake2sDigest.personalization);
        this.fanout = blake2sDigest.fanout;
        this.depth = blake2sDigest.depth;
        this.leafLength = blake2sDigest.leafLength;
        this.nodeOffset = blake2sDigest.nodeOffset;
        this.nodeDepth = blake2sDigest.nodeDepth;
        this.innerHashLength = blake2sDigest.innerHashLength;
        this.purpose = blake2sDigest.purpose;
    }

    public Blake2sDigest(int i, CryptoServicePurpose cryptoServicePurpose) {
        this.digestLength = 32;
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
        this.internalState = new int[16];
        this.chainValue = null;
        this.t0 = 0;
        this.t1 = 0;
        this.f0 = 0;
        this.f1 = 0;
        if (i < 8 || i > 256 || i % 8 != 0) {
            throw new IllegalArgumentException("BLAKE2s digest bit length must be a multiple of 8 and not greater than 256");
        }
        this.digestLength = i / 8;
        this.purpose = cryptoServicePurpose;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, i, cryptoServicePurpose));
        init(null, null, null);
    }

    public Blake2sDigest(byte[] bArr) {
        this(bArr, CryptoServicePurpose.ANY);
    }

    public Blake2sDigest(byte[] bArr, CryptoServicePurpose cryptoServicePurpose) {
        this.digestLength = 32;
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
        this.internalState = new int[16];
        this.chainValue = null;
        this.t0 = 0;
        this.t1 = 0;
        this.f0 = 0;
        this.f1 = 0;
        this.purpose = cryptoServicePurpose;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, bArr.length * 8, cryptoServicePurpose));
        init(null, null, bArr);
    }

    public Blake2sDigest(byte[] bArr, int i, byte[] bArr2, byte[] bArr3) {
        this(bArr, i, bArr2, bArr3, CryptoServicePurpose.ANY);
    }

    public Blake2sDigest(byte[] bArr, int i, byte[] bArr2, byte[] bArr3, CryptoServicePurpose cryptoServicePurpose) {
        this.digestLength = 32;
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
        this.internalState = new int[16];
        this.chainValue = null;
        this.t0 = 0;
        this.t1 = 0;
        this.f0 = 0;
        this.f1 = 0;
        if (i < 1 || i > 32) {
            throw new IllegalArgumentException("Invalid digest length (required: 1 - 32)");
        }
        this.digestLength = i;
        this.purpose = cryptoServicePurpose;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, i * 8, cryptoServicePurpose));
        init(bArr2, bArr3, bArr);
    }

    Blake2sDigest(int i, byte[] bArr, byte[] bArr2, byte[] bArr3, long j, CryptoServicePurpose cryptoServicePurpose) {
        this.digestLength = 32;
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
        this.internalState = new int[16];
        this.chainValue = null;
        this.t0 = 0;
        this.t1 = 0;
        this.f0 = 0;
        this.f1 = 0;
        this.digestLength = i;
        this.nodeOffset = j;
        this.purpose = cryptoServicePurpose;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, i * 8, cryptoServicePurpose));
        init(bArr2, bArr3, bArr);
    }

    Blake2sDigest(int i, int i2, long j) {
        this(i, i2, j, CryptoServicePurpose.ANY);
    }

    Blake2sDigest(int i, int i2, long j, CryptoServicePurpose cryptoServicePurpose) {
        this.digestLength = 32;
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
        this.internalState = new int[16];
        this.chainValue = null;
        this.t0 = 0;
        this.t1 = 0;
        this.f0 = 0;
        this.f1 = 0;
        this.digestLength = i;
        this.nodeOffset = j;
        this.fanout = 0;
        this.depth = 0;
        this.leafLength = i2;
        this.innerHashLength = i2;
        this.nodeDepth = 0;
        this.purpose = cryptoServicePurpose;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, i * 8, cryptoServicePurpose));
        init(null, null, null);
    }

    Blake2sDigest(byte[] bArr, byte[] bArr2) {
        this.digestLength = 32;
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
        this.internalState = new int[16];
        this.chainValue = null;
        this.t0 = 0;
        this.t1 = 0;
        this.f0 = 0;
        this.f1 = 0;
        this.purpose = CryptoServicePurpose.ANY;
        this.digestLength = bArr2[0];
        this.keyLength = bArr2[1];
        this.fanout = bArr2[2];
        this.depth = bArr2[3];
        this.leafLength = Pack.littleEndianToInt(bArr2, 4);
        this.nodeOffset |= (long) Pack.littleEndianToInt(bArr2, 8);
        this.nodeDepth = bArr2[14];
        this.innerHashLength = bArr2[15];
        byte[] bArr3 = new byte[8];
        byte[] bArr4 = new byte[8];
        System.arraycopy(bArr2, 16, bArr3, 0, 8);
        System.arraycopy(bArr2, 24, bArr4, 0, 8);
        init(bArr3, bArr4, bArr);
    }

    private void init(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        this.buffer = new byte[64];
        if (bArr3 != null && bArr3.length > 0) {
            this.keyLength = bArr3.length;
            if (this.keyLength > 32) {
                throw new IllegalArgumentException("Keys > 32 bytes are not supported");
            }
            this.key = new byte[this.keyLength];
            System.arraycopy(bArr3, 0, this.key, 0, this.keyLength);
            System.arraycopy(bArr3, 0, this.buffer, 0, this.keyLength);
            this.bufferPos = 64;
        }
        if (this.chainValue == null) {
            this.chainValue = new int[8];
            this.chainValue[0] = blake2s_IV[0] ^ ((this.digestLength | (this.keyLength << 8)) | ((this.fanout << 16) | (this.depth << 24)));
            this.chainValue[1] = blake2s_IV[1] ^ this.leafLength;
            int i = (int) (this.nodeOffset >> 32);
            this.chainValue[2] = blake2s_IV[2] ^ ((int) this.nodeOffset);
            this.chainValue[3] = blake2s_IV[3] ^ ((i | (this.nodeDepth << 16)) | (this.innerHashLength << 24));
            this.chainValue[4] = blake2s_IV[4];
            this.chainValue[5] = blake2s_IV[5];
            if (bArr != null) {
                if (bArr.length != 8) {
                    throw new IllegalArgumentException("Salt length must be exactly 8 bytes");
                }
                this.salt = new byte[8];
                System.arraycopy(bArr, 0, this.salt, 0, bArr.length);
                int[] iArr = this.chainValue;
                iArr[4] = iArr[4] ^ Pack.littleEndianToInt(bArr, 0);
                int[] iArr2 = this.chainValue;
                iArr2[5] = iArr2[5] ^ Pack.littleEndianToInt(bArr, 4);
            }
            this.chainValue[6] = blake2s_IV[6];
            this.chainValue[7] = blake2s_IV[7];
            if (bArr2 != null) {
                if (bArr2.length != 8) {
                    throw new IllegalArgumentException("Personalization length must be exactly 8 bytes");
                }
                this.personalization = new byte[8];
                System.arraycopy(bArr2, 0, this.personalization, 0, bArr2.length);
                int[] iArr3 = this.chainValue;
                iArr3[6] = iArr3[6] ^ Pack.littleEndianToInt(bArr2, 0);
                int[] iArr4 = this.chainValue;
                iArr4[7] = iArr4[7] ^ Pack.littleEndianToInt(bArr2, 4);
            }
        }
    }

    private void initializeInternalState() {
        System.arraycopy(this.chainValue, 0, this.internalState, 0, this.chainValue.length);
        System.arraycopy(blake2s_IV, 0, this.internalState, this.chainValue.length, 4);
        this.internalState[12] = this.t0 ^ blake2s_IV[4];
        this.internalState[13] = this.t1 ^ blake2s_IV[5];
        this.internalState[14] = this.f0 ^ blake2s_IV[6];
        this.internalState[15] = this.f1 ^ blake2s_IV[7];
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) {
        if (64 - this.bufferPos != 0) {
            this.buffer[this.bufferPos] = b;
            this.bufferPos++;
            return;
        }
        this.t0 += 64;
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
            i3 = 64 - this.bufferPos;
            if (i3 >= i2) {
                System.arraycopy(bArr, i, this.buffer, this.bufferPos, i2);
                this.bufferPos += i2;
                return;
            }
            System.arraycopy(bArr, i, this.buffer, this.bufferPos, i3);
            this.t0 += 64;
            if (this.t0 == 0) {
                this.t1++;
            }
            compress(this.buffer, 0);
            this.bufferPos = 0;
            Arrays.fill(this.buffer, (byte) 0);
        }
        int i4 = (i + i2) - 64;
        int i5 = i + i3;
        while (i5 < i4) {
            this.t0 += 64;
            if (this.t0 == 0) {
                this.t1++;
            }
            compress(bArr, i5);
            i5 += 64;
        }
        System.arraycopy(bArr, i5, this.buffer, 0, (i + i2) - i5);
        this.bufferPos += (i + i2) - i5;
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        if (i > bArr.length - this.digestLength) {
            throw new OutputLengthException("output buffer too short");
        }
        this.f0 = -1;
        if (this.isLastNode) {
            this.f1 = -1;
        }
        this.t0 += this.bufferPos;
        if (this.t0 < 0 && this.bufferPos > (-this.t0)) {
            this.t1++;
        }
        compress(this.buffer, 0);
        Arrays.fill(this.buffer, (byte) 0);
        Arrays.fill(this.internalState, 0);
        int i2 = this.digestLength >>> 2;
        int i3 = this.digestLength & 3;
        Pack.intToLittleEndian(this.chainValue, 0, i2, bArr, i);
        if (i3 > 0) {
            byte[] bArr2 = new byte[4];
            Pack.intToLittleEndian(this.chainValue[i2], bArr2, 0);
            System.arraycopy(bArr2, 0, bArr, (i + this.digestLength) - i3, i3);
        }
        Arrays.fill(this.chainValue, 0);
        reset();
        return this.digestLength;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void reset() {
        this.bufferPos = 0;
        this.f0 = 0;
        this.f1 = 0;
        this.t0 = 0;
        this.t1 = 0;
        this.isLastNode = false;
        this.chainValue = null;
        Arrays.fill(this.buffer, (byte) 0);
        if (this.key != null) {
            System.arraycopy(this.key, 0, this.buffer, 0, this.key.length);
            this.bufferPos = 64;
        }
        init(this.salt, this.personalization, this.key);
    }

    private void compress(byte[] bArr, int i) {
        initializeInternalState();
        int[] iArr = new int[16];
        Pack.littleEndianToInt(bArr, i, iArr);
        for (int i2 = 0; i2 < 10; i2++) {
            G(iArr[blake2s_sigma[i2][0]], iArr[blake2s_sigma[i2][1]], 0, 4, 8, 12);
            G(iArr[blake2s_sigma[i2][2]], iArr[blake2s_sigma[i2][3]], 1, 5, 9, 13);
            G(iArr[blake2s_sigma[i2][4]], iArr[blake2s_sigma[i2][5]], 2, 6, 10, 14);
            G(iArr[blake2s_sigma[i2][6]], iArr[blake2s_sigma[i2][7]], 3, 7, 11, 15);
            G(iArr[blake2s_sigma[i2][8]], iArr[blake2s_sigma[i2][9]], 0, 5, 10, 15);
            G(iArr[blake2s_sigma[i2][10]], iArr[blake2s_sigma[i2][11]], 1, 6, 11, 12);
            G(iArr[blake2s_sigma[i2][12]], iArr[blake2s_sigma[i2][13]], 2, 7, 8, 13);
            G(iArr[blake2s_sigma[i2][14]], iArr[blake2s_sigma[i2][15]], 3, 4, 9, 14);
        }
        for (int i3 = 0; i3 < this.chainValue.length; i3++) {
            this.chainValue[i3] = (this.chainValue[i3] ^ this.internalState[i3]) ^ this.internalState[i3 + 8];
        }
    }

    private void G(int i, int i2, int i3, int i4, int i5, int i6) {
        this.internalState[i3] = this.internalState[i3] + this.internalState[i4] + i;
        this.internalState[i6] = Integers.rotateRight(this.internalState[i6] ^ this.internalState[i3], 16);
        this.internalState[i5] = this.internalState[i5] + this.internalState[i6];
        this.internalState[i4] = Integers.rotateRight(this.internalState[i4] ^ this.internalState[i5], 12);
        this.internalState[i3] = this.internalState[i3] + this.internalState[i4] + i2;
        this.internalState[i6] = Integers.rotateRight(this.internalState[i6] ^ this.internalState[i3], 8);
        this.internalState[i5] = this.internalState[i5] + this.internalState[i6];
        this.internalState[i4] = Integers.rotateRight(this.internalState[i4] ^ this.internalState[i5], 7);
    }

    protected void setAsLastNode() {
        this.isLastNode = true;
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "BLAKE2s";
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return this.digestLength;
    }

    @Override // org.bouncycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return 64;
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
