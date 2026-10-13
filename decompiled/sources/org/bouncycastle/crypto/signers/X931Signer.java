package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/signers/X931Signer.class */
public class X931Signer implements Signer {
    public static final int TRAILER_IMPLICIT = 188;
    public static final int TRAILER_RIPEMD160 = 12748;
    public static final int TRAILER_RIPEMD128 = 13004;
    public static final int TRAILER_SHA1 = 13260;
    public static final int TRAILER_SHA256 = 13516;
    public static final int TRAILER_SHA512 = 13772;
    public static final int TRAILER_SHA384 = 14028;
    public static final int TRAILER_WHIRLPOOL = 14284;
    public static final int TRAILER_SHA224 = 14540;
    private Digest digest;
    private AsymmetricBlockCipher cipher;
    private RSAKeyParameters kParam;
    private int trailer;
    private int keyBits;
    private byte[] block;

    public X931Signer(AsymmetricBlockCipher asymmetricBlockCipher, Digest digest, boolean z) {
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

    public X931Signer(AsymmetricBlockCipher asymmetricBlockCipher, Digest digest) {
        this(asymmetricBlockCipher, digest, false);
    }

    @Override // org.bouncycastle.crypto.Signer
    public void init(boolean z, CipherParameters cipherParameters) {
        this.kParam = (RSAKeyParameters) cipherParameters;
        this.cipher.init(z, this.kParam);
        this.keyBits = this.kParam.getModulus().bitLength();
        this.block = new byte[(this.keyBits + 7) / 8];
        reset();
    }

    private void clearBlock(byte[] bArr) {
        for (int i = 0; i != bArr.length; i++) {
            bArr[i] = 0;
        }
    }

    @Override // org.bouncycastle.crypto.Signer
    public void update(byte b) {
        this.digest.update(b);
    }

    @Override // org.bouncycastle.crypto.Signer
    public void update(byte[] bArr, int i, int i2) {
        this.digest.update(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Signer
    public void reset() {
        this.digest.reset();
    }

    @Override // org.bouncycastle.crypto.Signer
    public byte[] generateSignature() throws CryptoException {
        createSignatureBlock(this.trailer);
        BigInteger bigInteger = new BigInteger(1, this.cipher.processBlock(this.block, 0, this.block.length));
        clearBlock(this.block);
        return BigIntegers.asUnsignedByteArray(BigIntegers.getUnsignedByteLength(this.kParam.getModulus()), bigInteger.min(this.kParam.getModulus().subtract(bigInteger)));
    }

    private void createSignatureBlock(int i) {
        int length;
        int digestSize = this.digest.getDigestSize();
        if (i == 188) {
            length = (this.block.length - digestSize) - 1;
            this.digest.doFinal(this.block, length);
            this.block[this.block.length - 1] = -68;
        } else {
            length = (this.block.length - digestSize) - 2;
            this.digest.doFinal(this.block, length);
            this.block[this.block.length - 2] = (byte) (i >>> 8);
            this.block[this.block.length - 1] = (byte) i;
        }
        this.block[0] = 107;
        for (int i2 = length - 2; i2 != 0; i2--) {
            this.block[i2] = -69;
        }
        this.block[length - 1] = -70;
    }

    @Override // org.bouncycastle.crypto.Signer
    public boolean verifySignature(byte[] bArr) {
        BigInteger bigInteger;
        try {
            this.block = this.cipher.processBlock(bArr, 0, bArr.length);
            BigInteger bigInteger2 = new BigInteger(1, this.block);
            if ((bigInteger2.intValue() & 15) == 12) {
                bigInteger = bigInteger2;
            } else {
                BigInteger bigIntegerSubtract = this.kParam.getModulus().subtract(bigInteger2);
                if ((bigIntegerSubtract.intValue() & 15) != 12) {
                    return false;
                }
                bigInteger = bigIntegerSubtract;
            }
            createSignatureBlock(this.trailer);
            byte[] bArrAsUnsignedByteArray = BigIntegers.asUnsignedByteArray(this.block.length, bigInteger);
            boolean zConstantTimeAreEqual = Arrays.constantTimeAreEqual(this.block, bArrAsUnsignedByteArray);
            if (this.trailer == 15052 && !zConstantTimeAreEqual) {
                this.block[this.block.length - 2] = 64;
                zConstantTimeAreEqual = Arrays.constantTimeAreEqual(this.block, bArrAsUnsignedByteArray);
            }
            clearBlock(this.block);
            clearBlock(bArrAsUnsignedByteArray);
            return zConstantTimeAreEqual;
        } catch (Exception e) {
            return false;
        }
    }
}
