package org.bouncycastle.pqc.crypto.mlkem;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mlkem/MLKEMKeyPairGenerator.class */
public class MLKEMKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private MLKEMParameters mlkemParams;
    private SecureRandom random;

    private void initialize(KeyGenerationParameters keyGenerationParameters) {
        this.mlkemParams = ((MLKEMKeyGenerationParameters) keyGenerationParameters).getParameters();
        this.random = keyGenerationParameters.getRandom();
    }

    private AsymmetricCipherKeyPair genKeyPair() {
        MLKEMEngine engine = this.mlkemParams.getEngine();
        engine.init(this.random);
        byte[][] bArrGenerateKemKeyPair = engine.generateKemKeyPair();
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new MLKEMPublicKeyParameters(this.mlkemParams, bArrGenerateKemKeyPair[0], bArrGenerateKemKeyPair[1]), (AsymmetricKeyParameter) new MLKEMPrivateKeyParameters(this.mlkemParams, bArrGenerateKemKeyPair[2], bArrGenerateKemKeyPair[3], bArrGenerateKemKeyPair[4], bArrGenerateKemKeyPair[0], bArrGenerateKemKeyPair[1], bArrGenerateKemKeyPair[5]));
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        initialize(keyGenerationParameters);
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        return genKeyPair();
    }

    public AsymmetricCipherKeyPair internalGenerateKeyPair(byte[] bArr, byte[] bArr2) {
        byte[][] bArrGenerateKemKeyPairInternal = this.mlkemParams.getEngine().generateKemKeyPairInternal(bArr, bArr2);
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new MLKEMPublicKeyParameters(this.mlkemParams, bArrGenerateKemKeyPairInternal[0], bArrGenerateKemKeyPairInternal[1]), (AsymmetricKeyParameter) new MLKEMPrivateKeyParameters(this.mlkemParams, bArrGenerateKemKeyPairInternal[2], bArrGenerateKemKeyPairInternal[3], bArrGenerateKemKeyPairInternal[4], bArrGenerateKemKeyPairInternal[0], bArrGenerateKemKeyPairInternal[1], bArrGenerateKemKeyPairInternal[5]));
    }
}
