package org.bouncycastle.pqc.crypto.sphincsplus;

import com.sun.jna.platform.win32.Ddeml;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Winspool;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/sphincsplus/HarakaSBase.class */
class HarakaSBase {
    protected long[][] haraka512_rc = {new long[]{2652350495371256459L, -4767360454786055294L, -2778808723033108313L, -6138960262205972599L, 4944264682582508575L, 5312892415214084856L, 390034814247088728L, 2584105839607850161L}, new long[]{-2829930801980875922L, 9137660425067592590L, 7974068014816832049L, -4665944065725157058L, 2602240152241800734L, -1525694355931290902L, 8634660511727056099L, 1757945485816280992L}, new long[]{1181946526362588450L, -2765192619992380293L, 3395396416743122529L, -5116273100549372423L, -1285454309797503998L, -3363297609815171261L, -8360835858392998991L, -2371352336613968487L}, new long[]{-2500853454776756032L, 8465221333286591414L, 8817016078209461823L, 9067727467981428858L, 4244107674518258433L, -4347326460570889538L, 1711371409274742987L, 6486926172609168623L}, new long[]{1689001080716996467L, -491496126278250673L, 1273395568185090836L, 5805238412293617850L, -3441289770925384855L, 4592753210857527691L, 7062886034259989751L, -7974393977033172556L}, new long[]{-797818098819718290L, -41460260651793472L, 476036171179798187L, 7391697506481003962L, -855662275170689475L, -3489340839585811635L, -4891525734487956488L, 9110006695579921767L}, new long[]{-886938081943560790L, 4212830408327159617L, -3546674487567282635L, -1955379422127038289L, 3174578079917510314L, 5156046680874954380L, -318545805834821831L, -6176414008149462342L}, new long[]{2529785914229181047L, 2966313764524854080L, 6363694428402697361L, 8292109690175819701L, -8497546332135459587L, -3211108476154815616L, -5526938793786642321L, -4975969843627057770L}, new long[]{3357847021085574721L, -4764837212565187058L, -626391829400648692L, 2124133995575340009L, 7425858999829294301L, -3432032868905637771L, 1119301198758921294L, 1907812968586478892L}, new long[]{-8986524826712832802L, 3356175496741300052L, -5764600317639896362L, 4002747967109689317L, -8718925159733497197L, -1938063772587374661L, -8003749789895945835L, 7302960353763723932L}};
    protected int[][] haraka256_rc = new int[10][8];
    protected final byte[] buffer = new byte[64];
    protected int off = 0;

    protected HarakaSBase() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void reset() {
        this.off = 0;
        Arrays.clear(this.buffer);
    }

    private void brRangeDec32Le(byte[] bArr, int[] iArr, int i) {
        for (int i2 = 0; i2 < iArr.length; i2++) {
            int i3 = i + (i2 << 2);
            iArr[i2] = (bArr[i3] & 255) | ((bArr[i3 + 1] << 8) & 65280) | ((bArr[i3 + 2] << 16) & Winspool.PRINTER_ENUM_ICONMASK) | (bArr[i3 + 3] << 24);
        }
    }

    protected void interleaveConstant(long[] jArr, byte[] bArr, int i) {
        int[] iArr = new int[16];
        brRangeDec32Le(bArr, iArr, i);
        for (int i2 = 0; i2 < 4; i2++) {
            brAesCt64InterleaveIn(jArr, i2, iArr, i2 << 2);
        }
        brAesCt64Ortho(jArr);
    }

    protected void interleaveConstant32(int[] iArr, byte[] bArr, int i) {
        for (int i2 = 0; i2 < 4; i2++) {
            iArr[i2 << 1] = brDec32Le(bArr, i + (i2 << 2));
            iArr[(i2 << 1) + 1] = brDec32Le(bArr, i + (i2 << 2) + 16);
        }
        brAesCtOrtho(iArr);
    }

    private int brDec32Le(byte[] bArr, int i) {
        return (bArr[i] & 255) | ((bArr[i + 1] << 8) & 65280) | ((bArr[i + 2] << 16) & Winspool.PRINTER_ENUM_ICONMASK) | (bArr[i + 3] << 24);
    }

