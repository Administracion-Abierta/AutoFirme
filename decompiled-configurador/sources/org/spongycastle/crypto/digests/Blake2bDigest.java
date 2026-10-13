package org.spongycastle.crypto.digests;

import android.R;
import org.spongycastle.crypto.ExtendedDigest;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/digests/Blake2bDigest.class */
public class Blake2bDigest implements ExtendedDigest {
    private static final long[] blake2b_IV = {7640891576956012808L, -4942790177534073029L, 4354685564936845355L, -6534734903238641935L, 5840696475078001361L, -7276294671716946913L, 2270897969802886507L, 6620516959819538809L};
    private static final byte[][] blake2b_sigma = {new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, new byte[]{14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3}, new byte[]{11, 8, 12, 0, 5, 2, 15, 13, 10, 14, 3, 6, 7, 1, 9, 4}, new byte[]{7, 9, 3, 1, 13, 12, 11, 14, 2, 6, 5, 10, 4, 0, 15, 8}, new byte[]{9, 0, 5, 7, 2, 4, 10, 15, 14, 1, 11, 12, 6, 8, 3, 13}, new byte[]{2, 12, 6, 10, 0, 11, 8, 3, 4, 13, 7, 5, 15, 14, 1, 9}, new byte[]{12, 5, 1, 15, 14, 13, 4, 10, 0, 7, 6, 3, 9, 2, 8, 11}, new byte[]{13, 11, 7, 14, 12, 1, 3, 9, 5, 0, 15, 4, 8, 6, 2, 10}, new byte[]{6, 15, 14, 9, 11, 3, 0, 8, 12, 2, 13, 7, 1, 4, 10, 5}, new byte[]{10, 2, 8, 4, 7, 6, 1, 5, 15, 11, 9, 14, 3, 12, 13, 0}, new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, new byte[]{14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3}};
    private static int rOUNDS = 12;
    private static final int BLOCK_LENGTH_BYTES = 128;
    private int digestLength;
    private int keyLength;
    private byte[] salt;
    private byte[] personalization;
    private byte[] key;
    private byte[] buffer;
    private int bufferPos;
    private long[] internalState;
    private long[] chainValue;
    private long t0;
    private long t1;
    private long f0;

    public Blake2bDigest() {
        this(512);
    }

    public Blake2bDigest(Blake2bDigest digest) {
        this.digestLength = 64;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new long[16];
        this.chainValue = null;
        this.t0 = 0L;
        this.t1 = 0L;
        this.f0 = 0L;
        this.bufferPos = digest.bufferPos;
        this.buffer = Arrays.clone(digest.buffer);
        this.keyLength = digest.keyLength;
        this.key = Arrays.clone(digest.key);
        this.digestLength = digest.digestLength;
        this.chainValue = Arrays.clone(digest.chainValue);
        this.personalization = Arrays.clone(digest.personalization);
        this.salt = Arrays.clone(digest.salt);
        this.t0 = digest.t0;
        this.t1 = digest.t1;
        this.f0 = digest.f0;
    }

    public Blake2bDigest(int digestSize) {
        this.digestLength = 64;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new long[16];
        this.chainValue = null;
        this.t0 = 0L;
        this.t1 = 0L;
        this.f0 = 0L;
        if (digestSize != 160 && digestSize != 256 && digestSize != 384 && digestSize != 512) {
            throw new IllegalArgumentException("Blake2b digest restricted to one of [160, 256, 384, 512]");
        }
        this.buffer = new byte[128];
        this.keyLength = 0;
        this.digestLength = digestSize / 8;
        init();
    }

    public Blake2bDigest(byte[] key) {
        this.digestLength = 64;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new long[16];
        this.chainValue = null;
        this.t0 = 0L;
        this.t1 = 0L;
        this.f0 = 0L;
        this.buffer = new byte[128];
        if (key != null) {
            this.key = new byte[key.length];
            System.arraycopy(key, 0, this.key, 0, key.length);
            if (key.length > 64) {
                throw new IllegalArgumentException("Keys > 64 are not supported");
            }
            this.keyLength = key.length;
            System.arraycopy(key, 0, this.buffer, 0, key.length);
            this.bufferPos = 128;
        }
        this.digestLength = 64;
        init();
    }

