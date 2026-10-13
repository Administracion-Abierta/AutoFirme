package org.bouncycastle.crypto.engines;

import com.sun.jna.platform.win32.WinError;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultMultiBlockCipher;
import org.bouncycastle.crypto.MultiBlockCipher;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/AESEngine.class */
public class AESEngine extends DefaultMultiBlockCipher {
    private static final byte[] S = {99, 124, 119, 123, -14, 107, 111, -59, 48, 1, 103, 43, -2, -41, -85, 118, -54, -126, -55, 125, -6, 89, 71, -16, -83, -44, -94, -81, -100, -92, 114, -64, -73, -3, -109, 38, 54, 63, -9, -52, 52, -91, -27, -15, 113, -40, 49, 21, 4, -57, 35, -61, 24, -106, 5, -102, 7, 18, -128, -30, -21, 39, -78, 117, 9, -125, 44, 26, 27, 110, 90, -96, 82, 59, -42, -77, 41, -29, 47, -124, 83, -47, 0, -19, 32, -4, -79, 91, 106, -53, -66, 57, 74, 76, 88, -49, -48, -17, -86, -5, 67, 77, 51, -123, 69, -7, 2, 127, 80, 60, -97, -88, 81, -93, 64, -113, -110, -99, 56, -11, -68, -74, -38, 33, 16, -1, -13, -46, -51, 12, 19, -20, 95, -105, 68, 23, -60, -89, 126, 61, 100, 93, 25, 115, 96, -127, 79, -36, 34, 42, -112, -120, 70, -18, -72, 20, -34, 94, 11, -37, -32, 50, 58, 10, 73, 6, 36, 92, -62, -45, -84, 98, -111, -107, -28, 121, -25, -56, 55, 109, -115, -43, 78, -87, 108, 86, -12, -22, 101, 122, -82, 8, -70, 120, 37, 46, 28, -90, -76, -58, -24, -35, 116, 31, 75, -67, -117, -118, 112, 62, -75, 102, 72, 3, -10, 14, 97, 53, 87, -71, -122, -63, 29, -98, -31, -8, -104, 17, 105, -39, -114, -108, -101, 30, -121, -23, -50, 85, 40, -33, -116, -95, -119, 13, -65, -26, 66, 104, 65, -103, 45, 15, -80, 84, -69, 22};
    private static final byte[] Si = {82, 9, 106, -43, 48, 54, -91, 56, -65, 64, -93, -98, -127, -13, -41, -5, 124, -29, 57, -126, -101, 47, -1, -121, 52, -114, 67, 68, -60, -34, -23, -53, 84, 123, -108, 50, -90, -62, 35, 61, -18, 76, -107, 11, 66, -6, -61, 78, 8, 46, -95, 102, 40, -39, 36, -78, 118, 91, -94, 73, 109, -117, -47, 37, 114, -8, -10, 100, -122, 104, -104, 22, -44, -92, 92, -52, 93, 101, -74, -110, 108, 112, 72, 80, -3, -19, -71, -38, 94, 21, 70, 87, -89, -115, -99, -124, -112, -40, -85, 0, -116, -68, -45, 10, -9, -28, 88, 5, -72, -77, 69, 6, -48, 44, 30, -113, -54, 63, 15, 2, -63, -81, -67, 3, 1, 19, -118, 107, 58, -111, 17, 65, 79, 103, -36, -22, -105, -14, -49, -50, -16, -76, -26, 115, -106, -84, 116, 34, -25, -83, 53, -123, -30, -7, 55, -24, 28, 117, -33, 110, 71, -15, 26, 113, 29, 41, -59, -119, 111, -73, 98, 14, -86, 24, -66, 27, -4, 86, 62, 75, -58, -46, 121, 32, -102, -37, -64, -2, 120, -51, 90, -12, 31, -35, -88, 51, -120, 7, -57, 49, -79, 18, 16, 89, 39, -128, -20, 95, 96, 81, 127, -87, 25, -75, 74, 13, 45, -27, 122, -97, -109, -55, -100, -17, -96, -32, 59, 77, -82, 42, -11, -80, -56, -21, -69, 60, -125, 83, -103, 97, 23, 43, 4, 126, -70, 119, -42, 38, -31, 105, 20, 99, 85, 33, 12, 125};
    private static final int[] rcon = {1, 2, 4, 8, 16, 32, 64, 128, 27, 54, 108, WinError.ERROR_EXE_MACHINE_TYPE_MISMATCH, 171, 77, 154, 47, 94, 188, 99, 198, 151, 53, 106, WinError.ERROR_LOCKED, 179, 125, 250, 239, 197, 145};
    private static final int[] T0 = {-1520213050, -2072216328, -1720223762, -1921287178, 234025727, -1117033514, -1318096930, 1422247313, 1345335392, 50397442, -1452841010, 2099981142, 436141799, 1658312629, -424957107, -1703512340, 1170918031, -1652391393, 1086966153, -2021818886, 368769775, -346465870, -918075506, 200339707, -324162239, 1742001331, -39673249, -357585083, -1080255453, -140204973, -1770884380, 1539358875, -1028147339, 486407649, -1366060227, 1780885068, 1513502316, 1094664062, 49805301, 1338821763, 1546925160, -190470831, 887481809, 150073849, -1821281822, 1943591083, 1395732834, 1058346282, 201589768, 1388824469, 1696801606, 1589887901, 672667696, -1583966665, 251987210, -1248159185, 151455502, 907153956, -1686077413, 1038279391, 652995533, 1764173646, -843926913, -1619692054, 453576978, -1635548387, 1949051992, 773462580, 756751158, -1301385508, -296068428, -73359269, -162377052, 1295727478, 1641469623, -827083907, 2066295122, 1055122397, 1898917726, -1752923117, -179088474, 1758581177, 0, 753790401, 1612718144, 536673507, -927878791, -312779850, -1100322092, 1187761037, -641810841, 1262041458, -565556588, -733197160, -396863312, 1255133061, 1808847035, 720367557, -441800113, 385612781, -985447546, -682799718, 1429418854, -1803188975, -817543798, 284817897, 100794884, -2122350594, -263171936, 1144798328, -1163944155, -475486133, -212774494, -22830243, -1069531008, -1970303227, -1382903233, -1130521311, 1211644016, 83228145, -541279133, -1044990345, 1977277103, 1663115586, 806359072, 452984805, 250868733, 1842533055, 1288555905, 336333848, 890442534, 804056259, -513843266, -1567123659, -867941240, 957814574, 1472513171, -223893675, -2105639172, 1195195770, -1402706744, -413311558, 723065138, -1787595802, -1604296512, -1736343271, -783331426, 2145180835, 1713513028, 2116692564, -1416589253, -2088204277, -901364084, 703524551, -742868885, 1007948840, 2044649127, -497131844, 487262998, 1994120109, 1004593371, 1446130276, 1312438900, 503974420, -615954030, 168166924, 1814307912, -463709000, 1573044895, 1859376061, -273896381, -1503501628, -1466855111, -1533700815, 937747667, -1954973198, 854058965, 1137232011, 1496790894, -1217565222, -1936880383, 1691735473, -766620004, -525751991, -1267962664, -95005012, 133494003, 636152527, -1352309302, -1904575756, -374428089, 403179536, -709182865, -2005370640, 1864705354, 1915629148, 605822008, -240736681, -944458637, 1371981463, 602466507, 2094914977, -1670089496, 555687742, -582268010, -591544991, -2037675251, -2054518257, -1871679264, 1111375484, -994724495, -1436129588, -666351472, 84083462, 32962295, 302911004, -1553899070, 1597322602, -111716434, -793134743, -1853454825, 1489093017, 656219450, -1180787161, 954327513, 335083755, -1281845205, 856756514, -1150719534, 1893325225, -1987146233, -1483434957, -1231316179, 572399164, -1836611819, 552200649, 1238290055, -11184726, 2015897680, 2061492133, -1886614525, -123625127, -2138470135, 386731290, -624967835, 837215959, -968736124, -1201116976, -1019133566, -1332111063, 1999449434, 286199582, -877612933, -61582168, -692339859, 974525996};
    private static final int[] Tinv0 = {1353184337, 1399144830, -1012656358, -1772214470, -882136261, -247096033, -1420232020, -1828461749, 1442459680, -160598355, -1854485368, 625738485, -52959921, -674551099, -2143013594, -1885117771, 1230680542, 1729870373, -1743852987, -507445667, 41234371, 317738113, -1550367091, -956705941, -413167869, -1784901099, -344298049, -631680363, 763608788, -752782248, 694804553, 1154009486, 1787413109, 2021232372, 1799248025, -579749593, -1236278850, 397248752, 1722556617, -1271214467, 407560035, -2110711067, 1613975959, 1165972322, -529046351, -2068943941, 480281086, -1809118983, 1483229296, 436028815, -2022908268, -1208452270, 601060267, -503166094, 1468997603, 715871590, 120122290, 63092015, -1703164538, -1526188077, -226023376, -1297760477, -1167457534, 1552029421, 723308426, -1833666137, -252573709, -1578997426, -839591323, -708967162, 526529745, -1963022652, -1655493068, -1604979806, 853641733, 1978398372, 971801355, -1427152832, 111112542, 1360031421, -108388034, 1023860118, -1375387939, 1186850381, -1249028975, 90031217, 1876166148, -15380384, 620468249, -1746289194, -868007799, 2006899047, -1119688528, -2004121337, 945494503, -605108103, 1191869601, -384875908, -920746760, 0, -2088337399, 1223502642, -1401941730, 1316117100, -67170563, 1446544655, 517320253, 658058550, 1691946762, 564550760, -783000677, 976107044, -1318647284, 266819475, -761860428, -1634624741, 1338359936, -1574904735, 1766553434, 370807324, 179999714, -450191168, 1138762300, 488053522, 185403662, -1379431438, -1180125651, -928440812, -2061897385, 1275557295, -1143105042, -44007517, -1624899081, -1124765092, -985962940, 880737115, 1982415755, -590994485, 1761406390, 1676797112, -891538985, 277177154, 1076008723, 538035844, 2099530373, -130171950, 288553390, 1839278535, 1261411869, -214912292, -330136051, -790380169, 1813426987, -1715900247, -95906799, 577038663, -997393240, 440397984, -668172970, -275762398, -951170681, -1043253031, -22885748, 906744984, -813566554, 685669029, 646887386, -1530942145, -459458004, 227702864, -1681105046, 1648787028, -1038905866, -390539120, 1593260334, -173030526, -1098883681, 2090061929, -1456614033, -1290656305, 999926984, -1484974064, 1852021992, 2075868123, 158869197, -199730834, 28809964, -1466282109, 1701746150, 2129067946, 147831841, -420997649, -644094022, -835293366, -737566742, -696471511, -1347247055, 824393514, 815048134, -1067015627, 935087732, -1496677636, -1328508704, 366520115, 1251476721, -136647615, 240176511, 804688151, -1915335306, 1303441219, 1414376140, -553347356, -474623586, 461924940, -1205916479, 2136040774, 82468509, 1563790337, 1937016826, 776014843, 1511876531, 1389550482, 861278441, 323475053, -1939744870, 2047648055, -1911228327, -1992551445, -299390514, 902390199, -303751967, 1018251130, 1507840668, 1064563285, 2043548696, -1086863501, -355600557, 1537932639, 342834655, -2032450440, -2114736182, 1053059257, 741614648, 1598071746, 1925389590, 203809468, -1958134744, 1100287487, 1895934009, -558691320, -1662733096, -1866377628, 1636092795, 1890988757, 1952214088, 1113045200};
    private static final int m1 = -2139062144;
    private static final int m2 = 2139062143;
    private static final int m3 = 27;
    private static final int m4 = -1061109568;
    private static final int m5 = 1061109567;
    private int ROUNDS;
    private int[][] WorkingKey = null;
    private boolean forEncryption;
    private byte[] s;
    private static final int BLOCK_SIZE = 16;

