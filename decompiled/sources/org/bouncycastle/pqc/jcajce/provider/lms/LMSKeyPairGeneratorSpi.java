package org.bouncycastle.pqc.jcajce.provider.lms;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.lms.HSSKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.lms.HSSKeyPairGenerator;
import org.bouncycastle.pqc.crypto.lms.HSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMOtsParameters;
import org.bouncycastle.pqc.crypto.lms.LMSKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.lms.LMSKeyPairGenerator;
import org.bouncycastle.pqc.crypto.lms.LMSParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSigParameters;
import org.bouncycastle.pqc.jcajce.spec.LMSHSSKeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.LMSHSSParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.LMSKeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.LMSParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/lms/LMSKeyPairGeneratorSpi.class */
public class LMSKeyPairGeneratorSpi extends KeyPairGenerator {
    private KeyGenerationParameters param;
    private ASN1ObjectIdentifier treeDigest;
    private AsymmetricCipherKeyPairGenerator engine;
    private SecureRandom random;
    private boolean initialised;

    public LMSKeyPairGeneratorSpi() {
        super("LMS");
        this.engine = new LMSKeyPairGenerator();
        this.random = CryptoServicesRegistrar.getSecureRandom();
        this.initialised = false;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(int i, SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec instanceof LMSKeyGenParameterSpec) {
            LMSKeyGenParameterSpec lMSKeyGenParameterSpec = (LMSKeyGenParameterSpec) algorithmParameterSpec;
            this.param = new LMSKeyGenerationParameters(new LMSParameters(lMSKeyGenParameterSpec.getSigParams(), lMSKeyGenParameterSpec.getOtsParams()), secureRandom);
            this.engine = new LMSKeyPairGenerator();
            this.engine.init(this.param);
        } else if (algorithmParameterSpec instanceof LMSHSSKeyGenParameterSpec) {
            LMSKeyGenParameterSpec[] lMSSpecs = ((LMSHSSKeyGenParameterSpec) algorithmParameterSpec).getLMSSpecs();
            LMSParameters[] lMSParametersArr = new LMSParameters[lMSSpecs.length];
            for (int i = 0; i != lMSSpecs.length; i++) {
                lMSParametersArr[i] = new LMSParameters(lMSSpecs[i].getSigParams(), lMSSpecs[i].getOtsParams());
            }
            this.param = new HSSKeyGenerationParameters(lMSParametersArr, secureRandom);
            this.engine = new HSSKeyPairGenerator();
            this.engine.init(this.param);
        } else if (algorithmParameterSpec instanceof LMSParameterSpec) {
            LMSParameterSpec lMSParameterSpec = (LMSParameterSpec) algorithmParameterSpec;
            this.param = new LMSKeyGenerationParameters(new LMSParameters(lMSParameterSpec.getSigParams(), lMSParameterSpec.getOtsParams()), secureRandom);
            this.engine = new LMSKeyPairGenerator();
            this.engine.init(this.param);
        } else {
            if (!(algorithmParameterSpec instanceof LMSHSSParameterSpec)) {
                throw new InvalidAlgorithmParameterException("parameter object not a LMSParameterSpec/LMSHSSParameterSpec");
            }
            LMSParameterSpec[] lMSSpecs2 = ((LMSHSSParameterSpec) algorithmParameterSpec).getLMSSpecs();
            LMSParameters[] lMSParametersArr2 = new LMSParameters[lMSSpecs2.length];
            for (int i2 = 0; i2 != lMSSpecs2.length; i2++) {
                lMSParametersArr2[i2] = new LMSParameters(lMSSpecs2[i2].getSigParams(), lMSSpecs2[i2].getOtsParams());
            }
            this.param = new HSSKeyGenerationParameters(lMSParametersArr2, secureRandom);
            this.engine = new HSSKeyPairGenerator();
            this.engine.init(this.param);
        }
        this.initialised = true;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = new LMSKeyGenerationParameters(new LMSParameters(LMSigParameters.lms_sha256_n32_h10, LMOtsParameters.sha256_n32_w2), this.random);
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPairGenerateKeyPair = this.engine.generateKeyPair();
        if (this.engine instanceof LMSKeyPairGenerator) {
            return new KeyPair(new BCLMSPublicKey((LMSPublicKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPublic()), new BCLMSPrivateKey((LMSPrivateKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPrivate()));
        }
        return new KeyPair(new BCLMSPublicKey((HSSPublicKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPublic()), new BCLMSPrivateKey((HSSPrivateKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPrivate()));
    }
}
