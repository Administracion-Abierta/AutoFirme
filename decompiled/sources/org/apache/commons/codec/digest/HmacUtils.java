package org.apache.commons.codec.digest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/digest/HmacUtils.class */
public final class HmacUtils {
    private static final int STREAM_BUFFER_LENGTH = 1024;
    private final Mac mac;

    @Deprecated
    public static Mac getHmacMd5(byte[] key) {
        return getInitializedMac(HmacAlgorithms.HMAC_MD5, key);
    }

    @Deprecated
    public static Mac getHmacSha1(byte[] key) {
        return getInitializedMac(HmacAlgorithms.HMAC_SHA_1, key);
    }

    @Deprecated
    public static Mac getHmacSha256(byte[] key) {
        return getInitializedMac(HmacAlgorithms.HMAC_SHA_256, key);
    }

    @Deprecated
    public static Mac getHmacSha384(byte[] key) {
        return getInitializedMac(HmacAlgorithms.HMAC_SHA_384, key);
    }

    @Deprecated
    public static Mac getHmacSha512(byte[] key) {
        return getInitializedMac(HmacAlgorithms.HMAC_SHA_512, key);
    }

    public static Mac getInitializedMac(HmacAlgorithms algorithm, byte[] key) {
        return getInitializedMac(algorithm.getName(), key);
    }

