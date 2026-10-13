package org.bouncycastle.crypto.engines;

import android.R;
import com.sun.jna.platform.win32.Ddeml;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import com.twelvemonkeys.imageio.metadata.psd.PSD;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/DESEngine.class */
public class DESEngine extends DESBase implements BlockCipher {
    protected static final int BLOCK_SIZE = 8;
    private boolean forEncryption;
    private int[] workingKey = null;
    private static final short[] bytebit = {128, 64, 32, 16, 8, 4, 2, 1};
    private static final int[] bigbyte = {8388608, 4194304, 2097152, 1048576, 524288, 262144, 131072, 65536, 32768, 16384, 8192, 4096, 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1};
    private static final byte[] pc1 = {56, 48, 40, 32, 24, 16, 8, 0, 57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 60, 52, 44, 36, 28, 20, 12, 4, 27, 19, 11, 3};
    private static final byte[] totrot = {1, 2, 4, 6, 8, 10, 12, 14, 15, 17, 19, 21, 23, 25, 27, 28};
    private static final byte[] pc2 = {13, 16, 10, 23, 0, 4, 2, 27, 14, 5, 20, 9, 22, 18, 11, 3, 25, 7, 15, 6, 26, 19, 12, 1, 40, 51, 30, 36, 46, 54, 29, 39, 50, 44, 32, 47, 43, 48, 38, 55, 33, 52, 45, 41, 49, 35, 28, 31};
    private static final int[] SP1 = {R.attr.transitionName, 0, 65536, R.attr.fillColor, R.attr.manageSpaceActivity, 66564, 4, 65536, 1024, R.attr.transitionName, R.attr.fillColor, 1024, 16778244, R.attr.manageSpaceActivity, 16777216, 4, PSD.RES_IPTC_NAA, 16778240, 16778240, 66560, 66560, R.attr.theme, R.attr.theme, 16778244, 65540, 16777220, 16777220, 65540, 0, PSD.RES_IPTC_NAA, 66564, 16777216, 65536, R.attr.fillColor, 4, R.attr.theme, R.attr.transitionName, 16777216, 16777216, 1024, R.attr.manageSpaceActivity, 65536, 66560, 16777220, 1024, 4, 16778244, 66564, R.attr.fillColor, 65540, R.attr.theme, 16778244, 16777220, PSD.RES_IPTC_NAA, 66564, R.attr.transitionName, PSD.RES_IPTC_NAA, 16778240, 16778240, 0, 65540, 66560, 0, R.attr.manageSpaceActivity};
    private static final int[] SP2 = {-2146402272, -2147450880, 32768, 1081376, 1048576, 32, WinError.SCARD_E_ICC_INSTALLATION, -2147450848, -2147483616, -2146402272, -2146402304, Integer.MIN_VALUE, -2147450880, 1048576, 32, WinError.SCARD_E_ICC_INSTALLATION, 1081344, 1048608, -2147450848, 0, Integer.MIN_VALUE, 32768, 1081376, -2146435072, 1048608, -2147483616, 0, 1081344, 32800, -2146402304, -2146435072, 32800, 0, 1081376, WinError.SCARD_E_ICC_INSTALLATION, 1048576, -2147450848, -2146435072, -2146402304, 32768, -2146435072, -2147450880, 32, -2146402272, 1081376, 32, 32768, Integer.MIN_VALUE, 32800, -2146402304, 1048576, -2147483616, 1048608, -2147450848, -2147483616, 1048608, 1081344, 0, -2147450880, 32800, Integer.MIN_VALUE, WinError.SCARD_E_ICC_INSTALLATION, -2146402272, 1081344};
    private static final int[] SP3 = {520, 134349312, 0, 134348808, 134218240, 0, 131592, 134218240, WinNT.TOKEN_READ, 134217736, 134217736, 131072, 134349320, WinNT.TOKEN_READ, 134348800, 520, 134217728, 8, 134349312, 512, 131584, 134348800, 134348808, 131592, 134218248, 131584, 131072, 134218248, 8, 134349320, 512, 134217728, 134349312, 134217728, WinNT.TOKEN_READ, 520, 131072, 134349312, 134218240, 0, 512, WinNT.TOKEN_READ, 134349320, 134218240, 134217736, 512, 0, 134348808, 134218248, 131072, 134217728, 134349320, 8, 131592, 131584, 134217736, 134348800, 134218248, 520, 134348800, 131592, 8, 134348808, 131584};
    private static final int[] SP4 = {8396801, WinError.ERROR_DS_SINGLE_VALUE_CONSTRAINT, WinError.ERROR_DS_SINGLE_VALUE_CONSTRAINT, 128, 8396928, 8388737, 8388609, 8193, 0, 8396800, 8396800, 8396929, 129, 0, 8388736, 8388609, 1, 8192, 8388608, 8396801, 128, 8388608, 8193, WinError.ERROR_DS_CANT_ADD_ATT_VALUES, 8388737, 1, WinError.ERROR_DS_CANT_ADD_ATT_VALUES, 8388736, 8192, 8396928, 8396929, 129, 8388736, 8388609, 8396800, 8396929, 129, 0, 0, 8396800, WinError.ERROR_DS_CANT_ADD_ATT_VALUES, 8388736, 8388737, 1, 8396801, WinError.ERROR_DS_SINGLE_VALUE_CONSTRAINT, WinError.ERROR_DS_SINGLE_VALUE_CONSTRAINT, 128, 8396929, 129, 1, 8192, 8388609, 8193, 8396928, 8388737, 8193, WinError.ERROR_DS_CANT_ADD_ATT_VALUES, 8388608, 8396801, 128, 8388608, 8192, 8396928};
    private static final int[] SP5 = {256, 34078976, 34078720, 1107296512, 524288, 256, 1073741824, 34078720, 1074266368, 524288, 33554688, 1074266368, 1107296512, 1107820544, 524544, 1073741824, 33554432, 1074266112, 1074266112, 0, 1073742080, 1107820800, 1107820800, 33554688, 1107820544, 1073742080, 0, 1107296256, 34078976, 33554432, 1107296256, 524544, 524288, 1107296512, 256, 33554432, 1073741824, 34078720, 1107296512, 1074266368, 33554688, 1073741824, 1107820544, 34078976, 1074266368, 256, 33554432, 1107820544, 1107820800, 524544, 1107296256, 1107820800, 34078720, 0, 1074266112, 1107296256, 524544, 33554688, 1073742080, 524288, 0, 1074266112, 34078976, 1073742080};
    private static final int[] SP6 = {536870928, 541065216, 16384, 541081616, 541065216, 16, 541081616, 4194304, 536887296, 4210704, 4194304, 536870928, 4194320, 536887296, 536870912, 16400, 0, 4194320, 536887312, 16384, 4210688, 536887312, 16, 541065232, 541065232, 0, 4210704, 541081600, 16400, 4210688, 541081600, 536870912, 536887296, 16, 541065232, 4210688, 541081616, 4194304, 16400, 536870928, 4194304, 536887296, 536870912, 16400, 536870928, 541081616, 4210688, 541065216, 4210704, 541081600, 0, 541065232, 16, 16384, 541065216, 4210704, 16384, 4194320, 536887312, 0, 541081600, 536870912, 4194320, 536887312};
    private static final int[] SP7 = {2097152, 69206018, 67110914, 0, 2048, 67110914, 2099202, 69208064, 69208066, 2097152, 0, 67108866, 2, 67108864, 69206018, 2050, 67110912, 2099202, 2097154, 67110912, 67108866, 69206016, 69208064, 2097154, 69206016, 2048, 2050, 69208066, 2099200, 2, 67108864, 2099200, 67108864, 2099200, 2097152, 67110914, 67110914, 69206018, 69206018, 2, 2097154, 67108864, 67110912, 2097152, 69208064, 2050, 2099202, 69208064, 2050, 67108866, 69208066, 69206016, 2099200, 0, 2, 69208066, 0, 2099202, 69206016, 2048, 67108866, 67110912, 2048, 2097154};
    private static final int[] SP8 = {268439616, 4096, 262144, 268701760, 268435456, 268439616, 64, 268435456, 262208, 268697600, 268701760, 266240, 268701696, 266304, 4096, 64, 268697600, 268435520, 268439552, 4160, 266240, 262208, 268697664, 268701696, 4160, 0, 0, 268697664, 268435520, 268439552, 266304, 262144, 266304, 262144, 268701696, 4096, 64, 268697664, 4096, 266304, 268439552, 64, 268435520, 268697600, 268697664, 268435456, 262144, 268439616, 0, 268701760, 262208, 268435520, 268697600, 268439552, 268439616, 0, 268701760, 266240, 266240, 4160, 4160, 262208, 268435456, 268701696};

