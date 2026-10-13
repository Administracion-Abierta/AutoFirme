package org.spongycastle.crypto.engines;

import org.spongycastle.asn1.cmc.BodyPartID;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/engines/RijndaelEngine.class */
public class RijndaelEngine implements BlockCipher {
    private static final int MAXROUNDS = 14;
    private static final int MAXKC = 64;
    private static final byte[] logtable = {0, 0, 25, 1, 50, 2, 26, -58, 75, -57, 27, 104, 51, -18, -33, 3, 100, 4, -32, 14, 52, -115, -127, -17, 76, 113, 8, -56, -8, 105, 28, -63, 125, -62, 29, -75, -7, -71, 39, 106, 77, -28, -90, 114, -102, -55, 9, 120, 101, 47, -118, 5, 33, 15, -31, 36, 18, -16, -126, 69, 53, -109, -38, -114, -106, -113, -37, -67, 54, -48, -50, -108, 19, 92, -46, -15, 64, 70, -125, 56, 102, -35, -3, 48, -65, 6, -117, 98, -77, 37, -30, -104, 34, -120, -111, 16, 126, 110, 72, -61, -93, -74, 30, 66, 58, 107, 40, 84, -6, -123, 61, -70, 43, 121, 10, 21, -101, -97, 94, -54, 78, -44, -84, -27, -13, 115, -89, 87, -81, 88, -88, 80, -12, -22, -42, 116, 79, -82, -23, -43, -25, -26, -83, -24, 44, -41, 117, 122, -21, 22, 11, -11, 89, -53, 95, -80, -100, -87, 81, -96, 127, 12, -10, 111, 23, -60, 73, -20, -40, 67, 31, 45, -92, 118, 123, -73, -52, -69, 62, 90, -5, 96, -79, -122, 59, 82, -95, 108, -86, 85, 41, -99, -105, -78, -121, -112, 97, -66, -36, -4, -68, -107, -49, -51, 55, 63, 91, -47, 83, 57, -124, 60, 65, -94, 109, 71, 20, 42, -98, 93, 86, -14, -45, -85, 68, 17, -110, -39, 35, 32, 46, -119, -76, 124, -72, 38, 119, -103, -29, -91, 103, 74, -19, -34, -59, 49, -2, 24, 13, 99, -116, -128, -64, -9, 112, 7};
    private static final byte[] aLogtable = {0, 3, 5, 15, 17, 51, 85, -1, 26, 46, 114, -106, -95, -8, 19, 53, 95, -31, 56, 72, -40, 115, -107, -92, -9, 2, 6, 10, 30, 34, 102, -86, -27, 52, 92, -28, 55, 89, -21, 38, 106, -66, -39, 112, -112, -85, -26, 49, 83, -11, 4, 12, 20, 60, 68, -52, 79, -47, 104, -72, -45, 110, -78, -51, 76, -44, 103, -87, -32, 59, 77, -41, 98, -90, -15, 8, 24, 40, 120, -120, -125, -98, -71, -48, 107, -67, -36, 127, -127, -104, -77, -50, 73, -37, 118, -102, -75, -60, 87, -7, 16, 48, 80, -16, 11, 29, 39, 105, -69, -42, 97, -93, -2, 25, 43, 125, -121, -110, -83, -20, 47, 113, -109, -82, -23, 32, 96, -96, -5, 22, 58, 78, -46, 109, -73, -62, 93, -25, 50, 86, -6, 21, 63, 65, -61, 94, -30, 61, 71, -55, 64, -64, 91, -19, 44, 116, -100, -65, -38, 117, -97, -70, -43, 100, -84, -17, 42, 126, -126, -99, -68, -33, 122, -114, -119, -128, -101, -74, -63, 88, -24, 35, 101, -81, -22, 37, 111, -79, -56, 67, -59, 84, -4, 31, 33, 99, -91, -12, 7, 9, 27, 45, 119, -103, -80, -53, 70, -54, 69, -49, 74, -34, 121, -117, -122, -111, -88, -29, 62, 66, -58, 81, -13, 14, 18, 54, 90, -18, 41, 123, -115, -116, -113, -118, -123, -108, -89, -14, 13, 23, 57, 75, -35, 124, -124, -105, -94, -3, 28, 36, 108, -76, -57, 82, -10, 1, 3, 5, 15, 17, 51, 85, -1, 26, 46, 114, -106, -95, -8, 19, 53, 95, -31, 56, 72, -40, 115, -107, -92, -9, 2, 6, 10, 30, 34, 102, -86, -27, 52, 92, -28, 55, 89, -21, 38, 106, -66, -39, 112, -112, -85, -26, 49, 83, -11, 4, 12, 20, 60, 68, -52, 79, -47, 104, -72, -45, 110, -78, -51, 76, -44, 103, -87, -32, 59, 77, -41, 98, -90, -15, 8, 24, 40, 120, -120, -125, -98, -71, -48, 107, -67, -36, 127, -127, -104, -77, -50, 73, -37, 118, -102, -75, -60, 87, -7, 16, 48, 80, -16, 11, 29, 39, 105, -69, -42, 97, -93, -2, 25, 43, 125, -121, -110, -83, -20, 47, 113, -109, -82, -23, 32, 96, -96, -5, 22, 58, 78, -46, 109, -73, -62, 93, -25, 50, 86, -6, 21, 63, 65, -61, 94, -30, 61, 71, -55, 64, -64, 91, -19, 44, 116, -100, -65, -38, 117, -97, -70, -43, 100, -84, -17, 42, 126, -126, -99, -68, -33, 122, -114, -119, -128, -101, -74, -63, 88, -24, 35, 101, -81, -22, 37, 111, -79, -56, 67, -59, 84, -4, 31, 33, 99, -91, -12, 7, 9, 27, 45, 119, -103, -80, -53, 70, -54, 69, -49, 74, -34, 121, -117, -122, -111, -88, -29, 62, 66, -58, 81, -13, 14, 18, 54, 90, -18, 41, 123, -115, -116, -113, -118, -123, -108, -89, -14, 13, 23, 57, 75, -35, 124, -124, -105, -94, -3, 28, 36, 108, -76, -57, 82, -10, 1};
    private static final byte[] S = {99, 124, 119, 123, -14, 107, 111, -59, 48, 1, 103, 43, -2, -41, -85, 118, -54, -126, -55, 125, -6, 89, 71, -16, -83, -44, -94, -81, -100, -92, 114, -64, -73, -3, -109, 38, 54, 63, -9, -52, 52, -91, -27, -15, 113, -40, 49, 21, 4, -57, 35, -61, 24, -106, 5, -102, 7, 18, -128, -30, -21, 39, -78, 117, 9, -125, 44, 26, 27, 110, 90, -96, 82, 59, -42, -77, 41, -29, 47, -124, 83, -47, 0, -19, 32, -4, -79, 91, 106, -53, -66, 57, 74, 76, 88, -49, -48, -17, -86, -5, 67, 77, 51, -123, 69, -7, 2, 127, 80, 60, -97, -88, 81, -93, 64, -113, -110, -99, 56, -11, -68, -74, -38, 33, 16, -1, -13, -46, -51, 12, 19, -20, 95, -105, 68, 23, -60, -89, 126, 61, 100, 93, 25, 115, 96, -127, 79, -36, 34, 42, -112, -120, 70, -18, -72, 20, -34, 94, 11, -37, -32, 50, 58, 10, 73, 6, 36, 92, -62, -45, -84, 98, -111, -107, -28, 121, -25, -56, 55, 109, -115, -43, 78, -87, 108, 86, -12, -22, 101, 122, -82, 8, -70, 120, 37, 46, 28, -90, -76, -58, -24, -35, 116, 31, 75, -67, -117, -118, 112, 62, -75, 102, 72, 3, -10, 14, 97, 53, 87, -71, -122, -63, 29, -98, -31, -8, -104, 17, 105, -39, -114, -108, -101, 30, -121, -23, -50, 85, 40, -33, -116, -95, -119, 13, -65, -26, 66, 104, 65, -103, 45, 15, -80, 84, -69, 22};
    private static final byte[] Si = {82, 9, 106, -43, 48, 54, -91, 56, -65, 64, -93, -98, -127, -13, -41, -5, 124, -29, 57, -126, -101, 47, -1, -121, 52, -114, 67, 68, -60, -34, -23, -53, 84, 123, -108, 50, -90, -62, 35, 61, -18, 76, -107, 11, 66, -6, -61, 78, 8, 46, -95, 102, 40, -39, 36, -78, 118, 91, -94, 73, 109, -117, -47, 37, 114, -8, -10, 100, -122, 104, -104, 22, -44, -92, 92, -52, 93, 101, -74, -110, 108, 112, 72, 80, -3, -19, -71, -38, 94, 21, 70, 87, -89, -115, -99, -124, -112, -40, -85, 0, -116, -68, -45, 10, -9, -28, 88, 5, -72, -77, 69, 6, -48, 44, 30, -113, -54, 63, 15, 2, -63, -81, -67, 3, 1, 19, -118, 107, 58, -111, 17, 65, 79, 103, -36, -22, -105, -14, -49, -50, -16, -76, -26, 115, -106, -84, 116, 34, -25, -83, 53, -123, -30, -7, 55, -24, 28, 117, -33, 110, 71, -15, 26, 113, 29, 41, -59, -119, 111, -73, 98, 14, -86, 24, -66, 27, -4, 86, 62, 75, -58, -46, 121, 32, -102, -37, -64, -2, 120, -51, 90, -12, 31, -35, -88, 51, -120, 7, -57, 49, -79, 18, 16, 89, 39, -128, -20, 95, 96, 81, 127, -87, 25, -75, 74, 13, 45, -27, 122, -97, -109, -55, -100, -17, -96, -32, 59, 77, -82, 42, -11, -80, -56, -21, -69, 60, -125, 83, -103, 97, 23, 43, 4, 126, -70, 119, -42, 38, -31, 105, 20, 99, 85, 33, 12, 125};
    private static final int[] rcon = {1, 2, 4, 8, 16, 32, 64, 128, 27, 54, CipherSuite.TLS_DH_anon_WITH_AES_128_CBC_SHA256, 216, CipherSuite.TLS_DHE_PSK_WITH_AES_256_GCM_SHA384, 77, CipherSuite.TLS_DHE_RSA_WITH_SEED_CBC_SHA, 47, 94, 188, 99, 198, CipherSuite.TLS_DH_DSS_WITH_SEED_CBC_SHA, 53, CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA256, 212, CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA384, 125, 250, 239, CipherSuite.TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA256, CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA};
    static byte[][] shifts0 = {new byte[]{0, 8, 16, 24}, new byte[]{0, 8, 16, 24}, new byte[]{0, 8, 16, 24}, new byte[]{0, 8, 16, 32}, new byte[]{0, 8, 24, 32}};
    static byte[][] shifts1 = {new byte[]{0, 24, 16, 8}, new byte[]{0, 32, 24, 16}, new byte[]{0, 40, 32, 24}, new byte[]{0, 48, 40, 24}, new byte[]{0, 56, 40, 32}};
    private int BC;
    private long BC_MASK;
    private int ROUNDS;
    private int blockBits;
    private long[][] workingKey;
    private long A0;
    private long A1;
    private long A2;
    private long A3;
    private boolean forEncryption;
    private byte[] shifts0SC;
    private byte[] shifts1SC;

