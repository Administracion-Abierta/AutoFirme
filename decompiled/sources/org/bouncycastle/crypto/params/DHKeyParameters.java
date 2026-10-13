package org.bouncycastle.crypto.params;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/DHKeyParameters.class */
public class DHKeyParameters extends AsymmetricKeyParameter {
    private DHParameters params;

    protected DHKeyParameters(boolean z, DHParameters dHParameters) {
        super(z);
        this.params = dHParameters;
    }

    public DHParameters getParameters() {
        return this.params;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DHKeyParameters)) {
            return false;
        }
        DHKeyParameters dHKeyParameters = (DHKeyParameters) obj;
        if (this.params == null) {
            return dHKeyParameters.getParameters() == null;
        }
        return this.params.equals(dHKeyParameters.getParameters());
    }

    public int hashCode() {
        int iHashCode = isPrivate() ? 0 : 1;
        if (this.params != null) {
            iHashCode ^= this.params.hashCode();
        }
        return iHashCode;
    }
}
