package org.bouncycastle.pqc.crypto.crystals.dilithium;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/crystals/dilithium/DilithiumKeyPairGenerator.class */
public class DilithiumKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private DilithiumParameters dilithiumParams;
    private SecureRandom random;

    private void initialize(KeyGenerationParameters keyGenerationParameters) {
        this.dilithiumParams = ((DilithiumKeyGenerationParameters) keyGenerationParameters).getParameters();
        this.random = keyGenerationParameters.getRandom();
    }

    private AsymmetricCipherKeyPair genKeyPair() {
        byte[][] bArrGenerateKeyPair = this.dilithiumParams.getEngine(this.random).generateKeyPair();
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new DilithiumPublicKeyParameters(this.dilithiumParams, bArrGenerateKeyPair[0], bArrGenerateKeyPair[6]), (AsymmetricKeyParameter) new DilithiumPrivateKeyParameters(this.dilithiumParams, bArrGenerateKeyPair[0], bArrGenerateKeyPair[1], bArrGenerateKeyPair[2], bArrGenerateKeyPair[3], bArrGenerateKeyPair[4], bArrGenerateKeyPair[5], bArrGenerateKeyPair[6]));
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        initialize(keyGenerationParameters);
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        return genKeyPair();
    }

    public AsymmetricCipherKeyPair internalGenerateKeyPair(byte[] bArr) {
        byte[][] bArrGenerateKeyPairInternal = this.dilithiumParams.getEngine(this.random).generateKeyPairInternal(bArr);
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new DilithiumPublicKeyParameters(this.dilithiumParams, bArrGenerateKeyPairInternal[0], bArrGenerateKeyPairInternal[6]), (AsymmetricKeyParameter) new DilithiumPrivateKeyParameters(this.dilithiumParams, bArrGenerateKeyPairInternal[0], bArrGenerateKeyPairInternal[1], bArrGenerateKeyPairInternal[2], bArrGenerateKeyPairInternal[3], bArrGenerateKeyPairInternal[4], bArrGenerateKeyPairInternal[5], bArrGenerateKeyPairInternal[6]));
    }
}
