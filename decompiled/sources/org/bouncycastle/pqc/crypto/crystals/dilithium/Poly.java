package org.bouncycastle.pqc.crypto.crystals.dilithium;

import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import org.bouncycastle.crypto.digests.SHAKEDigest;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/crystals/dilithium/Poly.class */
class Poly {
    private final int polyUniformNBlocks;
    private final DilithiumEngine engine;
    private final Symmetric symmetric;
    private final int dilithiumN = 256;
    private int[] coeffs = new int[this.dilithiumN];

    public Poly(DilithiumEngine dilithiumEngine) {
        this.engine = dilithiumEngine;
        this.symmetric = dilithiumEngine.GetSymmetric();
        this.polyUniformNBlocks = ((768 + this.symmetric.stream128BlockBytes) - 1) / this.symmetric.stream128BlockBytes;
    }

    public int getCoeffIndex(int i) {
        return this.coeffs[i];
    }

    public int[] getCoeffs() {
        return this.coeffs;
    }

    public void setCoeffIndex(int i, int i2) {
        this.coeffs[i] = i2;
    }

    public void setCoeffs(int[] iArr) {
        this.coeffs = iArr;
    }

    public void uniformBlocks(byte[] bArr, short s) {
        int i = this.polyUniformNBlocks * this.symmetric.stream128BlockBytes;
        byte[] bArr2 = new byte[i + 2];
        this.symmetric.stream128init(bArr, s);
        this.symmetric.stream128squeezeBlocks(bArr2, 0, i);
        int iRejectUniform = rejectUniform(this, 0, this.dilithiumN, bArr2, i);
        while (true) {
            int i2 = iRejectUniform;
            if (i2 >= this.dilithiumN) {
                return;
            }
            int i3 = i % 3;
            for (int i4 = 0; i4 < i3; i4++) {
                bArr2[i4] = bArr2[(i - i3) + i4];
            }
            this.symmetric.stream128squeezeBlocks(bArr2, i3, this.symmetric.stream128BlockBytes);
            i = this.symmetric.stream128BlockBytes + i3;
            iRejectUniform = i2 + rejectUniform(this, i2, this.dilithiumN - i2, bArr2, i);
        }
    }

    private static int rejectUniform(Poly poly, int i, int i2, byte[] bArr, int i3) {
        int i4 = 0;
        int i5 = 0;
        while (i5 < i2 && i4 + 3 <= i3) {
            int i6 = i4;
            int i7 = i4 + 1;
            int i8 = i7 + 1;
            int i9 = (bArr[i6] & 255) | ((bArr[i7] & 255) << 8);
            i4 = i8 + 1;
            int i10 = (i9 | ((bArr[i8] & 255) << 16)) & 8388607;
            if (i10 < 8380417) {
                poly.setCoeffIndex(i + i5, i10);
                i5++;
            }
        }
        return i5;
    }

    public void uniformEta(byte[] bArr, short s) {
        int i;
        int dilithiumEta = this.engine.getDilithiumEta();
        if (this.engine.getDilithiumEta() == 2) {
            i = ((136 + this.symmetric.stream256BlockBytes) - 1) / this.symmetric.stream256BlockBytes;
        } else {
            if (this.engine.getDilithiumEta() != 4) {
                throw new RuntimeException("Wrong Dilithium Eta!");
            }
            i = ((227 + this.symmetric.stream256BlockBytes) - 1) / this.symmetric.stream256BlockBytes;
        }
        int i2 = i * this.symmetric.stream256BlockBytes;
        byte[] bArr2 = new byte[i2];
        this.symmetric.stream256init(bArr, s);
        this.symmetric.stream256squeezeBlocks(bArr2, 0, i2);
        int iRejectEta = rejectEta(this, 0, this.dilithiumN, bArr2, i2, dilithiumEta);
        while (true) {
            int i3 = iRejectEta;
            if (i3 >= 256) {
                return;
            }
            this.symmetric.stream256squeezeBlocks(bArr2, 0, this.symmetric.stream256BlockBytes);
            iRejectEta = i3 + rejectEta(this, i3, this.dilithiumN - i3, bArr2, this.symmetric.stream256BlockBytes, dilithiumEta);
        }
    }

