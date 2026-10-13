package org.spongycastle.crypto.encodings;

import java.math.BigInteger;
import org.spongycastle.crypto.AsymmetricBlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.params.RSAKeyParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/encodings/ISO9796d1Encoding.class */
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

    public ISO9796d1Encoding(AsymmetricBlockCipher cipher) {
        this.engine = cipher;
    }

    public AsymmetricBlockCipher getUnderlyingCipher() {
        return this.engine;
    }

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public void init(boolean forEncryption, CipherParameters param) {
        RSAKeyParameters kParam;
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom) param;
            kParam = (RSAKeyParameters) rParam.getParameters();
        } else {
            kParam = (RSAKeyParameters) param;
        }
        this.engine.init(forEncryption, param);
        this.modulus = kParam.getModulus();
        this.bitSize = this.modulus.bitLength();
        this.forEncryption = forEncryption;
    }

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public int getInputBlockSize() {
        int baseBlockSize = this.engine.getInputBlockSize();
        if (this.forEncryption) {
            return (baseBlockSize + 1) / 2;
        }
        return baseBlockSize;
    }

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public int getOutputBlockSize() {
        int baseBlockSize = this.engine.getOutputBlockSize();
        if (this.forEncryption) {
            return baseBlockSize;
        }
        return (baseBlockSize + 1) / 2;
    }

    public void setPadBits(int padBits) {
        if (padBits > 7) {
            throw new IllegalArgumentException("padBits > 7");
        }
        this.padBits = padBits;
    }

    public int getPadBits() {
        return this.padBits;
    }

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public byte[] processBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.forEncryption) {
            return encodeBlock(in, inOff, inLen);
        }
        return decodeBlock(in, inOff, inLen);
    }

    private byte[] encodeBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        byte[] block = new byte[(this.bitSize + 7) / 8];
        int r = this.padBits + 1;
        int t = (this.bitSize + 13) / 16;
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= t) {
                break;
            }
            if (i2 > t - inLen) {
                System.arraycopy(in, (inOff + inLen) - (t - i2), block, block.length - t, t - i2);
            } else {
                System.arraycopy(in, inOff, block, block.length - (i2 + inLen), inLen);
            }
            i = i2 + inLen;
        }
        for (int i3 = block.length - (2 * t); i3 != block.length; i3 += 2) {
            byte val = block[(block.length - t) + (i3 / 2)];
            block[i3] = (byte) ((shadows[(val & 255) >>> 4] << 4) | shadows[val & 15]);
            block[i3 + 1] = val;
        }
        int length = block.length - (2 * inLen);
        block[length] = (byte) (block[length] ^ r);
        block[block.length - 1] = (byte) ((block[block.length - 1] << 4) | 6);
        int maxBit = 8 - ((this.bitSize - 1) % 8);
        int offSet = 0;
        if (maxBit != 8) {
            block[0] = (byte) (block[0] & (255 >>> maxBit));
            block[0] = (byte) (block[0] | (128 >>> maxBit));
        } else {
            block[0] = 0;
            block[1] = (byte) (block[1] | 128);
            offSet = 1;
        }
        return this.engine.processBlock(block, offSet, block.length - offSet);
    }

    private byte[] decodeBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        BigInteger iR;
        byte[] block = this.engine.processBlock(in, inOff, inLen);
        int r = 1;
        int t = (this.bitSize + 13) / 16;
        BigInteger iS = new BigInteger(1, block);
        if (iS.mod(SIXTEEN).equals(SIX)) {
            iR = iS;
        } else if (this.modulus.subtract(iS).mod(SIXTEEN).equals(SIX)) {
            iR = this.modulus.subtract(iS);
        } else {
            throw new InvalidCipherTextException("resulting integer iS or (modulus - iS) is not congruent to 6 mod 16");
        }
        byte[] block2 = convertOutputDecryptOnly(iR);
        if ((block2[block2.length - 1] & 15) != 6) {
            throw new InvalidCipherTextException("invalid forcing byte in block");
        }
        block2[block2.length - 1] = (byte) (((block2[block2.length - 1] & 255) >>> 4) | (inverse[(block2[block2.length - 2] & 255) >> 4] << 4));
        block2[0] = (byte) ((shadows[(block2[1] & 255) >>> 4] << 4) | shadows[block2[1] & 15]);
        boolean boundaryFound = false;
        int boundary = 0;
        for (int i = block2.length - 1; i >= block2.length - (2 * t); i -= 2) {
            int val = (shadows[(block2[i] & 255) >>> 4] << 4) | shadows[block2[i] & 15];
            if (((block2[i - 1] ^ val) & 255) != 0) {
                if (!boundaryFound) {
                    boundaryFound = true;
                    r = (block2[i - 1] ^ val) & 255;
                    boundary = i - 1;
                } else {
                    throw new InvalidCipherTextException("invalid tsums in block");
                }
            }
        }
        block2[boundary] = 0;
        byte[] nblock = new byte[(block2.length - boundary) / 2];
        for (int i2 = 0; i2 < nblock.length; i2++) {
            nblock[i2] = block2[(2 * i2) + boundary + 1];
        }
        this.padBits = r - 1;
        return nblock;
    }

    private static byte[] convertOutputDecryptOnly(BigInteger result) {
        byte[] output = result.toByteArray();
        if (output[0] == 0) {
            byte[] tmp = new byte[output.length - 1];
            System.arraycopy(output, 1, tmp, 0, tmp.length);
            return tmp;
        }
        return output;
    }
}
