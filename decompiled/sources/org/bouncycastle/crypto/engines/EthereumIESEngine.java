package org.bouncycastle.crypto.engines;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.DigestDerivationFunction;
import org.bouncycastle.crypto.EphemeralKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.KeyParser;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.SavableDigest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.EphemeralKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.IESParameters;
import org.bouncycastle.crypto.params.IESWithCipherParameters;
import org.bouncycastle.crypto.params.ISO18033KDFParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/EthereumIESEngine.class */
public class EthereumIESEngine {
    BasicAgreement agree;
    DerivationFunction kdf;
    Mac mac;
    BufferedBlockCipher cipher;
    byte[] macBuf;
    byte[] commonMac;
    boolean forEncryption;
    CipherParameters privParam;
    CipherParameters pubParam;
    IESParameters param;
    byte[] V;
    private EphemeralKeyPairGenerator keyPairGenerator;
    private KeyParser keyParser;
    private byte[] IV;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/EthereumIESEngine$HandshakeKDFFunction.class */
    public static class HandshakeKDFFunction implements DigestDerivationFunction {
        private int counterStart;
        private Digest digest;
        private byte[] shared;
        private byte[] iv;

        public HandshakeKDFFunction(int i, Digest digest) {
            this.counterStart = i;
            this.digest = digest;
        }

        @Override // org.bouncycastle.crypto.DerivationFunction
        public void init(DerivationParameters derivationParameters) {
            if (derivationParameters instanceof KDFParameters) {
                KDFParameters kDFParameters = (KDFParameters) derivationParameters;
                this.shared = kDFParameters.getSharedSecret();
                this.iv = kDFParameters.getIV();
            } else {
                if (!(derivationParameters instanceof ISO18033KDFParameters)) {
                    throw new IllegalArgumentException("KDF parameters required for generator");
                }
                this.shared = ((ISO18033KDFParameters) derivationParameters).getSeed();
                this.iv = null;
            }
        }

        @Override // org.bouncycastle.crypto.DigestDerivationFunction
        public Digest getDigest() {
            return this.digest;
        }

        @Override // org.bouncycastle.crypto.DerivationFunction
        public int generateBytes(byte[] bArr, int i, int i2) throws DataLengthException, IllegalArgumentException {
            if (bArr.length - i2 < i) {
                throw new OutputLengthException("output buffer too small");
            }
            long j = i2;
            int digestSize = this.digest.getDigestSize();
            if (j > 8589934591L) {
                throw new IllegalArgumentException("output length too large");
            }
            int i3 = (int) (((j + ((long) digestSize)) - 1) / ((long) digestSize));
            byte[] bArr2 = new byte[this.digest.getDigestSize()];
            byte[] bArr3 = new byte[4];
            Pack.intToBigEndian(this.counterStart, bArr3, 0);
            int i4 = this.counterStart & (-256);
            for (int i5 = 0; i5 < i3; i5++) {
                this.digest.update(bArr3, 0, bArr3.length);
                this.digest.update(this.shared, 0, this.shared.length);
                if (this.iv != null) {
                    this.digest.update(this.iv, 0, this.iv.length);
                }
                this.digest.doFinal(bArr2, 0);
                if (i2 > digestSize) {
                    System.arraycopy(bArr2, 0, bArr, i, digestSize);
                    i += digestSize;
                    i2 -= digestSize;
                } else {
                    System.arraycopy(bArr2, 0, bArr, i, i2);
                }
                byte b = (byte) (bArr3[3] + 1);
                bArr3[3] = b;
                if (b == 0) {
                    i4 += 256;
                    Pack.intToBigEndian(i4, bArr3, 0);
                }
            }
            this.digest.reset();
            return (int) j;
        }
    }

    public EthereumIESEngine(BasicAgreement basicAgreement, DerivationFunction derivationFunction, Mac mac, byte[] bArr) {
        this.agree = basicAgreement;
        this.kdf = derivationFunction;
        this.mac = mac;
        this.macBuf = new byte[mac.getMacSize()];
        this.commonMac = bArr;
        this.cipher = null;
    }

