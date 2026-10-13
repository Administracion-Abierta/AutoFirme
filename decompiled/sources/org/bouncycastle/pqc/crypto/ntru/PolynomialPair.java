package org.bouncycastle.pqc.crypto.ntru;

import org.bouncycastle.pqc.math.ntru.Polynomial;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntru/PolynomialPair.class */
class PolynomialPair {
    private final Polynomial a;
    private final Polynomial b;

    public PolynomialPair(Polynomial polynomial, Polynomial polynomial2) {
        this.a = polynomial;
        this.b = polynomial2;
    }

    public Polynomial f() {
        return this.a;
    }

    public Polynomial g() {
        return this.b;
    }

    public Polynomial r() {
        return this.a;
    }

    public Polynomial m() {
        return this.b;
    }
}
