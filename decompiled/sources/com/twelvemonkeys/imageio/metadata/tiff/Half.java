package com.twelvemonkeys.imageio.metadata.tiff;

import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/tiff/Half.class */
public final class Half extends Number implements Comparable<Half> {
    public static final int SIZE = 16;
    private final short shortBits;
    private final transient float floatValue;

    public Half(short s) {
        this.shortBits = s;
        this.floatValue = shortBitsToFloat(s);
    }

    @Override // java.lang.Number
    public int intValue() {
        return (int) this.floatValue;
    }

    @Override // java.lang.Number
    public long longValue() {
        return (long) this.floatValue;
    }

    @Override // java.lang.Number
    public float floatValue() {
        return this.floatValue;
    }

    @Override // java.lang.Number
    public double doubleValue() {
        return this.floatValue;
    }

    public int hashCode() {
        return this.shortBits;
    }

    public boolean equals(Object obj) {
        return (obj instanceof Half) && ((Half) obj).shortBits == this.shortBits;
    }

    @Override // java.lang.Comparable
    public int compareTo(Half half) {
        return Float.compare(this.floatValue, half.floatValue);
    }

    public String toString() {
        return Float.toString(this.floatValue);
    }

    public static Half valueOf(String str) throws NumberFormatException {
        return new Half(parseHalf(str));
    }

    public static short parseHalf(String str) throws NumberFormatException {
        return floatToShortBits(Float.parseFloat(str));
    }

    public static float shortBitsToFloat(short s) {
        int i = s & 1023;
        int i2 = s & 31744;
        if (i2 == 31744) {
            i2 = 261120;
        } else if (i2 != 0) {
            i2 += 114688;
        } else if (i != 0) {
            i2 = 115712;
            do {
                i <<= 1;
                i2 -= 1024;
            } while ((i & 1024) == 0);
            i &= WinUser.CF_GDIOBJLAST;
        }
        return Float.intBitsToFloat(((s & Short.MIN_VALUE) << 16) | ((i2 | i) << 13));
    }

    public static short floatToShortBits(float f) {
        return (short) floatTo16Bits(f);
    }

    private static int floatTo16Bits(float f) {
        int iFloatToIntBits = Float.floatToIntBits(f);
        int i = (iFloatToIntBits >>> 16) & 32768;
        int i2 = (iFloatToIntBits & WinNT.MAXLONG) + 4096;
        if (i2 >= 1199570944) {
            if ((iFloatToIntBits & WinNT.MAXLONG) >= 1199570944) {
                if (i2 < 2139095040) {
                    return i | 31744;
                }
                return i | 31744 | ((iFloatToIntBits & 8388607) >>> 13);
            }
            return i | 31743;
        }
        if (i2 >= 947912704) {
            return i | ((i2 - 939524096) >>> 13);
        }
        if (i2 < 855638016) {
            return i;
        }
        int i3 = (iFloatToIntBits & WinNT.MAXLONG) >>> 23;
        return i | ((((iFloatToIntBits & 8388607) | 8388608) + (8388608 >>> (i3 - 102))) >>> (126 - i3));
    }

    private Object readResolve() {
        return new Half(this.shortBits);
    }
}
