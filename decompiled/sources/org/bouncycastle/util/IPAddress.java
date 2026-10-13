package org.bouncycastle.util;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/IPAddress.class */
public class IPAddress {
    public static boolean isValid(String str) {
        return isValidIPv4(str) || isValidIPv6(str);
    }

    public static boolean isValidWithNetMask(String str) {
        return isValidIPv4WithNetmask(str) || isValidIPv6WithNetmask(str);
    }

    public static boolean isValidIPv4(String str) {
        int length = str.length();
        if (length < 7 || length > 15) {
            return false;
        }
        int i = 0;
        for (int i2 = 0; i2 < 3; i2++) {
            int iIndexOf = str.indexOf(46, i);
            if (!isParseableIPv4Octet(str, i, iIndexOf)) {
                return false;
            }
            i = iIndexOf + 1;
        }
        return isParseableIPv4Octet(str, i, length);
    }

    public static boolean isValidIPv4WithNetmask(String str) {
        int iIndexOf = str.indexOf("/");
        if (iIndexOf < 1) {
            return false;
        }
        String strSubstring = str.substring(0, iIndexOf);
        String strSubstring2 = str.substring(iIndexOf + 1);
        return isValidIPv4(strSubstring) && (isValidIPv4(strSubstring2) || isParseableIPv4Mask(strSubstring2));
    }

    public static boolean isValidIPv6(String str) {
        int iIndexOf;
        if (str.length() == 0) {
            return false;
        }
        char cCharAt = str.charAt(0);
        if (cCharAt != ':' && Character.digit(cCharAt, 16) < 0) {
            return false;
        }
        int i = 0;
        String str2 = str + ":";
        boolean z = false;
        int i2 = 0;
        while (i2 < str2.length() && (iIndexOf = str2.indexOf(58, i2)) >= i2) {
            if (i == 8) {
                return false;
            }
            if (i2 != iIndexOf) {
                String strSubstring = str2.substring(i2, iIndexOf);
                if (iIndexOf == str2.length() - 1 && strSubstring.indexOf(46) > 0) {
                    i++;
                    if (i == 8 || !isValidIPv4(strSubstring)) {
                        return false;
                    }
                } else if (!isParseableIPv6Segment(str2, i2, iIndexOf)) {
                    return false;
                }
            } else {
                if (iIndexOf != 1 && iIndexOf != str2.length() - 1 && z) {
                    return false;
                }
                z = true;
            }
            i2 = iIndexOf + 1;
            i++;
        }
        return i == 8 || z;
    }

    public static boolean isValidIPv6WithNetmask(String str) {
        int iIndexOf = str.indexOf("/");
        if (iIndexOf < 1) {
            return false;
        }
        String strSubstring = str.substring(0, iIndexOf);
        String strSubstring2 = str.substring(iIndexOf + 1);
        return isValidIPv6(strSubstring) && (isValidIPv6(strSubstring2) || isParseableIPv6Mask(strSubstring2));
    }

    private static boolean isParseableIPv4Mask(String str) {
        return isParseable(str, 0, str.length(), 10, 2, false, 0, 32);
    }

    private static boolean isParseableIPv4Octet(String str, int i, int i2) {
        return isParseable(str, i, i2, 10, 3, true, 0, 255);
    }

    private static boolean isParseableIPv6Mask(String str) {
        return isParseable(str, 0, str.length(), 10, 3, false, 1, 128);
    }

    private static boolean isParseableIPv6Segment(String str, int i, int i2) {
        return isParseable(str, i, i2, 16, 4, true, 0, 65535);
    }

    private static boolean isParseable(String str, int i, int i2, int i3, int i4, boolean z, int i5, int i6) {
        int i7 = i2 - i;
        if ((i7 < 1) || (i7 > i4)) {
            return false;
        }
        if (((i7 > 1) && (!z)) && Character.digit(str.charAt(i), i3) <= 0) {
            return false;
        }
        int i8 = 0;
        while (true) {
            int i9 = i8;
            if (i >= i2) {
                return (i9 >= i5) & (i9 <= i6);
            }
            int i10 = i;
            i++;
            int iDigit = Character.digit(str.charAt(i10), i3);
            if (iDigit < 0) {
                return false;
            }
            i8 = (i9 * i3) + iDigit;
        }
    }
}