    private static int rejectEta(Poly poly, int i, int i2, byte[] bArr, int i3, int i4) {
        int i5 = 0;
        int i6 = 0;
        while (i6 < i2 && i5 < i3) {
            int i7 = bArr[i5] & 255 & 15;
            int i8 = i5;
            i5++;
            int i9 = (bArr[i8] & 255) >> 4;
            if (i4 == 2) {
                if (i7 < 15) {
                    poly.setCoeffIndex(i + i6, 2 - (i7 - (((205 * i7) >> 10) * 5)));
                    i6++;
                }
                if (i9 < 15 && i6 < i2) {
                    poly.setCoeffIndex(i + i6, 2 - (i9 - (((205 * i9) >> 10) * 5)));
                    i6++;
                }
            } else if (i4 == 4) {
                if (i7 < 9) {
                    poly.setCoeffIndex(i + i6, 4 - i7);
                    i6++;
                }
                if (i9 < 9 && i6 < i2) {
                    poly.setCoeffIndex(i + i6, 4 - i9);
                    i6++;
                }
            }
        }
        return i6;
    }

    public void polyNtt() {
        setCoeffs(Ntt.ntt(this.coeffs));
    }

    public void pointwiseMontgomery(Poly poly, Poly poly2) {
        for (int i = 0; i < this.dilithiumN; i++) {
            setCoeffIndex(i, Reduce.montgomeryReduce(((long) poly.getCoeffIndex(i)) * ((long) poly2.getCoeffIndex(i))));
        }
    }

    public void pointwiseAccountMontgomery(PolyVecL polyVecL, PolyVecL polyVecL2) {
        Poly poly = new Poly(this.engine);
        pointwiseMontgomery(polyVecL.getVectorIndex(0), polyVecL2.getVectorIndex(0));
        for (int i = 1; i < this.engine.getDilithiumL(); i++) {
            poly.pointwiseMontgomery(polyVecL.getVectorIndex(i), polyVecL2.getVectorIndex(i));
            addPoly(poly);
        }
    }

    public void addPoly(Poly poly) {
        for (int i = 0; i < this.dilithiumN; i++) {
            setCoeffIndex(i, getCoeffIndex(i) + poly.getCoeffIndex(i));
        }
    }

    public void reduce() {
        for (int i = 0; i < this.dilithiumN; i++) {
            setCoeffIndex(i, Reduce.reduce32(getCoeffIndex(i)));
        }
    }

    public void invNttToMont() {
        setCoeffs(Ntt.invNttToMont(getCoeffs()));
    }

    public void conditionalAddQ() {
        for (int i = 0; i < this.dilithiumN; i++) {
            setCoeffIndex(i, Reduce.conditionalAddQ(getCoeffIndex(i)));
        }
    }

    public void power2Round(Poly poly) {
        for (int i = 0; i < this.dilithiumN; i++) {
            int[] iArrPower2Round = Rounding.power2Round(getCoeffIndex(i));
            setCoeffIndex(i, iArrPower2Round[0]);
            poly.setCoeffIndex(i, iArrPower2Round[1]);
        }
    }

    public byte[] polyt1Pack() {
        byte[] bArr = new byte[320];
        for (int i = 0; i < this.dilithiumN / 4; i++) {
            bArr[(5 * i) + 0] = (byte) (this.coeffs[(4 * i) + 0] >> 0);
            bArr[(5 * i) + 1] = (byte) ((this.coeffs[(4 * i) + 0] >> 8) | (this.coeffs[(4 * i) + 1] << 2));
            bArr[(5 * i) + 2] = (byte) ((this.coeffs[(4 * i) + 1] >> 6) | (this.coeffs[(4 * i) + 2] << 4));
            bArr[(5 * i) + 3] = (byte) ((this.coeffs[(4 * i) + 2] >> 4) | (this.coeffs[(4 * i) + 3] << 6));
            bArr[(5 * i) + 4] = (byte) (this.coeffs[(4 * i) + 3] >> 2);
        }
        return bArr;
    }