    public static Mac getInitializedMac(String algorithm, byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("Null key");
        }
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(keySpec);
            return mac;
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Deprecated
    public static byte[] hmacMd5(byte[] key, byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_MD5, key).hmac(valueToDigest);
    }

    @Deprecated
    public static byte[] hmacMd5(byte[] key, InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_MD5, key).hmac(valueToDigest);
    }

    @Deprecated
    public static byte[] hmacMd5(String key, String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_MD5, key).hmac(valueToDigest);
    }

    @Deprecated
    public static String hmacMd5Hex(byte[] key, byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_MD5, key).hmacHex(valueToDigest);
    }

    @Deprecated
    public static String hmacMd5Hex(byte[] key, InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_MD5, key).hmacHex(valueToDigest);
    }

    @Deprecated
    public static String hmacMd5Hex(String key, String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_MD5, key).hmacHex(valueToDigest);
    }

    @Deprecated
    public static byte[] hmacSha1(byte[] key, byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key).hmac(valueToDigest);
    }

    @Deprecated
    public static byte[] hmacSha1(byte[] key, InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key).hmac(valueToDigest);
    }

    @Deprecated
    public static byte[] hmacSha1(String key, String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key).hmac(valueToDigest);
    }

    @Deprecated
    public static String hmacSha1Hex(byte[] key, byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key).hmacHex(valueToDigest);
    }

    @Deprecated
    public static String hmacSha1Hex(byte[] key, InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key).hmacHex(valueToDigest);
    }

    @Deprecated
    public static String hmacSha1Hex(String key, String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key).hmacHex(valueToDigest);
    }

    @Deprecated
    public static byte[] hmacSha256(byte[] key, byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key).hmac(valueToDigest);
    }

    @Deprecated
    public static byte[] hmacSha256(byte[] key, InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key).hmac(valueToDigest);
    }

    @Deprecated
    public static byte[] hmacSha256(String key, String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key).hmac(valueToDigest);
    }

    @Deprecated
    public static String hmacSha256Hex(byte[] key, byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key).hmacHex(valueToDigest);
    }

    @Deprecated
    public static String hmacSha256Hex(byte[] key, InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key).hmacHex(valueToDigest);
    }

    @Deprecated
    public static String hmacSha256Hex(String key, String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key).hmacHex(valueToDigest);
    }

    @Deprecated
    public static byte[] hmacSha384(byte[] key, byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key).hmac(valueToDigest);
    }

    @Deprecated
    public static byte[] hmacSha384(byte[] key, InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key).hmac(valueToDigest);
    }

    @Deprecated
    public static byte[] hmacSha384(String key, String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key).hmac(valueToDigest);
    }

    @Deprecated
    public static String hmacSha384Hex(byte[] key, byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key).hmacHex(valueToDigest);
    }

    @Deprecated
    public static String hmacSha384Hex(byte[] key, InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key).hmacHex(valueToDigest);
    }

    @Deprecated
    public static String hmacSha384Hex(String key, String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key).hmacHex(valueToDigest);
    }

    @Deprecated
    public static byte[] hmacSha512(byte[] key, byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key).hmac(valueToDigest);
    }

    @Deprecated
    public static byte[] hmacSha512(byte[] key, InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key).hmac(valueToDigest);
    }

    @Deprecated
    public static byte[] hmacSha512(String key, String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key).hmac(valueToDigest);
    }

    @Deprecated
    public static String hmacSha512Hex(byte[] key, byte[] valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key).hmacHex(valueToDigest);
    }

    @Deprecated
    public static String hmacSha512Hex(byte[] key, InputStream valueToDigest) throws IOException {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key).hmacHex(valueToDigest);
    }

    @Deprecated
    public static String hmacSha512Hex(String key, String valueToDigest) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key).hmacHex(valueToDigest);
    }

    public static boolean isAvailable(HmacAlgorithms name) {
        try {
            Mac.getInstance(name.getName());
            return true;
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    public static boolean isAvailable(String name) {
        try {
            Mac.getInstance(name);
            return true;
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    public static Mac updateHmac(Mac mac, byte[] valueToDigest) {
        mac.reset();
        mac.update(valueToDigest);
        return mac;
    }

    public static Mac updateHmac(Mac mac, InputStream valueToDigest) throws IOException {
        mac.reset();
        byte[] buffer = new byte[1024];
        int i = valueToDigest.read(buffer, 0, 1024);
        while (true) {
            int read = i;
            if (read > -1) {
                mac.update(buffer, 0, read);
                i = valueToDigest.read(buffer, 0, 1024);
            } else {
                return mac;
            }
        }
    }

    public static Mac updateHmac(Mac mac, String valueToDigest) {
        mac.reset();
        mac.update(StringUtils.getBytesUtf8(valueToDigest));
        return mac;
    }

    @Deprecated
    public HmacUtils() {
        this(null);
    }

    public HmacUtils(HmacAlgorithms algorithm, byte[] key) {
        this(algorithm.getName(), key);
    }

    public HmacUtils(HmacAlgorithms algorithm, String key) {
        this(algorithm.getName(), StringUtils.getBytesUtf8(key));
    }

    private HmacUtils(Mac mac) {
        this.mac = mac;
    }

    public HmacUtils(String algorithm, byte[] key) {
        this(getInitializedMac(algorithm, key));
    }

    public HmacUtils(String algorithm, String key) {
        this(algorithm, StringUtils.getBytesUtf8(key));
    }

    public byte[] hmac(byte[] valueToDigest) {
        return this.mac.doFinal(valueToDigest);
    }

    public byte[] hmac(ByteBuffer valueToDigest) {
        this.mac.update(valueToDigest);
        return this.mac.doFinal();
    }

    public byte[] hmac(File valueToDigest) throws IOException {
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(valueToDigest));
        try {
            byte[] bArrHmac = hmac(stream);
            stream.close();
            return bArrHmac;
        } catch (Throwable th) {
            try {
                stream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public byte[] hmac(InputStream valueToDigest) throws IOException {
        byte[] buffer = new byte[1024];
        while (true) {
            int read = valueToDigest.read(buffer, 0, 1024);
            if (read > -1) {
                this.mac.update(buffer, 0, read);
            } else {
                return this.mac.doFinal();
            }
        }
    }

    public byte[] hmac(String valueToDigest) {
        return this.mac.doFinal(StringUtils.getBytesUtf8(valueToDigest));
    }

    public String hmacHex(byte[] valueToDigest) {
        return Hex.encodeHexString(hmac(valueToDigest));
    }

    public String hmacHex(ByteBuffer valueToDigest) {
        return Hex.encodeHexString(hmac(valueToDigest));
    }

    public String hmacHex(File valueToDigest) throws IOException {
        return Hex.encodeHexString(hmac(valueToDigest));
    }

    public String hmacHex(InputStream valueToDigest) throws IOException {
        return Hex.encodeHexString(hmac(valueToDigest));
    }

    public String hmacHex(String valueToDigest) {
        return Hex.encodeHexString(hmac(valueToDigest));
    }
}
