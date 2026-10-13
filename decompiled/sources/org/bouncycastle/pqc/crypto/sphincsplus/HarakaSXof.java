package org.bouncycastle.pqc.crypto.sphincsplus;

import com.sun.jna.platform.win32.WinError;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/sphincsplus/HarakaSXof.class */
class HarakaSXof extends HarakaSBase {
    public String getAlgorithmName() {
        return "Haraka-S";
    }

    public HarakaSXof(byte[] bArr) {
        byte[] bArr2 = new byte[WinError.ERROR_MULTIPLE_FAULT_VIOLATION];
        update(bArr, 0, bArr.length);
        doFinal(bArr2, 0, bArr2.length);
        this.haraka512_rc = new long[10][8];
        this.haraka256_rc = new int[10][8];
        for (int i = 0; i < 10; i++) {
            interleaveConstant32(this.haraka256_rc[i], bArr2, i << 5);
            interleaveConstant(this.haraka512_rc[i], bArr2, i << 6);
        }
    }

    public void update(byte[] bArr, int i, int i2) {
        int i3 = i;
        int i4 = (i2 + this.off) >> 5;
        for (int i5 = 0; i5 < i4; i5++) {
            while (this.off < 32) {
                byte[] bArr2 = this.buffer;
                int i6 = this.off;
                this.off = i6 + 1;
                int i7 = i3;
                i3++;
                bArr2[i6] = (byte) (bArr2[i6] ^ bArr[i7]);
            }
            haraka512Perm(this.buffer);
            this.off = 0;
        }
        while (i3 < i + i2) {
            byte[] bArr3 = this.buffer;
            int i8 = this.off;
            this.off = i8 + 1;
            int i9 = i3;
            i3++;
            bArr3[i8] = (byte) (bArr3[i8] ^ bArr[i9]);
        }
    }

    public void update(byte b) {
        byte[] bArr = this.buffer;
        int i = this.off;
        this.off = i + 1;
        bArr[i] = (byte) (bArr[i] ^ b);
        if (this.off == 32) {
            haraka512Perm(this.buffer);
            this.off = 0;
        }
    }

    public int doFinal(byte[] bArr, int i, int i2) {
        byte[] bArr2 = this.buffer;
        int i3 = this.off;
        bArr2[i3] = (byte) (bArr2[i3] ^ 31);
        byte[] bArr3 = this.buffer;
        bArr3[31] = (byte) (bArr3[31] ^ 128);
        while (i2 >= 32) {
            haraka512Perm(this.buffer);
            System.arraycopy(this.buffer, 0, bArr, i, 32);
            i += 32;
            i2 -= 32;
        }
        if (i2 > 0) {
            haraka512Perm(this.buffer);
            System.arraycopy(this.buffer, 0, bArr, i, i2);
        }
        reset();
        return i2;
    }
}