    public void polyt1Unpack(byte[] bArr) {
        for (int i = 0; i < this.dilithiumN / 4; i++) {
            setCoeffIndex((4 * i) + 0, (((bArr[(5 * i) + 0] & 255) >> 0) | ((bArr[(5 * i) + 1] & 255) << 8)) & WinUser.CF_GDIOBJLAST);
            setCoeffIndex((4 * i) + 1, (((bArr[(5 * i) + 1] & 255) >> 2) | ((bArr[(5 * i) + 2] & 255) << 6)) & WinUser.CF_GDIOBJLAST);
            setCoeffIndex((4 * i) + 2, (((bArr[(5 * i) + 2] & 255) >> 4) | ((bArr[(5 * i) + 3] & 255) << 4)) & WinUser.CF_GDIOBJLAST);
            setCoeffIndex((4 * i) + 3, (((bArr[(5 * i) + 3] & 255) >> 6) | ((bArr[(5 * i) + 4] & 255) << 2)) & WinUser.CF_GDIOBJLAST);
        }
    }

    public byte[] polyEtaPack(byte[] bArr, int i) {
        byte[] bArr2 = new byte[8];
        if (this.engine.getDilithiumEta() == 2) {
            for (int i2 = 0; i2 < this.dilithiumN / 8; i2++) {
                bArr2[0] = (byte) (this.engine.getDilithiumEta() - getCoeffIndex((8 * i2) + 0));
                bArr2[1] = (byte) (this.engine.getDilithiumEta() - getCoeffIndex((8 * i2) + 1));
                bArr2[2] = (byte) (this.engine.getDilithiumEta() - getCoeffIndex((8 * i2) + 2));
                bArr2[3] = (byte) (this.engine.getDilithiumEta() - getCoeffIndex((8 * i2) + 3));
                bArr2[4] = (byte) (this.engine.getDilithiumEta() - getCoeffIndex((8 * i2) + 4));
                bArr2[5] = (byte) (this.engine.getDilithiumEta() - getCoeffIndex((8 * i2) + 5));
                bArr2[6] = (byte) (this.engine.getDilithiumEta() - getCoeffIndex((8 * i2) + 6));
                bArr2[7] = (byte) (this.engine.getDilithiumEta() - getCoeffIndex((8 * i2) + 7));
                bArr[i + (3 * i2) + 0] = (byte) ((bArr2[0] >> 0) | (bArr2[1] << 3) | (bArr2[2] << 6));
                bArr[i + (3 * i2) + 1] = (byte) ((bArr2[2] >> 2) | (bArr2[3] << 1) | (bArr2[4] << 4) | (bArr2[5] << 7));
                bArr[i + (3 * i2) + 2] = (byte) ((bArr2[5] >> 1) | (bArr2[6] << 2) | (bArr2[7] << 5));
            }
        } else {
            if (this.engine.getDilithiumEta() != 4) {
                throw new RuntimeException("Eta needs to be 2 or 4!");
            }
            for (int i3 = 0; i3 < this.dilithiumN / 2; i3++) {
                bArr2[0] = (byte) (this.engine.getDilithiumEta() - getCoeffIndex((2 * i3) + 0));
                bArr2[1] = (byte) (this.engine.getDilithiumEta() - getCoeffIndex((2 * i3) + 1));
                bArr[i + i3] = (byte) (bArr2[0] | (bArr2[1] << 4));
            }
        }
        return bArr;
    }

