package org.spongycastle.math.ec.custom.sec;

import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.raw.Nat;
import org.spongycastle.math.raw.Nat384;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/ec/custom/sec/SecP384R1Point.class */
public class SecP384R1Point extends ECPoint.AbstractFp {
    public SecP384R1Point(ECCurve curve, ECFieldElement x, ECFieldElement y) {
        this(curve, x, y, false);
    }

    public SecP384R1Point(ECCurve curve, ECFieldElement x, ECFieldElement y, boolean withCompression) {
        super(curve, x, y);
        if ((x == null) != (y == null)) {
            throw new IllegalArgumentException("Exactly one of the field elements is null");
        }
        this.withCompression = withCompression;
    }

    SecP384R1Point(ECCurve curve, ECFieldElement x, ECFieldElement y, ECFieldElement[] zs, boolean withCompression) {
        super(curve, x, y, zs);
        this.withCompression = withCompression;
    }

    @Override // org.spongycastle.math.ec.ECPoint
    protected ECPoint detach() {
        return new SecP384R1Point(null, getAffineXCoord(), getAffineYCoord());
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
        SecP384R1FieldElement X1 = (SecP384R1FieldElement) this.x;
        SecP384R1FieldElement Y1 = (SecP384R1FieldElement) this.y;
        SecP384R1FieldElement X2 = (SecP384R1FieldElement) b.getXCoord();
        SecP384R1FieldElement Y2 = (SecP384R1FieldElement) b.getYCoord();
        SecP384R1FieldElement Z1 = (SecP384R1FieldElement) this.zs[0];
        SecP384R1FieldElement Z2 = (SecP384R1FieldElement) b.getZCoord(0);
        int[] tt1 = Nat.create(24);
        int[] tt2 = Nat.create(24);
        int[] t3 = Nat.create(12);
        int[] t4 = Nat.create(12);
        boolean Z1IsOne = Z1.isOne();
        if (Z1IsOne) {
            U2 = X2.x;
            S2 = Y2.x;
        } else {
            S2 = t3;
            SecP384R1Field.square(Z1.x, S2);
            U2 = tt2;
            SecP384R1Field.multiply(S2, X2.x, U2);
            SecP384R1Field.multiply(S2, Z1.x, S2);
            SecP384R1Field.multiply(S2, Y2.x, S2);
        }
        boolean Z2IsOne = Z2.isOne();
        if (Z2IsOne) {
            U1 = X1.x;
            S1 = Y1.x;
        } else {
            S1 = t4;
            SecP384R1Field.square(Z2.x, S1);
            U1 = tt1;
            SecP384R1Field.multiply(S1, X1.x, U1);
            SecP384R1Field.multiply(S1, Z2.x, S1);
            SecP384R1Field.multiply(S1, Y1.x, S1);
        }
        int[] H = Nat.create(12);
        SecP384R1Field.subtract(U1, U2, H);
        int[] R = Nat.create(12);
        SecP384R1Field.subtract(S1, S2, R);
        if (Nat.isZero(12, H)) {
            if (Nat.isZero(12, R)) {
                return twice();
            }
            return curve.getInfinity();
        }
        SecP384R1Field.square(H, t3);
        int[] G = Nat.create(12);
        SecP384R1Field.multiply(t3, H, G);
        SecP384R1Field.multiply(t3, U1, t3);
        SecP384R1Field.negate(G, G);
        Nat384.mul(S1, G, tt1);
        int c = Nat.addBothTo(12, t3, t3, G);
        SecP384R1Field.reduce32(c, G);
        SecP384R1FieldElement X3 = new SecP384R1FieldElement(t4);
        SecP384R1Field.square(R, X3.x);
        SecP384R1Field.subtract(X3.x, G, X3.x);
        SecP384R1FieldElement Y3 = new SecP384R1FieldElement(G);
        SecP384R1Field.subtract(t3, X3.x, Y3.x);
        Nat384.mul(Y3.x, R, tt2);
        SecP384R1Field.addExt(tt1, tt2, tt1);
        SecP384R1Field.reduce(tt1, Y3.x);
        SecP384R1FieldElement Z3 = new SecP384R1FieldElement(H);
        if (!Z1IsOne) {
            SecP384R1Field.multiply(Z3.x, Z1.x, Z3.x);
        }
        if (!Z2IsOne) {
            SecP384R1Field.multiply(Z3.x, Z2.x, Z3.x);
        }
        ECFieldElement[] zs = {Z3};
        return new SecP384R1Point(curve, X3, Y3, zs, this.withCompression);
    }

    @Override // org.spongycastle.math.ec.ECPoint
    public ECPoint twice() {
        if (isInfinity()) {
            return this;
        }
        ECCurve curve = getCurve();
        SecP384R1FieldElement Y1 = (SecP384R1FieldElement) this.y;
        if (Y1.isZero()) {
            return curve.getInfinity();
        }
        SecP384R1FieldElement X1 = (SecP384R1FieldElement) this.x;
        SecP384R1FieldElement Z1 = (SecP384R1FieldElement) this.zs[0];
        int[] t1 = Nat.create(12);
        int[] t2 = Nat.create(12);
        int[] Y1Squared = Nat.create(12);
        SecP384R1Field.square(Y1.x, Y1Squared);
        int[] T = Nat.create(12);
        SecP384R1Field.square(Y1Squared, T);
        boolean Z1IsOne = Z1.isOne();
        int[] Z1Squared = Z1.x;
        if (!Z1IsOne) {
            Z1Squared = t2;
            SecP384R1Field.square(Z1.x, Z1Squared);
        }
        SecP384R1Field.subtract(X1.x, Z1Squared, t1);
        SecP384R1Field.add(X1.x, Z1Squared, t2);
        SecP384R1Field.multiply(t2, t1, t2);
        int c = Nat.addBothTo(12, t2, t2, t2);
        SecP384R1Field.reduce32(c, t2);
        SecP384R1Field.multiply(Y1Squared, X1.x, Y1Squared);
        int c2 = Nat.shiftUpBits(12, Y1Squared, 2, 0);
        SecP384R1Field.reduce32(c2, Y1Squared);
        int c3 = Nat.shiftUpBits(12, T, 3, 0, t1);
        SecP384R1Field.reduce32(c3, t1);
        SecP384R1FieldElement X3 = new SecP384R1FieldElement(T);
        SecP384R1Field.square(t2, X3.x);
        SecP384R1Field.subtract(X3.x, Y1Squared, X3.x);
        SecP384R1Field.subtract(X3.x, Y1Squared, X3.x);
        SecP384R1FieldElement Y3 = new SecP384R1FieldElement(Y1Squared);
        SecP384R1Field.subtract(Y1Squared, X3.x, Y3.x);
        SecP384R1Field.multiply(Y3.x, t2, Y3.x);
        SecP384R1Field.subtract(Y3.x, t1, Y3.x);
        SecP384R1FieldElement Z3 = new SecP384R1FieldElement(t2);
        SecP384R1Field.twice(Y1.x, Z3.x);
        if (!Z1IsOne) {
            SecP384R1Field.multiply(Z3.x, Z1.x, Z3.x);
        }
        return new SecP384R1Point(curve, X3, Y3, new ECFieldElement[]{Z3}, this.withCompression);
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
        return new SecP384R1Point(this.curve, this.x, this.y.negate(), this.zs, this.withCompression);
    }
}
