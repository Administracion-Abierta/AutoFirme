package org.bouncycastle.pqc.crypto.lms;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/LMSParameters.class */
public class LMSParameters {
    private final LMSigParameters lmSigParam;
    private final LMOtsParameters lmOTSParam;

    public LMSParameters(LMSigParameters lMSigParameters, LMOtsParameters lMOtsParameters) {
        this.lmSigParam = lMSigParameters;
        this.lmOTSParam = lMOtsParameters;
    }

    public LMSigParameters getLMSigParam() {
        return this.lmSigParam;
    }

    public LMOtsParameters getLMOTSParam() {
        return this.lmOTSParam;
    }
}