    public void polyEtaUnpack(byte[] bArr, int i) {
        int dilithiumEta = this.engine.getDilithiumEta();
        if (this.engine.getDilithiumEta() != 2) {
            if (this.engine.getDilithiumEta() == 4) {
                for (int i2 = 0; i2 < this.dilithiumN / 2; i2++) {
                    setCoeffIndex((2 * i2) + 0, bArr[i + i2] & 15);
                    setCoeffIndex((2 * i2) + 1, (bArr[i + i2] & 255) >> 4);
                    setCoeffIndex((2 * i2) + 0, dilithiumEta - getCoeffIndex((2 * i2) + 0));
                    setCoeffIndex((2 * i2) + 1, dilithiumEta - getCoeffIndex((2 * i2) + 1));
                }
                return;
            }
            return;
        }
        for (int i3 = 0; i3 < this.dilithiumN / 8; i3++) {
            int i4 = i + (3 * i3);
            setCoeffIndex((8 * i3) + 0, ((bArr[i4 + 0] & 255) >> 0) & 7);
            setCoeffIndex((8 * i3) + 1, ((bArr[i4 + 0] & 255) >> 3) & 7);
            setCoeffIndex((8 * i3) + 2, ((bArr[i4 + 0] & 255) >> 6) | (((bArr[i4 + 1] & 255) << 2) & 7));
            setCoeffIndex((8 * i3) + 3, ((bArr[i4 + 1] & 255) >> 1) & 7);
            setCoeffIndex((8 * i3) + 4, ((bArr[i4 + 1] & 255) >> 4) & 7);
            setCoeffIndex((8 * i3) + 5, ((bArr[i4 + 1] & 255) >> 7) | (((bArr[i4 + 2] & 255) << 1) & 7));
            setCoeffIndex((8 * i3) + 6, ((bArr[i4 + 2] & 255) >> 2) & 7);
            setCoeffIndex((8 * i3) + 7, ((bArr[i4 + 2] & 255) >> 5) & 7);
            setCoeffIndex((8 * i3) + 0, dilithiumEta - getCoeffIndex((8 * i3) + 0));
            setCoeffIndex((8 * i3) + 1, dilithiumEta - getCoeffIndex((8 * i3) + 1));
            setCoeffIndex((8 * i3) + 2, dilithiumEta - getCoeffIndex((8 * i3) + 2));
            setCoeffIndex((8 * i3) + 3, dilithiumEta - getCoeffIndex((8 * i3) + 3));
            setCoeffIndex((8 * i3) + 4, dilithiumEta - getCoeffIndex((8 * i3) + 4));
            setCoeffIndex((8 * i3) + 5, dilithiumEta - getCoeffIndex((8 * i3) + 5));
            setCoeffIndex((8 * i3) + 6, dilithiumEta - getCoeffIndex((8 * i3) + 6));
            setCoeffIndex((8 * i3) + 7, dilithiumEta - getCoeffIndex((8 * i3) + 7));
        }
    }

    public byte[] polyt0Pack(byte[] bArr, int i) {
        int[] iArr = new int[8];
        for (int i2 = 0; i2 < this.dilithiumN / 8; i2++) {
            iArr[0] = 4096 - getCoeffIndex((8 * i2) + 0);
            iArr[1] = 4096 - getCoeffIndex((8 * i2) + 1);
            iArr[2] = 4096 - getCoeffIndex((8 * i2) + 2);
            iArr[3] = 4096 - getCoeffIndex((8 * i2) + 3);
            iArr[4] = 4096 - getCoeffIndex((8 * i2) + 4);
            iArr[5] = 4096 - getCoeffIndex((8 * i2) + 5);
            iArr[6] = 4096 - getCoeffIndex((8 * i2) + 6);
            iArr[7] = 4096 - getCoeffIndex((8 * i2) + 7);
            int i3 = i + (13 * i2);
            bArr[i3 + 0] = (byte) iArr[0];
            bArr[i3 + 1] = (byte) (iArr[0] >> 8);
            bArr[i3 + 1] = (byte) (bArr[i3 + 1] | ((byte) (iArr[1] << 5)));
            bArr[i3 + 2] = (byte) (iArr[1] >> 3);
            bArr[i3 + 3] = (byte) (iArr[1] >> 11);
            bArr[i3 + 3] = (byte) (bArr[i3 + 3] | ((byte) (iArr[2] << 2)));
            bArr[i3 + 4] = (byte) (iArr[2] >> 6);
            bArr[i3 + 4] = (byte) (bArr[i3 + 4] | ((byte) (iArr[3] << 7)));
            bArr[i3 + 5] = (byte) (iArr[3] >> 1);
            bArr[i3 + 6] = (byte) (iArr[3] >> 9);
            bArr[i3 + 6] = (byte) (bArr[i3 + 6] | ((byte) (iArr[4] << 4)));
            bArr[i3 + 7] = (byte) (iArr[4] >> 4);
            bArr[i3 + 8] = (byte) (iArr[4] >> 12);
            bArr[i3 + 8] = (byte) (bArr[i3 + 8] | ((byte) (iArr[5] << 1)));
            bArr[i3 + 9] = (byte) (iArr[5] >> 7);
            bArr[i3 + 9] = (byte) (bArr[i3 + 9] | ((byte) (iArr[6] << 6)));
            bArr[i3 + 10] = (byte) (iArr[6] >> 2);
            bArr[i3 + 11] = (byte) (iArr[6] >> 10);
            bArr[i3 + 11] = (byte) (bArr[i3 + 11] | ((byte) (iArr[7] << 3)));
            bArr[i3 + 12] = (byte) (iArr[7] >> 5);
        }
        return bArr;
    }