    public DESEngine() {
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), 56));
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to DES init - " + cipherParameters.getClass().getName());
        }
        if (((KeyParameter) cipherParameters).getKeyLength() > 8) {
            throw new IllegalArgumentException("DES key too long - should be 8 bytes");
        }
        this.forEncryption = z;
        this.workingKey = generateWorkingKey(z, ((KeyParameter) cipherParameters).getKey());
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), 56, cipherParameters, Utils.getPurpose(this.forEncryption)));
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "DES";
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 8;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (this.workingKey == null) {
            throw new IllegalStateException("DES engine not initialised");
        }
        if (i + 8 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (i2 + 8 > bArr2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        desFunc(this.workingKey, bArr, i, bArr2, i2);
        return 8;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void reset() {
    }

    @Override // org.bouncycastle.crypto.engines.DESBase
    protected int[] generateWorkingKey(boolean z, byte[] bArr) {
        int[] iArr = new int[32];
        boolean[] zArr = new boolean[56];
        boolean[] zArr2 = new boolean[56];
        for (int i = 0; i < 56; i++) {
            byte b = pc1[i];
            zArr[i] = (bArr[b >>> 3] & bytebit[b & 7]) != 0;
        }
        for (int i2 = 0; i2 < 16; i2++) {
            int i3 = z ? i2 << 1 : (15 - i2) << 1;
            int i4 = i3 + 1;
            iArr[i4] = 0;
            iArr[i3] = 0;
            for (int i5 = 0; i5 < 28; i5++) {
                int i6 = i5 + totrot[i2];
                if (i6 < 28) {
                    zArr2[i5] = zArr[i6];
                } else {
                    zArr2[i5] = zArr[i6 - 28];
                }
            }
            for (int i7 = 28; i7 < 56; i7++) {
                int i8 = i7 + totrot[i2];
                if (i8 < 56) {
                    zArr2[i7] = zArr[i8];
                } else {
                    zArr2[i7] = zArr[i8 - 28];
                }
            }
            for (int i9 = 0; i9 < 24; i9++) {
                if (zArr2[pc2[i9]]) {
                    int i10 = i3;
                    iArr[i10] = iArr[i10] | bigbyte[i9];
                }
                if (zArr2[pc2[i9 + 24]]) {
                    iArr[i4] = iArr[i4] | bigbyte[i9];
                }
            }
        }
        for (int i11 = 0; i11 != 32; i11 += 2) {
            int i12 = iArr[i11];
            int i13 = iArr[i11 + 1];
            iArr[i11] = ((i12 & 16515072) << 6) | ((i12 & 4032) << 10) | ((i13 & 16515072) >>> 10) | ((i13 & 4032) >>> 6);
            iArr[i11 + 1] = ((i12 & Ddeml.CBF_FAIL_ALLSVRXACTIONS) << 12) | ((i12 & 63) << 16) | ((i13 & Ddeml.CBF_FAIL_ALLSVRXACTIONS) >>> 4) | (i13 & 63);
        }
        return iArr;
    }

    @Override // org.bouncycastle.crypto.engines.DESBase
    protected void desFunc(int[] iArr, byte[] bArr, int i, byte[] bArr2, int i2) {
        int iBigEndianToInt = Pack.bigEndianToInt(bArr, i);
        int iBigEndianToInt2 = Pack.bigEndianToInt(bArr, i + 4);
        int i3 = ((iBigEndianToInt >>> 4) ^ iBigEndianToInt2) & 252645135;
        int i4 = iBigEndianToInt2 ^ i3;
        int i5 = iBigEndianToInt ^ (i3 << 4);
        int i6 = ((i5 >>> 16) ^ i4) & 65535;
        int i7 = i4 ^ i6;
        int i8 = i5 ^ (i6 << 16);
        int i9 = ((i7 >>> 2) ^ i8) & 858993459;
        int i10 = i8 ^ i9;
        int i11 = i7 ^ (i9 << 2);
        int i12 = ((i11 >>> 8) ^ i10) & 16711935;
        int i13 = i10 ^ i12;
        int i14 = i11 ^ (i12 << 8);
        int i15 = (i14 << 1) | (i14 >>> 31);
        int i16 = (i13 ^ i15) & (-1431655766);
        int i17 = i13 ^ i16;
        int i18 = i15 ^ i16;
        int i19 = (i17 << 1) | (i17 >>> 31);
        for (int i20 = 0; i20 < 8; i20++) {
            int i21 = ((i18 << 28) | (i18 >>> 4)) ^ iArr[(i20 * 4) + 0];
            int i22 = SP7[i21 & 63] | SP5[(i21 >>> 8) & 63] | SP3[(i21 >>> 16) & 63] | SP1[(i21 >>> 24) & 63];
            int i23 = i18 ^ iArr[(i20 * 4) + 1];
            i19 ^= (((i22 | SP8[i23 & 63]) | SP6[(i23 >>> 8) & 63]) | SP4[(i23 >>> 16) & 63]) | SP2[(i23 >>> 24) & 63];
            int i24 = ((i19 << 28) | (i19 >>> 4)) ^ iArr[(i20 * 4) + 2];
            int i25 = SP7[i24 & 63] | SP5[(i24 >>> 8) & 63] | SP3[(i24 >>> 16) & 63] | SP1[(i24 >>> 24) & 63];
            int i26 = i19 ^ iArr[(i20 * 4) + 3];
            i18 ^= (((i25 | SP8[i26 & 63]) | SP6[(i26 >>> 8) & 63]) | SP4[(i26 >>> 16) & 63]) | SP2[(i26 >>> 24) & 63];
        }
        int i27 = (i18 << 31) | (i18 >>> 1);
        int i28 = (i19 ^ i27) & (-1431655766);
        int i29 = i19 ^ i28;
        int i30 = i27 ^ i28;
        int i31 = (i29 << 31) | (i29 >>> 1);
        int i32 = ((i31 >>> 8) ^ i30) & 16711935;
        int i33 = i30 ^ i32;
        int i34 = i31 ^ (i32 << 8);
        int i35 = ((i34 >>> 2) ^ i33) & 858993459;
        int i36 = i33 ^ i35;
        int i37 = i34 ^ (i35 << 2);
        int i38 = ((i36 >>> 16) ^ i37) & 65535;
        int i39 = i37 ^ i38;
        int i40 = i36 ^ (i38 << 16);
        int i41 = ((i40 >>> 4) ^ i39) & 252645135;
        Pack.intToBigEndian(i40 ^ (i41 << 4), bArr2, i2);
        Pack.intToBigEndian(i39 ^ i41, bArr2, i2 + 4);
    }
}
