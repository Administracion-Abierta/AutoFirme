package com.ctc.wstx.shaded.msv_core.datatype.regexp;

import com.ctc.wstx.api.InvalidCharHandler;
import com.sun.jna.platform.win32.Ddeml;
import com.sun.jna.platform.win32.WinUser;
import java.text.CharacterIterator;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/REUtil.class */
final class REUtil {
    static final int CACHESIZE = 20;
    static final RegularExpression[] regexCache = new RegularExpression[20];

    private REUtil() {
    }

    static final int composeFromSurrogates(int high, int low) {
        return ((65536 + ((high - InvalidCharHandler.FailingHandler.SURR1_FIRST) << 10)) + low) - InvalidCharHandler.FailingHandler.SURR2_FIRST;
    }

    static final boolean isLowSurrogate(int ch) {
        return (ch & Ddeml.XCLASS_MASK) == 56320;
    }

    static final boolean isHighSurrogate(int ch) {
        return (ch & Ddeml.XCLASS_MASK) == 55296;
    }

    static final String decomposeToSurrogates(int ch) {
        int ch2 = ch - 65536;
        char[] chs = {(char) ((ch2 >> 10) + InvalidCharHandler.FailingHandler.SURR1_FIRST), (char) ((ch2 & WinUser.CF_GDIOBJLAST) + InvalidCharHandler.FailingHandler.SURR2_FIRST)};
        return new String(chs);
    }

    static final String substring(CharacterIterator iterator, int begin, int end) {
        char[] src = new char[end - begin];
        for (int i = 0; i < src.length; i++) {
            src[i] = iterator.setIndex(i + begin);
        }
        return new String(src);
    }

    static final int getOptionValue(int ch) {
        int ret = 0;
        switch (ch) {
            case 44:
                ret = 1024;
                break;
            case 70:
                ret = 256;
                break;
            case 72:
                ret = 128;
                break;
            case 88:
                ret = 512;
                break;
            case 105:
                ret = 2;
                break;
            case 109:
                ret = 8;
                break;
            case 115:
                ret = 4;
                break;
            case 117:
                ret = 32;
                break;
            case 119:
                ret = 64;
                break;
            case 120:
                ret = 16;
                break;
        }
        return ret;
    }

    static final int parseOptions(String opts) throws ParseException {
        if (opts == null) {
            return 0;
        }
        int options = 0;
        for (int i = 0; i < opts.length(); i++) {
            int v = getOptionValue(opts.charAt(i));
            if (v == 0) {
                throw new ParseException("Unknown Option: " + opts.substring(i), -1);
            }
            options |= v;
        }
        return options;
    }

    static final String createOptionString(int options) {
        StringBuffer sb = new StringBuffer(9);
        if ((options & 256) != 0) {
            sb.append('F');
        }
        if ((options & 128) != 0) {
            sb.append('H');
        }
        if ((options & 512) != 0) {
            sb.append('X');
        }
        if ((options & 2) != 0) {
            sb.append('i');
        }
        if ((options & 8) != 0) {
            sb.append('m');
        }
        if ((options & 4) != 0) {
            sb.append('s');
        }
        if ((options & 32) != 0) {
            sb.append('u');
        }
        if ((options & 64) != 0) {
            sb.append('w');
        }
        if ((options & 16) != 0) {
            sb.append('x');
        }
        if ((options & 1024) != 0) {
            sb.append(',');
        }
        return sb.toString().intern();
    }

    static String stripExtendedComment(String regex) {
        int len = regex.length();
        StringBuffer buffer = new StringBuffer(len);
        int offset = 0;
        while (offset < len) {
            int i = offset;
            offset++;
            int ch = regex.charAt(i);
            if (ch != 9 && ch != 10 && ch != 12 && ch != 13 && ch != 32) {
                if (ch == 35) {
                    while (offset < len) {
                        int i2 = offset;
                        offset++;
                        int ch2 = regex.charAt(i2);
                        if (ch2 == 13 || ch2 == 10) {
                            break;
                        }
                    }
                } else if (ch == 92 && offset < len) {
                    int next = regex.charAt(offset);
                    if (next == 35 || next == 9 || next == 10 || next == 12 || next == 13 || next == 32) {
                        buffer.append((char) next);
                        offset++;
                    } else {
                        buffer.append('\\');
                        buffer.append((char) next);
                        offset++;
                    }
                } else {
                    buffer.append((char) ch);
                }
            }
        }
        return buffer.toString();
    }

