package org.mozilla.javascript.v8dtoa;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/v8dtoa/DiyFp.class */
class DiyFp {
    private long f;
    private int e;
    static final int kSignificandSize = 64;
    static final long kUint64MSB = Long.MIN_VALUE;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !DiyFp.class.desiredAssertionStatus();
    }

    DiyFp() {
        this.f = 0L;
        this.e = 0;
    }

    DiyFp(long f, int e) {
        this.f = f;
        this.e = e;
    }

    private static boolean uint64_gte(long a, long b) {
        if (a != b) {
            if (!(((a > b) ^ (a < 0)) ^ (b < 0))) {
                return false;
            }
        }
        return true;
    }

    void subtract(DiyFp other) {
        if (!$assertionsDisabled && this.e != other.e) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && !uint64_gte(this.f, other.f)) {
            throw new AssertionError();
        }
        this.f -= other.f;
    }

    static DiyFp minus(DiyFp a, DiyFp b) {
        DiyFp result = new DiyFp(a.f, a.e);
        result.subtract(b);
        return result;
    }

    void multiply(DiyFp other) {
        long a = this.f >>> 32;
        long b = this.f & 4294967295L;
        long c = other.f >>> 32;
        long d = other.f & 4294967295L;
        long ac = a * c;
        long bc = b * c;
        long ad = a * d;
        long bd = b * d;
        long tmp = (bd >>> 32) + (ad & 4294967295L) + (bc & 4294967295L);
        long result_f = ac + (ad >>> 32) + (bc >>> 32) + ((tmp + 2147483648L) >>> 32);
        this.e += other.e + 64;
        this.f = result_f;
    }

    static DiyFp times(DiyFp a, DiyFp b) {
        DiyFp result = new DiyFp(a.f, a.e);
        result.multiply(b);
        return result;
    }

    void normalize() {
        if (!$assertionsDisabled && this.f == 0) {
            throw new AssertionError();
        }
        long f = this.f;
        int e = this.e;
        while ((f & (-18014398509481984L)) == 0) {
            f <<= 10;
            e -= 10;
        }
        while ((f & kUint64MSB) == 0) {
            f <<= 1;
            e--;
        }
        this.f = f;
        this.e = e;
    }

    static DiyFp normalize(DiyFp a) {
        DiyFp result = new DiyFp(a.f, a.e);
        result.normalize();
        return result;
    }

    long f() {
        return this.f;
    }

    int e() {
        return this.e;
    }

    void setF(long new_value) {
        this.f = new_value;
    }

    void setE(int new_value) {
        this.e = new_value;
    }

    public String toString() {
        return "[DiyFp f:" + this.f + ", e:" + this.e + "]";
    }
}
