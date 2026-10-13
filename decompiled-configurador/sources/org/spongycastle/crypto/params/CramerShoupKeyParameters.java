package org.spongycastle.crypto.params;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/params/CramerShoupKeyParameters.class */
public class CramerShoupKeyParameters extends AsymmetricKeyParameter {
    private CramerShoupParameters params;

    protected CramerShoupKeyParameters(boolean isPrivate, CramerShoupParameters params) {
        super(isPrivate);
        this.params = params;
    }

    public CramerShoupParameters getParameters() {
        return this.params;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CramerShoupKeyParameters)) {
            return false;
        }
        CramerShoupKeyParameters csKey = (CramerShoupKeyParameters) obj;
        if (this.params == null) {
            return csKey.getParameters() == null;
        }
        return this.params.equals(csKey.getParameters());
    }

    public int hashCode() {
        int code = isPrivate() ? 0 : 1;
        if (this.params != null) {
            code ^= this.params.hashCode();
        }
        return code;
    }
}
