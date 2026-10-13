package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat224;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/custom/sec/SecP224K1Point.class */
public class SecP224K1Point extends ECPoint.AbstractFp {
    SecP224K1Point(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        super(eCCurve, eCFieldElement, eCFieldElement2);
    }

    SecP224K1Point(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArr) {
        super(eCCurve, eCFieldElement, eCFieldElement2, eCFieldElementArr);
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    protected ECPoint detach() {
        return new SecP224K1Point(null, getAffineXCoord(), getAffineYCoord());
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    public ECPoint add(ECPoint eCPoint) {
        int[] iArr;
        int[] iArr2;
        int[] iArr3;
        int[] iArr4;
        if (isInfinity()) {
            return eCPoint;
        }
        if (eCPoint.isInfinity()) {
            return this;
        }
        if (this == eCPoint) {
            return twice();
        }
        ECCurve curve = getCurve();
        SecP224K1FieldElement secP224K1FieldElement = (SecP224K1FieldElement) this.x;
        SecP224K1FieldElement secP224K1FieldElement2 = (SecP224K1FieldElement) this.y;
        SecP224K1FieldElement secP224K1FieldElement3 = (SecP224K1FieldElement) eCPoint.getXCoord();
        SecP224K1FieldElement secP224K1FieldElement4 = (SecP224K1FieldElement) eCPoint.getYCoord();
        SecP224K1FieldElement secP224K1FieldElement5 = (SecP224K1FieldElement) this.zs[0];
        SecP224K1FieldElement secP224K1FieldElement6 = (SecP224K1FieldElement) eCPoint.getZCoord(0);
        int[] iArrCreateExt = Nat224.createExt();
        int[] iArrCreate = Nat224.create();
        int[] iArrCreate2 = Nat224.create();
        int[] iArrCreate3 = Nat224.create();
        boolean zIsOne = secP224K1FieldElement5.isOne();
        if (zIsOne) {
            iArr2 = secP224K1FieldElement3.x;
            iArr = secP224K1FieldElement4.x;
        } else {
            iArr = iArrCreate2;
            SecP224K1Field.square(secP224K1FieldElement5.x, iArr);
            iArr2 = iArrCreate;
            SecP224K1Field.multiply(iArr, secP224K1FieldElement3.x, iArr2);
            SecP224K1Field.multiply(iArr, secP224K1FieldElement5.x, iArr);
            SecP224K1Field.multiply(iArr, secP224K1FieldElement4.x, iArr);
        }
        boolean zIsOne2 = secP224K1FieldElement6.isOne();
        if (zIsOne2) {
            iArr4 = secP224K1FieldElement.x;
            iArr3 = secP224K1FieldElement2.x;
        } else {
            iArr3 = iArrCreate3;
            SecP224K1Field.square(secP224K1FieldElement6.x, iArr3);
            iArr4 = iArrCreateExt;
            SecP224K1Field.multiply(iArr3, secP224K1FieldElement.x, iArr4);
            SecP224K1Field.multiply(iArr3, secP224K1FieldElement6.x, iArr3);
            SecP224K1Field.multiply(iArr3, secP224K1FieldElement2.x, iArr3);
        }
        int[] iArrCreate4 = Nat224.create();
        SecP224K1Field.subtract(iArr4, iArr2, iArrCreate4);
        SecP224K1Field.subtract(iArr3, iArr, iArrCreate);
        if (Nat224.isZero(iArrCreate4)) {
            return Nat224.isZero(iArrCreate) ? twice() : curve.getInfinity();
        }
        SecP224K1Field.square(iArrCreate4, iArrCreate2);
        int[] iArrCreate5 = Nat224.create();
        SecP224K1Field.multiply(iArrCreate2, iArrCreate4, iArrCreate5);
        SecP224K1Field.multiply(iArrCreate2, iArr4, iArrCreate2);
        SecP224K1Field.negate(iArrCreate5, iArrCreate5);
        Nat224.mul(iArr3, iArrCreate5, iArrCreateExt);
        SecP224K1Field.reduce32(Nat224.addBothTo(iArrCreate2, iArrCreate2, iArrCreate5), iArrCreate5);
        SecP224K1FieldElement secP224K1FieldElement7 = new SecP224K1FieldElement(iArrCreate3);
        SecP224K1Field.square(iArrCreate, secP224K1FieldElement7.x);
        SecP224K1Field.subtract(secP224K1FieldElement7.x, iArrCreate5, secP224K1FieldElement7.x);
        SecP224K1FieldElement secP224K1FieldElement8 = new SecP224K1FieldElement(iArrCreate5);
        SecP224K1Field.subtract(iArrCreate2, secP224K1FieldElement7.x, secP224K1FieldElement8.x);
        SecP224K1Field.multiplyAddToExt(secP224K1FieldElement8.x, iArrCreate, iArrCreateExt);
        SecP224K1Field.reduce(iArrCreateExt, secP224K1FieldElement8.x);
        SecP224K1FieldElement secP224K1FieldElement9 = new SecP224K1FieldElement(iArrCreate4);
        if (!zIsOne) {
            SecP224K1Field.multiply(secP224K1FieldElement9.x, secP224K1FieldElement5.x, secP224K1FieldElement9.x);
        }
        if (!zIsOne2) {
            SecP224K1Field.multiply(secP224K1FieldElement9.x, secP224K1FieldElement6.x, secP224K1FieldElement9.x);
        }
        return new SecP224K1Point(curve, secP224K1FieldElement7, secP224K1FieldElement8, new ECFieldElement[]{secP224K1FieldElement9});
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    public ECPoint twice() {
        if (isInfinity()) {
            return this;
        }
        ECCurve curve = getCurve();
        SecP224K1FieldElement secP224K1FieldElement = (SecP224K1FieldElement) this.y;
        if (secP224K1FieldElement.isZero()) {
            return curve.getInfinity();
        }
        SecP224K1FieldElement secP224K1FieldElement2 = (SecP224K1FieldElement) this.x;
        SecP224K1FieldElement secP224K1FieldElement3 = (SecP224K1FieldElement) this.zs[0];
        int[] iArrCreate = Nat224.create();
        SecP224K1Field.square(secP224K1FieldElement.x, iArrCreate);
        int[] iArrCreate2 = Nat224.create();
        SecP224K1Field.square(iArrCreate, iArrCreate2);
        int[] iArrCreate3 = Nat224.create();
        SecP224K1Field.square(secP224K1FieldElement2.x, iArrCreate3);
        SecP224K1Field.reduce32(Nat224.addBothTo(iArrCreate3, iArrCreate3, iArrCreate3), iArrCreate3);
        SecP224K1Field.multiply(iArrCreate, secP224K1FieldElement2.x, iArrCreate);
        SecP224K1Field.reduce32(Nat.shiftUpBits(7, iArrCreate, 2, 0), iArrCreate);
        int[] iArrCreate4 = Nat224.create();
        SecP224K1Field.reduce32(Nat.shiftUpBits(7, iArrCreate2, 3, 0, iArrCreate4), iArrCreate4);
        SecP224K1FieldElement secP224K1FieldElement4 = new SecP224K1FieldElement(iArrCreate2);
        SecP224K1Field.square(iArrCreate3, secP224K1FieldElement4.x);
        SecP224K1Field.subtract(secP224K1FieldElement4.x, iArrCreate, secP224K1FieldElement4.x);
        SecP224K1Field.subtract(secP224K1FieldElement4.x, iArrCreate, secP224K1FieldElement4.x);
        SecP224K1FieldElement secP224K1FieldElement5 = new SecP224K1FieldElement(iArrCreate);
        SecP224K1Field.subtract(iArrCreate, secP224K1FieldElement4.x, secP224K1FieldElement5.x);
        SecP224K1Field.multiply(secP224K1FieldElement5.x, iArrCreate3, secP224K1FieldElement5.x);
        SecP224K1Field.subtract(secP224K1FieldElement5.x, iArrCreate4, secP224K1FieldElement5.x);
        SecP224K1FieldElement secP224K1FieldElement6 = new SecP224K1FieldElement(iArrCreate3);
        SecP224K1Field.twice(secP224K1FieldElement.x, secP224K1FieldElement6.x);
        if (!secP224K1FieldElement3.isOne()) {
            SecP224K1Field.multiply(secP224K1FieldElement6.x, secP224K1FieldElement3.x, secP224K1FieldElement6.x);
        }
        return new SecP224K1Point(curve, secP224K1FieldElement4, secP224K1FieldElement5, new ECFieldElement[]{secP224K1FieldElement6});
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    public ECPoint twicePlus(ECPoint eCPoint) {
        if (this == eCPoint) {
            return threeTimes();
        }
        if (isInfinity()) {
            return eCPoint;
        }
        if (eCPoint.isInfinity()) {
            return twice();
        }
        return this.y.isZero() ? eCPoint : twice().add(eCPoint);
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    public ECPoint threeTimes() {
        return (isInfinity() || this.y.isZero()) ? this : twice().add(this);
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    public ECPoint negate() {
        return isInfinity() ? this : new SecP224K1Point(this.curve, this.x, this.y.negate(), this.zs);
    }
}
