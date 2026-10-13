package org.apache.pdfbox.util;

import com.aowagie.text.pdf.ColumnText;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/util/NumberFormatUtil.class */
public class NumberFormatUtil {
    private static final int MAX_FRACTION_DIGITS = 5;
    private static final long[] POWER_OF_TENS = new long[19];
    private static final int[] POWER_OF_TENS_INT;

    static {
        POWER_OF_TENS[0] = 1;
        for (int exp = 1; exp < POWER_OF_TENS.length; exp++) {
            POWER_OF_TENS[exp] = POWER_OF_TENS[exp - 1] * 10;
        }
        POWER_OF_TENS_INT = new int[10];
        POWER_OF_TENS_INT[0] = 1;
        for (int exp2 = 1; exp2 < POWER_OF_TENS_INT.length; exp2++) {
            POWER_OF_TENS_INT[exp2] = POWER_OF_TENS_INT[exp2 - 1] * 10;
        }
    }

    private NumberFormatUtil() {
    }

    public static int formatFloatFast(float value, int maxFractionDigits, byte[] asciiBuffer) {
        if (Float.isNaN(value) || Float.isInfinite(value) || value > 9.223372E18f || value <= -9.223372E18f || maxFractionDigits > 5) {
            return -1;
        }
        int offset = 0;
        long integerPart = (long) value;
        if (value < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            offset = 0 + 1;
            asciiBuffer[0] = 45;
            integerPart = -integerPart;
        }
        long fractionPart = (long) (((Math.abs(value) - integerPart) * POWER_OF_TENS[maxFractionDigits]) + 0.5d);
        if (fractionPart >= POWER_OF_TENS[maxFractionDigits]) {
            integerPart++;
            fractionPart -= POWER_OF_TENS[maxFractionDigits];
        }
        int offset2 = formatPositiveNumber(integerPart, getExponent(integerPart), false, asciiBuffer, offset);
        if (fractionPart > 0 && maxFractionDigits > 0) {
            asciiBuffer[offset2] = 46;
            offset2 = formatPositiveNumber(fractionPart, maxFractionDigits - 1, true, asciiBuffer, offset2 + 1);
        }
        return offset2;
    }

    private static int formatPositiveNumber(long number, int exp, boolean omitTrailingZeros, byte[] asciiBuffer, int startOffset) {
        int offset = startOffset;
        long remaining = number;
        while (remaining > 2147483647L && (!omitTrailingZeros || remaining > 0)) {
            long digit = remaining / POWER_OF_TENS[exp];
            remaining -= digit * POWER_OF_TENS[exp];
            int i = offset;
            offset++;
            asciiBuffer[i] = (byte) (48 + digit);
            exp--;
        }
        int remainingInt = (int) remaining;
        while (exp >= 0 && (!omitTrailingZeros || remainingInt > 0)) {
            int digit2 = remainingInt / POWER_OF_TENS_INT[exp];
            remainingInt -= digit2 * POWER_OF_TENS_INT[exp];
            int i2 = offset;
            offset++;
            asciiBuffer[i2] = (byte) (48 + digit2);
            exp--;
        }
        return offset;
    }

    private static int getExponent(long number) {
        for (int exp = 0; exp < POWER_OF_TENS.length - 1; exp++) {
            if (number < POWER_OF_TENS[exp + 1]) {
                return exp;
            }
        }
        return POWER_OF_TENS.length - 1;
    }
}
