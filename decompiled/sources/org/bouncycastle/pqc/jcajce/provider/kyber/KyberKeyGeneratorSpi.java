package org.bouncycastle.pqc.jcajce.provider.kyber;

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
import org.bouncycastle.pqc.crypto.mlkem.MLKEMExtractor;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/kyber/KyberKeyGeneratorSpi.class */
public class KyberKeyGeneratorSpi extends KeyGeneratorSpi {
    private KEMGenerateSpec genSpec;
    private SecureRandom random;
    private KEMExtractSpec extSpec;
    private MLKEMParameters kyberParameters;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/kyber/KyberKeyGeneratorSpi$Kyber1024.class */
    public static class Kyber1024 extends KyberKeyGeneratorSpi {
        public Kyber1024() {
            super(MLKEMParameters.ml_kem_1024);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/kyber/KyberKeyGeneratorSpi$Kyber512.class */
    public static class Kyber512 extends KyberKeyGeneratorSpi {
        public Kyber512() {
            super(MLKEMParameters.ml_kem_512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/kyber/KyberKeyGeneratorSpi$Kyber768.class */
    public static class Kyber768 extends KyberKeyGeneratorSpi {
        public Kyber768() {
            super(MLKEMParameters.ml_kem_768);
        }
    }

    public KyberKeyGeneratorSpi() {
        this(null);
    }

    protected KyberKeyGeneratorSpi(MLKEMParameters mLKEMParameters) {
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
                String upperCase = Strings.toUpperCase(this.kyberParameters.getName());
                if (!upperCase.equals(this.genSpec.getPublicKey().getAlgorithm())) {
                    throw new InvalidAlgorithmParameterException("key generator locked to " + upperCase);
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
            String upperCase2 = Strings.toUpperCase(this.kyberParameters.getName());
            if (!upperCase2.equals(this.extSpec.getPrivateKey().getAlgorithm())) {
                throw new InvalidAlgorithmParameterException("key generator locked to " + upperCase2);
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
            MLKEMExtractor mLKEMExtractor = new MLKEMExtractor(((BCKyberPrivateKey) this.extSpec.getPrivateKey()).getKeyParams());
            byte[] encapsulation = this.extSpec.getEncapsulation();
            byte[] bArrExtractSecret = mLKEMExtractor.extractSecret(encapsulation);
            byte[] bArrCopyOfRange = Arrays.copyOfRange(bArrExtractSecret, 0, (this.extSpec.getKeySize() + 7) / 8);
            Arrays.clear(bArrExtractSecret);
            SecretKeyWithEncapsulation secretKeyWithEncapsulation = new SecretKeyWithEncapsulation(new SecretKeySpec(bArrCopyOfRange, this.extSpec.getKeyAlgorithmName()), encapsulation);
            Arrays.clear(bArrCopyOfRange);
            return secretKeyWithEncapsulation;
        }
        SecretWithEncapsulation secretWithEncapsulationGenerateEncapsulated = new MLKEMGenerator(this.random).generateEncapsulated(((BCKyberPublicKey) this.genSpec.getPublicKey()).getKeyParams());
        byte[] secret = secretWithEncapsulationGenerateEncapsulated.getSecret();
        byte[] bArrCopyOfRange2 = Arrays.copyOfRange(secret, 0, (this.genSpec.getKeySize() + 7) / 8);
        Arrays.clear(secret);
        SecretKeyWithEncapsulation secretKeyWithEncapsulation2 = new SecretKeyWithEncapsulation(new SecretKeySpec(bArrCopyOfRange2, this.genSpec.getKeyAlgorithmName()), secretWithEncapsulationGenerateEncapsulated.getEncapsulation());
        try {
            secretWithEncapsulationGenerateEncapsulated.destroy();
            return secretKeyWithEncapsulation2;
        } catch (DestroyFailedException e) {
            throw new IllegalStateException("key cleanup failed");
        }
    }
}