    private byte mul0x2(int b) {
        if (b != 0) {
            return aLogtable[25 + (logtable[b] & 255)];
        }
        return (byte) 0;
    }

    private byte mul0x3(int b) {
        if (b != 0) {
            return aLogtable[1 + (logtable[b] & 255)];
        }
        return (byte) 0;
    }

    private byte mul0x9(int b) {
        if (b >= 0) {
            return aLogtable[199 + b];
        }
        return (byte) 0;
    }

    private byte mul0xb(int b) {
        if (b >= 0) {
            return aLogtable[104 + b];
        }
        return (byte) 0;
    }

    private byte mul0xd(int b) {
        if (b >= 0) {
            return aLogtable[238 + b];
        }
        return (byte) 0;
    }

    private byte mul0xe(int b) {
        if (b >= 0) {
            return aLogtable[223 + b];
        }
        return (byte) 0;
    }

    private void KeyAddition(long[] rk) {
        this.A0 ^= rk[0];
        this.A1 ^= rk[1];
        this.A2 ^= rk[2];
        this.A3 ^= rk[3];
    }

    private long shift(long r, int shift) {
        return ((r >>> shift) | (r << (this.BC - shift))) & this.BC_MASK;
    }

    private void ShiftRow(byte[] shiftsSC) {
        this.A1 = shift(this.A1, shiftsSC[1]);
        this.A2 = shift(this.A2, shiftsSC[2]);
        this.A3 = shift(this.A3, shiftsSC[3]);
    }

