package org.spongycastle.pqc.crypto.sphincs;

import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/sphincs/Permute.class */
class Permute {
    private static final int CHACHA_ROUNDS = 12;

    Permute() {
    }

    protected static int rotl(int x, int y) {
        return (x << y) | (x >>> (-y));
    }

    public static void permute(int rounds, int[] x) {
        if (x.length != 16) {
            throw new IllegalArgumentException();
        }
        if (rounds % 2 != 0) {
            throw new IllegalArgumentException("Number of rounds must be even");
        }
        int x00 = x[0];
        int x01 = x[1];
        int x02 = x[2];
        int x03 = x[3];
        int x04 = x[4];
        int x05 = x[5];
        int x06 = x[6];
        int x07 = x[7];
        int x08 = x[8];
        int x09 = x[9];
        int x10 = x[10];
        int x11 = x[11];
        int x12 = x[12];
        int x13 = x[13];
        int x14 = x[14];
        int x15 = x[15];
        for (int i = rounds; i > 0; i -= 2) {
            int x010 = x00 + x04;
            int x16 = rotl(x12 ^ x010, 16);
            int x011 = x08 + x16;
            int x012 = rotl(x04 ^ x011, 12);
            int x013 = x010 + x012;
            int x17 = rotl(x16 ^ x013, 8);
            int x014 = x011 + x17;
            int x015 = rotl(x012 ^ x014, 7);
            int x016 = x01 + x05;
            int x18 = rotl(x13 ^ x016, 16);
            int x017 = x09 + x18;
            int x018 = rotl(x05 ^ x017, 12);
            int x019 = x016 + x018;
            int x19 = rotl(x18 ^ x019, 8);
            int x020 = x017 + x19;
            int x021 = rotl(x018 ^ x020, 7);
            int x022 = x02 + x06;
            int x110 = rotl(x14 ^ x022, 16);
            int x111 = x10 + x110;
            int x023 = rotl(x06 ^ x111, 12);
            int x024 = x022 + x023;
            int x112 = rotl(x110 ^ x024, 8);
            int x113 = x111 + x112;
            int x025 = rotl(x023 ^ x113, 7);
            int x026 = x03 + x07;
            int x114 = rotl(x15 ^ x026, 16);
            int x115 = x11 + x114;
            int x027 = rotl(x07 ^ x115, 12);
            int x028 = x026 + x027;
            int x116 = rotl(x114 ^ x028, 8);
            int x117 = x115 + x116;
            int x029 = rotl(x027 ^ x117, 7);
            int x030 = x013 + x021;
            int x118 = rotl(x116 ^ x030, 16);
            int x119 = x113 + x118;
            int x031 = rotl(x021 ^ x119, 12);
            x00 = x030 + x031;
            x15 = rotl(x118 ^ x00, 8);
            x10 = x119 + x15;
            x05 = rotl(x031 ^ x10, 7);
            int x032 = x019 + x025;
            int x120 = rotl(x17 ^ x032, 16);
            int x121 = x117 + x120;
            int x033 = rotl(x025 ^ x121, 12);
            x01 = x032 + x033;
            x12 = rotl(x120 ^ x01, 8);
            x11 = x121 + x12;
            x06 = rotl(x033 ^ x11, 7);
            int x034 = x024 + x029;
            int x122 = rotl(x19 ^ x034, 16);
            int x035 = x014 + x122;
            int x036 = rotl(x029 ^ x035, 12);
            x02 = x034 + x036;
            x13 = rotl(x122 ^ x02, 8);
            x08 = x035 + x13;
            x07 = rotl(x036 ^ x08, 7);
            int x037 = x028 + x015;
            int x123 = rotl(x112 ^ x037, 16);
            int x038 = x020 + x123;
            int x039 = rotl(x015 ^ x038, 12);
            x03 = x037 + x039;
            x14 = rotl(x123 ^ x03, 8);
            x09 = x038 + x14;
            x04 = rotl(x039 ^ x09, 7);
        }
        x[0] = x00;
        x[1] = x01;
        x[2] = x02;
        x[3] = x03;
        x[4] = x04;
        x[5] = x05;
        x[6] = x06;
        x[7] = x07;
        x[8] = x08;
        x[9] = x09;
        x[10] = x10;
        x[11] = x11;
        x[12] = x12;
        x[13] = x13;
        x[14] = x14;
        x[15] = x15;
    }

    void chacha_permute(byte[] out, byte[] in) {
        int[] x = new int[16];
        for (int i = 0; i < 16; i++) {
            x[i] = Pack.littleEndianToInt(in, 4 * i);
        }
        permute(12, x);
        for (int i2 = 0; i2 < 16; i2++) {
            Pack.intToLittleEndian(x[i2], out, 4 * i2);
        }
    }
}
