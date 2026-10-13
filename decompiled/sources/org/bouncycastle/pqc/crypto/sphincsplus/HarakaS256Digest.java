package org.bouncycastle.pqc.crypto.sphincsplus;

import org.bouncycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/sphincsplus/HarakaS256Digest.class */
class HarakaS256Digest extends HarakaSBase implements Digest {
    public HarakaS256Digest(HarakaSXof harakaSXof) {
        this.haraka256_rc = harakaSXof.haraka256_rc;
    }

    @Override // org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "HarakaS-256";
    }

    @Override // org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return 32;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte b) {
        if (this.off > 31) {
            throw new IllegalArgumentException("total input cannot be more than 32 bytes");
        }
        byte[] bArr = this.buffer;
        int i = this.off;
        this.off = i + 1;
        bArr[i] = b;
    }

    @Override // org.bouncycastle.crypto.Digest
    public void update(byte[] bArr, int i, int i2) {
        if (this.off > 32 - i2) {
            throw new IllegalArgumentException("total input cannot be more than 32 bytes");
        }
        System.arraycopy(bArr, i, this.buffer, this.off, i2);
        this.off += i2;
    }

    @Override // org.bouncycastle.crypto.Digest
    public int doFinal(byte[] bArr, int i) {
        byte[] bArr2 = new byte[32];
        haraka256Perm(bArr2);
        xor(bArr2, 0, this.buffer, 0, bArr, i, 32);
        reset();
        return bArr.length;
    }

    @Override // org.bouncycastle.pqc.crypto.sphincsplus.HarakaSBase, org.bouncycastle.crypto.Digest
    public void reset() {
        super.reset();
    }
}
