package org.spongycastle.pqc.math.linearalgebra;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/math/linearalgebra/GF2nPolynomial.class */
public class GF2nPolynomial {
    private GF2nElement[] coeff;
    private int size;

    public GF2nPolynomial(int deg, GF2nElement elem) {
        this.size = deg;
        this.coeff = new GF2nElement[this.size];
        for (int i = 0; i < this.size; i++) {
            this.coeff[i] = (GF2nElement) elem.clone();
        }
    }

    private GF2nPolynomial(int deg) {
        this.size = deg;
        this.coeff = new GF2nElement[this.size];
    }

    public GF2nPolynomial(GF2nPolynomial a) {
        this.coeff = new GF2nElement[a.size];
        this.size = a.size;
        for (int i = 0; i < this.size; i++) {
            this.coeff[i] = (GF2nElement) a.coeff[i].clone();
        }
    }

    public GF2nPolynomial(GF2Polynomial polynomial, GF2nField B1) {
        this.size = B1.getDegree() + 1;
        this.coeff = new GF2nElement[this.size];
        if (B1 instanceof GF2nONBField) {
            for (int i = 0; i < this.size; i++) {
                if (polynomial.testBit(i)) {
                    this.coeff[i] = GF2nONBElement.ONE((GF2nONBField) B1);
                } else {
                    this.coeff[i] = GF2nONBElement.ZERO((GF2nONBField) B1);
                }
            }
            return;
        }
        if (B1 instanceof GF2nPolynomialField) {
            for (int i2 = 0; i2 < this.size; i2++) {
                if (polynomial.testBit(i2)) {
                    this.coeff[i2] = GF2nPolynomialElement.ONE((GF2nPolynomialField) B1);
                } else {
                    this.coeff[i2] = GF2nPolynomialElement.ZERO((GF2nPolynomialField) B1);
                }
            }
            return;
        }
        throw new IllegalArgumentException("PolynomialGF2n(Bitstring, GF2nField): B1 must be an instance of GF2nONBField or GF2nPolynomialField!");
    }

    public final void assignZeroToElements() {
        for (int i = 0; i < this.size; i++) {
            this.coeff[i].assignZero();
        }
    }

    public final int size() {
        return this.size;
    }

    public final int getDegree() {
        for (int i = this.size - 1; i >= 0; i--) {
            if (!this.coeff[i].isZero()) {
                return i;
            }
        }
        return -1;
    }

    public final void enlarge(int k) {
        if (k <= this.size) {
            return;
        }
        GF2nElement[] res = new GF2nElement[k];
        System.arraycopy(this.coeff, 0, res, 0, this.size);
        GF2nField f = this.coeff[0].getField();
        if (this.coeff[0] instanceof GF2nPolynomialElement) {
            for (int i = this.size; i < k; i++) {
                res[i] = GF2nPolynomialElement.ZERO((GF2nPolynomialField) f);
            }
        } else if (this.coeff[0] instanceof GF2nONBElement) {
            for (int i2 = this.size; i2 < k; i2++) {
                res[i2] = GF2nONBElement.ZERO((GF2nONBField) f);
            }
        }
        this.size = k;
        this.coeff = res;
    }

    public final void shrink() {
        int i = this.size - 1;
        while (this.coeff[i].isZero() && i > 0) {
            i--;
        }
        int i2 = i + 1;
        if (i2 < this.size) {
            GF2nElement[] res = new GF2nElement[i2];
            System.arraycopy(this.coeff, 0, res, 0, i2);
            this.coeff = res;
            this.size = i2;
        }
    }

    public final void set(int index, GF2nElement elem) {
        if (!(elem instanceof GF2nPolynomialElement) && !(elem instanceof GF2nONBElement)) {
            throw new IllegalArgumentException("PolynomialGF2n.set f must be an instance of either GF2nPolynomialElement or GF2nONBElement!");
        }
        this.coeff[index] = (GF2nElement) elem.clone();
    }

    public final GF2nElement at(int index) {
        return this.coeff[index];
    }

    public final boolean isZero() {
        for (int i = 0; i < this.size; i++) {
            if (this.coeff[i] != null && !this.coeff[i].isZero()) {
                return false;
            }
        }
        return true;
    }

