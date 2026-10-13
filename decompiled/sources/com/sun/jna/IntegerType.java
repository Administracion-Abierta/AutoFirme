package com.sun.jna;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/IntegerType.class */
public abstract class IntegerType extends Number implements NativeMapped {
    private static final long serialVersionUID = 1;
    private int size;
    private Number number;
    private boolean unsigned;
    private long value;

    public IntegerType(int size) {
        this(size, 0L, false);
    }

    public IntegerType(int size, boolean unsigned) {
        this(size, 0L, unsigned);
    }

    public IntegerType(int size, long value) {
        this(size, value, false);
    }

    public IntegerType(int size, long value, boolean unsigned) {
        this.size = size;
        this.unsigned = unsigned;
        setValue(value);
    }

    public void setValue(long value) {
        long truncated = value;
        this.value = value;
        switch (this.size) {
            case 1:
                if (this.unsigned) {
                    this.value = value & 255;
                }
                truncated = (byte) value;
                this.number = Byte.valueOf((byte) value);
                break;
            case 2:
                if (this.unsigned) {
                    this.value = value & 65535;
                }
                truncated = (short) value;
                this.number = Short.valueOf((short) value);
                break;
            case 3:
            case 5:
            case 6:
            case 7:
            default:
                throw new IllegalArgumentException("Unsupported size: " + this.size);
            case 4:
                if (this.unsigned) {
                    this.value = value & 4294967295L;
                }
                truncated = (int) value;
                this.number = Integer.valueOf((int) value);
                break;
            case 8:
                this.number = Long.valueOf(value);
                break;
        }
        if (this.size < 8) {
            long mask = ((serialVersionUID << (this.size * 8)) - serialVersionUID) ^ (-1);
            if ((value < 0 && truncated != value) || (value >= 0 && (mask & value) != 0)) {
                throw new IllegalArgumentException("Argument value 0x" + Long.toHexString(value) + " exceeds native capacity (" + this.size + " bytes) mask=0x" + Long.toHexString(mask));
            }
        }
    }

    @Override // com.sun.jna.NativeMapped
    public Object toNative() {
        return this.number;
    }

    @Override // com.sun.jna.NativeMapped
    public Object fromNative(Object nativeValue, FromNativeContext context) {
        long value = nativeValue == null ? 0L : ((Number) nativeValue).longValue();
        IntegerType number = (IntegerType) Klass.newInstance(getClass());
        number.setValue(value);
        return number;
    }

    @Override // com.sun.jna.NativeMapped
    public Class<?> nativeType() {
        return this.number.getClass();
    }

    @Override // java.lang.Number
    public int intValue() {
        return (int) this.value;
    }

    @Override // java.lang.Number
    public long longValue() {
        return this.value;
    }

    @Override // java.lang.Number
    public float floatValue() {
        return this.number.floatValue();
    }

    @Override // java.lang.Number
    public double doubleValue() {
        return this.number.doubleValue();
    }

    public boolean equals(Object rhs) {
        return (rhs instanceof IntegerType) && this.number.equals(((IntegerType) rhs).number);
    }

    public String toString() {
        return this.number.toString();
    }

    public int hashCode() {
        return this.number.hashCode();
    }

    public static <T extends IntegerType> int compare(T v1, T v2) {
        if (v1 == v2) {
            return 0;
        }
        if (v1 == null) {
            return 1;
        }
        if (v2 == null) {
            return -1;
        }
        return compare(v1.longValue(), v2.longValue());
    }

    public static int compare(IntegerType v1, long v2) {
        if (v1 == null) {
            return 1;
        }
        return compare(v1.longValue(), v2);
    }

    public static final int compare(long v1, long v2) {
        if (v1 == v2) {
            return 0;
        }
        if (v1 < v2) {
            return -1;
        }
        return 1;
    }
}
