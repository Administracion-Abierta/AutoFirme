package org.bouncycastle.pqc.crypto.lms;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/LMSKeyPairGenerator.class */
public class LMSKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    LMSKeyGenerationParameters param;

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.param = (LMSKeyGenerationParameters) keyGenerationParameters;
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        SecureRandom random = this.param.getRandom();
        byte[] bArr = new byte[16];
        random.nextBytes(bArr);
        LMSigParameters lMSigParam = this.param.getParameters().getLMSigParam();
        byte[] bArr2 = new byte[lMSigParam.getM()];
        random.nextBytes(bArr2);
        LMSPrivateKeyParameters lMSPrivateKeyParametersGenerateKeys = LMS.generateKeys(lMSigParam, this.param.getParameters().getLMOTSParam(), 0, bArr, bArr2);
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) lMSPrivateKeyParametersGenerateKeys.getPublicKey(), (AsymmetricKeyParameter) lMSPrivateKeyParametersGenerateKeys);
    }
}
