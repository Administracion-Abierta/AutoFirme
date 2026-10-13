package org.spongycastle.crypto.digests;

import org.spongycastle.asn1.eac.CertificateBody;
import org.spongycastle.asn1.x509.DisplayText;
import org.spongycastle.crypto.ExtendedDigest;
import org.spongycastle.crypto.tls.AlertDescription;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.crypto.tls.ExtensionType;
import org.spongycastle.math.Primes;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Memoable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/digests/WhirlpoolDigest.class */
public final class WhirlpoolDigest implements ExtendedDigest, Memoable {
    private static final int BYTE_LENGTH = 64;
    private static final int DIGEST_LENGTH_BYTES = 64;
    private static final int ROUNDS = 10;
    private static final int REDUCTION_POLYNOMIAL = 285;
    private final long[] _rc;
    private static final int BITCOUNT_ARRAY_SIZE = 32;
    private byte[] _buffer;
    private int _bufferPos;
    private short[] _bitCount;
    private long[] _hash;
    private long[] _K;
    private long[] _L;
    private long[] _block;
    private long[] _state;
    private static final int[] SBOX = {24, 35, 198, 232, CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA, CipherSuite.TLS_RSA_PSK_WITH_NULL_SHA256, 1, 79, 54, CipherSuite.TLS_DH_anon_WITH_AES_128_GCM_SHA256, 210, 245, 121, AlertDescription.certificate_unobtainable, CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA, 82, 96, 188, CipherSuite.TLS_DH_anon_WITH_SEED_CBC_SHA, CipherSuite.TLS_DHE_PSK_WITH_RC4_128_SHA, CipherSuite.TLS_DHE_DSS_WITH_AES_256_GCM_SHA384, 12, 123, 53, 29, 224, 215, CipherSuite.TLS_DH_RSA_WITH_CAMELLIA_256_CBC_SHA256, 46, 75, 254, 87, 21, 119, 55, 229, CipherSuite.TLS_DHE_RSA_WITH_AES_256_GCM_SHA384, 240, 74, 218, 88, 201, 41, 10, CipherSuite.TLS_PSK_WITH_NULL_SHA384, CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA256, 133, CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA256, 93, 16, 244, 203, 62, 5, 103, 228, 39, 65, CipherSuite.TLS_PSK_WITH_3DES_EDE_CBC_SHA, CipherSuite.TLS_DH_anon_WITH_AES_256_GCM_SHA384, 125, CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA, 216, 251, 238, 124, 102, 221, 23, 71, CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256, 202, 45, CipherSuite.TLS_DH_anon_WITH_CAMELLIA_128_CBC_SHA256, 7, CipherSuite.TLS_RSA_PSK_WITH_AES_256_GCM_SHA384, 90, 131, 51, 99, 2, CipherSuite.TLS_DHE_PSK_WITH_AES_128_GCM_SHA256, AlertDescription.bad_certificate_status_response, DisplayText.DISPLAY_TEXT_MAXIMUM_SIZE, 25, 73, 217, 242, 227, 91, CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA, CipherSuite.TLS_DHE_RSA_WITH_SEED_CBC_SHA, 38, 50, CipherSuite.TLS_PSK_WITH_NULL_SHA256, 233, 15, 213, 128, CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA256, 205, 52, 72, 255, 122, CipherSuite.TLS_DHE_PSK_WITH_AES_128_CBC_SHA, 95, 32, 104, 26, CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA256, CipherSuite.TLS_DHE_PSK_WITH_NULL_SHA256, 84, CipherSuite.TLS_RSA_PSK_WITH_3DES_EDE_CBC_SHA, 34, 100, 241, AlertDescription.unknown_psk_identity, 18, 64, 8, CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA256, 236, 219, CipherSuite.TLS_DH_RSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA, 61, CipherSuite.TLS_DH_DSS_WITH_SEED_CBC_SHA, 0, 207, 43, 118, 130, 214, 27, CipherSuite.TLS_DHE_PSK_WITH_NULL_SHA384, CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA384, CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA256, 80, 69, 243, 48, 239, 63, 85, CipherSuite.TLS_DHE_DSS_WITH_AES_128_GCM_SHA256, 234, ExtensionType.negotiated_ff_dhe_groups, CipherSuite.TLS_RSA_WITH_CAMELLIA_128_CBC_SHA256, 47, 192, 222, 28, 253, 77, CipherSuite.TLS_RSA_PSK_WITH_RC4_128_SHA, 117, 6, CipherSuite.TLS_PSK_WITH_RC4_128_SHA, CipherSuite.TLS_DHE_PSK_WITH_AES_128_CBC_SHA256, 230, 14, 31, 98, 212, CipherSuite.TLS_PSK_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_RSA_WITH_SEED_CBC_SHA, 249, CipherSuite.TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA256, 37, 89, CipherSuite.TLS_RSA_WITH_CAMELLIA_256_CBC_SHA, AlertDescription.bad_certificate_hash_value, 57, 76, 94, 120, 56, CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA, 209, CipherSuite.TLS_DH_DSS_WITH_AES_256_GCM_SHA384, 226, 97, CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA384, 33, CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256, 30, 67, 199, 252, 4, 81, CipherSuite.TLS_DHE_DSS_WITH_SEED_CBC_SHA, CipherSuite.TLS_DH_anon_WITH_AES_256_CBC_SHA256, 13, 250, 223, 126, 36, 59, CipherSuite.TLS_DHE_PSK_WITH_AES_256_GCM_SHA384, 206, 17, CipherSuite.TLS_DHE_PSK_WITH_3DES_EDE_CBC_SHA, 78, CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA384, 235, 60, 129, CipherSuite.TLS_RSA_PSK_WITH_AES_128_CBC_SHA, 247, CipherSuite.TLS_RSA_PSK_WITH_NULL_SHA384, 19, 44, Primes.SMALL_FACTOR_LIMIT, 231, AlertDescription.unsupported_extension, CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA256, 3, 86, 68, CertificateBody.profileType, CipherSuite.TLS_PSK_WITH_AES_256_GCM_SHA384, 42, CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_128_CBC_SHA256, CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_256_CBC_SHA256, 83, 220, 11, CipherSuite.TLS_RSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_DH_anon_WITH_AES_128_CBC_SHA256, 49, 116, 246, 70, CipherSuite.TLS_RSA_PSK_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA, 20, 225, 22, 58, CipherSuite.TLS_DH_RSA_WITH_AES_256_CBC_SHA256, 9, AlertDescription.unrecognized_name, CipherSuite.TLS_RSA_PSK_WITH_AES_128_CBC_SHA256, 208, 237, 204, 66, CipherSuite.TLS_DH_RSA_WITH_SEED_CBC_SHA, CipherSuite.TLS_DH_DSS_WITH_AES_128_GCM_SHA256, 40, 92, 248, CipherSuite.TLS_DH_RSA_WITH_CAMELLIA_256_CBC_SHA};
    private static final long[] C0 = new long[256];
    private static final long[] C1 = new long[256];
    private static final long[] C2 = new long[256];
    private static final long[] C3 = new long[256];
    private static final long[] C4 = new long[256];
    private static final long[] C5 = new long[256];
    private static final long[] C6 = new long[256];
    private static final long[] C7 = new long[256];
    private static final short[] EIGHT = new short[32];

