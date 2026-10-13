package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import es.gob.afirma.core.signers.AOSignConstants;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.ISO9796d1Encoding;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.tls.TlsRsaKeyExchange;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi;
import org.bouncycastle.jcajce.provider.util.BadBlockException;
import org.bouncycastle.jcajce.provider.util.DigestFactory;
import org.bouncycastle.jcajce.spec.TLSRSAPremasterSecretParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/CipherSpi.class */
public class CipherSpi extends BaseCipherSpi {
    private final JcaJceHelper helper;
    private AsymmetricBlockCipher cipher;
    private AlgorithmParameterSpec paramSpec;
    private AlgorithmParameters engineParams;
    private boolean publicKeyOnly;
    private boolean privateKeyOnly;
    private BaseCipherSpi.ErasableOutputStream bOut;
    private TLSRSAPremasterSecretParameterSpec tlsRsaSpec;
    private CipherParameters param;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/CipherSpi$ISO9796d1Padding.class */
    public static class ISO9796d1Padding extends CipherSpi {
        public ISO9796d1Padding() {
            super(new ISO9796d1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/CipherSpi$NoPadding.class */
    public static class NoPadding extends CipherSpi {
        public NoPadding() {
            super(new RSABlindedEngine());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/CipherSpi$OAEPPadding.class */
    public static class OAEPPadding extends CipherSpi {
        public OAEPPadding() {
            super(OAEPParameterSpec.DEFAULT);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/CipherSpi$PKCS1v1_5Padding.class */
    public static class PKCS1v1_5Padding extends CipherSpi {
        public PKCS1v1_5Padding() {
            super(new CustomPKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/CipherSpi$PKCS1v1_5Padding_PrivateOnly.class */
    public static class PKCS1v1_5Padding_PrivateOnly extends CipherSpi {
        public PKCS1v1_5Padding_PrivateOnly() {
            super(false, true, new CustomPKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/CipherSpi$PKCS1v1_5Padding_PublicOnly.class */
    public static class PKCS1v1_5Padding_PublicOnly extends CipherSpi {
        public PKCS1v1_5Padding_PublicOnly() {
            super(true, false, new CustomPKCS1Encoding(new RSABlindedEngine()));
        }
    }

    public CipherSpi(AsymmetricBlockCipher asymmetricBlockCipher) {
        this.helper = new BCJcaJceHelper();
        this.publicKeyOnly = false;
        this.privateKeyOnly = false;
        this.bOut = new BaseCipherSpi.ErasableOutputStream();
        this.tlsRsaSpec = null;
        this.param = null;
        this.cipher = asymmetricBlockCipher;
    }

    public CipherSpi(OAEPParameterSpec oAEPParameterSpec) {
        this.helper = new BCJcaJceHelper();
        this.publicKeyOnly = false;
        this.privateKeyOnly = false;
        this.bOut = new BaseCipherSpi.ErasableOutputStream();
        this.tlsRsaSpec = null;
        this.param = null;
        try {
            initFromSpec(oAEPParameterSpec);
        } catch (NoSuchPaddingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public CipherSpi(boolean z, boolean z2, AsymmetricBlockCipher asymmetricBlockCipher) {
        this.helper = new BCJcaJceHelper();
        this.publicKeyOnly = false;
        this.privateKeyOnly = false;
        this.bOut = new BaseCipherSpi.ErasableOutputStream();
        this.tlsRsaSpec = null;
        this.param = null;
        this.publicKeyOnly = z;
        this.privateKeyOnly = z2;
        this.cipher = asymmetricBlockCipher;
    }

    private void initFromSpec(OAEPParameterSpec oAEPParameterSpec) throws NoSuchPaddingException {
        MGF1ParameterSpec mGF1ParameterSpec = (MGF1ParameterSpec) oAEPParameterSpec.getMGFParameters();
        Digest digest = DigestFactory.getDigest(mGF1ParameterSpec.getDigestAlgorithm());
        if (digest == null) {
            throw new NoSuchPaddingException("no match on OAEP constructor for digest algorithm: " + mGF1ParameterSpec.getDigestAlgorithm());
        }
        this.cipher = new OAEPEncoding(new RSABlindedEngine(), digest, ((PSource.PSpecified) oAEPParameterSpec.getPSource()).getValue());
        this.paramSpec = oAEPParameterSpec;
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    protected int engineGetBlockSize() {
        try {
            return this.cipher.getInputBlockSize();
        } catch (NullPointerException e) {
            throw new IllegalStateException("RSA Cipher not initialised");
        }
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    protected int engineGetKeySize(Key key) {
        if (key instanceof RSAPrivateKey) {
            return ((RSAPrivateKey) key).getModulus().bitLength();
        }
        if (key instanceof RSAPublicKey) {
            return ((RSAPublicKey) key).getModulus().bitLength();
        }
        throw new IllegalArgumentException("not an RSA key!");
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    protected int engineGetOutputSize(int i) {
        if (this.tlsRsaSpec != null) {
            return 48;
        }
        try {
            return this.cipher.getOutputBlockSize();
        } catch (NullPointerException e) {
            throw new IllegalStateException("RSA Cipher not initialised");
        }
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null && this.paramSpec != null) {
            try {
                this.engineParams = this.helper.createAlgorithmParameters("OAEP");
                this.engineParams.init(this.paramSpec);
            } catch (Exception e) {
                throw new RuntimeException(e.toString());
            }
        }
        return this.engineParams;
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    protected void engineSetMode(String str) throws NoSuchAlgorithmException {
        String upperCase = Strings.toUpperCase(str);
        if (upperCase.equals(AOSignConstants.SIGN_FORMAT_PKCS1) || upperCase.equals("ECB")) {
            return;
        }
        if (upperCase.equals("1")) {
            this.privateKeyOnly = true;
            this.publicKeyOnly = false;
        } else {
            if (!upperCase.equals("2")) {
                throw new NoSuchAlgorithmException("can't support mode " + str);
            }
            this.privateKeyOnly = false;
            this.publicKeyOnly = true;
        }
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    protected void engineSetPadding(String str) throws NoSuchPaddingException {
        String upperCase = Strings.toUpperCase(str);
        if (upperCase.equals("NOPADDING")) {
            this.cipher = new RSABlindedEngine();
            return;
        }
        if (upperCase.equals("PKCS1PADDING")) {
            this.cipher = new CustomPKCS1Encoding(new RSABlindedEngine());
            return;
        }
        if (upperCase.equals("ISO9796-1PADDING")) {
            this.cipher = new ISO9796d1Encoding(new RSABlindedEngine());
            return;
        }
        if (upperCase.equals("OAEPWITHMD5ANDMGF1PADDING")) {
            initFromSpec(new OAEPParameterSpec(MessageDigestAlgorithms.MD5, "MGF1", new MGF1ParameterSpec(MessageDigestAlgorithms.MD5), PSource.PSpecified.DEFAULT));
            return;
        }
        if (upperCase.equals("OAEPPADDING")) {
            initFromSpec(OAEPParameterSpec.DEFAULT);
            return;
        }
        if (upperCase.equals("OAEPWITHSHA1ANDMGF1PADDING") || upperCase.equals("OAEPWITHSHA-1ANDMGF1PADDING")) {
            initFromSpec(OAEPParameterSpec.DEFAULT);
            return;
        }
        if (upperCase.equals("OAEPWITHSHA224ANDMGF1PADDING") || upperCase.equals("OAEPWITHSHA-224ANDMGF1PADDING")) {
            initFromSpec(new OAEPParameterSpec("SHA-224", "MGF1", new MGF1ParameterSpec("SHA-224"), PSource.PSpecified.DEFAULT));
            return;
        }
        if (upperCase.equals("OAEPWITHSHA256ANDMGF1PADDING") || upperCase.equals("OAEPWITHSHA-256ANDMGF1PADDING")) {
            initFromSpec(new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
            return;
        }
        if (upperCase.equals("OAEPWITHSHA384ANDMGF1PADDING") || upperCase.equals("OAEPWITHSHA-384ANDMGF1PADDING")) {
            initFromSpec(new OAEPParameterSpec("SHA-384", "MGF1", MGF1ParameterSpec.SHA384, PSource.PSpecified.DEFAULT));
            return;
        }
        if (upperCase.equals("OAEPWITHSHA512ANDMGF1PADDING") || upperCase.equals("OAEPWITHSHA-512ANDMGF1PADDING")) {
            initFromSpec(new OAEPParameterSpec("SHA-512", "MGF1", MGF1ParameterSpec.SHA512, PSource.PSpecified.DEFAULT));
            return;
        }
        if (upperCase.equals("OAEPWITHSHA3-224ANDMGF1PADDING")) {
            initFromSpec(new OAEPParameterSpec(MessageDigestAlgorithms.SHA3_224, "MGF1", new MGF1ParameterSpec(MessageDigestAlgorithms.SHA3_224), PSource.PSpecified.DEFAULT));
            return;
        }
        if (upperCase.equals("OAEPWITHSHA3-256ANDMGF1PADDING")) {
            initFromSpec(new OAEPParameterSpec("SHA3-256", "MGF1", new MGF1ParameterSpec("SHA3-256"), PSource.PSpecified.DEFAULT));
        } else if (upperCase.equals("OAEPWITHSHA3-384ANDMGF1PADDING")) {
            initFromSpec(new OAEPParameterSpec(MessageDigestAlgorithms.SHA3_384, "MGF1", new MGF1ParameterSpec(MessageDigestAlgorithms.SHA3_384), PSource.PSpecified.DEFAULT));
        } else {
            if (!upperCase.equals("OAEPWITHSHA3-512ANDMGF1PADDING")) {
                throw new NoSuchPaddingException(str + " unavailable with RSA.");
            }
            initFromSpec(new OAEPParameterSpec(MessageDigestAlgorithms.SHA3_512, "MGF1", new MGF1ParameterSpec(MessageDigestAlgorithms.SHA3_512), PSource.PSpecified.DEFAULT));
        }
    }

    @Override // javax.crypto.CipherSpi
    protected void engineInit(int i, Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.tlsRsaSpec = null;
        if (algorithmParameterSpec != null && !(algorithmParameterSpec instanceof OAEPParameterSpec) && !(algorithmParameterSpec instanceof TLSRSAPremasterSecretParameterSpec)) {
            throw new InvalidAlgorithmParameterException("unknown parameter type: " + algorithmParameterSpec.getClass().getName());
        }
        if (key instanceof RSAPublicKey) {
            if (this.privateKeyOnly && i == 1) {
                throw new InvalidKeyException("mode 1 requires RSAPrivateKey");
            }
            this.param = RSAUtil.generatePublicKeyParameter((RSAPublicKey) key);
        } else {
            if (!(key instanceof RSAPrivateKey)) {
                throw new InvalidKeyException("unknown key type passed to RSA");
            }
            if (this.publicKeyOnly && i == 1) {
                throw new InvalidKeyException("mode 2 requires RSAPublicKey");
            }
            this.param = RSAUtil.generatePrivateKeyParameter((RSAPrivateKey) key);
        }
        if (algorithmParameterSpec instanceof OAEPParameterSpec) {
            OAEPParameterSpec oAEPParameterSpec = (OAEPParameterSpec) algorithmParameterSpec;
            this.paramSpec = algorithmParameterSpec;
            if (!oAEPParameterSpec.getMGFAlgorithm().equalsIgnoreCase("MGF1") && !oAEPParameterSpec.getMGFAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1.getId())) {
                throw new InvalidAlgorithmParameterException("unknown mask generation function specified");
            }
            if (!(oAEPParameterSpec.getMGFParameters() instanceof MGF1ParameterSpec)) {
                throw new InvalidAlgorithmParameterException("unkown MGF parameters");
            }
            Digest digest = DigestFactory.getDigest(oAEPParameterSpec.getDigestAlgorithm());
            if (digest == null) {
                throw new InvalidAlgorithmParameterException("no match on digest algorithm: " + oAEPParameterSpec.getDigestAlgorithm());
            }
            MGF1ParameterSpec mGF1ParameterSpec = (MGF1ParameterSpec) oAEPParameterSpec.getMGFParameters();
            Digest digest2 = DigestFactory.getDigest(mGF1ParameterSpec.getDigestAlgorithm());
            if (digest2 == null) {
                throw new InvalidAlgorithmParameterException("no match on MGF digest algorithm: " + mGF1ParameterSpec.getDigestAlgorithm());
            }
            this.cipher = new OAEPEncoding(new RSABlindedEngine(), digest, digest2, ((PSource.PSpecified) oAEPParameterSpec.getPSource()).getValue());
        } else if (algorithmParameterSpec instanceof TLSRSAPremasterSecretParameterSpec) {
            if (!(this.param instanceof RSAKeyParameters) || !((RSAKeyParameters) this.param).isPrivate()) {
                throw new InvalidKeyException("RSA private key required for TLS decryption");
            }
            this.tlsRsaSpec = (TLSRSAPremasterSecretParameterSpec) algorithmParameterSpec;
        }
        if (secureRandom != null) {
            this.param = new ParametersWithRandom(this.param, secureRandom);
        } else {
            this.param = new ParametersWithRandom(this.param, CryptoServicesRegistrar.getSecureRandom());
        }
        this.bOut.reset();
        switch (i) {
            case 1:
            case 3:
                this.cipher.init(true, this.param);
                return;
            case 2:
            case 4:
                this.cipher.init(false, this.param);
                return;
            default:
                throw new InvalidParameterException("unknown opmode " + i + " passed to RSA");
        }
    }

    @Override // javax.crypto.CipherSpi
    protected void engineInit(int i, Key key, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec parameterSpec = null;
        if (algorithmParameters != null) {
            try {
                parameterSpec = algorithmParameters.getParameterSpec(OAEPParameterSpec.class);
            } catch (InvalidParameterSpecException e) {
                throw new InvalidAlgorithmParameterException("cannot recognise parameters: " + e.toString(), e);
            }
        }
        this.engineParams = algorithmParameters;
        engineInit(i, key, parameterSpec, secureRandom);
    }

    @Override // javax.crypto.CipherSpi
    protected void engineInit(int i, Key key, SecureRandom secureRandom) throws InvalidKeyException {
        try {
            engineInit(i, key, (AlgorithmParameterSpec) null, secureRandom);
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidKeyException("Eeeek! " + e.toString(), e);
        }
    }

    @Override // javax.crypto.CipherSpi
    protected byte[] engineUpdate(byte[] bArr, int i, int i2) {
        if (i2 > getInputLimit() - this.bOut.size()) {
            throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
        }
        this.bOut.write(bArr, i, i2);
        return null;
    }

    @Override // javax.crypto.CipherSpi
    protected int engineUpdate(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        engineUpdate(bArr, i, i2);
        return 0;
    }

    @Override // javax.crypto.CipherSpi
    protected byte[] engineDoFinal(byte[] bArr, int i, int i2) throws BadPaddingException, IllegalBlockSizeException {
        if (bArr != null) {
            engineUpdate(bArr, i, i2);
        }
        return getOutput();
    }

    @Override // javax.crypto.CipherSpi
    protected int engineDoFinal(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        if (i3 > bArr2.length - engineGetOutputSize(bArr == null ? 0 : i2)) {
            throw new ShortBufferException("output buffer too short for input.");
        }
        byte[] bArrEngineDoFinal = engineDoFinal(bArr, i, i2);
        System.arraycopy(bArrEngineDoFinal, 0, bArr2, i3, bArrEngineDoFinal.length);
        return bArrEngineDoFinal.length;
    }

    private int getInputLimit() {
        if (this.tlsRsaSpec != null) {
            return TlsRsaKeyExchange.getInputLimit((RSAKeyParameters) ((ParametersWithRandom) this.param).getParameters());
        }
        return this.cipher instanceof RSABlindedEngine ? this.cipher.getInputBlockSize() + 1 : this.cipher.getInputBlockSize();
    }

    private byte[] getOutput() throws BadPaddingException {
        try {
            if (this.tlsRsaSpec != null) {
                ParametersWithRandom parametersWithRandom = (ParametersWithRandom) this.param;
                byte[] bArrDecryptPreMasterSecret = TlsRsaKeyExchange.decryptPreMasterSecret(this.bOut.getBuf(), 0, this.bOut.size(), (RSAKeyParameters) parametersWithRandom.getParameters(), this.tlsRsaSpec.getProtocolVersion(), parametersWithRandom.getRandom());
                this.bOut.erase();
                return bArrDecryptPreMasterSecret;
            }
            try {
                byte[] bArrProcessBlock = this.cipher.processBlock(this.bOut.getBuf(), 0, this.bOut.size());
                if (bArrProcessBlock == null) {
                    throw new BadBlockException("unable to decrypt block", null);
                }
                this.bOut.erase();
                return bArrProcessBlock;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new BadBlockException("unable to decrypt block", e);
            } catch (InvalidCipherTextException e2) {
                throw new BadBlockException("unable to decrypt block", e2);
            }
        } catch (Throwable th) {
            this.bOut.erase();
            throw th;
        }
    }
}
