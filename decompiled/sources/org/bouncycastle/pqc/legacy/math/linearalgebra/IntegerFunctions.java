package org.bouncycastle.pqc.legacy.math.linearalgebra;

import com.sun.jna.platform.win32.WinError;
import com.twelvemonkeys.imageio.metadata.psd.PSD;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.BigIntegers;
import org.java_websocket.WebSocketImpl;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/linearalgebra/IntegerFunctions.class */
public final class IntegerFunctions {
    private static final long SMALL_PRIME_PRODUCT = 152125131763605L;
    private static final BigInteger ZERO = BigInteger.valueOf(0);
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger FOUR = BigInteger.valueOf(4);
    private static final int[] SMALL_PRIMES = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41};
    private static SecureRandom sr = null;
    private static final int[] jacobiTable = {0, 1, 0, -1, 0, -1, 0, 1};

    private IntegerFunctions() {
    }

    public static int jacobi(BigInteger bigInteger, BigInteger bigInteger2) {
        long j = 1;
        if (bigInteger2.equals(ZERO)) {
            return bigInteger.abs().equals(ONE) ? 1 : 0;
        }
        if (!bigInteger.testBit(0) && !bigInteger2.testBit(0)) {
            return 0;
        }
        BigInteger bigIntegerSubtract = bigInteger;
        BigInteger bigIntegerDivide = bigInteger2;
        if (bigIntegerDivide.signum() == -1) {
            bigIntegerDivide = bigIntegerDivide.negate();
            if (bigIntegerSubtract.signum() == -1) {
                j = -1;
            }
        }
        BigInteger bigIntegerAdd = ZERO;
        while (!bigIntegerDivide.testBit(0)) {
            bigIntegerAdd = bigIntegerAdd.add(ONE);
            bigIntegerDivide = bigIntegerDivide.divide(TWO);
        }
        if (bigIntegerAdd.testBit(0)) {
            j *= (long) jacobiTable[bigIntegerSubtract.intValue() & 7];
        }
        if (bigIntegerSubtract.signum() < 0) {
            if (bigIntegerDivide.testBit(1)) {
                j = -j;
            }
            bigIntegerSubtract = bigIntegerSubtract.negate();
        }
        while (bigIntegerSubtract.signum() != 0) {
            BigInteger bigIntegerAdd2 = ZERO;
            while (!bigIntegerSubtract.testBit(0)) {
                bigIntegerAdd2 = bigIntegerAdd2.add(ONE);
                bigIntegerSubtract = bigIntegerSubtract.divide(TWO);
            }
            if (bigIntegerAdd2.testBit(0)) {
                j *= (long) jacobiTable[bigIntegerDivide.intValue() & 7];
            }
            if (bigIntegerSubtract.compareTo(bigIntegerDivide) < 0) {
                BigInteger bigInteger3 = bigIntegerSubtract;
                bigIntegerSubtract = bigIntegerDivide;
                bigIntegerDivide = bigInteger3;
                if (bigIntegerSubtract.testBit(1) && bigIntegerDivide.testBit(1)) {
                    j = -j;
                }
            }
            bigIntegerSubtract = bigIntegerSubtract.subtract(bigIntegerDivide);
        }
        if (bigIntegerDivide.equals(ONE)) {
            return (int) j;
        }
        return 0;
    }

    public static int gcd(int i, int i2) {
        return BigInteger.valueOf(i).gcd(BigInteger.valueOf(i2)).intValue();
    }

    public static int[] extGCD(int i, int i2) {
        BigInteger[] bigIntegerArrExtgcd = extgcd(BigInteger.valueOf(i), BigInteger.valueOf(i2));
        return new int[]{bigIntegerArrExtgcd[0].intValue(), bigIntegerArrExtgcd[1].intValue(), bigIntegerArrExtgcd[2].intValue()};
    }

    public static BigInteger divideAndRound(BigInteger bigInteger, BigInteger bigInteger2) {
        if (bigInteger.signum() < 0) {
            return divideAndRound(bigInteger.negate(), bigInteger2).negate();
        }
        return bigInteger2.signum() < 0 ? divideAndRound(bigInteger, bigInteger2.negate()).negate() : bigInteger.shiftLeft(1).add(bigInteger2).divide(bigInteger2.shiftLeft(1));
    }

    public static BigInteger[] divideAndRound(BigInteger[] bigIntegerArr, BigInteger bigInteger) {
        BigInteger[] bigIntegerArr2 = new BigInteger[bigIntegerArr.length];
        for (int i = 0; i < bigIntegerArr.length; i++) {
            bigIntegerArr2[i] = divideAndRound(bigIntegerArr[i], bigInteger);
        }
        return bigIntegerArr2;
    }

    public static int ceilLog(BigInteger bigInteger) {
        int i = 0;
        BigInteger bigIntegerShiftLeft = ONE;
        while (true) {
            BigInteger bigInteger2 = bigIntegerShiftLeft;
            if (bigInteger2.compareTo(bigInteger) >= 0) {
                return i;
            }
            i++;
            bigIntegerShiftLeft = bigInteger2.shiftLeft(1);
        }
    }

    public static int ceilLog(int i) {
        int i2 = 0;
        int i3 = 1;
        while (i3 < i) {
            i3 <<= 1;
            i2++;
        }
        return i2;
    }

    public static int ceilLog256(int i) {
        if (i == 0) {
            return 1;
        }
        int i2 = 0;
        for (int i3 = i < 0 ? -i : i; i3 > 0; i3 >>>= 8) {
            i2++;
        }
        return i2;
    }

    public static int ceilLog256(long j) {
        if (j == 0) {
            return 1;
        }
        int i = 0;
        for (long j2 = j < 0 ? -j : j; j2 > 0; j2 >>>= 8) {
            i++;
        }
        return i;
    }

    public static int floorLog(BigInteger bigInteger) {
        int i = -1;
        BigInteger bigIntegerShiftLeft = ONE;
        while (true) {
            BigInteger bigInteger2 = bigIntegerShiftLeft;
            if (bigInteger2.compareTo(bigInteger) > 0) {
                return i;
            }
            i++;
            bigIntegerShiftLeft = bigInteger2.shiftLeft(1);
        }
    }

    public static int floorLog(int i) {
        int i2 = 0;
        if (i <= 0) {
            return -1;
        }
        int i3 = i;
        while (true) {
            int i4 = i3 >>> 1;
            if (i4 <= 0) {
                return i2;
            }
            i2++;
            i3 = i4;
        }
    }

    public static int maxPower(int i) {
        int i2 = 0;
        if (i != 0) {
            int i3 = 1;
            while (true) {
                int i4 = i3;
                if ((i & i4) != 0) {
                    break;
                }
                i2++;
                i3 = i4 << 1;
            }
        }
        return i2;
    }

    public static int bitCount(int i) {
        int i2 = 0;
        while (i != 0) {
            i2 += i & 1;
            i >>>= 1;
        }
        return i2;
    }

    public static int order(int i, int i2) {
        int i3 = i % i2;
        int i4 = 1;
        if (i3 == 0) {
            throw new IllegalArgumentException(i + " is not an element of Z/(" + i2 + "Z)^*; it is not meaningful to compute its order.");
        }
        while (i3 != 1) {
            i3 = (i3 * i) % i2;
            if (i3 < 0) {
                i3 += i2;
            }
            i4++;
        }
        return i4;
    }

    public static BigInteger reduceInto(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3) {
        return bigInteger.subtract(bigInteger2).mod(bigInteger3.subtract(bigInteger2)).add(bigInteger2);
    }

    public static int pow(int i, int i2) {
        int i3 = 1;
        while (i2 > 0) {
            if ((i2 & 1) == 1) {
                i3 *= i;
            }
            i *= i;
            i2 >>>= 1;
        }
        return i3;
    }

    public static long pow(long j, int i) {
        long j2 = 1;
        while (i > 0) {
            if ((i & 1) == 1) {
                j2 *= j;
            }
            j *= j;
            i >>>= 1;
        }
        return j2;
    }

    public static int modPow(int i, int i2, int i3) {
        if (i3 <= 0 || ((long) i3) * ((long) i3) > 2147483647L || i2 < 0) {
            return 0;
        }
        int i4 = 1;
        int i5 = ((i % i3) + i3) % i3;
        while (i2 > 0) {
            if ((i2 & 1) == 1) {
                i4 = (i4 * i5) % i3;
            }
            i5 = (i5 * i5) % i3;
            i2 >>>= 1;
        }
        return i4;
    }

    public static BigInteger[] extgcd(BigInteger bigInteger, BigInteger bigInteger2) {
        BigInteger bigInteger3 = ONE;
        BigInteger bigIntegerDivide = ZERO;
        BigInteger bigInteger4 = bigInteger;
        if (bigInteger2.signum() != 0) {
            BigInteger bigInteger5 = ZERO;
            BigInteger bigInteger6 = bigInteger2;
            while (true) {
                BigInteger bigInteger7 = bigInteger6;
                if (bigInteger7.signum() == 0) {
                    break;
                }
                BigInteger[] bigIntegerArrDivideAndRemainder = bigInteger4.divideAndRemainder(bigInteger7);
                BigInteger bigInteger8 = bigIntegerArrDivideAndRemainder[0];
                BigInteger bigInteger9 = bigIntegerArrDivideAndRemainder[1];
                BigInteger bigIntegerSubtract = bigInteger3.subtract(bigInteger8.multiply(bigInteger5));
                bigInteger3 = bigInteger5;
                bigInteger4 = bigInteger7;
                bigInteger5 = bigIntegerSubtract;
                bigInteger6 = bigInteger9;
            }
            bigIntegerDivide = bigInteger4.subtract(bigInteger.multiply(bigInteger3)).divide(bigInteger2);
        }
        return new BigInteger[]{bigInteger4, bigInteger3, bigIntegerDivide};
    }

    public static BigInteger leastCommonMultiple(BigInteger[] bigIntegerArr) {
        int length = bigIntegerArr.length;
        BigInteger bigIntegerDivide = bigIntegerArr[0];
        for (int i = 1; i < length; i++) {
            bigIntegerDivide = bigIntegerDivide.multiply(bigIntegerArr[i]).divide(bigIntegerDivide.gcd(bigIntegerArr[i]));
        }
        return bigIntegerDivide;
    }

    public static long mod(long j, long j2) {
        long j3 = j % j2;
        if (j3 < 0) {
            j3 += j2;
        }
        return j3;
    }

    public static int modInverse(int i, int i2) {
        return BigInteger.valueOf(i).modInverse(BigInteger.valueOf(i2)).intValue();
    }

    public static long modInverse(long j, long j2) {
        return BigInteger.valueOf(j).modInverse(BigInteger.valueOf(j2)).longValue();
    }

    public static int isPower(int i, int i2) {
        if (i <= 0) {
            return -1;
        }
        int i3 = 0;
        int i4 = i;
        while (i4 > 1) {
            if (i4 % i2 != 0) {
                return -1;
            }
            i4 /= i2;
            i3++;
        }
        return i3;
    }

    public static int leastDiv(int i) {
        if (i < 0) {
            i = -i;
        }
        if (i == 0) {
            return 1;
        }
        if ((i & 1) == 0) {
            return 2;
        }
        for (int i2 = 3; i2 <= i / i2; i2 += 2) {
            if (i % i2 == 0) {
                return i2;
            }
        }
        return i;
    }

    public static boolean isPrime(int i) {
        if (i < 2) {
            return false;
        }
        if (i == 2) {
            return true;
        }
        if ((i & 1) == 0) {
            return false;
        }
        if (i < 42) {
            for (int i2 = 0; i2 < SMALL_PRIMES.length; i2++) {
                if (i == SMALL_PRIMES[i2]) {
                    return true;
                }
            }
        }
        if (i % 3 == 0 || i % 5 == 0 || i % 7 == 0 || i % 11 == 0 || i % 13 == 0 || i % 17 == 0 || i % 19 == 0 || i % 23 == 0 || i % 29 == 0 || i % 31 == 0 || i % 37 == 0 || i % 41 == 0) {
            return false;
        }
        return BigInteger.valueOf(i).isProbablePrime(20);
    }

    public static boolean passesSmallPrimeTest(BigInteger bigInteger) {
        for (int i : new int[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, WinError.ERROR_FILE_TOO_LARGE, 227, WinError.ERROR_PIPE_LOCAL, WinError.ERROR_PIPE_NOT_CONNECTED, 239, 241, 251, 257, 263, TIFF.TAG_DOCUMENT_NAME, TIFF.TAG_MAKE, 277, 281, TIFF.TAG_Y_RESOLUTION, TIFF.TAG_GROUP4OPTIONS, 307, 311, 313, 317, 331, 337, TIFF.TAG_JPEG_TABLES, 349, WinError.ERROR_MAX_SESSIONS_REACHED, 359, 367, 373, 379, 383, 389, 397, WinError.ERROR_THREAD_MODE_NOT_BACKGROUND, 409, 419, 421, 431, 433, 439, WebSocketImpl.DEFAULT_WSS_PORT, 449, 457, 461, 463, 467, 479, WinError.ERROR_INVALID_ADDRESS, 491, 499, 503, 509, TIFF.TAG_OLD_JPEG_AC_TABLES, 523, WinError.ERROR_TIMER_NOT_CANCELED, 547, 557, WinError.ERROR_INVALID_LDT_OFFSET, WinError.ERROR_SYNCHRONIZATION_REQUIRED, WinError.ERROR_IO_PRIVILEGE_FAILED, 577, 587, WinError.ERROR_VDM_HARD_ERROR, WinError.ERROR_STACK_OVERFLOW_READ, WinError.ERROR_FOUND_OUT_OF_SCOPE, 607, 613, 617, WinError.ERROR_INVALID_HW_PROFILE, WinError.ERROR_FLOAT_MULTIPLE_TRAPS, WinError.ERROR_SYSTEM_SHUTDOWN, 643, 647, WinError.ERROR_SYSTEM_HIVE_TOO_LARGE, 659, 661, WinError.ERROR_PNP_IRQ_TRANSLATION_FAILED, WinError.ERROR_EXTRANEOUS_INFORMATION, WinError.ERROR_PLUGPLAY_QUERY_VETOED, WinError.ERROR_DBG_TERMINATE_THREAD, WinError.ERROR_RXACT_STATE_CREATED, WinError.ERROR_RECEIVE_PARTIAL_EXPEDITED, WinError.ERROR_ALREADY_WIN32, WinError.ERROR_RESUME_HIBERNATION, WinError.ERROR_WAIT_3, WinError.ERROR_ALERTED, WinError.ERROR_VOLUME_MOUNTED, WinError.ERROR_PAGE_FAULT_PAGING_FILE, WinError.ERROR_TRANSLATION_COMPLETE, WinError.ERROR_VOLSNAP_HIBERNATE_READY, WinError.ERROR_COMPRESSION_DISABLED, WinError.ERROR_BAD_ACCESSOR_FLAGS, WinError.ERROR_ABANDON_HIBERFILE, 797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, WinError.ERROR_IO_PENDING, 1009, 1013, WinError.ERROR_NO_LOG_SPACE, WinError.ERROR_CHILD_MUST_BE_VOLATILE, 1031, 1033, PSD.RES_ICC_PROFILE, 1049, WinError.ERROR_DEPENDENT_SERVICES_RUNNING, WinError.ERROR_SERVICE_CANNOT_ACCEPT_CTRL, WinError.ERROR_FAILED_SERVICE_CONTROLLER_CONNECT, WinError.ERROR_SERVICE_LOGON_FAILED, 1087, 1091, 1093, 1097, WinError.ERROR_SETMARK_DETECTED, WinError.ERROR_UNABLE_TO_UNLOAD_MEDIA, WinError.ERROR_IO_DEVICE, WinError.ERROR_FLOPPY_WRONG_CYLINDER, WinError.ERROR_EOM_OVERFLOW, WinError.ERROR_APP_WRONG_OS, WinError.ERROR_RMODE_APP, WinError.ERROR_MAGAZINE_NOT_PRESENT, WinError.ERROR_POINT_NOT_FOUND, WinError.ERROR_JOURNAL_ENTRY_DELETED, 1187, 1193, WinError.ERROR_CONNECTION_UNAVAIL, WinError.ERROR_INVALID_SERVICENAME, WinError.ERROR_INVALID_MESSAGENAME, WinError.ERROR_CANCELLED, WinError.ERROR_CONNECTION_INVALID, WinError.ERROR_NETWORK_UNREACHABLE, WinError.ERROR_RETRY, WinError.ERROR_NO_SUCH_SITE, WinError.ERROR_APPHELP_BLOCK, WinError.ERROR_ACCESS_DISABLED_WEBBLADE, WinError.ERROR_RECOVERY_FAILURE, 1283, WinError.ERROR_BEYOND_VDL, WinError.ERROR_DRIVER_PROCESS_TERMINATED, WinError.ERROR_INCOMPATIBLE_SERVICE_PRIVILEGE, WinError.ERROR_SOME_NOT_MAPPED, WinError.ERROR_LOCAL_USER_SESSION_KEY, WinError.ERROR_INVALID_OWNER, WinError.ERROR_NO_SUCH_GROUP, WinError.ERROR_MEMBER_NOT_IN_GROUP, WinError.ERROR_ACCOUNT_RESTRICTION, WinError.ERROR_BAD_DESCRIPTOR_FORMAT, WinError.ERROR_INVALID_LOGON_TYPE, WinError.ERROR_SPECIAL_USER, WinError.ERROR_TOO_MANY_SECRETS, WinError.ERROR_CURRENT_DOMAIN_NOT_ALLOWED, WinError.ERROR_HOTKEY_ALREADY_REGISTERED, WinError.ERROR_WINDOW_NOT_COMBOBOX, WinError.ERROR_INVALID_FILTER_PROC, WinError.ERROR_GLOBAL_ONLY_HOOK, WinError.ERROR_SETCOUNT_ON_BAD_LB, WinError.ERROR_INVALID_SPI_VALUE, WinError.ERROR_NO_SCROLLBARS, WinError.ERROR_NONPAGED_SYSTEM_RESOURCES, WinError.ERROR_WORKING_SET_QUOTA, WinError.ERROR_REQUIRES_INTERACTIVE_WINDOWSTATION, 1471, 1481, 1483, 1487, 1489, 1493, 1499}) {
            if (bigInteger.mod(BigInteger.valueOf(i)).equals(ZERO)) {
                return false;
            }
        }
        return true;
    }

    public static int nextSmallerPrime(int i) {
        if (i <= 2) {
            return 1;
        }
        if (i == 3) {
            return 2;
        }
        int i2 = (i & 1) == 0 ? i - 1 : i - 2;
        while (i2 > 3 && !isPrime(i2)) {
            i2 -= 2;
        }
        return i2;
    }

    public static BigInteger nextProbablePrime(BigInteger bigInteger, int i) {
        if (bigInteger.signum() < 0 || bigInteger.signum() == 0 || bigInteger.equals(ONE)) {
            return TWO;
        }
        BigInteger bigIntegerAdd = bigInteger.add(ONE);
        if (!bigIntegerAdd.testBit(0)) {
            bigIntegerAdd = bigIntegerAdd.add(ONE);
        }
        while (true) {
            if (bigIntegerAdd.bitLength() > 6) {
                long jLongValue = bigIntegerAdd.remainder(BigInteger.valueOf(SMALL_PRIME_PRODUCT)).longValue();
                if (jLongValue % 3 == 0 || jLongValue % 5 == 0 || jLongValue % 7 == 0 || jLongValue % 11 == 0 || jLongValue % 13 == 0 || jLongValue % 17 == 0 || jLongValue % 19 == 0 || jLongValue % 23 == 0 || jLongValue % 29 == 0 || jLongValue % 31 == 0 || jLongValue % 37 == 0 || jLongValue % 41 == 0) {
                    bigIntegerAdd = bigIntegerAdd.add(TWO);
                }
            }
            if (bigIntegerAdd.bitLength() >= 4 && !bigIntegerAdd.isProbablePrime(i)) {
                bigIntegerAdd = bigIntegerAdd.add(TWO);
            }
            return bigIntegerAdd;
        }
    }

    public static BigInteger nextProbablePrime(BigInteger bigInteger) {
        return nextProbablePrime(bigInteger, 20);
    }

    public static BigInteger nextPrime(long j) {
        boolean z;
        boolean z2 = false;
        long j2 = 0;
        if (j <= 1) {
            return BigInteger.valueOf(2L);
        }
        if (j == 2) {
            return BigInteger.valueOf(3L);
        }
        long j3 = j + 1;
        long j4 = j & 1;
        while (true) {
            long j5 = j3 + j4;
            if (j5 > (j << 1) || z2) {
                break;
            }
            long j6 = 3;
            while (true) {
                long j7 = j6;
                if (j7 > (j5 >> 1) || z2) {
                    break;
                }
                if (j5 % j7 == 0) {
                    z2 = true;
                }
                j6 = j7 + 2;
            }
            if (z2) {
                z = false;
            } else {
                j2 = j5;
                z = true;
            }
            z2 = z;
            j3 = j5;
            j4 = 2;
        }
        return BigInteger.valueOf(j2);
    }

    public static BigInteger binomial(int i, int i2) {
        BigInteger bigIntegerDivide = ONE;
        if (i == 0) {
            return i2 == 0 ? bigIntegerDivide : ZERO;
        }
        if (i2 > (i >>> 1)) {
            i2 = i - i2;
        }
        for (int i3 = 1; i3 <= i2; i3++) {
            bigIntegerDivide = bigIntegerDivide.multiply(BigInteger.valueOf(i - (i3 - 1))).divide(BigInteger.valueOf(i3));
        }
        return bigIntegerDivide;
    }

    public static BigInteger randomize(BigInteger bigInteger) {
        if (sr == null) {
            sr = CryptoServicesRegistrar.getSecureRandom();
        }
        return randomize(bigInteger, sr);
    }

    public static BigInteger randomize(BigInteger bigInteger, SecureRandom secureRandom) {
        int iBitLength = bigInteger.bitLength();
        BigInteger bigIntegerValueOf = BigInteger.valueOf(0L);
        if (secureRandom == null) {
            secureRandom = sr != null ? sr : CryptoServicesRegistrar.getSecureRandom();
        }
        for (int i = 0; i < 20; i++) {
            bigIntegerValueOf = BigIntegers.createRandomBigInteger(iBitLength, secureRandom);
            if (bigIntegerValueOf.compareTo(bigInteger) < 0) {
                return bigIntegerValueOf;
            }
        }
        return bigIntegerValueOf.mod(bigInteger);
    }

    public static BigInteger squareRoot(BigInteger bigInteger) {
        if (bigInteger.compareTo(ZERO) < 0) {
            throw new ArithmeticException("cannot extract root of negative number" + bigInteger + ".");
        }
        int iBitLength = bigInteger.bitLength();
        BigInteger bigIntegerMultiply = ZERO;
        BigInteger bigIntegerAdd = ZERO;
        if ((iBitLength & 1) != 0) {
            bigIntegerMultiply = bigIntegerMultiply.add(ONE);
            iBitLength--;
        }
        while (iBitLength > 0) {
            BigInteger bigIntegerMultiply2 = bigIntegerAdd.multiply(FOUR);
            int i = iBitLength - 1;
            int i2 = bigInteger.testBit(i) ? 2 : 0;
            iBitLength = i - 1;
            bigIntegerAdd = bigIntegerMultiply2.add(BigInteger.valueOf(i2 + (bigInteger.testBit(iBitLength) ? 1 : 0)));
            BigInteger bigIntegerAdd2 = bigIntegerMultiply.multiply(FOUR).add(ONE);
            bigIntegerMultiply = bigIntegerMultiply.multiply(TWO);
            if (bigIntegerAdd.compareTo(bigIntegerAdd2) != -1) {
                bigIntegerMultiply = bigIntegerMultiply.add(ONE);
                bigIntegerAdd = bigIntegerAdd.subtract(bigIntegerAdd2);
            }
        }
        return bigIntegerMultiply;
    }

    public static float intRoot(int i, int i2) {
        float f;
        float fFloatPow = i / i2;
        float f2 = 0.0f;
        int i3 = 0;
        while (Math.abs(f2 - fFloatPow) > 1.0E-4d) {
            float fFloatPow2 = floatPow(fFloatPow, i2);
            while (true) {
                f = fFloatPow2;
                if (Float.isInfinite(f)) {
                    fFloatPow = (fFloatPow + f2) / 2.0f;
                    fFloatPow2 = floatPow(fFloatPow, i2);
                }
            }
            i3++;
            f2 = fFloatPow;
            fFloatPow = f2 - ((f - i) / (i2 * floatPow(f2, i2 - 1)));
        }
        return fFloatPow;
    }

    public static float floatPow(float f, int i) {
        float f2 = 1.0f;
        while (i > 0) {
            f2 *= f;
            i--;
        }
        return f2;
    }

    public static double log(double d) {
        if (d > 0.0d && d < 1.0d) {
            return -log(1.0d / d);
        }
        int i = 0;
        double d2 = 1.0d;
        double d3 = d;
        while (d3 > 2.0d) {
            d3 /= 2.0d;
            i++;
            d2 *= 2.0d;
        }
        return ((double) i) + logBKM(d / d2);
    }

    public static double log(long j) {
        int iFloorLog = floorLog(BigInteger.valueOf(j));
        return ((double) iFloorLog) + logBKM(j / ((double) (1 << iFloorLog)));
    }

    private static double logBKM(double d) {
        double[] dArr = {1.0d, 0.5849625007211562d, 0.32192809488736235d, 0.16992500144231237d, 0.0874628412503394d, 0.044394119358453436d, 0.02236781302845451d, 0.01122725542325412d, 0.005624549193878107d, 0.0028150156070540383d, 0.0014081943928083889d, 7.042690112466433E-4d, 3.5217748030102726E-4d, 1.7609948644250602E-4d, 8.80524301221769E-5d, 4.4026886827316716E-5d, 2.2013611360340496E-5d, 1.1006847667481442E-5d, 5.503434330648604E-6d, 2.751719789561283E-6d, 1.375860550841138E-6d, 6.879304394358497E-7d, 3.4396526072176454E-7d, 1.7198264061184464E-7d, 8.599132286866321E-8d, 4.299566207501687E-8d, 2.1497831197679756E-8d, 1.0748915638882709E-8d, 5.374457829452062E-9d, 2.687228917228708E-9d, 1.3436144592400231E-9d, 6.718072297764289E-10d, 3.3590361492731876E-10d, 1.6795180747343547E-10d, 8.397590373916176E-11d, 4.1987951870191886E-11d, 2.0993975935248694E-11d, 1.0496987967662534E-11d, 5.2484939838408146E-12d, 2.624246991922794E-12d, 1.3121234959619935E-12d, 6.56061747981146E-13d, 3.2803087399061026E-13d, 1.6401543699531447E-13d, 8.200771849765956E-14d, 4.1003859248830365E-14d, 2.0501929624415328E-14d, 1.02509648122077E-14d, 5.1254824061038595E-15d, 2.5627412030519317E-15d, 1.2813706015259665E-15d, 6.406853007629834E-16d, 3.203426503814917E-16d, 1.6017132519074588E-16d, 8.008566259537294E-17d, 4.004283129768647E-17d, 2.0021415648843235E-17d, 1.0010707824421618E-17d, 5.005353912210809E-18d, 2.5026769561054044E-18d, 1.2513384780527022E-18d, 6.256692390263511E-19d, 3.1283461951317555E-19d, 1.5641730975658778E-19d, 7.820865487829389E-20d, 3.9104327439146944E-20d, 1.9552163719573472E-20d, 9.776081859786736E-21d, 4.888040929893368E-21d, 2.444020464946684E-21d, 1.222010232473342E-21d, 6.11005116236671E-22d, 3.055025581183355E-22d, 1.5275127905916775E-22d, 7.637563952958387E-23d, 3.818781976479194E-23d, 1.909390988239597E-23d, 9.546954941197984E-24d, 4.773477470598992E-24d, 2.386738735299496E-24d, 1.193369367649748E-24d, 5.96684683824874E-25d, 2.98342341912437E-25d, 1.491711709562185E-25d, 7.458558547810925E-26d, 3.7292792739054626E-26d, 1.8646396369527313E-26d, 9.323198184763657E-27d, 4.661599092381828E-27d, 2.330799546190914E-27d, 1.165399773095457E-27d, 5.826998865477285E-28d, 2.9134994327386427E-28d, 1.4567497163693213E-28d, 7.283748581846607E-29d, 3.6418742909233034E-29d, 1.8209371454616517E-29d, 9.104685727308258E-30d, 4.552342863654129E-30d, 2.2761714318270646E-30d};
        double d2 = 1.0d;
        double d3 = 0.0d;
        double d4 = 1.0d;
        for (int i = 0; i < 53; i++) {
            double d5 = d2 + (d2 * d4);
            if (d5 <= d) {
                d2 = d5;
                d3 += dArr[i];
            }
            d4 *= 0.5d;
        }
        return d3;
    }

    public static boolean isIncreasing(int[] iArr) {
        for (int i = 1; i < iArr.length; i++) {
            if (iArr[i - 1] >= iArr[i]) {
                return false;
            }
        }
        return true;
    }

    public static byte[] integerToOctets(BigInteger bigInteger) {
        byte[] byteArray = bigInteger.abs().toByteArray();
        if ((bigInteger.bitLength() & 7) != 0) {
            return byteArray;
        }
        byte[] bArr = new byte[bigInteger.bitLength() >> 3];
        System.arraycopy(byteArray, 1, bArr, 0, bArr.length);
        return bArr;
    }

    public static BigInteger octetsToInteger(byte[] bArr, int i, int i2) {
        byte[] bArr2 = new byte[i2 + 1];
        bArr2[0] = 0;
        System.arraycopy(bArr, i, bArr2, 1, i2);
        return new BigInteger(bArr2);
    }

    public static BigInteger octetsToInteger(byte[] bArr) {
        return octetsToInteger(bArr, 0, bArr.length);
    }
}
