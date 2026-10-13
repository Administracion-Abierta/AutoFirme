package org.spongycastle.pqc.crypto.sphincs;

import com.sun.jna.platform.win32.WinError;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/sphincs/Horst.class */
class Horst {
    static final int HORST_LOGT = 16;
    static final int HORST_T = 65536;
    static final int HORST_K = 32;
    static final int HORST_SKBYTES = 32;
    static final int HORST_SIGBYTES = 13312;
    static final int N_MASKS = 32;

    Horst() {
    }

    static void expand_seed(byte[] outseeds, byte[] inseed) {
        Seed.prg(outseeds, 0, 2097152L, inseed, 0);
    }

    static int horst_sign(HashFunctions hs, byte[] sig, int sigOff, byte[] pk, byte[] seed, byte[] masks, byte[] m_hash) {
        byte[] sk = new byte[2097152];
        int sigpos = sigOff;
        byte[] tree = new byte[4194272];
        expand_seed(sk, seed);
        for (int i = 0; i < 65536; i++) {
            hs.hash_n_n(tree, (65535 + i) * 32, sk, i * 32);
        }
        for (int i2 = 0; i2 < 16; i2++) {
            long offset_in = (1 << (16 - i2)) - 1;
            long offset_out = (1 << ((16 - i2) - 1)) - 1;
            for (int j = 0; j < (1 << ((16 - i2) - 1)); j++) {
                hs.hash_2n_n_mask(tree, (int) ((offset_out + ((long) j)) * 32), tree, (int) ((offset_in + ((long) (2 * j))) * 32), masks, 2 * i2 * 32);
            }
        }
        for (int j2 = 2016; j2 < 4064; j2++) {
            int i3 = sigpos;
            sigpos++;
            sig[i3] = tree[j2];
        }
        for (int i4 = 0; i4 < 32; i4++) {
            int idx = (m_hash[2 * i4] & 255) + ((m_hash[(2 * i4) + 1] & 255) << 8);
            for (int k = 0; k < 32; k++) {
                int i5 = sigpos;
                sigpos++;
                sig[i5] = sk[(idx * 32) + k];
            }
            int idx2 = idx + 65535;
            for (int j3 = 0; j3 < 10; j3++) {
                int idx3 = (idx2 & 1) != 0 ? idx2 + 1 : idx2 - 1;
                for (int k2 = 0; k2 < 32; k2++) {
                    int i6 = sigpos;
                    sigpos++;
                    sig[i6] = tree[(idx3 * 32) + k2];
                }
                idx2 = (idx3 - 1) / 2;
            }
        }
        for (int i7 = 0; i7 < 32; i7++) {
            pk[i7] = tree[i7];
        }
        return HORST_SIGBYTES;
    }

    static int horst_verify(HashFunctions hs, byte[] pk, byte[] sig, int sigOff, byte[] masks, byte[] m_hash) {
        byte[] buffer = new byte[1024];
        int sigOffset = sigOff + 2048;
        for (int i = 0; i < 32; i++) {
            int idx = (m_hash[2 * i] & 255) + ((m_hash[(2 * i) + 1] & 255) << 8);
            if ((idx & 1) == 0) {
                hs.hash_n_n(buffer, 0, sig, sigOffset);
                for (int k = 0; k < 32; k++) {
                    buffer[32 + k] = sig[sigOffset + 32 + k];
                }
            } else {
                hs.hash_n_n(buffer, 32, sig, sigOffset);
                for (int k2 = 0; k2 < 32; k2++) {
                    buffer[k2] = sig[sigOffset + 32 + k2];
                }
            }
            sigOffset += 64;
            for (int j = 1; j < 10; j++) {
                idx >>>= 1;
                if ((idx & 1) == 0) {
                    hs.hash_2n_n_mask(buffer, 0, buffer, 0, masks, 2 * (j - 1) * 32);
                    for (int k3 = 0; k3 < 32; k3++) {
                        buffer[32 + k3] = sig[sigOffset + k3];
                    }
                } else {
                    hs.hash_2n_n_mask(buffer, 32, buffer, 0, masks, 2 * (j - 1) * 32);
                    for (int k4 = 0; k4 < 32; k4++) {
                        buffer[k4] = sig[sigOffset + k4];
                    }
                }
                sigOffset += 32;
            }
            int idx2 = idx >>> 1;
            hs.hash_2n_n_mask(buffer, 0, buffer, 0, masks, WinError.ERROR_PAGEFILE_CREATE_FAILED);
            for (int k5 = 0; k5 < 32; k5++) {
                if (sig[sigOff + (idx2 * 32) + k5] != buffer[k5]) {
                    for (int k6 = 0; k6 < 32; k6++) {
                        pk[k6] = 0;
                    }
                    return -1;
                }
            }
        }
        for (int j2 = 0; j2 < 32; j2++) {
            hs.hash_2n_n_mask(buffer, j2 * 32, sig, sigOff + (2 * j2 * 32), masks, WinError.ERROR_MULTIPLE_FAULT_VIOLATION);
        }
        for (int j3 = 0; j3 < 16; j3++) {
            hs.hash_2n_n_mask(buffer, j3 * 32, buffer, 2 * j3 * 32, masks, WinError.ERROR_FT_READ_RECOVERY_FROM_BACKUP);
        }
        for (int j4 = 0; j4 < 8; j4++) {
            hs.hash_2n_n_mask(buffer, j4 * 32, buffer, 2 * j4 * 32, masks, 768);
        }
        for (int j5 = 0; j5 < 4; j5++) {
            hs.hash_2n_n_mask(buffer, j5 * 32, buffer, 2 * j5 * 32, masks, 832);
        }
        for (int j6 = 0; j6 < 2; j6++) {
            hs.hash_2n_n_mask(buffer, j6 * 32, buffer, 2 * j6 * 32, masks, 896);
        }
        hs.hash_2n_n_mask(pk, 0, buffer, 0, masks, 960);
        return 0;
    }
}
