package org.bouncycastle.crypto.agreement.ecjpake;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/agreement/ecjpake/ECJPAKEUtil.class */
public class ECJPAKEUtil {
    static final BigInteger ZERO = BigInteger.valueOf(0);
    static final BigInteger ONE = BigInteger.valueOf(1);

    public static BigInteger generateX1(BigInteger bigInteger, SecureRandom secureRandom) {
        return BigIntegers.createRandomInRange(ONE, bigInteger.subtract(ONE), secureRandom);
    }

    public static BigInteger calculateS(BigInteger bigInteger, byte[] bArr) throws CryptoException {
        BigInteger bigIntegerMod = new BigInteger(1, bArr).mod(bigInteger);
        if (bigIntegerMod.signum() == 0) {
            throw new CryptoException("MUST ensure s is not equal to 0 modulo n");
        }
        return bigIntegerMod;
    }

    public static BigInteger calculateS(BigInteger bigInteger, char[] cArr) throws CryptoException {
        return calculateS(bigInteger, Strings.toUTF8ByteArray(cArr));
    }

    public static ECPoint calculateGx(ECPoint eCPoint, BigInteger bigInteger) {
        return eCPoint.multiply(bigInteger);
    }

    public static ECPoint calculateGA(ECPoint eCPoint, ECPoint eCPoint2, ECPoint eCPoint3) {
        return eCPoint.add(eCPoint2).add(eCPoint3);
    }

    public static BigInteger calculateX2s(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3) {
        return bigInteger2.multiply(bigInteger3).mod(bigInteger);
    }

    public static ECPoint calculateA(ECPoint eCPoint, BigInteger bigInteger) {
        return eCPoint.multiply(bigInteger);
    }

    public static ECSchnorrZKP calculateZeroKnowledgeProof(ECPoint eCPoint, BigInteger bigInteger, BigInteger bigInteger2, ECPoint eCPoint2, Digest digest, String str, SecureRandom secureRandom) {
        BigInteger bigIntegerCreateRandomInRange = BigIntegers.createRandomInRange(BigInteger.ONE, bigInteger.subtract(BigInteger.ONE), secureRandom);
        ECPoint eCPointMultiply = eCPoint.multiply(bigIntegerCreateRandomInRange);
        return new ECSchnorrZKP(eCPointMultiply, bigIntegerCreateRandomInRange.subtract(bigInteger2.multiply(calculateHashForZeroKnowledgeProof(eCPoint, eCPointMultiply, eCPoint2, str, digest))).mod(bigInteger));
    }

    private static BigInteger calculateHashForZeroKnowledgeProof(ECPoint eCPoint, ECPoint eCPoint2, ECPoint eCPoint3, String str, Digest digest) {
        digest.reset();
        updateDigestIncludingSize(digest, eCPoint);
        updateDigestIncludingSize(digest, eCPoint2);
        updateDigestIncludingSize(digest, eCPoint3);
        updateDigestIncludingSize(digest, str);
        byte[] bArr = new byte[digest.getDigestSize()];
        digest.doFinal(bArr, 0);
        return new BigInteger(bArr);
    }

    private static void updateDigestIncludingSize(Digest digest, ECPoint eCPoint) {
        byte[] encoded = eCPoint.getEncoded(true);
        digest.update(intToByteArray(encoded.length), 0, 4);
        digest.update(encoded, 0, encoded.length);
        Arrays.fill(encoded, (byte) 0);
    }

    private static void updateDigestIncludingSize(Digest digest, String str) {
        byte[] uTF8ByteArray = Strings.toUTF8ByteArray(str);
        digest.update(intToByteArray(uTF8ByteArray.length), 0, 4);
        digest.update(uTF8ByteArray, 0, uTF8ByteArray.length);
        Arrays.fill(uTF8ByteArray, (byte) 0);
    }

    public static void validateZeroKnowledgeProof(ECPoint eCPoint, ECPoint eCPoint2, ECSchnorrZKP eCSchnorrZKP, BigInteger bigInteger, BigInteger bigInteger2, ECCurve eCCurve, BigInteger bigInteger3, String str, Digest digest) throws CryptoException {
        ECPoint v = eCSchnorrZKP.getV();
        BigInteger rVar = eCSchnorrZKP.getr();
        BigInteger bigIntegerCalculateHashForZeroKnowledgeProof = calculateHashForZeroKnowledgeProof(eCPoint, v, eCPoint2, str, digest);
        if (eCPoint2.isInfinity()) {
            throw new CryptoException("Zero-knowledge proof validation failed: X cannot equal infinity");
        }
        ECPoint eCPointNormalize = eCPoint2.normalize();
        if (eCPointNormalize.getAffineXCoord().toBigInteger().compareTo(BigInteger.ZERO) == -1 || eCPointNormalize.getAffineXCoord().toBigInteger().compareTo(bigInteger.subtract(BigInteger.ONE)) == 1 || eCPointNormalize.getAffineYCoord().toBigInteger().compareTo(BigInteger.ZERO) == -1 || eCPointNormalize.getAffineYCoord().toBigInteger().compareTo(bigInteger.subtract(BigInteger.ONE)) == 1) {
            throw new CryptoException("Zero-knowledge proof validation failed: x and y are not in the field");
        }
        try {
            eCCurve.decodePoint(eCPoint2.getEncoded(true));
            if (eCPoint2.multiply(bigInteger3).isInfinity()) {
                throw new CryptoException("Zero-knowledge proof validation failed: Nx cannot be infinity");
            }
            if (!v.equals(eCPoint.multiply(rVar).add(eCPoint2.multiply(bigIntegerCalculateHashForZeroKnowledgeProof.mod(bigInteger2))))) {
                throw new CryptoException("Zero-knowledge proof validation failed: V must be a point on the curve");
            }
        } catch (Exception e) {
            throw new CryptoException("Zero-knowledge proof validation failed: x does not lie on the curve", e);
        }
    }

