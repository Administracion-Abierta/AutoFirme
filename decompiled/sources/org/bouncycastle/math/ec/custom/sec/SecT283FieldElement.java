package org.bouncycastle.math.ec.custom.sec;

import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat320;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/custom/sec/SecT283FieldElement.class */
public class SecT283FieldElement extends ECFieldElement.AbstractF2m {
    protected long[] x;

    public SecT283FieldElement(BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.bitLength() > 283) {
            throw new IllegalArgumentException("x value invalid for SecT283FieldElement");
        }
        this.x = SecT283Field.fromBigInteger(bigInteger);
    }

    public SecT283FieldElement() {
        this.x = Nat320.create64();
    }

    protected SecT283FieldElement(long[] jArr) {
        this.x = jArr;
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public boolean isOne() {
        return Nat320.isOne64(this.x);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public boolean isZero() {
        return Nat320.isZero64(this.x);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public boolean testBitZero() {
        return (this.x[0] & 1) != 0;
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public BigInteger toBigInteger() {
        return Nat320.toBigInteger64(this.x);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public String getFieldName() {
        return "SecT283Field";
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public int getFieldSize() {
        return TIFF.TAG_Y_RESOLUTION;
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement add(ECFieldElement eCFieldElement) {
        long[] jArrCreate64 = Nat320.create64();
        SecT283Field.add(this.x, ((SecT283FieldElement) eCFieldElement).x, jArrCreate64);
        return new SecT283FieldElement(jArrCreate64);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement addOne() {
        long[] jArrCreate64 = Nat320.create64();
        SecT283Field.addOne(this.x, jArrCreate64);
        return new SecT283FieldElement(jArrCreate64);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement subtract(ECFieldElement eCFieldElement) {
        return add(eCFieldElement);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement multiply(ECFieldElement eCFieldElement) {
        long[] jArrCreate64 = Nat320.create64();
        SecT283Field.multiply(this.x, ((SecT283FieldElement) eCFieldElement).x, jArrCreate64);
        return new SecT283FieldElement(jArrCreate64);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement multiplyMinusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement eCFieldElement3) {
        return multiplyPlusProduct(eCFieldElement, eCFieldElement2, eCFieldElement3);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement multiplyPlusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement eCFieldElement3) {
        long[] jArr = this.x;
        long[] jArr2 = ((SecT283FieldElement) eCFieldElement).x;
        long[] jArr3 = ((SecT283FieldElement) eCFieldElement2).x;
        long[] jArr4 = ((SecT283FieldElement) eCFieldElement3).x;
        long[] jArrCreate64 = Nat.create64(9);
        SecT283Field.multiplyAddToExt(jArr, jArr2, jArrCreate64);
        SecT283Field.multiplyAddToExt(jArr3, jArr4, jArrCreate64);
        long[] jArrCreate65 = Nat320.create64();
        SecT283Field.reduce(jArrCreate64, jArrCreate65);
        return new SecT283FieldElement(jArrCreate65);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement divide(ECFieldElement eCFieldElement) {
        return multiply(eCFieldElement.invert());
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement negate() {
        return this;
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement square() {
        long[] jArrCreate64 = Nat320.create64();
        SecT283Field.square(this.x, jArrCreate64);
        return new SecT283FieldElement(jArrCreate64);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement squareMinusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        return squarePlusProduct(eCFieldElement, eCFieldElement2);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement squarePlusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        long[] jArr = this.x;
        long[] jArr2 = ((SecT283FieldElement) eCFieldElement).x;
        long[] jArr3 = ((SecT283FieldElement) eCFieldElement2).x;
        long[] jArrCreate64 = Nat.create64(9);
        SecT283Field.squareAddToExt(jArr, jArrCreate64);
        SecT283Field.multiplyAddToExt(jArr2, jArr3, jArrCreate64);
        long[] jArrCreate65 = Nat320.create64();
        SecT283Field.reduce(jArrCreate64, jArrCreate65);
        return new SecT283FieldElement(jArrCreate65);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement squarePow(int i) {
        if (i < 1) {
            return this;
        }
        long[] jArrCreate64 = Nat320.create64();
        SecT283Field.squareN(this.x, i, jArrCreate64);
        return new SecT283FieldElement(jArrCreate64);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement.AbstractF2m
    public ECFieldElement halfTrace() {
        long[] jArrCreate64 = Nat320.create64();
        SecT283Field.halfTrace(this.x, jArrCreate64);
        return new SecT283FieldElement(jArrCreate64);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement.AbstractF2m
    public boolean hasFastTrace() {
        return true;
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement.AbstractF2m
    public int trace() {
        return SecT283Field.trace(this.x);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement invert() {
        long[] jArrCreate64 = Nat320.create64();
        SecT283Field.invert(this.x, jArrCreate64);
        return new SecT283FieldElement(jArrCreate64);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement sqrt() {
        long[] jArrCreate64 = Nat320.create64();
        SecT283Field.sqrt(this.x, jArrCreate64);
        return new SecT283FieldElement(jArrCreate64);
    }

    public int getRepresentation() {
        return 3;
    }

    public int getM() {
        return TIFF.TAG_Y_RESOLUTION;
    }

    public int getK1() {
        return 5;
    }

    public int getK2() {
        return 7;
    }

    public int getK3() {
        return 12;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SecT283FieldElement) {
            return Nat320.eq64(this.x, ((SecT283FieldElement) obj).x);
        }
        return false;
    }

    public int hashCode() {
        return 2831275 ^ Arrays.hashCode(this.x, 0, 5);
    }
}
