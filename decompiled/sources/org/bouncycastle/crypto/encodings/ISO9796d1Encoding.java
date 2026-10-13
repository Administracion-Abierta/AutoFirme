package org.bouncycastle.crypto.encodings;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/encodings/ISO9796d1Encoding.class */
public class ISO9796d1Encoding implements AsymmetricBlockCipher {
    private static final BigInteger SIXTEEN = BigInteger.valueOf(16);
    private static final BigInteger SIX = BigInteger.valueOf(6);
    private static byte[] shadows = {14, 3, 5, 8, 9, 4, 2, 15, 0, 13, 11, 6, 7, 10, 12, 1};
    private static byte[] inverse = {8, 15, 6, 1, 5, 2, 11, 12, 3, 4, 13, 10, 14, 9, 0, 7};
    private AsymmetricBlockCipher engine;
    private boolean forEncryption;
    private int bitSize;
    private int padBits = 0;
    private BigInteger modulus;

    public ISO9796d1Encoding(AsymmetricBlockCipher asymmetricBlockCipher) {
        this.engine = asymmetricBlockCipher;
    }

    public AsymmetricBlockCipher getUnderlyingCipher() {
        return this.engine;
    }

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        RSAKeyParameters rSAKeyParameters = cipherParameters instanceof ParametersWithRandom ? (RSAKeyParameters) ((ParametersWithRandom) cipherParameters).getParameters() : (RSAKeyParameters) cipherParameters;
        this.engine.init(z, cipherParameters);
        this.modulus = rSAKeyParameters.getModulus();
        this.bitSize = this.modulus.bitLength();
        this.forEncryption = z;
    }

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public int getInputBlockSize() {
        int inputBlockSize = this.engine.getInputBlockSize();
        return this.forEncryption ? (inputBlockSize + 1) / 2 : inputBlockSize;
    }

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public int getOutputBlockSize() {
        int outputBlockSize = this.engine.getOutputBlockSize();
        return this.forEncryption ? outputBlockSize : (outputBlockSize + 1) / 2;
    }

    public void setPadBits(int i) {
        if (i > 7) {
            throw new IllegalArgumentException("padBits > 7");
        }
        this.padBits = i;
    }

    public int getPadBits() {
        return this.padBits;
    }

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public byte[] processBlock(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        return this.forEncryption ? encodeBlock(bArr, i, i2) : decodeBlock(bArr, i, i2);
    }

    private byte[] encodeBlock(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        byte[] bArr2 = new byte[(this.bitSize + 7) / 8];
        int i3 = this.padBits + 1;
        int i4 = (this.bitSize + 13) / 16;
        int i5 = 0;
        while (true) {
            int i6 = i5;
            if (i6 >= i4) {
                break;
            }
            if (i6 > i4 - i2) {
                System.arraycopy(bArr, (i + i2) - (i4 - i6), bArr2, bArr2.length - i4, i4 - i6);
            } else {
                System.arraycopy(bArr, i, bArr2, bArr2.length - (i6 + i2), i2);
            }
            i5 = i6 + i2;
        }
        for (int length = bArr2.length - (2 * i4); length != bArr2.length; length += 2) {
            byte b = bArr2[(bArr2.length - i4) + (length / 2)];
            bArr2[length] = (byte) ((shadows[(b & 255) >>> 4] << 4) | shadows[b & 15]);
            bArr2[length + 1] = b;
        }
        int length2 = bArr2.length - (2 * i2);
        bArr2[length2] = (byte) (bArr2[length2] ^ i3);
        bArr2[bArr2.length - 1] = (byte) ((bArr2[bArr2.length - 1] << 4) | 6);
        int i7 = 8 - ((this.bitSize - 1) % 8);
        int i8 = 0;
        if (i7 != 8) {
            bArr2[0] = (byte) (bArr2[0] & (255 >>> i7));
            bArr2[0] = (byte) (bArr2[0] | (128 >>> i7));
        } else {
            bArr2[0] = 0;
            bArr2[1] = (byte) (bArr2[1] | 128);
            i8 = 1;
        }
        return this.engine.processBlock(bArr2, i8, bArr2.length - i8);
    }

    private byte[] decodeBlock(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        BigInteger bigIntegerSubtract;
        byte[] bArrProcessBlock = this.engine.processBlock(bArr, i, i2);
        int i3 = 1;
        int i4 = (this.bitSize + 13) / 16;
        BigInteger bigInteger = new BigInteger(1, bArrProcessBlock);
        if (bigInteger.mod(SIXTEEN).equals(SIX)) {
            bigIntegerSubtract = bigInteger;
        } else {
            if (!this.modulus.subtract(bigInteger).mod(SIXTEEN).equals(SIX)) {
                throw new InvalidCipherTextException("resulting integer iS or (modulus - iS) is not congruent to 6 mod 16");
            }
            bigIntegerSubtract = this.modulus.subtract(bigInteger);
        }
        byte[] bArrConvertOutputDecryptOnly = convertOutputDecryptOnly(bigIntegerSubtract);
        if ((bArrConvertOutputDecryptOnly[bArrConvertOutputDecryptOnly.length - 1] & 15) != 6) {
            throw new InvalidCipherTextException("invalid forcing byte in block");
        }
        bArrConvertOutputDecryptOnly[bArrConvertOutputDecryptOnly.length - 1] = (byte) (((bArrConvertOutputDecryptOnly[bArrConvertOutputDecryptOnly.length - 1] & 255) >>> 4) | (inverse[(bArrConvertOutputDecryptOnly[bArrConvertOutputDecryptOnly.length - 2] & 255) >> 4] << 4));
        bArrConvertOutputDecryptOnly[0] = (byte) ((shadows[(bArrConvertOutputDecryptOnly[1] & 255) >>> 4] << 4) | shadows[bArrConvertOutputDecryptOnly[1] & 15]);
        boolean z = false;
        int i5 = 0;
        for (int length = bArrConvertOutputDecryptOnly.length - 1; length >= bArrConvertOutputDecryptOnly.length - (2 * i4); length -= 2) {
            int i6 = (shadows[(bArrConvertOutputDecryptOnly[length] & 255) >>> 4] << 4) | shadows[bArrConvertOutputDecryptOnly[length] & 15];
            if (((bArrConvertOutputDecryptOnly[length - 1] ^ i6) & 255) != 0) {
                if (z) {
                    throw new InvalidCipherTextException("invalid tsums in block");
                }
                z = true;
                i3 = (bArrConvertOutputDecryptOnly[length - 1] ^ i6) & 255;
                i5 = length - 1;
            }
        }
        bArrConvertOutputDecryptOnly[i5] = 0;
        byte[] bArr2 = new byte[(bArrConvertOutputDecryptOnly.length - i5) / 2];
        for (int i7 = 0; i7 < bArr2.length; i7++) {
            bArr2[i7] = bArrConvertOutputDecryptOnly[(2 * i7) + i5 + 1];
        }
        this.padBits = i3 - 1;
        return bArr2;
    }

    private static byte[] convertOutputDecryptOnly(BigInteger bigInteger) {
        byte[] byteArray = bigInteger.toByteArray();
        if (byteArray[0] != 0) {
            return byteArray;
        }
        byte[] bArr = new byte[byteArray.length - 1];
        System.arraycopy(byteArray, 1, bArr, 0, bArr.length);
        return bArr;
    }
}
