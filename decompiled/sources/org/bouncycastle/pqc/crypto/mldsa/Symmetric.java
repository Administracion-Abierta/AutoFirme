package org.bouncycastle.pqc.crypto.mldsa;

import org.bouncycastle.crypto.digests.SHAKEDigest;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mldsa/Symmetric.class */
abstract class Symmetric {
    final int stream128BlockBytes;
    final int stream256BlockBytes;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mldsa/Symmetric$ShakeSymmetric.class */
    static class ShakeSymmetric extends Symmetric {
        private final SHAKEDigest digest128;
        private final SHAKEDigest digest256;

        ShakeSymmetric() {
            super(168, 136);
            this.digest128 = new SHAKEDigest(128);
            this.digest256 = new SHAKEDigest(256);
        }

        private void streamInit(SHAKEDigest sHAKEDigest, byte[] bArr, short s) {
            sHAKEDigest.reset();
            byte[] bArr2 = {(byte) s, (byte) (s >> 8)};
            sHAKEDigest.update(bArr, 0, bArr.length);
            sHAKEDigest.update(bArr2, 0, bArr2.length);
        }

        @Override // org.bouncycastle.pqc.crypto.mldsa.Symmetric
        void stream128init(byte[] bArr, short s) {
            streamInit(this.digest128, bArr, s);
        }

        @Override // org.bouncycastle.pqc.crypto.mldsa.Symmetric
        void stream256init(byte[] bArr, short s) {
            streamInit(this.digest256, bArr, s);
        }

        @Override // org.bouncycastle.pqc.crypto.mldsa.Symmetric
        void stream128squeezeBlocks(byte[] bArr, int i, int i2) {
            this.digest128.doOutput(bArr, i, i2);
        }

        @Override // org.bouncycastle.pqc.crypto.mldsa.Symmetric
        void stream256squeezeBlocks(byte[] bArr, int i, int i2) {
            this.digest256.doOutput(bArr, i, i2);
        }
    }

    Symmetric(int i, int i2) {
        this.stream128BlockBytes = i;
        this.stream256BlockBytes = i2;
    }

    abstract void stream128init(byte[] bArr, short s);

    abstract void stream256init(byte[] bArr, short s);

    abstract void stream128squeezeBlocks(byte[] bArr, int i, int i2);

    abstract void stream256squeezeBlocks(byte[] bArr, int i, int i2);
}
