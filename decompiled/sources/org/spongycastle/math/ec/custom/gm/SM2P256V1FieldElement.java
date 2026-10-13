package org.spongycastle.math.ec.custom.gm;

import java.math.BigInteger;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.raw.Mod;
import org.spongycastle.math.raw.Nat256;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/custom/gm/SM2P256V1FieldElement.class */
public class SM2P256V1FieldElement extends ECFieldElement {
    public static final BigInteger Q = SM2P256V1Curve.q;
    protected int[] x;

    public SM2P256V1FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.compareTo(Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SM2P256V1FieldElement");
        }
        this.x = SM2P256V1Field.fromBigInteger(x);
    }

    public SM2P256V1FieldElement() {
        this.x = Nat256.create();
    }

    protected SM2P256V1FieldElement(int[] x) {
        this.x = x;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean isZero() {
        return Nat256.isZero(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean isOne() {
        return Nat256.isOne(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public boolean testBitZero() {
        return Nat256.getBit(this.x, 0) == 1;
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public BigInteger toBigInteger() {
        return Nat256.toBigInteger(this.x);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public String getFieldName() {
        return "SM2P256V1Field";
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public int getFieldSize() {
        return Q.bitLength();
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement add(ECFieldElement b) {
        int[] z = Nat256.create();
        SM2P256V1Field.add(this.x, ((SM2P256V1FieldElement) b).x, z);
        return new SM2P256V1FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement addOne() {
        int[] z = Nat256.create();
        SM2P256V1Field.addOne(this.x, z);
        return new SM2P256V1FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement subtract(ECFieldElement b) {
        int[] z = Nat256.create();
        SM2P256V1Field.subtract(this.x, ((SM2P256V1FieldElement) b).x, z);
        return new SM2P256V1FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement multiply(ECFieldElement b) {
        int[] z = Nat256.create();
        SM2P256V1Field.multiply(this.x, ((SM2P256V1FieldElement) b).x, z);
        return new SM2P256V1FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement divide(ECFieldElement b) {
        int[] z = Nat256.create();
        Mod.invert(SM2P256V1Field.P, ((SM2P256V1FieldElement) b).x, z);
        SM2P256V1Field.multiply(z, this.x, z);
        return new SM2P256V1FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement negate() {
        int[] z = Nat256.create();
        SM2P256V1Field.negate(this.x, z);
        return new SM2P256V1FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement square() {
        int[] z = Nat256.create();
        SM2P256V1Field.square(this.x, z);
        return new SM2P256V1FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement invert() {
        int[] z = Nat256.create();
        Mod.invert(SM2P256V1Field.P, this.x, z);
        return new SM2P256V1FieldElement(z);
    }

    @Override // org.spongycastle.math.ec.ECFieldElement
    public ECFieldElement sqrt() {
        int[] x1 = this.x;
        if (Nat256.isZero(x1) || Nat256.isOne(x1)) {
            return this;
        }
        int[] x2 = Nat256.create();
        SM2P256V1Field.square(x1, x2);
        SM2P256V1Field.multiply(x2, x1, x2);
        int[] x4 = Nat256.create();
        SM2P256V1Field.squareN(x2, 2, x4);
        SM2P256V1Field.multiply(x4, x2, x4);
        int[] x6 = Nat256.create();
        SM2P256V1Field.squareN(x4, 2, x6);
        SM2P256V1Field.multiply(x6, x2, x6);
        SM2P256V1Field.squareN(x6, 6, x2);
        SM2P256V1Field.multiply(x2, x6, x2);
        int[] x24 = Nat256.create();
        SM2P256V1Field.squareN(x2, 12, x24);
        SM2P256V1Field.multiply(x24, x2, x24);
        SM2P256V1Field.squareN(x24, 6, x2);
        SM2P256V1Field.multiply(x2, x6, x2);
        SM2P256V1Field.square(x2, x6);
        SM2P256V1Field.multiply(x6, x1, x6);
        SM2P256V1Field.squareN(x6, 31, x24);
        SM2P256V1Field.multiply(x24, x6, x2);
        SM2P256V1Field.squareN(x24, 32, x24);
        SM2P256V1Field.multiply(x24, x2, x24);
        SM2P256V1Field.squareN(x24, 62, x24);
        SM2P256V1Field.multiply(x24, x2, x24);
        SM2P256V1Field.squareN(x24, 4, x24);
        SM2P256V1Field.multiply(x24, x4, x24);
        SM2P256V1Field.squareN(x24, 32, x24);
        SM2P256V1Field.multiply(x24, x1, x24);
        SM2P256V1Field.squareN(x24, 62, x24);
        SM2P256V1Field.square(x24, x4);
        if (Nat256.eq(x1, x4)) {
            return new SM2P256V1FieldElement(x24);
        }
        return null;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SM2P256V1FieldElement)) {
            return false;
        }
        SM2P256V1FieldElement o = (SM2P256V1FieldElement) other;
        return Nat256.eq(this.x, o.x);
    }

    public int hashCode() {
        return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 8);
    }
}
