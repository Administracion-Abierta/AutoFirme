package org.bouncycastle.pqc.crypto.mlkem;

import com.sun.jna.platform.win32.WinError;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mlkem/Poly.class */
class Poly {
    private MLKEMEngine engine;
    private int polyCompressedBytes;
    private int eta1;
    private Symmetric symmetric;
    private short[] coeffs = new short[256];
    private int eta2 = MLKEMEngine.getKyberEta2();

    public Poly(MLKEMEngine mLKEMEngine) {
        this.engine = mLKEMEngine;
        this.polyCompressedBytes = mLKEMEngine.getKyberPolyCompressedBytes();
        this.eta1 = mLKEMEngine.getKyberEta1();
        this.symmetric = mLKEMEngine.getSymmetric();
    }

    public short getCoeffIndex(int i) {
        return this.coeffs[i];
    }

    public short[] getCoeffs() {
        return this.coeffs;
    }

    public void setCoeffIndex(int i, short s) {
        this.coeffs[i] = s;
    }

    public void setCoeffs(short[] sArr) {
        this.coeffs = sArr;
    }

    public void polyNtt() {
        setCoeffs(Ntt.ntt(getCoeffs()));
        reduce();
    }

    public void polyInverseNttToMont() {
        setCoeffs(Ntt.invNtt(getCoeffs()));
    }

    public void reduce() {
        for (int i = 0; i < 256; i++) {
            setCoeffIndex(i, Reduce.barretReduce(getCoeffIndex(i)));
        }
    }

    public static void baseMultMontgomery(Poly poly, Poly poly2, Poly poly3) {
        for (int i = 0; i < 64; i++) {
            Ntt.baseMult(poly, 4 * i, poly2.getCoeffIndex(4 * i), poly2.getCoeffIndex((4 * i) + 1), poly3.getCoeffIndex(4 * i), poly3.getCoeffIndex((4 * i) + 1), Ntt.nttZetas[64 + i]);
            Ntt.baseMult(poly, (4 * i) + 2, poly2.getCoeffIndex((4 * i) + 2), poly2.getCoeffIndex((4 * i) + 3), poly3.getCoeffIndex((4 * i) + 2), poly3.getCoeffIndex((4 * i) + 3), (short) ((-1) * Ntt.nttZetas[64 + i]));
        }
    }

    public void addCoeffs(Poly poly) {
        for (int i = 0; i < 256; i++) {
            setCoeffIndex(i, (short) (getCoeffIndex(i) + poly.getCoeffIndex(i)));
        }
    }

    public void convertToMont() {
        for (int i = 0; i < 256; i++) {
            setCoeffIndex(i, Reduce.montgomeryReduce(getCoeffIndex(i) * WinError.ERROR_INVALID_DOMAIN_STATE));
        }
    }

    public byte[] compressPoly() {
        byte[] bArr = new byte[8];
        byte[] bArr2 = new byte[this.polyCompressedBytes];
        int i = 0;
        conditionalSubQ();
        if (this.polyCompressedBytes == 128) {
            for (int i2 = 0; i2 < 32; i2++) {
                for (int i3 = 0; i3 < 8; i3++) {
                    bArr[i3] = (byte) (((((getCoeffIndex((8 * i2) + i3) << 4) + 1665) * 80635) >> 28) & 15);
                }
                bArr2[i + 0] = (byte) (bArr[0] | (bArr[1] << 4));
                bArr2[i + 1] = (byte) (bArr[2] | (bArr[3] << 4));
                bArr2[i + 2] = (byte) (bArr[4] | (bArr[5] << 4));
                bArr2[i + 3] = (byte) (bArr[6] | (bArr[7] << 4));
                i += 4;
            }
        } else {
            if (this.polyCompressedBytes != 160) {
                throw new RuntimeException("PolyCompressedBytes is neither 128 or 160!");
            }
            for (int i4 = 0; i4 < 32; i4++) {
                for (int i5 = 0; i5 < 8; i5++) {
                    bArr[i5] = (byte) (((((getCoeffIndex((8 * i4) + i5) << 5) + 1664) * 40318) >> 27) & 31);
                }
                bArr2[i + 0] = (byte) ((bArr[0] >> 0) | (bArr[1] << 5));
                bArr2[i + 1] = (byte) ((bArr[1] >> 3) | (bArr[2] << 2) | (bArr[3] << 7));
                bArr2[i + 2] = (byte) ((bArr[3] >> 1) | (bArr[4] << 4));
                bArr2[i + 3] = (byte) ((bArr[4] >> 4) | (bArr[5] << 1) | (bArr[6] << 6));
                bArr2[i + 4] = (byte) ((bArr[6] >> 2) | (bArr[7] << 3));
                i += 5;
            }
        }
        return bArr2;
    }

