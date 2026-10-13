package org.bouncycastle.math.ec;

import java.math.BigInteger;
import java.util.Hashtable;
import org.bouncycastle.crypto.CryptoServicesRegistrar;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECPoint.class */
public abstract class ECPoint {
    protected static final ECFieldElement[] EMPTY_ZS = new ECFieldElement[0];
    protected ECCurve curve;
    protected ECFieldElement x;
    protected ECFieldElement y;
    protected ECFieldElement[] zs;
    protected Hashtable preCompTable;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECPoint$AbstractF2m.class */
    public static abstract class AbstractF2m extends ECPoint {
        protected AbstractF2m(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
            super(eCCurve, eCFieldElement, eCFieldElement2);
        }

        protected AbstractF2m(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArr) {
            super(eCCurve, eCFieldElement, eCFieldElement2, eCFieldElementArr);
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        protected boolean satisfiesCurveEquation() {
            ECFieldElement eCFieldElementMultiplyPlusProduct;
            ECFieldElement eCFieldElementSquarePlusProduct;
            ECCurve curve = getCurve();
            ECFieldElement eCFieldElement = this.x;
            ECFieldElement a = curve.getA();
            ECFieldElement b = curve.getB();
            int coordinateSystem = curve.getCoordinateSystem();
            if (coordinateSystem != 6) {
                ECFieldElement eCFieldElement2 = this.y;
                ECFieldElement eCFieldElementMultiply = eCFieldElement2.add(eCFieldElement).multiply(eCFieldElement2);
                switch (coordinateSystem) {
                    case 0:
                        break;
                    case 1:
                        ECFieldElement eCFieldElement3 = this.zs[0];
                        if (!eCFieldElement3.isOne()) {
                            ECFieldElement eCFieldElementMultiply2 = eCFieldElement3.multiply(eCFieldElement3.square());
                            eCFieldElementMultiply = eCFieldElementMultiply.multiply(eCFieldElement3);
                            a = a.multiply(eCFieldElement3);
                            b = b.multiply(eCFieldElementMultiply2);
                        }
                        break;
                    default:
                        throw new IllegalStateException("unsupported coordinate system");
                }
                return eCFieldElementMultiply.equals(eCFieldElement.add(a).multiply(eCFieldElement.square()).add(b));
            }
            ECFieldElement eCFieldElement4 = this.zs[0];
            boolean zIsOne = eCFieldElement4.isOne();
            if (eCFieldElement.isZero()) {
                ECFieldElement eCFieldElementSquare = this.y.square();
                ECFieldElement eCFieldElementMultiply3 = b;
                if (!zIsOne) {
                    eCFieldElementMultiply3 = eCFieldElementMultiply3.multiply(eCFieldElement4.square());
                }
                return eCFieldElementSquare.equals(eCFieldElementMultiply3);
            }
            ECFieldElement eCFieldElement5 = this.y;
            ECFieldElement eCFieldElementSquare2 = eCFieldElement.square();
            if (zIsOne) {
                eCFieldElementMultiplyPlusProduct = eCFieldElement5.square().add(eCFieldElement5).add(a);
                eCFieldElementSquarePlusProduct = eCFieldElementSquare2.square().add(b);
            } else {
                ECFieldElement eCFieldElementSquare3 = eCFieldElement4.square();
                ECFieldElement eCFieldElementSquare4 = eCFieldElementSquare3.square();
                eCFieldElementMultiplyPlusProduct = eCFieldElement5.add(eCFieldElement4).multiplyPlusProduct(eCFieldElement5, a, eCFieldElementSquare3);
                eCFieldElementSquarePlusProduct = eCFieldElementSquare2.squarePlusProduct(b, eCFieldElementSquare4);
            }
            return eCFieldElementMultiplyPlusProduct.multiply(eCFieldElementSquare2).equals(eCFieldElementSquarePlusProduct);
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        protected boolean satisfiesOrder() {
            BigInteger cofactor = this.curve.getCofactor();
            if (ECConstants.TWO.equals(cofactor)) {
                return 0 != ((ECFieldElement.AbstractF2m) normalize().getAffineXCoord()).trace();
            }
            if (!ECConstants.FOUR.equals(cofactor)) {
                return super.satisfiesOrder();
            }
            ECPoint eCPointNormalize = normalize();
            ECFieldElement affineXCoord = eCPointNormalize.getAffineXCoord();
            ECFieldElement eCFieldElementSolveQuadraticEquation = ((ECCurve.AbstractF2m) this.curve).solveQuadraticEquation(affineXCoord.add(this.curve.getA()));
            if (null == eCFieldElementSolveQuadraticEquation) {
                return false;
            }
            return 0 == ((ECFieldElement.AbstractF2m) affineXCoord.multiply(eCFieldElementSolveQuadraticEquation).add(eCPointNormalize.getAffineYCoord())).trace();
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        public ECPoint scaleX(ECFieldElement eCFieldElement) {
            if (isInfinity()) {
                return this;
            }
            switch (getCurveCoordinateSystem()) {
                case 5:
                    ECFieldElement rawXCoord = getRawXCoord();
                    return getCurve().createRawPoint(rawXCoord, getRawYCoord().add(rawXCoord).divide(eCFieldElement).add(rawXCoord.multiply(eCFieldElement)), getRawZCoords());
                case 6:
                    ECFieldElement rawXCoord2 = getRawXCoord();
                    ECFieldElement rawYCoord = getRawYCoord();
                    ECFieldElement eCFieldElement2 = getRawZCoords()[0];
                    ECFieldElement eCFieldElementMultiply = rawXCoord2.multiply(eCFieldElement.square());
                    return getCurve().createRawPoint(eCFieldElementMultiply, rawYCoord.add(rawXCoord2).add(eCFieldElementMultiply), new ECFieldElement[]{eCFieldElement2.multiply(eCFieldElement)});
                default:
                    return super.scaleX(eCFieldElement);
            }
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        public ECPoint scaleXNegateY(ECFieldElement eCFieldElement) {
            return scaleX(eCFieldElement);
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        public ECPoint scaleY(ECFieldElement eCFieldElement) {
            if (isInfinity()) {
                return this;
            }
            switch (getCurveCoordinateSystem()) {
                case 5:
                case 6:
                    ECFieldElement rawXCoord = getRawXCoord();
                    return getCurve().createRawPoint(rawXCoord, getRawYCoord().add(rawXCoord).multiply(eCFieldElement).add(rawXCoord), getRawZCoords());
                default:
                    return super.scaleY(eCFieldElement);
            }
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        public ECPoint scaleYNegateX(ECFieldElement eCFieldElement) {
            return scaleY(eCFieldElement);
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        public ECPoint subtract(ECPoint eCPoint) {
            return eCPoint.isInfinity() ? this : add(eCPoint.negate());
        }

        public AbstractF2m tau() {
            if (isInfinity()) {
                return this;
            }
            ECCurve curve = getCurve();
            int coordinateSystem = curve.getCoordinateSystem();
            ECFieldElement eCFieldElement = this.x;
            switch (coordinateSystem) {
                case 0:
                case 5:
                    return (AbstractF2m) curve.createRawPoint(eCFieldElement.square(), this.y.square());
                case 1:
                case 6:
                    return (AbstractF2m) curve.createRawPoint(eCFieldElement.square(), this.y.square(), new ECFieldElement[]{this.zs[0].square()});
                case 2:
                case 3:
                case 4:
                default:
                    throw new IllegalStateException("unsupported coordinate system");
            }
        }

        public AbstractF2m tauPow(int i) {
            if (isInfinity()) {
                return this;
            }
            ECCurve curve = getCurve();
            int coordinateSystem = curve.getCoordinateSystem();
            ECFieldElement eCFieldElement = this.x;
            switch (coordinateSystem) {
                case 0:
                case 5:
                    return (AbstractF2m) curve.createRawPoint(eCFieldElement.squarePow(i), this.y.squarePow(i));
                case 1:
                case 6:
                    return (AbstractF2m) curve.createRawPoint(eCFieldElement.squarePow(i), this.y.squarePow(i), new ECFieldElement[]{this.zs[0].squarePow(i)});
                case 2:
                case 3:
                case 4:
                default:
                    throw new IllegalStateException("unsupported coordinate system");
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECPoint$AbstractFp.class */
    public static abstract class AbstractFp extends ECPoint {
        protected AbstractFp(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
            super(eCCurve, eCFieldElement, eCFieldElement2);
        }

        protected AbstractFp(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArr) {
            super(eCCurve, eCFieldElement, eCFieldElement2, eCFieldElementArr);
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        protected boolean getCompressionYTilde() {
            return getAffineYCoord().testBitZero();
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        protected boolean satisfiesCurveEquation() {
            ECFieldElement eCFieldElement = this.x;
            ECFieldElement eCFieldElement2 = this.y;
            ECFieldElement a = this.curve.getA();
            ECFieldElement b = this.curve.getB();
            ECFieldElement eCFieldElementSquare = eCFieldElement2.square();
            switch (getCurveCoordinateSystem()) {
                case 0:
                    break;
                case 1:
                    ECFieldElement eCFieldElement3 = this.zs[0];
                    if (!eCFieldElement3.isOne()) {
                        ECFieldElement eCFieldElementSquare2 = eCFieldElement3.square();
                        ECFieldElement eCFieldElementMultiply = eCFieldElement3.multiply(eCFieldElementSquare2);
                        eCFieldElementSquare = eCFieldElementSquare.multiply(eCFieldElement3);
                        a = a.multiply(eCFieldElementSquare2);
                        b = b.multiply(eCFieldElementMultiply);
                    }
                    break;
                case 2:
                case 3:
                case 4:
                    ECFieldElement eCFieldElement4 = this.zs[0];
                    if (!eCFieldElement4.isOne()) {
                        ECFieldElement eCFieldElementSquare3 = eCFieldElement4.square();
                        ECFieldElement eCFieldElementSquare4 = eCFieldElementSquare3.square();
                        ECFieldElement eCFieldElementMultiply2 = eCFieldElementSquare3.multiply(eCFieldElementSquare4);
                        a = a.multiply(eCFieldElementSquare4);
                        b = b.multiply(eCFieldElementMultiply2);
                    }
                    break;
                default:
                    throw new IllegalStateException("unsupported coordinate system");
            }
            return eCFieldElementSquare.equals(eCFieldElement.square().add(a).multiply(eCFieldElement).add(b));
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        public ECPoint subtract(ECPoint eCPoint) {
            return eCPoint.isInfinity() ? this : add(eCPoint.negate());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECPoint$F2m.class */
    public static class F2m extends AbstractF2m {
        F2m(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
            super(eCCurve, eCFieldElement, eCFieldElement2);
        }

        F2m(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArr) {
            super(eCCurve, eCFieldElement, eCFieldElement2, eCFieldElementArr);
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        protected ECPoint detach() {
            return new F2m(null, getAffineXCoord(), getAffineYCoord());
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        public ECFieldElement getYCoord() {
            int curveCoordinateSystem = getCurveCoordinateSystem();
            switch (curveCoordinateSystem) {
                case 5:
                case 6:
                    ECFieldElement eCFieldElement = this.x;
                    ECFieldElement eCFieldElement2 = this.y;
                    if (isInfinity() || eCFieldElement.isZero()) {
                        return eCFieldElement2;
                    }
                    ECFieldElement eCFieldElementMultiply = eCFieldElement2.add(eCFieldElement).multiply(eCFieldElement);
                    if (6 == curveCoordinateSystem) {
                        ECFieldElement eCFieldElement3 = this.zs[0];
                        if (!eCFieldElement3.isOne()) {
                            eCFieldElementMultiply = eCFieldElementMultiply.divide(eCFieldElement3);
                        }
                    }
                    return eCFieldElementMultiply;
                default:
                    return this.y;
            }
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        protected boolean getCompressionYTilde() {
            ECFieldElement rawXCoord = getRawXCoord();
            if (rawXCoord.isZero()) {
                return false;
            }
            ECFieldElement rawYCoord = getRawYCoord();
            switch (getCurveCoordinateSystem()) {
                case 5:
                case 6:
                    return rawYCoord.testBitZero() != rawXCoord.testBitZero();
                default:
                    return rawYCoord.divide(rawXCoord).testBitZero();
            }
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
            int coordinateSystem = curve.getCoordinateSystem();
            ECFieldElement eCFieldElement = this.x;
            ECFieldElement eCFieldElement2 = eCPoint.x;
            switch (coordinateSystem) {
                case 0:
                    ECFieldElement eCFieldElement3 = this.y;
                    ECFieldElement eCFieldElement4 = eCPoint.y;
                    ECFieldElement eCFieldElementAdd = eCFieldElement.add(eCFieldElement2);
                    ECFieldElement eCFieldElementAdd2 = eCFieldElement3.add(eCFieldElement4);
                    if (eCFieldElementAdd.isZero()) {
                        return eCFieldElementAdd2.isZero() ? twice() : curve.getInfinity();
                    }
                    ECFieldElement eCFieldElementDivide = eCFieldElementAdd2.divide(eCFieldElementAdd);
                    ECFieldElement eCFieldElementAdd3 = eCFieldElementDivide.square().add(eCFieldElementDivide).add(eCFieldElementAdd).add(curve.getA());
                    return new F2m(curve, eCFieldElementAdd3, eCFieldElementDivide.multiply(eCFieldElement.add(eCFieldElementAdd3)).add(eCFieldElementAdd3).add(eCFieldElement3));
                case 1:
                    ECFieldElement eCFieldElement5 = this.y;
                    ECFieldElement eCFieldElement6 = this.zs[0];
                    ECFieldElement eCFieldElement7 = eCPoint.y;
                    ECFieldElement eCFieldElement8 = eCPoint.zs[0];
                    boolean zIsOne = eCFieldElement8.isOne();
                    ECFieldElement eCFieldElementAdd4 = eCFieldElement6.multiply(eCFieldElement7).add(zIsOne ? eCFieldElement5 : eCFieldElement5.multiply(eCFieldElement8));
                    ECFieldElement eCFieldElementAdd5 = eCFieldElement6.multiply(eCFieldElement2).add(zIsOne ? eCFieldElement : eCFieldElement.multiply(eCFieldElement8));
                    if (eCFieldElementAdd5.isZero()) {
                        return eCFieldElementAdd4.isZero() ? twice() : curve.getInfinity();
                    }
                    ECFieldElement eCFieldElementSquare = eCFieldElementAdd5.square();
                    ECFieldElement eCFieldElementMultiply3 = eCFieldElementSquare.multiply(eCFieldElementAdd5);
                    ECFieldElement eCFieldElementMultiply4 = zIsOne ? eCFieldElement6 : eCFieldElement6.multiply(eCFieldElement8);
                    ECFieldElement eCFieldElementAdd6 = eCFieldElementAdd4.add(eCFieldElementAdd5);
                    ECFieldElement eCFieldElementAdd7 = eCFieldElementAdd6.multiplyPlusProduct(eCFieldElementAdd4, eCFieldElementSquare, curve.getA()).multiply(eCFieldElementMultiply4).add(eCFieldElementMultiply3);
                    return new F2m(curve, eCFieldElementAdd5.multiply(eCFieldElementAdd7), eCFieldElementAdd4.multiplyPlusProduct(eCFieldElement, eCFieldElementAdd5, eCFieldElement5).multiplyPlusProduct(zIsOne ? eCFieldElementSquare : eCFieldElementSquare.multiply(eCFieldElement8), eCFieldElementAdd6, eCFieldElementAdd7), new ECFieldElement[]{eCFieldElementMultiply3.multiply(eCFieldElementMultiply4)});
                case 6:
                    if (eCFieldElement.isZero()) {
                        return eCFieldElement2.isZero() ? curve.getInfinity() : eCPoint.add(this);
                    }
                    ECFieldElement eCFieldElement9 = this.y;
                    ECFieldElement eCFieldElement10 = this.zs[0];
                    ECFieldElement eCFieldElement11 = eCPoint.y;
                    ECFieldElement eCFieldElement12 = eCPoint.zs[0];
                    boolean zIsOne2 = eCFieldElement10.isOne();
                    ECFieldElement eCFieldElementMultiply5 = eCFieldElement2;
                    ECFieldElement eCFieldElementMultiply6 = eCFieldElement11;
                    if (!zIsOne2) {
                        eCFieldElementMultiply5 = eCFieldElementMultiply5.multiply(eCFieldElement10);
                        eCFieldElementMultiply6 = eCFieldElementMultiply6.multiply(eCFieldElement10);
                    }
                    boolean zIsOne3 = eCFieldElement12.isOne();
                    ECFieldElement eCFieldElementMultiply7 = eCFieldElement;
                    ECFieldElement eCFieldElementMultiply8 = eCFieldElement9;
                    if (!zIsOne3) {
                        eCFieldElementMultiply7 = eCFieldElementMultiply7.multiply(eCFieldElement12);
                        eCFieldElementMultiply8 = eCFieldElementMultiply8.multiply(eCFieldElement12);
                    }
                    ECFieldElement eCFieldElementAdd8 = eCFieldElementMultiply8.add(eCFieldElementMultiply6);
                    ECFieldElement eCFieldElementAdd9 = eCFieldElementMultiply7.add(eCFieldElementMultiply5);
                    if (eCFieldElementAdd9.isZero()) {
                        return eCFieldElementAdd8.isZero() ? twice() : curve.getInfinity();
                    }
                    if (eCFieldElement2.isZero()) {
                        ECPoint eCPointNormalize = normalize();
                        ECFieldElement xCoord = eCPointNormalize.getXCoord();
                        ECFieldElement yCoord = eCPointNormalize.getYCoord();
                        ECFieldElement eCFieldElementDivide2 = yCoord.add(eCFieldElement11).divide(xCoord);
                        eCFieldElementMultiply = eCFieldElementDivide2.square().add(eCFieldElementDivide2).add(xCoord).add(curve.getA());
                        if (eCFieldElementMultiply.isZero()) {
                            return new F2m(curve, eCFieldElementMultiply, curve.getB().sqrt());
                        }
                        eCFieldElementSquarePlusProduct = eCFieldElementDivide2.multiply(xCoord.add(eCFieldElementMultiply)).add(eCFieldElementMultiply).add(yCoord).divide(eCFieldElementMultiply).add(eCFieldElementMultiply);
                        eCFieldElementMultiply2 = curve.fromBigInteger(ECConstants.ONE);
                    } else {
                        ECFieldElement eCFieldElementSquare2 = eCFieldElementAdd9.square();
                        ECFieldElement eCFieldElementMultiply9 = eCFieldElementAdd8.multiply(eCFieldElementMultiply7);
                        ECFieldElement eCFieldElementMultiply10 = eCFieldElementAdd8.multiply(eCFieldElementMultiply5);
                        eCFieldElementMultiply = eCFieldElementMultiply9.multiply(eCFieldElementMultiply10);
                        if (eCFieldElementMultiply.isZero()) {
                            return new F2m(curve, eCFieldElementMultiply, curve.getB().sqrt());
                        }
                        ECFieldElement eCFieldElementMultiply11 = eCFieldElementAdd8.multiply(eCFieldElementSquare2);
                        if (!zIsOne3) {
                            eCFieldElementMultiply11 = eCFieldElementMultiply11.multiply(eCFieldElement12);
                        }
                        eCFieldElementSquarePlusProduct = eCFieldElementMultiply10.add(eCFieldElementSquare2).squarePlusProduct(eCFieldElementMultiply11, eCFieldElement9.add(eCFieldElement10));
                        eCFieldElementMultiply2 = eCFieldElementMultiply11;
                        if (!zIsOne2) {
                            eCFieldElementMultiply2 = eCFieldElementMultiply2.multiply(eCFieldElement10);
                        }
                    }
                    return new F2m(curve, eCFieldElementMultiply, eCFieldElementSquarePlusProduct, new ECFieldElement[]{eCFieldElementMultiply2});
                default:
                    throw new IllegalStateException("unsupported coordinate system");
            }
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        public ECPoint twice() {
            ECFieldElement eCFieldElementAdd;
            if (isInfinity()) {
                return this;
            }
            ECCurve curve = getCurve();
            ECFieldElement eCFieldElement = this.x;
            if (eCFieldElement.isZero()) {
                return curve.getInfinity();
            }
            switch (curve.getCoordinateSystem()) {
                case 0:
                    ECFieldElement eCFieldElementAdd2 = this.y.divide(eCFieldElement).add(eCFieldElement);
                    ECFieldElement eCFieldElementAdd3 = eCFieldElementAdd2.square().add(eCFieldElementAdd2).add(curve.getA());
                    return new F2m(curve, eCFieldElementAdd3, eCFieldElement.squarePlusProduct(eCFieldElementAdd3, eCFieldElementAdd2.addOne()));
                case 1:
                    ECFieldElement eCFieldElement2 = this.y;
                    ECFieldElement eCFieldElement3 = this.zs[0];
                    boolean zIsOne = eCFieldElement3.isOne();
                    ECFieldElement eCFieldElementMultiply = zIsOne ? eCFieldElement : eCFieldElement.multiply(eCFieldElement3);
                    ECFieldElement eCFieldElementMultiply2 = zIsOne ? eCFieldElement2 : eCFieldElement2.multiply(eCFieldElement3);
                    ECFieldElement eCFieldElementSquare = eCFieldElement.square();
                    ECFieldElement eCFieldElementAdd4 = eCFieldElementSquare.add(eCFieldElementMultiply2);
                    ECFieldElement eCFieldElementSquare2 = eCFieldElementMultiply.square();
                    ECFieldElement eCFieldElementAdd5 = eCFieldElementAdd4.add(eCFieldElementMultiply);
                    ECFieldElement eCFieldElementMultiplyPlusProduct = eCFieldElementAdd5.multiplyPlusProduct(eCFieldElementAdd4, eCFieldElementSquare2, curve.getA());
                    return new F2m(curve, eCFieldElementMultiply.multiply(eCFieldElementMultiplyPlusProduct), eCFieldElementSquare.square().multiplyPlusProduct(eCFieldElementMultiply, eCFieldElementMultiplyPlusProduct, eCFieldElementAdd5), new ECFieldElement[]{eCFieldElementMultiply.multiply(eCFieldElementSquare2)});
                case 6:
                    ECFieldElement eCFieldElement4 = this.y;
                    ECFieldElement eCFieldElement5 = this.zs[0];
                    boolean zIsOne2 = eCFieldElement5.isOne();
                    ECFieldElement eCFieldElementMultiply3 = zIsOne2 ? eCFieldElement4 : eCFieldElement4.multiply(eCFieldElement5);
                    ECFieldElement eCFieldElementSquare3 = zIsOne2 ? eCFieldElement5 : eCFieldElement5.square();
                    ECFieldElement a = curve.getA();
                    ECFieldElement eCFieldElementMultiply4 = zIsOne2 ? a : a.multiply(eCFieldElementSquare3);
                    ECFieldElement eCFieldElementAdd6 = eCFieldElement4.square().add(eCFieldElementMultiply3).add(eCFieldElementMultiply4);
                    if (eCFieldElementAdd6.isZero()) {
                        return new F2m(curve, eCFieldElementAdd6, curve.getB().sqrt());
                    }
                    ECFieldElement eCFieldElementSquare4 = eCFieldElementAdd6.square();
                    ECFieldElement eCFieldElementMultiply5 = zIsOne2 ? eCFieldElementAdd6 : eCFieldElementAdd6.multiply(eCFieldElementSquare3);
                    ECFieldElement b = curve.getB();
                    if (b.bitLength() < (curve.getFieldSize() >> 1)) {
                        ECFieldElement eCFieldElementSquare5 = eCFieldElement4.add(eCFieldElement).square();
                        eCFieldElementAdd = eCFieldElementSquare5.add(eCFieldElementAdd6).add(eCFieldElementSquare3).multiply(eCFieldElementSquare5).add(b.isOne() ? eCFieldElementMultiply4.add(eCFieldElementSquare3).square() : eCFieldElementMultiply4.squarePlusProduct(b, eCFieldElementSquare3.square())).add(eCFieldElementSquare4);
                        if (a.isZero()) {
                            eCFieldElementAdd = eCFieldElementAdd.add(eCFieldElementMultiply5);
                        } else if (!a.isOne()) {
                            eCFieldElementAdd = eCFieldElementAdd.add(a.addOne().multiply(eCFieldElementMultiply5));
                        }
                    } else {
                        eCFieldElementAdd = (zIsOne2 ? eCFieldElement : eCFieldElement.multiply(eCFieldElement5)).squarePlusProduct(eCFieldElementAdd6, eCFieldElementMultiply3).add(eCFieldElementSquare4).add(eCFieldElementMultiply5);
                    }
                    return new F2m(curve, eCFieldElementSquare4, eCFieldElementAdd, new ECFieldElement[]{eCFieldElementMultiply5});
                default:
                    throw new IllegalStateException("unsupported coordinate system");
            }
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
            switch (curve.getCoordinateSystem()) {
                case 6:
                    ECFieldElement eCFieldElement2 = eCPoint.x;
                    ECFieldElement eCFieldElement3 = eCPoint.zs[0];
                    if (eCFieldElement2.isZero() || !eCFieldElement3.isOne()) {
                        return twice().add(eCPoint);
                    }
                    ECFieldElement eCFieldElement4 = this.y;
                    ECFieldElement eCFieldElement5 = this.zs[0];
                    ECFieldElement eCFieldElement6 = eCPoint.y;
                    ECFieldElement eCFieldElementSquare = eCFieldElement.square();
                    ECFieldElement eCFieldElementSquare2 = eCFieldElement4.square();
                    ECFieldElement eCFieldElementSquare3 = eCFieldElement5.square();
                    ECFieldElement eCFieldElementAdd = curve.getA().multiply(eCFieldElementSquare3).add(eCFieldElementSquare2).add(eCFieldElement4.multiply(eCFieldElement5));
                    ECFieldElement eCFieldElementAddOne = eCFieldElement6.addOne();
                    ECFieldElement eCFieldElementMultiplyPlusProduct = curve.getA().add(eCFieldElementAddOne).multiply(eCFieldElementSquare3).add(eCFieldElementSquare2).multiplyPlusProduct(eCFieldElementAdd, eCFieldElementSquare, eCFieldElementSquare3);
                    ECFieldElement eCFieldElementMultiply = eCFieldElement2.multiply(eCFieldElementSquare3);
                    ECFieldElement eCFieldElementSquare4 = eCFieldElementMultiply.add(eCFieldElementAdd).square();
                    if (eCFieldElementSquare4.isZero()) {
                        return eCFieldElementMultiplyPlusProduct.isZero() ? eCPoint.twice() : curve.getInfinity();
                    }
                    if (eCFieldElementMultiplyPlusProduct.isZero()) {
                        return new F2m(curve, eCFieldElementMultiplyPlusProduct, curve.getB().sqrt());
                    }
                    ECFieldElement eCFieldElementMultiply2 = eCFieldElementMultiplyPlusProduct.square().multiply(eCFieldElementMultiply);
                    ECFieldElement eCFieldElementMultiply3 = eCFieldElementMultiplyPlusProduct.multiply(eCFieldElementSquare4).multiply(eCFieldElementSquare3);
                    return new F2m(curve, eCFieldElementMultiply2, eCFieldElementMultiplyPlusProduct.add(eCFieldElementSquare4).square().multiplyPlusProduct(eCFieldElementAdd, eCFieldElementAddOne, eCFieldElementMultiply3), new ECFieldElement[]{eCFieldElementMultiply3});
                default:
                    return twice().add(eCPoint);
            }
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
            switch (getCurveCoordinateSystem()) {
                case 0:
                    return new F2m(this.curve, eCFieldElement, this.y.add(eCFieldElement));
                case 1:
                    return new F2m(this.curve, eCFieldElement, this.y.add(eCFieldElement), new ECFieldElement[]{this.zs[0]});
                case 2:
                case 3:
                case 4:
                default:
                    throw new IllegalStateException("unsupported coordinate system");
                case 5:
                    return new F2m(this.curve, eCFieldElement, this.y.addOne());
                case 6:
                    ECFieldElement eCFieldElement2 = this.y;
                    ECFieldElement eCFieldElement3 = this.zs[0];
                    return new F2m(this.curve, eCFieldElement, eCFieldElement2.add(eCFieldElement3), new ECFieldElement[]{eCFieldElement3});
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/ECPoint$Fp.class */
    public static class Fp extends AbstractFp {
        Fp(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
            super(eCCurve, eCFieldElement, eCFieldElement2);
        }

        Fp(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArr) {
            super(eCCurve, eCFieldElement, eCFieldElement2, eCFieldElementArr);
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        protected ECPoint detach() {
            return new Fp(null, getAffineXCoord(), getAffineYCoord());
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        public ECFieldElement getZCoord(int i) {
            return (i == 1 && 4 == getCurveCoordinateSystem()) ? getJacobianModifiedW() : super.getZCoord(i);
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        public ECPoint add(ECPoint eCPoint) {
            ECFieldElement eCFieldElementMultiply;
            ECFieldElement eCFieldElementMultiply2;
            ECFieldElement eCFieldElementMultiply3;
            ECFieldElement eCFieldElementMultiply4;
            ECFieldElement eCFieldElementSubtract;
            ECFieldElement eCFieldElementMultiplyMinusProduct;
            ECFieldElement eCFieldElementMultiply5;
            ECFieldElement eCFieldElementMultiply6;
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
            int coordinateSystem = curve.getCoordinateSystem();
            ECFieldElement eCFieldElement = this.x;
            ECFieldElement eCFieldElement2 = this.y;
            ECFieldElement eCFieldElement3 = eCPoint.x;
            ECFieldElement eCFieldElement4 = eCPoint.y;
            switch (coordinateSystem) {
                case 0:
                    ECFieldElement eCFieldElementSubtract2 = eCFieldElement3.subtract(eCFieldElement);
                    ECFieldElement eCFieldElementSubtract3 = eCFieldElement4.subtract(eCFieldElement2);
                    if (eCFieldElementSubtract2.isZero()) {
                        return eCFieldElementSubtract3.isZero() ? twice() : curve.getInfinity();
                    }
                    ECFieldElement eCFieldElementDivide = eCFieldElementSubtract3.divide(eCFieldElementSubtract2);
                    ECFieldElement eCFieldElementSubtract4 = eCFieldElementDivide.square().subtract(eCFieldElement).subtract(eCFieldElement3);
                    return new Fp(curve, eCFieldElementSubtract4, eCFieldElementDivide.multiply(eCFieldElement.subtract(eCFieldElementSubtract4)).subtract(eCFieldElement2));
                case 1:
                    ECFieldElement eCFieldElement5 = this.zs[0];
                    ECFieldElement eCFieldElement6 = eCPoint.zs[0];
                    boolean zIsOne = eCFieldElement5.isOne();
                    boolean zIsOne2 = eCFieldElement6.isOne();
                    ECFieldElement eCFieldElementMultiply7 = zIsOne ? eCFieldElement4 : eCFieldElement4.multiply(eCFieldElement5);
                    ECFieldElement eCFieldElementMultiply8 = zIsOne2 ? eCFieldElement2 : eCFieldElement2.multiply(eCFieldElement6);
                    ECFieldElement eCFieldElementSubtract5 = eCFieldElementMultiply7.subtract(eCFieldElementMultiply8);
                    ECFieldElement eCFieldElementMultiply9 = zIsOne ? eCFieldElement3 : eCFieldElement3.multiply(eCFieldElement5);
                    ECFieldElement eCFieldElementMultiply10 = zIsOne2 ? eCFieldElement : eCFieldElement.multiply(eCFieldElement6);
                    ECFieldElement eCFieldElementSubtract6 = eCFieldElementMultiply9.subtract(eCFieldElementMultiply10);
                    if (eCFieldElementSubtract6.isZero()) {
                        return eCFieldElementSubtract5.isZero() ? twice() : curve.getInfinity();
                    }
                    if (zIsOne) {
                        eCFieldElementMultiply6 = eCFieldElement6;
                    } else {
                        eCFieldElementMultiply6 = zIsOne2 ? eCFieldElement5 : eCFieldElement5.multiply(eCFieldElement6);
                    }
                    ECFieldElement eCFieldElement7 = eCFieldElementMultiply6;
                    ECFieldElement eCFieldElementSquare = eCFieldElementSubtract6.square();
                    ECFieldElement eCFieldElementMultiply11 = eCFieldElementSquare.multiply(eCFieldElementSubtract6);
                    ECFieldElement eCFieldElementMultiply12 = eCFieldElementSquare.multiply(eCFieldElementMultiply10);
                    ECFieldElement eCFieldElementSubtract7 = eCFieldElementSubtract5.square().multiply(eCFieldElement7).subtract(eCFieldElementMultiply11).subtract(two(eCFieldElementMultiply12));
                    return new Fp(curve, eCFieldElementSubtract6.multiply(eCFieldElementSubtract7), eCFieldElementMultiply12.subtract(eCFieldElementSubtract7).multiplyMinusProduct(eCFieldElementSubtract5, eCFieldElementMultiply8, eCFieldElementMultiply11), new ECFieldElement[]{eCFieldElementMultiply11.multiply(eCFieldElement7)});
                case 2:
                case 4:
                    ECFieldElement eCFieldElement8 = this.zs[0];
                    ECFieldElement eCFieldElement9 = eCPoint.zs[0];
                    boolean zIsOne3 = eCFieldElement8.isOne();
                    ECFieldElement eCFieldElement10 = null;
                    if (zIsOne3 || !eCFieldElement8.equals(eCFieldElement9)) {
                        if (zIsOne3) {
                            eCFieldElementMultiply = eCFieldElement3;
                            eCFieldElementMultiply2 = eCFieldElement4;
                        } else {
                            ECFieldElement eCFieldElementSquare2 = eCFieldElement8.square();
                            eCFieldElementMultiply = eCFieldElementSquare2.multiply(eCFieldElement3);
                            eCFieldElementMultiply2 = eCFieldElementSquare2.multiply(eCFieldElement8).multiply(eCFieldElement4);
                        }
                        boolean zIsOne4 = eCFieldElement9.isOne();
                        if (zIsOne4) {
                            eCFieldElementMultiply3 = eCFieldElement;
                            eCFieldElementMultiply4 = eCFieldElement2;
                        } else {
                            ECFieldElement eCFieldElementSquare3 = eCFieldElement9.square();
                            eCFieldElementMultiply3 = eCFieldElementSquare3.multiply(eCFieldElement);
                            eCFieldElementMultiply4 = eCFieldElementSquare3.multiply(eCFieldElement9).multiply(eCFieldElement2);
                        }
                        ECFieldElement eCFieldElementSubtract8 = eCFieldElementMultiply3.subtract(eCFieldElementMultiply);
                        ECFieldElement eCFieldElementSubtract9 = eCFieldElementMultiply4.subtract(eCFieldElementMultiply2);
                        if (eCFieldElementSubtract8.isZero()) {
                            return eCFieldElementSubtract9.isZero() ? twice() : curve.getInfinity();
                        }
                        ECFieldElement eCFieldElementSquare4 = eCFieldElementSubtract8.square();
                        ECFieldElement eCFieldElementMultiply13 = eCFieldElementSquare4.multiply(eCFieldElementSubtract8);
                        ECFieldElement eCFieldElementMultiply14 = eCFieldElementSquare4.multiply(eCFieldElementMultiply3);
                        eCFieldElementSubtract = eCFieldElementSubtract9.square().add(eCFieldElementMultiply13).subtract(two(eCFieldElementMultiply14));
                        eCFieldElementMultiplyMinusProduct = eCFieldElementMultiply14.subtract(eCFieldElementSubtract).multiplyMinusProduct(eCFieldElementSubtract9, eCFieldElementMultiply13, eCFieldElementMultiply4);
                        eCFieldElementMultiply5 = eCFieldElementSubtract8;
                        if (!zIsOne3) {
                            eCFieldElementMultiply5 = eCFieldElementMultiply5.multiply(eCFieldElement8);
                        }
                        if (!zIsOne4) {
                            eCFieldElementMultiply5 = eCFieldElementMultiply5.multiply(eCFieldElement9);
                        }
                        if (eCFieldElementMultiply5 == eCFieldElementSubtract8) {
                            eCFieldElement10 = eCFieldElementSquare4;
                        }
                    } else {
                        ECFieldElement eCFieldElementSubtract10 = eCFieldElement.subtract(eCFieldElement3);
                        ECFieldElement eCFieldElementSubtract11 = eCFieldElement2.subtract(eCFieldElement4);
                        if (eCFieldElementSubtract10.isZero()) {
                            return eCFieldElementSubtract11.isZero() ? twice() : curve.getInfinity();
                        }
                        ECFieldElement eCFieldElementSquare5 = eCFieldElementSubtract10.square();
                        ECFieldElement eCFieldElementMultiply15 = eCFieldElement.multiply(eCFieldElementSquare5);
                        ECFieldElement eCFieldElementMultiply16 = eCFieldElement3.multiply(eCFieldElementSquare5);
                        ECFieldElement eCFieldElementMultiply17 = eCFieldElementMultiply15.subtract(eCFieldElementMultiply16).multiply(eCFieldElement2);
                        eCFieldElementSubtract = eCFieldElementSubtract11.square().subtract(eCFieldElementMultiply15).subtract(eCFieldElementMultiply16);
                        eCFieldElementMultiplyMinusProduct = eCFieldElementMultiply15.subtract(eCFieldElementSubtract).multiply(eCFieldElementSubtract11).subtract(eCFieldElementMultiply17);
                        eCFieldElementMultiply5 = eCFieldElementSubtract10.multiply(eCFieldElement8);
                    }
                    return new Fp(curve, eCFieldElementSubtract, eCFieldElementMultiplyMinusProduct, coordinateSystem == 4 ? new ECFieldElement[]{eCFieldElementMultiply5, calculateJacobianModifiedW(eCFieldElementMultiply5, eCFieldElement10)} : new ECFieldElement[]{eCFieldElementMultiply5});
                case 3:
                default:
                    throw new IllegalStateException("unsupported coordinate system");
            }
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        public ECPoint twice() {
            ECFieldElement eCFieldElementThree;
            ECFieldElement eCFieldElementFour;
            if (isInfinity()) {
                return this;
            }
            ECCurve curve = getCurve();
            ECFieldElement eCFieldElement = this.y;
            if (eCFieldElement.isZero()) {
                return curve.getInfinity();
            }
            int coordinateSystem = curve.getCoordinateSystem();
            ECFieldElement eCFieldElement2 = this.x;
            switch (coordinateSystem) {
                case 0:
                    ECFieldElement eCFieldElementDivide = three(eCFieldElement2.square()).add(getCurve().getA()).divide(two(eCFieldElement));
                    ECFieldElement eCFieldElementSubtract = eCFieldElementDivide.square().subtract(two(eCFieldElement2));
                    return new Fp(curve, eCFieldElementSubtract, eCFieldElementDivide.multiply(eCFieldElement2.subtract(eCFieldElementSubtract)).subtract(eCFieldElement));
                case 1:
                    ECFieldElement eCFieldElement3 = this.zs[0];
                    boolean zIsOne = eCFieldElement3.isOne();
                    ECFieldElement a = curve.getA();
                    if (!a.isZero() && !zIsOne) {
                        a = a.multiply(eCFieldElement3.square());
                    }
                    ECFieldElement eCFieldElementAdd = a.add(three(eCFieldElement2.square()));
                    ECFieldElement eCFieldElementMultiply = zIsOne ? eCFieldElement : eCFieldElement.multiply(eCFieldElement3);
                    ECFieldElement eCFieldElementSquare = zIsOne ? eCFieldElement.square() : eCFieldElementMultiply.multiply(eCFieldElement);
                    ECFieldElement eCFieldElementFour2 = four(eCFieldElement2.multiply(eCFieldElementSquare));
                    ECFieldElement eCFieldElementSubtract2 = eCFieldElementAdd.square().subtract(two(eCFieldElementFour2));
                    ECFieldElement eCFieldElementTwo = two(eCFieldElementMultiply);
                    ECFieldElement eCFieldElementMultiply2 = eCFieldElementSubtract2.multiply(eCFieldElementTwo);
                    ECFieldElement eCFieldElementTwo2 = two(eCFieldElementSquare);
                    return new Fp(curve, eCFieldElementMultiply2, eCFieldElementFour2.subtract(eCFieldElementSubtract2).multiply(eCFieldElementAdd).subtract(two(eCFieldElementTwo2.square())), new ECFieldElement[]{two(zIsOne ? two(eCFieldElementTwo2) : eCFieldElementTwo.square()).multiply(eCFieldElementMultiply)});
                case 2:
                    ECFieldElement eCFieldElement4 = this.zs[0];
                    boolean zIsOne2 = eCFieldElement4.isOne();
                    ECFieldElement eCFieldElementSquare2 = eCFieldElement.square();
                    ECFieldElement eCFieldElementSquare3 = eCFieldElementSquare2.square();
                    ECFieldElement a2 = curve.getA();
                    ECFieldElement eCFieldElementNegate = a2.negate();
                    if (eCFieldElementNegate.toBigInteger().equals(BigInteger.valueOf(3L))) {
                        ECFieldElement eCFieldElementSquare4 = zIsOne2 ? eCFieldElement4 : eCFieldElement4.square();
                        eCFieldElementThree = three(eCFieldElement2.add(eCFieldElementSquare4).multiply(eCFieldElement2.subtract(eCFieldElementSquare4)));
                        eCFieldElementFour = four(eCFieldElementSquare2.multiply(eCFieldElement2));
                    } else {
                        eCFieldElementThree = three(eCFieldElement2.square());
                        if (zIsOne2) {
                            eCFieldElementThree = eCFieldElementThree.add(a2);
                        } else if (!a2.isZero()) {
                            ECFieldElement eCFieldElementSquare5 = eCFieldElement4.square().square();
                            eCFieldElementThree = eCFieldElementNegate.bitLength() < a2.bitLength() ? eCFieldElementThree.subtract(eCFieldElementSquare5.multiply(eCFieldElementNegate)) : eCFieldElementThree.add(eCFieldElementSquare5.multiply(a2));
                        }
                        eCFieldElementFour = four(eCFieldElement2.multiply(eCFieldElementSquare2));
                    }
                    ECFieldElement eCFieldElementSubtract3 = eCFieldElementThree.square().subtract(two(eCFieldElementFour));
                    ECFieldElement eCFieldElementSubtract4 = eCFieldElementFour.subtract(eCFieldElementSubtract3).multiply(eCFieldElementThree).subtract(eight(eCFieldElementSquare3));
                    ECFieldElement eCFieldElementTwo3 = two(eCFieldElement);
                    if (!zIsOne2) {
                        eCFieldElementTwo3 = eCFieldElementTwo3.multiply(eCFieldElement4);
                    }
                    return new Fp(curve, eCFieldElementSubtract3, eCFieldElementSubtract4, new ECFieldElement[]{eCFieldElementTwo3});
                case 3:
                default:
                    throw new IllegalStateException("unsupported coordinate system");
                case 4:
                    return twiceJacobianModified(true);
            }
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
            ECFieldElement eCFieldElement = this.y;
            if (eCFieldElement.isZero()) {
                return eCPoint;
            }
            ECCurve curve = getCurve();
            switch (curve.getCoordinateSystem()) {
                case 0:
                    ECFieldElement eCFieldElement2 = this.x;
                    ECFieldElement eCFieldElement3 = eCPoint.x;
                    ECFieldElement eCFieldElement4 = eCPoint.y;
                    ECFieldElement eCFieldElementSubtract = eCFieldElement3.subtract(eCFieldElement2);
                    ECFieldElement eCFieldElementSubtract2 = eCFieldElement4.subtract(eCFieldElement);
                    if (eCFieldElementSubtract.isZero()) {
                        return eCFieldElementSubtract2.isZero() ? threeTimes() : this;
                    }
                    ECFieldElement eCFieldElementSquare = eCFieldElementSubtract.square();
                    ECFieldElement eCFieldElementSubtract3 = eCFieldElementSquare.multiply(two(eCFieldElement2).add(eCFieldElement3)).subtract(eCFieldElementSubtract2.square());
                    if (eCFieldElementSubtract3.isZero()) {
                        return curve.getInfinity();
                    }
                    ECFieldElement eCFieldElementInvert = eCFieldElementSubtract3.multiply(eCFieldElementSubtract).invert();
                    ECFieldElement eCFieldElementMultiply = eCFieldElementSubtract3.multiply(eCFieldElementInvert).multiply(eCFieldElementSubtract2);
                    ECFieldElement eCFieldElementSubtract4 = two(eCFieldElement).multiply(eCFieldElementSquare).multiply(eCFieldElementSubtract).multiply(eCFieldElementInvert).subtract(eCFieldElementMultiply);
                    ECFieldElement eCFieldElementAdd = eCFieldElementSubtract4.subtract(eCFieldElementMultiply).multiply(eCFieldElementMultiply.add(eCFieldElementSubtract4)).add(eCFieldElement3);
                    return new Fp(curve, eCFieldElementAdd, eCFieldElement2.subtract(eCFieldElementAdd).multiply(eCFieldElementSubtract4).subtract(eCFieldElement));
                case 4:
                    return twiceJacobianModified(false).add(eCPoint);
                default:
                    return twice().add(eCPoint);
            }
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        public ECPoint threeTimes() {
            if (isInfinity()) {
                return this;
            }
            ECFieldElement eCFieldElement = this.y;
            if (eCFieldElement.isZero()) {
                return this;
            }
            ECCurve curve = getCurve();
            switch (curve.getCoordinateSystem()) {
                case 0:
                    ECFieldElement eCFieldElement2 = this.x;
                    ECFieldElement eCFieldElementTwo = two(eCFieldElement);
                    ECFieldElement eCFieldElementSquare = eCFieldElementTwo.square();
                    ECFieldElement eCFieldElementAdd = three(eCFieldElement2.square()).add(getCurve().getA());
                    ECFieldElement eCFieldElementSubtract = three(eCFieldElement2).multiply(eCFieldElementSquare).subtract(eCFieldElementAdd.square());
                    if (eCFieldElementSubtract.isZero()) {
                        return getCurve().getInfinity();
                    }
                    ECFieldElement eCFieldElementInvert = eCFieldElementSubtract.multiply(eCFieldElementTwo).invert();
                    ECFieldElement eCFieldElementMultiply = eCFieldElementSubtract.multiply(eCFieldElementInvert).multiply(eCFieldElementAdd);
                    ECFieldElement eCFieldElementSubtract2 = eCFieldElementSquare.square().multiply(eCFieldElementInvert).subtract(eCFieldElementMultiply);
                    ECFieldElement eCFieldElementAdd2 = eCFieldElementSubtract2.subtract(eCFieldElementMultiply).multiply(eCFieldElementMultiply.add(eCFieldElementSubtract2)).add(eCFieldElement2);
                    return new Fp(curve, eCFieldElementAdd2, eCFieldElement2.subtract(eCFieldElementAdd2).multiply(eCFieldElementSubtract2).subtract(eCFieldElement));
                case 4:
                    return twiceJacobianModified(false).add(this);
                default:
                    return twice().add(this);
            }
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        public ECPoint timesPow2(int i) {
            if (i < 0) {
                throw new IllegalArgumentException("'e' cannot be negative");
            }
            if (i == 0 || isInfinity()) {
                return this;
            }
            if (i == 1) {
                return twice();
            }
            ECCurve curve = getCurve();
            ECFieldElement eCFieldElementSubtract = this.y;
            if (eCFieldElementSubtract.isZero()) {
                return curve.getInfinity();
            }
            int coordinateSystem = curve.getCoordinateSystem();
            ECFieldElement a = curve.getA();
            ECFieldElement eCFieldElementSubtract2 = this.x;
            ECFieldElement eCFieldElementFromBigInteger = this.zs.length < 1 ? curve.fromBigInteger(ECConstants.ONE) : this.zs[0];
            if (!eCFieldElementFromBigInteger.isOne()) {
                switch (coordinateSystem) {
                    case 0:
                        break;
                    case 1:
                        ECFieldElement eCFieldElementSquare = eCFieldElementFromBigInteger.square();
                        eCFieldElementSubtract2 = eCFieldElementSubtract2.multiply(eCFieldElementFromBigInteger);
                        eCFieldElementSubtract = eCFieldElementSubtract.multiply(eCFieldElementSquare);
                        a = calculateJacobianModifiedW(eCFieldElementFromBigInteger, eCFieldElementSquare);
                        break;
                    case 2:
                        a = calculateJacobianModifiedW(eCFieldElementFromBigInteger, null);
                        break;
                    case 3:
                    default:
                        throw new IllegalStateException("unsupported coordinate system");
                    case 4:
                        a = getJacobianModifiedW();
                        break;
                }
            }
            for (int i2 = 0; i2 < i; i2++) {
                if (eCFieldElementSubtract.isZero()) {
                    return curve.getInfinity();
                }
                ECFieldElement eCFieldElementThree = three(eCFieldElementSubtract2.square());
                ECFieldElement eCFieldElementTwo = two(eCFieldElementSubtract);
                ECFieldElement eCFieldElementMultiply = eCFieldElementTwo.multiply(eCFieldElementSubtract);
                ECFieldElement eCFieldElementTwo2 = two(eCFieldElementSubtract2.multiply(eCFieldElementMultiply));
                ECFieldElement eCFieldElementTwo3 = two(eCFieldElementMultiply.square());
                if (!a.isZero()) {
                    eCFieldElementThree = eCFieldElementThree.add(a);
                    a = two(eCFieldElementTwo3.multiply(a));
                }
                eCFieldElementSubtract2 = eCFieldElementThree.square().subtract(two(eCFieldElementTwo2));
                eCFieldElementSubtract = eCFieldElementThree.multiply(eCFieldElementTwo2.subtract(eCFieldElementSubtract2)).subtract(eCFieldElementTwo3);
                eCFieldElementFromBigInteger = eCFieldElementFromBigInteger.isOne() ? eCFieldElementTwo : eCFieldElementTwo.multiply(eCFieldElementFromBigInteger);
            }
            switch (coordinateSystem) {
                case 0:
                    ECFieldElement eCFieldElementInvert = eCFieldElementFromBigInteger.invert();
                    ECFieldElement eCFieldElementSquare2 = eCFieldElementInvert.square();
                    return new Fp(curve, eCFieldElementSubtract2.multiply(eCFieldElementSquare2), eCFieldElementSubtract.multiply(eCFieldElementSquare2.multiply(eCFieldElementInvert)));
                case 1:
                    return new Fp(curve, eCFieldElementSubtract2.multiply(eCFieldElementFromBigInteger), eCFieldElementSubtract, new ECFieldElement[]{eCFieldElementFromBigInteger.multiply(eCFieldElementFromBigInteger.square())});
                case 2:
                    return new Fp(curve, eCFieldElementSubtract2, eCFieldElementSubtract, new ECFieldElement[]{eCFieldElementFromBigInteger});
                case 3:
                default:
                    throw new IllegalStateException("unsupported coordinate system");
                case 4:
                    return new Fp(curve, eCFieldElementSubtract2, eCFieldElementSubtract, new ECFieldElement[]{eCFieldElementFromBigInteger, a});
            }
        }

        protected ECFieldElement two(ECFieldElement eCFieldElement) {
            return eCFieldElement.add(eCFieldElement);
        }

        protected ECFieldElement three(ECFieldElement eCFieldElement) {
            return two(eCFieldElement).add(eCFieldElement);
        }

        protected ECFieldElement four(ECFieldElement eCFieldElement) {
            return two(two(eCFieldElement));
        }

        protected ECFieldElement eight(ECFieldElement eCFieldElement) {
            return four(two(eCFieldElement));
        }

        protected ECFieldElement doubleProductFromSquares(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement eCFieldElement3, ECFieldElement eCFieldElement4) {
            return eCFieldElement.add(eCFieldElement2).square().subtract(eCFieldElement3).subtract(eCFieldElement4);
        }

        @Override // org.bouncycastle.math.ec.ECPoint
        public ECPoint negate() {
            if (isInfinity()) {
                return this;
            }
            ECCurve curve = getCurve();
            return 0 != curve.getCoordinateSystem() ? new Fp(curve, this.x, this.y.negate(), this.zs) : new Fp(curve, this.x, this.y.negate());
        }

        protected ECFieldElement calculateJacobianModifiedW(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
            ECFieldElement a = getCurve().getA();
            if (a.isZero() || eCFieldElement.isOne()) {
                return a;
            }
            if (eCFieldElement2 == null) {
                eCFieldElement2 = eCFieldElement.square();
            }
            ECFieldElement eCFieldElementSquare = eCFieldElement2.square();
            ECFieldElement eCFieldElementNegate = a.negate();
            return eCFieldElementNegate.bitLength() < a.bitLength() ? eCFieldElementSquare.multiply(eCFieldElementNegate).negate() : eCFieldElementSquare.multiply(a);
        }

        protected ECFieldElement getJacobianModifiedW() {
            ECFieldElement eCFieldElement = this.zs[1];
            if (eCFieldElement == null) {
                ECFieldElement[] eCFieldElementArr = this.zs;
                ECFieldElement eCFieldElementCalculateJacobianModifiedW = calculateJacobianModifiedW(this.zs[0], null);
                eCFieldElement = eCFieldElementCalculateJacobianModifiedW;
                eCFieldElementArr[1] = eCFieldElementCalculateJacobianModifiedW;
            }
            return eCFieldElement;
        }

        protected Fp twiceJacobianModified(boolean z) {
            ECFieldElement eCFieldElement = this.x;
            ECFieldElement eCFieldElement2 = this.y;
            ECFieldElement eCFieldElement3 = this.zs[0];
            ECFieldElement jacobianModifiedW = getJacobianModifiedW();
            ECFieldElement eCFieldElementAdd = three(eCFieldElement.square()).add(jacobianModifiedW);
            ECFieldElement eCFieldElementTwo = two(eCFieldElement2);
            ECFieldElement eCFieldElementMultiply = eCFieldElementTwo.multiply(eCFieldElement2);
            ECFieldElement eCFieldElementTwo2 = two(eCFieldElement.multiply(eCFieldElementMultiply));
            ECFieldElement eCFieldElementSubtract = eCFieldElementAdd.square().subtract(two(eCFieldElementTwo2));
            ECFieldElement eCFieldElementTwo3 = two(eCFieldElementMultiply.square());
            return new Fp(getCurve(), eCFieldElementSubtract, eCFieldElementAdd.multiply(eCFieldElementTwo2.subtract(eCFieldElementSubtract)).subtract(eCFieldElementTwo3), new ECFieldElement[]{eCFieldElement3.isOne() ? eCFieldElementTwo : eCFieldElementTwo.multiply(eCFieldElement3), z ? two(eCFieldElementTwo3.multiply(jacobianModifiedW)) : null});
        }
    }

    protected static ECFieldElement[] getInitialZCoords(ECCurve eCCurve) {
        int coordinateSystem = null == eCCurve ? 0 : eCCurve.getCoordinateSystem();
        switch (coordinateSystem) {
            case 0:
            case 5:
                return EMPTY_ZS;
            default:
                ECFieldElement eCFieldElementFromBigInteger = eCCurve.fromBigInteger(ECConstants.ONE);
                switch (coordinateSystem) {
                    case 1:
                    case 2:
                    case 6:
                        return new ECFieldElement[]{eCFieldElementFromBigInteger};
                    case 3:
                        return new ECFieldElement[]{eCFieldElementFromBigInteger, eCFieldElementFromBigInteger, eCFieldElementFromBigInteger};
                    case 4:
                        return new ECFieldElement[]{eCFieldElementFromBigInteger, eCCurve.getA()};
                    case 5:
                    default:
                        throw new IllegalArgumentException("unknown coordinate system");
                }
        }
    }

    protected ECPoint(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        this(eCCurve, eCFieldElement, eCFieldElement2, getInitialZCoords(eCCurve));
    }

    protected ECPoint(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArr) {
        this.preCompTable = null;
        this.curve = eCCurve;
        this.x = eCFieldElement;
        this.y = eCFieldElement2;
        this.zs = eCFieldElementArr;
    }

    protected abstract boolean satisfiesCurveEquation();

    protected boolean satisfiesOrder() {
        BigInteger order;
        return ECConstants.ONE.equals(this.curve.getCofactor()) || (order = this.curve.getOrder()) == null || ECAlgorithms.referenceMultiply(this, order).isInfinity();
    }

    public final ECPoint getDetachedPoint() {
        return normalize().detach();
    }

    public ECCurve getCurve() {
        return this.curve;
    }

    protected abstract ECPoint detach();

    protected int getCurveCoordinateSystem() {
        if (null == this.curve) {
            return 0;
        }
        return this.curve.getCoordinateSystem();
    }

    public ECFieldElement getAffineXCoord() {
        checkNormalized();
        return getXCoord();
    }

    public ECFieldElement getAffineYCoord() {
        checkNormalized();
        return getYCoord();
    }

    public ECFieldElement getXCoord() {
        return this.x;
    }

    public ECFieldElement getYCoord() {
        return this.y;
    }

    public ECFieldElement getZCoord(int i) {
        if (i < 0 || i >= this.zs.length) {
            return null;
        }
        return this.zs[i];
    }

    public ECFieldElement[] getZCoords() {
        int length = this.zs.length;
        if (length == 0) {
            return EMPTY_ZS;
        }
        ECFieldElement[] eCFieldElementArr = new ECFieldElement[length];
        System.arraycopy(this.zs, 0, eCFieldElementArr, 0, length);
        return eCFieldElementArr;
    }

    public final ECFieldElement getRawXCoord() {
        return this.x;
    }

    public final ECFieldElement getRawYCoord() {
        return this.y;
    }

    protected final ECFieldElement[] getRawZCoords() {
        return this.zs;
    }

    protected void checkNormalized() {
        if (!isNormalized()) {
            throw new IllegalStateException("point not in normal form");
        }
    }

    public boolean isNormalized() {
        int curveCoordinateSystem = getCurveCoordinateSystem();
        return curveCoordinateSystem == 0 || curveCoordinateSystem == 5 || isInfinity() || this.zs[0].isOne();
    }

    public ECPoint normalize() {
        if (isInfinity()) {
            return this;
        }
        switch (getCurveCoordinateSystem()) {
            case 0:
            case 5:
                return this;
            default:
                ECFieldElement zCoord = getZCoord(0);
                if (zCoord.isOne()) {
                    return this;
                }
                if (null == this.curve) {
                    throw new IllegalStateException("Detached points must be in affine coordinates");
                }
                ECFieldElement eCFieldElementRandomFieldElementMult = this.curve.randomFieldElementMult(CryptoServicesRegistrar.getSecureRandom());
                return normalize(zCoord.multiply(eCFieldElementRandomFieldElementMult).invert().multiply(eCFieldElementRandomFieldElementMult));
        }
    }

    ECPoint normalize(ECFieldElement eCFieldElement) {
        switch (getCurveCoordinateSystem()) {
            case 1:
            case 6:
                return createScaledPoint(eCFieldElement, eCFieldElement);
            case 2:
            case 3:
            case 4:
                ECFieldElement eCFieldElementSquare = eCFieldElement.square();
                return createScaledPoint(eCFieldElementSquare, eCFieldElementSquare.multiply(eCFieldElement));
            case 5:
            default:
                throw new IllegalStateException("not a projective coordinate system");
        }
    }

    protected ECPoint createScaledPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        return getCurve().createRawPoint(getRawXCoord().multiply(eCFieldElement), getRawYCoord().multiply(eCFieldElement2));
    }

    public boolean isInfinity() {
        return this.x == null || this.y == null || (this.zs.length > 0 && this.zs[0].isZero());
    }

    public boolean isValid() {
        return implIsValid(false, true);
    }

    boolean isValidPartial() {
        return implIsValid(false, false);
    }

    boolean implIsValid(final boolean z, final boolean z2) {
        return isInfinity() || !((ValidityPrecompInfo) getCurve().precompute(this, "bc_validity", new PreCompCallback() { // from class: org.bouncycastle.math.ec.ECPoint.1
            @Override // org.bouncycastle.math.ec.PreCompCallback
            public PreCompInfo precompute(PreCompInfo preCompInfo) {
                ValidityPrecompInfo validityPrecompInfo = preCompInfo instanceof ValidityPrecompInfo ? (ValidityPrecompInfo) preCompInfo : null;
                if (validityPrecompInfo == null) {
                    validityPrecompInfo = new ValidityPrecompInfo();
                }
                if (validityPrecompInfo.hasFailed()) {
                    return validityPrecompInfo;
                }
                if (!validityPrecompInfo.hasCurveEquationPassed()) {
                    if (!z && !ECPoint.this.satisfiesCurveEquation()) {
                        validityPrecompInfo.reportFailed();
                        return validityPrecompInfo;
                    }
                    validityPrecompInfo.reportCurveEquationPassed();
                }
                if (z2 && !validityPrecompInfo.hasOrderPassed()) {
                    if (!ECPoint.this.satisfiesOrder()) {
                        validityPrecompInfo.reportFailed();
                        return validityPrecompInfo;
                    }
                    validityPrecompInfo.reportOrderPassed();
                }
                return validityPrecompInfo;
            }
        })).hasFailed();
    }

    public ECPoint scaleX(ECFieldElement eCFieldElement) {
        return isInfinity() ? this : getCurve().createRawPoint(getRawXCoord().multiply(eCFieldElement), getRawYCoord(), getRawZCoords());
    }

    public ECPoint scaleXNegateY(ECFieldElement eCFieldElement) {
        return isInfinity() ? this : getCurve().createRawPoint(getRawXCoord().multiply(eCFieldElement), getRawYCoord().negate(), getRawZCoords());
    }

    public ECPoint scaleY(ECFieldElement eCFieldElement) {
        return isInfinity() ? this : getCurve().createRawPoint(getRawXCoord(), getRawYCoord().multiply(eCFieldElement), getRawZCoords());
    }

    public ECPoint scaleYNegateX(ECFieldElement eCFieldElement) {
        return isInfinity() ? this : getCurve().createRawPoint(getRawXCoord().negate(), getRawYCoord().multiply(eCFieldElement), getRawZCoords());
    }

    public boolean equals(ECPoint eCPoint) {
        if (null == eCPoint) {
            return false;
        }
        ECCurve curve = getCurve();
        ECCurve curve2 = eCPoint.getCurve();
        boolean z = null == curve;
        boolean z2 = null == curve2;
        boolean zIsInfinity = isInfinity();
        boolean zIsInfinity2 = eCPoint.isInfinity();
        if (zIsInfinity || zIsInfinity2) {
            return zIsInfinity && zIsInfinity2 && (z || z2 || curve.equals(curve2));
        }
        ECPoint eCPointNormalize = this;
        ECPoint eCPointNormalize2 = eCPoint;
        if (!z || !z2) {
            if (z) {
                eCPointNormalize2 = eCPointNormalize2.normalize();
            } else if (z2) {
                eCPointNormalize = eCPointNormalize.normalize();
            } else {
                if (!curve.equals(curve2)) {
                    return false;
                }
                ECPoint[] eCPointArr = {this, curve.importPoint(eCPointNormalize2)};
                curve.normalizeAll(eCPointArr);
                eCPointNormalize = eCPointArr[0];
                eCPointNormalize2 = eCPointArr[1];
            }
        }
        return eCPointNormalize.getXCoord().equals(eCPointNormalize2.getXCoord()) && eCPointNormalize.getYCoord().equals(eCPointNormalize2.getYCoord());
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ECPoint) {
            return equals((ECPoint) obj);
        }
        return false;
    }

    public int hashCode() {
        ECCurve curve = getCurve();
        int iHashCode = null == curve ? 0 : curve.hashCode() ^ (-1);
        if (!isInfinity()) {
            ECPoint eCPointNormalize = normalize();
            iHashCode = (iHashCode ^ (eCPointNormalize.getXCoord().hashCode() * 17)) ^ (eCPointNormalize.getYCoord().hashCode() * 257);
        }
        return iHashCode;
    }

    public String toString() {
        if (isInfinity()) {
            return "INF";
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('(');
        stringBuffer.append(getRawXCoord());
        stringBuffer.append(',');
        stringBuffer.append(getRawYCoord());
        for (int i = 0; i < this.zs.length; i++) {
            stringBuffer.append(',');
            stringBuffer.append(this.zs[i]);
        }
        stringBuffer.append(')');
        return stringBuffer.toString();
    }

    public byte[] getEncoded(boolean z) {
        if (isInfinity()) {
            return new byte[1];
        }
        ECPoint eCPointNormalize = normalize();
        byte[] encoded = eCPointNormalize.getXCoord().getEncoded();
        if (z) {
            byte[] bArr = new byte[encoded.length + 1];
            bArr[0] = (byte) (eCPointNormalize.getCompressionYTilde() ? 3 : 2);
            System.arraycopy(encoded, 0, bArr, 1, encoded.length);
            return bArr;
        }
        byte[] encoded2 = eCPointNormalize.getYCoord().getEncoded();
        byte[] bArr2 = new byte[encoded.length + encoded2.length + 1];
        bArr2[0] = 4;
        System.arraycopy(encoded, 0, bArr2, 1, encoded.length);
        System.arraycopy(encoded2, 0, bArr2, encoded.length + 1, encoded2.length);
        return bArr2;
    }

    public int getEncodedLength(boolean z) {
        if (isInfinity()) {
            return 1;
        }
        return z ? 1 + getXCoord().getEncodedLength() : 1 + getXCoord().getEncodedLength() + getYCoord().getEncodedLength();
    }

    public void encodeTo(boolean z, byte[] bArr, int i) {
        if (isInfinity()) {
            bArr[i] = 0;
            return;
        }
        ECPoint eCPointNormalize = normalize();
        ECFieldElement xCoord = eCPointNormalize.getXCoord();
        ECFieldElement yCoord = eCPointNormalize.getYCoord();
        if (z) {
            bArr[i] = (byte) (eCPointNormalize.getCompressionYTilde() ? 3 : 2);
            xCoord.encodeTo(bArr, i + 1);
        } else {
            bArr[i] = 4;
            xCoord.encodeTo(bArr, i + 1);
            yCoord.encodeTo(bArr, i + 1 + xCoord.getEncodedLength());
        }
    }

    protected abstract boolean getCompressionYTilde();

    public abstract ECPoint add(ECPoint eCPoint);

    public abstract ECPoint negate();

    public abstract ECPoint subtract(ECPoint eCPoint);

    public ECPoint timesPow2(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("'e' cannot be negative");
        }
        ECPoint eCPointTwice = this;
        while (true) {
            ECPoint eCPoint = eCPointTwice;
            i--;
            if (i < 0) {
                return eCPoint;
            }
            eCPointTwice = eCPoint.twice();
        }
    }

    public abstract ECPoint twice();

    public ECPoint twicePlus(ECPoint eCPoint) {
        return twice().add(eCPoint);
    }

    public ECPoint threeTimes() {
        return twicePlus(this);
    }

    public ECPoint multiply(BigInteger bigInteger) {
        return getCurve().getMultiplier().multiply(this, bigInteger);
    }
}
