package org.bouncycastle.jcajce.spec;

import java.math.BigInteger;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPrivateKeySpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/DHExtendedPrivateKeySpec.class */
public class DHExtendedPrivateKeySpec extends DHPrivateKeySpec {
    private final DHParameterSpec params;

    public DHExtendedPrivateKeySpec(BigInteger bigInteger, DHParameterSpec dHParameterSpec) {
        super(bigInteger, dHParameterSpec.getP(), dHParameterSpec.getG());
        this.params = dHParameterSpec;
    }

    public DHParameterSpec getParams() {
        return this.params;
    }
}
