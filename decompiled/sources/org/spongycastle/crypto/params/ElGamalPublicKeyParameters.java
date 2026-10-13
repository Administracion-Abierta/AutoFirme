package org.spongycastle.crypto.params;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/ElGamalPublicKeyParameters.class */
public class ElGamalPublicKeyParameters extends ElGamalKeyParameters {
    private BigInteger y;

    public ElGamalPublicKeyParameters(BigInteger y, ElGamalParameters params) {
        super(false, params);
        this.y = y;
    }

    public BigInteger getY() {
        return this.y;
    }

    @Override // org.spongycastle.crypto.params.ElGamalKeyParameters
    public int hashCode() {
        return this.y.hashCode() ^ super.hashCode();
    }

    @Override // org.spongycastle.crypto.params.ElGamalKeyParameters
    public boolean equals(Object obj) {
        if (!(obj instanceof ElGamalPublicKeyParameters)) {
            return false;
        }
        ElGamalPublicKeyParameters other = (ElGamalPublicKeyParameters) obj;
        return other.getY().equals(this.y) && super.equals(obj);
    }
}
