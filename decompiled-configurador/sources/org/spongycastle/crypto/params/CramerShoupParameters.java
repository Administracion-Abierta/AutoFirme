package org.spongycastle.crypto.params;

import java.math.BigInteger;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/params/CramerShoupParameters.class */
public class CramerShoupParameters implements CipherParameters {
    private BigInteger p;
    private BigInteger g1;
    private BigInteger g2;
    private Digest H;

    public CramerShoupParameters(BigInteger p, BigInteger g1, BigInteger g2, Digest H) {
        this.p = p;
        this.g1 = g1;
        this.g2 = g2;
        this.H = H;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DSAParameters)) {
            return false;
        }
        CramerShoupParameters pm = (CramerShoupParameters) obj;
        return pm.getP().equals(this.p) && pm.getG1().equals(this.g1) && pm.getG2().equals(this.g2);
    }

    public int hashCode() {
        return (getP().hashCode() ^ getG1().hashCode()) ^ getG2().hashCode();
    }

    public BigInteger getG1() {
        return this.g1;
    }

    public BigInteger getG2() {
        return this.g2;
    }

    public BigInteger getP() {
        return this.p;
    }

    public Digest getH() {
        this.H.reset();
        return this.H;
    }
}
