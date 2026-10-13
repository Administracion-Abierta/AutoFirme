package org.bouncycastle.pqc.crypto.ntru;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntru/NTRUKeyPairGenerator.class */
public class NTRUKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private NTRUKeyGenerationParameters params;
    private SecureRandom random;

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.params = (NTRUKeyGenerationParameters) keyGenerationParameters;
        this.random = keyGenerationParameters.getRandom();
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        NTRUParameters parameters = this.params.getParameters();
        NTRUParameterSet parameterSet = parameters.getParameterSet();
        byte[] bArr = new byte[parameterSet.sampleFgBytes()];
        this.random.nextBytes(bArr);
        OWCPAKeyPair oWCPAKeyPairKeypair = new NTRUOWCPA(parameterSet).keypair(bArr);
        byte[] bArr2 = oWCPAKeyPairKeypair.publicKey;
        byte[] bArr3 = new byte[parameterSet.prfKeyBytes()];
        this.random.nextBytes(bArr3);
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new NTRUPublicKeyParameters(parameters, bArr2), (AsymmetricKeyParameter) new NTRUPrivateKeyParameters(parameters, Arrays.concatenate(oWCPAKeyPairKeypair.privateKey, bArr3)));
    }
}
