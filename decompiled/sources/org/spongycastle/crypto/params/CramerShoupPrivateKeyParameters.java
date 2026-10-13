package org.spongycastle.crypto.params;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/CramerShoupPrivateKeyParameters.class */
public class CramerShoupPrivateKeyParameters extends CramerShoupKeyParameters {
    private BigInteger x1;
    private BigInteger x2;
    private BigInteger y1;
    private BigInteger y2;
    private BigInteger z;
    private CramerShoupPublicKeyParameters pk;

    public CramerShoupPrivateKeyParameters(CramerShoupParameters params, BigInteger x1, BigInteger x2, BigInteger y1, BigInteger y2, BigInteger z) {
        super(true, params);
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.z = z;
    }

    public BigInteger getX1() {
        return this.x1;
    }

    public BigInteger getX2() {
        return this.x2;
    }

    public BigInteger getY1() {
        return this.y1;
    }

    public BigInteger getY2() {
        return this.y2;
    }

    public BigInteger getZ() {
        return this.z;
    }

    public void setPk(CramerShoupPublicKeyParameters pk) {
        this.pk = pk;
    }

    public CramerShoupPublicKeyParameters getPk() {
        return this.pk;
    }

    @Override // org.spongycastle.crypto.params.CramerShoupKeyParameters
    public int hashCode() {
        return ((((this.x1.hashCode() ^ this.x2.hashCode()) ^ this.y1.hashCode()) ^ this.y2.hashCode()) ^ this.z.hashCode()) ^ super.hashCode();
    }

    @Override // org.spongycastle.crypto.params.CramerShoupKeyParameters
    public boolean equals(Object obj) {
        if (!(obj instanceof CramerShoupPrivateKeyParameters)) {
            return false;
        }
        CramerShoupPrivateKeyParameters other = (CramerShoupPrivateKeyParameters) obj;
        return other.getX1().equals(this.x1) && other.getX2().equals(this.x2) && other.getY1().equals(this.y1) && other.getY2().equals(this.y2) && other.getZ().equals(this.z) && super.equals(obj);
    }
}
