package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat128;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/custom/sec/SecP128R1Point.class */
public class SecP128R1Point extends ECPoint.AbstractFp {
    SecP128R1Point(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        super(eCCurve, eCFieldElement, eCFieldElement2);
    }

    SecP128R1Point(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArr) {
        super(eCCurve, eCFieldElement, eCFieldElement2, eCFieldElementArr);
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    protected ECPoint detach() {
        return new SecP128R1Point(null, getAffineXCoord(), getAffineYCoord());
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
        SecP128R1FieldElement secP128R1FieldElement = (SecP128R1FieldElement) this.x;
        SecP128R1FieldElement secP128R1FieldElement2 = (SecP128R1FieldElement) this.y;
        SecP128R1FieldElement secP128R1FieldElement3 = (SecP128R1FieldElement) eCPoint.getXCoord();
        SecP128R1FieldElement secP128R1FieldElement4 = (SecP128R1FieldElement) eCPoint.getYCoord();
        SecP128R1FieldElement secP128R1FieldElement5 = (SecP128R1FieldElement) this.zs[0];
        SecP128R1FieldElement secP128R1FieldElement6 = (SecP128R1FieldElement) eCPoint.getZCoord(0);
        int[] iArrCreateExt = Nat128.createExt();
        int[] iArrCreate = Nat128.create();
        int[] iArrCreate2 = Nat128.create();
        int[] iArrCreate3 = Nat128.create();
        boolean zIsOne = secP128R1FieldElement5.isOne();
        if (zIsOne) {
            iArr2 = secP128R1FieldElement3.x;
            iArr = secP128R1FieldElement4.x;
        } else {
            iArr = iArrCreate2;
            SecP128R1Field.square(secP128R1FieldElement5.x, iArr);
            iArr2 = iArrCreate;
            SecP128R1Field.multiply(iArr, secP128R1FieldElement3.x, iArr2);
            SecP128R1Field.multiply(iArr, secP128R1FieldElement5.x, iArr);
            SecP128R1Field.multiply(iArr, secP128R1FieldElement4.x, iArr);
        }
        boolean zIsOne2 = secP128R1FieldElement6.isOne();
        if (zIsOne2) {
            iArr4 = secP128R1FieldElement.x;
            iArr3 = secP128R1FieldElement2.x;
        } else {
            iArr3 = iArrCreate3;
            SecP128R1Field.square(secP128R1FieldElement6.x, iArr3);
            iArr4 = iArrCreateExt;
            SecP128R1Field.multiply(iArr3, secP128R1FieldElement.x, iArr4);
            SecP128R1Field.multiply(iArr3, secP128R1FieldElement6.x, iArr3);
            SecP128R1Field.multiply(iArr3, secP128R1FieldElement2.x, iArr3);
        }
        int[] iArrCreate4 = Nat128.create();
        SecP128R1Field.subtract(iArr4, iArr2, iArrCreate4);
        SecP128R1Field.subtract(iArr3, iArr, iArrCreate);
        if (Nat128.isZero(iArrCreate4)) {
            return Nat128.isZero(iArrCreate) ? twice() : curve.getInfinity();
        }
        SecP128R1Field.square(iArrCreate4, iArrCreate2);
        int[] iArrCreate5 = Nat128.create();
        SecP128R1Field.multiply(iArrCreate2, iArrCreate4, iArrCreate5);
        SecP128R1Field.multiply(iArrCreate2, iArr4, iArrCreate2);
        SecP128R1Field.negate(iArrCreate5, iArrCreate5);
        Nat128.mul(iArr3, iArrCreate5, iArrCreateExt);
        SecP128R1Field.reduce32(Nat128.addBothTo(iArrCreate2, iArrCreate2, iArrCreate5), iArrCreate5);
        SecP128R1FieldElement secP128R1FieldElement7 = new SecP128R1FieldElement(iArrCreate3);
        SecP128R1Field.square(iArrCreate, secP128R1FieldElement7.x);
        SecP128R1Field.subtract(secP128R1FieldElement7.x, iArrCreate5, secP128R1FieldElement7.x);
        SecP128R1FieldElement secP128R1FieldElement8 = new SecP128R1FieldElement(iArrCreate5);
        SecP128R1Field.subtract(iArrCreate2, secP128R1FieldElement7.x, secP128R1FieldElement8.x);
        SecP128R1Field.multiplyAddToExt(secP128R1FieldElement8.x, iArrCreate, iArrCreateExt);
        SecP128R1Field.reduce(iArrCreateExt, secP128R1FieldElement8.x);
        SecP128R1FieldElement secP128R1FieldElement9 = new SecP128R1FieldElement(iArrCreate4);
        if (!zIsOne) {
            SecP128R1Field.multiply(secP128R1FieldElement9.x, secP128R1FieldElement5.x, secP128R1FieldElement9.x);
        }
        if (!zIsOne2) {
            SecP128R1Field.multiply(secP128R1FieldElement9.x, secP128R1FieldElement6.x, secP128R1FieldElement9.x);
        }
        return new SecP128R1Point(curve, secP128R1FieldElement7, secP128R1FieldElement8, new ECFieldElement[]{secP128R1FieldElement9});
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    public ECPoint twice() {
        if (isInfinity()) {
            return this;
        }
        ECCurve curve = getCurve();
        SecP128R1FieldElement secP128R1FieldElement = (SecP128R1FieldElement) this.y;
        if (secP128R1FieldElement.isZero()) {
            return curve.getInfinity();
        }
        SecP128R1FieldElement secP128R1FieldElement2 = (SecP128R1FieldElement) this.x;
        SecP128R1FieldElement secP128R1FieldElement3 = (SecP128R1FieldElement) this.zs[0];
        int[] iArrCreate = Nat128.create();
        int[] iArrCreate2 = Nat128.create();
        int[] iArrCreate3 = Nat128.create();
        SecP128R1Field.square(secP128R1FieldElement.x, iArrCreate3);
        int[] iArrCreate4 = Nat128.create();
        SecP128R1Field.square(iArrCreate3, iArrCreate4);
        boolean zIsOne = secP128R1FieldElement3.isOne();
        int[] iArr = secP128R1FieldElement3.x;
        if (!zIsOne) {
            iArr = iArrCreate2;
            SecP128R1Field.square(secP128R1FieldElement3.x, iArr);
        }
        SecP128R1Field.subtract(secP128R1FieldElement2.x, iArr, iArrCreate);
        SecP128R1Field.add(secP128R1FieldElement2.x, iArr, iArrCreate2);
        SecP128R1Field.multiply(iArrCreate2, iArrCreate, iArrCreate2);
        SecP128R1Field.reduce32(Nat128.addBothTo(iArrCreate2, iArrCreate2, iArrCreate2), iArrCreate2);
        SecP128R1Field.multiply(iArrCreate3, secP128R1FieldElement2.x, iArrCreate3);
        SecP128R1Field.reduce32(Nat.shiftUpBits(4, iArrCreate3, 2, 0), iArrCreate3);
        SecP128R1Field.reduce32(Nat.shiftUpBits(4, iArrCreate4, 3, 0, iArrCreate), iArrCreate);
        SecP128R1FieldElement secP128R1FieldElement4 = new SecP128R1FieldElement(iArrCreate4);
        SecP128R1Field.square(iArrCreate2, secP128R1FieldElement4.x);
        SecP128R1Field.subtract(secP128R1FieldElement4.x, iArrCreate3, secP128R1FieldElement4.x);
        SecP128R1Field.subtract(secP128R1FieldElement4.x, iArrCreate3, secP128R1FieldElement4.x);
        SecP128R1FieldElement secP128R1FieldElement5 = new SecP128R1FieldElement(iArrCreate3);
        SecP128R1Field.subtract(iArrCreate3, secP128R1FieldElement4.x, secP128R1FieldElement5.x);
        SecP128R1Field.multiply(secP128R1FieldElement5.x, iArrCreate2, secP128R1FieldElement5.x);
        SecP128R1Field.subtract(secP128R1FieldElement5.x, iArrCreate, secP128R1FieldElement5.x);
        SecP128R1FieldElement secP128R1FieldElement6 = new SecP128R1FieldElement(iArrCreate2);
        SecP128R1Field.twice(secP128R1FieldElement.x, secP128R1FieldElement6.x);
        if (!zIsOne) {
            SecP128R1Field.multiply(secP128R1FieldElement6.x, secP128R1FieldElement3.x, secP128R1FieldElement6.x);
        }
        return new SecP128R1Point(curve, secP128R1FieldElement4, secP128R1FieldElement5, new ECFieldElement[]{secP128R1FieldElement6});
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
        return isInfinity() ? this : new SecP128R1Point(this.curve, this.x, this.y.negate(), this.zs);
    }
}
