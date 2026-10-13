package org.bouncycastle.math.ec.custom.djb;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat256;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/custom/djb/Curve25519Point.class */
public class Curve25519Point extends ECPoint.AbstractFp {
    Curve25519Point(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        super(eCCurve, eCFieldElement, eCFieldElement2);
    }

    Curve25519Point(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArr) {
        super(eCCurve, eCFieldElement, eCFieldElement2, eCFieldElementArr);
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    protected ECPoint detach() {
        return new Curve25519Point(null, getAffineXCoord(), getAffineYCoord());
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    public ECFieldElement getZCoord(int i) {
        return i == 1 ? getJacobianModifiedW() : super.getZCoord(i);
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
        Curve25519FieldElement curve25519FieldElement = (Curve25519FieldElement) this.x;
        Curve25519FieldElement curve25519FieldElement2 = (Curve25519FieldElement) this.y;
        Curve25519FieldElement curve25519FieldElement3 = (Curve25519FieldElement) this.zs[0];
        Curve25519FieldElement curve25519FieldElement4 = (Curve25519FieldElement) eCPoint.getXCoord();
        Curve25519FieldElement curve25519FieldElement5 = (Curve25519FieldElement) eCPoint.getYCoord();
        Curve25519FieldElement curve25519FieldElement6 = (Curve25519FieldElement) eCPoint.getZCoord(0);
        int[] iArrCreateExt = Nat256.createExt();
        int[] iArrCreate = Nat256.create();
        int[] iArrCreate2 = Nat256.create();
        int[] iArrCreate3 = Nat256.create();
        boolean zIsOne = curve25519FieldElement3.isOne();
        if (zIsOne) {
            iArr2 = curve25519FieldElement4.x;
            iArr = curve25519FieldElement5.x;
        } else {
            iArr = iArrCreate2;
            Curve25519Field.square(curve25519FieldElement3.x, iArr);
            iArr2 = iArrCreate;
            Curve25519Field.multiply(iArr, curve25519FieldElement4.x, iArr2);
            Curve25519Field.multiply(iArr, curve25519FieldElement3.x, iArr);
            Curve25519Field.multiply(iArr, curve25519FieldElement5.x, iArr);
        }
        boolean zIsOne2 = curve25519FieldElement6.isOne();
        if (zIsOne2) {
            iArr4 = curve25519FieldElement.x;
            iArr3 = curve25519FieldElement2.x;
        } else {
            iArr3 = iArrCreate3;
            Curve25519Field.square(curve25519FieldElement6.x, iArr3);
            iArr4 = iArrCreateExt;
            Curve25519Field.multiply(iArr3, curve25519FieldElement.x, iArr4);
            Curve25519Field.multiply(iArr3, curve25519FieldElement6.x, iArr3);
            Curve25519Field.multiply(iArr3, curve25519FieldElement2.x, iArr3);
        }
        int[] iArrCreate4 = Nat256.create();
        Curve25519Field.subtract(iArr4, iArr2, iArrCreate4);
        Curve25519Field.subtract(iArr3, iArr, iArrCreate);
        if (Nat256.isZero(iArrCreate4)) {
            return Nat256.isZero(iArrCreate) ? twice() : curve.getInfinity();
        }
        int[] iArrCreate5 = Nat256.create();
        Curve25519Field.square(iArrCreate4, iArrCreate5);
        int[] iArrCreate6 = Nat256.create();
        Curve25519Field.multiply(iArrCreate5, iArrCreate4, iArrCreate6);
        Curve25519Field.multiply(iArrCreate5, iArr4, iArrCreate2);
        Curve25519Field.negate(iArrCreate6, iArrCreate6);
        Nat256.mul(iArr3, iArrCreate6, iArrCreateExt);
        Curve25519Field.reduce27(Nat256.addBothTo(iArrCreate2, iArrCreate2, iArrCreate6), iArrCreate6);
        Curve25519FieldElement curve25519FieldElement7 = new Curve25519FieldElement(iArrCreate3);
        Curve25519Field.square(iArrCreate, curve25519FieldElement7.x);
        Curve25519Field.subtract(curve25519FieldElement7.x, iArrCreate6, curve25519FieldElement7.x);
        Curve25519FieldElement curve25519FieldElement8 = new Curve25519FieldElement(iArrCreate6);
        Curve25519Field.subtract(iArrCreate2, curve25519FieldElement7.x, curve25519FieldElement8.x);
        Curve25519Field.multiplyAddToExt(curve25519FieldElement8.x, iArrCreate, iArrCreateExt);
        Curve25519Field.reduce(iArrCreateExt, curve25519FieldElement8.x);
        Curve25519FieldElement curve25519FieldElement9 = new Curve25519FieldElement(iArrCreate4);
        if (!zIsOne) {
            Curve25519Field.multiply(curve25519FieldElement9.x, curve25519FieldElement3.x, curve25519FieldElement9.x);
        }
        if (!zIsOne2) {
            Curve25519Field.multiply(curve25519FieldElement9.x, curve25519FieldElement6.x, curve25519FieldElement9.x);
        }
        return new Curve25519Point(curve, curve25519FieldElement7, curve25519FieldElement8, new ECFieldElement[]{curve25519FieldElement9, calculateJacobianModifiedW(curve25519FieldElement9, (zIsOne && zIsOne2) ? iArrCreate5 : null)});
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    public ECPoint twice() {
        if (isInfinity()) {
            return this;
        }
        return this.y.isZero() ? getCurve().getInfinity() : twiceJacobianModified(true);
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
        return this.y.isZero() ? eCPoint : twiceJacobianModified(false).add(eCPoint);
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    public ECPoint threeTimes() {
        if (!isInfinity() && !this.y.isZero()) {
            return twiceJacobianModified(false).add(this);
        }
        return this;
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    public ECPoint negate() {
        return isInfinity() ? this : new Curve25519Point(getCurve(), this.x, this.y.negate(), this.zs);
    }

    protected Curve25519FieldElement calculateJacobianModifiedW(Curve25519FieldElement curve25519FieldElement, int[] iArr) {
        Curve25519FieldElement curve25519FieldElement2 = (Curve25519FieldElement) getCurve().getA();
        if (curve25519FieldElement.isOne()) {
            return curve25519FieldElement2;
        }
        Curve25519FieldElement curve25519FieldElement3 = new Curve25519FieldElement();
        if (iArr == null) {
            iArr = curve25519FieldElement3.x;
            Curve25519Field.square(curve25519FieldElement.x, iArr);
        }
        Curve25519Field.square(iArr, curve25519FieldElement3.x);
        Curve25519Field.multiply(curve25519FieldElement3.x, curve25519FieldElement2.x, curve25519FieldElement3.x);
        return curve25519FieldElement3;
    }

    protected Curve25519FieldElement getJacobianModifiedW() {
        Curve25519FieldElement curve25519FieldElement = (Curve25519FieldElement) this.zs[1];
        if (curve25519FieldElement == null) {
            ECFieldElement[] eCFieldElementArr = this.zs;
            Curve25519FieldElement curve25519FieldElementCalculateJacobianModifiedW = calculateJacobianModifiedW((Curve25519FieldElement) this.zs[0], null);
            curve25519FieldElement = curve25519FieldElementCalculateJacobianModifiedW;
            eCFieldElementArr[1] = curve25519FieldElementCalculateJacobianModifiedW;
        }
        return curve25519FieldElement;
    }

    protected Curve25519Point twiceJacobianModified(boolean z) {
        Curve25519FieldElement curve25519FieldElement = (Curve25519FieldElement) this.x;
        Curve25519FieldElement curve25519FieldElement2 = (Curve25519FieldElement) this.y;
        Curve25519FieldElement curve25519FieldElement3 = (Curve25519FieldElement) this.zs[0];
        Curve25519FieldElement jacobianModifiedW = getJacobianModifiedW();
        int[] iArrCreate = Nat256.create();
        Curve25519Field.square(curve25519FieldElement.x, iArrCreate);
        Curve25519Field.reduce27(Nat256.addBothTo(iArrCreate, iArrCreate, iArrCreate) + Nat256.addTo(jacobianModifiedW.x, iArrCreate), iArrCreate);
        int[] iArrCreate2 = Nat256.create();
        Curve25519Field.twice(curve25519FieldElement2.x, iArrCreate2);
        int[] iArrCreate3 = Nat256.create();
        Curve25519Field.multiply(iArrCreate2, curve25519FieldElement2.x, iArrCreate3);
        int[] iArrCreate4 = Nat256.create();
        Curve25519Field.multiply(iArrCreate3, curve25519FieldElement.x, iArrCreate4);
        Curve25519Field.twice(iArrCreate4, iArrCreate4);
        int[] iArrCreate5 = Nat256.create();
        Curve25519Field.square(iArrCreate3, iArrCreate5);
        Curve25519Field.twice(iArrCreate5, iArrCreate5);
        Curve25519FieldElement curve25519FieldElement4 = new Curve25519FieldElement(iArrCreate3);
        Curve25519Field.square(iArrCreate, curve25519FieldElement4.x);
        Curve25519Field.subtract(curve25519FieldElement4.x, iArrCreate4, curve25519FieldElement4.x);
        Curve25519Field.subtract(curve25519FieldElement4.x, iArrCreate4, curve25519FieldElement4.x);
        Curve25519FieldElement curve25519FieldElement5 = new Curve25519FieldElement(iArrCreate4);
        Curve25519Field.subtract(iArrCreate4, curve25519FieldElement4.x, curve25519FieldElement5.x);
        Curve25519Field.multiply(curve25519FieldElement5.x, iArrCreate, curve25519FieldElement5.x);
        Curve25519Field.subtract(curve25519FieldElement5.x, iArrCreate5, curve25519FieldElement5.x);
        Curve25519FieldElement curve25519FieldElement6 = new Curve25519FieldElement(iArrCreate2);
        if (!Nat256.isOne(curve25519FieldElement3.x)) {
            Curve25519Field.multiply(curve25519FieldElement6.x, curve25519FieldElement3.x, curve25519FieldElement6.x);
        }
        Curve25519FieldElement curve25519FieldElement7 = null;
        if (z) {
            curve25519FieldElement7 = new Curve25519FieldElement(iArrCreate5);
            Curve25519Field.multiply(curve25519FieldElement7.x, jacobianModifiedW.x, curve25519FieldElement7.x);
            Curve25519Field.twice(curve25519FieldElement7.x, curve25519FieldElement7.x);
        }
        return new Curve25519Point(getCurve(), curve25519FieldElement4, curve25519FieldElement5, new ECFieldElement[]{curve25519FieldElement6, curve25519FieldElement7});
    }
}
