package org.bouncycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/rainbow/RainbowDRBG.class */
class RainbowDRBG extends SecureRandom {
    private byte[] seed;
    private byte[] key;
    private byte[] v;
    private Digest hashAlgo;

    public RainbowDRBG(byte[] bArr, Digest digest) {
        this.seed = bArr;
        this.hashAlgo = digest;
        init(256);
    }

    private void init(int i) {
        if (this.seed.length >= 48) {
            randombytes_init(this.seed, i);
        } else {
            randombytes_init(Arrays.concatenate(this.seed, RainbowUtil.hash(this.hashAlgo, this.seed, 48 - this.seed.length)), i);
        }
    }

    @Override // java.security.SecureRandom, java.util.Random
    public void nextBytes(byte[] bArr) {
        byte[] bArr2 = new byte[16];
        int i = 0;
        int length = bArr.length;
        while (length > 0) {
            for (int i2 = 15; i2 >= 0; i2--) {
                if ((this.v[i2] & 255) != 255) {
                    byte[] bArr3 = this.v;
                    int i3 = i2;
                    bArr3[i3] = (byte) (bArr3[i3] + 1);
                    break;
                }
                this.v[i2] = 0;
            }
            AES256_ECB(this.key, this.v, bArr2, 0);
            if (length > 15) {
                System.arraycopy(bArr2, 0, bArr, i, bArr2.length);
                i += 16;
                length -= 16;
            } else {
                System.arraycopy(bArr2, 0, bArr, i, length);
                length = 0;
            }
        }
        AES256_CTR_DRBG_Update(null, this.key, this.v);
    }

    private void AES256_ECB(byte[] bArr, byte[] bArr2, byte[] bArr3, int i) {
        try {
            AESEngine aESEngine = new AESEngine();
            aESEngine.init(true, new KeyParameter(bArr));
            for (int i2 = 0; i2 != bArr2.length; i2 += 16) {
                aESEngine.processBlock(bArr2, i2, bArr3, i + i2);
            }
        } catch (Throwable th) {
            throw new IllegalStateException("drbg failure: " + th.getMessage(), th);
        }
    }

    private void AES256_CTR_DRBG_Update(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        byte[] bArr4 = new byte[48];
        for (int i = 0; i < 3; i++) {
            for (int i2 = 15; i2 >= 0; i2--) {
                if ((bArr3[i2] & 255) != 255) {
                    int i3 = i2;
                    bArr3[i3] = (byte) (bArr3[i3] + 1);
                    break;
                }
                bArr3[i2] = 0;
            }
            AES256_ECB(bArr2, bArr3, bArr4, 16 * i);
        }
        if (bArr != null) {
            for (int i4 = 0; i4 < 48; i4++) {
                int i5 = i4;
                bArr4[i5] = (byte) (bArr4[i5] ^ bArr[i4]);
            }
        }
        System.arraycopy(bArr4, 0, bArr2, 0, bArr2.length);
        System.arraycopy(bArr4, 32, bArr3, 0, bArr3.length);
    }

    private void randombytes_init(byte[] bArr, int i) {
        byte[] bArr2 = new byte[48];
        System.arraycopy(bArr, 0, bArr2, 0, bArr2.length);
        this.key = new byte[32];
        this.v = new byte[16];
        AES256_CTR_DRBG_Update(bArr2, this.key, this.v);
    }
}
