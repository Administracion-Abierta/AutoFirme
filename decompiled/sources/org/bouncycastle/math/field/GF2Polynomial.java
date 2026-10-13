package org.bouncycastle.math.field;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/field/GF2Polynomial.class */
class GF2Polynomial implements Polynomial {
    protected final int[] exponents;

    GF2Polynomial(int[] iArr) {
        this.exponents = Arrays.clone(iArr);
    }

    @Override // org.bouncycastle.math.field.Polynomial
    public int getDegree() {
        return this.exponents[this.exponents.length - 1];
    }

    @Override // org.bouncycastle.math.field.Polynomial
    public int[] getExponentsPresent() {
        return Arrays.clone(this.exponents);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GF2Polynomial) {
            return Arrays.areEqual(this.exponents, ((GF2Polynomial) obj).exponents);
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(this.exponents);
    }
}
