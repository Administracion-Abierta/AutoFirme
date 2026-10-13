package org.spongycastle.jce.spec;

import java.security.spec.AlgorithmParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/spec/ECNamedCurveGenParameterSpec.class */
public class ECNamedCurveGenParameterSpec implements AlgorithmParameterSpec {
    private String name;

    public ECNamedCurveGenParameterSpec(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