    public final boolean equals(Object other) {
        if (other == null || !(other instanceof GF2nPolynomial)) {
            return false;
        }
        GF2nPolynomial otherPol = (GF2nPolynomial) other;
        if (getDegree() != otherPol.getDegree()) {
            return false;
        }
        for (int i = 0; i < this.size; i++) {
            if (!this.coeff[i].equals(otherPol.coeff[i])) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return getDegree() + this.coeff.hashCode();
    }

    public final GF2nPolynomial add(GF2nPolynomial b) throws RuntimeException {
        GF2nPolynomial result;
        if (size() >= b.size()) {
            result = new GF2nPolynomial(size());
            int i = 0;
            while (i < b.size()) {
                result.coeff[i] = (GF2nElement) this.coeff[i].add(b.coeff[i]);
                i++;
            }
            while (i < size()) {
                result.coeff[i] = this.coeff[i];
                i++;
            }
        } else {
            result = new GF2nPolynomial(b.size());
            int i2 = 0;
            while (i2 < size()) {
                result.coeff[i2] = (GF2nElement) this.coeff[i2].add(b.coeff[i2]);
                i2++;
            }
            while (i2 < b.size()) {
                result.coeff[i2] = b.coeff[i2];
                i2++;
            }
        }
        return result;
    }

    public final GF2nPolynomial scalarMultiply(GF2nElement s) throws RuntimeException {
        GF2nPolynomial result = new GF2nPolynomial(size());
        for (int i = 0; i < size(); i++) {
            result.coeff[i] = (GF2nElement) this.coeff[i].multiply(s);
        }
        return result;
    }

    public final GF2nPolynomial multiply(GF2nPolynomial b) throws RuntimeException {
        int aDegree = size();
        int bDegree = b.size();
        if (aDegree != bDegree) {
            throw new IllegalArgumentException("PolynomialGF2n.multiply: this and b must have the same size!");
        }
        GF2nPolynomial result = new GF2nPolynomial((aDegree << 1) - 1);
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                if (result.coeff[i + j] == null) {
                    result.coeff[i + j] = (GF2nElement) this.coeff[i].multiply(b.coeff[j]);
                } else {
                    result.coeff[i + j] = (GF2nElement) result.coeff[i + j].add(this.coeff[i].multiply(b.coeff[j]));
                }
            }
        }
        return result;
    }

    public final GF2nPolynomial multiplyAndReduce(GF2nPolynomial b, GF2nPolynomial g) throws RuntimeException {
        return multiply(b).reduce(g);
    }

    public final GF2nPolynomial reduce(GF2nPolynomial g) throws RuntimeException {
        return remainder(g);
    }

    public final void shiftThisLeft(int amount) {
        if (amount > 0) {
            int oldSize = this.size;
            GF2nField f = this.coeff[0].getField();
            enlarge(this.size + amount);
            for (int i = oldSize - 1; i >= 0; i--) {
                this.coeff[i + amount] = this.coeff[i];
            }
            if (this.coeff[0] instanceof GF2nPolynomialElement) {
                for (int i2 = amount - 1; i2 >= 0; i2--) {
                    this.coeff[i2] = GF2nPolynomialElement.ZERO((GF2nPolynomialField) f);
                }
                return;
            }
            if (this.coeff[0] instanceof GF2nONBElement) {
                for (int i3 = amount - 1; i3 >= 0; i3--) {
                    this.coeff[i3] = GF2nONBElement.ZERO((GF2nONBField) f);
                }
            }
        }
    }

    public final GF2nPolynomial shiftLeft(int amount) {
        if (amount <= 0) {
            return new GF2nPolynomial(this);
        }
        GF2nPolynomial result = new GF2nPolynomial(this.size + amount, this.coeff[0]);
        result.assignZeroToElements();
        for (int i = 0; i < this.size; i++) {
            result.coeff[i + amount] = this.coeff[i];
        }
        return result;
    }

    public final GF2nPolynomial[] divide(GF2nPolynomial b) throws RuntimeException {
        GF2nPolynomial[] result = new GF2nPolynomial[2];
        GF2nPolynomial a = new GF2nPolynomial(this);
        a.shrink();
        int bDegree = b.getDegree();
        GF2nElement inv = (GF2nElement) b.coeff[bDegree].invert();
        if (a.getDegree() < bDegree) {
            result[0] = new GF2nPolynomial(this);
            result[0].assignZeroToElements();
            result[0].shrink();
            result[1] = new GF2nPolynomial(this);
            result[1].shrink();
            return result;
        }
        result[0] = new GF2nPolynomial(this);
        result[0].assignZeroToElements();
        int degree = a.getDegree();
        while (true) {
            int i = degree - bDegree;
            if (i >= 0) {
                GF2nElement factor = (GF2nElement) a.coeff[a.getDegree()].multiply(inv);
                GF2nPolynomial shift = b.scalarMultiply(factor);
                shift.shiftThisLeft(i);
                a = a.add(shift);
                a.shrink();
                result[0].coeff[i] = (GF2nElement) factor.clone();
                degree = a.getDegree();
            } else {
                result[1] = a;
                result[0].shrink();
                return result;
            }
        }
    }

    public final GF2nPolynomial remainder(GF2nPolynomial b) throws RuntimeException {
        GF2nPolynomial[] gF2nPolynomialArr = new GF2nPolynomial[2];
        GF2nPolynomial[] result = divide(b);
        return result[1];
    }

    public final GF2nPolynomial quotient(GF2nPolynomial b) throws RuntimeException {
        GF2nPolynomial[] gF2nPolynomialArr = new GF2nPolynomial[2];
        GF2nPolynomial[] result = divide(b);
        return result[0];
    }

    public final GF2nPolynomial gcd(GF2nPolynomial g) throws RuntimeException {
        GF2nPolynomial a = new GF2nPolynomial(this);
        GF2nPolynomial b = new GF2nPolynomial(g);
        a.shrink();
        b.shrink();
        while (!b.isZero()) {
            GF2nPolynomial c = a.remainder(b);
            a = b;
            b = c;
        }
        GF2nElement alpha = a.coeff[a.getDegree()];
        GF2nPolynomial result = a.scalarMultiply((GF2nElement) alpha.invert());
        return result;
    }
}
