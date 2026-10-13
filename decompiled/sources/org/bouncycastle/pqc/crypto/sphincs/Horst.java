package org.bouncycastle.pqc.crypto.sphincs;

import com.sun.jna.platform.win32.WinError;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/sphincs/Horst.class */
class Horst {
    static final int HORST_LOGT = 16;
    static final int HORST_T = 65536;
    static final int HORST_K = 32;
    static final int HORST_SKBYTES = 32;
    static final int HORST_SIGBYTES = 13312;
    static final int N_MASKS = 32;

    Horst() {
    }

    static void expand_seed(byte[] bArr, byte[] bArr2) {
        Seed.prg(bArr, 0, 2097152L, bArr2, 0);
    }

    static int horst_sign(HashFunctions hashFunctions, byte[] bArr, int i, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5) {
        byte[] bArr6 = new byte[2097152];
        int i2 = i;
        byte[] bArr7 = new byte[4194272];
        expand_seed(bArr6, bArr3);
        for (int i3 = 0; i3 < 65536; i3++) {
            hashFunctions.hash_n_n(bArr7, (65535 + i3) * 32, bArr6, i3 * 32);
        }
        for (int i4 = 0; i4 < 16; i4++) {
            long j = (1 << (16 - i4)) - 1;
            long j2 = (1 << ((16 - i4) - 1)) - 1;
            for (int i5 = 0; i5 < (1 << ((16 - i4) - 1)); i5++) {
                hashFunctions.hash_2n_n_mask(bArr7, (int) ((j2 + ((long) i5)) * 32), bArr7, (int) ((j + ((long) (2 * i5))) * 32), bArr4, 2 * i4 * 32);
            }
        }
        for (int i6 = 2016; i6 < 4064; i6++) {
            int i7 = i2;
            i2++;
            bArr[i7] = bArr7[i6];
        }
        for (int i8 = 0; i8 < 32; i8++) {
            int i9 = (bArr5[2 * i8] & 255) + ((bArr5[(2 * i8) + 1] & 255) << 8);
            for (int i10 = 0; i10 < 32; i10++) {
                int i11 = i2;
                i2++;
                bArr[i11] = bArr6[(i9 * 32) + i10];
            }
            int i12 = i9 + 65535;
            for (int i13 = 0; i13 < 10; i13++) {
                int i14 = (i12 & 1) != 0 ? i12 + 1 : i12 - 1;
                for (int i15 = 0; i15 < 32; i15++) {
                    int i16 = i2;
                    i2++;
                    bArr[i16] = bArr7[(i14 * 32) + i15];
                }
                i12 = (i14 - 1) / 2;
            }
        }
        for (int i17 = 0; i17 < 32; i17++) {
            bArr2[i17] = bArr7[i17];
        }
        return HORST_SIGBYTES;
    }

    static int horst_verify(HashFunctions hashFunctions, byte[] bArr, byte[] bArr2, int i, byte[] bArr3, byte[] bArr4) {
        byte[] bArr5 = new byte[1024];
        int i2 = i + 2048;
        for (int i3 = 0; i3 < 32; i3++) {
            int i4 = (bArr4[2 * i3] & 255) + ((bArr4[(2 * i3) + 1] & 255) << 8);
            if ((i4 & 1) == 0) {
                hashFunctions.hash_n_n(bArr5, 0, bArr2, i2);
                for (int i5 = 0; i5 < 32; i5++) {
                    bArr5[32 + i5] = bArr2[i2 + 32 + i5];
                }
            } else {
                hashFunctions.hash_n_n(bArr5, 32, bArr2, i2);
                for (int i6 = 0; i6 < 32; i6++) {
                    bArr5[i6] = bArr2[i2 + 32 + i6];
                }
            }
            i2 += 64;
            for (int i7 = 1; i7 < 10; i7++) {
                i4 >>>= 1;
                if ((i4 & 1) == 0) {
                    hashFunctions.hash_2n_n_mask(bArr5, 0, bArr5, 0, bArr3, 2 * (i7 - 1) * 32);
                    for (int i8 = 0; i8 < 32; i8++) {
                        bArr5[32 + i8] = bArr2[i2 + i8];
                    }
                } else {
                    hashFunctions.hash_2n_n_mask(bArr5, 32, bArr5, 0, bArr3, 2 * (i7 - 1) * 32);
                    for (int i9 = 0; i9 < 32; i9++) {
                        bArr5[i9] = bArr2[i2 + i9];
                    }
                }
                i2 += 32;
            }
            int i10 = i4 >>> 1;
            hashFunctions.hash_2n_n_mask(bArr5, 0, bArr5, 0, bArr3, WinError.ERROR_PAGEFILE_CREATE_FAILED);
            for (int i11 = 0; i11 < 32; i11++) {
                if (bArr2[i + (i10 * 32) + i11] != bArr5[i11]) {
                    for (int i12 = 0; i12 < 32; i12++) {
                        bArr[i12] = 0;
                    }
                    return -1;
                }
            }
        }
        for (int i13 = 0; i13 < 32; i13++) {
            hashFunctions.hash_2n_n_mask(bArr5, i13 * 32, bArr2, i + (2 * i13 * 32), bArr3, WinError.ERROR_MULTIPLE_FAULT_VIOLATION);
        }
        for (int i14 = 0; i14 < 16; i14++) {
            hashFunctions.hash_2n_n_mask(bArr5, i14 * 32, bArr5, 2 * i14 * 32, bArr3, WinError.ERROR_FT_READ_RECOVERY_FROM_BACKUP);
        }
        for (int i15 = 0; i15 < 8; i15++) {
            hashFunctions.hash_2n_n_mask(bArr5, i15 * 32, bArr5, 2 * i15 * 32, bArr3, 768);
        }
        for (int i16 = 0; i16 < 4; i16++) {
            hashFunctions.hash_2n_n_mask(bArr5, i16 * 32, bArr5, 2 * i16 * 32, bArr3, 832);
        }
        for (int i17 = 0; i17 < 2; i17++) {
            hashFunctions.hash_2n_n_mask(bArr5, i17 * 32, bArr5, 2 * i17 * 32, bArr3, 896);
        }
        hashFunctions.hash_2n_n_mask(bArr, 0, bArr5, 0, bArr3, 960);
        return 0;
    }
}
