package org.spongycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.raw.Mod;
import org.spongycastle.math.raw.Nat160;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/custom/sec/SecP160R1FieldElement.class */
public class SecP160R1FieldElement extends ECFieldElement {
    public static final BigInteger Q = SecP160R1Curve.q;
    protected int[] x;

    public SecP160R1FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.compareTo(Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP160R1FieldElement");
        }
        this.x = SecP160R1Field.fromBigInteger(x);
    }

    public SecP160R1FieldElement() {
        this.x = Nat160.create();
    }

    protected SecP160R1FieldElement(int[] x) {
        this.x = x;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean isZero() {
        return Nat160.isZero(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean isOne() {
        return Nat160.isOne(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean testBitZero() {
        return Nat160.getBit(this.x, 0) == 1;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public BigInteger toBigInteger() {
        return Nat160.toBigInteger(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public String getFieldName() {
        return "SecP160R1Field";
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public int getFieldSize() {
        return Q.bitLength();
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement add(ECFieldElement b) {
        int[] z = Nat160.create();
        SecP160R1Field.add(this.x, ((SecP160R1FieldElement) b).x, z);
        return new SecP160R1FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement addOne() {
        int[] z = Nat160.create();
        SecP160R1Field.addOne(this.x, z);
        return new SecP160R1FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement subtract(ECFieldElement b) {
        int[] z = Nat160.create();
        SecP160R1Field.subtract(this.x, ((SecP160R1FieldElement) b).x, z);
        return new SecP160R1FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement multiply(ECFieldElement b) {
        int[] z = Nat160.create();
        SecP160R1Field.multiply(this.x, ((SecP160R1FieldElement) b).x, z);
        return new SecP160R1FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement divide(ECFieldElement b) {
        int[] z = Nat160.create();
        Mod.invert(SecP160R1Field.P, ((SecP160R1FieldElement) b).x, z);
        SecP160R1Field.multiply(z, this.x, z);
        return new SecP160R1FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement negate() {
        int[] z = Nat160.create();
        SecP160R1Field.negate(this.x, z);
        return new SecP160R1FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement square() {
        int[] z = Nat160.create();
        SecP160R1Field.square(this.x, z);
        return new SecP160R1FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement invert() {
        int[] z = Nat160.create();
        Mod.invert(SecP160R1Field.P, this.x, z);
        return new SecP160R1FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement sqrt() {
        int[] x1 = this.x;
        if (Nat160.isZero(x1) || Nat160.isOne(x1)) {
            return this;
        }
        int[] x2 = Nat160.create();
        SecP160R1Field.square(x1, x2);
        SecP160R1Field.multiply(x2, x1, x2);
        int[] x4 = Nat160.create();
        SecP160R1Field.squareN(x2, 2, x4);
        SecP160R1Field.multiply(x4, x2, x4);
        SecP160R1Field.squareN(x4, 4, x2);
        SecP160R1Field.multiply(x2, x4, x2);
        SecP160R1Field.squareN(x2, 8, x4);
        SecP160R1Field.multiply(x4, x2, x4);
        SecP160R1Field.squareN(x4, 16, x2);
        SecP160R1Field.multiply(x2, x4, x2);
        SecP160R1Field.squareN(x2, 32, x4);
        SecP160R1Field.multiply(x4, x2, x4);
        SecP160R1Field.squareN(x4, 64, x2);
        SecP160R1Field.multiply(x2, x4, x2);
        SecP160R1Field.square(x2, x4);
        SecP160R1Field.multiply(x4, x1, x4);
        SecP160R1Field.squareN(x4, 29, x4);
        SecP160R1Field.square(x4, x2);
        if (Nat160.eq(x1, x2)) {
            return new SecP160R1FieldElement(x4);
        }
        return null;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecP160R1FieldElement)) {
            return false;
        }
        SecP160R1FieldElement o = (SecP160R1FieldElement) other;
        return Nat160.eq(this.x, o.x);
    }

    public int hashCode() {
        return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 5);
    }
}
