package org.spongycastle.crypto.params;

import java.math.BigInteger;
import java.util.Vector;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/params/NaccacheSternPrivateKeyParameters.class */
public class NaccacheSternPrivateKeyParameters extends NaccacheSternKeyParameters {
    private BigInteger phi_n;
    private Vector smallPrimes;

    public NaccacheSternPrivateKeyParameters(BigInteger g, BigInteger n, int lowerSigmaBound, Vector smallPrimes, BigInteger phi_n) {
        super(true, g, n, lowerSigmaBound);
        this.smallPrimes = smallPrimes;
        this.phi_n = phi_n;
    }

    public BigInteger getPhi_n() {
        return this.phi_n;
    }

    public Vector getSmallPrimes() {
        return this.smallPrimes;
    }
}
