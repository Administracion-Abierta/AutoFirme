package org.bouncycastle.math.ec.rfc8032;

import com.sun.jna.platform.win32.WinNT;
import java.security.SecureRandom;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.math.ec.rfc7748.X25519;
import org.bouncycastle.math.ec.rfc7748.X25519Field;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat256;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc8032/Ed25519.class */
public abstract class Ed25519 {
    private static final int COORD_INTS = 8;
    private static final int POINT_BYTES = 32;
    private static final int SCALAR_INTS = 8;
    private static final int SCALAR_BYTES = 32;
    public static final int PREHASH_SIZE = 64;
    public static final int PUBLIC_KEY_SIZE = 32;
    public static final int SECRET_KEY_SIZE = 32;
    public static final int SIGNATURE_SIZE = 64;
    private static final int WNAF_WIDTH_128 = 4;
    private static final int WNAF_WIDTH_BASE = 6;
    private static final int PRECOMP_BLOCKS = 8;
    private static final int PRECOMP_TEETH = 4;
    private static final int PRECOMP_SPACING = 8;
    private static final int PRECOMP_RANGE = 256;
    private static final int PRECOMP_POINTS = 8;
    private static final int PRECOMP_MASK = 7;
    private static final byte[] DOM2_PREFIX = {83, 105, 103, 69, 100, 50, 53, 53, 49, 57, 32, 110, 111, 32, 69, 100, 50, 53, 53, 49, 57, 32, 99, 111, 108, 108, 105, 115, 105, 111, 110, 115};
    private static final int[] P = {-19, -1, -1, -1, -1, -1, -1, WinNT.MAXLONG};
    private static final int[] ORDER8_y1 = {1886001095, 1339575613, 1980447930, 258412557, -95215574, -959694548, 2013120334, 2047061138};
    private static final int[] ORDER8_y2 = {-1886001114, -1339575614, -1980447931, -258412558, 95215573, 959694547, -2013120335, 100422509};
    private static final int[] B_x = {52811034, 25909283, 8072341, 50637101, 13785486, 30858332, 20483199, 20966410, 43936626, 4379245};
    private static final int[] B_y = {40265304, 26843545, 6710886, 53687091, 13421772, 40265318, 26843545, 6710886, 53687091, 13421772};
    private static final int[] B128_x = {12052516, 1174424, 4087752, 38672185, 20040971, 21899680, 55468344, 20105554, 66708015, 9981791};
    private static final int[] B128_y = {66430571, 45040722, 4842939, 15895846, 18981244, 46308410, 4697481, 8903007, 53646190, 12474675};
    private static final int[] C_d = {56195235, 47411844, 25868126, 40503822, 57364, 58321048, 30416477, 31930572, 57760639, 10749657};
    private static final int[] C_d2 = {45281625, 27714825, 18181821, 13898781, 114729, 49533232, 60832955, 30306712, 48412415, 4722099};
    private static final int[] C_d4 = {23454386, 55429651, 2809210, 27797563, 229458, 31957600, 54557047, 27058993, 29715967, 9444199};
    private static final Object PRECOMP_LOCK = new Object();
    private static PointPrecomp[] PRECOMP_BASE_WNAF = null;
    private static PointPrecomp[] PRECOMP_BASE128_WNAF = null;
    private static int[] PRECOMP_BASE_COMB = null;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc8032/Ed25519$Algorithm.class */
    public static final class Algorithm {
        public static final int Ed25519 = 0;
        public static final int Ed25519ctx = 1;
        public static final int Ed25519ph = 2;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc8032/Ed25519$F.class */
    private static class F extends X25519Field {
        private F() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc8032/Ed25519$PointAccum.class */
    private static class PointAccum {
        int[] x;
        int[] y;
        int[] z;
        int[] u;
        int[] v;

