package org.mozilla.javascript.v8dtoa;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/v8dtoa/DoubleHelper.class */
public class DoubleHelper {
    static final long kSignMask = Long.MIN_VALUE;
    static final long kExponentMask = 9218868437227405312L;
    static final long kSignificandMask = 4503599627370495L;
    static final long kHiddenBit = 4503599627370496L;
    private static final int kSignificandSize = 52;
    private static final int kExponentBias = 1075;
    private static final int kDenormalExponent = -1074;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !DoubleHelper.class.desiredAssertionStatus();
    }

    static DiyFp asDiyFp(long d64) {
        if ($assertionsDisabled || !isSpecial(d64)) {
            return new DiyFp(significand(d64), exponent(d64));
        }
        throw new AssertionError();
    }

    static DiyFp asNormalizedDiyFp(long d64) {
        long f = significand(d64);
        int e = exponent(d64);
        if (!$assertionsDisabled && f == 0) {
            throw new AssertionError();
        }
        while ((f & kHiddenBit) == 0) {
            f <<= 1;
            e--;
        }
        return new DiyFp(f << 11, e - 11);
    }

    static int exponent(long d64) {
        if (isDenormal(d64)) {
            return kDenormalExponent;
        }
        int biased_e = (int) (((d64 & kExponentMask) >>> 52) & 4294967295L);
        return biased_e - 1075;
    }

    static long significand(long d64) {
        long significand = d64 & kSignificandMask;
        if (!isDenormal(d64)) {
            return significand + kHiddenBit;
        }
        return significand;
    }

    static boolean isDenormal(long d64) {
        return (d64 & kExponentMask) == 0;
    }

    static boolean isSpecial(long d64) {
        return (d64 & kExponentMask) == kExponentMask;
    }

    static boolean isNan(long d64) {
        return (d64 & kExponentMask) == kExponentMask && (d64 & kSignificandMask) != 0;
    }

    static boolean isInfinite(long d64) {
        return (d64 & kExponentMask) == kExponentMask && (d64 & kSignificandMask) == 0;
    }

    static int sign(long d64) {
        return (d64 & kSignMask) == 0 ? 1 : -1;
    }

    static void normalizedBoundaries(long d64, DiyFp m_minus, DiyFp m_plus) {
        DiyFp v = asDiyFp(d64);
        boolean significand_is_zero = v.f() == kHiddenBit;
        m_plus.setF((v.f() << 1) + 1);
        m_plus.setE(v.e() - 1);
        m_plus.normalize();
        if (significand_is_zero && v.e() != kDenormalExponent) {
            m_minus.setF((v.f() << 2) - 1);
            m_minus.setE(v.e() - 2);
        } else {
            m_minus.setF((v.f() << 1) - 1);
            m_minus.setE(v.e() - 1);
        }
        m_minus.setF(m_minus.f() << (m_minus.e() - m_plus.e()));
        m_minus.setE(m_plus.e());
    }
}
