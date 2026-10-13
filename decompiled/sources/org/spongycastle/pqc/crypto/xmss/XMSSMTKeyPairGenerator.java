package org.spongycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/XMSSMTKeyPairGenerator.class */
public final class XMSSMTKeyPairGenerator {
    private XMSSMTParameters params;
    private XMSSParameters xmssParams;
    private SecureRandom prng;

    public void init(KeyGenerationParameters param) {
        XMSSMTKeyGenerationParameters parameters = (XMSSMTKeyGenerationParameters) param;
        this.prng = parameters.getRandom();
        this.params = parameters.getParameters();
        this.xmssParams = this.params.getXMSSParameters();
    }

    public AsymmetricCipherKeyPair generateKeyPair() {
        XMSSMTPrivateKeyParameters privateKey = generatePrivateKey(new XMSSMTPrivateKeyParameters.Builder(this.params).build().getBDSState());
        this.xmssParams.getWOTSPlus().importKeys(new byte[this.params.getDigestSize()], privateKey.getPublicSeed());
        int rootLayerIndex = this.params.getLayers() - 1;
        OTSHashAddress otsHashAddress = (OTSHashAddress) new OTSHashAddress.Builder().withLayerAddress(rootLayerIndex).build();
        BDS bdsRoot = new BDS(this.xmssParams, privateKey.getPublicSeed(), privateKey.getSecretKeySeed(), otsHashAddress);
        XMSSNode root = bdsRoot.getRoot();
        privateKey.getBDSState().put(rootLayerIndex, bdsRoot);
        XMSSMTPrivateKeyParameters privateKey2 = new XMSSMTPrivateKeyParameters.Builder(this.params).withSecretKeySeed(privateKey.getSecretKeySeed()).withSecretKeyPRF(privateKey.getSecretKeyPRF()).withPublicSeed(privateKey.getPublicSeed()).withRoot(root.getValue()).withBDSState(privateKey.getBDSState()).build();
        XMSSMTPublicKeyParameters publicKey = new XMSSMTPublicKeyParameters.Builder(this.params).withRoot(root.getValue()).withPublicSeed(privateKey2.getPublicSeed()).build();
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) publicKey, (AsymmetricKeyParameter) privateKey2);
    }

    private XMSSMTPrivateKeyParameters generatePrivateKey(BDSStateMap bdsState) {
        int n = this.params.getDigestSize();
        byte[] secretKeySeed = new byte[n];
        this.prng.nextBytes(secretKeySeed);
        byte[] secretKeyPRF = new byte[n];
        this.prng.nextBytes(secretKeyPRF);
        byte[] publicSeed = new byte[n];
        this.prng.nextBytes(publicSeed);
        XMSSMTPrivateKeyParameters privateKey = new XMSSMTPrivateKeyParameters.Builder(this.params).withSecretKeySeed(secretKeySeed).withSecretKeyPRF(secretKeyPRF).withPublicSeed(publicSeed).withBDSState(bdsState).build();
        return privateKey;
    }
}