    public static void main(String[] argv) {
        String pattern = null;
        try {
            String options = "";
            String target = null;
            if (argv.length == 0) {
                System.out.println("Error:Usage: java REUtil -i|-m|-s|-u|-w|-X regularExpression String");
                System.exit(0);
            }
            for (int i = 0; i < argv.length; i++) {
                if (argv[i].length() == 0 || argv[i].charAt(0) != '-') {
                    if (pattern == null) {
                        pattern = argv[i];
                    } else if (target == null) {
                        target = argv[i];
                    } else {
                        System.err.println("Unnecessary: " + argv[i]);
                    }
                } else if (argv[i].equals("-i")) {
                    options = options + OperatorName.SET_FLATNESS;
                } else if (argv[i].equals("-m")) {
                    options = options + OperatorName.MOVE_TO;
                } else if (argv[i].equals("-s")) {
                    options = options + OperatorName.CLOSE_AND_STROKE;
                } else if (argv[i].equals("-u")) {
                    options = options + "u";
                } else if (argv[i].equals("-w")) {
                    options = options + OperatorName.SET_LINE_WIDTH;
                } else if (argv[i].equals("-X")) {
                    options = options + "X";
                } else {
                    System.err.println("Unknown option: " + argv[i]);
                }
            }
            RegularExpression reg = new RegularExpression(pattern, options);
            System.out.println("RegularExpression: " + reg);
            Match match = new Match();
            reg.matches(target, match);
            for (int i2 = 0; i2 < match.getNumberOfGroups(); i2++) {
                if (i2 == 0) {
                    System.out.print("Matched range for the whole pattern: ");
                } else {
                    System.out.print("[" + i2 + "]: ");
                }
                if (match.getBeginning(i2) < 0) {
                    System.out.println("-1");
                } else {
                    System.out.print(match.getBeginning(i2) + ", " + match.getEnd(i2) + ", ");
                    System.out.println(OperatorName.SHOW_TEXT_LINE_AND_SPACE + match.getCapturedText(i2) + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
                }
            }
        } catch (ParseException pe) {
            if (pattern == null) {
                pe.printStackTrace();
                return;
            }
            System.err.println("org.apache.xerces.utils.regex.ParseException: " + pe.getMessage());
            System.err.println("        " + pattern);
            int loc = pe.getLocation();
            if (loc >= 0) {
                System.err.print("        ");
                for (int i3 = 0; i3 < loc; i3++) {
                    System.err.print("-");
                }
                System.err.println("^");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RegularExpression createRegex(String pattern, String options) throws ParseException {
        RegularExpression re = null;
        int intOptions = parseOptions(options);
        synchronized (regexCache) {
            int i = 0;
            while (i < 20) {
                RegularExpression cached = regexCache[i];
                if (cached == null) {
                    i = -1;
                    break;
                }
                if (!cached.equals(pattern, intOptions)) {
                    i++;
                } else {
                    re = cached;
                    break;
                }
            }
            if (re != null) {
                if (i != 0) {
                    System.arraycopy(regexCache, 0, regexCache, 1, i);
                    regexCache[0] = re;
                }
            } else {
                re = new RegularExpression(pattern, options);
                System.arraycopy(regexCache, 0, regexCache, 1, 19);
                regexCache[0] = re;
            }
        }
        return re;
    }

    public static boolean matches(String regex, String target) throws ParseException {
        return createRegex(regex, null).matches(target);
    }

    public static boolean matches(String regex, String options, String target) throws ParseException {
        return createRegex(regex, options).matches(target);
    }

    public static String quoteMeta(String literal) {
        int len = literal.length();
        StringBuffer buffer = null;
        for (int i = 0; i < len; i++) {
            int ch = literal.charAt(i);
            if (".*+?{[()|\\^$".indexOf(ch) >= 0) {
                if (buffer == null) {
                    buffer = new StringBuffer(i + ((len - i) * 2));
                    if (i > 0) {
                        buffer.append(literal.substring(0, i));
                    }
                }
                buffer.append('\\');
                buffer.append((char) ch);
            } else if (buffer != null) {
                buffer.append((char) ch);
            }
        }
        return buffer != null ? buffer.toString() : literal;
    }

    static void dumpString(String v) {
        for (int i = 0; i < v.length(); i++) {
            System.out.print(Integer.toHexString(v.charAt(i)));
            System.out.print(" ");
        }
        System.out.println();
    }
}
