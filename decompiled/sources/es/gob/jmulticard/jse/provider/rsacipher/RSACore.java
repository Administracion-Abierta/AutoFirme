package es.gob.jmulticard.jse.provider.rsacipher;

import es.gob.jmulticard.card.CryptoCardException;
import es.gob.jmulticard.card.PinException;
import es.gob.jmulticard.card.dnie.Dnie;
import es.gob.jmulticard.connection.LostChannelException;
import es.gob.jmulticard.jse.provider.DniePrivateKey;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.WeakHashMap;
import javax.crypto.BadPaddingException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/rsacipher/RSACore.class */
final class RSACore {
    private static final boolean ENABLE_BLINDING = true;
    private static final Map<BigInteger, BlindingParameters> BLINDING_CACHE;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !RSACore.class.desiredAssertionStatus();
        BLINDING_CACHE = new WeakHashMap();
    }

    private RSACore() {
    }

    static int getByteLength(BigInteger b) {
        int n = b.bitLength();
        return (n + 7) >> 3;
    }

    static byte[] convert(byte[] b, int ofs, int len) {
        if (ofs == 0 && len == b.length) {
            return b;
        }
        byte[] t = new byte[len];
        System.arraycopy(b, ofs, t, 0, len);
        return t;
    }

    static byte[] rsa(byte[] msg, RSAPublicKey key) throws BadPaddingException {
        return crypt(msg, key.getModulus(), key.getPublicExponent());
    }

    static byte[] rsa(byte[] msg, RSAPrivateKey key) throws BadPaddingException, CryptoCardException, PinException, LostChannelException {
        if (key instanceof DniePrivateKey) {
            Dnie dni = (Dnie) ((DniePrivateKey) key).getCryptoCard();
            return dni.cipherData(msg, ((DniePrivateKey) key).getDniePrivateKeyReference());
        }
        return priCrypt(msg, key.getModulus(), key.getPrivateExponent());
    }

    private static byte[] crypt(byte[] msg, BigInteger n, BigInteger exp) throws BadPaddingException {
        BigInteger m = parseMsg(msg, n);
        BigInteger c = m.modPow(exp, n);
        return toByteArray(c, getByteLength(n));
    }

    private static byte[] priCrypt(byte[] msg, BigInteger n, BigInteger exp) throws BadPaddingException {
        BigInteger c = parseMsg(msg, n);
        BlindingRandomPair brp = getBlindingRandomPair(null, exp, n);
        BigInteger m = c.multiply(brp.u).mod(n).modPow(exp, n).multiply(brp.v).mod(n);
        return toByteArray(m, getByteLength(n));
    }

    private static BigInteger parseMsg(byte[] msg, BigInteger n) throws BadPaddingException {
        BigInteger m = new BigInteger(1, msg);
        if (m.compareTo(n) >= 0) {
            throw new BadPaddingException("El mensaje es mas grande que el modulo");
        }
        return m;
    }

    private static byte[] toByteArray(BigInteger bi, int len) {
        byte[] b = bi.toByteArray();
        int n = b.length;
        if (n == len) {
            return b;
        }
        if (n == len + 1 && b[0] == 0) {
            byte[] t = new byte[len];
            System.arraycopy(b, 1, t, 0, len);
            return t;
        }
        if (!$assertionsDisabled && n >= len) {
            throw new AssertionError();
        }
        byte[] t2 = new byte[len];
        System.arraycopy(b, 0, t2, len - n, n);
        return t2;
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/rsacipher/RSACore$BlindingRandomPair.class */
    private static final class BlindingRandomPair {
        final BigInteger u;
        final BigInteger v;

        BlindingRandomPair(BigInteger pairU, BigInteger pairV) {
            this.u = pairU;
            this.v = pairV;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/rsacipher/RSACore$BlindingParameters.class */
    private static final class BlindingParameters {
        private static final BigInteger BIG_TWO = BigInteger.valueOf(2);
        private final BigInteger e;
        private final BigInteger d;
        private BigInteger u;
        private BigInteger v;

        BlindingParameters(BigInteger exponent, BigInteger privateExponent, BigInteger modulus) {
            this.u = null;
            this.v = null;
            this.e = exponent;
            this.d = privateExponent;
            int len = modulus.bitLength();
            SecureRandom random = new SecureRandom();
            this.u = new BigInteger(len, random).mod(modulus);
            if (this.u.equals(BigInteger.ZERO)) {
                this.u = BigInteger.ONE;
            }
            try {
                this.v = this.u.modInverse(modulus);
            } catch (ArithmeticException e) {
                this.u = BigInteger.ONE;
                this.v = BigInteger.ONE;
            }
            if (exponent != null) {
                this.u = this.u.modPow(exponent, modulus);
            } else {
                this.v = this.v.modPow(privateExponent, modulus);
            }
        }

        BlindingRandomPair getBlindingRandomPair(BigInteger exponent, BigInteger privateRsaExponentHash, BigInteger n) {
            if ((this.e != null && this.e.equals(exponent)) || (this.d != null && this.d.equals(privateRsaExponentHash))) {
                BlindingRandomPair brp = null;
                synchronized (this) {
                    if (!this.u.equals(BigInteger.ZERO) && !this.v.equals(BigInteger.ZERO)) {
                        brp = new BlindingRandomPair(this.u, this.v);
                        if (this.u.compareTo(BigInteger.ONE) <= 0 || this.v.compareTo(BigInteger.ONE) <= 0) {
                            this.u = BigInteger.ZERO;
                            this.v = BigInteger.ZERO;
                        } else {
                            this.u = this.u.modPow(BIG_TWO, n);
                            this.v = this.v.modPow(BIG_TWO, n);
                        }
                    }
                }
                return brp;
            }
            return null;
        }
    }

    private static BlindingRandomPair getBlindingRandomPair(BigInteger e, BigInteger d, BigInteger n) {
        BlindingParameters bps;
        synchronized (BLINDING_CACHE) {
            bps = BLINDING_CACHE.get(n);
        }
        if (bps == null) {
            bps = new BlindingParameters(e, d, n);
            synchronized (BLINDING_CACHE) {
                if (BLINDING_CACHE.get(n) == null) {
                    BLINDING_CACHE.put(n, bps);
                }
            }
        }
        BlindingRandomPair brp = bps.getBlindingRandomPair(e, d, n);
        if (brp == null) {
            BlindingParameters bps2 = new BlindingParameters(e, d, n);
            synchronized (BLINDING_CACHE) {
                if (BLINDING_CACHE.containsKey(n)) {
                    BLINDING_CACHE.put(n, bps2);
                }
            }
            return bps2.getBlindingRandomPair(e, d, n);
        }
        return brp;
    }
}