    public EthereumIESEngine(BasicAgreement basicAgreement, DerivationFunction derivationFunction, Mac mac, byte[] bArr, BufferedBlockCipher bufferedBlockCipher) {
        this.agree = basicAgreement;
        this.kdf = derivationFunction;
        this.mac = mac;
        this.macBuf = new byte[mac.getMacSize()];
        this.commonMac = bArr;
        this.cipher = bufferedBlockCipher;
    }

    public void init(boolean z, CipherParameters cipherParameters, CipherParameters cipherParameters2, CipherParameters cipherParameters3) {
        this.forEncryption = z;
        this.privParam = cipherParameters;
        this.pubParam = cipherParameters2;
        this.V = new byte[0];
        extractParams(cipherParameters3);
    }

    public void init(AsymmetricKeyParameter asymmetricKeyParameter, CipherParameters cipherParameters, EphemeralKeyPairGenerator ephemeralKeyPairGenerator) {
        this.forEncryption = true;
        this.pubParam = asymmetricKeyParameter;
        this.keyPairGenerator = ephemeralKeyPairGenerator;
        extractParams(cipherParameters);
    }

    public void init(AsymmetricKeyParameter asymmetricKeyParameter, CipherParameters cipherParameters, KeyParser keyParser) {
        this.forEncryption = false;
        this.privParam = asymmetricKeyParameter;
        this.keyParser = keyParser;
        extractParams(cipherParameters);
    }

    private void extractParams(CipherParameters cipherParameters) {
        if (cipherParameters instanceof ParametersWithIV) {
            this.IV = ((ParametersWithIV) cipherParameters).getIV();
            this.param = (IESParameters) ((ParametersWithIV) cipherParameters).getParameters();
        } else {
            this.IV = null;
            this.param = (IESParameters) cipherParameters;
        }
    }

    public BufferedBlockCipher getCipher() {
        return this.cipher;
    }

    public Mac getMac() {
        return this.mac;
    }

    private byte[] encryptBlock(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        byte[] bArr2;
        byte[] bArr3;
        int iDoFinal;
        if (this.cipher == null) {
            byte[] bArr4 = new byte[i2];
            bArr2 = new byte[this.param.getMacKeySize() / 8];
            byte[] bArr5 = new byte[bArr4.length + bArr2.length];
            this.kdf.generateBytes(bArr5, 0, bArr5.length);
            if (this.V.length != 0) {
                System.arraycopy(bArr5, 0, bArr2, 0, bArr2.length);
                System.arraycopy(bArr5, bArr2.length, bArr4, 0, bArr4.length);
            } else {
                System.arraycopy(bArr5, 0, bArr4, 0, bArr4.length);
                System.arraycopy(bArr5, i2, bArr2, 0, bArr2.length);
            }
            bArr3 = new byte[i2];
            for (int i3 = 0; i3 != i2; i3++) {
                bArr3[i3] = (byte) (bArr[i + i3] ^ bArr4[i3]);
            }
            iDoFinal = i2;
        } else {
            byte[] bArr6 = new byte[((IESWithCipherParameters) this.param).getCipherKeySize() / 8];
            bArr2 = new byte[this.param.getMacKeySize() / 8];
            byte[] bArr7 = new byte[bArr6.length + bArr2.length];
            this.kdf.generateBytes(bArr7, 0, bArr7.length);
            System.arraycopy(bArr7, 0, bArr6, 0, bArr6.length);
            System.arraycopy(bArr7, bArr6.length, bArr2, 0, bArr2.length);
            if (this.IV != null) {
                this.cipher.init(true, new ParametersWithIV(new KeyParameter(bArr6), this.IV));
            } else {
                this.cipher.init(true, new KeyParameter(bArr6));
            }
            bArr3 = new byte[this.cipher.getOutputSize(i2)];
            int iProcessBytes = this.cipher.processBytes(bArr, i, i2, bArr3, 0);
            iDoFinal = iProcessBytes + this.cipher.doFinal(bArr3, iProcessBytes);
        }
        byte[] encodingV = this.param.getEncodingV();
        byte[] lengthTag = this.V.length != 0 ? getLengthTag(encodingV) : null;
        byte[] bArr8 = new byte[this.mac.getMacSize()];
        SavableDigest savableDigestNewInstance = SHA256Digest.newInstance();
        byte[] bArr9 = new byte[savableDigestNewInstance.getDigestSize()];
        savableDigestNewInstance.reset();
        savableDigestNewInstance.update(bArr2, 0, bArr2.length);
        savableDigestNewInstance.doFinal(bArr9, 0);
        this.mac.init(new KeyParameter(bArr9));
        this.mac.update(this.IV, 0, this.IV.length);
        this.mac.update(bArr3, 0, bArr3.length);
        if (encodingV != null) {
            this.mac.update(encodingV, 0, encodingV.length);
        }
        if (this.V.length != 0) {
            this.mac.update(lengthTag, 0, lengthTag.length);
        }
        this.mac.update(this.commonMac, 0, this.commonMac.length);
        this.mac.doFinal(bArr8, 0);
        byte[] bArr10 = new byte[this.V.length + iDoFinal + bArr8.length];
        System.arraycopy(this.V, 0, bArr10, 0, this.V.length);
        System.arraycopy(bArr3, 0, bArr10, this.V.length, iDoFinal);
        System.arraycopy(bArr8, 0, bArr10, this.V.length + iDoFinal, bArr8.length);
        return bArr10;
    }

