package org.spongycastle.crypto.signers;

import java.security.SecureRandom;
import org.spongycastle.crypto.AsymmetricBlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.SignerWithRecovery;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.params.ParametersWithSalt;
import org.spongycastle.crypto.params.RSAKeyParameters;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/signers/ISO9796d2PSSSigner.class */
public class ISO9796d2PSSSigner implements SignerWithRecovery {
    public static final int TRAILER_IMPLICIT = 188;
    public static final int TRAILER_RIPEMD160 = 12748;
    public static final int TRAILER_RIPEMD128 = 13004;
    public static final int TRAILER_SHA1 = 13260;
    public static final int TRAILER_SHA256 = 13516;
    public static final int TRAILER_SHA512 = 13772;
    public static final int TRAILER_SHA384 = 14028;
    public static final int TRAILER_WHIRLPOOL = 14284;
    private Digest digest;
    private AsymmetricBlockCipher cipher;
    private SecureRandom random;
    private byte[] standardSalt;
    private int hLen;
    private int trailer;
    private int keyBits;
    private byte[] block;
    private byte[] mBuf;
    private int messageLength;
    private int saltLength;
    private boolean fullMessage;
    private byte[] recoveredMessage;
    private byte[] preSig;
    private byte[] preBlock;
    private int preMStart;
    private int preTLength;

    public ISO9796d2PSSSigner(AsymmetricBlockCipher cipher, Digest digest, int saltLength, boolean implicit) {
        this.cipher = cipher;
        this.digest = digest;
        this.hLen = digest.getDigestSize();
        this.saltLength = saltLength;
        if (implicit) {
            this.trailer = 188;
            return;
        }
        Integer trailerObj = ISOTrailers.getTrailer(digest);
        if (trailerObj != null) {
            this.trailer = trailerObj.intValue();
            return;
        }
        throw new IllegalArgumentException("no valid trailer for digest: " + digest.getAlgorithmName());
    }

    public ISO9796d2PSSSigner(AsymmetricBlockCipher cipher, Digest digest, int saltLength) {
        this(cipher, digest, saltLength, false);
    }