    static {
        EIGHT[31] = 8;
    }

    public WhirlpoolDigest() {
        this._rc = new long[11];
        this._buffer = new byte[64];
        this._bufferPos = 0;
        this._bitCount = new short[32];
        this._hash = new long[8];
        this._K = new long[8];
        this._L = new long[8];
        this._block = new long[8];
        this._state = new long[8];
        for (int i = 0; i < 256; i++) {
            int v1 = SBOX[i];
            int v2 = maskWithReductionPolynomial(v1 << 1);
            int v4 = maskWithReductionPolynomial(v2 << 1);
            int v5 = v4 ^ v1;
            int v8 = maskWithReductionPolynomial(v4 << 1);
            int v9 = v8 ^ v1;
            C0[i] = packIntoLong(v1, v1, v4, v1, v8, v5, v2, v9);
            C1[i] = packIntoLong(v9, v1, v1, v4, v1, v8, v5, v2);
            C2[i] = packIntoLong(v2, v9, v1, v1, v4, v1, v8, v5);
            C3[i] = packIntoLong(v5, v2, v9, v1, v1, v4, v1, v8);
            C4[i] = packIntoLong(v8, v5, v2, v9, v1, v1, v4, v1);
            C5[i] = packIntoLong(v1, v8, v5, v2, v9, v1, v1, v4);
            C6[i] = packIntoLong(v4, v1, v8, v5, v2, v9, v1, v1);
            C7[i] = packIntoLong(v1, v4, v1, v8, v5, v2, v9, v1);
        }
        this._rc[0] = 0;
        for (int r = 1; r <= 10; r++) {
            int i2 = 8 * (r - 1);
            this._rc[r] = (((((((C0[i2] & (-72057594037927936L)) ^ (C1[i2 + 1] & 71776119061217280L)) ^ (C2[i2 + 2] & 280375465082880L)) ^ (C3[i2 + 3] & 1095216660480L)) ^ (C4[i2 + 4] & 4278190080L)) ^ (C5[i2 + 5] & 16711680)) ^ (C6[i2 + 6] & 65280)) ^ (C7[i2 + 7] & 255);
        }
    }