    private byte[] decryptBlock(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        byte[] bArr2;
        byte[] bArr3;
        int iProcessBytes = 0;
        if (i2 < this.V.length + this.mac.getMacSize()) {
            throw new InvalidCipherTextException("length of input must be greater than the MAC and V combined");
        }
        if (this.cipher == null) {
            byte[] bArr4 = new byte[(i2 - this.V.length) - this.mac.getMacSize()];
            bArr2 = new byte[this.param.getMacKeySize() / 8];
            byte[] bArr5 = new byte[bArr4.length + bArr2.length];
            this.kdf.generateBytes(bArr5, 0, bArr5.length);
            if (this.V.length != 0) {
                System.arraycopy(bArr5, 0, bArr2, 0, bArr2.length);
                System.arraycopy(bArr5, bArr2.length, bArr4, 0, bArr4.length);
            } else {
                System.arraycopy(bArr5, 0, bArr4, 0, bArr4.length);
                System.arraycopy(bArr5, bArr4.length, bArr2, 0, bArr2.length);
            }
            bArr3 = new byte[bArr4.length];
            for (int i3 = 0; i3 != bArr4.length; i3++) {
                bArr3[i3] = (byte) (bArr[(i + this.V.length) + i3] ^ bArr4[i3]);
            }
        } else {
            byte[] bArr6 = new byte[((IESWithCipherParameters) this.param).getCipherKeySize() / 8];
            bArr2 = new byte[this.param.getMacKeySize() / 8];
            byte[] bArr7 = new byte[bArr6.length + bArr2.length];
            this.kdf.generateBytes(bArr7, 0, bArr7.length);
            System.arraycopy(bArr7, 0, bArr6, 0, bArr6.length);
            System.arraycopy(bArr7, bArr6.length, bArr2, 0, bArr2.length);
            CipherParameters keyParameter = new KeyParameter(bArr6);
            if (this.IV != null) {
                keyParameter = new ParametersWithIV(keyParameter, this.IV);
            }
            this.cipher.init(false, keyParameter);
            bArr3 = new byte[this.cipher.getOutputSize((i2 - this.V.length) - this.mac.getMacSize())];
            iProcessBytes = this.cipher.processBytes(bArr, i + this.V.length, (i2 - this.V.length) - this.mac.getMacSize(), bArr3, 0);
        }
        byte[] encodingV = this.param.getEncodingV();
        byte[] lengthTag = this.V.length != 0 ? getLengthTag(encodingV) : null;
        int i4 = i + i2;
        byte[] bArrCopyOfRange = Arrays.copyOfRange(bArr, i4 - this.mac.getMacSize(), i4);
        byte[] bArr8 = new byte[bArrCopyOfRange.length];
        SavableDigest savableDigestNewInstance = SHA256Digest.newInstance();
        byte[] bArr9 = new byte[savableDigestNewInstance.getDigestSize()];
        savableDigestNewInstance.reset();
        savableDigestNewInstance.update(bArr2, 0, bArr2.length);
        savableDigestNewInstance.doFinal(bArr9, 0);
        this.mac.init(new KeyParameter(bArr9));
        this.mac.update(this.IV, 0, this.IV.length);
        this.mac.update(bArr, i + this.V.length, (i2 - this.V.length) - bArr8.length);
        if (encodingV != null) {
            this.mac.update(encodingV, 0, encodingV.length);
        }
        if (this.V.length != 0) {
            this.mac.update(lengthTag, 0, lengthTag.length);
        }
        this.mac.update(this.commonMac, 0, this.commonMac.length);
        this.mac.doFinal(bArr8, 0);
        if (!Arrays.constantTimeAreEqual(bArrCopyOfRange, bArr8)) {
            throw new InvalidCipherTextException("invalid MAC");
        }
        if (this.cipher == null) {
            return bArr3;
        }
        return Arrays.copyOfRange(bArr3, 0, iProcessBytes + this.cipher.doFinal(bArr3, iProcessBytes));
    }

