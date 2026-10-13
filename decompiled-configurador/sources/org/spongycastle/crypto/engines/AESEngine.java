package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/engines/AESEngine.class */
public class AESEngine implements BlockCipher {
    private static final byte[] S = {99, 124, 119, 123, -14, 107, 111, -59, 48, 1, 103, 43, -2, -41, -85, 118, -54, -126, -55, 125, -6, 89, 71, -16, -83, -44, -94, -81, -100, -92, 114, -64, -73, -3, -109, 38, 54, 63, -9, -52, 52, -91, -27, -15, 113, -40, 49, 21, 4, -57, 35, -61, 24, -106, 5, -102, 7, 18, -128, -30, -21, 39, -78, 117, 9, -125, 44, 26, 27, 110, 90, -96, 82, 59, -42, -77, 41, -29, 47, -124, 83, -47, 0, -19, 32, -4, -79, 91, 106, -53, -66, 57, 74, 76, 88, -49, -48, -17, -86, -5, 67, 77, 51, -123, 69, -7, 2, 127, 80, 60, -97, -88, 81, -93, 64, -113, -110, -99, 56, -11, -68, -74, -38, 33, 16, -1, -13, -46, -51, 12, 19, -20, 95, -105, 68, 23, -60, -89, 126, 61, 100, 93, 25, 115, 96, -127, 79, -36, 34, 42, -112, -120, 70, -18, -72, 20, -34, 94, 11, -37, -32, 50, 58, 10, 73, 6, 36, 92, -62, -45, -84, 98, -111, -107, -28, 121, -25, -56, 55, 109, -115, -43, 78, -87, 108, 86, -12, -22, 101, 122, -82, 8, -70, 120, 37, 46, 28, -90, -76, -58, -24, -35, 116, 31, 75, -67, -117, -118, 112, 62, -75, 102, 72, 3, -10, 14, 97, 53, 87, -71, -122, -63, 29, -98, -31, -8, -104, 17, 105, -39, -114, -108, -101, 30, -121, -23, -50, 85, 40, -33, -116, -95, -119, 13, -65, -26, 66, 104, 65, -103, 45, 15, -80, 84, -69, 22};
    private static final byte[] Si = {82, 9, 106, -43, 48, 54, -91, 56, -65, 64, -93, -98, -127, -13, -41, -5, 124, -29, 57, -126, -101, 47, -1, -121, 52, -114, 67, 68, -60, -34, -23, -53, 84, 123, -108, 50, -90, -62, 35, 61, -18, 76, -107, 11, 66, -6, -61, 78, 8, 46, -95, 102, 40, -39, 36, -78, 118, 91, -94, 73, 109, -117, -47, 37, 114, -8, -10, 100, -122, 104, -104, 22, -44, -92, 92, -52, 93, 101, -74, -110, 108, 112, 72, 80, -3, -19, -71, -38, 94, 21, 70, 87, -89, -115, -99, -124, -112, -40, -85, 0, -116, -68, -45, 10, -9, -28, 88, 5, -72, -77, 69, 6, -48, 44, 30, -113, -54, 63, 15, 2, -63, -81, -67, 3, 1, 19, -118, 107, 58, -111, 17, 65, 79, 103, -36, -22, -105, -14, -49, -50, -16, -76, -26, 115, -106, -84, 116, 34, -25, -83, 53, -123, -30, -7, 55, -24, 28, 117, -33, 110, 71, -15, 26, 113, 29, 41, -59, -119, 111, -73, 98, 14, -86, 24, -66, 27, -4, 86, 62, 75, -58, -46, 121, 32, -102, -37, -64, -2, 120, -51, 90, -12, 31, -35, -88, 51, -120, 7, -57, 49, -79, 18, 16, 89, 39, -128, -20, 95, 96, 81, 127, -87, 25, -75, 74, 13, 45, -27, 122, -97, -109, -55, -100, -17, -96, -32, 59, 77, -82, 42, -11, -80, -56, -21, -69, 60, -125, 83, -103, 97, 23, 43, 4, 126, -70, 119, -42, 38, -31, 105, 20, 99, 85, 33, 12, 125};
    private static final int[] rcon = {1, 2, 4, 8, 16, 32, 64, 128, 27, 54, CipherSuite.TLS_DH_anon_WITH_AES_128_CBC_SHA256, 216, CipherSuite.TLS_DHE_PSK_WITH_AES_256_GCM_SHA384, 77, CipherSuite.TLS_DHE_RSA_WITH_SEED_CBC_SHA, 47, 94, 188, 99, 198, CipherSuite.TLS_DH_DSS_WITH_SEED_CBC_SHA, 53, CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA256, 212, CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA384, 125, 250, 239, CipherSuite.TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA256, CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA};
    private static final int[] T0 = {-1520213050, -2072216328, -1720223762, -1921287178, 234025727, -1117033514, -1318096930, 1422247313, 1345335392, 50397442, -1452841010, 2099981142, 436141799, 1658312629, -424957107, -1703512340, 1170918031, -1652391393, 1086966153, -2021818886, 368769775, -346465870, -918075506, 200339707, -324162239, 1742001331, -39673249, -357585083, -1080255453, -140204973, -1770884380, 1539358875, -1028147339, 486407649, -1366060227, 1780885068, 1513502316, 1094664062, 49805301, 1338821763, 1546925160, -190470831, 887481809, 150073849, -1821281822, 1943591083, 1395732834, 1058346282, 201589768, 1388824469, 1696801606, 1589887901, 672667696, -1583966665, 251987210, -1248159185, 151455502, 907153956, -1686077413, 1038279391, 652995533, 1764173646, -843926913, -1619692054, 453576978, -1635548387, 1949051992, 773462580, 756751158, -1301385508, -296068428, -73359269, -162377052, 1295727478, 1641469623, -827083907, 2066295122, 1055122397, 1898917726, -1752923117, -179088474, 1758581177, 0, 753790401, 1612718144, 536673507, -927878791, -312779850, -1100322092, 1187761037, -641810841, 1262041458, -565556588, -733197160, -396863312, 1255133061, 1808847035, 720367557, -441800113, 385612781, -985447546, -682799718, 1429418854, -1803188975, -817543798, 284817897, 100794884, -2122350594, -263171936, 1144798328, -1163944155, -475486133, -212774494, -22830243, -1069531008, -1970303227, -1382903233, -1130521311, 1211644016, 83228145, -541279133, -1044990345, 1977277103, 1663115586, 806359072, 452984805, 250868733, 1842533055, 1288555905, 336333848, 890442534, 804056259, -513843266, -1567123659, -867941240, 957814574, 1472513171, -223893675, -2105639172, 1195195770, -1402706744, -413311558, 723065138, -1787595802, -1604296512, -1736343271, -783331426, 2145180835, 1713513028, 2116692564, -1416589253, -2088204277, -901364084, 703524551, -742868885, 1007948840, 2044649127, -497131844, 487262998, 1994120109, 1004593371, 1446130276, 1312438900, 503974420, -615954030, 168166924, 1814307912, -463709000, 1573044895, 1859376061, -273896381, -1503501628, -1466855111, -1533700815, 937747667, -1954973198, 854058965, 1137232011, 1496790894, -1217565222, -1936880383, 1691735473, -766620004, -525751991, -1267962664, -95005012, 133494003, 636152527, -1352309302, -1904575756, -374428089, 403179536, -709182865, -2005370640, 1864705354, 1915629148, 605822008, -240736681, -944458637, 1371981463, 602466507, 2094914977, -1670089496, 555687742, -582268010, -591544991, -2037675251, -2054518257, -1871679264, 1111375484, -994724495, -1436129588, -666351472, 84083462, 32962295, 302911004, -1553899070, 1597322602, -111716434, -793134743, -1853454825, 1489093017, 656219450, -1180787161, 954327513, 335083755, -1281845205, 856756514, -1150719534, 1893325225, -1987146233, -1483434957, -1231316179, 572399164, -1836611819, 552200649, 1238290055, -11184726, 2015897680, 2061492133, -1886614525, -123625127, -2138470135, 386731290, -624967835, 837215959, -968736124, -1201116976, -1019133566, -1332111063, 1999449434, 286199582, -877612933, -61582168, -692339859, 974525996};
    private static final int[] Tinv0 = {1353184337, 1399144830, -1012656358, -1772214470, -882136261, -247096033, -1420232020, -1828461749, 1442459680, -160598355, -1854485368, 625738485, -52959921, -674551099, -2143013594, -1885117771, 1230680542, 1729870373, -1743852987, -507445667, 41234371, 317738113, -1550367091, -956705941, -413167869, -1784901099, -344298049, -631680363, 763608788, -752782248, 694804553, 1154009486, 1787413109, 2021232372, 1799248025, -579749593, -1236278850, 397248752, 1722556617, -1271214467, 407560035, -2110711067, 1613975959, 1165972322, -529046351, -2068943941, 480281086, -1809118983, 1483229296, 436028815, -2022908268, -1208452270, 601060267, -503166094, 1468997603, 715871590, 120122290, 63092015, -1703164538, -1526188077, -226023376, -1297760477, -1167457534, 1552029421, 723308426, -1833666137, -252573709, -1578997426, -839591323, -708967162, 526529745, -1963022652, -1655493068, -1604979806, 853641733, 1978398372, 971801355, -1427152832, 111112542, 1360031421, -108388034, 1023860118, -1375387939, 1186850381, -1249028975, 90031217, 1876166148, -15380384, 620468249, -1746289194, -868007799, 2006899047, -1119688528, -2004121337, 945494503, -605108103, 1191869601, -384875908, -920746760, 0, -2088337399, 1223502642, -1401941730, 1316117100, -67170563, 1446544655, 517320253, 658058550, 1691946762, 564550760, -783000677, 976107044, -1318647284, 266819475, -761860428, -1634624741, 1338359936, -1574904735, 1766553434, 370807324, 179999714, -450191168, 1138762300, 488053522, 185403662, -1379431438, -1180125651, -928440812, -2061897385, 1275557295, -1143105042, -44007517, -1624899081, -1124765092, -985962940, 880737115, 1982415755, -590994485, 1761406390, 1676797112, -891538985, 277177154, 1076008723, 538035844, 2099530373, -130171950, 288553390, 1839278535, 1261411869, -214912292, -330136051, -790380169, 1813426987, -1715900247, -95906799, 577038663, -997393240, 440397984, -668172970, -275762398, -951170681, -1043253031, -22885748, 906744984, -813566554, 685669029, 646887386, -1530942145, -459458004, 227702864, -1681105046, 1648787028, -1038905866, -390539120, 1593260334, -173030526, -1098883681, 2090061929, -1456614033, -1290656305, 999926984, -1484974064, 1852021992, 2075868123, 158869197, -199730834, 28809964, -1466282109, 1701746150, 2129067946, 147831841, -420997649, -644094022, -835293366, -737566742, -696471511, -1347247055, 824393514, 815048134, -1067015627, 935087732, -1496677636, -1328508704, 366520115, 1251476721, -136647615, 240176511, 804688151, -1915335306, 1303441219, 1414376140, -553347356, -474623586, 461924940, -1205916479, 2136040774, 82468509, 1563790337, 1937016826, 776014843, 1511876531, 1389550482, 861278441, 323475053, -1939744870, 2047648055, -1911228327, -1992551445, -299390514, 902390199, -303751967, 1018251130, 1507840668, 1064563285, 2043548696, -1086863501, -355600557, 1537932639, 342834655, -2032450440, -2114736182, 1053059257, 741614648, 1598071746, 1925389590, 203809468, -1958134744, 1100287487, 1895934009, -558691320, -1662733096, -1866377628, 1636092795, 1890988757, 1952214088, 1113045200};
    private static final int m1 = -2139062144;
    private static final int m2 = 2139062143;
    private static final int m3 = 27;
    private static final int m4 = -1061109568;
    private static final int m5 = 1061109567;
    private int ROUNDS;
    private int[][] WorkingKey = (int[][]) null;
    private int C0;
    private int C1;
    private int C2;
    private int C3;
    private boolean forEncryption;
    private byte[] s;
    private static final int BLOCK_SIZE = 16;

