package org.bouncycastle.crypto.engines;

import com.sun.jna.platform.win32.DBT;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ISAPEngine.class */
public class ISAPEngine extends AEADBufferBaseEngine {
    final int ISAP_STATE_SZ = 40;
    private byte[] k;
    private byte[] npub;
    private int ISAP_rH;
    private ISAP_AEAD ISAPAEAD;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ISAPEngine$ISAPAEAD_A.class */
    private abstract class ISAPAEAD_A implements ISAP_AEAD {
        protected long[] k64;
        protected long[] npub64;
        protected long ISAP_IV1_64;
        protected long ISAP_IV2_64;
        protected long ISAP_IV3_64;
        protected long x0;
        protected long x1;
        protected long x2;
        protected long x3;
        protected long x4;
        protected long t0;
        protected long t1;
        protected long t2;
        protected long t3;
        protected long t4;
        protected long macx0;
        protected long macx1;
        protected long macx2;
        protected long macx3;
        protected long macx4;

        public ISAPAEAD_A() {
            ISAPEngine.this.ISAP_rH = 64;
            ISAPEngine.this.BlockSize = (ISAPEngine.this.ISAP_rH + 7) >> 3;
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAP_AEAD
        public void init() {
            this.npub64 = new long[getLongSize(ISAPEngine.this.npub.length)];
            this.k64 = new long[getLongSize(ISAPEngine.this.k.length)];
            Pack.bigEndianToLong(ISAPEngine.this.npub, 0, this.npub64);
            Pack.bigEndianToLong(ISAPEngine.this.k, 0, this.k64);
        }

        protected abstract void PX1();

        protected abstract void PX2();

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAP_AEAD
        public void swapInternalState() {
            this.t0 = this.x0;
            this.t1 = this.x1;
            this.t2 = this.x2;
            this.t3 = this.x3;
            this.t4 = this.x4;
            this.x0 = this.macx0;
            this.x1 = this.macx1;
            this.x2 = this.macx2;
            this.x3 = this.macx3;
            this.x4 = this.macx4;
            this.macx0 = this.t0;
            this.macx1 = this.t1;
            this.macx2 = this.t2;
            this.macx3 = this.t3;
            this.macx4 = this.t4;
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAP_AEAD
        public void absorbMacBlock(byte[] bArr, int i) {
            this.x0 ^= Pack.bigEndianToLong(bArr, i);
            P12();
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAP_AEAD
        public void absorbFinalAADBlock() {
            if (ISAPEngine.this.m_aadPos == ISAPEngine.this.AADBufferSize) {
                absorbMacBlock(ISAPEngine.this.m_aad, 0);
                ISAPEngine.this.m_aadPos = 0;
            } else {
                for (int i = 0; i < ISAPEngine.this.m_aadPos; i++) {
                    this.x0 ^= (((long) ISAPEngine.this.m_aad[i]) & 255) << ((7 - i) << 3);
                }
            }
            this.x0 ^= 128 << ((7 - ISAPEngine.this.m_aadPos) << 3);
            P12();
            this.x4 ^= 1;
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAP_AEAD
        public void processMACFinal(byte[] bArr, int i, int i2, byte[] bArr2) {
            if (i2 == ISAPEngine.this.BlockSize) {
                absorbMacBlock(bArr, i);
                i2 = 0;
            } else {
                for (int i3 = 0; i3 < i2; i3++) {
                    int i4 = i;
                    i++;
                    this.x0 ^= (((long) bArr[i4]) & 255) << ((7 - i3) << 3);
                }
            }
            this.x0 ^= 128 << ((7 - i2) << 3);
            P12();
            Pack.longToBigEndian(this.x0, bArr2, 0);
            Pack.longToBigEndian(this.x1, bArr2, 8);
            long j = this.x2;
            long j2 = this.x3;
            long j3 = this.x4;
            isap_rk(this.ISAP_IV2_64, bArr2, ISAPEngine.this.KEY_SIZE);
            this.x2 = j;
            this.x3 = j2;
            this.x4 = j3;
            P12();
            Pack.longToBigEndian(this.x0, bArr2, 0);
            Pack.longToBigEndian(this.x1, bArr2, 8);
        }

        public void isap_rk(long j, byte[] bArr, int i) {
            this.x0 = this.k64[0];
            this.x1 = this.k64[1];
            this.x2 = j;
            this.x4 = 0L;
            this.x3 = 0L;
            P12();
            for (int i2 = 0; i2 < (i << 3) - 1; i2++) {
                this.x0 ^= (((long) (((bArr[i2 >>> 3] >>> (7 - (i2 & 7))) & 1) << 7)) & 255) << 56;
                PX2();
            }
            this.x0 ^= ((((long) bArr[i - 1]) & 1) << 7) << 56;
            P12();
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAP_AEAD
        public void processEncBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
            long jU64BIG = U64BIG(this.x0) ^ Pack.littleEndianToLong(bArr, i);
            PX1();
            Pack.longToLittleEndian(jU64BIG, bArr2, i2);
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAP_AEAD
        public void processEncFinalBlock(byte[] bArr, int i) {
            if (ISAPEngine.this.m_bufPos == ISAPEngine.this.BlockSize) {
                processEncBlock(ISAPEngine.this.m_buf, 0, bArr, i);
                return;
            }
            byte[] bArrLongToLittleEndian = Pack.longToLittleEndian(this.x0);
            int i2 = ISAPEngine.this.m_bufPos;
            while (i2 > 0) {
                int i3 = (i + i2) - 1;
                byte b = bArrLongToLittleEndian[ISAPEngine.this.BlockSize - i2];
                i2--;
                bArr[i3] = (byte) (b ^ ISAPEngine.this.m_buf[i2]);
            }
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAP_AEAD
        public void reset() {
            isap_rk(this.ISAP_IV3_64, ISAPEngine.this.npub, ISAPEngine.this.IV_SIZE);
            this.x3 = this.npub64[0];
            this.x4 = this.npub64[1];
            PX1();
            swapInternalState();
            this.x0 = this.npub64[0];
            this.x1 = this.npub64[1];
            this.x2 = this.ISAP_IV1_64;
            this.x4 = 0L;
            this.x3 = 0L;
            P12();
        }

        private int getLongSize(int i) {
            return (i >>> 3) + ((i & 7) != 0 ? 1 : 0);
        }

        private long ROTR(long j, long j2) {
            return (j >>> ((int) j2)) | (j << ((int) (64 - j2)));
        }

        protected long U64BIG(long j) {
            return (ROTR(j, 8L) & (-72057589759737856L)) | (ROTR(j, 24L) & 71776119077928960L) | (ROTR(j, 40L) & 280375465148160L) | (ROTR(j, 56L) & 1095216660735L);
        }

        protected void ROUND(long j) {
            this.t0 = ((((this.x0 ^ this.x1) ^ this.x2) ^ this.x3) ^ j) ^ (this.x1 & (((this.x0 ^ this.x2) ^ this.x4) ^ j));
            this.t1 = ((((this.x0 ^ this.x2) ^ this.x3) ^ this.x4) ^ j) ^ (((this.x1 ^ this.x2) ^ j) & (this.x1 ^ this.x3));
            this.t2 = (((this.x1 ^ this.x2) ^ this.x4) ^ j) ^ (this.x3 & this.x4);
            this.t3 = (((this.x0 ^ this.x1) ^ this.x2) ^ j) ^ ((this.x0 ^ (-1)) & (this.x3 ^ this.x4));
            this.t4 = ((this.x1 ^ this.x3) ^ this.x4) ^ ((this.x0 ^ this.x4) & this.x1);
            this.x0 = (this.t0 ^ ROTR(this.t0, 19L)) ^ ROTR(this.t0, 28L);
            this.x1 = (this.t1 ^ ROTR(this.t1, 39L)) ^ ROTR(this.t1, 61L);
            this.x2 = ((this.t2 ^ ROTR(this.t2, 1L)) ^ ROTR(this.t2, 6L)) ^ (-1);
            this.x3 = (this.t3 ^ ROTR(this.t3, 10L)) ^ ROTR(this.t3, 17L);
            this.x4 = (this.t4 ^ ROTR(this.t4, 7L)) ^ ROTR(this.t4, 41L);
        }

        public void P12() {
            ROUND(240L);
            ROUND(225L);
            ROUND(210L);
            ROUND(195L);
            ROUND(180L);
            ROUND(165L);
            P6();
        }

        protected void P6() {
            ROUND(150L);
            ROUND(135L);
            ROUND(120L);
            ROUND(105L);
            ROUND(90L);
            ROUND(75L);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ISAPEngine$ISAPAEAD_A_128.class */
    private class ISAPAEAD_A_128 extends ISAPAEAD_A {
        public ISAPAEAD_A_128() {
            super();
            this.ISAP_IV1_64 = 108156764298152972L;
            this.ISAP_IV2_64 = 180214358336080908L;
            this.ISAP_IV3_64 = 252271952374008844L;
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAPAEAD_A
        protected void PX1() {
            P12();
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAPAEAD_A
        protected void PX2() {
            P12();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ISAPEngine$ISAPAEAD_A_128A.class */
    private class ISAPAEAD_A_128A extends ISAPAEAD_A {
        public ISAPAEAD_A_128A() {
            super();
            this.ISAP_IV1_64 = 108156764297430540L;
            this.ISAP_IV2_64 = 180214358335358476L;
            this.ISAP_IV3_64 = 252271952373286412L;
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAPAEAD_A
        protected void PX1() {
            P6();
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAPAEAD_A
        protected void PX2() {
            ROUND(75L);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ISAPEngine$ISAPAEAD_K.class */
    private abstract class ISAPAEAD_K implements ISAP_AEAD {
        final int ISAP_STATE_SZ_CRYPTO_NPUBBYTES;
        protected short[] ISAP_IV1_16;
        protected short[] ISAP_IV2_16;
        protected short[] ISAP_IV3_16;
        protected short[] k16;
        protected short[] iv16;
        private final int[] KeccakF400RoundConstants = {1, 32898, 32906, 32768, 32907, 1, 32897, 32777, 138, 136, 32777, 10, 32907, 139, 32905, DBT.DBT_DEVICEREMOVEPENDING, 32770, 128, 32778, 10};
        protected short[] SX = new short[25];
        protected short[] macSX = new short[25];
        protected short[] E = new short[25];
        protected short[] C = new short[5];
        protected short[] macE = new short[25];
        protected short[] macC = new short[5];

        public ISAPAEAD_K() {
            this.ISAP_STATE_SZ_CRYPTO_NPUBBYTES = 40 - ISAPEngine.this.IV_SIZE;
            ISAPEngine.this.ISAP_rH = 144;
            ISAPEngine.this.BlockSize = (ISAPEngine.this.ISAP_rH + 7) >> 3;
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAP_AEAD
        public void init() {
            this.k16 = new short[ISAPEngine.this.k.length >> 1];
            byteToShort(ISAPEngine.this.k, this.k16, this.k16.length);
            this.iv16 = new short[ISAPEngine.this.npub.length >> 1];
            byteToShort(ISAPEngine.this.npub, this.iv16, this.iv16.length);
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAP_AEAD
        public void reset() {
            Arrays.fill(this.SX, (short) 0);
            isap_rk(this.ISAP_IV3_16, ISAPEngine.this.npub, ISAPEngine.this.IV_SIZE, this.SX, this.ISAP_STATE_SZ_CRYPTO_NPUBBYTES, this.C);
            System.arraycopy(this.iv16, 0, this.SX, 17, 8);
            PermuteRoundsKX(this.SX, this.E, this.C);
            swapInternalState();
            Arrays.fill(this.SX, 12, 25, (short) 0);
            System.arraycopy(this.iv16, 0, this.SX, 0, 8);
            System.arraycopy(this.ISAP_IV1_16, 0, this.SX, 8, 4);
            PermuteRoundsHX(this.SX, this.E, this.C);
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAP_AEAD
        public void swapInternalState() {
            short[] sArr = this.SX;
            this.SX = this.macSX;
            this.macSX = sArr;
            short[] sArr2 = this.E;
            this.E = this.macE;
            this.macE = sArr2;
            short[] sArr3 = this.C;
            this.C = this.macC;
            this.macC = sArr3;
        }

        protected abstract void PermuteRoundsHX(short[] sArr, short[] sArr2, short[] sArr3);

        protected abstract void PermuteRoundsKX(short[] sArr, short[] sArr2, short[] sArr3);

        protected abstract void PermuteRoundsBX(short[] sArr, short[] sArr2, short[] sArr3);

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAP_AEAD
        public void absorbMacBlock(byte[] bArr, int i) {
            byteToShortXor(bArr, i, this.SX, ISAPEngine.this.BlockSize >> 1);
            PermuteRoundsHX(this.SX, this.E, this.C);
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAP_AEAD
        public void absorbFinalAADBlock() {
            if (ISAPEngine.this.m_aadPos == ISAPEngine.this.AADBufferSize) {
                absorbMacBlock(ISAPEngine.this.m_aad, 0);
                ISAPEngine.this.m_aadPos = 0;
            } else {
                for (int i = 0; i < ISAPEngine.this.m_aadPos; i++) {
                    short[] sArr = this.SX;
                    int i2 = i >> 1;
                    sArr[i2] = (short) (sArr[i2] ^ ((ISAPEngine.this.m_aad[i] & 255) << ((i & 1) << 3)));
                }
            }
            short[] sArr2 = this.SX;
            int i3 = ISAPEngine.this.m_aadPos >> 1;
            sArr2[i3] = (short) (sArr2[i3] ^ (128 << ((ISAPEngine.this.m_aadPos & 1) << 3)));
            PermuteRoundsHX(this.SX, this.E, this.C);
            short[] sArr3 = this.SX;
            sArr3[24] = (short) (sArr3[24] ^ 256);
        }

        public void isap_rk(short[] sArr, byte[] bArr, int i, short[] sArr2, int i2, short[] sArr3) {
            short[] sArr4 = new short[25];
            short[] sArr5 = new short[25];
            System.arraycopy(this.k16, 0, sArr4, 0, 8);
            System.arraycopy(sArr, 0, sArr4, 8, 4);
            PermuteRoundsKX(sArr4, sArr5, sArr3);
            for (int i3 = 0; i3 < (i << 3) - 1; i3++) {
                sArr4[0] = (short) (sArr4[0] ^ (((bArr[i3 >> 3] >>> (7 - (i3 & 7))) & 1) << 7));
                PermuteRoundsBX(sArr4, sArr5, sArr3);
            }
            sArr4[0] = (short) (sArr4[0] ^ ((bArr[i - 1] & 1) << 7));
            PermuteRoundsKX(sArr4, sArr5, sArr3);
            System.arraycopy(sArr4, 0, sArr2, 0, i2 == this.ISAP_STATE_SZ_CRYPTO_NPUBBYTES ? 17 : 8);
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAP_AEAD
        public void processMACFinal(byte[] bArr, int i, int i2, byte[] bArr2) {
            if (i2 == ISAPEngine.this.BlockSize) {
                absorbMacBlock(bArr, i);
                i2 = 0;
            } else {
                for (int i3 = 0; i3 < i2; i3++) {
                    short[] sArr = this.SX;
                    int i4 = i3 >> 1;
                    int i5 = i;
                    i++;
                    sArr[i4] = (short) (sArr[i4] ^ ((bArr[i5] & 255) << ((i3 & 1) << 3)));
                }
            }
            short[] sArr2 = this.SX;
            int i6 = i2 >> 1;
            sArr2[i6] = (short) (sArr2[i6] ^ (128 << ((i2 & 1) << 3)));
            PermuteRoundsHX(this.SX, this.E, this.C);
            shortToByte(this.SX, bArr2);
            isap_rk(this.ISAP_IV2_16, bArr2, ISAPEngine.this.KEY_SIZE, this.SX, ISAPEngine.this.KEY_SIZE, this.C);
            PermuteRoundsHX(this.SX, this.E, this.C);
            shortToByte(this.SX, bArr2);
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAP_AEAD
        public void processEncBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
            for (int i3 = 0; i3 < ISAPEngine.this.BlockSize; i3++) {
                int i4 = i2;
                i2++;
                int i5 = i;
                i++;
                bArr2[i4] = (byte) ((this.SX[i3 >> 1] >>> ((i3 & 1) << 3)) ^ bArr[i5]);
            }
            PermuteRoundsKX(this.SX, this.E, this.C);
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAP_AEAD
        public void processEncFinalBlock(byte[] bArr, int i) {
            int i2 = ISAPEngine.this.m_bufPos;
            for (int i3 = 0; i3 < i2; i3++) {
                int i4 = i;
                i++;
                bArr[i4] = (byte) ((this.SX[i3 >> 1] >>> ((i3 & 1) << 3)) ^ ISAPEngine.this.m_buf[i3]);
            }
        }

        private void byteToShortXor(byte[] bArr, int i, short[] sArr, int i2) {
            for (int i3 = 0; i3 < i2; i3++) {
                int i4 = i3;
                sArr[i4] = (short) (sArr[i4] ^ Pack.littleEndianToShort(bArr, i + (i3 << 1)));
            }
        }

        private void byteToShort(byte[] bArr, short[] sArr, int i) {
            for (int i2 = 0; i2 < i; i2++) {
                sArr[i2] = Pack.littleEndianToShort(bArr, i2 << 1);
            }
        }

        private void shortToByte(short[] sArr, byte[] bArr) {
            for (int i = 0; i < 8; i++) {
                Pack.shortToLittleEndian(sArr[i], bArr, i << 1);
            }
        }

        protected void rounds12X(short[] sArr, short[] sArr2, short[] sArr3) {
            prepareThetaX(sArr, sArr3);
            rounds_8_18(sArr, sArr2, sArr3);
        }

        protected void rounds_4_18(short[] sArr, short[] sArr2, short[] sArr3) {
            thetaRhoPiChiIotaPrepareTheta(4, sArr, sArr2, sArr3);
            thetaRhoPiChiIotaPrepareTheta(5, sArr2, sArr, sArr3);
            thetaRhoPiChiIotaPrepareTheta(6, sArr, sArr2, sArr3);
            thetaRhoPiChiIotaPrepareTheta(7, sArr2, sArr, sArr3);
            rounds_8_18(sArr, sArr2, sArr3);
        }

        protected void rounds_8_18(short[] sArr, short[] sArr2, short[] sArr3) {
            thetaRhoPiChiIotaPrepareTheta(8, sArr, sArr2, sArr3);
            thetaRhoPiChiIotaPrepareTheta(9, sArr2, sArr, sArr3);
            thetaRhoPiChiIotaPrepareTheta(10, sArr, sArr2, sArr3);
            thetaRhoPiChiIotaPrepareTheta(11, sArr2, sArr, sArr3);
            rounds_12_18(sArr, sArr2, sArr3);
        }

        protected void rounds_12_18(short[] sArr, short[] sArr2, short[] sArr3) {
            thetaRhoPiChiIotaPrepareTheta(12, sArr, sArr2, sArr3);
            thetaRhoPiChiIotaPrepareTheta(13, sArr2, sArr, sArr3);
            thetaRhoPiChiIotaPrepareTheta(14, sArr, sArr2, sArr3);
            thetaRhoPiChiIotaPrepareTheta(15, sArr2, sArr, sArr3);
            thetaRhoPiChiIotaPrepareTheta(16, sArr, sArr2, sArr3);
            thetaRhoPiChiIotaPrepareTheta(17, sArr2, sArr, sArr3);
            thetaRhoPiChiIotaPrepareTheta(18, sArr, sArr2, sArr3);
            thetaRhoPiChiIota(sArr2, sArr, sArr3);
        }

        protected void prepareThetaX(short[] sArr, short[] sArr2) {
            sArr2[0] = (short) ((((sArr[0] ^ sArr[5]) ^ sArr[10]) ^ sArr[15]) ^ sArr[20]);
            sArr2[1] = (short) ((((sArr[1] ^ sArr[6]) ^ sArr[11]) ^ sArr[16]) ^ sArr[21]);
            sArr2[2] = (short) ((((sArr[2] ^ sArr[7]) ^ sArr[12]) ^ sArr[17]) ^ sArr[22]);
            sArr2[3] = (short) ((((sArr[3] ^ sArr[8]) ^ sArr[13]) ^ sArr[18]) ^ sArr[23]);
            sArr2[4] = (short) ((((sArr[4] ^ sArr[9]) ^ sArr[14]) ^ sArr[19]) ^ sArr[24]);
        }

        private short ROL16(short s, int i) {
            return (short) (((s & 65535) << i) ^ ((s & 65535) >>> (16 - i)));
        }

        protected void thetaRhoPiChiIotaPrepareTheta(int i, short[] sArr, short[] sArr2, short[] sArr3) {
            short sROL16 = (short) (sArr3[4] ^ ROL16(sArr3[1], 1));
            short sROL17 = (short) (sArr3[0] ^ ROL16(sArr3[2], 1));
            short sROL18 = (short) (sArr3[1] ^ ROL16(sArr3[3], 1));
            short sROL19 = (short) (sArr3[2] ^ ROL16(sArr3[4], 1));
            short sROL110 = (short) (sArr3[3] ^ ROL16(sArr3[0], 1));
            short s = (short) (sArr[0] ^ sROL16);
            sArr[0] = s;
            sArr[6] = (short) (sArr[6] ^ sROL17);
            short sROL111 = ROL16(sArr[6], 12);
            sArr[12] = (short) (sArr[12] ^ sROL18);
            short sROL112 = ROL16(sArr[12], 11);
            sArr[18] = (short) (sArr[18] ^ sROL19);
            short sROL113 = ROL16(sArr[18], 5);
            sArr[24] = (short) (sArr[24] ^ sROL110);
            short sROL114 = ROL16(sArr[24], 14);
            short s2 = (short) ((s ^ ((sROL111 ^ (-1)) & sROL112)) ^ this.KeccakF400RoundConstants[i]);
            sArr2[0] = s2;
            sArr3[0] = s2;
            short s3 = (short) (sROL111 ^ ((sROL112 ^ (-1)) & sROL113));
            sArr2[1] = s3;
            sArr3[1] = s3;
            short s4 = (short) (sROL112 ^ ((sROL113 ^ (-1)) & sROL114));
            sArr2[2] = s4;
            sArr3[2] = s4;
            short s5 = (short) (sROL113 ^ ((sROL114 ^ (-1)) & s));
            sArr2[3] = s5;
            sArr3[3] = s5;
            short s6 = (short) (sROL114 ^ ((s ^ (-1)) & sROL111));
            sArr2[4] = s6;
            sArr3[4] = s6;
            sArr[3] = (short) (sArr[3] ^ sROL19);
            short sROL115 = ROL16(sArr[3], 12);
            sArr[9] = (short) (sArr[9] ^ sROL110);
            short sROL116 = ROL16(sArr[9], 4);
            sArr[10] = (short) (sArr[10] ^ sROL16);
            short sROL117 = ROL16(sArr[10], 3);
            sArr[16] = (short) (sArr[16] ^ sROL17);
            short sROL118 = ROL16(sArr[16], 13);
            sArr[22] = (short) (sArr[22] ^ sROL18);
            short sROL119 = ROL16(sArr[22], 13);
            sArr2[5] = (short) (sROL115 ^ ((sROL116 ^ (-1)) & sROL117));
            sArr3[0] = (short) (sArr3[0] ^ sArr2[5]);
            sArr2[6] = (short) (sROL116 ^ ((sROL117 ^ (-1)) & sROL118));
            sArr3[1] = (short) (sArr3[1] ^ sArr2[6]);
            sArr2[7] = (short) (sROL117 ^ ((sROL118 ^ (-1)) & sROL119));
            sArr3[2] = (short) (sArr3[2] ^ sArr2[7]);
            sArr2[8] = (short) (sROL118 ^ ((sROL119 ^ (-1)) & sROL115));
            sArr3[3] = (short) (sArr3[3] ^ sArr2[8]);
            sArr2[9] = (short) (sROL119 ^ ((sROL115 ^ (-1)) & sROL116));
            sArr3[4] = (short) (sArr3[4] ^ sArr2[9]);
            sArr[1] = (short) (sArr[1] ^ sROL17);
            short sROL120 = ROL16(sArr[1], 1);
            sArr[7] = (short) (sArr[7] ^ sROL18);
            short sROL121 = ROL16(sArr[7], 6);
            sArr[13] = (short) (sArr[13] ^ sROL19);
            short sROL122 = ROL16(sArr[13], 9);
            sArr[19] = (short) (sArr[19] ^ sROL110);
            short sROL123 = ROL16(sArr[19], 8);
            sArr[20] = (short) (sArr[20] ^ sROL16);
            short sROL124 = ROL16(sArr[20], 2);
            sArr2[10] = (short) (sROL120 ^ ((sROL121 ^ (-1)) & sROL122));
            sArr3[0] = (short) (sArr3[0] ^ sArr2[10]);
            sArr2[11] = (short) (sROL121 ^ ((sROL122 ^ (-1)) & sROL123));
            sArr3[1] = (short) (sArr3[1] ^ sArr2[11]);
            sArr2[12] = (short) (sROL122 ^ ((sROL123 ^ (-1)) & sROL124));
            sArr3[2] = (short) (sArr3[2] ^ sArr2[12]);
            sArr2[13] = (short) (sROL123 ^ ((sROL124 ^ (-1)) & sROL120));
            sArr3[3] = (short) (sArr3[3] ^ sArr2[13]);
            sArr2[14] = (short) (sROL124 ^ ((sROL120 ^ (-1)) & sROL121));
            sArr3[4] = (short) (sArr3[4] ^ sArr2[14]);
            sArr[4] = (short) (sArr[4] ^ sROL110);
            short sROL125 = ROL16(sArr[4], 11);
            sArr[5] = (short) (sArr[5] ^ sROL16);
            short sROL126 = ROL16(sArr[5], 4);
            sArr[11] = (short) (sArr[11] ^ sROL17);
            short sROL127 = ROL16(sArr[11], 10);
            sArr[17] = (short) (sArr[17] ^ sROL18);
            short sROL128 = ROL16(sArr[17], 15);
            sArr[23] = (short) (sArr[23] ^ sROL19);
            short sROL129 = ROL16(sArr[23], 8);
            sArr2[15] = (short) (sROL125 ^ ((sROL126 ^ (-1)) & sROL127));
            sArr3[0] = (short) (sArr3[0] ^ sArr2[15]);
            sArr2[16] = (short) (sROL126 ^ ((sROL127 ^ (-1)) & sROL128));
            sArr3[1] = (short) (sArr3[1] ^ sArr2[16]);
            sArr2[17] = (short) (sROL127 ^ ((sROL128 ^ (-1)) & sROL129));
            sArr3[2] = (short) (sArr3[2] ^ sArr2[17]);
            sArr2[18] = (short) (sROL128 ^ ((sROL129 ^ (-1)) & sROL125));
            sArr3[3] = (short) (sArr3[3] ^ sArr2[18]);
            sArr2[19] = (short) (sROL129 ^ ((sROL125 ^ (-1)) & sROL126));
            sArr3[4] = (short) (sArr3[4] ^ sArr2[19]);
            sArr[2] = (short) (sArr[2] ^ sROL18);
            short sROL130 = ROL16(sArr[2], 14);
            sArr[8] = (short) (sArr[8] ^ sROL19);
            short sROL131 = ROL16(sArr[8], 7);
            sArr[14] = (short) (sArr[14] ^ sROL110);
            short sROL132 = ROL16(sArr[14], 7);
            sArr[15] = (short) (sArr[15] ^ sROL16);
            short sROL133 = ROL16(sArr[15], 9);
            sArr[21] = (short) (sArr[21] ^ sROL17);
            short sROL134 = ROL16(sArr[21], 2);
            sArr2[20] = (short) (sROL130 ^ ((sROL131 ^ (-1)) & sROL132));
            sArr3[0] = (short) (sArr3[0] ^ sArr2[20]);
            sArr2[21] = (short) (sROL131 ^ ((sROL132 ^ (-1)) & sROL133));
            sArr3[1] = (short) (sArr3[1] ^ sArr2[21]);
            sArr2[22] = (short) (sROL132 ^ ((sROL133 ^ (-1)) & sROL134));
            sArr3[2] = (short) (sArr3[2] ^ sArr2[22]);
            sArr2[23] = (short) (sROL133 ^ ((sROL134 ^ (-1)) & sROL130));
            sArr3[3] = (short) (sArr3[3] ^ sArr2[23]);
            sArr2[24] = (short) (sROL134 ^ ((sROL130 ^ (-1)) & sROL131));
            sArr3[4] = (short) (sArr3[4] ^ sArr2[24]);
        }

        protected void thetaRhoPiChiIota(short[] sArr, short[] sArr2, short[] sArr3) {
            short sROL16 = (short) (sArr3[4] ^ ROL16(sArr3[1], 1));
            short sROL17 = (short) (sArr3[0] ^ ROL16(sArr3[2], 1));
            short sROL18 = (short) (sArr3[1] ^ ROL16(sArr3[3], 1));
            short sROL19 = (short) (sArr3[2] ^ ROL16(sArr3[4], 1));
            short sROL110 = (short) (sArr3[3] ^ ROL16(sArr3[0], 1));
            short s = (short) (sArr[0] ^ sROL16);
            sArr[0] = s;
            sArr[6] = (short) (sArr[6] ^ sROL17);
            short sROL111 = ROL16(sArr[6], 12);
            sArr[12] = (short) (sArr[12] ^ sROL18);
            short sROL112 = ROL16(sArr[12], 11);
            sArr[18] = (short) (sArr[18] ^ sROL19);
            short sROL113 = ROL16(sArr[18], 5);
            sArr[24] = (short) (sArr[24] ^ sROL110);
            short sROL114 = ROL16(sArr[24], 14);
            sArr2[0] = (short) ((s ^ ((sROL111 ^ (-1)) & sROL112)) ^ this.KeccakF400RoundConstants[19]);
            sArr2[1] = (short) (sROL111 ^ ((sROL112 ^ (-1)) & sROL113));
            sArr2[2] = (short) (sROL112 ^ ((sROL113 ^ (-1)) & sROL114));
            sArr2[3] = (short) (sROL113 ^ ((sROL114 ^ (-1)) & s));
            sArr2[4] = (short) (sROL114 ^ ((s ^ (-1)) & sROL111));
            sArr[3] = (short) (sArr[3] ^ sROL19);
            short sROL115 = ROL16(sArr[3], 12);
            sArr[9] = (short) (sArr[9] ^ sROL110);
            short sROL116 = ROL16(sArr[9], 4);
            sArr[10] = (short) (sArr[10] ^ sROL16);
            short sROL117 = ROL16(sArr[10], 3);
            sArr[16] = (short) (sArr[16] ^ sROL17);
            short sROL118 = ROL16(sArr[16], 13);
            sArr[22] = (short) (sArr[22] ^ sROL18);
            short sROL119 = ROL16(sArr[22], 13);
            sArr2[5] = (short) (sROL115 ^ ((sROL116 ^ (-1)) & sROL117));
            sArr2[6] = (short) (sROL116 ^ ((sROL117 ^ (-1)) & sROL118));
            sArr2[7] = (short) (sROL117 ^ ((sROL118 ^ (-1)) & sROL119));
            sArr2[8] = (short) (sROL118 ^ ((sROL119 ^ (-1)) & sROL115));
            sArr2[9] = (short) (sROL119 ^ ((sROL115 ^ (-1)) & sROL116));
            sArr[1] = (short) (sArr[1] ^ sROL17);
            short sROL120 = ROL16(sArr[1], 1);
            sArr[7] = (short) (sArr[7] ^ sROL18);
            short sROL121 = ROL16(sArr[7], 6);
            sArr[13] = (short) (sArr[13] ^ sROL19);
            short sROL122 = ROL16(sArr[13], 9);
            sArr[19] = (short) (sArr[19] ^ sROL110);
            short sROL123 = ROL16(sArr[19], 8);
            sArr[20] = (short) (sArr[20] ^ sROL16);
            short sROL124 = ROL16(sArr[20], 2);
            sArr2[10] = (short) (sROL120 ^ ((sROL121 ^ (-1)) & sROL122));
            sArr2[11] = (short) (sROL121 ^ ((sROL122 ^ (-1)) & sROL123));
            sArr2[12] = (short) (sROL122 ^ ((sROL123 ^ (-1)) & sROL124));
            sArr2[13] = (short) (sROL123 ^ ((sROL124 ^ (-1)) & sROL120));
            sArr2[14] = (short) (sROL124 ^ ((sROL120 ^ (-1)) & sROL121));
            sArr[4] = (short) (sArr[4] ^ sROL110);
            short sROL125 = ROL16(sArr[4], 11);
            sArr[5] = (short) (sArr[5] ^ sROL16);
            short sROL126 = ROL16(sArr[5], 4);
            sArr[11] = (short) (sArr[11] ^ sROL17);
            short sROL127 = ROL16(sArr[11], 10);
            sArr[17] = (short) (sArr[17] ^ sROL18);
            short sROL128 = ROL16(sArr[17], 15);
            sArr[23] = (short) (sArr[23] ^ sROL19);
            short sROL129 = ROL16(sArr[23], 8);
            sArr2[15] = (short) (sROL125 ^ ((sROL126 ^ (-1)) & sROL127));
            sArr2[16] = (short) (sROL126 ^ ((sROL127 ^ (-1)) & sROL128));
            sArr2[17] = (short) (sROL127 ^ ((sROL128 ^ (-1)) & sROL129));
            sArr2[18] = (short) (sROL128 ^ ((sROL129 ^ (-1)) & sROL125));
            sArr2[19] = (short) (sROL129 ^ ((sROL125 ^ (-1)) & sROL126));
            sArr[2] = (short) (sArr[2] ^ sROL18);
            short sROL130 = ROL16(sArr[2], 14);
            sArr[8] = (short) (sArr[8] ^ sROL19);
            short sROL131 = ROL16(sArr[8], 7);
            sArr[14] = (short) (sArr[14] ^ sROL110);
            short sROL132 = ROL16(sArr[14], 7);
            sArr[15] = (short) (sArr[15] ^ sROL16);
            short sROL133 = ROL16(sArr[15], 9);
            sArr[21] = (short) (sArr[21] ^ sROL17);
            short sROL134 = ROL16(sArr[21], 2);
            sArr2[20] = (short) (sROL130 ^ ((sROL131 ^ (-1)) & sROL132));
            sArr2[21] = (short) (sROL131 ^ ((sROL132 ^ (-1)) & sROL133));
            sArr2[22] = (short) (sROL132 ^ ((sROL133 ^ (-1)) & sROL134));
            sArr2[23] = (short) (sROL133 ^ ((sROL134 ^ (-1)) & sROL130));
            sArr2[24] = (short) (sROL134 ^ ((sROL130 ^ (-1)) & sROL131));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ISAPEngine$ISAPAEAD_K_128.class */
    private class ISAPAEAD_K_128 extends ISAPAEAD_K {
        public ISAPAEAD_K_128() {
            super();
            this.ISAP_IV1_16 = new short[]{-32767, 400, 3092, 3084};
            this.ISAP_IV2_16 = new short[]{-32766, 400, 3092, 3084};
            this.ISAP_IV3_16 = new short[]{-32765, 400, 3092, 3084};
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAPAEAD_K
        protected void PermuteRoundsHX(short[] sArr, short[] sArr2, short[] sArr3) {
            prepareThetaX(sArr, sArr3);
            thetaRhoPiChiIotaPrepareTheta(0, sArr, sArr2, sArr3);
            thetaRhoPiChiIotaPrepareTheta(1, sArr2, sArr, sArr3);
            thetaRhoPiChiIotaPrepareTheta(2, sArr, sArr2, sArr3);
            thetaRhoPiChiIotaPrepareTheta(3, sArr2, sArr, sArr3);
            rounds_4_18(sArr, sArr2, sArr3);
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAPAEAD_K
        protected void PermuteRoundsKX(short[] sArr, short[] sArr2, short[] sArr3) {
            rounds12X(sArr, sArr2, sArr3);
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAPAEAD_K
        protected void PermuteRoundsBX(short[] sArr, short[] sArr2, short[] sArr3) {
            rounds12X(sArr, sArr2, sArr3);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ISAPEngine$ISAPAEAD_K_128A.class */
    private class ISAPAEAD_K_128A extends ISAPAEAD_K {
        public ISAPAEAD_K_128A() {
            super();
            this.ISAP_IV1_16 = new short[]{-32767, 400, 272, 2056};
            this.ISAP_IV2_16 = new short[]{-32766, 400, 272, 2056};
            this.ISAP_IV3_16 = new short[]{-32765, 400, 272, 2056};
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAPAEAD_K
        protected void PermuteRoundsHX(short[] sArr, short[] sArr2, short[] sArr3) {
            prepareThetaX(sArr, sArr3);
            rounds_4_18(sArr, sArr2, sArr3);
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAPAEAD_K
        protected void PermuteRoundsKX(short[] sArr, short[] sArr2, short[] sArr3) {
            prepareThetaX(sArr, sArr3);
            rounds_12_18(sArr, sArr2, sArr3);
        }

        @Override // org.bouncycastle.crypto.engines.ISAPEngine.ISAPAEAD_K
        protected void PermuteRoundsBX(short[] sArr, short[] sArr2, short[] sArr3) {
            prepareThetaX(sArr, sArr3);
            thetaRhoPiChiIotaPrepareTheta(19, sArr, sArr2, sArr3);
            System.arraycopy(sArr2, 0, sArr, 0, sArr2.length);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ISAPEngine$ISAP_AEAD.class */
    private interface ISAP_AEAD {
        void init();

        void reset();

        void absorbMacBlock(byte[] bArr, int i);

        void absorbFinalAADBlock();

        void swapInternalState();

        void processEncBlock(byte[] bArr, int i, byte[] bArr2, int i2);

        void processEncFinalBlock(byte[] bArr, int i);

        void processMACFinal(byte[] bArr, int i, int i2, byte[] bArr2);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ISAPEngine$IsapType.class */
    public enum IsapType {
        ISAP_A_128A,
        ISAP_K_128A,
        ISAP_A_128,
        ISAP_K_128
    }

    public ISAPEngine(IsapType isapType) {
        this.KEY_SIZE = 16;
        this.IV_SIZE = 16;
        this.MAC_SIZE = 16;
        switch (isapType) {
            case ISAP_A_128A:
                this.ISAPAEAD = new ISAPAEAD_A_128A();
                this.algorithmName = "ISAP-A-128A AEAD";
                break;
            case ISAP_K_128A:
                this.ISAPAEAD = new ISAPAEAD_K_128A();
                this.algorithmName = "ISAP-K-128A AEAD";
                break;
            case ISAP_A_128:
                this.ISAPAEAD = new ISAPAEAD_A_128();
                this.algorithmName = "ISAP-A-128 AEAD";
                break;
            case ISAP_K_128:
                this.ISAPAEAD = new ISAPAEAD_K_128();
                this.algorithmName = "ISAP-K-128 AEAD";
                break;
        }
        this.AADBufferSize = this.BlockSize;
        this.m_aad = new byte[this.AADBufferSize];
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    protected void init(byte[] bArr, byte[] bArr2) throws IllegalArgumentException {
        this.npub = bArr2;
        this.k = bArr;
        this.m_buf = new byte[this.BlockSize + (this.forEncryption ? 0 : this.MAC_SIZE)];
        this.ISAPAEAD.init();
        this.initialised = true;
        this.m_state = this.forEncryption ? AEADBufferBaseEngine.State.EncInit : AEADBufferBaseEngine.State.DecInit;
        reset();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    protected void processBufferAAD(byte[] bArr, int i) {
        this.ISAPAEAD.absorbMacBlock(bArr, i);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    protected void processFinalAAD() {
        if (this.aadFinished) {
            return;
        }
        this.ISAPAEAD.absorbFinalAADBlock();
        this.ISAPAEAD.swapInternalState();
        this.m_aadPos = 0;
        this.aadFinished = true;
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    protected void processBuffer(byte[] bArr, int i, byte[] bArr2, int i2) {
        processFinalAAD();
        this.ISAPAEAD.processEncBlock(bArr, i, bArr2, i2);
        this.ISAPAEAD.swapInternalState();
        if (this.forEncryption) {
            this.ISAPAEAD.absorbMacBlock(bArr2, i2);
        } else {
            this.ISAPAEAD.absorbMacBlock(bArr, i);
        }
        this.ISAPAEAD.swapInternalState();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    protected void processFinalBlock(byte[] bArr, int i) {
        processFinalAAD();
        int i2 = this.m_bufPos;
        this.mac = new byte[this.MAC_SIZE];
        this.ISAPAEAD.processEncFinalBlock(bArr, i);
        this.ISAPAEAD.swapInternalState();
        if (this.forEncryption) {
            this.ISAPAEAD.processMACFinal(bArr, i, i2, this.mac);
        } else {
            this.ISAPAEAD.processMACFinal(this.m_buf, 0, i2, this.mac);
        }
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    protected void reset(boolean z) {
        if (!this.initialised) {
            throw new IllegalStateException("Need call init function before encryption/decryption");
        }
        Arrays.fill(this.m_buf, (byte) 0);
        Arrays.fill(this.m_aad, (byte) 0);
        this.ISAPAEAD.reset();
        this.m_bufPos = 0;
        this.m_aadPos = 0;
        this.aadFinished = false;
        super.reset(z);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int getOutputSize(int i) {
        return super.getOutputSize(i);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int getUpdateOutputSize(int i) {
        return super.getUpdateOutputSize(i);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    public /* bridge */ /* synthetic */ int getBlockSize() {
        return super.getBlockSize();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int doFinal(byte[] bArr, int i) throws InvalidCipherTextException, IllegalStateException {
        return super.doFinal(bArr, i);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException {
        return super.processBytes(bArr, i, i2, bArr2, i3);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ void processAADBytes(byte[] bArr, int i, int i2) {
        super.processAADBytes(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ void processAADByte(byte b) {
        super.processAADByte(b);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ void init(boolean z, CipherParameters cipherParameters) {
        super.init(z, cipherParameters);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int processByte(byte b, byte[] bArr, int i) throws DataLengthException {
        return super.processByte(b, bArr, i);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ void reset() {
        super.reset();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ byte[] getMac() {
        return super.getMac();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    public /* bridge */ /* synthetic */ int getIVBytesSize() {
        return super.getIVBytesSize();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    public /* bridge */ /* synthetic */ int getKeyBytesSize() {
        return super.getKeyBytesSize();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ String getAlgorithmName() {
        return super.getAlgorithmName();
    }
}
