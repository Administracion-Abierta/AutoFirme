package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntruprime/NTRULPRimeKeyPairGenerator.class */
public class NTRULPRimeKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private NTRULPRimeKeyGenerationParameters params;

    public NTRULPRimeKeyGenerationParameters getParams() {
        return this.params;
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.params = (NTRULPRimeKeyGenerationParameters) keyGenerationParameters;
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        int p = this.params.getNtrulprParams().getP();
        int q = this.params.getNtrulprParams().getQ();
        int w = this.params.getNtrulprParams().getW();
        byte[] bArr = new byte[32];
        this.params.getRandom().nextBytes(bArr);
        short[] sArr = new short[p];
        Utils.generatePolynomialInRQFromSeed(sArr, bArr, p, q);
        byte[] bArr2 = new byte[p];
        Utils.getRandomShortPolynomial(this.params.getRandom(), bArr2, p, w);
        short[] sArr2 = new short[p];
        Utils.multiplicationInRQ(sArr2, sArr, bArr2, p, q);
        short[] sArr3 = new short[p];
        Utils.roundPolynomial(sArr3, sArr2);
        byte[] bArr3 = new byte[this.params.getNtrulprParams().getPublicKeyBytes() - 32];
        Utils.getRoundedEncodedPolynomial(bArr3, sArr3, p, q);
        NTRULPRimePublicKeyParameters nTRULPRimePublicKeyParameters = new NTRULPRimePublicKeyParameters(this.params.getNtrulprParams(), bArr, bArr3);
        byte[] bArr4 = new byte[(p + 3) / 4];
        Utils.getEncodedSmallPolynomial(bArr4, bArr2, p);
        byte[] bArr5 = new byte[32];
        this.params.getRandom().nextBytes(bArr5);
        byte[] hashWithPrefix = Utils.getHashWithPrefix(new byte[]{4}, nTRULPRimePublicKeyParameters.getEncoded());
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) nTRULPRimePublicKeyParameters, (AsymmetricKeyParameter) new NTRULPRimePrivateKeyParameters(this.params.getNtrulprParams(), bArr4, nTRULPRimePublicKeyParameters.getEncoded(), bArr5, Arrays.copyOfRange(hashWithPrefix, 0, hashWithPrefix.length / 2)));
    }
}