    protected void haraka512Perm(byte[] bArr) {
        int[] iArr = new int[16];
        long[] jArr = new long[8];
        brRangeDec32Le(this.buffer, iArr, 0);
        for (int i = 0; i < 4; i++) {
            brAesCt64InterleaveIn(jArr, i, iArr, i << 2);
        }
        brAesCt64Ortho(jArr);
        for (int i2 = 0; i2 < 5; i2++) {
            for (int i3 = 0; i3 < 2; i3++) {
                brAesCt64BitsliceSbox(jArr);
                shiftRows(jArr);
                mixColumns(jArr);
                addRoundKey(jArr, this.haraka512_rc[(i2 << 1) + i3]);
            }
            for (int i4 = 0; i4 < 8; i4++) {
                long j = jArr[i4];
                jArr[i4] = ((j & 281479271743489L) << 5) | ((j & 562958543486978L) << 12) | ((j & 1125917086973956L) >>> 1) | ((j & 2251834173947912L) << 6) | ((j & 9007336695791648L) << 9) | ((j & 18014673391583296L) >>> 4) | ((j & 36029346783166592L) << 3) | ((j & 2377936887688995072L) >>> 5) | ((j & 148621055480562192L) << 2) | ((j & 576469548530665472L) << 4) | ((j & 1152939097061330944L) >>> 12) | ((j & 4611756388245323776L) >>> 10) | ((j & (-8934996522953571328L)) >>> 3);
            }
        }
        brAesCt64Ortho(jArr);
        for (int i5 = 0; i5 < 4; i5++) {
            brAesCt64InterleaveOut(iArr, jArr, i5);
        }
        for (int i6 = 0; i6 < 16; i6++) {
            for (int i7 = 0; i7 < 4; i7++) {
                bArr[(i6 << 2) + i7] = (byte) ((iArr[i6] >>> (i7 << 3)) & 255);
            }
        }
    }

    protected void haraka256Perm(byte[] bArr) {
        int[] iArr = new int[8];
        interleaveConstant32(iArr, this.buffer, 0);
        for (int i = 0; i < 5; i++) {
            for (int i2 = 0; i2 < 2; i2++) {
                brAesCtBitsliceSbox(iArr);
                shiftRows32(iArr);
                mixColumns32(iArr);
                addRoundKey32(iArr, this.haraka256_rc[(i << 1) + i2]);
            }
            for (int i3 = 0; i3 < 8; i3++) {
                int i4 = iArr[i3];
                iArr[i3] = (i4 & (-2122219135)) | ((i4 & 33686018) << 1) | ((i4 & 67372036) << 2) | ((i4 & 134744072) << 3) | ((i4 & 269488144) >>> 3) | ((i4 & 538976288) >>> 2) | ((i4 & 1077952576) >>> 1);
            }
        }
        brAesCtOrtho(iArr);
        for (int i5 = 0; i5 < 4; i5++) {
            brEnc32Le(bArr, iArr[i5 << 1], i5 << 2);
            brEnc32Le(bArr, iArr[(i5 << 1) + 1], (i5 << 2) + 16);
        }
    }

    private void brEnc32Le(byte[] bArr, int i, int i2) {
        for (int i3 = 0; i3 < 4; i3++) {
            bArr[i2 + i3] = (byte) (i >> (i3 << 3));
        }
    }

