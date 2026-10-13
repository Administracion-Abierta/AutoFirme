package org.mozilla.javascript;

import com.sun.jna.platform.win32.Ddeml;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/DToA.class */
class DToA {
    static final int DTOSTR_STANDARD = 0;
    static final int DTOSTR_STANDARD_EXPONENTIAL = 1;
    static final int DTOSTR_FIXED = 2;
    static final int DTOSTR_EXPONENTIAL = 3;
    static final int DTOSTR_PRECISION = 4;
    private static final int Frac_mask = 1048575;
    private static final int Exp_shift = 20;
    private static final int Exp_msk1 = 1048576;
    private static final long Frac_maskL = 4503599627370495L;
    private static final int Exp_shiftL = 52;
    private static final long Exp_msk1L = 4503599627370496L;
    private static final int Bias = 1023;
    private static final int P = 53;
    private static final int Exp_shift1 = 20;
    private static final int Exp_mask = 2146435072;
    private static final int Exp_mask_shifted = 2047;
    private static final int Bndry_mask = 1048575;
    private static final int Log2P = 1;
    private static final int Sign_bit = Integer.MIN_VALUE;
    private static final int Exp_11 = 1072693248;
    private static final int Ten_pmax = 22;
    private static final int Quick_max = 14;
    private static final int Bletch = 16;
    private static final int Frac_mask1 = 1048575;
    private static final int Int_max = 14;
    private static final int n_bigtens = 5;
    private static final double[] tens = {1.0d, 10.0d, 100.0d, 1000.0d, 10000.0d, 100000.0d, 1000000.0d, 1.0E7d, 1.0E8d, 1.0E9d, 1.0E10d, 1.0E11d, 1.0E12d, 1.0E13d, 1.0E14d, 1.0E15d, 1.0E16d, 1.0E17d, 1.0E18d, 1.0E19d, 1.0E20d, 1.0E21d, 1.0E22d};
    private static final double[] bigtens = {1.0E16d, 1.0E32d, 1.0E64d, 1.0E128d, 1.0E256d};
    private static final int[] dtoaModes = {0, 0, 3, 2, 2};

    DToA() {
    }

    private static char BASEDIGIT(int digit) {
        return (char) (digit >= 10 ? 87 + digit : 48 + digit);
    }

    private static int lo0bits(int y) {
        int x = y;
        if ((x & 7) != 0) {
            if ((x & 1) != 0) {
                return 0;
            }
            if ((x & 2) != 0) {
                return 1;
            }
            return 2;
        }
        int k = 0;
        if ((x & 65535) == 0) {
            k = 16;
            x >>>= 16;
        }
        if ((x & 255) == 0) {
            k += 8;
            x >>>= 8;
        }
        if ((x & 15) == 0) {
            k += 4;
            x >>>= 4;
        }
        if ((x & 3) == 0) {
            k += 2;
            x >>>= 2;
        }
        if ((x & 1) == 0) {
            k++;
            if (((x >>> 1) & 1) == 0) {
                return 32;
            }
        }
        return k;
    }

    private static int hi0bits(int x) {
        int k = 0;
        if ((x & (-65536)) == 0) {
            k = 16;
            x <<= 16;
        }
        if ((x & Ddeml.MF_MASK) == 0) {
            k += 8;
            x <<= 8;
        }
        if ((x & (-268435456)) == 0) {
            k += 4;
            x <<= 4;
        }
        if ((x & (-1073741824)) == 0) {
            k += 2;
            x <<= 2;
        }
        if ((x & Integer.MIN_VALUE) == 0) {
            k++;
            if ((x & 1073741824) == 0) {
                return 32;
            }
        }
        return k;
    }

    private static void stuffBits(byte[] bits, int offset, int val) {
        bits[offset] = (byte) (val >> 24);
        bits[offset + 1] = (byte) (val >> 16);
        bits[offset + 2] = (byte) (val >> 8);
        bits[offset + 3] = (byte) val;
    }

