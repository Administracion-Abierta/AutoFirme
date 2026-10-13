package org.bouncycastle.crypto.engines;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/DESedeWrapEngine.class */
public class DESedeWrapEngine implements Wrapper {
    private CBCBlockCipher engine;
    private KeyParameter param;
    private ParametersWithIV paramPlusIV;
    private byte[] iv;
    private boolean forWrapping;
    private static final byte[] IV2 = {74, -35, -94, 44, 121, -24, 33, 5};
    Digest sha1 = DigestFactory.createSHA1();
    byte[] digest = new byte[20];

    @Override // org.bouncycastle.crypto.Wrapper
    public void init(boolean z, CipherParameters cipherParameters) {
        SecureRandom secureRandom;
        this.forWrapping = z;
        this.engine = new CBCBlockCipher(new DESedeEngine());
        if (cipherParameters instanceof ParametersWithRandom) {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom) cipherParameters;
            cipherParameters = parametersWithRandom.getParameters();
            secureRandom = parametersWithRandom.getRandom();
        } else {
            secureRandom = CryptoServicesRegistrar.getSecureRandom();
        }
        if (cipherParameters instanceof KeyParameter) {
            this.param = (KeyParameter) cipherParameters;
            if (this.forWrapping) {
                this.iv = new byte[8];
                secureRandom.nextBytes(this.iv);
                this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
                return;
            }
            return;
        }
        if (cipherParameters instanceof ParametersWithIV) {
            this.paramPlusIV = (ParametersWithIV) cipherParameters;
            this.iv = this.paramPlusIV.getIV();
            this.param = (KeyParameter) this.paramPlusIV.getParameters();
            if (!this.forWrapping) {
                throw new IllegalArgumentException("You should not supply an IV for unwrapping");
            }
            if (this.iv == null || this.iv.length != 8) {
                throw new IllegalArgumentException("IV is not 8 octets");
            }
        }
    }

    @Override // org.bouncycastle.crypto.Wrapper
    public String getAlgorithmName() {
        return "DESede";
    }

    @Override // org.bouncycastle.crypto.Wrapper
    public byte[] wrap(byte[] bArr, int i, int i2) {
        if (!this.forWrapping) {
            throw new IllegalStateException("Not initialized for wrapping");
        }
        byte[] bArr2 = new byte[i2];
        System.arraycopy(bArr, i, bArr2, 0, i2);
        byte[] bArrCalculateCMSKeyChecksum = calculateCMSKeyChecksum(bArr2);
        byte[] bArr3 = new byte[bArr2.length + bArrCalculateCMSKeyChecksum.length];
        System.arraycopy(bArr2, 0, bArr3, 0, bArr2.length);
        System.arraycopy(bArrCalculateCMSKeyChecksum, 0, bArr3, bArr2.length, bArrCalculateCMSKeyChecksum.length);
        int blockSize = this.engine.getBlockSize();
        if (bArr3.length % blockSize != 0) {
            throw new IllegalStateException("Not multiple of block length");
        }
        this.engine.init(true, this.paramPlusIV);
        byte[] bArr4 = new byte[bArr3.length];
        int i3 = 0;
        while (true) {
            int i4 = i3;
            if (i4 == bArr3.length) {
                break;
            }
            this.engine.processBlock(bArr3, i4, bArr4, i4);
            i3 = i4 + blockSize;
        }
        byte[] bArr5 = new byte[this.iv.length + bArr4.length];
        System.arraycopy(this.iv, 0, bArr5, 0, this.iv.length);
        System.arraycopy(bArr4, 0, bArr5, this.iv.length, bArr4.length);
        Arrays.reverseInPlace(bArr5);
        this.engine.init(true, new ParametersWithIV(this.param, IV2));
        int i5 = 0;
        while (true) {
            int i6 = i5;
            if (i6 == bArr5.length) {
                return bArr5;
            }
            this.engine.processBlock(bArr5, i6, bArr5, i6);
            i5 = i6 + blockSize;
        }
    }

    @Override // org.bouncycastle.crypto.Wrapper
    public byte[] unwrap(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("Not set for unwrapping");
        }
        if (bArr == null) {
            throw new InvalidCipherTextException("Null pointer as ciphertext");
        }
        int blockSize = this.engine.getBlockSize();
        if (i2 % blockSize != 0) {
            throw new InvalidCipherTextException("Ciphertext not multiple of " + blockSize);
        }
        this.engine.init(false, new ParametersWithIV(this.param, IV2));
        byte[] bArr2 = new byte[i2];
        int i3 = 0;
        while (true) {
            int i4 = i3;
            if (i4 == i2) {
                break;
            }
            this.engine.processBlock(bArr, i + i4, bArr2, i4);
            i3 = i4 + blockSize;
        }
        Arrays.reverseInPlace(bArr2);
        this.iv = new byte[8];
        byte[] bArr3 = new byte[bArr2.length - 8];
        System.arraycopy(bArr2, 0, this.iv, 0, 8);
        System.arraycopy(bArr2, 8, bArr3, 0, bArr2.length - 8);
        this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
        this.engine.init(false, this.paramPlusIV);
        byte[] bArr4 = new byte[bArr3.length];
        int i5 = 0;
        while (true) {
            int i6 = i5;
            if (i6 == bArr4.length) {
                break;
            }
            this.engine.processBlock(bArr3, i6, bArr4, i6);
            i5 = i6 + blockSize;
        }
        byte[] bArr5 = new byte[bArr4.length - 8];
        byte[] bArr6 = new byte[8];
        System.arraycopy(bArr4, 0, bArr5, 0, bArr4.length - 8);
        System.arraycopy(bArr4, bArr4.length - 8, bArr6, 0, 8);
        if (checkCMSKeyChecksum(bArr5, bArr6)) {
            return bArr5;
        }
        throw new InvalidCipherTextException("Checksum inside ciphertext is corrupted");
    }

    private byte[] calculateCMSKeyChecksum(byte[] bArr) {
        byte[] bArr2 = new byte[8];
        this.sha1.update(bArr, 0, bArr.length);
        this.sha1.doFinal(this.digest, 0);
        System.arraycopy(this.digest, 0, bArr2, 0, 8);
        return bArr2;
    }

    private boolean checkCMSKeyChecksum(byte[] bArr, byte[] bArr2) {
        return Arrays.constantTimeAreEqual(calculateCMSKeyChecksum(bArr), bArr2);
    }
}
