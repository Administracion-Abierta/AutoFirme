package org.spongycastle.crypto.params;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/DHPublicKeyParameters.class */
public class DHPublicKeyParameters extends DHKeyParameters {
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private BigInteger y;

    public DHPublicKeyParameters(BigInteger y, DHParameters params) {
        super(false, params);
        this.y = validate(y, params);
    }

    private BigInteger validate(BigInteger y, DHParameters dhParams) {
        if (y == null) {
            throw new NullPointerException("y value cannot be null");
        }
        if (y.compareTo(TWO) < 0 || y.compareTo(dhParams.getP().subtract(TWO)) > 0) {
            throw new IllegalArgumentException("invalid DH public key");
        }
        if (dhParams.getQ() != null) {
            if (ONE.equals(y.modPow(dhParams.getQ(), dhParams.getP()))) {
                return y;
            }
            throw new IllegalArgumentException("Y value does not appear to be in correct group");
        }
        return y;
    }

    public BigInteger getY() {
        return this.y;
    }

    @Override // org.spongycastle.crypto.params.DHKeyParameters
    public int hashCode() {
        return this.y.hashCode() ^ super.hashCode();
    }

    @Override // org.spongycastle.crypto.params.DHKeyParameters
    public boolean equals(Object obj) {
        if (!(obj instanceof DHPublicKeyParameters)) {
            return false;
        }
        DHPublicKeyParameters other = (DHPublicKeyParameters) obj;
        return other.getY().equals(this.y) && super.equals(obj);
    }
}
