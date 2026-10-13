package org.spongycastle.pqc.crypto.gmss;

import org.spongycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/gmss/GMSSKeyParameters.class */
public class GMSSKeyParameters extends AsymmetricKeyParameter {
    private GMSSParameters params;

    public GMSSKeyParameters(boolean isPrivate, GMSSParameters params) {
        super(isPrivate);
        this.params = params;
    }

    public GMSSParameters getParameters() {
        return this.params;
    }
}
