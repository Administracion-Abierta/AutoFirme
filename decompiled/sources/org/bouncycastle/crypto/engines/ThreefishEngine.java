package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.TweakableBlockCipherParameters;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ThreefishEngine.class */
public class ThreefishEngine implements BlockCipher {
    public static final int BLOCKSIZE_256 = 256;
    public static final int BLOCKSIZE_512 = 512;
    public static final int BLOCKSIZE_1024 = 1024;
    private static final int TWEAK_SIZE_BYTES = 16;
    private static final int TWEAK_SIZE_WORDS = 2;
    private static final int ROUNDS_256 = 72;
    private static final int ROUNDS_512 = 72;
    private static final int ROUNDS_1024 = 80;
    private static final int MAX_ROUNDS = 80;
    private static final long C_240 = 2004413935125273122L;
    private static int[] MOD9 = new int[80];
    private static int[] MOD17 = new int[MOD9.length];
    private static int[] MOD5 = new int[MOD9.length];
    private static int[] MOD3 = new int[MOD9.length];
    private int blocksizeBytes;
    private int blocksizeWords;
    private long[] currentBlock;
    private long[] t = new long[5];
    private long[] kw;
    private ThreefishCipher cipher;
    private boolean forEncryption;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ThreefishEngine$Threefish1024Cipher.class */
    private static final class Threefish1024Cipher extends ThreefishCipher {
        private static final int ROTATION_0_0 = 24;
        private static final int ROTATION_0_1 = 13;
        private static final int ROTATION_0_2 = 8;
        private static final int ROTATION_0_3 = 47;
        private static final int ROTATION_0_4 = 8;
        private static final int ROTATION_0_5 = 17;
        private static final int ROTATION_0_6 = 22;
        private static final int ROTATION_0_7 = 37;
        private static final int ROTATION_1_0 = 38;
        private static final int ROTATION_1_1 = 19;
        private static final int ROTATION_1_2 = 10;
        private static final int ROTATION_1_3 = 55;
        private static final int ROTATION_1_4 = 49;
        private static final int ROTATION_1_5 = 18;
        private static final int ROTATION_1_6 = 23;
        private static final int ROTATION_1_7 = 52;
        private static final int ROTATION_2_0 = 33;
        private static final int ROTATION_2_1 = 4;
        private static final int ROTATION_2_2 = 51;
        private static final int ROTATION_2_3 = 13;
        private static final int ROTATION_2_4 = 34;
        private static final int ROTATION_2_5 = 41;
        private static final int ROTATION_2_6 = 59;
        private static final int ROTATION_2_7 = 17;
        private static final int ROTATION_3_0 = 5;
        private static final int ROTATION_3_1 = 20;
        private static final int ROTATION_3_2 = 48;
        private static final int ROTATION_3_3 = 41;
        private static final int ROTATION_3_4 = 47;
        private static final int ROTATION_3_5 = 28;
        private static final int ROTATION_3_6 = 16;
        private static final int ROTATION_3_7 = 25;
        private static final int ROTATION_4_0 = 41;
        private static final int ROTATION_4_1 = 9;
        private static final int ROTATION_4_2 = 37;
        private static final int ROTATION_4_3 = 31;
        private static final int ROTATION_4_4 = 12;
        private static final int ROTATION_4_5 = 47;
        private static final int ROTATION_4_6 = 44;
        private static final int ROTATION_4_7 = 30;
        private static final int ROTATION_5_0 = 16;
        private static final int ROTATION_5_1 = 34;
        private static final int ROTATION_5_2 = 56;
        private static final int ROTATION_5_3 = 51;
        private static final int ROTATION_5_4 = 4;
        private static final int ROTATION_5_5 = 53;
        private static final int ROTATION_5_6 = 42;
        private static final int ROTATION_5_7 = 41;
        private static final int ROTATION_6_0 = 31;
        private static final int ROTATION_6_1 = 44;
        private static final int ROTATION_6_2 = 47;
        private static final int ROTATION_6_3 = 46;
        private static final int ROTATION_6_4 = 19;
        private static final int ROTATION_6_5 = 42;
        private static final int ROTATION_6_6 = 44;
        private static final int ROTATION_6_7 = 25;
        private static final int ROTATION_7_0 = 9;
        private static final int ROTATION_7_1 = 48;
        private static final int ROTATION_7_2 = 35;
        private static final int ROTATION_7_3 = 52;
        private static final int ROTATION_7_4 = 23;
        private static final int ROTATION_7_5 = 31;
        private static final int ROTATION_7_6 = 37;
        private static final int ROTATION_7_7 = 20;

        public Threefish1024Cipher(long[] jArr, long[] jArr2) {
            super(jArr, jArr2);
        }

