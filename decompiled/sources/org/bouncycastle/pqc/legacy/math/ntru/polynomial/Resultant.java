package org.bouncycastle.pqc.legacy.math.ntru.polynomial;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/ntru/polynomial/Resultant.class */
public class Resultant {
    public BigIntPolynomial rho;
    public BigInteger res;

    Resultant(BigIntPolynomial bigIntPolynomial, BigInteger bigInteger) {
        this.rho = bigIntPolynomial;
        this.res = bigInteger;
    }
}
