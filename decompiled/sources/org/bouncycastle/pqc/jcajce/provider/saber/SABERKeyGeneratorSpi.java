package org.bouncycastle.pqc.jcajce.provider.saber;

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
import org.bouncycastle.pqc.crypto.saber.SABERKEMExtractor;
import org.bouncycastle.pqc.crypto.saber.SABERKEMGenerator;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/saber/SABERKeyGeneratorSpi.class */
public class SABERKeyGeneratorSpi extends KeyGeneratorSpi {
    private KEMGenerateSpec genSpec;
    private SecureRandom random;
    private KEMExtractSpec extSpec;

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
        } else {
            if (!(algorithmParameterSpec instanceof KEMExtractSpec)) {
                throw new InvalidAlgorithmParameterException("unknown spec");
            }
            this.genSpec = null;
            this.extSpec = (KEMExtractSpec) algorithmParameterSpec;
        }
    }

    @Override // javax.crypto.KeyGeneratorSpi
    protected void engineInit(int i, SecureRandom secureRandom) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override // javax.crypto.KeyGeneratorSpi
    protected SecretKey engineGenerateKey() {
        if (this.genSpec != null) {
            SecretWithEncapsulation secretWithEncapsulationGenerateEncapsulated = new SABERKEMGenerator(this.random).generateEncapsulated(((BCSABERPublicKey) this.genSpec.getPublicKey()).getKeyParams());
            SecretKeyWithEncapsulation secretKeyWithEncapsulation = new SecretKeyWithEncapsulation(new SecretKeySpec(secretWithEncapsulationGenerateEncapsulated.getSecret(), this.genSpec.getKeyAlgorithmName()), secretWithEncapsulationGenerateEncapsulated.getEncapsulation());
            try {
                secretWithEncapsulationGenerateEncapsulated.destroy();
                return secretKeyWithEncapsulation;
            } catch (DestroyFailedException e) {
                throw new IllegalStateException("key cleanup failed");
            }
        }
        SABERKEMExtractor sABERKEMExtractor = new SABERKEMExtractor(((BCSABERPrivateKey) this.extSpec.getPrivateKey()).getKeyParams());
        byte[] encapsulation = this.extSpec.getEncapsulation();
        byte[] bArrExtractSecret = sABERKEMExtractor.extractSecret(encapsulation);
        SecretKeyWithEncapsulation secretKeyWithEncapsulation2 = new SecretKeyWithEncapsulation(new SecretKeySpec(bArrExtractSecret, this.extSpec.getKeyAlgorithmName()), encapsulation);
        Arrays.clear(bArrExtractSecret);
        return secretKeyWithEncapsulation2;
    }
}
