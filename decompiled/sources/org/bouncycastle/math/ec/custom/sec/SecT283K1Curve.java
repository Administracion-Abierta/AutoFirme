package org.bouncycastle.math.ec.custom.sec;

import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.math.BigInteger;
import org.bouncycastle.math.ec.AbstractECLookupTable;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.WTauNafMultiplier;
import org.bouncycastle.math.raw.Nat320;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/custom/sec/SecT283K1Curve.class */
public class SecT283K1Curve extends ECCurve.AbstractF2m {
    private static final int SECT283K1_DEFAULT_COORDS = 6;
    private static final ECFieldElement[] SECT283K1_AFFINE_ZS = {new SecT283FieldElement(ECConstants.ONE)};
    protected SecT283K1Point infinity;

    public SecT283K1Curve() {
        super(TIFF.TAG_Y_RESOLUTION, 5, 7, 12);
        this.infinity = new SecT283K1Point(this, null, null);
        this.a = fromBigInteger(BigInteger.valueOf(0L));
        this.b = fromBigInteger(BigInteger.valueOf(1L));
        this.order = new BigInteger(1, Hex.decodeStrict("01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFE9AE2ED07577265DFF7F94451E061E163C61"));
        this.cofactor = BigInteger.valueOf(4L);
        this.coord = 6;
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    protected ECCurve cloneCurve() {
        return new SecT283K1Curve();
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    public boolean supportsCoordinateSystem(int i) {
        switch (i) {
            case 6:
                return true;
            default:
                return false;
        }
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    protected ECMultiplier createDefaultMultiplier() {
        return new WTauNafMultiplier();
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    public int getFieldSize() {
        return TIFF.TAG_Y_RESOLUTION;
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    public ECFieldElement fromBigInteger(BigInteger bigInteger) {
        return new SecT283FieldElement(bigInteger);
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    protected ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        return new SecT283K1Point(this, eCFieldElement, eCFieldElement2);
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    protected ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArr) {
        return new SecT283K1Point(this, eCFieldElement, eCFieldElement2, eCFieldElementArr);
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    public ECPoint getInfinity() {
        return this.infinity;
    }

    @Override // org.bouncycastle.math.ec.ECCurve.AbstractF2m
    public boolean isKoblitz() {
        return true;
    }

    public int getM() {
        return TIFF.TAG_Y_RESOLUTION;
    }

    public boolean isTrinomial() {
        return false;
    }

    public int getK1() {
        return 5;
    }

    public int getK2() {
        return 7;
    }

    public int getK3() {
        return 12;
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    public ECLookupTable createCacheSafeLookupTable(ECPoint[] eCPointArr, int i, final int i2) {
        final long[] jArr = new long[i2 * 5 * 2];
        int i3 = 0;
        for (int i4 = 0; i4 < i2; i4++) {
            ECPoint eCPoint = eCPointArr[i + i4];
            Nat320.copy64(((SecT283FieldElement) eCPoint.getRawXCoord()).x, 0, jArr, i3);
            int i5 = i3 + 5;
            Nat320.copy64(((SecT283FieldElement) eCPoint.getRawYCoord()).x, 0, jArr, i5);
            i3 = i5 + 5;
        }
        return new AbstractECLookupTable() { // from class: org.bouncycastle.math.ec.custom.sec.SecT283K1Curve.1
            @Override // org.bouncycastle.math.ec.ECLookupTable
            public int getSize() {
                return i2;
            }

            @Override // org.bouncycastle.math.ec.ECLookupTable
            public ECPoint lookup(int i6) {
                long[] jArrCreate64 = Nat320.create64();
                long[] jArrCreate65 = Nat320.create64();
                int i7 = 0;
                for (int i8 = 0; i8 < i2; i8++) {
                    long j = ((i8 ^ i6) - 1) >> 31;
                    for (int i9 = 0; i9 < 5; i9++) {
                        int i10 = i9;
                        jArrCreate64[i10] = jArrCreate64[i10] ^ (jArr[i7 + i9] & j);
                        int i11 = i9;
                        jArrCreate65[i11] = jArrCreate65[i11] ^ (jArr[(i7 + 5) + i9] & j);
                    }
                    i7 += 10;
                }
                return createPoint(jArrCreate64, jArrCreate65);
            }

            @Override // org.bouncycastle.math.ec.AbstractECLookupTable, org.bouncycastle.math.ec.ECLookupTable
            public ECPoint lookupVar(int i6) {
                long[] jArrCreate64 = Nat320.create64();
                long[] jArrCreate65 = Nat320.create64();
                int i7 = i6 * 5 * 2;
                for (int i8 = 0; i8 < 5; i8++) {
                    jArrCreate64[i8] = jArr[i7 + i8];
                    jArrCreate65[i8] = jArr[i7 + 5 + i8];
                }
                return createPoint(jArrCreate64, jArrCreate65);
            }

            private ECPoint createPoint(long[] jArr2, long[] jArr3) {
                return SecT283K1Curve.this.createRawPoint(new SecT283FieldElement(jArr2), new SecT283FieldElement(jArr3), SecT283K1Curve.SECT283K1_AFFINE_ZS);
            }
        };
    }
}
