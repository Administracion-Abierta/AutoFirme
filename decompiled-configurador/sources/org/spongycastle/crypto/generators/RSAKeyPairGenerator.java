package org.spongycastle.crypto.generators;

import java.math.BigInteger;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.RSAKeyGenerationParameters;
import org.spongycastle.crypto.params.RSAKeyParameters;
import org.spongycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.spongycastle.crypto.tls.AlertDescription;
import org.spongycastle.math.Primes;
import org.spongycastle.math.ec.WNafUtil;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/generators/RSAKeyPairGenerator.class */
public class RSAKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private RSAKeyGenerationParameters param;
    private int iterations;

    @Override // org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters param) {
        this.param = (RSAKeyGenerationParameters) param;
        this.iterations = getNumberOfIterations(this.param.getStrength(), this.param.getCertainty());
    }

    @Override // org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        BigInteger q;
        BigInteger n;
        AsymmetricCipherKeyPair result = null;
        boolean done = false;
        int strength = this.param.getStrength();
        int pbitlength = (strength + 1) / 2;
        int qbitlength = strength - pbitlength;
        int mindiffbits = (strength / 2) - 100;
        if (mindiffbits < strength / 3) {
            mindiffbits = strength / 3;
        }
        int minWeight = strength >> 2;
        BigInteger dLowerBound = BigInteger.valueOf(2L).pow(strength / 2);
        BigInteger squaredBound = ONE.shiftLeft(strength - 1);
        BigInteger minDiff = ONE.shiftLeft(mindiffbits);
        while (!done) {
            BigInteger e = this.param.getPublicExponent();
            BigInteger p = chooseRandomPrime(pbitlength, e, squaredBound);
            while (true) {
                q = chooseRandomPrime(qbitlength, e, squaredBound);
                BigInteger diff = q.subtract(p).abs();
                if (diff.bitLength() >= mindiffbits && diff.compareTo(minDiff) > 0) {
                    n = p.multiply(q);
                    if (n.bitLength() != strength) {
                        p = p.max(q);
                    } else {
                        if (WNafUtil.getNafWeight(n) >= minWeight) {
                            break;
                        }
                        p = chooseRandomPrime(pbitlength, e, squaredBound);
                    }
                }
            }
            if (p.compareTo(q) < 0) {
                BigInteger gcd = p;
                p = q;
                q = gcd;
            }
            BigInteger pSub1 = p.subtract(ONE);
            BigInteger qSub1 = q.subtract(ONE);
            BigInteger gcd2 = pSub1.gcd(qSub1);
            BigInteger lcm = pSub1.divide(gcd2).multiply(qSub1);
            BigInteger d = e.modInverse(lcm);
            if (d.compareTo(dLowerBound) > 0) {
                done = true;
                BigInteger dP = d.remainder(pSub1);
                BigInteger dQ = d.remainder(qSub1);
                BigInteger qInv = q.modInverse(p);
                result = new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new RSAKeyParameters(false, n, e), (AsymmetricKeyParameter) new RSAPrivateCrtKeyParameters(n, e, d, p, q, dP, dQ, qInv));
            }
        }
        return result;
    }

    protected BigInteger chooseRandomPrime(int bitlength, BigInteger e, BigInteger sqrdBound) {
        for (int i = 0; i != 5 * bitlength; i++) {
            BigInteger p = new BigInteger(bitlength, 1, this.param.getRandom());
            if (!p.mod(e).equals(ONE) && p.multiply(p).compareTo(sqrdBound) >= 0 && isProbablePrime(p) && e.gcd(p.subtract(ONE)).equals(ONE)) {
                return p;
            }
        }
        throw new IllegalStateException("unable to generate prime number for RSA key");
    }

    protected boolean isProbablePrime(BigInteger x) {
        return !Primes.hasAnySmallFactors(x) && Primes.isMRProbablePrime(x, this.param.getRandom(), this.iterations);
    }

    private static int getNumberOfIterations(int bits, int certainty) {
        if (bits >= 1536) {
            if (certainty <= 100) {
                return 3;
            }
            if (certainty <= 128) {
                return 4;
            }
            return 4 + (((certainty - 128) + 1) / 2);
        }
        if (bits >= 1024) {
            if (certainty <= 100) {
                return 4;
            }
            if (certainty <= 112) {
                return 5;
            }
            return 5 + (((certainty - AlertDescription.unrecognized_name) + 1) / 2);
        }
        if (bits >= 512) {
            if (certainty <= 80) {
                return 5;
            }
            if (certainty <= 100) {
                return 7;
            }
            return 7 + (((certainty - 100) + 1) / 2);
        }
        if (certainty <= 80) {
            return 40;
        }
        return 40 + (((certainty - 80) + 1) / 2);
    }
}
