package org.spongycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.raw.Nat576;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/ec/custom/sec/SecT571FieldElement.class */
public class SecT571FieldElement extends ECFieldElement {
    protected long[] x;

    public SecT571FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.bitLength() > 571) {
            throw new IllegalArgumentException("x value invalid for SecT571FieldElement");
        }
        this.x = SecT571Field.fromBigInteger(x);
    }

    public SecT571FieldElement() {
        this.x = Nat576.create64();
    }

    protected SecT571FieldElement(long[] x) {
        this.x = x;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean isOne() {
        return Nat576.isOne64(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean isZero() {
        return Nat576.isZero64(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean testBitZero() {
        return (this.x[0] & 1) != 0;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public BigInteger toBigInteger() {
        return Nat576.toBigInteger64(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public String getFieldName() {
        return "SecT571Field";
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public int getFieldSize() {
        return 571;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement add(ECFieldElement b) {
        long[] z = Nat576.create64();
        SecT571Field.add(this.x, ((SecT571FieldElement) b).x, z);
        return new SecT571FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement addOne() {
        long[] z = Nat576.create64();
        SecT571Field.addOne(this.x, z);
        return new SecT571FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement subtract(ECFieldElement b) {
        return add(b);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement multiply(ECFieldElement b) {
        long[] z = Nat576.create64();
        SecT571Field.multiply(this.x, ((SecT571FieldElement) b).x, z);
        return new SecT571FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement multiplyMinusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        return multiplyPlusProduct(b, x, y);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement multiplyPlusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] bx = ((SecT571FieldElement) b).x;
        long[] xx = ((SecT571FieldElement) x).x;
        long[] yx = ((SecT571FieldElement) y).x;
        long[] tt = Nat576.createExt64();
        SecT571Field.multiplyAddToExt(ax, bx, tt);
        SecT571Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat576.create64();
        SecT571Field.reduce(tt, z);
        return new SecT571FieldElement(z);
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
        long[] z = Nat576.create64();
        SecT571Field.square(this.x, z);
        return new SecT571FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement squareMinusProduct(ECFieldElement x, ECFieldElement y) {
        return squarePlusProduct(x, y);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement squarePlusProduct(ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] xx = ((SecT571FieldElement) x).x;
        long[] yx = ((SecT571FieldElement) y).x;
        long[] tt = Nat576.createExt64();
        SecT571Field.squareAddToExt(ax, tt);
        SecT571Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat576.create64();
        SecT571Field.reduce(tt, z);
        return new SecT571FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement squarePow(int pow) {
        if (pow < 1) {
            return this;
        }
        long[] z = Nat576.create64();
        SecT571Field.squareN(this.x, pow, z);
        return new SecT571FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement invert() {
        long[] z = Nat576.create64();
        SecT571Field.invert(this.x, z);
        return new SecT571FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement sqrt() {
        long[] z = Nat576.create64();
        SecT571Field.sqrt(this.x, z);
        return new SecT571FieldElement(z);
    }

    public int getRepresentation() {
        return 3;
    }

    public int getM() {
        return 571;
    }

    public int getK1() {
        return 2;
    }

    public int getK2() {
        return 5;
    }

    public int getK3() {
        return 10;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecT571FieldElement)) {
            return false;
        }
        SecT571FieldElement o = (SecT571FieldElement) other;
        return Nat576.eq64(this.x, o.x);
    }

    public int hashCode() {
        return 5711052 ^ Arrays.hashCode(this.x, 0, 9);
    }
}
