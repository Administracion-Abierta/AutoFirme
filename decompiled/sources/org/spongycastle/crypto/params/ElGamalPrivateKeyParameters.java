package org.spongycastle.crypto.params;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/ElGamalPrivateKeyParameters.class */
public class ElGamalPrivateKeyParameters extends ElGamalKeyParameters {
    private BigInteger x;

    public ElGamalPrivateKeyParameters(BigInteger x, ElGamalParameters params) {
        super(true, params);
        this.x = x;
    }

    public BigInteger getX() {
        return this.x;
    }

    @Override // org.spongycastle.crypto.params.ElGamalKeyParameters
    public boolean equals(Object obj) {
        if (!(obj instanceof ElGamalPrivateKeyParameters)) {
            return false;
        }
        ElGamalPrivateKeyParameters pKey = (ElGamalPrivateKeyParameters) obj;
        if (!pKey.getX().equals(this.x)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override // org.spongycastle.crypto.params.ElGamalKeyParameters
    public int hashCode() {
        return getX().hashCode();
    }
}