    public void decompressPoly(byte[] bArr) {
        int i = 0;
        if (this.engine.getKyberPolyCompressedBytes() == 128) {
            for (int i2 = 0; i2 < 128; i2++) {
                setCoeffIndex((2 * i2) + 0, (short) (((((short) ((bArr[i] & 255) & 15)) * MLKEMEngine.KyberQ) + 8) >> 4));
                setCoeffIndex((2 * i2) + 1, (short) (((((short) ((bArr[i] & 255) >> 4)) * MLKEMEngine.KyberQ) + 8) >> 4));
                i++;
            }
            return;
        }
        if (this.engine.getKyberPolyCompressedBytes() != 160) {
            throw new RuntimeException("PolyCompressedBytes is neither 128 or 160!");
        }
        byte[] bArr2 = new byte[8];
        for (int i3 = 0; i3 < 32; i3++) {
            bArr2[0] = (byte) ((bArr[i + 0] & 255) >> 0);
            bArr2[1] = (byte) (((bArr[i + 0] & 255) >> 5) | ((bArr[i + 1] & 255) << 3));
            bArr2[2] = (byte) ((bArr[i + 1] & 255) >> 2);
            bArr2[3] = (byte) (((bArr[i + 1] & 255) >> 7) | ((bArr[i + 2] & 255) << 1));
            bArr2[4] = (byte) (((bArr[i + 2] & 255) >> 4) | ((bArr[i + 3] & 255) << 4));
            bArr2[5] = (byte) ((bArr[i + 3] & 255) >> 1);
            bArr2[6] = (byte) (((bArr[i + 3] & 255) >> 6) | ((bArr[i + 4] & 255) << 2));
            bArr2[7] = (byte) ((bArr[i + 4] & 255) >> 3);
            i += 5;
            for (int i4 = 0; i4 < 8; i4++) {
                setCoeffIndex((8 * i3) + i4, (short) ((((bArr2[i4] & 31) * MLKEMEngine.KyberQ) + 16) >> 5));
            }
        }
    }

    public byte[] toBytes() {
        byte[] bArr = new byte[384];
        conditionalSubQ();
        for (int i = 0; i < 128; i++) {
            short coeffIndex = getCoeffIndex(2 * i);
            short coeffIndex2 = getCoeffIndex((2 * i) + 1);
            bArr[3 * i] = (byte) (coeffIndex >> 0);
            bArr[(3 * i) + 1] = (byte) ((coeffIndex >> 8) | (coeffIndex2 << 4));
            bArr[(3 * i) + 2] = (byte) (coeffIndex2 >> 4);
        }
        return bArr;
    }

    public void fromBytes(byte[] bArr) {
        for (int i = 0; i < 128; i++) {
            setCoeffIndex(2 * i, (short) ((((bArr[(3 * i) + 0] & 255) >> 0) | ((bArr[(3 * i) + 1] & 255) << 8)) & 4095));
            setCoeffIndex((2 * i) + 1, (short) ((((long) ((bArr[(3 * i) + 1] & 255) >> 4)) | ((long) ((bArr[(3 * i) + 2] & 255) << 4))) & 4095));
        }
    }

    public byte[] toMsg() {
        int i = MLKEMEngine.KyberQ - 832;
        byte[] bArr = new byte[MLKEMEngine.getKyberIndCpaMsgBytes()];
        conditionalSubQ();
        for (int i2 = 0; i2 < 32; i2++) {
            bArr[i2] = 0;
            for (int i3 = 0; i3 < 8; i3++) {
                short coeffIndex = getCoeffIndex((8 * i2) + i3);
                int i4 = i2;
                bArr[i4] = (byte) (bArr[i4] | ((byte) ((((832 - coeffIndex) & (coeffIndex - i)) >>> 31) << i3)));
            }
        }
        return bArr;
    }

    public void fromMsg(byte[] bArr) {
        if (bArr.length != 32) {
            throw new RuntimeException("KYBER_INDCPA_MSGBYTES must be equal to KYBER_N/8 bytes!");
        }
        for (int i = 0; i < 32; i++) {
            for (int i2 = 0; i2 < 8; i2++) {
                setCoeffIndex((8 * i) + i2, (short) (((short) ((-1) * ((short) (((bArr[i] & 255) >> i2) & 1)))) & 1665));
            }
        }
    }

    public void conditionalSubQ() {
        for (int i = 0; i < 256; i++) {
            setCoeffIndex(i, Reduce.conditionalSubQ(getCoeffIndex(i)));
        }
    }

    public void getEta1Noise(byte[] bArr, byte b) {
        byte[] bArr2 = new byte[(256 * this.eta1) / 4];
        this.symmetric.prf(bArr2, bArr, b);
        CBD.mlkemCBD(this, bArr2, this.eta1);
    }

    public void getEta2Noise(byte[] bArr, byte b) {
        byte[] bArr2 = new byte[(256 * this.eta2) / 4];
        this.symmetric.prf(bArr2, bArr, b);
        CBD.mlkemCBD(this, bArr2, this.eta2);
    }

    public void polySubtract(Poly poly) {
        for (int i = 0; i < 256; i++) {
            setCoeffIndex(i, (short) (poly.getCoeffIndex(i) - getCoeffIndex(i)));
        }
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        for (int i = 0; i < this.coeffs.length; i++) {
            stringBuffer.append((int) this.coeffs[i]);
            if (i != this.coeffs.length - 1) {
                stringBuffer.append(", ");
            }
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }
}