        private PointAccum() {
            this.x = F.create();
            this.y = F.create();
            this.z = F.create();
            this.u = F.create();
            this.v = F.create();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc8032/Ed25519$PointAffine.class */
    private static class PointAffine {
        int[] x;
        int[] y;

        private PointAffine() {
            this.x = F.create();
            this.y = F.create();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc8032/Ed25519$PointExtended.class */
    private static class PointExtended {
        int[] x;
        int[] y;
        int[] z;
        int[] t;

        private PointExtended() {
            this.x = F.create();
            this.y = F.create();
            this.z = F.create();
            this.t = F.create();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc8032/Ed25519$PointPrecomp.class */
    private static class PointPrecomp {
        int[] ymx_h;
        int[] ypx_h;
        int[] xyd;

        private PointPrecomp() {
            this.ymx_h = F.create();
            this.ypx_h = F.create();
            this.xyd = F.create();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc8032/Ed25519$PointPrecompZ.class */
    private static class PointPrecompZ {
        int[] ymx_h;
        int[] ypx_h;
        int[] xyd;
        int[] z;

        private PointPrecompZ() {
            this.ymx_h = F.create();
            this.ypx_h = F.create();
            this.xyd = F.create();
            this.z = F.create();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc8032/Ed25519$PointTemp.class */
    private static class PointTemp {
        int[] r0;
        int[] r1;

        private PointTemp() {
            this.r0 = F.create();
            this.r1 = F.create();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/rfc8032/Ed25519$PublicPoint.class */
    public static final class PublicPoint {
        final int[] data;

        PublicPoint(int[] iArr) {
            this.data = iArr;
        }
    }

    private static byte[] calculateS(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        int[] iArr = new int[16];
        Scalar25519.decode(bArr, iArr);
        int[] iArr2 = new int[8];
        Scalar25519.decode(bArr2, iArr2);
        int[] iArr3 = new int[8];
        Scalar25519.decode(bArr3, iArr3);
        Nat256.mulAddTo(iArr2, iArr3, iArr);
        byte[] bArr4 = new byte[64];
        Codec.encode32(iArr, 0, iArr.length, bArr4, 0);
        return Scalar25519.reduce512(bArr4);
    }

    private static boolean checkContextVar(byte[] bArr, byte b) {
        return (bArr == null && b == 0) || (bArr != null && bArr.length < 256);
    }

    private static int checkPoint(PointAffine pointAffine) {
        int[] iArrCreate = F.create();
        int[] iArrCreate2 = F.create();
        int[] iArrCreate3 = F.create();
        F.sqr(pointAffine.x, iArrCreate2);
        F.sqr(pointAffine.y, iArrCreate3);
        F.mul(iArrCreate2, iArrCreate3, iArrCreate);
        F.sub(iArrCreate2, iArrCreate3, iArrCreate2);
        F.mul(iArrCreate, C_d, iArrCreate);
        F.addOne(iArrCreate);
        F.add(iArrCreate, iArrCreate2, iArrCreate);
        F.normalize(iArrCreate);
        F.normalize(iArrCreate3);
        return F.isZero(iArrCreate) & (F.isZero(iArrCreate3) ^ (-1));
    }

    private static int checkPoint(PointAccum pointAccum) {
        int[] iArrCreate = F.create();
        int[] iArrCreate2 = F.create();
        int[] iArrCreate3 = F.create();
        int[] iArrCreate4 = F.create();
        F.sqr(pointAccum.x, iArrCreate2);
        F.sqr(pointAccum.y, iArrCreate3);
        F.sqr(pointAccum.z, iArrCreate4);
        F.mul(iArrCreate2, iArrCreate3, iArrCreate);
        F.sub(iArrCreate2, iArrCreate3, iArrCreate2);
        F.mul(iArrCreate2, iArrCreate4, iArrCreate2);
        F.sqr(iArrCreate4, iArrCreate4);
        F.mul(iArrCreate, C_d, iArrCreate);
        F.add(iArrCreate, iArrCreate4, iArrCreate);
        F.add(iArrCreate, iArrCreate2, iArrCreate);
        F.normalize(iArrCreate);
        F.normalize(iArrCreate3);
        F.normalize(iArrCreate4);
        return F.isZero(iArrCreate) & (F.isZero(iArrCreate3) ^ (-1)) & (F.isZero(iArrCreate4) ^ (-1));
    }

    private static boolean checkPointFullVar(byte[] bArr) {
        int iDecode32 = Codec.decode32(bArr, 28) & WinNT.MAXLONG;
        int i = iDecode32;
        int i2 = iDecode32 ^ P[7];
        int i3 = iDecode32 ^ ORDER8_y1[7];
        int i4 = iDecode32 ^ ORDER8_y2[7];
        for (int i5 = 6; i5 > 0; i5--) {
            int iDecode33 = Codec.decode32(bArr, i5 * 4);
            i |= iDecode33;
            i2 |= iDecode33 ^ P[i5];
            i3 |= iDecode33 ^ ORDER8_y1[i5];
            i4 |= iDecode33 ^ ORDER8_y2[i5];
        }
        int iDecode34 = Codec.decode32(bArr, 0);
        if (i == 0 && iDecode34 - Integer.MIN_VALUE <= -2147483647) {
            return false;
        }
        if (i2 == 0 && iDecode34 - Integer.MIN_VALUE >= (P[0] - 1) - Integer.MIN_VALUE) {
            return false;
        }
        return ((i3 | (iDecode34 ^ ORDER8_y1[0])) != 0) & ((i4 | (iDecode34 ^ ORDER8_y2[0])) != 0);
    }

    private static boolean checkPointOrderVar(PointAffine pointAffine) {
        PointAccum pointAccum = new PointAccum();
        scalarMultOrderVar(pointAffine, pointAccum);
        return normalizeToNeutralElementVar(pointAccum);
    }

    private static boolean checkPointVar(byte[] bArr) {
        if ((Codec.decode32(bArr, 28) & WinNT.MAXLONG) < P[7]) {
            return true;
        }
        int[] iArr = new int[8];
        Codec.decode32(bArr, 0, iArr, 0, 8);
        iArr[7] = iArr[7] & WinNT.MAXLONG;
        return !Nat256.gte(iArr, P);
    }

    private static byte[] copy(byte[] bArr, int i, int i2) {
        byte[] bArr2 = new byte[i2];
        System.arraycopy(bArr, i, bArr2, 0, i2);
        return bArr2;
    }

    private static Digest createDigest() {
        SHA512Digest sHA512Digest = new SHA512Digest();
        if (sHA512Digest.getDigestSize() != 64) {
            throw new IllegalStateException();
        }
        return sHA512Digest;
    }

    public static Digest createPrehash() {
        return createDigest();
    }

    private static boolean decodePointVar(byte[] bArr, boolean z, PointAffine pointAffine) {
        int i = (bArr[31] & 128) >>> 7;
        F.decode(bArr, pointAffine.y);
        int[] iArrCreate = F.create();
        int[] iArrCreate2 = F.create();
        F.sqr(pointAffine.y, iArrCreate);
        F.mul(C_d, iArrCreate, iArrCreate2);
        F.subOne(iArrCreate);
        F.addOne(iArrCreate2);
        if (!F.sqrtRatioVar(iArrCreate, iArrCreate2, pointAffine.x)) {
            return false;
        }
        F.normalize(pointAffine.x);
        if (i == 1 && F.isZeroVar(pointAffine.x)) {
            return false;
        }
        if (!(z ^ (i != (pointAffine.x[0] & 1)))) {
            return true;
        }
        F.negate(pointAffine.x, pointAffine.x);
        F.normalize(pointAffine.x);
        return true;
    }

    private static void dom2(Digest digest, byte b, byte[] bArr) {
        int length = DOM2_PREFIX.length;
        byte[] bArr2 = new byte[length + 2 + bArr.length];
        System.arraycopy(DOM2_PREFIX, 0, bArr2, 0, length);
        bArr2[length] = b;
        bArr2[length + 1] = (byte) bArr.length;
        System.arraycopy(bArr, 0, bArr2, length + 2, bArr.length);
        digest.update(bArr2, 0, bArr2.length);
    }

    private static void encodePoint(PointAffine pointAffine, byte[] bArr, int i) {
        F.encode(pointAffine.y, bArr, i);
        int i2 = (i + 32) - 1;
        bArr[i2] = (byte) (bArr[i2] | ((pointAffine.x[0] & 1) << 7));
    }

    public static void encodePublicPoint(PublicPoint publicPoint, byte[] bArr, int i) {
        F.encode(publicPoint.data, 10, bArr, i);
        int i2 = (i + 32) - 1;
        bArr[i2] = (byte) (bArr[i2] | ((publicPoint.data[0] & 1) << 7));
    }

    private static int encodeResult(PointAccum pointAccum, byte[] bArr, int i) {
        PointAffine pointAffine = new PointAffine();
        normalizeToAffine(pointAccum, pointAffine);
        int iCheckPoint = checkPoint(pointAffine);
        encodePoint(pointAffine, bArr, i);
        return iCheckPoint;
    }

    private static PublicPoint exportPoint(PointAffine pointAffine) {
        int[] iArr = new int[20];
        F.copy(pointAffine.x, 0, iArr, 0);
        F.copy(pointAffine.y, 0, iArr, 10);
        return new PublicPoint(iArr);
    }

    public static void generatePrivateKey(SecureRandom secureRandom, byte[] bArr) {
        if (bArr.length != 32) {
            throw new IllegalArgumentException(OperatorName.NON_STROKING_CMYK);
        }
        secureRandom.nextBytes(bArr);
    }

    public static void generatePublicKey(byte[] bArr, int i, byte[] bArr2, int i2) {
        Digest digestCreateDigest = createDigest();
        byte[] bArr3 = new byte[64];
        digestCreateDigest.update(bArr, i, 32);
        digestCreateDigest.doFinal(bArr3, 0);
        byte[] bArr4 = new byte[32];
        pruneScalar(bArr3, 0, bArr4);
        scalarMultBaseEncoded(bArr4, bArr2, i2);
    }

    public static PublicPoint generatePublicKey(byte[] bArr, int i) {
        Digest digestCreateDigest = createDigest();
        byte[] bArr2 = new byte[64];
        digestCreateDigest.update(bArr, i, 32);
        digestCreateDigest.doFinal(bArr2, 0);
        byte[] bArr3 = new byte[32];
        pruneScalar(bArr2, 0, bArr3);
        PointAccum pointAccum = new PointAccum();
        scalarMultBase(bArr3, pointAccum);
        PointAffine pointAffine = new PointAffine();
        normalizeToAffine(pointAccum, pointAffine);
        if (0 == checkPoint(pointAffine)) {
            throw new IllegalStateException();
        }
        return exportPoint(pointAffine);
    }

    private static int getWindow4(int[] iArr, int i) {
        return (iArr[i >>> 3] >>> ((i & 7) << 2)) & 15;
    }

    private static void groupCombBits(int[] iArr) {
        for (int i = 0; i < iArr.length; i++) {
            iArr[i] = Interleave.shuffle2(iArr[i]);
        }
    }

    private static void implSign(Digest digest, byte[] bArr, byte[] bArr2, byte[] bArr3, int i, byte[] bArr4, byte b, byte[] bArr5, int i2, int i3, byte[] bArr6, int i4) {
        if (bArr4 != null) {
            dom2(digest, b, bArr4);
        }
        digest.update(bArr, 32, 32);
        digest.update(bArr5, i2, i3);
        digest.doFinal(bArr, 0);
        byte[] bArrReduce512 = Scalar25519.reduce512(bArr);
        byte[] bArr7 = new byte[32];
        scalarMultBaseEncoded(bArrReduce512, bArr7, 0);
        if (bArr4 != null) {
            dom2(digest, b, bArr4);
        }
        digest.update(bArr7, 0, 32);
        digest.update(bArr3, i, 32);
        digest.update(bArr5, i2, i3);
        digest.doFinal(bArr, 0);
        byte[] bArrCalculateS = calculateS(bArrReduce512, Scalar25519.reduce512(bArr), bArr2);
        System.arraycopy(bArr7, 0, bArr6, i4, 32);
        System.arraycopy(bArrCalculateS, 0, bArr6, i4 + 32, 32);
    }

    private static void implSign(byte[] bArr, int i, byte[] bArr2, byte b, byte[] bArr3, int i2, int i3, byte[] bArr4, int i4) {
        if (!checkContextVar(bArr2, b)) {
            throw new IllegalArgumentException("ctx");
        }
        Digest digestCreateDigest = createDigest();
        byte[] bArr5 = new byte[64];
        digestCreateDigest.update(bArr, i, 32);
        digestCreateDigest.doFinal(bArr5, 0);
        byte[] bArr6 = new byte[32];
        pruneScalar(bArr5, 0, bArr6);
        byte[] bArr7 = new byte[32];
        scalarMultBaseEncoded(bArr6, bArr7, 0);
        implSign(digestCreateDigest, bArr5, bArr6, bArr7, 0, bArr2, b, bArr3, i2, i3, bArr4, i4);
    }

    private static void implSign(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte b, byte[] bArr4, int i3, int i4, byte[] bArr5, int i5) {
        if (!checkContextVar(bArr3, b)) {
            throw new IllegalArgumentException("ctx");
        }
        Digest digestCreateDigest = createDigest();
        byte[] bArr6 = new byte[64];
        digestCreateDigest.update(bArr, i, 32);
        digestCreateDigest.doFinal(bArr6, 0);
        byte[] bArr7 = new byte[32];
        pruneScalar(bArr6, 0, bArr7);
        implSign(digestCreateDigest, bArr6, bArr7, bArr2, i2, bArr3, b, bArr4, i3, i4, bArr5, i5);
    }

    private static boolean implVerify(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte b, byte[] bArr4, int i3, int i4) {
        if (!checkContextVar(bArr3, b)) {
            throw new IllegalArgumentException("ctx");
        }
        byte[] bArrCopy = copy(bArr, i, 32);
        byte[] bArrCopy2 = copy(bArr, i + 32, 32);
        byte[] bArrCopy3 = copy(bArr2, i2, 32);
        if (!checkPointVar(bArrCopy)) {
            return false;
        }
        int[] iArr = new int[8];
        if (!Scalar25519.checkVar(bArrCopy2, iArr) || !checkPointFullVar(bArrCopy3)) {
            return false;
        }
        PointAffine pointAffine = new PointAffine();
        if (!decodePointVar(bArrCopy, true, pointAffine)) {
            return false;
        }
        PointAffine pointAffine2 = new PointAffine();
        if (!decodePointVar(bArrCopy3, true, pointAffine2)) {
            return false;
        }
        Digest digestCreateDigest = createDigest();
        byte[] bArr5 = new byte[64];
        if (bArr3 != null) {
            dom2(digestCreateDigest, b, bArr3);
        }
        digestCreateDigest.update(bArrCopy, 0, 32);
        digestCreateDigest.update(bArrCopy3, 0, 32);
        digestCreateDigest.update(bArr4, i3, i4);
        digestCreateDigest.doFinal(bArr5, 0);
        byte[] bArrReduce512 = Scalar25519.reduce512(bArr5);
        int[] iArr2 = new int[8];
        Scalar25519.decode(bArrReduce512, iArr2);
        int[] iArr3 = new int[4];
        int[] iArr4 = new int[4];
        if (!Scalar25519.reduceBasisVar(iArr2, iArr3, iArr4)) {
            throw new IllegalStateException();
        }
        Scalar25519.multiply128Var(iArr, iArr4, iArr);
        PointAccum pointAccum = new PointAccum();
        scalarMultStraus128Var(iArr, iArr3, pointAffine2, iArr4, pointAffine, pointAccum);
        return normalizeToNeutralElementVar(pointAccum);
    }

    private static boolean implVerify(byte[] bArr, int i, PublicPoint publicPoint, byte[] bArr2, byte b, byte[] bArr3, int i2, int i3) {
        if (!checkContextVar(bArr2, b)) {
            throw new IllegalArgumentException("ctx");
        }
        byte[] bArrCopy = copy(bArr, i, 32);
        byte[] bArrCopy2 = copy(bArr, i + 32, 32);
        if (!checkPointVar(bArrCopy)) {
            return false;
        }
        int[] iArr = new int[8];
        if (!Scalar25519.checkVar(bArrCopy2, iArr)) {
            return false;
        }
        PointAffine pointAffine = new PointAffine();
        if (!decodePointVar(bArrCopy, true, pointAffine)) {
            return false;
        }
        PointAffine pointAffine2 = new PointAffine();
        F.negate(publicPoint.data, pointAffine2.x);
        F.copy(publicPoint.data, 10, pointAffine2.y, 0);
        byte[] bArr4 = new byte[32];
        encodePublicPoint(publicPoint, bArr4, 0);
        Digest digestCreateDigest = createDigest();
        byte[] bArr5 = new byte[64];
        if (bArr2 != null) {
            dom2(digestCreateDigest, b, bArr2);
        }
        digestCreateDigest.update(bArrCopy, 0, 32);
        digestCreateDigest.update(bArr4, 0, 32);
        digestCreateDigest.update(bArr3, i2, i3);
        digestCreateDigest.doFinal(bArr5, 0);
        byte[] bArrReduce512 = Scalar25519.reduce512(bArr5);
        int[] iArr2 = new int[8];
        Scalar25519.decode(bArrReduce512, iArr2);
        int[] iArr3 = new int[4];
        int[] iArr4 = new int[4];
        if (!Scalar25519.reduceBasisVar(iArr2, iArr3, iArr4)) {
            throw new IllegalStateException();
        }
        Scalar25519.multiply128Var(iArr, iArr4, iArr);
        PointAccum pointAccum = new PointAccum();
        scalarMultStraus128Var(iArr, iArr3, pointAffine2, iArr4, pointAffine, pointAccum);
        return normalizeToNeutralElementVar(pointAccum);
    }

    private static void invertDoubleZs(PointExtended[] pointExtendedArr) {
        int length = pointExtendedArr.length;
        int[] iArrCreateTable = F.createTable(length);
        int[] iArrCreate = F.create();
        F.copy(pointExtendedArr[0].z, 0, iArrCreate, 0);
        F.copy(iArrCreate, 0, iArrCreateTable, 0);
        int i = 0;
        while (true) {
            i++;
            if (i >= length) {
                break;
            }
            F.mul(iArrCreate, pointExtendedArr[i].z, iArrCreate);
            F.copy(iArrCreate, 0, iArrCreateTable, i * 10);
        }
        F.add(iArrCreate, iArrCreate, iArrCreate);
        F.invVar(iArrCreate, iArrCreate);
        int i2 = i - 1;
        int[] iArrCreate2 = F.create();
        while (i2 > 0) {
            int i3 = i2;
            i2--;
            F.copy(iArrCreateTable, i2 * 10, iArrCreate2, 0);
            F.mul(iArrCreate2, iArrCreate, iArrCreate2);
            F.mul(iArrCreate, pointExtendedArr[i3].z, iArrCreate);
            F.copy(iArrCreate2, 0, pointExtendedArr[i3].z, 0);
        }
        F.copy(iArrCreate, 0, pointExtendedArr[0].z, 0);
    }

    private static void normalizeToAffine(PointAccum pointAccum, PointAffine pointAffine) {
        F.inv(pointAccum.z, pointAffine.y);
        F.mul(pointAffine.y, pointAccum.x, pointAffine.x);
        F.mul(pointAffine.y, pointAccum.y, pointAffine.y);
        F.normalize(pointAffine.x);
        F.normalize(pointAffine.y);
    }

    private static boolean normalizeToNeutralElementVar(PointAccum pointAccum) {
        F.normalize(pointAccum.x);
        F.normalize(pointAccum.y);
        F.normalize(pointAccum.z);
        return F.isZeroVar(pointAccum.x) && !F.isZeroVar(pointAccum.y) && F.areEqualVar(pointAccum.y, pointAccum.z);
    }

    private static void pointAdd(PointExtended pointExtended, PointExtended pointExtended2, PointExtended pointExtended3, PointTemp pointTemp) {
        int[] iArr = pointExtended3.x;
        int[] iArr2 = pointExtended3.y;
        int[] iArr3 = pointTemp.r0;
        int[] iArr4 = pointTemp.r1;
        F.apm(pointExtended.y, pointExtended.x, iArr2, iArr);
        F.apm(pointExtended2.y, pointExtended2.x, iArr4, iArr3);
        F.mul(iArr, iArr3, iArr);
        F.mul(iArr2, iArr4, iArr2);
        F.mul(pointExtended.t, pointExtended2.t, iArr3);
        F.mul(iArr3, C_d2, iArr3);
        F.add(pointExtended.z, pointExtended.z, iArr4);
        F.mul(iArr4, pointExtended2.z, iArr4);
        F.apm(iArr2, iArr, iArr2, iArr);
        F.apm(iArr4, iArr3, iArr4, iArr3);
        F.mul(iArr, iArr2, pointExtended3.t);
        F.mul(iArr3, iArr4, pointExtended3.z);
        F.mul(iArr, iArr3, pointExtended3.x);
        F.mul(iArr2, iArr4, pointExtended3.y);
    }

    private static void pointAdd(PointPrecomp pointPrecomp, PointAccum pointAccum, PointTemp pointTemp) {
        int[] iArr = pointAccum.x;
        int[] iArr2 = pointAccum.y;
        int[] iArr3 = pointTemp.r0;
        int[] iArr4 = pointAccum.u;
        int[] iArr5 = pointAccum.v;
        F.apm(pointAccum.y, pointAccum.x, iArr2, iArr);
        F.mul(iArr, pointPrecomp.ymx_h, iArr);
        F.mul(iArr2, pointPrecomp.ypx_h, iArr2);
        F.mul(pointAccum.u, pointAccum.v, iArr3);
        F.mul(iArr3, pointPrecomp.xyd, iArr3);
        F.apm(iArr2, iArr, iArr5, iArr4);
        F.apm(pointAccum.z, iArr3, iArr2, iArr);
        F.mul(iArr, iArr2, pointAccum.z);
        F.mul(iArr, iArr4, pointAccum.x);
        F.mul(iArr2, iArr5, pointAccum.y);
    }

    private static void pointAdd(PointPrecompZ pointPrecompZ, PointAccum pointAccum, PointTemp pointTemp) {
        int[] iArr = pointAccum.x;
        int[] iArr2 = pointAccum.y;
        int[] iArr3 = pointTemp.r0;
        int[] iArr4 = pointAccum.z;
        int[] iArr5 = pointAccum.u;
        int[] iArr6 = pointAccum.v;
        F.apm(pointAccum.y, pointAccum.x, iArr2, iArr);
        F.mul(iArr, pointPrecompZ.ymx_h, iArr);
        F.mul(iArr2, pointPrecompZ.ypx_h, iArr2);
        F.mul(pointAccum.u, pointAccum.v, iArr3);
        F.mul(iArr3, pointPrecompZ.xyd, iArr3);
        F.mul(pointAccum.z, pointPrecompZ.z, iArr4);
        F.apm(iArr2, iArr, iArr6, iArr5);
        F.apm(iArr4, iArr3, iArr2, iArr);
        F.mul(iArr, iArr2, pointAccum.z);
        F.mul(iArr, iArr5, pointAccum.x);
        F.mul(iArr2, iArr6, pointAccum.y);
    }

    private static void pointAddVar(boolean z, PointPrecomp pointPrecomp, PointAccum pointAccum, PointTemp pointTemp) {
        int[] iArr;
        int[] iArr2;
        int[] iArr3 = pointAccum.x;
        int[] iArr4 = pointAccum.y;
        int[] iArr5 = pointTemp.r0;
        int[] iArr6 = pointAccum.u;
        int[] iArr7 = pointAccum.v;
        if (z) {
            iArr = iArr4;
            iArr2 = iArr3;
        } else {
            iArr = iArr3;
            iArr2 = iArr4;
        }
        F.apm(pointAccum.y, pointAccum.x, iArr4, iArr3);
        F.mul(iArr, pointPrecomp.ymx_h, iArr);
        F.mul(iArr2, pointPrecomp.ypx_h, iArr2);
        F.mul(pointAccum.u, pointAccum.v, iArr5);
        F.mul(iArr5, pointPrecomp.xyd, iArr5);
        F.apm(iArr4, iArr3, iArr7, iArr6);
        int[] iArr8 = pointAccum.z;
        F.apm(iArr8, iArr5, iArr2, iArr);
        F.mul(iArr3, iArr4, pointAccum.z);
        F.mul(iArr3, iArr6, pointAccum.x);
        F.mul(iArr4, iArr7, pointAccum.y);
    }

    private static void pointAddVar(boolean z, PointPrecompZ pointPrecompZ, PointAccum pointAccum, PointTemp pointTemp) {
        int[] iArr;
        int[] iArr2;
        int[] iArr3 = pointAccum.x;
        int[] iArr4 = pointAccum.y;
        int[] iArr5 = pointTemp.r0;
        int[] iArr6 = pointAccum.z;
        int[] iArr7 = pointAccum.u;
        int[] iArr8 = pointAccum.v;
        if (z) {
            iArr = iArr4;
            iArr2 = iArr3;
        } else {
            iArr = iArr3;
            iArr2 = iArr4;
        }
        F.apm(pointAccum.y, pointAccum.x, iArr4, iArr3);
        F.mul(iArr, pointPrecompZ.ymx_h, iArr);
        F.mul(iArr2, pointPrecompZ.ypx_h, iArr2);
        F.mul(pointAccum.u, pointAccum.v, iArr5);
        F.mul(iArr5, pointPrecompZ.xyd, iArr5);
        F.mul(pointAccum.z, pointPrecompZ.z, iArr6);
        F.apm(iArr4, iArr3, iArr8, iArr7);
        F.apm(iArr6, iArr5, iArr2, iArr);
        F.mul(iArr3, iArr4, pointAccum.z);
        F.mul(iArr3, iArr7, pointAccum.x);
        F.mul(iArr4, iArr8, pointAccum.y);
    }

    private static void pointCopy(PointAccum pointAccum, PointExtended pointExtended) {
        F.copy(pointAccum.x, 0, pointExtended.x, 0);
        F.copy(pointAccum.y, 0, pointExtended.y, 0);
        F.copy(pointAccum.z, 0, pointExtended.z, 0);
        F.mul(pointAccum.u, pointAccum.v, pointExtended.t);
    }

    private static void pointCopy(PointAffine pointAffine, PointExtended pointExtended) {
        F.copy(pointAffine.x, 0, pointExtended.x, 0);
        F.copy(pointAffine.y, 0, pointExtended.y, 0);
        F.one(pointExtended.z);
        F.mul(pointAffine.x, pointAffine.y, pointExtended.t);
    }

    private static void pointCopy(PointExtended pointExtended, PointPrecompZ pointPrecompZ) {
        F.apm(pointExtended.y, pointExtended.x, pointPrecompZ.ypx_h, pointPrecompZ.ymx_h);
        F.mul(pointExtended.t, C_d2, pointPrecompZ.xyd);
        F.add(pointExtended.z, pointExtended.z, pointPrecompZ.z);
    }

    private static void pointDouble(PointAccum pointAccum) {
        int[] iArr = pointAccum.x;
        int[] iArr2 = pointAccum.y;
        int[] iArr3 = pointAccum.z;
        int[] iArr4 = pointAccum.u;
        int[] iArr5 = pointAccum.v;
        F.add(pointAccum.x, pointAccum.y, iArr4);
        F.sqr(pointAccum.x, iArr);
        F.sqr(pointAccum.y, iArr2);
        F.sqr(pointAccum.z, iArr3);
        F.add(iArr3, iArr3, iArr3);
        F.apm(iArr, iArr2, iArr5, iArr2);
        F.sqr(iArr4, iArr4);
        F.sub(iArr5, iArr4, iArr4);
        F.add(iArr3, iArr2, iArr);
        F.carry(iArr);
        F.mul(iArr, iArr2, pointAccum.z);
        F.mul(iArr, iArr4, pointAccum.x);
        F.mul(iArr2, iArr5, pointAccum.y);
    }

    private static void pointLookup(int i, int i2, PointPrecomp pointPrecomp) {
        int i3 = i * 8 * 3 * 10;
        for (int i4 = 0; i4 < 8; i4++) {
            int i5 = ((i4 ^ i2) - 1) >> 31;
            F.cmov(i5, PRECOMP_BASE_COMB, i3, pointPrecomp.ymx_h, 0);
            int i6 = i3 + 10;
            F.cmov(i5, PRECOMP_BASE_COMB, i6, pointPrecomp.ypx_h, 0);
            int i7 = i6 + 10;
            F.cmov(i5, PRECOMP_BASE_COMB, i7, pointPrecomp.xyd, 0);
            i3 = i7 + 10;
        }
    }

    private static void pointLookupZ(int[] iArr, int i, int[] iArr2, PointPrecompZ pointPrecompZ) {
        int window4 = getWindow4(iArr, i);
        int i2 = (window4 >>> 3) ^ 1;
        int i3 = (window4 ^ (-i2)) & 7;
        int i4 = 0;
        for (int i5 = 0; i5 < 8; i5++) {
            int i6 = ((i5 ^ i3) - 1) >> 31;
            F.cmov(i6, iArr2, i4, pointPrecompZ.ymx_h, 0);
            int i7 = i4 + 10;
            F.cmov(i6, iArr2, i7, pointPrecompZ.ypx_h, 0);
            int i8 = i7 + 10;
            F.cmov(i6, iArr2, i8, pointPrecompZ.xyd, 0);
            int i9 = i8 + 10;
            F.cmov(i6, iArr2, i9, pointPrecompZ.z, 0);
            i4 = i9 + 10;
        }
        F.cswap(i2, pointPrecompZ.ymx_h, pointPrecompZ.ypx_h);
        F.cnegate(i2, pointPrecompZ.xyd);
    }

    private static void pointPrecompute(PointAffine pointAffine, PointExtended[] pointExtendedArr, int i, int i2, PointTemp pointTemp) {
        PointExtended pointExtended = new PointExtended();
        pointExtendedArr[i] = pointExtended;
        pointCopy(pointAffine, pointExtended);
        PointExtended pointExtended2 = new PointExtended();
        pointAdd(pointExtendedArr[i], pointExtendedArr[i], pointExtended2, pointTemp);
        for (int i3 = 1; i3 < i2; i3++) {
            PointExtended pointExtended3 = pointExtendedArr[(i + i3) - 1];
            PointExtended pointExtended4 = new PointExtended();
            pointExtendedArr[i + i3] = pointExtended4;
            pointAdd(pointExtended3, pointExtended2, pointExtended4, pointTemp);
        }
    }

    private static int[] pointPrecomputeZ(PointAffine pointAffine, int i, PointTemp pointTemp) {
        PointExtended pointExtended = new PointExtended();
        pointCopy(pointAffine, pointExtended);
        PointExtended pointExtended2 = new PointExtended();
        pointAdd(pointExtended, pointExtended, pointExtended2, pointTemp);
        PointPrecompZ pointPrecompZ = new PointPrecompZ();
        int[] iArrCreateTable = F.createTable(i * 4);
        int i2 = 0;
        int i3 = 0;
        while (true) {
            pointCopy(pointExtended, pointPrecompZ);
            F.copy(pointPrecompZ.ymx_h, 0, iArrCreateTable, i2);
            int i4 = i2 + 10;
            F.copy(pointPrecompZ.ypx_h, 0, iArrCreateTable, i4);
            int i5 = i4 + 10;
            F.copy(pointPrecompZ.xyd, 0, iArrCreateTable, i5);
            int i6 = i5 + 10;
            F.copy(pointPrecompZ.z, 0, iArrCreateTable, i6);
            i2 = i6 + 10;
            i3++;
            if (i3 == i) {
                return iArrCreateTable;
            }
            pointAdd(pointExtended, pointExtended2, pointExtended, pointTemp);
        }
    }

    private static void pointPrecomputeZ(PointAffine pointAffine, PointPrecompZ[] pointPrecompZArr, int i, PointTemp pointTemp) {
        PointExtended pointExtended = new PointExtended();
        pointCopy(pointAffine, pointExtended);
        PointExtended pointExtended2 = new PointExtended();
        pointAdd(pointExtended, pointExtended, pointExtended2, pointTemp);
        int i2 = 0;
        while (true) {
            PointPrecompZ pointPrecompZ = new PointPrecompZ();
            pointPrecompZArr[i2] = pointPrecompZ;
            pointCopy(pointExtended, pointPrecompZ);
            i2++;
            if (i2 == i) {
                return;
            } else {
                pointAdd(pointExtended, pointExtended2, pointExtended, pointTemp);
            }
        }
    }

    private static void pointSetNeutral(PointAccum pointAccum) {
        F.zero(pointAccum.x);
        F.one(pointAccum.y);
        F.one(pointAccum.z);
        F.zero(pointAccum.u);
        F.one(pointAccum.v);
    }

    public static void precompute() {
        synchronized (PRECOMP_LOCK) {
            if (PRECOMP_BASE_COMB != null) {
                return;
            }
            int i = (16 * 2) + 64;
            PointExtended[] pointExtendedArr = new PointExtended[i];
            PointTemp pointTemp = new PointTemp();
            PointAffine pointAffine = new PointAffine();
            F.copy(B_x, 0, pointAffine.x, 0);
            F.copy(B_y, 0, pointAffine.y, 0);
            pointPrecompute(pointAffine, pointExtendedArr, 0, 16, pointTemp);
            PointAffine pointAffine2 = new PointAffine();
            F.copy(B128_x, 0, pointAffine2.x, 0);
            F.copy(B128_y, 0, pointAffine2.y, 0);
            pointPrecompute(pointAffine2, pointExtendedArr, 16, 16, pointTemp);
            PointAccum pointAccum = new PointAccum();
            F.copy(B_x, 0, pointAccum.x, 0);
            F.copy(B_y, 0, pointAccum.y, 0);
            F.one(pointAccum.z);
            F.copy(pointAccum.x, 0, pointAccum.u, 0);
            F.copy(pointAccum.y, 0, pointAccum.v, 0);
            int i2 = 16 * 2;
            PointExtended[] pointExtendedArr2 = new PointExtended[4];
            for (int i3 = 0; i3 < 4; i3++) {
                pointExtendedArr2[i3] = new PointExtended();
            }
            PointExtended pointExtended = new PointExtended();
            for (int i4 = 0; i4 < 8; i4++) {
                int i5 = i2;
                i2++;
                PointExtended pointExtended2 = new PointExtended();
                pointExtendedArr[i5] = pointExtended2;
                for (int i6 = 0; i6 < 4; i6++) {
                    if (i6 == 0) {
                        pointCopy(pointAccum, pointExtended2);
                    } else {
                        pointCopy(pointAccum, pointExtended);
                        pointAdd(pointExtended2, pointExtended, pointExtended2, pointTemp);
                    }
                    pointDouble(pointAccum);
                    pointCopy(pointAccum, pointExtendedArr2[i6]);
                    if (i4 + i6 != 10) {
                        for (int i7 = 1; i7 < 8; i7++) {
                            pointDouble(pointAccum);
                        }
                    }
                }
                F.negate(pointExtended2.x, pointExtended2.x);
                F.negate(pointExtended2.t, pointExtended2.t);
                for (int i8 = 0; i8 < 3; i8++) {
                    int i9 = 1 << i8;
                    int i10 = 0;
                    while (i10 < i9) {
                        pointExtendedArr[i2] = new PointExtended();
                        pointAdd(pointExtendedArr[i2 - i9], pointExtendedArr2[i8], pointExtendedArr[i2], pointTemp);
                        i10++;
                        i2++;
                    }
                }
            }
            invertDoubleZs(pointExtendedArr);
            PRECOMP_BASE_WNAF = new PointPrecomp[16];
            for (int i11 = 0; i11 < 16; i11++) {
                PointExtended pointExtended3 = pointExtendedArr[i11];
                PointPrecomp pointPrecomp = new PointPrecomp();
                PRECOMP_BASE_WNAF[i11] = pointPrecomp;
                F.mul(pointExtended3.x, pointExtended3.z, pointExtended3.x);
                F.mul(pointExtended3.y, pointExtended3.z, pointExtended3.y);
                F.apm(pointExtended3.y, pointExtended3.x, pointPrecomp.ypx_h, pointPrecomp.ymx_h);
                F.mul(pointExtended3.x, pointExtended3.y, pointPrecomp.xyd);
                F.mul(pointPrecomp.xyd, C_d4, pointPrecomp.xyd);
                F.normalize(pointPrecomp.ymx_h);
                F.normalize(pointPrecomp.ypx_h);
                F.normalize(pointPrecomp.xyd);
            }
            PRECOMP_BASE128_WNAF = new PointPrecomp[16];
            for (int i12 = 0; i12 < 16; i12++) {
                PointExtended pointExtended4 = pointExtendedArr[16 + i12];
                PointPrecomp pointPrecomp2 = new PointPrecomp();
                PRECOMP_BASE128_WNAF[i12] = pointPrecomp2;
                F.mul(pointExtended4.x, pointExtended4.z, pointExtended4.x);
                F.mul(pointExtended4.y, pointExtended4.z, pointExtended4.y);
                F.apm(pointExtended4.y, pointExtended4.x, pointPrecomp2.ypx_h, pointPrecomp2.ymx_h);
                F.mul(pointExtended4.x, pointExtended4.y, pointPrecomp2.xyd);
                F.mul(pointPrecomp2.xyd, C_d4, pointPrecomp2.xyd);
                F.normalize(pointPrecomp2.ymx_h);
                F.normalize(pointPrecomp2.ypx_h);
                F.normalize(pointPrecomp2.xyd);
            }
            PRECOMP_BASE_COMB = F.createTable(64 * 3);
            PointPrecomp pointPrecomp3 = new PointPrecomp();
            int i13 = 0;
            for (int i14 = 16 * 2; i14 < i; i14++) {
                PointExtended pointExtended5 = pointExtendedArr[i14];
                F.mul(pointExtended5.x, pointExtended5.z, pointExtended5.x);
                F.mul(pointExtended5.y, pointExtended5.z, pointExtended5.y);
                F.apm(pointExtended5.y, pointExtended5.x, pointPrecomp3.ypx_h, pointPrecomp3.ymx_h);
                F.mul(pointExtended5.x, pointExtended5.y, pointPrecomp3.xyd);
                F.mul(pointPrecomp3.xyd, C_d4, pointPrecomp3.xyd);
                F.normalize(pointPrecomp3.ymx_h);
                F.normalize(pointPrecomp3.ypx_h);
                F.normalize(pointPrecomp3.xyd);
                F.copy(pointPrecomp3.ymx_h, 0, PRECOMP_BASE_COMB, i13);
                int i15 = i13 + 10;
                F.copy(pointPrecomp3.ypx_h, 0, PRECOMP_BASE_COMB, i15);
                int i16 = i15 + 10;
                F.copy(pointPrecomp3.xyd, 0, PRECOMP_BASE_COMB, i16);
                i13 = i16 + 10;
            }
        }
    }

    private static void pruneScalar(byte[] bArr, int i, byte[] bArr2) {
        System.arraycopy(bArr, i, bArr2, 0, 32);
        bArr2[0] = (byte) (bArr2[0] & 248);
        bArr2[31] = (byte) (bArr2[31] & 127);
        bArr2[31] = (byte) (bArr2[31] | 64);
    }

    private static void scalarMult(byte[] bArr, PointAffine pointAffine, PointAccum pointAccum) {
        int[] iArr = new int[8];
        Scalar25519.decode(bArr, iArr);
        Scalar25519.toSignedDigits(256, iArr);
        PointPrecompZ pointPrecompZ = new PointPrecompZ();
        PointTemp pointTemp = new PointTemp();
        int[] iArrPointPrecomputeZ = pointPrecomputeZ(pointAffine, 8, pointTemp);
        pointSetNeutral(pointAccum);
        int i = 63;
        while (true) {
            pointLookupZ(iArr, i, iArrPointPrecomputeZ, pointPrecompZ);
            pointAdd(pointPrecompZ, pointAccum, pointTemp);
            i--;
            if (i < 0) {
                return;
            }
            for (int i2 = 0; i2 < 4; i2++) {
                pointDouble(pointAccum);
            }
        }
    }

    private static void scalarMultBase(byte[] bArr, PointAccum pointAccum) {
        precompute();
        int[] iArr = new int[8];
        Scalar25519.decode(bArr, iArr);
        Scalar25519.toSignedDigits(256, iArr);
        groupCombBits(iArr);
        PointPrecomp pointPrecomp = new PointPrecomp();
        PointTemp pointTemp = new PointTemp();
        pointSetNeutral(pointAccum);
        int i = 0;
        int i2 = 28;
        while (true) {
            for (int i3 = 0; i3 < 8; i3++) {
                int i4 = iArr[i3] >>> i2;
                int i5 = (i4 >>> 3) & 1;
                pointLookup(i3, (i4 ^ (-i5)) & 7, pointPrecomp);
                F.cnegate(i ^ i5, pointAccum.x);
                F.cnegate(i ^ i5, pointAccum.u);
                i = i5;
                pointAdd(pointPrecomp, pointAccum, pointTemp);
            }
            i2 -= 4;
            if (i2 < 0) {
                F.cnegate(i, pointAccum.x);
                F.cnegate(i, pointAccum.u);
                return;
            }
            pointDouble(pointAccum);
        }
    }

    private static void scalarMultBaseEncoded(byte[] bArr, byte[] bArr2, int i) {
        PointAccum pointAccum = new PointAccum();
        scalarMultBase(bArr, pointAccum);
        if (0 == encodeResult(pointAccum, bArr2, i)) {
            throw new IllegalStateException();
        }
    }

    public static void scalarMultBaseYZ(X25519.Friend friend, byte[] bArr, int i, int[] iArr, int[] iArr2) {
        if (null == friend) {
            throw new NullPointerException("This method is only for use by X25519");
        }
        byte[] bArr2 = new byte[32];
        pruneScalar(bArr, i, bArr2);
        PointAccum pointAccum = new PointAccum();
        scalarMultBase(bArr2, pointAccum);
        if (0 == checkPoint(pointAccum)) {
            throw new IllegalStateException();
        }
        F.copy(pointAccum.y, 0, iArr, 0);
        F.copy(pointAccum.z, 0, iArr2, 0);
    }

    private static void scalarMultOrderVar(PointAffine pointAffine, PointAccum pointAccum) {
        byte[] bArr = new byte[253];
        Scalar25519.getOrderWnafVar(4, bArr);
        PointPrecompZ[] pointPrecompZArr = new PointPrecompZ[4];
        PointTemp pointTemp = new PointTemp();
        pointPrecomputeZ(pointAffine, pointPrecompZArr, 4, pointTemp);
        pointSetNeutral(pointAccum);
        int i = 252;
        while (true) {
            byte b = bArr[i];
            if (b != 0) {
                pointAddVar(b < 0, pointPrecompZArr[(b >> 1) ^ (b >> 31)], pointAccum, pointTemp);
            }
            i--;
            if (i < 0) {
                return;
            } else {
                pointDouble(pointAccum);
            }
        }
    }

    private static void scalarMultStraus128Var(int[] iArr, int[] iArr2, PointAffine pointAffine, int[] iArr3, PointAffine pointAffine2, PointAccum pointAccum) {
        precompute();
        byte[] bArr = new byte[256];
        byte[] bArr2 = new byte[128];
        byte[] bArr3 = new byte[128];
        Wnaf.getSignedVar(iArr, 6, bArr);
        Wnaf.getSignedVar(iArr2, 4, bArr2);
        Wnaf.getSignedVar(iArr3, 4, bArr3);
        PointPrecompZ[] pointPrecompZArr = new PointPrecompZ[4];
        PointPrecompZ[] pointPrecompZArr2 = new PointPrecompZ[4];
        PointTemp pointTemp = new PointTemp();
        pointPrecomputeZ(pointAffine, pointPrecompZArr, 4, pointTemp);
        pointPrecomputeZ(pointAffine2, pointPrecompZArr2, 4, pointTemp);
        pointSetNeutral(pointAccum);
        int i = 128;
        do {
            i--;
            if (i < 0) {
                break;
            }
        } while ((bArr[i] | bArr[128 + i] | bArr2[i] | bArr3[i]) == 0);
        while (i >= 0) {
            byte b = bArr[i];
            if (b != 0) {
                pointAddVar(b < 0, PRECOMP_BASE_WNAF[(b >> 1) ^ (b >> 31)], pointAccum, pointTemp);
            }
            byte b2 = bArr[128 + i];
            if (b2 != 0) {
                pointAddVar(b2 < 0, PRECOMP_BASE128_WNAF[(b2 >> 1) ^ (b2 >> 31)], pointAccum, pointTemp);
            }
            byte b3 = bArr2[i];
            if (b3 != 0) {
                pointAddVar(b3 < 0, pointPrecompZArr[(b3 >> 1) ^ (b3 >> 31)], pointAccum, pointTemp);
            }
            byte b4 = bArr3[i];
            if (b4 != 0) {
                pointAddVar(b4 < 0, pointPrecompZArr2[(b4 >> 1) ^ (b4 >> 31)], pointAccum, pointTemp);
            }
            pointDouble(pointAccum);
            i--;
        }
        pointDouble(pointAccum);
        pointDouble(pointAccum);
    }

    public static void sign(byte[] bArr, int i, byte[] bArr2, int i2, int i3, byte[] bArr3, int i4) {
        implSign(bArr, i, null, (byte) 0, bArr2, i2, i3, bArr3, i4);
    }

    public static void sign(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, int i3, int i4, byte[] bArr4, int i5) {
        implSign(bArr, i, bArr2, i2, null, (byte) 0, bArr3, i3, i4, bArr4, i5);
    }

    public static void sign(byte[] bArr, int i, byte[] bArr2, byte[] bArr3, int i2, int i3, byte[] bArr4, int i4) {
        implSign(bArr, i, bArr2, (byte) 0, bArr3, i2, i3, bArr4, i4);
    }

    public static void sign(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte[] bArr4, int i3, int i4, byte[] bArr5, int i5) {
        implSign(bArr, i, bArr2, i2, bArr3, (byte) 0, bArr4, i3, i4, bArr5, i5);
    }

    public static void signPrehash(byte[] bArr, int i, byte[] bArr2, byte[] bArr3, int i2, byte[] bArr4, int i3) {
        implSign(bArr, i, bArr2, (byte) 1, bArr3, i2, 64, bArr4, i3);
    }

    public static void signPrehash(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte[] bArr4, int i3, byte[] bArr5, int i4) {
        implSign(bArr, i, bArr2, i2, bArr3, (byte) 1, bArr4, i3, 64, bArr5, i4);
    }

    public static void signPrehash(byte[] bArr, int i, byte[] bArr2, Digest digest, byte[] bArr3, int i2) {
        byte[] bArr4 = new byte[64];
        if (64 != digest.doFinal(bArr4, 0)) {
            throw new IllegalArgumentException("ph");
        }
        implSign(bArr, i, bArr2, (byte) 1, bArr4, 0, bArr4.length, bArr3, i2);
    }

    public static void signPrehash(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, Digest digest, byte[] bArr4, int i3) {
        byte[] bArr5 = new byte[64];
        if (64 != digest.doFinal(bArr5, 0)) {
            throw new IllegalArgumentException("ph");
        }
        implSign(bArr, i, bArr2, i2, bArr3, (byte) 1, bArr5, 0, bArr5.length, bArr4, i3);
    }

    public static boolean validatePublicKeyFull(byte[] bArr, int i) {
        byte[] bArrCopy = copy(bArr, i, 32);
        if (!checkPointFullVar(bArrCopy)) {
            return false;
        }
        PointAffine pointAffine = new PointAffine();
        if (decodePointVar(bArrCopy, false, pointAffine)) {
            return checkPointOrderVar(pointAffine);
        }
        return false;
    }

    public static PublicPoint validatePublicKeyFullExport(byte[] bArr, int i) {
        byte[] bArrCopy = copy(bArr, i, 32);
        if (!checkPointFullVar(bArrCopy)) {
            return null;
        }
        PointAffine pointAffine = new PointAffine();
        if (decodePointVar(bArrCopy, false, pointAffine) && checkPointOrderVar(pointAffine)) {
            return exportPoint(pointAffine);
        }
        return null;
    }

    public static boolean validatePublicKeyPartial(byte[] bArr, int i) {
        byte[] bArrCopy = copy(bArr, i, 32);
        if (checkPointFullVar(bArrCopy)) {
            return decodePointVar(bArrCopy, false, new PointAffine());
        }
        return false;
    }

    public static PublicPoint validatePublicKeyPartialExport(byte[] bArr, int i) {
        byte[] bArrCopy = copy(bArr, i, 32);
        if (!checkPointFullVar(bArrCopy)) {
            return null;
        }
        PointAffine pointAffine = new PointAffine();
        if (decodePointVar(bArrCopy, false, pointAffine)) {
            return exportPoint(pointAffine);
        }
        return null;
    }

    public static boolean verify(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, int i3, int i4) {
        return implVerify(bArr, i, bArr2, i2, null, (byte) 0, bArr3, i3, i4);
    }

    public static boolean verify(byte[] bArr, int i, PublicPoint publicPoint, byte[] bArr2, int i2, int i3) {
        return implVerify(bArr, i, publicPoint, null, (byte) 0, bArr2, i2, i3);
    }

    public static boolean verify(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte[] bArr4, int i3, int i4) {
        return implVerify(bArr, i, bArr2, i2, bArr3, (byte) 0, bArr4, i3, i4);
    }

    public static boolean verify(byte[] bArr, int i, PublicPoint publicPoint, byte[] bArr2, byte[] bArr3, int i2, int i3) {
        return implVerify(bArr, i, publicPoint, bArr2, (byte) 0, bArr3, i2, i3);
    }

    public static boolean verifyPrehash(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, byte[] bArr4, int i3) {
        return implVerify(bArr, i, bArr2, i2, bArr3, (byte) 1, bArr4, i3, 64);
    }

    public static boolean verifyPrehash(byte[] bArr, int i, PublicPoint publicPoint, byte[] bArr2, byte[] bArr3, int i2) {
        return implVerify(bArr, i, publicPoint, bArr2, (byte) 1, bArr3, i2, 64);
    }

    public static boolean verifyPrehash(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3, Digest digest) {
        byte[] bArr4 = new byte[64];
        if (64 != digest.doFinal(bArr4, 0)) {
            throw new IllegalArgumentException("ph");
        }
        return implVerify(bArr, i, bArr2, i2, bArr3, (byte) 1, bArr4, 0, bArr4.length);
    }

    public static boolean verifyPrehash(byte[] bArr, int i, PublicPoint publicPoint, byte[] bArr2, Digest digest) {
        byte[] bArr3 = new byte[64];
        if (64 != digest.doFinal(bArr3, 0)) {
            throw new IllegalArgumentException("ph");
        }
        return implVerify(bArr, i, publicPoint, bArr2, (byte) 1, bArr3, 0, bArr3.length);
    }
}
