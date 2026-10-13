package org.codehaus.stax2.ri.typed;

import com.sun.jna.platform.win32.WinError;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/typed/NumberUtil.class */
public final class NumberUtil {
    public static final int MAX_INT_CLEN = 11;
    public static final int MAX_LONG_CLEN = 21;
    public static final int MAX_DOUBLE_CLEN = 32;
    public static final int MAX_FLOAT_CLEN = 32;
    private static final char NULL_CHAR = 0;
    private static final int MILLION = 1000000;
    private static final int BILLION = 1000000000;
    private static final long TEN_BILLION_L = 10000000000L;
    private static final long THOUSAND_L = 1000;
    private static final byte BYTE_HYPHEN = 45;
    private static final byte BYTE_1 = 49;
    private static final byte BYTE_2 = 50;
    private static long MIN_INT_AS_LONG = -2147483647L;
    private static long MAX_INT_AS_LONG = 2147483647L;
    static final char[] LEADING_TRIPLETS = new char[WinError.ERROR_WINS_INTERNAL];
    static final char[] FULL_TRIPLETS = new char[WinError.ERROR_WINS_INTERNAL];

    static {
        int ix = 0;
        int i1 = 0;
        while (i1 < 10) {
            char f1 = (char) (48 + i1);
            char l1 = i1 == 0 ? (char) 0 : f1;
            int i2 = 0;
            while (i2 < 10) {
                char f2 = (char) (48 + i2);
                char l2 = (i1 == 0 && i2 == 0) ? (char) 0 : f2;
                for (int i3 = 0; i3 < 10; i3++) {
                    char f3 = (char) (48 + i3);
                    LEADING_TRIPLETS[ix] = l1;
                    LEADING_TRIPLETS[ix + 1] = l2;
                    LEADING_TRIPLETS[ix + 2] = f3;
                    FULL_TRIPLETS[ix] = f1;
                    FULL_TRIPLETS[ix + 1] = f2;
                    FULL_TRIPLETS[ix + 2] = f3;
                    ix += 4;
                }
                i2++;
            }
            i1++;
        }
    }

    public static int writeInt(int value, char[] buffer, int offset) {
        int offset2;
        int offset3;
        if (value < 0) {
            if (value == Integer.MIN_VALUE) {
                return writeLong(value, buffer, offset);
            }
            offset++;
            buffer[offset] = '-';
            value = -value;
        }
        if (value < MILLION) {
            if (value < 1000) {
                if (value < 10) {
                    int i = offset;
                    offset3 = offset + 1;
                    buffer[i] = (char) (48 + value);
                } else {
                    offset3 = writeLeadingTriplet(value, buffer, offset);
                }
            } else {
                int thousands = value / 1000;
                offset3 = writeFullTriplet(value - (thousands * 1000), buffer, writeLeadingTriplet(thousands, buffer, offset));
            }
            return offset3;
        }
        boolean hasBillions = value >= BILLION;
        if (hasBillions) {
            value -= BILLION;
            if (value >= BILLION) {
                value -= BILLION;
                int i2 = offset;
                offset++;
                buffer[i2] = '2';
            } else {
                int i3 = offset;
                offset++;
                buffer[i3] = '1';
            }
        }
        int newValue = value / 1000;
        int ones = value - (newValue * 1000);
        int newValue2 = newValue / 1000;
        int thousands2 = newValue - (newValue2 * 1000);
        if (hasBillions) {
            offset2 = writeFullTriplet(newValue2, buffer, offset);
        } else {
            offset2 = writeLeadingTriplet(newValue2, buffer, offset);
        }
        return writeFullTriplet(ones, buffer, writeFullTriplet(thousands2, buffer, offset2));
    }

