package org.bouncycastle.pqc.crypto.gemss;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/Rem_GF2n.class */
abstract class Rem_GF2n {
    protected long mask;
    protected int ki;
    protected int ki64;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/Rem_GF2n$REM192_SPECIALIZED_TRINOMIAL_GF2X.class */
    public static class REM192_SPECIALIZED_TRINOMIAL_GF2X extends Rem_GF2n {
        private final int k3;
        private final int k364;
        private final int ki_k3;

        REM192_SPECIALIZED_TRINOMIAL_GF2X(int i, int i2, int i3, int i4, long j) {
            this.k3 = i;
            this.ki = i2;
            this.ki64 = i3;
            this.k364 = i4;
            this.mask = j;
            this.ki_k3 = i2 - i;
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Rem_GF2n
        public void rem_gf2n(long[] jArr, int i, long[] jArr2) {
            long j = (jArr2[2] >>> this.ki) ^ (jArr2[3] << this.ki64);
            long j2 = (jArr2[3] >>> this.ki) ^ (jArr2[4] << this.ki64);
            long j3 = (jArr2[4] >>> this.ki) ^ (jArr2[5] << this.ki64);
            jArr[i + 1] = ((jArr2[1] ^ j2) ^ (j >>> this.k364)) ^ (j2 << this.k3);
            jArr[i + 2] = (((jArr2[2] ^ j3) ^ (j2 >>> this.k364)) ^ (j3 << this.k3)) & this.mask;
            long j4 = j ^ (j3 >>> this.ki_k3);
            jArr[i] = (jArr2[0] ^ j4) ^ (j4 << this.k3);
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Rem_GF2n
        public void rem_gf2n_xor(long[] jArr, int i, long[] jArr2) {
            long j = (jArr2[2] >>> this.ki) ^ (jArr2[3] << this.ki64);
            long j2 = (jArr2[3] >>> this.ki) ^ (jArr2[4] << this.ki64);
            long j3 = (jArr2[4] >>> this.ki) ^ (jArr2[5] << this.ki64);
            int i2 = i + 1;
            jArr[i2] = jArr[i2] ^ (((jArr2[1] ^ j2) ^ (j >>> this.k364)) ^ (j2 << this.k3));
            int i3 = i + 2;
            jArr[i3] = jArr[i3] ^ ((((jArr2[2] ^ j3) ^ (j2 >>> this.k364)) ^ (j3 << this.k3)) & this.mask);
            long j4 = j ^ (j3 >>> this.ki_k3);
            jArr[i] = jArr[i] ^ ((jArr2[0] ^ j4) ^ (j4 << this.k3));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/Rem_GF2n$REM288_SPECIALIZED_TRINOMIAL_GF2X.class */
    public static class REM288_SPECIALIZED_TRINOMIAL_GF2X extends Rem_GF2n {
        private final int k3;
        private final int k364;
        private final int k364ki;
        private final int k3_ki;

        public REM288_SPECIALIZED_TRINOMIAL_GF2X(int i, int i2, int i3, int i4, long j) {
            this.k3 = i;
            this.ki = i2;
            this.ki64 = i3;
            this.k364 = i4;
            this.mask = j;
            this.k364ki = i4 + i2;
            this.k3_ki = i - i2;
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Rem_GF2n
        public void rem_gf2n(long[] jArr, int i, long[] jArr2) {
            long j = (jArr2[5] >>> this.ki) ^ (jArr2[6] << this.ki64);
            long j2 = (jArr2[6] >>> this.ki) ^ (jArr2[7] << this.ki64);
            jArr[i + 2] = ((jArr2[2] ^ j2) ^ (j >>> this.k364)) ^ (j2 << this.k3);
            long j3 = (jArr2[7] >>> this.ki) ^ (jArr2[8] << this.ki64);
            jArr[i + 3] = ((jArr2[3] ^ j3) ^ (j2 >>> this.k364)) ^ (j3 << this.k3);
            long j4 = jArr2[8] >>> this.ki;
            long j5 = (((jArr2[4] >>> this.ki) ^ (jArr2[5] << this.ki64)) ^ (j3 >>> this.k364ki)) ^ (j4 << this.k3_ki);
            jArr[i + 4] = (((jArr2[4] ^ j4) ^ (j3 >>> this.k364)) ^ (j4 << this.k3)) & this.mask;
            jArr[i] = (jArr2[0] ^ j5) ^ (j5 << this.k3);
            jArr[i + 1] = ((jArr2[1] ^ j) ^ (j << this.k3)) ^ (j5 >>> this.k364);
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Rem_GF2n
        public void rem_gf2n_xor(long[] jArr, int i, long[] jArr2) {
            long j = (jArr2[5] >>> this.ki) ^ (jArr2[6] << this.ki64);
            long j2 = (jArr2[6] >>> this.ki) ^ (jArr2[7] << this.ki64);
            int i2 = i + 2;
            jArr[i2] = jArr[i2] ^ (((jArr2[2] ^ j2) ^ (j >>> this.k364)) ^ (j2 << this.k3));
            long j3 = (jArr2[7] >>> this.ki) ^ (jArr2[8] << this.ki64);
            int i3 = i + 3;
            jArr[i3] = jArr[i3] ^ (((jArr2[3] ^ j3) ^ (j2 >>> this.k364)) ^ (j3 << this.k3));
            long j4 = jArr2[8] >>> this.ki;
            int i4 = i + 4;
            jArr[i4] = jArr[i4] ^ ((((jArr2[4] ^ j4) ^ (j3 >>> this.k364)) ^ (j4 << this.k3)) & this.mask);
            long j5 = (((jArr2[4] >>> this.ki) ^ (jArr2[5] << this.ki64)) ^ (j3 >>> this.k364ki)) ^ (j4 << this.k3_ki);
            jArr[i] = jArr[i] ^ ((jArr2[0] ^ j5) ^ (j5 << this.k3));
            int i5 = i + 1;
            jArr[i5] = jArr[i5] ^ (((jArr2[1] ^ j) ^ (j << this.k3)) ^ (j5 >>> this.k364));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/Rem_GF2n$REM384_SPECIALIZED358_TRINOMIAL_GF2X.class */
    public static class REM384_SPECIALIZED358_TRINOMIAL_GF2X extends Rem_GF2n {
        private final int k3;
        private final int k364;
        private final int k364ki;
        private final int k3_ki;

        public REM384_SPECIALIZED358_TRINOMIAL_GF2X(int i, int i2, int i3, int i4, long j) {
            this.k3 = i;
            this.ki = i2;
            this.ki64 = i3;
            this.k364 = i4;
            this.mask = j;
            this.k364ki = i4 + i2;
            this.k3_ki = i - i2;
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Rem_GF2n
        public void rem_gf2n(long[] jArr, int i, long[] jArr2) {
            long j = (jArr2[6] >>> this.ki) ^ (jArr2[7] << this.ki64);
            long j2 = (jArr2[7] >>> this.ki) ^ (jArr2[8] << this.ki64);
            jArr[i + 2] = ((jArr2[2] ^ j2) ^ (j >>> this.k364)) ^ (j2 << this.k3);
            long j3 = (jArr2[8] >>> this.ki) ^ (jArr2[9] << this.ki64);
            jArr[i + 3] = ((jArr2[3] ^ j3) ^ (j2 >>> this.k364)) ^ (j3 << this.k3);
            long j4 = (jArr2[9] >>> this.ki) ^ (jArr2[10] << this.ki64);
            jArr[i + 4] = ((jArr2[4] ^ j4) ^ (j3 >>> this.k364)) ^ (j4 << this.k3);
            long j5 = (jArr2[10] >>> this.ki) ^ (jArr2[11] << this.ki64);
            long j6 = (((jArr2[5] >>> this.ki) ^ (jArr2[6] << this.ki64)) ^ (j4 >>> this.k364ki)) ^ (j5 << this.k3_ki);
            jArr[i + 5] = ((jArr2[5] ^ j5) ^ (j4 >>> this.k364)) & this.mask;
            jArr[i] = (jArr2[0] ^ j6) ^ (j6 << this.k3);
            jArr[i + 1] = ((jArr2[1] ^ j) ^ (j6 >>> this.k364)) ^ (j << this.k3);
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Rem_GF2n
        public void rem_gf2n_xor(long[] jArr, int i, long[] jArr2) {
            long j = (jArr2[6] >>> this.ki) ^ (jArr2[7] << this.ki64);
            long j2 = (jArr2[7] >>> this.ki) ^ (jArr2[8] << this.ki64);
            int i2 = i + 2;
            jArr[i2] = jArr[i2] ^ (((jArr2[2] ^ j2) ^ (j >>> this.k364)) ^ (j2 << this.k3));
            long j3 = (jArr2[8] >>> this.ki) ^ (jArr2[9] << this.ki64);
            int i3 = i + 3;
            jArr[i3] = jArr[i3] ^ (((jArr2[3] ^ j3) ^ (j2 >>> this.k364)) ^ (j3 << this.k3));
            long j4 = (jArr2[9] >>> this.ki) ^ (jArr2[10] << this.ki64);
            int i4 = i + 4;
            jArr[i4] = jArr[i4] ^ (((jArr2[4] ^ j4) ^ (j3 >>> this.k364)) ^ (j4 << this.k3));
            long j5 = (jArr2[10] >>> this.ki) ^ (jArr2[11] << this.ki64);
            int i5 = i + 5;
            jArr[i5] = jArr[i5] ^ (((jArr2[5] ^ j5) ^ (j4 >>> this.k364)) & this.mask);
            long j6 = (((jArr2[5] >>> this.ki) ^ (jArr2[6] << this.ki64)) ^ (j4 >>> this.k364ki)) ^ (j5 << this.k3_ki);
            jArr[i] = jArr[i] ^ ((jArr2[0] ^ j6) ^ (j6 << this.k3));
            int i6 = i + 1;
            jArr[i6] = jArr[i6] ^ (((jArr2[1] ^ j) ^ (j6 >>> this.k364)) ^ (j << this.k3));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/Rem_GF2n$REM384_SPECIALIZED_TRINOMIAL_GF2X.class */
    public static class REM384_SPECIALIZED_TRINOMIAL_GF2X extends Rem_GF2n {
        private final int k3;
        private final int k364;
        private final int k364ki;
        private final int k3_ki;

        public REM384_SPECIALIZED_TRINOMIAL_GF2X(int i, int i2, int i3, int i4, long j) {
            this.k3 = i;
            this.ki = i2;
            this.ki64 = i3;
            this.k364 = i4;
            this.mask = j;
            this.k364ki = i4 + i2;
            this.k3_ki = i - i2;
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Rem_GF2n
        public void rem_gf2n(long[] jArr, int i, long[] jArr2) {
            long j = (jArr2[7] >>> this.ki) ^ (jArr2[8] << this.ki64);
            long j2 = (jArr2[8] >>> this.ki) ^ (jArr2[9] << this.ki64);
            long j3 = (jArr2[9] >>> this.ki) ^ (jArr2[10] << this.ki64);
            long j4 = (jArr2[10] >>> this.ki) ^ (jArr2[11] << this.ki64);
            long j5 = (((jArr2[5] >>> this.ki) ^ (jArr2[6] << this.ki64)) ^ (j2 >>> this.k364ki)) ^ (j3 << this.k3_ki);
            long j6 = (((jArr2[6] >>> this.ki) ^ (jArr2[7] << this.ki64)) ^ (j3 >>> this.k364ki)) ^ (j4 << this.k3_ki);
            jArr[i] = jArr2[0] ^ j5;
            jArr[i + 1] = (jArr2[1] ^ j6) ^ (j5 << this.k3);
            jArr[i + 2] = ((jArr2[2] ^ j) ^ (j5 >>> this.k364)) ^ (j6 << this.k3);
            jArr[i + 3] = ((jArr2[3] ^ j2) ^ (j6 >>> this.k364)) ^ (j << this.k3);
            jArr[i + 4] = ((jArr2[4] ^ j3) ^ (j >>> this.k364)) ^ (j2 << this.k3);
            jArr[i + 5] = ((jArr2[5] ^ j4) ^ (j2 >>> this.k364)) & this.mask;
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Rem_GF2n
        public void rem_gf2n_xor(long[] jArr, int i, long[] jArr2) {
            long j = (jArr2[7] >>> this.ki) ^ (jArr2[8] << this.ki64);
            long j2 = (jArr2[8] >>> this.ki) ^ (jArr2[9] << this.ki64);
            long j3 = (jArr2[9] >>> this.ki) ^ (jArr2[10] << this.ki64);
            long j4 = (jArr2[10] >>> this.ki) ^ (jArr2[11] << this.ki64);
            long j5 = (((jArr2[5] >>> this.ki) ^ (jArr2[6] << this.ki64)) ^ (j2 >>> this.k364ki)) ^ (j3 << this.k3_ki);
            long j6 = (((jArr2[6] >>> this.ki) ^ (jArr2[7] << this.ki64)) ^ (j3 >>> this.k364ki)) ^ (j4 << this.k3_ki);
            jArr[i] = jArr[i] ^ (jArr2[0] ^ j5);
            int i2 = i + 1;
            jArr[i2] = jArr[i2] ^ ((jArr2[1] ^ j6) ^ (j5 << this.k3));
            int i3 = i + 2;
            jArr[i3] = jArr[i3] ^ (((jArr2[2] ^ j) ^ (j5 >>> this.k364)) ^ (j6 << this.k3));
            int i4 = i + 3;
            jArr[i4] = jArr[i4] ^ (((jArr2[3] ^ j2) ^ (j6 >>> this.k364)) ^ (j << this.k3));
            int i5 = i + 4;
            jArr[i5] = jArr[i5] ^ (((jArr2[4] ^ j3) ^ (j >>> this.k364)) ^ (j2 << this.k3));
            int i6 = i + 5;
            jArr[i6] = jArr[i6] ^ (((jArr2[5] ^ j4) ^ (j2 >>> this.k364)) & this.mask);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/Rem_GF2n$REM384_TRINOMIAL_GF2X.class */
    public static class REM384_TRINOMIAL_GF2X extends Rem_GF2n {
        private final int k3;
        private final int k364;
        private final int ki_k3;

        public REM384_TRINOMIAL_GF2X(int i, int i2, int i3, int i4, long j) {
            this.k3 = i;
            this.ki = i2;
            this.ki64 = i3;
            this.k364 = i4;
            this.mask = j;
            this.ki_k3 = i2 - i;
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Rem_GF2n
        public void rem_gf2n(long[] jArr, int i, long[] jArr2) {
            long j = (jArr2[5] >>> this.ki) ^ (jArr2[6] << this.ki64);
            long j2 = (jArr2[6] >>> this.ki) ^ (jArr2[7] << this.ki64);
            long j3 = (jArr2[7] >>> this.ki) ^ (jArr2[8] << this.ki64);
            long j4 = (jArr2[8] >>> this.ki) ^ (jArr2[9] << this.ki64);
            long j5 = (jArr2[9] >>> this.ki) ^ (jArr2[10] << this.ki64);
            long j6 = (jArr2[10] >>> this.ki) ^ (jArr2[11] << this.ki64);
            long j7 = j ^ (j6 >>> this.ki_k3);
            jArr[i] = (jArr2[0] ^ j7) ^ (j7 << this.k3);
            jArr[i + 1] = ((jArr2[1] ^ j2) ^ (j >>> this.k364)) ^ (j2 << this.k3);
            jArr[i + 2] = ((jArr2[2] ^ j3) ^ (j2 >>> this.k364)) ^ (j3 << this.k3);
            jArr[i + 3] = ((jArr2[3] ^ j4) ^ (j3 >>> this.k364)) ^ (j4 << this.k3);
            jArr[i + 4] = ((jArr2[4] ^ j5) ^ (j4 >>> this.k364)) ^ (j5 << this.k3);
            jArr[i + 5] = (((jArr2[5] ^ j6) ^ (j5 >>> this.k364)) ^ (j6 << this.k3)) & this.mask;
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Rem_GF2n
        public void rem_gf2n_xor(long[] jArr, int i, long[] jArr2) {
            long j = (jArr2[5] >>> this.ki) ^ (jArr2[6] << this.ki64);
            long j2 = (jArr2[6] >>> this.ki) ^ (jArr2[7] << this.ki64);
            long j3 = (jArr2[7] >>> this.ki) ^ (jArr2[8] << this.ki64);
            long j4 = (jArr2[8] >>> this.ki) ^ (jArr2[9] << this.ki64);
            long j5 = (jArr2[9] >>> this.ki) ^ (jArr2[10] << this.ki64);
            long j6 = (jArr2[10] >>> this.ki) ^ (jArr2[11] << this.ki64);
            long j7 = j ^ (j6 >>> this.ki_k3);
            jArr[i] = jArr[i] ^ ((jArr2[0] ^ j7) ^ (j7 << this.k3));
            int i2 = i + 1;
            jArr[i2] = jArr[i2] ^ (((jArr2[1] ^ j2) ^ (j >>> this.k364)) ^ (j2 << this.k3));
            int i3 = i + 2;
            jArr[i3] = jArr[i3] ^ (((jArr2[2] ^ j3) ^ (j2 >>> this.k364)) ^ (j3 << this.k3));
            int i4 = i + 3;
            jArr[i4] = jArr[i4] ^ (((jArr2[3] ^ j4) ^ (j3 >>> this.k364)) ^ (j4 << this.k3));
            int i5 = i + 4;
            jArr[i5] = jArr[i5] ^ (((jArr2[4] ^ j5) ^ (j4 >>> this.k364)) ^ (j5 << this.k3));
            int i6 = i + 5;
            jArr[i6] = jArr[i6] ^ ((((jArr2[5] ^ j6) ^ (j5 >>> this.k364)) ^ (j6 << this.k3)) & this.mask);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/Rem_GF2n$REM402_SPECIALIZED_TRINOMIAL_GF2X.class */
    public static class REM402_SPECIALIZED_TRINOMIAL_GF2X extends Rem_GF2n {
        private final int k3;
        private final int k364;

        public REM402_SPECIALIZED_TRINOMIAL_GF2X(int i, int i2, int i3, int i4, long j) {
            this.k3 = i;
            this.ki = i2;
            this.ki64 = i3;
            this.k364 = i4;
            this.mask = j;
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Rem_GF2n
        public void rem_gf2n(long[] jArr, int i, long[] jArr2) {
            long j = (jArr2[9] >>> this.ki) ^ (jArr2[10] << this.ki64);
            long j2 = (jArr2[10] >>> this.ki) ^ (jArr2[11] << this.ki64);
            long j3 = (jArr2[11] >>> this.ki) ^ (jArr2[12] << this.ki64);
            long j4 = jArr2[12] >>> this.ki;
            long j5 = (((j >>> 39) ^ (j2 << 25)) ^ (jArr2[6] >>> this.ki)) ^ (jArr2[7] << this.ki64);
            long j6 = (((j2 >>> 39) ^ (j3 << 25)) ^ (jArr2[7] >>> this.ki)) ^ (jArr2[8] << this.ki64);
            long j7 = (((j3 >>> 39) ^ (j4 << 25)) ^ (jArr2[8] >>> this.ki)) ^ (jArr2[9] << this.ki64);
            jArr[i] = jArr2[0] ^ j5;
            jArr[i + 1] = jArr2[1] ^ j6;
            jArr[i + 2] = (jArr2[2] ^ j7) ^ (j5 << this.k3);
            jArr[i + 3] = ((jArr2[3] ^ j) ^ (j5 >>> this.k364)) ^ (j6 << this.k3);
            jArr[i + 4] = ((jArr2[4] ^ j2) ^ (j6 >>> this.k364)) ^ (j7 << this.k3);
            jArr[i + 5] = ((jArr2[5] ^ j3) ^ (j7 >>> this.k364)) ^ (j << this.k3);
            jArr[i + 6] = ((jArr2[6] ^ j4) ^ (j >>> this.k364)) & this.mask;
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Rem_GF2n
        public void rem_gf2n_xor(long[] jArr, int i, long[] jArr2) {
            long j = (jArr2[9] >>> this.ki) ^ (jArr2[10] << this.ki64);
            long j2 = (jArr2[10] >>> this.ki) ^ (jArr2[11] << this.ki64);
            long j3 = (jArr2[11] >>> this.ki) ^ (jArr2[12] << this.ki64);
            long j4 = jArr2[12] >>> this.ki;
            long j5 = (((j >>> 39) ^ (j2 << 25)) ^ (jArr2[6] >>> this.ki)) ^ (jArr2[7] << this.ki64);
            long j6 = (((j2 >>> 39) ^ (j3 << 25)) ^ (jArr2[7] >>> this.ki)) ^ (jArr2[8] << this.ki64);
            long j7 = (((j3 >>> 39) ^ (j4 << 25)) ^ (jArr2[8] >>> this.ki)) ^ (jArr2[9] << this.ki64);
            jArr[i] = jArr[i] ^ (jArr2[0] ^ j5);
            int i2 = i + 1;
            jArr[i2] = jArr[i2] ^ (jArr2[1] ^ j6);
            int i3 = i + 2;
            jArr[i3] = jArr[i3] ^ ((jArr2[2] ^ j7) ^ (j5 << this.k3));
            int i4 = i + 3;
            jArr[i4] = jArr[i4] ^ (((jArr2[3] ^ j) ^ (j5 >>> this.k364)) ^ (j6 << this.k3));
            int i5 = i + 4;
            jArr[i5] = jArr[i5] ^ (((jArr2[4] ^ j2) ^ (j6 >>> this.k364)) ^ (j7 << this.k3));
            int i6 = i + 5;
            jArr[i6] = jArr[i6] ^ (((jArr2[5] ^ j3) ^ (j7 >>> this.k364)) ^ (j << this.k3));
            int i7 = i + 6;
            jArr[i7] = jArr[i7] ^ (((jArr2[6] ^ j4) ^ (j >>> this.k364)) & this.mask);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/Rem_GF2n$REM544_PENTANOMIAL_GF2X.class */
    public static class REM544_PENTANOMIAL_GF2X extends Rem_GF2n {
        private final int k1;
        private final int k2;
        private final int k3;
        private final int k164;
        private final int k264;
        private final int k364;
        private final int ki_k3;
        private final int ki_k2;
        private final int ki_k1;

        public REM544_PENTANOMIAL_GF2X(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, long j) {
            this.k1 = i;
            this.k2 = i2;
            this.k3 = i3;
            this.ki = i4;
            this.ki64 = i5;
            this.k164 = i6;
            this.k264 = i7;
            this.k364 = i8;
            this.mask = j;
            this.ki_k3 = i4 - i3;
            this.ki_k2 = i4 - i2;
            this.ki_k1 = i4 - i;
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Rem_GF2n
        public void rem_gf2n(long[] jArr, int i, long[] jArr2) {
            long j = jArr2[16] >>> this.ki;
            long j2 = (jArr2[8] >>> this.ki) ^ (jArr2[9] << this.ki64);
            long j3 = (jArr2[9] >>> this.ki) ^ (jArr2[10] << this.ki64);
            jArr[i + 1] = ((((((jArr2[1] ^ j3) ^ (j2 >>> this.k164)) ^ (j3 << this.k1)) ^ (j2 >>> this.k264)) ^ (j3 << this.k2)) ^ (j2 >>> this.k364)) ^ (j3 << this.k3);
            long j4 = j2 ^ (((j >>> this.ki_k3) ^ (j >>> this.ki_k2)) ^ (j >>> this.ki_k1));
            jArr[i] = (((jArr2[0] ^ j4) ^ (j4 << this.k1)) ^ (j4 << this.k2)) ^ (j4 << this.k3);
            long j5 = (jArr2[10] >>> this.ki) ^ (jArr2[11] << this.ki64);
            jArr[i + 2] = ((((((jArr2[2] ^ j5) ^ (j3 >>> this.k164)) ^ (j5 << this.k1)) ^ (j3 >>> this.k264)) ^ (j5 << this.k2)) ^ (j3 >>> this.k364)) ^ (j5 << this.k3);
            long j6 = (jArr2[11] >>> this.ki) ^ (jArr2[12] << this.ki64);
            jArr[i + 3] = ((((((jArr2[3] ^ j6) ^ (j5 >>> this.k164)) ^ (j6 << this.k1)) ^ (j5 >>> this.k264)) ^ (j6 << this.k2)) ^ (j5 >>> this.k364)) ^ (j6 << this.k3);
            long j7 = (jArr2[12] >>> this.ki) ^ (jArr2[13] << this.ki64);
            jArr[i + 4] = ((((((jArr2[4] ^ j7) ^ (j6 >>> this.k164)) ^ (j7 << this.k1)) ^ (j6 >>> this.k264)) ^ (j7 << this.k2)) ^ (j6 >>> this.k364)) ^ (j7 << this.k3);
            long j8 = (jArr2[13] >>> this.ki) ^ (jArr2[14] << this.ki64);
            jArr[i + 5] = ((((((jArr2[5] ^ j8) ^ (j7 >>> this.k164)) ^ (j8 << this.k1)) ^ (j7 >>> this.k264)) ^ (j8 << this.k2)) ^ (j7 >>> this.k364)) ^ (j8 << this.k3);
            long j9 = (jArr2[14] >>> this.ki) ^ (jArr2[15] << this.ki64);
            jArr[i + 6] = ((((((jArr2[6] ^ j9) ^ (j8 >>> this.k164)) ^ (j9 << this.k1)) ^ (j8 >>> this.k264)) ^ (j9 << this.k2)) ^ (j8 >>> this.k364)) ^ (j9 << this.k3);
            long j10 = (jArr2[15] >>> this.ki) ^ (jArr2[16] << this.ki64);
            jArr[i + 7] = ((((((jArr2[7] ^ j10) ^ (j9 >>> this.k164)) ^ (j10 << this.k1)) ^ (j9 >>> this.k264)) ^ (j10 << this.k2)) ^ (j9 >>> this.k364)) ^ (j10 << this.k3);
            jArr[i + 8] = (((((((jArr2[8] ^ j) ^ (j10 >>> this.k164)) ^ (j << this.k1)) ^ (j10 >>> this.k264)) ^ (j << this.k2)) ^ (j10 >>> this.k364)) ^ (j << this.k3)) & this.mask;
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Rem_GF2n
        public void rem_gf2n_xor(long[] jArr, int i, long[] jArr2) {
            long j = jArr2[16] >>> this.ki;
            long j2 = (jArr2[8] >>> this.ki) ^ (jArr2[9] << this.ki64);
            long j3 = (jArr2[9] >>> this.ki) ^ (jArr2[10] << this.ki64);
            int i2 = i + 1;
            jArr[i2] = jArr[i2] ^ (((((((jArr2[1] ^ j3) ^ (j2 >>> this.k164)) ^ (j3 << this.k1)) ^ (j2 >>> this.k264)) ^ (j3 << this.k2)) ^ (j2 >>> this.k364)) ^ (j3 << this.k3));
            long j4 = j2 ^ (((j >>> this.ki_k3) ^ (j >>> this.ki_k2)) ^ (j >>> this.ki_k1));
            jArr[i] = jArr[i] ^ ((((jArr2[0] ^ j4) ^ (j4 << this.k1)) ^ (j4 << this.k2)) ^ (j4 << this.k3));
            long j5 = (jArr2[10] >>> this.ki) ^ (jArr2[11] << this.ki64);
            int i3 = i + 2;
            jArr[i3] = jArr[i3] ^ (((((((jArr2[2] ^ j5) ^ (j3 >>> this.k164)) ^ (j5 << this.k1)) ^ (j3 >>> this.k264)) ^ (j5 << this.k2)) ^ (j3 >>> this.k364)) ^ (j5 << this.k3));
            long j6 = (jArr2[11] >>> this.ki) ^ (jArr2[12] << this.ki64);
            int i4 = i + 3;
            jArr[i4] = jArr[i4] ^ (((((((jArr2[3] ^ j6) ^ (j5 >>> this.k164)) ^ (j6 << this.k1)) ^ (j5 >>> this.k264)) ^ (j6 << this.k2)) ^ (j5 >>> this.k364)) ^ (j6 << this.k3));
            long j7 = (jArr2[12] >>> this.ki) ^ (jArr2[13] << this.ki64);
            int i5 = i + 4;
            jArr[i5] = jArr[i5] ^ (((((((jArr2[4] ^ j7) ^ (j6 >>> this.k164)) ^ (j7 << this.k1)) ^ (j6 >>> this.k264)) ^ (j7 << this.k2)) ^ (j6 >>> this.k364)) ^ (j7 << this.k3));
            long j8 = (jArr2[13] >>> this.ki) ^ (jArr2[14] << this.ki64);
            int i6 = i + 5;
            jArr[i6] = jArr[i6] ^ (((((((jArr2[5] ^ j8) ^ (j7 >>> this.k164)) ^ (j8 << this.k1)) ^ (j7 >>> this.k264)) ^ (j8 << this.k2)) ^ (j7 >>> this.k364)) ^ (j8 << this.k3));
            long j9 = (jArr2[14] >>> this.ki) ^ (jArr2[15] << this.ki64);
            int i7 = i + 6;
            jArr[i7] = jArr[i7] ^ (((((((jArr2[6] ^ j9) ^ (j8 >>> this.k164)) ^ (j9 << this.k1)) ^ (j8 >>> this.k264)) ^ (j9 << this.k2)) ^ (j8 >>> this.k364)) ^ (j9 << this.k3));
            long j10 = (jArr2[15] >>> this.ki) ^ (jArr2[16] << this.ki64);
            int i8 = i + 7;
            jArr[i8] = jArr[i8] ^ (((((((jArr2[7] ^ j10) ^ (j9 >>> this.k164)) ^ (j10 << this.k1)) ^ (j9 >>> this.k264)) ^ (j10 << this.k2)) ^ (j9 >>> this.k364)) ^ (j10 << this.k3));
            int i9 = i + 8;
            jArr[i9] = jArr[i9] ^ ((((((((jArr2[8] ^ j) ^ (j10 >>> this.k164)) ^ (j << this.k1)) ^ (j10 >>> this.k264)) ^ (j << this.k2)) ^ (j10 >>> this.k364)) ^ (j << this.k3)) & this.mask);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/Rem_GF2n$REM544_PENTANOMIAL_K3_IS_128_GF2X.class */
    public static class REM544_PENTANOMIAL_K3_IS_128_GF2X extends Rem_GF2n {
        private final int k1;
        private final int k2;
        private final int k164;
        private final int k264;

        public REM544_PENTANOMIAL_K3_IS_128_GF2X(int i, int i2, int i3, int i4, int i5, int i6, long j) {
            this.k1 = i;
            this.k2 = i2;
            this.ki = i3;
            this.ki64 = i4;
            this.k164 = i5;
            this.k264 = i6;
            this.mask = j;
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Rem_GF2n
        public void rem_gf2n(long[] jArr, int i, long[] jArr2) {
            long j = (jArr2[10] >>> this.ki) ^ (jArr2[11] << this.ki64);
            long j2 = (jArr2[11] >>> this.ki) ^ (jArr2[12] << this.ki64);
            long j3 = (jArr2[12] >>> this.ki) ^ (jArr2[13] << this.ki64);
            jArr[i + 4] = (((((jArr2[4] ^ j3) ^ j) ^ (j2 >>> this.k164)) ^ (j3 << this.k1)) ^ (j2 >>> this.k264)) ^ (j3 << this.k2);
            long j4 = (jArr2[13] >>> this.ki) ^ (jArr2[14] << this.ki64);
            jArr[i + 5] = (((((jArr2[5] ^ j4) ^ j2) ^ (j3 >>> this.k164)) ^ (j4 << this.k1)) ^ (j3 >>> this.k264)) ^ (j4 << this.k2);
            long j5 = (jArr2[14] >>> this.ki) ^ (jArr2[15] << this.ki64);
            jArr[i + 6] = (((((jArr2[6] ^ j5) ^ j3) ^ (j4 >>> this.k164)) ^ (j5 << this.k1)) ^ (j4 >>> this.k264)) ^ (j5 << this.k2);
            long j6 = (jArr2[15] >>> this.ki) ^ (jArr2[16] << this.ki64);
            jArr[i + 7] = (((((jArr2[7] ^ j6) ^ j4) ^ (j5 >>> this.k164)) ^ (j6 << this.k1)) ^ (j5 >>> this.k264)) ^ (j6 << this.k2);
            long j7 = jArr2[16] >>> this.ki;
            jArr[i + 8] = ((((((jArr2[8] ^ j7) ^ j5) ^ (j6 >>> this.k164)) ^ (j7 << this.k1)) ^ (j6 >>> this.k264)) ^ (j7 << this.k2)) & this.mask;
            long j8 = (((jArr2[8] ^ j5) >>> this.ki) ^ ((jArr2[9] ^ j6) << this.ki64)) ^ (jArr2[16] >>> this.k264);
            long j9 = ((jArr2[9] ^ j6) >>> this.ki) ^ ((jArr2[10] ^ j7) << this.ki64);
            jArr[i] = ((jArr2[0] ^ j8) ^ (j8 << this.k1)) ^ (j8 << this.k2);
            jArr[i + 1] = ((((jArr2[1] ^ j9) ^ (j8 >>> this.k164)) ^ (j9 << this.k1)) ^ (j8 >>> this.k264)) ^ (j9 << this.k2);
            jArr[i + 2] = (((((jArr2[2] ^ j) ^ j8) ^ (j9 >>> this.k164)) ^ (j << this.k1)) ^ (j9 >>> this.k264)) ^ (j << this.k2);
            jArr[i + 3] = (((((jArr2[3] ^ j2) ^ j9) ^ (j >>> this.k164)) ^ (j2 << this.k1)) ^ (j >>> this.k264)) ^ (j2 << this.k2);
        }

        @Override // org.bouncycastle.pqc.crypto.gemss.Rem_GF2n
        public void rem_gf2n_xor(long[] jArr, int i, long[] jArr2) {
            long j = (jArr2[10] >>> this.ki) ^ (jArr2[11] << this.ki64);
            long j2 = (jArr2[11] >>> this.ki) ^ (jArr2[12] << this.ki64);
            long j3 = (jArr2[12] >>> this.ki) ^ (jArr2[13] << this.ki64);
            int i2 = i + 4;
            jArr[i2] = jArr[i2] ^ ((((((jArr2[4] ^ j3) ^ j) ^ (j2 >>> this.k164)) ^ (j3 << this.k1)) ^ (j2 >>> this.k264)) ^ (j3 << this.k2));
            long j4 = (jArr2[13] >>> this.ki) ^ (jArr2[14] << this.ki64);
            int i3 = i + 5;
            jArr[i3] = jArr[i3] ^ ((((((jArr2[5] ^ j4) ^ j2) ^ (j3 >>> this.k164)) ^ (j4 << this.k1)) ^ (j3 >>> this.k264)) ^ (j4 << this.k2));
            long j5 = (jArr2[14] >>> this.ki) ^ (jArr2[15] << this.ki64);
            int i4 = i + 6;
            jArr[i4] = jArr[i4] ^ ((((((jArr2[6] ^ j5) ^ j3) ^ (j4 >>> this.k164)) ^ (j5 << this.k1)) ^ (j4 >>> this.k264)) ^ (j5 << this.k2));
            long j6 = (jArr2[15] >>> this.ki) ^ (jArr2[16] << this.ki64);
            int i5 = i + 7;
            jArr[i5] = jArr[i5] ^ ((((((jArr2[7] ^ j6) ^ j4) ^ (j5 >>> this.k164)) ^ (j6 << this.k1)) ^ (j5 >>> this.k264)) ^ (j6 << this.k2));
            long j7 = jArr2[16] >>> this.ki;
            int i6 = i + 8;
            jArr[i6] = jArr[i6] ^ (((((((jArr2[8] ^ j7) ^ j5) ^ (j6 >>> this.k164)) ^ (j7 << this.k1)) ^ (j6 >>> this.k264)) ^ (j7 << this.k2)) & this.mask);
            long j8 = (((jArr2[8] ^ j5) >>> this.ki) ^ ((jArr2[9] ^ j6) << this.ki64)) ^ (jArr2[16] >>> this.k264);
            long j9 = ((jArr2[9] ^ j6) >>> this.ki) ^ ((jArr2[10] ^ j7) << this.ki64);
            jArr[i] = jArr[i] ^ (((jArr2[0] ^ j8) ^ (j8 << this.k1)) ^ (j8 << this.k2));
            int i7 = i + 1;
            jArr[i7] = jArr[i7] ^ (((((jArr2[1] ^ j9) ^ (j8 >>> this.k164)) ^ (j9 << this.k1)) ^ (j8 >>> this.k264)) ^ (j9 << this.k2));
            int i8 = i + 2;
            jArr[i8] = jArr[i8] ^ ((((((jArr2[2] ^ j) ^ j8) ^ (j9 >>> this.k164)) ^ (j << this.k1)) ^ (j9 >>> this.k264)) ^ (j << this.k2));
            int i9 = i + 3;
            jArr[i9] = jArr[i9] ^ ((((((jArr2[3] ^ j2) ^ j9) ^ (j >>> this.k164)) ^ (j2 << this.k1)) ^ (j >>> this.k264)) ^ (j2 << this.k2));
        }
    }

    Rem_GF2n() {
    }

    public abstract void rem_gf2n(long[] jArr, int i, long[] jArr2);

    public abstract void rem_gf2n_xor(long[] jArr, int i, long[] jArr2);
}
