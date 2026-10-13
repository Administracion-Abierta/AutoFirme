package org.spongycastle.math.ec.custom.sec;

import org.spongycastle.math.ec.ECConstants;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.raw.Nat;
import org.spongycastle.math.raw.Nat576;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/ec/custom/sec/SecT571R1Point.class */
public class SecT571R1Point extends ECPoint.AbstractF2m {
    public SecT571R1Point(ECCurve curve, ECFieldElement x, ECFieldElement y) {
        this(curve, x, y, false);
    }

    public SecT571R1Point(ECCurve curve, ECFieldElement x, ECFieldElement y, boolean withCompression) {
        super(curve, x, y);
        if ((x == null) != (y == null)) {
            throw new IllegalArgumentException("Exactly one of the field elements is null");
        }
        this.withCompression = withCompression;
    }

    SecT571R1Point(ECCurve curve, ECFieldElement x, ECFieldElement y, ECFieldElement[] zs, boolean withCompression) {
        super(curve, x, y, zs);
        this.withCompression = withCompression;
    }

    @Override // org.spongycastle.math.ec.ECPoint
    protected ECPoint detach() {
        return new SecT571R1Point(null, getAffineXCoord(), getAffineYCoord());
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
        long[] U2;
        long[] S2;
        long[] U1;
        long[] S1;
        SecT571FieldElement X3;
        SecT571FieldElement Z3;
        SecT571FieldElement L3;
        if (isInfinity()) {
            return b;
        }
        if (b.isInfinity()) {
            return this;
        }
        ECCurve curve = getCurve();
        SecT571FieldElement X1 = (SecT571FieldElement) this.x;
        SecT571FieldElement X2 = (SecT571FieldElement) b.getRawXCoord();
        if (X1.isZero()) {
            if (X2.isZero()) {
                return curve.getInfinity();
            }
            return b.add(this);
        }
        SecT571FieldElement L1 = (SecT571FieldElement) this.y;
        SecT571FieldElement Z1 = (SecT571FieldElement) this.zs[0];
        SecT571FieldElement L2 = (SecT571FieldElement) b.getRawYCoord();
        SecT571FieldElement Z2 = (SecT571FieldElement) b.getZCoord(0);
        long[] t1 = Nat576.create64();
        long[] t2 = Nat576.create64();
        long[] t3 = Nat576.create64();
        long[] t4 = Nat576.create64();
        long[] Z1Precomp = Z1.isOne() ? null : SecT571Field.precompMultiplicand(Z1.x);
        if (Z1Precomp == null) {
            U2 = X2.x;
            S2 = L2.x;
        } else {
            U2 = t2;
            SecT571Field.multiplyPrecomp(X2.x, Z1Precomp, t2);
            S2 = t4;
            SecT571Field.multiplyPrecomp(L2.x, Z1Precomp, t4);
        }
        long[] Z2Precomp = Z2.isOne() ? null : SecT571Field.precompMultiplicand(Z2.x);
        if (Z2Precomp == null) {
            U1 = X1.x;
            S1 = L1.x;
        } else {
            U1 = t1;
            SecT571Field.multiplyPrecomp(X1.x, Z2Precomp, t1);
            S1 = t3;
            SecT571Field.multiplyPrecomp(L1.x, Z2Precomp, t3);
        }
        SecT571Field.add(S1, S2, t3);
        SecT571Field.add(U1, U2, t4);
        if (Nat576.isZero64(t4)) {
            if (Nat576.isZero64(t3)) {
                return twice();
            }
            return curve.getInfinity();
        }
        if (X2.isZero()) {
            ECPoint p = normalize();
            SecT571FieldElement X4 = (SecT571FieldElement) p.getXCoord();
            ECFieldElement Y1 = p.getYCoord();
            ECFieldElement L = Y1.add(L2).divide(X4);
            X3 = (SecT571FieldElement) L.square().add(L).add(X4).addOne();
            if (X3.isZero()) {
                return new SecT571R1Point(curve, X3, SecT571R1Curve.SecT571R1_B_SQRT, this.withCompression);
            }
            ECFieldElement Y3 = L.multiply(X4.add(X3)).add(X3).add(Y1);
            L3 = (SecT571FieldElement) Y3.divide(X3).add(X3);
            Z3 = (SecT571FieldElement) curve.fromBigInteger(ECConstants.ONE);
        } else {
            SecT571Field.square(t4, t4);
            long[] APrecomp = SecT571Field.precompMultiplicand(t3);
            SecT571Field.multiplyPrecomp(U1, APrecomp, t1);
            SecT571Field.multiplyPrecomp(U2, APrecomp, t2);
            X3 = new SecT571FieldElement(t1);
            SecT571Field.multiply(t1, t2, X3.x);
            if (X3.isZero()) {
                return new SecT571R1Point(curve, X3, SecT571R1Curve.SecT571R1_B_SQRT, this.withCompression);
            }
            Z3 = new SecT571FieldElement(t3);
            SecT571Field.multiplyPrecomp(t4, APrecomp, Z3.x);
            if (Z2Precomp != null) {
                SecT571Field.multiplyPrecomp(Z3.x, Z2Precomp, Z3.x);
            }
            long[] tt = Nat576.createExt64();
            SecT571Field.add(t2, t4, t4);
            SecT571Field.squareAddToExt(t4, tt);
            SecT571Field.add(L1.x, Z1.x, t4);
            SecT571Field.multiplyAddToExt(t4, Z3.x, tt);
            L3 = new SecT571FieldElement(t4);
            SecT571Field.reduce(tt, L3.x);
            if (Z1Precomp != null) {
                SecT571Field.multiplyPrecomp(Z3.x, Z1Precomp, Z3.x);
            }
        }
        return new SecT571R1Point(curve, X3, L3, new ECFieldElement[]{Z3}, this.withCompression);
    }