    public Blake2bDigest(byte[] key, int digestLength, byte[] salt, byte[] personalization) {
        this.digestLength = 64;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new long[16];
        this.chainValue = null;
        this.t0 = 0L;
        this.t1 = 0L;
        this.f0 = 0L;
        this.buffer = new byte[128];
        if (digestLength < 1 || digestLength > 64) {
            throw new IllegalArgumentException("Invalid digest length (required: 1 - 64)");
        }
        this.digestLength = digestLength;
        if (salt != null) {
            if (salt.length != 16) {
                throw new IllegalArgumentException("salt length must be exactly 16 bytes");
            }
            this.salt = new byte[16];
            System.arraycopy(salt, 0, this.salt, 0, salt.length);
        }
        if (personalization != null) {
            if (personalization.length != 16) {
                throw new IllegalArgumentException("personalization length must be exactly 16 bytes");
            }
            this.personalization = new byte[16];
            System.arraycopy(personalization, 0, this.personalization, 0, personalization.length);
        }
        if (key != null) {
            this.key = new byte[key.length];
            System.arraycopy(key, 0, this.key, 0, key.length);
            if (key.length > 64) {
                throw new IllegalArgumentException("Keys > 64 are not supported");
            }
            this.keyLength = key.length;
            System.arraycopy(key, 0, this.buffer, 0, key.length);
            this.bufferPos = 128;
        }
        init();
    }

    private void init() {
        if (this.chainValue == null) {
            this.chainValue = new long[8];
            this.chainValue[0] = blake2b_IV[0] ^ ((long) ((this.digestLength | (this.keyLength << 8)) | R.attr.theme));
            this.chainValue[1] = blake2b_IV[1];
            this.chainValue[2] = blake2b_IV[2];
            this.chainValue[3] = blake2b_IV[3];
            this.chainValue[4] = blake2b_IV[4];
            this.chainValue[5] = blake2b_IV[5];
            if (this.salt != null) {
                long[] jArr = this.chainValue;
                jArr[4] = jArr[4] ^ bytes2long(this.salt, 0);
                long[] jArr2 = this.chainValue;
                jArr2[5] = jArr2[5] ^ bytes2long(this.salt, 8);
            }
            this.chainValue[6] = blake2b_IV[6];
            this.chainValue[7] = blake2b_IV[7];
            if (this.personalization != null) {
                long[] jArr3 = this.chainValue;
                jArr3[6] = jArr3[6] ^ bytes2long(this.personalization, 0);
                long[] jArr4 = this.chainValue;
                jArr4[7] = jArr4[7] ^ bytes2long(this.personalization, 8);
            }
        }
    }

    private void initializeInternalState() {
        System.arraycopy(this.chainValue, 0, this.internalState, 0, this.chainValue.length);
        System.arraycopy(blake2b_IV, 0, this.internalState, this.chainValue.length, 4);
        this.internalState[12] = this.t0 ^ blake2b_IV[4];
        this.internalState[13] = this.t1 ^ blake2b_IV[5];
        this.internalState[14] = this.f0 ^ blake2b_IV[6];
        this.internalState[15] = blake2b_IV[7];
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte b) {
        int remainingLength = 128 - this.bufferPos;
        if (remainingLength == 0) {
            this.t0 += 128;
            if (this.t0 == 0) {
                this.t1++;
            }
            compress(this.buffer, 0);
            Arrays.fill(this.buffer, (byte) 0);
            this.buffer[0] = b;
            this.bufferPos = 1;
            return;
        }
        this.buffer[this.bufferPos] = b;
        this.bufferPos++;
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte[] message, int offset, int len) {
        if (message == null || len == 0) {
            return;
        }
        int remainingLength = 0;
        if (this.bufferPos != 0) {
            remainingLength = 128 - this.bufferPos;
            if (remainingLength < len) {
                System.arraycopy(message, offset, this.buffer, this.bufferPos, remainingLength);
                this.t0 += 128;
                if (this.t0 == 0) {
                    this.t1++;
                }
                compress(this.buffer, 0);
                this.bufferPos = 0;
                Arrays.fill(this.buffer, (byte) 0);
            } else {
                System.arraycopy(message, offset, this.buffer, this.bufferPos, len);
                this.bufferPos += len;
                return;
            }
        }
        int blockWiseLastPos = (offset + len) - 128;
        int messagePos = offset + remainingLength;
        while (messagePos < blockWiseLastPos) {
            this.t0 += 128;
            if (this.t0 == 0) {
                this.t1++;
            }
            compress(message, messagePos);
            messagePos += 128;
        }
        System.arraycopy(message, messagePos, this.buffer, 0, (offset + len) - messagePos);
        this.bufferPos += (offset + len) - messagePos;
    }