    private static int shift(int r, int shift) {
        return (r >>> shift) | (r << (-shift));
    }

    private static int FFmulX(int x) {
        return ((x & m2) << 1) ^ (((x & m1) >>> 7) * 27);
    }

    private static int FFmulX2(int x) {
        int t0 = (x & m5) << 2;
        int t1 = x & m4;
        int t2 = t1 ^ (t1 >>> 1);
        return (t0 ^ (t2 >>> 2)) ^ (t2 >>> 5);
    }

    private static int inv_mcol(int x) {
        int t1 = x ^ shift(x, 8);
        int t0 = x ^ FFmulX(t1);
        int t2 = t1 ^ FFmulX2(t0);
        return t0 ^ (t2 ^ shift(t2, 16));
    }

    private static int subWord(int x) {
        return (S[x & 255] & 255) | ((S[(x >> 8) & 255] & 255) << 8) | ((S[(x >> 16) & 255] & 255) << 16) | (S[(x >> 24) & 255] << 24);
    }

    private int[][] generateWorkingKey(byte[] key, boolean forEncryption) {
        int keyLen = key.length;
        if (keyLen < 16 || keyLen > 32 || (keyLen & 7) != 0) {
            throw new IllegalArgumentException("Key length not 128/192/256 bits.");
        }
        int KC = keyLen >>> 2;
        this.ROUNDS = KC + 6;
        int[][] W = new int[this.ROUNDS + 1][4];
        switch (KC) {
            case 4:
                int t0 = Pack.littleEndianToInt(key, 0);
                W[0][0] = t0;
                int t1 = Pack.littleEndianToInt(key, 4);
                W[0][1] = t1;
                int t2 = Pack.littleEndianToInt(key, 8);
                W[0][2] = t2;
                int t3 = Pack.littleEndianToInt(key, 12);
                W[0][3] = t3;
                for (int i = 1; i <= 10; i++) {
                    int u = subWord(shift(t3, 8)) ^ rcon[i - 1];
                    t0 ^= u;
                    W[i][0] = t0;
                    t1 ^= t0;
                    W[i][1] = t1;
                    t2 ^= t1;
                    W[i][2] = t2;
                    t3 ^= t2;
                    W[i][3] = t3;
                }
                break;
            case 5:
            case 7:
            default:
                throw new IllegalStateException("Should never get here");
            case 6:
                int t4 = Pack.littleEndianToInt(key, 0);
                W[0][0] = t4;
                int t5 = Pack.littleEndianToInt(key, 4);
                W[0][1] = t5;
                int t6 = Pack.littleEndianToInt(key, 8);
                W[0][2] = t6;
                int t7 = Pack.littleEndianToInt(key, 12);
                W[0][3] = t7;
                int t8 = Pack.littleEndianToInt(key, 16);
                W[1][0] = t8;
                int t9 = Pack.littleEndianToInt(key, 20);
                W[1][1] = t9;
                int u2 = subWord(shift(t9, 8)) ^ 1;
                int rcon2 = 1 << 1;
                int t10 = t4 ^ u2;
                W[1][2] = t10;
                int t11 = t5 ^ t10;
                W[1][3] = t11;
                int t12 = t6 ^ t11;
                W[2][0] = t12;
                int t13 = t7 ^ t12;
                W[2][1] = t13;
                int t14 = t8 ^ t13;
                W[2][2] = t14;
                int t15 = t9 ^ t14;
                W[2][3] = t15;
                for (int i2 = 3; i2 < 12; i2 += 3) {
                    int u3 = subWord(shift(t15, 8)) ^ rcon2;
                    int rcon3 = rcon2 << 1;
                    int t16 = t10 ^ u3;
                    W[i2][0] = t16;
                    int t17 = t11 ^ t16;
                    W[i2][1] = t17;
                    int t18 = t12 ^ t17;
                    W[i2][2] = t18;
                    int t19 = t13 ^ t18;
                    W[i2][3] = t19;
                    int t20 = t14 ^ t19;
                    W[i2 + 1][0] = t20;
                    int t21 = t15 ^ t20;
                    W[i2 + 1][1] = t21;
                    int u4 = subWord(shift(t21, 8)) ^ rcon3;
                    rcon2 = rcon3 << 1;
                    t10 = t16 ^ u4;
                    W[i2 + 1][2] = t10;
                    t11 = t17 ^ t10;
                    W[i2 + 1][3] = t11;
                    t12 = t18 ^ t11;
                    W[i2 + 2][0] = t12;
                    t13 = t19 ^ t12;
                    W[i2 + 2][1] = t13;
                    t14 = t20 ^ t13;
                    W[i2 + 2][2] = t14;
                    t15 = t21 ^ t14;
                    W[i2 + 2][3] = t15;
                }
                int u5 = subWord(shift(t15, 8)) ^ rcon2;
                int t22 = t10 ^ u5;
                W[12][0] = t22;
                int t23 = t11 ^ t22;
                W[12][1] = t23;
                int t24 = t12 ^ t23;
                W[12][2] = t24;
                W[12][3] = t13 ^ t24;
                break;
            case 8:
                int t25 = Pack.littleEndianToInt(key, 0);
                W[0][0] = t25;
                int t26 = Pack.littleEndianToInt(key, 4);
                W[0][1] = t26;
                int t27 = Pack.littleEndianToInt(key, 8);
                W[0][2] = t27;
                int t28 = Pack.littleEndianToInt(key, 12);
                W[0][3] = t28;
                int t29 = Pack.littleEndianToInt(key, 16);
                W[1][0] = t29;
                int t30 = Pack.littleEndianToInt(key, 20);
                W[1][1] = t30;
                int t31 = Pack.littleEndianToInt(key, 24);
                W[1][2] = t31;
                int t32 = Pack.littleEndianToInt(key, 28);
                W[1][3] = t32;
                int rcon4 = 1;
                for (int i3 = 2; i3 < 14; i3 += 2) {
                    int u6 = subWord(shift(t32, 8)) ^ rcon4;
                    rcon4 <<= 1;
                    t25 ^= u6;
                    W[i3][0] = t25;
                    t26 ^= t25;
                    W[i3][1] = t26;
                    t27 ^= t26;
                    W[i3][2] = t27;
                    t28 ^= t27;
                    W[i3][3] = t28;
                    int u7 = subWord(t28);
                    t29 ^= u7;
                    W[i3 + 1][0] = t29;
                    t30 ^= t29;
                    W[i3 + 1][1] = t30;
                    t31 ^= t30;
                    W[i3 + 1][2] = t31;
                    t32 ^= t31;
                    W[i3 + 1][3] = t32;
                }
                int u8 = subWord(shift(t32, 8)) ^ rcon4;
                int t33 = t25 ^ u8;
                W[14][0] = t33;
                int t34 = t26 ^ t33;
                W[14][1] = t34;
                int t35 = t27 ^ t34;
                W[14][2] = t35;
                W[14][3] = t28 ^ t35;
                break;
        }
        if (!forEncryption) {
            for (int j = 1; j < this.ROUNDS; j++) {
                for (int i4 = 0; i4 < 4; i4++) {
                    W[j][i4] = inv_mcol(W[j][i4]);
                }
            }
        }
        return W;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean forEncryption, CipherParameters params) {
        if (params instanceof KeyParameter) {
            this.WorkingKey = generateWorkingKey(((KeyParameter) params).getKey(), forEncryption);
            this.forEncryption = forEncryption;
            if (forEncryption) {
                this.s = Arrays.clone(S);
                return;
            } else {
                this.s = Arrays.clone(Si);
                return;
            }
        }
        throw new IllegalArgumentException("invalid parameter passed to AES init - " + params.getClass().getName());
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "AES";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 16;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (this.WorkingKey == null) {
            throw new IllegalStateException("AES engine not initialised");
        }
        if (inOff + 16 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + 16 > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.forEncryption) {
            unpackBlock(in, inOff);
            encryptBlock(this.WorkingKey);
            packBlock(out, outOff);
            return 16;
        }
        unpackBlock(in, inOff);
        decryptBlock(this.WorkingKey);
        packBlock(out, outOff);
        return 16;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
    }

