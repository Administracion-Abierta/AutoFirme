package org.bouncycastle.crypto.macs;

import com.sun.jna.platform.win32.WinNT;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/macs/Poly1305.class */
public class Poly1305 implements Mac {
    private static final int BLOCK_SIZE = 16;
    private final BlockCipher cipher;
    private final byte[] singleByte;
    private int r0;
    private int r1;
    private int r2;
    private int r3;
    private int r4;
    private int s1;
    private int s2;
    private int s3;
    private int s4;
    private int k0;
    private int k1;
    private int k2;
    private int k3;
    private final byte[] currentBlock;
    private int currentBlockOffset;
    private int h0;
    private int h1;
    private int h2;
    private int h3;
    private int h4;

    public Poly1305() {
        this.singleByte = new byte[1];
        this.currentBlock = new byte[16];
        this.currentBlockOffset = 0;
        this.cipher = null;
    }

    public Poly1305(BlockCipher blockCipher) {
        this.singleByte = new byte[1];
        this.currentBlock = new byte[16];
        this.currentBlockOffset = 0;
        if (blockCipher.getBlockSize() != 16) {
            throw new IllegalArgumentException("Poly1305 requires a 128 bit block cipher.");
        }
        this.cipher = blockCipher;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void init(CipherParameters cipherParameters) throws IllegalArgumentException {
        byte[] iv = null;
        if (this.cipher != null) {
            if (!(cipherParameters instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("Poly1305 requires an IV when used with a block cipher.");
            }
            ParametersWithIV parametersWithIV = (ParametersWithIV) cipherParameters;
            iv = parametersWithIV.getIV();
            cipherParameters = parametersWithIV.getParameters();
        }
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("Poly1305 requires a key.");
        }
        setKey(((KeyParameter) cipherParameters).getKey(), iv);
        reset();
    }

    private void setKey(byte[] bArr, byte[] bArr2) {
        byte[] bArr3;
        int i;
        if (bArr.length != 32) {
            throw new IllegalArgumentException("Poly1305 key must be 256 bits.");
        }
        if (this.cipher != null && (bArr2 == null || bArr2.length != 16)) {
            throw new IllegalArgumentException("Poly1305 requires a 128 bit IV.");
        }
        int iLittleEndianToInt = Pack.littleEndianToInt(bArr, 0);
        int iLittleEndianToInt2 = Pack.littleEndianToInt(bArr, 4);
        int iLittleEndianToInt3 = Pack.littleEndianToInt(bArr, 8);
        int iLittleEndianToInt4 = Pack.littleEndianToInt(bArr, 12);
        this.r0 = iLittleEndianToInt & 67108863;
        this.r1 = ((iLittleEndianToInt >>> 26) | (iLittleEndianToInt2 << 6)) & 67108611;
        this.r2 = ((iLittleEndianToInt2 >>> 20) | (iLittleEndianToInt3 << 12)) & 67092735;
        this.r3 = ((iLittleEndianToInt3 >>> 14) | (iLittleEndianToInt4 << 18)) & 66076671;
        this.r4 = (iLittleEndianToInt4 >>> 8) & WinNT.NLS_VALID_LOCALE_MASK;
        this.s1 = this.r1 * 5;
        this.s2 = this.r2 * 5;
        this.s3 = this.r3 * 5;
        this.s4 = this.r4 * 5;
        if (this.cipher == null) {
            bArr3 = bArr;
            i = 16;
        } else {
            bArr3 = new byte[16];
            i = 0;
            this.cipher.init(true, new KeyParameter(bArr, 16, 16));
            this.cipher.processBlock(bArr2, 0, bArr3, 0);
        }
        this.k0 = Pack.littleEndianToInt(bArr3, i + 0);
        this.k1 = Pack.littleEndianToInt(bArr3, i + 4);
        this.k2 = Pack.littleEndianToInt(bArr3, i + 8);
        this.k3 = Pack.littleEndianToInt(bArr3, i + 12);
    }

    @Override // org.bouncycastle.crypto.Mac
    public String getAlgorithmName() {
        return this.cipher == null ? "Poly1305" : "Poly1305-" + this.cipher.getAlgorithmName();
    }

    @Override // org.bouncycastle.crypto.Mac
    public int getMacSize() {
        return 16;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte b) throws IllegalStateException {
        this.singleByte[0] = b;
        update(this.singleByte, 0, 1);
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte[] bArr, int i, int i2) throws IllegalStateException, DataLengthException {
        int i3 = 0;
        while (i2 > i3) {
            if (this.currentBlockOffset == 16) {
                processBlock();
                this.currentBlockOffset = 0;
            }
            int iMin = Math.min(i2 - i3, 16 - this.currentBlockOffset);
            System.arraycopy(bArr, i3 + i, this.currentBlock, this.currentBlockOffset, iMin);
            i3 += iMin;
            this.currentBlockOffset += iMin;
        }
    }

    private void processBlock() {
        if (this.currentBlockOffset < 16) {
            this.currentBlock[this.currentBlockOffset] = 1;
            for (int i = this.currentBlockOffset + 1; i < 16; i++) {
                this.currentBlock[i] = 0;
            }
        }
        long jLittleEndianToInt = 4294967295L & ((long) Pack.littleEndianToInt(this.currentBlock, 0));
        long jLittleEndianToInt2 = 4294967295L & ((long) Pack.littleEndianToInt(this.currentBlock, 4));
        long jLittleEndianToInt3 = 4294967295L & ((long) Pack.littleEndianToInt(this.currentBlock, 8));
        long jLittleEndianToInt4 = 4294967295L & ((long) Pack.littleEndianToInt(this.currentBlock, 12));
        this.h0 = (int) (((long) this.h0) + (jLittleEndianToInt & 67108863));
        this.h1 = (int) (((long) this.h1) + ((((jLittleEndianToInt2 << 32) | jLittleEndianToInt) >>> 26) & 67108863));
        this.h2 = (int) (((long) this.h2) + ((((jLittleEndianToInt3 << 32) | jLittleEndianToInt2) >>> 20) & 67108863));
        this.h3 = (int) (((long) this.h3) + ((((jLittleEndianToInt4 << 32) | jLittleEndianToInt3) >>> 14) & 67108863));
        this.h4 = (int) (((long) this.h4) + (jLittleEndianToInt4 >>> 8));
        if (this.currentBlockOffset == 16) {
            this.h4 += 16777216;
        }
        long jMul32x32_64 = mul32x32_64(this.h0, this.r0) + mul32x32_64(this.h1, this.s4) + mul32x32_64(this.h2, this.s3) + mul32x32_64(this.h3, this.s2) + mul32x32_64(this.h4, this.s1);
        long jMul32x32_65 = mul32x32_64(this.h0, this.r1) + mul32x32_64(this.h1, this.r0) + mul32x32_64(this.h2, this.s4) + mul32x32_64(this.h3, this.s3) + mul32x32_64(this.h4, this.s2);
        long jMul32x32_66 = mul32x32_64(this.h0, this.r2) + mul32x32_64(this.h1, this.r1) + mul32x32_64(this.h2, this.r0) + mul32x32_64(this.h3, this.s4) + mul32x32_64(this.h4, this.s3);
        long jMul32x32_67 = mul32x32_64(this.h0, this.r3) + mul32x32_64(this.h1, this.r2) + mul32x32_64(this.h2, this.r1) + mul32x32_64(this.h3, this.r0) + mul32x32_64(this.h4, this.s4);
        long jMul32x32_68 = mul32x32_64(this.h0, this.r4) + mul32x32_64(this.h1, this.r3) + mul32x32_64(this.h2, this.r2) + mul32x32_64(this.h3, this.r1) + mul32x32_64(this.h4, this.r0);
        this.h0 = ((int) jMul32x32_64) & 67108863;
        long j = jMul32x32_65 + (jMul32x32_64 >>> 26);
        this.h1 = ((int) j) & 67108863;
        long j2 = jMul32x32_66 + (j >>> 26);
        this.h2 = ((int) j2) & 67108863;
        long j3 = jMul32x32_67 + (j2 >>> 26);
        this.h3 = ((int) j3) & 67108863;
        long j4 = jMul32x32_68 + (j3 >>> 26);
        this.h4 = ((int) j4) & 67108863;
        this.h0 += ((int) (j4 >>> 26)) * 5;
        this.h1 += this.h0 >>> 26;
        this.h0 &= 67108863;
    }

    @Override // org.bouncycastle.crypto.Mac
    public int doFinal(byte[] bArr, int i) throws IllegalStateException, DataLengthException {
        if (i + 16 > bArr.length) {
            throw new OutputLengthException("Output buffer is too short.");
        }
        if (this.currentBlockOffset > 0) {
            processBlock();
        }
        this.h1 += this.h0 >>> 26;
        this.h0 &= 67108863;
        this.h2 += this.h1 >>> 26;
        this.h1 &= 67108863;
        this.h3 += this.h2 >>> 26;
        this.h2 &= 67108863;
        this.h4 += this.h3 >>> 26;
        this.h3 &= 67108863;
        this.h0 += (this.h4 >>> 26) * 5;
        this.h4 &= 67108863;
        this.h1 += this.h0 >>> 26;
        this.h0 &= 67108863;
        int i2 = this.h0 + 5;
        int i3 = i2 >>> 26;
        int i4 = i2 & 67108863;
        int i5 = this.h1 + i3;
        int i6 = i5 >>> 26;
        int i7 = i5 & 67108863;
        int i8 = this.h2 + i6;
        int i9 = i8 >>> 26;
        int i10 = i8 & 67108863;
        int i11 = this.h3 + i9;
        int i12 = i11 >>> 26;
        int i13 = i11 & 67108863;
        int i14 = (this.h4 + i12) - 67108864;
        int i15 = (i14 >>> 31) - 1;
        int i16 = i15 ^ (-1);
        this.h0 = (this.h0 & i16) | (i4 & i15);
        this.h1 = (this.h1 & i16) | (i7 & i15);
        this.h2 = (this.h2 & i16) | (i10 & i15);
        this.h3 = (this.h3 & i16) | (i13 & i15);
        this.h4 = (this.h4 & i16) | (i14 & i15);
        long j = (((long) (this.h0 | (this.h1 << 26))) & 4294967295L) + (4294967295L & ((long) this.k0));
        long j2 = (((long) ((this.h1 >>> 6) | (this.h2 << 20))) & 4294967295L) + (4294967295L & ((long) this.k1));
        long j3 = (((long) ((this.h2 >>> 12) | (this.h3 << 14))) & 4294967295L) + (4294967295L & ((long) this.k2));
        long j4 = (((long) ((this.h3 >>> 18) | (this.h4 << 8))) & 4294967295L) + (4294967295L & ((long) this.k3));
        Pack.intToLittleEndian((int) j, bArr, i);
        long j5 = j2 + (j >>> 32);
        Pack.intToLittleEndian((int) j5, bArr, i + 4);
        long j6 = j3 + (j5 >>> 32);
        Pack.intToLittleEndian((int) j6, bArr, i + 8);
        Pack.intToLittleEndian((int) (j4 + (j6 >>> 32)), bArr, i + 12);
        reset();
        return 16;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void reset() {
        this.currentBlockOffset = 0;
        this.h4 = 0;
        this.h3 = 0;
        this.h2 = 0;
        this.h1 = 0;
        this.h0 = 0;
    }

    private static final long mul32x32_64(int i, int i2) {
        return (((long) i) & 4294967295L) * ((long) i2);
    }
}
