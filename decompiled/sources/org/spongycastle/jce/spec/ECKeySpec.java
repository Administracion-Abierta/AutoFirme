package org.spongycastle.jce.spec;

import java.security.spec.KeySpec;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/spec/ECKeySpec.class */
public class ECKeySpec implements KeySpec {
    private ECParameterSpec spec;

    protected ECKeySpec(ECParameterSpec spec) {
        this.spec = spec;
    }

    public ECParameterSpec getParams() {
        return this.spec;
    }
}
