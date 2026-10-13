package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.EphemeralKeyPair;
import org.bouncycastle.crypto.KeyEncoder;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.agreement.ECDHCBasicAgreement;
import org.bouncycastle.crypto.engines.IESEngine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.EphemeralKeyPairGenerator;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi;
import org.bouncycastle.jcajce.spec.IESKEMParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/IESKEMCipher.class */
public class IESKEMCipher extends BaseCipherSpi {
    private static final X9IntegerConverter converter = new X9IntegerConverter();
    private final ECDHCBasicAgreement agreement;
    private final KDF2BytesGenerator kdf;
    private final Mac hMac;
    private final int macKeyLength;
    private final int macLength;
    private int ivLength;
    private IESEngine engine;
    private AsymmetricKeyParameter key;
    private SecureRandom random;
    private final JcaJceHelper helper = new BCJcaJceHelper();
    private int state = -1;
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private AlgorithmParameters engineParam = null;
    private IESKEMParameterSpec engineSpec = null;
    private boolean dhaesMode = false;
    private AsymmetricKeyParameter otherKeyParameter = null;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/IESKEMCipher$KEM.class */
    public static class KEM extends IESKEMCipher {
        public KEM(Digest digest, Digest digest2, int i, int i2) {
            super(new ECDHCBasicAgreement(), new KDF2BytesGenerator(digest), new HMac(digest2), i, i2);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/IESKEMCipher$KEMwithSHA256.class */
    public static class KEMwithSHA256 extends KEM {
        public KEMwithSHA256() {
            super(DigestFactory.createSHA256(), DigestFactory.createSHA256(), 32, 16);
        }
    }

    public IESKEMCipher(ECDHCBasicAgreement eCDHCBasicAgreement, KDF2BytesGenerator kDF2BytesGenerator, Mac mac, int i, int i2) {
        this.agreement = eCDHCBasicAgreement;
        this.kdf = kDF2BytesGenerator;
        this.hMac = mac;
        this.macKeyLength = i;
        this.macLength = i2;
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    public int engineGetBlockSize() {
        return 0;
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    public int engineGetKeySize(Key key) {
        if (key instanceof ECKey) {
            return ((ECKey) key).getParameters().getCurve().getFieldSize();
        }
        throw new IllegalArgumentException("not an EC key");
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    public byte[] engineGetIV() {
        return null;
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    public AlgorithmParameters engineGetParameters() {
        if (this.engineParam == null && this.engineSpec != null) {
            try {
                this.engineParam = this.helper.createAlgorithmParameters("IES");
                this.engineParam.init(this.engineSpec);
            } catch (Exception e) {
                throw new RuntimeException(e.toString());
            }
        }
        return this.engineParam;
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    public void engineSetMode(String str) throws NoSuchAlgorithmException {
        throw new NoSuchAlgorithmException("can't support mode " + str);
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    public int engineGetOutputSize(int i) {
        int outputSize;
        if (this.key == null) {
            throw new IllegalStateException("cipher not initialised");
        }
        int macSize = this.engine.getMac().getMacSize();
        int fieldSize = this.otherKeyParameter == null ? 2 * ((((ECKeyParameters) this.key).getParameters().getCurve().getFieldSize() + 7) / 8) : 0;
        int size = this.buffer.size() + i;
        if (this.engine.getCipher() == null) {
            outputSize = size;
        } else if (this.state == 1 || this.state == 3) {
            outputSize = this.engine.getCipher().getOutputSize(size);
        } else {
            if (this.state != 2 && this.state != 4) {
                throw new IllegalStateException("cipher not initialised");
            }
            outputSize = this.engine.getCipher().getOutputSize((size - macSize) - fieldSize);
        }
        if (this.state == 1 || this.state == 3) {
            return macSize + fieldSize + outputSize;
        }
        if (this.state == 2 || this.state == 4) {
            return outputSize;
        }
        throw new IllegalStateException("cipher not initialised");
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    public void engineSetPadding(String str) throws NoSuchPaddingException {
        throw new NoSuchPaddingException("padding not available with IESCipher");
    }

    @Override // javax.crypto.CipherSpi
    public void engineInit(int i, Key key, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec parameterSpec = null;
        if (algorithmParameters != null) {
            try {
                parameterSpec = algorithmParameters.getParameterSpec(IESParameterSpec.class);
            } catch (Exception e) {
                throw new InvalidAlgorithmParameterException("cannot recognise parameters: " + e.toString());
            }
        }
        this.engineParam = algorithmParameters;
        engineInit(i, key, parameterSpec, secureRandom);
    }

    @Override // javax.crypto.CipherSpi
    public void engineInit(int i, Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.otherKeyParameter = null;
        this.engineSpec = (IESKEMParameterSpec) algorithmParameterSpec;
        if (i == 1 || i == 3) {
            if (!(key instanceof PublicKey)) {
                throw new InvalidKeyException("must be passed recipient's public EC key for encryption");
            }
            this.key = ECUtils.generatePublicKeyParameter((PublicKey) key);
        } else {
            if (i != 2 && i != 4) {
                throw new InvalidKeyException("must be passed EC key");
            }
            if (!(key instanceof PrivateKey)) {
                throw new InvalidKeyException("must be passed recipient's private EC key for decryption");
            }
            this.key = ECUtils.generatePrivateKeyParameter((PrivateKey) key);
        }
        this.random = secureRandom;
        this.state = i;
        this.buffer.reset();
    }

    @Override // javax.crypto.CipherSpi
    public void engineInit(int i, Key key, SecureRandom secureRandom) throws InvalidKeyException {
        try {
            engineInit(i, key, (AlgorithmParameterSpec) null, secureRandom);
        } catch (InvalidAlgorithmParameterException e) {
            throw new IllegalArgumentException("cannot handle supplied parameter spec: " + e.getMessage());
        }
    }

    @Override // javax.crypto.CipherSpi
    public byte[] engineUpdate(byte[] bArr, int i, int i2) {
        this.buffer.write(bArr, i, i2);
        return null;
    }

    @Override // javax.crypto.CipherSpi
    public int engineUpdate(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        this.buffer.write(bArr, i, i2);
        return 0;
    }

    @Override // javax.crypto.CipherSpi
    public byte[] engineDoFinal(byte[] bArr, int i, int i2) throws BadPaddingException, IllegalBlockSizeException {
        if (i2 != 0) {
            this.buffer.write(bArr, i, i2);
        }
        this.buffer.toByteArray();
        this.buffer.reset();
        ECDomainParameters parameters = ((ECKeyParameters) this.key).getParameters();
        if (this.state == 1 || this.state == 3) {
            ECKeyPairGenerator eCKeyPairGenerator = new ECKeyPairGenerator();
            eCKeyPairGenerator.init(new ECKeyGenerationParameters(parameters, this.random));
            final boolean zHasUsePointCompression = this.engineSpec.hasUsePointCompression();
            EphemeralKeyPair ephemeralKeyPairGenerate = new EphemeralKeyPairGenerator(eCKeyPairGenerator, new KeyEncoder() { // from class: org.bouncycastle.jcajce.provider.asymmetric.ec.IESKEMCipher.1
                @Override // org.bouncycastle.crypto.KeyEncoder
                public byte[] getEncoded(AsymmetricKeyParameter asymmetricKeyParameter) {
                    return ((ECPublicKeyParameters) asymmetricKeyParameter).getQ().getEncoded(zHasUsePointCompression);
                }
            }).generate();
            this.agreement.init(ephemeralKeyPairGenerate.getKeyPair().getPrivate());
            byte[] bArrIntegerToBytes = converter.integerToBytes(this.agreement.calculateAgreement(this.key), converter.getByteLength(parameters.getCurve()));
            byte[] bArr2 = new byte[i2 + this.macKeyLength];
            this.kdf.init(new KDFParameters(bArrIntegerToBytes, this.engineSpec.getRecipientInfo()));
            this.kdf.generateBytes(bArr2, 0, bArr2.length);
            byte[] bArr3 = new byte[i2 + this.macLength];
            for (int i3 = 0; i3 != i2; i3++) {
                bArr3[i3] = (byte) (bArr[i + i3] ^ bArr2[i3]);
            }
            KeyParameter keyParameter = new KeyParameter(bArr2, i2, bArr2.length - i2);
            this.hMac.init(keyParameter);
            this.hMac.update(bArr3, 0, i2);
            byte[] bArr4 = new byte[this.hMac.getMacSize()];
            this.hMac.doFinal(bArr4, 0);
            Arrays.clear(keyParameter.getKey());
            Arrays.clear(bArr2);
            System.arraycopy(bArr4, 0, bArr3, i2, this.macLength);
            return Arrays.concatenate(ephemeralKeyPairGenerate.getEncodedPublicKey(), bArr3);
        }
        if (this.state != 2 && this.state != 4) {
            throw new IllegalStateException("cipher not initialised");
        }
        ECPrivateKeyParameters eCPrivateKeyParameters = (ECPrivateKeyParameters) this.key;
        ECCurve curve = eCPrivateKeyParameters.getParameters().getCurve();
        int fieldSize = (curve.getFieldSize() + 7) / 8;
        int i4 = bArr[i] == 4 ? 1 + (2 * fieldSize) : 1 + fieldSize;
        int i5 = i2 - (i4 + this.macLength);
        ECPoint eCPointDecodePoint = curve.decodePoint(Arrays.copyOfRange(bArr, i, i + i4));
        this.agreement.init(this.key);
        byte[] bArrIntegerToBytes2 = converter.integerToBytes(this.agreement.calculateAgreement(new ECPublicKeyParameters(eCPointDecodePoint, eCPrivateKeyParameters.getParameters())), converter.getByteLength(parameters.getCurve()));
        byte[] bArr5 = new byte[i5 + this.macKeyLength];
        this.kdf.init(new KDFParameters(bArrIntegerToBytes2, this.engineSpec.getRecipientInfo()));
        this.kdf.generateBytes(bArr5, 0, bArr5.length);
        byte[] bArr6 = new byte[i5];
        for (int i6 = 0; i6 != bArr6.length; i6++) {
            bArr6[i6] = (byte) (bArr[(i + i4) + i6] ^ bArr5[i6]);
        }
        KeyParameter keyParameter2 = new KeyParameter(bArr5, i5, bArr5.length - i5);
        this.hMac.init(keyParameter2);
        this.hMac.update(bArr, i + i4, bArr6.length);
        byte[] bArr7 = new byte[this.hMac.getMacSize()];
        this.hMac.doFinal(bArr7, 0);
        Arrays.clear(keyParameter2.getKey());
        Arrays.clear(bArr5);
        if (Arrays.constantTimeAreEqual(this.macLength, bArr7, 0, bArr, i + (i2 - this.macLength))) {
            return bArr6;
        }
        throw new BadPaddingException("mac field");
    }

    @Override // javax.crypto.CipherSpi
    public int engineDoFinal(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        byte[] bArrEngineDoFinal = engineDoFinal(bArr, i, i2);
        System.arraycopy(bArrEngineDoFinal, 0, bArr2, i3, bArrEngineDoFinal.length);
        return bArrEngineDoFinal.length;
    }
}
