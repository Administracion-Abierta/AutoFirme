package org.spongycastle.math.ec.custom.sec;

import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.raw.Nat;
import org.spongycastle.math.raw.Nat160;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/custom/sec/SecP160R2Point.class */
public class SecP160R2Point extends ECPoint.AbstractFp {
    public SecP160R2Point(ECCurve curve, ECFieldElement x, ECFieldElement y) {
        this(curve, x, y, false);
    }

    public SecP160R2Point(ECCurve curve, ECFieldElement x, ECFieldElement y, boolean withCompression) {
        super(curve, x, y);
        if ((x == null) != (y == null)) {
            throw new IllegalArgumentException("Exactly one of the field elements is null");
        }
        this.withCompression = withCompression;
    }

    SecP160R2Point(ECCurve curve, ECFieldElement x, ECFieldElement y, ECFieldElement[] zs, boolean withCompression) {
        super(curve, x, y, zs);
        this.withCompression = withCompression;
    }

    @Override // org.spongycastle.math.ec.ECPoint
    protected ECPoint detach() {
        return new SecP160R2Point(null, getAffineXCoord(), getAffineYCoord());
    }

    @Override // org.spongycastle.math.ec.ECPoint
    public ECPoint add(ECPoint b) {
        int[] S2;
        int[] U2;
        int[] S1;
        int[] U1;
        if (isInfinity()) {
            return b;
        }
        if (b.isInfinity()) {
            return this;
        }
        if (this == b) {
            return twice();
        }
        ECCurve curve = getCurve();
        SecP160R2FieldElement X1 = (SecP160R2FieldElement) this.x;
        SecP160R2FieldElement Y1 = (SecP160R2FieldElement) this.y;
        SecP160R2FieldElement X2 = (SecP160R2FieldElement) b.getXCoord();
        SecP160R2FieldElement Y2 = (SecP160R2FieldElement) b.getYCoord();
        SecP160R2FieldElement Z1 = (SecP160R2FieldElement) this.zs[0];
        SecP160R2FieldElement Z2 = (SecP160R2FieldElement) b.getZCoord(0);
        int[] tt1 = Nat160.createExt();
        int[] t2 = Nat160.create();
        int[] t3 = Nat160.create();
        int[] t4 = Nat160.create();
        boolean Z1IsOne = Z1.isOne();
        if (Z1IsOne) {
            U2 = X2.x;
            S2 = Y2.x;
        } else {
            S2 = t3;
            SecP160R2Field.square(Z1.x, S2);
            U2 = t2;
            SecP160R2Field.multiply(S2, X2.x, U2);
            SecP160R2Field.multiply(S2, Z1.x, S2);
            SecP160R2Field.multiply(S2, Y2.x, S2);
        }
        boolean Z2IsOne = Z2.isOne();
        if (Z2IsOne) {
            U1 = X1.x;
            S1 = Y1.x;
        } else {
            S1 = t4;
            SecP160R2Field.square(Z2.x, S1);
            U1 = tt1;
            SecP160R2Field.multiply(S1, X1.x, U1);
            SecP160R2Field.multiply(S1, Z2.x, S1);
            SecP160R2Field.multiply(S1, Y1.x, S1);
        }
        int[] H = Nat160.create();
        SecP160R2Field.subtract(U1, U2, H);
        SecP160R2Field.subtract(S1, S2, t2);
        if (Nat160.isZero(H)) {
            if (Nat160.isZero(t2)) {
                return twice();
            }
            return curve.getInfinity();
        }
        SecP160R2Field.square(H, t3);
        int[] G = Nat160.create();
        SecP160R2Field.multiply(t3, H, G);
        SecP160R2Field.multiply(t3, U1, t3);
        SecP160R2Field.negate(G, G);
        Nat160.mul(S1, G, tt1);
        int c = Nat160.addBothTo(t3, t3, G);
        SecP160R2Field.reduce32(c, G);
        SecP160R2FieldElement X3 = new SecP160R2FieldElement(t4);
        SecP160R2Field.square(t2, X3.x);
        SecP160R2Field.subtract(X3.x, G, X3.x);
        SecP160R2FieldElement Y3 = new SecP160R2FieldElement(G);
        SecP160R2Field.subtract(t3, X3.x, Y3.x);
        SecP160R2Field.multiplyAddToExt(Y3.x, t2, tt1);
        SecP160R2Field.reduce(tt1, Y3.x);
        SecP160R2FieldElement Z3 = new SecP160R2FieldElement(H);
        if (!Z1IsOne) {
            SecP160R2Field.multiply(Z3.x, Z1.x, Z3.x);
        }
        if (!Z2IsOne) {
            SecP160R2Field.multiply(Z3.x, Z2.x, Z3.x);
        }
        ECFieldElement[] zs = {Z3};
        return new SecP160R2Point(curve, X3, Y3, zs, this.withCompression);
    }

