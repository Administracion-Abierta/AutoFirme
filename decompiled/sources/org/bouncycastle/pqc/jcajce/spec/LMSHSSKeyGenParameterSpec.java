package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/spec/LMSHSSKeyGenParameterSpec.class */
public class LMSHSSKeyGenParameterSpec implements AlgorithmParameterSpec {
    private final LMSKeyGenParameterSpec[] specs;

    public LMSHSSKeyGenParameterSpec(LMSKeyGenParameterSpec... lMSKeyGenParameterSpecArr) {
        if (lMSKeyGenParameterSpecArr.length == 0) {
            throw new IllegalArgumentException("at least one LMSKeyGenParameterSpec required");
        }
        this.specs = (LMSKeyGenParameterSpec[]) lMSKeyGenParameterSpecArr.clone();
    }

    public LMSKeyGenParameterSpec[] getLMSSpecs() {
        return (LMSKeyGenParameterSpec[]) this.specs.clone();
    }
}
