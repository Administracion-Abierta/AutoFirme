package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/rainbow/RainbowKeyPairGenerator.class */
public class RainbowKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private RainbowKeyComputation rkc;
    private Version version;

    private void initialize(KeyGenerationParameters keyGenerationParameters) {
        RainbowParameters parameters = ((RainbowKeyGenerationParameters) keyGenerationParameters).getParameters();
        this.rkc = new RainbowKeyComputation(parameters, keyGenerationParameters.getRandom());
        this.version = parameters.getVersion();
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        initialize(keyGenerationParameters);
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        switch (this.version) {
            case CLASSIC:
                return this.rkc.genKeyPairClassical();
            case CIRCUMZENITHAL:
                return this.rkc.genKeyPairCircumzenithal();
            case COMPRESSED:
                return this.rkc.genKeyPairCompressed();
            default:
                throw new IllegalArgumentException("No valid version. Please choose one of the following: classic, circumzenithal, compressed");
        }
    }
}