    @Override // org.spongycastle.crypto.Digest
    public int doFinal(byte[] out, int outOffset) {
        this.f0 = -1L;
        this.t0 += (long) this.bufferPos;
        if (this.t0 < 0 && this.bufferPos > (-this.t0)) {
            this.t1++;
        }
        compress(this.buffer, 0);
        Arrays.fill(this.buffer, (byte) 0);
        Arrays.fill(this.internalState, 0L);
        for (int i = 0; i < this.chainValue.length && i * 8 < this.digestLength; i++) {
            byte[] bytes = long2bytes(this.chainValue[i]);
            if (i * 8 < this.digestLength - 8) {
                System.arraycopy(bytes, 0, out, outOffset + (i * 8), 8);
            } else {
                System.arraycopy(bytes, 0, out, outOffset + (i * 8), this.digestLength - (i * 8));
            }
        }
        Arrays.fill(this.chainValue, 0L);
        reset();
        return this.digestLength;
    }

    @Override // org.spongycastle.crypto.Digest
    public void reset() {
        this.bufferPos = 0;
        this.f0 = 0L;
        this.t0 = 0L;
        this.t1 = 0L;
        this.chainValue = null;
        Arrays.fill(this.buffer, (byte) 0);
        if (this.key != null) {
            System.arraycopy(this.key, 0, this.buffer, 0, this.key.length);
            this.bufferPos = 128;
        }
        init();
    }

    private void compress(byte[] message, int messagePos) {
        initializeInternalState();
        long[] m = new long[16];
        for (int j = 0; j < 16; j++) {
            m[j] = bytes2long(message, messagePos + (j * 8));
        }
        for (int round = 0; round < rOUNDS; round++) {
            G(m[blake2b_sigma[round][0]], m[blake2b_sigma[round][1]], 0, 4, 8, 12);
            G(m[blake2b_sigma[round][2]], m[blake2b_sigma[round][3]], 1, 5, 9, 13);
            G(m[blake2b_sigma[round][4]], m[blake2b_sigma[round][5]], 2, 6, 10, 14);
            G(m[blake2b_sigma[round][6]], m[blake2b_sigma[round][7]], 3, 7, 11, 15);
            G(m[blake2b_sigma[round][8]], m[blake2b_sigma[round][9]], 0, 5, 10, 15);
            G(m[blake2b_sigma[round][10]], m[blake2b_sigma[round][11]], 1, 6, 11, 12);
            G(m[blake2b_sigma[round][12]], m[blake2b_sigma[round][13]], 2, 7, 8, 13);
            G(m[blake2b_sigma[round][14]], m[blake2b_sigma[round][15]], 3, 4, 9, 14);
        }
        for (int offset = 0; offset < this.chainValue.length; offset++) {
            this.chainValue[offset] = (this.chainValue[offset] ^ this.internalState[offset]) ^ this.internalState[offset + 8];
        }
    }

    private void G(long m1, long m2, int posA, int posB, int posC, int posD) {
        this.internalState[posA] = this.internalState[posA] + this.internalState[posB] + m1;
        this.internalState[posD] = rotr64(this.internalState[posD] ^ this.internalState[posA], 32);
        this.internalState[posC] = this.internalState[posC] + this.internalState[posD];
        this.internalState[posB] = rotr64(this.internalState[posB] ^ this.internalState[posC], 24);
        this.internalState[posA] = this.internalState[posA] + this.internalState[posB] + m2;
        this.internalState[posD] = rotr64(this.internalState[posD] ^ this.internalState[posA], 16);
        this.internalState[posC] = this.internalState[posC] + this.internalState[posD];
        this.internalState[posB] = rotr64(this.internalState[posB] ^ this.internalState[posC], 63);
    }

    private long rotr64(long x, int rot) {
        return (x >>> rot) | (x << (64 - rot));
    }

    private final byte[] long2bytes(long longValue) {
        return new byte[]{(byte) longValue, (byte) (longValue >> 8), (byte) (longValue >> 16), (byte) (longValue >> 24), (byte) (longValue >> 32), (byte) (longValue >> 40), (byte) (longValue >> 48), (byte) (longValue >> 56)};
    }

    private final long bytes2long(byte[] byteArray, int offset) {
        return (((long) byteArray[offset]) & 255) | ((((long) byteArray[offset + 1]) & 255) << 8) | ((((long) byteArray[offset + 2]) & 255) << 16) | ((((long) byteArray[offset + 3]) & 255) << 24) | ((((long) byteArray[offset + 4]) & 255) << 32) | ((((long) byteArray[offset + 5]) & 255) << 40) | ((((long) byteArray[offset + 6]) & 255) << 48) | ((((long) byteArray[offset + 7]) & 255) << 56);
    }

    @Override // org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        return "Blake2b";
    }

    @Override // org.spongycastle.crypto.Digest
    public int getDigestSize() {
        return this.digestLength;
    }

    @Override // org.spongycastle.crypto.ExtendedDigest
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