    private long applyS(long r, byte[] box) {
        long res = 0;
        for (int j = 0; j < this.BC; j += 8) {
            res |= ((long) (box[(int) ((r >> j) & 255)] & 255)) << j;
        }
        return res;
    }

    private void Substitution(byte[] box) {
        this.A0 = applyS(this.A0, box);
        this.A1 = applyS(this.A1, box);
        this.A2 = applyS(this.A2, box);
        this.A3 = applyS(this.A3, box);
    }

    private void MixColumn() {
        long r3 = 0;
        long r2 = 0;
        long r1 = 0;
        long r0 = 0;
        for (int j = 0; j < this.BC; j += 8) {
            int a0 = (int) ((this.A0 >> j) & 255);
            int a1 = (int) ((this.A1 >> j) & 255);
            int a2 = (int) ((this.A2 >> j) & 255);
            int a3 = (int) ((this.A3 >> j) & 255);
            r0 |= ((long) ((((mul0x2(a0) ^ mul0x3(a1)) ^ a2) ^ a3) & 255)) << j;
            r1 |= ((long) ((((mul0x2(a1) ^ mul0x3(a2)) ^ a3) ^ a0) & 255)) << j;
            r2 |= ((long) ((((mul0x2(a2) ^ mul0x3(a3)) ^ a0) ^ a1) & 255)) << j;
            r3 |= ((long) ((((mul0x2(a3) ^ mul0x3(a0)) ^ a1) ^ a2) & 255)) << j;
        }
        this.A0 = r0;
        this.A1 = r1;
        this.A2 = r2;
        this.A3 = r3;
    }