    private void brAesCt64InterleaveIn(long[] jArr, int i, int[] iArr, int i2) {
        long j = ((long) iArr[i2]) & 4294967295L;
        long j2 = ((long) iArr[i2 + 1]) & 4294967295L;
        long j3 = ((long) iArr[i2 + 2]) & 4294967295L;
        long j4 = ((long) iArr[i2 + 3]) & 4294967295L;
        long j5 = j | (j << 16);
        long j6 = j2 | (j2 << 16);
        long j7 = j3 | (j3 << 16);
        long j8 = j4 | (j4 << 16);
        long j9 = j5 & 281470681808895L;
        long j10 = j6 & 281470681808895L;
        long j11 = j7 & 281470681808895L;
        long j12 = j8 & 281470681808895L;
        long j13 = j9 | (j9 << 8);
        long j14 = j10 | (j10 << 8);
        long j15 = j11 | (j11 << 8);
        long j16 = j12 | (j12 << 8);
        long j17 = j13 & 71777214294589695L;
        long j18 = j14 & 71777214294589695L;
        jArr[i] = j17 | ((j15 & 71777214294589695L) << 8);
        jArr[i + 4] = j18 | ((j16 & 71777214294589695L) << 8);
    }

    private static void brAesCtBitsliceSbox(int[] iArr) {
        int i = iArr[7];
        int i2 = iArr[6];
        int i3 = iArr[5];
        int i4 = iArr[4];
        int i5 = iArr[3];
        int i6 = iArr[2];
        int i7 = iArr[1];
        int i8 = iArr[0];
        int i9 = i4 ^ i6;
        int i10 = i ^ i7;
        int i11 = i ^ i4;
        int i12 = i ^ i6;
        int i13 = i2 ^ i3;
        int i14 = i13 ^ i8;
        int i15 = i14 ^ i4;
        int i16 = i10 ^ i9;
        int i17 = i14 ^ i;
        int i18 = i14 ^ i7;
        int i19 = i18 ^ i12;
        int i20 = i5 ^ i16;
        int i21 = i20 ^ i6;
        int i22 = i20 ^ i2;
        int i23 = i21 ^ i8;
        int i24 = i21 ^ i13;
        int i25 = i22 ^ i11;
        int i26 = i8 ^ i25;
        int i27 = i24 ^ i25;
        int i28 = i24 ^ i12;
        int i29 = i13 ^ i25;
        int i30 = i10 ^ i29;
        int i31 = i ^ i29;
        int i32 = i16 & i21;
        int i33 = (i19 & i23) ^ i32;
        int i34 = (i15 & i8) ^ i32;
        int i35 = i10 & i29;
        int i36 = (i18 & i14) ^ i35;
        int i37 = (i17 & i26) ^ i35;
        int i38 = i11 & i25;
        int i39 = (i9 & i27) ^ i38;
        int i40 = (i12 & i24) ^ i38;
        int i41 = i33 ^ i39;
        int i42 = i34 ^ i40;
        int i43 = i36 ^ i39;
        int i44 = i37 ^ i40;
        int i45 = i41 ^ i22;
        int i46 = i42 ^ i28;
        int i47 = i43 ^ i30;
        int i48 = i44 ^ i31;
        int i49 = i45 ^ i46;
        int i50 = i45 & i47;
        int i51 = i48 ^ i50;
        int i52 = (i49 & i51) ^ i46;
        int i53 = ((i46 ^ i50) & (i47 ^ i48)) ^ i48;
        int i54 = i47 ^ i53;
        int i55 = i48 & (i51 ^ i53);
        int i56 = i55 ^ i54;
        int i57 = i49 ^ (i52 & (i51 ^ i55));
        int i58 = i57 ^ i56;
        int i59 = i52 ^ i53;
        int i60 = i52 ^ i57;
        int i61 = i53 ^ i56;
        int i62 = i59 ^ i58;
        int i63 = i61 & i21;
        int i64 = i56 & i23;
        int i65 = i53 & i8;
        int i66 = i60 & i29;
        int i67 = i57 & i14;
        int i68 = i52 & i26;
        int i69 = i59 & i25;
        int i70 = i62 & i27;
        int i71 = i58 & i24;
        int i72 = i61 & i16;
        int i73 = i56 & i19;
        int i74 = i53 & i15;
        int i75 = i60 & i10;
        int i76 = i57 & i18;
        int i77 = i52 & i17;
        int i78 = i59 & i11;
        int i79 = i62 & i9;
        int i80 = i58 & i12;
        int i81 = i78 ^ i79;
        int i82 = i73 ^ i74;
        int i83 = i68 ^ i76;
        int i84 = i72 ^ i73;
        int i85 = i65 ^ i75;
        int i86 = i65 ^ i68;
        int i87 = i70 ^ i71;
        int i88 = i63 ^ i66;
        int i89 = i69 ^ i70;
        int i90 = i79 ^ i80;
        int i91 = i75 ^ i83;
        int i92 = i85 ^ i88;
        int i93 = i67 ^ i81;
        int i94 = i66 ^ i89;
        int i95 = i81 ^ i92;
        int i96 = i77 ^ i92;
        int i97 = i87 ^ i93;
        int i98 = i84 ^ i93;
        int i99 = i67 ^ i94;
        int i100 = i96 ^ i97;
        int i101 = i64 ^ i98;
        int i102 = i94 ^ i98;
        int i103 = i91 ^ (i97 ^ (-1));
        int i104 = i83 ^ (i95 ^ (-1));
        int i105 = i99 ^ i100;
        int i106 = i88 ^ i101;
        int i107 = i86 ^ i101;
        int i108 = i82 ^ i100;
        iArr[7] = i102;
        iArr[6] = i99 ^ (i106 ^ (-1));
        iArr[5] = i90 ^ (i105 ^ (-1));
        iArr[4] = i106;
        iArr[3] = i107;
        iArr[2] = i108;
        iArr[1] = i103;
        iArr[0] = i104;
    }

