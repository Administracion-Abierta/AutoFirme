package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/RFC5649WrapEngine.class */
public class RFC5649WrapEngine implements Wrapper {
    private static final byte[] DEFAULT_IV = {-90, 89, 89, -90};
    private final BlockCipher engine;
    private final byte[] preIV = new byte[4];
    private KeyParameter param = null;
    private boolean forWrapping = true;

    public RFC5649WrapEngine(BlockCipher blockCipher) {
        this.engine = blockCipher;
    }

    @Override // org.bouncycastle.crypto.Wrapper
    public void init(boolean z, CipherParameters cipherParameters) {
        this.forWrapping = z;
        if (cipherParameters instanceof ParametersWithRandom) {
            cipherParameters = ((ParametersWithRandom) cipherParameters).getParameters();
        }
        if (cipherParameters instanceof KeyParameter) {
            this.param = (KeyParameter) cipherParameters;
            System.arraycopy(DEFAULT_IV, 0, this.preIV, 0, 4);
        } else if (cipherParameters instanceof ParametersWithIV) {
            ParametersWithIV parametersWithIV = (ParametersWithIV) cipherParameters;
            byte[] iv = parametersWithIV.getIV();
            if (iv.length != 4) {
                throw new IllegalArgumentException("IV length not equal to 4");
            }
            this.param = (KeyParameter) parametersWithIV.getParameters();
            System.arraycopy(iv, 0, this.preIV, 0, 4);
        }
    }

    @Override // org.bouncycastle.crypto.Wrapper
    public String getAlgorithmName() {
        return this.engine.getAlgorithmName();
    }

    private byte[] padPlaintext(byte[] bArr) {
        int length = bArr.length;
        int i = (8 - (length % 8)) % 8;
        byte[] bArr2 = new byte[length + i];
        System.arraycopy(bArr, 0, bArr2, 0, length);
        if (i != 0) {
            System.arraycopy(new byte[i], 0, bArr2, length, i);
        }
        return bArr2;
    }

    @Override // org.bouncycastle.crypto.Wrapper
    public byte[] wrap(byte[] bArr, int i, int i2) {
        if (!this.forWrapping) {
            throw new IllegalStateException("not set for wrapping");
        }
        byte[] bArr2 = new byte[8];
        System.arraycopy(this.preIV, 0, bArr2, 0, 4);
        Pack.intToBigEndian(i2, bArr2, 4);
        byte[] bArr3 = new byte[i2];
        System.arraycopy(bArr, i, bArr3, 0, i2);
        byte[] bArrPadPlaintext = padPlaintext(bArr3);
        if (bArrPadPlaintext.length != 8) {
            RFC3394WrapEngine rFC3394WrapEngine = new RFC3394WrapEngine(this.engine);
            rFC3394WrapEngine.init(true, new ParametersWithIV(this.param, bArr2));
            return rFC3394WrapEngine.wrap(bArrPadPlaintext, 0, bArrPadPlaintext.length);
        }
        byte[] bArr4 = new byte[bArrPadPlaintext.length + bArr2.length];
        System.arraycopy(bArr2, 0, bArr4, 0, bArr2.length);
        System.arraycopy(bArrPadPlaintext, 0, bArr4, bArr2.length, bArrPadPlaintext.length);
        this.engine.init(true, this.param);
        int blockSize = this.engine.getBlockSize();
        for (int i3 = 0; i3 < bArr4.length; i3 += blockSize) {
            this.engine.processBlock(bArr4, i3, bArr4, i3);
        }
        return bArr4;
    }

