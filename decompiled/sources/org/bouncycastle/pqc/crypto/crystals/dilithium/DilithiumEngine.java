package org.bouncycastle.pqc.crypto.crystals.dilithium;

import com.sun.jna.platform.win32.WinError;
import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/crystals/dilithium/DilithiumEngine.class */
class DilithiumEngine {
    private final SecureRandom random;
    private final SHAKEDigest shake256Digest = new SHAKEDigest(256);
    public static final int DilithiumN = 256;
    public static final int DilithiumQ = 8380417;
    public static final int DilithiumQinv = 58728449;
    public static final int DilithiumD = 13;
    public static final int DilithiumRootOfUnity = 1753;
    public static final int SeedBytes = 32;
    public static final int CrhBytes = 64;
    public static final int RndBytes = 32;
    public static final int TrBytes = 64;
    public static final int DilithiumPolyT1PackedBytes = 320;
    public static final int DilithiumPolyT0PackedBytes = 416;
    private final int DilithiumPolyVecHPackedBytes;
    private final int DilithiumPolyZPackedBytes;
    private final int DilithiumPolyW1PackedBytes;
    private final int DilithiumPolyEtaPackedBytes;
    private final int DilithiumMode;
    private final int DilithiumK;
    private final int DilithiumL;
    private final int DilithiumEta;
    private final int DilithiumTau;
    private final int DilithiumBeta;
    private final int DilithiumGamma1;
    private final int DilithiumGamma2;
    private final int DilithiumOmega;
    private final int DilithiumCTilde;
    private final int CryptoPublicKeyBytes;
    private final int CryptoSecretKeyBytes;
    private final int CryptoBytes;
    private final int PolyUniformGamma1NBlocks;
    private final Symmetric symmetric;

    protected Symmetric GetSymmetric() {
        return this.symmetric;
    }

    int getDilithiumPolyVecHPackedBytes() {
        return this.DilithiumPolyVecHPackedBytes;
    }

    int getDilithiumPolyZPackedBytes() {
        return this.DilithiumPolyZPackedBytes;
    }

    int getDilithiumPolyW1PackedBytes() {
        return this.DilithiumPolyW1PackedBytes;
    }

    int getDilithiumPolyEtaPackedBytes() {
        return this.DilithiumPolyEtaPackedBytes;
    }

    int getDilithiumMode() {
        return this.DilithiumMode;
    }

    int getDilithiumK() {
        return this.DilithiumK;
    }

    int getDilithiumL() {
        return this.DilithiumL;
    }

    int getDilithiumEta() {
        return this.DilithiumEta;
    }

    int getDilithiumTau() {
        return this.DilithiumTau;
    }

    int getDilithiumBeta() {
        return this.DilithiumBeta;
    }

    int getDilithiumGamma1() {
        return this.DilithiumGamma1;
    }

    int getDilithiumGamma2() {
        return this.DilithiumGamma2;
    }

    int getDilithiumOmega() {
        return this.DilithiumOmega;
    }

    int getDilithiumCTilde() {
        return this.DilithiumCTilde;
    }

    int getCryptoPublicKeyBytes() {
        return this.CryptoPublicKeyBytes;
    }

    int getCryptoSecretKeyBytes() {
        return this.CryptoSecretKeyBytes;
    }

    int getCryptoBytes() {
        return this.CryptoBytes;
    }

    int getPolyUniformGamma1NBlocks() {
        return this.PolyUniformGamma1NBlocks;
    }

    SHAKEDigest getShake256Digest() {
        return this.shake256Digest;
    }

