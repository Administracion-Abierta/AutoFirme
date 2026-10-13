package com.ctc.wstx.shaded.msv_core.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/util/Uri.class */
public class Uri {
    private static final String HEX_DIGITS = "0123456789abcdef";
    private static String utf8 = "UTF-8";
    private static String excluded = "<>\"{}|\\^`";

    public static boolean isValid(String s) {
        return isValidPercent(s) && isValidFragment(s) && isValidScheme(s);
    }

    public static String escapeDisallowedChars(String s) {
        byte[] bytes;
        StringBuffer buf = null;
        int len = s.length();
        int i = 0;
        while (true) {
            int done = i;
            int i2 = done;
            while (true) {
                if (i2 == len) {
                    if (done != 0) {
                        break;
                    }
                    return s;
                }
                if (isExcluded(s.charAt(i2))) {
                    break;
                }
                i2++;
            }
            if (buf == null) {
                buf = new StringBuffer();
            }
            if (i2 > done) {
                buf.append(s.substring(done, i2));
                done = i2;
            }
            if (i2 != len) {
                do {
                    i2++;
                    if (i2 >= len) {
                        break;
                    }
                } while (isExcluded(s.charAt(i2)));
                String tem = s.substring(done, i2);
                try {
                    bytes = tem.getBytes(utf8);
                } catch (UnsupportedEncodingException e) {
                    utf8 = "UTF8";
                    try {
                        bytes = tem.getBytes(utf8);
                    } catch (UnsupportedEncodingException e2) {
                        return s;
                    }
                }
                for (int j = 0; j < bytes.length; j++) {
                    buf.append('%');
                    buf.append(HEX_DIGITS.charAt((bytes[j] & 255) >> 4));
                    buf.append(HEX_DIGITS.charAt(bytes[j] & 15));
                }
                i = i2;
            } else {
                return buf.toString();
            }
        }
    }

    private static boolean isExcluded(char c) {
        return c <= ' ' || c >= 127 || excluded.indexOf(c) >= 0;
    }

    private static boolean isAlpha(char c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
    }

    private static boolean isHexDigit(char c) {
        return ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F') || isDigit(c);
    }

    private static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    private static boolean isSchemeChar(char c) {
        return isAlpha(c) || isDigit(c) || c == '+' || c == '-' || c == '.';
    }

    private static boolean isValidPercent(String s) {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (s.charAt(i) == '%' && (i + 2 >= len || !isHexDigit(s.charAt(i + 1)) || !isHexDigit(s.charAt(i + 2)))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidFragment(String s) {
        int i = s.indexOf(35);
        return i < 0 || s.indexOf(35, i + 1) < 0;
    }

    private static boolean isValidScheme(String s) {
        if (!isAbsolute(s)) {
            return true;
        }
        int i = s.indexOf(58);
        if (i == 0 || i + 1 == s.length() || !isAlpha(s.charAt(0))) {
            return false;
        }
        do {
            i--;
            if (i <= 0) {
                return true;
            }
        } while (isSchemeChar(s.charAt(i)));
        return false;
    }

    public static String resolve(String baseUri, String uriReference) {
        if (!isAbsolute(uriReference) && baseUri != null && isAbsolute(baseUri)) {
            try {
                return new URL(new URL(baseUri), uriReference).toString();
            } catch (MalformedURLException e) {
            }
        }
        return uriReference;
    }

    public static boolean hasFragmentId(String uri) {
        return uri.indexOf(35) >= 0;
    }

    public static boolean isAbsolute(String uri) {
        int i = uri.indexOf(58);
        if (i < 0) {
            return false;
        }
        while (true) {
            i--;
            if (i >= 0) {
                switch (uri.charAt(i)) {
                    case '#':
                    case '/':
                    case '?':
                        return false;
                    default:
                        break;
                }
            } else {
                return true;
            }
        }
    }
}
