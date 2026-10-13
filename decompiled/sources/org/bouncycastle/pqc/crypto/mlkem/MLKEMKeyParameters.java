package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mlkem/MLKEMKeyParameters.class */
public class MLKEMKeyParameters extends AsymmetricKeyParameter {
    private MLKEMParameters params;

    public MLKEMKeyParameters(boolean z, MLKEMParameters mLKEMParameters) {
        super(z);
        this.params = mLKEMParameters;
    }

    public MLKEMParameters getParameters() {
        return this.params;
    }
}
