package org.bouncycastle.pqc.crypto.hqc;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/hqc/HQCKeyPairGenerator.class */
public class HQCKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private int n;
    private int k;
    private int delta;
    private int w;
    private int wr;
    private int we;
    private int N_BYTE;
    private HQCKeyGenerationParameters hqcKeyGenerationParameters;
    private SecureRandom random;

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.hqcKeyGenerationParameters = (HQCKeyGenerationParameters) keyGenerationParameters;
        this.random = keyGenerationParameters.getRandom();
        this.n = this.hqcKeyGenerationParameters.getParameters().getN();
        this.k = this.hqcKeyGenerationParameters.getParameters().getK();
        this.delta = this.hqcKeyGenerationParameters.getParameters().getDelta();
        this.w = this.hqcKeyGenerationParameters.getParameters().getW();
        this.wr = this.hqcKeyGenerationParameters.getParameters().getWr();
        this.we = this.hqcKeyGenerationParameters.getParameters().getWe();
        this.N_BYTE = (this.n + 7) / 8;
    }

    private AsymmetricCipherKeyPair genKeyPair(byte[] bArr) {
        HQCEngine engine = this.hqcKeyGenerationParameters.getParameters().getEngine();
        byte[] bArr2 = new byte[40 + this.N_BYTE];
        byte[] bArr3 = new byte[80 + this.k + this.N_BYTE];
        engine.genKeyPair(bArr2, bArr3, bArr);
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new HQCPublicKeyParameters(this.hqcKeyGenerationParameters.getParameters(), bArr2), (AsymmetricKeyParameter) new HQCPrivateKeyParameters(this.hqcKeyGenerationParameters.getParameters(), bArr3));
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        byte[] bArr = new byte[48];
        this.random.nextBytes(bArr);
        return genKeyPair(bArr);
    }

    public AsymmetricCipherKeyPair generateKeyPairWithSeed(byte[] bArr) {
        return genKeyPair(bArr);
    }
}