        @Override // org.bouncycastle.crypto.engines.ThreefishEngine.ThreefishCipher
        void encryptBlock(long[] jArr, long[] jArr2) {
            long[] jArr3 = this.kw;
            long[] jArr4 = this.t;
            int[] iArr = ThreefishEngine.MOD17;
            int[] iArr2 = ThreefishEngine.MOD3;
            if (jArr3.length != 33) {
                throw new IllegalArgumentException();
            }
            if (jArr4.length != 5) {
                throw new IllegalArgumentException();
            }
            long j = jArr[0];
            long j2 = jArr[1];
            long j3 = jArr[2];
            long j4 = jArr[3];
            long j5 = jArr[4];
            long j6 = jArr[5];
            long j7 = jArr[6];
            long j8 = jArr[7];
            long j9 = jArr[8];
            long j10 = jArr[9];
            long j11 = jArr[10];
            long j12 = jArr[11];
            long j13 = jArr[12];
            long j14 = jArr[13];
            long j15 = jArr[14];
            long j16 = jArr[15];
            long j17 = j + jArr3[0];
            long j18 = j2 + jArr3[1];
            long j19 = j3 + jArr3[2];
            long j20 = j4 + jArr3[3];
            long j21 = j5 + jArr3[4];
            long j22 = j6 + jArr3[5];
            long j23 = j7 + jArr3[6];
            long j24 = j8 + jArr3[7];
            long j25 = j9 + jArr3[8];
            long j26 = j10 + jArr3[9];
            long j27 = j11 + jArr3[10];
            long j28 = j12 + jArr3[11];
            long j29 = j13 + jArr3[12];
            long j30 = j14 + jArr3[13] + jArr4[0];
            long j31 = j15 + jArr3[14] + jArr4[1];
            long j32 = j16 + jArr3[15];
            for (int i = 1; i < 20; i += 2) {
                int i2 = iArr[i];
                int i3 = iArr2[i];
                long j33 = j18;
                long jRotlXor = ThreefishEngine.rotlXor(j33, 24, j17 + j18);
                long j34 = j19 + j20;
                long jRotlXor2 = ThreefishEngine.rotlXor(j20, 13, j34);
                long j35 = j21 + j22;
                long jRotlXor3 = ThreefishEngine.rotlXor(j22, 8, j35);
                long j36 = j23 + j24;
                long jRotlXor4 = ThreefishEngine.rotlXor(j24, 47, j36);
                long j37 = j25 + j26;
                long jRotlXor5 = ThreefishEngine.rotlXor(j26, 8, j37);
                long j38 = j27 + j28;
                long jRotlXor6 = ThreefishEngine.rotlXor(j28, 17, j38);
                long j39 = j29 + j30;
                long jRotlXor7 = ThreefishEngine.rotlXor(j30, 22, j39);
                long j40 = j31 + j32;
                long jRotlXor8 = ThreefishEngine.rotlXor(j32, 37, j40);
                long j41 = j33 + jRotlXor5;
                long jRotlXor9 = ThreefishEngine.rotlXor(jRotlXor5, 38, j41);
                long j42 = j34 + jRotlXor7;
                long jRotlXor10 = ThreefishEngine.rotlXor(jRotlXor7, 19, j42);
                long j43 = j36 + jRotlXor6;
                long jRotlXor11 = ThreefishEngine.rotlXor(jRotlXor6, 10, j43);
                long j44 = j35 + jRotlXor8;
                long jRotlXor12 = ThreefishEngine.rotlXor(jRotlXor8, 55, j44);
                long j45 = j38 + jRotlXor4;
                long jRotlXor13 = ThreefishEngine.rotlXor(jRotlXor4, 49, j45);
                long j46 = j39 + jRotlXor2;
                long jRotlXor14 = ThreefishEngine.rotlXor(jRotlXor2, 18, j46);
                long j47 = j40 + jRotlXor3;
                long jRotlXor15 = ThreefishEngine.rotlXor(jRotlXor3, 23, j47);
                long j48 = j37 + jRotlXor;
                long jRotlXor16 = ThreefishEngine.rotlXor(jRotlXor, 52, j48);
                long j49 = j41 + jRotlXor13;
                long jRotlXor17 = ThreefishEngine.rotlXor(jRotlXor13, 33, j49);
                long j50 = j42 + jRotlXor15;
                long jRotlXor18 = ThreefishEngine.rotlXor(jRotlXor15, 4, j50);
                long j51 = j44 + jRotlXor14;
                long jRotlXor19 = ThreefishEngine.rotlXor(jRotlXor14, 51, j51);
                long j52 = j43 + jRotlXor16;
                long jRotlXor20 = ThreefishEngine.rotlXor(jRotlXor16, 13, j52);
                long j53 = j46 + jRotlXor12;
                long jRotlXor21 = ThreefishEngine.rotlXor(jRotlXor12, 34, j53);
                long j54 = j47 + jRotlXor10;
                long jRotlXor22 = ThreefishEngine.rotlXor(jRotlXor10, 41, j54);
                long j55 = j48 + jRotlXor11;
                long jRotlXor23 = ThreefishEngine.rotlXor(jRotlXor11, 59, j55);
                long j56 = j45 + jRotlXor9;
                long jRotlXor24 = ThreefishEngine.rotlXor(jRotlXor9, 17, j56);
                long j57 = j49 + jRotlXor21;
                long jRotlXor25 = ThreefishEngine.rotlXor(jRotlXor21, 5, j57);
                long j58 = j50 + jRotlXor23;
                long jRotlXor26 = ThreefishEngine.rotlXor(jRotlXor23, 20, j58);
                long j59 = j52 + jRotlXor22;
                long jRotlXor27 = ThreefishEngine.rotlXor(jRotlXor22, 48, j59);
                long j60 = j51 + jRotlXor24;
                long jRotlXor28 = ThreefishEngine.rotlXor(jRotlXor24, 41, j60);
                long j61 = j54 + jRotlXor20;
                long jRotlXor29 = ThreefishEngine.rotlXor(jRotlXor20, 47, j61);
                long j62 = j55 + jRotlXor18;
                long jRotlXor30 = ThreefishEngine.rotlXor(jRotlXor18, 28, j62);
                long j63 = j56 + jRotlXor19;
                long jRotlXor31 = ThreefishEngine.rotlXor(jRotlXor19, 16, j63);
                long j64 = j53 + jRotlXor17;
                long jRotlXor32 = ThreefishEngine.rotlXor(jRotlXor17, 25, j64);
                long j65 = j57 + jArr3[i2];
                long j66 = jRotlXor29 + jArr3[i2 + 1];
                long j67 = j58 + jArr3[i2 + 2];
                long j68 = jRotlXor31 + jArr3[i2 + 3];
                long j69 = j60 + jArr3[i2 + 4];
                long j70 = jRotlXor30 + jArr3[i2 + 5];
                long j71 = j59 + jArr3[i2 + 6];
                long j72 = jRotlXor32 + jArr3[i2 + 7];
                long j73 = j62 + jArr3[i2 + 8];
                long j74 = jRotlXor28 + jArr3[i2 + 9];
                long j75 = j63 + jArr3[i2 + 10];
                long j76 = jRotlXor26 + jArr3[i2 + 11];
                long j77 = j64 + jArr3[i2 + 12];
                long j78 = jRotlXor27 + jArr3[i2 + 13] + jArr4[i3];
                long j79 = j61 + jArr3[i2 + 14] + jArr4[i3 + 1];
                long j80 = jRotlXor25 + jArr3[i2 + 15] + ((long) i);
                long j81 = j65 + j66;
                long jRotlXor33 = ThreefishEngine.rotlXor(j66, 41, j81);
                long j82 = j67 + j68;
                long jRotlXor34 = ThreefishEngine.rotlXor(j68, 9, j82);
                long j83 = j69 + j70;
                long jRotlXor35 = ThreefishEngine.rotlXor(j70, 37, j83);
                long j84 = j71 + j72;
                long jRotlXor36 = ThreefishEngine.rotlXor(j72, 31, j84);
                long j85 = j73 + j74;
                long jRotlXor37 = ThreefishEngine.rotlXor(j74, 12, j85);
                long j86 = j75 + j76;
                long jRotlXor38 = ThreefishEngine.rotlXor(j76, 47, j86);
                long j87 = j77 + j78;
                long jRotlXor39 = ThreefishEngine.rotlXor(j78, 44, j87);
                long j88 = j79 + j80;
                long jRotlXor40 = ThreefishEngine.rotlXor(j80, 30, j88);
                long j89 = j81 + jRotlXor37;
                long jRotlXor41 = ThreefishEngine.rotlXor(jRotlXor37, 16, j89);
                long j90 = j82 + jRotlXor39;
                long jRotlXor42 = ThreefishEngine.rotlXor(jRotlXor39, 34, j90);
                long j91 = j84 + jRotlXor38;
                long jRotlXor43 = ThreefishEngine.rotlXor(jRotlXor38, 56, j91);
                long j92 = j83 + jRotlXor40;
                long jRotlXor44 = ThreefishEngine.rotlXor(jRotlXor40, 51, j92);
                long j93 = j86 + jRotlXor36;
                long jRotlXor45 = ThreefishEngine.rotlXor(jRotlXor36, 4, j93);
                long j94 = j87 + jRotlXor34;
                long jRotlXor46 = ThreefishEngine.rotlXor(jRotlXor34, 53, j94);
                long j95 = j88 + jRotlXor35;
                long jRotlXor47 = ThreefishEngine.rotlXor(jRotlXor35, 42, j95);
                long j96 = j85 + jRotlXor33;
                long jRotlXor48 = ThreefishEngine.rotlXor(jRotlXor33, 41, j96);
                long j97 = j89 + jRotlXor45;
                long jRotlXor49 = ThreefishEngine.rotlXor(jRotlXor45, 31, j97);
                long j98 = j90 + jRotlXor47;
                long jRotlXor50 = ThreefishEngine.rotlXor(jRotlXor47, 44, j98);
                long j99 = j92 + jRotlXor46;
                long jRotlXor51 = ThreefishEngine.rotlXor(jRotlXor46, 47, j99);
                long j100 = j91 + jRotlXor48;
                long jRotlXor52 = ThreefishEngine.rotlXor(jRotlXor48, 46, j100);
                long j101 = j94 + jRotlXor44;
                long jRotlXor53 = ThreefishEngine.rotlXor(jRotlXor44, 19, j101);
                long j102 = j95 + jRotlXor42;
                long jRotlXor54 = ThreefishEngine.rotlXor(jRotlXor42, 42, j102);
                long j103 = j96 + jRotlXor43;
                long jRotlXor55 = ThreefishEngine.rotlXor(jRotlXor43, 44, j103);
                long j104 = j93 + jRotlXor41;
                long jRotlXor56 = ThreefishEngine.rotlXor(jRotlXor41, 25, j104);
                long j105 = j97 + jRotlXor53;
                long jRotlXor57 = ThreefishEngine.rotlXor(jRotlXor53, 9, j105);
                long j106 = j98 + jRotlXor55;
                long jRotlXor58 = ThreefishEngine.rotlXor(jRotlXor55, 48, j106);
                long j107 = j100 + jRotlXor54;
                long jRotlXor59 = ThreefishEngine.rotlXor(jRotlXor54, 35, j107);
                long j108 = j99 + jRotlXor56;
                long jRotlXor60 = ThreefishEngine.rotlXor(jRotlXor56, 52, j108);
                long j109 = j102 + jRotlXor52;
                long jRotlXor61 = ThreefishEngine.rotlXor(jRotlXor52, 23, j109);
                long j110 = j103 + jRotlXor50;
                long jRotlXor62 = ThreefishEngine.rotlXor(jRotlXor50, 31, j110);
                long j111 = j104 + jRotlXor51;
                long jRotlXor63 = ThreefishEngine.rotlXor(jRotlXor51, 37, j111);
                long j112 = j101 + jRotlXor49;
                long jRotlXor64 = ThreefishEngine.rotlXor(jRotlXor49, 20, j112);
                j17 = j105 + jArr3[i2 + 1];
                j18 = jRotlXor61 + jArr3[i2 + 2];
                j19 = j106 + jArr3[i2 + 3];
                j20 = jRotlXor63 + jArr3[i2 + 4];
                j21 = j108 + jArr3[i2 + 5];
                j22 = jRotlXor62 + jArr3[i2 + 6];
                j23 = j107 + jArr3[i2 + 7];
                j24 = jRotlXor64 + jArr3[i2 + 8];
                j25 = j110 + jArr3[i2 + 9];
                j26 = jRotlXor60 + jArr3[i2 + 10];
                j27 = j111 + jArr3[i2 + 11];
                j28 = jRotlXor58 + jArr3[i2 + 12];
                j29 = j112 + jArr3[i2 + 13];
                j30 = jRotlXor59 + jArr3[i2 + 14] + jArr4[i3 + 1];
                j31 = j109 + jArr3[i2 + 15] + jArr4[i3 + 2];
                j32 = jRotlXor57 + jArr3[i2 + 16] + ((long) i) + 1;
            }
            jArr2[0] = j17;
            jArr2[1] = j18;
            jArr2[2] = j19;
            jArr2[3] = j20;
            jArr2[4] = j21;
            jArr2[5] = j22;
            jArr2[6] = j23;
            jArr2[7] = j24;
            jArr2[8] = j25;
            jArr2[9] = j26;
            jArr2[10] = j27;
            jArr2[11] = j28;
            jArr2[12] = j29;
            jArr2[13] = j30;
            jArr2[14] = j31;
            jArr2[15] = j32;
        }