    private void InvMixColumn() {
        long r3 = 0;
        long r2 = 0;
        long r1 = 0;
        long r0 = 0;
        for (int j = 0; j < this.BC; j += 8) {
            int a0 = (int) ((this.A0 >> j) & 255);
            int a1 = (int) ((this.A1 >> j) & 255);
            int a2 = (int) ((this.A2 >> j) & 255);
            int a3 = (int) ((this.A3 >> j) & 255);
            int a4 = a0 != 0 ? logtable[a0 & 255] & 255 : -1;
            int a5 = a1 != 0 ? logtable[a1 & 255] & 255 : -1;
            int a6 = a2 != 0 ? logtable[a2 & 255] & 255 : -1;
            int a7 = a3 != 0 ? logtable[a3 & 255] & 255 : -1;
            r0 |= ((long) ((((mul0xe(a4) ^ mul0xb(a5)) ^ mul0xd(a6)) ^ mul0x9(a7)) & 255)) << j;
            r1 |= ((long) ((((mul0xe(a5) ^ mul0xb(a6)) ^ mul0xd(a7)) ^ mul0x9(a4)) & 255)) << j;
            r2 |= ((long) ((((mul0xe(a6) ^ mul0xb(a7)) ^ mul0xd(a4)) ^ mul0x9(a5)) & 255)) << j;
            r3 |= ((long) ((((mul0xe(a7) ^ mul0xb(a4)) ^ mul0xd(a5)) ^ mul0x9(a6)) & 255)) << j;
        }
        this.A0 = r0;
        this.A1 = r1;
        this.A2 = r2;
        this.A3 = r3;
    }

