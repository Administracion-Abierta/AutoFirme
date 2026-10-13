package org.bouncycastle.jcajce.provider.asymmetric.ec;

import es.gob.afirma.core.signers.AOSignConstants;
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
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.KeyEncoder;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.IESEngine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.EphemeralKeyPairGenerator;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.IESWithCipherParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.parsers.ECIESPublicKeyParser;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.IESUtil;
import org.bouncycastle.jcajce.provider.util.BadBlockException;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.jce.interfaces.IESKey;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/IESCipher.class */
public class IESCipher extends BaseCipherSpi {
    private final JcaJceHelper helper;
    private int ivLength;
    private IESEngine engine;
    private int state;
    private ByteArrayOutputStream buffer;
    private AlgorithmParameters engineParam;
    private IESParameterSpec engineSpec;
    private AsymmetricKeyParameter key;
    private SecureRandom random;
    private boolean dhaesMode;
    private AsymmetricKeyParameter otherKeyParameter;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/IESCipher$ECIES.class */
    public static class ECIES extends IESCipher {
        public ECIES() {
            this(DigestFactory.createSHA1(), DigestFactory.createSHA1());
        }

        public ECIES(Digest digest, Digest digest2) {
            super(new IESEngine(new ECDHBasicAgreement(), new KDF2BytesGenerator(digest), new HMac(digest2)));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/IESCipher$ECIESwithAESCBC.class */
    public static class ECIESwithAESCBC extends ECIESwithCipher {
        public ECIESwithAESCBC() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 16);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/IESCipher$ECIESwithCipher.class */
    public static class ECIESwithCipher extends IESCipher {
        public ECIESwithCipher(BlockCipher blockCipher, int i) {
            this(blockCipher, i, DigestFactory.createSHA1(), DigestFactory.createSHA1());
        }

        public ECIESwithCipher(BlockCipher blockCipher, int i, Digest digest, Digest digest2) {
            super(new IESEngine(new ECDHBasicAgreement(), new KDF2BytesGenerator(digest), new HMac(digest2), new PaddedBufferedBlockCipher(blockCipher)), i);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/IESCipher$ECIESwithDESedeCBC.class */
    public static class ECIESwithDESedeCBC extends ECIESwithCipher {
        public ECIESwithDESedeCBC() {
            super(CBCBlockCipher.newInstance(new DESedeEngine()), 8);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/IESCipher$ECIESwithSHA256.class */
    public static class ECIESwithSHA256 extends ECIES {
        public ECIESwithSHA256() {
            super(DigestFactory.createSHA256(), DigestFactory.createSHA256());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/IESCipher$ECIESwithSHA256andAESCBC.class */
    public static class ECIESwithSHA256andAESCBC extends ECIESwithCipher {
        public ECIESwithSHA256andAESCBC() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 16, DigestFactory.createSHA256(), DigestFactory.createSHA256());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/IESCipher$ECIESwithSHA256andDESedeCBC.class */
    public static class ECIESwithSHA256andDESedeCBC extends ECIESwithCipher {
        public ECIESwithSHA256andDESedeCBC() {
            super(CBCBlockCipher.newInstance(new DESedeEngine()), 8, DigestFactory.createSHA256(), DigestFactory.createSHA256());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/IESCipher$ECIESwithSHA384.class */
    public static class ECIESwithSHA384 extends ECIES {
        public ECIESwithSHA384() {
            super(DigestFactory.createSHA384(), DigestFactory.createSHA384());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/IESCipher$ECIESwithSHA384andAESCBC.class */
    public static class ECIESwithSHA384andAESCBC extends ECIESwithCipher {
        public ECIESwithSHA384andAESCBC() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 16, DigestFactory.createSHA384(), DigestFactory.createSHA384());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/IESCipher$ECIESwithSHA384andDESedeCBC.class */
    public static class ECIESwithSHA384andDESedeCBC extends ECIESwithCipher {
        public ECIESwithSHA384andDESedeCBC() {
            super(CBCBlockCipher.newInstance(new DESedeEngine()), 8, DigestFactory.createSHA384(), DigestFactory.createSHA384());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/IESCipher$ECIESwithSHA512.class */
    public static class ECIESwithSHA512 extends ECIES {
        public ECIESwithSHA512() {
            super(DigestFactory.createSHA512(), DigestFactory.createSHA512());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/IESCipher$ECIESwithSHA512andAESCBC.class */
    public static class ECIESwithSHA512andAESCBC extends ECIESwithCipher {
        public ECIESwithSHA512andAESCBC() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 16, DigestFactory.createSHA512(), DigestFactory.createSHA512());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/IESCipher$ECIESwithSHA512andDESedeCBC.class */
    public static class ECIESwithSHA512andDESedeCBC extends ECIESwithCipher {
        public ECIESwithSHA512andDESedeCBC() {
            super(CBCBlockCipher.newInstance(new DESedeEngine()), 8, DigestFactory.createSHA512(), DigestFactory.createSHA512());
        }
    }

    public IESCipher(IESEngine iESEngine) {
        this.helper = new BCJcaJceHelper();
        this.state = -1;
        this.buffer = new ByteArrayOutputStream();
        this.engineParam = null;
        this.engineSpec = null;
        this.dhaesMode = false;
        this.otherKeyParameter = null;
        this.engine = iESEngine;
        this.ivLength = 0;
    }

    public IESCipher(IESEngine iESEngine, int i) {
        this.helper = new BCJcaJceHelper();
        this.state = -1;
        this.buffer = new ByteArrayOutputStream();
        this.engineParam = null;
        this.engineSpec = null;
        this.dhaesMode = false;
        this.otherKeyParameter = null;
        this.engine = iESEngine;
        this.ivLength = i;
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    public int engineGetBlockSize() {
        BufferedBlockCipher cipher = this.engine.getCipher();
        if (cipher == null) {
            return 0;
        }
        return cipher.getBlockSize();
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
        if (this.engineSpec != null) {
            return this.engineSpec.getNonce();
        }
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
        String upperCase = Strings.toUpperCase(str);
        if (upperCase.equals(AOSignConstants.SIGN_FORMAT_PKCS1)) {
            this.dhaesMode = false;
        } else {
            if (!upperCase.equals("DHAES")) {
                throw new IllegalArgumentException("can't support mode " + str);
            }
            this.dhaesMode = true;
        }
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    public int engineGetOutputSize(int i) {
        int outputSize;
        if (this.key == null) {
            throw new IllegalStateException("cipher not initialised");
        }
        int macSize = this.engine.getMac().getMacSize();
        int fieldSize = this.otherKeyParameter == null ? 1 + (2 * ((((ECKeyParameters) this.key).getParameters().getCurve().getFieldSize() + 7) / 8)) : 0;
        int size = this.buffer.size() + i;
        if (this.engine.getCipher() == null) {
            outputSize = (this.state == 2 || this.state == 4) ? (size - macSize) - fieldSize : size;
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
        String upperCase = Strings.toUpperCase(str);
        if (!upperCase.equals("NOPADDING") && !upperCase.equals("PKCS5PADDING") && !upperCase.equals("PKCS7PADDING")) {
            throw new NoSuchPaddingException("padding not available with IESCipher");
        }
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
        if (algorithmParameterSpec == null && this.ivLength == 0) {
            this.engineSpec = IESUtil.guessParameterSpec(this.engine.getCipher(), null);
        } else {
            if (!(algorithmParameterSpec instanceof IESParameterSpec)) {
                throw new InvalidAlgorithmParameterException("must be passed IES parameters");
            }
            this.engineSpec = (IESParameterSpec) algorithmParameterSpec;
        }
        byte[] nonce = this.engineSpec.getNonce();
        if (this.ivLength != 0 && (nonce == null || nonce.length != this.ivLength)) {
            throw new InvalidAlgorithmParameterException("NONCE in IES Parameters needs to be " + this.ivLength + " bytes long");
        }
        if (i == 1 || i == 3) {
            if (key instanceof PublicKey) {
                this.key = ECUtils.generatePublicKeyParameter((PublicKey) key);
            } else {
                if (!(key instanceof IESKey)) {
                    throw new InvalidKeyException("must be passed recipient's public EC key for encryption");
                }
                IESKey iESKey = (IESKey) key;
                this.key = ECUtils.generatePublicKeyParameter(iESKey.getPublic());
                this.otherKeyParameter = ECUtils.generatePrivateKeyParameter(iESKey.getPrivate());
            }
        } else {
            if (i != 2 && i != 4) {
                throw new InvalidKeyException("must be passed EC key");
            }
            if (key instanceof PrivateKey) {
                this.key = ECUtils.generatePrivateKeyParameter((PrivateKey) key);
            } else {
                if (!(key instanceof IESKey)) {
                    throw new InvalidKeyException("must be passed recipient's private EC key for decryption");
                }
                IESKey iESKey2 = (IESKey) key;
                this.otherKeyParameter = ECUtils.generatePublicKeyParameter(iESKey2.getPublic());
                this.key = ECUtils.generatePrivateKeyParameter(iESKey2.getPrivate());
            }
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
        byte[] byteArray = this.buffer.toByteArray();
        this.buffer.reset();
        CipherParameters iESWithCipherParameters = new IESWithCipherParameters(this.engineSpec.getDerivationV(), this.engineSpec.getEncodingV(), this.engineSpec.getMacKeySize(), this.engineSpec.getCipherKeySize());
        byte[] nonce = this.engineSpec.getNonce();
        if (nonce != null) {
            iESWithCipherParameters = new ParametersWithIV(iESWithCipherParameters, nonce);
        }
        ECDomainParameters parameters = ((ECKeyParameters) this.key).getParameters();
        if (this.otherKeyParameter != null) {
            try {
                if (this.state == 1 || this.state == 3) {
                    this.engine.init(true, this.otherKeyParameter, this.key, iESWithCipherParameters);
                } else {
                    this.engine.init(false, this.key, this.otherKeyParameter, iESWithCipherParameters);
                }
                return this.engine.processBlock(byteArray, 0, byteArray.length);
            } catch (Exception e) {
                throw new BadBlockException("unable to process block", e);
            }
        }
        if (this.state != 1 && this.state != 3) {
            if (this.state != 2 && this.state != 4) {
                throw new IllegalStateException("cipher not initialised");
            }
            try {
                this.engine.init(this.key, iESWithCipherParameters, new ECIESPublicKeyParser(parameters));
                return this.engine.processBlock(byteArray, 0, byteArray.length);
            } catch (InvalidCipherTextException e2) {
                throw new BadBlockException("unable to process block", e2);
            }
        }
        ECKeyPairGenerator eCKeyPairGenerator = new ECKeyPairGenerator();
        eCKeyPairGenerator.init(new ECKeyGenerationParameters(parameters, this.random));
        final boolean pointCompression = this.engineSpec.getPointCompression();
        try {
            this.engine.init(this.key, iESWithCipherParameters, new EphemeralKeyPairGenerator(eCKeyPairGenerator, new KeyEncoder() { // from class: org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher.1
                @Override // org.bouncycastle.crypto.KeyEncoder
                public byte[] getEncoded(AsymmetricKeyParameter asymmetricKeyParameter) {
                    return ((ECPublicKeyParameters) asymmetricKeyParameter).getQ().getEncoded(pointCompression);
                }
            }));
            return this.engine.processBlock(byteArray, 0, byteArray.length);
        } catch (Exception e3) {
            throw new BadBlockException("unable to process block", e3);
        }
    }

    @Override // javax.crypto.CipherSpi
    public int engineDoFinal(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        byte[] bArrEngineDoFinal = engineDoFinal(bArr, i, i2);
        System.arraycopy(bArrEngineDoFinal, 0, bArr2, i3, bArrEngineDoFinal.length);
        return bArrEngineDoFinal.length;
    }
}
