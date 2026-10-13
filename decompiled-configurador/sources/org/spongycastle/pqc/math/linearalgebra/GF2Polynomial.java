package org.spongycastle.pqc.math.linearalgebra;

import com.ctc.wstx.cfg.InputConfigFlags;
import java.math.BigInteger;
import java.util.Random;
import org.apache.commons.codec.language.bm.Rule;
import org.spongycastle.asn1.cmc.BodyPartID;
import org.spongycastle.asn1.cmp.PKIFailureInfo;
import org.spongycastle.asn1.eac.CertificateBody;
import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/math/linearalgebra/GF2Polynomial.class */
public class GF2Polynomial {
    private int len;
    private int blocks;
    private int[] value;
    private static Random rand = new Random();
    private static final boolean[] parity = {false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false};
    private static final short[] squaringTable = {0, 1, 4, 5, 16, 17, 20, 21, 64, 65, 68, 69, 80, 81, 84, 85, 256, 257, 260, 261, 272, 273, 276, 277, 320, 321, 324, 325, 336, 337, 340, 341, 1024, 1025, 1028, 1029, 1040, 1041, 1044, 1045, 1088, 1089, 1092, 1093, 1104, 1105, 1108, 1109, 1280, 1281, 1284, 1285, 1296, 1297, 1300, 1301, 1344, 1345, 1348, 1349, 1360, 1361, 1364, 1365, 4096, 4097, 4100, 4101, 4112, 4113, 4116, 4117, 4160, 4161, 4164, 4165, 4176, 4177, 4180, 4181, 4352, 4353, 4356, 4357, 4368, 4369, 4372, 4373, 4416, 4417, 4420, 4421, 4432, 4433, 4436, 4437, 5120, 5121, 5124, 5125, 5136, 5137, 5140, 5141, 5184, 5185, 5188, 5189, 5200, 5201, 5204, 5205, 5376, 5377, 5380, 5381, 5392, 5393, 5396, 5397, 5440, 5441, 5444, 5445, 5456, 5457, 5460, 5461, 16384, 16385, 16388, 16389, 16400, 16401, 16404, 16405, 16448, 16449, 16452, 16453, 16464, 16465, 16468, 16469, 16640, 16641, 16644, 16645, 16656, 16657, 16660, 16661, 16704, 16705, 16708, 16709, 16720, 16721, 16724, 16725, 17408, 17409, 17412, 17413, 17424, 17425, 17428, 17429, 17472, 17473, 17476, 17477, 17488, 17489, 17492, 17493, 17664, 17665, 17668, 17669, 17680, 17681, 17684, 17685, 17728, 17729, 17732, 17733, 17744, 17745, 17748, 17749, 20480, 20481, 20484, 20485, 20496, 20497, 20500, 20501, 20544, 20545, 20548, 20549, 20560, 20561, 20564, 20565, 20736, 20737, 20740, 20741, 20752, 20753, 20756, 20757, 20800, 20801, 20804, 20805, 20816, 20817, 20820, 20821, 21504, 21505, 21508, 21509, 21520, 21521, 21524, 21525, 21568, 21569, 21572, 21573, 21584, 21585, 21588, 21589, 21760, 21761, 21764, 21765, 21776, 21777, 21780, 21781, 21824, 21825, 21828, 21829, 21840, 21841, 21844, 21845};
    private static final int[] bitMask = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, PKIFailureInfo.badCertTemplate, 2097152, 4194304, 8388608, InputConfigFlags.CFG_ALLOW_XML11_ESCAPED_CHARS_IN_XML10, InputConfigFlags.CFG_JAXP_FEATURE_SECURE_PROCESSING, 67108864, 134217728, 268435456, PKIFailureInfo.duplicateCertReq, 1073741824, PKIFailureInfo.systemUnavail, 0};
    private static final int[] reverseRightMask = {0, 1, 3, 7, 15, 31, 63, CertificateBody.profileType, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 131071, 262143, 524287, 1048575, 2097151, 4194303, 8388607, 16777215, 33554431, 67108863, 134217727, 268435455, 536870911, 1073741823, Integer.MAX_VALUE, -1};

    public GF2Polynomial(int length) {
        int l = length;
        l = l < 1 ? 1 : l;
        this.blocks = ((l - 1) >> 5) + 1;
        this.value = new int[this.blocks];
        this.len = l;
    }

    public GF2Polynomial(int length, Random rand2) {
        int l = length;
        l = l < 1 ? 1 : l;
        this.blocks = ((l - 1) >> 5) + 1;
        this.value = new int[this.blocks];
        this.len = l;
        randomize(rand2);
    }

    public GF2Polynomial(int length, String value) {
        int l = length;
        l = l < 1 ? 1 : l;
        this.blocks = ((l - 1) >> 5) + 1;
        this.value = new int[this.blocks];
        this.len = l;
        if (value.equalsIgnoreCase("ZERO")) {
            assignZero();
            return;
        }
        if (value.equalsIgnoreCase("ONE")) {
            assignOne();
            return;
        }
        if (value.equalsIgnoreCase("RANDOM")) {
            randomize();
        } else if (value.equalsIgnoreCase("X")) {
            assignX();
        } else {
            if (value.equalsIgnoreCase(Rule.ALL)) {
                assignAll();
                return;
            }
            throw new IllegalArgumentException("Error: GF2Polynomial was called using " + value + " as value!");
        }
    }

    public GF2Polynomial(int length, int[] bs) {
        int leng = length;
        leng = leng < 1 ? 1 : leng;
        this.blocks = ((leng - 1) >> 5) + 1;
        this.value = new int[this.blocks];
        this.len = leng;
        int l = Math.min(this.blocks, bs.length);
        System.arraycopy(bs, 0, this.value, 0, l);
        zeroUnusedBits();
    }

    public GF2Polynomial(int length, byte[] os) {
        int l = length;
        l = l < 1 ? 1 : l;
        this.blocks = ((l - 1) >> 5) + 1;
        this.value = new int[this.blocks];
        this.len = l;
        int k = Math.min(((os.length - 1) >> 2) + 1, this.blocks);
        for (int i = 0; i < k - 1; i++) {
            int m = (os.length - (i << 2)) - 1;
            this.value[i] = os[m] & 255;
            int[] iArr = this.value;
            int i2 = i;
            iArr[i2] = iArr[i2] | ((os[m - 1] << 8) & CipherSuite.DRAFT_TLS_DHE_RSA_WITH_AES_128_OCB);
            int[] iArr2 = this.value;
            int i3 = i;
            iArr2[i3] = iArr2[i3] | ((os[m - 2] << 16) & 16711680);
            int[] iArr3 = this.value;
            int i4 = i;
            iArr3[i4] = iArr3[i4] | ((os[m - 3] << 24) & (-16777216));
        }
        int i5 = k - 1;
        int m2 = (os.length - (i5 << 2)) - 1;
        this.value[i5] = os[m2] & 255;
        if (m2 > 0) {
            int[] iArr4 = this.value;
            iArr4[i5] = iArr4[i5] | ((os[m2 - 1] << 8) & CipherSuite.DRAFT_TLS_DHE_RSA_WITH_AES_128_OCB);
        }
        if (m2 > 1) {
            int[] iArr5 = this.value;
            iArr5[i5] = iArr5[i5] | ((os[m2 - 2] << 16) & 16711680);
        }
        if (m2 > 2) {
            int[] iArr6 = this.value;
            iArr6[i5] = iArr6[i5] | ((os[m2 - 3] << 24) & (-16777216));
        }
        zeroUnusedBits();
        reduceN();
    }

    public GF2Polynomial(int length, BigInteger bi) {
        int l = length;
        l = l < 1 ? 1 : l;
        this.blocks = ((l - 1) >> 5) + 1;
        this.value = new int[this.blocks];
        this.len = l;
        byte[] val = bi.toByteArray();
        if (val[0] == 0) {
            byte[] dummy = new byte[val.length - 1];
            System.arraycopy(val, 1, dummy, 0, dummy.length);
            val = dummy;
        }
        int ov = val.length & 3;
        int k = ((val.length - 1) >> 2) + 1;
        for (int i = 0; i < ov; i++) {
            int[] iArr = this.value;
            int i2 = k - 1;
            iArr[i2] = iArr[i2] | ((val[i] & 255) << (((ov - 1) - i) << 3));
        }
        for (int i3 = 0; i3 <= ((val.length - 4) >> 2); i3++) {
            int m = (val.length - 1) - (i3 << 2);
            this.value[i3] = val[m] & 255;
            int[] iArr2 = this.value;
            int i4 = i3;
            iArr2[i4] = iArr2[i4] | ((val[m - 1] << 8) & CipherSuite.DRAFT_TLS_DHE_RSA_WITH_AES_128_OCB);
            int[] iArr3 = this.value;
            int i5 = i3;
            iArr3[i5] = iArr3[i5] | ((val[m - 2] << 16) & 16711680);
            int[] iArr4 = this.value;
            int i6 = i3;
            iArr4[i6] = iArr4[i6] | ((val[m - 3] << 24) & (-16777216));
        }
        if ((this.len & 31) != 0) {
            int[] iArr5 = this.value;
            int i7 = this.blocks - 1;
            iArr5[i7] = iArr5[i7] & reverseRightMask[this.len & 31];
        }
        reduceN();
    }

    public GF2Polynomial(GF2Polynomial b) {
        this.len = b.len;
        this.blocks = b.blocks;
        this.value = IntUtils.clone(b.value);
    }

    public Object clone() {
        return new GF2Polynomial(this);
    }

    public int getLength() {
        return this.len;
    }

    public int[] toIntegerArray() {
        int[] result = new int[this.blocks];
        System.arraycopy(this.value, 0, result, 0, this.blocks);
        return result;
    }

    public String toString(int radix) {
        char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        String[] BIN_CHARS = {"0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"};
        String res = new String();
        if (radix == 16) {
            for (int i = this.blocks - 1; i >= 0; i--) {
                res = ((((((((res + HEX_CHARS[(this.value[i] >>> 28) & 15]) + HEX_CHARS[(this.value[i] >>> 24) & 15]) + HEX_CHARS[(this.value[i] >>> 20) & 15]) + HEX_CHARS[(this.value[i] >>> 16) & 15]) + HEX_CHARS[(this.value[i] >>> 12) & 15]) + HEX_CHARS[(this.value[i] >>> 8) & 15]) + HEX_CHARS[(this.value[i] >>> 4) & 15]) + HEX_CHARS[this.value[i] & 15]) + " ";
            }
        } else {
            for (int i2 = this.blocks - 1; i2 >= 0; i2--) {
                res = ((((((((res + BIN_CHARS[(this.value[i2] >>> 28) & 15]) + BIN_CHARS[(this.value[i2] >>> 24) & 15]) + BIN_CHARS[(this.value[i2] >>> 20) & 15]) + BIN_CHARS[(this.value[i2] >>> 16) & 15]) + BIN_CHARS[(this.value[i2] >>> 12) & 15]) + BIN_CHARS[(this.value[i2] >>> 8) & 15]) + BIN_CHARS[(this.value[i2] >>> 4) & 15]) + BIN_CHARS[this.value[i2] & 15]) + " ";
            }
        }
        return res;
    }

    public byte[] toByteArray() {
        int k = ((this.len - 1) >> 3) + 1;
        int ov = k & 3;
        byte[] res = new byte[k];
        for (int i = 0; i < (k >> 2); i++) {
            int m = (k - (i << 2)) - 1;
            res[m] = (byte) (this.value[i] & 255);
            res[m - 1] = (byte) ((this.value[i] & CipherSuite.DRAFT_TLS_DHE_RSA_WITH_AES_128_OCB) >>> 8);
            res[m - 2] = (byte) ((this.value[i] & 16711680) >>> 16);
            res[m - 3] = (byte) ((this.value[i] & (-16777216)) >>> 24);
        }
        for (int i2 = 0; i2 < ov; i2++) {
            int m2 = ((ov - i2) - 1) << 3;
            res[i2] = (byte) ((this.value[this.blocks - 1] & (255 << m2)) >>> m2);
        }
        return res;
    }

    public BigInteger toFlexiBigInt() {
        if (this.len == 0 || isZero()) {
            return new BigInteger(0, new byte[0]);
        }
        return new BigInteger(1, toByteArray());
    }

    public void assignOne() {
        for (int i = 1; i < this.blocks; i++) {
            this.value[i] = 0;
        }
        this.value[0] = 1;
    }

    public void assignX() {
        for (int i = 1; i < this.blocks; i++) {
            this.value[i] = 0;
        }
        this.value[0] = 2;
    }

    public void assignAll() {
        for (int i = 0; i < this.blocks; i++) {
            this.value[i] = -1;
        }
        zeroUnusedBits();
    }

    public void assignZero() {
        for (int i = 0; i < this.blocks; i++) {
            this.value[i] = 0;
        }
    }

    public void randomize() {
        for (int i = 0; i < this.blocks; i++) {
            this.value[i] = rand.nextInt();
        }
        zeroUnusedBits();
    }

    public void randomize(Random rand2) {
        for (int i = 0; i < this.blocks; i++) {
            this.value[i] = rand2.nextInt();
        }
        zeroUnusedBits();
    }

    public boolean equals(Object other) {
        if (other == null || !(other instanceof GF2Polynomial)) {
            return false;
        }
        GF2Polynomial otherPol = (GF2Polynomial) other;
        if (this.len != otherPol.len) {
            return false;
        }
        for (int i = 0; i < this.blocks; i++) {
            if (this.value[i] != otherPol.value[i]) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return this.len + this.value.hashCode();
    }

    public boolean isZero() {
        if (this.len == 0) {
            return true;
        }
        for (int i = 0; i < this.blocks; i++) {
            if (this.value[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isOne() {
        for (int i = 1; i < this.blocks; i++) {
            if (this.value[i] != 0) {
                return false;
            }
        }
        if (this.value[0] != 1) {
            return false;
        }
        return true;
    }

    public void addToThis(GF2Polynomial b) {
        expandN(b.len);
        xorThisBy(b);
    }

    public GF2Polynomial add(GF2Polynomial b) {
        return xor(b);
    }

    public void subtractFromThis(GF2Polynomial b) {
        expandN(b.len);
        xorThisBy(b);
    }

    public GF2Polynomial subtract(GF2Polynomial b) {
        return xor(b);
    }

    public void increaseThis() {
        xorBit(0);
    }

    public GF2Polynomial increase() {
        GF2Polynomial result = new GF2Polynomial(this);
        result.increaseThis();
        return result;
    }

    public GF2Polynomial multiplyClassic(GF2Polynomial b) {
        GF2Polynomial result = new GF2Polynomial(Math.max(this.len, b.len) << 1);
        GF2Polynomial[] m = new GF2Polynomial[32];
        m[0] = new GF2Polynomial(this);
        for (int i = 1; i <= 31; i++) {
            m[i] = m[i - 1].shiftLeft();
        }
        for (int i2 = 0; i2 < b.blocks; i2++) {
            for (int j = 0; j <= 31; j++) {
                if ((b.value[i2] & bitMask[j]) != 0) {
                    result.xorThisBy(m[j]);
                }
            }
            for (int j2 = 0; j2 <= 31; j2++) {
                m[j2].shiftBlocksLeft();
            }
        }
        return result;
    }

    public GF2Polynomial multiply(GF2Polynomial b) {
        int n = Math.max(this.len, b.len);
        expandN(n);
        b.expandN(n);
        return karaMult(b);
    }

    private GF2Polynomial karaMult(GF2Polynomial b) {
        GF2Polynomial result = new GF2Polynomial(this.len << 1);
        if (this.len <= 32) {
            result.value = mult32(this.value[0], b.value[0]);
            return result;
        }
        if (this.len <= 64) {
            result.value = mult64(this.value, b.value);
            return result;
        }
        if (this.len <= 128) {
            result.value = mult128(this.value, b.value);
            return result;
        }
        if (this.len <= 256) {
            result.value = mult256(this.value, b.value);
            return result;
        }
        if (this.len <= 512) {
            result.value = mult512(this.value, b.value);
            return result;
        }
        int n = bitMask[IntegerFunctions.floorLog(this.len - 1)];
        GF2Polynomial a0 = lower(((n - 1) >> 5) + 1);
        GF2Polynomial a1 = upper(((n - 1) >> 5) + 1);
        GF2Polynomial b0 = b.lower(((n - 1) >> 5) + 1);
        GF2Polynomial b1 = b.upper(((n - 1) >> 5) + 1);
        GF2Polynomial c = a1.karaMult(b1);
        GF2Polynomial e = a0.karaMult(b0);
        a0.addToThis(a1);
        b0.addToThis(b1);
        GF2Polynomial d = a0.karaMult(b0);
        result.shiftLeftAddThis(c, n << 1);
        result.shiftLeftAddThis(c, n);
        result.shiftLeftAddThis(d, n);
        result.shiftLeftAddThis(e, n);
        result.addToThis(e);
        return result;
    }

    private static int[] mult512(int[] a, int[] b) {
        int[] result = new int[32];
        int[] a0 = new int[8];
        System.arraycopy(a, 0, a0, 0, Math.min(8, a.length));
        int[] a1 = new int[8];
        if (a.length > 8) {
            System.arraycopy(a, 8, a1, 0, Math.min(8, a.length - 8));
        }
        int[] b0 = new int[8];
        System.arraycopy(b, 0, b0, 0, Math.min(8, b.length));
        int[] b1 = new int[8];
        if (b.length > 8) {
            System.arraycopy(b, 8, b1, 0, Math.min(8, b.length - 8));
        }
        int[] c = mult256(a1, b1);
        result[31] = result[31] ^ c[15];
        result[30] = result[30] ^ c[14];
        result[29] = result[29] ^ c[13];
        result[28] = result[28] ^ c[12];
        result[27] = result[27] ^ c[11];
        result[26] = result[26] ^ c[10];
        result[25] = result[25] ^ c[9];
        result[24] = result[24] ^ c[8];
        result[23] = result[23] ^ (c[7] ^ c[15]);
        result[22] = result[22] ^ (c[6] ^ c[14]);
        result[21] = result[21] ^ (c[5] ^ c[13]);
        result[20] = result[20] ^ (c[4] ^ c[12]);
        result[19] = result[19] ^ (c[3] ^ c[11]);
        result[18] = result[18] ^ (c[2] ^ c[10]);
        result[17] = result[17] ^ (c[1] ^ c[9]);
        result[16] = result[16] ^ (c[0] ^ c[8]);
        result[15] = result[15] ^ c[7];
        result[14] = result[14] ^ c[6];
        result[13] = result[13] ^ c[5];
        result[12] = result[12] ^ c[4];
        result[11] = result[11] ^ c[3];
        result[10] = result[10] ^ c[2];
        result[9] = result[9] ^ c[1];
        result[8] = result[8] ^ c[0];
        a1[0] = a1[0] ^ a0[0];
        a1[1] = a1[1] ^ a0[1];
        a1[2] = a1[2] ^ a0[2];
        a1[3] = a1[3] ^ a0[3];
        a1[4] = a1[4] ^ a0[4];
        a1[5] = a1[5] ^ a0[5];
        a1[6] = a1[6] ^ a0[6];
        a1[7] = a1[7] ^ a0[7];
        b1[0] = b1[0] ^ b0[0];
        b1[1] = b1[1] ^ b0[1];
        b1[2] = b1[2] ^ b0[2];
        b1[3] = b1[3] ^ b0[3];
        b1[4] = b1[4] ^ b0[4];
        b1[5] = b1[5] ^ b0[5];
        b1[6] = b1[6] ^ b0[6];
        b1[7] = b1[7] ^ b0[7];
        int[] d = mult256(a1, b1);
        result[23] = result[23] ^ d[15];
        result[22] = result[22] ^ d[14];
        result[21] = result[21] ^ d[13];
        result[20] = result[20] ^ d[12];
        result[19] = result[19] ^ d[11];
        result[18] = result[18] ^ d[10];
        result[17] = result[17] ^ d[9];
        result[16] = result[16] ^ d[8];
        result[15] = result[15] ^ d[7];
        result[14] = result[14] ^ d[6];
        result[13] = result[13] ^ d[5];
        result[12] = result[12] ^ d[4];
        result[11] = result[11] ^ d[3];
        result[10] = result[10] ^ d[2];
        result[9] = result[9] ^ d[1];
        result[8] = result[8] ^ d[0];
        int[] e = mult256(a0, b0);
        result[23] = result[23] ^ e[15];
        result[22] = result[22] ^ e[14];
        result[21] = result[21] ^ e[13];
        result[20] = result[20] ^ e[12];
        result[19] = result[19] ^ e[11];
        result[18] = result[18] ^ e[10];
        result[17] = result[17] ^ e[9];
        result[16] = result[16] ^ e[8];
        result[15] = result[15] ^ (e[7] ^ e[15]);
        result[14] = result[14] ^ (e[6] ^ e[14]);
        result[13] = result[13] ^ (e[5] ^ e[13]);
        result[12] = result[12] ^ (e[4] ^ e[12]);
        result[11] = result[11] ^ (e[3] ^ e[11]);
        result[10] = result[10] ^ (e[2] ^ e[10]);
        result[9] = result[9] ^ (e[1] ^ e[9]);
        result[8] = result[8] ^ (e[0] ^ e[8]);
        result[7] = result[7] ^ e[7];
        result[6] = result[6] ^ e[6];
        result[5] = result[5] ^ e[5];
        result[4] = result[4] ^ e[4];
        result[3] = result[3] ^ e[3];
        result[2] = result[2] ^ e[2];
        result[1] = result[1] ^ e[1];
        result[0] = result[0] ^ e[0];
        return result;
    }

    private static int[] mult256(int[] a, int[] b) {
        int[] result = new int[16];
        int[] a0 = new int[4];
        System.arraycopy(a, 0, a0, 0, Math.min(4, a.length));
        int[] a1 = new int[4];
        if (a.length > 4) {
            System.arraycopy(a, 4, a1, 0, Math.min(4, a.length - 4));
        }
        int[] b0 = new int[4];
        System.arraycopy(b, 0, b0, 0, Math.min(4, b.length));
        int[] b1 = new int[4];
        if (b.length > 4) {
            System.arraycopy(b, 4, b1, 0, Math.min(4, b.length - 4));
        }
        if (a1[3] == 0 && a1[2] == 0 && b1[3] == 0 && b1[2] == 0) {
            if (a1[1] == 0 && b1[1] == 0) {
                if (a1[0] != 0 || b1[0] != 0) {
                    int[] c = mult32(a1[0], b1[0]);
                    result[9] = result[9] ^ c[1];
                    result[8] = result[8] ^ c[0];
                    result[5] = result[5] ^ c[1];
                    result[4] = result[4] ^ c[0];
                }
            } else {
                int[] c2 = mult64(a1, b1);
                result[11] = result[11] ^ c2[3];
                result[10] = result[10] ^ c2[2];
                result[9] = result[9] ^ c2[1];
                result[8] = result[8] ^ c2[0];
                result[7] = result[7] ^ c2[3];
                result[6] = result[6] ^ c2[2];
                result[5] = result[5] ^ c2[1];
                result[4] = result[4] ^ c2[0];
            }
        } else {
            int[] c3 = mult128(a1, b1);
            result[15] = result[15] ^ c3[7];
            result[14] = result[14] ^ c3[6];
            result[13] = result[13] ^ c3[5];
            result[12] = result[12] ^ c3[4];
            result[11] = result[11] ^ (c3[3] ^ c3[7]);
            result[10] = result[10] ^ (c3[2] ^ c3[6]);
            result[9] = result[9] ^ (c3[1] ^ c3[5]);
            result[8] = result[8] ^ (c3[0] ^ c3[4]);
            result[7] = result[7] ^ c3[3];
            result[6] = result[6] ^ c3[2];
            result[5] = result[5] ^ c3[1];
            result[4] = result[4] ^ c3[0];
        }
        a1[0] = a1[0] ^ a0[0];
        a1[1] = a1[1] ^ a0[1];
        a1[2] = a1[2] ^ a0[2];
        a1[3] = a1[3] ^ a0[3];
        b1[0] = b1[0] ^ b0[0];
        b1[1] = b1[1] ^ b0[1];
        b1[2] = b1[2] ^ b0[2];
        b1[3] = b1[3] ^ b0[3];
        int[] d = mult128(a1, b1);
        result[11] = result[11] ^ d[7];
        result[10] = result[10] ^ d[6];
        result[9] = result[9] ^ d[5];
        result[8] = result[8] ^ d[4];
        result[7] = result[7] ^ d[3];
        result[6] = result[6] ^ d[2];
        result[5] = result[5] ^ d[1];
        result[4] = result[4] ^ d[0];
        int[] e = mult128(a0, b0);
        result[11] = result[11] ^ e[7];
        result[10] = result[10] ^ e[6];
        result[9] = result[9] ^ e[5];
        result[8] = result[8] ^ e[4];
        result[7] = result[7] ^ (e[3] ^ e[7]);
        result[6] = result[6] ^ (e[2] ^ e[6]);
        result[5] = result[5] ^ (e[1] ^ e[5]);
        result[4] = result[4] ^ (e[0] ^ e[4]);
        result[3] = result[3] ^ e[3];
        result[2] = result[2] ^ e[2];
        result[1] = result[1] ^ e[1];
        result[0] = result[0] ^ e[0];
        return result;
    }

    private static int[] mult128(int[] a, int[] b) {
        int[] result = new int[8];
        int[] a0 = new int[2];
        System.arraycopy(a, 0, a0, 0, Math.min(2, a.length));
        int[] a1 = new int[2];
        if (a.length > 2) {
            System.arraycopy(a, 2, a1, 0, Math.min(2, a.length - 2));
        }
        int[] b0 = new int[2];
        System.arraycopy(b, 0, b0, 0, Math.min(2, b.length));
        int[] b1 = new int[2];
        if (b.length > 2) {
            System.arraycopy(b, 2, b1, 0, Math.min(2, b.length - 2));
        }
        if (a1[1] == 0 && b1[1] == 0) {
            if (a1[0] != 0 || b1[0] != 0) {
                int[] c = mult32(a1[0], b1[0]);
                result[5] = result[5] ^ c[1];
                result[4] = result[4] ^ c[0];
                result[3] = result[3] ^ c[1];
                result[2] = result[2] ^ c[0];
            }
        } else {
            int[] c2 = mult64(a1, b1);
            result[7] = result[7] ^ c2[3];
            result[6] = result[6] ^ c2[2];
            result[5] = result[5] ^ (c2[1] ^ c2[3]);
            result[4] = result[4] ^ (c2[0] ^ c2[2]);
            result[3] = result[3] ^ c2[1];
            result[2] = result[2] ^ c2[0];
        }
        a1[0] = a1[0] ^ a0[0];
        a1[1] = a1[1] ^ a0[1];
        b1[0] = b1[0] ^ b0[0];
        b1[1] = b1[1] ^ b0[1];
        if (a1[1] == 0 && b1[1] == 0) {
            int[] d = mult32(a1[0], b1[0]);
            result[3] = result[3] ^ d[1];
            result[2] = result[2] ^ d[0];
        } else {
            int[] d2 = mult64(a1, b1);
            result[5] = result[5] ^ d2[3];
            result[4] = result[4] ^ d2[2];
            result[3] = result[3] ^ d2[1];
            result[2] = result[2] ^ d2[0];
        }
        if (a0[1] == 0 && b0[1] == 0) {
            int[] e = mult32(a0[0], b0[0]);
            result[3] = result[3] ^ e[1];
            result[2] = result[2] ^ e[0];
            result[1] = result[1] ^ e[1];
            result[0] = result[0] ^ e[0];
        } else {
            int[] e2 = mult64(a0, b0);
            result[5] = result[5] ^ e2[3];
            result[4] = result[4] ^ e2[2];
            result[3] = result[3] ^ (e2[1] ^ e2[3]);
            result[2] = result[2] ^ (e2[0] ^ e2[2]);
            result[1] = result[1] ^ e2[1];
            result[0] = result[0] ^ e2[0];
        }
        return result;
    }

    private static int[] mult64(int[] a, int[] b) {
        int[] result = new int[4];
        int a0 = a[0];
        int a1 = 0;
        if (a.length > 1) {
            a1 = a[1];
        }
        int b0 = b[0];
        int b1 = 0;
        if (b.length > 1) {
            b1 = b[1];
        }
        if (a1 != 0 || b1 != 0) {
            int[] c = mult32(a1, b1);
            result[3] = result[3] ^ c[1];
            result[2] = result[2] ^ (c[0] ^ c[1]);
            result[1] = result[1] ^ c[0];
        }
        int[] d = mult32(a0 ^ a1, b0 ^ b1);
        result[2] = result[2] ^ d[1];
        result[1] = result[1] ^ d[0];
        int[] e = mult32(a0, b0);
        result[2] = result[2] ^ e[1];
        result[1] = result[1] ^ (e[0] ^ e[1]);
        result[0] = result[0] ^ e[0];
        return result;
    }

    private static int[] mult32(int a, int b) {
        int[] result = new int[2];
        if (a == 0 || b == 0) {
            return result;
        }
        long b2 = b;
        long b3 = b2 & BodyPartID.bodyIdMax;
        long h = 0;
        for (int i = 1; i <= 32; i++) {
            if ((a & bitMask[i - 1]) != 0) {
                h ^= b3;
            }
            b3 <<= 1;
        }
        result[1] = (int) (h >>> 32);
        result[0] = (int) (h & BodyPartID.bodyIdMax);
        return result;
    }

    private GF2Polynomial upper(int k) {
        int j = Math.min(k, this.blocks - k);
        GF2Polynomial result = new GF2Polynomial(j << 5);
        if (this.blocks >= k) {
            System.arraycopy(this.value, k, result.value, 0, j);
        }
        return result;
    }

    private GF2Polynomial lower(int k) {
        GF2Polynomial result = new GF2Polynomial(k << 5);
        System.arraycopy(this.value, 0, result.value, 0, Math.min(k, this.blocks));
        return result;
    }

    public GF2Polynomial remainder(GF2Polynomial g) throws RuntimeException {
        GF2Polynomial a = new GF2Polynomial(this);
        GF2Polynomial b = new GF2Polynomial(g);
        if (b.isZero()) {
            throw new RuntimeException();
        }
        a.reduceN();
        b.reduceN();
        if (a.len < b.len) {
            return a;
        }
        int i = a.len;
        int i2 = b.len;
        while (true) {
            int i3 = i - i2;
            if (i3 >= 0) {
                GF2Polynomial j = b.shiftLeft(i3);
                a.subtractFromThis(j);
                a.reduceN();
                i = a.len;
                i2 = b.len;
            } else {
                return a;
            }
        }
    }

    public GF2Polynomial quotient(GF2Polynomial g) throws RuntimeException {
        GF2Polynomial q = new GF2Polynomial(this.len);
        GF2Polynomial a = new GF2Polynomial(this);
        GF2Polynomial b = new GF2Polynomial(g);
        if (b.isZero()) {
            throw new RuntimeException();
        }
        a.reduceN();
        b.reduceN();
        if (a.len < b.len) {
            return new GF2Polynomial(0);
        }
        int i = a.len - b.len;
        q.expandN(i + 1);
        while (i >= 0) {
            GF2Polynomial j = b.shiftLeft(i);
            a.subtractFromThis(j);
            a.reduceN();
            q.xorBit(i);
            i = a.len - b.len;
        }
        return q;
    }

    public GF2Polynomial[] divide(GF2Polynomial g) throws RuntimeException {
        GF2Polynomial[] result = new GF2Polynomial[2];
        GF2Polynomial q = new GF2Polynomial(this.len);
        GF2Polynomial a = new GF2Polynomial(this);
        GF2Polynomial b = new GF2Polynomial(g);
        if (b.isZero()) {
            throw new RuntimeException();
        }
        a.reduceN();
        b.reduceN();
        if (a.len < b.len) {
            result[0] = new GF2Polynomial(0);
            result[1] = a;
            return result;
        }
        int i = a.len - b.len;
        q.expandN(i + 1);
        while (i >= 0) {
            GF2Polynomial j = b.shiftLeft(i);
            a.subtractFromThis(j);
            a.reduceN();
            q.xorBit(i);
            i = a.len - b.len;
        }
        result[0] = q;
        result[1] = a;
        return result;
    }

    public GF2Polynomial gcd(GF2Polynomial g) throws RuntimeException {
        if (isZero() && g.isZero()) {
            throw new ArithmeticException("Both operands of gcd equal zero.");
        }
        if (isZero()) {
            return new GF2Polynomial(g);
        }
        if (g.isZero()) {
            return new GF2Polynomial(this);
        }
        GF2Polynomial a = new GF2Polynomial(this);
        GF2Polynomial gF2Polynomial = new GF2Polynomial(g);
        while (true) {
            GF2Polynomial b = gF2Polynomial;
            if (!b.isZero()) {
                GF2Polynomial c = a.remainder(b);
                a = b;
                gF2Polynomial = c;
            } else {
                return a;
            }
        }
    }

    public boolean isIrreducible() {
        if (isZero()) {
            return false;
        }
        GF2Polynomial f = new GF2Polynomial(this);
        f.reduceN();
        int d = f.len - 1;
        GF2Polynomial u = new GF2Polynomial(f.len, "X");
        for (int i = 1; i <= (d >> 1); i++) {
            u.squareThisPreCalc();
            u = u.remainder(f);
            GF2Polynomial dummy = u.add(new GF2Polynomial(32, "X"));
            if (!dummy.isZero()) {
                GF2Polynomial g = f.gcd(dummy);
                if (!g.isOne()) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    void reduceTrinomial(int m, int tc) {
        int p0 = m >>> 5;
        int q0 = 32 - (m & 31);
        int p1 = (m - tc) >>> 5;
        int q1 = 32 - ((m - tc) & 31);
        int max = ((m << 1) - 2) >>> 5;
        for (int i = max; i > p0; i--) {
            long t = ((long) this.value[i]) & BodyPartID.bodyIdMax;
            int[] iArr = this.value;
            int i2 = (i - p0) - 1;
            iArr[i2] = iArr[i2] ^ ((int) (t << q0));
            int[] iArr2 = this.value;
            int i3 = i - p0;
            iArr2[i3] = (int) (((long) iArr2[i3]) ^ (t >>> (32 - q0)));
            int[] iArr3 = this.value;
            int i4 = (i - p1) - 1;
            iArr3[i4] = iArr3[i4] ^ ((int) (t << q1));
            int[] iArr4 = this.value;
            int i5 = i - p1;
            iArr4[i5] = (int) (((long) iArr4[i5]) ^ (t >>> (32 - q1)));
            this.value[i] = 0;
        }
        long t2 = ((long) this.value[p0]) & BodyPartID.bodyIdMax & (BodyPartID.bodyIdMax << (m & 31));
        int[] iArr5 = this.value;
        iArr5[0] = (int) (((long) iArr5[0]) ^ (t2 >>> (32 - q0)));
        if ((p0 - p1) - 1 >= 0) {
            int[] iArr6 = this.value;
            int i6 = (p0 - p1) - 1;
            iArr6[i6] = iArr6[i6] ^ ((int) (t2 << q1));
        }
        int[] iArr7 = this.value;
        int i7 = p0 - p1;
        iArr7[i7] = (int) (((long) iArr7[i7]) ^ (t2 >>> (32 - q1)));
        int[] iArr8 = this.value;
        iArr8[p0] = iArr8[p0] & reverseRightMask[m & 31];
        this.blocks = ((m - 1) >>> 5) + 1;
        this.len = m;
    }

    void reducePentanomial(int m, int[] pc) {
        int p0 = m >>> 5;
        int q0 = 32 - (m & 31);
        int p1 = (m - pc[0]) >>> 5;
        int q1 = 32 - ((m - pc[0]) & 31);
        int p2 = (m - pc[1]) >>> 5;
        int q2 = 32 - ((m - pc[1]) & 31);
        int p3 = (m - pc[2]) >>> 5;
        int q3 = 32 - ((m - pc[2]) & 31);
        int max = ((m << 1) - 2) >>> 5;
        for (int i = max; i > p0; i--) {
            long t = ((long) this.value[i]) & BodyPartID.bodyIdMax;
            int[] iArr = this.value;
            int i2 = (i - p0) - 1;
            iArr[i2] = iArr[i2] ^ ((int) (t << q0));
            int[] iArr2 = this.value;
            int i3 = i - p0;
            iArr2[i3] = (int) (((long) iArr2[i3]) ^ (t >>> (32 - q0)));
            int[] iArr3 = this.value;
            int i4 = (i - p1) - 1;
            iArr3[i4] = iArr3[i4] ^ ((int) (t << q1));
            int[] iArr4 = this.value;
            int i5 = i - p1;
            iArr4[i5] = (int) (((long) iArr4[i5]) ^ (t >>> (32 - q1)));
            int[] iArr5 = this.value;
            int i6 = (i - p2) - 1;
            iArr5[i6] = iArr5[i6] ^ ((int) (t << q2));
            int[] iArr6 = this.value;
            int i7 = i - p2;
            iArr6[i7] = (int) (((long) iArr6[i7]) ^ (t >>> (32 - q2)));
            int[] iArr7 = this.value;
            int i8 = (i - p3) - 1;
            iArr7[i8] = iArr7[i8] ^ ((int) (t << q3));
            int[] iArr8 = this.value;
            int i9 = i - p3;
            iArr8[i9] = (int) (((long) iArr8[i9]) ^ (t >>> (32 - q3)));
            this.value[i] = 0;
        }
        long t2 = ((long) this.value[p0]) & BodyPartID.bodyIdMax & (BodyPartID.bodyIdMax << (m & 31));
        int[] iArr9 = this.value;
        iArr9[0] = (int) (((long) iArr9[0]) ^ (t2 >>> (32 - q0)));
        if ((p0 - p1) - 1 >= 0) {
            int[] iArr10 = this.value;
            int i10 = (p0 - p1) - 1;
            iArr10[i10] = iArr10[i10] ^ ((int) (t2 << q1));
        }
        int[] iArr11 = this.value;
        int i11 = p0 - p1;
        iArr11[i11] = (int) (((long) iArr11[i11]) ^ (t2 >>> (32 - q1)));
        if ((p0 - p2) - 1 >= 0) {
            int[] iArr12 = this.value;
            int i12 = (p0 - p2) - 1;
            iArr12[i12] = iArr12[i12] ^ ((int) (t2 << q2));
        }
        int[] iArr13 = this.value;
        int i13 = p0 - p2;
        iArr13[i13] = (int) (((long) iArr13[i13]) ^ (t2 >>> (32 - q2)));
        if ((p0 - p3) - 1 >= 0) {
            int[] iArr14 = this.value;
            int i14 = (p0 - p3) - 1;
            iArr14[i14] = iArr14[i14] ^ ((int) (t2 << q3));
        }
        int[] iArr15 = this.value;
        int i15 = p0 - p3;
        iArr15[i15] = (int) (((long) iArr15[i15]) ^ (t2 >>> (32 - q3)));
        int[] iArr16 = this.value;
        iArr16[p0] = iArr16[p0] & reverseRightMask[m & 31];
        this.blocks = ((m - 1) >>> 5) + 1;
        this.len = m;
    }

    public void reduceN() {
        int i = this.blocks - 1;
        while (this.value[i] == 0 && i > 0) {
            i--;
        }
        int h = this.value[i];
        int j = 0;
        while (h != 0) {
            h >>>= 1;
            j++;
        }
        this.len = (i << 5) + j;
        this.blocks = i + 1;
    }

    public void expandN(int i) {
        if (this.len >= i) {
            return;
        }
        this.len = i;
        int k = ((i - 1) >>> 5) + 1;
        if (this.blocks >= k) {
            return;
        }
        if (this.value.length >= k) {
            for (int j = this.blocks; j < k; j++) {
                this.value[j] = 0;
            }
            this.blocks = k;
            return;
        }
        int[] bs = new int[k];
        System.arraycopy(this.value, 0, bs, 0, this.blocks);
        this.blocks = k;
        this.value = null;
        this.value = bs;
    }

    public void squareThisBitwise() {
        if (isZero()) {
            return;
        }
        int[] result = new int[this.blocks << 1];
        for (int i = this.blocks - 1; i >= 0; i--) {
            int h = this.value[i];
            int j = 1;
            for (int k = 0; k < 16; k++) {
                if ((h & 1) != 0) {
                    int i2 = i << 1;
                    result[i2] = result[i2] | j;
                }
                if ((h & 65536) != 0) {
                    int i3 = (i << 1) + 1;
                    result[i3] = result[i3] | j;
                }
                j <<= 2;
                h >>>= 1;
            }
        }
        this.value = null;
        this.value = result;
        this.blocks = result.length;
        this.len = (this.len << 1) - 1;
    }

    public void squareThisPreCalc() {
        if (isZero()) {
            return;
        }
        if (this.value.length >= (this.blocks << 1)) {
            for (int i = this.blocks - 1; i >= 0; i--) {
                this.value[(i << 1) + 1] = squaringTable[(this.value[i] & 16711680) >>> 16] | (squaringTable[(this.value[i] & (-16777216)) >>> 24] << 16);
                this.value[i << 1] = squaringTable[this.value[i] & 255] | (squaringTable[(this.value[i] & CipherSuite.DRAFT_TLS_DHE_RSA_WITH_AES_128_OCB) >>> 8] << 16);
            }
            this.blocks <<= 1;
            this.len = (this.len << 1) - 1;
            return;
        }
        int[] result = new int[this.blocks << 1];
        for (int i2 = 0; i2 < this.blocks; i2++) {
            result[i2 << 1] = squaringTable[this.value[i2] & 255] | (squaringTable[(this.value[i2] & CipherSuite.DRAFT_TLS_DHE_RSA_WITH_AES_128_OCB) >>> 8] << 16);
            result[(i2 << 1) + 1] = squaringTable[(this.value[i2] & 16711680) >>> 16] | (squaringTable[(this.value[i2] & (-16777216)) >>> 24] << 16);
        }
        this.value = null;
        this.value = result;
        this.blocks <<= 1;
        this.len = (this.len << 1) - 1;
    }

    public boolean vectorMult(GF2Polynomial b) throws RuntimeException {
        boolean result = false;
        if (this.len != b.len) {
            throw new RuntimeException();
        }
        for (int i = 0; i < this.blocks; i++) {
            int h = this.value[i] & b.value[i];
            result = (((result ^ parity[h & 255]) ^ parity[(h >>> 8) & 255]) ^ parity[(h >>> 16) & 255]) ^ parity[(h >>> 24) & 255];
        }
        return result;
    }

    public GF2Polynomial xor(GF2Polynomial b) {
        GF2Polynomial result;
        int k = Math.min(this.blocks, b.blocks);
        if (this.len >= b.len) {
            result = new GF2Polynomial(this);
            for (int i = 0; i < k; i++) {
                int[] iArr = result.value;
                int i2 = i;
                iArr[i2] = iArr[i2] ^ b.value[i];
            }
        } else {
            result = new GF2Polynomial(b);
            for (int i3 = 0; i3 < k; i3++) {
                int[] iArr2 = result.value;
                int i4 = i3;
                iArr2[i4] = iArr2[i4] ^ this.value[i3];
            }
        }
        result.zeroUnusedBits();
        return result;
    }

    public void xorThisBy(GF2Polynomial b) {
        for (int i = 0; i < Math.min(this.blocks, b.blocks); i++) {
            int[] iArr = this.value;
            int i2 = i;
            iArr[i2] = iArr[i2] ^ b.value[i];
        }
        zeroUnusedBits();
    }

    private void zeroUnusedBits() {
        if ((this.len & 31) != 0) {
            int[] iArr = this.value;
            int i = this.blocks - 1;
            iArr[i] = iArr[i] & reverseRightMask[this.len & 31];
        }
    }

    public void setBit(int i) throws RuntimeException {
        if (i < 0 || i > this.len - 1) {
            throw new RuntimeException();
        }
        int[] iArr = this.value;
        int i2 = i >>> 5;
        iArr[i2] = iArr[i2] | bitMask[i & 31];
    }

    public int getBit(int i) {
        if (i < 0) {
            throw new RuntimeException();
        }
        return (i <= this.len - 1 && (this.value[i >>> 5] & bitMask[i & 31]) != 0) ? 1 : 0;
    }

    public void resetBit(int i) throws RuntimeException {
        if (i < 0) {
            throw new RuntimeException();
        }
        if (i > this.len - 1) {
            return;
        }
        int[] iArr = this.value;
        int i2 = i >>> 5;
        iArr[i2] = iArr[i2] & (bitMask[i & 31] ^ (-1));
    }

    public void xorBit(int i) throws RuntimeException {
        if (i < 0 || i > this.len - 1) {
            throw new RuntimeException();
        }
        int[] iArr = this.value;
        int i2 = i >>> 5;
        iArr[i2] = iArr[i2] ^ bitMask[i & 31];
    }

    public boolean testBit(int i) {
        if (i < 0) {
            throw new RuntimeException();
        }
        return i <= this.len - 1 && (this.value[i >>> 5] & bitMask[i & 31]) != 0;
    }

    public GF2Polynomial shiftLeft() {
        GF2Polynomial result = new GF2Polynomial(this.len + 1, this.value);
        for (int i = result.blocks - 1; i >= 1; i--) {
            int[] iArr = result.value;
            int i2 = i;
            iArr[i2] = iArr[i2] << 1;
            int[] iArr2 = result.value;
            int i3 = i;
            iArr2[i3] = iArr2[i3] | (result.value[i - 1] >>> 31);
        }
        int[] iArr3 = result.value;
        iArr3[0] = iArr3[0] << 1;
        return result;
    }

    public void shiftLeftThis() {
        if ((this.len & 31) == 0) {
            this.len++;
            this.blocks++;
            if (this.blocks > this.value.length) {
                int[] bs = new int[this.blocks];
                System.arraycopy(this.value, 0, bs, 0, this.value.length);
                this.value = null;
                this.value = bs;
            }
            for (int i = this.blocks - 1; i >= 1; i--) {
                int[] iArr = this.value;
                int i2 = i;
                iArr[i2] = iArr[i2] | (this.value[i - 1] >>> 31);
                int[] iArr2 = this.value;
                int i3 = i - 1;
                iArr2[i3] = iArr2[i3] << 1;
            }
            return;
        }
        this.len++;
        for (int i4 = this.blocks - 1; i4 >= 1; i4--) {
            int[] iArr3 = this.value;
            int i5 = i4;
            iArr3[i5] = iArr3[i5] << 1;
            int[] iArr4 = this.value;
            int i6 = i4;
            iArr4[i6] = iArr4[i6] | (this.value[i4 - 1] >>> 31);
        }
        int[] iArr5 = this.value;
        iArr5[0] = iArr5[0] << 1;
    }

    public GF2Polynomial shiftLeft(int k) {
        GF2Polynomial result = new GF2Polynomial(this.len + k, this.value);
        if (k >= 32) {
            result.doShiftBlocksLeft(k >>> 5);
        }
        int remaining = k & 31;
        if (remaining != 0) {
            for (int i = result.blocks - 1; i >= 1; i--) {
                int[] iArr = result.value;
                int i2 = i;
                iArr[i2] = iArr[i2] << remaining;
                int[] iArr2 = result.value;
                int i3 = i;
                iArr2[i3] = iArr2[i3] | (result.value[i - 1] >>> (32 - remaining));
            }
            int[] iArr3 = result.value;
            iArr3[0] = iArr3[0] << remaining;
        }
        return result;
    }

    public void shiftLeftAddThis(GF2Polynomial b, int k) {
        if (k == 0) {
            addToThis(b);
            return;
        }
        expandN(b.len + k);
        int d = k >>> 5;
        for (int i = b.blocks - 1; i >= 0; i--) {
            if (i + d + 1 < this.blocks && (k & 31) != 0) {
                int[] iArr = this.value;
                int i2 = i + d + 1;
                iArr[i2] = iArr[i2] ^ (b.value[i] >>> (32 - (k & 31)));
            }
            int[] iArr2 = this.value;
            int i3 = i + d;
            iArr2[i3] = iArr2[i3] ^ (b.value[i] << (k & 31));
        }
    }

    void shiftBlocksLeft() {
        this.blocks++;
        this.len += 32;
        if (this.blocks <= this.value.length) {
            for (int i = this.blocks - 1; i >= 1; i--) {
                this.value[i] = this.value[i - 1];
            }
            this.value[0] = 0;
            return;
        }
        int[] result = new int[this.blocks];
        System.arraycopy(this.value, 0, result, 1, this.blocks - 1);
        this.value = null;
        this.value = result;
    }

    private void doShiftBlocksLeft(int b) {
        if (this.blocks <= this.value.length) {
            for (int i = this.blocks - 1; i >= b; i--) {
                this.value[i] = this.value[i - b];
            }
            for (int i2 = 0; i2 < b; i2++) {
                this.value[i2] = 0;
            }
            return;
        }
        int[] result = new int[this.blocks];
        System.arraycopy(this.value, 0, result, b, this.blocks - b);
        this.value = null;
        this.value = result;
    }

    public GF2Polynomial shiftRight() {
        GF2Polynomial result = new GF2Polynomial(this.len - 1);
        System.arraycopy(this.value, 0, result.value, 0, result.blocks);
        for (int i = 0; i <= result.blocks - 2; i++) {
            int[] iArr = result.value;
            int i2 = i;
            iArr[i2] = iArr[i2] >>> 1;
            int[] iArr2 = result.value;
            int i3 = i;
            iArr2[i3] = iArr2[i3] | (result.value[i + 1] << 31);
        }
        int[] iArr3 = result.value;
        int i4 = result.blocks - 1;
        iArr3[i4] = iArr3[i4] >>> 1;
        if (result.blocks < this.blocks) {
            int[] iArr4 = result.value;
            int i5 = result.blocks - 1;
            iArr4[i5] = iArr4[i5] | (this.value[result.blocks] << 31);
        }
        return result;
    }

    public void shiftRightThis() {
        this.len--;
        this.blocks = ((this.len - 1) >>> 5) + 1;
        for (int i = 0; i <= this.blocks - 2; i++) {
            int[] iArr = this.value;
            int i2 = i;
            iArr[i2] = iArr[i2] >>> 1;
            int[] iArr2 = this.value;
            int i3 = i;
            iArr2[i3] = iArr2[i3] | (this.value[i + 1] << 31);
        }
        int[] iArr3 = this.value;
        int i4 = this.blocks - 1;
        iArr3[i4] = iArr3[i4] >>> 1;
        if ((this.len & 31) == 0) {
            int[] iArr4 = this.value;
            int i5 = this.blocks - 1;
            iArr4[i5] = iArr4[i5] | (this.value[this.blocks] << 31);
        }
    }
}
