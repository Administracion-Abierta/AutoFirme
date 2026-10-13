package org.bouncycastle.pqc.crypto.sphincsplus;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/sphincsplus/SPHINCSPlusKeyPairGenerator.class */
public class SPHINCSPlusKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private SecureRandom random;
    private SPHINCSPlusParameters parameters;

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.random = keyGenerationParameters.getRandom();
        this.parameters = ((SPHINCSPlusKeyGenerationParameters) keyGenerationParameters).getParameters();
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        SK sk;
        byte[] bArrSec_rand;
        SPHINCSPlusEngine engine = this.parameters.getEngine();
        if (engine instanceof SPHINCSPlusEngine.HarakaSEngine) {
            byte[] bArrSec_rand2 = sec_rand(engine.N * 3);
            byte[] bArr = new byte[engine.N];
            byte[] bArr2 = new byte[engine.N];
            bArrSec_rand = new byte[engine.N];
            System.arraycopy(bArrSec_rand2, 0, bArr, 0, engine.N);
            System.arraycopy(bArrSec_rand2, engine.N, bArr2, 0, engine.N);
            System.arraycopy(bArrSec_rand2, engine.N << 1, bArrSec_rand, 0, engine.N);
            sk = new SK(bArr, bArr2);
        } else {
            sk = new SK(sec_rand(engine.N), sec_rand(engine.N));
            bArrSec_rand = sec_rand(engine.N);
        }
        engine.init(bArrSec_rand);
        PK pk = new PK(bArrSec_rand, new HT(engine, sk.seed, bArrSec_rand).htPubKey);
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new SPHINCSPlusPublicKeyParameters(this.parameters, pk), (AsymmetricKeyParameter) new SPHINCSPlusPrivateKeyParameters(this.parameters, sk, pk));
    }

    private byte[] sec_rand(int i) {
        byte[] bArr = new byte[i];
        this.random.nextBytes(bArr);
        return bArr;
    }
}
