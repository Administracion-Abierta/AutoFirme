package org.spongycastle.crypto.params;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/params/DHKeyParameters.class */
public class DHKeyParameters extends AsymmetricKeyParameter {
    private DHParameters params;

    protected DHKeyParameters(boolean isPrivate, DHParameters params) {
        super(isPrivate);
        this.params = params;
    }

    public DHParameters getParameters() {
        return this.params;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DHKeyParameters)) {
            return false;
        }
        DHKeyParameters dhKey = (DHKeyParameters) obj;
        if (this.params == null) {
            return dhKey.getParameters() == null;
        }
        return this.params.equals(dhKey.getParameters());
    }

    public int hashCode() {
        int code = isPrivate() ? 0 : 1;
        if (this.params != null) {
            code ^= this.params.hashCode();
        }
        return code;
    }
}
