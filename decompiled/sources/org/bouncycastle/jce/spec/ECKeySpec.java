package org.bouncycastle.jce.spec;

import java.security.spec.KeySpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/spec/ECKeySpec.class */
public class ECKeySpec implements KeySpec {
    private ECParameterSpec spec;

    protected ECKeySpec(ECParameterSpec eCParameterSpec) {
        this.spec = eCParameterSpec;
    }

    public ECParameterSpec getParams() {
        return this.spec;
    }
}
