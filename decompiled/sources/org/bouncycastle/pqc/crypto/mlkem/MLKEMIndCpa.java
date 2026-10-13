package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mlkem/MLKEMIndCpa.class */
class MLKEMIndCpa {
    private MLKEMEngine engine;
    private int kyberK;
    private int indCpaPublicKeyBytes;
    private int polyVecBytes;
    private int indCpaBytes;
    private int polyVecCompressedBytes;
    private int polyCompressedBytes;
    private Symmetric symmetric;
    public final int KyberGenerateMatrixNBlocks;

    public MLKEMIndCpa(MLKEMEngine mLKEMEngine) {
        this.engine = mLKEMEngine;
        this.kyberK = mLKEMEngine.getKyberK();
        this.indCpaPublicKeyBytes = mLKEMEngine.getKyberPublicKeyBytes();
        this.polyVecBytes = mLKEMEngine.getKyberPolyVecBytes();
        this.indCpaBytes = mLKEMEngine.getKyberIndCpaBytes();
        this.polyVecCompressedBytes = mLKEMEngine.getKyberPolyVecCompressedBytes();
        this.polyCompressedBytes = mLKEMEngine.getKyberPolyCompressedBytes();
        this.symmetric = mLKEMEngine.getSymmetric();
        this.KyberGenerateMatrixNBlocks = (472 + this.symmetric.xofBlockBytes) / this.symmetric.xofBlockBytes;
    }

    byte[][] generateKeyPair(byte[] bArr) {
        PolyVec polyVec = new PolyVec(this.engine);
        PolyVec polyVec2 = new PolyVec(this.engine);
        PolyVec polyVec3 = new PolyVec(this.engine);
        byte[] bArr2 = new byte[64];
        this.symmetric.hash_g(bArr2, Arrays.append(bArr, (byte) this.kyberK));
        byte[] bArr3 = new byte[32];
        byte[] bArr4 = new byte[32];
        System.arraycopy(bArr2, 0, bArr3, 0, 32);
        System.arraycopy(bArr2, 32, bArr4, 0, 32);
        byte b = 0;
        PolyVec[] polyVecArr = new PolyVec[this.kyberK];
        for (int i = 0; i < this.kyberK; i++) {
            polyVecArr[i] = new PolyVec(this.engine);
        }
        generateMatrix(polyVecArr, bArr3, false);
        for (int i2 = 0; i2 < this.kyberK; i2++) {
            polyVec.getVectorIndex(i2).getEta1Noise(bArr4, b);
            b = (byte) (b + 1);
        }
        for (int i3 = 0; i3 < this.kyberK; i3++) {
            polyVec3.getVectorIndex(i3).getEta1Noise(bArr4, b);
            b = (byte) (b + 1);
        }
        polyVec.polyVecNtt();
        polyVec3.polyVecNtt();
        for (int i4 = 0; i4 < this.kyberK; i4++) {
            PolyVec.pointwiseAccountMontgomery(polyVec2.getVectorIndex(i4), polyVecArr[i4], polyVec, this.engine);
            polyVec2.getVectorIndex(i4).convertToMont();
        }
        polyVec2.addPoly(polyVec3);
        polyVec2.reducePoly();
        return new byte[][]{packPublicKey(polyVec2, bArr3), packSecretKey(polyVec)};
    }

    public byte[] encrypt(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        byte b = 0;
        PolyVec polyVec = new PolyVec(this.engine);
        PolyVec polyVec2 = new PolyVec(this.engine);
        PolyVec polyVec3 = new PolyVec(this.engine);
        PolyVec polyVec4 = new PolyVec(this.engine);
        PolyVec[] polyVecArr = new PolyVec[this.engine.getKyberK()];
        Poly poly = new Poly(this.engine);
        Poly poly2 = new Poly(this.engine);
        Poly poly3 = new Poly(this.engine);
        byte[] bArrUnpackPublicKey = unpackPublicKey(polyVec2, bArr);
        poly3.fromMsg(bArr2);
        for (int i = 0; i < this.kyberK; i++) {
            polyVecArr[i] = new PolyVec(this.engine);
        }
        generateMatrix(polyVecArr, bArrUnpackPublicKey, true);
        for (int i2 = 0; i2 < this.kyberK; i2++) {
            polyVec.getVectorIndex(i2).getEta1Noise(bArr3, b);
            b = (byte) (b + 1);
        }
        for (int i3 = 0; i3 < this.kyberK; i3++) {
            polyVec3.getVectorIndex(i3).getEta2Noise(bArr3, b);
            b = (byte) (b + 1);
        }
        poly.getEta2Noise(bArr3, b);
        polyVec.polyVecNtt();
        for (int i4 = 0; i4 < this.kyberK; i4++) {
            PolyVec.pointwiseAccountMontgomery(polyVec4.getVectorIndex(i4), polyVecArr[i4], polyVec, this.engine);
        }
        PolyVec.pointwiseAccountMontgomery(poly2, polyVec2, polyVec, this.engine);
        polyVec4.polyVecInverseNttToMont();
        poly2.polyInverseNttToMont();
        polyVec4.addPoly(polyVec3);
        poly2.addCoeffs(poly);
        poly2.addCoeffs(poly3);
        polyVec4.reducePoly();
        poly2.reduce();
        return packCipherText(polyVec4, poly2);
    }

