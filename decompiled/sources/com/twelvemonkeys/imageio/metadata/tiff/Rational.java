package com.twelvemonkeys.imageio.metadata.tiff;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/tiff/Rational.class */
public final class Rational extends Number implements Comparable<Rational> {
    static final Rational ZERO = new Rational(0, 1);
    static final Rational NaN = new Rational();
    private final long numerator;
    private final long denominator;

    private Rational() {
        this.numerator = 0L;
        this.denominator = 0L;
    }

    public Rational(long j) {
        this(j, 1L);
    }

    public Rational(long j, long j2) {
        if (j2 == 0) {
            throw new IllegalArgumentException("denominator == 0");
        }
        if (j == Long.MIN_VALUE || j2 == Long.MIN_VALUE) {
            throw new IllegalArgumentException("value == Long.MIN_VALUE");
        }
        long jGcd = gcd(j, j2);
        long j3 = j / jGcd;
        long j4 = j2 / jGcd;
        this.numerator = j2 >= 0 ? j3 : -j3;
        this.denominator = j2 >= 0 ? j4 : -j4;
    }

    private static long gcd(long j, long j2) {
        if (j < 0) {
            return gcd(j2, -j);
        }
        return j2 == 0 ? j : gcd(j2, j % j2);
    }

    private static long lcm(long j, long j2) {
        if (j < 0) {
            return lcm(j2, -j);
        }
        return j * (j2 / gcd(j, j2));
    }

    public long numerator() {
        return this.numerator;
    }

    public long denominator() {
        return this.denominator;
    }

    @Override // java.lang.Number
    public int intValue() {
        return (int) doubleValue();
    }

    @Override // java.lang.Number
    public long longValue() {
        return (long) doubleValue();
    }

    @Override // java.lang.Number
    public float floatValue() {
        return (float) doubleValue();
    }

    @Override // java.lang.Number
    public double doubleValue() {
        if (this == NaN) {
            return Double.NaN;
        }
        return this.numerator / this.denominator;
    }

    @Override // java.lang.Comparable
    public int compareTo(Rational rational) {
        return Double.compare(doubleValue(), rational.doubleValue());
    }

    public int hashCode() {
        return Float.floatToIntBits(floatValue());
    }

    public boolean equals(Object obj) {
        return obj == this || ((obj instanceof Rational) && compareTo((Rational) obj) == 0);
    }

    public String toString() {
        if (this == NaN) {
            return "NaN";
        }
        return this.denominator == 1 ? Long.toString(this.numerator) : String.format("%s/%s", Long.valueOf(this.numerator), Long.valueOf(this.denominator));
    }

    public Rational times(Rational rational) {
        if (equals(ZERO) || rational.equals(ZERO)) {
            return ZERO;
        }
        Rational rational2 = new Rational(this.numerator, rational.denominator);
        Rational rational3 = new Rational(rational.numerator, this.denominator);
        return new Rational(rational2.numerator * rational3.numerator, rational2.denominator * rational3.denominator);
    }

    public Rational plus(Rational rational) {
        if (equals(ZERO)) {
            return rational;
        }
        if (rational.equals(ZERO)) {
            return this;
        }
        long jGcd = gcd(this.numerator, rational.numerator);
        long jGcd2 = gcd(this.denominator, rational.denominator);
        return new Rational((((this.numerator / jGcd) * (rational.denominator / jGcd2)) + ((rational.numerator / jGcd) * (this.denominator / jGcd2))) * jGcd, lcm(this.denominator, rational.denominator));
    }

    public Rational negate() {
        return new Rational(-this.numerator, this.denominator);
    }

    public Rational minus(Rational rational) {
        return plus(rational.negate());
    }

    public Rational reciprocal() {
        return new Rational(this.denominator, this.numerator);
    }

    public Rational divides(Rational rational) {
        if (rational.equals(ZERO)) {
            throw new ArithmeticException("/ by zero");
        }
        return times(rational.reciprocal());
    }
}
