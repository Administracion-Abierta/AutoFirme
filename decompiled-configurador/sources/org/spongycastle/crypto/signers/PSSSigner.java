package org.spongycastle.crypto.signers;

import java.security.SecureRandom;
import org.spongycastle.crypto.AsymmetricBlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Signer;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.params.RSABlindingParameters;
import org.spongycastle.crypto.params.RSAKeyParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/signers/PSSSigner.class */
public class PSSSigner implements Signer {
    public static final byte TRAILER_IMPLICIT = -68;
    private Digest contentDigest;
    private Digest mgfDigest;
    private AsymmetricBlockCipher cipher;
    private SecureRandom random;
    private int hLen;
    private int mgfhLen;
    private boolean sSet;
    private int sLen;
    private int emBits;
    private byte[] salt;
    private byte[] mDash;
    private byte[] block;
    private byte trailer;

    public PSSSigner(AsymmetricBlockCipher cipher, Digest digest, int sLen) {
        this(cipher, digest, sLen, (byte) -68);
    }

    public PSSSigner(AsymmetricBlockCipher cipher, Digest contentDigest, Digest mgfDigest, int sLen) {
        this(cipher, contentDigest, mgfDigest, sLen, (byte) -68);
    }

    public PSSSigner(AsymmetricBlockCipher cipher, Digest digest, int sLen, byte trailer) {
        this(cipher, digest, digest, sLen, trailer);
    }

    public PSSSigner(AsymmetricBlockCipher cipher, Digest contentDigest, Digest mgfDigest, int sLen, byte trailer) {
        this.cipher = cipher;
        this.contentDigest = contentDigest;
        this.mgfDigest = mgfDigest;
        this.hLen = contentDigest.getDigestSize();
        this.mgfhLen = mgfDigest.getDigestSize();
        this.sSet = false;
        this.sLen = sLen;
        this.salt = new byte[sLen];
        this.mDash = new byte[8 + sLen + this.hLen];
        this.trailer = trailer;
    }

    public PSSSigner(AsymmetricBlockCipher cipher, Digest digest, byte[] salt) {
        this(cipher, digest, digest, salt, (byte) -68);
    }

    public PSSSigner(AsymmetricBlockCipher cipher, Digest contentDigest, Digest mgfDigest, byte[] salt) {
        this(cipher, contentDigest, mgfDigest, salt, (byte) -68);
    }

    public PSSSigner(AsymmetricBlockCipher cipher, Digest contentDigest, Digest mgfDigest, byte[] salt, byte trailer) {
        this.cipher = cipher;
        this.contentDigest = contentDigest;
        this.mgfDigest = mgfDigest;
        this.hLen = contentDigest.getDigestSize();
        this.mgfhLen = mgfDigest.getDigestSize();
        this.sSet = true;
        this.sLen = salt.length;
        this.salt = salt;
        this.mDash = new byte[8 + this.sLen + this.hLen];
        this.trailer = trailer;
    }

