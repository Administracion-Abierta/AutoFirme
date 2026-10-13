package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/generators/DSTU4145KeyPairGenerator.class */
public class DSTU4145KeyPairGenerator extends ECKeyPairGenerator {
    @Override // org.bouncycastle.crypto.generators.ECKeyPairGenerator, org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        AsymmetricCipherKeyPair asymmetricCipherKeyPairGenerateKeyPair = super.generateKeyPair();
        ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPublic();
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new ECPublicKeyParameters(eCPublicKeyParameters.getQ().negate(), eCPublicKeyParameters.getParameters()), asymmetricCipherKeyPairGenerateKeyPair.getPrivate());
    }
}
