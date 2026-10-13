package org.spongycastle.crypto.signers;

import org.spongycastle.crypto.AsymmetricBlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.SignerWithRecovery;
import org.spongycastle.crypto.params.RSAKeyParameters;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/signers/ISO9796d2Signer.class */
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

    public ISO9796d2Signer(AsymmetricBlockCipher cipher, Digest digest, boolean implicit) {
        this.cipher = cipher;
        this.digest = digest;
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

    public ISO9796d2Signer(AsymmetricBlockCipher cipher, Digest digest) {
        this(cipher, digest, false);
    }

    @Override // org.spongycastle.crypto.Signer
    public void init(boolean forSigning, CipherParameters param) {
        RSAKeyParameters kParam = (RSAKeyParameters) param;
        this.cipher.init(forSigning, kParam);
        this.keyBits = kParam.getModulus().bitLength();
        this.block = new byte[(this.keyBits + 7) / 8];
        if (this.trailer == 188) {
            this.mBuf = new byte[(this.block.length - this.digest.getDigestSize()) - 2];
        } else {
            this.mBuf = new byte[(this.block.length - this.digest.getDigestSize()) - 3];
        }
        reset();
    }

    private boolean isSameAs(byte[] a, byte[] b) {
        boolean isOkay = true;
        if (this.messageLength > this.mBuf.length) {
            if (this.mBuf.length > b.length) {
                isOkay = false;
            }
            for (int i = 0; i != this.mBuf.length; i++) {
                if (a[i] != b[i]) {
                    isOkay = false;
                }
            }
        } else {
            if (this.messageLength != b.length) {
                isOkay = false;
            }
            for (int i2 = 0; i2 != b.length; i2++) {
                if (a[i2] != b[i2]) {
                    isOkay = false;
                }
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
        int delta;
        byte[] block = this.cipher.processBlock(signature, 0, signature.length);
        if (((block[0] & 192) ^ 64) != 0) {
            throw new InvalidCipherTextException("malformed signature");
        }
        if (((block[block.length - 1] & 15) ^ 12) != 0) {
            throw new InvalidCipherTextException("malformed signature");
        }
        if (((block[block.length - 1] & 255) ^ 188) == 0) {
            delta = 1;
        } else {
            int sigTrail = ((block[block.length - 2] & 255) << 8) | (block[block.length - 1] & 255);
            Integer trailerObj = ISOTrailers.getTrailer(this.digest);
            if (trailerObj != null) {
                if (sigTrail != trailerObj.intValue()) {
                    throw new IllegalStateException("signer initialised with wrong digest for trailer " + sigTrail);
                }
                delta = 2;
            } else {
                throw new IllegalArgumentException("unrecognised hash in signature");
            }
        }
        int mStart = 0;
        while (mStart != block.length && ((block[mStart] & 15) ^ 10) != 0) {
            mStart++;
        }
        int mStart2 = mStart + 1;
        int off = (block.length - delta) - this.digest.getDigestSize();
        if (off - mStart2 <= 0) {
            throw new InvalidCipherTextException("malformed block");
        }
        if ((block[0] & 32) == 0) {
            this.fullMessage = true;
            this.recoveredMessage = new byte[off - mStart2];
            System.arraycopy(block, mStart2, this.recoveredMessage, 0, this.recoveredMessage.length);
        } else {
            this.fullMessage = false;
            this.recoveredMessage = new byte[off - mStart2];
            System.arraycopy(block, mStart2, this.recoveredMessage, 0, this.recoveredMessage.length);
        }
        this.preSig = signature;
        this.preBlock = block;
        this.digest.update(this.recoveredMessage, 0, this.recoveredMessage.length);
        this.messageLength = this.recoveredMessage.length;
        System.arraycopy(this.recoveredMessage, 0, this.mBuf, 0, this.recoveredMessage.length);
    }

    @Override // org.spongycastle.crypto.Signer
    public void update(byte b) {
        this.digest.update(b);
        if (this.messageLength < this.mBuf.length) {
            this.mBuf[this.messageLength] = b;
        }
        this.messageLength++;
    }

    @Override // org.spongycastle.crypto.Signer
    public void update(byte[] in, int off, int len) {
        while (len > 0 && this.messageLength < this.mBuf.length) {
            update(in[off]);
            off++;
            len--;
        }
        this.digest.update(in, off, len);
        this.messageLength += len;
    }

    @Override // org.spongycastle.crypto.Signer
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

    @Override // org.spongycastle.crypto.Signer
    public byte[] generateSignature() throws CryptoException {
        int t;
        int delta;
        byte header;
        int delta2;
        int digSize = this.digest.getDigestSize();
        if (this.trailer == 188) {
            t = 8;
            delta = (this.block.length - digSize) - 1;
            this.digest.doFinal(this.block, delta);
            this.block[this.block.length - 1] = -68;
        } else {
            t = 16;
            delta = (this.block.length - digSize) - 2;
            this.digest.doFinal(this.block, delta);
            this.block[this.block.length - 2] = (byte) (this.trailer >>> 8);
            this.block[this.block.length - 1] = (byte) this.trailer;
        }
        int x = ((((digSize + this.messageLength) * 8) + t) + 4) - this.keyBits;
        if (x > 0) {
            int mR = this.messageLength - ((x + 7) / 8);
            header = 96;
            delta2 = delta - mR;
            System.arraycopy(this.mBuf, 0, this.block, delta2, mR);
            this.recoveredMessage = new byte[mR];
        } else {
            header = 64;
            delta2 = delta - this.messageLength;
            System.arraycopy(this.mBuf, 0, this.block, delta2, this.messageLength);
            this.recoveredMessage = new byte[this.messageLength];
        }
        if (delta2 - 1 > 0) {
            for (int i = delta2 - 1; i != 0; i--) {
                this.block[i] = -69;
            }
            byte[] bArr = this.block;
            int i2 = delta2 - 1;
            bArr[i2] = (byte) (bArr[i2] ^ 1);
            this.block[0] = 11;
            byte[] bArr2 = this.block;
            bArr2[0] = (byte) (bArr2[0] | header);
        } else {
            this.block[0] = 10;
            byte[] bArr3 = this.block;
            bArr3[0] = (byte) (bArr3[0] | header);
        }
        byte[] b = this.cipher.processBlock(this.block, 0, this.block.length);
        this.fullMessage = (header & 32) == 0;
        System.arraycopy(this.mBuf, 0, this.recoveredMessage, 0, this.recoveredMessage.length);
        this.messageLength = 0;
        clearBlock(this.mBuf);
        clearBlock(this.block);
        return b;
    }

    @Override // org.spongycastle.crypto.Signer
    public boolean verifySignature(byte[] signature) {
        byte[] block;
        int delta;
        if (this.preSig == null) {
            try {
                block = this.cipher.processBlock(signature, 0, signature.length);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (!Arrays.areEqual(this.preSig, signature)) {
                throw new IllegalStateException("updateWithRecoveredMessage called on different signature");
            }
            block = this.preBlock;
            this.preSig = null;
            this.preBlock = null;
        }
        if (((block[0] & 192) ^ 64) != 0) {
            return returnFalse(block);
        }
        if (((block[block.length - 1] & 15) ^ 12) != 0) {
            return returnFalse(block);
        }
        if (((block[block.length - 1] & 255) ^ 188) == 0) {
            delta = 1;
        } else {
            int sigTrail = ((block[block.length - 2] & 255) << 8) | (block[block.length - 1] & 255);
            Integer trailerObj = ISOTrailers.getTrailer(this.digest);
            if (trailerObj != null) {
                if (sigTrail != trailerObj.intValue()) {
                    throw new IllegalStateException("signer initialised with wrong digest for trailer " + sigTrail);
                }
                delta = 2;
            } else {
                throw new IllegalArgumentException("unrecognised hash in signature");
            }
        }
        int mStart = 0;
        while (mStart != block.length && ((block[mStart] & 15) ^ 10) != 0) {
            mStart++;
        }
        int mStart2 = mStart + 1;
        byte[] hash = new byte[this.digest.getDigestSize()];
        int off = (block.length - delta) - hash.length;
        if (off - mStart2 <= 0) {
            return returnFalse(block);
        }
        if ((block[0] & 32) == 0) {
            this.fullMessage = true;
            if (this.messageLength > off - mStart2) {
                return returnFalse(block);
            }
            this.digest.reset();
            this.digest.update(block, mStart2, off - mStart2);
            this.digest.doFinal(hash, 0);
            boolean isOkay = true;
            for (int i = 0; i != hash.length; i++) {
                byte[] bArr = block;
                int i2 = off + i;
                bArr[i2] = (byte) (bArr[i2] ^ hash[i]);
                if (block[off + i] != 0) {
                    isOkay = false;
                }
            }
            if (!isOkay) {
                return returnFalse(block);
            }
            this.recoveredMessage = new byte[off - mStart2];
            System.arraycopy(block, mStart2, this.recoveredMessage, 0, this.recoveredMessage.length);
        } else {
            this.fullMessage = false;
            this.digest.doFinal(hash, 0);
            boolean isOkay2 = true;
            for (int i3 = 0; i3 != hash.length; i3++) {
                byte[] bArr2 = block;
                int i4 = off + i3;
                bArr2[i4] = (byte) (bArr2[i4] ^ hash[i3]);
                if (block[off + i3] != 0) {
                    isOkay2 = false;
                }
            }
            if (!isOkay2) {
                return returnFalse(block);
            }
            this.recoveredMessage = new byte[off - mStart2];
            System.arraycopy(block, mStart2, this.recoveredMessage, 0, this.recoveredMessage.length);
        }
        if (this.messageLength != 0 && !isSameAs(this.mBuf, this.recoveredMessage)) {
            return returnFalse(block);
        }
        clearBlock(this.mBuf);
        clearBlock(block);
        this.messageLength = 0;
        return true;
    }

    private boolean returnFalse(byte[] block) {
        this.messageLength = 0;
        clearBlock(this.mBuf);
        clearBlock(block);
        return false;
    }

    @Override // org.spongycastle.crypto.SignerWithRecovery
    public boolean hasFullMessage() {
        return this.fullMessage;
    }

    @Override // org.spongycastle.crypto.SignerWithRecovery
    public byte[] getRecoveredMessage() {
        return this.recoveredMessage;
    }
}
