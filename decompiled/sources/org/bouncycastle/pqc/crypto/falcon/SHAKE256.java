package org.bouncycastle.pqc.crypto.falcon;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/falcon/SHAKE256.class */
class SHAKE256 {
    private long[] RC = {1, 32898, -9223372036854742902L, -9223372034707259392L, 32907, 2147483649L, -9223372034707259263L, -9223372036854743031L, 138, 136, 2147516425L, 2147483658L, 2147516555L, -9223372036854775669L, -9223372036854742903L, -9223372036854743037L, -9223372036854743038L, -9223372036854775680L, 32778, -9223372034707292150L, -9223372034707259263L, -9223372036854742912L, 2147483649L, -9223372034707259384L};
    long[] A = new long[25];
    byte[] dbuf = new byte[200];
    long dptr = 0;

    SHAKE256() {
    }

    void process_block(long[] jArr) {
        jArr[1] = jArr[1] ^ (-1);
        jArr[2] = jArr[2] ^ (-1);
        jArr[8] = jArr[8] ^ (-1);
        jArr[12] = jArr[12] ^ (-1);
        jArr[17] = jArr[17] ^ (-1);
        jArr[20] = jArr[20] ^ (-1);
        for (int i = 0; i < 24; i += 2) {
            long j = (jArr[1] ^ jArr[6]) ^ (jArr[21] ^ (jArr[11] ^ jArr[16]));
            long j2 = (((j << 1) | (j >>> 63)) ^ jArr[24]) ^ ((jArr[4] ^ jArr[9]) ^ (jArr[14] ^ jArr[19]));
            long j3 = (jArr[2] ^ jArr[7]) ^ (jArr[22] ^ (jArr[12] ^ jArr[17]));
            long j4 = (((j3 << 1) | (j3 >>> 63)) ^ jArr[20]) ^ ((jArr[0] ^ jArr[5]) ^ (jArr[10] ^ jArr[15]));
            long j5 = (jArr[3] ^ jArr[8]) ^ (jArr[23] ^ (jArr[13] ^ jArr[18]));
            long j6 = (((j5 << 1) | (j5 >>> 63)) ^ jArr[21]) ^ ((jArr[1] ^ jArr[6]) ^ (jArr[11] ^ jArr[16]));
            long j7 = (jArr[4] ^ jArr[9]) ^ (jArr[24] ^ (jArr[14] ^ jArr[19]));
            long j8 = (((j7 << 1) | (j7 >>> 63)) ^ jArr[22]) ^ ((jArr[2] ^ jArr[7]) ^ (jArr[12] ^ jArr[17]));
            long j9 = (jArr[0] ^ jArr[5]) ^ (jArr[20] ^ (jArr[10] ^ jArr[15]));
            long j10 = (((j9 << 1) | (j9 >>> 63)) ^ jArr[23]) ^ ((jArr[3] ^ jArr[8]) ^ (jArr[13] ^ jArr[18]));
            jArr[0] = jArr[0] ^ j2;
            jArr[5] = jArr[5] ^ j2;
            jArr[10] = jArr[10] ^ j2;
            jArr[15] = jArr[15] ^ j2;
            jArr[20] = jArr[20] ^ j2;
            jArr[1] = jArr[1] ^ j4;
            jArr[6] = jArr[6] ^ j4;
            jArr[11] = jArr[11] ^ j4;
            jArr[16] = jArr[16] ^ j4;
            jArr[21] = jArr[21] ^ j4;
            jArr[2] = jArr[2] ^ j6;
            jArr[7] = jArr[7] ^ j6;
            jArr[12] = jArr[12] ^ j6;
            jArr[17] = jArr[17] ^ j6;
            jArr[22] = jArr[22] ^ j6;
            jArr[3] = jArr[3] ^ j8;
            jArr[8] = jArr[8] ^ j8;
            jArr[13] = jArr[13] ^ j8;
            jArr[18] = jArr[18] ^ j8;
            jArr[23] = jArr[23] ^ j8;
            jArr[4] = jArr[4] ^ j10;
            jArr[9] = jArr[9] ^ j10;
            jArr[14] = jArr[14] ^ j10;
            jArr[19] = jArr[19] ^ j10;
            jArr[24] = jArr[24] ^ j10;
            jArr[5] = (jArr[5] << 36) | (jArr[5] >>> 28);
            jArr[10] = (jArr[10] << 3) | (jArr[10] >>> 61);
            jArr[15] = (jArr[15] << 41) | (jArr[15] >>> 23);
            jArr[20] = (jArr[20] << 18) | (jArr[20] >>> 46);
            jArr[1] = (jArr[1] << 1) | (jArr[1] >>> 63);
            jArr[6] = (jArr[6] << 44) | (jArr[6] >>> 20);
            jArr[11] = (jArr[11] << 10) | (jArr[11] >>> 54);
            jArr[16] = (jArr[16] << 45) | (jArr[16] >>> 19);
            jArr[21] = (jArr[21] << 2) | (jArr[21] >>> 62);
            jArr[2] = (jArr[2] << 62) | (jArr[2] >>> 2);
            jArr[7] = (jArr[7] << 6) | (jArr[7] >>> 58);
            jArr[12] = (jArr[12] << 43) | (jArr[12] >>> 21);
            jArr[17] = (jArr[17] << 15) | (jArr[17] >>> 49);
            jArr[22] = (jArr[22] << 61) | (jArr[22] >>> 3);
            jArr[3] = (jArr[3] << 28) | (jArr[3] >>> 36);
            jArr[8] = (jArr[8] << 55) | (jArr[8] >>> 9);
            jArr[13] = (jArr[13] << 25) | (jArr[13] >>> 39);
            jArr[18] = (jArr[18] << 21) | (jArr[18] >>> 43);
            jArr[23] = (jArr[23] << 56) | (jArr[23] >>> 8);
            jArr[4] = (jArr[4] << 27) | (jArr[4] >>> 37);
            jArr[9] = (jArr[9] << 20) | (jArr[9] >>> 44);
            jArr[14] = (jArr[14] << 39) | (jArr[14] >>> 25);
            jArr[19] = (jArr[19] << 8) | (jArr[19] >>> 56);
            jArr[24] = (jArr[24] << 14) | (jArr[24] >>> 50);
            long j11 = jArr[12] ^ (-1);
            long j12 = jArr[0] ^ (jArr[6] | jArr[12]);
            long j13 = jArr[6] ^ (j11 | jArr[18]);
            long j14 = jArr[12] ^ (jArr[18] & jArr[24]);
            long j15 = jArr[18] ^ (jArr[24] | jArr[0]);
            long j16 = jArr[24] ^ (jArr[0] & jArr[6]);
            jArr[0] = j12;
            jArr[6] = j13;
            jArr[12] = j14;
            jArr[18] = j15;
            jArr[24] = j16;
            long j17 = jArr[22] ^ (-1);
            long j18 = jArr[3] ^ (jArr[9] | jArr[10]);
            long j19 = jArr[9] ^ (jArr[10] & jArr[16]);
            long j20 = jArr[10] ^ (jArr[16] | j17);
            long j21 = jArr[16] ^ (jArr[22] | jArr[3]);
            long j22 = jArr[22] ^ (jArr[3] & jArr[9]);
            jArr[3] = j18;
            jArr[9] = j19;
            jArr[10] = j20;
            jArr[16] = j21;
            jArr[22] = j22;
            long j23 = jArr[19] ^ (-1);
            long j24 = jArr[1] ^ (jArr[7] | jArr[13]);
            long j25 = jArr[7] ^ (jArr[13] & jArr[19]);
            long j26 = jArr[13] ^ (j23 & jArr[20]);
            long j27 = j23 ^ (jArr[20] | jArr[1]);
            long j28 = jArr[20] ^ (jArr[1] & jArr[7]);
            jArr[1] = j24;
            jArr[7] = j25;
            jArr[13] = j26;
            jArr[19] = j27;
            jArr[20] = j28;
            long j29 = jArr[17] ^ (-1);
            long j30 = jArr[4] ^ (jArr[5] & jArr[11]);
            long j31 = jArr[5] ^ (jArr[11] | jArr[17]);
            long j32 = jArr[11] ^ (j29 | jArr[23]);
            long j33 = j29 ^ (jArr[23] & jArr[4]);
            long j34 = jArr[23] ^ (jArr[4] | jArr[5]);
            jArr[4] = j30;
            jArr[5] = j31;
            jArr[11] = j32;
            jArr[17] = j33;
            jArr[23] = j34;
            long j35 = jArr[8] ^ (-1);
            long j36 = jArr[2] ^ (j35 & jArr[14]);
            long j37 = j35 ^ (jArr[14] | jArr[15]);
            long j38 = jArr[14] ^ (jArr[15] & jArr[21]);
            long j39 = jArr[15] ^ (jArr[21] | jArr[2]);
            long j40 = jArr[21] ^ (jArr[2] & jArr[8]);
            jArr[2] = j36;
            jArr[8] = j37;
            jArr[14] = j38;
            jArr[15] = j39;
            jArr[21] = j40;
            jArr[0] = jArr[0] ^ this.RC[i + 0];
            long j41 = (jArr[6] ^ jArr[9]) ^ (jArr[8] ^ (jArr[7] ^ jArr[5]));
            long j42 = (((j41 << 1) | (j41 >>> 63)) ^ jArr[21]) ^ ((jArr[24] ^ jArr[22]) ^ (jArr[20] ^ jArr[23]));
            long j43 = (jArr[12] ^ jArr[10]) ^ (jArr[14] ^ (jArr[13] ^ jArr[11]));
            long j44 = (((j43 << 1) | (j43 >>> 63)) ^ jArr[2]) ^ ((jArr[0] ^ jArr[3]) ^ (jArr[1] ^ jArr[4]));
            long j45 = (jArr[18] ^ jArr[16]) ^ (jArr[15] ^ (jArr[19] ^ jArr[17]));
            long j46 = (((j45 << 1) | (j45 >>> 63)) ^ jArr[8]) ^ ((jArr[6] ^ jArr[9]) ^ (jArr[7] ^ jArr[5]));
            long j47 = (jArr[24] ^ jArr[22]) ^ (jArr[21] ^ (jArr[20] ^ jArr[23]));
            long j48 = (((j47 << 1) | (j47 >>> 63)) ^ jArr[14]) ^ ((jArr[12] ^ jArr[10]) ^ (jArr[13] ^ jArr[11]));
            long j49 = (jArr[0] ^ jArr[3]) ^ (jArr[2] ^ (jArr[1] ^ jArr[4]));
            long j50 = (((j49 << 1) | (j49 >>> 63)) ^ jArr[15]) ^ ((jArr[18] ^ jArr[16]) ^ (jArr[19] ^ jArr[17]));
            jArr[0] = jArr[0] ^ j42;
            jArr[3] = jArr[3] ^ j42;
            jArr[1] = jArr[1] ^ j42;
            jArr[4] = jArr[4] ^ j42;
            jArr[2] = jArr[2] ^ j42;
            jArr[6] = jArr[6] ^ j44;
            jArr[9] = jArr[9] ^ j44;
            jArr[7] = jArr[7] ^ j44;
            jArr[5] = jArr[5] ^ j44;
            jArr[8] = jArr[8] ^ j44;
            jArr[12] = jArr[12] ^ j46;
            jArr[10] = jArr[10] ^ j46;
            jArr[13] = jArr[13] ^ j46;
            jArr[11] = jArr[11] ^ j46;
            jArr[14] = jArr[14] ^ j46;
            jArr[18] = jArr[18] ^ j48;
            jArr[16] = jArr[16] ^ j48;
            jArr[19] = jArr[19] ^ j48;
            jArr[17] = jArr[17] ^ j48;
            jArr[15] = jArr[15] ^ j48;
            jArr[24] = jArr[24] ^ j50;
            jArr[22] = jArr[22] ^ j50;
            jArr[20] = jArr[20] ^ j50;
            jArr[23] = jArr[23] ^ j50;
            jArr[21] = jArr[21] ^ j50;
            jArr[3] = (jArr[3] << 36) | (jArr[3] >>> 28);
            jArr[1] = (jArr[1] << 3) | (jArr[1] >>> 61);
            jArr[4] = (jArr[4] << 41) | (jArr[4] >>> 23);
            jArr[2] = (jArr[2] << 18) | (jArr[2] >>> 46);
            jArr[6] = (jArr[6] << 1) | (jArr[6] >>> 63);
            jArr[9] = (jArr[9] << 44) | (jArr[9] >>> 20);
            jArr[7] = (jArr[7] << 10) | (jArr[7] >>> 54);
            jArr[5] = (jArr[5] << 45) | (jArr[5] >>> 19);
            jArr[8] = (jArr[8] << 2) | (jArr[8] >>> 62);
            jArr[12] = (jArr[12] << 62) | (jArr[12] >>> 2);
            jArr[10] = (jArr[10] << 6) | (jArr[10] >>> 58);
            jArr[13] = (jArr[13] << 43) | (jArr[13] >>> 21);
            jArr[11] = (jArr[11] << 15) | (jArr[11] >>> 49);
            jArr[14] = (jArr[14] << 61) | (jArr[14] >>> 3);
            jArr[18] = (jArr[18] << 28) | (jArr[18] >>> 36);
            jArr[16] = (jArr[16] << 55) | (jArr[16] >>> 9);
            jArr[19] = (jArr[19] << 25) | (jArr[19] >>> 39);
            jArr[17] = (jArr[17] << 21) | (jArr[17] >>> 43);
            jArr[15] = (jArr[15] << 56) | (jArr[15] >>> 8);
            jArr[24] = (jArr[24] << 27) | (jArr[24] >>> 37);
            jArr[22] = (jArr[22] << 20) | (jArr[22] >>> 44);
            jArr[20] = (jArr[20] << 39) | (jArr[20] >>> 25);
            jArr[23] = (jArr[23] << 8) | (jArr[23] >>> 56);
            jArr[21] = (jArr[21] << 14) | (jArr[21] >>> 50);
            long j51 = jArr[13] ^ (-1);
            long j52 = jArr[0] ^ (jArr[9] | jArr[13]);
            long j53 = jArr[9] ^ (j51 | jArr[17]);
            long j54 = jArr[13] ^ (jArr[17] & jArr[21]);
            long j55 = jArr[17] ^ (jArr[21] | jArr[0]);
            long j56 = jArr[21] ^ (jArr[0] & jArr[9]);
            jArr[0] = j52;
            jArr[9] = j53;
            jArr[13] = j54;
            jArr[17] = j55;
            jArr[21] = j56;
            long j57 = jArr[14] ^ (-1);
            long j58 = jArr[18] ^ (jArr[22] | jArr[1]);
            long j59 = jArr[22] ^ (jArr[1] & jArr[5]);
            long j60 = jArr[1] ^ (jArr[5] | j57);
            long j61 = jArr[5] ^ (jArr[14] | jArr[18]);
            long j62 = jArr[14] ^ (jArr[18] & jArr[22]);
            jArr[18] = j58;
            jArr[22] = j59;
            jArr[1] = j60;
            jArr[5] = j61;
            jArr[14] = j62;
            long j63 = jArr[23] ^ (-1);
            long j64 = jArr[6] ^ (jArr[10] | jArr[19]);
            long j65 = jArr[10] ^ (jArr[19] & jArr[23]);
            long j66 = jArr[19] ^ (j63 & jArr[2]);
            long j67 = j63 ^ (jArr[2] | jArr[6]);
            long j68 = jArr[2] ^ (jArr[6] & jArr[10]);
            jArr[6] = j64;
            jArr[10] = j65;
            jArr[19] = j66;
            jArr[23] = j67;
            jArr[2] = j68;
            long j69 = jArr[11] ^ (-1);
            long j70 = jArr[24] ^ (jArr[3] & jArr[7]);
            long j71 = jArr[3] ^ (jArr[7] | jArr[11]);
            long j72 = jArr[7] ^ (j69 | jArr[15]);
            long j73 = j69 ^ (jArr[15] & jArr[24]);
            long j74 = jArr[15] ^ (jArr[24] | jArr[3]);
            jArr[24] = j70;
            jArr[3] = j71;
            jArr[7] = j72;
            jArr[11] = j73;
            jArr[15] = j74;
            long j75 = jArr[16] ^ (-1);
            long j76 = jArr[12] ^ (j75 & jArr[20]);
            long j77 = j75 ^ (jArr[20] | jArr[4]);
            long j78 = jArr[20] ^ (jArr[4] & jArr[8]);
            long j79 = jArr[4] ^ (jArr[8] | jArr[12]);
            long j80 = jArr[8] ^ (jArr[12] & jArr[16]);
            jArr[12] = j76;
            jArr[16] = j77;
            jArr[20] = j78;
            jArr[4] = j79;
            jArr[8] = j80;
            jArr[0] = jArr[0] ^ this.RC[i + 1];
            long j81 = jArr[5];
            jArr[5] = jArr[18];
            jArr[18] = jArr[11];
            jArr[11] = jArr[10];
            jArr[10] = jArr[6];
            jArr[6] = jArr[22];
            jArr[22] = jArr[20];
            jArr[20] = jArr[12];
            jArr[12] = jArr[19];
            jArr[19] = jArr[15];
            jArr[15] = jArr[24];
            jArr[24] = jArr[8];
            jArr[8] = j81;
            long j82 = jArr[1];
            jArr[1] = jArr[9];
            jArr[9] = jArr[14];
            jArr[14] = jArr[2];
            jArr[2] = jArr[13];
            jArr[13] = jArr[23];
            jArr[23] = jArr[4];
            jArr[4] = jArr[21];
            jArr[21] = jArr[16];
            jArr[16] = jArr[3];
            jArr[3] = jArr[17];
            jArr[17] = jArr[7];
            jArr[7] = j82;
        }
        jArr[1] = jArr[1] ^ (-1);
        jArr[2] = jArr[2] ^ (-1);
        jArr[8] = jArr[8] ^ (-1);
        jArr[12] = jArr[12] ^ (-1);
        jArr[17] = jArr[17] ^ (-1);
        jArr[20] = jArr[20] ^ (-1);
    }

