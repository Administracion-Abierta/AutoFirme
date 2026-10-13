package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/RFC3394WrapEngine.class */
public class RFC3394WrapEngine implements Wrapper {
    private static final byte[] DEFAULT_IV = {-90, -90, -90, -90, -90, -90, -90, -90};
    private final BlockCipher engine;
    private final boolean wrapCipherMode;
    private final byte[] iv;
    private KeyParameter param;
    private boolean forWrapping;

    public RFC3394WrapEngine(BlockCipher blockCipher) {
        this(blockCipher, false);
    }

    public RFC3394WrapEngine(BlockCipher blockCipher, boolean z) {
        this.iv = new byte[8];
        this.param = null;
        this.forWrapping = true;
        this.engine = blockCipher;
        this.wrapCipherMode = !z;
    }

    @Override // org.bouncycastle.crypto.Wrapper
    public void init(boolean z, CipherParameters cipherParameters) {
        this.forWrapping = z;
        if (cipherParameters instanceof ParametersWithRandom) {
            cipherParameters = ((ParametersWithRandom) cipherParameters).getParameters();
        }
        if (cipherParameters instanceof KeyParameter) {
            this.param = (KeyParameter) cipherParameters;
            System.arraycopy(DEFAULT_IV, 0, this.iv, 0, 8);
        } else if (cipherParameters instanceof ParametersWithIV) {
            ParametersWithIV parametersWithIV = (ParametersWithIV) cipherParameters;
            byte[] iv = parametersWithIV.getIV();
            if (iv.length != 8) {
                throw new IllegalArgumentException("IV not equal to 8");
            }
            this.param = (KeyParameter) parametersWithIV.getParameters();
            System.arraycopy(iv, 0, this.iv, 0, 8);
        }
    }

    @Override // org.bouncycastle.crypto.Wrapper
    public String getAlgorithmName() {
        return this.engine.getAlgorithmName();
    }

    @Override // org.bouncycastle.crypto.Wrapper
    public byte[] wrap(byte[] bArr, int i, int i2) {
        if (!this.forWrapping) {
            throw new IllegalStateException("not set for wrapping");
        }
        if (i2 < 8) {
            throw new DataLengthException("wrap data must be at least 8 bytes");
        }
        int i3 = i2 / 8;
        if (i3 * 8 != i2) {
            throw new DataLengthException("wrap data must be a multiple of 8 bytes");
        }
        this.engine.init(this.wrapCipherMode, this.param);
        byte[] bArr2 = new byte[i2 + this.iv.length];
        System.arraycopy(this.iv, 0, bArr2, 0, this.iv.length);
        System.arraycopy(bArr, i, bArr2, this.iv.length, i2);
        if (i3 == 1) {
            this.engine.processBlock(bArr2, 0, bArr2, 0);
        } else {
            byte[] bArr3 = new byte[8 + this.iv.length];
            for (int i4 = 0; i4 != 6; i4++) {
                for (int i5 = 1; i5 <= i3; i5++) {
                    System.arraycopy(bArr2, 0, bArr3, 0, this.iv.length);
                    System.arraycopy(bArr2, 8 * i5, bArr3, this.iv.length, 8);
                    this.engine.processBlock(bArr3, 0, bArr3, 0);
                    int i6 = (i3 * i4) + i5;
                    int i7 = 1;
                    while (i6 != 0) {
                        int length = this.iv.length - i7;
                        bArr3[length] = (byte) (bArr3[length] ^ ((byte) i6));
                        i6 >>>= 8;
                        i7++;
                    }
                    System.arraycopy(bArr3, 0, bArr2, 0, 8);
                    System.arraycopy(bArr3, 8, bArr2, 8 * i5, 8);
                }
            }
        }
        return bArr2;
    }

    @Override // org.bouncycastle.crypto.Wrapper
    public byte[] unwrap(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("not set for unwrapping");
        }
        if (i2 < 16) {
            throw new InvalidCipherTextException("unwrap data too short");
        }
        int i3 = i2 / 8;
        if (i3 * 8 != i2) {
            throw new InvalidCipherTextException("unwrap data must be a multiple of 8 bytes");
        }
        this.engine.init(!this.wrapCipherMode, this.param);
        byte[] bArr2 = new byte[i2 - this.iv.length];
        byte[] bArr3 = new byte[this.iv.length];
        byte[] bArr4 = new byte[8 + this.iv.length];
        int i4 = i3 - 1;
        if (i4 == 1) {
            this.engine.processBlock(bArr, i, bArr4, 0);
            System.arraycopy(bArr4, 0, bArr3, 0, this.iv.length);
            System.arraycopy(bArr4, this.iv.length, bArr2, 0, 8);
        } else {
            System.arraycopy(bArr, i, bArr3, 0, this.iv.length);
            System.arraycopy(bArr, i + this.iv.length, bArr2, 0, i2 - this.iv.length);
            for (int i5 = 5; i5 >= 0; i5--) {
                for (int i6 = i4; i6 >= 1; i6--) {
                    System.arraycopy(bArr3, 0, bArr4, 0, this.iv.length);
                    System.arraycopy(bArr2, 8 * (i6 - 1), bArr4, this.iv.length, 8);
                    int i7 = (i4 * i5) + i6;
                    int i8 = 1;
                    while (i7 != 0) {
                        int length = this.iv.length - i8;
                        bArr4[length] = (byte) (bArr4[length] ^ ((byte) i7));
                        i7 >>>= 8;
                        i8++;
                    }
                    this.engine.processBlock(bArr4, 0, bArr4, 0);
                    System.arraycopy(bArr4, 0, bArr3, 0, 8);
                    System.arraycopy(bArr4, 8, bArr2, 8 * (i6 - 1), 8);
                }
            }
        }
        if (i4 != 1) {
            if (!Arrays.constantTimeAreEqual(bArr3, this.iv)) {
                throw new InvalidCipherTextException("checksum failed");
            }
        } else if (!Arrays.constantTimeAreEqual(bArr3, this.iv)) {
            System.arraycopy(bArr, i, bArr3, 0, this.iv.length);
            System.arraycopy(bArr, i + this.iv.length, bArr2, 0, i2 - this.iv.length);
            for (int i9 = 5; i9 >= 0; i9--) {
                System.arraycopy(bArr3, 0, bArr4, 0, this.iv.length);
                System.arraycopy(bArr2, 0, bArr4, this.iv.length, 8);
                int i10 = (i4 * i9) + 1;
                int i11 = 1;
                while (i10 != 0) {
                    int length2 = this.iv.length - i11;
                    bArr4[length2] = (byte) (bArr4[length2] ^ ((byte) i10));
                    i10 >>>= 8;
                    i11++;
                }
                this.engine.processBlock(bArr4, 0, bArr4, 0);
                System.arraycopy(bArr4, 0, bArr3, 0, 8);
                System.arraycopy(bArr4, 8, bArr2, 0, 8);
            }
            if (!Arrays.constantTimeAreEqual(bArr3, this.iv)) {
                throw new InvalidCipherTextException("checksum failed");
            }
        }
        return bArr2;
    }
}
