package org.mozilla.javascript.v8dtoa;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/v8dtoa/FastDtoa.class */
public class FastDtoa {
    static final int kFastDtoaMaximalLength = 17;
    static final int minimal_target_exponent = -60;
    static final int maximal_target_exponent = -32;
    static final int kTen4 = 10000;
    static final int kTen5 = 100000;
    static final int kTen6 = 1000000;
    static final int kTen7 = 10000000;
    static final int kTen8 = 100000000;
    static final int kTen9 = 1000000000;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !FastDtoa.class.desiredAssertionStatus();
    }

    static boolean roundWeed(FastDtoaBuilder buffer, long distance_too_high_w, long unsafe_interval, long rest, long ten_kappa, long unit) {
        long small_distance = distance_too_high_w - unit;
        long big_distance = distance_too_high_w + unit;
        while (rest < small_distance && unsafe_interval - rest >= ten_kappa && (rest + ten_kappa < small_distance || small_distance - rest >= (rest + ten_kappa) - small_distance)) {
            buffer.decreaseLast();
            rest += ten_kappa;
        }
        return (rest >= big_distance || unsafe_interval - rest < ten_kappa || (rest + ten_kappa >= big_distance && big_distance - rest <= (rest + ten_kappa) - big_distance)) && 2 * unit <= rest && rest <= unsafe_interval - (4 * unit);
    }

    static long biggestPowerTen(int number, int number_bits) {
        int power;
        int exponent;
        switch (number_bits) {
            case 30:
            case 31:
            case 32:
                if (kTen9 <= number) {
                    power = kTen9;
                    exponent = 9;
                    break;
                }
            case 27:
            case 28:
            case 29:
                if (kTen8 <= number) {
                    power = kTen8;
                    exponent = 8;
                    break;
                }
            case 24:
            case 25:
            case 26:
                if (kTen7 <= number) {
                    power = kTen7;
                    exponent = 7;
                    break;
                }
            case 20:
            case 21:
            case 22:
            case 23:
                if (kTen6 <= number) {
                    power = kTen6;
                    exponent = 6;
                    break;
                }
            case 17:
            case 18:
            case 19:
                if (100000 <= number) {
                    power = 100000;
                    exponent = 5;
                    break;
                }
            case 14:
            case 15:
            case 16:
                if (10000 <= number) {
                    power = 10000;
                    exponent = 4;
                    break;
                }
            case 10:
            case 11:
            case 12:
            case 13:
                if (1000 <= number) {
                    power = 1000;
                    exponent = 3;
                    break;
                }
            case 7:
            case 8:
            case 9:
                if (100 <= number) {
                    power = 100;
                    exponent = 2;
                    break;
                }
            case 4:
            case 5:
            case 6:
                if (10 <= number) {
                    power = 10;
                    exponent = 1;
                    break;
                }
            case 1:
            case 2:
            case 3:
                if (1 <= number) {
                    power = 1;
                    exponent = 0;
                    break;
                }
            case 0:
                power = 0;
                exponent = -1;
                break;
            default:
                power = 0;
                exponent = 0;
                break;
        }
        return (((long) power) << 32) | (4294967295L & ((long) exponent));
    }

    private static boolean uint64_lte(long a, long b) {
        if (a != b) {
            if (!(((a < b) ^ (a < 0)) ^ (b < 0))) {
                return false;
            }
        }
        return true;
    }

    static boolean digitGen(DiyFp low, DiyFp w, DiyFp high, FastDtoaBuilder buffer, int mk) {
        if (!$assertionsDisabled && (low.e() != w.e() || w.e() != high.e())) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && !uint64_lte(low.f() + 1, high.f() - 1)) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && (minimal_target_exponent > w.e() || w.e() > -32)) {
            throw new AssertionError();
        }
        long unit = 1;
        DiyFp too_low = new DiyFp(low.f() - 1, low.e());
        DiyFp too_high = new DiyFp(high.f() + 1, high.e());
        DiyFp unsafe_interval = DiyFp.minus(too_high, too_low);
        DiyFp one = new DiyFp(1 << (-w.e()), w.e());
        int integrals = (int) ((too_high.f() >>> (-one.e())) & 4294967295L);
        long fractionals = too_high.f() & (one.f() - 1);
        long result = biggestPowerTen(integrals, 64 - (-one.e()));
        int divider = (int) ((result >>> 32) & 4294967295L);
        int divider_exponent = (int) (result & 4294967295L);
        int kappa = divider_exponent + 1;
        while (kappa > 0) {
            int digit = integrals / divider;
            buffer.append((char) (48 + digit));
            integrals %= divider;
            kappa--;
            long rest = (((long) integrals) << (-one.e())) + fractionals;
            if (rest < unsafe_interval.f()) {
                buffer.point = (buffer.end - mk) + kappa;
                return roundWeed(buffer, DiyFp.minus(too_high, w).f(), unsafe_interval.f(), rest, ((long) divider) << (-one.e()), 1L);
            }
            divider /= 10;
        }
        do {
            long fractionals2 = fractionals * 5;
            unit *= 5;
            unsafe_interval.setF(unsafe_interval.f() * 5);
            unsafe_interval.setE(unsafe_interval.e() + 1);
            one.setF(one.f() >>> 1);
            one.setE(one.e() + 1);
            int digit2 = (int) ((fractionals2 >>> (-one.e())) & 4294967295L);
            buffer.append((char) (48 + digit2));
            fractionals = fractionals2 & (one.f() - 1);
            kappa--;
        } while (fractionals >= unsafe_interval.f());
        buffer.point = (buffer.end - mk) + kappa;
        return roundWeed(buffer, DiyFp.minus(too_high, w).f() * unit, unsafe_interval.f(), fractionals, one.f(), unit);
    }

    static boolean grisu3(double v, FastDtoaBuilder buffer) {
        long bits = Double.doubleToLongBits(v);
        DiyFp w = DoubleHelper.asNormalizedDiyFp(bits);
        DiyFp boundary_minus = new DiyFp();
        DiyFp boundary_plus = new DiyFp();
        DoubleHelper.normalizedBoundaries(bits, boundary_minus, boundary_plus);
        if (!$assertionsDisabled && boundary_plus.e() != w.e()) {
            throw new AssertionError();
        }
        DiyFp ten_mk = new DiyFp();
        int mk = CachedPowers.getCachedPower(w.e() + 64, minimal_target_exponent, -32, ten_mk);
        if (!$assertionsDisabled && (minimal_target_exponent > w.e() + ten_mk.e() + 64 || -32 < w.e() + ten_mk.e() + 64)) {
            throw new AssertionError();
        }
        DiyFp scaled_w = DiyFp.times(w, ten_mk);
        if (!$assertionsDisabled && scaled_w.e() != boundary_plus.e() + ten_mk.e() + 64) {
            throw new AssertionError();
        }
        DiyFp scaled_boundary_minus = DiyFp.times(boundary_minus, ten_mk);
        DiyFp scaled_boundary_plus = DiyFp.times(boundary_plus, ten_mk);
        return digitGen(scaled_boundary_minus, scaled_w, scaled_boundary_plus, buffer, mk);
    }

    public static boolean dtoa(double v, FastDtoaBuilder buffer) {
        if (!$assertionsDisabled && v <= 0.0d) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && Double.isNaN(v)) {
            throw new AssertionError();
        }
        if ($assertionsDisabled || !Double.isInfinite(v)) {
            return grisu3(v, buffer);
        }
        throw new AssertionError();
    }

    public static String numberToString(double v) {
        FastDtoaBuilder buffer = new FastDtoaBuilder();
        if (numberToString(v, buffer)) {
            return buffer.format();
        }
        return null;
    }

    public static boolean numberToString(double v, FastDtoaBuilder buffer) {
        buffer.reset();
        if (v < 0.0d) {
            buffer.append('-');
            v = -v;
        }
        return dtoa(v, buffer);
    }
}