    private void unpackBlock(byte[] bytes, int off) {
        int index = off + 1;
        this.C0 = bytes[off] & 255;
        int index2 = index + 1;
        this.C0 |= (bytes[index] & 255) << 8;
        int index3 = index2 + 1;
        this.C0 |= (bytes[index2] & 255) << 16;
        int index4 = index3 + 1;
        this.C0 |= bytes[index3] << 24;
        int index5 = index4 + 1;
        this.C1 = bytes[index4] & 255;
        int index6 = index5 + 1;
        this.C1 |= (bytes[index5] & 255) << 8;
        int index7 = index6 + 1;
        this.C1 |= (bytes[index6] & 255) << 16;
        int index8 = index7 + 1;
        this.C1 |= bytes[index7] << 24;
        int index9 = index8 + 1;
        this.C2 = bytes[index8] & 255;
        int index10 = index9 + 1;
        this.C2 |= (bytes[index9] & 255) << 8;
        int index11 = index10 + 1;
        this.C2 |= (bytes[index10] & 255) << 16;
        int index12 = index11 + 1;
        this.C2 |= bytes[index11] << 24;
        int index13 = index12 + 1;
        this.C3 = bytes[index12] & 255;
        int index14 = index13 + 1;
        this.C3 |= (bytes[index13] & 255) << 8;
        int index15 = index14 + 1;
        this.C3 |= (bytes[index14] & 255) << 16;
        int i = index15 + 1;
        this.C3 |= bytes[index15] << 24;
    }