    private static int shift(int i, int i2) {
        return (i >>> i2) | (i << (-i2));
    }

    private static int FFmulX(int i) {
        return ((i & m2) << 1) ^ (((i & m1) >>> 7) * 27);
    }

    private static int FFmulX2(int i) {
        int i2 = (i & m5) << 2;
        int i3 = i & m4;
        int i4 = i3 ^ (i3 >>> 1);
        return (i2 ^ (i4 >>> 2)) ^ (i4 >>> 5);
    }

    private static int inv_mcol(int i) {
        int iShift = i ^ shift(i, 8);
        int iFFmulX = i ^ FFmulX(iShift);
        int iFFmulX2 = iShift ^ FFmulX2(iFFmulX);
        return iFFmulX ^ (iFFmulX2 ^ shift(iFFmulX2, 16));
    }

    private static int subWord(int i) {
        return (S[i & 255] & 255) | ((S[(i >> 8) & 255] & 255) << 8) | ((S[(i >> 16) & 255] & 255) << 16) | (S[(i >> 24) & 255] << 24);
    }

    private int[][] generateWorkingKey(byte[] bArr, boolean z) {
        int length = bArr.length;
        if (length < 16 || length > 32 || (length & 7) != 0) {
            throw new IllegalArgumentException("Key length not 128/192/256 bits.");
        }
        int i = length >>> 2;
        this.ROUNDS = i + 6;
        int[][] iArr = new int[this.ROUNDS + 1][4];
        switch (i) {
            case 4:
                int iLittleEndianToInt = Pack.littleEndianToInt(bArr, 0);
                iArr[0][0] = iLittleEndianToInt;
                int iLittleEndianToInt2 = Pack.littleEndianToInt(bArr, 4);
                iArr[0][1] = iLittleEndianToInt2;
                int iLittleEndianToInt3 = Pack.littleEndianToInt(bArr, 8);
                iArr[0][2] = iLittleEndianToInt3;
                int iLittleEndianToInt4 = Pack.littleEndianToInt(bArr, 12);
                iArr[0][3] = iLittleEndianToInt4;
                for (int i2 = 1; i2 <= 10; i2++) {
                    iLittleEndianToInt ^= subWord(shift(iLittleEndianToInt4, 8)) ^ rcon[i2 - 1];
                    iArr[i2][0] = iLittleEndianToInt;
                    iLittleEndianToInt2 ^= iLittleEndianToInt;
                    iArr[i2][1] = iLittleEndianToInt2;
                    iLittleEndianToInt3 ^= iLittleEndianToInt2;
                    iArr[i2][2] = iLittleEndianToInt3;
                    iLittleEndianToInt4 ^= iLittleEndianToInt3;
                    iArr[i2][3] = iLittleEndianToInt4;
                }
                break;
            case 5:
            case 7:
            default:
                throw new IllegalStateException("Should never get here");
            case 6:
                int iLittleEndianToInt5 = Pack.littleEndianToInt(bArr, 0);
                iArr[0][0] = iLittleEndianToInt5;
                int iLittleEndianToInt6 = Pack.littleEndianToInt(bArr, 4);
                iArr[0][1] = iLittleEndianToInt6;
                int iLittleEndianToInt7 = Pack.littleEndianToInt(bArr, 8);
                iArr[0][2] = iLittleEndianToInt7;
                int iLittleEndianToInt8 = Pack.littleEndianToInt(bArr, 12);
                iArr[0][3] = iLittleEndianToInt8;
                int iLittleEndianToInt9 = Pack.littleEndianToInt(bArr, 16);
                int iLittleEndianToInt10 = Pack.littleEndianToInt(bArr, 20);
                int i3 = 1;
                int i4 = 1;
                while (true) {
                    iArr[i3][0] = iLittleEndianToInt9;
                    iArr[i3][1] = iLittleEndianToInt10;
                    int i5 = i4 << 1;
                    int iSubWord = iLittleEndianToInt5 ^ (subWord(shift(iLittleEndianToInt10, 8)) ^ i4);
                    iArr[i3][2] = iSubWord;
                    int i6 = iLittleEndianToInt6 ^ iSubWord;
                    iArr[i3][3] = i6;
                    int i7 = iLittleEndianToInt7 ^ i6;
                    iArr[i3 + 1][0] = i7;
                    int i8 = iLittleEndianToInt8 ^ i7;
                    iArr[i3 + 1][1] = i8;
                    int i9 = iLittleEndianToInt9 ^ i8;
                    iArr[i3 + 1][2] = i9;
                    int i10 = iLittleEndianToInt10 ^ i9;
                    iArr[i3 + 1][3] = i10;
                    i4 = i5 << 1;
                    iLittleEndianToInt5 = iSubWord ^ (subWord(shift(i10, 8)) ^ i5);
                    iArr[i3 + 2][0] = iLittleEndianToInt5;
                    iLittleEndianToInt6 = i6 ^ iLittleEndianToInt5;
                    iArr[i3 + 2][1] = iLittleEndianToInt6;
                    iLittleEndianToInt7 = i7 ^ iLittleEndianToInt6;
                    iArr[i3 + 2][2] = iLittleEndianToInt7;
                    iLittleEndianToInt8 = i8 ^ iLittleEndianToInt7;
                    iArr[i3 + 2][3] = iLittleEndianToInt8;
                    i3 += 3;
                    if (i3 < 13) {
                        iLittleEndianToInt9 = i9 ^ iLittleEndianToInt8;
                        iLittleEndianToInt10 = i10 ^ iLittleEndianToInt9;
                    }
                    break;
                }
                break;
            case 8:
                int iLittleEndianToInt11 = Pack.littleEndianToInt(bArr, 0);
                iArr[0][0] = iLittleEndianToInt11;
                int iLittleEndianToInt12 = Pack.littleEndianToInt(bArr, 4);
                iArr[0][1] = iLittleEndianToInt12;
                int iLittleEndianToInt13 = Pack.littleEndianToInt(bArr, 8);
                iArr[0][2] = iLittleEndianToInt13;
                int iLittleEndianToInt14 = Pack.littleEndianToInt(bArr, 12);
                iArr[0][3] = iLittleEndianToInt14;
                int iLittleEndianToInt15 = Pack.littleEndianToInt(bArr, 16);
                iArr[1][0] = iLittleEndianToInt15;
                int iLittleEndianToInt16 = Pack.littleEndianToInt(bArr, 20);
                iArr[1][1] = iLittleEndianToInt16;
                int iLittleEndianToInt17 = Pack.littleEndianToInt(bArr, 24);
                iArr[1][2] = iLittleEndianToInt17;
                int iLittleEndianToInt18 = Pack.littleEndianToInt(bArr, 28);
                iArr[1][3] = iLittleEndianToInt18;
                int i11 = 2;
                int i12 = 1;
                while (true) {
                    int iSubWord2 = subWord(shift(iLittleEndianToInt18, 8)) ^ i12;
                    i12 <<= 1;
                    iLittleEndianToInt11 ^= iSubWord2;
                    iArr[i11][0] = iLittleEndianToInt11;
                    iLittleEndianToInt12 ^= iLittleEndianToInt11;
                    iArr[i11][1] = iLittleEndianToInt12;
                    iLittleEndianToInt13 ^= iLittleEndianToInt12;
                    iArr[i11][2] = iLittleEndianToInt13;
                    iLittleEndianToInt14 ^= iLittleEndianToInt13;
                    iArr[i11][3] = iLittleEndianToInt14;
                    int i13 = i11 + 1;
                    if (i13 < 15) {
                        iLittleEndianToInt15 ^= subWord(iLittleEndianToInt14);
                        iArr[i13][0] = iLittleEndianToInt15;
                        iLittleEndianToInt16 ^= iLittleEndianToInt15;
                        iArr[i13][1] = iLittleEndianToInt16;
                        iLittleEndianToInt17 ^= iLittleEndianToInt16;
                        iArr[i13][2] = iLittleEndianToInt17;
                        iLittleEndianToInt18 ^= iLittleEndianToInt17;
                        iArr[i13][3] = iLittleEndianToInt18;
                        i11 = i13 + 1;
                    }
                    break;
                }
                break;
        }
        if (!z) {
            for (int i14 = 1; i14 < this.ROUNDS; i14++) {
                for (int i15 = 0; i15 < 4; i15++) {
                    iArr[i14][i15] = inv_mcol(iArr[i14][i15]);
                }
            }
        }
        return iArr;
    }

