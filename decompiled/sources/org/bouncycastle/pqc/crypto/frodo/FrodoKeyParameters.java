package org.bouncycastle.pqc.crypto.frodo;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/frodo/FrodoKeyParameters.class */
public class FrodoKeyParameters extends AsymmetricKeyParameter {
    private FrodoParameters params;

    public FrodoKeyParameters(boolean z, FrodoParameters frodoParameters) {
        super(z);
        this.params = frodoParameters;
    }

    public FrodoParameters getParameters() {
        return this.params;
    }
}
