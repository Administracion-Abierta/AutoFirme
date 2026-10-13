package org.bouncycastle.math.field;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/field/FiniteField.class */
public interface FiniteField {
    BigInteger getCharacteristic();

    int getDimension();
}