    public static MultiBlockCipher newInstance() {
        return new AESEngine();
    }

    public AESEngine() {
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), 256));
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to AES init - " + cipherParameters.getClass().getName());
        }
        this.WorkingKey = generateWorkingKey(((KeyParameter) cipherParameters).getKey(), z);
        this.forEncryption = z;
        if (z) {
            this.s = Arrays.clone(S);
        } else {
            this.s = Arrays.clone(Si);
        }
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), bitsOfSecurity(), cipherParameters, Utils.getPurpose(z)));
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "AES";
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 16;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (this.WorkingKey == null) {
            throw new IllegalStateException("AES engine not initialised");
        }
        if (i > bArr.length - 16) {
            throw new DataLengthException("input buffer too short");
        }
        if (i2 > bArr2.length - 16) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.forEncryption) {
            encryptBlock(bArr, i, bArr2, i2, this.WorkingKey);
            return 16;
        }
        decryptBlock(bArr, i, bArr2, i2, this.WorkingKey);
        return 16;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void reset() {
    }

    private void encryptBlock(byte[] bArr, int i, byte[] bArr2, int i2, int[][] iArr) {
        int iLittleEndianToInt = Pack.littleEndianToInt(bArr, i + 0);
        int iLittleEndianToInt2 = Pack.littleEndianToInt(bArr, i + 4);
        int iLittleEndianToInt3 = Pack.littleEndianToInt(bArr, i + 8);
        int iLittleEndianToInt4 = Pack.littleEndianToInt(bArr, i + 12);
        int iShift = iLittleEndianToInt ^ iArr[0][0];
        int iShift2 = iLittleEndianToInt2 ^ iArr[0][1];
        int iShift3 = iLittleEndianToInt3 ^ iArr[0][2];
        int i3 = 1;
        int iShift4 = iLittleEndianToInt4;
        int i4 = iArr[0][3];
        while (true) {
            int i5 = iShift4 ^ i4;
            if (i3 >= this.ROUNDS - 1) {
                int iShift5 = (((T0[iShift & 255] ^ shift(T0[(iShift2 >> 8) & 255], 24)) ^ shift(T0[(iShift3 >> 16) & 255], 16)) ^ shift(T0[(i5 >> 24) & 255], 8)) ^ iArr[i3][0];
                int iShift6 = (((T0[iShift2 & 255] ^ shift(T0[(iShift3 >> 8) & 255], 24)) ^ shift(T0[(i5 >> 16) & 255], 16)) ^ shift(T0[(iShift >> 24) & 255], 8)) ^ iArr[i3][1];
                int iShift7 = (((T0[iShift3 & 255] ^ shift(T0[(i5 >> 8) & 255], 24)) ^ shift(T0[(iShift >> 16) & 255], 16)) ^ shift(T0[(iShift2 >> 24) & 255], 8)) ^ iArr[i3][2];
                int i6 = i3;
                int i7 = i3 + 1;
                int iShift8 = (((T0[i5 & 255] ^ shift(T0[(iShift >> 8) & 255], 24)) ^ shift(T0[(iShift2 >> 16) & 255], 16)) ^ shift(T0[(iShift3 >> 24) & 255], 8)) ^ iArr[i6][3];
                int i8 = ((((S[iShift5 & 255] & 255) ^ ((S[(iShift6 >> 8) & 255] & 255) << 8)) ^ ((this.s[(iShift7 >> 16) & 255] & 255) << 16)) ^ (this.s[(iShift8 >> 24) & 255] << 24)) ^ iArr[i7][0];
                int i9 = ((((this.s[iShift6 & 255] & 255) ^ ((S[(iShift7 >> 8) & 255] & 255) << 8)) ^ ((S[(iShift8 >> 16) & 255] & 255) << 16)) ^ (this.s[(iShift5 >> 24) & 255] << 24)) ^ iArr[i7][1];
                int i10 = ((((this.s[iShift7 & 255] & 255) ^ ((S[(iShift8 >> 8) & 255] & 255) << 8)) ^ ((S[(iShift5 >> 16) & 255] & 255) << 16)) ^ (S[(iShift6 >> 24) & 255] << 24)) ^ iArr[i7][2];
                int i11 = ((((this.s[iShift8 & 255] & 255) ^ ((this.s[(iShift5 >> 8) & 255] & 255) << 8)) ^ ((this.s[(iShift6 >> 16) & 255] & 255) << 16)) ^ (S[(iShift7 >> 24) & 255] << 24)) ^ iArr[i7][3];
                Pack.intToLittleEndian(i8, bArr2, i2 + 0);
                Pack.intToLittleEndian(i9, bArr2, i2 + 4);
                Pack.intToLittleEndian(i10, bArr2, i2 + 8);
                Pack.intToLittleEndian(i11, bArr2, i2 + 12);
                return;
            }
            int iShift9 = (((T0[iShift & 255] ^ shift(T0[(iShift2 >> 8) & 255], 24)) ^ shift(T0[(iShift3 >> 16) & 255], 16)) ^ shift(T0[(i5 >> 24) & 255], 8)) ^ iArr[i3][0];
            int iShift10 = (((T0[iShift2 & 255] ^ shift(T0[(iShift3 >> 8) & 255], 24)) ^ shift(T0[(i5 >> 16) & 255], 16)) ^ shift(T0[(iShift >> 24) & 255], 8)) ^ iArr[i3][1];
            int iShift11 = (((T0[iShift3 & 255] ^ shift(T0[(i5 >> 8) & 255], 24)) ^ shift(T0[(iShift >> 16) & 255], 16)) ^ shift(T0[(iShift2 >> 24) & 255], 8)) ^ iArr[i3][2];
            int i12 = i3;
            int i13 = i3 + 1;
            int iShift12 = (((T0[i5 & 255] ^ shift(T0[(iShift >> 8) & 255], 24)) ^ shift(T0[(iShift2 >> 16) & 255], 16)) ^ shift(T0[(iShift3 >> 24) & 255], 8)) ^ iArr[i12][3];
            iShift = (((T0[iShift9 & 255] ^ shift(T0[(iShift10 >> 8) & 255], 24)) ^ shift(T0[(iShift11 >> 16) & 255], 16)) ^ shift(T0[(iShift12 >> 24) & 255], 8)) ^ iArr[i13][0];
            iShift2 = (((T0[iShift10 & 255] ^ shift(T0[(iShift11 >> 8) & 255], 24)) ^ shift(T0[(iShift12 >> 16) & 255], 16)) ^ shift(T0[(iShift9 >> 24) & 255], 8)) ^ iArr[i13][1];
            iShift3 = (((T0[iShift11 & 255] ^ shift(T0[(iShift12 >> 8) & 255], 24)) ^ shift(T0[(iShift9 >> 16) & 255], 16)) ^ shift(T0[(iShift10 >> 24) & 255], 8)) ^ iArr[i13][2];
            iShift4 = ((T0[iShift12 & 255] ^ shift(T0[(iShift9 >> 8) & 255], 24)) ^ shift(T0[(iShift10 >> 16) & 255], 16)) ^ shift(T0[(iShift11 >> 24) & 255], 8);
            i3 = i13 + 1;
            i4 = iArr[i13][3];
        }
    }

    private void decryptBlock(byte[] bArr, int i, byte[] bArr2, int i2, int[][] iArr) {
        int iLittleEndianToInt = Pack.littleEndianToInt(bArr, i + 0);
        int iLittleEndianToInt2 = Pack.littleEndianToInt(bArr, i + 4);
        int iLittleEndianToInt3 = Pack.littleEndianToInt(bArr, i + 8);
        int iLittleEndianToInt4 = Pack.littleEndianToInt(bArr, i + 12);
        int iShift = iLittleEndianToInt ^ iArr[this.ROUNDS][0];
        int iShift2 = iLittleEndianToInt2 ^ iArr[this.ROUNDS][1];
        int iShift3 = iLittleEndianToInt3 ^ iArr[this.ROUNDS][2];
        int i3 = this.ROUNDS - 1;
        int iShift4 = iLittleEndianToInt4;
        int i4 = iArr[this.ROUNDS][3];
        while (true) {
            int i5 = iShift4 ^ i4;
            if (i3 <= 1) {
                int iShift5 = (((Tinv0[iShift & 255] ^ shift(Tinv0[(i5 >> 8) & 255], 24)) ^ shift(Tinv0[(iShift3 >> 16) & 255], 16)) ^ shift(Tinv0[(iShift2 >> 24) & 255], 8)) ^ iArr[i3][0];
                int iShift6 = (((Tinv0[iShift2 & 255] ^ shift(Tinv0[(iShift >> 8) & 255], 24)) ^ shift(Tinv0[(i5 >> 16) & 255], 16)) ^ shift(Tinv0[(iShift3 >> 24) & 255], 8)) ^ iArr[i3][1];
                int iShift7 = (((Tinv0[iShift3 & 255] ^ shift(Tinv0[(iShift2 >> 8) & 255], 24)) ^ shift(Tinv0[(iShift >> 16) & 255], 16)) ^ shift(Tinv0[(i5 >> 24) & 255], 8)) ^ iArr[i3][2];
                int iShift8 = (((Tinv0[i5 & 255] ^ shift(Tinv0[(iShift3 >> 8) & 255], 24)) ^ shift(Tinv0[(iShift2 >> 16) & 255], 16)) ^ shift(Tinv0[(iShift >> 24) & 255], 8)) ^ iArr[i3][3];
                int i6 = ((((Si[iShift5 & 255] & 255) ^ ((this.s[(iShift8 >> 8) & 255] & 255) << 8)) ^ ((this.s[(iShift7 >> 16) & 255] & 255) << 16)) ^ (Si[(iShift6 >> 24) & 255] << 24)) ^ iArr[0][0];
                int i7 = ((((this.s[iShift6 & 255] & 255) ^ ((this.s[(iShift5 >> 8) & 255] & 255) << 8)) ^ ((Si[(iShift8 >> 16) & 255] & 255) << 16)) ^ (this.s[(iShift7 >> 24) & 255] << 24)) ^ iArr[0][1];
                int i8 = ((((this.s[iShift7 & 255] & 255) ^ ((Si[(iShift6 >> 8) & 255] & 255) << 8)) ^ ((Si[(iShift5 >> 16) & 255] & 255) << 16)) ^ (this.s[(iShift8 >> 24) & 255] << 24)) ^ iArr[0][2];
                int i9 = ((((Si[iShift8 & 255] & 255) ^ ((this.s[(iShift7 >> 8) & 255] & 255) << 8)) ^ ((this.s[(iShift6 >> 16) & 255] & 255) << 16)) ^ (this.s[(iShift5 >> 24) & 255] << 24)) ^ iArr[0][3];
                Pack.intToLittleEndian(i6, bArr2, i2 + 0);
                Pack.intToLittleEndian(i7, bArr2, i2 + 4);
                Pack.intToLittleEndian(i8, bArr2, i2 + 8);
                Pack.intToLittleEndian(i9, bArr2, i2 + 12);
                return;
            }
            int iShift9 = (((Tinv0[iShift & 255] ^ shift(Tinv0[(i5 >> 8) & 255], 24)) ^ shift(Tinv0[(iShift3 >> 16) & 255], 16)) ^ shift(Tinv0[(iShift2 >> 24) & 255], 8)) ^ iArr[i3][0];
            int iShift10 = (((Tinv0[iShift2 & 255] ^ shift(Tinv0[(iShift >> 8) & 255], 24)) ^ shift(Tinv0[(i5 >> 16) & 255], 16)) ^ shift(Tinv0[(iShift3 >> 24) & 255], 8)) ^ iArr[i3][1];
            int iShift11 = (((Tinv0[iShift3 & 255] ^ shift(Tinv0[(iShift2 >> 8) & 255], 24)) ^ shift(Tinv0[(iShift >> 16) & 255], 16)) ^ shift(Tinv0[(i5 >> 24) & 255], 8)) ^ iArr[i3][2];
            int i10 = i3;
            int i11 = i3 - 1;
            int iShift12 = (((Tinv0[i5 & 255] ^ shift(Tinv0[(iShift3 >> 8) & 255], 24)) ^ shift(Tinv0[(iShift2 >> 16) & 255], 16)) ^ shift(Tinv0[(iShift >> 24) & 255], 8)) ^ iArr[i10][3];
            iShift = (((Tinv0[iShift9 & 255] ^ shift(Tinv0[(iShift12 >> 8) & 255], 24)) ^ shift(Tinv0[(iShift11 >> 16) & 255], 16)) ^ shift(Tinv0[(iShift10 >> 24) & 255], 8)) ^ iArr[i11][0];
            iShift2 = (((Tinv0[iShift10 & 255] ^ shift(Tinv0[(iShift9 >> 8) & 255], 24)) ^ shift(Tinv0[(iShift12 >> 16) & 255], 16)) ^ shift(Tinv0[(iShift11 >> 24) & 255], 8)) ^ iArr[i11][1];
            iShift3 = (((Tinv0[iShift11 & 255] ^ shift(Tinv0[(iShift10 >> 8) & 255], 24)) ^ shift(Tinv0[(iShift9 >> 16) & 255], 16)) ^ shift(Tinv0[(iShift12 >> 24) & 255], 8)) ^ iArr[i11][2];
            iShift4 = ((Tinv0[iShift12 & 255] ^ shift(Tinv0[(iShift11 >> 8) & 255], 24)) ^ shift(Tinv0[(iShift10 >> 16) & 255], 16)) ^ shift(Tinv0[(iShift9 >> 24) & 255], 8);
            i3 = i11 - 1;
            i4 = iArr[i11][3];
        }
    }

    private int bitsOfSecurity() {
        if (this.WorkingKey == null) {
            return 256;
        }
        return (this.WorkingKey.length - 7) << 5;
    }
}