    private static BigInteger d2b(double d, int[] e, int[] bits) {
        byte[] dbl_bits;
        int k;
        int i;
        long dBits = Double.doubleToLongBits(d);
        int d0 = (int) (dBits >>> 32);
        int d1 = (int) dBits;
        int z = d0 & WinNT.NLS_VALID_LOCALE_MASK;
        int de = (d0 & WinNT.MAXLONG) >>> 20;
        if (de != 0) {
            z |= 1048576;
        }
        if (d1 != 0) {
            dbl_bits = new byte[8];
            k = lo0bits(d1);
            int y = d1 >>> k;
            if (k != 0) {
                stuffBits(dbl_bits, 4, y | (z << (32 - k)));
                z >>= k;
            } else {
                stuffBits(dbl_bits, 4, y);
            }
            stuffBits(dbl_bits, 0, z);
            i = z != 0 ? 2 : 1;
        } else {
            dbl_bits = new byte[4];
            int k2 = lo0bits(z);
            z >>>= k2;
            stuffBits(dbl_bits, 0, z);
            k = k2 + 32;
            i = 1;
        }
        if (de != 0) {
            e[0] = ((de - 1023) - 52) + k;
            bits[0] = 53 - k;
        } else {
            e[0] = ((de - 1023) - 52) + 1 + k;
            bits[0] = (32 * i) - hi0bits(z);
        }
        return new BigInteger(dbl_bits);
    }

