package org.bouncycastle.pqc.crypto.frodo;

import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/frodo/FrodoMatrixGenerator.class */
abstract class FrodoMatrixGenerator {
    int n;
    int q;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/frodo/FrodoMatrixGenerator$Aes128MatrixGenerator.class */
    static class Aes128MatrixGenerator extends FrodoMatrixGenerator {
        public Aes128MatrixGenerator(int i, int i2) {
            super(i, i2);
        }

        @Override // org.bouncycastle.pqc.crypto.frodo.FrodoMatrixGenerator
        short[] genMatrix(byte[] bArr) {
            short[] sArr = new short[this.n * this.n];
            byte[] bArr2 = new byte[16];
            byte[] bArr3 = new byte[16];
            AESEngine aESEngine = new AESEngine();
            aESEngine.init(true, new KeyParameter(bArr));
            for (int i = 0; i < this.n; i++) {
                Pack.shortToLittleEndian((short) i, bArr2, 0);
                for (int i2 = 0; i2 < this.n; i2 += 8) {
                    Pack.shortToLittleEndian((short) i2, bArr2, 2);
                    aESEngine.processBlock(bArr2, 0, bArr3, 0);
                    for (int i3 = 0; i3 < 8; i3++) {
                        sArr[(i * this.n) + i2 + i3] = (short) (Pack.littleEndianToShort(bArr3, 2 * i3) & (this.q - 1));
                    }
                }
            }
            return sArr;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/frodo/FrodoMatrixGenerator$Shake128MatrixGenerator.class */
    static class Shake128MatrixGenerator extends FrodoMatrixGenerator {
        public Shake128MatrixGenerator(int i, int i2) {
            super(i, i2);
        }

        @Override // org.bouncycastle.pqc.crypto.frodo.FrodoMatrixGenerator
        short[] genMatrix(byte[] bArr) {
            short[] sArr = new short[this.n * this.n];
            byte[] bArr2 = new byte[(16 * this.n) / 8];
            byte[] bArr3 = new byte[2 + bArr.length];
            System.arraycopy(bArr, 0, bArr3, 2, bArr.length);
            SHAKEDigest sHAKEDigest = new SHAKEDigest(128);
            short s = 0;
            while (true) {
                short s2 = s;
                if (s2 >= this.n) {
                    return sArr;
                }
                Pack.shortToLittleEndian(s2, bArr3, 0);
                sHAKEDigest.update(bArr3, 0, bArr3.length);
                sHAKEDigest.doFinal(bArr2, 0, bArr2.length);
                short s3 = 0;
                while (true) {
                    short s4 = s3;
                    if (s4 < this.n) {
                        sArr[(s2 * this.n) + s4] = (short) (Pack.littleEndianToShort(bArr2, 2 * s4) & (this.q - 1));
                        s3 = (short) (s4 + 1);
                    }
                }
                s = (short) (s2 + 1);
            }
        }
    }

    public FrodoMatrixGenerator(int i, int i2) {
        this.n = i;
        this.q = i2;
    }

    abstract short[] genMatrix(byte[] bArr);
}
