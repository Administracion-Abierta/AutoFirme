package org.bouncycastle.crypto.digests;

import com.sun.jna.platform.win32.WinError;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/XoodyakDigest.class */
public class XoodyakDigest implements Digest {
    private int phase;
    private MODE mode;
    private int Rabsorb;
    private final int f_bPrime = 48;
    private final int Rhash = 16;
    private final int PhaseDown = 1;
    private final int PhaseUp = 2;
    private final int MAXROUNDS = 12;
    private final int TAGLEN = 16;
    private final int[] RC = {88, 56, 960, WinError.ERROR_META_EXPANSION_TOO_LONG, 288, 20, 96, 44, 896, 240, 416, 18};
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private byte[] state = new byte[48];

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/XoodyakDigest$MODE.class */
    enum MODE {
        ModeHash,
        ModeKeyed
    }

    public XoodyakDigest() {
        reset();
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "Xoodyak Hash";
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return 32;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) {
        this.buffer.write(b);
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) {
        if (i + i2 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        this.buffer.write(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        if (32 + i > bArr.length) {
            throw new OutputLengthException("output buffer is too short");
        }
        byte[] byteArray = this.buffer.toByteArray();
        int i2 = 0;
        int size = this.buffer.size();
        int i3 = 3;
        do {
            if (this.phase != 2) {
                Up(null, 0, 0, 0);
            }
            int iMin = Math.min(size, this.Rabsorb);
            Down(byteArray, i2, iMin, i3);
            i3 = 0;
            i2 += iMin;
            size -= iMin;
        } while (size != 0);
        Up(bArr, i, 16, 64);
        Down(null, 0, 0, 0);
        Up(bArr, i + 16, 16, 0);
        reset();
        return 32;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void reset() {
        Arrays.fill(this.state, (byte) 0);
        this.phase = 2;
        this.mode = MODE.ModeHash;
        this.Rabsorb = 16;
        this.buffer.reset();
    }

    private void Up(byte[] bArr, int i, int i2, int i3) {
        if (this.mode != MODE.ModeHash) {
            byte[] bArr2 = this.state;
            bArr2[47] = (byte) (bArr2[47] ^ i3);
        }
        int iLittleEndianToInt = Pack.littleEndianToInt(this.state, 0);
        int iLittleEndianToInt2 = Pack.littleEndianToInt(this.state, 4);
        int iLittleEndianToInt3 = Pack.littleEndianToInt(this.state, 8);
        int iLittleEndianToInt4 = Pack.littleEndianToInt(this.state, 12);
        int iLittleEndianToInt5 = Pack.littleEndianToInt(this.state, 16);
        int iLittleEndianToInt6 = Pack.littleEndianToInt(this.state, 20);
        int iLittleEndianToInt7 = Pack.littleEndianToInt(this.state, 24);
        int iLittleEndianToInt8 = Pack.littleEndianToInt(this.state, 28);
        int iLittleEndianToInt9 = Pack.littleEndianToInt(this.state, 32);
        int iLittleEndianToInt10 = Pack.littleEndianToInt(this.state, 36);
        int iLittleEndianToInt11 = Pack.littleEndianToInt(this.state, 40);
        int iLittleEndianToInt12 = Pack.littleEndianToInt(this.state, 44);
        for (int i4 = 0; i4 < 12; i4++) {
            int i5 = (iLittleEndianToInt ^ iLittleEndianToInt5) ^ iLittleEndianToInt9;
            int i6 = (iLittleEndianToInt2 ^ iLittleEndianToInt6) ^ iLittleEndianToInt10;
            int i7 = (iLittleEndianToInt3 ^ iLittleEndianToInt7) ^ iLittleEndianToInt11;
            int i8 = (iLittleEndianToInt4 ^ iLittleEndianToInt8) ^ iLittleEndianToInt12;
            int iRotateLeft = Integers.rotateLeft(i8, 5) ^ Integers.rotateLeft(i8, 14);
            int iRotateLeft2 = Integers.rotateLeft(i5, 5) ^ Integers.rotateLeft(i5, 14);
            int iRotateLeft3 = Integers.rotateLeft(i6, 5) ^ Integers.rotateLeft(i6, 14);
            int iRotateLeft4 = Integers.rotateLeft(i7, 5) ^ Integers.rotateLeft(i7, 14);
            int i9 = iLittleEndianToInt ^ iRotateLeft;
            int i10 = iLittleEndianToInt5 ^ iRotateLeft;
            int i11 = iLittleEndianToInt9 ^ iRotateLeft;
            int i12 = iLittleEndianToInt2 ^ iRotateLeft2;
            int i13 = iLittleEndianToInt6 ^ iRotateLeft2;
            int i14 = iLittleEndianToInt10 ^ iRotateLeft2;
            int i15 = iLittleEndianToInt3 ^ iRotateLeft3;
            int i16 = iLittleEndianToInt7 ^ iRotateLeft3;
            int i17 = iLittleEndianToInt11 ^ iRotateLeft3;
            int i18 = iLittleEndianToInt4 ^ iRotateLeft4;
            int i19 = iLittleEndianToInt8 ^ iRotateLeft4;
            int i20 = iLittleEndianToInt12 ^ iRotateLeft4;
            int iRotateLeft5 = Integers.rotateLeft(i11, 11);
            int iRotateLeft6 = Integers.rotateLeft(i14, 11);
            int iRotateLeft7 = Integers.rotateLeft(i17, 11);
            int iRotateLeft8 = Integers.rotateLeft(i20, 11);
            int i21 = i9 ^ this.RC[i4];
            iLittleEndianToInt = i21 ^ ((i19 ^ (-1)) & iRotateLeft5);
            iLittleEndianToInt2 = i12 ^ ((i10 ^ (-1)) & iRotateLeft6);
            iLittleEndianToInt3 = i15 ^ ((i13 ^ (-1)) & iRotateLeft7);
            iLittleEndianToInt4 = i18 ^ ((i16 ^ (-1)) & iRotateLeft8);
            int i22 = i19 ^ ((iRotateLeft5 ^ (-1)) & i21);
            int i23 = i10 ^ ((iRotateLeft6 ^ (-1)) & i12);
            int i24 = i13 ^ ((iRotateLeft7 ^ (-1)) & i15);
            int i25 = i16 ^ ((iRotateLeft8 ^ (-1)) & i18);
            int i26 = iRotateLeft5 ^ ((i21 ^ (-1)) & i19);
            int i27 = iRotateLeft6 ^ ((i12 ^ (-1)) & i10);
            iLittleEndianToInt5 = Integers.rotateLeft(i22, 1);
            iLittleEndianToInt6 = Integers.rotateLeft(i23, 1);
            iLittleEndianToInt7 = Integers.rotateLeft(i24, 1);
            iLittleEndianToInt8 = Integers.rotateLeft(i25, 1);
            iLittleEndianToInt9 = Integers.rotateLeft(iRotateLeft7 ^ ((i15 ^ (-1)) & i13), 8);
            iLittleEndianToInt10 = Integers.rotateLeft(iRotateLeft8 ^ ((i18 ^ (-1)) & i16), 8);
            iLittleEndianToInt11 = Integers.rotateLeft(i26, 8);
            iLittleEndianToInt12 = Integers.rotateLeft(i27, 8);
        }
        Pack.intToLittleEndian(iLittleEndianToInt, this.state, 0);
        Pack.intToLittleEndian(iLittleEndianToInt2, this.state, 4);
        Pack.intToLittleEndian(iLittleEndianToInt3, this.state, 8);
        Pack.intToLittleEndian(iLittleEndianToInt4, this.state, 12);
        Pack.intToLittleEndian(iLittleEndianToInt5, this.state, 16);
        Pack.intToLittleEndian(iLittleEndianToInt6, this.state, 20);
        Pack.intToLittleEndian(iLittleEndianToInt7, this.state, 24);
        Pack.intToLittleEndian(iLittleEndianToInt8, this.state, 28);
        Pack.intToLittleEndian(iLittleEndianToInt9, this.state, 32);
        Pack.intToLittleEndian(iLittleEndianToInt10, this.state, 36);
        Pack.intToLittleEndian(iLittleEndianToInt11, this.state, 40);
        Pack.intToLittleEndian(iLittleEndianToInt12, this.state, 44);
        this.phase = 2;
        if (bArr != null) {
            System.arraycopy(this.state, 0, bArr, i, i2);
        }
    }

    void Down(byte[] bArr, int i, int i2, int i3) {
        for (int i4 = 0; i4 < i2; i4++) {
            byte[] bArr2 = this.state;
            int i5 = i4;
            int i6 = i;
            i++;
            bArr2[i5] = (byte) (bArr2[i5] ^ bArr[i6]);
        }
        byte[] bArr3 = this.state;
        bArr3[i2] = (byte) (bArr3[i2] ^ 1);
        byte[] bArr4 = this.state;
        bArr4[47] = (byte) (bArr4[47] ^ (this.mode == MODE.ModeHash ? i3 & 1 : i3));
        this.phase = 1;
    }
}
