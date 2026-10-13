package org.spongycastle.pqc.math.ntru.polynomial;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/ntru/polynomial/Resultant.class */
public class Resultant {
    public BigIntPolynomial rho;
    public BigInteger res;

    Resultant(BigIntPolynomial rho, BigInteger res) {
        this.rho = rho;
        this.res = res;
    }
}
