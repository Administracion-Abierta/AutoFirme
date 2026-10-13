package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.AbstractECLookupTable;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat128;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/custom/sec/SecP128R1Curve.class */
public class SecP128R1Curve extends ECCurve.AbstractFp {
    private static final int SECP128R1_DEFAULT_COORDS = 2;
    protected SecP128R1Point infinity;
    public static final BigInteger q = SecP128R1FieldElement.Q;
    private static final ECFieldElement[] SECP128R1_AFFINE_ZS = {new SecP128R1FieldElement(ECConstants.ONE)};

    public SecP128R1Curve() {
        super(q);
        this.infinity = new SecP128R1Point(this, null, null);
        this.a = fromBigInteger(new BigInteger(1, Hex.decodeStrict("FFFFFFFDFFFFFFFFFFFFFFFFFFFFFFFC")));
        this.b = fromBigInteger(new BigInteger(1, Hex.decodeStrict("E87579C11079F43DD824993C2CEE5ED3")));
        this.order = new BigInteger(1, Hex.decodeStrict("FFFFFFFE0000000075A30D1B9038A115"));
        this.cofactor = BigInteger.valueOf(1L);
        this.coord = 2;
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    protected ECCurve cloneCurve() {
        return new SecP128R1Curve();
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    public boolean supportsCoordinateSystem(int i) {
        switch (i) {
            case 2:
                return true;
            default:
                return false;
        }
    }

    public BigInteger getQ() {
        return q;
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    public int getFieldSize() {
        return q.bitLength();
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    public ECFieldElement fromBigInteger(BigInteger bigInteger) {
        return new SecP128R1FieldElement(bigInteger);
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    protected ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        return new SecP128R1Point(this, eCFieldElement, eCFieldElement2);
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    protected ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArr) {
        return new SecP128R1Point(this, eCFieldElement, eCFieldElement2, eCFieldElementArr);
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    public ECPoint getInfinity() {
        return this.infinity;
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    public ECLookupTable createCacheSafeLookupTable(ECPoint[] eCPointArr, int i, final int i2) {
        final int[] iArr = new int[i2 * 4 * 2];
        int i3 = 0;
        for (int i4 = 0; i4 < i2; i4++) {
            ECPoint eCPoint = eCPointArr[i + i4];
            Nat128.copy(((SecP128R1FieldElement) eCPoint.getRawXCoord()).x, 0, iArr, i3);
            int i5 = i3 + 4;
            Nat128.copy(((SecP128R1FieldElement) eCPoint.getRawYCoord()).x, 0, iArr, i5);
            i3 = i5 + 4;
        }
        return new AbstractECLookupTable() { // from class: org.bouncycastle.math.ec.custom.sec.SecP128R1Curve.1
            @Override // org.bouncycastle.math.ec.ECLookupTable
            public int getSize() {
                return i2;
            }

            @Override // org.bouncycastle.math.ec.ECLookupTable
            public ECPoint lookup(int i6) {
                int[] iArrCreate = Nat128.create();
                int[] iArrCreate2 = Nat128.create();
                int i7 = 0;
                for (int i8 = 0; i8 < i2; i8++) {
                    int i9 = ((i8 ^ i6) - 1) >> 31;
                    for (int i10 = 0; i10 < 4; i10++) {
                        int i11 = i10;
                        iArrCreate[i11] = iArrCreate[i11] ^ (iArr[i7 + i10] & i9);
                        int i12 = i10;
                        iArrCreate2[i12] = iArrCreate2[i12] ^ (iArr[(i7 + 4) + i10] & i9);
                    }
                    i7 += 8;
                }
                return createPoint(iArrCreate, iArrCreate2);
            }

            @Override // org.bouncycastle.math.ec.AbstractECLookupTable, org.bouncycastle.math.ec.ECLookupTable
            public ECPoint lookupVar(int i6) {
                int[] iArrCreate = Nat128.create();
                int[] iArrCreate2 = Nat128.create();
                int i7 = i6 * 4 * 2;
                for (int i8 = 0; i8 < 4; i8++) {
                    iArrCreate[i8] = iArr[i7 + i8];
                    iArrCreate2[i8] = iArr[i7 + 4 + i8];
                }
                return createPoint(iArrCreate, iArrCreate2);
            }

            private ECPoint createPoint(int[] iArr2, int[] iArr3) {
                return SecP128R1Curve.this.createRawPoint(new SecP128R1FieldElement(iArr2), new SecP128R1FieldElement(iArr3), SecP128R1Curve.SECP128R1_AFFINE_ZS);
            }
        };
    }

    @Override // org.bouncycastle.math.ec.ECCurve.AbstractFp, org.bouncycastle.math.ec.ECCurve
    public ECFieldElement randomFieldElement(SecureRandom secureRandom) {
        int[] iArrCreate = Nat128.create();
        SecP128R1Field.random(secureRandom, iArrCreate);
        return new SecP128R1FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECCurve.AbstractFp, org.bouncycastle.math.ec.ECCurve
    public ECFieldElement randomFieldElementMult(SecureRandom secureRandom) {
        int[] iArrCreate = Nat128.create();
        SecP128R1Field.randomMult(secureRandom, iArrCreate);
        return new SecP128R1FieldElement(iArrCreate);
    }
}
