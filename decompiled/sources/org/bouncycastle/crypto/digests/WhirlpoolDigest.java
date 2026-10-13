package org.bouncycastle.crypto.digests;

import com.aowagie.text.pdf.Barcode128;
import com.github.markusbernhardt.proxy.search.wpad.dhcp.DHCPOptions;
import com.sun.jna.platform.win32.WinError;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/WhirlpoolDigest.class */
public final class WhirlpoolDigest implements ExtendedDigest, Memoable {
    private static final int BITCOUNT_ARRAY_SIZE = 32;
    private static final int BYTE_LENGTH = 64;
    private static final int DIGEST_LENGTH_BYTES = 64;
    private static final int REDUCTION_POLYNOMIAL = 285;
    private static final int ROUNDS = 10;
    private static final int[] SBOX = {24, 35, 198, WinError.ERROR_NO_DATA, 135, 184, 1, 79, 54, 166, WinError.ERROR_THREAD_1_INACTIVE, 245, 121, 111, 145, 82, 96, 188, 155, 142, 163, 12, 123, 53, 29, 224, WinError.ERROR_NESTING_NOT_ALLOWED, 194, 46, 75, 254, 87, 21, 119, 55, WinError.ERROR_PIPE_LOCAL, 159, 240, 74, WinError.ERROR_EXE_CANNOT_MODIFY_STRONG_SIGNED_BINARY, 88, 201, 41, 10, 177, 160, 107, 133, 189, 93, 16, 244, 203, 62, 5, 103, 228, 39, 65, 139, 167, 125, 149, WinError.ERROR_EXE_MACHINE_TYPE_MISMATCH, 251, 238, 124, 102, WinError.ERROR_CHECKOUT_REQUIRED, 23, 71, 158, 202, 45, 191, 7, 173, 90, 131, 51, 99, 2, 170, 113, 200, 25, 73, WinError.ERROR_EXE_CANNOT_MODIFY_SIGNED_BINARY, 242, 227, 91, 136, 154, 38, 50, 176, WinError.ERROR_PIPE_NOT_CONNECTED, 15, 213, 128, 190, 205, 52, 72, 255, 122, 144, 95, 32, 104, 26, 174, 180, 84, 147, 34, 100, 241, 115, 18, 64, 8, 195, 236, 219, 161, 141, 61, 151, 0, WinError.ERROR_RING2_STACK_IN_USE, 43, 118, 130, WinError.ERROR_TOO_MANY_MODULES, 27, 181, 175, 106, 80, 69, 243, 48, 239, 63, 85, 162, WinError.ERROR_MORE_DATA, 101, 186, 47, 192, WinError.ERROR_BAD_FILE_TYPE, 28, 253, 77, 146, 117, 6, 138, 178, WinError.ERROR_BAD_PIPE, 14, 31, 98, WinError.ERROR_LOCKED, 168, 150, 249, 197, 37, 89, 132, 114, 57, 76, 94, 120, 56, 140, WinError.ERROR_INVALID_SIGNAL_NUMBER, 165, WinError.ERROR_VIRUS_DELETED, 97, 179, 33, 156, 30, 67, 199, DHCPOptions.OPTION_PROXY_AUTODISCOVERY, 4, 81, 153, 109, 13, 250, WinError.ERROR_FILE_TOO_LARGE, 126, 36, 59, 171, WinError.ERROR_FILENAME_EXCED_RANGE, 17, 143, 78, 183, 235, 60, 129, 148, 247, 185, 19, 44, 211, WinError.ERROR_PIPE_BUSY, 110, 196, 3, 86, 68, 127, 169, 42, 187, 193, 83, WinError.ERROR_FILE_CHECKED_OUT, 11, 157, 108, 49, 116, 246, 70, 172, 137, 20, WinError.ERROR_VIRUS_INFECTED, 22, 58, 105, 9, 112, 182, WinError.ERROR_META_EXPANSION_TOO_LONG, 237, Barcode128.STARTB, 66, 152, 164, 40, 92, 248, 134};
    private static final long[] C0 = new long[256];
    private static final long[] C1 = new long[256];
    private static final long[] C2 = new long[256];
    private static final long[] C3 = new long[256];
    private static final long[] C4 = new long[256];
    private static final long[] C5 = new long[256];
    private static final long[] C6 = new long[256];
    private static final long[] C7 = new long[256];
    private static final short[] EIGHT = new short[32];
    private final long[] _rc;
    private final CryptoServicePurpose purpose;
    private byte[] _buffer;
    private int _bufferPos;
    private short[] _bitCount;
    private long[] _hash;
    private long[] _K;
    private long[] _L;
    private long[] _block;
    private long[] _state;

    private static int mulX(int i) {
        return (i << 1) ^ ((-(i >>> 7)) & 285);
    }

    private static long packIntoLong(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        return (((((((((long) i) << 56) ^ (((long) i2) << 48)) ^ (((long) i3) << 40)) ^ (((long) i4) << 32)) ^ (((long) i5) << 24)) ^ (((long) i6) << 16)) ^ (((long) i7) << 8)) ^ ((long) i8);
    }