    public static int writeInt(int value, byte[] buffer, int offset) {
        int offset2;
        int offset3;
        if (value < 0) {
            if (value == Integer.MIN_VALUE) {
                return writeLong(value, buffer, offset);
            }
            offset++;
            buffer[offset] = 45;
            value = -value;
        }
        if (value < MILLION) {
            if (value < 1000) {
                if (value < 10) {
                    int i = offset;
                    offset3 = offset + 1;
                    buffer[i] = (byte) (48 + value);
                } else {
                    offset3 = writeLeadingTriplet(value, buffer, offset);
                }
            } else {
                int thousands = value / 1000;
                offset3 = writeFullTriplet(value - (thousands * 1000), buffer, writeLeadingTriplet(thousands, buffer, offset));
            }
            return offset3;
        }
        boolean hasBillions = value >= BILLION;
        if (hasBillions) {
            value -= BILLION;
            if (value >= BILLION) {
                value -= BILLION;
                int i2 = offset;
                offset++;
                buffer[i2] = 50;
            } else {
                int i3 = offset;
                offset++;
                buffer[i3] = 49;
            }
        }
        int newValue = value / 1000;
        int ones = value - (newValue * 1000);
        int newValue2 = newValue / 1000;
        int thousands2 = newValue - (newValue2 * 1000);
        if (hasBillions) {
            offset2 = writeFullTriplet(newValue2, buffer, offset);
        } else {
            offset2 = writeLeadingTriplet(newValue2, buffer, offset);
        }
        return writeFullTriplet(ones, buffer, writeFullTriplet(thousands2, buffer, offset2));
    }

    public static int writeLong(long value, char[] buffer, int offset) {
        if (value < 0) {
            if (value >= MIN_INT_AS_LONG) {
                return writeInt((int) value, buffer, offset);
            }
            if (value == Long.MIN_VALUE) {
                return getChars(String.valueOf(value), buffer, offset);
            }
            offset++;
            buffer[offset] = '-';
            value = -value;
        } else if (value <= MAX_INT_AS_LONG) {
            return writeInt((int) value, buffer, offset);
        }
        int origOffset = offset;
        int offset2 = offset + calcLongStrLength(value);
        int ptr = offset2;
        while (value > MAX_INT_AS_LONG) {
            ptr -= 3;
            long newValue = value / 1000;
            int triplet = (int) (value - (newValue * 1000));
            writeFullTriplet(triplet, buffer, ptr);
            value = newValue;
        }
        int i = (int) value;
        while (true) {
            int ivalue = i;
            if (ivalue >= 1000) {
                ptr -= 3;
                int newValue2 = ivalue / 1000;
                int triplet2 = ivalue - (newValue2 * 1000);
                writeFullTriplet(triplet2, buffer, ptr);
                i = newValue2;
            } else {
                writeLeadingTriplet(ivalue, buffer, origOffset);
                return offset2;
            }
        }
    }

    public static int writeLong(long value, byte[] buffer, int offset) {
        if (value < 0) {
            if (value >= MIN_INT_AS_LONG) {
                return writeInt((int) value, buffer, offset);
            }
            if (value == Long.MIN_VALUE) {
                return getAsciiBytes(String.valueOf(value), buffer, offset);
            }
            offset++;
            buffer[offset] = 45;
            value = -value;
        } else if (value <= MAX_INT_AS_LONG) {
            return writeInt((int) value, buffer, offset);
        }
        int origOffset = offset;
        int offset2 = offset + calcLongStrLength(value);
        int ptr = offset2;
        while (value > MAX_INT_AS_LONG) {
            ptr -= 3;
            long newValue = value / 1000;
            int triplet = (int) (value - (newValue * 1000));
            writeFullTriplet(triplet, buffer, ptr);
            value = newValue;
        }
        int i = (int) value;
        while (true) {
            int ivalue = i;
            if (ivalue >= 1000) {
                ptr -= 3;
                int newValue2 = ivalue / 1000;
                int triplet2 = ivalue - (newValue2 * 1000);
                writeFullTriplet(triplet2, buffer, ptr);
                i = newValue2;
            } else {
                writeLeadingTriplet(ivalue, buffer, origOffset);
                return offset2;
            }
        }
    }

