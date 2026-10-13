package org.spongycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.spongycastle.crypto.tls.AlertDescription;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.raw.Nat128;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/ec/custom/sec/SecT113FieldElement.class */
public class SecT113FieldElement extends ECFieldElement {
    protected long[] x;

    public SecT113FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.bitLength() > 113) {
            throw new IllegalArgumentException("x value invalid for SecT113FieldElement");
        }
        this.x = SecT113Field.fromBigInteger(x);
    }

    public SecT113FieldElement() {
        this.x = Nat128.create64();
    }

    protected SecT113FieldElement(long[] x) {
        this.x = x;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean isOne() {
        return Nat128.isOne64(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean isZero() {
        return Nat128.isZero64(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean testBitZero() {
        return (this.x[0] & 1) != 0;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public BigInteger toBigInteger() {
        return Nat128.toBigInteger64(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public String getFieldName() {
        return "SecT113Field";
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public int getFieldSize() {
        return AlertDescription.bad_certificate_status_response;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement add(ECFieldElement b) {
        long[] z = Nat128.create64();
        SecT113Field.add(this.x, ((SecT113FieldElement) b).x, z);
        return new SecT113FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement addOne() {
        long[] z = Nat128.create64();
        SecT113Field.addOne(this.x, z);
        return new SecT113FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement subtract(ECFieldElement b) {
        return add(b);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement multiply(ECFieldElement b) {
        long[] z = Nat128.create64();
        SecT113Field.multiply(this.x, ((SecT113FieldElement) b).x, z);
        return new SecT113FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement multiplyMinusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        return multiplyPlusProduct(b, x, y);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement multiplyPlusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] bx = ((SecT113FieldElement) b).x;
        long[] xx = ((SecT113FieldElement) x).x;
        long[] yx = ((SecT113FieldElement) y).x;
        long[] tt = Nat128.createExt64();
        SecT113Field.multiplyAddToExt(ax, bx, tt);
        SecT113Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat128.create64();
        SecT113Field.reduce(tt, z);
        return new SecT113FieldElement(z);
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
        long[] z = Nat128.create64();
        SecT113Field.square(this.x, z);
        return new SecT113FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement squareMinusProduct(ECFieldElement x, ECFieldElement y) {
        return squarePlusProduct(x, y);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement squarePlusProduct(ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] xx = ((SecT113FieldElement) x).x;
        long[] yx = ((SecT113FieldElement) y).x;
        long[] tt = Nat128.createExt64();
        SecT113Field.squareAddToExt(ax, tt);
        SecT113Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat128.create64();
        SecT113Field.reduce(tt, z);
        return new SecT113FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement squarePow(int pow) {
        if (pow < 1) {
            return this;
        }
        long[] z = Nat128.create64();
        SecT113Field.squareN(this.x, pow, z);
        return new SecT113FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement invert() {
        long[] z = Nat128.create64();
        SecT113Field.invert(this.x, z);
        return new SecT113FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement sqrt() {
        long[] z = Nat128.create64();
        SecT113Field.sqrt(this.x, z);
        return new SecT113FieldElement(z);
    }

    public int getRepresentation() {
        return 2;
    }

    public int getM() {
        return AlertDescription.bad_certificate_status_response;
    }

    public int getK1() {
        return 9;
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
        if (!(other instanceof SecT113FieldElement)) {
            return false;
        }
        SecT113FieldElement o = (SecT113FieldElement) other;
        return Nat128.eq64(this.x, o.x);
    }

    public int hashCode() {
        return 113009 ^ Arrays.hashCode(this.x, 0, 2);
    }
}
