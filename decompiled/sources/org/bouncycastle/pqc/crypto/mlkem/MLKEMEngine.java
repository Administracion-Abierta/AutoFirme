package org.bouncycastle.pqc.crypto.mlkem;

import com.sun.jna.platform.win32.WinError;
import java.security.SecureRandom;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mlkem/MLKEMEngine.class */
class MLKEMEngine {
    private SecureRandom random;
    private MLKEMIndCpa indCpa;
    public static final int KyberN = 256;
    public static final int KyberQ = 3329;
    public static final int KyberQinv = 62209;
    public static final int KyberSymBytes = 32;
    private static final int KyberSharedSecretBytes = 32;
    public static final int KyberPolyBytes = 384;
    private static final int KyberEta2 = 2;
    private static final int KyberIndCpaMsgBytes = 32;
    private final int KyberK;
    private final int KyberPolyVecBytes;
    private final int KyberPolyCompressedBytes;
    private final int KyberPolyVecCompressedBytes;
    private final int KyberEta1;
    private final int KyberIndCpaPublicKeyBytes;
    private final int KyberIndCpaSecretKeyBytes;
    private final int KyberIndCpaBytes;
    private final int KyberPublicKeyBytes;
    private final int KyberSecretKeyBytes;
    private final int KyberCipherTextBytes;
    private final int CryptoBytes;
    private final int CryptoSecretKeyBytes;
    private final int CryptoPublicKeyBytes;
    private final int CryptoCipherTextBytes;
    private final int sessionKeyLength;
    private final Symmetric symmetric;

    public Symmetric getSymmetric() {
        return this.symmetric;
    }

    public static int getKyberEta2() {
        return 2;
    }

    public static int getKyberIndCpaMsgBytes() {
        return 32;
    }

    public int getCryptoCipherTextBytes() {
        return this.CryptoCipherTextBytes;
    }

    public int getCryptoPublicKeyBytes() {
        return this.CryptoPublicKeyBytes;
    }

    public int getCryptoSecretKeyBytes() {
        return this.CryptoSecretKeyBytes;
    }

    public int getCryptoBytes() {
        return this.CryptoBytes;
    }

    public int getKyberCipherTextBytes() {
        return this.KyberCipherTextBytes;
    }

    public int getKyberSecretKeyBytes() {
        return this.KyberSecretKeyBytes;
    }

    public int getKyberIndCpaPublicKeyBytes() {
        return this.KyberIndCpaPublicKeyBytes;
    }

    public int getKyberIndCpaSecretKeyBytes() {
        return this.KyberIndCpaSecretKeyBytes;
    }

    public int getKyberIndCpaBytes() {
        return this.KyberIndCpaBytes;
    }

    public int getKyberPublicKeyBytes() {
        return this.KyberPublicKeyBytes;
    }

    public int getKyberPolyCompressedBytes() {
        return this.KyberPolyCompressedBytes;
    }

    public int getKyberK() {
        return this.KyberK;
    }

    public int getKyberPolyVecBytes() {
        return this.KyberPolyVecBytes;
    }

    public int getKyberPolyVecCompressedBytes() {
        return this.KyberPolyVecCompressedBytes;
    }

    public int getKyberEta1() {
        return this.KyberEta1;
    }

    public MLKEMEngine(int i) {
        this.KyberK = i;
        switch (i) {
            case 2:
                this.KyberEta1 = 3;
                this.KyberPolyCompressedBytes = 128;
                this.KyberPolyVecCompressedBytes = i * 320;
                this.sessionKeyLength = 32;
                break;
            case 3:
                this.KyberEta1 = 2;
                this.KyberPolyCompressedBytes = 128;
                this.KyberPolyVecCompressedBytes = i * 320;
                this.sessionKeyLength = 32;
                break;
            case 4:
                this.KyberEta1 = 2;
                this.KyberPolyCompressedBytes = 160;
                this.KyberPolyVecCompressedBytes = i * WinError.ERROR_FAIL_RESTART;
                this.sessionKeyLength = 32;
                break;
            default:
                throw new IllegalArgumentException("K: " + i + " is not supported for Crystals Kyber");
        }
        this.KyberPolyVecBytes = i * 384;
        this.KyberIndCpaPublicKeyBytes = this.KyberPolyVecBytes + 32;
        this.KyberIndCpaSecretKeyBytes = this.KyberPolyVecBytes;
        this.KyberIndCpaBytes = this.KyberPolyVecCompressedBytes + this.KyberPolyCompressedBytes;
        this.KyberPublicKeyBytes = this.KyberIndCpaPublicKeyBytes;
        this.KyberSecretKeyBytes = this.KyberIndCpaSecretKeyBytes + this.KyberIndCpaPublicKeyBytes + 64;
        this.KyberCipherTextBytes = this.KyberIndCpaBytes;
        this.CryptoBytes = 32;
        this.CryptoSecretKeyBytes = this.KyberSecretKeyBytes;
        this.CryptoPublicKeyBytes = this.KyberPublicKeyBytes;
        this.CryptoCipherTextBytes = this.KyberCipherTextBytes;
        this.symmetric = new Symmetric.ShakeSymmetric();
        this.indCpa = new MLKEMIndCpa(this);
    }