    private long[][] generateWorkingKey(byte[] key) {
        int KC;
        int rconpointer = 0;
        int keyBits = key.length * 8;
        byte[][] tk = new byte[4][64];
        long[][] W = new long[15][4];
        switch (keyBits) {
            case 128:
                KC = 4;
                break;
            case CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256 /* 160 */:
                KC = 5;
                break;
            case 192:
                KC = 6;
                break;
            case 224:
                KC = 7;
                break;
            case 256:
                KC = 8;
                break;
            default:
                throw new IllegalArgumentException("Key length not 128/160/192/224/256 bits.");
        }
        if (keyBits >= this.blockBits) {
            this.ROUNDS = KC + 6;
        } else {
            this.ROUNDS = (this.BC / 8) + 6;
        }
        int index = 0;
        for (int i = 0; i < key.length; i++) {
            int i2 = index;
            index++;
            tk[i % 4][i / 4] = key[i2];
        }
        int t = 0;
        int j = 0;
        while (j < KC && t < (this.ROUNDS + 1) * (this.BC / 8)) {
            for (int i3 = 0; i3 < 4; i3++) {
                long[] jArr = W[t / (this.BC / 8)];
                int i4 = i3;
                jArr[i4] = jArr[i4] | (((long) (tk[i3][j] & 255)) << ((t * 8) % this.BC));
            }
            j++;
            t++;
        }
        while (t < (this.ROUNDS + 1) * (this.BC / 8)) {
            for (int i5 = 0; i5 < 4; i5++) {
                byte[] bArr = tk[i5];
                bArr[0] = (byte) (bArr[0] ^ S[tk[(i5 + 1) % 4][KC - 1] & 255]);
            }
            byte[] bArr2 = tk[0];
            int i6 = rconpointer;
            rconpointer++;
            bArr2[0] = (byte) (bArr2[0] ^ rcon[i6]);
            if (KC <= 6) {
                for (int j2 = 1; j2 < KC; j2++) {
                    for (int i7 = 0; i7 < 4; i7++) {
                        byte[] bArr3 = tk[i7];
                        int i8 = j2;
                        bArr3[i8] = (byte) (bArr3[i8] ^ tk[i7][j2 - 1]);
                    }
                }
            } else {
                for (int j3 = 1; j3 < 4; j3++) {
                    for (int i9 = 0; i9 < 4; i9++) {
                        byte[] bArr4 = tk[i9];
                        int i10 = j3;
                        bArr4[i10] = (byte) (bArr4[i10] ^ tk[i9][j3 - 1]);
                    }
                }
                for (int i11 = 0; i11 < 4; i11++) {
                    byte[] bArr5 = tk[i11];
                    bArr5[4] = (byte) (bArr5[4] ^ S[tk[i11][3] & 255]);
                }
                for (int j4 = 5; j4 < KC; j4++) {
                    for (int i12 = 0; i12 < 4; i12++) {
                        byte[] bArr6 = tk[i12];
                        int i13 = j4;
                        bArr6[i13] = (byte) (bArr6[i13] ^ tk[i12][j4 - 1]);
                    }
                }
            }
            int j5 = 0;
            while (j5 < KC && t < (this.ROUNDS + 1) * (this.BC / 8)) {
                for (int i14 = 0; i14 < 4; i14++) {
                    long[] jArr2 = W[t / (this.BC / 8)];
                    int i15 = i14;
                    jArr2[i15] = jArr2[i15] | (((long) (tk[i14][j5] & 255)) << ((t * 8) % this.BC));
                }
                j5++;
                t++;
            }
        }
        return W;
    }

    public RijndaelEngine() {
        this(128);
    }

