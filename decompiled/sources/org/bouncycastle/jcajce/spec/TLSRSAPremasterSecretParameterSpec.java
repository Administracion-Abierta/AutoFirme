package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/TLSRSAPremasterSecretParameterSpec.class */
public class TLSRSAPremasterSecretParameterSpec implements AlgorithmParameterSpec {
    private final int protocolVersion;

    public TLSRSAPremasterSecretParameterSpec(int i) {
        this.protocolVersion = i;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }
}
