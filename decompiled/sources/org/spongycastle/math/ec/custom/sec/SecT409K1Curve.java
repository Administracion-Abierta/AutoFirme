package org.spongycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.ec.ECMultiplier;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.ec.WTauNafMultiplier;
import org.spongycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/ec/custom/sec/SecT409K1Curve.class */
public class SecT409K1Curve extends ECCurve.AbstractF2m {
    private static final int SecT409K1_DEFAULT_COORDS = 6;
    protected SecT409K1Point infinity;

    public SecT409K1Curve() {
        super(409, 87, 0, 0);
        this.infinity = new SecT409K1Point(this, null, null);
        this.a = fromBigInteger(BigInteger.valueOf(0L));
        this.b = fromBigInteger(BigInteger.valueOf(1L));
        this.order = new BigInteger(1, Hex.decode("7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFE5F83B2D4EA20400EC4557D5ED3E3E7CA5B4B5C83B8E01E5FCF"));
        this.cofactor = BigInteger.valueOf(4L);
        this.coord = 6;
    }

    @Override // org.spongycastle.math.ec.ECCurve
    protected ECCurve cloneCurve() {
        return new SecT409K1Curve();
    }

    @Override // org.spongycastle.math.ec.ECCurve
    public boolean supportsCoordinateSystem(int coord) {
        switch (coord) {
            case 6:
                return true;
            default:
                return false;
        }
    }

    @Override // org.spongycastle.math.ec.ECCurve
    protected ECMultiplier createDefaultMultiplier() {
        return new WTauNafMultiplier();
    }

    @Override // org.spongycastle.math.ec.ECCurve
    public int getFieldSize() {
        return 409;
    }

    @Override // org.spongycastle.math.ec.ECCurve
    public ECFieldElement fromBigInteger(BigInteger x) {
        return new SecT409FieldElement(x);
    }

    @Override // org.spongycastle.math.ec.ECCurve
    protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y, boolean withCompression) {
        return new SecT409K1Point(this, x, y, withCompression);
    }

    @Override // org.spongycastle.math.ec.ECCurve
    protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y, ECFieldElement[] zs, boolean withCompression) {
        return new SecT409K1Point(this, x, y, zs, withCompression);
    }

    @Override // org.spongycastle.math.ec.ECCurve
    public ECPoint getInfinity() {
        return this.infinity;
    }

    @Override // org.spongycastle.math.ec.ECCurve.AbstractF2m
    public boolean isKoblitz() {
        return true;
    }

    public int getM() {
        return 409;
    }

    public boolean isTrinomial() {
        return true;
    }

    public int getK1() {
        return 87;
    }

    public int getK2() {
        return 0;
    }

    public int getK3() {
        return 0;
    }
}