    @Override // org.spongycastle.math.ec.ECPoint
    public ECPoint twice() {
        if (isInfinity()) {
            return this;
        }
        ECCurve curve = getCurve();
        SecP160R2FieldElement Y1 = (SecP160R2FieldElement) this.y;
        if (Y1.isZero()) {
            return curve.getInfinity();
        }
        SecP160R2FieldElement X1 = (SecP160R2FieldElement) this.x;
        SecP160R2FieldElement Z1 = (SecP160R2FieldElement) this.zs[0];
        int[] t1 = Nat160.create();
        int[] t2 = Nat160.create();
        int[] Y1Squared = Nat160.create();
        SecP160R2Field.square(Y1.x, Y1Squared);
        int[] T = Nat160.create();
        SecP160R2Field.square(Y1Squared, T);
        boolean Z1IsOne = Z1.isOne();
        int[] Z1Squared = Z1.x;
        if (!Z1IsOne) {
            Z1Squared = t2;
            SecP160R2Field.square(Z1.x, Z1Squared);
        }
        SecP160R2Field.subtract(X1.x, Z1Squared, t1);
        SecP160R2Field.add(X1.x, Z1Squared, t2);
        SecP160R2Field.multiply(t2, t1, t2);
        int c = Nat160.addBothTo(t2, t2, t2);
        SecP160R2Field.reduce32(c, t2);
        SecP160R2Field.multiply(Y1Squared, X1.x, Y1Squared);
        int c2 = Nat.shiftUpBits(5, Y1Squared, 2, 0);
        SecP160R2Field.reduce32(c2, Y1Squared);
        int c3 = Nat.shiftUpBits(5, T, 3, 0, t1);
        SecP160R2Field.reduce32(c3, t1);
        SecP160R2FieldElement X3 = new SecP160R2FieldElement(T);
        SecP160R2Field.square(t2, X3.x);
        SecP160R2Field.subtract(X3.x, Y1Squared, X3.x);
        SecP160R2Field.subtract(X3.x, Y1Squared, X3.x);
        SecP160R2FieldElement Y3 = new SecP160R2FieldElement(Y1Squared);
        SecP160R2Field.subtract(Y1Squared, X3.x, Y3.x);
        SecP160R2Field.multiply(Y3.x, t2, Y3.x);
        SecP160R2Field.subtract(Y3.x, t1, Y3.x);
        SecP160R2FieldElement Z3 = new SecP160R2FieldElement(t2);
        SecP160R2Field.twice(Y1.x, Z3.x);
        if (!Z1IsOne) {
            SecP160R2Field.multiply(Z3.x, Z1.x, Z3.x);
        }
        return new SecP160R2Point(curve, X3, Y3, new ECFieldElement[]{Z3}, this.withCompression);
    }

    @Override // org.spongycastle.math.ec.ECPoint
    public ECPoint twicePlus(ECPoint b) {
        if (this == b) {
            return threeTimes();
        }
        if (isInfinity()) {
            return b;
        }
        if (b.isInfinity()) {
            return twice();
        }
        ECFieldElement Y1 = this.y;
        if (Y1.isZero()) {
            return b;
        }
        return twice().add(b);
    }

    @Override // org.spongycastle.math.ec.ECPoint
    public ECPoint threeTimes() {
        if (isInfinity() || this.y.isZero()) {
            return this;
        }
        return twice().add(this);
    }

    @Override // org.spongycastle.math.ec.ECPoint
    public ECPoint negate() {
        if (isInfinity()) {
            return this;
        }
        return new SecP160R2Point(this.curve, this.x, this.y.negate(), this.zs, this.withCompression);
    }
}
