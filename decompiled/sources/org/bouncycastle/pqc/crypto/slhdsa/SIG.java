package org.bouncycastle.pqc.crypto.slhdsa;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/slhdsa/SIG.class */
class SIG {
    private final byte[] r;
    private final SIG_FORS[] sig_fors;
    private final SIG_XMSS[] sig_ht;

    public SIG(int i, int i2, int i3, int i4, int i5, int i6, byte[] bArr) {
        this.r = new byte[i];
        System.arraycopy(bArr, 0, this.r, 0, i);
        this.sig_fors = new SIG_FORS[i2];
        int length = i;
        for (int i7 = 0; i7 != i2; i7++) {
            byte[] bArr2 = new byte[i];
            System.arraycopy(bArr, length, bArr2, 0, i);
            length += i;
            byte[][] bArr3 = new byte[i3][];
            for (int i8 = 0; i8 != i3; i8++) {
                bArr3[i8] = new byte[i];
                System.arraycopy(bArr, length, bArr3[i8], 0, i);
                length += i;
            }
            this.sig_fors[i7] = new SIG_FORS(bArr2, bArr3);
        }
        this.sig_ht = new SIG_XMSS[i4];
        for (int i9 = 0; i9 != i4; i9++) {
            byte[] bArr4 = new byte[i6 * i];
            System.arraycopy(bArr, length, bArr4, 0, bArr4.length);
            length += bArr4.length;
            byte[][] bArr5 = new byte[i5][];
            for (int i10 = 0; i10 != i5; i10++) {
                bArr5[i10] = new byte[i];
                System.arraycopy(bArr, length, bArr5[i10], 0, i);
                length += i;
            }
            this.sig_ht[i9] = new SIG_XMSS(bArr4, bArr5);
        }
        if (length != bArr.length) {
            throw new IllegalArgumentException("signature wrong length");
        }
    }

    public byte[] getR() {
        return this.r;
    }

    public SIG_FORS[] getSIG_FORS() {
        return this.sig_fors;
    }

    public SIG_XMSS[] getSIG_HT() {
        return this.sig_ht;
    }
}
