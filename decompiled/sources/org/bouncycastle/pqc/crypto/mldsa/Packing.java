package org.bouncycastle.pqc.crypto.mldsa;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mldsa/Packing.class */
class Packing {
    Packing() {
    }

    static byte[] packPublicKey(PolyVecK polyVecK, MLDSAEngine mLDSAEngine) {
        byte[] bArr = new byte[mLDSAEngine.getCryptoPublicKeyBytes() - 32];
        for (int i = 0; i < mLDSAEngine.getDilithiumK(); i++) {
            System.arraycopy(polyVecK.getVectorIndex(i).polyt1Pack(), 0, bArr, i * 320, 320);
        }
        return bArr;
    }

    static PolyVecK unpackPublicKey(PolyVecK polyVecK, byte[] bArr, MLDSAEngine mLDSAEngine) {
        for (int i = 0; i < mLDSAEngine.getDilithiumK(); i++) {
            polyVecK.getVectorIndex(i).polyt1Unpack(Arrays.copyOfRange(bArr, i * 320, (i + 1) * 320));
        }
        return polyVecK;
    }

    static byte[][] packSecretKey(byte[] bArr, byte[] bArr2, byte[] bArr3, PolyVecK polyVecK, PolyVecL polyVecL, PolyVecK polyVecK2, MLDSAEngine mLDSAEngine) {
        byte[][] bArr4 = new byte[6][];
        bArr4[0] = bArr;
        bArr4[1] = bArr3;
        bArr4[2] = bArr2;
        bArr4[3] = new byte[mLDSAEngine.getDilithiumL() * mLDSAEngine.getDilithiumPolyEtaPackedBytes()];
        for (int i = 0; i < mLDSAEngine.getDilithiumL(); i++) {
            polyVecL.getVectorIndex(i).polyEtaPack(bArr4[3], i * mLDSAEngine.getDilithiumPolyEtaPackedBytes());
        }
        bArr4[4] = new byte[mLDSAEngine.getDilithiumK() * mLDSAEngine.getDilithiumPolyEtaPackedBytes()];
        for (int i2 = 0; i2 < mLDSAEngine.getDilithiumK(); i2++) {
            polyVecK2.getVectorIndex(i2).polyEtaPack(bArr4[4], i2 * mLDSAEngine.getDilithiumPolyEtaPackedBytes());
        }
        bArr4[5] = new byte[mLDSAEngine.getDilithiumK() * 416];
        for (int i3 = 0; i3 < mLDSAEngine.getDilithiumK(); i3++) {
            polyVecK.getVectorIndex(i3).polyt0Pack(bArr4[5], i3 * 416);
        }
        return bArr4;
    }

    static void unpackSecretKey(PolyVecK polyVecK, PolyVecL polyVecL, PolyVecK polyVecK2, byte[] bArr, byte[] bArr2, byte[] bArr3, MLDSAEngine mLDSAEngine) {
        for (int i = 0; i < mLDSAEngine.getDilithiumL(); i++) {
            polyVecL.getVectorIndex(i).polyEtaUnpack(bArr2, i * mLDSAEngine.getDilithiumPolyEtaPackedBytes());
        }
        for (int i2 = 0; i2 < mLDSAEngine.getDilithiumK(); i2++) {
            polyVecK2.getVectorIndex(i2).polyEtaUnpack(bArr3, i2 * mLDSAEngine.getDilithiumPolyEtaPackedBytes());
        }
        for (int i3 = 0; i3 < mLDSAEngine.getDilithiumK(); i3++) {
            polyVecK.getVectorIndex(i3).polyt0Unpack(bArr, i3 * 416);
        }
    }

    static byte[] packSignature(byte[] bArr, PolyVecL polyVecL, PolyVecK polyVecK, MLDSAEngine mLDSAEngine) {
        byte[] bArr2 = new byte[mLDSAEngine.getCryptoBytes()];
        System.arraycopy(bArr, 0, bArr2, 0, mLDSAEngine.getDilithiumCTilde());
        int dilithiumCTilde = 0 + mLDSAEngine.getDilithiumCTilde();
        for (int i = 0; i < mLDSAEngine.getDilithiumL(); i++) {
            System.arraycopy(polyVecL.getVectorIndex(i).zPack(), 0, bArr2, dilithiumCTilde + (i * mLDSAEngine.getDilithiumPolyZPackedBytes()), mLDSAEngine.getDilithiumPolyZPackedBytes());
        }
        int dilithiumL = dilithiumCTilde + (mLDSAEngine.getDilithiumL() * mLDSAEngine.getDilithiumPolyZPackedBytes());
        for (int i2 = 0; i2 < mLDSAEngine.getDilithiumOmega() + mLDSAEngine.getDilithiumK(); i2++) {
            bArr2[dilithiumL + i2] = 0;
        }
        int i3 = 0;
        for (int i4 = 0; i4 < mLDSAEngine.getDilithiumK(); i4++) {
            for (int i5 = 0; i5 < 256; i5++) {
                if (polyVecK.getVectorIndex(i4).getCoeffIndex(i5) != 0) {
                    int i6 = i3;
                    i3++;
                    bArr2[dilithiumL + i6] = (byte) i5;
                }
            }
            bArr2[dilithiumL + mLDSAEngine.getDilithiumOmega() + i4] = (byte) i3;
        }
        return bArr2;
    }

    static boolean unpackSignature(PolyVecL polyVecL, PolyVecK polyVecK, byte[] bArr, MLDSAEngine mLDSAEngine) {
        int dilithiumCTilde = mLDSAEngine.getDilithiumCTilde();
        for (int i = 0; i < mLDSAEngine.getDilithiumL(); i++) {
            polyVecL.getVectorIndex(i).zUnpack(Arrays.copyOfRange(bArr, dilithiumCTilde + (i * mLDSAEngine.getDilithiumPolyZPackedBytes()), dilithiumCTilde + ((i + 1) * mLDSAEngine.getDilithiumPolyZPackedBytes())));
        }
        int dilithiumL = dilithiumCTilde + (mLDSAEngine.getDilithiumL() * mLDSAEngine.getDilithiumPolyZPackedBytes());
        int i2 = 0;
        for (int i3 = 0; i3 < mLDSAEngine.getDilithiumK(); i3++) {
            for (int i4 = 0; i4 < 256; i4++) {
                polyVecK.getVectorIndex(i3).setCoeffIndex(i4, 0);
            }
            if ((bArr[dilithiumL + mLDSAEngine.getDilithiumOmega() + i3] & 255) < i2 || (bArr[dilithiumL + mLDSAEngine.getDilithiumOmega() + i3] & 255) > mLDSAEngine.getDilithiumOmega()) {
                return false;
            }
            for (int i5 = i2; i5 < (bArr[dilithiumL + mLDSAEngine.getDilithiumOmega() + i3] & 255); i5++) {
                if (i5 > i2 && (bArr[dilithiumL + i5] & 255) <= (bArr[(dilithiumL + i5) - 1] & 255)) {
                    return false;
                }
                polyVecK.getVectorIndex(i3).setCoeffIndex(bArr[dilithiumL + i5] & 255, 1);
            }
            i2 = bArr[dilithiumL + mLDSAEngine.getDilithiumOmega() + i3];
        }
        for (int i6 = i2; i6 < mLDSAEngine.getDilithiumOmega(); i6++) {
            if ((bArr[dilithiumL + i6] & 255) != 0) {
                return false;
            }
        }
        return true;
    }
}
