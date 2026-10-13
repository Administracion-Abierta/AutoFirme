package com.twelvemonkeys.imageio.metadata.exif;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/exif/Rational.class */
@Deprecated
public final class Rational extends Number implements Comparable<Rational> {
    private final com.twelvemonkeys.imageio.metadata.tiff.Rational delegate;

    public Rational(long j) {
        this(new com.twelvemonkeys.imageio.metadata.tiff.Rational(j, 1L));
    }

    public Rational(long j, long j2) {
        this(new com.twelvemonkeys.imageio.metadata.tiff.Rational(j, j2));
    }

    private Rational(com.twelvemonkeys.imageio.metadata.tiff.Rational rational) {
        this.delegate = rational;
    }

    public long numerator() {
        return this.delegate.numerator();
    }

    public long denominator() {
        return this.delegate.denominator();
    }

    @Override // java.lang.Number
    public byte byteValue() {
        return this.delegate.byteValue();
    }

    @Override // java.lang.Number
    public short shortValue() {
        return this.delegate.shortValue();
    }

    @Override // java.lang.Number
    public int intValue() {
        return this.delegate.intValue();
    }

    @Override // java.lang.Number
    public long longValue() {
        return this.delegate.longValue();
    }

    @Override // java.lang.Number
    public float floatValue() {
        return this.delegate.floatValue();
    }

    @Override // java.lang.Number
    public double doubleValue() {
        return this.delegate.doubleValue();
    }

    @Override // java.lang.Comparable
    public int compareTo(Rational rational) {
        return this.delegate.compareTo(rational.delegate);
    }

    public int hashCode() {
        return this.delegate.hashCode();
    }

    public boolean equals(Object obj) {
        return obj == this || ((obj instanceof Rational) && this.delegate.equals(((Rational) obj).delegate));
    }

    public String toString() {
        return this.delegate.toString();
    }
}
