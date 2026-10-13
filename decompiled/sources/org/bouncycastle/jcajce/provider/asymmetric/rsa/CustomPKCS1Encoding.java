package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/CustomPKCS1Encoding.class */
class CustomPKCS1Encoding implements AsymmetricBlockCipher {
    private static final int HEADER_LENGTH = 10;
    private SecureRandom random;
    private AsymmetricBlockCipher engine;
    private boolean forEncryption;
    private boolean forPrivateKey;
    private boolean useStrictLength = useStrict();
    private byte[] blockBuffer;

    CustomPKCS1Encoding(AsymmetricBlockCipher asymmetricBlockCipher) {
        this.engine = asymmetricBlockCipher;
    }

    private boolean useStrict() {
        return (Properties.isOverrideSetTo(PKCS1Encoding.NOT_STRICT_LENGTH_ENABLED_PROPERTY, true) || Properties.isOverrideSetTo(PKCS1Encoding.STRICT_LENGTH_ENABLED_PROPERTY, false)) ? false : true;
    }

    public AsymmetricBlockCipher getUnderlyingCipher() {
        return this.engine;
    }

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        AsymmetricKeyParameter asymmetricKeyParameter;
        if (cipherParameters instanceof ParametersWithRandom) {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom) cipherParameters;
            this.random = parametersWithRandom.getRandom();
            asymmetricKeyParameter = (AsymmetricKeyParameter) parametersWithRandom.getParameters();
        } else {
            asymmetricKeyParameter = (AsymmetricKeyParameter) cipherParameters;
            if (!asymmetricKeyParameter.isPrivate() && z) {
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
        }
        this.engine.init(z, cipherParameters);
        this.forPrivateKey = asymmetricKeyParameter.isPrivate();
        this.forEncryption = z;
        this.blockBuffer = new byte[this.engine.getOutputBlockSize()];
    }

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public int getInputBlockSize() {
        int inputBlockSize = this.engine.getInputBlockSize();
        return this.forEncryption ? inputBlockSize - 10 : inputBlockSize;
    }

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public int getOutputBlockSize() {
        int outputBlockSize = this.engine.getOutputBlockSize();
        return this.forEncryption ? outputBlockSize : outputBlockSize - 10;
    }

    @Override // org.bouncycastle.crypto.AsymmetricBlockCipher
    public byte[] processBlock(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        return this.forEncryption ? encodeBlock(bArr, i, i2) : decodeBlock(bArr, i, i2);
    }

    private byte[] encodeBlock(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        if (i2 > getInputBlockSize()) {
            throw new IllegalArgumentException("input data too large");
        }
        byte[] bArr2 = new byte[this.engine.getInputBlockSize()];
        if (this.forPrivateKey) {
            bArr2[0] = 1;
            for (int i3 = 1; i3 != (bArr2.length - i2) - 1; i3++) {
                bArr2[i3] = -1;
            }
        } else {
            this.random.nextBytes(bArr2);
            bArr2[0] = 2;
            for (int i4 = 1; i4 != (bArr2.length - i2) - 1; i4++) {
                while (bArr2[i4] == 0) {
                    bArr2[i4] = (byte) this.random.nextInt();
                }
            }
        }
        bArr2[(bArr2.length - i2) - 1] = 0;
        System.arraycopy(bArr, i, bArr2, bArr2.length - i2, i2);
        return this.engine.processBlock(bArr2, 0, bArr2.length);
    }

    private static int checkPkcs1Encoding1(byte[] bArr) {
        int i = 0;
        int i2 = 0;
        int i3 = -((bArr[0] & 255) ^ 1);
        for (int i4 = 1; i4 < bArr.length; i4++) {
            int i5 = bArr[i4] & 255;
            int i6 = ((i5 ^ 0) - 1) >> 31;
            i2 ^= (i4 & (i ^ (-1))) & i6;
            i |= i6;
            i3 |= (i | (((i5 ^ 255) - 1) >> 31)) ^ (-1);
        }
        return ((bArr.length - 1) - i2) | ((i3 | (i2 - 9)) >> 31);
    }

    private static int checkPkcs1Encoding2(byte[] bArr) {
        int i = 0;
        int i2 = 0;
        int i3 = -((bArr[0] & 255) ^ 2);
        for (int i4 = 1; i4 < bArr.length; i4++) {
            int i5 = (((bArr[i4] & 255) ^ 0) - 1) >> 31;
            i2 ^= (i4 & (i ^ (-1))) & i5;
            i |= i5;
        }
        return ((bArr.length - 1) - i2) | ((i3 | (i2 - 9)) >> 31);
    }

    private byte[] decodeBlock(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        int outputBlockSize = this.engine.getOutputBlockSize();
        byte[] bArrProcessBlock = this.engine.processBlock(bArr, i, i2);
        boolean z = this.useStrictLength & (bArrProcessBlock.length != outputBlockSize);
        byte[] bArr2 = bArrProcessBlock;
        if (bArrProcessBlock.length < outputBlockSize) {
            bArr2 = this.blockBuffer;
        }
        int iCheckPkcs1Encoding2 = this.forPrivateKey ? checkPkcs1Encoding2(bArr2) : checkPkcs1Encoding1(bArr2);
        try {
            if ((iCheckPkcs1Encoding2 < 0) || z) {
                return null;
            }
            byte[] bArr3 = new byte[iCheckPkcs1Encoding2];
            System.arraycopy(bArr2, bArr2.length - iCheckPkcs1Encoding2, bArr3, 0, iCheckPkcs1Encoding2);
            return bArr3;
        } finally {
            Arrays.fill(bArrProcessBlock, (byte) 0);
            Arrays.fill(this.blockBuffer, 0, Math.max(0, this.blockBuffer.length - bArrProcessBlock.length), (byte) 0);
        }
    }
}
