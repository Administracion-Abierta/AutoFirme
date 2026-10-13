package org.spongycastle.crypto.params;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/GOST3410KeyParameters.class */
public class GOST3410KeyParameters extends AsymmetricKeyParameter {
    private GOST3410Parameters params;

    public GOST3410KeyParameters(boolean isPrivate, GOST3410Parameters params) {
        super(isPrivate);
        this.params = params;
    }

    public GOST3410Parameters getParameters() {
        return this.params;
    }
}
