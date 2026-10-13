package org.bouncycastle.crypto.engines;

import java.security.SecureRandom;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/RFC3211WrapEngine.class */
public class RFC3211WrapEngine implements Wrapper {
    private CBCBlockCipher engine;
    private ParametersWithIV param;
    private boolean forWrapping;
    private SecureRandom rand;

    public RFC3211WrapEngine(BlockCipher blockCipher) {
        this.engine = new CBCBlockCipher(blockCipher);
    }

    @Override // org.bouncycastle.crypto.Wrapper
    public void init(boolean z, CipherParameters cipherParameters) {
        this.forWrapping = z;
        if (cipherParameters instanceof ParametersWithRandom) {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom) cipherParameters;
            this.rand = parametersWithRandom.getRandom();
            if (!(parametersWithRandom.getParameters() instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("RFC3211Wrap requires an IV");
            }
            this.param = (ParametersWithIV) parametersWithRandom.getParameters();
            return;
        }
        if (z) {
            this.rand = CryptoServicesRegistrar.getSecureRandom();
        }
        if (!(cipherParameters instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("RFC3211Wrap requires an IV");
        }
        this.param = (ParametersWithIV) cipherParameters;
    }

    @Override // org.bouncycastle.crypto.Wrapper
    public String getAlgorithmName() {
        return this.engine.getUnderlyingCipher().getAlgorithmName() + "/RFC3211Wrap";
    }

    @Override // org.bouncycastle.crypto.Wrapper
    public byte[] wrap(byte[] bArr, int i, int i2) {
        byte[] bArr2;
        if (!this.forWrapping) {
            throw new IllegalStateException("not set for wrapping");
        }
        if (i2 > 255 || i2 < 0) {
            throw new IllegalArgumentException("input must be from 0 to 255 bytes");
        }
        this.engine.init(true, this.param);
        int blockSize = this.engine.getBlockSize();
        if (i2 + 4 < blockSize * 2) {
            bArr2 = new byte[blockSize * 2];
        } else {
            bArr2 = new byte[(i2 + 4) % blockSize == 0 ? i2 + 4 : (((i2 + 4) / blockSize) + 1) * blockSize];
        }
        bArr2[0] = (byte) i2;
        System.arraycopy(bArr, i, bArr2, 4, i2);
        byte[] bArr3 = new byte[bArr2.length - (i2 + 4)];
        this.rand.nextBytes(bArr3);
        System.arraycopy(bArr3, 0, bArr2, i2 + 4, bArr3.length);
        bArr2[1] = (byte) (bArr2[4] ^ (-1));
        bArr2[2] = (byte) (bArr2[5] ^ (-1));
        bArr2[3] = (byte) (bArr2[6] ^ (-1));
        int i3 = 0;
        while (true) {
            int i4 = i3;
            if (i4 >= bArr2.length) {
                break;
            }
            this.engine.processBlock(bArr2, i4, bArr2, i4);
            i3 = i4 + blockSize;
        }
        int i5 = 0;
        while (true) {
            int i6 = i5;
            if (i6 >= bArr2.length) {
                return bArr2;
            }
            this.engine.processBlock(bArr2, i6, bArr2, i6);
            i5 = i6 + blockSize;
        }
    }

    @Override // org.bouncycastle.crypto.Wrapper
    public byte[] unwrap(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("not set for unwrapping");
        }
        int blockSize = this.engine.getBlockSize();
        if (i2 < 2 * blockSize) {
            throw new InvalidCipherTextException("input too short");
        }
        byte[] bArr2 = new byte[i2];
        byte[] bArr3 = new byte[blockSize];
        System.arraycopy(bArr, i, bArr2, 0, i2);
        System.arraycopy(bArr, i, bArr3, 0, bArr3.length);
        this.engine.init(false, new ParametersWithIV(this.param.getParameters(), bArr3));
        int i3 = blockSize;
        while (true) {
            int i4 = i3;
            if (i4 >= bArr2.length) {
                break;
            }
            this.engine.processBlock(bArr2, i4, bArr2, i4);
            i3 = i4 + blockSize;
        }
        System.arraycopy(bArr2, bArr2.length - bArr3.length, bArr3, 0, bArr3.length);
        this.engine.init(false, new ParametersWithIV(this.param.getParameters(), bArr3));
        this.engine.processBlock(bArr2, 0, bArr2, 0);
        this.engine.init(false, this.param);
        int i5 = 0;
        while (true) {
            int i6 = i5;
            if (i6 >= bArr2.length) {
                break;
            }
            this.engine.processBlock(bArr2, i6, bArr2, i6);
            i5 = i6 + blockSize;
        }
        boolean z = (bArr2[0] & 255) > bArr2.length - 4;
        byte[] bArr4 = z ? new byte[bArr2.length - 4] : new byte[bArr2[0] & 255];
        System.arraycopy(bArr2, 4, bArr4, 0, bArr4.length);
        int i7 = 0;
        for (int i8 = 0; i8 != 3; i8++) {
            i7 |= ((byte) (bArr2[1 + i8] ^ (-1))) ^ bArr2[4 + i8];
        }
        Arrays.clear(bArr2);
        if ((i7 != 0) || z) {
            throw new InvalidCipherTextException("wrapped key corrupted");
        }
        return bArr4;
    }
}