    @Override // org.bouncycastle.crypto.Wrapper
    public byte[] unwrap(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        byte[] bArrRfc3394UnwrapNoIvCheck;
        if (this.forWrapping) {
            throw new IllegalStateException("not set for unwrapping");
        }
        int i3 = i2 / 8;
        if (i3 * 8 != i2) {
            throw new InvalidCipherTextException("unwrap data must be a multiple of 8 bytes");
        }
        if (i3 <= 1) {
            throw new InvalidCipherTextException("unwrap data must be at least 16 bytes");
        }
        byte[] bArr2 = new byte[i2];
        System.arraycopy(bArr, i, bArr2, 0, i2);
        byte[] bArr3 = new byte[i2];
        byte[] bArr4 = new byte[8];
        if (i3 == 2) {
            this.engine.init(false, this.param);
            int blockSize = this.engine.getBlockSize();
            for (int i4 = 0; i4 < bArr2.length; i4 += blockSize) {
                this.engine.processBlock(bArr2, i4, bArr3, i4);
            }
            System.arraycopy(bArr3, 0, bArr4, 0, bArr4.length);
            bArrRfc3394UnwrapNoIvCheck = new byte[bArr3.length - bArr4.length];
            System.arraycopy(bArr3, bArr4.length, bArrRfc3394UnwrapNoIvCheck, 0, bArrRfc3394UnwrapNoIvCheck.length);
        } else {
            bArrRfc3394UnwrapNoIvCheck = rfc3394UnwrapNoIvCheck(bArr, i, i2, bArr4);
        }
        byte[] bArr5 = new byte[4];
        System.arraycopy(bArr4, 0, bArr5, 0, 4);
        int iBigEndianToInt = Pack.bigEndianToInt(bArr4, 4);
        boolean zConstantTimeAreEqual = Arrays.constantTimeAreEqual(bArr5, this.preIV);
        int length = bArrRfc3394UnwrapNoIvCheck.length;
        if (iBigEndianToInt <= length - 8) {
            zConstantTimeAreEqual = false;
        }
        if (iBigEndianToInt > length) {
            zConstantTimeAreEqual = false;
        }
        int i5 = length - iBigEndianToInt;
        if (i5 >= 8 || i5 < 0) {
            zConstantTimeAreEqual = false;
            i5 = 4;
        }
        byte[] bArr6 = new byte[i5];
        byte[] bArr7 = new byte[i5];
        System.arraycopy(bArrRfc3394UnwrapNoIvCheck, bArrRfc3394UnwrapNoIvCheck.length - i5, bArr7, 0, i5);
        if (!Arrays.constantTimeAreEqual(bArr7, bArr6)) {
            zConstantTimeAreEqual = false;
        }
        if (!zConstantTimeAreEqual) {
            throw new InvalidCipherTextException("checksum failed");
        }
        byte[] bArr8 = new byte[iBigEndianToInt];
        System.arraycopy(bArrRfc3394UnwrapNoIvCheck, 0, bArr8, 0, bArr8.length);
        return bArr8;
    }

    private byte[] rfc3394UnwrapNoIvCheck(byte[] bArr, int i, int i2, byte[] bArr2) {
        byte[] bArr3 = new byte[i2 - 8];
        byte[] bArr4 = new byte[16];
        System.arraycopy(bArr, i, bArr4, 0, 8);
        System.arraycopy(bArr, i + 8, bArr3, 0, i2 - 8);
        this.engine.init(false, this.param);
        int i3 = (i2 / 8) - 1;
        for (int i4 = 5; i4 >= 0; i4--) {
            for (int i5 = i3; i5 >= 1; i5--) {
                System.arraycopy(bArr3, 8 * (i5 - 1), bArr4, 8, 8);
                int i6 = (i3 * i4) + i5;
                int i7 = 1;
                while (i6 != 0) {
                    int i8 = 8 - i7;
                    bArr4[i8] = (byte) (bArr4[i8] ^ ((byte) i6));
                    i6 >>>= 8;
                    i7++;
                }
                this.engine.processBlock(bArr4, 0, bArr4, 0);
                System.arraycopy(bArr4, 8, bArr3, 8 * (i5 - 1), 8);
            }
        }
        System.arraycopy(bArr4, 0, bArr2, 0, 8);
        return bArr3;
    }
}