    private void shiftRows32(int[] iArr) {
        for (int i = 0; i < 8; i++) {
            int i2 = iArr[i];
            iArr[i] = (i2 & 255) | ((i2 & Ddeml.XCLASS_MASK) >>> 2) | ((i2 & 768) << 6) | ((i2 & 15728640) >>> 4) | ((i2 & WinNT.STANDARD_RIGHTS_REQUIRED) << 4) | ((i2 & (-1073741824)) >>> 6) | ((i2 & 1056964608) << 2);
        }
    }

    private void mixColumns32(int[] iArr) {
        int i = iArr[0];
        int i2 = iArr[1];
        int i3 = iArr[2];
        int i4 = iArr[3];
        int i5 = iArr[4];
        int i6 = iArr[5];
        int i7 = iArr[6];
        int i8 = iArr[7];
        int i9 = (i >>> 8) | (i << 24);
        int i10 = (i2 >>> 8) | (i2 << 24);
        int i11 = (i3 >>> 8) | (i3 << 24);
        int i12 = (i4 >>> 8) | (i4 << 24);
        int i13 = (i5 >>> 8) | (i5 << 24);
        int i14 = (i6 >>> 8) | (i6 << 24);
        int i15 = (i7 >>> 8) | (i7 << 24);
        int i16 = (i8 >>> 8) | (i8 << 24);
        iArr[0] = ((i8 ^ i16) ^ i9) ^ rotr16(i ^ i9);
        iArr[1] = ((((i ^ i9) ^ i8) ^ i16) ^ i10) ^ rotr16(i2 ^ i10);
        iArr[2] = ((i2 ^ i10) ^ i11) ^ rotr16(i3 ^ i11);
        iArr[3] = ((((i3 ^ i11) ^ i8) ^ i16) ^ i12) ^ rotr16(i4 ^ i12);
        iArr[4] = ((((i4 ^ i12) ^ i8) ^ i16) ^ i13) ^ rotr16(i5 ^ i13);
        iArr[5] = ((i5 ^ i13) ^ i14) ^ rotr16(i6 ^ i14);
        iArr[6] = ((i6 ^ i14) ^ i15) ^ rotr16(i7 ^ i15);
        iArr[7] = ((i7 ^ i15) ^ i16) ^ rotr16(i8 ^ i16);
    }

    private void addRoundKey32(int[] iArr, int[] iArr2) {
        iArr[0] = iArr[0] ^ iArr2[0];
        iArr[1] = iArr[1] ^ iArr2[1];
        iArr[2] = iArr[2] ^ iArr2[2];
        iArr[3] = iArr[3] ^ iArr2[3];
        iArr[4] = iArr[4] ^ iArr2[4];
        iArr[5] = iArr[5] ^ iArr2[5];
        iArr[6] = iArr[6] ^ iArr2[6];
        iArr[7] = iArr[7] ^ iArr2[7];
    }

