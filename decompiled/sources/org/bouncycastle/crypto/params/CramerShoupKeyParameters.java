package org.bouncycastle.crypto.params;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/CramerShoupKeyParameters.class */
public class CramerShoupKeyParameters extends AsymmetricKeyParameter {
    private CramerShoupParameters params;

    protected CramerShoupKeyParameters(boolean z, CramerShoupParameters cramerShoupParameters) {
        super(z);
        this.params = cramerShoupParameters;
    }

    public CramerShoupParameters getParameters() {
        return this.params;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CramerShoupKeyParameters)) {
            return false;
        }
        CramerShoupKeyParameters cramerShoupKeyParameters = (CramerShoupKeyParameters) obj;
        if (this.params == null) {
            return cramerShoupKeyParameters.getParameters() == null;
        }
        return this.params.equals(cramerShoupKeyParameters.getParameters());
    }

    public int hashCode() {
        int iHashCode = isPrivate() ? 0 : 1;
        if (this.params != null) {
            iHashCode ^= this.params.hashCode();
        }
        return iHashCode;
    }
}