    public byte[] processBlock(byte[] bArr, int i, int i2) throws InvalidCipherTextException {
        if (this.forEncryption) {
            if (this.keyPairGenerator != null) {
                EphemeralKeyPair ephemeralKeyPairGenerate = this.keyPairGenerator.generate();
                this.privParam = ephemeralKeyPairGenerate.getKeyPair().getPrivate();
                this.V = ephemeralKeyPairGenerate.getEncodedPublicKey();
            }
        } else if (this.keyParser != null) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr, i, i2);
            try {
                this.pubParam = this.keyParser.readKey(byteArrayInputStream);
                this.V = Arrays.copyOfRange(bArr, i, i + (i2 - byteArrayInputStream.available()));
            } catch (IOException e) {
                throw new InvalidCipherTextException("unable to recover ephemeral public key: " + e.getMessage(), e);
            } catch (IllegalArgumentException e2) {
                throw new InvalidCipherTextException("unable to recover ephemeral public key: " + e2.getMessage(), e2);
            }
        }
        this.agree.init(this.privParam);
        byte[] bArrAsUnsignedByteArray = BigIntegers.asUnsignedByteArray(this.agree.getFieldSize(), this.agree.calculateAgreement(this.pubParam));
        if (this.V.length != 0) {
            byte[] bArrConcatenate = Arrays.concatenate(this.V, bArrAsUnsignedByteArray);
            Arrays.fill(bArrAsUnsignedByteArray, (byte) 0);
            bArrAsUnsignedByteArray = bArrConcatenate;
        }
        try {
            this.kdf.init(new KDFParameters(bArrAsUnsignedByteArray, this.param.getDerivationV()));
            return this.forEncryption ? encryptBlock(bArr, i, i2) : decryptBlock(bArr, i, i2);
        } finally {
            Arrays.fill(bArrAsUnsignedByteArray, (byte) 0);
        }
    }

    protected byte[] getLengthTag(byte[] bArr) {
        byte[] bArr2 = new byte[8];
        if (bArr != null) {
            Pack.longToBigEndian(((long) bArr.length) * 8, bArr2, 0);
        }
        return bArr2;
    }
}
