package org.bouncycastle.crypto.signers;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.SignerWithRecovery;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithSalt;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/signers/ISO9796d2PSSSigner.class */
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

    public ISO9796d2PSSSigner(AsymmetricBlockCipher asymmetricBlockCipher, Digest digest, int i, boolean z) {
        this.cipher = asymmetricBlockCipher;
        this.digest = digest;
        this.hLen = digest.getDigestSize();
        this.saltLength = i;
        if (z) {
            this.trailer = 188;
            return;
        }
        Integer trailer = ISOTrailers.getTrailer(digest);
        if (trailer == null) {
            throw new IllegalArgumentException("no valid trailer for digest: " + digest.getAlgorithmName());
        }
        this.trailer = trailer.intValue();
    }

    public ISO9796d2PSSSigner(AsymmetricBlockCipher asymmetricBlockCipher, Digest digest, int i) {
        this(asymmetricBlockCipher, digest, i, false);
    }

    @Override // org.bouncycastle.crypto.Signer
    public void init(boolean z, CipherParameters cipherParameters) {
        RSAKeyParameters rSAKeyParameters;
        int length = this.saltLength;
        if (cipherParameters instanceof ParametersWithRandom) {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom) cipherParameters;
            rSAKeyParameters = (RSAKeyParameters) parametersWithRandom.getParameters();
            if (z) {
                this.random = parametersWithRandom.getRandom();
            }
        } else if (cipherParameters instanceof ParametersWithSalt) {
            ParametersWithSalt parametersWithSalt = (ParametersWithSalt) cipherParameters;
            rSAKeyParameters = (RSAKeyParameters) parametersWithSalt.getParameters();
            this.standardSalt = parametersWithSalt.getSalt();
            length = this.standardSalt.length;
            if (this.standardSalt.length != this.saltLength) {
                throw new IllegalArgumentException("Fixed salt is of wrong length");
            }
        } else {
            rSAKeyParameters = (RSAKeyParameters) cipherParameters;
            if (z) {
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
        }
        this.cipher.init(z, rSAKeyParameters);
        this.keyBits = rSAKeyParameters.getModulus().bitLength();
        this.block = new byte[(this.keyBits + 7) / 8];
        if (this.trailer == 188) {
            this.mBuf = new byte[(((this.block.length - this.digest.getDigestSize()) - length) - 1) - 1];
        } else {
            this.mBuf = new byte[(((this.block.length - this.digest.getDigestSize()) - length) - 1) - 2];
        }
        reset();
    }

    private boolean isSameAs(byte[] bArr, byte[] bArr2) {
        boolean z = this.messageLength == bArr2.length;
        for (int i = 0; i != bArr2.length; i++) {
            if (bArr[i] != bArr2[i]) {
                z = false;
            }
        }
        return z;
    }

    private void clearBlock(byte[] bArr) {
        for (int i = 0; i != bArr.length; i++) {
            bArr[i] = 0;
        }
    }

    @Override // org.bouncycastle.crypto.SignerWithRecovery
    public void updateWithRecoveredMessage(byte[] bArr) throws InvalidCipherTextException {
        int i;
        byte[] bArrProcessBlock = this.cipher.processBlock(bArr, 0, bArr.length);
        if (bArrProcessBlock.length < (this.keyBits + 7) / 8) {
            byte[] bArr2 = new byte[(this.keyBits + 7) / 8];
            System.arraycopy(bArrProcessBlock, 0, bArr2, bArr2.length - bArrProcessBlock.length, bArrProcessBlock.length);
            clearBlock(bArrProcessBlock);
            bArrProcessBlock = bArr2;
        }
        if (((bArrProcessBlock[bArrProcessBlock.length - 1] & 255) ^ 188) == 0) {
            i = 1;
        } else {
            int i2 = ((bArrProcessBlock[bArrProcessBlock.length - 2] & 255) << 8) | (bArrProcessBlock[bArrProcessBlock.length - 1] & 255);
            Integer trailer = ISOTrailers.getTrailer(this.digest);
            if (trailer == null) {
                throw new IllegalArgumentException("unrecognised hash in signature");
            }
            int iIntValue = trailer.intValue();
            if (i2 != iIntValue && (iIntValue != 15052 || i2 != 16588)) {
                throw new IllegalStateException("signer initialised with wrong digest for trailer " + i2);
            }
            i = 2;
        }
        this.digest.doFinal(new byte[this.hLen], 0);
        byte[] bArrMaskGeneratorFunction1 = maskGeneratorFunction1(bArrProcessBlock, (bArrProcessBlock.length - this.hLen) - i, this.hLen, (bArrProcessBlock.length - this.hLen) - i);
        for (int i3 = 0; i3 != bArrMaskGeneratorFunction1.length; i3++) {
            byte[] bArr3 = bArrProcessBlock;
            int i4 = i3;
            bArr3[i4] = (byte) (bArr3[i4] ^ bArrMaskGeneratorFunction1[i3]);
        }
        byte[] bArr4 = bArrProcessBlock;
        bArr4[0] = (byte) (bArr4[0] & 127);
        int i5 = 0;
        while (i5 != bArrProcessBlock.length && bArrProcessBlock[i5] != 1) {
            i5++;
        }
        int i6 = i5 + 1;
        if (i6 >= bArrProcessBlock.length) {
            clearBlock(bArrProcessBlock);
        }
        this.fullMessage = i6 > 1;
        this.recoveredMessage = new byte[(bArrMaskGeneratorFunction1.length - i6) - this.saltLength];
        System.arraycopy(bArrProcessBlock, i6, this.recoveredMessage, 0, this.recoveredMessage.length);
        System.arraycopy(this.recoveredMessage, 0, this.mBuf, 0, this.recoveredMessage.length);
        this.preSig = bArr;
        this.preBlock = bArrProcessBlock;
        this.preMStart = i6;
        this.preTLength = i;
    }

    @Override // org.bouncycastle.crypto.Signer
    public void update(byte b) {
        if (this.preSig != null || this.messageLength >= this.mBuf.length) {
            this.digest.update(b);
            return;
        }
        byte[] bArr = this.mBuf;
        int i = this.messageLength;
        this.messageLength = i + 1;
        bArr[i] = b;
    }

    @Override // org.bouncycastle.crypto.Signer
    public void update(byte[] bArr, int i, int i2) {
        if (this.preSig == null) {
            while (i2 > 0 && this.messageLength < this.mBuf.length) {
                update(bArr[i]);
                i++;
                i2--;
            }
        }
        if (i2 > 0) {
            this.digest.update(bArr, i, i2);
        }
    }

    @Override // org.bouncycastle.crypto.Signer
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

    @Override // org.bouncycastle.crypto.Signer
    public byte[] generateSignature() throws CryptoException {
        byte[] bArr;
        byte[] bArr2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(bArr2, 0);
        byte[] bArr3 = new byte[8];
        LtoOSP(this.messageLength * 8, bArr3);
        this.digest.update(bArr3, 0, bArr3.length);
        this.digest.update(this.mBuf, 0, this.messageLength);
        this.digest.update(bArr2, 0, bArr2.length);
        if (this.standardSalt != null) {
            bArr = this.standardSalt;
        } else {
            bArr = new byte[this.saltLength];
            this.random.nextBytes(bArr);
        }
        this.digest.update(bArr, 0, bArr.length);
        byte[] bArr4 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(bArr4, 0);
        int i = this.trailer == 188 ? 1 : 2;
        int length = ((((this.block.length - this.messageLength) - bArr.length) - this.hLen) - i) - 1;
        this.block[length] = 1;
        System.arraycopy(this.mBuf, 0, this.block, length + 1, this.messageLength);
        System.arraycopy(bArr, 0, this.block, length + 1 + this.messageLength, bArr.length);
        byte[] bArrMaskGeneratorFunction1 = maskGeneratorFunction1(bArr4, 0, bArr4.length, (this.block.length - this.hLen) - i);
        for (int i2 = 0; i2 != bArrMaskGeneratorFunction1.length; i2++) {
            byte[] bArr5 = this.block;
            int i3 = i2;
            bArr5[i3] = (byte) (bArr5[i3] ^ bArrMaskGeneratorFunction1[i2]);
        }
        System.arraycopy(bArr4, 0, this.block, (this.block.length - this.hLen) - i, this.hLen);
        if (this.trailer == 188) {
            this.block[this.block.length - 1] = -68;
        } else {
            this.block[this.block.length - 2] = (byte) (this.trailer >>> 8);
            this.block[this.block.length - 1] = (byte) this.trailer;
        }
        byte[] bArr6 = this.block;
        bArr6[0] = (byte) (bArr6[0] & 127);
        byte[] bArrProcessBlock = this.cipher.processBlock(this.block, 0, this.block.length);
        this.recoveredMessage = new byte[this.messageLength];
        this.fullMessage = this.messageLength <= this.mBuf.length;
        System.arraycopy(this.mBuf, 0, this.recoveredMessage, 0, this.recoveredMessage.length);
        clearBlock(this.mBuf);
        clearBlock(this.block);
        this.messageLength = 0;
        return bArrProcessBlock;
    }

    @Override // org.bouncycastle.crypto.Signer
    public boolean verifySignature(byte[] bArr) {
        byte[] bArr2 = new byte[this.hLen];
        this.digest.doFinal(bArr2, 0);
        if (this.preSig == null) {
            try {
                updateWithRecoveredMessage(bArr);
            } catch (Exception e) {
                return false;
            }
        } else if (!Arrays.areEqual(this.preSig, bArr)) {
            throw new IllegalStateException("updateWithRecoveredMessage called on different signature");
        }
        byte[] bArr3 = this.preBlock;
        int i = this.preMStart;
        int i2 = this.preTLength;
        this.preSig = null;
        this.preBlock = null;
        byte[] bArr4 = new byte[8];
        LtoOSP(this.recoveredMessage.length * 8, bArr4);
        this.digest.update(bArr4, 0, bArr4.length);
        if (this.recoveredMessage.length != 0) {
            this.digest.update(this.recoveredMessage, 0, this.recoveredMessage.length);
        }
        this.digest.update(bArr2, 0, bArr2.length);
        if (this.standardSalt != null) {
            this.digest.update(this.standardSalt, 0, this.standardSalt.length);
        } else {
            this.digest.update(bArr3, i + this.recoveredMessage.length, this.saltLength);
        }
        byte[] bArr5 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(bArr5, 0);
        int length = (bArr3.length - i2) - bArr5.length;
        boolean z = true;
        for (int i3 = 0; i3 != bArr5.length; i3++) {
            if (bArr5[i3] != bArr3[length + i3]) {
                z = false;
            }
        }
        clearBlock(bArr3);
        clearBlock(bArr5);
        if (!z) {
            this.fullMessage = false;
            this.messageLength = 0;
            clearBlock(this.recoveredMessage);
            return false;
        }
        if (this.messageLength == 0 || isSameAs(this.mBuf, this.recoveredMessage)) {
            this.messageLength = 0;
            clearBlock(this.mBuf);
            return true;
        }
        this.messageLength = 0;
        clearBlock(this.mBuf);
        return false;
    }

    @Override // org.bouncycastle.crypto.SignerWithRecovery
    public boolean hasFullMessage() {
        return this.fullMessage;
    }

    @Override // org.bouncycastle.crypto.SignerWithRecovery
    public byte[] getRecoveredMessage() {
        return this.recoveredMessage;
    }

    private void ItoOSP(int i, byte[] bArr) {
        bArr[0] = (byte) (i >>> 24);
        bArr[1] = (byte) (i >>> 16);
        bArr[2] = (byte) (i >>> 8);
        bArr[3] = (byte) (i >>> 0);
    }

    private void LtoOSP(long j, byte[] bArr) {
        bArr[0] = (byte) (j >>> 56);
        bArr[1] = (byte) (j >>> 48);
        bArr[2] = (byte) (j >>> 40);
        bArr[3] = (byte) (j >>> 32);
        bArr[4] = (byte) (j >>> 24);
        bArr[5] = (byte) (j >>> 16);
        bArr[6] = (byte) (j >>> 8);
        bArr[7] = (byte) (j >>> 0);
    }

    private byte[] maskGeneratorFunction1(byte[] bArr, int i, int i2, int i3) {
        byte[] bArr2 = new byte[i3];
        byte[] bArr3 = new byte[this.hLen];
        byte[] bArr4 = new byte[4];
        int i4 = 0;
        this.digest.reset();
        while (i4 < i3 / this.hLen) {
            ItoOSP(i4, bArr4);
            this.digest.update(bArr, i, i2);
            this.digest.update(bArr4, 0, bArr4.length);
            this.digest.doFinal(bArr3, 0);
            System.arraycopy(bArr3, 0, bArr2, i4 * this.hLen, this.hLen);
            i4++;
        }
        if (i4 * this.hLen < i3) {
            ItoOSP(i4, bArr4);
            this.digest.update(bArr, i, i2);
            this.digest.update(bArr4, 0, bArr4.length);
            this.digest.doFinal(bArr3, 0);
            System.arraycopy(bArr3, 0, bArr2, i4 * this.hLen, bArr2.length - (i4 * this.hLen));
        }
        return bArr2;
    }
}
