package org.bouncycastle.crypto.engines;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/RC2WrapEngine.class */
public class RC2WrapEngine implements Wrapper {
    private CBCBlockCipher engine;
    private CipherParameters param;
    private ParametersWithIV paramPlusIV;
    private byte[] iv;
    private boolean forWrapping;
    private SecureRandom sr;
    private static final byte[] IV2 = {74, -35, -94, 44, 121, -24, 33, 5};
    Digest sha1 = DigestFactory.createSHA1();
    byte[] digest = new byte[20];

    @Override // org.bouncycastle.crypto.Wrapper
    public void init(boolean z, CipherParameters cipherParameters) {
        this.forWrapping = z;
        this.engine = new CBCBlockCipher(new RC2Engine());
        if (cipherParameters instanceof ParametersWithRandom) {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom) cipherParameters;
            this.sr = parametersWithRandom.getRandom();
            cipherParameters = parametersWithRandom.getParameters();
        } else {
            this.sr = CryptoServicesRegistrar.getSecureRandom();
        }
        if (!(cipherParameters instanceof ParametersWithIV)) {
            this.param = cipherParameters;
            if (this.forWrapping) {
                this.iv = new byte[8];
                this.sr.nextBytes(this.iv);
                this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
                return;
            }
            return;
        }
        this.paramPlusIV = (ParametersWithIV) cipherParameters;
        this.iv = this.paramPlusIV.getIV();
        this.param = this.paramPlusIV.getParameters();
        if (!this.forWrapping) {
            throw new IllegalArgumentException("You should not supply an IV for unwrapping");
        }
        if (this.iv == null || this.iv.length != 8) {
            throw new IllegalArgumentException("IV is not 8 octets");
        }
    }

    @Override // org.bouncycastle.crypto.Wrapper
    public String getAlgorithmName() {
        return "RC2";
    }

    @Override // org.bouncycastle.crypto.Wrapper
    public byte[] wrap(byte[] bArr, int i, int i2) {
        if (!this.forWrapping) {
            throw new IllegalStateException("Not initialized for wrapping");
        }
        int i3 = i2 + 1;
        if (i3 % 8 != 0) {
            i3 += 8 - (i3 % 8);
        }
        byte[] bArr2 = new byte[i3];
        bArr2[0] = (byte) i2;
        System.arraycopy(bArr, i, bArr2, 1, i2);
        byte[] bArr3 = new byte[(bArr2.length - i2) - 1];
        if (bArr3.length > 0) {
            this.sr.nextBytes(bArr3);
            System.arraycopy(bArr3, 0, bArr2, i2 + 1, bArr3.length);
        }
        byte[] bArrCalculateCMSKeyChecksum = calculateCMSKeyChecksum(bArr2);
        byte[] bArr4 = new byte[bArr2.length + bArrCalculateCMSKeyChecksum.length];
        System.arraycopy(bArr2, 0, bArr4, 0, bArr2.length);
        System.arraycopy(bArrCalculateCMSKeyChecksum, 0, bArr4, bArr2.length, bArrCalculateCMSKeyChecksum.length);
        byte[] bArr5 = new byte[bArr4.length];
        System.arraycopy(bArr4, 0, bArr5, 0, bArr4.length);
        int length = bArr4.length / this.engine.getBlockSize();
        if (bArr4.length % this.engine.getBlockSize() != 0) {
            throw new IllegalStateException("Not multiple of block length");
        }
        this.engine.init(true, this.paramPlusIV);
        for (int i4 = 0; i4 < length; i4++) {
            int blockSize = i4 * this.engine.getBlockSize();
            this.engine.processBlock(bArr5, blockSize, bArr5, blockSize);
        }
        byte[] bArr6 = new byte[this.iv.length + bArr5.length];
        System.arraycopy(this.iv, 0, bArr6, 0, this.iv.length);
        System.arraycopy(bArr5, 0, bArr6, this.iv.length, bArr5.length);
        byte[] bArr7 = new byte[bArr6.length];
        for (int i5 = 0; i5 < bArr6.length; i5++) {
            bArr7[i5] = bArr6[bArr6.length - (i5 + 1)];
        }
        this.engine.init(true, new ParametersWithIV(this.param, IV2));
        for (int i6 = 0; i6 < length + 1; i6++) {
            int blockSize2 = i6 * this.engine.getBlockSize();
            this.engine.processBlock(bArr7, blockSize2, bArr7, blockSize2);
        }
        return bArr7;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.bouncycastle.crypto.Wrapper
    public byte[] unwrap(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("Not set for unwrapping");
        }
        if (bArr == null) {
            throw new InvalidCipherTextException("Null pointer as ciphertext");
        }
        if (i2 % this.engine.getBlockSize() != 0) {
            throw new InvalidCipherTextException("Ciphertext not multiple of " + this.engine.getBlockSize());
        }
        this.engine.init(false, new ParametersWithIV(this.param, IV2));
        byte[] bArr2 = new byte[i2];
        System.arraycopy(bArr, i, bArr2, 0, i2);
        for (int i3 = 0; i3 < bArr2.length / this.engine.getBlockSize(); i3++) {
            int blockSize = i3 * this.engine.getBlockSize();
            this.engine.processBlock(bArr2, blockSize, bArr2, blockSize);
        }
        byte[] bArr3 = new byte[bArr2.length];
        for (int i4 = 0; i4 < bArr2.length; i4++) {
            bArr3[i4] = bArr2[bArr2.length - (i4 + 1)];
        }
        this.iv = new byte[8];
        byte[] bArr4 = new byte[bArr3.length - 8];
        System.arraycopy(bArr3, 0, this.iv, 0, 8);
        System.arraycopy(bArr3, 8, bArr4, 0, bArr3.length - 8);
        this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
        this.engine.init(false, this.paramPlusIV);
        byte[] bArr5 = new byte[bArr4.length];
        System.arraycopy(bArr4, 0, bArr5, 0, bArr4.length);
        for (int i5 = 0; i5 < bArr5.length / this.engine.getBlockSize(); i5++) {
            int blockSize2 = i5 * this.engine.getBlockSize();
            this.engine.processBlock(bArr5, blockSize2, bArr5, blockSize2);
        }
        byte[] bArr6 = new byte[bArr5.length - 8];
        byte[] bArr7 = new byte[8];
        System.arraycopy(bArr5, 0, bArr6, 0, bArr5.length - 8);
        System.arraycopy(bArr5, bArr5.length - 8, bArr7, 0, 8);
        if (!checkCMSKeyChecksum(bArr6, bArr7)) {
            throw new InvalidCipherTextException("Checksum inside ciphertext is corrupted");
        }
        if (bArr6.length - (((bArr6[0] ? 1 : 0) & 255) + 1) > 7) {
            throw new InvalidCipherTextException("too many pad bytes (" + (bArr6.length - (((bArr6[0] ? 1 : 0) & 255) + 1)) + ")");
        }
        byte[] bArr8 = new byte[bArr6[0]];
        System.arraycopy(bArr6, 1, bArr8, 0, bArr8.length);
        return bArr8;
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
