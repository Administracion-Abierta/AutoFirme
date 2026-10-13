package org.spongycastle.crypto.params;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/ECKeyParameters.class */
public class ECKeyParameters extends AsymmetricKeyParameter {
    ECDomainParameters params;

    protected ECKeyParameters(boolean isPrivate, ECDomainParameters params) {
        super(isPrivate);
        this.params = params;
    }

    public ECDomainParameters getParameters() {
        return this.params;
    }
}
