package org.spongycastle.pqc.math.ntru.polynomial;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/math/ntru/polynomial/TernaryPolynomial.class */
public interface TernaryPolynomial extends Polynomial {
    @Override // org.spongycastle.pqc.math.ntru.polynomial.Polynomial
    IntegerPolynomial mult(IntegerPolynomial integerPolynomial);

    int[] getOnes();

    int[] getNegOnes();

    int size();

    void clear();
}