    void inner_shake256_init() {
        this.dptr = 0L;
        for (int i = 0; i < this.A.length; i++) {
            this.A[i] = 0;
        }
    }

    void inner_shake256_inject(byte[] bArr, int i, int i2) {
        long j = this.dptr;
        while (i2 > 0) {
            long j2 = 136 - j;
            if (j2 > i2) {
                j2 = i2;
            }
            long j3 = 0;
            while (true) {
                long j4 = j3;
                if (j4 >= j2) {
                    break;
                }
                long j5 = j4 + j;
                long[] jArr = this.A;
                int i3 = (int) (j5 >> 3);
                jArr[i3] = jArr[i3] ^ ((((long) bArr[i + ((int) j4)]) & 255) << ((int) ((j5 & 7) << 3)));
                j3 = j4 + 1;
            }
            j += j2;
            i = (int) (((long) i) + j2);
            i2 = (int) (((long) i2) - j2);
            if (j == 136) {
                process_block(this.A);
                j = 0;
            }
        }
        this.dptr = j;
    }

    void i_shake256_flip() {
        int i = (int) this.dptr;
        long[] jArr = this.A;
        int i2 = i >> 3;
        jArr[i2] = jArr[i2] ^ (31 << ((i & 7) << 3));
        long[] jArr2 = this.A;
        jArr2[16] = jArr2[16] ^ Long.MIN_VALUE;
        this.dptr = 136L;
    }

    void inner_shake256_extract(byte[] bArr, int i, int i2) {
        int i3 = i;
        int i4 = (int) this.dptr;
        while (i2 > 0) {
            if (i4 == 136) {
                process_block(this.A);
                i4 = 0;
            }
            int i5 = 136 - i4;
            if (i5 > i2) {
                i5 = i2;
            }
            i2 -= i5;
            while (true) {
                int i6 = i5;
                i5--;
                if (i6 > 0) {
                    int i7 = i3;
                    i3++;
                    bArr[i7] = (byte) (this.A[i4 >> 3] >>> ((i4 & 7) << 3));
                    i4++;
                }
            }
        }
        this.dptr = i4;
    }
}
