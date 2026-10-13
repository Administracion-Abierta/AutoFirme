package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/custom/sec/SecP384R1FieldElement.class */
public class SecP384R1FieldElement extends ECFieldElement.AbstractFp {
    public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFF"));
    protected int[] x;

    public SecP384R1FieldElement(BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP384R1FieldElement");
        }
        this.x = SecP384R1Field.fromBigInteger(bigInteger);
    }

    public SecP384R1FieldElement() {
        this.x = Nat.create(12);
    }

    protected SecP384R1FieldElement(int[] iArr) {
        this.x = iArr;
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public boolean isZero() {
        return Nat.isZero(12, this.x);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public boolean isOne() {
        return Nat.isOne(12, this.x);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public boolean testBitZero() {
        return Nat.getBit(this.x, 0) == 1;
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public BigInteger toBigInteger() {
        return Nat.toBigInteger(12, this.x);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public String getFieldName() {
        return "SecP384R1Field";
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public int getFieldSize() {
        return Q.bitLength();
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement add(ECFieldElement eCFieldElement) {
        int[] iArrCreate = Nat.create(12);
        SecP384R1Field.add(this.x, ((SecP384R1FieldElement) eCFieldElement).x, iArrCreate);
        return new SecP384R1FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement addOne() {
        int[] iArrCreate = Nat.create(12);
        SecP384R1Field.addOne(this.x, iArrCreate);
        return new SecP384R1FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement subtract(ECFieldElement eCFieldElement) {
        int[] iArrCreate = Nat.create(12);
        SecP384R1Field.subtract(this.x, ((SecP384R1FieldElement) eCFieldElement).x, iArrCreate);
        return new SecP384R1FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement multiply(ECFieldElement eCFieldElement) {
        int[] iArrCreate = Nat.create(12);
        SecP384R1Field.multiply(this.x, ((SecP384R1FieldElement) eCFieldElement).x, iArrCreate);
        return new SecP384R1FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement divide(ECFieldElement eCFieldElement) {
        int[] iArrCreate = Nat.create(12);
        SecP384R1Field.inv(((SecP384R1FieldElement) eCFieldElement).x, iArrCreate);
        SecP384R1Field.multiply(iArrCreate, this.x, iArrCreate);
        return new SecP384R1FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement negate() {
        int[] iArrCreate = Nat.create(12);
        SecP384R1Field.negate(this.x, iArrCreate);
        return new SecP384R1FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement square() {
        int[] iArrCreate = Nat.create(12);
        SecP384R1Field.square(this.x, iArrCreate);
        return new SecP384R1FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement invert() {
        int[] iArrCreate = Nat.create(12);
        SecP384R1Field.inv(this.x, iArrCreate);
        return new SecP384R1FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement sqrt() {
        int[] iArr = this.x;
        if (Nat.isZero(12, iArr) || Nat.isOne(12, iArr)) {
            return this;
        }
        int[] iArrCreate = Nat.create(24);
        int[] iArrCreate2 = Nat.create(12);
        int[] iArrCreate3 = Nat.create(12);
        int[] iArrCreate4 = Nat.create(12);
        int[] iArrCreate5 = Nat.create(12);
        SecP384R1Field.square(iArr, iArrCreate2, iArrCreate);
        SecP384R1Field.multiply(iArrCreate2, iArr, iArrCreate2, iArrCreate);
        SecP384R1Field.squareN(iArrCreate2, 2, iArrCreate3, iArrCreate);
        SecP384R1Field.multiply(iArrCreate3, iArrCreate2, iArrCreate3, iArrCreate);
        SecP384R1Field.square(iArrCreate3, iArrCreate3, iArrCreate);
        SecP384R1Field.multiply(iArrCreate3, iArr, iArrCreate3, iArrCreate);
        SecP384R1Field.squareN(iArrCreate3, 5, iArrCreate4, iArrCreate);
        SecP384R1Field.multiply(iArrCreate4, iArrCreate3, iArrCreate4, iArrCreate);
        SecP384R1Field.squareN(iArrCreate4, 5, iArrCreate5, iArrCreate);
        SecP384R1Field.multiply(iArrCreate5, iArrCreate3, iArrCreate5, iArrCreate);
        SecP384R1Field.squareN(iArrCreate5, 15, iArrCreate3, iArrCreate);
        SecP384R1Field.multiply(iArrCreate3, iArrCreate5, iArrCreate3, iArrCreate);
        SecP384R1Field.squareN(iArrCreate3, 2, iArrCreate4, iArrCreate);
        SecP384R1Field.multiply(iArrCreate2, iArrCreate4, iArrCreate2, iArrCreate);
        SecP384R1Field.squareN(iArrCreate4, 28, iArrCreate4, iArrCreate);
        SecP384R1Field.multiply(iArrCreate3, iArrCreate4, iArrCreate3, iArrCreate);
        SecP384R1Field.squareN(iArrCreate3, 60, iArrCreate4, iArrCreate);
        SecP384R1Field.multiply(iArrCreate4, iArrCreate3, iArrCreate4, iArrCreate);
        SecP384R1Field.squareN(iArrCreate4, 120, iArrCreate3, iArrCreate);
        SecP384R1Field.multiply(iArrCreate3, iArrCreate4, iArrCreate3, iArrCreate);
        SecP384R1Field.squareN(iArrCreate3, 15, iArrCreate3, iArrCreate);
        SecP384R1Field.multiply(iArrCreate3, iArrCreate5, iArrCreate3, iArrCreate);
        SecP384R1Field.squareN(iArrCreate3, 33, iArrCreate3, iArrCreate);
        SecP384R1Field.multiply(iArrCreate3, iArrCreate2, iArrCreate3, iArrCreate);
        SecP384R1Field.squareN(iArrCreate3, 64, iArrCreate3, iArrCreate);
        SecP384R1Field.multiply(iArrCreate3, iArr, iArrCreate3, iArrCreate);
        SecP384R1Field.squareN(iArrCreate3, 30, iArrCreate2, iArrCreate);
        SecP384R1Field.square(iArrCreate2, iArrCreate3, iArrCreate);
        if (Nat.eq(12, iArr, iArrCreate3)) {
            return new SecP384R1FieldElement(iArrCreate2);
        }
        return null;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SecP384R1FieldElement) {
            return Nat.eq(12, this.x, ((SecP384R1FieldElement) obj).x);
        }
        return false;
    }

    public int hashCode() {
        return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 12);
    }
}
