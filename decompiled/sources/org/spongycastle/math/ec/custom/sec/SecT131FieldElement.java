package org.spongycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.raw.Nat;
import org.spongycastle.math.raw.Nat192;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/custom/sec/SecT131FieldElement.class */
public class SecT131FieldElement extends ECFieldElement {
    protected long[] x;

    public SecT131FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.bitLength() > 131) {
            throw new IllegalArgumentException("x value invalid for SecT131FieldElement");
        }
        this.x = SecT131Field.fromBigInteger(x);
    }

    public SecT131FieldElement() {
        this.x = Nat192.create64();
    }

    protected SecT131FieldElement(long[] x) {
        this.x = x;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean isOne() {
        return Nat192.isOne64(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean isZero() {
        return Nat192.isZero64(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean testBitZero() {
        return (this.x[0] & 1) != 0;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public BigInteger toBigInteger() {
        return Nat192.toBigInteger64(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public String getFieldName() {
        return "SecT131Field";
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public int getFieldSize() {
        return 131;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement add(ECFieldElement b) {
        long[] z = Nat192.create64();
        SecT131Field.add(this.x, ((SecT131FieldElement) b).x, z);
        return new SecT131FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement addOne() {
        long[] z = Nat192.create64();
        SecT131Field.addOne(this.x, z);
        return new SecT131FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement subtract(ECFieldElement b) {
        return add(b);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement multiply(ECFieldElement b) {
        long[] z = Nat192.create64();
        SecT131Field.multiply(this.x, ((SecT131FieldElement) b).x, z);
        return new SecT131FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement multiplyMinusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        return multiplyPlusProduct(b, x, y);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement multiplyPlusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] bx = ((SecT131FieldElement) b).x;
        long[] xx = ((SecT131FieldElement) x).x;
        long[] yx = ((SecT131FieldElement) y).x;
        long[] tt = Nat.create64(5);
        SecT131Field.multiplyAddToExt(ax, bx, tt);
        SecT131Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat192.create64();
        SecT131Field.reduce(tt, z);
        return new SecT131FieldElement(z);
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
        long[] z = Nat192.create64();
        SecT131Field.square(this.x, z);
        return new SecT131FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement squareMinusProduct(ECFieldElement x, ECFieldElement y) {
        return squarePlusProduct(x, y);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement squarePlusProduct(ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] xx = ((SecT131FieldElement) x).x;
        long[] yx = ((SecT131FieldElement) y).x;
        long[] tt = Nat.create64(5);
        SecT131Field.squareAddToExt(ax, tt);
        SecT131Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat192.create64();
        SecT131Field.reduce(tt, z);
        return new SecT131FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement squarePow(int pow) {
        if (pow < 1) {
            return this;
        }
        long[] z = Nat192.create64();
        SecT131Field.squareN(this.x, pow, z);
        return new SecT131FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement invert() {
        long[] z = Nat192.create64();
        SecT131Field.invert(this.x, z);
        return new SecT131FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement sqrt() {
        long[] z = Nat192.create64();
        SecT131Field.sqrt(this.x, z);
        return new SecT131FieldElement(z);
    }

    public int getRepresentation() {
        return 3;
    }

    public int getM() {
        return 131;
    }

    public int getK1() {
        return 2;
    }

    public int getK2() {
        return 3;
    }

    public int getK3() {
        return 8;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecT131FieldElement)) {
            return false;
        }
        SecT131FieldElement o = (SecT131FieldElement) other;
        return Nat192.eq64(this.x, o.x);
    }

    public int hashCode() {
        return 131832 ^ Arrays.hashCode(this.x, 0, 3);
    }
}