    private int rotr16(int i) {
        return (i << 16) | (i >>> 16);
    }

    private void brAesCt64Ortho(long[] jArr) {
        Swapn(jArr, 1, 0, 1);
        Swapn(jArr, 1, 2, 3);
        Swapn(jArr, 1, 4, 5);
        Swapn(jArr, 1, 6, 7);
        Swapn(jArr, 2, 0, 2);
        Swapn(jArr, 2, 1, 3);
        Swapn(jArr, 2, 4, 6);
        Swapn(jArr, 2, 5, 7);
        Swapn(jArr, 4, 0, 4);
        Swapn(jArr, 4, 1, 5);
        Swapn(jArr, 4, 2, 6);
        Swapn(jArr, 4, 3, 7);
    }

    private void brAesCtOrtho(int[] iArr) {
        Swapn32(iArr, 1, 0, 1);
        Swapn32(iArr, 1, 2, 3);
        Swapn32(iArr, 1, 4, 5);
        Swapn32(iArr, 1, 6, 7);
        Swapn32(iArr, 2, 0, 2);
        Swapn32(iArr, 2, 1, 3);
        Swapn32(iArr, 2, 4, 6);
        Swapn32(iArr, 2, 5, 7);
        Swapn32(iArr, 4, 0, 4);
        Swapn32(iArr, 4, 1, 5);
        Swapn32(iArr, 4, 2, 6);
        Swapn32(iArr, 4, 3, 7);
    }

    private void Swapn32(int[] iArr, int i, int i2, int i3) {
        int i4 = 0;
        int i5 = 0;
        switch (i) {
            case 1:
                i4 = 1431655765;
                i5 = -1431655766;
                break;
            case 2:
                i4 = 858993459;
                i5 = -858993460;
                break;
            case 4:
                i4 = 252645135;
                i5 = -252645136;
                break;
        }
        int i6 = iArr[i2];
        int i7 = iArr[i3];
        iArr[i2] = (i6 & i4) | ((i7 & i4) << i);
        iArr[i3] = ((i6 & i5) >>> i) | (i7 & i5);
    }

    private void Swapn(long[] jArr, int i, int i2, int i3) {
        long j;
        long j2;
        switch (i) {
            case 1:
                j = 6148914691236517205L;
                j2 = -6148914691236517206L;
                break;
            case 2:
                j = 3689348814741910323L;
                j2 = -3689348814741910324L;
                break;
            case 3:
            default:
                return;
            case 4:
                j = 1085102592571150095L;
                j2 = -1085102592571150096L;
                break;
        }
        long j3 = jArr[i2];
        long j4 = jArr[i3];
        jArr[i2] = (j3 & j) | ((j4 & j) << i);
        jArr[i3] = ((j3 & j2) >>> i) | (j4 & j2);
    }

