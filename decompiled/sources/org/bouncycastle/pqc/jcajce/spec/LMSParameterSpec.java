package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.pqc.crypto.lms.LMOtsParameters;
import org.bouncycastle.pqc.crypto.lms.LMSigParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/spec/LMSParameterSpec.class */
public class LMSParameterSpec implements AlgorithmParameterSpec {
    private final LMSigParameters lmSigParams;
    private final LMOtsParameters lmOtsParameters;

    public LMSParameterSpec(LMSigParameters lMSigParameters, LMOtsParameters lMOtsParameters) {
        this.lmSigParams = lMSigParameters;
        this.lmOtsParameters = lMOtsParameters;
    }

    public LMSigParameters getSigParams() {
        return this.lmSigParams;
    }

    public LMOtsParameters getOtsParams() {
        return this.lmOtsParameters;
    }
}
