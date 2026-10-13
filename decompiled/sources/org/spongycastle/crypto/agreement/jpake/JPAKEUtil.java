package org.spongycastle.crypto.agreement.jpake;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.BigIntegers;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/agreement/jpake/JPAKEUtil.class */
public class JPAKEUtil {
    static final BigInteger ZERO = BigInteger.valueOf(0);
    static final BigInteger ONE = BigInteger.valueOf(1);

    public static BigInteger generateX1(BigInteger q, SecureRandom random) {
        BigInteger min = ZERO;
        BigInteger max = q.subtract(ONE);
        return BigIntegers.createRandomInRange(min, max, random);
    }

    public static BigInteger generateX2(BigInteger q, SecureRandom random) {
        BigInteger min = ONE;
        BigInteger max = q.subtract(ONE);
        return BigIntegers.createRandomInRange(min, max, random);
    }

    public static BigInteger calculateS(char[] password) {
        return new BigInteger(Strings.toUTF8ByteArray(password));
    }

    public static BigInteger calculateGx(BigInteger p, BigInteger g, BigInteger x) {
        return g.modPow(x, p);
    }

    public static BigInteger calculateGA(BigInteger p, BigInteger gx1, BigInteger gx3, BigInteger gx4) {
        return gx1.multiply(gx3).multiply(gx4).mod(p);
    }

    public static BigInteger calculateX2s(BigInteger q, BigInteger x2, BigInteger s) {
        return x2.multiply(s).mod(q);
    }

    public static BigInteger calculateA(BigInteger p, BigInteger q, BigInteger gA, BigInteger x2s) {
        return gA.modPow(x2s, p);
    }

    public static BigInteger[] calculateZeroKnowledgeProof(BigInteger p, BigInteger q, BigInteger g, BigInteger gx, BigInteger x, String participantId, Digest digest, SecureRandom random) {
        BigInteger vMin = ZERO;
        BigInteger vMax = q.subtract(ONE);
        BigInteger v = BigIntegers.createRandomInRange(vMin, vMax, random);
        BigInteger gv = g.modPow(v, p);
        BigInteger h = calculateHashForZeroKnowledgeProof(g, gv, gx, participantId, digest);
        BigInteger[] zeroKnowledgeProof = {gv, v.subtract(x.multiply(h)).mod(q)};
        return zeroKnowledgeProof;
    }

    private static BigInteger calculateHashForZeroKnowledgeProof(BigInteger g, BigInteger gr, BigInteger gx, String participantId, Digest digest) {
        digest.reset();
        updateDigestIncludingSize(digest, g);
        updateDigestIncludingSize(digest, gr);
        updateDigestIncludingSize(digest, gx);
        updateDigestIncludingSize(digest, participantId);
        byte[] output = new byte[digest.getDigestSize()];
        digest.doFinal(output, 0);
        return new BigInteger(output);
    }

    public static void validateGx4(BigInteger gx4) throws CryptoException {
        if (gx4.equals(ONE)) {
            throw new CryptoException("g^x validation failed.  g^x should not be 1.");
        }
    }

    public static void validateGa(BigInteger ga) throws CryptoException {
        if (ga.equals(ONE)) {
            throw new CryptoException("ga is equal to 1.  It should not be.  The chances of this happening are on the order of 2^160 for a 160-bit q.  Try again.");
        }
    }

    public static void validateZeroKnowledgeProof(BigInteger p, BigInteger q, BigInteger g, BigInteger gx, BigInteger[] zeroKnowledgeProof, String participantId, Digest digest) throws CryptoException {
        BigInteger gv = zeroKnowledgeProof[0];
        BigInteger r = zeroKnowledgeProof[1];
        BigInteger h = calculateHashForZeroKnowledgeProof(g, gv, gx, participantId, digest);
        if (gx.compareTo(ZERO) != 1 || gx.compareTo(p) != -1 || gx.modPow(q, p).compareTo(ONE) != 0 || g.modPow(r, p).multiply(gx.modPow(h, p)).mod(p).compareTo(gv) != 0) {
            throw new CryptoException("Zero-knowledge proof validation failed");
        }
    }

    public static BigInteger calculateKeyingMaterial(BigInteger p, BigInteger q, BigInteger gx4, BigInteger x2, BigInteger s, BigInteger B) {
        return gx4.modPow(x2.multiply(s).negate().mod(q), p).multiply(B).modPow(x2, p);
    }

