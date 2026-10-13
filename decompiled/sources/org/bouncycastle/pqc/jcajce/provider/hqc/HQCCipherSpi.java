package org.bouncycastle.pqc.jcajce.provider.hqc;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.spec.KEMParameterSpec;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.pqc.crypto.hqc.HQCKEMExtractor;
import org.bouncycastle.pqc.crypto.hqc.HQCKEMGenerator;
import org.bouncycastle.pqc.crypto.hqc.HQCParameters;
import org.bouncycastle.pqc.jcajce.provider.util.WrapUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/hqc/HQCCipherSpi.class */
class HQCCipherSpi extends CipherSpi {
    private final String algorithmName;
    private HQCKEMGenerator kemGen;
    private KTSParameterSpec kemParameterSpec;
    private BCHQCPublicKey wrapKey;
    private BCHQCPrivateKey unwrapKey;
    private AlgorithmParameters engineParams;
    private HQCParameters hqcParameters;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/hqc/HQCCipherSpi$Base.class */
    public static class Base extends HQCCipherSpi {
        public Base() throws NoSuchAlgorithmException {
            super("HQC");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/hqc/HQCCipherSpi$HQC128.class */
    public static class HQC128 extends HQCCipherSpi {
        public HQC128() {
            super(HQCParameters.hqc128);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/hqc/HQCCipherSpi$HQC192.class */
    public static class HQC192 extends HQCCipherSpi {
        public HQC192() {
            super(HQCParameters.hqc192);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/hqc/HQCCipherSpi$HQC256.class */
    public static class HQC256 extends HQCCipherSpi {
        public HQC256() {
            super(HQCParameters.hqc256);
        }
    }

    HQCCipherSpi(String str) throws NoSuchAlgorithmException {
        this.algorithmName = str;
    }

    HQCCipherSpi(HQCParameters hQCParameters) {
        this.hqcParameters = hQCParameters;
        this.algorithmName = Strings.toUpperCase(hQCParameters.getName());
    }

    @Override // javax.crypto.CipherSpi
    protected void engineSetMode(String str) throws NoSuchAlgorithmException {
        throw new NoSuchAlgorithmException("Cannot support mode " + str);
    }

    @Override // javax.crypto.CipherSpi
    protected void engineSetPadding(String str) throws NoSuchPaddingException {
        throw new NoSuchPaddingException("Padding " + str + " unknown");
    }

    @Override // javax.crypto.CipherSpi
    protected int engineGetKeySize(Key key) {
        return 2048;
    }

    @Override // javax.crypto.CipherSpi
    protected int engineGetBlockSize() {
        return 0;
    }

    @Override // javax.crypto.CipherSpi
    protected int engineGetOutputSize(int i) {
        return -1;
    }

    @Override // javax.crypto.CipherSpi
    protected byte[] engineGetIV() {
        return null;
    }

    @Override // javax.crypto.CipherSpi
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null) {
            try {
                this.engineParams = AlgorithmParameters.getInstance(this.algorithmName, "BCPQC");
                this.engineParams.init(this.kemParameterSpec);
            } catch (Exception e) {
                throw Exceptions.illegalStateException(e.toString(), e);
            }
        }
        return this.engineParams;
    }

    @Override // javax.crypto.CipherSpi
    protected void engineInit(int i, Key key, SecureRandom secureRandom) throws InvalidKeyException {
        try {
            engineInit(i, key, (AlgorithmParameterSpec) null, secureRandom);
        } catch (InvalidAlgorithmParameterException e) {
            throw Exceptions.illegalArgumentException(e.getMessage(), e);
        }
    }

    @Override // javax.crypto.CipherSpi
    protected void engineInit(int i, Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameterSpec == null) {
            this.kemParameterSpec = new KEMParameterSpec("AES-KWP");
        } else {
            if (!(algorithmParameterSpec instanceof KTSParameterSpec)) {
                throw new InvalidAlgorithmParameterException(this.algorithmName + " can only accept KTSParameterSpec");
            }
            this.kemParameterSpec = (KTSParameterSpec) algorithmParameterSpec;
        }
        if (i == 3) {
            if (!(key instanceof BCHQCPublicKey)) {
                throw new InvalidKeyException("Only a " + this.algorithmName + " public key can be used for wrapping");
            }
            this.wrapKey = (BCHQCPublicKey) key;
            this.kemGen = new HQCKEMGenerator(CryptoServicesRegistrar.getSecureRandom(secureRandom));
        } else {
            if (i != 4) {
                throw new InvalidParameterException("Cipher only valid for wrapping/unwrapping");
            }
            if (!(key instanceof BCHQCPrivateKey)) {
                throw new InvalidKeyException("Only a " + this.algorithmName + " private key can be used for unwrapping");
            }
            this.unwrapKey = (BCHQCPrivateKey) key;
        }
        if (this.hqcParameters != null) {
            String upperCase = Strings.toUpperCase(this.hqcParameters.getName());
            if (!upperCase.equals(key.getAlgorithm())) {
                throw new InvalidKeyException("cipher locked to " + upperCase);
            }
        }
    }

    @Override // javax.crypto.CipherSpi
    protected void engineInit(int i, Key key, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec parameterSpec = null;
        if (algorithmParameters != null) {
            try {
                parameterSpec = algorithmParameters.getParameterSpec(KEMParameterSpec.class);
            } catch (Exception e) {
                throw new InvalidAlgorithmParameterException("can't handle parameter " + algorithmParameters.toString());
            }
        }
        engineInit(i, key, parameterSpec, secureRandom);
    }

    @Override // javax.crypto.CipherSpi
    protected byte[] engineUpdate(byte[] bArr, int i, int i2) {
        throw new IllegalStateException("Not supported in a wrapping mode");
    }

    @Override // javax.crypto.CipherSpi
    protected int engineUpdate(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws ShortBufferException {
        throw new IllegalStateException("Not supported in a wrapping mode");
    }

    @Override // javax.crypto.CipherSpi
    protected byte[] engineDoFinal(byte[] bArr, int i, int i2) throws BadPaddingException, IllegalBlockSizeException {
        throw new IllegalStateException("Not supported in a wrapping mode");
    }

    @Override // javax.crypto.CipherSpi
    protected int engineDoFinal(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        throw new IllegalStateException("Not supported in a wrapping mode");
    }

    @Override // javax.crypto.CipherSpi
    protected byte[] engineWrap(Key key) throws IllegalBlockSizeException, InvalidKeyException {
        if (key.getEncoded() == null) {
            throw new InvalidKeyException("Cannot wrap key, null encoding.");
        }
        try {
            SecretWithEncapsulation secretWithEncapsulationGenerateEncapsulated = this.kemGen.generateEncapsulated(this.wrapKey.getKeyParams());
            Wrapper wrapper = WrapUtil.getWrapper(this.kemParameterSpec.getKeyAlgorithmName());
            wrapper.init(true, new KeyParameter(secretWithEncapsulationGenerateEncapsulated.getSecret()));
            byte[] encapsulation = secretWithEncapsulationGenerateEncapsulated.getEncapsulation();
            secretWithEncapsulationGenerateEncapsulated.destroy();
            byte[] encoded = key.getEncoded();
            byte[] bArrConcatenate = Arrays.concatenate(encapsulation, wrapper.wrap(encoded, 0, encoded.length));
            Arrays.clear(encoded);
            return bArrConcatenate;
        } catch (IllegalArgumentException e) {
            throw new IllegalBlockSizeException("unable to generate KTS secret: " + e.getMessage());
        } catch (DestroyFailedException e2) {
            throw new IllegalBlockSizeException("unable to destroy interim values: " + e2.getMessage());
        }
    }

    @Override // javax.crypto.CipherSpi
    protected Key engineUnwrap(byte[] bArr, String str, int i) throws NoSuchAlgorithmException, InvalidKeyException {
        if (i != 3) {
            throw new InvalidKeyException("only SECRET_KEY supported");
        }
        try {
            HQCKEMExtractor hQCKEMExtractor = new HQCKEMExtractor(this.unwrapKey.getKeyParams());
            byte[] bArrExtractSecret = hQCKEMExtractor.extractSecret(Arrays.copyOfRange(bArr, 0, hQCKEMExtractor.getEncapsulationLength()));
            Wrapper wrapper = WrapUtil.getWrapper(this.kemParameterSpec.getKeyAlgorithmName());
            KeyParameter keyParameter = new KeyParameter(bArrExtractSecret);
            Arrays.clear(bArrExtractSecret);
            wrapper.init(false, keyParameter);
            byte[] bArrCopyOfRange = Arrays.copyOfRange(bArr, hQCKEMExtractor.getEncapsulationLength(), bArr.length);
            SecretKeySpec secretKeySpec = new SecretKeySpec(wrapper.unwrap(bArrCopyOfRange, 0, bArrCopyOfRange.length), str);
            Arrays.clear(keyParameter.getKey());
            return secretKeySpec;
        } catch (IllegalArgumentException e) {
            throw new NoSuchAlgorithmException("unable to extract KTS secret: " + e.getMessage());
        } catch (InvalidCipherTextException e2) {
            throw new InvalidKeyException("unable to extract KTS secret: " + e2.getMessage());
        }
    }
}