    public void polyt0Unpack(byte[] bArr, int i) {
        for (int i2 = 0; i2 < this.dilithiumN / 8; i2++) {
            int i3 = i + (13 * i2);
            setCoeffIndex((8 * i2) + 0, ((bArr[i3 + 0] & 255) | ((bArr[i3 + 1] & 255) << 8)) & 8191);
            setCoeffIndex((8 * i2) + 1, (((bArr[i3 + 1] & 255) >> 5) | ((bArr[i3 + 2] & 255) << 3) | ((bArr[i3 + 3] & 255) << 11)) & 8191);
            setCoeffIndex((8 * i2) + 2, (((bArr[i3 + 3] & 255) >> 2) | ((bArr[i3 + 4] & 255) << 6)) & 8191);
            setCoeffIndex((8 * i2) + 3, (((bArr[i3 + 4] & 255) >> 7) | ((bArr[i3 + 5] & 255) << 1) | ((bArr[i3 + 6] & 255) << 9)) & 8191);
            setCoeffIndex((8 * i2) + 4, (((bArr[i3 + 6] & 255) >> 4) | ((bArr[i3 + 7] & 255) << 4) | ((bArr[i3 + 8] & 255) << 12)) & 8191);
            setCoeffIndex((8 * i2) + 5, (((bArr[i3 + 8] & 255) >> 1) | ((bArr[i3 + 9] & 255) << 7)) & 8191);
            setCoeffIndex((8 * i2) + 6, (((bArr[i3 + 9] & 255) >> 6) | ((bArr[i3 + 10] & 255) << 2) | ((bArr[i3 + 11] & 255) << 10)) & 8191);
            setCoeffIndex((8 * i2) + 7, (((bArr[i3 + 11] & 255) >> 3) | ((bArr[i3 + 12] & 255) << 5)) & 8191);
            setCoeffIndex((8 * i2) + 0, 4096 - getCoeffIndex((8 * i2) + 0));
            setCoeffIndex((8 * i2) + 1, 4096 - getCoeffIndex((8 * i2) + 1));
            setCoeffIndex((8 * i2) + 2, 4096 - getCoeffIndex((8 * i2) + 2));
            setCoeffIndex((8 * i2) + 3, 4096 - getCoeffIndex((8 * i2) + 3));
            setCoeffIndex((8 * i2) + 4, 4096 - getCoeffIndex((8 * i2) + 4));
            setCoeffIndex((8 * i2) + 5, 4096 - getCoeffIndex((8 * i2) + 5));
            setCoeffIndex((8 * i2) + 6, 4096 - getCoeffIndex((8 * i2) + 6));
            setCoeffIndex((8 * i2) + 7, 4096 - getCoeffIndex((8 * i2) + 7));
        }
    }

    public void uniformGamma1(byte[] bArr, short s) {
        byte[] bArr2 = new byte[this.engine.getPolyUniformGamma1NBlocks() * this.symmetric.stream256BlockBytes];
        this.symmetric.stream256init(bArr, s);
        this.symmetric.stream256squeezeBlocks(bArr2, 0, this.engine.getPolyUniformGamma1NBlocks() * this.symmetric.stream256BlockBytes);
        unpackZ(bArr2);
    }

