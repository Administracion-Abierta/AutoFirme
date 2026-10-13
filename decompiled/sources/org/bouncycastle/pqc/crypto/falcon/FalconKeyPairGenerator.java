package org.bouncycastle.pqc.crypto.falcon;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/falcon/FalconKeyPairGenerator.class */
public class FalconKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private FalconKeyGenerationParameters params;
    private SecureRandom random;
    private FalconNIST nist;
    private int logn;
    private int noncelen;
    private int pk_size;
    private int sk_size;

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.params = (FalconKeyGenerationParameters) keyGenerationParameters;
        this.random = keyGenerationParameters.getRandom();
        this.logn = ((FalconKeyGenerationParameters) keyGenerationParameters).getParameters().getLogN();
        this.noncelen = ((FalconKeyGenerationParameters) keyGenerationParameters).getParameters().getNonceLength();
        this.nist = new FalconNIST(this.logn, this.noncelen, this.random);
        int i = 1 << this.logn;
        int i2 = 8;
        if (i == 1024) {
            i2 = 5;
        } else if (i == 256 || i == 512) {
            i2 = 6;
        } else if (i == 64 || i == 128) {
            i2 = 7;
        }
        this.pk_size = 1 + ((14 * i) / 8);
        this.sk_size = 1 + (((2 * i2) * i) / 8) + i;
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        byte[][] bArrCrypto_sign_keypair = this.nist.crypto_sign_keypair(new byte[this.pk_size], 0, new byte[this.sk_size], 0);
        FalconParameters parameters = this.params.getParameters();
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new FalconPublicKeyParameters(parameters, bArrCrypto_sign_keypair[0]), (AsymmetricKeyParameter) new FalconPrivateKeyParameters(parameters, bArrCrypto_sign_keypair[1], bArrCrypto_sign_keypair[2], bArrCrypto_sign_keypair[3], bArrCrypto_sign_keypair[0]));
    }
}