    @Override // org.spongycastle.crypto.Signer
    public void init(boolean forSigning, CipherParameters param) {
        CipherParameters params;
        RSAKeyParameters kParam;
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom p = (ParametersWithRandom) param;
            params = p.getParameters();
            this.random = p.getRandom();
        } else {
            params = param;
            if (forSigning) {
                this.random = new SecureRandom();
            }
        }
        if (params instanceof RSABlindingParameters) {
            kParam = ((RSABlindingParameters) params).getPublicKey();
            this.cipher.init(forSigning, param);
        } else {
            kParam = (RSAKeyParameters) params;
            this.cipher.init(forSigning, params);
        }
        this.emBits = kParam.getModulus().bitLength() - 1;
        if (this.emBits < (8 * this.hLen) + (8 * this.sLen) + 9) {
            throw new IllegalArgumentException("key too small for specified hash and salt lengths");
        }
        this.block = new byte[(this.emBits + 7) / 8];
        reset();
    }

    private void clearBlock(byte[] block) {
        for (int i = 0; i != block.length; i++) {
            block[i] = 0;
        }
    }

    @Override // org.spongycastle.crypto.Signer
    public void update(byte b) {
        this.contentDigest.update(b);
    }

    @Override // org.spongycastle.crypto.Signer
    public void update(byte[] in, int off, int len) {
        this.contentDigest.update(in, off, len);
    }

    @Override // org.spongycastle.crypto.Signer
    public void reset() {
        this.contentDigest.reset();
    }

    @Override // org.spongycastle.crypto.Signer
    public byte[] generateSignature() throws DataLengthException, CryptoException {
        this.contentDigest.doFinal(this.mDash, (this.mDash.length - this.hLen) - this.sLen);
        if (this.sLen != 0) {
            if (!this.sSet) {
                this.random.nextBytes(this.salt);
            }
            System.arraycopy(this.salt, 0, this.mDash, this.mDash.length - this.sLen, this.sLen);
        }
        byte[] h = new byte[this.hLen];
        this.contentDigest.update(this.mDash, 0, this.mDash.length);
        this.contentDigest.doFinal(h, 0);
        this.block[(((this.block.length - this.sLen) - 1) - this.hLen) - 1] = 1;
        System.arraycopy(this.salt, 0, this.block, ((this.block.length - this.sLen) - this.hLen) - 1, this.sLen);
        byte[] dbMask = maskGeneratorFunction1(h, 0, h.length, (this.block.length - this.hLen) - 1);
        for (int i = 0; i != dbMask.length; i++) {
            byte[] bArr = this.block;
            int i2 = i;
            bArr[i2] = (byte) (bArr[i2] ^ dbMask[i]);
        }
        byte[] bArr2 = this.block;
        bArr2[0] = (byte) (bArr2[0] & (255 >> ((this.block.length * 8) - this.emBits)));
        System.arraycopy(h, 0, this.block, (this.block.length - this.hLen) - 1, this.hLen);
        this.block[this.block.length - 1] = this.trailer;
        byte[] b = this.cipher.processBlock(this.block, 0, this.block.length);
        clearBlock(this.block);
        return b;
    }

    @Override // org.spongycastle.crypto.Signer
    public boolean verifySignature(byte[] signature) {
        this.contentDigest.doFinal(this.mDash, (this.mDash.length - this.hLen) - this.sLen);
        try {
            byte[] b = this.cipher.processBlock(signature, 0, signature.length);
            System.arraycopy(b, 0, this.block, this.block.length - b.length, b.length);
            if (this.block[this.block.length - 1] != this.trailer) {
                clearBlock(this.block);
                return false;
            }
            byte[] dbMask = maskGeneratorFunction1(this.block, (this.block.length - this.hLen) - 1, this.hLen, (this.block.length - this.hLen) - 1);
            for (int i = 0; i != dbMask.length; i++) {
                byte[] bArr = this.block;
                int i2 = i;
                bArr[i2] = (byte) (bArr[i2] ^ dbMask[i]);
            }
            byte[] bArr2 = this.block;
            bArr2[0] = (byte) (bArr2[0] & (255 >> ((this.block.length * 8) - this.emBits)));
            for (int i3 = 0; i3 != ((this.block.length - this.hLen) - this.sLen) - 2; i3++) {
                if (this.block[i3] != 0) {
                    clearBlock(this.block);
                    return false;
                }
            }
            if (this.block[((this.block.length - this.hLen) - this.sLen) - 2] != 1) {
                clearBlock(this.block);
                return false;
            }
            if (this.sSet) {
                System.arraycopy(this.salt, 0, this.mDash, this.mDash.length - this.sLen, this.sLen);
            } else {
                System.arraycopy(this.block, ((this.block.length - this.sLen) - this.hLen) - 1, this.mDash, this.mDash.length - this.sLen, this.sLen);
            }
            this.contentDigest.update(this.mDash, 0, this.mDash.length);
            this.contentDigest.doFinal(this.mDash, this.mDash.length - this.hLen);
            int i4 = (this.block.length - this.hLen) - 1;
            for (int j = this.mDash.length - this.hLen; j != this.mDash.length; j++) {
                if ((this.block[i4] ^ this.mDash[j]) == 0) {
                    i4++;
                } else {
                    clearBlock(this.mDash);
                    clearBlock(this.block);
                    return false;
                }
            }
            clearBlock(this.mDash);
            clearBlock(this.block);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void ItoOSP(int i, byte[] sp) {
        sp[0] = (byte) (i >>> 24);
        sp[1] = (byte) (i >>> 16);
        sp[2] = (byte) (i >>> 8);
        sp[3] = (byte) (i >>> 0);
    }

    private byte[] maskGeneratorFunction1(byte[] Z, int zOff, int zLen, int length) {
        byte[] mask = new byte[length];
        byte[] hashBuf = new byte[this.mgfhLen];
        byte[] C = new byte[4];
        int counter = 0;
        this.mgfDigest.reset();
        while (counter < length / this.mgfhLen) {
            ItoOSP(counter, C);
            this.mgfDigest.update(Z, zOff, zLen);
            this.mgfDigest.update(C, 0, C.length);
            this.mgfDigest.doFinal(hashBuf, 0);
            System.arraycopy(hashBuf, 0, mask, counter * this.mgfhLen, this.mgfhLen);
            counter++;
        }
        if (counter * this.mgfhLen < length) {
            ItoOSP(counter, C);
            this.mgfDigest.update(Z, zOff, zLen);
            this.mgfDigest.update(C, 0, C.length);
            this.mgfDigest.doFinal(hashBuf, 0);
            System.arraycopy(hashBuf, 0, mask, counter * this.mgfhLen, mask.length - (counter * this.mgfhLen));
        }
        return mask;
    }
}
