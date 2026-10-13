package org.bouncycastle.math.ec.custom.djb;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.AbstractECLookupTable;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/custom/djb/Curve25519.class */
public class Curve25519 extends ECCurve.AbstractFp {
    private static final int CURVE25519_DEFAULT_COORDS = 4;
    protected Curve25519Point infinity;
    public static final BigInteger q = Curve25519FieldElement.Q;
    private static final BigInteger C_a = new BigInteger(1, Hex.decodeStrict("2AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA984914A144"));
    private static final BigInteger C_b = new BigInteger(1, Hex.decodeStrict("7B425ED097B425ED097B425ED097B425ED097B425ED097B4260B5E9C7710C864"));
    private static final ECFieldElement[] CURVE25519_AFFINE_ZS = {new Curve25519FieldElement(ECConstants.ONE), new Curve25519FieldElement(C_a)};

    public Curve25519() {
        super(q);
        this.infinity = new Curve25519Point(this, null, null);
        this.a = fromBigInteger(C_a);
        this.b = fromBigInteger(C_b);
        this.order = new BigInteger(1, Hex.decodeStrict("1000000000000000000000000000000014DEF9DEA2F79CD65812631A5CF5D3ED"));
        this.cofactor = BigInteger.valueOf(8L);
        this.coord = 4;
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    protected ECCurve cloneCurve() {
        return new Curve25519();
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    public boolean supportsCoordinateSystem(int i) {
        switch (i) {
            case 4:
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
        return new Curve25519FieldElement(bigInteger);
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    protected ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        return new Curve25519Point(this, eCFieldElement, eCFieldElement2);
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    protected ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArr) {
        return new Curve25519Point(this, eCFieldElement, eCFieldElement2, eCFieldElementArr);
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    public ECPoint getInfinity() {
        return this.infinity;
    }

    @Override // org.bouncycastle.math.ec.ECCurve
    public ECLookupTable createCacheSafeLookupTable(ECPoint[] eCPointArr, int i, final int i2) {
        final int[] iArr = new int[i2 * 8 * 2];
        int i3 = 0;
        for (int i4 = 0; i4 < i2; i4++) {
            ECPoint eCPoint = eCPointArr[i + i4];
            Nat256.copy(((Curve25519FieldElement) eCPoint.getRawXCoord()).x, 0, iArr, i3);
            int i5 = i3 + 8;
            Nat256.copy(((Curve25519FieldElement) eCPoint.getRawYCoord()).x, 0, iArr, i5);
            i3 = i5 + 8;
        }
        return new AbstractECLookupTable() { // from class: org.bouncycastle.math.ec.custom.djb.Curve25519.1
            @Override // org.bouncycastle.math.ec.ECLookupTable
            public int getSize() {
                return i2;
            }

            @Override // org.bouncycastle.math.ec.ECLookupTable
            public ECPoint lookup(int i6) {
                int[] iArrCreate = Nat256.create();
                int[] iArrCreate2 = Nat256.create();
                int i7 = 0;
                for (int i8 = 0; i8 < i2; i8++) {
                    int i9 = ((i8 ^ i6) - 1) >> 31;
                    for (int i10 = 0; i10 < 8; i10++) {
                        int i11 = i10;
                        iArrCreate[i11] = iArrCreate[i11] ^ (iArr[i7 + i10] & i9);
                        int i12 = i10;
                        iArrCreate2[i12] = iArrCreate2[i12] ^ (iArr[(i7 + 8) + i10] & i9);
                    }
                    i7 += 16;
                }
                return createPoint(iArrCreate, iArrCreate2);
            }

            @Override // org.bouncycastle.math.ec.AbstractECLookupTable, org.bouncycastle.math.ec.ECLookupTable
            public ECPoint lookupVar(int i6) {
                int[] iArrCreate = Nat256.create();
                int[] iArrCreate2 = Nat256.create();
                int i7 = i6 * 8 * 2;
                for (int i8 = 0; i8 < 8; i8++) {
                    iArrCreate[i8] = iArr[i7 + i8];
                    iArrCreate2[i8] = iArr[i7 + 8 + i8];
                }
                return createPoint(iArrCreate, iArrCreate2);
            }

            private ECPoint createPoint(int[] iArr2, int[] iArr3) {
                return Curve25519.this.createRawPoint(new Curve25519FieldElement(iArr2), new Curve25519FieldElement(iArr3), Curve25519.CURVE25519_AFFINE_ZS);
            }
        };
    }

    @Override // org.bouncycastle.math.ec.ECCurve.AbstractFp, org.bouncycastle.math.ec.ECCurve
    public ECFieldElement randomFieldElement(SecureRandom secureRandom) {
        int[] iArrCreate = Nat256.create();
        Curve25519Field.random(secureRandom, iArrCreate);
        return new Curve25519FieldElement(iArrCreate);
    }

    @Override // org.bouncycastle.math.ec.ECCurve.AbstractFp, org.bouncycastle.math.ec.ECCurve
    public ECFieldElement randomFieldElementMult(SecureRandom secureRandom) {
        int[] iArrCreate = Nat256.create();
        Curve25519Field.randomMult(secureRandom, iArrCreate);
        return new Curve25519FieldElement(iArrCreate);
    }
}
