package org.bouncycastle.jce.spec;

import java.security.spec.AlgorithmParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/spec/ECNamedCurveGenParameterSpec.class */
public class ECNamedCurveGenParameterSpec implements AlgorithmParameterSpec {
    private String name;

    public ECNamedCurveGenParameterSpec(String str) {
        this.name = str;
    }

    public String getName() {
        return this.name;
    }
}