    private void unpackZ(byte[] bArr) {
        if (this.engine.getDilithiumGamma1() != 131072) {
            if (this.engine.getDilithiumGamma1() != 524288) {
                throw new RuntimeException("Wrong Dilithiumn Gamma1!");
            }
            for (int i = 0; i < this.dilithiumN / 2; i++) {
                setCoeffIndex((2 * i) + 0, ((bArr[(5 * i) + 0] & 255) | ((bArr[(5 * i) + 1] & 255) << 8) | ((bArr[(5 * i) + 2] & 255) << 16)) & WinNT.NLS_VALID_LOCALE_MASK);
                setCoeffIndex((2 * i) + 1, (((bArr[(5 * i) + 2] & 255) >> 4) | ((bArr[(5 * i) + 3] & 255) << 4) | ((bArr[(5 * i) + 4] & 255) << 12)) & WinNT.NLS_VALID_LOCALE_MASK);
                setCoeffIndex((2 * i) + 0, this.engine.getDilithiumGamma1() - getCoeffIndex((2 * i) + 0));
                setCoeffIndex((2 * i) + 1, this.engine.getDilithiumGamma1() - getCoeffIndex((2 * i) + 1));
            }
            return;
        }
        for (int i2 = 0; i2 < this.dilithiumN / 4; i2++) {
            setCoeffIndex((4 * i2) + 0, ((bArr[(9 * i2) + 0] & 255) | ((bArr[(9 * i2) + 1] & 255) << 8) | ((bArr[(9 * i2) + 2] & 255) << 16)) & 262143);
            setCoeffIndex((4 * i2) + 1, (((bArr[(9 * i2) + 2] & 255) >> 2) | ((bArr[(9 * i2) + 3] & 255) << 6) | ((bArr[(9 * i2) + 4] & 255) << 14)) & 262143);
            setCoeffIndex((4 * i2) + 2, (((bArr[(9 * i2) + 4] & 255) >> 4) | ((bArr[(9 * i2) + 5] & 255) << 4) | ((bArr[(9 * i2) + 6] & 255) << 12)) & 262143);
            setCoeffIndex((4 * i2) + 3, (((bArr[(9 * i2) + 6] & 255) >> 6) | ((bArr[(9 * i2) + 7] & 255) << 2) | ((bArr[(9 * i2) + 8] & 255) << 10)) & 262143);
            setCoeffIndex((4 * i2) + 0, this.engine.getDilithiumGamma1() - getCoeffIndex((4 * i2) + 0));
            setCoeffIndex((4 * i2) + 1, this.engine.getDilithiumGamma1() - getCoeffIndex((4 * i2) + 1));
            setCoeffIndex((4 * i2) + 2, this.engine.getDilithiumGamma1() - getCoeffIndex((4 * i2) + 2));
            setCoeffIndex((4 * i2) + 3, this.engine.getDilithiumGamma1() - getCoeffIndex((4 * i2) + 3));
        }
    }

    public void decompose(Poly poly) {
        for (int i = 0; i < this.dilithiumN; i++) {
            int[] iArrDecompose = Rounding.decompose(getCoeffIndex(i), this.engine.getDilithiumGamma2());
            setCoeffIndex(i, iArrDecompose[1]);
            poly.setCoeffIndex(i, iArrDecompose[0]);
        }
    }

    public byte[] w1Pack() {
        byte[] bArr = new byte[this.engine.getDilithiumPolyW1PackedBytes()];
        if (this.engine.getDilithiumGamma2() == 95232) {
            for (int i = 0; i < this.dilithiumN / 4; i++) {
                bArr[(3 * i) + 0] = (byte) (((byte) getCoeffIndex((4 * i) + 0)) | (getCoeffIndex((4 * i) + 1) << 6));
                bArr[(3 * i) + 1] = (byte) (((byte) (getCoeffIndex((4 * i) + 1) >> 2)) | (getCoeffIndex((4 * i) + 2) << 4));
                bArr[(3 * i) + 2] = (byte) (((byte) (getCoeffIndex((4 * i) + 2) >> 4)) | (getCoeffIndex((4 * i) + 3) << 2));
            }
        } else if (this.engine.getDilithiumGamma2() == 261888) {
            for (int i2 = 0; i2 < this.dilithiumN / 2; i2++) {
                bArr[i2] = (byte) (getCoeffIndex((2 * i2) + 0) | (getCoeffIndex((2 * i2) + 1) << 4));
            }
        }
        return bArr;
    }

