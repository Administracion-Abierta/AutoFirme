package org.bouncycastle.pqc.jcajce.provider.ntru;

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
import org.bouncycastle.pqc.crypto.ntru.NTRUKEMExtractor;
import org.bouncycastle.pqc.crypto.ntru.NTRUKEMGenerator;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/ntru/NTRUKeyGeneratorSpi.class */
public class NTRUKeyGeneratorSpi extends KeyGeneratorSpi {
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
        if (this.genSpec == null) {
            NTRUKEMExtractor nTRUKEMExtractor = new NTRUKEMExtractor(((BCNTRUPrivateKey) this.extSpec.getPrivateKey()).getKeyParams());
            byte[] encapsulation = this.extSpec.getEncapsulation();
            byte[] bArrExtractSecret = nTRUKEMExtractor.extractSecret(encapsulation);
            byte[] bArrCopyOfRange = Arrays.copyOfRange(bArrExtractSecret, 0, (this.extSpec.getKeySize() + 7) / 8);
            Arrays.clear(bArrExtractSecret);
            SecretKeyWithEncapsulation secretKeyWithEncapsulation = new SecretKeyWithEncapsulation(new SecretKeySpec(bArrCopyOfRange, this.extSpec.getKeyAlgorithmName()), encapsulation);
            Arrays.clear(bArrCopyOfRange);
            return secretKeyWithEncapsulation;
        }
        SecretWithEncapsulation secretWithEncapsulationGenerateEncapsulated = new NTRUKEMGenerator(this.random).generateEncapsulated(((BCNTRUPublicKey) this.genSpec.getPublicKey()).getKeyParams());
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
