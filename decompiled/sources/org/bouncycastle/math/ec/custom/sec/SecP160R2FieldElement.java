package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat160;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/custom/sec/SecP160R2FieldElement.class */
public class SecP160R2FieldElement extends ECFieldElement.AbstractFp {
    public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFAC73"));
    protected int[] x;

    public SecP160R2FieldElement(BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP160R2FieldElement");
        }
        this.x = SecP160R2Field.fromBigInteger(bigInteger);
    }

    public SecP160R2FieldElement() {
        this.x = Nat160.create();
    }

    protected SecP160R2FieldElement(int[] iArr) {
        this.x = iArr;
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public boolean isZero() {
        return Nat160.isZero(this.x);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public boolean isOne() {
        return Nat160.isOne(this.x);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public boolean testBitZero() {
        return Nat160.getBit(this.x, 0) == 1;
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public BigInteger toBigInteger() {
        return Nat160.toBigInteger(this.x);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public String getFieldName() {
        return "SecP160R2Field";
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public int getFieldSize() {
        return Q.bitLength();
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement add(ECFieldElement eCFieldElement) {
        int[] iArrCreate = Nat160.create();
        SecP160R2Field.add(this.x, ((SecP160R2FieldElement) eCFieldElement).x, iArrCreate);
        return new SecP160R2FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement addOne() {
        int[] iArrCreate = Nat160.create();
        SecP160R2Field.addOne(this.x, iArrCreate);
        return new SecP160R2FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement subtract(ECFieldElement eCFieldElement) {
        int[] iArrCreate = Nat160.create();
        SecP160R2Field.subtract(this.x, ((SecP160R2FieldElement) eCFieldElement).x, iArrCreate);
        return new SecP160R2FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement multiply(ECFieldElement eCFieldElement) {
        int[] iArrCreate = Nat160.create();
        SecP160R2Field.multiply(this.x, ((SecP160R2FieldElement) eCFieldElement).x, iArrCreate);
        return new SecP160R2FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement divide(ECFieldElement eCFieldElement) {
        int[] iArrCreate = Nat160.create();
        SecP160R2Field.inv(((SecP160R2FieldElement) eCFieldElement).x, iArrCreate);
        SecP160R2Field.multiply(iArrCreate, this.x, iArrCreate);
        return new SecP160R2FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement negate() {
        int[] iArrCreate = Nat160.create();
        SecP160R2Field.negate(this.x, iArrCreate);
        return new SecP160R2FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement square() {
        int[] iArrCreate = Nat160.create();
        SecP160R2Field.square(this.x, iArrCreate);
        return new SecP160R2FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement invert() {
        int[] iArrCreate = Nat160.create();
        SecP160R2Field.inv(this.x, iArrCreate);
        return new SecP160R2FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement sqrt() {
        int[] iArr = this.x;
        if (Nat160.isZero(iArr) || Nat160.isOne(iArr)) {
            return this;
        }
        int[] iArrCreate = Nat160.create();
        SecP160R2Field.square(iArr, iArrCreate);
        SecP160R2Field.multiply(iArrCreate, iArr, iArrCreate);
        int[] iArrCreate2 = Nat160.create();
        SecP160R2Field.square(iArrCreate, iArrCreate2);
        SecP160R2Field.multiply(iArrCreate2, iArr, iArrCreate2);
        int[] iArrCreate3 = Nat160.create();
        SecP160R2Field.square(iArrCreate2, iArrCreate3);
        SecP160R2Field.multiply(iArrCreate3, iArr, iArrCreate3);
        int[] iArrCreate4 = Nat160.create();
        SecP160R2Field.squareN(iArrCreate3, 3, iArrCreate4);
        SecP160R2Field.multiply(iArrCreate4, iArrCreate2, iArrCreate4);
        SecP160R2Field.squareN(iArrCreate4, 7, iArrCreate3);
        SecP160R2Field.multiply(iArrCreate3, iArrCreate4, iArrCreate3);
        SecP160R2Field.squareN(iArrCreate3, 3, iArrCreate4);
        SecP160R2Field.multiply(iArrCreate4, iArrCreate2, iArrCreate4);
        int[] iArrCreate5 = Nat160.create();
        SecP160R2Field.squareN(iArrCreate4, 14, iArrCreate5);
        SecP160R2Field.multiply(iArrCreate5, iArrCreate3, iArrCreate5);
        SecP160R2Field.squareN(iArrCreate5, 31, iArrCreate3);
        SecP160R2Field.multiply(iArrCreate3, iArrCreate5, iArrCreate3);
        SecP160R2Field.squareN(iArrCreate3, 62, iArrCreate5);
        SecP160R2Field.multiply(iArrCreate5, iArrCreate3, iArrCreate5);
        SecP160R2Field.squareN(iArrCreate5, 3, iArrCreate3);
        SecP160R2Field.multiply(iArrCreate3, iArrCreate2, iArrCreate3);
        SecP160R2Field.squareN(iArrCreate3, 18, iArrCreate3);
        SecP160R2Field.multiply(iArrCreate3, iArrCreate4, iArrCreate3);
        SecP160R2Field.squareN(iArrCreate3, 2, iArrCreate3);
        SecP160R2Field.multiply(iArrCreate3, iArr, iArrCreate3);
        SecP160R2Field.squareN(iArrCreate3, 3, iArrCreate3);
        SecP160R2Field.multiply(iArrCreate3, iArrCreate, iArrCreate3);
        SecP160R2Field.squareN(iArrCreate3, 6, iArrCreate3);
        SecP160R2Field.multiply(iArrCreate3, iArrCreate2, iArrCreate3);
        SecP160R2Field.squareN(iArrCreate3, 2, iArrCreate3);
        SecP160R2Field.multiply(iArrCreate3, iArr, iArrCreate3);
        SecP160R2Field.square(iArrCreate3, iArrCreate);
        if (Nat160.eq(iArr, iArrCreate)) {
            return new SecP160R2FieldElement(iArrCreate3);
        }
        return null;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SecP160R2FieldElement) {
            return Nat160.eq(this.x, ((SecP160R2FieldElement) obj).x);
        }
        return false;
    }

    public int hashCode() {
        return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 5);
    }
}
