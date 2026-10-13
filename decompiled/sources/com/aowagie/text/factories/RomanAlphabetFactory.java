package com.aowagie.text.factories;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/factories/RomanAlphabetFactory.class */
public class RomanAlphabetFactory {
    private static final String getString(int index) {
        if (index < 1) {
            throw new NumberFormatException("You can't translate a negative number into an alphabetical value.");
        }
        int index2 = index - 1;
        int bytes = 1;
        int start = 0;
        int i = 26;
        while (true) {
            int symbols = i;
            if (index2 < symbols + start) {
                break;
            }
            bytes++;
            start += symbols;
            i = symbols * 26;
        }
        int c = index2 - start;
        char[] value = new char[bytes];
        while (bytes > 0) {
            bytes--;
            value[bytes] = (char) (97 + (c % 26));
            c /= 26;
        }
        return new String(value);
    }

    private static final String getLowerCaseString(int index) {
        return getString(index);
    }

    private static final String getUpperCaseString(int index) {
        return getString(index).toUpperCase();
    }

    public static final String getString(int index, boolean lowercase) {
        if (lowercase) {
            return getLowerCaseString(index);
        }
        return getUpperCaseString(index);
    }
}
