package org.bouncycastle.pqc.crypto.mldsa;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mldsa/MLDSAKeyParameters.class */
public class MLDSAKeyParameters extends AsymmetricKeyParameter {
    private final MLDSAParameters params;

    public MLDSAKeyParameters(boolean z, MLDSAParameters mLDSAParameters) {
        super(z);
        this.params = mLDSAParameters;
    }

    public MLDSAParameters getParameters() {
        return this.params;
    }
}
