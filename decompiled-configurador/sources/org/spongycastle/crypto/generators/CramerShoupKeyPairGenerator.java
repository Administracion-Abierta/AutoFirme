package org.spongycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.CramerShoupKeyGenerationParameters;
import org.spongycastle.crypto.params.CramerShoupParameters;
import org.spongycastle.crypto.params.CramerShoupPrivateKeyParameters;
import org.spongycastle.crypto.params.CramerShoupPublicKeyParameters;
import org.spongycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/generators/CramerShoupKeyPairGenerator.class */
public class CramerShoupKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private CramerShoupKeyGenerationParameters param;

    @Override // org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters param) {
        this.param = (CramerShoupKeyGenerationParameters) param;
    }

    @Override // org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        CramerShoupParameters csParams = this.param.getParameters();
        CramerShoupPrivateKeyParameters sk = generatePrivateKey(this.param.getRandom(), csParams);
        CramerShoupPublicKeyParameters pk = calculatePublicKey(csParams, sk);
        sk.setPk(pk);
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) pk, (AsymmetricKeyParameter) sk);
    }

    private BigInteger generateRandomElement(BigInteger p, SecureRandom random) {
        return BigIntegers.createRandomInRange(ONE, p.subtract(ONE), random);
    }

    private CramerShoupPrivateKeyParameters generatePrivateKey(SecureRandom random, CramerShoupParameters csParams) {
        BigInteger p = csParams.getP();
        CramerShoupPrivateKeyParameters key = new CramerShoupPrivateKeyParameters(csParams, generateRandomElement(p, random), generateRandomElement(p, random), generateRandomElement(p, random), generateRandomElement(p, random), generateRandomElement(p, random));
        return key;
    }

    private CramerShoupPublicKeyParameters calculatePublicKey(CramerShoupParameters csParams, CramerShoupPrivateKeyParameters sk) {
        BigInteger g1 = csParams.getG1();
        BigInteger g2 = csParams.getG2();
        BigInteger p = csParams.getP();
        BigInteger c = g1.modPow(sk.getX1(), p).multiply(g2.modPow(sk.getX2(), p));
        BigInteger d = g1.modPow(sk.getY1(), p).multiply(g2.modPow(sk.getY2(), p));
        BigInteger h = g1.modPow(sk.getZ(), p);
        return new CramerShoupPublicKeyParameters(csParams, c, d, h);
    }
}
