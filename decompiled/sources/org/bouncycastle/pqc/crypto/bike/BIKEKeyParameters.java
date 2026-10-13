package org.bouncycastle.pqc.crypto.bike;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/bike/BIKEKeyParameters.class */
public class BIKEKeyParameters extends AsymmetricKeyParameter {
    private BIKEParameters params;

    public BIKEKeyParameters(boolean z, BIKEParameters bIKEParameters) {
        super(z);
        this.params = bIKEParameters;
    }

    public BIKEParameters getParameters() {
        return this.params;
    }
}