    DilithiumEngine(int i, SecureRandom secureRandom, boolean z) {
        this.DilithiumMode = i;
        switch (i) {
            case 2:
                this.DilithiumK = 4;
                this.DilithiumL = 4;
                this.DilithiumEta = 2;
                this.DilithiumTau = 39;
                this.DilithiumBeta = 78;
                this.DilithiumGamma1 = 131072;
                this.DilithiumGamma2 = 95232;
                this.DilithiumOmega = 80;
                this.DilithiumPolyZPackedBytes = WinError.ERROR_PAGEFILE_CREATE_FAILED;
                this.DilithiumPolyW1PackedBytes = 192;
                this.DilithiumPolyEtaPackedBytes = 96;
                this.DilithiumCTilde = 32;
                break;
            case 3:
                this.DilithiumK = 6;
                this.DilithiumL = 5;
                this.DilithiumEta = 4;
                this.DilithiumTau = 49;
                this.DilithiumBeta = 196;
                this.DilithiumGamma1 = 524288;
                this.DilithiumGamma2 = 261888;
                this.DilithiumOmega = 55;
                this.DilithiumPolyZPackedBytes = WinError.ERROR_MULTIPLE_FAULT_VIOLATION;
                this.DilithiumPolyW1PackedBytes = 128;
                this.DilithiumPolyEtaPackedBytes = 128;
                this.DilithiumCTilde = 48;
                break;
            case 4:
            default:
                throw new IllegalArgumentException("The mode " + i + "is not supported by Crystals Dilithium!");
            case 5:
                this.DilithiumK = 8;
                this.DilithiumL = 7;
                this.DilithiumEta = 2;
                this.DilithiumTau = 60;
                this.DilithiumBeta = 120;
                this.DilithiumGamma1 = 524288;
                this.DilithiumGamma2 = 261888;
                this.DilithiumOmega = 75;
                this.DilithiumPolyZPackedBytes = WinError.ERROR_MULTIPLE_FAULT_VIOLATION;
                this.DilithiumPolyW1PackedBytes = 128;
                this.DilithiumPolyEtaPackedBytes = 96;
                this.DilithiumCTilde = 64;
                break;
        }
        if (z) {
            this.symmetric = new Symmetric.AesSymmetric();
        } else {
            this.symmetric = new Symmetric.ShakeSymmetric();
        }
        this.random = secureRandom;
        this.DilithiumPolyVecHPackedBytes = this.DilithiumOmega + this.DilithiumK;
        this.CryptoPublicKeyBytes = 32 + (this.DilithiumK * 320);
        this.CryptoSecretKeyBytes = 128 + (this.DilithiumL * this.DilithiumPolyEtaPackedBytes) + (this.DilithiumK * this.DilithiumPolyEtaPackedBytes) + (this.DilithiumK * 416);
        this.CryptoBytes = this.DilithiumCTilde + (this.DilithiumL * this.DilithiumPolyZPackedBytes) + this.DilithiumPolyVecHPackedBytes;
        if (this.DilithiumGamma1 == 131072) {
            this.PolyUniformGamma1NBlocks = ((WinError.ERROR_PAGEFILE_CREATE_FAILED + this.symmetric.stream256BlockBytes) - 1) / this.symmetric.stream256BlockBytes;
        } else {
            if (this.DilithiumGamma1 != 524288) {
                throw new RuntimeException("Wrong Dilithium Gamma1!");
            }
            this.PolyUniformGamma1NBlocks = ((WinError.ERROR_MULTIPLE_FAULT_VIOLATION + this.symmetric.stream256BlockBytes) - 1) / this.symmetric.stream256BlockBytes;
        }
    }

    public byte[][] generateKeyPairInternal(byte[] bArr) {
        byte[] bArr2 = new byte[128];
        byte[] bArr3 = new byte[64];
        byte[] bArr4 = new byte[32];
        byte[] bArr5 = new byte[64];
        byte[] bArr6 = new byte[32];
        PolyVecMatrix polyVecMatrix = new PolyVecMatrix(this);
        PolyVecL polyVecL = new PolyVecL(this);
        PolyVecK polyVecK = new PolyVecK(this);
        PolyVecK polyVecK2 = new PolyVecK(this);
        PolyVecK polyVecK3 = new PolyVecK(this);
        this.shake256Digest.update(bArr, 0, 32);
        this.shake256Digest.update((byte) this.DilithiumK);
        this.shake256Digest.update((byte) this.DilithiumL);
        this.shake256Digest.doFinal(bArr2, 0, 128);
        System.arraycopy(bArr2, 0, bArr4, 0, 32);
        System.arraycopy(bArr2, 32, bArr5, 0, 64);
        System.arraycopy(bArr2, 96, bArr6, 0, 32);
        polyVecMatrix.expandMatrix(bArr4);
        polyVecL.uniformEta(bArr5, (short) 0);
        polyVecK.uniformEta(bArr5, (short) this.DilithiumL);
        PolyVecL polyVecL2 = new PolyVecL(this);
        polyVecL.copyPolyVecL(polyVecL2);
        polyVecL2.polyVecNtt();
        polyVecMatrix.pointwiseMontgomery(polyVecK2, polyVecL2);
        polyVecK2.reduce();
        polyVecK2.invNttToMont();
        polyVecK2.addPolyVecK(polyVecK);
        polyVecK2.conditionalAddQ();
        polyVecK2.power2Round(polyVecK3);
        byte[] bArrPackPublicKey = Packing.packPublicKey(polyVecK2, this);
        this.shake256Digest.update(bArr4, 0, bArr4.length);
        this.shake256Digest.update(bArrPackPublicKey, 0, bArrPackPublicKey.length);
        this.shake256Digest.doFinal(bArr3, 0, 64);
        byte[][] bArrPackSecretKey = Packing.packSecretKey(bArr4, bArr3, bArr6, polyVecK3, polyVecL, polyVecK, this);
        return new byte[][]{bArrPackSecretKey[0], bArrPackSecretKey[1], bArrPackSecretKey[2], bArrPackSecretKey[3], bArrPackSecretKey[4], bArrPackSecretKey[5], bArrPackPublicKey};
    }

