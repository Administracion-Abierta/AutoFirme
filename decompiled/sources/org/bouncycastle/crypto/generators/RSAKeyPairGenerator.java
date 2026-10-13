package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.math.Primes;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/generators/RSAKeyPairGenerator.class */
public class RSAKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private RSAKeyGenerationParameters param;

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.param = (RSAKeyGenerationParameters) keyGenerationParameters;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("RSAKeyGen", ConstraintUtils.bitsOfSecurityForFF(keyGenerationParameters.getStrength()), null, CryptoServicePurpose.KEYGEN));
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        BigInteger bigIntegerChooseRandomPrime;
        BigInteger bigIntegerMultiply;
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = null;
        boolean z = false;
        int strength = this.param.getStrength();
        int i = (strength + 1) / 2;
        int i2 = strength - i;
        int i3 = (strength / 2) - 100;
        if (i3 < strength / 3) {
            i3 = strength / 3;
        }
        int i4 = strength >> 2;
        BigInteger bigIntegerPow = BigInteger.valueOf(2L).pow(strength / 2);
        BigInteger bigIntegerShiftLeft = ONE.shiftLeft(strength - 1);
        BigInteger bigIntegerShiftLeft2 = ONE.shiftLeft(i3);
        while (!z) {
            BigInteger publicExponent = this.param.getPublicExponent();
            BigInteger bigIntegerChooseRandomPrime2 = chooseRandomPrime(i, publicExponent, bigIntegerShiftLeft);
            while (true) {
                bigIntegerChooseRandomPrime = chooseRandomPrime(i2, publicExponent, bigIntegerShiftLeft);
                BigInteger bigIntegerAbs = bigIntegerChooseRandomPrime.subtract(bigIntegerChooseRandomPrime2).abs();
                if (bigIntegerAbs.bitLength() >= i3 && bigIntegerAbs.compareTo(bigIntegerShiftLeft2) > 0) {
                    bigIntegerMultiply = bigIntegerChooseRandomPrime2.multiply(bigIntegerChooseRandomPrime);
                    if (bigIntegerMultiply.bitLength() == strength) {
                        if (WNafUtil.getNafWeight(bigIntegerMultiply) >= i4) {
                            break;
                        }
                        bigIntegerChooseRandomPrime2 = chooseRandomPrime(i, publicExponent, bigIntegerShiftLeft);
                    } else {
                        bigIntegerChooseRandomPrime2 = bigIntegerChooseRandomPrime2.max(bigIntegerChooseRandomPrime);
                    }
                }
            }
            if (bigIntegerChooseRandomPrime2.compareTo(bigIntegerChooseRandomPrime) < 0) {
                BigInteger bigInteger = bigIntegerChooseRandomPrime2;
                bigIntegerChooseRandomPrime2 = bigIntegerChooseRandomPrime;
                bigIntegerChooseRandomPrime = bigInteger;
            }
            BigInteger bigIntegerSubtract = bigIntegerChooseRandomPrime2.subtract(ONE);
            BigInteger bigIntegerSubtract2 = bigIntegerChooseRandomPrime.subtract(ONE);
            BigInteger bigIntegerModInverse = publicExponent.modInverse(bigIntegerSubtract.divide(bigIntegerSubtract.gcd(bigIntegerSubtract2)).multiply(bigIntegerSubtract2));
            if (bigIntegerModInverse.compareTo(bigIntegerPow) > 0) {
                z = true;
                asymmetricCipherKeyPair = new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new RSAKeyParameters(false, bigIntegerMultiply, publicExponent, true), (AsymmetricKeyParameter) new RSAPrivateCrtKeyParameters(bigIntegerMultiply, publicExponent, bigIntegerModInverse, bigIntegerChooseRandomPrime2, bigIntegerChooseRandomPrime, bigIntegerModInverse.remainder(bigIntegerSubtract), bigIntegerModInverse.remainder(bigIntegerSubtract2), BigIntegers.modOddInverse(bigIntegerChooseRandomPrime2, bigIntegerChooseRandomPrime), true));
            }
        }
        return asymmetricCipherKeyPair;
    }

    protected BigInteger chooseRandomPrime(int i, BigInteger bigInteger, BigInteger bigInteger2) {
        for (int i2 = 0; i2 != 5 * i; i2++) {
            BigInteger bigIntegerCreateRandomPrime = BigIntegers.createRandomPrime(i, 1, this.param.getRandom());
            if (!bigIntegerCreateRandomPrime.mod(bigInteger).equals(ONE) && bigIntegerCreateRandomPrime.multiply(bigIntegerCreateRandomPrime).compareTo(bigInteger2) >= 0 && isProbablePrime(bigIntegerCreateRandomPrime) && bigInteger.gcd(bigIntegerCreateRandomPrime.subtract(ONE)).equals(ONE)) {
                return bigIntegerCreateRandomPrime;
            }
        }
        throw new IllegalStateException("unable to generate prime number for RSA key");
    }

    protected boolean isProbablePrime(BigInteger bigInteger) {
        return !Primes.hasAnySmallFactors(bigInteger) && Primes.isMRProbablePrime(bigInteger, this.param.getRandom(), getNumberOfIterations(bigInteger.bitLength(), this.param.getCertainty()));
    }

    private static int getNumberOfIterations(int i, int i2) {
        if (i >= 1536) {
            if (i2 <= 100) {
                return 3;
            }
            if (i2 <= 128) {
                return 4;
            }
            return 4 + (((i2 - 128) + 1) / 2);
        }
        if (i >= 1024) {
            if (i2 <= 100) {
                return 4;
            }
            if (i2 <= 112) {
                return 5;
            }
            return 5 + (((i2 - 112) + 1) / 2);
        }
        if (i < 512) {
            if (i2 <= 80) {
                return 40;
            }
            return 40 + (((i2 - 80) + 1) / 2);
        }
        if (i2 <= 80) {
            return 5;
        }
        if (i2 <= 100) {
            return 7;
        }
        return 7 + (((i2 - 100) + 1) / 2);
    }
}
