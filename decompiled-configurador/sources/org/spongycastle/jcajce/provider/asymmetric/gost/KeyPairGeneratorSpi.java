package org.spongycastle.jcajce.provider.asymmetric.gost;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.generators.GOST3410KeyPairGenerator;
import org.spongycastle.crypto.params.GOST3410KeyGenerationParameters;
import org.spongycastle.crypto.params.GOST3410Parameters;
import org.spongycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.spongycastle.crypto.params.GOST3410PublicKeyParameters;
import org.spongycastle.jce.spec.GOST3410ParameterSpec;
import org.spongycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/gost/KeyPairGeneratorSpi.class */
public class KeyPairGeneratorSpi extends KeyPairGenerator {
    GOST3410KeyGenerationParameters param;
    GOST3410KeyPairGenerator engine;
    GOST3410ParameterSpec gost3410Params;
    int strength;
    SecureRandom random;
    boolean initialised;

    public KeyPairGeneratorSpi() {
        super("GOST3410");
        this.engine = new GOST3410KeyPairGenerator();
        this.strength = 1024;
        this.random = null;
        this.initialised = false;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(int strength, SecureRandom random) {
        this.strength = strength;
        this.random = random;
    }

    private void init(GOST3410ParameterSpec gParams, SecureRandom random) {
        GOST3410PublicKeyParameterSetSpec spec = gParams.getPublicKeyParameters();
        this.param = new GOST3410KeyGenerationParameters(random, new GOST3410Parameters(spec.getP(), spec.getQ(), spec.getA()));
        this.engine.init(this.param);
        this.initialised = true;
        this.gost3410Params = gParams;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(params instanceof GOST3410ParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a GOST3410ParameterSpec");
        }
        init((GOST3410ParameterSpec) params, random);
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            init(new GOST3410ParameterSpec(CryptoProObjectIdentifiers.gostR3410_94_CryptoPro_A.getId()), new SecureRandom());
        }
        AsymmetricCipherKeyPair pair = this.engine.generateKeyPair();
        GOST3410PublicKeyParameters pub = (GOST3410PublicKeyParameters) pair.getPublic();
        GOST3410PrivateKeyParameters priv = (GOST3410PrivateKeyParameters) pair.getPrivate();
        return new KeyPair(new BCGOST3410PublicKey(pub, this.gost3410Params), new BCGOST3410PrivateKey(priv, this.gost3410Params));
    }
}
