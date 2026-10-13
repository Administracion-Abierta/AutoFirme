package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntruprime/SNTRUPrimeKeyPairGenerator.class */
public class SNTRUPrimeKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private SNTRUPrimeKeyGenerationParameters params;

    public SNTRUPrimeKeyGenerationParameters getParams() {
        return this.params;
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.params = (SNTRUPrimeKeyGenerationParameters) keyGenerationParameters;
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        int p = this.params.getSntrupParams().getP();
        int q = this.params.getSntrupParams().getQ();
        int w = this.params.getSntrupParams().getW();
        byte[] bArr = new byte[p];
        byte[] bArr2 = new byte[p];
        do {
            Utils.getRandomSmallPolynomial(this.params.getRandom(), bArr);
        } while (!Utils.isInvertiblePolynomialInR3(bArr, bArr2, p));
        byte[] bArr3 = new byte[p];
        Utils.getRandomShortPolynomial(this.params.getRandom(), bArr3, p, w);
        short[] sArr = new short[p];
        Utils.getOneThirdInverseInRQ(sArr, bArr3, p, q);
        short[] sArr2 = new short[p];
        Utils.multiplicationInRQ(sArr2, sArr, bArr, p, q);
        byte[] bArr4 = new byte[this.params.getSntrupParams().getPublicKeyBytes()];
        Utils.getEncodedPolynomial(bArr4, sArr2, p, q);
        SNTRUPrimePublicKeyParameters sNTRUPrimePublicKeyParameters = new SNTRUPrimePublicKeyParameters(this.params.getSntrupParams(), bArr4);
        byte[] bArr5 = new byte[(p + 3) / 4];
        Utils.getEncodedSmallPolynomial(bArr5, bArr3, p);
        byte[] bArr6 = new byte[(p + 3) / 4];
        Utils.getEncodedSmallPolynomial(bArr6, bArr2, p);
        byte[] bArr7 = new byte[(p + 3) / 4];
        this.params.getRandom().nextBytes(bArr7);
        byte[] hashWithPrefix = Utils.getHashWithPrefix(new byte[]{4}, bArr4);
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) sNTRUPrimePublicKeyParameters, (AsymmetricKeyParameter) new SNTRUPrimePrivateKeyParameters(this.params.getSntrupParams(), bArr5, bArr6, bArr4, bArr7, Arrays.copyOfRange(hashWithPrefix, 0, hashWithPrefix.length / 2)));
    }
}
