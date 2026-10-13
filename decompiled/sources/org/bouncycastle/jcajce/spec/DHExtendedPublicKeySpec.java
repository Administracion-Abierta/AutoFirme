package org.bouncycastle.jcajce.spec;

import java.math.BigInteger;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/DHExtendedPublicKeySpec.class */
public class DHExtendedPublicKeySpec extends DHPublicKeySpec {
    private final DHParameterSpec params;

    public DHExtendedPublicKeySpec(BigInteger bigInteger, DHParameterSpec dHParameterSpec) {
        super(bigInteger, dHParameterSpec.getP(), dHParameterSpec.getG());
        this.params = dHParameterSpec;
    }

    public DHParameterSpec getParams() {
        return this.params;
    }
}
