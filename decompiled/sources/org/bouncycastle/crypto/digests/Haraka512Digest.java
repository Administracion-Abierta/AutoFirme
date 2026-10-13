package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/digests/Haraka512Digest.class */
public class Haraka512Digest extends HarakaBase {
    private final byte[] buffer;
    private int off;
    private final CryptoServicePurpose purpose;

    public Haraka512Digest() {
        this(CryptoServicePurpose.ANY);
    }

    public Haraka512Digest(CryptoServicePurpose cryptoServicePurpose) {
        this.purpose = cryptoServicePurpose;
        this.buffer = new byte[64];
    }

    public Haraka512Digest(Haraka512Digest haraka512Digest) {
        this.purpose = haraka512Digest.purpose;
        this.buffer = Arrays.clone(haraka512Digest.buffer);
        this.off = haraka512Digest.off;
    }

    private void mix512(byte[][] bArr, byte[][] bArr2) {
        System.arraycopy(bArr[0], 12, bArr2[0], 0, 4);
        System.arraycopy(bArr[2], 12, bArr2[0], 4, 4);
        System.arraycopy(bArr[1], 12, bArr2[0], 8, 4);
        System.arraycopy(bArr[3], 12, bArr2[0], 12, 4);
        System.arraycopy(bArr[2], 0, bArr2[1], 0, 4);
        System.arraycopy(bArr[0], 0, bArr2[1], 4, 4);
        System.arraycopy(bArr[3], 0, bArr2[1], 8, 4);
        System.arraycopy(bArr[1], 0, bArr2[1], 12, 4);
        System.arraycopy(bArr[2], 4, bArr2[2], 0, 4);
        System.arraycopy(bArr[0], 4, bArr2[2], 4, 4);
        System.arraycopy(bArr[3], 4, bArr2[2], 8, 4);
        System.arraycopy(bArr[1], 4, bArr2[2], 12, 4);
        System.arraycopy(bArr[0], 8, bArr2[3], 0, 4);
        System.arraycopy(bArr[2], 8, bArr2[3], 4, 4);
        System.arraycopy(bArr[1], 8, bArr2[3], 8, 4);
        System.arraycopy(bArr[3], 8, bArr2[3], 12, 4);
    }

    private int haraka512256(byte[] bArr, byte[] bArr2, int i) {
        byte[][] bArr3 = new byte[4][16];
        byte[][] bArr4 = new byte[4][16];
        System.arraycopy(bArr, 0, bArr3[0], 0, 16);
        System.arraycopy(bArr, 16, bArr3[1], 0, 16);
        System.arraycopy(bArr, 32, bArr3[2], 0, 16);
        System.arraycopy(bArr, 48, bArr3[3], 0, 16);
        bArr3[0] = aesEnc(bArr3[0], RC[0]);
        bArr3[1] = aesEnc(bArr3[1], RC[1]);
        bArr3[2] = aesEnc(bArr3[2], RC[2]);
        bArr3[3] = aesEnc(bArr3[3], RC[3]);
        bArr3[0] = aesEnc(bArr3[0], RC[4]);
        bArr3[1] = aesEnc(bArr3[1], RC[5]);
        bArr3[2] = aesEnc(bArr3[2], RC[6]);
        bArr3[3] = aesEnc(bArr3[3], RC[7]);
        mix512(bArr3, bArr4);
        bArr3[0] = aesEnc(bArr4[0], RC[8]);
        bArr3[1] = aesEnc(bArr4[1], RC[9]);
        bArr3[2] = aesEnc(bArr4[2], RC[10]);
        bArr3[3] = aesEnc(bArr4[3], RC[11]);
        bArr3[0] = aesEnc(bArr3[0], RC[12]);
        bArr3[1] = aesEnc(bArr3[1], RC[13]);
        bArr3[2] = aesEnc(bArr3[2], RC[14]);
        bArr3[3] = aesEnc(bArr3[3], RC[15]);
        mix512(bArr3, bArr4);
        bArr3[0] = aesEnc(bArr4[0], RC[16]);
        bArr3[1] = aesEnc(bArr4[1], RC[17]);
        bArr3[2] = aesEnc(bArr4[2], RC[18]);
        bArr3[3] = aesEnc(bArr4[3], RC[19]);
        bArr3[0] = aesEnc(bArr3[0], RC[20]);
        bArr3[1] = aesEnc(bArr3[1], RC[21]);
        bArr3[2] = aesEnc(bArr3[2], RC[22]);
        bArr3[3] = aesEnc(bArr3[3], RC[23]);
        mix512(bArr3, bArr4);
        bArr3[0] = aesEnc(bArr4[0], RC[24]);
        bArr3[1] = aesEnc(bArr4[1], RC[25]);
        bArr3[2] = aesEnc(bArr4[2], RC[26]);
        bArr3[3] = aesEnc(bArr4[3], RC[27]);
        bArr3[0] = aesEnc(bArr3[0], RC[28]);
        bArr3[1] = aesEnc(bArr3[1], RC[29]);
        bArr3[2] = aesEnc(bArr3[2], RC[30]);
        bArr3[3] = aesEnc(bArr3[3], RC[31]);
        mix512(bArr3, bArr4);
        bArr3[0] = aesEnc(bArr4[0], RC[32]);
        bArr3[1] = aesEnc(bArr4[1], RC[33]);
        bArr3[2] = aesEnc(bArr4[2], RC[34]);
        bArr3[3] = aesEnc(bArr4[3], RC[35]);
        bArr3[0] = aesEnc(bArr3[0], RC[36]);
        bArr3[1] = aesEnc(bArr3[1], RC[37]);
        bArr3[2] = aesEnc(bArr3[2], RC[38]);
        bArr3[3] = aesEnc(bArr3[3], RC[39]);
        mix512(bArr3, bArr4);
        Bytes.xor(16, bArr4[0], 0, bArr, 0, bArr3[0], 0);
        Bytes.xor(16, bArr4[1], 0, bArr, 16, bArr3[1], 0);
        Bytes.xor(16, bArr4[2], 0, bArr, 32, bArr3[2], 0);
        Bytes.xor(16, bArr4[3], 0, bArr, 48, bArr3[3], 0);
        System.arraycopy(bArr3[0], 8, bArr2, i, 8);
        System.arraycopy(bArr3[1], 8, bArr2, i + 8, 8);
        System.arraycopy(bArr3[2], 0, bArr2, i + 16, 8);
        System.arraycopy(bArr3[3], 0, bArr2, i + 24, 8);
        return 32;
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "Haraka-512";
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) {
        if (this.off > 63) {
            throw new IllegalArgumentException("total input cannot be more than 64 bytes");
        }
        byte[] bArr = this.buffer;
        int i = this.off;
        this.off = i + 1;
        bArr[i] = b;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) {
        if (this.off > 64 - i2) {
            throw new IllegalArgumentException("total input cannot be more than 64 bytes");
        }
        System.arraycopy(bArr, i, this.buffer, this.off, i2);
        this.off += i2;
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        if (this.off != 64) {
            throw new IllegalStateException("input must be exactly 64 bytes");
        }
        if (bArr.length - i < 32) {
            throw new IllegalArgumentException("output too short to receive digest");
        }
        int iHaraka512256 = haraka512256(this.buffer, bArr, i);
        reset();
        return iHaraka512256;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void reset() {
        this.off = 0;
        Arrays.clear(this.buffer);
    }
}