    @Override // org.spongycastle.crypto.Signer
    public void init(boolean forSigning, CipherParameters param) {
        RSAKeyParameters kParam;
        int lengthOfSalt = this.saltLength;
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom p = (ParametersWithRandom) param;
            kParam = (RSAKeyParameters) p.getParameters();
            if (forSigning) {
                this.random = p.getRandom();
            }
        } else if (param instanceof ParametersWithSalt) {
            ParametersWithSalt p2 = (ParametersWithSalt) param;
            kParam = (RSAKeyParameters) p2.getParameters();
            this.standardSalt = p2.getSalt();
            lengthOfSalt = this.standardSalt.length;
            if (this.standardSalt.length != this.saltLength) {
                throw new IllegalArgumentException("Fixed salt is of wrong length");
            }
        } else {
            kParam = (RSAKeyParameters) param;
            if (forSigning) {
                this.random = new SecureRandom();
            }
        }
        this.cipher.init(forSigning, kParam);
        this.keyBits = kParam.getModulus().bitLength();
        this.block = new byte[(this.keyBits + 7) / 8];
        if (this.trailer == 188) {
            this.mBuf = new byte[(((this.block.length - this.digest.getDigestSize()) - lengthOfSalt) - 1) - 1];
        } else {
            this.mBuf = new byte[(((this.block.length - this.digest.getDigestSize()) - lengthOfSalt) - 1) - 2];
        }
        reset();
    }

    private boolean isSameAs(byte[] a, byte[] b) {
        boolean isOkay = true;
        if (this.messageLength != b.length) {
            isOkay = false;
        }
        for (int i = 0; i != b.length; i++) {
            if (a[i] != b[i]) {
                isOkay = false;
            }
        }
        return isOkay;
    }

    private void clearBlock(byte[] block) {
        for (int i = 0; i != block.length; i++) {
            block[i] = 0;
        }
    }

    @Override // org.spongycastle.crypto.SignerWithRecovery
    public void updateWithRecoveredMessage(byte[] signature) throws InvalidCipherTextException {
        int tLength;
        byte[] block = this.cipher.processBlock(signature, 0, signature.length);
        if (block.length < (this.keyBits + 7) / 8) {
            byte[] tmp = new byte[(this.keyBits + 7) / 8];
            System.arraycopy(block, 0, tmp, tmp.length - block.length, block.length);
            clearBlock(block);
            block = tmp;
        }
        if (((block[block.length - 1] & 255) ^ 188) == 0) {
            tLength = 1;
        } else {
            int sigTrail = ((block[block.length - 2] & 255) << 8) | (block[block.length - 1] & 255);
            Integer trailerObj = ISOTrailers.getTrailer(this.digest);
            if (trailerObj != null) {
                if (sigTrail != trailerObj.intValue()) {
                    throw new IllegalStateException("signer initialised with wrong digest for trailer " + sigTrail);
                }
                tLength = 2;
            } else {
                throw new IllegalArgumentException("unrecognised hash in signature");
            }
        }
        byte[] m2Hash = new byte[this.hLen];
        this.digest.doFinal(m2Hash, 0);
        byte[] dbMask = maskGeneratorFunction1(block, (block.length - this.hLen) - tLength, this.hLen, (block.length - this.hLen) - tLength);
        for (int i = 0; i != dbMask.length; i++) {
            byte[] bArr = block;
            int i2 = i;
            bArr[i2] = (byte) (bArr[i2] ^ dbMask[i]);
        }
        byte[] bArr2 = block;
        bArr2[0] = (byte) (bArr2[0] & 127);
        int mStart = 0;
        while (mStart != block.length && block[mStart] != 1) {
            mStart++;
        }
        int mStart2 = mStart + 1;
        if (mStart2 >= block.length) {
            clearBlock(block);
        }
        this.fullMessage = mStart2 > 1;
        this.recoveredMessage = new byte[(dbMask.length - mStart2) - this.saltLength];
        System.arraycopy(block, mStart2, this.recoveredMessage, 0, this.recoveredMessage.length);
        System.arraycopy(this.recoveredMessage, 0, this.mBuf, 0, this.recoveredMessage.length);
        this.preSig = signature;
        this.preBlock = block;
        this.preMStart = mStart2;
        this.preTLength = tLength;
    }

    @Override // org.spongycastle.crypto.Signer
    public void update(byte b) {
        if (this.preSig == null && this.messageLength < this.mBuf.length) {
            byte[] bArr = this.mBuf;
            int i = this.messageLength;
            this.messageLength = i + 1;
            bArr[i] = b;
            return;
        }
        this.digest.update(b);
    }

    @Override // org.spongycastle.crypto.Signer
    public void update(byte[] in, int off, int len) {
        if (this.preSig == null) {
            while (len > 0 && this.messageLength < this.mBuf.length) {
                update(in[off]);
                off++;
                len--;
            }
        }
        if (len > 0) {
            this.digest.update(in, off, len);
        }
    }

    @Override // org.spongycastle.crypto.Signer
    public void reset() {
        this.digest.reset();
        this.messageLength = 0;
        if (this.mBuf != null) {
            clearBlock(this.mBuf);
        }
        if (this.recoveredMessage != null) {
            clearBlock(this.recoveredMessage);
            this.recoveredMessage = null;
        }
        this.fullMessage = false;
        if (this.preSig != null) {
            this.preSig = null;
            clearBlock(this.preBlock);
            this.preBlock = null;
        }
    }

    @Override // org.spongycastle.crypto.Signer
    public byte[] generateSignature() throws CryptoException {
        byte[] salt;
        int digSize = this.digest.getDigestSize();
        byte[] m2Hash = new byte[digSize];
        this.digest.doFinal(m2Hash, 0);
        byte[] C = new byte[8];
        LtoOSP(this.messageLength * 8, C);
        this.digest.update(C, 0, C.length);
        this.digest.update(this.mBuf, 0, this.messageLength);
        this.digest.update(m2Hash, 0, m2Hash.length);
        if (this.standardSalt != null) {
            salt = this.standardSalt;
        } else {
            salt = new byte[this.saltLength];
            this.random.nextBytes(salt);
        }
        this.digest.update(salt, 0, salt.length);
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        int tLength = 2;
        if (this.trailer == 188) {
            tLength = 1;
        }
        int off = ((((this.block.length - this.messageLength) - salt.length) - this.hLen) - tLength) - 1;
        this.block[off] = 1;
        System.arraycopy(this.mBuf, 0, this.block, off + 1, this.messageLength);
        System.arraycopy(salt, 0, this.block, off + 1 + this.messageLength, salt.length);
        byte[] dbMask = maskGeneratorFunction1(hash, 0, hash.length, (this.block.length - this.hLen) - tLength);
        for (int i = 0; i != dbMask.length; i++) {
            byte[] bArr = this.block;
            int i2 = i;
            bArr[i2] = (byte) (bArr[i2] ^ dbMask[i]);
        }
        System.arraycopy(hash, 0, this.block, (this.block.length - this.hLen) - tLength, this.hLen);
        if (this.trailer == 188) {
            this.block[this.block.length - 1] = -68;
        } else {
            this.block[this.block.length - 2] = (byte) (this.trailer >>> 8);
            this.block[this.block.length - 1] = (byte) this.trailer;
        }
        byte[] bArr2 = this.block;
        bArr2[0] = (byte) (bArr2[0] & 127);
        byte[] b = this.cipher.processBlock(this.block, 0, this.block.length);
        this.recoveredMessage = new byte[this.messageLength];
        this.fullMessage = this.messageLength <= this.mBuf.length;
        System.arraycopy(this.mBuf, 0, this.recoveredMessage, 0, this.recoveredMessage.length);
        clearBlock(this.mBuf);
        clearBlock(this.block);
        this.messageLength = 0;
        return b;
    }

    @Override // org.spongycastle.crypto.Signer
    public boolean verifySignature(byte[] signature) {
        byte[] m2Hash = new byte[this.hLen];
        this.digest.doFinal(m2Hash, 0);
        if (this.preSig == null) {
            try {
                updateWithRecoveredMessage(signature);
            } catch (Exception e) {
                return false;
            }
        } else if (!Arrays.areEqual(this.preSig, signature)) {
            throw new IllegalStateException("updateWithRecoveredMessage called on different signature");
        }
        byte[] block = this.preBlock;
        int mStart = this.preMStart;
        int tLength = this.preTLength;
        this.preSig = null;
        this.preBlock = null;
        byte[] C = new byte[8];
        LtoOSP(this.recoveredMessage.length * 8, C);
        this.digest.update(C, 0, C.length);
        if (this.recoveredMessage.length != 0) {
            this.digest.update(this.recoveredMessage, 0, this.recoveredMessage.length);
        }
        this.digest.update(m2Hash, 0, m2Hash.length);
        if (this.standardSalt != null) {
            this.digest.update(this.standardSalt, 0, this.standardSalt.length);
        } else {
            this.digest.update(block, mStart + this.recoveredMessage.length, this.saltLength);
        }
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        int off = (block.length - tLength) - hash.length;
        boolean isOkay = true;
        for (int i = 0; i != hash.length; i++) {
            if (hash[i] != block[off + i]) {
                isOkay = false;
            }
        }
        clearBlock(block);
        clearBlock(hash);
        if (!isOkay) {
            this.fullMessage = false;
            this.messageLength = 0;
            clearBlock(this.recoveredMessage);
            return false;
        }
        if (this.messageLength != 0 && !isSameAs(this.mBuf, this.recoveredMessage)) {
            this.messageLength = 0;
            clearBlock(this.mBuf);
            return false;
        }
        this.messageLength = 0;
        clearBlock(this.mBuf);
        return true;
    }

    @Override // org.spongycastle.crypto.SignerWithRecovery
    public boolean hasFullMessage() {
        return this.fullMessage;
    }

    @Override // org.spongycastle.crypto.SignerWithRecovery
    public byte[] getRecoveredMessage() {
        return this.recoveredMessage;
    }

    private void ItoOSP(int i, byte[] sp) {
        sp[0] = (byte) (i >>> 24);
        sp[1] = (byte) (i >>> 16);
        sp[2] = (byte) (i >>> 8);
        sp[3] = (byte) (i >>> 0);
    }

    private void LtoOSP(long l, byte[] sp) {
        sp[0] = (byte) (l >>> 56);
        sp[1] = (byte) (l >>> 48);
        sp[2] = (byte) (l >>> 40);
        sp[3] = (byte) (l >>> 32);
        sp[4] = (byte) (l >>> 24);
        sp[5] = (byte) (l >>> 16);
        sp[6] = (byte) (l >>> 8);
        sp[7] = (byte) (l >>> 0);
    }

    private byte[] maskGeneratorFunction1(byte[] Z, int zOff, int zLen, int length) {
        byte[] mask = new byte[length];
        byte[] hashBuf = new byte[this.hLen];
        byte[] C = new byte[4];
        int counter = 0;
        this.digest.reset();
        while (counter < length / this.hLen) {
            ItoOSP(counter, C);
            this.digest.update(Z, zOff, zLen);
            this.digest.update(C, 0, C.length);
            this.digest.doFinal(hashBuf, 0);
            System.arraycopy(hashBuf, 0, mask, counter * this.hLen, this.hLen);
            counter++;
        }
        if (counter * this.hLen < length) {
            ItoOSP(counter, C);
            this.digest.update(Z, zOff, zLen);
            this.digest.update(C, 0, C.length);
            this.digest.doFinal(hashBuf, 0);
            System.arraycopy(hashBuf, 0, mask, counter * this.hLen, mask.length - (counter * this.hLen));
        }
        return mask;
    }
}
