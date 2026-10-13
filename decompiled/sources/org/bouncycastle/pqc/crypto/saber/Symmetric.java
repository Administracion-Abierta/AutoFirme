package org.bouncycastle.pqc.crypto.saber;

import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/saber/Symmetric.class */
abstract class Symmetric {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/saber/Symmetric$AesSymmetric.class */
    static class AesSymmetric extends Symmetric {
        private final SHA256Digest sha256Digest = new SHA256Digest();
        private final SHA512Digest sha512Digest = new SHA512Digest();
        private final StreamCipher cipher = SICBlockCipher.newInstance(AESEngine.newInstance());

        AesSymmetric() {
        }

        @Override // org.bouncycastle.pqc.crypto.saber.Symmetric
        void hash_h(byte[] bArr, byte[] bArr2, int i) {
            this.sha256Digest.update(bArr2, 0, bArr2.length);
            this.sha256Digest.doFinal(bArr, i);
        }

        @Override // org.bouncycastle.pqc.crypto.saber.Symmetric
        void hash_g(byte[] bArr, byte[] bArr2) {
            this.sha512Digest.update(bArr2, 0, bArr2.length);
            this.sha512Digest.doFinal(bArr, 0);
        }

        @Override // org.bouncycastle.pqc.crypto.saber.Symmetric
        void prf(byte[] bArr, byte[] bArr2, int i, int i2) {
            this.cipher.init(true, new ParametersWithIV(new KeyParameter(bArr2, 0, i), new byte[16]));
            this.cipher.processBytes(new byte[i2], 0, i2, bArr, 0);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/saber/Symmetric$ShakeSymmetric.class */
    static class ShakeSymmetric extends Symmetric {
        private final Xof shakeDigest = new SHAKEDigest(128);
        private final SHA3Digest sha3Digest256 = new SHA3Digest(256);
        private final SHA3Digest sha3Digest512 = new SHA3Digest(512);

        ShakeSymmetric() {
        }

        @Override // org.bouncycastle.pqc.crypto.saber.Symmetric
        void hash_h(byte[] bArr, byte[] bArr2, int i) {
            this.sha3Digest256.update(bArr2, 0, bArr2.length);
            this.sha3Digest256.doFinal(bArr, i);
        }

        @Override // org.bouncycastle.pqc.crypto.saber.Symmetric
        void hash_g(byte[] bArr, byte[] bArr2) {
            this.sha3Digest512.update(bArr2, 0, bArr2.length);
            this.sha3Digest512.doFinal(bArr, 0);
        }

        @Override // org.bouncycastle.pqc.crypto.saber.Symmetric
        void prf(byte[] bArr, byte[] bArr2, int i, int i2) {
            this.shakeDigest.reset();
            this.shakeDigest.update(bArr2, 0, i);
            this.shakeDigest.doFinal(bArr, 0, i2);
        }
    }

    Symmetric() {
    }

    abstract void hash_h(byte[] bArr, byte[] bArr2, int i);

    abstract void hash_g(byte[] bArr, byte[] bArr2);

    abstract void prf(byte[] bArr, byte[] bArr2, int i, int i2);
}
