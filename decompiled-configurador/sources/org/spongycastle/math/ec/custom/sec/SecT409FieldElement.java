package org.spongycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.raw.Nat;
import org.spongycastle.math.raw.Nat448;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/ec/custom/sec/SecT409FieldElement.class */
public class SecT409FieldElement extends ECFieldElement {
    protected long[] x;

    public SecT409FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.bitLength() > 409) {
            throw new IllegalArgumentException("x value invalid for SecT409FieldElement");
        }
        this.x = SecT409Field.fromBigInteger(x);
    }

    public SecT409FieldElement() {
        this.x = Nat448.create64();
    }

    protected SecT409FieldElement(long[] x) {
        this.x = x;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean isOne() {
        return Nat448.isOne64(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean isZero() {
        return Nat448.isZero64(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean testBitZero() {
        return (this.x[0] & 1) != 0;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public BigInteger toBigInteger() {
        return Nat448.toBigInteger64(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public String getFieldName() {
        return "SecT409Field";
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public int getFieldSize() {
        return 409;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement add(ECFieldElement b) {
        long[] z = Nat448.create64();
        SecT409Field.add(this.x, ((SecT409FieldElement) b).x, z);
        return new SecT409FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement addOne() {
        long[] z = Nat448.create64();
        SecT409Field.addOne(this.x, z);
        return new SecT409FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement subtract(ECFieldElement b) {
        return add(b);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement multiply(ECFieldElement b) {
        long[] z = Nat448.create64();
        SecT409Field.multiply(this.x, ((SecT409FieldElement) b).x, z);
        return new SecT409FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement multiplyMinusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        return multiplyPlusProduct(b, x, y);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement multiplyPlusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] bx = ((SecT409FieldElement) b).x;
        long[] xx = ((SecT409FieldElement) x).x;
        long[] yx = ((SecT409FieldElement) y).x;
        long[] tt = Nat.create64(13);
        SecT409Field.multiplyAddToExt(ax, bx, tt);
        SecT409Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat448.create64();
        SecT409Field.reduce(tt, z);
        return new SecT409FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement divide(ECFieldElement b) {
        return multiply(b.invert());
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement negate() {
        return this;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement square() {
        long[] z = Nat448.create64();
        SecT409Field.square(this.x, z);
        return new SecT409FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement squareMinusProduct(ECFieldElement x, ECFieldElement y) {
        return squarePlusProduct(x, y);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement squarePlusProduct(ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] xx = ((SecT409FieldElement) x).x;
        long[] yx = ((SecT409FieldElement) y).x;
        long[] tt = Nat.create64(13);
        SecT409Field.squareAddToExt(ax, tt);
        SecT409Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat448.create64();
        SecT409Field.reduce(tt, z);
        return new SecT409FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement squarePow(int pow) {
        if (pow < 1) {
            return this;
        }
        long[] z = Nat448.create64();
        SecT409Field.squareN(this.x, pow, z);
        return new SecT409FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement invert() {
        long[] z = Nat448.create64();
        SecT409Field.invert(this.x, z);
        return new SecT409FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement sqrt() {
        long[] z = Nat448.create64();
        SecT409Field.sqrt(this.x, z);
        return new SecT409FieldElement(z);
    }

    public int getRepresentation() {
        return 2;
    }

    public int getM() {
        return 409;
    }

    public int getK1() {
        return 87;
    }

    public int getK2() {
        return 0;
    }

    public int getK3() {
        return 0;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecT409FieldElement)) {
            return false;
        }
        SecT409FieldElement o = (SecT409FieldElement) other;
        return Nat448.eq64(this.x, o.x);
    }

    public int hashCode() {
        return 4090087 ^ Arrays.hashCode(this.x, 0, 7);
    }
}
