package org.bouncycastle.pqc.crypto.mlkem;

import com.sun.jna.platform.win32.WinError;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mlkem/PolyVec.class */
class PolyVec {
    Poly[] vec;
    private MLKEMEngine engine;
    private int kyberK;
    private int polyVecBytes;

    public PolyVec(MLKEMEngine mLKEMEngine) {
        this.engine = mLKEMEngine;
        this.kyberK = mLKEMEngine.getKyberK();
        this.polyVecBytes = mLKEMEngine.getKyberPolyVecBytes();
        this.vec = new Poly[this.kyberK];
        for (int i = 0; i < this.kyberK; i++) {
            this.vec[i] = new Poly(mLKEMEngine);
        }
    }

    public PolyVec() throws Exception {
        throw new Exception("Requires Parameter");
    }

    public Poly getVectorIndex(int i) {
        return this.vec[i];
    }

    public void polyVecNtt() {
        for (int i = 0; i < this.kyberK; i++) {
            getVectorIndex(i).polyNtt();
        }
    }

    public void polyVecInverseNttToMont() {
        for (int i = 0; i < this.kyberK; i++) {
            getVectorIndex(i).polyInverseNttToMont();
        }
    }

    public byte[] compressPolyVec() {
        conditionalSubQ();
        byte[] bArr = new byte[this.engine.getKyberPolyVecCompressedBytes()];
        int i = 0;
        if (this.engine.getKyberPolyVecCompressedBytes() == this.kyberK * 320) {
            short[] sArr = new short[4];
            for (int i2 = 0; i2 < this.kyberK; i2++) {
                for (int i3 = 0; i3 < 64; i3++) {
                    for (int i4 = 0; i4 < 4; i4++) {
                        sArr[i4] = (short) (((((((long) getVectorIndex(i2).getCoeffIndex((4 * i3) + i4)) << 10) + 1665) * 1290167) >> 32) & 1023);
                    }
                    bArr[i + 0] = (byte) (sArr[0] >> 0);
                    bArr[i + 1] = (byte) ((sArr[0] >> 8) | (sArr[1] << 2));
                    bArr[i + 2] = (byte) ((sArr[1] >> 6) | (sArr[2] << 4));
                    bArr[i + 3] = (byte) ((sArr[2] >> 4) | (sArr[3] << 6));
                    bArr[i + 4] = (byte) (sArr[3] >> 2);
                    i += 5;
                }
            }
        } else {
            if (this.engine.getKyberPolyVecCompressedBytes() != this.kyberK * WinError.ERROR_FAIL_RESTART) {
                throw new RuntimeException("Kyber PolyVecCompressedBytes neither 320 * KyberK or 352 * KyberK!");
            }
            short[] sArr2 = new short[8];
            for (int i5 = 0; i5 < this.kyberK; i5++) {
                for (int i6 = 0; i6 < 32; i6++) {
                    for (int i7 = 0; i7 < 8; i7++) {
                        sArr2[i7] = (short) (((((((long) getVectorIndex(i5).getCoeffIndex((8 * i6) + i7)) << 11) + 1664) * 645084) >> 31) & 2047);
                    }
                    bArr[i + 0] = (byte) (sArr2[0] >> 0);
                    bArr[i + 1] = (byte) ((sArr2[0] >> 8) | (sArr2[1] << 3));
                    bArr[i + 2] = (byte) ((sArr2[1] >> 5) | (sArr2[2] << 6));
                    bArr[i + 3] = (byte) (sArr2[2] >> 2);
                    bArr[i + 4] = (byte) ((sArr2[2] >> 10) | (sArr2[3] << 1));
                    bArr[i + 5] = (byte) ((sArr2[3] >> 7) | (sArr2[4] << 4));
                    bArr[i + 6] = (byte) ((sArr2[4] >> 4) | (sArr2[5] << 7));
                    bArr[i + 7] = (byte) (sArr2[5] >> 1);
                    bArr[i + 8] = (byte) ((sArr2[5] >> 9) | (sArr2[6] << 2));
                    bArr[i + 9] = (byte) ((sArr2[6] >> 6) | (sArr2[7] << 5));
                    bArr[i + 10] = (byte) (sArr2[7] >> 3);
                    i += 11;
                }
            }
        }
        return bArr;
    }