    public void challenge(byte[] bArr) {
        int i;
        byte[] bArr2 = new byte[this.symmetric.stream256BlockBytes];
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update(bArr, 0, this.engine.getDilithiumCTilde());
        sHAKEDigest.doOutput(bArr2, 0, this.symmetric.stream256BlockBytes);
        long j = 0;
        for (int i2 = 0; i2 < 8; i2++) {
            j |= ((long) (bArr2[i2] & 255)) << (8 * i2);
        }
        int i3 = 8;
        for (int i4 = 0; i4 < this.dilithiumN; i4++) {
            setCoeffIndex(i4, 0);
        }
        for (int dilithiumTau = this.dilithiumN - this.engine.getDilithiumTau(); dilithiumTau < this.dilithiumN; dilithiumTau++) {
            do {
                if (i3 >= this.symmetric.stream256BlockBytes) {
                    sHAKEDigest.doOutput(bArr2, 0, this.symmetric.stream256BlockBytes);
                    i3 = 0;
                }
                int i5 = i3;
                i3++;
                i = bArr2[i5] & 255;
            } while (i > dilithiumTau);
            setCoeffIndex(dilithiumTau, getCoeffIndex(i));
            setCoeffIndex(i, (int) (1 - (2 * (j & 1))));
            j >>= 1;
        }
    }

    public boolean checkNorm(int i) {
        if (i > 1047552) {
            return true;
        }
        for (int i2 = 0; i2 < this.dilithiumN; i2++) {
            if (getCoeffIndex(i2) - ((getCoeffIndex(i2) >> 31) & (2 * getCoeffIndex(i2))) >= i) {
                return true;
            }
        }
        return false;
    }

    public void subtract(Poly poly) {
        for (int i = 0; i < this.dilithiumN; i++) {
            setCoeffIndex(i, getCoeffIndex(i) - poly.getCoeffIndex(i));
        }
    }

    public int polyMakeHint(Poly poly, Poly poly2) {
        int coeffIndex = 0;
        for (int i = 0; i < this.dilithiumN; i++) {
            setCoeffIndex(i, Rounding.makeHint(poly.getCoeffIndex(i), poly2.getCoeffIndex(i), this.engine));
            coeffIndex += getCoeffIndex(i);
        }
        return coeffIndex;
    }

    public void polyUseHint(Poly poly, Poly poly2) {
        for (int i = 0; i < this.dilithiumN; i++) {
            setCoeffIndex(i, Rounding.useHint(poly.getCoeffIndex(i), poly2.getCoeffIndex(i), this.engine.getDilithiumGamma2()));
        }
    }

    public byte[] zPack() {
        byte[] bArr = new byte[this.engine.getDilithiumPolyZPackedBytes()];
        int[] iArr = new int[4];
        if (this.engine.getDilithiumGamma1() == 131072) {
            for (int i = 0; i < this.dilithiumN / 4; i++) {
                iArr[0] = this.engine.getDilithiumGamma1() - getCoeffIndex((4 * i) + 0);
                iArr[1] = this.engine.getDilithiumGamma1() - getCoeffIndex((4 * i) + 1);
                iArr[2] = this.engine.getDilithiumGamma1() - getCoeffIndex((4 * i) + 2);
                iArr[3] = this.engine.getDilithiumGamma1() - getCoeffIndex((4 * i) + 3);
                bArr[(9 * i) + 0] = (byte) iArr[0];
                bArr[(9 * i) + 1] = (byte) (iArr[0] >> 8);
                bArr[(9 * i) + 2] = (byte) (((byte) (iArr[0] >> 16)) | (iArr[1] << 2));
                bArr[(9 * i) + 3] = (byte) (iArr[1] >> 6);
                bArr[(9 * i) + 4] = (byte) (((byte) (iArr[1] >> 14)) | (iArr[2] << 4));
                bArr[(9 * i) + 5] = (byte) (iArr[2] >> 4);
                bArr[(9 * i) + 6] = (byte) (((byte) (iArr[2] >> 12)) | (iArr[3] << 6));
                bArr[(9 * i) + 7] = (byte) (iArr[3] >> 2);
                bArr[(9 * i) + 8] = (byte) (iArr[3] >> 10);
            }
        } else {
            if (this.engine.getDilithiumGamma1() != 524288) {
                throw new RuntimeException("Wrong Dilithium Gamma1!");
            }
            for (int i2 = 0; i2 < this.dilithiumN / 2; i2++) {
                iArr[0] = this.engine.getDilithiumGamma1() - getCoeffIndex((2 * i2) + 0);
                iArr[1] = this.engine.getDilithiumGamma1() - getCoeffIndex((2 * i2) + 1);
                bArr[(5 * i2) + 0] = (byte) iArr[0];
                bArr[(5 * i2) + 1] = (byte) (iArr[0] >> 8);
                bArr[(5 * i2) + 2] = (byte) (((byte) (iArr[0] >> 16)) | (iArr[1] << 4));
                bArr[(5 * i2) + 3] = (byte) (iArr[1] >> 4);
                bArr[(5 * i2) + 4] = (byte) (iArr[1] >> 12);
            }
        }
        return bArr;
    }

