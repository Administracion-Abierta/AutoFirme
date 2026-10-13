package com.ctc.wstx.util;

import java.util.Collection;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/util/StringUtil.class */
public final class StringUtil {
    static final char CHAR_SPACE = ' ';
    private static final char INT_SPACE = ' ';
    static String sLF = null;
    private static final int EOS = 65536;

    public static String getLF() {
        String lf = sLF;
        if (lf == null) {
            try {
                lf = System.getProperty("line.separator");
                sLF = lf == null ? "\n" : lf;
            } catch (Throwable th) {
                lf = "\n";
                sLF = "\n";
            }
        }
        return lf;
    }

    public static void appendLF(StringBuilder sb) {
        sb.append(getLF());
    }

    public static String concatEntries(Collection<?> coll, String sep, String lastSep) {
        if (lastSep == null) {
            lastSep = sep;
        }
        int len = coll.size();
        StringBuilder sb = new StringBuilder(16 + (len << 3));
        Iterator<?> it = coll.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (i != 0) {
                if (i == len - 1) {
                    sb.append(lastSep);
                } else {
                    sb.append(sep);
                }
            }
            i++;
            sb.append(it.next());
        }
        return sb.toString();
    }

    public static String normalizeSpaces(char[] buf, int origStart, int origEnd) {
        char c;
        int origEnd2 = origEnd - 1;
        int start = origStart;
        int end = origEnd2;
        while (start <= end && buf[start] == ' ') {
            start++;
        }
        if (start > end) {
            return "";
        }
        while (end > start && buf[end] == ' ') {
            end--;
        }
        int i = start + 1;
        while (i < end) {
            if (buf[i] == ' ') {
                if (buf[i + 1] == ' ') {
                    break;
                }
                i += 2;
            } else {
                i++;
            }
        }
        if (i >= end) {
            if (start == origStart && end == origEnd2) {
                return null;
            }
            return new String(buf, start, (end - start) + 1);
        }
        StringBuilder sb = new StringBuilder(end - start);
        sb.append(buf, start, i - start);
        while (i <= end) {
            int i2 = i;
            i++;
            char c2 = buf[i2];
            if (c2 == ' ') {
                sb.append(' ');
                do {
                    int i3 = i;
                    i++;
                    c = buf[i3];
                } while (c == ' ');
                sb.append(c);
            } else {
                sb.append(c2);
            }
        }
        return sb.toString();
    }

    public static boolean isAllWhitespace(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (str.charAt(i) > ' ') {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllWhitespace(char[] ch, int start, int len) {
        int len2 = len + start;
        while (start < len2) {
            if (ch[start] <= ' ') {
                start++;
            } else {
                return false;
            }
        }
        return true;
    }

    /*  JADX ERROR: Type inference failed
        jadx.core.utils.exceptions.JadxOverflowException: Type inference error: updates count limit reached with updateSeq = 2891. Try increasing type updates limit count.
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:79)
        */
    public static boolean equalEncodings(java.lang.String r3, java.lang.String r4) {
        /*
            Method dump skipped, instruction units count: 289
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ctc.wstx.util.StringUtil.equalEncodings(java.lang.String, java.lang.String):boolean");
    }

    public static boolean encodingStartsWith(String enc, String prefix) {
        int iCharAt;
        int iCharAt2;
        int iCharAt3;
        int iCharAt4;
        int len1 = enc.length();
        int len2 = prefix.length();
        int i1 = 0;
        int i2 = 0;
        while (true) {
            if (i1 < len1 || i2 < len2) {
                if (i1 >= len1) {
                    iCharAt = 65536;
                } else {
                    int i = i1;
                    i1++;
                    iCharAt = enc.charAt(i);
                }
                int c1 = iCharAt;
                if (i2 >= len2) {
                    iCharAt2 = 65536;
                } else {
                    int i3 = i2;
                    i2++;
                    iCharAt2 = prefix.charAt(i3);
                }
                int c2 = iCharAt2;
                if (c1 != c2) {
                    while (true) {
                        if (c1 > 32 && c1 != 95 && c1 != 45) {
                            break;
                        }
                        if (i1 >= len1) {
                            iCharAt3 = 65536;
                        } else {
                            int i4 = i1;
                            i1++;
                            iCharAt3 = enc.charAt(i4);
                        }
                        c1 = iCharAt3;
                    }
                    while (true) {
                        if (c2 > 32 && c2 != 95 && c2 != 45) {
                            break;
                        }
                        if (i2 >= len2) {
                            iCharAt4 = 65536;
                        } else {
                            int i5 = i2;
                            i2++;
                            iCharAt4 = prefix.charAt(i5);
                        }
                        c2 = iCharAt4;
                    }
                    if (c1 == c2) {
                        continue;
                    } else {
                        if (c2 == 65536) {
                            return true;
                        }
                        if (c1 == 65536 || Character.toLowerCase((char) c1) != Character.toLowerCase((char) c2)) {
                            return false;
                        }
                    }
                }
            } else {
                return true;
            }
        }
    }

    public static String trimEncoding(String str, boolean upperCase) {
        char c;
        int i = 0;
        int len = str.length();
        while (i < len && (c = str.charAt(i)) > ' ' && Character.isLetterOrDigit(c)) {
            i++;
        }
        if (i == len) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        if (i > 0) {
            sb.append(str.substring(0, i));
        }
        while (i < len) {
            char c2 = str.charAt(i);
            if (c2 > ' ' && Character.isLetterOrDigit(c2)) {
                if (upperCase) {
                    c2 = Character.toUpperCase(c2);
                }
                sb.append(c2);
            }
            i++;
        }
        return sb.toString();
    }

    public static boolean matches(String str, char[] cbuf, int offset, int len) {
        if (str.length() != len) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (str.charAt(i) != cbuf[offset + i]) {
                return false;
            }
        }
        return true;
    }

    public static final boolean isSpace(char c) {
        return c <= ' ';
    }
}
