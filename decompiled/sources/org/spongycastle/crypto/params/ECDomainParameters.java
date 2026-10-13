package org.spongycastle.crypto.params;

import java.math.BigInteger;
import org.spongycastle.math.ec.ECConstants;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/ECDomainParameters.class */
public class ECDomainParameters implements ECConstants {
    private ECCurve curve;
    private byte[] seed;
    private ECPoint G;
    private BigInteger n;
    private BigInteger h;

    public ECDomainParameters(ECCurve curve, ECPoint G, BigInteger n) {
        this(curve, G, n, ONE, null);
    }

    public ECDomainParameters(ECCurve curve, ECPoint G, BigInteger n, BigInteger h) {
        this(curve, G, n, h, null);
    }

    public ECDomainParameters(ECCurve curve, ECPoint G, BigInteger n, BigInteger h, byte[] seed) {
        this.curve = curve;
        this.G = G.normalize();
        this.n = n;
        this.h = h;
        this.seed = seed;
    }

    public ECCurve getCurve() {
        return this.curve;
    }

    public ECPoint getG() {
        return this.G;
    }

    public BigInteger getN() {
        return this.n;
    }

    public BigInteger getH() {
        return this.h;
    }

    public byte[] getSeed() {
        return Arrays.clone(this.seed);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ECDomainParameters) {
            ECDomainParameters other = (ECDomainParameters) obj;
            return this.curve.equals(other.curve) && this.G.equals(other.G) && this.n.equals(other.n) && this.h.equals(other.h);
        }
        return false;
    }

    public int hashCode() {
        int hc = this.curve.hashCode();
        return (((((hc * 37) ^ this.G.hashCode()) * 37) ^ this.n.hashCode()) * 37) ^ this.h.hashCode();
    }
}
