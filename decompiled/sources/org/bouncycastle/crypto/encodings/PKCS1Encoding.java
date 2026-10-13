package org.bouncycastle.crypto.encodings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/encodings/PKCS1Encoding.class */
public class PKCS1Encoding implements AsymmetricBlockCipher {
    public static final String STRICT_LENGTH_ENABLED_PROPERTY = "org.bouncycastle.pkcs1.strict";
    public static final String NOT_STRICT_LENGTH_ENABLED_PROPERTY = "org.bouncycastle.pkcs1.not_strict";
    private static final int HEADER_LENGTH = 10;
    private SecureRandom random;
    private AsymmetricBlockCipher engine;
    private boolean forEncryption;
    private boolean forPrivateKey;
    private boolean useStrictLength;
    private int pLen;
    private byte[] fallback;
    private byte[] blockBuffer;

    public PKCS1Encoding(AsymmetricBlockCipher asymmetricBlockCipher) {
        this.pLen = -1;
        this.fallback = null;
        this.engine = asymmetricBlockCipher;
        this.useStrictLength = useStrict();
    }

    public PKCS1Encoding(AsymmetricBlockCipher asymmetricBlockCipher, int i) {
        this.pLen = -1;
        this.fallback = null;
        this.engine = asymmetricBlockCipher;
        this.useStrictLength = useStrict();
        this.pLen = i;
    }

    public PKCS1Encoding(AsymmetricBlockCipher asymmetricBlockCipher, byte[] bArr) {
        this.pLen = -1;
        this.fallback = null;
        this.engine = asymmetricBlockCipher;
        this.useStrictLength = useStrict();
        this.fallback = bArr;
        this.pLen = bArr.length;
    }

    private boolean useStrict() {
        return (Properties.isOverrideSetTo(NOT_STRICT_LENGTH_ENABLED_PROPERTY, true) || Properties.isOverrideSetTo(STRICT_LENGTH_ENABLED_PROPERTY, false)) ? false : true;
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
        if (this.pLen > 0 && this.fallback == null && this.random == null) {
            throw new IllegalArgumentException("encoder requires random");
        }
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

    private static int checkPkcs1Encoding2(byte[] bArr, int i) {
        int i2 = -((bArr[0] & 255) ^ 2);
        int length = (bArr.length - 1) - i;
        int i3 = i2 | (length - 9);
        for (int i4 = 1; i4 < length; i4++) {
            i3 |= (bArr[i4] & 255) - 1;
        }
        return (i3 | (-(bArr[length] & 255))) >> 31;
    }

    private byte[] decodeBlockOrRandom(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        if (!this.forPrivateKey) {
            throw new InvalidCipherTextException("sorry, this method is only for decryption, not for signing");
        }
        int i3 = this.pLen;
        byte[] bArr2 = this.fallback;
        if (this.fallback == null) {
            bArr2 = new byte[i3];
            this.random.nextBytes(bArr2);
        }
        int outputBlockSize = this.engine.getOutputBlockSize();
        byte[] bArrProcessBlock = this.engine.processBlock(bArr, i, i2);
        byte[] bArr3 = bArrProcessBlock;
        if (bArrProcessBlock.length != outputBlockSize && (this.useStrictLength || bArrProcessBlock.length < outputBlockSize)) {
            bArr3 = this.blockBuffer;
        }
        int iCheckPkcs1Encoding2 = 0 | checkPkcs1Encoding2(bArr3, i3);
        int length = bArr3.length - i3;
        byte[] bArr4 = new byte[i3];
        for (int i4 = 0; i4 < i3; i4++) {
            bArr4[i4] = (byte) ((bArr3[length + i4] & (iCheckPkcs1Encoding2 ^ (-1))) | (bArr2[i4] & iCheckPkcs1Encoding2));
        }
        Arrays.fill(bArrProcessBlock, (byte) 0);
        Arrays.fill(this.blockBuffer, 0, Math.max(0, this.blockBuffer.length - bArrProcessBlock.length), (byte) 0);
        return bArr4;
    }

    private byte[] decodeBlock(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        if (this.forPrivateKey && this.pLen != -1) {
            return decodeBlockOrRandom(bArr, i, i2);
        }
        int outputBlockSize = this.engine.getOutputBlockSize();
        byte[] bArrProcessBlock = this.engine.processBlock(bArr, i, i2);
        boolean z = this.useStrictLength & (bArrProcessBlock.length != outputBlockSize);
        byte[] bArr2 = bArrProcessBlock;
        if (bArrProcessBlock.length < outputBlockSize) {
            bArr2 = this.blockBuffer;
        }
        int iCheckPkcs1Encoding2 = this.forPrivateKey ? checkPkcs1Encoding2(bArr2) : checkPkcs1Encoding1(bArr2);
        try {
            if (iCheckPkcs1Encoding2 < 0) {
                throw new InvalidCipherTextException("block incorrect");
            }
            if (z) {
                throw new InvalidCipherTextException("block incorrect size");
            }
            byte[] bArr3 = new byte[iCheckPkcs1Encoding2];
            System.arraycopy(bArr2, bArr2.length - iCheckPkcs1Encoding2, bArr3, 0, iCheckPkcs1Encoding2);
            Arrays.fill(bArrProcessBlock, (byte) 0);
            Arrays.fill(this.blockBuffer, 0, Math.max(0, this.blockBuffer.length - bArrProcessBlock.length), (byte) 0);
            return bArr3;
        } catch (Throwable th) {
            Arrays.fill(bArrProcessBlock, (byte) 0);
            Arrays.fill(this.blockBuffer, 0, Math.max(0, this.blockBuffer.length - bArrProcessBlock.length), (byte) 0);
            throw th;
        }
    }
}