    void zUnpack(byte[] bArr) {
        if (this.engine.getDilithiumGamma1() != 131072) {
            if (this.engine.getDilithiumGamma1() != 524288) {
                throw new RuntimeException("Wrong Dilithium Gamma1!");
            }
            for (int i = 0; i < this.dilithiumN / 2; i++) {
                setCoeffIndex((2 * i) + 0, ((bArr[(5 * i) + 0] & 255) | ((bArr[(5 * i) + 1] & 255) << 8) | ((bArr[(5 * i) + 2] & 255) << 16)) & WinNT.NLS_VALID_LOCALE_MASK);
                setCoeffIndex((2 * i) + 1, (((bArr[(5 * i) + 2] & 255) >>> 4) | ((bArr[(5 * i) + 3] & 255) << 4) | ((bArr[(5 * i) + 4] & 255) << 12)) & WinNT.NLS_VALID_LOCALE_MASK);
                setCoeffIndex((2 * i) + 0, this.engine.getDilithiumGamma1() - getCoeffIndex((2 * i) + 0));
                setCoeffIndex((2 * i) + 1, this.engine.getDilithiumGamma1() - getCoeffIndex((2 * i) + 1));
            }
            return;
        }
        for (int i2 = 0; i2 < this.dilithiumN / 4; i2++) {
            setCoeffIndex((4 * i2) + 0, ((bArr[(9 * i2) + 0] & 255) | ((bArr[(9 * i2) + 1] & 255) << 8) | ((bArr[(9 * i2) + 2] & 255) << 16)) & 262143);
            setCoeffIndex((4 * i2) + 1, (((bArr[(9 * i2) + 2] & 255) >>> 2) | ((bArr[(9 * i2) + 3] & 255) << 6) | ((bArr[(9 * i2) + 4] & 255) << 14)) & 262143);
            setCoeffIndex((4 * i2) + 2, (((bArr[(9 * i2) + 4] & 255) >>> 4) | ((bArr[(9 * i2) + 5] & 255) << 4) | ((bArr[(9 * i2) + 6] & 255) << 12)) & 262143);
            setCoeffIndex((4 * i2) + 3, (((bArr[(9 * i2) + 6] & 255) >>> 6) | ((bArr[(9 * i2) + 7] & 255) << 2) | ((bArr[(9 * i2) + 8] & 255) << 10)) & 262143);
            setCoeffIndex((4 * i2) + 0, this.engine.getDilithiumGamma1() - getCoeffIndex((4 * i2) + 0));
            setCoeffIndex((4 * i2) + 1, this.engine.getDilithiumGamma1() - getCoeffIndex((4 * i2) + 1));
            setCoeffIndex((4 * i2) + 2, this.engine.getDilithiumGamma1() - getCoeffIndex((4 * i2) + 2));
            setCoeffIndex((4 * i2) + 3, this.engine.getDilithiumGamma1() - getCoeffIndex((4 * i2) + 3));
        }
    }

    public void shiftLeft() {
        for (int i = 0; i < this.dilithiumN; i++) {
            setCoeffIndex(i, getCoeffIndex(i) << 13);
        }
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        for (int i = 0; i < this.coeffs.length; i++) {
            stringBuffer.append(this.coeffs[i]);
            if (i != this.coeffs.length - 1) {
                stringBuffer.append(", ");
            }
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }
}