    public WhirlpoolDigest() {
        this(CryptoServicePurpose.ANY);
    }

    public WhirlpoolDigest(CryptoServicePurpose cryptoServicePurpose) {
        this._rc = new long[11];
        this._buffer = new byte[64];
        this._bufferPos = 0;
        this._bitCount = new short[32];
        this._hash = new long[8];
        this._K = new long[8];
        this._L = new long[8];
        this._block = new long[8];
        this._state = new long[8];
        this._rc[0] = 0;
        for (int i = 1; i <= 10; i++) {
            int i2 = 8 * (i - 1);
            this._rc[i] = (((((((C0[i2] & (-72057594037927936L)) ^ (C1[i2 + 1] & 71776119061217280L)) ^ (C2[i2 + 2] & 280375465082880L)) ^ (C3[i2 + 3] & 1095216660480L)) ^ (C4[i2 + 4] & 4278190080L)) ^ (C5[i2 + 5] & 16711680)) ^ (C6[i2 + 6] & 65280)) ^ (C7[i2 + 7] & 255);
        }
        this.purpose = cryptoServicePurpose;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, getDigestSize(), cryptoServicePurpose));
    }

    public WhirlpoolDigest(WhirlpoolDigest whirlpoolDigest) {
        this._rc = new long[11];
        this._buffer = new byte[64];
        this._bufferPos = 0;
        this._bitCount = new short[32];
        this._hash = new long[8];
        this._K = new long[8];
        this._L = new long[8];
        this._block = new long[8];
        this._state = new long[8];
        this.purpose = whirlpoolDigest.purpose;
        reset(whirlpoolDigest);
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, getDigestSize(), this.purpose));
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "Whirlpool";
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return 64;
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        finish();
        Pack.longToBigEndian(this._hash, bArr, i);
        reset();
        return getDigestSize();
    }

    @Override // org.bouncycastle.crypto.Digest
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

    private void processFilledBuffer(byte[] bArr, int i) {
        Pack.bigEndianToLong(this._buffer, 0, this._block);
        processBlock();
        this._bufferPos = 0;
        Arrays.fill(this._buffer, (byte) 0);
    }

    protected void processBlock() {
        for (int i = 0; i < 8; i++) {
            long j = this._block[i];
            long j2 = this._hash[i];
            this._K[i] = j2;
            this._state[i] = j ^ j2;
        }
        for (int i2 = 1; i2 <= 10; i2++) {
            for (int i3 = 0; i3 < 8; i3++) {
                this._L[i3] = 0;
                long[] jArr = this._L;
                int i4 = i3;
                jArr[i4] = jArr[i4] ^ C0[((int) (this._K[(i3 - 0) & 7] >>> 56)) & 255];
                long[] jArr2 = this._L;
                int i5 = i3;
                jArr2[i5] = jArr2[i5] ^ C1[((int) (this._K[(i3 - 1) & 7] >>> 48)) & 255];
                long[] jArr3 = this._L;
                int i6 = i3;
                jArr3[i6] = jArr3[i6] ^ C2[((int) (this._K[(i3 - 2) & 7] >>> 40)) & 255];
                long[] jArr4 = this._L;
                int i7 = i3;
                jArr4[i7] = jArr4[i7] ^ C3[((int) (this._K[(i3 - 3) & 7] >>> 32)) & 255];
                long[] jArr5 = this._L;
                int i8 = i3;
                jArr5[i8] = jArr5[i8] ^ C4[((int) (this._K[(i3 - 4) & 7] >>> 24)) & 255];
                long[] jArr6 = this._L;
                int i9 = i3;
                jArr6[i9] = jArr6[i9] ^ C5[((int) (this._K[(i3 - 5) & 7] >>> 16)) & 255];
                long[] jArr7 = this._L;
                int i10 = i3;
                jArr7[i10] = jArr7[i10] ^ C6[((int) (this._K[(i3 - 6) & 7] >>> 8)) & 255];
                long[] jArr8 = this._L;
                int i11 = i3;
                jArr8[i11] = jArr8[i11] ^ C7[((int) this._K[(i3 - 7) & 7]) & 255];
            }
            System.arraycopy(this._L, 0, this._K, 0, this._K.length);
            long[] jArr9 = this._K;
            jArr9[0] = jArr9[0] ^ this._rc[i2];
            for (int i12 = 0; i12 < 8; i12++) {
                this._L[i12] = this._K[i12];
                long[] jArr10 = this._L;
                int i13 = i12;
                jArr10[i13] = jArr10[i13] ^ C0[((int) (this._state[(i12 - 0) & 7] >>> 56)) & 255];
                long[] jArr11 = this._L;
                int i14 = i12;
                jArr11[i14] = jArr11[i14] ^ C1[((int) (this._state[(i12 - 1) & 7] >>> 48)) & 255];
                long[] jArr12 = this._L;
                int i15 = i12;
                jArr12[i15] = jArr12[i15] ^ C2[((int) (this._state[(i12 - 2) & 7] >>> 40)) & 255];
                long[] jArr13 = this._L;
                int i16 = i12;
                jArr13[i16] = jArr13[i16] ^ C3[((int) (this._state[(i12 - 3) & 7] >>> 32)) & 255];
                long[] jArr14 = this._L;
                int i17 = i12;
                jArr14[i17] = jArr14[i17] ^ C4[((int) (this._state[(i12 - 4) & 7] >>> 24)) & 255];
                long[] jArr15 = this._L;
                int i18 = i12;
                jArr15[i18] = jArr15[i18] ^ C5[((int) (this._state[(i12 - 5) & 7] >>> 16)) & 255];
                long[] jArr16 = this._L;
                int i19 = i12;
                jArr16[i19] = jArr16[i19] ^ C6[((int) (this._state[(i12 - 6) & 7] >>> 8)) & 255];
                long[] jArr17 = this._L;
                int i20 = i12;
                jArr17[i20] = jArr17[i20] ^ C7[((int) this._state[(i12 - 7) & 7]) & 255];
            }
            System.arraycopy(this._L, 0, this._state, 0, this._state.length);
        }
        for (int i21 = 0; i21 < 8; i21++) {
            long[] jArr18 = this._hash;
            int i22 = i21;
            jArr18[i22] = jArr18[i22] ^ (this._state[i21] ^ this._block[i21]);
        }
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) {
        this._buffer[this._bufferPos] = b;
        int i = this._bufferPos + 1;
        this._bufferPos = i;
        if (i == this._buffer.length) {
            processFilledBuffer(this._buffer, 0);
        }
        increment();
    }

    private void increment() {
        int i = 0;
        for (int length = this._bitCount.length - 1; length >= 0; length--) {
            int i2 = (this._bitCount[length] & 255) + EIGHT[length] + i;
            i = i2 >>> 8;
            this._bitCount[length] = (short) (i2 & 255);
        }
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) {
        while (i2 > 0) {
            update(bArr[i]);
            i++;
            i2--;
        }
    }

    private void finish() {
        byte[] bArrCopyBitLength = copyBitLength();
        byte[] bArr = this._buffer;
        int i = this._bufferPos;
        bArr[i] = (byte) (bArr[i] | 128);
        int i2 = this._bufferPos + 1;
        this._bufferPos = i2;
        if (i2 == this._buffer.length) {
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
        System.arraycopy(bArrCopyBitLength, 0, this._buffer, 32, bArrCopyBitLength.length);
        processFilledBuffer(this._buffer, 0);
    }

    private byte[] copyBitLength() {
        byte[] bArr = new byte[32];
        for (int i = 0; i < bArr.length; i++) {
            bArr[i] = (byte) (this._bitCount[i] & 255);
        }
        return bArr;
    }

    @Override // org.bouncycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return 64;
    }

    @Override // org.bouncycastle.util.Memoable
    public Memoable copy() {
        return new WhirlpoolDigest(this);
    }

    @Override // org.bouncycastle.util.Memoable
    public void reset(Memoable memoable) {
        WhirlpoolDigest whirlpoolDigest = (WhirlpoolDigest) memoable;
        System.arraycopy(whirlpoolDigest._rc, 0, this._rc, 0, this._rc.length);
        System.arraycopy(whirlpoolDigest._buffer, 0, this._buffer, 0, this._buffer.length);
        this._bufferPos = whirlpoolDigest._bufferPos;
        System.arraycopy(whirlpoolDigest._bitCount, 0, this._bitCount, 0, this._bitCount.length);
        System.arraycopy(whirlpoolDigest._hash, 0, this._hash, 0, this._hash.length);
        System.arraycopy(whirlpoolDigest._K, 0, this._K, 0, this._K.length);
        System.arraycopy(whirlpoolDigest._L, 0, this._L, 0, this._L.length);
        System.arraycopy(whirlpoolDigest._block, 0, this._block, 0, this._block.length);
        System.arraycopy(whirlpoolDigest._state, 0, this._state, 0, this._state.length);
    }

    static {
        EIGHT[31] = 8;
        for (int i = 0; i < 256; i++) {
            int i2 = SBOX[i];
            int iMulX = mulX(i2);
            int iMulX2 = mulX(iMulX);
            int i3 = iMulX2 ^ i2;
            int iMulX3 = mulX(iMulX2);
            int i4 = iMulX3 ^ i2;
            C0[i] = packIntoLong(i2, i2, iMulX2, i2, iMulX3, i3, iMulX, i4);
            C1[i] = packIntoLong(i4, i2, i2, iMulX2, i2, iMulX3, i3, iMulX);
            C2[i] = packIntoLong(iMulX, i4, i2, i2, iMulX2, i2, iMulX3, i3);
            C3[i] = packIntoLong(i3, iMulX, i4, i2, i2, iMulX2, i2, iMulX3);
            C4[i] = packIntoLong(iMulX3, i3, iMulX, i4, i2, i2, iMulX2, i2);
            C5[i] = packIntoLong(i2, iMulX3, i3, iMulX, i4, i2, i2, iMulX2);
            C6[i] = packIntoLong(iMulX2, i2, iMulX3, i3, iMulX, i4, i2, i2);
            C7[i] = packIntoLong(i2, iMulX2, i2, iMulX3, i3, iMulX, i4, i2);
        }
    }
}