    private void packBlock(byte[] bytes, int off) {
        int index = off + 1;
        bytes[off] = (byte) this.C0;
        int index2 = index + 1;
        bytes[index] = (byte) (this.C0 >> 8);
        int index3 = index2 + 1;
        bytes[index2] = (byte) (this.C0 >> 16);
        int index4 = index3 + 1;
        bytes[index3] = (byte) (this.C0 >> 24);
        int index5 = index4 + 1;
        bytes[index4] = (byte) this.C1;
        int index6 = index5 + 1;
        bytes[index5] = (byte) (this.C1 >> 8);
        int index7 = index6 + 1;
        bytes[index6] = (byte) (this.C1 >> 16);
        int index8 = index7 + 1;
        bytes[index7] = (byte) (this.C1 >> 24);
        int index9 = index8 + 1;
        bytes[index8] = (byte) this.C2;
        int index10 = index9 + 1;
        bytes[index9] = (byte) (this.C2 >> 8);
        int index11 = index10 + 1;
        bytes[index10] = (byte) (this.C2 >> 16);
        int index12 = index11 + 1;
        bytes[index11] = (byte) (this.C2 >> 24);
        int index13 = index12 + 1;
        bytes[index12] = (byte) this.C3;
        int index14 = index13 + 1;
        bytes[index13] = (byte) (this.C3 >> 8);
        int index15 = index14 + 1;
        bytes[index14] = (byte) (this.C3 >> 16);
        int i = index15 + 1;
        bytes[index15] = (byte) (this.C3 >> 24);
    }

