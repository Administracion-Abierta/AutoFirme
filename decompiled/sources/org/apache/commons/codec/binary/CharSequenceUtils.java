package org.apache.commons.codec.binary;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/binary/CharSequenceUtils.class */
public class CharSequenceUtils {
    static boolean regionMatches(CharSequence cs, boolean ignoreCase, int thisStart, CharSequence substring, int start, int length) {
        if ((cs instanceof String) && (substring instanceof String)) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        }
        int index1 = thisStart;
        int index2 = start;
        int tmpLen = length;
        while (true) {
            int i = tmpLen;
            tmpLen--;
            if (i > 0) {
                int i2 = index1;
                index1++;
                char c1 = cs.charAt(i2);
                int i3 = index2;
                index2++;
                char c2 = substring.charAt(i3);
                if (c1 != c2) {
                    if (!ignoreCase) {
                        return false;
                    }
                    if (Character.toUpperCase(c1) != Character.toUpperCase(c2) && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                        return false;
                    }
                }
            } else {
                return true;
            }
        }
    }
}
