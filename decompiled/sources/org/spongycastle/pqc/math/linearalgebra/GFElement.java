package org.spongycastle.pqc.math.linearalgebra;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/linearalgebra/GFElement.class */
public interface GFElement {
    Object clone();

    boolean equals(Object obj);

    int hashCode();

    boolean isZero();

    boolean isOne();

    GFElement add(GFElement gFElement) throws RuntimeException;

    void addToThis(GFElement gFElement) throws RuntimeException;

    GFElement subtract(GFElement gFElement) throws RuntimeException;

    void subtractFromThis(GFElement gFElement);

    GFElement multiply(GFElement gFElement) throws RuntimeException;

    void multiplyThisBy(GFElement gFElement) throws RuntimeException;

    GFElement invert() throws ArithmeticException;

    BigInteger toFlexiBigInt();

    byte[] toByteArray();

    String toString();

    String toString(int i);
}