    public byte[] signSignatureInternal(byte[] bArr, int i, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, byte[] bArr6, byte[] bArr7, byte[] bArr8) {
        byte[] bArr9 = new byte[this.CryptoBytes + i];
        byte[] bArr10 = new byte[64];
        byte[] bArr11 = new byte[64];
        short s = 0;
        PolyVecL polyVecL = new PolyVecL(this);
        PolyVecL polyVecL2 = new PolyVecL(this);
        PolyVecL polyVecL3 = new PolyVecL(this);
        PolyVecK polyVecK = new PolyVecK(this);
        PolyVecK polyVecK2 = new PolyVecK(this);
        PolyVecK polyVecK3 = new PolyVecK(this);
        PolyVecK polyVecK4 = new PolyVecK(this);
        PolyVecK polyVecK5 = new PolyVecK(this);
        Poly poly = new Poly(this);
        PolyVecMatrix polyVecMatrix = new PolyVecMatrix(this);
        Packing.unpackSecretKey(polyVecK, polyVecL, polyVecK2, bArr5, bArr6, bArr7, this);
        this.shake256Digest.update(bArr4, 0, 64);
        this.shake256Digest.update(bArr, 0, i);
        this.shake256Digest.doFinal(bArr10, 0, 64);
        byte[] bArrCopyOf = Arrays.copyOf(bArr3, 128);
        System.arraycopy(bArr8, 0, bArrCopyOf, 32, 32);
        System.arraycopy(bArr10, 0, bArrCopyOf, 64, 64);
        this.shake256Digest.update(bArrCopyOf, 0, 128);
        this.shake256Digest.doFinal(bArr11, 0, 64);
        polyVecMatrix.expandMatrix(bArr2);
        polyVecL.polyVecNtt();
        polyVecK2.polyVecNtt();
        polyVecK.polyVecNtt();
        int i2 = 0;
        while (i2 < 1000) {
            i2++;
            short s2 = s;
            s = (short) (s + 1);
            polyVecL2.uniformGamma1(bArr11, s2);
            polyVecL2.copyPolyVecL(polyVecL3);
            polyVecL3.polyVecNtt();
            polyVecMatrix.pointwiseMontgomery(polyVecK3, polyVecL3);
            polyVecK3.reduce();
            polyVecK3.invNttToMont();
            polyVecK3.conditionalAddQ();
            polyVecK3.decompose(polyVecK4);
            System.arraycopy(polyVecK3.packW1(), 0, bArr9, 0, this.DilithiumK * this.DilithiumPolyW1PackedBytes);
            this.shake256Digest.update(bArr10, 0, 64);
            this.shake256Digest.update(bArr9, 0, this.DilithiumK * this.DilithiumPolyW1PackedBytes);
            this.shake256Digest.doFinal(bArr9, 0, this.DilithiumCTilde);
            poly.challenge(Arrays.copyOfRange(bArr9, 0, this.DilithiumCTilde));
            poly.polyNtt();
            polyVecL3.pointwisePolyMontgomery(poly, polyVecL);
            polyVecL3.invNttToMont();
            polyVecL3.addPolyVecL(polyVecL2);
            polyVecL3.reduce();
            if (!polyVecL3.checkNorm(this.DilithiumGamma1 - this.DilithiumBeta)) {
                polyVecK5.pointwisePolyMontgomery(poly, polyVecK2);
                polyVecK5.invNttToMont();
                polyVecK4.subtract(polyVecK5);
                polyVecK4.reduce();
                if (polyVecK4.checkNorm(this.DilithiumGamma2 - this.DilithiumBeta)) {
                    continue;
                } else {
                    polyVecK5.pointwisePolyMontgomery(poly, polyVecK);
                    polyVecK5.invNttToMont();
                    polyVecK5.reduce();
                    if (polyVecK5.checkNorm(this.DilithiumGamma2)) {
                        continue;
                    } else {
                        polyVecK4.addPolyVecK(polyVecK5);
                        polyVecK4.conditionalAddQ();
                        if (polyVecK5.makeHint(polyVecK4, polyVecK3) <= this.DilithiumOmega) {
                            return Packing.packSignature(bArr9, polyVecL3, polyVecK5, this);
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean signVerifyInternal(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte[] bArr4) {
        byte[] bArr5 = new byte[64];
        byte[] bArr6 = new byte[this.DilithiumCTilde];
        Poly poly = new Poly(this);
        PolyVecMatrix polyVecMatrix = new PolyVecMatrix(this);
        PolyVecL polyVecL = new PolyVecL(this);
        PolyVecK polyVecK = new PolyVecK(this);
        PolyVecK polyVecK2 = new PolyVecK(this);
        PolyVecK polyVecK3 = new PolyVecK(this);
        if (i != this.CryptoBytes) {
            return false;
        }
        PolyVecK polyVecKUnpackPublicKey = Packing.unpackPublicKey(polyVecK, bArr4, this);
        if (!Packing.unpackSignature(polyVecL, polyVecK3, bArr, this)) {
            return false;
        }
        byte[] bArrCopyOfRange = Arrays.copyOfRange(bArr, 0, this.DilithiumCTilde);
        if (polyVecL.checkNorm(getDilithiumGamma1() - getDilithiumBeta())) {
            return false;
        }
        this.shake256Digest.update(bArr3, 0, bArr3.length);
        this.shake256Digest.update(bArr4, 0, bArr4.length);
        this.shake256Digest.doFinal(bArr5, 0, 64);
        this.shake256Digest.update(bArr5, 0, 64);
        this.shake256Digest.update(bArr2, 0, i2);
        this.shake256Digest.doFinal(bArr5, 0);
        poly.challenge(Arrays.copyOfRange(bArrCopyOfRange, 0, this.DilithiumCTilde));
        polyVecMatrix.expandMatrix(bArr3);
        polyVecL.polyVecNtt();
        polyVecMatrix.pointwiseMontgomery(polyVecK2, polyVecL);
        poly.polyNtt();
        polyVecKUnpackPublicKey.shiftLeft();
        polyVecKUnpackPublicKey.polyVecNtt();
        polyVecKUnpackPublicKey.pointwisePolyMontgomery(poly, polyVecKUnpackPublicKey);
        polyVecK2.subtract(polyVecKUnpackPublicKey);
        polyVecK2.reduce();
        polyVecK2.invNttToMont();
        polyVecK2.conditionalAddQ();
        polyVecK2.useHint(polyVecK2, polyVecK3);
        byte[] bArrPackW1 = polyVecK2.packW1();
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update(bArr5, 0, 64);
        sHAKEDigest.update(bArrPackW1, 0, this.DilithiumK * this.DilithiumPolyW1PackedBytes);
        sHAKEDigest.doFinal(bArr6, 0, this.DilithiumCTilde);
        return Arrays.constantTimeAreEqual(bArrCopyOfRange, bArr6);
    }

    public byte[][] generateKeyPair() {
        byte[] bArr = new byte[32];
        this.random.nextBytes(bArr);
        return generateKeyPairInternal(bArr);
    }

    public byte[] signSignature(byte[] bArr, int i, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, byte[] bArr6, byte[] bArr7) {
        byte[] bArr8 = new byte[32];
        if (this.random != null) {
            this.random.nextBytes(bArr8);
        }
        return signSignatureInternal(bArr, i, bArr2, bArr3, bArr4, bArr5, bArr6, bArr7, bArr8);
    }

    public byte[] sign(byte[] bArr, int i, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, byte[] bArr6, byte[] bArr7) {
        return signSignature(bArr, i, bArr2, bArr3, bArr4, bArr5, bArr6, bArr7);
    }

    public boolean signVerify(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte[] bArr4) {
        return signVerifyInternal(bArr, i, bArr2, i2, bArr3, bArr4);
    }

    public boolean signOpen(byte[] bArr, byte[] bArr2, int i, byte[] bArr3, byte[] bArr4) {
        return signVerify(bArr2, i, bArr, bArr.length, bArr3, bArr4);
    }
}