    private void encryptBlock(int[][] KW) {
        int t0 = this.C0 ^ KW[0][0];
        int t1 = this.C1 ^ KW[0][1];
        int t2 = this.C2 ^ KW[0][2];
        int r = 1;
        int iShift = this.C3;
        int i = KW[0][3];
        while (true) {
            int r3 = iShift ^ i;
            if (r < this.ROUNDS - 1) {
                int r0 = (((T0[t0 & 255] ^ shift(T0[(t1 >> 8) & 255], 24)) ^ shift(T0[(t2 >> 16) & 255], 16)) ^ shift(T0[(r3 >> 24) & 255], 8)) ^ KW[r][0];
                int r1 = (((T0[t1 & 255] ^ shift(T0[(t2 >> 8) & 255], 24)) ^ shift(T0[(r3 >> 16) & 255], 16)) ^ shift(T0[(t0 >> 24) & 255], 8)) ^ KW[r][1];
                int r2 = (((T0[t2 & 255] ^ shift(T0[(r3 >> 8) & 255], 24)) ^ shift(T0[(t0 >> 16) & 255], 16)) ^ shift(T0[(t1 >> 24) & 255], 8)) ^ KW[r][2];
                int i2 = r;
                int r4 = r + 1;
                int r5 = (((T0[r3 & 255] ^ shift(T0[(t0 >> 8) & 255], 24)) ^ shift(T0[(t1 >> 16) & 255], 16)) ^ shift(T0[(t2 >> 24) & 255], 8)) ^ KW[i2][3];
                t0 = (((T0[r0 & 255] ^ shift(T0[(r1 >> 8) & 255], 24)) ^ shift(T0[(r2 >> 16) & 255], 16)) ^ shift(T0[(r5 >> 24) & 255], 8)) ^ KW[r4][0];
                t1 = (((T0[r1 & 255] ^ shift(T0[(r2 >> 8) & 255], 24)) ^ shift(T0[(r5 >> 16) & 255], 16)) ^ shift(T0[(r0 >> 24) & 255], 8)) ^ KW[r4][1];
                t2 = (((T0[r2 & 255] ^ shift(T0[(r5 >> 8) & 255], 24)) ^ shift(T0[(r0 >> 16) & 255], 16)) ^ shift(T0[(r1 >> 24) & 255], 8)) ^ KW[r4][2];
                iShift = ((T0[r5 & 255] ^ shift(T0[(r0 >> 8) & 255], 24)) ^ shift(T0[(r1 >> 16) & 255], 16)) ^ shift(T0[(r2 >> 24) & 255], 8);
                r = r4 + 1;
                i = KW[r4][3];
            } else {
                int r6 = (((T0[t0 & 255] ^ shift(T0[(t1 >> 8) & 255], 24)) ^ shift(T0[(t2 >> 16) & 255], 16)) ^ shift(T0[(r3 >> 24) & 255], 8)) ^ KW[r][0];
                int r7 = (((T0[t1 & 255] ^ shift(T0[(t2 >> 8) & 255], 24)) ^ shift(T0[(r3 >> 16) & 255], 16)) ^ shift(T0[(t0 >> 24) & 255], 8)) ^ KW[r][1];
                int r8 = (((T0[t2 & 255] ^ shift(T0[(r3 >> 8) & 255], 24)) ^ shift(T0[(t0 >> 16) & 255], 16)) ^ shift(T0[(t1 >> 24) & 255], 8)) ^ KW[r][2];
                int i3 = r;
                int r9 = r + 1;
                int r10 = (((T0[r3 & 255] ^ shift(T0[(t0 >> 8) & 255], 24)) ^ shift(T0[(t1 >> 16) & 255], 16)) ^ shift(T0[(t2 >> 24) & 255], 8)) ^ KW[i3][3];
                this.C0 = ((((S[r6 & 255] & 255) ^ ((S[(r7 >> 8) & 255] & 255) << 8)) ^ ((this.s[(r8 >> 16) & 255] & 255) << 16)) ^ (this.s[(r10 >> 24) & 255] << 24)) ^ KW[r9][0];
                this.C1 = ((((this.s[r7 & 255] & 255) ^ ((S[(r8 >> 8) & 255] & 255) << 8)) ^ ((S[(r10 >> 16) & 255] & 255) << 16)) ^ (this.s[(r6 >> 24) & 255] << 24)) ^ KW[r9][1];
                this.C2 = ((((this.s[r8 & 255] & 255) ^ ((S[(r10 >> 8) & 255] & 255) << 8)) ^ ((S[(r6 >> 16) & 255] & 255) << 16)) ^ (S[(r7 >> 24) & 255] << 24)) ^ KW[r9][2];
                this.C3 = ((((this.s[r10 & 255] & 255) ^ ((this.s[(r6 >> 8) & 255] & 255) << 8)) ^ ((this.s[(r7 >> 16) & 255] & 255) << 16)) ^ (S[(r8 >> 24) & 255] << 24)) ^ KW[r9][3];
                return;
            }
        }
    }

