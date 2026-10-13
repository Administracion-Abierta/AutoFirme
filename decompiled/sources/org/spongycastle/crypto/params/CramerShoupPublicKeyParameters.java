package org.spongycastle.crypto.params;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/CramerShoupPublicKeyParameters.class */
public class CramerShoupPublicKeyParameters extends CramerShoupKeyParameters {
    private BigInteger c;
    private BigInteger d;
    private BigInteger h;

    public CramerShoupPublicKeyParameters(CramerShoupParameters params, BigInteger c, BigInteger d, BigInteger h) {
        super(false, params);
        this.c = c;
        this.d = d;
        this.h = h;
    }

    public BigInteger getC() {
        return this.c;
    }

    public BigInteger getD() {
        return this.d;
    }

    public BigInteger getH() {
        return this.h;
    }

    @Override // org.spongycastle.crypto.params.CramerShoupKeyParameters
    public int hashCode() {
        return ((this.c.hashCode() ^ this.d.hashCode()) ^ this.h.hashCode()) ^ super.hashCode();
    }

    @Override // org.spongycastle.crypto.params.CramerShoupKeyParameters
    public boolean equals(Object obj) {
        if (!(obj instanceof CramerShoupPublicKeyParameters)) {
            return false;
        }
        CramerShoupPublicKeyParameters other = (CramerShoupPublicKeyParameters) obj;
        return other.getC().equals(this.c) && other.getD().equals(this.d) && other.getH().equals(this.h) && super.equals(obj);
    }
}
