package org.bouncycastle.pqc.crypto.mldsa;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mldsa/MLDSAKeyPairGenerator.class */
public class MLDSAKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private MLDSAParameters parameters;
    private SecureRandom random;

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.parameters = ((MLDSAKeyGenerationParameters) keyGenerationParameters).getParameters();
        this.random = keyGenerationParameters.getRandom();
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        byte[][] bArrGenerateKeyPair = this.parameters.getEngine(this.random).generateKeyPair();
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new MLDSAPublicKeyParameters(this.parameters, bArrGenerateKeyPair[0], bArrGenerateKeyPair[6]), (AsymmetricKeyParameter) new MLDSAPrivateKeyParameters(this.parameters, bArrGenerateKeyPair[0], bArrGenerateKeyPair[1], bArrGenerateKeyPair[2], bArrGenerateKeyPair[3], bArrGenerateKeyPair[4], bArrGenerateKeyPair[5], bArrGenerateKeyPair[6], bArrGenerateKeyPair[7]));
    }
}