    private void brAesCt64BitsliceSbox(long[] jArr) {
        long j = jArr[7];
        long j2 = jArr[6];
        long j3 = jArr[5];
        long j4 = jArr[4];
        long j5 = jArr[3];
        long j6 = jArr[2];
        long j7 = jArr[1];
        long j8 = jArr[0];
        long j9 = j4 ^ j6;
        long j10 = j ^ j7;
        long j11 = j ^ j4;
        long j12 = j ^ j6;
        long j13 = j2 ^ j3;
        long j14 = j13 ^ j8;
        long j15 = j14 ^ j4;
        long j16 = j10 ^ j9;
        long j17 = j14 ^ j;
        long j18 = j14 ^ j7;
        long j19 = j18 ^ j12;
        long j20 = j5 ^ j16;
        long j21 = j20 ^ j6;
        long j22 = j20 ^ j2;
        long j23 = j21 ^ j8;
        long j24 = j21 ^ j13;
        long j25 = j22 ^ j11;
        long j26 = j8 ^ j25;
        long j27 = j24 ^ j25;
        long j28 = j24 ^ j12;
        long j29 = j13 ^ j25;
        long j30 = j10 ^ j29;
        long j31 = j ^ j29;
        long j32 = j16 & j21;
        long j33 = (j19 & j23) ^ j32;
        long j34 = (j15 & j8) ^ j32;
        long j35 = j10 & j29;
        long j36 = (j18 & j14) ^ j35;
        long j37 = (j17 & j26) ^ j35;
        long j38 = j11 & j25;
        long j39 = (j9 & j27) ^ j38;
        long j40 = (j12 & j24) ^ j38;
        long j41 = j33 ^ j39;
        long j42 = j34 ^ j40;
        long j43 = j36 ^ j39;
        long j44 = j37 ^ j40;
        long j45 = j41 ^ j22;
        long j46 = j42 ^ j28;
        long j47 = j43 ^ j30;
        long j48 = j44 ^ j31;
        long j49 = j45 ^ j46;
        long j50 = j45 & j47;
        long j51 = j48 ^ j50;
        long j52 = (j49 & j51) ^ j46;
        long j53 = ((j46 ^ j50) & (j47 ^ j48)) ^ j48;
        long j54 = j47 ^ j53;
        long j55 = j48 & (j51 ^ j53);
        long j56 = j55 ^ j54;
        long j57 = j49 ^ (j52 & (j51 ^ j55));
        long j58 = j57 ^ j56;
        long j59 = j52 ^ j53;
        long j60 = j52 ^ j57;
        long j61 = j53 ^ j56;
        long j62 = j59 ^ j58;
        long j63 = j61 & j21;
        long j64 = j56 & j23;
        long j65 = j53 & j8;
        long j66 = j60 & j29;
        long j67 = j57 & j14;
        long j68 = j52 & j26;
        long j69 = j59 & j25;
        long j70 = j62 & j27;
        long j71 = j58 & j24;
        long j72 = j61 & j16;
        long j73 = j56 & j19;
        long j74 = j53 & j15;
        long j75 = j60 & j10;
        long j76 = j57 & j18;
        long j77 = j52 & j17;
        long j78 = j59 & j11;
        long j79 = j62 & j9;
        long j80 = j58 & j12;
        long j81 = j78 ^ j79;
        long j82 = j73 ^ j74;
        long j83 = j68 ^ j76;
        long j84 = j72 ^ j73;
        long j85 = j65 ^ j75;
        long j86 = j65 ^ j68;
        long j87 = j70 ^ j71;
        long j88 = j63 ^ j66;
        long j89 = j69 ^ j70;
        long j90 = j79 ^ j80;
        long j91 = j75 ^ j83;
        long j92 = j85 ^ j88;
        long j93 = j67 ^ j81;
        long j94 = j66 ^ j89;
        long j95 = j81 ^ j92;
        long j96 = j77 ^ j92;
        long j97 = j87 ^ j93;
        long j98 = j84 ^ j93;
        long j99 = j67 ^ j94;
        long j100 = j96 ^ j97;
        long j101 = j64 ^ j98;
        long j102 = j94 ^ j98;
        long j103 = j91 ^ (j97 ^ (-1));
        long j104 = j83 ^ (j95 ^ (-1));
        long j105 = j99 ^ j100;
        long j106 = j88 ^ j101;
        long j107 = j86 ^ j101;
        long j108 = j82 ^ j100;
        jArr[7] = j102;
        jArr[6] = j99 ^ (j106 ^ (-1));
        jArr[5] = j90 ^ (j105 ^ (-1));
        jArr[4] = j106;
        jArr[3] = j107;
        jArr[2] = j108;
        jArr[1] = j103;
        jArr[0] = j104;
    }

    private void shiftRows(long[] jArr) {
        for (int i = 0; i < jArr.length; i++) {
            long j = jArr[i];
            jArr[i] = (j & 65535) | ((j & 4293918720L) >>> 4) | ((j & 983040) << 12) | ((j & 280375465082880L) >>> 8) | ((j & 1095216660480L) << 8) | ((j & (-1152921504606846976L)) >>> 12) | ((j & 1152640029630136320L) << 4);
        }
    }