    private byte[] packCipherText(PolyVec polyVec, Poly poly) {
        byte[] bArr = new byte[this.indCpaBytes];
        System.arraycopy(polyVec.compressPolyVec(), 0, bArr, 0, this.polyVecCompressedBytes);
        System.arraycopy(poly.compressPoly(), 0, bArr, this.polyVecCompressedBytes, this.polyCompressedBytes);
        return bArr;
    }

    private void unpackCipherText(PolyVec polyVec, Poly poly, byte[] bArr) {
        polyVec.decompressPolyVec(Arrays.copyOfRange(bArr, 0, this.engine.getKyberPolyVecCompressedBytes()));
        poly.decompressPoly(Arrays.copyOfRange(bArr, this.engine.getKyberPolyVecCompressedBytes(), bArr.length));
    }

    public byte[] packPublicKey(PolyVec polyVec, byte[] bArr) {
        byte[] bArr2 = new byte[this.indCpaPublicKeyBytes];
        System.arraycopy(polyVec.toBytes(), 0, bArr2, 0, this.polyVecBytes);
        System.arraycopy(bArr, 0, bArr2, this.polyVecBytes, 32);
        return bArr2;
    }

    public byte[] unpackPublicKey(PolyVec polyVec, byte[] bArr) {
        byte[] bArr2 = new byte[32];
        polyVec.fromBytes(bArr);
        System.arraycopy(bArr, this.polyVecBytes, bArr2, 0, 32);
        return bArr2;
    }

    public byte[] packSecretKey(PolyVec polyVec) {
        return polyVec.toBytes();
    }

    public void unpackSecretKey(PolyVec polyVec, byte[] bArr) {
        polyVec.fromBytes(bArr);
    }

    public void generateMatrix(PolyVec[] polyVecArr, byte[] bArr, boolean z) {
        byte[] bArr2 = new byte[(this.KyberGenerateMatrixNBlocks * this.symmetric.xofBlockBytes) + 2];
        for (int i = 0; i < this.kyberK; i++) {
            for (int i2 = 0; i2 < this.kyberK; i2++) {
                if (z) {
                    this.symmetric.xofAbsorb(bArr, (byte) i, (byte) i2);
                } else {
                    this.symmetric.xofAbsorb(bArr, (byte) i2, (byte) i);
                }
                this.symmetric.xofSqueezeBlocks(bArr2, 0, this.symmetric.xofBlockBytes * this.KyberGenerateMatrixNBlocks);
                int i3 = this.KyberGenerateMatrixNBlocks * this.symmetric.xofBlockBytes;
                int iRejectionSampling = rejectionSampling(polyVecArr[i].getVectorIndex(i2), 0, 256, bArr2, i3);
                while (true) {
                    int i4 = iRejectionSampling;
                    if (i4 < 256) {
                        int i5 = i3 % 3;
                        for (int i6 = 0; i6 < i5; i6++) {
                            bArr2[i6] = bArr2[(i3 - i5) + i6];
                        }
                        this.symmetric.xofSqueezeBlocks(bArr2, i5, this.symmetric.xofBlockBytes * 2);
                        i3 = i5 + this.symmetric.xofBlockBytes;
                        iRejectionSampling = i4 + rejectionSampling(polyVecArr[i].getVectorIndex(i2), i4, 256 - i4, bArr2, i3);
                    }
                }
            }
        }
    }

    private static int rejectionSampling(Poly poly, int i, int i2, byte[] bArr, int i3) {
        int i4 = 0;
        int i5 = 0;
        while (i5 < i2 && i4 + 3 <= i3) {
            short s = (short) (((((short) (bArr[i4] & 255)) >> 0) | (((short) (bArr[i4 + 1] & 255)) << 8)) & 4095);
            short s2 = (short) (((((short) (bArr[i4 + 1] & 255)) >> 4) | (((short) (bArr[i4 + 2] & 255)) << 4)) & 4095);
            i4 += 3;
            if (s < 3329) {
                poly.setCoeffIndex(i + i5, s);
                i5++;
            }
            if (i5 < i2 && s2 < 3329) {
                poly.setCoeffIndex(i + i5, s2);
                i5++;
            }
        }
        return i5;
    }

    public byte[] decrypt(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[MLKEMEngine.getKyberIndCpaMsgBytes()];
        PolyVec polyVec = new PolyVec(this.engine);
        PolyVec polyVec2 = new PolyVec(this.engine);
        Poly poly = new Poly(this.engine);
        Poly poly2 = new Poly(this.engine);
        unpackCipherText(polyVec, poly, bArr2);
        unpackSecretKey(polyVec2, bArr);
        polyVec.polyVecNtt();
        PolyVec.pointwiseAccountMontgomery(poly2, polyVec2, polyVec, this.engine);
        poly2.polyInverseNttToMont();
        poly2.polySubtract(poly);
        poly2.reduce();
        return poly2.toMsg();
    }
}