    public static int writeFloat(float value, char[] buffer, int offset) {
        return getChars(String.valueOf(value), buffer, offset);
    }

    public static int writeFloat(float value, byte[] buffer, int offset) {
        return getAsciiBytes(String.valueOf(value), buffer, offset);
    }

    public static int writeDouble(double value, char[] buffer, int offset) {
        return getChars(String.valueOf(value), buffer, offset);
    }

    public static int writeDouble(double value, byte[] buffer, int offset) {
        return getAsciiBytes(String.valueOf(value), buffer, offset);
    }

    private static int writeLeadingTriplet(int triplet, char[] buffer, int offset) {
        int digitOffset = triplet << 2;
        int digitOffset2 = digitOffset + 1;
        char c = LEADING_TRIPLETS[digitOffset];
        if (c != 0) {
            offset++;
            buffer[offset] = c;
        }
        int digitOffset3 = digitOffset2 + 1;
        char c2 = LEADING_TRIPLETS[digitOffset2];
        if (c2 != 0) {
            int i = offset;
            offset++;
            buffer[i] = c2;
        }
        int i2 = offset;
        int offset2 = offset + 1;
        buffer[i2] = LEADING_TRIPLETS[digitOffset3];
        return offset2;
    }

    private static int writeLeadingTriplet(int triplet, byte[] buffer, int offset) {
        int digitOffset = triplet << 2;
        int digitOffset2 = digitOffset + 1;
        char c = LEADING_TRIPLETS[digitOffset];
        if (c != 0) {
            offset++;
            buffer[offset] = (byte) c;
        }
        int digitOffset3 = digitOffset2 + 1;
        char c2 = LEADING_TRIPLETS[digitOffset2];
        if (c2 != 0) {
            int i = offset;
            offset++;
            buffer[i] = (byte) c2;
        }
        int i2 = offset;
        int offset2 = offset + 1;
        buffer[i2] = (byte) LEADING_TRIPLETS[digitOffset3];
        return offset2;
    }

    private static int writeFullTriplet(int triplet, char[] buffer, int offset) {
        int digitOffset = triplet << 2;
        int offset2 = offset + 1;
        int digitOffset2 = digitOffset + 1;
        buffer[offset] = FULL_TRIPLETS[digitOffset];
        int offset3 = offset2 + 1;
        buffer[offset2] = FULL_TRIPLETS[digitOffset2];
        int offset4 = offset3 + 1;
        buffer[offset3] = FULL_TRIPLETS[digitOffset2 + 1];
        return offset4;
    }

    private static int writeFullTriplet(int triplet, byte[] buffer, int offset) {
        int digitOffset = triplet << 2;
        int offset2 = offset + 1;
        int digitOffset2 = digitOffset + 1;
        buffer[offset] = (byte) FULL_TRIPLETS[digitOffset];
        int offset3 = offset2 + 1;
        buffer[offset2] = (byte) FULL_TRIPLETS[digitOffset2];
        int offset4 = offset3 + 1;
        buffer[offset3] = (byte) FULL_TRIPLETS[digitOffset2 + 1];
        return offset4;
    }

    private static int calcLongStrLength(long posValue) {
        int len = 10;
        long j = TEN_BILLION_L;
        while (true) {
            long comp = j;
            if (posValue < comp || len == 19) {
                break;
            }
            len++;
            j = (comp << 3) + (comp << 1);
        }
        return len;
    }

    private static int getChars(String str, char[] buffer, int ptr) {
        int len = str.length();
        str.getChars(0, len, buffer, ptr);
        return ptr + len;
    }

    private static int getAsciiBytes(String str, byte[] buffer, int ptr) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            int i2 = ptr;
            ptr++;
            buffer[i2] = (byte) str.charAt(i);
        }
        return ptr;
    }
}
