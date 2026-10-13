package org.spongycastle.jcajce.provider.asymmetric.rsa;

import es.gob.afirma.core.signers.AOSignConstants;
import java.io.ByteArrayOutputStream;
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
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.crypto.AsymmetricBlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.encodings.ISO9796d1Encoding;
import org.spongycastle.crypto.encodings.OAEPEncoding;
import org.spongycastle.crypto.encodings.PKCS1Encoding;
import org.spongycastle.crypto.engines.RSABlindedEngine;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.jcajce.provider.asymmetric.util.BaseCipherSpi;
import org.spongycastle.jcajce.provider.util.BadBlockException;
import org.spongycastle.jcajce.provider.util.DigestFactory;
import org.spongycastle.jcajce.util.BCJcaJceHelper;
import org.spongycastle.jcajce.util.JcaJceHelper;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/rsa/CipherSpi.class */
public class CipherSpi extends BaseCipherSpi {
    private final JcaJceHelper helper;
    private AsymmetricBlockCipher cipher;
    private AlgorithmParameterSpec paramSpec;
    private AlgorithmParameters engineParams;
    private boolean publicKeyOnly;
    private boolean privateKeyOnly;
    private ByteArrayOutputStream bOut;

    public CipherSpi(AsymmetricBlockCipher engine) {
        this.helper = new BCJcaJceHelper();
        this.publicKeyOnly = false;
        this.privateKeyOnly = false;
        this.bOut = new ByteArrayOutputStream();
        this.cipher = engine;
    }

    public CipherSpi(OAEPParameterSpec pSpec) {
        this.helper = new BCJcaJceHelper();
        this.publicKeyOnly = false;
        this.privateKeyOnly = false;
        this.bOut = new ByteArrayOutputStream();
        try {
            initFromSpec(pSpec);
        } catch (NoSuchPaddingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public CipherSpi(boolean publicKeyOnly, boolean privateKeyOnly, AsymmetricBlockCipher engine) {
        this.helper = new BCJcaJceHelper();
        this.publicKeyOnly = false;
        this.privateKeyOnly = false;
        this.bOut = new ByteArrayOutputStream();
        this.publicKeyOnly = publicKeyOnly;
        this.privateKeyOnly = privateKeyOnly;
        this.cipher = engine;
    }

    private void initFromSpec(OAEPParameterSpec pSpec) throws NoSuchPaddingException {
        MGF1ParameterSpec mgfParams = (MGF1ParameterSpec) pSpec.getMGFParameters();
        Digest digest = DigestFactory.getDigest(mgfParams.getDigestAlgorithm());
        if (digest == null) {
            throw new NoSuchPaddingException("no match on OAEP constructor for digest algorithm: " + mgfParams.getDigestAlgorithm());
        }
        this.cipher = new OAEPEncoding(new RSABlindedEngine(), digest, ((PSource.PSpecified) pSpec.getPSource()).getValue());
        this.paramSpec = pSpec;
    }

    @Override // org.spongycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    protected int engineGetBlockSize() {
        try {
            return this.cipher.getInputBlockSize();
        } catch (NullPointerException e) {
            throw new IllegalStateException("RSA Cipher not initialised");
        }
    }

    @Override // org.spongycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    protected int engineGetKeySize(Key key) {
        if (key instanceof RSAPrivateKey) {
            RSAPrivateKey k = (RSAPrivateKey) key;
            return k.getModulus().bitLength();
        }
        if (key instanceof RSAPublicKey) {
            RSAPublicKey k2 = (RSAPublicKey) key;
            return k2.getModulus().bitLength();
        }
        throw new IllegalArgumentException("not an RSA key!");
    }

