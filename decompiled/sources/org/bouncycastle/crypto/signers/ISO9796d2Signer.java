package org.bouncycastle.crypto.signers;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.SignerWithRecovery;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/signers/ISO9796d2Signer.class */
public class ISO9796d2Signer implements SignerWithRecovery {
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
    private int trailer;
    private int keyBits;
    private byte[] block;
    private byte[] mBuf;
    private int messageLength;
    private boolean fullMessage;
    private byte[] recoveredMessage;
    private byte[] preSig;
    private byte[] preBlock;

    public ISO9796d2Signer(AsymmetricBlockCipher asymmetricBlockCipher, Digest digest, boolean z) {
        this.cipher = asymmetricBlockCipher;
        this.digest = digest;
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

    public ISO9796d2Signer(AsymmetricBlockCipher asymmetricBlockCipher, Digest digest) {
        this(asymmetricBlockCipher, digest, false);
    }

    @Override // org.bouncycastle.crypto.Signer
    public void init(boolean z, CipherParameters cipherParameters) {
        RSAKeyParameters rSAKeyParameters = (RSAKeyParameters) cipherParameters;
        this.cipher.init(z, rSAKeyParameters);
        this.keyBits = rSAKeyParameters.getModulus().bitLength();
        this.block = new byte[(this.keyBits + 7) / 8];
        if (this.trailer == 188) {
            this.mBuf = new byte[(this.block.length - this.digest.getDigestSize()) - 2];
        } else {
            this.mBuf = new byte[(this.block.length - this.digest.getDigestSize()) - 3];
        }
        reset();
    }

    private boolean isSameAs(byte[] bArr, byte[] bArr2) {
        boolean z;
        if (this.messageLength > this.mBuf.length) {
            z = this.mBuf.length <= bArr2.length;
            for (int i = 0; i != this.mBuf.length; i++) {
                if (bArr[i] != bArr2[i]) {
                    z = false;
                }
            }
        } else {
            z = this.messageLength == bArr2.length;
            for (int i2 = 0; i2 != bArr2.length; i2++) {
                if (bArr[i2] != bArr2[i2]) {
                    z = false;
                }
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
        if (((bArrProcessBlock[0] & 192) ^ 64) != 0) {
            throw new InvalidCipherTextException("malformed signature");
        }
        if (((bArrProcessBlock[bArrProcessBlock.length - 1] & 15) ^ 12) != 0) {
            throw new InvalidCipherTextException("malformed signature");
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
        int i3 = 0;
        while (i3 != bArrProcessBlock.length && ((bArrProcessBlock[i3] & 15) ^ 10) != 0) {
            i3++;
        }
        int i4 = i3 + 1;
        int length = (bArrProcessBlock.length - i) - this.digest.getDigestSize();
        if (length - i4 <= 0) {
            throw new InvalidCipherTextException("malformed block");
        }
        if ((bArrProcessBlock[0] & 32) == 0) {
            this.fullMessage = true;
            this.recoveredMessage = new byte[length - i4];
            System.arraycopy(bArrProcessBlock, i4, this.recoveredMessage, 0, this.recoveredMessage.length);
        } else {
            this.fullMessage = false;
            this.recoveredMessage = new byte[length - i4];
            System.arraycopy(bArrProcessBlock, i4, this.recoveredMessage, 0, this.recoveredMessage.length);
        }
        this.preSig = bArr;
        this.preBlock = bArrProcessBlock;
        this.digest.update(this.recoveredMessage, 0, this.recoveredMessage.length);
        this.messageLength = this.recoveredMessage.length;
        System.arraycopy(this.recoveredMessage, 0, this.mBuf, 0, this.recoveredMessage.length);
    }

    @Override // org.bouncycastle.crypto.Signer
    public void update(byte b) {
        this.digest.update(b);
        if (this.messageLength < this.mBuf.length) {
            this.mBuf[this.messageLength] = b;
        }
        this.messageLength++;
    }

    @Override // org.bouncycastle.crypto.Signer
    public void update(byte[] bArr, int i, int i2) {
        while (i2 > 0 && this.messageLength < this.mBuf.length) {
            update(bArr[i]);
            i++;
            i2--;
        }
        this.digest.update(bArr, i, i2);
        this.messageLength += i2;
    }

    @Override // org.bouncycastle.crypto.Signer
    public void reset() {
        this.digest.reset();
        this.messageLength = 0;
        clearBlock(this.mBuf);
        if (this.recoveredMessage != null) {
            clearBlock(this.recoveredMessage);
        }
        this.recoveredMessage = null;
        this.fullMessage = false;
        if (this.preSig != null) {
            this.preSig = null;
            clearBlock(this.preBlock);
            this.preBlock = null;
        }
    }

    @Override // org.bouncycastle.crypto.Signer
    public byte[] generateSignature() throws CryptoException {
        int i;
        int length;
        byte b;
        int i2;
        int digestSize = this.digest.getDigestSize();
        if (this.trailer == 188) {
            i = 8;
            length = (this.block.length - digestSize) - 1;
            this.digest.doFinal(this.block, length);
            this.block[this.block.length - 1] = -68;
        } else {
            i = 16;
            length = (this.block.length - digestSize) - 2;
            this.digest.doFinal(this.block, length);
            this.block[this.block.length - 2] = (byte) (this.trailer >>> 8);
            this.block[this.block.length - 1] = (byte) this.trailer;
        }
        int i3 = ((((digestSize + this.messageLength) * 8) + i) + 4) - this.keyBits;
        if (i3 > 0) {
            int i4 = this.messageLength - ((i3 + 7) / 8);
            b = 96;
            i2 = length - i4;
            System.arraycopy(this.mBuf, 0, this.block, i2, i4);
            this.recoveredMessage = new byte[i4];
        } else {
            b = 64;
            i2 = length - this.messageLength;
            System.arraycopy(this.mBuf, 0, this.block, i2, this.messageLength);
            this.recoveredMessage = new byte[this.messageLength];
        }
        if (i2 - 1 > 0) {
            for (int i5 = i2 - 1; i5 != 0; i5--) {
                this.block[i5] = -69;
            }
            byte[] bArr = this.block;
            int i6 = i2 - 1;
            bArr[i6] = (byte) (bArr[i6] ^ 1);
            this.block[0] = 11;
            byte[] bArr2 = this.block;
            bArr2[0] = (byte) (bArr2[0] | b);
        } else {
            this.block[0] = 10;
            byte[] bArr3 = this.block;
            bArr3[0] = (byte) (bArr3[0] | b);
        }
        byte[] bArrProcessBlock = this.cipher.processBlock(this.block, 0, this.block.length);
        this.fullMessage = (b & 32) == 0;
        System.arraycopy(this.mBuf, 0, this.recoveredMessage, 0, this.recoveredMessage.length);
        this.messageLength = 0;
        clearBlock(this.mBuf);
        clearBlock(this.block);
        return bArrProcessBlock;
    }

    @Override // org.bouncycastle.crypto.Signer
    public boolean verifySignature(byte[] bArr) {
        byte[] bArrProcessBlock;
        int i;
        if (this.preSig == null) {
            try {
                bArrProcessBlock = this.cipher.processBlock(bArr, 0, bArr.length);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (!Arrays.areEqual(this.preSig, bArr)) {
                throw new IllegalStateException("updateWithRecoveredMessage called on different signature");
            }
            bArrProcessBlock = this.preBlock;
            this.preSig = null;
            this.preBlock = null;
        }
        if (((bArrProcessBlock[0] & 192) ^ 64) == 0 && ((bArrProcessBlock[bArrProcessBlock.length - 1] & 15) ^ 12) == 0) {
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
            int i3 = 0;
            while (i3 != bArrProcessBlock.length && ((bArrProcessBlock[i3] & 15) ^ 10) != 0) {
                i3++;
            }
            int i4 = i3 + 1;
            byte[] bArr2 = new byte[this.digest.getDigestSize()];
            int length = (bArrProcessBlock.length - i) - bArr2.length;
            if (length - i4 <= 0) {
                return returnFalse(bArrProcessBlock);
            }
            if ((bArrProcessBlock[0] & 32) == 0) {
                this.fullMessage = true;
                if (this.messageLength > length - i4) {
                    return returnFalse(bArrProcessBlock);
                }
                this.digest.reset();
                this.digest.update(bArrProcessBlock, i4, length - i4);
                this.digest.doFinal(bArr2, 0);
                boolean z = true;
                for (int i5 = 0; i5 != bArr2.length; i5++) {
                    byte[] bArr3 = bArrProcessBlock;
                    int i6 = length + i5;
                    bArr3[i6] = (byte) (bArr3[i6] ^ bArr2[i5]);
                    if (bArrProcessBlock[length + i5] != 0) {
                        z = false;
                    }
                }
                if (!z) {
                    return returnFalse(bArrProcessBlock);
                }
                this.recoveredMessage = new byte[length - i4];
                System.arraycopy(bArrProcessBlock, i4, this.recoveredMessage, 0, this.recoveredMessage.length);
            } else {
                this.fullMessage = false;
                this.digest.doFinal(bArr2, 0);
                boolean z2 = true;
                for (int i7 = 0; i7 != bArr2.length; i7++) {
                    byte[] bArr4 = bArrProcessBlock;
                    int i8 = length + i7;
                    bArr4[i8] = (byte) (bArr4[i8] ^ bArr2[i7]);
                    if (bArrProcessBlock[length + i7] != 0) {
                        z2 = false;
                    }
                }
                if (!z2) {
                    return returnFalse(bArrProcessBlock);
                }
                this.recoveredMessage = new byte[length - i4];
                System.arraycopy(bArrProcessBlock, i4, this.recoveredMessage, 0, this.recoveredMessage.length);
            }
            if (this.messageLength != 0 && !isSameAs(this.mBuf, this.recoveredMessage)) {
                return returnFalse(bArrProcessBlock);
            }
            clearBlock(this.mBuf);
            clearBlock(bArrProcessBlock);
            this.messageLength = 0;
            return true;
        }
        return returnFalse(bArrProcessBlock);
    }

    private boolean returnFalse(byte[] bArr) {
        this.messageLength = 0;
        clearBlock(this.mBuf);
        clearBlock(bArr);
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
}