    public void decompressPolyVec(byte[] bArr) {
        int i = 0;
        if (this.engine.getKyberPolyVecCompressedBytes() == this.kyberK * 320) {
            short[] sArr = new short[4];
            for (int i2 = 0; i2 < this.kyberK; i2++) {
                for (int i3 = 0; i3 < 64; i3++) {
                    sArr[0] = (short) (((bArr[i] & 255) >> 0) | ((short) ((bArr[i + 1] & 255) << 8)));
                    sArr[1] = (short) (((bArr[i + 1] & 255) >> 2) | ((short) ((bArr[i + 2] & 255) << 6)));
                    sArr[2] = (short) (((bArr[i + 2] & 255) >> 4) | ((short) ((bArr[i + 3] & 255) << 4)));
                    sArr[3] = (short) (((bArr[i + 3] & 255) >> 6) | ((short) ((bArr[i + 4] & 255) << 2)));
                    i += 5;
                    for (int i4 = 0; i4 < 4; i4++) {
                        this.vec[i2].setCoeffIndex((4 * i3) + i4, (short) ((((sArr[i4] & 1023) * MLKEMEngine.KyberQ) + 512) >> 10));
                    }
                }
            }
            return;
        }
        if (this.engine.getKyberPolyVecCompressedBytes() != this.kyberK * WinError.ERROR_FAIL_RESTART) {
            throw new RuntimeException("Kyber PolyVecCompressedBytes neither 320 * KyberK or 352 * KyberK!");
        }
        short[] sArr2 = new short[8];
        for (int i5 = 0; i5 < this.kyberK; i5++) {
            for (int i6 = 0; i6 < 32; i6++) {
                sArr2[0] = (short) (((bArr[i] & 255) >> 0) | (((short) (bArr[i + 1] & 255)) << 8));
                sArr2[1] = (short) (((bArr[i + 1] & 255) >> 3) | (((short) (bArr[i + 2] & 255)) << 5));
                sArr2[2] = (short) (((bArr[i + 2] & 255) >> 6) | (((short) (bArr[i + 3] & 255)) << 2) | ((short) ((bArr[i + 4] & 255) << 10)));
                sArr2[3] = (short) (((bArr[i + 4] & 255) >> 1) | (((short) (bArr[i + 5] & 255)) << 7));
                sArr2[4] = (short) (((bArr[i + 5] & 255) >> 4) | (((short) (bArr[i + 6] & 255)) << 4));
                sArr2[5] = (short) (((bArr[i + 6] & 255) >> 7) | (((short) (bArr[i + 7] & 255)) << 1) | ((short) ((bArr[i + 8] & 255) << 9)));
                sArr2[6] = (short) (((bArr[i + 8] & 255) >> 2) | (((short) (bArr[i + 9] & 255)) << 6));
                sArr2[7] = (short) (((bArr[i + 9] & 255) >> 5) | (((short) (bArr[i + 10] & 255)) << 3));
                i += 11;
                for (int i7 = 0; i7 < 8; i7++) {
                    this.vec[i5].setCoeffIndex((8 * i6) + i7, (short) ((((sArr2[i7] & 2047) * MLKEMEngine.KyberQ) + 1024) >> 11));
                }
            }
        }
    }

    public static void pointwiseAccountMontgomery(Poly poly, PolyVec polyVec, PolyVec polyVec2, MLKEMEngine mLKEMEngine) {
        Poly poly2 = new Poly(mLKEMEngine);
        Poly.baseMultMontgomery(poly, polyVec.getVectorIndex(0), polyVec2.getVectorIndex(0));
        for (int i = 1; i < mLKEMEngine.getKyberK(); i++) {
            Poly.baseMultMontgomery(poly2, polyVec.getVectorIndex(i), polyVec2.getVectorIndex(i));
            poly.addCoeffs(poly2);
        }
        poly.reduce();
    }

    public void reducePoly() {
        for (int i = 0; i < this.kyberK; i++) {
            getVectorIndex(i).reduce();
        }
    }

    public void addPoly(PolyVec polyVec) {
        for (int i = 0; i < this.kyberK; i++) {
            getVectorIndex(i).addCoeffs(polyVec.getVectorIndex(i));
        }
    }

    public byte[] toBytes() {
        byte[] bArr = new byte[this.polyVecBytes];
        for (int i = 0; i < this.kyberK; i++) {
            System.arraycopy(this.vec[i].toBytes(), 0, bArr, i * 384, 384);
        }
        return bArr;
    }

    public void fromBytes(byte[] bArr) {
        for (int i = 0; i < this.kyberK; i++) {
            getVectorIndex(i).fromBytes(Arrays.copyOfRange(bArr, i * 384, (i + 1) * 384));
        }
    }

    public void conditionalSubQ() {
        for (int i = 0; i < this.kyberK; i++) {
            getVectorIndex(i).conditionalSubQ();
        }
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        for (int i = 0; i < this.kyberK; i++) {
            stringBuffer.append(this.vec[i].toString());
            if (i != this.kyberK - 1) {
                stringBuffer.append(", ");
            }
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }
}
