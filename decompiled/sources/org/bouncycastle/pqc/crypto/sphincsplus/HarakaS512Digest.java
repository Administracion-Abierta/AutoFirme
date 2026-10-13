package org.bouncycastle.pqc.crypto.sphincsplus;

import org.bouncycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/sphincsplus/HarakaS512Digest.class */
class HarakaS512Digest extends HarakaSBase implements Digest {
    public HarakaS512Digest(HarakaSXof harakaSXof) {
        this.haraka512_rc = harakaSXof.haraka512_rc;
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "HarakaS-512";
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return 32;
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
        byte[] bArr2 = new byte[64];
        haraka512Perm(bArr2);
        xor(bArr2, 8, this.buffer, 8, bArr, i, 8);
        xor(bArr2, 24, this.buffer, 24, bArr, i + 8, 16);
        xor(bArr2, 48, this.buffer, 48, bArr, i + 24, 8);
        reset();
        return bArr2.length;
    }

    @Override // org.bouncycastle.pqc.crypto.sphincsplus.HarakaSBase, org.bouncycastle.crypto.Digest
    public void reset() {
        super.reset();
    }
}
