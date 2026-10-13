package org.spongycastle.jcajce.provider.asymmetric.gost;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.spongycastle.crypto.generators.GOST3410ParametersGenerator;
import org.spongycastle.crypto.params.GOST3410Parameters;
import org.spongycastle.jcajce.provider.asymmetric.util.BaseAlgorithmParameterGeneratorSpi;
import org.spongycastle.jce.spec.GOST3410ParameterSpec;
import org.spongycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/gost/AlgorithmParameterGeneratorSpi.class */
public abstract class AlgorithmParameterGeneratorSpi extends BaseAlgorithmParameterGeneratorSpi {
    protected SecureRandom random;
    protected int strength = 1024;

    @Override // java.security.AlgorithmParameterGeneratorSpi
    protected void engineInit(int strength, SecureRandom random) {
        this.strength = strength;
        this.random = random;
    }

    @Override // java.security.AlgorithmParameterGeneratorSpi
    protected void engineInit(AlgorithmParameterSpec genParamSpec, SecureRandom random) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for GOST3410 parameter generation.");
    }

    @Override // java.security.AlgorithmParameterGeneratorSpi
    protected AlgorithmParameters engineGenerateParameters() {
        GOST3410ParametersGenerator pGen = new GOST3410ParametersGenerator();
        if (this.random != null) {
            pGen.init(this.strength, 2, this.random);
        } else {
            pGen.init(this.strength, 2, new SecureRandom());
        }
        GOST3410Parameters p = pGen.generateParameters();
        try {
            AlgorithmParameters params = createParametersInstance("GOST3410");
            params.init(new GOST3410ParameterSpec(new GOST3410PublicKeyParameterSetSpec(p.getP(), p.getQ(), p.getA())));
            return params;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
