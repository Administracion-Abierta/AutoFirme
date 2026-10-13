package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/custom/sec/SecP256K1FieldElement.class */
public class SecP256K1FieldElement extends ECFieldElement.AbstractFp {
    public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F"));
    protected int[] x;

    public SecP256K1FieldElement(BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP256K1FieldElement");
        }
        this.x = SecP256K1Field.fromBigInteger(bigInteger);
    }

    public SecP256K1FieldElement() {
        this.x = Nat256.create();
    }

    protected SecP256K1FieldElement(int[] iArr) {
        this.x = iArr;
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public boolean isZero() {
        return Nat256.isZero(this.x);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public boolean isOne() {
        return Nat256.isOne(this.x);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public boolean testBitZero() {
        return Nat256.getBit(this.x, 0) == 1;
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public BigInteger toBigInteger() {
        return Nat256.toBigInteger(this.x);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public String getFieldName() {
        return "SecP256K1Field";
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public int getFieldSize() {
        return Q.bitLength();
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement add(ECFieldElement eCFieldElement) {
        int[] iArrCreate = Nat256.create();
        SecP256K1Field.add(this.x, ((SecP256K1FieldElement) eCFieldElement).x, iArrCreate);
        return new SecP256K1FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement addOne() {
        int[] iArrCreate = Nat256.create();
        SecP256K1Field.addOne(this.x, iArrCreate);
        return new SecP256K1FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement subtract(ECFieldElement eCFieldElement) {
        int[] iArrCreate = Nat256.create();
        SecP256K1Field.subtract(this.x, ((SecP256K1FieldElement) eCFieldElement).x, iArrCreate);
        return new SecP256K1FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement multiply(ECFieldElement eCFieldElement) {
        int[] iArrCreate = Nat256.create();
        SecP256K1Field.multiply(this.x, ((SecP256K1FieldElement) eCFieldElement).x, iArrCreate);
        return new SecP256K1FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement divide(ECFieldElement eCFieldElement) {
        int[] iArrCreate = Nat256.create();
        SecP256K1Field.inv(((SecP256K1FieldElement) eCFieldElement).x, iArrCreate);
        SecP256K1Field.multiply(iArrCreate, this.x, iArrCreate);
        return new SecP256K1FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement negate() {
        int[] iArrCreate = Nat256.create();
        SecP256K1Field.negate(this.x, iArrCreate);
        return new SecP256K1FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement square() {
        int[] iArrCreate = Nat256.create();
        SecP256K1Field.square(this.x, iArrCreate);
        return new SecP256K1FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement invert() {
        int[] iArrCreate = Nat256.create();
        SecP256K1Field.inv(this.x, iArrCreate);
        return new SecP256K1FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement sqrt() {
        int[] iArr = this.x;
        if (Nat256.isZero(iArr) || Nat256.isOne(iArr)) {
            return this;
        }
        int[] iArrCreateExt = Nat256.createExt();
        int[] iArrCreate = Nat256.create();
        SecP256K1Field.square(iArr, iArrCreate, iArrCreateExt);
        SecP256K1Field.multiply(iArrCreate, iArr, iArrCreate, iArrCreateExt);
        int[] iArrCreate2 = Nat256.create();
        SecP256K1Field.square(iArrCreate, iArrCreate2, iArrCreateExt);
        SecP256K1Field.multiply(iArrCreate2, iArr, iArrCreate2, iArrCreateExt);
        int[] iArrCreate3 = Nat256.create();
        SecP256K1Field.squareN(iArrCreate2, 3, iArrCreate3, iArrCreateExt);
        SecP256K1Field.multiply(iArrCreate3, iArrCreate2, iArrCreate3, iArrCreateExt);
        SecP256K1Field.squareN(iArrCreate3, 3, iArrCreate3, iArrCreateExt);
        SecP256K1Field.multiply(iArrCreate3, iArrCreate2, iArrCreate3, iArrCreateExt);
        SecP256K1Field.squareN(iArrCreate3, 2, iArrCreate3, iArrCreateExt);
        SecP256K1Field.multiply(iArrCreate3, iArrCreate, iArrCreate3, iArrCreateExt);
        int[] iArrCreate4 = Nat256.create();
        SecP256K1Field.squareN(iArrCreate3, 11, iArrCreate4, iArrCreateExt);
        SecP256K1Field.multiply(iArrCreate4, iArrCreate3, iArrCreate4, iArrCreateExt);
        SecP256K1Field.squareN(iArrCreate4, 22, iArrCreate3, iArrCreateExt);
        SecP256K1Field.multiply(iArrCreate3, iArrCreate4, iArrCreate3, iArrCreateExt);
        int[] iArrCreate5 = Nat256.create();
        SecP256K1Field.squareN(iArrCreate3, 44, iArrCreate5, iArrCreateExt);
        SecP256K1Field.multiply(iArrCreate5, iArrCreate3, iArrCreate5, iArrCreateExt);
        int[] iArrCreate6 = Nat256.create();
        SecP256K1Field.squareN(iArrCreate5, 88, iArrCreate6, iArrCreateExt);
        SecP256K1Field.multiply(iArrCreate6, iArrCreate5, iArrCreate6, iArrCreateExt);
        SecP256K1Field.squareN(iArrCreate6, 44, iArrCreate5, iArrCreateExt);
        SecP256K1Field.multiply(iArrCreate5, iArrCreate3, iArrCreate5, iArrCreateExt);
        SecP256K1Field.squareN(iArrCreate5, 3, iArrCreate3, iArrCreateExt);
        SecP256K1Field.multiply(iArrCreate3, iArrCreate2, iArrCreate3, iArrCreateExt);
        SecP256K1Field.squareN(iArrCreate3, 23, iArrCreate3, iArrCreateExt);
        SecP256K1Field.multiply(iArrCreate3, iArrCreate4, iArrCreate3, iArrCreateExt);
        SecP256K1Field.squareN(iArrCreate3, 6, iArrCreate3, iArrCreateExt);
        SecP256K1Field.multiply(iArrCreate3, iArrCreate, iArrCreate3, iArrCreateExt);
        SecP256K1Field.squareN(iArrCreate3, 2, iArrCreate3, iArrCreateExt);
        SecP256K1Field.square(iArrCreate3, iArrCreate, iArrCreateExt);
        if (Nat256.eq(iArr, iArrCreate)) {
            return new SecP256K1FieldElement(iArrCreate3);
        }
        return null;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SecP256K1FieldElement) {
            return Nat256.eq(this.x, ((SecP256K1FieldElement) obj).x);
        }
        return false;
    }

    public int hashCode() {
        return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 8);
    }
}
