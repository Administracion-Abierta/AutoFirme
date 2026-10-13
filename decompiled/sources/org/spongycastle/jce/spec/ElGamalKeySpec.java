package org.spongycastle.jce.spec;

import java.security.spec.KeySpec;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/spec/ElGamalKeySpec.class */
public class ElGamalKeySpec implements KeySpec {
    private ElGamalParameterSpec spec;

    public ElGamalKeySpec(ElGamalParameterSpec spec) {
        this.spec = spec;
    }

    public ElGamalParameterSpec getParams() {
        return this.spec;
    }
}
