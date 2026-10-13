package org.spongycastle.math.ec.custom.sec;

import org.spongycastle.math.ec.ECConstants;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/custom/sec/SecT283K1Point.class */
public class SecT283K1Point extends ECPoint.AbstractF2m {
    public SecT283K1Point(ECCurve curve, ECFieldElement x, ECFieldElement y) {
        this(curve, x, y, false);
    }

    public SecT283K1Point(ECCurve curve, ECFieldElement x, ECFieldElement y, boolean withCompression) {
        super(curve, x, y);
        if ((x == null) != (y == null)) {
            throw new IllegalArgumentException("Exactly one of the field elements is null");
        }
        this.withCompression = withCompression;
    }

    SecT283K1Point(ECCurve curve, ECFieldElement x, ECFieldElement y, ECFieldElement[] zs, boolean withCompression) {
        super(curve, x, y, zs);
        this.withCompression = withCompression;
    }

    @Override // org.spongycastle.math.ec.ECPoint
    protected ECPoint detach() {
        return new SecT283K1Point(null, getAffineXCoord(), getAffineYCoord());
    }

    @Override // org.spongycastle.math.ec.ECPoint
    public ECFieldElement getYCoord() {
        ECFieldElement X = this.x;
        ECFieldElement L = this.y;
        if (isInfinity() || X.isZero()) {
            return L;
        }
        ECFieldElement Y = L.add(X).multiply(X);
        ECFieldElement Z = this.zs[0];
        if (!Z.isOne()) {
            Y = Y.divide(Z);
        }
        return Y;
    }

    @Override // org.spongycastle.math.ec.ECPoint
    protected boolean getCompressionYTilde() {
        ECFieldElement X = getRawXCoord();
        if (X.isZero()) {
            return false;
        }
        ECFieldElement Y = getRawYCoord();
        return Y.testBitZero() != X.testBitZero();
    }

    @Override // org.spongycastle.math.ec.ECPoint
    public ECPoint add(ECPoint b) {
        ECFieldElement X3;
        ECFieldElement L3;
        ECFieldElement Z3;
        if (isInfinity()) {
            return b;
        }
        if (b.isInfinity()) {
            return this;
        }
        ECCurve curve = getCurve();
        ECFieldElement X1 = this.x;
        ECFieldElement X2 = b.getRawXCoord();
        if (X1.isZero()) {
            if (X2.isZero()) {
                return curve.getInfinity();
            }
            return b.add(this);
        }
        ECFieldElement L1 = this.y;
        ECFieldElement Z1 = this.zs[0];
        ECFieldElement L2 = b.getRawYCoord();
        ECFieldElement Z2 = b.getZCoord(0);
        boolean Z1IsOne = Z1.isOne();
        ECFieldElement U2 = X2;
        ECFieldElement S2 = L2;
        if (!Z1IsOne) {
            U2 = U2.multiply(Z1);
            S2 = S2.multiply(Z1);
        }
        boolean Z2IsOne = Z2.isOne();
        ECFieldElement U1 = X1;
        ECFieldElement S1 = L1;
        if (!Z2IsOne) {
            U1 = U1.multiply(Z2);
            S1 = S1.multiply(Z2);
        }
        ECFieldElement A = S1.add(S2);
        ECFieldElement B = U1.add(U2);
        if (B.isZero()) {
            if (A.isZero()) {
                return twice();
            }
            return curve.getInfinity();
        }
        if (X2.isZero()) {
            ECPoint p = normalize();
            ECFieldElement X4 = p.getXCoord();
            ECFieldElement Y1 = p.getYCoord();
            ECFieldElement L = Y1.add(L2).divide(X4);
            X3 = L.square().add(L).add(X4);
            if (X3.isZero()) {
                return new SecT283K1Point(curve, X3, curve.getB(), this.withCompression);
            }
            ECFieldElement Y3 = L.multiply(X4.add(X3)).add(X3).add(Y1);
            L3 = Y3.divide(X3).add(X3);
            Z3 = curve.fromBigInteger(ECConstants.ONE);
        } else {
            ECFieldElement B2 = B.square();
            ECFieldElement AU1 = A.multiply(U1);
            ECFieldElement AU2 = A.multiply(U2);
            X3 = AU1.multiply(AU2);
            if (X3.isZero()) {
                return new SecT283K1Point(curve, X3, curve.getB(), this.withCompression);
            }
            ECFieldElement ABZ2 = A.multiply(B2);
            if (!Z2IsOne) {
                ABZ2 = ABZ2.multiply(Z2);
            }
            L3 = AU2.add(B2).squarePlusProduct(ABZ2, L1.add(Z1));
            Z3 = ABZ2;
            if (!Z1IsOne) {
                Z3 = Z3.multiply(Z1);
            }
        }
        return new SecT283K1Point(curve, X3, L3, new ECFieldElement[]{Z3}, this.withCompression);
    }

