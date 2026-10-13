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

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/ec/ECNewRandomnessTransform.class */
public class ECNewRandomnessTransform implements ECPairFactorTransform {
    private ECPublicKeyParameters key;
    private SecureRandom random;
    private BigInteger lastK;

    @Override // org.spongycastle.crypto.ec.ECPairTransform
    public void init(CipherParameters param) {
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom p = (ParametersWithRandom) param;
            if (!(p.getParameters() instanceof ECPublicKeyParameters)) {
                throw new IllegalArgumentException("ECPublicKeyParameters are required for new randomness transform.");
            }
            this.key = (ECPublicKeyParameters) p.getParameters();
            this.random = p.getRandom();
            return;
        }
        if (!(param instanceof ECPublicKeyParameters)) {
            throw new IllegalArgumentException("ECPublicKeyParameters are required for new randomness transform.");
        }
        this.key = (ECPublicKeyParameters) param;
        this.random = new SecureRandom();
    }

    @Override // org.spongycastle.crypto.ec.ECPairTransform
    public ECPair transform(ECPair cipherText) {
        if (this.key == null) {
            throw new IllegalStateException("ECNewRandomnessTransform not initialised");
        }
        ECDomainParameters ec = this.key.getParameters();
        BigInteger n = ec.getN();
        ECMultiplier basePointMultiplier = createBasePointMultiplier();
        BigInteger k = ECUtil.generateK(n, this.random);
        ECPoint[] gamma_phi = {basePointMultiplier.multiply(ec.getG(), k).add(cipherText.getX()), this.key.getQ().multiply(k).add(cipherText.getY())};
        ec.getCurve().normalizeAll(gamma_phi);
        this.lastK = k;
        return new ECPair(gamma_phi[0], gamma_phi[1]);
    }

    @Override // org.spongycastle.crypto.ec.ECPairFactorTransform
    public BigInteger getTransformValue() {
        return this.lastK;
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
}