    static String JS_dtobasestr(int base, double d) {
        boolean negative;
        long mantissa;
        String intDigits;
        if (2 > base || base > 36) {
            throw new IllegalArgumentException("Bad base: " + base);
        }
        if (Double.isNaN(d)) {
            return "NaN";
        }
        if (Double.isInfinite(d)) {
            return d > 0.0d ? "Infinity" : "-Infinity";
        }
        if (d == 0.0d) {
            return "0";
        }
        if (d >= 0.0d) {
            negative = false;
        } else {
            negative = true;
            d = -d;
        }
        double dfloor = Math.floor(d);
        long lfloor = (long) dfloor;
        if (lfloor == dfloor) {
            intDigits = Long.toString(negative ? -lfloor : lfloor, base);
        } else {
            long floorBits = Double.doubleToLongBits(dfloor);
            int exp = ((int) (floorBits >> 52)) & Exp_mask_shifted;
            if (exp == 0) {
                mantissa = (floorBits & Frac_maskL) << 1;
            } else {
                mantissa = (floorBits & Frac_maskL) | Exp_msk1L;
            }
            if (negative) {
                mantissa = -mantissa;
            }
            int exp2 = exp - 1075;
            BigInteger x = BigInteger.valueOf(mantissa);
            if (exp2 > 0) {
                x = x.shiftLeft(exp2);
            } else if (exp2 < 0) {
                x = x.shiftRight(-exp2);
            }
            intDigits = x.toString(base);
        }
        if (d == dfloor) {
            return intDigits;
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append(intDigits).append('.');
        double df = d - dfloor;
        long dBits = Double.doubleToLongBits(d);
        int word0 = (int) (dBits >> 32);
        int word1 = (int) dBits;
        int[] e = new int[1];
        int[] bbits = new int[1];
        BigInteger b = d2b(df, e, bbits);
        int s2 = -((word0 >>> 20) & Exp_mask_shifted);
        if (s2 == 0) {
            s2 = -1;
        }
        int s3 = s2 + WinError.ERROR_BOOT_ALREADY_ACCEPTED;
        BigInteger mlo = BigInteger.valueOf(1L);
        BigInteger mhi = mlo;
        if (word1 == 0 && (word0 & WinNT.NLS_VALID_LOCALE_MASK) == 0 && (word0 & 2145386496) != 0) {
            s3++;
            mhi = BigInteger.valueOf(2L);
        }
        BigInteger b2 = b.shiftLeft(e[0] + s3);
        BigInteger s = BigInteger.valueOf(1L).shiftLeft(s3);
        BigInteger bigBase = BigInteger.valueOf(base);
        boolean done = false;
        do {
            BigInteger[] divResult = b2.multiply(bigBase).divideAndRemainder(s);
            b2 = divResult[1];
            int digit = (char) divResult[0].intValue();
            if (mlo == mhi) {
                BigInteger bigIntegerMultiply = mlo.multiply(bigBase);
                mhi = bigIntegerMultiply;
                mlo = bigIntegerMultiply;
            } else {
                mlo = mlo.multiply(bigBase);
                mhi = mhi.multiply(bigBase);
            }
            int j = b2.compareTo(mlo);
            BigInteger delta = s.subtract(mhi);
            int j1 = delta.signum() <= 0 ? 1 : b2.compareTo(delta);
            if (j1 == 0 && (word1 & 1) == 0) {
                if (j > 0) {
                    digit++;
                }
                done = true;
            } else if (j < 0 || (j == 0 && (word1 & 1) == 0)) {
                if (j1 > 0) {
                    b2 = b2.shiftLeft(1);
                    if (b2.compareTo(s) > 0) {
                        digit++;
                    }
                }
                done = true;
            } else if (j1 > 0) {
                digit++;
                done = true;
            }
            buffer.append(BASEDIGIT(digit));
        } while (!done);
        return buffer.toString();
    }

    static int word0(double d) {
        long dBits = Double.doubleToLongBits(d);
        return (int) (dBits >> 32);
    }

    static double setWord0(double d, int i) {
        long dBits = Double.doubleToLongBits(d);
        return Double.longBitsToDouble((((long) i) << 32) | (dBits & 4294967295L));
    }

    static int word1(double d) {
        long dBits = Double.doubleToLongBits(d);
        return (int) dBits;
    }

    static BigInteger pow5mult(BigInteger b, int k) {
        return b.multiply(BigInteger.valueOf(5L).pow(k));
    }

    static boolean roundOff(StringBuilder buf) {
        int i = buf.length();
        while (i != 0) {
            i--;
            char c = buf.charAt(i);
            if (c != '9') {
                buf.setCharAt(i, (char) (c + 1));
                buf.setLength(i + 1);
                return false;
            }
        }
        buf.setLength(0);
        return true;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:63:0x01f8  */
    /* JADX WARN: Code duplicated, block: B:68:0x0223  */
    static int JS_dtoa(double d, int mode, boolean biasUp, int ndigits, boolean[] sign, StringBuilder buf) {
        long jWord1;
        double d2;
        int i;
        boolean denorm;
        int b2;
        int s2;
        int b5;
        int s5;
        BigInteger b;
        char dig;
        int j1;
        int i2;
        int i3;
        char lastCh;
        char lastCh2;
        char lastCh3;
        int i4;
        int[] be = new int[1];
        int[] bbits = new int[1];
        if ((word0(d) & Integer.MIN_VALUE) != 0) {
            sign[0] = true;
            d = setWord0(d, word0(d) & WinNT.MAXLONG);
        } else {
            sign[0] = false;
        }
        if ((word0(d) & Exp_mask) == Exp_mask) {
            buf.append((word1(d) == 0 && (word0(d) & WinNT.NLS_VALID_LOCALE_MASK) == 0) ? "Infinity" : "NaN");
            return 9999;
        }
        if (d == 0.0d) {
            buf.setLength(0);
            buf.append('0');
            return 1;
        }
        BigInteger b3 = d2b(d, be, bbits);
        int i5 = (word0(d) >>> 20) & Exp_mask_shifted;
        if (i5 != 0) {
            d2 = setWord0(d, (word0(d) & WinNT.NLS_VALID_LOCALE_MASK) | Exp_11);
            i = i5 - 1023;
            denorm = false;
        } else {
            int i6 = bbits[0] + be[0] + WinError.ERROR_ALREADY_RUNNING_LKG;
            if (i6 > 32) {
                jWord1 = (((long) word0(d)) << (64 - i6)) | ((long) (word1(d) >>> (i6 - 32)));
            } else {
                jWord1 = ((long) word1(d)) << (32 - i6);
            }
            long x = jWord1;
            d2 = setWord0(x, word0(x) - 32505856);
            i = i6 - 1075;
            denorm = true;
        }
        double ds = ((d2 - 1.5d) * 0.289529654602168d) + 0.1760912590558d + (((double) i) * 0.301029995663981d);
        int k = (int) ds;
        if (ds < 0.0d && ds != k) {
            k--;
        }
        boolean k_check = true;
        if (k >= 0 && k <= 22) {
            if (d < tens[k]) {
                k--;
            }
            k_check = false;
        }
        int j = (bbits[0] - i) - 1;
        if (j >= 0) {
            b2 = 0;
            s2 = j;
        } else {
            b2 = -j;
            s2 = 0;
        }
        if (k >= 0) {
            b5 = 0;
            s5 = k;
            s2 += k;
        } else {
            b2 -= k;
            b5 = -k;
            s5 = 0;
        }
        if (mode < 0 || mode > 9) {
            mode = 0;
        }
        boolean try_quick = true;
        if (mode > 5) {
            mode -= 4;
            try_quick = false;
        }
        boolean leftright = true;
        int ilim1 = 0;
        int ilim = 0;
        switch (mode) {
            case 0:
            case 1:
                ilim1 = -1;
                ilim = -1;
                ndigits = 0;
                break;
            case 2:
                leftright = false;
                if (ndigits <= 0) {
                    ndigits = 1;
                }
                int i7 = ndigits;
                ilim1 = i7;
                ilim = i7;
                break;
            case 3:
                leftright = false;
                i4 = ndigits + k + 1;
                ilim = i4;
                ilim1 = i4 - 1;
                if (i4 <= 0) {
                }
                break;
            case 4:
                if (ndigits <= 0) {
                    ndigits = 1;
                }
                int i8 = ndigits;
                ilim1 = i8;
                ilim = i8;
                break;
            case 5:
                i4 = ndigits + k + 1;
                ilim = i4;
                ilim1 = i4 - 1;
                if (i4 <= 0) {
                }
                break;
        }
        boolean fast_failed = false;
        if (ilim >= 0 && ilim <= 14 && try_quick) {
            int i9 = 0;
            double d3 = d;
            int k0 = k;
            int ilim0 = ilim;
            int ieps = 2;
            if (k > 0) {
                double ds2 = tens[k & 15];
                int j2 = k >> 4;
                if ((j2 & 16) != 0) {
                    j2 &= 15;
                    d /= bigtens[4];
                    ieps = 2 + 1;
                }
                while (j2 != 0) {
                    if ((j2 & 1) != 0) {
                        ieps++;
                        ds2 *= bigtens[i9];
                    }
                    j2 >>= 1;
                    i9++;
                }
                d /= ds2;
            } else {
                int j3 = -k;
                if (j3 != 0) {
                    d *= tens[j3 & 15];
                    int j4 = j3 >> 4;
                    while (j4 != 0) {
                        if ((j4 & 1) != 0) {
                            ieps++;
                            d *= bigtens[i9];
                        }
                        j4 >>= 1;
                        i9++;
                    }
                }
            }
            if (k_check && d < 1.0d && ilim > 0) {
                if (ilim1 <= 0) {
                    fast_failed = true;
                } else {
                    ilim = ilim1;
                    k--;
                    d *= 10.0d;
                    ieps++;
                }
            }
            double eps = (((double) ieps) * d) + 7.0d;
            double eps2 = setWord0(eps, word0(eps) - 54525952);
            if (ilim == 0) {
                d -= 5.0d;
                if (d > eps2) {
                    buf.append('1');
                    return k + 1 + 1;
                }
                if (d < (-eps2)) {
                    buf.setLength(0);
                    buf.append('0');
                    return 1;
                }
                fast_failed = true;
            }
            if (!fast_failed) {
                fast_failed = true;
                if (leftright) {
                    double eps3 = (0.5d / tens[ilim - 1]) - eps2;
                    int i10 = 0;
                    while (true) {
                        long L = (long) d;
                        d -= L;
                        buf.append((char) (48 + L));
                        if (d < eps3) {
                            return k + 1;
                        }
                        if (1.0d - d < eps3) {
                            do {
                                lastCh3 = buf.charAt(buf.length() - 1);
                                buf.setLength(buf.length() - 1);
                                if (lastCh3 != '9') {
                                }
                                buf.append((char) (lastCh3 + 1));
                                return k + 1;
                            } while (buf.length() != 0);
                            k++;
                            lastCh3 = '0';
                            buf.append((char) (lastCh3 + 1));
                            return k + 1;
                        }
                        i10++;
                        if (i10 < ilim) {
                            eps3 *= 10.0d;
                            d *= 10.0d;
                        }
                    }
                } else {
                    double eps4 = eps2 * tens[ilim - 1];
                    int i11 = 1;
                    while (true) {
                        long L2 = (long) d;
                        d -= L2;
                        buf.append((char) (48 + L2));
                        if (i11 != ilim) {
                            i11++;
                            d *= 10.0d;
                        } else {
                            if (d > 0.5d + eps4) {
                                do {
                                    lastCh2 = buf.charAt(buf.length() - 1);
                                    buf.setLength(buf.length() - 1);
                                    if (lastCh2 != '9') {
                                    }
                                    buf.append((char) (lastCh2 + 1));
                                    return k + 1;
                                } while (buf.length() != 0);
                                k++;
                                lastCh2 = '0';
                                buf.append((char) (lastCh2 + 1));
                                return k + 1;
                            }
                            if (d < 0.5d - eps4) {
                                stripTrailingZeroes(buf);
                                return k + 1;
                            }
                        }
                    }
                }
            }
            if (fast_failed) {
                buf.setLength(0);
                d = d3;
                k = k0;
                ilim = ilim0;
            }
        }
        if (be[0] >= 0 && k <= 14) {
            double ds3 = tens[k];
            if (ndigits < 0 && ilim <= 0) {
                if (ilim < 0 || d < 5.0d * ds3 || (!biasUp && d == 5.0d * ds3)) {
                    buf.setLength(0);
                    buf.append('0');
                    return 1;
                }
                buf.append('1');
                return k + 1 + 1;
            }
            int i12 = 1;
            while (true) {
                long L3 = (long) (d / ds3);
                double d4 = d - (L3 * ds3);
                buf.append((char) (48 + L3));
                if (i12 == ilim) {
                    double d5 = d4 + d4;
                    if (d5 > ds3 || (d5 == ds3 && ((L3 & 1) != 0 || biasUp))) {
                        while (true) {
                            lastCh = buf.charAt(buf.length() - 1);
                            buf.setLength(buf.length() - 1);
                            if (lastCh == '9') {
                                if (buf.length() == 0) {
                                    k++;
                                    lastCh = '0';
                                }
                            }
                        }
                        buf.append((char) (lastCh + 1));
                    }
                } else {
                    d = d4 * 10.0d;
                    if (d != 0.0d) {
                        i12++;
                    }
                }
            }
            return k + 1;
        }
        int m2 = b2;
        int m5 = b5;
        BigInteger mhi = null;
        if (leftright) {
            if (mode < 2) {
                i3 = denorm ? be[0] + WinError.ERROR_SERVICE_DEPENDENCY_DELETED : 54 - bbits[0];
            } else {
                int j5 = ilim - 1;
                if (m5 >= j5) {
                    m5 -= j5;
                } else {
                    int j6 = j5 - m5;
                    s5 += j6;
                    b5 += j6;
                    m5 = 0;
                }
                int i13 = ilim;
                i3 = i13;
                if (i13 < 0) {
                    m2 -= i3;
                    i3 = 0;
                }
            }
            b2 += i3;
            s2 += i3;
            mhi = BigInteger.valueOf(1L);
        }
        if (m2 > 0 && s2 > 0) {
            int i14 = m2 < s2 ? m2 : s2;
            b2 -= i14;
            m2 -= i14;
            s2 -= i14;
        }
        if (b5 > 0) {
            if (leftright) {
                if (m5 > 0) {
                    mhi = pow5mult(mhi, m5);
                    BigInteger b1 = mhi.multiply(b3);
                    b3 = b1;
                }
                int j7 = b5 - m5;
                if (j7 != 0) {
                    b3 = pow5mult(b3, j7);
                }
            } else {
                b3 = pow5mult(b3, b5);
            }
        }
        BigInteger S = BigInteger.valueOf(1L);
        if (s5 > 0) {
            S = pow5mult(S, s5);
        }
        boolean spec_case = false;
        if (mode < 2 && word1(d) == 0 && (word0(d) & WinNT.NLS_VALID_LOCALE_MASK) == 0 && (word0(d) & 2145386496) != 0) {
            b2++;
            s2++;
            spec_case = true;
        }
        byte[] S_bytes = S.toByteArray();
        int S_hiWord = 0;
        for (int idx = 0; idx < 4; idx++) {
            S_hiWord <<= 8;
            if (idx < S_bytes.length) {
                S_hiWord |= S_bytes[idx] & 255;
            }
        }
        int iHi0bits = ((s5 != 0 ? 32 - hi0bits(S_hiWord) : 1) + s2) & 31;
        int i15 = iHi0bits;
        if (iHi0bits != 0) {
            i15 = 32 - i15;
        }
        if (i15 > 4) {
            int i16 = i15 - 4;
            b2 += i16;
            m2 += i16;
            s2 += i16;
        } else if (i15 < 4) {
            int i17 = i15 + 28;
            b2 += i17;
            m2 += i17;
            s2 += i17;
        }
        if (b2 > 0) {
            b3 = b3.shiftLeft(b2);
        }
        if (s2 > 0) {
            S = S.shiftLeft(s2);
        }
        if (k_check && b3.compareTo(S) < 0) {
            k--;
            b3 = b3.multiply(BigInteger.valueOf(10L));
            if (leftright) {
                mhi = mhi.multiply(BigInteger.valueOf(10L));
            }
            ilim = ilim1;
        }
        if (ilim <= 0 && mode > 2) {
            if (ilim < 0 || (i2 = b3.compareTo(S.multiply(BigInteger.valueOf(5L)))) < 0 || (i2 == 0 && !biasUp)) {
                buf.setLength(0);
                buf.append('0');
                return 1;
            }
            buf.append('1');
            return k + 1 + 1;
        }
        if (leftright) {
            if (m2 > 0) {
                mhi = mhi.shiftLeft(m2);
            }
            BigInteger mlo = mhi;
            if (spec_case) {
                mhi = mlo.shiftLeft(1);
            }
            int i18 = 1;
            while (true) {
                BigInteger[] divResult = b3.divideAndRemainder(S);
                b = divResult[1];
                dig = (char) (divResult[0].intValue() + 48);
                int j8 = b.compareTo(mlo);
                BigInteger delta = S.subtract(mhi);
                int j9 = delta.signum() <= 0 ? 1 : b.compareTo(delta);
                if (j9 == 0 && mode == 0 && (word1(d) & 1) == 0) {
                    if (dig == '9') {
                        buf.append('9');
                        if (roundOff(buf)) {
                            k++;
                            buf.append('1');
                        }
                        return k + 1;
                    }
                    if (j8 > 0) {
                        dig = (char) (dig + 1);
                    }
                    buf.append(dig);
                    return k + 1;
                }
                if (j8 < 0 || (j8 == 0 && mode == 0 && (word1(d) & 1) == 0)) {
                    if (j9 > 0 && ((j1 = b.shiftLeft(1).compareTo(S)) > 0 || (j1 == 0 && ((dig & 1) == 1 || biasUp)))) {
                        dig = (char) (dig + 1);
                        if (dig == '9') {
                            buf.append('9');
                            if (roundOff(buf)) {
                                k++;
                                buf.append('1');
                            }
                            return k + 1;
                        }
                    }
                    buf.append(dig);
                    return k + 1;
                }
                if (j9 > 0) {
                    if (dig == '9') {
                        buf.append('9');
                        if (roundOff(buf)) {
                            k++;
                            buf.append('1');
                        }
                        return k + 1;
                    }
                    buf.append((char) (dig + 1));
                    return k + 1;
                }
                buf.append(dig);
                if (i18 != ilim) {
                    b3 = b.multiply(BigInteger.valueOf(10L));
                    if (mlo == mhi) {
                        BigInteger bigIntegerMultiply = mhi.multiply(BigInteger.valueOf(10L));
                        mhi = bigIntegerMultiply;
                        mlo = bigIntegerMultiply;
                    } else {
                        mlo = mlo.multiply(BigInteger.valueOf(10L));
                        mhi = mhi.multiply(BigInteger.valueOf(10L));
                    }
                    i18++;
                }
            }
        } else {
            int i19 = 1;
            while (true) {
                BigInteger[] divResult2 = b3.divideAndRemainder(S);
                b = divResult2[1];
                dig = (char) (divResult2[0].intValue() + 48);
                buf.append(dig);
                if (i19 < ilim) {
                    b3 = b.multiply(BigInteger.valueOf(10L));
                    i19++;
                }
            }
        }
        int j10 = b.shiftLeft(1).compareTo(S);
        if (j10 > 0 || (j10 == 0 && ((dig & 1) == 1 || biasUp))) {
            if (roundOff(buf)) {
                buf.append('1');
                return k + 1 + 1;
            }
        } else {
            stripTrailingZeroes(buf);
        }
        return k + 1;
    }

    private static void stripTrailingZeroes(StringBuilder buf) {
        int bl = buf.length();
        do {
            int i = bl;
            bl--;
            if (i <= 0) {
                break;
            }
        } while (buf.charAt(bl) == '0');
        buf.setLength(bl + 1);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    static void JS_dtostr(StringBuilder buffer, int mode, int precision, double d) {
        boolean[] sign = new boolean[1];
        if (mode == 2 && (d >= 1.0E21d || d <= -1.0E21d)) {
            mode = 0;
        }
        int decPt = JS_dtoa(d, dtoaModes[mode], mode >= 2, precision, sign, buffer);
        int nDigits = buffer.length();
        if (decPt != 9999) {
            boolean exponentialNotation = false;
            int minNDigits = 0;
            switch (mode) {
                case 0:
                    if (decPt < -5 || decPt > 21) {
                        exponentialNotation = true;
                    } else {
                        minNDigits = decPt;
                    }
                    break;
                case 1:
                    exponentialNotation = true;
                    break;
                case 2:
                    minNDigits = precision >= 0 ? decPt + precision : decPt;
                    break;
                case 3:
                    minNDigits = precision;
                    exponentialNotation = true;
                    break;
                case 4:
                    minNDigits = precision;
                    if (decPt < -5 || decPt > precision) {
                        exponentialNotation = true;
                    }
                    break;
            }
            if (nDigits < minNDigits) {
                int p = minNDigits;
                nDigits = minNDigits;
                do {
                    buffer.append('0');
                } while (buffer.length() != p);
            }
            if (exponentialNotation) {
                if (nDigits != 1) {
                    buffer.insert(1, '.');
                }
                buffer.append('e');
                if (decPt - 1 >= 0) {
                    buffer.append('+');
                }
                buffer.append(decPt - 1);
            } else if (decPt != nDigits) {
                if (decPt > 0) {
                    buffer.insert(decPt, '.');
                } else {
                    for (int i = 0; i < 1 - decPt; i++) {
                        buffer.insert(0, '0');
                    }
                    buffer.insert(1, '.');
                }
            }
        }
        if (sign[0]) {
            if (word0(d) != Integer.MIN_VALUE || word1(d) != 0) {
                if ((word0(d) & Exp_mask) != Exp_mask || (word1(d) == 0 && (word0(d) & WinNT.NLS_VALID_LOCALE_MASK) == 0)) {
                    buffer.insert(0, '-');
                }
            }
        }
    }
}