    @Override // org.spongycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    protected int engineGetOutputSize(int inputLen) {
        try {
            return this.cipher.getOutputBlockSize();
        } catch (NullPointerException e) {
            throw new IllegalStateException("RSA Cipher not initialised");
        }
    }

    @Override // org.spongycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
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

    @Override // org.spongycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    protected void engineSetMode(String mode) throws NoSuchAlgorithmException {
        String md = Strings.toUpperCase(mode);
        if (md.equals(AOSignConstants.SIGN_FORMAT_PKCS1) || md.equals("ECB")) {
            return;
        }
        if (md.equals("1")) {
            this.privateKeyOnly = true;
            this.publicKeyOnly = false;
        } else {
            if (md.equals("2")) {
                this.privateKeyOnly = false;
                this.publicKeyOnly = true;
                return;
            }
            throw new NoSuchAlgorithmException("can't support mode " + mode);
        }
    }

    @Override // org.spongycastle.jcajce.provider.asymmetric.util.BaseCipherSpi, javax.crypto.CipherSpi
    protected void engineSetPadding(String padding) throws NoSuchPaddingException {
        String pad = Strings.toUpperCase(padding);
        if (pad.equals("NOPADDING")) {
            this.cipher = new RSABlindedEngine();
            return;
        }
        if (pad.equals("PKCS1PADDING")) {
            this.cipher = new PKCS1Encoding(new RSABlindedEngine());
            return;
        }
        if (pad.equals("ISO9796-1PADDING")) {
            this.cipher = new ISO9796d1Encoding(new RSABlindedEngine());
            return;
        }
        if (pad.equals("OAEPWITHMD5ANDMGF1PADDING")) {
            initFromSpec(new OAEPParameterSpec(MessageDigestAlgorithms.MD5, "MGF1", new MGF1ParameterSpec(MessageDigestAlgorithms.MD5), PSource.PSpecified.DEFAULT));
            return;
        }
        if (pad.equals("OAEPPADDING")) {
            initFromSpec(OAEPParameterSpec.DEFAULT);
            return;
        }
        if (pad.equals("OAEPWITHSHA1ANDMGF1PADDING") || pad.equals("OAEPWITHSHA-1ANDMGF1PADDING")) {
            initFromSpec(OAEPParameterSpec.DEFAULT);
            return;
        }
        if (pad.equals("OAEPWITHSHA224ANDMGF1PADDING") || pad.equals("OAEPWITHSHA-224ANDMGF1PADDING")) {
            initFromSpec(new OAEPParameterSpec("SHA-224", "MGF1", new MGF1ParameterSpec("SHA-224"), PSource.PSpecified.DEFAULT));
            return;
        }
        if (pad.equals("OAEPWITHSHA256ANDMGF1PADDING") || pad.equals("OAEPWITHSHA-256ANDMGF1PADDING")) {
            initFromSpec(new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
            return;
        }
        if (pad.equals("OAEPWITHSHA384ANDMGF1PADDING") || pad.equals("OAEPWITHSHA-384ANDMGF1PADDING")) {
            initFromSpec(new OAEPParameterSpec("SHA-384", "MGF1", MGF1ParameterSpec.SHA384, PSource.PSpecified.DEFAULT));
            return;
        }
        if (pad.equals("OAEPWITHSHA512ANDMGF1PADDING") || pad.equals("OAEPWITHSHA-512ANDMGF1PADDING")) {
            initFromSpec(new OAEPParameterSpec("SHA-512", "MGF1", MGF1ParameterSpec.SHA512, PSource.PSpecified.DEFAULT));
            return;
        }
        if (pad.equals("OAEPWITHSHA3-224ANDMGF1PADDING")) {
            initFromSpec(new OAEPParameterSpec(MessageDigestAlgorithms.SHA3_224, "MGF1", new MGF1ParameterSpec(MessageDigestAlgorithms.SHA3_224), PSource.PSpecified.DEFAULT));
            return;
        }
        if (pad.equals("OAEPWITHSHA3-256ANDMGF1PADDING")) {
            initFromSpec(new OAEPParameterSpec("SHA3-256", "MGF1", new MGF1ParameterSpec("SHA3-256"), PSource.PSpecified.DEFAULT));
        } else if (pad.equals("OAEPWITHSHA3-384ANDMGF1PADDING")) {
            initFromSpec(new OAEPParameterSpec(MessageDigestAlgorithms.SHA3_384, "MGF1", new MGF1ParameterSpec(MessageDigestAlgorithms.SHA3_384), PSource.PSpecified.DEFAULT));
        } else {
            if (pad.equals("OAEPWITHSHA3-512ANDMGF1PADDING")) {
                initFromSpec(new OAEPParameterSpec(MessageDigestAlgorithms.SHA3_512, "MGF1", new MGF1ParameterSpec(MessageDigestAlgorithms.SHA3_512), PSource.PSpecified.DEFAULT));
                return;
            }
            throw new NoSuchPaddingException(padding + " unavailable with RSA.");
        }
    }

    @Override // javax.crypto.CipherSpi
    protected void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters param;
        if (params == null || (params instanceof OAEPParameterSpec)) {
            if (key instanceof RSAPublicKey) {
                if (this.privateKeyOnly && opmode == 1) {
                    throw new InvalidKeyException("mode 1 requires RSAPrivateKey");
                }
                param = RSAUtil.generatePublicKeyParameter((RSAPublicKey) key);
            } else if (key instanceof RSAPrivateKey) {
                if (this.publicKeyOnly && opmode == 1) {
                    throw new InvalidKeyException("mode 2 requires RSAPublicKey");
                }
                param = RSAUtil.generatePrivateKeyParameter((RSAPrivateKey) key);
            } else {
                throw new InvalidKeyException("unknown key type passed to RSA");
            }
            if (params != null) {
                OAEPParameterSpec spec = (OAEPParameterSpec) params;
                this.paramSpec = params;
                if (!spec.getMGFAlgorithm().equalsIgnoreCase("MGF1") && !spec.getMGFAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1.getId())) {
                    throw new InvalidAlgorithmParameterException("unknown mask generation function specified");
                }
                if (!(spec.getMGFParameters() instanceof MGF1ParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("unkown MGF parameters");
                }
                Digest digest = DigestFactory.getDigest(spec.getDigestAlgorithm());
                if (digest == null) {
                    throw new InvalidAlgorithmParameterException("no match on digest algorithm: " + spec.getDigestAlgorithm());
                }
                MGF1ParameterSpec mgfParams = (MGF1ParameterSpec) spec.getMGFParameters();
                Digest mgfDigest = DigestFactory.getDigest(mgfParams.getDigestAlgorithm());
                if (mgfDigest == null) {
                    throw new InvalidAlgorithmParameterException("no match on MGF digest algorithm: " + mgfParams.getDigestAlgorithm());
                }
                this.cipher = new OAEPEncoding(new RSABlindedEngine(), digest, mgfDigest, ((PSource.PSpecified) spec.getPSource()).getValue());
            }
            if (!(this.cipher instanceof RSABlindedEngine)) {
                if (random != null) {
                    param = new ParametersWithRandom(param, random);
                } else {
                    param = new ParametersWithRandom(param, new SecureRandom());
                }
            }
            this.bOut.reset();
            switch (opmode) {
                case 1:
                case 3:
                    this.cipher.init(true, param);
                    return;
                case 2:
                case 4:
                    this.cipher.init(false, param);
                    return;
                default:
                    throw new InvalidParameterException("unknown opmode " + opmode + " passed to RSA");
            }
        }
        throw new InvalidAlgorithmParameterException("unknown parameter type: " + params.getClass().getName());
    }

    @Override // javax.crypto.CipherSpi
    protected void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec paramSpec = null;
        if (params != null) {
            try {
                paramSpec = params.getParameterSpec(OAEPParameterSpec.class);
            } catch (InvalidParameterSpecException e) {
                throw new InvalidAlgorithmParameterException("cannot recognise parameters: " + e.toString(), e);
            }
        }
        this.engineParams = params;
        engineInit(opmode, key, paramSpec, random);
    }

    @Override // javax.crypto.CipherSpi
    protected void engineInit(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
        try {
            engineInit(opmode, key, (AlgorithmParameterSpec) null, random);
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidKeyException("Eeeek! " + e.toString(), e);
        }
    }

    @Override // javax.crypto.CipherSpi
    protected byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        this.bOut.write(input, inputOffset, inputLen);
        if (this.cipher instanceof RSABlindedEngine) {
            if (this.bOut.size() > this.cipher.getInputBlockSize() + 1) {
                throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
            }
            return null;
        }
        if (this.bOut.size() > this.cipher.getInputBlockSize()) {
            throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
        }
        return null;
    }

    @Override // javax.crypto.CipherSpi
    protected int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) {
        this.bOut.write(input, inputOffset, inputLen);
        if (this.cipher instanceof RSABlindedEngine) {
            if (this.bOut.size() > this.cipher.getInputBlockSize() + 1) {
                throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
            }
            return 0;
        }
        if (this.bOut.size() > this.cipher.getInputBlockSize()) {
            throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
        }
        return 0;
    }

    @Override // javax.crypto.CipherSpi
    protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen) throws BadPaddingException, IllegalBlockSizeException {
        if (input != null) {
            this.bOut.write(input, inputOffset, inputLen);
        }
        if (this.cipher instanceof RSABlindedEngine) {
            if (this.bOut.size() > this.cipher.getInputBlockSize() + 1) {
                throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
            }
        } else if (this.bOut.size() > this.cipher.getInputBlockSize()) {
            throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
        }
        return getOutput();
    }

    @Override // javax.crypto.CipherSpi
    protected int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws BadPaddingException, IllegalBlockSizeException {
        if (input != null) {
            this.bOut.write(input, inputOffset, inputLen);
        }
        if (this.cipher instanceof RSABlindedEngine) {
            if (this.bOut.size() > this.cipher.getInputBlockSize() + 1) {
                throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
            }
        } else if (this.bOut.size() > this.cipher.getInputBlockSize()) {
            throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
        }
        byte[] out = getOutput();
        for (int i = 0; i != out.length; i++) {
            output[outputOffset + i] = out[i];
        }
        return out.length;
    }

    private byte[] getOutput() throws BadPaddingException {
        try {
            try {
                try {
                    byte[] bytes = this.bOut.toByteArray();
                    byte[] bArrProcessBlock = this.cipher.processBlock(bytes, 0, bytes.length);
                    this.bOut.reset();
                    return bArrProcessBlock;
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new BadBlockException("unable to decrypt block", e);
                }
            } catch (InvalidCipherTextException e2) {
                throw new BadBlockException("unable to decrypt block", e2);
            }
        } catch (Throwable th) {
            this.bOut.reset();
            throw th;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/rsa/CipherSpi$NoPadding.class */
    public static class NoPadding extends CipherSpi {
        public NoPadding() {
            super(new RSABlindedEngine());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/rsa/CipherSpi$PKCS1v1_5Padding.class */
    public static class PKCS1v1_5Padding extends CipherSpi {
        public PKCS1v1_5Padding() {
            super(new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/rsa/CipherSpi$PKCS1v1_5Padding_PrivateOnly.class */
    public static class PKCS1v1_5Padding_PrivateOnly extends CipherSpi {
        public PKCS1v1_5Padding_PrivateOnly() {
            super(false, true, new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/rsa/CipherSpi$PKCS1v1_5Padding_PublicOnly.class */
    public static class PKCS1v1_5Padding_PublicOnly extends CipherSpi {
        public PKCS1v1_5Padding_PublicOnly() {
            super(true, false, new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/rsa/CipherSpi$OAEPPadding.class */
    public static class OAEPPadding extends CipherSpi {
        public OAEPPadding() {
            super(OAEPParameterSpec.DEFAULT);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/rsa/CipherSpi$ISO9796d1Padding.class */
    public static class ISO9796d1Padding extends CipherSpi {
        public ISO9796d1Padding() {
            super(new ISO9796d1Encoding(new RSABlindedEngine()));
        }
    }
}
