package org.spongycastle.math.field;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/field/PrimeField.class */
class PrimeField implements FiniteField {
    protected final BigInteger characteristic;

    PrimeField(BigInteger characteristic) {
        this.characteristic = characteristic;
    }

    @Override // org.spongycastle.math.field.FiniteField
    public BigInteger getCharacteristic() {
        return this.characteristic;
    }

    @Override // org.spongycastle.math.field.FiniteField
    public int getDimension() {
        return 1;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PrimeField)) {
            return false;
        }
        PrimeField other = (PrimeField) obj;
        return this.characteristic.equals(other.characteristic);
    }

    public int hashCode() {
        return this.characteristic.hashCode();
    }
}