        @Override // org.bouncycastle.crypto.engines.ThreefishEngine.ThreefishCipher
        void decryptBlock(long[] jArr, long[] jArr2) {
            long[] jArr3 = this.kw;
            long[] jArr4 = this.t;
            int[] iArr = ThreefishEngine.MOD17;
            int[] iArr2 = ThreefishEngine.MOD3;
            if (jArr3.length != 33) {
                throw new IllegalArgumentException();
            }
            if (jArr4.length != 5) {
                throw new IllegalArgumentException();
            }
            long j = jArr[0];
            long jXorRotr = jArr[1];
            long j2 = jArr[2];
            long jXorRotr2 = jArr[3];
            long j3 = jArr[4];
            long jXorRotr3 = jArr[5];
            long j4 = jArr[6];
            long jXorRotr4 = jArr[7];
            long j5 = jArr[8];
            long jXorRotr5 = jArr[9];
            long j6 = jArr[10];
            long jXorRotr6 = jArr[11];
            long j7 = jArr[12];
            long jXorRotr7 = jArr[13];
            long j8 = jArr[14];
            long jXorRotr8 = jArr[15];
            for (int i = 19; i >= 1; i -= 2) {
                int i2 = iArr[i];
                int i3 = iArr2[i];
                long j9 = j - jArr3[i2 + 1];
                long j10 = jXorRotr - jArr3[i2 + 2];
                long j11 = j2 - jArr3[i2 + 3];
                long j12 = jXorRotr2 - jArr3[i2 + 4];
                long j13 = j3 - jArr3[i2 + 5];
                long j14 = jXorRotr3 - jArr3[i2 + 6];
                long j15 = j4 - jArr3[i2 + 7];
                long j16 = jXorRotr4 - jArr3[i2 + 8];
                long j17 = j5 - jArr3[i2 + 9];
                long j18 = jXorRotr5 - jArr3[i2 + 10];
                long j19 = j6 - jArr3[i2 + 11];
                long j20 = jXorRotr6 - jArr3[i2 + 12];
                long j21 = j7 - jArr3[i2 + 13];
                long j22 = jXorRotr7 - (jArr3[i2 + 14] + jArr4[i3 + 1]);
                long j23 = j8 - (jArr3[i2 + 15] + jArr4[i3 + 2]);
                long jXorRotr9 = ThreefishEngine.xorRotr(jXorRotr8 - ((jArr3[i2 + 16] + ((long) i)) + 1), 9, j9);
                long j24 = j9 - jXorRotr9;
                long jXorRotr10 = ThreefishEngine.xorRotr(j20, 48, j11);
                long j25 = j11 - jXorRotr10;
                long jXorRotr11 = ThreefishEngine.xorRotr(j22, 35, j15);
                long j26 = j15 - jXorRotr11;
                long jXorRotr12 = ThreefishEngine.xorRotr(j18, 52, j13);
                long j27 = j13 - jXorRotr12;
                long jXorRotr13 = ThreefishEngine.xorRotr(j10, 23, j23);
                long j28 = j23 - jXorRotr13;
                long jXorRotr14 = ThreefishEngine.xorRotr(j14, 31, j17);
                long j29 = j17 - jXorRotr14;
                long jXorRotr15 = ThreefishEngine.xorRotr(j12, 37, j19);
                long j30 = j19 - jXorRotr15;
                long jXorRotr16 = ThreefishEngine.xorRotr(j16, 20, j21);
                long j31 = j21 - jXorRotr16;
                long jXorRotr17 = ThreefishEngine.xorRotr(jXorRotr16, 31, j24);
                long j32 = j24 - jXorRotr17;
                long jXorRotr18 = ThreefishEngine.xorRotr(jXorRotr14, 44, j25);
                long j33 = j25 - jXorRotr18;
                long jXorRotr19 = ThreefishEngine.xorRotr(jXorRotr15, 47, j27);
                long j34 = j27 - jXorRotr19;
                long jXorRotr20 = ThreefishEngine.xorRotr(jXorRotr13, 46, j26);
                long j35 = j26 - jXorRotr20;
                long jXorRotr21 = ThreefishEngine.xorRotr(jXorRotr9, 19, j31);
                long j36 = j31 - jXorRotr21;
                long jXorRotr22 = ThreefishEngine.xorRotr(jXorRotr11, 42, j28);
                long j37 = j28 - jXorRotr22;
                long jXorRotr23 = ThreefishEngine.xorRotr(jXorRotr10, 44, j29);
                long j38 = j29 - jXorRotr23;
                long jXorRotr24 = ThreefishEngine.xorRotr(jXorRotr12, 25, j30);
                long j39 = j30 - jXorRotr24;
                long jXorRotr25 = ThreefishEngine.xorRotr(jXorRotr24, 16, j32);
                long j40 = j32 - jXorRotr25;
                long jXorRotr26 = ThreefishEngine.xorRotr(jXorRotr22, 34, j33);
                long j41 = j33 - jXorRotr26;
                long jXorRotr27 = ThreefishEngine.xorRotr(jXorRotr23, 56, j35);
                long j42 = j35 - jXorRotr27;
                long jXorRotr28 = ThreefishEngine.xorRotr(jXorRotr21, 51, j34);
                long j43 = j34 - jXorRotr28;
                long jXorRotr29 = ThreefishEngine.xorRotr(jXorRotr17, 4, j39);
                long j44 = j39 - jXorRotr29;
                long jXorRotr30 = ThreefishEngine.xorRotr(jXorRotr19, 53, j36);
                long j45 = j36 - jXorRotr30;
                long jXorRotr31 = ThreefishEngine.xorRotr(jXorRotr18, 42, j37);
                long j46 = j37 - jXorRotr31;
                long jXorRotr32 = ThreefishEngine.xorRotr(jXorRotr20, 41, j38);
                long j47 = j38 - jXorRotr32;
                long jXorRotr33 = ThreefishEngine.xorRotr(jXorRotr32, 41, j40);
                long j48 = j40 - jXorRotr33;
                long jXorRotr34 = ThreefishEngine.xorRotr(jXorRotr30, 9, j41);
                long j49 = j41 - jXorRotr34;
                long jXorRotr35 = ThreefishEngine.xorRotr(jXorRotr31, 37, j43);
                long j50 = j43 - jXorRotr35;
                long jXorRotr36 = ThreefishEngine.xorRotr(jXorRotr29, 31, j42);
                long j51 = j42 - jXorRotr36;
                long jXorRotr37 = ThreefishEngine.xorRotr(jXorRotr25, 12, j47);
                long j52 = j47 - jXorRotr37;
                long jXorRotr38 = ThreefishEngine.xorRotr(jXorRotr27, 47, j44);
                long j53 = j44 - jXorRotr38;
                long jXorRotr39 = ThreefishEngine.xorRotr(jXorRotr26, 44, j45);
                long j54 = j45 - jXorRotr39;
                long jXorRotr40 = ThreefishEngine.xorRotr(jXorRotr28, 30, j46);
                long j55 = j46 - jXorRotr40;
                long j56 = j48 - jArr3[i2];
                long j57 = jXorRotr33 - jArr3[i2 + 1];
                long j58 = j49 - jArr3[i2 + 2];
                long j59 = jXorRotr34 - jArr3[i2 + 3];
                long j60 = j50 - jArr3[i2 + 4];
                long j61 = jXorRotr35 - jArr3[i2 + 5];
                long j62 = j51 - jArr3[i2 + 6];
                long j63 = jXorRotr36 - jArr3[i2 + 7];
                long j64 = j52 - jArr3[i2 + 8];
                long j65 = jXorRotr37 - jArr3[i2 + 9];
                long j66 = j53 - jArr3[i2 + 10];
                long j67 = jXorRotr38 - jArr3[i2 + 11];
                long j68 = j54 - jArr3[i2 + 12];
                long j69 = jXorRotr39 - (jArr3[i2 + 13] + jArr4[i3]);
                long j70 = j55 - (jArr3[i2 + 14] + jArr4[i3 + 1]);
                long jXorRotr41 = ThreefishEngine.xorRotr(jXorRotr40 - (jArr3[i2 + 15] + ((long) i)), 5, j56);
                long j71 = j56 - jXorRotr41;
                long jXorRotr42 = ThreefishEngine.xorRotr(j67, 20, j58);
                long j72 = j58 - jXorRotr42;
                long jXorRotr43 = ThreefishEngine.xorRotr(j69, 48, j62);
                long j73 = j62 - jXorRotr43;
                long jXorRotr44 = ThreefishEngine.xorRotr(j65, 41, j60);
                long j74 = j60 - jXorRotr44;
                long jXorRotr45 = ThreefishEngine.xorRotr(j57, 47, j70);
                long j75 = j70 - jXorRotr45;
                long jXorRotr46 = ThreefishEngine.xorRotr(j61, 28, j64);
                long j76 = j64 - jXorRotr46;
                long jXorRotr47 = ThreefishEngine.xorRotr(j59, 16, j66);
                long j77 = j66 - jXorRotr47;
                long jXorRotr48 = ThreefishEngine.xorRotr(j63, 25, j68);
                long j78 = j68 - jXorRotr48;
                long jXorRotr49 = ThreefishEngine.xorRotr(jXorRotr48, 33, j71);
                long j79 = j71 - jXorRotr49;
                long jXorRotr50 = ThreefishEngine.xorRotr(jXorRotr46, 4, j72);
                long j80 = j72 - jXorRotr50;
                long jXorRotr51 = ThreefishEngine.xorRotr(jXorRotr47, 51, j74);
                long j81 = j74 - jXorRotr51;
                long jXorRotr52 = ThreefishEngine.xorRotr(jXorRotr45, 13, j73);
                long j82 = j73 - jXorRotr52;
                long jXorRotr53 = ThreefishEngine.xorRotr(jXorRotr41, 34, j78);
                long j83 = j78 - jXorRotr53;
                long jXorRotr54 = ThreefishEngine.xorRotr(jXorRotr43, 41, j75);
                long j84 = j75 - jXorRotr54;
                long jXorRotr55 = ThreefishEngine.xorRotr(jXorRotr42, 59, j76);
                long j85 = j76 - jXorRotr55;
                long jXorRotr56 = ThreefishEngine.xorRotr(jXorRotr44, 17, j77);
                long j86 = j77 - jXorRotr56;
                long jXorRotr57 = ThreefishEngine.xorRotr(jXorRotr56, 38, j79);
                long j87 = j79 - jXorRotr57;
                long jXorRotr58 = ThreefishEngine.xorRotr(jXorRotr54, 19, j80);
                long j88 = j80 - jXorRotr58;
                long jXorRotr59 = ThreefishEngine.xorRotr(jXorRotr55, 10, j82);
                long j89 = j82 - jXorRotr59;
                long jXorRotr60 = ThreefishEngine.xorRotr(jXorRotr53, 55, j81);
                long j90 = j81 - jXorRotr60;
                long jXorRotr61 = ThreefishEngine.xorRotr(jXorRotr49, 49, j86);
                long j91 = j86 - jXorRotr61;
                long jXorRotr62 = ThreefishEngine.xorRotr(jXorRotr51, 18, j83);
                long j92 = j83 - jXorRotr62;
                long jXorRotr63 = ThreefishEngine.xorRotr(jXorRotr50, 23, j84);
                long j93 = j84 - jXorRotr63;
                long jXorRotr64 = ThreefishEngine.xorRotr(jXorRotr52, 52, j85);
                long j94 = j85 - jXorRotr64;
                jXorRotr = ThreefishEngine.xorRotr(jXorRotr64, 24, j87);
                j = j87 - jXorRotr;
                jXorRotr2 = ThreefishEngine.xorRotr(jXorRotr62, 13, j88);
                j2 = j88 - jXorRotr2;
                jXorRotr3 = ThreefishEngine.xorRotr(jXorRotr63, 8, j90);
                j3 = j90 - jXorRotr3;
                jXorRotr4 = ThreefishEngine.xorRotr(jXorRotr61, 47, j89);
                j4 = j89 - jXorRotr4;
                jXorRotr5 = ThreefishEngine.xorRotr(jXorRotr57, 8, j94);
                j5 = j94 - jXorRotr5;
                jXorRotr6 = ThreefishEngine.xorRotr(jXorRotr59, 17, j91);
                j6 = j91 - jXorRotr6;
                jXorRotr7 = ThreefishEngine.xorRotr(jXorRotr58, 22, j92);
                j7 = j92 - jXorRotr7;
                jXorRotr8 = ThreefishEngine.xorRotr(jXorRotr60, 37, j93);
                j8 = j93 - jXorRotr8;
            }
            long j95 = j - jArr3[0];
            long j96 = jXorRotr - jArr3[1];
            long j97 = j2 - jArr3[2];
            long j98 = jXorRotr2 - jArr3[3];
            long j99 = j3 - jArr3[4];
            long j100 = jXorRotr3 - jArr3[5];
            long j101 = j4 - jArr3[6];
            long j102 = jXorRotr4 - jArr3[7];
            long j103 = j5 - jArr3[8];
            long j104 = jXorRotr5 - jArr3[9];
            long j105 = j6 - jArr3[10];
            long j106 = jXorRotr6 - jArr3[11];
            long j107 = j7 - jArr3[12];
            long j108 = jXorRotr7 - (jArr3[13] + jArr4[0]);
            long j109 = j8 - (jArr3[14] + jArr4[1]);
            long j110 = jXorRotr8 - jArr3[15];
            jArr2[0] = j95;
            jArr2[1] = j96;
            jArr2[2] = j97;
            jArr2[3] = j98;
            jArr2[4] = j99;
            jArr2[5] = j100;
            jArr2[6] = j101;
            jArr2[7] = j102;
            jArr2[8] = j103;
            jArr2[9] = j104;
            jArr2[10] = j105;
            jArr2[11] = j106;
            jArr2[12] = j107;
            jArr2[13] = j108;
            jArr2[14] = j109;
            jArr2[15] = j110;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ThreefishEngine$Threefish256Cipher.class */
    private static final class Threefish256Cipher extends ThreefishCipher {
        private static final int ROTATION_0_0 = 14;
        private static final int ROTATION_0_1 = 16;
        private static final int ROTATION_1_0 = 52;
        private static final int ROTATION_1_1 = 57;
        private static final int ROTATION_2_0 = 23;
        private static final int ROTATION_2_1 = 40;
        private static final int ROTATION_3_0 = 5;
        private static final int ROTATION_3_1 = 37;
        private static final int ROTATION_4_0 = 25;
        private static final int ROTATION_4_1 = 33;
        private static final int ROTATION_5_0 = 46;
        private static final int ROTATION_5_1 = 12;
        private static final int ROTATION_6_0 = 58;
        private static final int ROTATION_6_1 = 22;
        private static final int ROTATION_7_0 = 32;
        private static final int ROTATION_7_1 = 32;

        public Threefish256Cipher(long[] jArr, long[] jArr2) {
            super(jArr, jArr2);
        }

        @Override // org.bouncycastle.crypto.engines.ThreefishEngine.ThreefishCipher
        void encryptBlock(long[] jArr, long[] jArr2) {
            long[] jArr3 = this.kw;
            long[] jArr4 = this.t;
            int[] iArr = ThreefishEngine.MOD5;
            int[] iArr2 = ThreefishEngine.MOD3;
            if (jArr3.length != 9) {
                throw new IllegalArgumentException();
            }
            if (jArr4.length != 5) {
                throw new IllegalArgumentException();
            }
            long j = jArr[0];
            long j2 = jArr[1];
            long j3 = jArr[2];
            long j4 = jArr[3];
            long j5 = j + jArr3[0];
            long j6 = j2 + jArr3[1] + jArr4[0];
            long j7 = j3 + jArr3[2] + jArr4[1];
            long j8 = j4 + jArr3[3];
            for (int i = 1; i < 18; i += 2) {
                int i2 = iArr[i];
                int i3 = iArr2[i];
                long j9 = j6;
                long jRotlXor = ThreefishEngine.rotlXor(j9, 14, j5 + j6);
                long j10 = j7 + j8;
                long jRotlXor2 = ThreefishEngine.rotlXor(j8, 16, j10);
                long j11 = j9 + jRotlXor2;
                long jRotlXor3 = ThreefishEngine.rotlXor(jRotlXor2, 52, j11);
                long j12 = j10 + jRotlXor;
                long jRotlXor4 = ThreefishEngine.rotlXor(jRotlXor, 57, j12);
                long j13 = j11 + jRotlXor4;
                long jRotlXor5 = ThreefishEngine.rotlXor(jRotlXor4, 23, j13);
                long j14 = j12 + jRotlXor3;
                long jRotlXor6 = ThreefishEngine.rotlXor(jRotlXor3, 40, j14);
                long j15 = j13 + jRotlXor6;
                long jRotlXor7 = ThreefishEngine.rotlXor(jRotlXor6, 5, j15);
                long j16 = j14 + jRotlXor5;
                long jRotlXor8 = ThreefishEngine.rotlXor(jRotlXor5, 37, j16);
                long j17 = j15 + jArr3[i2];
                long j18 = jRotlXor8 + jArr3[i2 + 1] + jArr4[i3];
                long j19 = j16 + jArr3[i2 + 2] + jArr4[i3 + 1];
                long j20 = jRotlXor7 + jArr3[i2 + 3] + ((long) i);
                long j21 = j17 + j18;
                long jRotlXor9 = ThreefishEngine.rotlXor(j18, 25, j21);
                long j22 = j19 + j20;
                long jRotlXor10 = ThreefishEngine.rotlXor(j20, 33, j22);
                long j23 = j21 + jRotlXor10;
                long jRotlXor11 = ThreefishEngine.rotlXor(jRotlXor10, 46, j23);
                long j24 = j22 + jRotlXor9;
                long jRotlXor12 = ThreefishEngine.rotlXor(jRotlXor9, 12, j24);
                long j25 = j23 + jRotlXor12;
                long jRotlXor13 = ThreefishEngine.rotlXor(jRotlXor12, 58, j25);
                long j26 = j24 + jRotlXor11;
                long jRotlXor14 = ThreefishEngine.rotlXor(jRotlXor11, 22, j26);
                long j27 = j25 + jRotlXor14;
                long jRotlXor15 = ThreefishEngine.rotlXor(jRotlXor14, 32, j27);
                long j28 = j26 + jRotlXor13;
                long jRotlXor16 = ThreefishEngine.rotlXor(jRotlXor13, 32, j28);
                j5 = j27 + jArr3[i2 + 1];
                j6 = jRotlXor16 + jArr3[i2 + 2] + jArr4[i3 + 1];
                j7 = j28 + jArr3[i2 + 3] + jArr4[i3 + 2];
                j8 = jRotlXor15 + jArr3[i2 + 4] + ((long) i) + 1;
            }
            jArr2[0] = j5;
            jArr2[1] = j6;
            jArr2[2] = j7;
            jArr2[3] = j8;
        }

        @Override // org.bouncycastle.crypto.engines.ThreefishEngine.ThreefishCipher
        void decryptBlock(long[] jArr, long[] jArr2) {
            long[] jArr3 = this.kw;
            long[] jArr4 = this.t;
            int[] iArr = ThreefishEngine.MOD5;
            int[] iArr2 = ThreefishEngine.MOD3;
            if (jArr3.length != 9) {
                throw new IllegalArgumentException();
            }
            if (jArr4.length != 5) {
                throw new IllegalArgumentException();
            }
            long j = jArr[0];
            long jXorRotr = jArr[1];
            long j2 = jArr[2];
            long jXorRotr2 = jArr[3];
            for (int i = 17; i >= 1; i -= 2) {
                int i2 = iArr[i];
                int i3 = iArr2[i];
                long j3 = j - jArr3[i2 + 1];
                long j4 = jXorRotr - (jArr3[i2 + 2] + jArr4[i3 + 1]);
                long j5 = j2 - (jArr3[i2 + 3] + jArr4[i3 + 2]);
                long jXorRotr3 = ThreefishEngine.xorRotr(jXorRotr2 - ((jArr3[i2 + 4] + ((long) i)) + 1), 32, j3);
                long j6 = j3 - jXorRotr3;
                long jXorRotr4 = ThreefishEngine.xorRotr(j4, 32, j5);
                long j7 = j5 - jXorRotr4;
                long jXorRotr5 = ThreefishEngine.xorRotr(jXorRotr4, 58, j6);
                long j8 = j6 - jXorRotr5;
                long jXorRotr6 = ThreefishEngine.xorRotr(jXorRotr3, 22, j7);
                long j9 = j7 - jXorRotr6;
                long jXorRotr7 = ThreefishEngine.xorRotr(jXorRotr6, 46, j8);
                long j10 = j8 - jXorRotr7;
                long jXorRotr8 = ThreefishEngine.xorRotr(jXorRotr5, 12, j9);
                long j11 = j9 - jXorRotr8;
                long jXorRotr9 = ThreefishEngine.xorRotr(jXorRotr8, 25, j10);
                long j12 = j10 - jXorRotr9;
                long jXorRotr10 = ThreefishEngine.xorRotr(jXorRotr7, 33, j11);
                long j13 = j11 - jXorRotr10;
                long j14 = j12 - jArr3[i2];
                long j15 = jXorRotr9 - (jArr3[i2 + 1] + jArr4[i3]);
                long j16 = j13 - (jArr3[i2 + 2] + jArr4[i3 + 1]);
                long jXorRotr11 = ThreefishEngine.xorRotr(jXorRotr10 - (jArr3[i2 + 3] + ((long) i)), 5, j14);
                long j17 = j14 - jXorRotr11;
                long jXorRotr12 = ThreefishEngine.xorRotr(j15, 37, j16);
                long j18 = j16 - jXorRotr12;
                long jXorRotr13 = ThreefishEngine.xorRotr(jXorRotr12, 23, j17);
                long j19 = j17 - jXorRotr13;
                long jXorRotr14 = ThreefishEngine.xorRotr(jXorRotr11, 40, j18);
                long j20 = j18 - jXorRotr14;
                long jXorRotr15 = ThreefishEngine.xorRotr(jXorRotr14, 52, j19);
                long j21 = j19 - jXorRotr15;
                long jXorRotr16 = ThreefishEngine.xorRotr(jXorRotr13, 57, j20);
                long j22 = j20 - jXorRotr16;
                jXorRotr = ThreefishEngine.xorRotr(jXorRotr16, 14, j21);
                j = j21 - jXorRotr;
                jXorRotr2 = ThreefishEngine.xorRotr(jXorRotr15, 16, j22);
                j2 = j22 - jXorRotr2;
            }
            long j23 = j - jArr3[0];
            long j24 = jXorRotr - (jArr3[1] + jArr4[0]);
            long j25 = j2 - (jArr3[2] + jArr4[1]);
            long j26 = jXorRotr2 - jArr3[3];
            jArr2[0] = j23;
            jArr2[1] = j24;
            jArr2[2] = j25;
            jArr2[3] = j26;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ThreefishEngine$Threefish512Cipher.class */
    private static final class Threefish512Cipher extends ThreefishCipher {
        private static final int ROTATION_0_0 = 46;
        private static final int ROTATION_0_1 = 36;
        private static final int ROTATION_0_2 = 19;
        private static final int ROTATION_0_3 = 37;
        private static final int ROTATION_1_0 = 33;
        private static final int ROTATION_1_1 = 27;
        private static final int ROTATION_1_2 = 14;
        private static final int ROTATION_1_3 = 42;
        private static final int ROTATION_2_0 = 17;
        private static final int ROTATION_2_1 = 49;
        private static final int ROTATION_2_2 = 36;
        private static final int ROTATION_2_3 = 39;
        private static final int ROTATION_3_0 = 44;
        private static final int ROTATION_3_1 = 9;
        private static final int ROTATION_3_2 = 54;
        private static final int ROTATION_3_3 = 56;
        private static final int ROTATION_4_0 = 39;
        private static final int ROTATION_4_1 = 30;
        private static final int ROTATION_4_2 = 34;
        private static final int ROTATION_4_3 = 24;
        private static final int ROTATION_5_0 = 13;
        private static final int ROTATION_5_1 = 50;
        private static final int ROTATION_5_2 = 10;
        private static final int ROTATION_5_3 = 17;
        private static final int ROTATION_6_0 = 25;
        private static final int ROTATION_6_1 = 29;
        private static final int ROTATION_6_2 = 39;
        private static final int ROTATION_6_3 = 43;
        private static final int ROTATION_7_0 = 8;
        private static final int ROTATION_7_1 = 35;
        private static final int ROTATION_7_2 = 56;
        private static final int ROTATION_7_3 = 22;

        protected Threefish512Cipher(long[] jArr, long[] jArr2) {
            super(jArr, jArr2);
        }

        @Override // org.bouncycastle.crypto.engines.ThreefishEngine.ThreefishCipher
        public void encryptBlock(long[] jArr, long[] jArr2) {
            long[] jArr3 = this.kw;
            long[] jArr4 = this.t;
            int[] iArr = ThreefishEngine.MOD9;
            int[] iArr2 = ThreefishEngine.MOD3;
            if (jArr3.length != 17) {
                throw new IllegalArgumentException();
            }
            if (jArr4.length != 5) {
                throw new IllegalArgumentException();
            }
            long j = jArr[0];
            long j2 = jArr[1];
            long j3 = jArr[2];
            long j4 = jArr[3];
            long j5 = jArr[4];
            long j6 = jArr[5];
            long j7 = jArr[6];
            long j8 = jArr[7];
            long j9 = j + jArr3[0];
            long j10 = j2 + jArr3[1];
            long j11 = j3 + jArr3[2];
            long j12 = j4 + jArr3[3];
            long j13 = j5 + jArr3[4];
            long j14 = j6 + jArr3[5] + jArr4[0];
            long j15 = j7 + jArr3[6] + jArr4[1];
            long j16 = j8 + jArr3[7];
            for (int i = 1; i < 18; i += 2) {
                int i2 = iArr[i];
                int i3 = iArr2[i];
                long j17 = j10;
                long jRotlXor = ThreefishEngine.rotlXor(j17, 46, j9 + j10);
                long j18 = j11 + j12;
                long jRotlXor2 = ThreefishEngine.rotlXor(j12, 36, j18);
                long j19 = j13 + j14;
                long jRotlXor3 = ThreefishEngine.rotlXor(j14, 19, j19);
                long j20 = j15 + j16;
                long jRotlXor4 = ThreefishEngine.rotlXor(j16, 37, j20);
                long j21 = j18 + jRotlXor;
                long jRotlXor5 = ThreefishEngine.rotlXor(jRotlXor, 33, j21);
                long j22 = j19 + jRotlXor4;
                long jRotlXor6 = ThreefishEngine.rotlXor(jRotlXor4, 27, j22);
                long j23 = j20 + jRotlXor3;
                long jRotlXor7 = ThreefishEngine.rotlXor(jRotlXor3, 14, j23);
                long j24 = j17 + jRotlXor2;
                long jRotlXor8 = ThreefishEngine.rotlXor(jRotlXor2, 42, j24);
                long j25 = j22 + jRotlXor5;
                long jRotlXor9 = ThreefishEngine.rotlXor(jRotlXor5, 17, j25);
                long j26 = j23 + jRotlXor8;
                long jRotlXor10 = ThreefishEngine.rotlXor(jRotlXor8, 49, j26);
                long j27 = j24 + jRotlXor7;
                long jRotlXor11 = ThreefishEngine.rotlXor(jRotlXor7, 36, j27);
                long j28 = j21 + jRotlXor6;
                long jRotlXor12 = ThreefishEngine.rotlXor(jRotlXor6, 39, j28);
                long j29 = j26 + jRotlXor9;
                long jRotlXor13 = ThreefishEngine.rotlXor(jRotlXor9, 44, j29);
                long j30 = j27 + jRotlXor12;
                long jRotlXor14 = ThreefishEngine.rotlXor(jRotlXor12, 9, j30);
                long j31 = j28 + jRotlXor11;
                long jRotlXor15 = ThreefishEngine.rotlXor(jRotlXor11, 54, j31);
                long j32 = j25 + jRotlXor10;
                long jRotlXor16 = ThreefishEngine.rotlXor(jRotlXor10, 56, j32);
                long j33 = j30 + jArr3[i2];
                long j34 = jRotlXor13 + jArr3[i2 + 1];
                long j35 = j31 + jArr3[i2 + 2];
                long j36 = jRotlXor16 + jArr3[i2 + 3];
                long j37 = j32 + jArr3[i2 + 4];
                long j38 = jRotlXor15 + jArr3[i2 + 5] + jArr4[i3];
                long j39 = j29 + jArr3[i2 + 6] + jArr4[i3 + 1];
                long j40 = jRotlXor14 + jArr3[i2 + 7] + ((long) i);
                long j41 = j33 + j34;
                long jRotlXor17 = ThreefishEngine.rotlXor(j34, 39, j41);
                long j42 = j35 + j36;
                long jRotlXor18 = ThreefishEngine.rotlXor(j36, 30, j42);
                long j43 = j37 + j38;
                long jRotlXor19 = ThreefishEngine.rotlXor(j38, 34, j43);
                long j44 = j39 + j40;
                long jRotlXor20 = ThreefishEngine.rotlXor(j40, 24, j44);
                long j45 = j42 + jRotlXor17;
                long jRotlXor21 = ThreefishEngine.rotlXor(jRotlXor17, 13, j45);
                long j46 = j43 + jRotlXor20;
                long jRotlXor22 = ThreefishEngine.rotlXor(jRotlXor20, 50, j46);
                long j47 = j44 + jRotlXor19;
                long jRotlXor23 = ThreefishEngine.rotlXor(jRotlXor19, 10, j47);
                long j48 = j41 + jRotlXor18;
                long jRotlXor24 = ThreefishEngine.rotlXor(jRotlXor18, 17, j48);
                long j49 = j46 + jRotlXor21;
                long jRotlXor25 = ThreefishEngine.rotlXor(jRotlXor21, 25, j49);
                long j50 = j47 + jRotlXor24;
                long jRotlXor26 = ThreefishEngine.rotlXor(jRotlXor24, 29, j50);
                long j51 = j48 + jRotlXor23;
                long jRotlXor27 = ThreefishEngine.rotlXor(jRotlXor23, 39, j51);
                long j52 = j45 + jRotlXor22;
                long jRotlXor28 = ThreefishEngine.rotlXor(jRotlXor22, 43, j52);
                long j53 = j50 + jRotlXor25;
                long jRotlXor29 = ThreefishEngine.rotlXor(jRotlXor25, 8, j53);
                long j54 = j51 + jRotlXor28;
                long jRotlXor30 = ThreefishEngine.rotlXor(jRotlXor28, 35, j54);
                long j55 = j52 + jRotlXor27;
                long jRotlXor31 = ThreefishEngine.rotlXor(jRotlXor27, 56, j55);
                long j56 = j49 + jRotlXor26;
                long jRotlXor32 = ThreefishEngine.rotlXor(jRotlXor26, 22, j56);
                j9 = j54 + jArr3[i2 + 1];
                j10 = jRotlXor29 + jArr3[i2 + 2];
                j11 = j55 + jArr3[i2 + 3];
                j12 = jRotlXor32 + jArr3[i2 + 4];
                j13 = j56 + jArr3[i2 + 5];
                j14 = jRotlXor31 + jArr3[i2 + 6] + jArr4[i3 + 1];
                j15 = j53 + jArr3[i2 + 7] + jArr4[i3 + 2];
                j16 = jRotlXor30 + jArr3[i2 + 8] + ((long) i) + 1;
            }
            jArr2[0] = j9;
            jArr2[1] = j10;
            jArr2[2] = j11;
            jArr2[3] = j12;
            jArr2[4] = j13;
            jArr2[5] = j14;
            jArr2[6] = j15;
            jArr2[7] = j16;
        }

        @Override // org.bouncycastle.crypto.engines.ThreefishEngine.ThreefishCipher
        public void decryptBlock(long[] jArr, long[] jArr2) {
            long[] jArr3 = this.kw;
            long[] jArr4 = this.t;
            int[] iArr = ThreefishEngine.MOD9;
            int[] iArr2 = ThreefishEngine.MOD3;
            if (jArr3.length != 17) {
                throw new IllegalArgumentException();
            }
            if (jArr4.length != 5) {
                throw new IllegalArgumentException();
            }
            long j = jArr[0];
            long jXorRotr = jArr[1];
            long j2 = jArr[2];
            long jXorRotr2 = jArr[3];
            long j3 = jArr[4];
            long jXorRotr3 = jArr[5];
            long j4 = jArr[6];
            long jXorRotr4 = jArr[7];
            for (int i = 17; i >= 1; i -= 2) {
                int i2 = iArr[i];
                int i3 = iArr2[i];
                long j5 = j - jArr3[i2 + 1];
                long j6 = jXorRotr - jArr3[i2 + 2];
                long j7 = j2 - jArr3[i2 + 3];
                long j8 = jXorRotr2 - jArr3[i2 + 4];
                long j9 = j3 - jArr3[i2 + 5];
                long j10 = jXorRotr3 - (jArr3[i2 + 6] + jArr4[i3 + 1]);
                long j11 = j4 - (jArr3[i2 + 7] + jArr4[i3 + 2]);
                long j12 = jXorRotr4 - ((jArr3[i2 + 8] + ((long) i)) + 1);
                long jXorRotr5 = ThreefishEngine.xorRotr(j6, 8, j11);
                long j13 = j11 - jXorRotr5;
                long jXorRotr6 = ThreefishEngine.xorRotr(j12, 35, j5);
                long j14 = j5 - jXorRotr6;
                long jXorRotr7 = ThreefishEngine.xorRotr(j10, 56, j7);
                long j15 = j7 - jXorRotr7;
                long jXorRotr8 = ThreefishEngine.xorRotr(j8, 22, j9);
                long j16 = j9 - jXorRotr8;
                long jXorRotr9 = ThreefishEngine.xorRotr(jXorRotr5, 25, j16);
                long j17 = j16 - jXorRotr9;
                long jXorRotr10 = ThreefishEngine.xorRotr(jXorRotr8, 29, j13);
                long j18 = j13 - jXorRotr10;
                long jXorRotr11 = ThreefishEngine.xorRotr(jXorRotr7, 39, j14);
                long j19 = j14 - jXorRotr11;
                long jXorRotr12 = ThreefishEngine.xorRotr(jXorRotr6, 43, j15);
                long j20 = j15 - jXorRotr12;
                long jXorRotr13 = ThreefishEngine.xorRotr(jXorRotr9, 13, j20);
                long j21 = j20 - jXorRotr13;
                long jXorRotr14 = ThreefishEngine.xorRotr(jXorRotr12, 50, j17);
                long j22 = j17 - jXorRotr14;
                long jXorRotr15 = ThreefishEngine.xorRotr(jXorRotr11, 10, j18);
                long j23 = j18 - jXorRotr15;
                long jXorRotr16 = ThreefishEngine.xorRotr(jXorRotr10, 17, j19);
                long j24 = j19 - jXorRotr16;
                long jXorRotr17 = ThreefishEngine.xorRotr(jXorRotr13, 39, j24);
                long j25 = j24 - jXorRotr17;
                long jXorRotr18 = ThreefishEngine.xorRotr(jXorRotr16, 30, j21);
                long j26 = j21 - jXorRotr18;
                long jXorRotr19 = ThreefishEngine.xorRotr(jXorRotr15, 34, j22);
                long j27 = j22 - jXorRotr19;
                long jXorRotr20 = ThreefishEngine.xorRotr(jXorRotr14, 24, j23);
                long j28 = j23 - jXorRotr20;
                long j29 = j25 - jArr3[i2];
                long j30 = jXorRotr17 - jArr3[i2 + 1];
                long j31 = j26 - jArr3[i2 + 2];
                long j32 = jXorRotr18 - jArr3[i2 + 3];
                long j33 = j27 - jArr3[i2 + 4];
                long j34 = jXorRotr19 - (jArr3[i2 + 5] + jArr4[i3]);
                long j35 = j28 - (jArr3[i2 + 6] + jArr4[i3 + 1]);
                long j36 = jXorRotr20 - (jArr3[i2 + 7] + ((long) i));
                long jXorRotr21 = ThreefishEngine.xorRotr(j30, 44, j35);
                long j37 = j35 - jXorRotr21;
                long jXorRotr22 = ThreefishEngine.xorRotr(j36, 9, j29);
                long j38 = j29 - jXorRotr22;
                long jXorRotr23 = ThreefishEngine.xorRotr(j34, 54, j31);
                long j39 = j31 - jXorRotr23;
                long jXorRotr24 = ThreefishEngine.xorRotr(j32, 56, j33);
                long j40 = j33 - jXorRotr24;
                long jXorRotr25 = ThreefishEngine.xorRotr(jXorRotr21, 17, j40);
                long j41 = j40 - jXorRotr25;
                long jXorRotr26 = ThreefishEngine.xorRotr(jXorRotr24, 49, j37);
                long j42 = j37 - jXorRotr26;
                long jXorRotr27 = ThreefishEngine.xorRotr(jXorRotr23, 36, j38);
                long j43 = j38 - jXorRotr27;
                long jXorRotr28 = ThreefishEngine.xorRotr(jXorRotr22, 39, j39);
                long j44 = j39 - jXorRotr28;
                long jXorRotr29 = ThreefishEngine.xorRotr(jXorRotr25, 33, j44);
                long j45 = j44 - jXorRotr29;
                long jXorRotr30 = ThreefishEngine.xorRotr(jXorRotr28, 27, j41);
                long j46 = j41 - jXorRotr30;
                long jXorRotr31 = ThreefishEngine.xorRotr(jXorRotr27, 14, j42);
                long j47 = j42 - jXorRotr31;
                long jXorRotr32 = ThreefishEngine.xorRotr(jXorRotr26, 42, j43);
                long j48 = j43 - jXorRotr32;
                jXorRotr = ThreefishEngine.xorRotr(jXorRotr29, 46, j48);
                j = j48 - jXorRotr;
                jXorRotr2 = ThreefishEngine.xorRotr(jXorRotr32, 36, j45);
                j2 = j45 - jXorRotr2;
                jXorRotr3 = ThreefishEngine.xorRotr(jXorRotr31, 19, j46);
                j3 = j46 - jXorRotr3;
                jXorRotr4 = ThreefishEngine.xorRotr(jXorRotr30, 37, j47);
                j4 = j47 - jXorRotr4;
            }
            long j49 = j - jArr3[0];
            long j50 = jXorRotr - jArr3[1];
            long j51 = j2 - jArr3[2];
            long j52 = jXorRotr2 - jArr3[3];
            long j53 = j3 - jArr3[4];
            long j54 = jXorRotr3 - (jArr3[5] + jArr4[0]);
            long j55 = j4 - (jArr3[6] + jArr4[1]);
            long j56 = jXorRotr4 - jArr3[7];
            jArr2[0] = j49;
            jArr2[1] = j50;
            jArr2[2] = j51;
            jArr2[3] = j52;
            jArr2[4] = j53;
            jArr2[5] = j54;
            jArr2[6] = j55;
            jArr2[7] = j56;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ThreefishEngine$ThreefishCipher.class */
    private static abstract class ThreefishCipher {
        protected final long[] t;
        protected final long[] kw;

        protected ThreefishCipher(long[] jArr, long[] jArr2) {
            this.kw = jArr;
            this.t = jArr2;
        }

        abstract void encryptBlock(long[] jArr, long[] jArr2);

        abstract void decryptBlock(long[] jArr, long[] jArr2);
    }

    public ThreefishEngine(int i) {
        this.blocksizeBytes = i / 8;
        this.blocksizeWords = this.blocksizeBytes / 8;
        this.currentBlock = new long[this.blocksizeWords];
        this.kw = new long[(2 * this.blocksizeWords) + 1];
        switch (i) {
            case 256:
                this.cipher = new Threefish256Cipher(this.kw, this.t);
                return;
            case 512:
                this.cipher = new Threefish512Cipher(this.kw, this.t);
                return;
            case 1024:
                this.cipher = new Threefish1024Cipher(this.kw, this.t);
                return;
            default:
                throw new IllegalArgumentException("Invalid blocksize - Threefish is defined with block size of 256, 512, or 1024 bits");
        }
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException {
        byte[] key;
        byte[] tweak;
        if (cipherParameters instanceof TweakableBlockCipherParameters) {
            TweakableBlockCipherParameters tweakableBlockCipherParameters = (TweakableBlockCipherParameters) cipherParameters;
            key = tweakableBlockCipherParameters.getKey().getKey();
            tweak = tweakableBlockCipherParameters.getTweak();
        } else {
            if (!(cipherParameters instanceof KeyParameter)) {
                throw new IllegalArgumentException("Invalid parameter passed to Threefish init - " + cipherParameters.getClass().getName());
            }
            key = ((KeyParameter) cipherParameters).getKey();
            tweak = null;
        }
        long[] jArr = null;
        long[] jArr2 = null;
        if (key != null) {
            if (key.length != this.blocksizeBytes) {
                throw new IllegalArgumentException("Threefish key must be same size as block (" + this.blocksizeBytes + " bytes)");
            }
            jArr = new long[this.blocksizeWords];
            Pack.littleEndianToLong(key, 0, jArr);
        }
        if (tweak != null) {
            if (tweak.length != 16) {
                throw new IllegalArgumentException("Threefish tweak must be 16 bytes");
            }
            jArr2 = new long[2];
            Pack.littleEndianToLong(tweak, 0, jArr2);
        }
        init(z, jArr, jArr2);
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), 256, cipherParameters, Utils.getPurpose(z)));
    }

    public void init(boolean z, long[] jArr, long[] jArr2) {
        this.forEncryption = z;
        if (jArr != null) {
            setKey(jArr);
        }
        if (jArr2 != null) {
            setTweak(jArr2);
        }
    }

    private void setKey(long[] jArr) {
        if (jArr.length != this.blocksizeWords) {
            throw new IllegalArgumentException("Threefish key must be same size as block (" + this.blocksizeWords + " words)");
        }
        long j = 2004413935125273122L;
        for (int i = 0; i < this.blocksizeWords; i++) {
            this.kw[i] = jArr[i];
            j ^= this.kw[i];
        }
        this.kw[this.blocksizeWords] = j;
        System.arraycopy(this.kw, 0, this.kw, this.blocksizeWords + 1, this.blocksizeWords);
    }

    private void setTweak(long[] jArr) {
        if (jArr.length != 2) {
            throw new IllegalArgumentException("Tweak must be 2 words.");
        }
        this.t[0] = jArr[0];
        this.t[1] = jArr[1];
        this.t[2] = this.t[0] ^ this.t[1];
        this.t[3] = this.t[0];
        this.t[4] = this.t[1];
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "Threefish-" + (this.blocksizeBytes * 8);
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return this.blocksizeBytes;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void reset() {
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) throws IllegalStateException, DataLengthException {
        if (i + this.blocksizeBytes > bArr.length) {
            throw new DataLengthException("Input buffer too short");
        }
        if (i2 + this.blocksizeBytes > bArr2.length) {
            throw new OutputLengthException("Output buffer too short");
        }
        Pack.littleEndianToLong(bArr, i, this.currentBlock);
        processBlock(this.currentBlock, this.currentBlock);
        Pack.longToLittleEndian(this.currentBlock, bArr2, i2);
        return this.blocksizeBytes;
    }

    public int processBlock(long[] jArr, long[] jArr2) throws IllegalStateException, DataLengthException {
        if (this.kw[this.blocksizeWords] == 0) {
            throw new IllegalStateException("Threefish engine not initialised");
        }
        if (jArr.length != this.blocksizeWords) {
            throw new DataLengthException("Input buffer too short");
        }
        if (jArr2.length != this.blocksizeWords) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (this.forEncryption) {
            this.cipher.encryptBlock(jArr, jArr2);
        } else {
            this.cipher.decryptBlock(jArr, jArr2);
        }
        return this.blocksizeWords;
    }

    public static long bytesToWord(byte[] bArr, int i) {
        return Pack.littleEndianToLong(bArr, i);
    }

    public static void wordToBytes(long j, byte[] bArr, int i) {
        Pack.longToLittleEndian(j, bArr, i);
    }

    static long rotlXor(long j, int i, long j2) {
        return ((j << i) | (j >>> (-i))) ^ j2;
    }

    static long xorRotr(long j, int i, long j2) {
        long j3 = j ^ j2;
        return (j3 >>> i) | (j3 << (-i));
    }

    static {
        for (int i = 0; i < MOD9.length; i++) {
            MOD17[i] = i % 17;
            MOD9[i] = i % 9;
            MOD5[i] = i % 5;
            MOD3[i] = i % 3;
        }
    }
}