    private void decryptBlock(int[][] KW) {
        int t0 = this.C0 ^ KW[this.ROUNDS][0];
        int t1 = this.C1 ^ KW[this.ROUNDS][1];
        int t2 = this.C2 ^ KW[this.ROUNDS][2];
        int r = this.ROUNDS - 1;
        int iShift = this.C3;
        int i = KW[this.ROUNDS][3];
        while (true) {
            int r3 = iShift ^ i;
            if (r > 1) {
                int r0 = (((Tinv0[t0 & 255] ^ shift(Tinv0[(r3 >> 8) & 255], 24)) ^ shift(Tinv0[(t2 >> 16) & 255], 16)) ^ shift(Tinv0[(t1 >> 24) & 255], 8)) ^ KW[r][0];
                int r1 = (((Tinv0[t1 & 255] ^ shift(Tinv0[(t0 >> 8) & 255], 24)) ^ shift(Tinv0[(r3 >> 16) & 255], 16)) ^ shift(Tinv0[(t2 >> 24) & 255], 8)) ^ KW[r][1];
                int r2 = (((Tinv0[t2 & 255] ^ shift(Tinv0[(t1 >> 8) & 255], 24)) ^ shift(Tinv0[(t0 >> 16) & 255], 16)) ^ shift(Tinv0[(r3 >> 24) & 255], 8)) ^ KW[r][2];
                int i2 = r;
                int r4 = r - 1;
                int r5 = (((Tinv0[r3 & 255] ^ shift(Tinv0[(t2 >> 8) & 255], 24)) ^ shift(Tinv0[(t1 >> 16) & 255], 16)) ^ shift(Tinv0[(t0 >> 24) & 255], 8)) ^ KW[i2][3];
                t0 = (((Tinv0[r0 & 255] ^ shift(Tinv0[(r5 >> 8) & 255], 24)) ^ shift(Tinv0[(r2 >> 16) & 255], 16)) ^ shift(Tinv0[(r1 >> 24) & 255], 8)) ^ KW[r4][0];
                t1 = (((Tinv0[r1 & 255] ^ shift(Tinv0[(r0 >> 8) & 255], 24)) ^ shift(Tinv0[(r5 >> 16) & 255], 16)) ^ shift(Tinv0[(r2 >> 24) & 255], 8)) ^ KW[r4][1];
                t2 = (((Tinv0[r2 & 255] ^ shift(Tinv0[(r1 >> 8) & 255], 24)) ^ shift(Tinv0[(r0 >> 16) & 255], 16)) ^ shift(Tinv0[(r5 >> 24) & 255], 8)) ^ KW[r4][2];
                iShift = ((Tinv0[r5 & 255] ^ shift(Tinv0[(r2 >> 8) & 255], 24)) ^ shift(Tinv0[(r1 >> 16) & 255], 16)) ^ shift(Tinv0[(r0 >> 24) & 255], 8);
                r = r4 - 1;
                i = KW[r4][3];
            } else {
                int r6 = (((Tinv0[t0 & 255] ^ shift(Tinv0[(r3 >> 8) & 255], 24)) ^ shift(Tinv0[(t2 >> 16) & 255], 16)) ^ shift(Tinv0[(t1 >> 24) & 255], 8)) ^ KW[r][0];
                int r7 = (((Tinv0[t1 & 255] ^ shift(Tinv0[(t0 >> 8) & 255], 24)) ^ shift(Tinv0[(r3 >> 16) & 255], 16)) ^ shift(Tinv0[(t2 >> 24) & 255], 8)) ^ KW[r][1];
                int r8 = (((Tinv0[t2 & 255] ^ shift(Tinv0[(t1 >> 8) & 255], 24)) ^ shift(Tinv0[(t0 >> 16) & 255], 16)) ^ shift(Tinv0[(r3 >> 24) & 255], 8)) ^ KW[r][2];
                int r9 = (((Tinv0[r3 & 255] ^ shift(Tinv0[(t2 >> 8) & 255], 24)) ^ shift(Tinv0[(t1 >> 16) & 255], 16)) ^ shift(Tinv0[(t0 >> 24) & 255], 8)) ^ KW[r][3];
                this.C0 = ((((Si[r6 & 255] & 255) ^ ((this.s[(r9 >> 8) & 255] & 255) << 8)) ^ ((this.s[(r8 >> 16) & 255] & 255) << 16)) ^ (Si[(r7 >> 24) & 255] << 24)) ^ KW[0][0];
                this.C1 = ((((this.s[r7 & 255] & 255) ^ ((this.s[(r6 >> 8) & 255] & 255) << 8)) ^ ((Si[(r9 >> 16) & 255] & 255) << 16)) ^ (this.s[(r8 >> 24) & 255] << 24)) ^ KW[0][1];
                this.C2 = ((((this.s[r8 & 255] & 255) ^ ((Si[(r7 >> 8) & 255] & 255) << 8)) ^ ((Si[(r6 >> 16) & 255] & 255) << 16)) ^ (this.s[(r9 >> 24) & 255] << 24)) ^ KW[0][2];
                this.C3 = ((((Si[r9 & 255] & 255) ^ ((this.s[(r8 >> 8) & 255] & 255) << 8)) ^ ((this.s[(r7 >> 16) & 255] & 255) << 16)) ^ (this.s[(r6 >> 24) & 255] << 24)) ^ KW[0][3];
                return;
            }
        }
    }
}
