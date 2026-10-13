package org.spongycastle.crypto.ec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.math.ec.ECMultiplier;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.ec.FixedPointCombMultiplier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/ec/ECNewPublicKeyTransform.class */
public class ECNewPublicKeyTransform implements ECPairTransform {
    private ECPublicKeyParameters key;
    private SecureRandom random;

    @Override // org.spongycastle.crypto.ec.ECPairTransform
    public void init(CipherParameters param) {
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom p = (ParametersWithRandom) param;
            if (!(p.getParameters() instanceof ECPublicKeyParameters)) {
                throw new IllegalArgumentException("ECPublicKeyParameters are required for new public key transform.");
            }
            this.key = (ECPublicKeyParameters) p.getParameters();
            this.random = p.getRandom();
            return;
        }
        if (!(param instanceof ECPublicKeyParameters)) {
            throw new IllegalArgumentException("ECPublicKeyParameters are required for new public key transform.");
        }
        this.key = (ECPublicKeyParameters) param;
        this.random = new SecureRandom();
    }

    @Override // org.spongycastle.crypto.ec.ECPairTransform
    public ECPair transform(ECPair cipherText) {
        if (this.key == null) {
            throw new IllegalStateException("ECNewPublicKeyTransform not initialised");
        }
        ECDomainParameters ec = this.key.getParameters();
        BigInteger n = ec.getN();
        ECMultiplier basePointMultiplier = createBasePointMultiplier();
        BigInteger k = ECUtil.generateK(n, this.random);
        ECPoint[] gamma_phi = {basePointMultiplier.multiply(ec.getG(), k), this.key.getQ().multiply(k).add(cipherText.getY())};
        ec.getCurve().normalizeAll(gamma_phi);
        return new ECPair(gamma_phi[0], gamma_phi[1]);
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
}
