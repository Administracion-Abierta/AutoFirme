package org.spongycastle.crypto.params;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/params/ElGamalKeyParameters.class */
public class ElGamalKeyParameters extends AsymmetricKeyParameter {
    private ElGamalParameters params;

    protected ElGamalKeyParameters(boolean isPrivate, ElGamalParameters params) {
        super(isPrivate);
        this.params = params;
    }

    public ElGamalParameters getParameters() {
        return this.params;
    }

    public int hashCode() {
        if (this.params != null) {
            return this.params.hashCode();
        }
        return 0;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ElGamalKeyParameters)) {
            return false;
        }
        ElGamalKeyParameters dhKey = (ElGamalKeyParameters) obj;
        if (this.params == null) {
            return dhKey.getParameters() == null;
        }
        return this.params.equals(dhKey.getParameters());
    }
}
