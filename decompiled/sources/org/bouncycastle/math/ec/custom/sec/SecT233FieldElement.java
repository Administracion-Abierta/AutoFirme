package org.bouncycastle.math.ec.custom.sec;

import com.sun.jna.platform.win32.WinError;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/custom/sec/SecT233FieldElement.class */
public class SecT233FieldElement extends ECFieldElement.AbstractF2m {
    protected long[] x;

    public SecT233FieldElement(BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.bitLength() > 233) {
            throw new IllegalArgumentException("x value invalid for SecT233FieldElement");
        }
        this.x = SecT233Field.fromBigInteger(bigInteger);
    }

    public SecT233FieldElement() {
        this.x = Nat256.create64();
    }

    protected SecT233FieldElement(long[] jArr) {
        this.x = jArr;
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public boolean isOne() {
        return Nat256.isOne64(this.x);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public boolean isZero() {
        return Nat256.isZero64(this.x);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public boolean testBitZero() {
        return (this.x[0] & 1) != 0;
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public BigInteger toBigInteger() {
        return Nat256.toBigInteger64(this.x);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public String getFieldName() {
        return "SecT233Field";
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public int getFieldSize() {
        return WinError.ERROR_PIPE_NOT_CONNECTED;
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement add(ECFieldElement eCFieldElement) {
        long[] jArrCreate64 = Nat256.create64();
        SecT233Field.add(this.x, ((SecT233FieldElement) eCFieldElement).x, jArrCreate64);
        return new SecT233FieldElement(jArrCreate64);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement addOne() {
        long[] jArrCreate64 = Nat256.create64();
        SecT233Field.addOne(this.x, jArrCreate64);
        return new SecT233FieldElement(jArrCreate64);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement subtract(ECFieldElement eCFieldElement) {
        return add(eCFieldElement);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement multiply(ECFieldElement eCFieldElement) {
        long[] jArrCreate64 = Nat256.create64();
        SecT233Field.multiply(this.x, ((SecT233FieldElement) eCFieldElement).x, jArrCreate64);
        return new SecT233FieldElement(jArrCreate64);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement multiplyMinusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement eCFieldElement3) {
        return multiplyPlusProduct(eCFieldElement, eCFieldElement2, eCFieldElement3);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement multiplyPlusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement eCFieldElement3) {
        long[] jArr = this.x;
        long[] jArr2 = ((SecT233FieldElement) eCFieldElement).x;
        long[] jArr3 = ((SecT233FieldElement) eCFieldElement2).x;
        long[] jArr4 = ((SecT233FieldElement) eCFieldElement3).x;
        long[] jArrCreateExt64 = Nat256.createExt64();
        SecT233Field.multiplyAddToExt(jArr, jArr2, jArrCreateExt64);
        SecT233Field.multiplyAddToExt(jArr3, jArr4, jArrCreateExt64);
        long[] jArrCreate64 = Nat256.create64();
        SecT233Field.reduce(jArrCreateExt64, jArrCreate64);
        return new SecT233FieldElement(jArrCreate64);
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
        long[] jArrCreate64 = Nat256.create64();
        SecT233Field.square(this.x, jArrCreate64);
        return new SecT233FieldElement(jArrCreate64);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement squareMinusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        return squarePlusProduct(eCFieldElement, eCFieldElement2);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement squarePlusProduct(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        long[] jArr = this.x;
        long[] jArr2 = ((SecT233FieldElement) eCFieldElement).x;
        long[] jArr3 = ((SecT233FieldElement) eCFieldElement2).x;
        long[] jArrCreateExt64 = Nat256.createExt64();
        SecT233Field.squareAddToExt(jArr, jArrCreateExt64);
        SecT233Field.multiplyAddToExt(jArr2, jArr3, jArrCreateExt64);
        long[] jArrCreate64 = Nat256.create64();
        SecT233Field.reduce(jArrCreateExt64, jArrCreate64);
        return new SecT233FieldElement(jArrCreate64);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement squarePow(int i) {
        if (i < 1) {
            return this;
        }
        long[] jArrCreate64 = Nat256.create64();
        SecT233Field.squareN(this.x, i, jArrCreate64);
        return new SecT233FieldElement(jArrCreate64);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement.AbstractF2m
    public ECFieldElement halfTrace() {
        long[] jArrCreate64 = Nat256.create64();
        SecT233Field.halfTrace(this.x, jArrCreate64);
        return new SecT233FieldElement(jArrCreate64);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement.AbstractF2m
    public boolean hasFastTrace() {
        return true;
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement.AbstractF2m
    public int trace() {
        return SecT233Field.trace(this.x);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement invert() {
        long[] jArrCreate64 = Nat256.create64();
        SecT233Field.invert(this.x, jArrCreate64);
        return new SecT233FieldElement(jArrCreate64);
    }

    @Override // org.bouncycastle.math.ec.ECFieldElement
    public ECFieldElement sqrt() {
        long[] jArrCreate64 = Nat256.create64();
        SecT233Field.sqrt(this.x, jArrCreate64);
        return new SecT233FieldElement(jArrCreate64);
    }

    public int getRepresentation() {
        return 2;
    }

    public int getM() {
        return WinError.ERROR_PIPE_NOT_CONNECTED;
    }

    public int getK1() {
        return 74;
    }

    public int getK2() {
        return 0;
    }

    public int getK3() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SecT233FieldElement) {
            return Nat256.eq64(this.x, ((SecT233FieldElement) obj).x);
        }
        return false;
    }

    public int hashCode() {
        return 2330074 ^ Arrays.hashCode(this.x, 0, 4);
    }
}
