package org.spongycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/XMSSKeyPairGenerator.class */
public final class XMSSKeyPairGenerator {
    private XMSSParameters params;
    private SecureRandom prng;

    public void init(KeyGenerationParameters param) {
        XMSSKeyGenerationParameters parameters = (XMSSKeyGenerationParameters) param;
        this.prng = parameters.getRandom();
        this.params = parameters.getParameters();
    }

    public AsymmetricCipherKeyPair generateKeyPair() {
        XMSSPrivateKeyParameters privateKey = generatePrivateKey(this.params, this.prng);
        XMSSNode root = privateKey.getBDSState().getRoot();
        XMSSPrivateKeyParameters privateKey2 = new XMSSPrivateKeyParameters.Builder(this.params).withSecretKeySeed(privateKey.getSecretKeySeed()).withSecretKeyPRF(privateKey.getSecretKeyPRF()).withPublicSeed(privateKey.getPublicSeed()).withRoot(root.getValue()).withBDSState(privateKey.getBDSState()).build();
        XMSSPublicKeyParameters publicKey = new XMSSPublicKeyParameters.Builder(this.params).withRoot(root.getValue()).withPublicSeed(privateKey2.getPublicSeed()).build();
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) publicKey, (AsymmetricKeyParameter) privateKey2);
    }

    private XMSSPrivateKeyParameters generatePrivateKey(XMSSParameters params, SecureRandom prng) {
        int n = params.getDigestSize();
        byte[] secretKeySeed = new byte[n];
        prng.nextBytes(secretKeySeed);
        byte[] secretKeyPRF = new byte[n];
        prng.nextBytes(secretKeyPRF);
        byte[] publicSeed = new byte[n];
        prng.nextBytes(publicSeed);
        XMSSPrivateKeyParameters privateKey = new XMSSPrivateKeyParameters.Builder(params).withSecretKeySeed(secretKeySeed).withSecretKeyPRF(secretKeyPRF).withPublicSeed(publicSeed).withBDSState(new BDS(params, publicSeed, secretKeySeed, (OTSHashAddress) new OTSHashAddress.Builder().build())).build();
        return privateKey;
    }
}