    @Override // org.spongycastle.math.ec.ECPoint
    public ECPoint twice() {
        ECFieldElement T;
        if (isInfinity()) {
            return this;
        }
        ECCurve curve = getCurve();
        ECFieldElement X1 = this.x;
        if (X1.isZero()) {
            return curve.getInfinity();
        }
        ECFieldElement L1 = this.y;
        ECFieldElement Z1 = this.zs[0];
        boolean Z1IsOne = Z1.isOne();
        ECFieldElement Z1Sq = Z1IsOne ? Z1 : Z1.square();
        if (Z1IsOne) {
            T = L1.square().add(L1);
        } else {
            T = L1.add(Z1).multiply(L1);
        }
        if (T.isZero()) {
            return new SecT283K1Point(curve, T, curve.getB(), this.withCompression);
        }
        ECFieldElement X3 = T.square();
        ECFieldElement Z3 = Z1IsOne ? T : T.multiply(Z1Sq);
        ECFieldElement t1 = L1.add(X1).square();
        ECFieldElement t2 = Z1IsOne ? Z1 : Z1Sq.square();
        ECFieldElement L3 = t1.add(T).add(Z1Sq).multiply(t1).add(t2).add(X3).add(Z3);
        return new SecT283K1Point(curve, X3, L3, new ECFieldElement[]{Z3}, this.withCompression);
    }

    @Override // org.spongycastle.math.ec.ECPoint
    public ECPoint twicePlus(ECPoint b) {
        if (isInfinity()) {
            return b;
        }
        if (b.isInfinity()) {
            return twice();
        }
        ECCurve curve = getCurve();
        ECFieldElement X1 = this.x;
        if (X1.isZero()) {
            return b;
        }
        ECFieldElement X2 = b.getRawXCoord();
        ECFieldElement Z2 = b.getZCoord(0);
        if (X2.isZero() || !Z2.isOne()) {
            return twice().add(b);
        }
        ECFieldElement L1 = this.y;
        ECFieldElement Z1 = this.zs[0];
        ECFieldElement L2 = b.getRawYCoord();
        ECFieldElement X1Sq = X1.square();
        ECFieldElement L1Sq = L1.square();
        ECFieldElement Z1Sq = Z1.square();
        ECFieldElement L1Z1 = L1.multiply(Z1);
        ECFieldElement T = L1Sq.add(L1Z1);
        ECFieldElement L2plus1 = L2.addOne();
        ECFieldElement A = L2plus1.multiply(Z1Sq).add(L1Sq).multiplyPlusProduct(T, X1Sq, Z1Sq);
        ECFieldElement X2Z1Sq = X2.multiply(Z1Sq);
        ECFieldElement B = X2Z1Sq.add(T).square();
        if (B.isZero()) {
            if (A.isZero()) {
                return b.twice();
            }
            return curve.getInfinity();
        }
        if (A.isZero()) {
            return new SecT283K1Point(curve, A, curve.getB(), this.withCompression);
        }
        ECFieldElement X3 = A.square().multiply(X2Z1Sq);
        ECFieldElement Z3 = A.multiply(B).multiply(Z1Sq);
        ECFieldElement L3 = A.add(B).square().multiplyPlusProduct(T, L2plus1, Z3);
        return new SecT283K1Point(curve, X3, L3, new ECFieldElement[]{Z3}, this.withCompression);
    }

    @Override // org.spongycastle.math.ec.ECPoint
    public ECPoint negate() {
        if (isInfinity()) {
            return this;
        }
        ECFieldElement X = this.x;
        if (X.isZero()) {
            return this;
        }
        ECFieldElement L = this.y;
        ECFieldElement Z = this.zs[0];
        return new SecT283K1Point(this.curve, X, L.add(Z), new ECFieldElement[]{Z}, this.withCompression);
    }
}