    private long packIntoLong(int b7, int b6, int b5, int b4, int b3, int b2, int b1, int b0) {
        return (((((((((long) b7) << 56) ^ (((long) b6) << 48)) ^ (((long) b5) << 40)) ^ (((long) b4) << 32)) ^ (((long) b3) << 24)) ^ (((long) b2) << 16)) ^ (((long) b1) << 8)) ^ ((long) b0);
    }

    private int maskWithReductionPolynomial(int input) {
        int rv = input;
        if (rv >= 256) {
            rv ^= REDUCTION_POLYNOMIAL;
        }
        return rv;
    }

    public WhirlpoolDigest(WhirlpoolDigest originalDigest) {
        this._rc = new long[11];
        this._buffer = new byte[64];
        this._bufferPos = 0;
        this._bitCount = new short[32];
        this._hash = new long[8];
        this._K = new long[8];
        this._L = new long[8];
        this._block = new long[8];
        this._state = new long[8];
        reset(originalDigest);
    }

    @Override // org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        return "Whirlpool";
    }

    @Override // org.spongycastle.crypto.Digest
    public int getDigestSize() {
        return 64;
    }

    @Override // org.spongycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        finish();
        for (int i = 0; i < 8; i++) {
            convertLongToByteArray(this._hash[i], out, outOff + (i * 8));
        }
        reset();
        return getDigestSize();
    }

    @Override // org.spongycastle.crypto.Digest
    public void reset() {
        this._bufferPos = 0;
        Arrays.fill(this._bitCount, (short) 0);
        Arrays.fill(this._buffer, (byte) 0);
        Arrays.fill(this._hash, 0L);
        Arrays.fill(this._K, 0L);
        Arrays.fill(this._L, 0L);
        Arrays.fill(this._block, 0L);
        Arrays.fill(this._state, 0L);
    }

    private void processFilledBuffer(byte[] in, int inOff) {
        for (int i = 0; i < this._state.length; i++) {
            this._block[i] = bytesToLongFromBuffer(this._buffer, i * 8);
        }
        processBlock();
        this._bufferPos = 0;
        Arrays.fill(this._buffer, (byte) 0);
    }

    private long bytesToLongFromBuffer(byte[] buffer, int startPos) {
        long rv = ((((long) buffer[startPos + 0]) & 255) << 56) | ((((long) buffer[startPos + 1]) & 255) << 48) | ((((long) buffer[startPos + 2]) & 255) << 40) | ((((long) buffer[startPos + 3]) & 255) << 32) | ((((long) buffer[startPos + 4]) & 255) << 24) | ((((long) buffer[startPos + 5]) & 255) << 16) | ((((long) buffer[startPos + 6]) & 255) << 8) | (((long) buffer[startPos + 7]) & 255);
        return rv;
    }

    private void convertLongToByteArray(long inputLong, byte[] outputArray, int offSet) {
        for (int i = 0; i < 8; i++) {
            outputArray[offSet + i] = (byte) ((inputLong >> (56 - (i * 8))) & 255);
        }
    }

    protected void processBlock() {
        for (int i = 0; i < 8; i++) {
            long j = this._block[i];
            long j2 = this._hash[i];
            this._K[i] = j2;
            this._state[i] = j ^ j2;
        }
        for (int round = 1; round <= 10; round++) {
            for (int i2 = 0; i2 < 8; i2++) {
                this._L[i2] = 0;
                long[] jArr = this._L;
                int i3 = i2;
                jArr[i3] = jArr[i3] ^ C0[((int) (this._K[(i2 - 0) & 7] >>> 56)) & 255];
                long[] jArr2 = this._L;
                int i4 = i2;
                jArr2[i4] = jArr2[i4] ^ C1[((int) (this._K[(i2 - 1) & 7] >>> 48)) & 255];
                long[] jArr3 = this._L;
                int i5 = i2;
                jArr3[i5] = jArr3[i5] ^ C2[((int) (this._K[(i2 - 2) & 7] >>> 40)) & 255];
                long[] jArr4 = this._L;
                int i6 = i2;
                jArr4[i6] = jArr4[i6] ^ C3[((int) (this._K[(i2 - 3) & 7] >>> 32)) & 255];
                long[] jArr5 = this._L;
                int i7 = i2;
                jArr5[i7] = jArr5[i7] ^ C4[((int) (this._K[(i2 - 4) & 7] >>> 24)) & 255];
                long[] jArr6 = this._L;
                int i8 = i2;
                jArr6[i8] = jArr6[i8] ^ C5[((int) (this._K[(i2 - 5) & 7] >>> 16)) & 255];
                long[] jArr7 = this._L;
                int i9 = i2;
                jArr7[i9] = jArr7[i9] ^ C6[((int) (this._K[(i2 - 6) & 7] >>> 8)) & 255];
                long[] jArr8 = this._L;
                int i10 = i2;
                jArr8[i10] = jArr8[i10] ^ C7[((int) this._K[(i2 - 7) & 7]) & 255];
            }
            System.arraycopy(this._L, 0, this._K, 0, this._K.length);
            long[] jArr9 = this._K;
            jArr9[0] = jArr9[0] ^ this._rc[round];
            for (int i11 = 0; i11 < 8; i11++) {
                this._L[i11] = this._K[i11];
                long[] jArr10 = this._L;
                int i12 = i11;
                jArr10[i12] = jArr10[i12] ^ C0[((int) (this._state[(i11 - 0) & 7] >>> 56)) & 255];
                long[] jArr11 = this._L;
                int i13 = i11;
                jArr11[i13] = jArr11[i13] ^ C1[((int) (this._state[(i11 - 1) & 7] >>> 48)) & 255];
                long[] jArr12 = this._L;
                int i14 = i11;
                jArr12[i14] = jArr12[i14] ^ C2[((int) (this._state[(i11 - 2) & 7] >>> 40)) & 255];
                long[] jArr13 = this._L;
                int i15 = i11;
                jArr13[i15] = jArr13[i15] ^ C3[((int) (this._state[(i11 - 3) & 7] >>> 32)) & 255];
                long[] jArr14 = this._L;
                int i16 = i11;
                jArr14[i16] = jArr14[i16] ^ C4[((int) (this._state[(i11 - 4) & 7] >>> 24)) & 255];
                long[] jArr15 = this._L;
                int i17 = i11;
                jArr15[i17] = jArr15[i17] ^ C5[((int) (this._state[(i11 - 5) & 7] >>> 16)) & 255];
                long[] jArr16 = this._L;
                int i18 = i11;
                jArr16[i18] = jArr16[i18] ^ C6[((int) (this._state[(i11 - 6) & 7] >>> 8)) & 255];
                long[] jArr17 = this._L;
                int i19 = i11;
                jArr17[i19] = jArr17[i19] ^ C7[((int) this._state[(i11 - 7) & 7]) & 255];
            }
            System.arraycopy(this._L, 0, this._state, 0, this._state.length);
        }
        for (int i20 = 0; i20 < 8; i20++) {
            long[] jArr18 = this._hash;
            int i21 = i20;
            jArr18[i21] = jArr18[i21] ^ (this._state[i20] ^ this._block[i20]);
        }
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte in) {
        this._buffer[this._bufferPos] = in;
        this._bufferPos++;
        if (this._bufferPos == this._buffer.length) {
            processFilledBuffer(this._buffer, 0);
        }
        increment();
    }

    private void increment() {
        int carry = 0;
        for (int i = this._bitCount.length - 1; i >= 0; i--) {
            int sum = (this._bitCount[i] & 255) + EIGHT[i] + carry;
            carry = sum >>> 8;
            this._bitCount[i] = (short) (sum & 255);
        }
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte[] in, int inOff, int len) {
        while (len > 0) {
            update(in[inOff]);
            inOff++;
            len--;
        }
    }

    private void finish() {
        byte[] bitLength = copyBitLength();
        byte[] bArr = this._buffer;
        int i = this._bufferPos;
        this._bufferPos = i + 1;
        bArr[i] = (byte) (bArr[i] | 128);
        if (this._bufferPos == this._buffer.length) {
            processFilledBuffer(this._buffer, 0);
        }
        if (this._bufferPos > 32) {
            while (this._bufferPos != 0) {
                update((byte) 0);
            }
        }
        while (this._bufferPos <= 32) {
            update((byte) 0);
        }
        System.arraycopy(bitLength, 0, this._buffer, 32, bitLength.length);
        processFilledBuffer(this._buffer, 0);
    }

    private byte[] copyBitLength() {
        byte[] rv = new byte[32];
        for (int i = 0; i < rv.length; i++) {
            rv[i] = (byte) (this._bitCount[i] & 255);
        }
        return rv;
    }

    @Override // org.spongycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return 64;
    }

    @Override // org.spongycastle.util.Memoable
    public Memoable copy() {
        return new WhirlpoolDigest(this);
    }

    @Override // org.spongycastle.util.Memoable
    public void reset(Memoable other) {
        WhirlpoolDigest originalDigest = (WhirlpoolDigest) other;
        System.arraycopy(originalDigest._rc, 0, this._rc, 0, this._rc.length);
        System.arraycopy(originalDigest._buffer, 0, this._buffer, 0, this._buffer.length);
        this._bufferPos = originalDigest._bufferPos;
        System.arraycopy(originalDigest._bitCount, 0, this._bitCount, 0, this._bitCount.length);
        System.arraycopy(originalDigest._hash, 0, this._hash, 0, this._hash.length);
        System.arraycopy(originalDigest._K, 0, this._K, 0, this._K.length);
        System.arraycopy(originalDigest._L, 0, this._L, 0, this._L.length);
        System.arraycopy(originalDigest._block, 0, this._block, 0, this._block.length);
        System.arraycopy(originalDigest._state, 0, this._state, 0, this._state.length);
    }
}
