package org.spongycastle.math.field;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/field/FiniteField.class */
public interface FiniteField {
    BigInteger getCharacteristic();

    int getDimension();
}
