package org.spongycastle.jce.spec;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/spec/GOST3410PublicKeyParameterSetSpec.class */
public class GOST3410PublicKeyParameterSetSpec {
    private BigInteger p;
    private BigInteger q;
    private BigInteger a;

    public GOST3410PublicKeyParameterSetSpec(BigInteger p, BigInteger q, BigInteger a) {
        this.p = p;
        this.q = q;
        this.a = a;
    }

    public BigInteger getP() {
        return this.p;
    }

    public BigInteger getQ() {
        return this.q;
    }

    public BigInteger getA() {
        return this.a;
    }

    public boolean equals(Object o) {
        if (o instanceof GOST3410PublicKeyParameterSetSpec) {
            GOST3410PublicKeyParameterSetSpec other = (GOST3410PublicKeyParameterSetSpec) o;
            return this.a.equals(other.a) && this.p.equals(other.p) && this.q.equals(other.q);
        }
        return false;
    }

    public int hashCode() {
        return (this.a.hashCode() ^ this.p.hashCode()) ^ this.q.hashCode();
    }
}