    public RijndaelEngine(int blockBits) {
        switch (blockBits) {
            case 128:
                this.BC = 32;
                this.BC_MASK = BodyPartID.bodyIdMax;
                this.shifts0SC = shifts0[0];
                this.shifts1SC = shifts1[0];
                break;
            case CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256 /* 160 */:
                this.BC = 40;
                this.BC_MASK = 1099511627775L;
                this.shifts0SC = shifts0[1];
                this.shifts1SC = shifts1[1];
                break;
            case 192:
                this.BC = 48;
                this.BC_MASK = 281474976710655L;
                this.shifts0SC = shifts0[2];
                this.shifts1SC = shifts1[2];
                break;
            case 224:
                this.BC = 56;
                this.BC_MASK = 72057594037927935L;
                this.shifts0SC = shifts0[3];
                this.shifts1SC = shifts1[3];
                break;
            case 256:
                this.BC = 64;
                this.BC_MASK = -1L;
                this.shifts0SC = shifts0[4];
                this.shifts1SC = shifts1[4];
                break;
            default:
                throw new IllegalArgumentException("unknown blocksize to Rijndael");
        }
        this.blockBits = blockBits;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean forEncryption, CipherParameters params) {
        if (params instanceof KeyParameter) {
            this.workingKey = generateWorkingKey(((KeyParameter) params).getKey());
            this.forEncryption = forEncryption;
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to Rijndael init - " + params.getClass().getName());
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "Rijndael";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return this.BC / 2;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (this.workingKey == null) {
            throw new IllegalStateException("Rijndael engine not initialised");
        }
        if (inOff + (this.BC / 2) > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + (this.BC / 2) > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.forEncryption) {
            unpackBlock(in, inOff);
            encryptBlock(this.workingKey);
            packBlock(out, outOff);
        } else {
            unpackBlock(in, inOff);
            decryptBlock(this.workingKey);
            packBlock(out, outOff);
        }
        return this.BC / 2;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
    }

    private void unpackBlock(byte[] bytes, int off) {
        int index = off + 1;
        this.A0 = bytes[off] & 255;
        int index2 = index + 1;
        this.A1 = bytes[index] & 255;
        int index3 = index2 + 1;
        this.A2 = bytes[index2] & 255;
        int index4 = index3 + 1;
        this.A3 = bytes[index3] & 255;
        for (int j = 8; j != this.BC; j += 8) {
            int i = index4;
            int index5 = index4 + 1;
            this.A0 |= ((long) (bytes[i] & 255)) << j;
            int index6 = index5 + 1;
            this.A1 |= ((long) (bytes[index5] & 255)) << j;
            int index7 = index6 + 1;
            this.A2 |= ((long) (bytes[index6] & 255)) << j;
            index4 = index7 + 1;
            this.A3 |= ((long) (bytes[index7] & 255)) << j;
        }
    }

    private void packBlock(byte[] bytes, int off) {
        int index = off;
        for (int j = 0; j != this.BC; j += 8) {
            int i = index;
            int index2 = index + 1;
            bytes[i] = (byte) (this.A0 >> j);
            int index3 = index2 + 1;
            bytes[index2] = (byte) (this.A1 >> j);
            int index4 = index3 + 1;
            bytes[index3] = (byte) (this.A2 >> j);
            index = index4 + 1;
            bytes[index4] = (byte) (this.A3 >> j);
        }
    }

    private void encryptBlock(long[][] rk) {
        KeyAddition(rk[0]);
        for (int r = 1; r < this.ROUNDS; r++) {
            Substitution(S);
            ShiftRow(this.shifts0SC);
            MixColumn();
            KeyAddition(rk[r]);
        }
        Substitution(S);
        ShiftRow(this.shifts0SC);
        KeyAddition(rk[this.ROUNDS]);
    }

    private void decryptBlock(long[][] rk) {
        KeyAddition(rk[this.ROUNDS]);
        Substitution(Si);
        ShiftRow(this.shifts1SC);
        for (int r = this.ROUNDS - 1; r > 0; r--) {
            KeyAddition(rk[r]);
            InvMixColumn();
            Substitution(Si);
            ShiftRow(this.shifts1SC);
        }
        KeyAddition(rk[0]);
    }
}
