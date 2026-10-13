package org.spongycastle.math;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.asn1.cmc.BodyPartID;
import org.spongycastle.asn1.eac.CertificateBody;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.tls.AlertDescription;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.crypto.tls.ExtensionType;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/Primes.class */
public abstract class Primes {
    public static final int SMALL_FACTOR_LIMIT = 211;
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger THREE = BigInteger.valueOf(3);

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/Primes$MROutput.class */
    public static class MROutput {
        private boolean provablyComposite;
        private BigInteger factor;

        /* JADX INFO: Access modifiers changed from: private */
        public static MROutput probablyPrime() {
            return new MROutput(false, null);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static MROutput provablyCompositeWithFactor(BigInteger factor) {
            return new MROutput(true, factor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static MROutput provablyCompositeNotPrimePower() {
            return new MROutput(true, null);
        }

        private MROutput(boolean provablyComposite, BigInteger factor) {
            this.provablyComposite = provablyComposite;
            this.factor = factor;
        }

        public BigInteger getFactor() {
            return this.factor;
        }

        public boolean isProvablyComposite() {
            return this.provablyComposite;
        }

        public boolean isNotPrimePower() {
            return this.provablyComposite && this.factor == null;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/Primes$STOutput.class */
    public static class STOutput {
        private BigInteger prime;
        private byte[] primeSeed;
        private int primeGenCounter;

        private STOutput(BigInteger prime, byte[] primeSeed, int primeGenCounter) {
            this.prime = prime;
            this.primeSeed = primeSeed;
            this.primeGenCounter = primeGenCounter;
        }

        public BigInteger getPrime() {
            return this.prime;
        }

        public byte[] getPrimeSeed() {
            return this.primeSeed;
        }

        public int getPrimeGenCounter() {
            return this.primeGenCounter;
        }
    }

    public static STOutput generateSTRandomPrime(Digest hash, int length, byte[] inputSeed) {
        if (hash == null) {
            throw new IllegalArgumentException("'hash' cannot be null");
        }
        if (length < 2) {
            throw new IllegalArgumentException("'length' must be >= 2");
        }
        if (inputSeed == null || inputSeed.length == 0) {
            throw new IllegalArgumentException("'inputSeed' cannot be null or empty");
        }
        return implSTRandomPrime(hash, length, Arrays.clone(inputSeed));
    }

    public static MROutput enhancedMRProbablePrimeTest(BigInteger candidate, SecureRandom random, int iterations) {
        checkCandidate(candidate, "candidate");
        if (random == null) {
            throw new IllegalArgumentException("'random' cannot be null");
        }
        if (iterations < 1) {
            throw new IllegalArgumentException("'iterations' must be > 0");
        }
        if (candidate.bitLength() == 2) {
            return MROutput.probablyPrime();
        }
        if (!candidate.testBit(0)) {
            return MROutput.provablyCompositeWithFactor(TWO);
        }
        BigInteger wSubOne = candidate.subtract(ONE);
        BigInteger wSubTwo = candidate.subtract(TWO);
        int a = wSubOne.getLowestSetBit();
        BigInteger m = wSubOne.shiftRight(a);
        for (int i = 0; i < iterations; i++) {
            BigInteger b = BigIntegers.createRandomInRange(TWO, wSubTwo, random);
            BigInteger g = b.gcd(candidate);
            if (g.compareTo(ONE) > 0) {
                return MROutput.provablyCompositeWithFactor(g);
            }
            BigInteger z = b.modPow(m, candidate);
            if (!z.equals(ONE) && !z.equals(wSubOne)) {
                boolean primeToBase = false;
                BigInteger x = z;
                for (int j = 1; j < a; j++) {
                    z = z.modPow(TWO, candidate);
                    if (z.equals(wSubOne)) {
                        primeToBase = true;
                        break;
                    }
                    if (z.equals(ONE)) {
                        break;
                    }
                    x = z;
                }
                if (!primeToBase) {
                    if (!z.equals(ONE)) {
                        x = z;
                        BigInteger z2 = z.modPow(TWO, candidate);
                        if (!z2.equals(ONE)) {
                            x = z2;
                        }
                    }
                    BigInteger g2 = x.subtract(ONE).gcd(candidate);
                    return g2.compareTo(ONE) > 0 ? MROutput.provablyCompositeWithFactor(g2) : MROutput.provablyCompositeNotPrimePower();
                }
            }
        }
        return MROutput.probablyPrime();
    }

    public static boolean hasAnySmallFactors(BigInteger candidate) {
        checkCandidate(candidate, "candidate");
        return implHasAnySmallFactors(candidate);
    }

    public static boolean isMRProbablePrime(BigInteger candidate, SecureRandom random, int iterations) {
        checkCandidate(candidate, "candidate");
        if (random == null) {
            throw new IllegalArgumentException("'random' cannot be null");
        }
        if (iterations < 1) {
            throw new IllegalArgumentException("'iterations' must be > 0");
        }
        if (candidate.bitLength() == 2) {
            return true;
        }
        if (!candidate.testBit(0)) {
            return false;
        }
        BigInteger wSubOne = candidate.subtract(ONE);
        BigInteger wSubTwo = candidate.subtract(TWO);
        int a = wSubOne.getLowestSetBit();
        BigInteger m = wSubOne.shiftRight(a);
        for (int i = 0; i < iterations; i++) {
            BigInteger b = BigIntegers.createRandomInRange(TWO, wSubTwo, random);
            if (!implMRProbablePrimeToBase(candidate, wSubOne, m, a, b)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isMRProbablePrimeToBase(BigInteger candidate, BigInteger base) {
        checkCandidate(candidate, "candidate");
        checkCandidate(base, "base");
        if (base.compareTo(candidate.subtract(ONE)) >= 0) {
            throw new IllegalArgumentException("'base' must be < ('candidate' - 1)");
        }
        if (candidate.bitLength() == 2) {
            return true;
        }
        BigInteger wSubOne = candidate.subtract(ONE);
        int a = wSubOne.getLowestSetBit();
        BigInteger m = wSubOne.shiftRight(a);
        return implMRProbablePrimeToBase(candidate, wSubOne, m, a, base);
    }

    private static void checkCandidate(BigInteger n, String name) {
        if (n == null || n.signum() < 1 || n.bitLength() < 2) {
            throw new IllegalArgumentException("'" + name + "' must be non-null and >= 2");
        }
    }

    private static boolean implHasAnySmallFactors(BigInteger x) {
        int r = x.mod(BigInteger.valueOf(223092870)).intValue();
        if (r % 2 == 0 || r % 3 == 0 || r % 5 == 0 || r % 7 == 0 || r % 11 == 0 || r % 13 == 0 || r % 17 == 0 || r % 19 == 0 || r % 23 == 0) {
            return true;
        }
        int r2 = x.mod(BigInteger.valueOf(58642669)).intValue();
        if (r2 % 29 == 0 || r2 % 31 == 0 || r2 % 37 == 0 || r2 % 41 == 0 || r2 % 43 == 0) {
            return true;
        }
        int r3 = x.mod(BigInteger.valueOf(600662303)).intValue();
        if (r3 % 47 == 0 || r3 % 53 == 0 || r3 % 59 == 0 || r3 % 61 == 0 || r3 % 67 == 0) {
            return true;
        }
        int r4 = x.mod(BigInteger.valueOf(33984931)).intValue();
        if (r4 % 71 == 0 || r4 % 73 == 0 || r4 % 79 == 0 || r4 % 83 == 0) {
            return true;
        }
        int r5 = x.mod(BigInteger.valueOf(89809099)).intValue();
        if (r5 % 89 == 0 || r5 % 97 == 0 || r5 % ExtensionType.negotiated_ff_dhe_groups == 0 || r5 % 103 == 0) {
            return true;
        }
        int r6 = x.mod(BigInteger.valueOf(167375713)).intValue();
        if (r6 % CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA256 == 0 || r6 % CipherSuite.TLS_DH_anon_WITH_AES_256_CBC_SHA256 == 0 || r6 % AlertDescription.bad_certificate_status_response == 0 || r6 % CertificateBody.profileType == 0) {
            return true;
        }
        int r7 = x.mod(BigInteger.valueOf(371700317)).intValue();
        if (r7 % 131 == 0 || r7 % CipherSuite.TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA == 0 || r7 % CipherSuite.TLS_PSK_WITH_3DES_EDE_CBC_SHA == 0 || r7 % CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA == 0) {
            return true;
        }
        int r8 = x.mod(BigInteger.valueOf(645328247)).intValue();
        if (r8 % CipherSuite.TLS_DH_DSS_WITH_SEED_CBC_SHA == 0 || r8 % CipherSuite.TLS_RSA_WITH_AES_256_GCM_SHA384 == 0 || r8 % CipherSuite.TLS_DHE_DSS_WITH_AES_256_GCM_SHA384 == 0 || r8 % CipherSuite.TLS_DH_anon_WITH_AES_256_GCM_SHA384 == 0) {
            return true;
        }
        int r9 = x.mod(BigInteger.valueOf(1070560157)).intValue();
        if (r9 % CipherSuite.TLS_RSA_PSK_WITH_AES_256_GCM_SHA384 == 0 || r9 % CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA384 == 0 || r9 % CipherSuite.TLS_DHE_PSK_WITH_NULL_SHA384 == 0 || r9 % CipherSuite.TLS_DH_anon_WITH_CAMELLIA_128_CBC_SHA256 == 0) {
            return true;
        }
        int r10 = x.mod(BigInteger.valueOf(1596463769)).intValue();
        if (r10 % CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_256_CBC_SHA256 == 0 || r10 % CipherSuite.TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA256 == 0 || r10 % 199 == 0 || r10 % SMALL_FACTOR_LIMIT == 0) {
            return true;
        }
        return false;
    }

    private static boolean implMRProbablePrimeToBase(BigInteger w, BigInteger wSubOne, BigInteger m, int a, BigInteger b) {
        BigInteger z = b.modPow(m, w);
        if (z.equals(ONE) || z.equals(wSubOne)) {
            return true;
        }
        boolean result = false;
        for (int j = 1; j < a; j++) {
            z = z.modPow(TWO, w);
            if (z.equals(wSubOne)) {
                result = true;
                break;
            }
            if (z.equals(ONE)) {
                return false;
            }
        }
        return result;
    }

    private static STOutput implSTRandomPrime(Digest d, int length, byte[] primeSeed) {
        int dLen = d.getDigestSize();
        if (length < 33) {
            int primeGenCounter = 0;
            byte[] c0 = new byte[dLen];
            byte[] c1 = new byte[dLen];
            do {
                hash(d, primeSeed, c0, 0);
                inc(primeSeed, 1);
                hash(d, primeSeed, c1, 0);
                inc(primeSeed, 1);
                primeGenCounter++;
                long c64 = ((long) (((extract32(c0) ^ extract32(c1)) & ((-1) >>> (32 - length))) | (1 << (length - 1)) | 1)) & BodyPartID.bodyIdMax;
                if (isPrime32(c64)) {
                    return new STOutput(BigInteger.valueOf(c64), primeSeed, primeGenCounter);
                }
            } while (primeGenCounter <= 4 * length);
            throw new IllegalStateException("Too many iterations in Shawe-Taylor Random_Prime Routine");
        }
        STOutput rec = implSTRandomPrime(d, (length + 3) / 2, primeSeed);
        BigInteger c2 = rec.getPrime();
        byte[] primeSeed2 = rec.getPrimeSeed();
        int primeGenCounter2 = rec.getPrimeGenCounter();
        int outlen = 8 * dLen;
        int iterations = (length - 1) / outlen;
        BigInteger x = hashGen(d, primeSeed2, iterations + 1);
        BigInteger x2 = x.mod(ONE.shiftLeft(length - 1)).setBit(length - 1);
        BigInteger c0x2 = c2.shiftLeft(1);
        BigInteger tx2 = x2.subtract(ONE).divide(c0x2).add(ONE).shiftLeft(1);
        int dt = 0;
        BigInteger bigIntegerAdd = tx2.multiply(c2).add(ONE);
        while (true) {
            BigInteger c = bigIntegerAdd;
            if (c.bitLength() > length) {
                tx2 = ONE.shiftLeft(length - 1).subtract(ONE).divide(c0x2).add(ONE).shiftLeft(1);
                c = tx2.multiply(c2).add(ONE);
            }
            primeGenCounter2++;
            if (!implHasAnySmallFactors(c)) {
                BigInteger a = hashGen(d, primeSeed2, iterations + 1);
                BigInteger a2 = a.mod(c.subtract(THREE)).add(TWO);
                tx2 = tx2.add(BigInteger.valueOf(dt));
                dt = 0;
                BigInteger z = a2.modPow(tx2, c);
                if (c.gcd(z.subtract(ONE)).equals(ONE) && z.modPow(c2, c).equals(ONE)) {
                    return new STOutput(c, primeSeed2, primeGenCounter2);
                }
            } else {
                inc(primeSeed2, iterations + 1);
            }
            if (primeGenCounter2 >= (4 * length) + primeGenCounter2) {
                throw new IllegalStateException("Too many iterations in Shawe-Taylor Random_Prime Routine");
            }
            dt += 2;
            bigIntegerAdd = c.add(c0x2);
        }
    }

    private static int extract32(byte[] bs) {
        int result = 0;
        int count = Math.min(4, bs.length);
        for (int i = 0; i < count; i++) {
            int b = bs[bs.length - (i + 1)] & 255;
            result |= b << (8 * i);
        }
        return result;
    }

    private static void hash(Digest d, byte[] input, byte[] output, int outPos) {
        d.update(input, 0, input.length);
        d.doFinal(output, outPos);
    }

    private static BigInteger hashGen(Digest d, byte[] seed, int count) {
        int dLen = d.getDigestSize();
        int pos = count * dLen;
        byte[] buf = new byte[pos];
        for (int i = 0; i < count; i++) {
            pos -= dLen;
            hash(d, seed, buf, pos);
            inc(seed, 1);
        }
        return new BigInteger(1, buf);
    }

    private static void inc(byte[] seed, int c) {
        int pos = seed.length;
        while (c > 0) {
            pos--;
            if (pos >= 0) {
                int c2 = c + (seed[pos] & 255);
                seed[pos] = (byte) c2;
                c = c2 >>> 8;
            } else {
                return;
            }
        }
    }

    private static boolean isPrime32(long x) {
        if ((x >>> 32) != 0) {
            throw new IllegalArgumentException("Size limit exceeded");
        }
        if (x <= 5) {
            return x == 2 || x == 3 || x == 5;
        }
        if ((x & 1) == 0 || x % 3 == 0 || x % 5 == 0) {
            return false;
        }
        long[] ds = {1, 7, 11, 13, 17, 19, 23, 29};
        long base = 0;
        int pos = 1;
        while (true) {
            if (pos < ds.length) {
                long d = base + ds[pos];
                if (x % d == 0) {
                    return x < 30;
                }
                pos++;
            } else {
                base += 30;
                if (base * base < x) {
                    pos = 0;
                } else {
                    return true;
                }
            }
        }
    }
}
