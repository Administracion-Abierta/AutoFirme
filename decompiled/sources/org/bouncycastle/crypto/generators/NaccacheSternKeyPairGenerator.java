package org.bouncycastle.crypto.generators;

import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Vector;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.NaccacheSternKeyGenerationParameters;
import org.bouncycastle.crypto.params.NaccacheSternKeyParameters;
import org.bouncycastle.crypto.params.NaccacheSternPrivateKeyParameters;
import org.bouncycastle.util.BigIntegers;
import org.java_websocket.WebSocketImpl;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/generators/NaccacheSternKeyPairGenerator.class */
public class NaccacheSternKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private NaccacheSternKeyGenerationParameters param;
    private static int[] smallPrimes = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, WinError.ERROR_FILE_TOO_LARGE, 227, WinError.ERROR_PIPE_LOCAL, WinError.ERROR_PIPE_NOT_CONNECTED, 239, 241, 251, 257, 263, TIFF.TAG_DOCUMENT_NAME, TIFF.TAG_MAKE, 277, 281, TIFF.TAG_Y_RESOLUTION, TIFF.TAG_GROUP4OPTIONS, 307, 311, 313, 317, 331, 337, TIFF.TAG_JPEG_TABLES, 349, WinError.ERROR_MAX_SESSIONS_REACHED, 359, 367, 373, 379, 383, 389, 397, WinError.ERROR_THREAD_MODE_NOT_BACKGROUND, 409, 419, 421, 431, 433, 439, WebSocketImpl.DEFAULT_WSS_PORT, 449, 457, 461, 463, 467, 479, WinError.ERROR_INVALID_ADDRESS, 491, 499, 503, 509, TIFF.TAG_OLD_JPEG_AC_TABLES, 523, WinError.ERROR_TIMER_NOT_CANCELED, 547, 557};
    private static final BigInteger ONE = BigInteger.valueOf(1);

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.param = (NaccacheSternKeyGenerationParameters) keyGenerationParameters;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("NaccacheStern KeyGen", ConstraintUtils.bitsOfSecurityForFF(keyGenerationParameters.getStrength()), keyGenerationParameters, CryptoServicePurpose.KEYGEN));
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        BigInteger bigIntegerGeneratePrime;
        BigInteger bigIntegerAdd;
        BigInteger bigIntegerGeneratePrime2;
        BigInteger bigIntegerAdd2;
        BigInteger bigIntegerMultiply;
        BigInteger bigIntegerMod;
        BigInteger bigIntegerCreateRandomPrime;
        int strength = this.param.getStrength();
        SecureRandom random = this.param.getRandom();
        int certainty = this.param.getCertainty();
        boolean zIsDebug = this.param.isDebug();
        if (zIsDebug) {
            System.out.println("Fetching first " + this.param.getCntSmallPrimes() + " primes.");
        }
        Vector vectorPermuteList = permuteList(findFirstPrimes(this.param.getCntSmallPrimes()), random);
        BigInteger bigIntegerMultiply2 = ONE;
        BigInteger bigIntegerMultiply3 = ONE;
        for (int i = 0; i < vectorPermuteList.size() / 2; i++) {
            bigIntegerMultiply2 = bigIntegerMultiply2.multiply((BigInteger) vectorPermuteList.elementAt(i));
        }
        for (int size = vectorPermuteList.size() / 2; size < vectorPermuteList.size(); size++) {
            bigIntegerMultiply3 = bigIntegerMultiply3.multiply((BigInteger) vectorPermuteList.elementAt(size));
        }
        BigInteger bigIntegerMultiply4 = bigIntegerMultiply2.multiply(bigIntegerMultiply3);
        int iBitLength = (strength - bigIntegerMultiply4.bitLength()) - 48;
        BigInteger bigIntegerGeneratePrime3 = generatePrime((iBitLength / 2) + 1, certainty, random);
        BigInteger bigIntegerGeneratePrime4 = generatePrime((iBitLength / 2) + 1, certainty, random);
        long j = 0;
        if (zIsDebug) {
            System.out.println("generating p and q");
        }
        BigInteger bigIntegerShiftLeft = bigIntegerGeneratePrime3.multiply(bigIntegerMultiply2).shiftLeft(1);
        BigInteger bigIntegerShiftLeft2 = bigIntegerGeneratePrime4.multiply(bigIntegerMultiply3).shiftLeft(1);
        while (true) {
            j++;
            bigIntegerGeneratePrime = generatePrime(24, certainty, random);
            bigIntegerAdd = bigIntegerGeneratePrime.multiply(bigIntegerShiftLeft).add(ONE);
            if (bigIntegerAdd.isProbablePrime(certainty)) {
                while (true) {
                    bigIntegerGeneratePrime2 = generatePrime(24, certainty, random);
                    if (!bigIntegerGeneratePrime.equals(bigIntegerGeneratePrime2)) {
                        bigIntegerAdd2 = bigIntegerGeneratePrime2.multiply(bigIntegerShiftLeft2).add(ONE);
                        if (bigIntegerAdd2.isProbablePrime(certainty)) {
                            break;
                        }
                    }
                }
                if (BigIntegers.modOddIsCoprime(bigIntegerGeneratePrime.multiply(bigIntegerGeneratePrime2), bigIntegerMultiply4)) {
                    bigIntegerMultiply = bigIntegerAdd.multiply(bigIntegerAdd2);
                    if (bigIntegerMultiply.bitLength() >= strength) {
                        break;
                    }
                    if (zIsDebug) {
                        System.out.println("key size too small. Should be " + strength + " but is actually " + bigIntegerAdd.multiply(bigIntegerAdd2).bitLength());
                    }
                } else {
                    continue;
                }
            }
        }
        if (zIsDebug) {
            System.out.println("needed " + j + " tries to generate p and q.");
        }
        BigInteger bigIntegerMultiply5 = bigIntegerAdd.subtract(ONE).multiply(bigIntegerAdd2.subtract(ONE));
        long j2 = 0;
        if (zIsDebug) {
            System.out.println("generating g");
        }
        while (true) {
            Vector vector = new Vector();
            for (int i2 = 0; i2 != vectorPermuteList.size(); i2++) {
                BigInteger bigIntegerDivide = bigIntegerMultiply5.divide((BigInteger) vectorPermuteList.elementAt(i2));
                do {
                    j2++;
                    bigIntegerCreateRandomPrime = BigIntegers.createRandomPrime(strength, certainty, random);
                } while (bigIntegerCreateRandomPrime.modPow(bigIntegerDivide, bigIntegerMultiply).equals(ONE));
                vector.addElement(bigIntegerCreateRandomPrime);
            }
            bigIntegerMod = ONE;
            for (int i3 = 0; i3 < vectorPermuteList.size(); i3++) {
                bigIntegerMod = bigIntegerMod.multiply(((BigInteger) vector.elementAt(i3)).modPow(bigIntegerMultiply4.divide((BigInteger) vectorPermuteList.elementAt(i3)), bigIntegerMultiply)).mod(bigIntegerMultiply);
            }
            boolean z = false;
            for (int i4 = 0; i4 < vectorPermuteList.size(); i4++) {
                if (bigIntegerMod.modPow(bigIntegerMultiply5.divide((BigInteger) vectorPermuteList.elementAt(i4)), bigIntegerMultiply).equals(ONE)) {
                    if (zIsDebug) {
                        System.out.println("g has order phi(n)/" + vectorPermuteList.elementAt(i4) + "\n g: " + bigIntegerMod);
                    }
                    z = true;
                    break;
                }
            }
            if (!z) {
                if (!bigIntegerMod.modPow(bigIntegerMultiply5.divide(BigInteger.valueOf(4L)), bigIntegerMultiply).equals(ONE)) {
                    if (!bigIntegerMod.modPow(bigIntegerMultiply5.divide(bigIntegerGeneratePrime), bigIntegerMultiply).equals(ONE)) {
                        if (!bigIntegerMod.modPow(bigIntegerMultiply5.divide(bigIntegerGeneratePrime2), bigIntegerMultiply).equals(ONE)) {
                            if (!bigIntegerMod.modPow(bigIntegerMultiply5.divide(bigIntegerGeneratePrime3), bigIntegerMultiply).equals(ONE)) {
                                if (!bigIntegerMod.modPow(bigIntegerMultiply5.divide(bigIntegerGeneratePrime4), bigIntegerMultiply).equals(ONE)) {
                                    break;
                                }
                                if (zIsDebug) {
                                    System.out.println("g has order phi(n)/b\n g: " + bigIntegerMod);
                                }
                            } else if (zIsDebug) {
                                System.out.println("g has order phi(n)/a\n g: " + bigIntegerMod);
                            }
                        } else if (zIsDebug) {
                            System.out.println("g has order phi(n)/q'\n g: " + bigIntegerMod);
                        }
                    } else if (zIsDebug) {
                        System.out.println("g has order phi(n)/p'\n g: " + bigIntegerMod);
                    }
                } else if (zIsDebug) {
                    System.out.println("g has order phi(n)/4\n g:" + bigIntegerMod);
                }
            }
        }
        if (zIsDebug) {
            System.out.println("needed " + j2 + " tries to generate g");
            System.out.println();
            System.out.println("found new NaccacheStern cipher variables:");
            System.out.println("smallPrimes: " + vectorPermuteList);
            System.out.println("sigma:...... " + bigIntegerMultiply4 + " (" + bigIntegerMultiply4.bitLength() + " bits)");
            System.out.println("a:.......... " + bigIntegerGeneratePrime3);
            System.out.println("b:.......... " + bigIntegerGeneratePrime4);
            System.out.println("p':......... " + bigIntegerGeneratePrime);
            System.out.println("q':......... " + bigIntegerGeneratePrime2);
            System.out.println("p:.......... " + bigIntegerAdd);
            System.out.println("q:.......... " + bigIntegerAdd2);
            System.out.println("n:.......... " + bigIntegerMultiply);
            System.out.println("phi(n):..... " + bigIntegerMultiply5);
            System.out.println("g:.......... " + bigIntegerMod);
            System.out.println();
        }
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new NaccacheSternKeyParameters(false, bigIntegerMod, bigIntegerMultiply, bigIntegerMultiply4.bitLength()), (AsymmetricKeyParameter) new NaccacheSternPrivateKeyParameters(bigIntegerMod, bigIntegerMultiply, bigIntegerMultiply4.bitLength(), vectorPermuteList, bigIntegerMultiply5));
    }

    private static BigInteger generatePrime(int i, int i2, SecureRandom secureRandom) {
        BigInteger bigIntegerCreateRandomPrime = BigIntegers.createRandomPrime(i, i2, secureRandom);
        while (true) {
            BigInteger bigInteger = bigIntegerCreateRandomPrime;
            if (bigInteger.bitLength() == i) {
                return bigInteger;
            }
            bigIntegerCreateRandomPrime = BigIntegers.createRandomPrime(i, i2, secureRandom);
        }
    }

    private static Vector permuteList(Vector vector, SecureRandom secureRandom) {
        Vector vector2 = new Vector();
        Vector vector3 = new Vector();
        for (int i = 0; i < vector.size(); i++) {
            vector3.addElement(vector.elementAt(i));
        }
        vector2.addElement(vector3.elementAt(0));
        vector3.removeElementAt(0);
        while (vector3.size() != 0) {
            vector2.insertElementAt(vector3.elementAt(0), getInt(secureRandom, vector2.size() + 1));
            vector3.removeElementAt(0);
        }
        return vector2;
    }

    private static int getInt(SecureRandom secureRandom, int i) {
        int iNextInt;
        int i2;
        if ((i & (-i)) == i) {
            return (int) ((((long) i) * ((long) (secureRandom.nextInt() & WinNT.MAXLONG))) >> 31);
        }
        do {
            iNextInt = secureRandom.nextInt() & WinNT.MAXLONG;
            i2 = iNextInt % i;
        } while ((iNextInt - i2) + (i - 1) < 0);
        return i2;
    }

    private static Vector findFirstPrimes(int i) {
        Vector vector = new Vector(i);
        for (int i2 = 0; i2 != i; i2++) {
            vector.addElement(BigInteger.valueOf(smallPrimes[i2]));
        }
        return vector;
    }
}
