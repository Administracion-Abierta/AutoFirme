package org.bouncycastle.jcajce.provider.asymmetric.mlkem;

import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.jcajce.spec.KEMExtractSpec;
import org.bouncycastle.jcajce.spec.KEMGenerateSpec;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMExtractor;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.jcajce.provider.util.KdfUtil;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mlkem/MLKEMKeyGeneratorSpi.class */
public class MLKEMKeyGeneratorSpi extends KeyGeneratorSpi {
    private KEMGenerateSpec genSpec;
    private SecureRandom random;
    private KEMExtractSpec extSpec;
    private MLKEMParameters kyberParameters;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mlkem/MLKEMKeyGeneratorSpi$MLKEM1024.class */
    public static class MLKEM1024 extends MLKEMKeyGeneratorSpi {
        public MLKEM1024() {
            super(MLKEMParameters.ml_kem_1024);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mlkem/MLKEMKeyGeneratorSpi$MLKEM512.class */
    public static class MLKEM512 extends MLKEMKeyGeneratorSpi {
        public MLKEM512() {
            super(MLKEMParameters.ml_kem_512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mlkem/MLKEMKeyGeneratorSpi$MLKEM768.class */
    public static class MLKEM768 extends MLKEMKeyGeneratorSpi {
        public MLKEM768() {
            super(MLKEMParameters.ml_kem_768);
        }
    }

    public MLKEMKeyGeneratorSpi() {
        this(null);
    }

    protected MLKEMKeyGeneratorSpi(MLKEMParameters mLKEMParameters) {
        this.kyberParameters = mLKEMParameters;
    }

    @Override // javax.crypto.KeyGeneratorSpi
    protected void engineInit(SecureRandom secureRandom) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override // javax.crypto.KeyGeneratorSpi
    protected void engineInit(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        this.random = secureRandom;
        if (algorithmParameterSpec instanceof KEMGenerateSpec) {
            this.genSpec = (KEMGenerateSpec) algorithmParameterSpec;
            this.extSpec = null;
            if (this.kyberParameters != null) {
                String name = MLKEMParameterSpec.fromName(this.kyberParameters.getName()).getName();
                if (!name.equals(this.genSpec.getPublicKey().getAlgorithm())) {
                    throw new InvalidAlgorithmParameterException("key generator locked to " + name);
                }
                return;
            }
            return;
        }
        if (!(algorithmParameterSpec instanceof KEMExtractSpec)) {
            throw new InvalidAlgorithmParameterException("unknown spec");
        }
        this.genSpec = null;
        this.extSpec = (KEMExtractSpec) algorithmParameterSpec;
        if (this.kyberParameters != null) {
            String name2 = MLKEMParameterSpec.fromName(this.kyberParameters.getName()).getName();
            if (!name2.equals(this.extSpec.getPrivateKey().getAlgorithm())) {
                throw new InvalidAlgorithmParameterException("key generator locked to " + name2);
            }
        }
    }

    @Override // javax.crypto.KeyGeneratorSpi
    protected void engineInit(int i, SecureRandom secureRandom) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override // javax.crypto.KeyGeneratorSpi
    protected SecretKey engineGenerateKey() {
        if (this.genSpec == null) {
            MLKEMExtractor mLKEMExtractor = new MLKEMExtractor(((BCMLKEMPrivateKey) this.extSpec.getPrivateKey()).getKeyParams());
            byte[] encapsulation = this.extSpec.getEncapsulation();
            byte[] bArrExtractSecret = mLKEMExtractor.extractSecret(encapsulation);
            byte[] bArrMakeKeyBytes = KdfUtil.makeKeyBytes(this.extSpec, bArrExtractSecret);
            Arrays.clear(bArrExtractSecret);
            SecretKeyWithEncapsulation secretKeyWithEncapsulation = new SecretKeyWithEncapsulation(new SecretKeySpec(bArrMakeKeyBytes, this.extSpec.getKeyAlgorithmName()), encapsulation);
            Arrays.clear(bArrMakeKeyBytes);
            return secretKeyWithEncapsulation;
        }
        SecretWithEncapsulation secretWithEncapsulationGenerateEncapsulated = new MLKEMGenerator(this.random).generateEncapsulated(((BCMLKEMPublicKey) this.genSpec.getPublicKey()).getKeyParams());
        byte[] secret = secretWithEncapsulationGenerateEncapsulated.getSecret();
        byte[] bArrMakeKeyBytes2 = KdfUtil.makeKeyBytes(this.genSpec, secret);
        Arrays.clear(secret);
        SecretKeyWithEncapsulation secretKeyWithEncapsulation2 = new SecretKeyWithEncapsulation(new SecretKeySpec(bArrMakeKeyBytes2, this.genSpec.getKeyAlgorithmName()), secretWithEncapsulationGenerateEncapsulated.getEncapsulation());
        try {
            secretWithEncapsulationGenerateEncapsulated.destroy();
            return secretKeyWithEncapsulation2;
        } catch (DestroyFailedException e) {
            throw new IllegalStateException("key cleanup failed");
        }
    }
}
