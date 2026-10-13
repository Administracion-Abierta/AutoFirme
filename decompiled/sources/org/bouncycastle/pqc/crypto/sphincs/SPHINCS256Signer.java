package org.bouncycastle.pqc.crypto.sphincs;

import com.sun.jna.platform.win32.LMErr;
import com.sun.jna.platform.win32.WinError;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/sphincs/SPHINCS256Signer.class */
public class SPHINCS256Signer implements MessageSigner {
    private final HashFunctions hashFunctions;
    private byte[] keyData;

    public SPHINCS256Signer(Digest digest, Digest digest2) {
        if (digest.getDigestSize() != 32) {
            throw new IllegalArgumentException("n-digest needs to produce 32 bytes of output");
        }
        if (digest2.getDigestSize() != 64) {
            throw new IllegalArgumentException("2n-digest needs to produce 64 bytes of output");
        }
        this.hashFunctions = new HashFunctions(digest, digest2);
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public void init(boolean z, CipherParameters cipherParameters) {
        if (!z) {
            this.keyData = ((SPHINCSPublicKeyParameters) cipherParameters).getKeyData();
        } else if (cipherParameters instanceof ParametersWithRandom) {
            this.keyData = ((SPHINCSPrivateKeyParameters) ((ParametersWithRandom) cipherParameters).getParameters()).getKeyData();
        } else {
            this.keyData = ((SPHINCSPrivateKeyParameters) cipherParameters).getKeyData();
        }
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public byte[] generateSignature(byte[] bArr) {
        return crypto_sign(this.hashFunctions, bArr, this.keyData);
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public boolean verifySignature(byte[] bArr, byte[] bArr2) {
        return verify(this.hashFunctions, bArr, bArr2, this.keyData);
    }

    static void validate_authpath(HashFunctions hashFunctions, byte[] bArr, byte[] bArr2, int i, byte[] bArr3, int i2, byte[] bArr4, int i3) {
        byte[] bArr5 = new byte[64];
        if ((i & 1) != 0) {
            for (int i4 = 0; i4 < 32; i4++) {
                bArr5[32 + i4] = bArr2[i4];
            }
            for (int i5 = 0; i5 < 32; i5++) {
                bArr5[i5] = bArr3[i2 + i5];
            }
        } else {
            for (int i6 = 0; i6 < 32; i6++) {
                bArr5[i6] = bArr2[i6];
            }
            for (int i7 = 0; i7 < 32; i7++) {
                bArr5[32 + i7] = bArr3[i2 + i7];
            }
        }
        int i8 = i2 + 32;
        for (int i9 = 0; i9 < i3 - 1; i9++) {
            i >>>= 1;
            if ((i & 1) != 0) {
                hashFunctions.hash_2n_n_mask(bArr5, 32, bArr5, 0, bArr4, 2 * (7 + i9) * 32);
                for (int i10 = 0; i10 < 32; i10++) {
                    bArr5[i10] = bArr3[i8 + i10];
                }
            } else {
                hashFunctions.hash_2n_n_mask(bArr5, 0, bArr5, 0, bArr4, 2 * (7 + i9) * 32);
                for (int i11 = 0; i11 < 32; i11++) {
                    bArr5[i11 + 32] = bArr3[i8 + i11];
                }
            }
            i8 += 32;
        }
        hashFunctions.hash_2n_n_mask(bArr, 0, bArr5, 0, bArr4, 2 * ((7 + i3) - 1) * 32);
    }

    static void compute_authpath_wots(HashFunctions hashFunctions, byte[] bArr, byte[] bArr2, int i, Tree.leafaddr leafaddrVar, byte[] bArr3, byte[] bArr4, int i2) {
        Tree.leafaddr leafaddrVar2 = new Tree.leafaddr(leafaddrVar);
        byte[] bArr5 = new byte[2048];
        byte[] bArr6 = new byte[1024];
        byte[] bArr7 = new byte[68608];
        leafaddrVar2.subleaf = 0L;
        while (leafaddrVar2.subleaf < 32) {
            Seed.get_seed(hashFunctions, bArr6, (int) (leafaddrVar2.subleaf * 32), bArr3, leafaddrVar2);
            leafaddrVar2.subleaf++;
        }
        Wots wots = new Wots();
        leafaddrVar2.subleaf = 0L;
        while (leafaddrVar2.subleaf < 32) {
            wots.wots_pkgen(hashFunctions, bArr7, (int) (leafaddrVar2.subleaf * 67 * 32), bArr6, (int) (leafaddrVar2.subleaf * 32), bArr4, 0);
            leafaddrVar2.subleaf++;
        }
        leafaddrVar2.subleaf = 0L;
        while (leafaddrVar2.subleaf < 32) {
            Tree.l_tree(hashFunctions, bArr5, (int) (1024 + (leafaddrVar2.subleaf * 32)), bArr7, (int) (leafaddrVar2.subleaf * 67 * 32), bArr4, 0);
            leafaddrVar2.subleaf++;
        }
        int i3 = 0;
        int i4 = 32;
        while (true) {
            int i5 = i4;
            if (i5 <= 0) {
                break;
            }
            for (int i6 = 0; i6 < i5; i6 += 2) {
                hashFunctions.hash_2n_n_mask(bArr5, ((i5 >>> 1) * 32) + ((i6 >>> 1) * 32), bArr5, (i5 * 32) + (i6 * 32), bArr4, 2 * (7 + i3) * 32);
            }
            i3++;
            i4 = i5 >>> 1;
        }
        int i7 = (int) leafaddrVar.subleaf;
        for (int i8 = 0; i8 < i2; i8++) {
            System.arraycopy(bArr5, ((32 >>> i8) * 32) + (((i7 >>> i8) ^ 1) * 32), bArr2, i + (i8 * 32), 32);
        }
        System.arraycopy(bArr5, 32, bArr, 0, 32);
    }

    byte[] crypto_sign(HashFunctions hashFunctions, byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[41000];
        byte[] bArr4 = new byte[32];
        byte[] bArr5 = new byte[64];
        long[] jArr = new long[8];
        byte[] bArr6 = new byte[32];
        byte[] bArr7 = new byte[32];
        byte[] bArr8 = new byte[1024];
        byte[] bArr9 = new byte[1088];
        for (int i = 0; i < 1088; i++) {
            bArr9[i] = bArr2[i];
        }
        System.arraycopy(bArr9, WinError.ERROR_SERVICE_ALREADY_RUNNING, bArr3, 40968, 32);
        Digest messageHash = hashFunctions.getMessageHash();
        byte[] bArr10 = new byte[messageHash.getDigestSize()];
        messageHash.update(bArr3, 40968, 32);
        messageHash.update(bArr, 0, bArr.length);
        messageHash.doFinal(bArr10, 0);
        zerobytes(bArr3, 40968, 32);
        for (int i2 = 0; i2 != jArr.length; i2++) {
            jArr[i2] = Pack.littleEndianToLong(bArr10, i2 * 8);
        }
        long j = jArr[0] & 1152921504606846975L;
        System.arraycopy(bArr10, 16, bArr4, 0, 32);
        System.arraycopy(bArr4, 0, bArr3, 39912, 32);
        Tree.leafaddr leafaddrVar = new Tree.leafaddr();
        leafaddrVar.level = 11;
        leafaddrVar.subtree = 0L;
        leafaddrVar.subleaf = 0L;
        int i3 = 39912 + 32;
        System.arraycopy(bArr9, 32, bArr3, i3, 1024);
        Tree.treehash(hashFunctions, bArr3, i3 + 1024, 5, bArr9, leafaddrVar, bArr3, i3);
        Digest messageHash2 = hashFunctions.getMessageHash();
        messageHash2.update(bArr3, 39912, 1088);
        messageHash2.update(bArr, 0, bArr.length);
        messageHash2.doFinal(bArr5, 0);
        Tree.leafaddr leafaddrVar2 = new Tree.leafaddr();
        leafaddrVar2.level = 12;
        leafaddrVar2.subleaf = (int) (j & 31);
        leafaddrVar2.subtree = j >>> 5;
        for (int i4 = 0; i4 < 32; i4++) {
            bArr3[i4] = bArr4[i4];
        }
        System.arraycopy(bArr9, 32, bArr8, 0, 1024);
        for (int i5 = 0; i5 < 8; i5++) {
            bArr3[32 + i5] = (byte) ((j >>> (8 * i5)) & 255);
        }
        int i6 = 32 + 8;
        Seed.get_seed(hashFunctions, bArr7, 0, bArr9, leafaddrVar2);
        new Horst();
        int iHorst_sign = i6 + Horst.horst_sign(hashFunctions, bArr3, i6, bArr6, bArr7, bArr8, bArr5);
        Wots wots = new Wots();
        for (int i7 = 0; i7 < 12; i7++) {
            leafaddrVar2.level = i7;
            Seed.get_seed(hashFunctions, bArr7, 0, bArr9, leafaddrVar2);
            wots.wots_sign(hashFunctions, bArr3, iHorst_sign, bArr6, bArr7, bArr8);
            int i8 = iHorst_sign + LMErr.NERR_DupNameReboot;
            compute_authpath_wots(hashFunctions, bArr6, bArr3, i8, leafaddrVar2, bArr9, bArr8, 5);
            iHorst_sign = i8 + 160;
            leafaddrVar2.subleaf = (int) (leafaddrVar2.subtree & 31);
            leafaddrVar2.subtree >>>= 5;
        }
        zerobytes(bArr9, 0, 1088);
        return bArr3;
    }

    private void zerobytes(byte[] bArr, int i, int i2) {
        for (int i3 = 0; i3 != i2; i3++) {
            bArr[i + i3] = 0;
        }
    }

    boolean verify(HashFunctions hashFunctions, byte[] bArr, byte[] bArr2, byte[] bArr3) {
        int length = bArr2.length;
        long j = 0;
        byte[] bArr4 = new byte[LMErr.NERR_DupNameReboot];
        byte[] bArr5 = new byte[32];
        byte[] bArr6 = new byte[32];
        byte[] bArr7 = new byte[41000];
        byte[] bArr8 = new byte[WinError.ERROR_SERVICE_ALREADY_RUNNING];
        if (length != 41000) {
            throw new IllegalArgumentException("signature wrong size");
        }
        byte[] bArr9 = new byte[64];
        for (int i = 0; i < 1056; i++) {
            bArr8[i] = bArr3[i];
        }
        byte[] bArr10 = new byte[32];
        for (int i2 = 0; i2 < 32; i2++) {
            bArr10[i2] = bArr2[i2];
        }
        System.arraycopy(bArr2, 0, bArr7, 0, 41000);
        Digest messageHash = hashFunctions.getMessageHash();
        messageHash.update(bArr10, 0, 32);
        messageHash.update(bArr8, 0, WinError.ERROR_SERVICE_ALREADY_RUNNING);
        messageHash.update(bArr, 0, bArr.length);
        messageHash.doFinal(bArr9, 0);
        int i3 = 0 + 32;
        int i4 = length - 32;
        for (int i5 = 0; i5 < 8; i5++) {
            j ^= ((long) (bArr7[i3 + i5] & 255)) << (8 * i5);
        }
        new Horst();
        Horst.horst_verify(hashFunctions, bArr6, bArr7, i3 + 8, bArr8, bArr9);
        int i6 = i3 + 8 + 13312;
        int i7 = (i4 - 8) - 13312;
        Wots wots = new Wots();
        for (int i8 = 0; i8 < 12; i8++) {
            wots.wots_verify(hashFunctions, bArr4, bArr7, i6, bArr6, bArr8);
            int i9 = i6 + LMErr.NERR_DupNameReboot;
            Tree.l_tree(hashFunctions, bArr5, 0, bArr4, 0, bArr8, 0);
            validate_authpath(hashFunctions, bArr6, bArr5, (int) (j & 31), bArr7, i9, bArr8, 5);
            j >>= 5;
            i6 = i9 + 160;
            i7 = (i7 - 2144) - 160;
        }
        boolean z = true;
        for (int i10 = 0; i10 < 32; i10++) {
            z &= bArr6[i10] == bArr8[i10 + 1024];
        }
        return z;
    }
}
