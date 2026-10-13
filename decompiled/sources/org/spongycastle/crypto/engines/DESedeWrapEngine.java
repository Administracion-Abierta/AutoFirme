package org.spongycastle.crypto.engines;

import java.security.SecureRandom;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.Wrapper;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.util.DigestFactory;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/DESedeWrapEngine.class */
public class DESedeWrapEngine implements Wrapper {
    private CBCBlockCipher engine;
    private KeyParameter param;
    private ParametersWithIV paramPlusIV;
    private byte[] iv;
    private boolean forWrapping;
    private static final byte[] IV2 = {74, -35, -94, 44, 121, -24, 33, 5};
    Digest sha1 = DigestFactory.createSHA1();
    byte[] digest = new byte[20];

    @Override // org.spongycastle.crypto.Wrapper
    public void init(boolean forWrapping, CipherParameters param) {
        SecureRandom sr;
        this.forWrapping = forWrapping;
        this.engine = new CBCBlockCipher(new DESedeEngine());
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom pr = (ParametersWithRandom) param;
            param = pr.getParameters();
            sr = pr.getRandom();
        } else {
            sr = new SecureRandom();
        }
        if (param instanceof KeyParameter) {
            this.param = (KeyParameter) param;
            if (this.forWrapping) {
                this.iv = new byte[8];
                sr.nextBytes(this.iv);
                this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
                return;
            }
            return;
        }
        if (param instanceof ParametersWithIV) {
            this.paramPlusIV = (ParametersWithIV) param;
            this.iv = this.paramPlusIV.getIV();
            this.param = (KeyParameter) this.paramPlusIV.getParameters();
            if (this.forWrapping) {
                if (this.iv == null || this.iv.length != 8) {
                    throw new IllegalArgumentException("IV is not 8 octets");
                }
                return;
            }
            throw new IllegalArgumentException("You should not supply an IV for unwrapping");
        }
    }

    @Override // org.spongycastle.crypto.Wrapper
    public String getAlgorithmName() {
        return "DESede";
    }

    @Override // org.spongycastle.crypto.Wrapper
    public byte[] wrap(byte[] in, int inOff, int inLen) {
        if (!this.forWrapping) {
            throw new IllegalStateException("Not initialized for wrapping");
        }
        byte[] keyToBeWrapped = new byte[inLen];
        System.arraycopy(in, inOff, keyToBeWrapped, 0, inLen);
        byte[] CKS = calculateCMSKeyChecksum(keyToBeWrapped);
        byte[] WKCKS = new byte[keyToBeWrapped.length + CKS.length];
        System.arraycopy(keyToBeWrapped, 0, WKCKS, 0, keyToBeWrapped.length);
        System.arraycopy(CKS, 0, WKCKS, keyToBeWrapped.length, CKS.length);
        int blockSize = this.engine.getBlockSize();
        if (WKCKS.length % blockSize != 0) {
            throw new IllegalStateException("Not multiple of block length");
        }
        this.engine.init(true, this.paramPlusIV);
        byte[] TEMP1 = new byte[WKCKS.length];
        int i = 0;
        while (true) {
            int currentBytePos = i;
            if (currentBytePos == WKCKS.length) {
                break;
            }
            this.engine.processBlock(WKCKS, currentBytePos, TEMP1, currentBytePos);
            i = currentBytePos + blockSize;
        }
        byte[] TEMP2 = new byte[this.iv.length + TEMP1.length];
        System.arraycopy(this.iv, 0, TEMP2, 0, this.iv.length);
        System.arraycopy(TEMP1, 0, TEMP2, this.iv.length, TEMP1.length);
        byte[] TEMP3 = reverse(TEMP2);
        ParametersWithIV param2 = new ParametersWithIV(this.param, IV2);
        this.engine.init(true, param2);
        int i2 = 0;
        while (true) {
            int currentBytePos2 = i2;
            if (currentBytePos2 != TEMP3.length) {
                this.engine.processBlock(TEMP3, currentBytePos2, TEMP3, currentBytePos2);
                i2 = currentBytePos2 + blockSize;
            } else {
                return TEMP3;
            }
        }
    }

    @Override // org.spongycastle.crypto.Wrapper
    public byte[] unwrap(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("Not set for unwrapping");
        }
        if (in == null) {
            throw new InvalidCipherTextException("Null pointer as ciphertext");
        }
        int blockSize = this.engine.getBlockSize();
        if (inLen % blockSize != 0) {
            throw new InvalidCipherTextException("Ciphertext not multiple of " + blockSize);
        }
        ParametersWithIV param2 = new ParametersWithIV(this.param, IV2);
        this.engine.init(false, param2);
        byte[] TEMP3 = new byte[inLen];
        int i = 0;
        while (true) {
            int currentBytePos = i;
            if (currentBytePos == inLen) {
                break;
            }
            this.engine.processBlock(in, inOff + currentBytePos, TEMP3, currentBytePos);
            i = currentBytePos + blockSize;
        }
        byte[] TEMP2 = reverse(TEMP3);
        this.iv = new byte[8];
        byte[] TEMP1 = new byte[TEMP2.length - 8];
        System.arraycopy(TEMP2, 0, this.iv, 0, 8);
        System.arraycopy(TEMP2, 8, TEMP1, 0, TEMP2.length - 8);
        this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
        this.engine.init(false, this.paramPlusIV);
        byte[] WKCKS = new byte[TEMP1.length];
        int i2 = 0;
        while (true) {
            int currentBytePos2 = i2;
            if (currentBytePos2 == WKCKS.length) {
                break;
            }
            this.engine.processBlock(TEMP1, currentBytePos2, WKCKS, currentBytePos2);
            i2 = currentBytePos2 + blockSize;
        }
        byte[] result = new byte[WKCKS.length - 8];
        byte[] CKStoBeVerified = new byte[8];
        System.arraycopy(WKCKS, 0, result, 0, WKCKS.length - 8);
        System.arraycopy(WKCKS, WKCKS.length - 8, CKStoBeVerified, 0, 8);
        if (!checkCMSKeyChecksum(result, CKStoBeVerified)) {
            throw new InvalidCipherTextException("Checksum inside ciphertext is corrupted");
        }
        return result;
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

    private static byte[] reverse(byte[] bs) {
        byte[] result = new byte[bs.length];
        for (int i = 0; i < bs.length; i++) {
            result[i] = bs[bs.length - (i + 1)];
        }
        return result;
    }
}