    public static void validateParticipantIdsDiffer(String participantId1, String participantId2) throws CryptoException {
        if (participantId1.equals(participantId2)) {
            throw new CryptoException("Both participants are using the same participantId (" + participantId1 + "). This is not allowed. Each participant must use a unique participantId.");
        }
    }

    public static void validateParticipantIdsEqual(String expectedParticipantId, String actualParticipantId) throws CryptoException {
        if (!expectedParticipantId.equals(actualParticipantId)) {
            throw new CryptoException("Received payload from incorrect partner (" + actualParticipantId + "). Expected to receive payload from " + expectedParticipantId + ".");
        }
    }

    public static void validateNotNull(Object object, String description) {
        if (object == null) {
            throw new NullPointerException(description + " must not be null");
        }
    }

    public static BigInteger calculateMacTag(String participantId, String partnerParticipantId, BigInteger gx1, BigInteger gx2, BigInteger gx3, BigInteger gx4, BigInteger keyingMaterial, Digest digest) {
        byte[] macKey = calculateMacKey(keyingMaterial, digest);
        HMac mac = new HMac(digest);
        byte[] macOutput = new byte[mac.getMacSize()];
        mac.init(new KeyParameter(macKey));
        updateMac(mac, "KC_1_U");
        updateMac(mac, participantId);
        updateMac(mac, partnerParticipantId);
        updateMac(mac, gx1);
        updateMac(mac, gx2);
        updateMac(mac, gx3);
        updateMac(mac, gx4);
        mac.doFinal(macOutput, 0);
        Arrays.fill(macKey, (byte) 0);
        return new BigInteger(macOutput);
    }

    private static byte[] calculateMacKey(BigInteger keyingMaterial, Digest digest) {
        digest.reset();
        updateDigest(digest, keyingMaterial);
        updateDigest(digest, "JPAKE_KC");
        byte[] output = new byte[digest.getDigestSize()];
        digest.doFinal(output, 0);
        return output;
    }

    public static void validateMacTag(String participantId, String partnerParticipantId, BigInteger gx1, BigInteger gx2, BigInteger gx3, BigInteger gx4, BigInteger keyingMaterial, Digest digest, BigInteger partnerMacTag) throws CryptoException {
        BigInteger expectedMacTag = calculateMacTag(partnerParticipantId, participantId, gx3, gx4, gx1, gx2, keyingMaterial, digest);
        if (!expectedMacTag.equals(partnerMacTag)) {
            throw new CryptoException("Partner MacTag validation failed. Therefore, the password, MAC, or digest algorithm of each participant does not match.");
        }
    }

    private static void updateDigest(Digest digest, BigInteger bigInteger) {
        byte[] byteArray = BigIntegers.asUnsignedByteArray(bigInteger);
        digest.update(byteArray, 0, byteArray.length);
        Arrays.fill(byteArray, (byte) 0);
    }

    private static void updateDigestIncludingSize(Digest digest, BigInteger bigInteger) {
        byte[] byteArray = BigIntegers.asUnsignedByteArray(bigInteger);
        digest.update(intToByteArray(byteArray.length), 0, 4);
        digest.update(byteArray, 0, byteArray.length);
        Arrays.fill(byteArray, (byte) 0);
    }

    private static void updateDigest(Digest digest, String string) {
        byte[] byteArray = Strings.toUTF8ByteArray(string);
        digest.update(byteArray, 0, byteArray.length);
        Arrays.fill(byteArray, (byte) 0);
    }

    private static void updateDigestIncludingSize(Digest digest, String string) {
        byte[] byteArray = Strings.toUTF8ByteArray(string);
        digest.update(intToByteArray(byteArray.length), 0, 4);
        digest.update(byteArray, 0, byteArray.length);
        Arrays.fill(byteArray, (byte) 0);
    }

    private static void updateMac(Mac mac, BigInteger bigInteger) {
        byte[] byteArray = BigIntegers.asUnsignedByteArray(bigInteger);
        mac.update(byteArray, 0, byteArray.length);
        Arrays.fill(byteArray, (byte) 0);
    }

    private static void updateMac(Mac mac, String string) {
        byte[] byteArray = Strings.toUTF8ByteArray(string);
        mac.update(byteArray, 0, byteArray.length);
        Arrays.fill(byteArray, (byte) 0);
    }

    private static byte[] intToByteArray(int value) {
        return new byte[]{(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value};
    }
}
