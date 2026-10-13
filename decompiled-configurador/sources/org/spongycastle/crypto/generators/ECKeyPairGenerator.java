package org.spongycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECKeyGenerationParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.math.ec.ECConstants;
import org.spongycastle.math.ec.ECMultiplier;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.ec.FixedPointCombMultiplier;
import org.spongycastle.math.ec.WNafUtil;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/generators/ECKeyPairGenerator.class */
public class ECKeyPairGenerator implements AsymmetricCipherKeyPairGenerator, ECConstants {
    ECDomainParameters params;
    SecureRandom random;

    @Override // org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters param) {
        ECKeyGenerationParameters ecP = (ECKeyGenerationParameters) param;
        this.random = ecP.getRandom();
        this.params = ecP.getDomainParameters();
        if (this.random == null) {
            this.random = new SecureRandom();
        }
    }

    @Override // org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        BigInteger n = this.params.getN();
        int nBitLength = n.bitLength();
        int minWeight = nBitLength >>> 2;
        while (true) {
            BigInteger d = new BigInteger(nBitLength, this.random);
            if (d.compareTo(TWO) >= 0 && d.compareTo(n) < 0 && WNafUtil.getNafWeight(d) >= minWeight) {
                ECPoint Q = createBasePointMultiplier().multiply(this.params.getG(), d);
                return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new ECPublicKeyParameters(Q, this.params), (AsymmetricKeyParameter) new ECPrivateKeyParameters(d, this.params));
            }
        }
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
}
