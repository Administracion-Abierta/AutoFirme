package org.spongycastle.math.field;

import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/field/GF2Polynomial.class */
class GF2Polynomial implements Polynomial {
    protected final int[] exponents;

    GF2Polynomial(int[] exponents) {
        this.exponents = Arrays.clone(exponents);
    }

    @Override // org.spongycastle.math.field.Polynomial
    public int getDegree() {
        return this.exponents[this.exponents.length - 1];
    }

    @Override // org.spongycastle.math.field.Polynomial
    public int[] getExponentsPresent() {
        return Arrays.clone(this.exponents);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GF2Polynomial)) {
            return false;
        }
        GF2Polynomial other = (GF2Polynomial) obj;
        return Arrays.areEqual(this.exponents, other.exponents);
    }

    public int hashCode() {
        return Arrays.hashCode(this.exponents);
    }
}