    private void mixColumns(long[] jArr) {
        long j = jArr[0];
        long j2 = jArr[1];
        long j3 = jArr[2];
        long j4 = jArr[3];
        long j5 = jArr[4];
        long j6 = jArr[5];
        long j7 = jArr[6];
        long j8 = jArr[7];
        long j9 = (j >>> 16) | (j << 48);
        long j10 = (j2 >>> 16) | (j2 << 48);
        long j11 = (j3 >>> 16) | (j3 << 48);
        long j12 = (j4 >>> 16) | (j4 << 48);
        long j13 = (j5 >>> 16) | (j5 << 48);
        long j14 = (j6 >>> 16) | (j6 << 48);
        long j15 = (j7 >>> 16) | (j7 << 48);
        long j16 = (j8 >>> 16) | (j8 << 48);
        jArr[0] = ((j8 ^ j16) ^ j9) ^ rotr32(j ^ j9);
        jArr[1] = ((((j ^ j9) ^ j8) ^ j16) ^ j10) ^ rotr32(j2 ^ j10);
        jArr[2] = ((j2 ^ j10) ^ j11) ^ rotr32(j3 ^ j11);
        jArr[3] = ((((j3 ^ j11) ^ j8) ^ j16) ^ j12) ^ rotr32(j4 ^ j12);
        jArr[4] = ((((j4 ^ j12) ^ j8) ^ j16) ^ j13) ^ rotr32(j5 ^ j13);
        jArr[5] = ((j5 ^ j13) ^ j14) ^ rotr32(j6 ^ j14);
        jArr[6] = ((j6 ^ j14) ^ j15) ^ rotr32(j7 ^ j15);
        jArr[7] = ((j7 ^ j15) ^ j16) ^ rotr32(j8 ^ j16);
    }

    private long rotr32(long j) {
        return (j << 32) | (j >>> 32);
    }

    private void addRoundKey(long[] jArr, long[] jArr2) {
        jArr[0] = jArr[0] ^ jArr2[0];
        jArr[1] = jArr[1] ^ jArr2[1];
        jArr[2] = jArr[2] ^ jArr2[2];
        jArr[3] = jArr[3] ^ jArr2[3];
        jArr[4] = jArr[4] ^ jArr2[4];
        jArr[5] = jArr[5] ^ jArr2[5];
        jArr[6] = jArr[6] ^ jArr2[6];
        jArr[7] = jArr[7] ^ jArr2[7];
    }

    private void brAesCt64InterleaveOut(int[] iArr, long[] jArr, int i) {
        long j = jArr[i] & 71777214294589695L;
        long j2 = jArr[i + 4] & 71777214294589695L;
        long j3 = (jArr[i] >>> 8) & 71777214294589695L;
        long j4 = (jArr[i + 4] >>> 8) & 71777214294589695L;
        long j5 = j | (j >>> 8);
        long j6 = j2 | (j2 >>> 8);
        long j7 = j3 | (j3 >>> 8);
        long j8 = j4 | (j4 >>> 8);
        long j9 = j5 & 281470681808895L;
        long j10 = j6 & 281470681808895L;
        long j11 = j7 & 281470681808895L;
        long j12 = j8 & 281470681808895L;
        int i2 = i << 2;
        iArr[i2] = (int) (j9 | (j9 >>> 16));
        iArr[i2 + 1] = (int) (j10 | (j10 >>> 16));
        iArr[i2 + 2] = (int) (j11 | (j11 >>> 16));
        iArr[i2 + 3] = (int) (j12 | (j12 >>> 16));
    }

    protected static void xor(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, int i3, int i4) {
        for (int i5 = 0; i5 < i4; i5++) {
            bArr3[i3 + i5] = (byte) (bArr[i + i5] ^ bArr2[i2 + i5]);
        }
    }
}