    public static void validateParticipantIdsDiffer(String str, String str2) throws CryptoException {
        if (str.equals(str2)) {
            throw new CryptoException("Both participants are using the same participantId (" + str + "). This is not allowed. Each participant must use a unique participantId.");
        }
    }

    public static void validateParticipantIdsEqual(String str, String str2) throws CryptoException {
        if (!str.equals(str2)) {
            throw new CryptoException("Received payload from incorrect partner (" + str2 + "). Expected to receive payload from " + str + ".");
        }
    }

    public static void validateNotNull(Object obj, String str) {
        if (obj == null) {
            throw new NullPointerException(str + " must not be null");
        }
    }

    public static BigInteger calculateKeyingMaterial(BigInteger bigInteger, ECPoint eCPoint, BigInteger bigInteger2, BigInteger bigInteger3, ECPoint eCPoint2) {
        return eCPoint2.subtract(eCPoint.multiply(bigInteger2.multiply(bigInteger3).mod(bigInteger))).multiply(bigInteger2).normalize().getAffineXCoord().toBigInteger();
    }

    public static BigInteger calculateMacTag(String str, String str2, ECPoint eCPoint, ECPoint eCPoint2, ECPoint eCPoint3, ECPoint eCPoint4, BigInteger bigInteger, Digest digest) {
        byte[] bArrCalculateMacKey = calculateMacKey(bigInteger, digest);
        HMac hMac = new HMac(digest);
        byte[] bArr = new byte[hMac.getMacSize()];
        hMac.init(new KeyParameter(bArrCalculateMacKey));
        updateMac(hMac, "KC_1_U");
        updateMac(hMac, str);
        updateMac(hMac, str2);
        updateMac(hMac, eCPoint);
        updateMac(hMac, eCPoint2);
        updateMac(hMac, eCPoint3);
        updateMac(hMac, eCPoint4);
        hMac.doFinal(bArr, 0);
        Arrays.fill(bArrCalculateMacKey, (byte) 0);
        return new BigInteger(bArr);
    }

    private static byte[] calculateMacKey(BigInteger bigInteger, Digest digest) {
        digest.reset();
        updateDigest(digest, bigInteger);
        updateDigest(digest, "ECJPAKE_KC");
        byte[] bArr = new byte[digest.getDigestSize()];
        digest.doFinal(bArr, 0);
        return bArr;
    }

    public static void validateMacTag(String str, String str2, ECPoint eCPoint, ECPoint eCPoint2, ECPoint eCPoint3, ECPoint eCPoint4, BigInteger bigInteger, Digest digest, BigInteger bigInteger2) throws CryptoException {
        if (!calculateMacTag(str2, str, eCPoint3, eCPoint4, eCPoint, eCPoint2, bigInteger, digest).equals(bigInteger2)) {
            throw new CryptoException("Partner MacTag validation failed. Therefore, the password, MAC, or digest algorithm of each participant does not match.");
        }
    }

    private static void updateMac(Mac mac, ECPoint eCPoint) {
        byte[] encoded = eCPoint.getEncoded(true);
        mac.update(encoded, 0, encoded.length);
        Arrays.fill(encoded, (byte) 0);
    }

    private static void updateMac(Mac mac, String str) {
        byte[] uTF8ByteArray = Strings.toUTF8ByteArray(str);
        mac.update(uTF8ByteArray, 0, uTF8ByteArray.length);
        Arrays.fill(uTF8ByteArray, (byte) 0);
    }

    private static void updateDigest(Digest digest, ECPoint eCPoint) {
        byte[] encoded = eCPoint.getEncoded(true);
        digest.update(encoded, 0, encoded.length);
        Arrays.fill(encoded, (byte) 0);
    }

    private static void updateDigest(Digest digest, String str) {
        byte[] uTF8ByteArray = Strings.toUTF8ByteArray(str);
        digest.update(uTF8ByteArray, 0, uTF8ByteArray.length);
        Arrays.fill(uTF8ByteArray, (byte) 0);
    }

    private static void updateDigest(Digest digest, BigInteger bigInteger) {
        byte[] bArrAsUnsignedByteArray = BigIntegers.asUnsignedByteArray(bigInteger);
        digest.update(bArrAsUnsignedByteArray, 0, bArrAsUnsignedByteArray.length);
        Arrays.fill(bArrAsUnsignedByteArray, (byte) 0);
    }

    private static byte[] intToByteArray(int i) {
        return new byte[]{(byte) (i >>> 24), (byte) (i >>> 16), (byte) (i >>> 8), (byte) i};
    }
}
