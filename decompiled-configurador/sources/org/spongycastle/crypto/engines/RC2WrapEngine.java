package org.spongycastle.crypto.engines;

import java.security.SecureRandom;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.Wrapper;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.util.DigestFactory;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/engines/RC2WrapEngine.class */
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

    @Override // org.spongycastle.crypto.Wrapper
    public void init(boolean forWrapping, CipherParameters param) {
        this.forWrapping = forWrapping;
        this.engine = new CBCBlockCipher(new RC2Engine());
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom pWithR = (ParametersWithRandom) param;
            this.sr = pWithR.getRandom();
            param = pWithR.getParameters();
        } else {
            this.sr = new SecureRandom();
        }
        if (param instanceof ParametersWithIV) {
            this.paramPlusIV = (ParametersWithIV) param;
            this.iv = this.paramPlusIV.getIV();
            this.param = this.paramPlusIV.getParameters();
            if (this.forWrapping) {
                if (this.iv == null || this.iv.length != 8) {
                    throw new IllegalArgumentException("IV is not 8 octets");
                }
                return;
            }
            throw new IllegalArgumentException("You should not supply an IV for unwrapping");
        }
        this.param = param;
        if (this.forWrapping) {
            this.iv = new byte[8];
            this.sr.nextBytes(this.iv);
            this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
        }
    }

    @Override // org.spongycastle.crypto.Wrapper
    public String getAlgorithmName() {
        return "RC2";
    }

    @Override // org.spongycastle.crypto.Wrapper
    public byte[] wrap(byte[] in, int inOff, int inLen) {
        if (!this.forWrapping) {
            throw new IllegalStateException("Not initialized for wrapping");
        }
        int length = inLen + 1;
        if (length % 8 != 0) {
            length += 8 - (length % 8);
        }
        byte[] keyToBeWrapped = new byte[length];
        keyToBeWrapped[0] = (byte) inLen;
        System.arraycopy(in, inOff, keyToBeWrapped, 1, inLen);
        byte[] pad = new byte[(keyToBeWrapped.length - inLen) - 1];
        if (pad.length > 0) {
            this.sr.nextBytes(pad);
            System.arraycopy(pad, 0, keyToBeWrapped, inLen + 1, pad.length);
        }
        byte[] CKS = calculateCMSKeyChecksum(keyToBeWrapped);
        byte[] WKCKS = new byte[keyToBeWrapped.length + CKS.length];
        System.arraycopy(keyToBeWrapped, 0, WKCKS, 0, keyToBeWrapped.length);
        System.arraycopy(CKS, 0, WKCKS, keyToBeWrapped.length, CKS.length);
        byte[] TEMP1 = new byte[WKCKS.length];
        System.arraycopy(WKCKS, 0, TEMP1, 0, WKCKS.length);
        int noOfBlocks = WKCKS.length / this.engine.getBlockSize();
        int extraBytes = WKCKS.length % this.engine.getBlockSize();
        if (extraBytes != 0) {
            throw new IllegalStateException("Not multiple of block length");
        }
        this.engine.init(true, this.paramPlusIV);
        for (int i = 0; i < noOfBlocks; i++) {
            int currentBytePos = i * this.engine.getBlockSize();
            this.engine.processBlock(TEMP1, currentBytePos, TEMP1, currentBytePos);
        }
        byte[] TEMP2 = new byte[this.iv.length + TEMP1.length];
        System.arraycopy(this.iv, 0, TEMP2, 0, this.iv.length);
        System.arraycopy(TEMP1, 0, TEMP2, this.iv.length, TEMP1.length);
        byte[] TEMP3 = new byte[TEMP2.length];
        for (int i2 = 0; i2 < TEMP2.length; i2++) {
            TEMP3[i2] = TEMP2[TEMP2.length - (i2 + 1)];
        }
        ParametersWithIV param2 = new ParametersWithIV(this.param, IV2);
        this.engine.init(true, param2);
        for (int i3 = 0; i3 < noOfBlocks + 1; i3++) {
            int currentBytePos2 = i3 * this.engine.getBlockSize();
            this.engine.processBlock(TEMP3, currentBytePos2, TEMP3, currentBytePos2);
        }
        return TEMP3;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.spongycastle.crypto.Wrapper
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

    private byte[] calculateCMSKeyChecksum(byte[] key) {
        byte[] result = new byte[8];
        this.sha1.update(key, 0, key.length);
        this.sha1.doFinal(this.digest, 0);
        System.arraycopy(this.digest, 0, result, 0, 8);
        return result;
    }

    private boolean checkCMSKeyChecksum(byte[] key, byte[] checksum) {
        return Arrays.constantTimeAreEqual(calculateCMSKeyChecksum(key), checksum);
    }
}
