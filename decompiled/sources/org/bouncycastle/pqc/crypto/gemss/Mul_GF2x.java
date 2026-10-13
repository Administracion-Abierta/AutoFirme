package org.bouncycastle.pqc.crypto.gemss;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/Mul_GF2x.class */
abstract class Mul_GF2x {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/Mul_GF2x$Mul12.class */
    public static class Mul12 extends Mul_GF2x {
        private long[] Buffer = new long[12];

        @Override // org.bouncycastle.pqc.crypto.gemss.Mul_GF2x
        public void mul_gf2x(Pointer pointer, Pointer pointer2, Pointer pointer3) {
            Mul_GF2x.mul384_no_simd_gf2x(pointer.array, pointer2.array, pointer2.cp, pointer3.array, pointer3.cp, this.Buffer);
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Mul_GF2x
        public void sqr_gf2x(long[] jArr, long[] jArr2, int i) {
            Mul_GF2x.SQR128_NO_SIMD_GF2X(jArr, 8, jArr2, i + 4);
            Mul_GF2x.SQR256_NO_SIMD_GF2X(jArr, 0, jArr2, i);
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Mul_GF2x
        public void mul_gf2x_xor(Pointer pointer, Pointer pointer2, Pointer pointer3) {
            Mul_GF2x.mul384_no_simd_gf2x_xor(pointer.array, pointer2.array, pointer2.cp, pointer3.array, pointer3.cp, this.Buffer);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/Mul_GF2x$Mul13.class */
    public static class Mul13 extends Mul_GF2x {
        private long[] Buffer = new long[13];
        private long[] Buffer2 = new long[4];

        @Override // org.bouncycastle.pqc.crypto.gemss.Mul_GF2x
        public void mul_gf2x(Pointer pointer, Pointer pointer2, Pointer pointer3) {
            Mul_GF2x.mul416_no_simd_gf2x(pointer.array, pointer2.array, pointer2.cp, pointer3.array, pointer3.cp, this.Buffer);
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Mul_GF2x
        public void sqr_gf2x(long[] jArr, long[] jArr2, int i) {
            jArr[12] = Mul_GF2x.SQR32_NO_SIMD_GF2X(jArr2[i + 6]);
            Mul_GF2x.SQR128_NO_SIMD_GF2X(jArr, 8, jArr2, i + 4);
            Mul_GF2x.SQR256_NO_SIMD_GF2X(jArr, 0, jArr2, i);
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Mul_GF2x
        public void mul_gf2x_xor(Pointer pointer, Pointer pointer2, Pointer pointer3) {
            Mul_GF2x.mul416_no_simd_gf2x_xor(pointer.array, pointer2.array, pointer2.cp, pointer3.array, pointer3.cp, this.Buffer, this.Buffer2);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/Mul_GF2x$Mul17.class */
    public static class Mul17 extends Mul_GF2x {
        private long[] AA = new long[5];
        private long[] BB = new long[5];
        private long[] Buffer1 = new long[17];
        private long[] Buffer2 = new long[4];

        @Override // org.bouncycastle.pqc.crypto.gemss.Mul_GF2x
        public void mul_gf2x(Pointer pointer, Pointer pointer2, Pointer pointer3) {
            Mul_GF2x.mul544_no_simd_gf2x(pointer.array, pointer2.array, pointer2.cp, pointer3.array, pointer3.cp, this.AA, this.BB, this.Buffer1);
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Mul_GF2x
        public void sqr_gf2x(long[] jArr, long[] jArr2, int i) {
            jArr[16] = Mul_GF2x.SQR32_NO_SIMD_GF2X(jArr2[i + 8]);
            Mul_GF2x.SQR256_NO_SIMD_GF2X(jArr, 8, jArr2, i + 4);
            Mul_GF2x.SQR256_NO_SIMD_GF2X(jArr, 0, jArr2, i);
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Mul_GF2x
        public void mul_gf2x_xor(Pointer pointer, Pointer pointer2, Pointer pointer3) {
            Mul_GF2x.mul544_no_simd_gf2x_xor(pointer.array, pointer2.array, pointer2.cp, pointer3.array, pointer3.cp, this.AA, this.BB, this.Buffer1, this.Buffer2);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/Mul_GF2x$Mul6.class */
    public static class Mul6 extends Mul_GF2x {
        private long[] Buffer = new long[6];

        @Override // org.bouncycastle.pqc.crypto.gemss.Mul_GF2x
        public void mul_gf2x(Pointer pointer, Pointer pointer2, Pointer pointer3) {
            mul192_no_simd_gf2x(pointer.array, 0, pointer2.array, pointer2.cp, pointer3.array, pointer3.cp);
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Mul_GF2x
        public void sqr_gf2x(long[] jArr, long[] jArr2, int i) {
            Mul_GF2x.SQR64_NO_SIMD_GF2X(jArr, 4, jArr2[i + 2]);
            Mul_GF2x.SQR128_NO_SIMD_GF2X(jArr, 0, jArr2, i);
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Mul_GF2x
        public void mul_gf2x_xor(Pointer pointer, Pointer pointer2, Pointer pointer3) {
            mul192_no_simd_gf2x_xor(pointer.array, pointer.cp, pointer2.array, pointer2.cp, pointer3.array, pointer3.cp, this.Buffer);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/Mul_GF2x$Mul9.class */
    public static class Mul9 extends Mul_GF2x {
        private long[] Buffer = new long[9];

        @Override // org.bouncycastle.pqc.crypto.gemss.Mul_GF2x
        public void mul_gf2x(Pointer pointer, Pointer pointer2, Pointer pointer3) {
            Mul_GF2x.mul288_no_simd_gf2x(pointer.array, 0, pointer2.array, pointer2.cp, pointer3.array, pointer3.cp, this.Buffer);
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Mul_GF2x
        public void sqr_gf2x(long[] jArr, long[] jArr2, int i) {
            jArr[8] = Mul_GF2x.SQR32_NO_SIMD_GF2X(jArr2[i + 4]);
            Mul_GF2x.SQR256_NO_SIMD_GF2X(jArr, 0, jArr2, i);
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Mul_GF2x
        public void mul_gf2x_xor(Pointer pointer, Pointer pointer2, Pointer pointer3) {
            Mul_GF2x.mul288_no_simd_gf2x_xor(pointer.array, pointer.cp, pointer2.array, pointer2.cp, pointer3.array, pointer3.cp, this.Buffer);
        }
    }

    Mul_GF2x() {
    }

    public abstract void mul_gf2x(Pointer pointer, Pointer pointer2, Pointer pointer3);

    public abstract void sqr_gf2x(long[] jArr, long[] jArr2, int i);

    public abstract void mul_gf2x_xor(Pointer pointer, Pointer pointer2, Pointer pointer3);

    /* JADX INFO: Access modifiers changed from: private */
    public static long SQR32_NO_SIMD_GF2X(long j) {
        long j2 = (j ^ (j << 16)) & 281470681808895L;
        long j3 = (j2 ^ (j2 << 8)) & 71777214294589695L;
        long j4 = (j3 ^ (j3 << 4)) & 1085102592571150095L;
        long j5 = (j4 ^ (j4 << 2)) & 3689348814741910323L;
        return (j5 ^ (j5 << 1)) & 6148914691236517205L;
    }

    private static long SQR64LOW_NO_SIMD_GF2X(long j) {
        long j2 = ((j & 4294967295L) ^ (j << 16)) & 281470681808895L;
        long j3 = (j2 ^ (j2 << 8)) & 71777214294589695L;
        long j4 = (j3 ^ (j3 << 4)) & 1085102592571150095L;
        long j5 = (j4 ^ (j4 << 2)) & 3689348814741910323L;
        return (j5 ^ (j5 << 1)) & 6148914691236517205L;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void SQR64_NO_SIMD_GF2X(long[] jArr, int i, long j) {
        jArr[i + 1] = SQR32_NO_SIMD_GF2X(j >>> 32);
        jArr[i] = SQR64LOW_NO_SIMD_GF2X(j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void SQR128_NO_SIMD_GF2X(long[] jArr, int i, long[] jArr2, int i2) {
        SQR64_NO_SIMD_GF2X(jArr, i + 2, jArr2[i2 + 1]);
        SQR64_NO_SIMD_GF2X(jArr, i, jArr2[i2]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void SQR256_NO_SIMD_GF2X(long[] jArr, int i, long[] jArr2, int i2) {
        SQR128_NO_SIMD_GF2X(jArr, i + 4, jArr2, i2 + 2);
        SQR128_NO_SIMD_GF2X(jArr, i, jArr2, i2);
    }

    private static long MUL32_NO_SIMD_GF2X(long j, long j2) {
        return ((((((((((((((((((((((((((((((((-(j2 & 1)) & j) ^ (((-((j2 >>> 1) & 1)) & j) << 1)) ^ (((-((j2 >>> 2) & 1)) & j) << 2)) ^ (((-((j2 >>> 3) & 1)) & j) << 3)) ^ (((-((j2 >>> 4) & 1)) & j) << 4)) ^ (((-((j2 >>> 5) & 1)) & j) << 5)) ^ (((-((j2 >>> 6) & 1)) & j) << 6)) ^ (((-((j2 >>> 7) & 1)) & j) << 7)) ^ (((-((j2 >>> 8) & 1)) & j) << 8)) ^ (((-((j2 >>> 9) & 1)) & j) << 9)) ^ (((-((j2 >>> 10) & 1)) & j) << 10)) ^ (((-((j2 >>> 11) & 1)) & j) << 11)) ^ (((-((j2 >>> 12) & 1)) & j) << 12)) ^ (((-((j2 >>> 13) & 1)) & j) << 13)) ^ (((-((j2 >>> 14) & 1)) & j) << 14)) ^ (((-((j2 >>> 15) & 1)) & j) << 15)) ^ (((-((j2 >>> 16) & 1)) & j) << 16)) ^ (((-((j2 >>> 17) & 1)) & j) << 17)) ^ (((-((j2 >>> 18) & 1)) & j) << 18)) ^ (((-((j2 >>> 19) & 1)) & j) << 19)) ^ (((-((j2 >>> 20) & 1)) & j) << 20)) ^ (((-((j2 >>> 21) & 1)) & j) << 21)) ^ (((-((j2 >>> 22) & 1)) & j) << 22)) ^ (((-((j2 >>> 23) & 1)) & j) << 23)) ^ (((-((j2 >>> 24) & 1)) & j) << 24)) ^ (((-((j2 >>> 25) & 1)) & j) << 25)) ^ (((-((j2 >>> 26) & 1)) & j) << 26)) ^ (((-((j2 >>> 27) & 1)) & j) << 27)) ^ (((-((j2 >>> 28) & 1)) & j) << 28)) ^ (((-((j2 >>> 29) & 1)) & j) << 29)) ^ (((-((j2 >>> 30) & 1)) & j) << 30)) ^ (((-((j2 >>> 31) & 1)) & j) << 31);
    }

    private static void MUL64_NO_SIMD_GF2X(long[] jArr, int i, long j, long j2) {
        long j3 = (-(j2 >>> 63)) & j;
        long j4 = ((-(j2 & 1)) & j) ^ (j3 << 63);
        long j5 = j3 >>> 1;
        long j6 = (-((j2 >>> 1) & 1)) & j;
        long j7 = j4 ^ (j6 << 1);
        long j8 = j5 ^ (j6 >>> 63);
        long j9 = (-((j2 >>> 2) & 1)) & j;
        long j10 = j7 ^ (j9 << 2);
        long j11 = j8 ^ (j9 >>> 62);
        long j12 = (-((j2 >>> 3) & 1)) & j;
        long j13 = j10 ^ (j12 << 3);
        long j14 = j11 ^ (j12 >>> 61);
        long j15 = (-((j2 >>> 4) & 1)) & j;
        long j16 = j13 ^ (j15 << 4);
        long j17 = j14 ^ (j15 >>> 60);
        long j18 = (-((j2 >>> 5) & 1)) & j;
        long j19 = j16 ^ (j18 << 5);
        long j20 = j17 ^ (j18 >>> 59);
        long j21 = (-((j2 >>> 6) & 1)) & j;
        long j22 = j19 ^ (j21 << 6);
        long j23 = j20 ^ (j21 >>> 58);
        long j24 = (-((j2 >>> 7) & 1)) & j;
        long j25 = j22 ^ (j24 << 7);
        long j26 = j23 ^ (j24 >>> 57);
        long j27 = (-((j2 >>> 8) & 1)) & j;
        long j28 = j25 ^ (j27 << 8);
        long j29 = j26 ^ (j27 >>> 56);
        long j30 = (-((j2 >>> 9) & 1)) & j;
        long j31 = j28 ^ (j30 << 9);
        long j32 = j29 ^ (j30 >>> 55);
        long j33 = (-((j2 >>> 10) & 1)) & j;
        long j34 = j31 ^ (j33 << 10);
        long j35 = j32 ^ (j33 >>> 54);
        long j36 = (-((j2 >>> 11) & 1)) & j;
        long j37 = j34 ^ (j36 << 11);
        long j38 = j35 ^ (j36 >>> 53);
        long j39 = (-((j2 >>> 12) & 1)) & j;
        long j40 = j37 ^ (j39 << 12);
        long j41 = j38 ^ (j39 >>> 52);
        long j42 = (-((j2 >>> 13) & 1)) & j;
        long j43 = j40 ^ (j42 << 13);
        long j44 = j41 ^ (j42 >>> 51);
        long j45 = (-((j2 >>> 14) & 1)) & j;
        long j46 = j43 ^ (j45 << 14);
        long j47 = j44 ^ (j45 >>> 50);
        long j48 = (-((j2 >>> 15) & 1)) & j;
        long j49 = j46 ^ (j48 << 15);
        long j50 = j47 ^ (j48 >>> 49);
        long j51 = (-((j2 >>> 16) & 1)) & j;
        long j52 = j49 ^ (j51 << 16);
        long j53 = j50 ^ (j51 >>> 48);
        long j54 = (-((j2 >>> 17) & 1)) & j;
        long j55 = j52 ^ (j54 << 17);
        long j56 = j53 ^ (j54 >>> 47);
        long j57 = (-((j2 >>> 18) & 1)) & j;
        long j58 = j55 ^ (j57 << 18);
        long j59 = j56 ^ (j57 >>> 46);
        long j60 = (-((j2 >>> 19) & 1)) & j;
        long j61 = j58 ^ (j60 << 19);
        long j62 = j59 ^ (j60 >>> 45);
        long j63 = (-((j2 >>> 20) & 1)) & j;
        long j64 = j61 ^ (j63 << 20);
        long j65 = j62 ^ (j63 >>> 44);
        long j66 = (-((j2 >>> 21) & 1)) & j;
        long j67 = j64 ^ (j66 << 21);
        long j68 = j65 ^ (j66 >>> 43);
        long j69 = (-((j2 >>> 22) & 1)) & j;
        long j70 = j67 ^ (j69 << 22);
        long j71 = j68 ^ (j69 >>> 42);
        long j72 = (-((j2 >>> 23) & 1)) & j;
        long j73 = j70 ^ (j72 << 23);
        long j74 = j71 ^ (j72 >>> 41);
        long j75 = (-((j2 >>> 24) & 1)) & j;
        long j76 = j73 ^ (j75 << 24);
        long j77 = j74 ^ (j75 >>> 40);
        long j78 = (-((j2 >>> 25) & 1)) & j;
        long j79 = j76 ^ (j78 << 25);
        long j80 = j77 ^ (j78 >>> 39);
        long j81 = (-((j2 >>> 26) & 1)) & j;
        long j82 = j79 ^ (j81 << 26);
        long j83 = j80 ^ (j81 >>> 38);
        long j84 = (-((j2 >>> 27) & 1)) & j;
        long j85 = j82 ^ (j84 << 27);
        long j86 = j83 ^ (j84 >>> 37);
        long j87 = (-((j2 >>> 28) & 1)) & j;
        long j88 = j85 ^ (j87 << 28);
        long j89 = j86 ^ (j87 >>> 36);
        long j90 = (-((j2 >>> 29) & 1)) & j;
        long j91 = j88 ^ (j90 << 29);
        long j92 = j89 ^ (j90 >>> 35);
        long j93 = (-((j2 >>> 30) & 1)) & j;
        long j94 = j91 ^ (j93 << 30);
        long j95 = j92 ^ (j93 >>> 34);
        long j96 = (-((j2 >>> 31) & 1)) & j;
        long j97 = j94 ^ (j96 << 31);
        long j98 = j95 ^ (j96 >>> 33);
        long j99 = (-((j2 >>> 32) & 1)) & j;
        long j100 = j97 ^ (j99 << 32);
        long j101 = j98 ^ (j99 >>> 32);
        long j102 = (-((j2 >>> 33) & 1)) & j;
        long j103 = j100 ^ (j102 << 33);
        long j104 = j101 ^ (j102 >>> 31);
        long j105 = (-((j2 >>> 34) & 1)) & j;
        long j106 = j103 ^ (j105 << 34);
        long j107 = j104 ^ (j105 >>> 30);
        long j108 = (-((j2 >>> 35) & 1)) & j;
        long j109 = j106 ^ (j108 << 35);
        long j110 = j107 ^ (j108 >>> 29);
        long j111 = (-((j2 >>> 36) & 1)) & j;
        long j112 = j109 ^ (j111 << 36);
        long j113 = j110 ^ (j111 >>> 28);
        long j114 = (-((j2 >>> 37) & 1)) & j;
        long j115 = j112 ^ (j114 << 37);
        long j116 = j113 ^ (j114 >>> 27);
        long j117 = (-((j2 >>> 38) & 1)) & j;
        long j118 = j115 ^ (j117 << 38);
        long j119 = j116 ^ (j117 >>> 26);
        long j120 = (-((j2 >>> 39) & 1)) & j;
        long j121 = j118 ^ (j120 << 39);
        long j122 = j119 ^ (j120 >>> 25);
        long j123 = (-((j2 >>> 40) & 1)) & j;
        long j124 = j121 ^ (j123 << 40);
        long j125 = j122 ^ (j123 >>> 24);
        long j126 = (-((j2 >>> 41) & 1)) & j;
        long j127 = j124 ^ (j126 << 41);
        long j128 = j125 ^ (j126 >>> 23);
        long j129 = (-((j2 >>> 42) & 1)) & j;
        long j130 = j127 ^ (j129 << 42);
        long j131 = j128 ^ (j129 >>> 22);
        long j132 = (-((j2 >>> 43) & 1)) & j;
        long j133 = j130 ^ (j132 << 43);
        long j134 = j131 ^ (j132 >>> 21);
        long j135 = (-((j2 >>> 44) & 1)) & j;
        long j136 = j133 ^ (j135 << 44);
        long j137 = j134 ^ (j135 >>> 20);
        long j138 = (-((j2 >>> 45) & 1)) & j;
        long j139 = j136 ^ (j138 << 45);
        long j140 = j137 ^ (j138 >>> 19);
        long j141 = (-((j2 >>> 46) & 1)) & j;
        long j142 = j139 ^ (j141 << 46);
        long j143 = j140 ^ (j141 >>> 18);
        long j144 = (-((j2 >>> 47) & 1)) & j;
        long j145 = j142 ^ (j144 << 47);
        long j146 = j143 ^ (j144 >>> 17);
        long j147 = (-((j2 >>> 48) & 1)) & j;
        long j148 = j145 ^ (j147 << 48);
        long j149 = j146 ^ (j147 >>> 16);
        long j150 = (-((j2 >>> 49) & 1)) & j;
        long j151 = j148 ^ (j150 << 49);
        long j152 = j149 ^ (j150 >>> 15);
        long j153 = (-((j2 >>> 50) & 1)) & j;
        long j154 = j151 ^ (j153 << 50);
        long j155 = j152 ^ (j153 >>> 14);
        long j156 = (-((j2 >>> 51) & 1)) & j;
        long j157 = j154 ^ (j156 << 51);
        long j158 = j155 ^ (j156 >>> 13);
        long j159 = (-((j2 >>> 52) & 1)) & j;
        long j160 = j157 ^ (j159 << 52);
        long j161 = j158 ^ (j159 >>> 12);
        long j162 = (-((j2 >>> 53) & 1)) & j;
        long j163 = j160 ^ (j162 << 53);
        long j164 = j161 ^ (j162 >>> 11);
        long j165 = (-((j2 >>> 54) & 1)) & j;
        long j166 = j163 ^ (j165 << 54);
        long j167 = j164 ^ (j165 >>> 10);
        long j168 = (-((j2 >>> 55) & 1)) & j;
        long j169 = j166 ^ (j168 << 55);
        long j170 = j167 ^ (j168 >>> 9);
        long j171 = (-((j2 >>> 56) & 1)) & j;
        long j172 = j169 ^ (j171 << 56);
        long j173 = j170 ^ (j171 >>> 8);
        long j174 = (-((j2 >>> 57) & 1)) & j;
        long j175 = j172 ^ (j174 << 57);
        long j176 = j173 ^ (j174 >>> 7);
        long j177 = (-((j2 >>> 58) & 1)) & j;
        long j178 = j175 ^ (j177 << 58);
        long j179 = j176 ^ (j177 >>> 6);
        long j180 = (-((j2 >>> 59) & 1)) & j;
        long j181 = j178 ^ (j180 << 59);
        long j182 = j179 ^ (j180 >>> 5);
        long j183 = (-((j2 >>> 60) & 1)) & j;
        long j184 = j181 ^ (j183 << 60);
        long j185 = j182 ^ (j183 >>> 4);
        long j186 = (-((j2 >>> 61) & 1)) & j;
        long j187 = j184 ^ (j186 << 61);
        long j188 = j185 ^ (j186 >>> 3);
        long j189 = (-((j2 >>> 62) & 1)) & j;
        jArr[i] = j187 ^ (j189 << 62);
        jArr[i + 1] = j188 ^ (j189 >>> 2);
    }

    private static void MUL64_NO_SIMD_GF2X_XOR(long[] jArr, int i, long j, long j2) {
        long j3 = (-(j2 >>> 63)) & j;
        long j4 = ((-(j2 & 1)) & j) ^ (j3 << 63);
        long j5 = j3 >>> 1;
        long j6 = (-((j2 >>> 1) & 1)) & j;
        long j7 = j4 ^ (j6 << 1);
        long j8 = j5 ^ (j6 >>> 63);
        long j9 = (-((j2 >>> 2) & 1)) & j;
        long j10 = j7 ^ (j9 << 2);
        long j11 = j8 ^ (j9 >>> 62);
        long j12 = (-((j2 >>> 3) & 1)) & j;
        long j13 = j10 ^ (j12 << 3);
        long j14 = j11 ^ (j12 >>> 61);
        long j15 = (-((j2 >>> 4) & 1)) & j;
        long j16 = j13 ^ (j15 << 4);
        long j17 = j14 ^ (j15 >>> 60);
        long j18 = (-((j2 >>> 5) & 1)) & j;
        long j19 = j16 ^ (j18 << 5);
        long j20 = j17 ^ (j18 >>> 59);
        long j21 = (-((j2 >>> 6) & 1)) & j;
        long j22 = j19 ^ (j21 << 6);
        long j23 = j20 ^ (j21 >>> 58);
        long j24 = (-((j2 >>> 7) & 1)) & j;
        long j25 = j22 ^ (j24 << 7);
        long j26 = j23 ^ (j24 >>> 57);
        long j27 = (-((j2 >>> 8) & 1)) & j;
        long j28 = j25 ^ (j27 << 8);
        long j29 = j26 ^ (j27 >>> 56);
        long j30 = (-((j2 >>> 9) & 1)) & j;
        long j31 = j28 ^ (j30 << 9);
        long j32 = j29 ^ (j30 >>> 55);
        long j33 = (-((j2 >>> 10) & 1)) & j;
        long j34 = j31 ^ (j33 << 10);
        long j35 = j32 ^ (j33 >>> 54);
        long j36 = (-((j2 >>> 11) & 1)) & j;
        long j37 = j34 ^ (j36 << 11);
        long j38 = j35 ^ (j36 >>> 53);
        long j39 = (-((j2 >>> 12) & 1)) & j;
        long j40 = j37 ^ (j39 << 12);
        long j41 = j38 ^ (j39 >>> 52);
        long j42 = (-((j2 >>> 13) & 1)) & j;
        long j43 = j40 ^ (j42 << 13);
        long j44 = j41 ^ (j42 >>> 51);
        long j45 = (-((j2 >>> 14) & 1)) & j;
        long j46 = j43 ^ (j45 << 14);
        long j47 = j44 ^ (j45 >>> 50);
        long j48 = (-((j2 >>> 15) & 1)) & j;
        long j49 = j46 ^ (j48 << 15);
        long j50 = j47 ^ (j48 >>> 49);
        long j51 = (-((j2 >>> 16) & 1)) & j;
        long j52 = j49 ^ (j51 << 16);
        long j53 = j50 ^ (j51 >>> 48);
        long j54 = (-((j2 >>> 17) & 1)) & j;
        long j55 = j52 ^ (j54 << 17);
        long j56 = j53 ^ (j54 >>> 47);
        long j57 = (-((j2 >>> 18) & 1)) & j;
        long j58 = j55 ^ (j57 << 18);
        long j59 = j56 ^ (j57 >>> 46);
        long j60 = (-((j2 >>> 19) & 1)) & j;
        long j61 = j58 ^ (j60 << 19);
        long j62 = j59 ^ (j60 >>> 45);
        long j63 = (-((j2 >>> 20) & 1)) & j;
        long j64 = j61 ^ (j63 << 20);
        long j65 = j62 ^ (j63 >>> 44);
        long j66 = (-((j2 >>> 21) & 1)) & j;
        long j67 = j64 ^ (j66 << 21);
        long j68 = j65 ^ (j66 >>> 43);
        long j69 = (-((j2 >>> 22) & 1)) & j;
        long j70 = j67 ^ (j69 << 22);
        long j71 = j68 ^ (j69 >>> 42);
        long j72 = (-((j2 >>> 23) & 1)) & j;
        long j73 = j70 ^ (j72 << 23);
        long j74 = j71 ^ (j72 >>> 41);
        long j75 = (-((j2 >>> 24) & 1)) & j;
        long j76 = j73 ^ (j75 << 24);
        long j77 = j74 ^ (j75 >>> 40);
        long j78 = (-((j2 >>> 25) & 1)) & j;
        long j79 = j76 ^ (j78 << 25);
        long j80 = j77 ^ (j78 >>> 39);
        long j81 = (-((j2 >>> 26) & 1)) & j;
        long j82 = j79 ^ (j81 << 26);
        long j83 = j80 ^ (j81 >>> 38);
        long j84 = (-((j2 >>> 27) & 1)) & j;
        long j85 = j82 ^ (j84 << 27);
        long j86 = j83 ^ (j84 >>> 37);
        long j87 = (-((j2 >>> 28) & 1)) & j;
        long j88 = j85 ^ (j87 << 28);
        long j89 = j86 ^ (j87 >>> 36);
        long j90 = (-((j2 >>> 29) & 1)) & j;
        long j91 = j88 ^ (j90 << 29);
        long j92 = j89 ^ (j90 >>> 35);
        long j93 = (-((j2 >>> 30) & 1)) & j;
        long j94 = j91 ^ (j93 << 30);
        long j95 = j92 ^ (j93 >>> 34);
        long j96 = (-((j2 >>> 31) & 1)) & j;
        long j97 = j94 ^ (j96 << 31);
        long j98 = j95 ^ (j96 >>> 33);
        long j99 = (-((j2 >>> 32) & 1)) & j;
        long j100 = j97 ^ (j99 << 32);
        long j101 = j98 ^ (j99 >>> 32);
        long j102 = (-((j2 >>> 33) & 1)) & j;
        long j103 = j100 ^ (j102 << 33);
        long j104 = j101 ^ (j102 >>> 31);
        long j105 = (-((j2 >>> 34) & 1)) & j;
        long j106 = j103 ^ (j105 << 34);
        long j107 = j104 ^ (j105 >>> 30);
        long j108 = (-((j2 >>> 35) & 1)) & j;
        long j109 = j106 ^ (j108 << 35);
        long j110 = j107 ^ (j108 >>> 29);
        long j111 = (-((j2 >>> 36) & 1)) & j;
        long j112 = j109 ^ (j111 << 36);
        long j113 = j110 ^ (j111 >>> 28);
        long j114 = (-((j2 >>> 37) & 1)) & j;
        long j115 = j112 ^ (j114 << 37);
        long j116 = j113 ^ (j114 >>> 27);
        long j117 = (-((j2 >>> 38) & 1)) & j;
        long j118 = j115 ^ (j117 << 38);
        long j119 = j116 ^ (j117 >>> 26);
        long j120 = (-((j2 >>> 39) & 1)) & j;
        long j121 = j118 ^ (j120 << 39);
        long j122 = j119 ^ (j120 >>> 25);
        long j123 = (-((j2 >>> 40) & 1)) & j;
        long j124 = j121 ^ (j123 << 40);
        long j125 = j122 ^ (j123 >>> 24);
        long j126 = (-((j2 >>> 41) & 1)) & j;
        long j127 = j124 ^ (j126 << 41);
        long j128 = j125 ^ (j126 >>> 23);
        long j129 = (-((j2 >>> 42) & 1)) & j;
        long j130 = j127 ^ (j129 << 42);
        long j131 = j128 ^ (j129 >>> 22);
        long j132 = (-((j2 >>> 43) & 1)) & j;
        long j133 = j130 ^ (j132 << 43);
        long j134 = j131 ^ (j132 >>> 21);
        long j135 = (-((j2 >>> 44) & 1)) & j;
        long j136 = j133 ^ (j135 << 44);
        long j137 = j134 ^ (j135 >>> 20);
        long j138 = (-((j2 >>> 45) & 1)) & j;
        long j139 = j136 ^ (j138 << 45);
        long j140 = j137 ^ (j138 >>> 19);
        long j141 = (-((j2 >>> 46) & 1)) & j;
        long j142 = j139 ^ (j141 << 46);
        long j143 = j140 ^ (j141 >>> 18);
        long j144 = (-((j2 >>> 47) & 1)) & j;
        long j145 = j142 ^ (j144 << 47);
        long j146 = j143 ^ (j144 >>> 17);
        long j147 = (-((j2 >>> 48) & 1)) & j;
        long j148 = j145 ^ (j147 << 48);
        long j149 = j146 ^ (j147 >>> 16);
        long j150 = (-((j2 >>> 49) & 1)) & j;
        long j151 = j148 ^ (j150 << 49);
        long j152 = j149 ^ (j150 >>> 15);
        long j153 = (-((j2 >>> 50) & 1)) & j;
        long j154 = j151 ^ (j153 << 50);
        long j155 = j152 ^ (j153 >>> 14);
        long j156 = (-((j2 >>> 51) & 1)) & j;
        long j157 = j154 ^ (j156 << 51);
        long j158 = j155 ^ (j156 >>> 13);
        long j159 = (-((j2 >>> 52) & 1)) & j;
        long j160 = j157 ^ (j159 << 52);
        long j161 = j158 ^ (j159 >>> 12);
        long j162 = (-((j2 >>> 53) & 1)) & j;
        long j163 = j160 ^ (j162 << 53);
        long j164 = j161 ^ (j162 >>> 11);
        long j165 = (-((j2 >>> 54) & 1)) & j;
        long j166 = j163 ^ (j165 << 54);
        long j167 = j164 ^ (j165 >>> 10);
        long j168 = (-((j2 >>> 55) & 1)) & j;
        long j169 = j166 ^ (j168 << 55);
        long j170 = j167 ^ (j168 >>> 9);
        long j171 = (-((j2 >>> 56) & 1)) & j;
        long j172 = j169 ^ (j171 << 56);
        long j173 = j170 ^ (j171 >>> 8);
        long j174 = (-((j2 >>> 57) & 1)) & j;
        long j175 = j172 ^ (j174 << 57);
        long j176 = j173 ^ (j174 >>> 7);
        long j177 = (-((j2 >>> 58) & 1)) & j;
        long j178 = j175 ^ (j177 << 58);
        long j179 = j176 ^ (j177 >>> 6);
        long j180 = (-((j2 >>> 59) & 1)) & j;
        long j181 = j178 ^ (j180 << 59);
        long j182 = j179 ^ (j180 >>> 5);
        long j183 = (-((j2 >>> 60) & 1)) & j;
        long j184 = j181 ^ (j183 << 60);
        long j185 = j182 ^ (j183 >>> 4);
        long j186 = (-((j2 >>> 61) & 1)) & j;
        long j187 = j184 ^ (j186 << 61);
        long j188 = j185 ^ (j186 >>> 3);
        long j189 = (-((j2 >>> 62) & 1)) & j;
        jArr[i] = jArr[i] ^ (j187 ^ (j189 << 62));
        int i2 = i + 1;
        jArr[i2] = jArr[i2] ^ (j188 ^ (j189 >>> 2));
    }

    private static void mul128_no_simd_gf2x(long[] jArr, int i, long[] jArr2, int i2, long[] jArr3, int i3) {
        MUL64_NO_SIMD_GF2X(jArr, i, jArr2[i2], jArr3[i3]);
        MUL64_NO_SIMD_GF2X(jArr, i + 2, jArr2[i2 + 1], jArr3[i3 + 1]);
        int i4 = i + 2;
        jArr[i4] = jArr[i4] ^ jArr[i + 1];
        jArr[i + 1] = jArr[i] ^ jArr[i + 2];
        int i5 = i + 2;
        jArr[i5] = jArr[i5] ^ jArr[i + 3];
        MUL64_NO_SIMD_GF2X_XOR(jArr, i + 1, jArr2[i2] ^ jArr2[i2 + 1], jArr3[i3] ^ jArr3[i3 + 1]);
    }

    private static void mul128_no_simd_gf2x(long[] jArr, int i, long j, long j2, long j3, long j4) {
        MUL64_NO_SIMD_GF2X(jArr, i, j, j3);
        MUL64_NO_SIMD_GF2X(jArr, i + 2, j2, j4);
        int i2 = i + 2;
        jArr[i2] = jArr[i2] ^ jArr[i + 1];
        jArr[i + 1] = jArr[i] ^ jArr[i + 2];
        int i3 = i + 2;
        jArr[i3] = jArr[i3] ^ jArr[i + 3];
        MUL64_NO_SIMD_GF2X_XOR(jArr, i + 1, j ^ j2, j3 ^ j4);
    }

    private static void mul128_no_simd_gf2x_xor(long[] jArr, int i, long j, long j2, long j3, long j4, long[] jArr2) {
        MUL64_NO_SIMD_GF2X(jArr2, 0, j, j3);
        MUL64_NO_SIMD_GF2X(jArr2, 2, j2, j4);
        jArr[i] = jArr[i] ^ jArr2[0];
        jArr2[2] = jArr2[2] ^ jArr2[1];
        int i2 = i + 1;
        jArr[i2] = jArr[i2] ^ (jArr2[0] ^ jArr2[2]);
        int i3 = i + 2;
        jArr[i3] = jArr[i3] ^ (jArr2[2] ^ jArr2[3]);
        int i4 = i + 3;
        jArr[i4] = jArr[i4] ^ jArr2[3];
        MUL64_NO_SIMD_GF2X_XOR(jArr, i + 1, j ^ j2, j3 ^ j4);
    }

    public static void mul192_no_simd_gf2x(long[] jArr, int i, long[] jArr2, int i2, long[] jArr3, int i3) {
        MUL64_NO_SIMD_GF2X(jArr, i, jArr2[i2], jArr3[i3]);
        MUL64_NO_SIMD_GF2X(jArr, i + 4, jArr2[i2 + 2], jArr3[i3 + 2]);
        MUL64_NO_SIMD_GF2X(jArr, i + 2, jArr2[i2 + 1], jArr3[i3 + 1]);
        int i4 = i + 1;
        jArr[i4] = jArr[i4] ^ jArr[i + 2];
        int i5 = i + 3;
        jArr[i5] = jArr[i5] ^ jArr[i + 4];
        jArr[i + 4] = jArr[i + 3] ^ jArr[i + 5];
        jArr[i + 2] = (jArr[i + 3] ^ jArr[i + 1]) ^ jArr[i];
        jArr[i + 3] = jArr[i + 1] ^ jArr[i + 4];
        int i6 = i + 1;
        jArr[i6] = jArr[i6] ^ jArr[i];
        MUL64_NO_SIMD_GF2X_XOR(jArr, i + 1, jArr2[i2] ^ jArr2[i2 + 1], jArr3[i3] ^ jArr3[i3 + 1]);
        MUL64_NO_SIMD_GF2X_XOR(jArr, i + 3, jArr2[i2 + 1] ^ jArr2[i2 + 2], jArr3[i3 + 1] ^ jArr3[i3 + 2]);
        MUL64_NO_SIMD_GF2X_XOR(jArr, i + 2, jArr2[i2] ^ jArr2[i2 + 2], jArr3[i3] ^ jArr3[i3 + 2]);
    }

    public static void mul192_no_simd_gf2x_xor(long[] jArr, int i, long[] jArr2, int i2, long[] jArr3, int i3, long[] jArr4) {
        MUL64_NO_SIMD_GF2X(jArr4, 0, jArr2[i2], jArr3[i3]);
        MUL64_NO_SIMD_GF2X(jArr4, 4, jArr2[i2 + 2], jArr3[i3 + 2]);
        MUL64_NO_SIMD_GF2X(jArr4, 2, jArr2[i2 + 1], jArr3[i3 + 1]);
        jArr[i] = jArr[i] ^ jArr4[0];
        jArr4[1] = jArr4[1] ^ jArr4[2];
        jArr4[3] = jArr4[3] ^ jArr4[4];
        jArr4[4] = jArr4[3] ^ jArr4[5];
        jArr4[0] = jArr4[0] ^ jArr4[1];
        int i4 = i + 1;
        jArr[i4] = jArr[i4] ^ jArr4[0];
        int i5 = i + 2;
        jArr[i5] = jArr[i5] ^ (jArr4[3] ^ jArr4[0]);
        int i6 = i + 3;
        jArr[i6] = jArr[i6] ^ (jArr4[1] ^ jArr4[4]);
        int i7 = i + 4;
        jArr[i7] = jArr[i7] ^ jArr4[4];
        int i8 = i + 5;
        jArr[i8] = jArr[i8] ^ jArr4[5];
        MUL64_NO_SIMD_GF2X_XOR(jArr, i + 1, jArr2[i2] ^ jArr2[i2 + 1], jArr3[i3] ^ jArr3[i3 + 1]);
        MUL64_NO_SIMD_GF2X_XOR(jArr, i + 3, jArr2[i2 + 1] ^ jArr2[i2 + 2], jArr3[i3 + 1] ^ jArr3[i3 + 2]);
        MUL64_NO_SIMD_GF2X_XOR(jArr, i + 2, jArr2[i2] ^ jArr2[i2 + 2], jArr3[i3] ^ jArr3[i3 + 2]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void mul288_no_simd_gf2x(long[] jArr, int i, long[] jArr2, int i2, long[] jArr3, int i3, long[] jArr4) {
        mul128_no_simd_gf2x(jArr, i, jArr2[i2], jArr2[i2 + 1], jArr3[i3], jArr3[i3 + 1]);
        MUL64_NO_SIMD_GF2X(jArr, i + 4, jArr2[i2 + 2], jArr3[i3 + 2]);
        MUL64_NO_SIMD_GF2X(jArr, i + 7, jArr2[i2 + 3], jArr3[i3 + 3]);
        int i4 = i + 7;
        jArr[i4] = jArr[i4] ^ jArr[i + 5];
        int i5 = i + 8;
        jArr[i5] = jArr[i5] ^ MUL32_NO_SIMD_GF2X(jArr2[i2 + 4], jArr3[i3 + 4]);
        jArr[i + 5] = jArr[i + 7] ^ jArr[i + 4];
        int i6 = i + 7;
        jArr[i6] = jArr[i6] ^ jArr[i + 8];
        jArr[i + 6] = jArr[i + 7] ^ jArr[i + 4];
        MUL64_NO_SIMD_GF2X_XOR(jArr, i + 5, jArr2[i2 + 2] ^ jArr2[i2 + 3], jArr3[i3 + 2] ^ jArr3[i3 + 3]);
        MUL64_NO_SIMD_GF2X_XOR(jArr, i + 7, jArr2[i2 + 3] ^ jArr2[i2 + 4], jArr3[i3 + 3] ^ jArr3[i3 + 4]);
        MUL64_NO_SIMD_GF2X_XOR(jArr, i + 6, jArr2[i2 + 2] ^ jArr2[i2 + 4], jArr3[i3 + 2] ^ jArr3[i3 + 4]);
        int i7 = i + 4;
        jArr[i7] = jArr[i7] ^ jArr[i + 2];
        int i8 = i + 5;
        jArr[i8] = jArr[i8] ^ jArr[i + 3];
        long j = jArr2[i2] ^ jArr2[i2 + 2];
        long j2 = jArr2[i2 + 1] ^ jArr2[i2 + 3];
        long j3 = jArr3[i3] ^ jArr3[i3 + 2];
        long j4 = jArr3[i3 + 1] ^ jArr3[i3 + 3];
        MUL64_NO_SIMD_GF2X(jArr4, 0, j, j3);
        MUL64_NO_SIMD_GF2X(jArr4, 2, j2, j4);
        jArr4[2] = jArr4[2] ^ jArr4[1];
        jArr4[3] = jArr4[3] ^ MUL32_NO_SIMD_GF2X(jArr2[i2 + 4], jArr3[i3 + 4]);
        jArr[i + 2] = (jArr[i + 4] ^ jArr[i]) ^ jArr4[0];
        jArr[i + 3] = ((jArr[i + 5] ^ jArr[i + 1]) ^ jArr4[2]) ^ jArr4[0];
        jArr4[2] = jArr4[2] ^ jArr4[3];
        int i9 = i + 4;
        jArr[i9] = jArr[i9] ^ ((jArr[i + 6] ^ jArr4[2]) ^ jArr4[0]);
        int i10 = i + 5;
        jArr[i10] = jArr[i10] ^ (jArr[i + 7] ^ jArr4[2]);
        int i11 = i + 6;
        jArr[i11] = jArr[i11] ^ (jArr[i + 8] ^ jArr4[3]);
        MUL64_NO_SIMD_GF2X_XOR(jArr, i + 3, j ^ j2, j3 ^ j4);
        MUL64_NO_SIMD_GF2X_XOR(jArr, i + 5, j2 ^ jArr2[i2 + 4], j4 ^ jArr3[i3 + 4]);
        MUL64_NO_SIMD_GF2X_XOR(jArr, i + 4, j ^ jArr2[i2 + 4], j3 ^ jArr3[i3 + 4]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void mul288_no_simd_gf2x_xor(long[] jArr, int i, long[] jArr2, int i2, long[] jArr3, int i3, long[] jArr4) {
        mul128_no_simd_gf2x(jArr4, 0, jArr2[i2], jArr2[i2 + 1], jArr3[i3], jArr3[i3 + 1]);
        MUL64_NO_SIMD_GF2X(jArr4, 4, jArr2[i2 + 2], jArr3[i3 + 2]);
        MUL64_NO_SIMD_GF2X(jArr4, 7, jArr2[i2 + 3], jArr3[i3 + 3]);
        jArr4[7] = jArr4[7] ^ jArr4[5];
        jArr4[8] = jArr4[8] ^ MUL32_NO_SIMD_GF2X(jArr2[i2 + 4], jArr3[i3 + 4]);
        jArr4[5] = jArr4[7] ^ jArr4[4];
        jArr4[7] = jArr4[7] ^ jArr4[8];
        jArr4[6] = jArr4[7] ^ jArr4[4];
        jArr4[4] = jArr4[4] ^ jArr4[2];
        jArr4[5] = jArr4[5] ^ jArr4[3];
        jArr[i] = jArr[i] ^ jArr4[0];
        int i4 = i + 1;
        jArr[i4] = jArr[i4] ^ jArr4[1];
        int i5 = i + 2;
        jArr[i5] = jArr[i5] ^ (jArr4[4] ^ jArr4[0]);
        MUL64_NO_SIMD_GF2X_XOR(jArr4, 5, jArr2[i2 + 2] ^ jArr2[i2 + 3], jArr3[i3 + 2] ^ jArr3[i3 + 3]);
        MUL64_NO_SIMD_GF2X_XOR(jArr4, 7, jArr2[i2 + 3] ^ jArr2[i2 + 4], jArr3[i3 + 3] ^ jArr3[i3 + 4]);
        MUL64_NO_SIMD_GF2X_XOR(jArr4, 6, jArr2[i2 + 2] ^ jArr2[i2 + 4], jArr3[i3 + 2] ^ jArr3[i3 + 4]);
        int i6 = i + 3;
        jArr[i6] = jArr[i6] ^ (jArr4[5] ^ jArr4[1]);
        int i7 = i + 4;
        jArr[i7] = jArr[i7] ^ (jArr4[4] ^ jArr4[6]);
        int i8 = i + 5;
        jArr[i8] = jArr[i8] ^ (jArr4[5] ^ jArr4[7]);
        int i9 = i + 6;
        jArr[i9] = jArr[i9] ^ (jArr4[6] ^ jArr4[8]);
        int i10 = i + 7;
        jArr[i10] = jArr[i10] ^ jArr4[7];
        int i11 = i + 8;
        jArr[i11] = jArr[i11] ^ jArr4[8];
        long j = jArr2[i2] ^ jArr2[i2 + 2];
        long j2 = jArr2[i2 + 1] ^ jArr2[i2 + 3];
        long j3 = jArr3[i3] ^ jArr3[i3 + 2];
        long j4 = jArr3[i3 + 1] ^ jArr3[i3 + 3];
        MUL64_NO_SIMD_GF2X(jArr4, 0, j, j3);
        MUL64_NO_SIMD_GF2X(jArr4, 2, j2, j4);
        jArr4[2] = jArr4[2] ^ jArr4[1];
        jArr4[3] = jArr4[3] ^ MUL32_NO_SIMD_GF2X(jArr2[i2 + 4], jArr3[i3 + 4]);
        int i12 = i + 2;
        jArr[i12] = jArr[i12] ^ jArr4[0];
        int i13 = i + 3;
        jArr[i13] = jArr[i13] ^ (jArr4[2] ^ jArr4[0]);
        jArr4[2] = jArr4[2] ^ jArr4[3];
        int i14 = i + 4;
        jArr[i14] = jArr[i14] ^ (jArr4[2] ^ jArr4[0]);
        int i15 = i + 5;
        jArr[i15] = jArr[i15] ^ jArr4[2];
        int i16 = i + 6;
        jArr[i16] = jArr[i16] ^ jArr4[3];
        MUL64_NO_SIMD_GF2X_XOR(jArr, i + 3, j ^ j2, j3 ^ j4);
        MUL64_NO_SIMD_GF2X_XOR(jArr, i + 5, j2 ^ jArr2[i2 + 4], j4 ^ jArr3[i3 + 4]);
        MUL64_NO_SIMD_GF2X_XOR(jArr, i + 4, j ^ jArr2[i2 + 4], j3 ^ jArr3[i3 + 4]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void mul384_no_simd_gf2x(long[] jArr, long[] jArr2, int i, long[] jArr3, int i2, long[] jArr4) {
        mul192_no_simd_gf2x(jArr, 0, jArr2, i, jArr3, i2);
        mul192_no_simd_gf2x(jArr, 6, jArr2, i + 3, jArr3, i2 + 3);
        long j = jArr2[i] ^ jArr2[i + 3];
        long j2 = jArr2[i + 1] ^ jArr2[i + 4];
        long j3 = jArr2[i + 2] ^ jArr2[i + 5];
        long j4 = jArr3[i2] ^ jArr3[i2 + 3];
        long j5 = jArr3[i2 + 1] ^ jArr3[i2 + 4];
        long j6 = jArr3[i2 + 2] ^ jArr3[i2 + 5];
        jArr[6] = jArr[6] ^ jArr[3];
        jArr[7] = jArr[7] ^ jArr[4];
        jArr[8] = jArr[8] ^ jArr[5];
        MUL64_NO_SIMD_GF2X(jArr4, 0, j, j4);
        MUL64_NO_SIMD_GF2X(jArr4, 4, j3, j6);
        MUL64_NO_SIMD_GF2X(jArr4, 2, j2, j5);
        jArr[3] = (jArr[6] ^ jArr[0]) ^ jArr4[0];
        jArr4[1] = jArr4[1] ^ jArr4[2];
        jArr4[0] = jArr4[0] ^ jArr4[1];
        jArr4[3] = jArr4[3] ^ jArr4[4];
        jArr4[4] = jArr4[3] ^ jArr4[5];
        jArr[5] = ((jArr[8] ^ jArr[2]) ^ jArr4[3]) ^ jArr4[0];
        jArr[6] = jArr[6] ^ ((jArr[9] ^ jArr4[1]) ^ jArr4[4]);
        jArr[4] = (jArr[7] ^ jArr[1]) ^ jArr4[0];
        jArr[7] = jArr[7] ^ (jArr[10] ^ jArr4[4]);
        jArr[8] = jArr[8] ^ (jArr[11] ^ jArr4[5]);
        MUL64_NO_SIMD_GF2X_XOR(jArr, 4, j ^ j2, j4 ^ j5);
        MUL64_NO_SIMD_GF2X_XOR(jArr, 6, j2 ^ j3, j5 ^ j6);
        MUL64_NO_SIMD_GF2X_XOR(jArr, 5, j ^ j3, j4 ^ j6);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void mul384_no_simd_gf2x_xor(long[] jArr, long[] jArr2, int i, long[] jArr3, int i2, long[] jArr4) {
        mul192_no_simd_gf2x(jArr4, 0, jArr2, i, jArr3, i2);
        mul192_no_simd_gf2x(jArr4, 6, jArr2, i + 3, jArr3, i2 + 3);
        long j = jArr2[i] ^ jArr2[i + 3];
        long j2 = jArr2[i + 1] ^ jArr2[i + 4];
        long j3 = jArr2[i + 2] ^ jArr2[i + 5];
        long j4 = jArr3[i2] ^ jArr3[i2 + 3];
        long j5 = jArr3[i2 + 1] ^ jArr3[i2 + 4];
        long j6 = jArr3[i2 + 2] ^ jArr3[i2 + 5];
        jArr4[6] = jArr4[6] ^ jArr4[3];
        jArr4[7] = jArr4[7] ^ jArr4[4];
        jArr4[8] = jArr4[8] ^ jArr4[5];
        jArr[0] = jArr[0] ^ jArr4[0];
        jArr[1] = jArr[1] ^ jArr4[1];
        jArr[2] = jArr[2] ^ jArr4[2];
        jArr[3] = jArr[3] ^ (jArr4[6] ^ jArr4[0]);
        jArr[5] = jArr[5] ^ (jArr4[8] ^ jArr4[2]);
        jArr[6] = jArr[6] ^ (jArr4[6] ^ jArr4[9]);
        jArr[4] = jArr[4] ^ (jArr4[7] ^ jArr4[1]);
        jArr[7] = jArr[7] ^ (jArr4[7] ^ jArr4[10]);
        jArr[8] = jArr[8] ^ (jArr4[8] ^ jArr4[11]);
        jArr[9] = jArr[9] ^ jArr4[9];
        jArr[10] = jArr[10] ^ jArr4[10];
        jArr[11] = jArr[11] ^ jArr4[11];
        MUL64_NO_SIMD_GF2X(jArr4, 0, j, j4);
        MUL64_NO_SIMD_GF2X(jArr4, 4, j3, j6);
        MUL64_NO_SIMD_GF2X(jArr4, 2, j2, j5);
        jArr[3] = jArr[3] ^ jArr4[0];
        jArr4[1] = jArr4[1] ^ jArr4[2];
        jArr4[0] = jArr4[0] ^ jArr4[1];
        jArr4[3] = jArr4[3] ^ jArr4[4];
        jArr4[4] = jArr4[3] ^ jArr4[5];
        jArr[5] = jArr[5] ^ (jArr4[3] ^ jArr4[0]);
        jArr[6] = jArr[6] ^ (jArr4[1] ^ jArr4[4]);
        jArr[4] = jArr[4] ^ jArr4[0];
        jArr[7] = jArr[7] ^ jArr4[4];
        jArr[8] = jArr[8] ^ jArr4[5];
        MUL64_NO_SIMD_GF2X_XOR(jArr, 4, j ^ j2, j4 ^ j5);
        MUL64_NO_SIMD_GF2X_XOR(jArr, 6, j2 ^ j3, j5 ^ j6);
        MUL64_NO_SIMD_GF2X_XOR(jArr, 5, j ^ j3, j4 ^ j6);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void mul416_no_simd_gf2x(long[] jArr, long[] jArr2, int i, long[] jArr3, int i2, long[] jArr4) {
        mul192_no_simd_gf2x(jArr, 0, jArr2, i, jArr3, i2);
        mul128_no_simd_gf2x(jArr, 6, jArr2[i + 3], jArr2[i + 4], jArr3[i2 + 3], jArr3[i2 + 4]);
        MUL64_NO_SIMD_GF2X(jArr, 10, jArr2[i + 5], jArr3[i2 + 5]);
        jArr[12] = MUL32_NO_SIMD_GF2X(jArr2[i + 6], jArr3[i2 + 6]) ^ jArr[11];
        jArr[11] = jArr[10] ^ jArr[12];
        MUL64_NO_SIMD_GF2X_XOR(jArr, 11, jArr2[i + 5] ^ jArr2[i + 6], jArr3[i2 + 5] ^ jArr3[i2 + 6]);
        jArr[8] = jArr[8] ^ jArr[10];
        jArr[11] = jArr[11] ^ jArr[9];
        jArr[10] = jArr[8] ^ jArr[12];
        jArr[8] = jArr[8] ^ jArr[6];
        jArr[9] = jArr[11] ^ jArr[7];
        mul128_no_simd_gf2x_xor(jArr, 8, jArr2[i + 3] ^ jArr2[i + 5], jArr2[i + 4] ^ jArr2[i + 6], jArr3[i2 + 3] ^ jArr3[i2 + 5], jArr3[i2 + 4] ^ jArr3[i2 + 6], jArr4);
        long j = jArr2[i] ^ jArr2[i + 3];
        long j2 = jArr2[i + 1] ^ jArr2[i + 4];
        long j3 = jArr2[i + 2] ^ jArr2[i + 5];
        long j4 = jArr2[i + 6];
        long j5 = jArr3[i2] ^ jArr3[i2 + 3];
        long j6 = jArr3[i2 + 1] ^ jArr3[i2 + 4];
        long j7 = jArr3[i2 + 2] ^ jArr3[i2 + 5];
        long j8 = jArr3[i2 + 6];
        jArr[6] = jArr[6] ^ jArr[3];
        jArr[7] = jArr[7] ^ jArr[4];
        jArr[8] = jArr[8] ^ jArr[5];
        mul128_no_simd_gf2x(jArr4, 0, j, j2, j5, j6);
        MUL64_NO_SIMD_GF2X(jArr4, 4, j3, j7);
        jArr4[6] = MUL32_NO_SIMD_GF2X(j4, j8) ^ jArr4[5];
        jArr4[5] = jArr4[4] ^ jArr4[6];
        MUL64_NO_SIMD_GF2X_XOR(jArr4, 5, j3 ^ j4, j7 ^ j8);
        jArr[3] = (jArr[6] ^ jArr[0]) ^ jArr4[0];
        jArr[4] = (jArr[7] ^ jArr[1]) ^ jArr4[1];
        jArr4[2] = jArr4[2] ^ jArr4[4];
        jArr4[3] = jArr4[3] ^ jArr4[5];
        jArr[5] = ((jArr[8] ^ jArr[2]) ^ jArr4[2]) ^ jArr4[0];
        jArr[6] = jArr[6] ^ ((jArr[9] ^ jArr4[3]) ^ jArr4[1]);
        jArr[7] = jArr[7] ^ ((jArr[10] ^ jArr4[2]) ^ jArr4[6]);
        jArr[8] = jArr[8] ^ (jArr[11] ^ jArr4[3]);
        jArr[9] = jArr[9] ^ (jArr[12] ^ jArr4[6]);
        mul128_no_simd_gf2x_xor(jArr, 5, j ^ j3, j2 ^ j4, j5 ^ j7, j6 ^ j8, jArr4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void mul416_no_simd_gf2x_xor(long[] jArr, long[] jArr2, int i, long[] jArr3, int i2, long[] jArr4, long[] jArr5) {
        mul192_no_simd_gf2x(jArr4, 0, jArr2, i, jArr3, i2);
        mul128_no_simd_gf2x(jArr4, 6, jArr2[i + 3], jArr2[i + 4], jArr3[i2 + 3], jArr3[i2 + 4]);
        MUL64_NO_SIMD_GF2X(jArr4, 10, jArr2[i + 5], jArr3[i2 + 5]);
        jArr4[12] = MUL32_NO_SIMD_GF2X(jArr2[i + 6], jArr3[i2 + 6]) ^ jArr4[11];
        jArr4[11] = jArr4[10] ^ jArr4[12];
        MUL64_NO_SIMD_GF2X_XOR(jArr4, 11, jArr2[i + 5] ^ jArr2[i + 6], jArr3[i2 + 5] ^ jArr3[i2 + 6]);
        jArr4[8] = jArr4[8] ^ jArr4[10];
        jArr4[11] = jArr4[11] ^ jArr4[9];
        jArr4[10] = jArr4[8] ^ jArr4[12];
        jArr4[8] = jArr4[8] ^ jArr4[6];
        jArr4[9] = jArr4[11] ^ jArr4[7];
        jArr4[6] = jArr4[6] ^ jArr4[3];
        jArr4[7] = jArr4[7] ^ jArr4[4];
        jArr4[8] = jArr4[8] ^ jArr4[5];
        mul128_no_simd_gf2x_xor(jArr4, 8, jArr2[i + 3] ^ jArr2[i + 5], jArr2[i + 4] ^ jArr2[i + 6], jArr3[i2 + 3] ^ jArr3[i2 + 5], jArr3[i2 + 4] ^ jArr3[i2 + 6], jArr5);
        jArr[0] = jArr[0] ^ jArr4[0];
        jArr[1] = jArr[1] ^ jArr4[1];
        jArr[2] = jArr[2] ^ jArr4[2];
        jArr[3] = jArr[3] ^ (jArr4[6] ^ jArr4[0]);
        jArr[4] = jArr[4] ^ (jArr4[7] ^ jArr4[1]);
        jArr[5] = jArr[5] ^ (jArr4[8] ^ jArr4[2]);
        jArr[6] = jArr[6] ^ (jArr4[6] ^ jArr4[9]);
        jArr[7] = jArr[7] ^ (jArr4[7] ^ jArr4[10]);
        jArr[8] = jArr[8] ^ (jArr4[8] ^ jArr4[11]);
        jArr[9] = jArr[9] ^ (jArr4[9] ^ jArr4[12]);
        jArr[10] = jArr[10] ^ jArr4[10];
        jArr[11] = jArr[11] ^ jArr4[11];
        jArr[12] = jArr[12] ^ jArr4[12];
        long j = jArr2[i] ^ jArr2[i + 3];
        long j2 = jArr2[i + 1] ^ jArr2[i + 4];
        long j3 = jArr2[i + 2] ^ jArr2[i + 5];
        long j4 = jArr2[i + 6];
        long j5 = jArr3[i2] ^ jArr3[i2 + 3];
        long j6 = jArr3[i2 + 1] ^ jArr3[i2 + 4];
        long j7 = jArr3[i2 + 2] ^ jArr3[i2 + 5];
        long j8 = jArr3[i2 + 6];
        mul128_no_simd_gf2x(jArr4, 0, j, j2, j5, j6);
        MUL64_NO_SIMD_GF2X(jArr4, 4, j3, j7);
        jArr4[6] = MUL32_NO_SIMD_GF2X(j4, j8) ^ jArr4[5];
        jArr4[5] = jArr4[4] ^ jArr4[6];
        MUL64_NO_SIMD_GF2X_XOR(jArr4, 5, j3 ^ j4, j7 ^ j8);
        jArr[3] = jArr[3] ^ jArr4[0];
        jArr[4] = jArr[4] ^ jArr4[1];
        jArr4[2] = jArr4[2] ^ jArr4[4];
        jArr4[3] = jArr4[3] ^ jArr4[5];
        jArr[5] = jArr[5] ^ (jArr4[2] ^ jArr4[0]);
        jArr[6] = jArr[6] ^ (jArr4[3] ^ jArr4[1]);
        jArr[7] = jArr[7] ^ (jArr4[2] ^ jArr4[6]);
        jArr[8] = jArr[8] ^ jArr4[3];
        jArr[9] = jArr[9] ^ jArr4[6];
        mul128_no_simd_gf2x_xor(jArr, 5, j ^ j3, j2 ^ j4, j5 ^ j7, j6 ^ j8, jArr4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void mul544_no_simd_gf2x(long[] jArr, long[] jArr2, int i, long[] jArr3, int i2, long[] jArr4, long[] jArr5, long[] jArr6) {
        mul128_no_simd_gf2x(jArr, 0, jArr2[i], jArr2[i + 1], jArr3[i2], jArr3[i2 + 1]);
        mul128_no_simd_gf2x(jArr, 4, jArr2[i + 2], jArr2[i + 3], jArr3[i2 + 2], jArr3[i2 + 3]);
        jArr[4] = jArr[4] ^ jArr[2];
        jArr[5] = jArr[5] ^ jArr[3];
        jArr[2] = jArr[4] ^ jArr[0];
        jArr[3] = jArr[5] ^ jArr[1];
        jArr[4] = jArr[4] ^ jArr[6];
        jArr[5] = jArr[5] ^ jArr[7];
        mul128_no_simd_gf2x_xor(jArr, 2, jArr2[i] ^ jArr2[i + 2], jArr2[i + 1] ^ jArr2[i + 3], jArr3[i2] ^ jArr3[i2 + 2], jArr3[i2 + 1] ^ jArr3[i2 + 3], jArr6);
        mul288_no_simd_gf2x(jArr, 8, jArr2, i + 4, jArr3, i2 + 4, jArr6);
        jArr[8] = jArr[8] ^ jArr[4];
        jArr[9] = jArr[9] ^ jArr[5];
        jArr[10] = jArr[10] ^ jArr[6];
        jArr[11] = jArr[11] ^ jArr[7];
        jArr[4] = jArr[8] ^ jArr[0];
        jArr[5] = jArr[9] ^ jArr[1];
        jArr[6] = jArr[10] ^ jArr[2];
        jArr[7] = jArr[11] ^ jArr[3];
        jArr[8] = jArr[8] ^ jArr[12];
        jArr[9] = jArr[9] ^ jArr[13];
        jArr[10] = jArr[10] ^ jArr[14];
        jArr[11] = jArr[11] ^ jArr[15];
        jArr[12] = jArr[12] ^ jArr[16];
        jArr4[0] = jArr2[i] ^ jArr2[i + 4];
        jArr4[1] = jArr2[i + 1] ^ jArr2[i + 5];
        jArr4[2] = jArr2[i + 2] ^ jArr2[i + 6];
        jArr4[3] = jArr2[i + 3] ^ jArr2[i + 7];
        jArr4[4] = jArr2[i + 8];
        jArr5[0] = jArr3[i2] ^ jArr3[i2 + 4];
        jArr5[1] = jArr3[i2 + 1] ^ jArr3[i2 + 5];
        jArr5[2] = jArr3[i2 + 2] ^ jArr3[i2 + 6];
        jArr5[3] = jArr3[i2 + 3] ^ jArr3[i2 + 7];
        jArr5[4] = jArr3[i2 + 8];
        mul288_no_simd_gf2x_xor(jArr, 4, jArr4, 0, jArr5, 0, jArr6);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void mul544_no_simd_gf2x_xor(long[] jArr, long[] jArr2, int i, long[] jArr3, int i2, long[] jArr4, long[] jArr5, long[] jArr6, long[] jArr7) {
        mul128_no_simd_gf2x(jArr6, 0, jArr2[i], jArr2[i + 1], jArr3[i2], jArr3[i2 + 1]);
        mul128_no_simd_gf2x(jArr6, 4, jArr2[i + 2], jArr2[i + 3], jArr3[i2 + 2], jArr3[i2 + 3]);
        jArr6[4] = jArr6[4] ^ jArr6[2];
        jArr6[5] = jArr6[5] ^ jArr6[3];
        jArr6[2] = jArr6[4] ^ jArr6[0];
        jArr6[3] = jArr6[5] ^ jArr6[1];
        jArr6[4] = jArr6[4] ^ jArr6[6];
        jArr6[5] = jArr6[5] ^ jArr6[7];
        mul128_no_simd_gf2x_xor(jArr6, 2, jArr2[i] ^ jArr2[i + 2], jArr2[i + 1] ^ jArr2[i + 3], jArr3[i2] ^ jArr3[i2 + 2], jArr3[i2 + 1] ^ jArr3[i2 + 3], jArr7);
        mul288_no_simd_gf2x(jArr6, 8, jArr2, i + 4, jArr3, i2 + 4, jArr7);
        jArr6[8] = jArr6[8] ^ jArr6[4];
        jArr6[9] = jArr6[9] ^ jArr6[5];
        jArr6[10] = jArr6[10] ^ jArr6[6];
        jArr6[11] = jArr6[11] ^ jArr6[7];
        jArr[0] = jArr[0] ^ jArr6[0];
        jArr[1] = jArr[1] ^ jArr6[1];
        jArr[2] = jArr[2] ^ jArr6[2];
        jArr[3] = jArr[3] ^ jArr6[3];
        jArr[4] = jArr[4] ^ (jArr6[8] ^ jArr6[0]);
        jArr[5] = jArr[5] ^ (jArr6[9] ^ jArr6[1]);
        jArr[6] = jArr[6] ^ (jArr6[10] ^ jArr6[2]);
        jArr[7] = jArr[7] ^ (jArr6[11] ^ jArr6[3]);
        jArr[8] = jArr[8] ^ (jArr6[8] ^ jArr6[12]);
        jArr[9] = jArr[9] ^ (jArr6[9] ^ jArr6[13]);
        jArr[10] = jArr[10] ^ (jArr6[10] ^ jArr6[14]);
        jArr[11] = jArr[11] ^ (jArr6[11] ^ jArr6[15]);
        jArr[12] = jArr[12] ^ (jArr6[12] ^ jArr6[16]);
        jArr[13] = jArr[13] ^ jArr6[13];
        jArr[14] = jArr[14] ^ jArr6[14];
        jArr[15] = jArr[15] ^ jArr6[15];
        jArr[16] = jArr[16] ^ jArr6[16];
        jArr4[0] = jArr2[i] ^ jArr2[i + 4];
        jArr4[1] = jArr2[i + 1] ^ jArr2[i + 5];
        jArr4[2] = jArr2[i + 2] ^ jArr2[i + 6];
        jArr4[3] = jArr2[i + 3] ^ jArr2[i + 7];
        jArr4[4] = jArr2[i + 8];
        jArr5[0] = jArr3[i2] ^ jArr3[i2 + 4];
        jArr5[1] = jArr3[i2 + 1] ^ jArr3[i2 + 5];
        jArr5[2] = jArr3[i2 + 2] ^ jArr3[i2 + 6];
        jArr5[3] = jArr3[i2 + 3] ^ jArr3[i2 + 7];
        jArr5[4] = jArr3[i2 + 8];
        mul288_no_simd_gf2x_xor(jArr, 4, jArr4, 0, jArr5, 0, jArr6);
    }
}
