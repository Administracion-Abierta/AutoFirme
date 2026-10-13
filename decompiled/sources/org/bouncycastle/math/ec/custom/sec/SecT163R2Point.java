package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/custom/sec/SecT163R2Point.class */
public class SecT163R2Point extends ECPoint.AbstractF2m {
    SecT163R2Point(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        super(eCCurve, eCFieldElement, eCFieldElement2);
    }

    SecT163R2Point(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArr) {
        super(eCCurve, eCFieldElement, eCFieldElement2, eCFieldElementArr);
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    protected ECPoint detach() {
        return new SecT163R2Point(null, getAffineXCoord(), getAffineYCoord());
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    public ECFieldElement getYCoord() {
        ECFieldElement eCFieldElement = this.x;
        ECFieldElement eCFieldElement2 = this.y;
        if (isInfinity() || eCFieldElement.isZero()) {
            return eCFieldElement2;
        }
        ECFieldElement eCFieldElementMultiply = eCFieldElement2.add(eCFieldElement).multiply(eCFieldElement);
        ECFieldElement eCFieldElement3 = this.zs[0];
        if (!eCFieldElement3.isOne()) {
            eCFieldElementMultiply = eCFieldElementMultiply.divide(eCFieldElement3);
        }
        return eCFieldElementMultiply;
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    protected boolean getCompressionYTilde() {
        ECFieldElement rawXCoord = getRawXCoord();
        return (rawXCoord.isZero() || getRawYCoord().testBitZero() == rawXCoord.testBitZero()) ? false : true;
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    public ECPoint add(ECPoint eCPoint) {
        ECFieldElement eCFieldElementMultiply;
        ECFieldElement eCFieldElementSquarePlusProduct;
        ECFieldElement eCFieldElementMultiply2;
        if (isInfinity()) {
            return eCPoint;
        }
        if (eCPoint.isInfinity()) {
            return this;
        }
        ECCurve curve = getCurve();
        ECFieldElement eCFieldElement = this.x;
        ECFieldElement rawXCoord = eCPoint.getRawXCoord();
        if (eCFieldElement.isZero()) {
            return rawXCoord.isZero() ? curve.getInfinity() : eCPoint.add(this);
        }
        ECFieldElement eCFieldElement2 = this.y;
        ECFieldElement eCFieldElement3 = this.zs[0];
        ECFieldElement rawYCoord = eCPoint.getRawYCoord();
        ECFieldElement zCoord = eCPoint.getZCoord(0);
        boolean zIsOne = eCFieldElement3.isOne();
        ECFieldElement eCFieldElementMultiply3 = rawXCoord;
        ECFieldElement eCFieldElementMultiply4 = rawYCoord;
        if (!zIsOne) {
            eCFieldElementMultiply3 = eCFieldElementMultiply3.multiply(eCFieldElement3);
            eCFieldElementMultiply4 = eCFieldElementMultiply4.multiply(eCFieldElement3);
        }
        boolean zIsOne2 = zCoord.isOne();
        ECFieldElement eCFieldElementMultiply5 = eCFieldElement;
        ECFieldElement eCFieldElementMultiply6 = eCFieldElement2;
        if (!zIsOne2) {
            eCFieldElementMultiply5 = eCFieldElementMultiply5.multiply(zCoord);
            eCFieldElementMultiply6 = eCFieldElementMultiply6.multiply(zCoord);
        }
        ECFieldElement eCFieldElementAdd = eCFieldElementMultiply6.add(eCFieldElementMultiply4);
        ECFieldElement eCFieldElementAdd2 = eCFieldElementMultiply5.add(eCFieldElementMultiply3);
        if (eCFieldElementAdd2.isZero()) {
            return eCFieldElementAdd.isZero() ? twice() : curve.getInfinity();
        }
        if (rawXCoord.isZero()) {
            ECPoint eCPointNormalize = normalize();
            ECFieldElement xCoord = eCPointNormalize.getXCoord();
            ECFieldElement yCoord = eCPointNormalize.getYCoord();
            ECFieldElement eCFieldElementDivide = yCoord.add(rawYCoord).divide(xCoord);
            eCFieldElementMultiply = eCFieldElementDivide.square().add(eCFieldElementDivide).add(xCoord).addOne();
            if (eCFieldElementMultiply.isZero()) {
                return new SecT163R2Point(curve, eCFieldElementMultiply, curve.getB().sqrt());
            }
            eCFieldElementSquarePlusProduct = eCFieldElementDivide.multiply(xCoord.add(eCFieldElementMultiply)).add(eCFieldElementMultiply).add(yCoord).divide(eCFieldElementMultiply).add(eCFieldElementMultiply);
            eCFieldElementMultiply2 = curve.fromBigInteger(ECConstants.ONE);
        } else {
            ECFieldElement eCFieldElementSquare = eCFieldElementAdd2.square();
            ECFieldElement eCFieldElementMultiply7 = eCFieldElementAdd.multiply(eCFieldElementMultiply5);
            ECFieldElement eCFieldElementMultiply8 = eCFieldElementAdd.multiply(eCFieldElementMultiply3);
            eCFieldElementMultiply = eCFieldElementMultiply7.multiply(eCFieldElementMultiply8);
            if (eCFieldElementMultiply.isZero()) {
                return new SecT163R2Point(curve, eCFieldElementMultiply, curve.getB().sqrt());
            }
            ECFieldElement eCFieldElementMultiply9 = eCFieldElementAdd.multiply(eCFieldElementSquare);
            if (!zIsOne2) {
                eCFieldElementMultiply9 = eCFieldElementMultiply9.multiply(zCoord);
            }
            eCFieldElementSquarePlusProduct = eCFieldElementMultiply8.add(eCFieldElementSquare).squarePlusProduct(eCFieldElementMultiply9, eCFieldElement2.add(eCFieldElement3));
            eCFieldElementMultiply2 = eCFieldElementMultiply9;
            if (!zIsOne) {
                eCFieldElementMultiply2 = eCFieldElementMultiply2.multiply(eCFieldElement3);
            }
        }
        return new SecT163R2Point(curve, eCFieldElementMultiply, eCFieldElementSquarePlusProduct, new ECFieldElement[]{eCFieldElementMultiply2});
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    public ECPoint twice() {
        if (isInfinity()) {
            return this;
        }
        ECCurve curve = getCurve();
        ECFieldElement eCFieldElement = this.x;
        if (eCFieldElement.isZero()) {
            return curve.getInfinity();
        }
        ECFieldElement eCFieldElement2 = this.y;
        ECFieldElement eCFieldElement3 = this.zs[0];
        boolean zIsOne = eCFieldElement3.isOne();
        ECFieldElement eCFieldElementMultiply = zIsOne ? eCFieldElement2 : eCFieldElement2.multiply(eCFieldElement3);
        ECFieldElement eCFieldElementSquare = zIsOne ? eCFieldElement3 : eCFieldElement3.square();
        ECFieldElement eCFieldElementAdd = eCFieldElement2.square().add(eCFieldElementMultiply).add(eCFieldElementSquare);
        if (eCFieldElementAdd.isZero()) {
            return new SecT163R2Point(curve, eCFieldElementAdd, curve.getB().sqrt());
        }
        ECFieldElement eCFieldElementSquare2 = eCFieldElementAdd.square();
        ECFieldElement eCFieldElementMultiply2 = zIsOne ? eCFieldElementAdd : eCFieldElementAdd.multiply(eCFieldElementSquare);
        return new SecT163R2Point(curve, eCFieldElementSquare2, (zIsOne ? eCFieldElement : eCFieldElement.multiply(eCFieldElement3)).squarePlusProduct(eCFieldElementAdd, eCFieldElementMultiply).add(eCFieldElementSquare2).add(eCFieldElementMultiply2), new ECFieldElement[]{eCFieldElementMultiply2});
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    public ECPoint twicePlus(ECPoint eCPoint) {
        if (isInfinity()) {
            return eCPoint;
        }
        if (eCPoint.isInfinity()) {
            return twice();
        }
        ECCurve curve = getCurve();
        ECFieldElement eCFieldElement = this.x;
        if (eCFieldElement.isZero()) {
            return eCPoint;
        }
        ECFieldElement rawXCoord = eCPoint.getRawXCoord();
        ECFieldElement zCoord = eCPoint.getZCoord(0);
        if (rawXCoord.isZero() || !zCoord.isOne()) {
            return twice().add(eCPoint);
        }
        ECFieldElement eCFieldElement2 = this.y;
        ECFieldElement eCFieldElement3 = this.zs[0];
        ECFieldElement rawYCoord = eCPoint.getRawYCoord();
        ECFieldElement eCFieldElementSquare = eCFieldElement.square();
        ECFieldElement eCFieldElementSquare2 = eCFieldElement2.square();
        ECFieldElement eCFieldElementSquare3 = eCFieldElement3.square();
        ECFieldElement eCFieldElementAdd = eCFieldElementSquare3.add(eCFieldElementSquare2).add(eCFieldElement2.multiply(eCFieldElement3));
        ECFieldElement eCFieldElementMultiplyPlusProduct = rawYCoord.multiply(eCFieldElementSquare3).add(eCFieldElementSquare2).multiplyPlusProduct(eCFieldElementAdd, eCFieldElementSquare, eCFieldElementSquare3);
        ECFieldElement eCFieldElementMultiply = rawXCoord.multiply(eCFieldElementSquare3);
        ECFieldElement eCFieldElementSquare4 = eCFieldElementMultiply.add(eCFieldElementAdd).square();
        if (eCFieldElementSquare4.isZero()) {
            return eCFieldElementMultiplyPlusProduct.isZero() ? eCPoint.twice() : curve.getInfinity();
        }
        if (eCFieldElementMultiplyPlusProduct.isZero()) {
            return new SecT163R2Point(curve, eCFieldElementMultiplyPlusProduct, curve.getB().sqrt());
        }
        ECFieldElement eCFieldElementMultiply2 = eCFieldElementMultiplyPlusProduct.square().multiply(eCFieldElementMultiply);
        ECFieldElement eCFieldElementMultiply3 = eCFieldElementMultiplyPlusProduct.multiply(eCFieldElementSquare4).multiply(eCFieldElementSquare3);
        return new SecT163R2Point(curve, eCFieldElementMultiply2, eCFieldElementMultiplyPlusProduct.add(eCFieldElementSquare4).square().multiplyPlusProduct(eCFieldElementAdd, rawYCoord.addOne(), eCFieldElementMultiply3), new ECFieldElement[]{eCFieldElementMultiply3});
    }

    @Override // org.bouncycastle.math.ec.ECPoint
    public ECPoint negate() {
        if (isInfinity()) {
            return this;
        }
        ECFieldElement eCFieldElement = this.x;
        if (eCFieldElement.isZero()) {
            return this;
        }
        ECFieldElement eCFieldElement2 = this.y;
        ECFieldElement eCFieldElement3 = this.zs[0];
        return new SecT163R2Point(this.curve, eCFieldElement, eCFieldElement2.add(eCFieldElement3), new ECFieldElement[]{eCFieldElement3});
    }
}
