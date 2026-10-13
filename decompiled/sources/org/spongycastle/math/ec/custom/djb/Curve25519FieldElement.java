package org.spongycastle.math.ec.custom.djb;

import java.math.BigInteger;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.raw.Mod;
import org.spongycastle.math.raw.Nat256;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/custom/djb/Curve25519FieldElement.class */
public class Curve25519FieldElement extends ECFieldElement {
    public static final BigInteger Q = Curve25519.q;
    private static final int[] PRECOMP_POW2 = {1242472624, -991028441, -1389370248, 792926214, 1039914919, 726466713, 1338105611, 730014848};
    protected int[] x;

    public Curve25519FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.compareTo(Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for Curve25519FieldElement");
        }
        this.x = Curve25519Field.fromBigInteger(x);
    }

    public Curve25519FieldElement() {
        this.x = Nat256.create();
    }

    protected Curve25519FieldElement(int[] x) {
        this.x = x;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean isZero() {
        return Nat256.isZero(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean isOne() {
        return Nat256.isOne(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean testBitZero() {
        return Nat256.getBit(this.x, 0) == 1;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public BigInteger toBigInteger() {
        return Nat256.toBigInteger(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public String getFieldName() {
        return "Curve25519Field";
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public int getFieldSize() {
        return Q.bitLength();
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement add(ECFieldElement b) {
        int[] z = Nat256.create();
        Curve25519Field.add(this.x, ((Curve25519FieldElement) b).x, z);
        return new Curve25519FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement addOne() {
        int[] z = Nat256.create();
        Curve25519Field.addOne(this.x, z);
        return new Curve25519FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement subtract(ECFieldElement b) {
        int[] z = Nat256.create();
        Curve25519Field.subtract(this.x, ((Curve25519FieldElement) b).x, z);
        return new Curve25519FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement multiply(ECFieldElement b) {
        int[] z = Nat256.create();
        Curve25519Field.multiply(this.x, ((Curve25519FieldElement) b).x, z);
        return new Curve25519FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement divide(ECFieldElement b) {
        int[] z = Nat256.create();
        Mod.invert(Curve25519Field.P, ((Curve25519FieldElement) b).x, z);
        Curve25519Field.multiply(z, this.x, z);
        return new Curve25519FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement negate() {
        int[] z = Nat256.create();
        Curve25519Field.negate(this.x, z);
        return new Curve25519FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement square() {
        int[] z = Nat256.create();
        Curve25519Field.square(this.x, z);
        return new Curve25519FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement invert() {
        int[] z = Nat256.create();
        Mod.invert(Curve25519Field.P, this.x, z);
        return new Curve25519FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement sqrt() {
        int[] x1 = this.x;
        if (Nat256.isZero(x1) || Nat256.isOne(x1)) {
            return this;
        }
        int[] x2 = Nat256.create();
        Curve25519Field.square(x1, x2);
        Curve25519Field.multiply(x2, x1, x2);
        Curve25519Field.square(x2, x2);
        Curve25519Field.multiply(x2, x1, x2);
        int[] x4 = Nat256.create();
        Curve25519Field.square(x2, x4);
        Curve25519Field.multiply(x4, x1, x4);
        int[] x7 = Nat256.create();
        Curve25519Field.squareN(x4, 3, x7);
        Curve25519Field.multiply(x7, x2, x7);
        Curve25519Field.squareN(x7, 4, x2);
        Curve25519Field.multiply(x2, x4, x2);
        Curve25519Field.squareN(x2, 4, x7);
        Curve25519Field.multiply(x7, x4, x7);
        Curve25519Field.squareN(x7, 15, x4);
        Curve25519Field.multiply(x4, x7, x4);
        Curve25519Field.squareN(x4, 30, x7);
        Curve25519Field.multiply(x7, x4, x7);
        Curve25519Field.squareN(x7, 60, x4);
        Curve25519Field.multiply(x4, x7, x4);
        Curve25519Field.squareN(x4, 11, x7);
        Curve25519Field.multiply(x7, x2, x7);
        Curve25519Field.squareN(x7, 120, x2);
        Curve25519Field.multiply(x2, x4, x2);
        Curve25519Field.square(x2, x2);
        Curve25519Field.square(x2, x4);
        if (Nat256.eq(x1, x4)) {
            return new Curve25519FieldElement(x2);
        }
        Curve25519Field.multiply(x2, PRECOMP_POW2, x2);
        Curve25519Field.square(x2, x4);
        if (Nat256.eq(x1, x4)) {
            return new Curve25519FieldElement(x2);
        }
        return null;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Curve25519FieldElement)) {
            return false;
        }
        Curve25519FieldElement o = (Curve25519FieldElement) other;
        return Nat256.eq(this.x, o.x);
    }

    public int hashCode() {
        return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 8);
    }
}
