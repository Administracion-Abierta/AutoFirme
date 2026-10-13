package org.spongycastle.crypto.generators;

import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/generators/DSTU4145KeyPairGenerator.class */
public class DSTU4145KeyPairGenerator extends ECKeyPairGenerator {
    @Override // org.spongycastle.crypto.generators.ECKeyPairGenerator, org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        AsymmetricCipherKeyPair pair = super.generateKeyPair();
        ECPublicKeyParameters pub = (ECPublicKeyParameters) pair.getPublic();
        ECPrivateKeyParameters priv = (ECPrivateKeyParameters) pair.getPrivate();
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new ECPublicKeyParameters(pub.getQ().negate(), pub.getParameters()), (AsymmetricKeyParameter) priv);
    }
}
