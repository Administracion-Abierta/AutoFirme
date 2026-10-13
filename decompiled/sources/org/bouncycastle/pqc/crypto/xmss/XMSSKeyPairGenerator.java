package org.bouncycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/XMSSKeyPairGenerator.class */
public final class XMSSKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private XMSSParameters params;
    private SecureRandom prng;

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        XMSSKeyGenerationParameters xMSSKeyGenerationParameters = (XMSSKeyGenerationParameters) keyGenerationParameters;
        this.prng = xMSSKeyGenerationParameters.getRandom();
        this.params = xMSSKeyGenerationParameters.getParameters();
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        XMSSPrivateKeyParameters xMSSPrivateKeyParametersGeneratePrivateKey = generatePrivateKey(this.params, this.prng);
        XMSSNode root = xMSSPrivateKeyParametersGeneratePrivateKey.getBDSState().getRoot();
        XMSSPrivateKeyParameters xMSSPrivateKeyParametersBuild = new XMSSPrivateKeyParameters.Builder(this.params).withSecretKeySeed(xMSSPrivateKeyParametersGeneratePrivateKey.getSecretKeySeed()).withSecretKeyPRF(xMSSPrivateKeyParametersGeneratePrivateKey.getSecretKeyPRF()).withPublicSeed(xMSSPrivateKeyParametersGeneratePrivateKey.getPublicSeed()).withRoot(root.getValue()).withBDSState(xMSSPrivateKeyParametersGeneratePrivateKey.getBDSState()).build();
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new XMSSPublicKeyParameters.Builder(this.params).withRoot(root.getValue()).withPublicSeed(xMSSPrivateKeyParametersBuild.getPublicSeed()).build(), (AsymmetricKeyParameter) xMSSPrivateKeyParametersBuild);
    }

    private XMSSPrivateKeyParameters generatePrivateKey(XMSSParameters xMSSParameters, SecureRandom secureRandom) {
        int treeDigestSize = xMSSParameters.getTreeDigestSize();
        byte[] bArr = new byte[treeDigestSize];
        secureRandom.nextBytes(bArr);
        byte[] bArr2 = new byte[treeDigestSize];
        secureRandom.nextBytes(bArr2);
        byte[] bArr3 = new byte[treeDigestSize];
        secureRandom.nextBytes(bArr3);
        return new XMSSPrivateKeyParameters.Builder(xMSSParameters).withSecretKeySeed(bArr).withSecretKeyPRF(bArr2).withPublicSeed(bArr3).withBDSState(new BDS(xMSSParameters, bArr3, bArr, (OTSHashAddress) new OTSHashAddress.Builder().build())).build();
    }
}
