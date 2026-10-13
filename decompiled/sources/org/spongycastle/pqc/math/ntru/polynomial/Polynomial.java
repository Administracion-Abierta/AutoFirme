package org.spongycastle.pqc.math.ntru.polynomial;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/ntru/polynomial/Polynomial.class */
public interface Polynomial {
    IntegerPolynomial mult(IntegerPolynomial integerPolynomial);

    IntegerPolynomial mult(IntegerPolynomial integerPolynomial, int i);

    IntegerPolynomial toIntegerPolynomial();

    BigIntPolynomial mult(BigIntPolynomial bigIntPolynomial);
}