    @Override // org.spongycastle.math.ec.ECPoint
    public ECPoint twice() {
        long[] L1Z1;
        long[] Z1Sq;
        long[] X1Z1;
        if (isInfinity()) {
            return this;
        }
        ECCurve curve = getCurve();
        SecT571FieldElement X1 = (SecT571FieldElement) this.x;
        if (X1.isZero()) {
            return curve.getInfinity();
        }
        SecT571FieldElement L1 = (SecT571FieldElement) this.y;
        SecT571FieldElement Z1 = (SecT571FieldElement) this.zs[0];
        long[] t1 = Nat576.create64();
        long[] t2 = Nat576.create64();
        long[] Z1Precomp = Z1.isOne() ? null : SecT571Field.precompMultiplicand(Z1.x);
        if (Z1Precomp == null) {
            L1Z1 = L1.x;
            Z1Sq = Z1.x;
        } else {
            L1Z1 = t1;
            SecT571Field.multiplyPrecomp(L1.x, Z1Precomp, t1);
            Z1Sq = t2;
            SecT571Field.square(Z1.x, t2);
        }
        long[] T = Nat576.create64();
        SecT571Field.square(L1.x, T);
        SecT571Field.addBothTo(L1Z1, Z1Sq, T);
        if (Nat576.isZero64(T)) {
            return new SecT571R1Point(curve, new SecT571FieldElement(T), SecT571R1Curve.SecT571R1_B_SQRT, this.withCompression);
        }
        long[] tt = Nat576.createExt64();
        SecT571Field.multiplyAddToExt(T, L1Z1, tt);
        SecT571FieldElement X3 = new SecT571FieldElement(t1);
        SecT571Field.square(T, X3.x);
        SecT571FieldElement Z3 = new SecT571FieldElement(T);
        if (Z1Precomp != null) {
            SecT571Field.multiply(Z3.x, Z1Sq, Z3.x);
        }
        if (Z1Precomp == null) {
            X1Z1 = X1.x;
        } else {
            X1Z1 = t2;
            SecT571Field.multiplyPrecomp(X1.x, Z1Precomp, t2);
        }
        SecT571Field.squareAddToExt(X1Z1, tt);
        SecT571Field.reduce(tt, t2);
        SecT571Field.addBothTo(X3.x, Z3.x, t2);
        SecT571FieldElement L3 = new SecT571FieldElement(t2);
        return new SecT571R1Point(curve, X3, L3, new ECFieldElement[]{Z3}, this.withCompression);
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
        SecT571FieldElement X1 = (SecT571FieldElement) this.x;
        if (X1.isZero()) {
            return b;
        }
        SecT571FieldElement X2 = (SecT571FieldElement) b.getRawXCoord();
        SecT571FieldElement Z2 = (SecT571FieldElement) b.getZCoord(0);
        if (X2.isZero() || !Z2.isOne()) {
            return twice().add(b);
        }
        SecT571FieldElement L1 = (SecT571FieldElement) this.y;
        SecT571FieldElement Z1 = (SecT571FieldElement) this.zs[0];
        SecT571FieldElement L2 = (SecT571FieldElement) b.getRawYCoord();
        long[] t1 = Nat576.create64();
        long[] t2 = Nat576.create64();
        long[] t3 = Nat576.create64();
        long[] t4 = Nat576.create64();
        SecT571Field.square(X1.x, t1);
        SecT571Field.square(L1.x, t2);
        SecT571Field.square(Z1.x, t3);
        SecT571Field.multiply(L1.x, Z1.x, t4);
        SecT571Field.addBothTo(t3, t2, t4);
        long[] Z1SqPrecomp = SecT571Field.precompMultiplicand(t3);
        SecT571Field.multiplyPrecomp(L2.x, Z1SqPrecomp, t3);
        SecT571Field.add(t3, t2, t3);
        long[] tt = Nat576.createExt64();
        SecT571Field.multiplyAddToExt(t3, t4, tt);
        SecT571Field.multiplyPrecompAddToExt(t1, Z1SqPrecomp, tt);
        SecT571Field.reduce(tt, t3);
        SecT571Field.multiplyPrecomp(X2.x, Z1SqPrecomp, t1);
        SecT571Field.add(t1, t4, t2);
        SecT571Field.square(t2, t2);
        if (Nat576.isZero64(t2)) {
            if (Nat576.isZero64(t3)) {
                return b.twice();
            }
            return curve.getInfinity();
        }
        if (Nat576.isZero64(t3)) {
            return new SecT571R1Point(curve, new SecT571FieldElement(t3), SecT571R1Curve.SecT571R1_B_SQRT, this.withCompression);
        }
        SecT571FieldElement X3 = new SecT571FieldElement();
        SecT571Field.square(t3, X3.x);
        SecT571Field.multiply(X3.x, t1, X3.x);
        SecT571FieldElement Z3 = new SecT571FieldElement(t1);
        SecT571Field.multiply(t3, t2, Z3.x);
        SecT571Field.multiplyPrecomp(Z3.x, Z1SqPrecomp, Z3.x);
        SecT571FieldElement L3 = new SecT571FieldElement(t2);
        SecT571Field.add(t3, t2, L3.x);
        SecT571Field.square(L3.x, L3.x);
        Nat.zero64(18, tt);
        SecT571Field.multiplyAddToExt(L3.x, t4, tt);
        SecT571Field.addOne(L2.x, t4);
        SecT571Field.multiplyAddToExt(t4, Z3.x, tt);
        SecT571Field.reduce(tt, L3.x);
        return new SecT571R1Point(curve, X3, L3, new ECFieldElement[]{Z3}, this.withCompression);
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
        return new SecT571R1Point(this.curve, X, L.add(Z), new ECFieldElement[]{Z}, this.withCompression);
    }
}