    public void init(SecureRandom secureRandom) {
        this.random = secureRandom;
    }

    public byte[][] generateKemKeyPair() {
        byte[] bArr = new byte[32];
        byte[] bArr2 = new byte[32];
        this.random.nextBytes(bArr);
        this.random.nextBytes(bArr2);
        return generateKemKeyPairInternal(bArr, bArr2);
    }

    public byte[][] generateKemKeyPairInternal(byte[] bArr, byte[] bArr2) {
        byte[][] bArrGenerateKeyPair = this.indCpa.generateKeyPair(bArr);
        byte[] bArr3 = new byte[this.KyberIndCpaSecretKeyBytes];
        System.arraycopy(bArrGenerateKeyPair[1], 0, bArr3, 0, this.KyberIndCpaSecretKeyBytes);
        byte[] bArr4 = new byte[32];
        this.symmetric.hash_h(bArr4, bArrGenerateKeyPair[0], 0);
        byte[] bArr5 = new byte[this.KyberIndCpaPublicKeyBytes];
        System.arraycopy(bArrGenerateKeyPair[0], 0, bArr5, 0, this.KyberIndCpaPublicKeyBytes);
        return new byte[][]{Arrays.copyOfRange(bArr5, 0, bArr5.length - 32), Arrays.copyOfRange(bArr5, bArr5.length - 32, bArr5.length), bArr3, bArr4, bArr2, Arrays.concatenate(bArr, bArr2)};
    }

    public byte[][] kemEncryptInternal(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[64];
        byte[] bArr4 = new byte[64];
        System.arraycopy(bArr2, 0, bArr3, 0, 32);
        this.symmetric.hash_h(bArr3, bArr, 32);
        this.symmetric.hash_g(bArr4, bArr3);
        byte[] bArrEncrypt = this.indCpa.encrypt(bArr, Arrays.copyOfRange(bArr3, 0, 32), Arrays.copyOfRange(bArr4, 32, bArr4.length));
        byte[] bArr5 = new byte[this.sessionKeyLength];
        System.arraycopy(bArr4, 0, bArr5, 0, bArr5.length);
        return new byte[][]{bArr5, bArrEncrypt};
    }

    public byte[] kemDecryptInternal(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[64];
        byte[] bArr4 = new byte[64];
        byte[] bArrCopyOfRange = Arrays.copyOfRange(bArr, this.KyberIndCpaSecretKeyBytes, bArr.length);
        System.arraycopy(this.indCpa.decrypt(bArr, bArr2), 0, bArr3, 0, 32);
        System.arraycopy(bArr, this.KyberSecretKeyBytes - 64, bArr3, 32, 32);
        this.symmetric.hash_g(bArr4, bArr3);
        byte[] bArr5 = new byte[32 + this.KyberCipherTextBytes];
        System.arraycopy(bArr, this.KyberSecretKeyBytes - 32, bArr5, 0, 32);
        System.arraycopy(bArr2, 0, bArr5, 32, this.KyberCipherTextBytes);
        this.symmetric.kdf(bArr5, bArr5);
        cmov(bArr4, bArr5, 32, !Arrays.constantTimeAreEqual(bArr2, this.indCpa.encrypt(bArrCopyOfRange, Arrays.copyOfRange(bArr3, 0, 32), Arrays.copyOfRange(bArr4, 32, bArr4.length))));
        return Arrays.copyOfRange(bArr4, 0, this.sessionKeyLength);
    }

    public byte[][] kemEncrypt(byte[] bArr, byte[] bArr2) {
        if (bArr.length != this.KyberIndCpaPublicKeyBytes) {
            throw new IllegalArgumentException("Input validation Error: Type check failed for ml-kem encapsulation");
        }
        PolyVec polyVec = new PolyVec(this);
        if (Arrays.areEqual(this.indCpa.packPublicKey(polyVec, this.indCpa.unpackPublicKey(polyVec, bArr)), bArr)) {
            return kemEncryptInternal(bArr, bArr2);
        }
        throw new IllegalArgumentException("Input validation: Modulus check failed for ml-kem encapsulation");
    }

    public byte[] kemDecrypt(byte[] bArr, byte[] bArr2) {
        return kemDecryptInternal(bArr, bArr2);
    }

    private void cmov(byte[] bArr, byte[] bArr2, int i, boolean z) {
        if (z) {
            System.arraycopy(bArr2, 0, bArr, 0, i);
        } else {
            System.arraycopy(bArr, 0, bArr, 0, i);
        }
    }

    public void getRandomBytes(byte[] bArr) {
        this.random.nextBytes(bArr);
    }
}
