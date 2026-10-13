package nu.xom;

import java.io.UnsupportedEncodingException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/URIUtil.class */
class URIUtil {

    /* JADX INFO: loaded from: autofirma.jar:nu/xom/URIUtil$ParsedURI.class */
    static class ParsedURI {
        String scheme;
        String schemeSpecificPart;
        String query;
        String fragment;
        String authority;
        String path;

        ParsedURI(String str) {
            this.path = "";
            int iIndexOf = str.indexOf(58);
            int iLastIndexOf = str.lastIndexOf(35);
            int iIndexOf2 = iLastIndexOf == -1 ? str.indexOf(63) : str.substring(0, iLastIndexOf).indexOf(63);
            if (iIndexOf != -1) {
                this.scheme = str.substring(0, iIndexOf);
            }
            if (iIndexOf2 == -1 && iLastIndexOf == -1) {
                this.schemeSpecificPart = str.substring(iIndexOf + 1);
            } else if (iIndexOf2 != -1) {
                if (iIndexOf2 < iIndexOf) {
                    MalformedURIException malformedURIException = new MalformedURIException("Unparseable URI");
                    malformedURIException.setData(str);
                    throw malformedURIException;
                }
                this.schemeSpecificPart = str.substring(iIndexOf + 1, iIndexOf2);
            } else {
                if (iLastIndexOf < iIndexOf) {
                    MalformedURIException malformedURIException2 = new MalformedURIException("Unparseable URI");
                    malformedURIException2.setData(str);
                    throw malformedURIException2;
                }
                this.schemeSpecificPart = str.substring(iIndexOf + 1, iLastIndexOf);
            }
            if (iLastIndexOf != -1) {
                this.fragment = str.substring(iLastIndexOf + 1);
            }
            if (iIndexOf2 != -1) {
                if (iLastIndexOf == -1) {
                    this.query = str.substring(iIndexOf2 + 1);
                } else {
                    this.query = str.substring(iIndexOf2 + 1, iLastIndexOf);
                }
            }
            if (!this.schemeSpecificPart.startsWith("//")) {
                this.path = this.schemeSpecificPart;
                return;
            }
            int iIndexOf3 = this.schemeSpecificPart.indexOf(47, 2);
            if (iIndexOf3 == -1) {
                this.authority = this.schemeSpecificPart.substring(2);
                this.path = "";
            } else {
                this.authority = this.schemeSpecificPart.substring(2, iIndexOf3);
                this.path = this.schemeSpecificPart.substring(iIndexOf3);
            }
        }

        ParsedURI() {
            this.path = "";
        }

        public String toString() {
            StringBuffer stringBuffer = new StringBuffer(30);
            if (this.scheme != null) {
                stringBuffer.append(this.scheme);
                stringBuffer.append(':');
            }
            if (this.schemeSpecificPart != null) {
                stringBuffer.append(this.schemeSpecificPart);
            } else {
                stringBuffer.append("//");
                if (this.authority != null) {
                    stringBuffer.append(this.authority);
                }
                stringBuffer.append(this.path);
            }
            if (this.query != null) {
                stringBuffer.append('?');
                stringBuffer.append(this.query);
            }
            if (this.fragment != null) {
                stringBuffer.append('#');
                stringBuffer.append(this.fragment);
            }
            return stringBuffer.toString();
        }
    }

    URIUtil() {
    }

    static boolean isOpaque(String str) {
        return !str.substring(str.indexOf(58) + 1).startsWith("/") && Verifier.isAlpha(str.charAt(0));
    }

    static boolean isAbsolute(String str) {
        return str.indexOf(58) >= 1;
    }

    static String absolutize(String str, String str2) {
        if ("".equals(str) || str == null) {
            return str2;
        }
        ParsedURI parsedURI = new ParsedURI(str);
        if (parsedURI.path.endsWith("/..")) {
            parsedURI.path = new StringBuffer().append(parsedURI.path).append('/').toString();
        }
        ParsedURI parsedURI2 = new ParsedURI(str2);
        ParsedURI parsedURI3 = new ParsedURI();
        if (parsedURI2.authority != null) {
            parsedURI3.authority = parsedURI2.authority;
            parsedURI3.query = parsedURI2.query;
            parsedURI3.path = removeDotSegments(parsedURI2.path);
        } else {
            if ("".equals(parsedURI2.path)) {
                parsedURI3.path = parsedURI.path;
                if (parsedURI2.query != null) {
                    parsedURI3.query = parsedURI2.query;
                } else {
                    parsedURI3.query = parsedURI.query;
                }
            } else {
                if (parsedURI2.path.startsWith("/")) {
                    parsedURI3.path = removeDotSegments(parsedURI2.path);
                } else {
                    parsedURI3.path = merge(parsedURI, parsedURI2.path);
                    parsedURI3.path = removeDotSegments(parsedURI3.path);
                }
                parsedURI3.query = parsedURI2.query;
            }
            parsedURI3.authority = parsedURI.authority;
        }
        parsedURI3.scheme = parsedURI.scheme;
        parsedURI3.fragment = parsedURI2.fragment;
        return parsedURI3.toString();
    }

    private static String merge(ParsedURI parsedURI, String str) {
        if (parsedURI.authority != null && "".equals(parsedURI.path) && !"".equals(parsedURI.authority)) {
            return new StringBuffer().append("/").append(str).toString();
        }
        int iLastIndexOf = parsedURI.path.lastIndexOf(47);
        if (iLastIndexOf == -1) {
            return str;
        }
        return new StringBuffer().append(parsedURI.path.substring(0, iLastIndexOf + 1)).append(str).toString();
    }

    static String removeDotSegments(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        while (str.length() > 0) {
            if (str.startsWith("/./")) {
                str = new StringBuffer().append('/').append(str.substring(3)).toString();
            } else if (str.equals("/.")) {
                str = "/";
            } else if (str.startsWith("/../")) {
                str = new StringBuffer().append('/').append(str.substring(4)).toString();
                int iLastIndexOf = stringBuffer.toString().lastIndexOf(47);
                if (iLastIndexOf != -1) {
                    stringBuffer.setLength(iLastIndexOf);
                }
            } else if (str.equals("/..")) {
                str = "/";
                int iLastIndexOf2 = stringBuffer.toString().lastIndexOf(47);
                if (iLastIndexOf2 != -1) {
                    stringBuffer.setLength(iLastIndexOf2);
                }
            } else {
                int iIndexOf = str.indexOf(47);
                if (iIndexOf == 0) {
                    iIndexOf = str.indexOf(47, 1);
                }
                if (iIndexOf == -1) {
                    stringBuffer.append(str);
                    str = "";
                } else {
                    stringBuffer.append(str.substring(0, iIndexOf));
                    str = str.substring(iIndexOf);
                }
            }
        }
        return stringBuffer.toString();
    }

    static String toURI(String str) {
        int length = str.length();
        StringBuffer stringBuffer = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            switch (cCharAt) {
                case ' ':
                    stringBuffer.append("%20");
                    break;
                case '!':
                    stringBuffer.append(cCharAt);
                    break;
                case '\"':
                    stringBuffer.append("%22");
                    break;
                case '#':
                    stringBuffer.append(cCharAt);
                    break;
                case '$':
                    stringBuffer.append(cCharAt);
                    break;
                case '%':
                    stringBuffer.append(cCharAt);
                    break;
                case '&':
                    stringBuffer.append(cCharAt);
                    break;
                case '\'':
                    stringBuffer.append(cCharAt);
                    break;
                case '(':
                    stringBuffer.append(cCharAt);
                    break;
                case ')':
                    stringBuffer.append(cCharAt);
                    break;
                case '*':
                    stringBuffer.append(cCharAt);
                    break;
                case '+':
                    stringBuffer.append(cCharAt);
                    break;
                case ',':
                    stringBuffer.append(cCharAt);
                    break;
                case '-':
                    stringBuffer.append(cCharAt);
                    break;
                case '.':
                    stringBuffer.append(cCharAt);
                    break;
                case '/':
                    stringBuffer.append(cCharAt);
                    break;
                case '0':
                    stringBuffer.append(cCharAt);
                    break;
                case '1':
                    stringBuffer.append(cCharAt);
                    break;
                case '2':
                    stringBuffer.append(cCharAt);
                    break;
                case '3':
                    stringBuffer.append(cCharAt);
                    break;
                case '4':
                    stringBuffer.append(cCharAt);
                    break;
                case '5':
                    stringBuffer.append(cCharAt);
                    break;
                case '6':
                    stringBuffer.append(cCharAt);
                    break;
                case '7':
                    stringBuffer.append(cCharAt);
                    break;
                case '8':
                    stringBuffer.append(cCharAt);
                    break;
                case '9':
                    stringBuffer.append(cCharAt);
                    break;
                case ':':
                    stringBuffer.append(cCharAt);
                    break;
                case ';':
                    stringBuffer.append(cCharAt);
                    break;
                case '<':
                    stringBuffer.append("%3C");
                    break;
                case '=':
                    stringBuffer.append(cCharAt);
                    break;
                case '>':
                    stringBuffer.append("%3E");
                    break;
                case '?':
                    stringBuffer.append(cCharAt);
                    break;
                case '@':
                    stringBuffer.append(cCharAt);
                    break;
                case 'A':
                    stringBuffer.append(cCharAt);
                    break;
                case 'B':
                    stringBuffer.append(cCharAt);
                    break;
                case 'C':
                    stringBuffer.append(cCharAt);
                    break;
                case 'D':
                    stringBuffer.append(cCharAt);
                    break;
                case 'E':
                    stringBuffer.append(cCharAt);
                    break;
                case 'F':
                    stringBuffer.append(cCharAt);
                    break;
                case 'G':
                    stringBuffer.append(cCharAt);
                    break;
                case 'H':
                    stringBuffer.append(cCharAt);
                    break;
                case 'I':
                    stringBuffer.append(cCharAt);
                    break;
                case 'J':
                    stringBuffer.append(cCharAt);
                    break;
                case 'K':
                    stringBuffer.append(cCharAt);
                    break;
                case 'L':
                    stringBuffer.append(cCharAt);
                    break;
                case 'M':
                    stringBuffer.append(cCharAt);
                    break;
                case 'N':
                    stringBuffer.append(cCharAt);
                    break;
                case 'O':
                    stringBuffer.append(cCharAt);
                    break;
                case 'P':
                    stringBuffer.append(cCharAt);
                    break;
                case 'Q':
                    stringBuffer.append(cCharAt);
                    break;
                case 'R':
                    stringBuffer.append(cCharAt);
                    break;
                case 'S':
                    stringBuffer.append(cCharAt);
                    break;
                case 'T':
                    stringBuffer.append(cCharAt);
                    break;
                case 'U':
                    stringBuffer.append(cCharAt);
                    break;
                case 'V':
                    stringBuffer.append(cCharAt);
                    break;
                case 'W':
                    stringBuffer.append(cCharAt);
                    break;
                case 'X':
                    stringBuffer.append(cCharAt);
                    break;
                case 'Y':
                    stringBuffer.append(cCharAt);
                    break;
                case 'Z':
                    stringBuffer.append(cCharAt);
                    break;
                case '[':
                    stringBuffer.append(cCharAt);
                    break;
                case '\\':
                    stringBuffer.append("%5C");
                    break;
                case ']':
                    stringBuffer.append(cCharAt);
                    break;
                case '^':
                    stringBuffer.append("%5E");
                    break;
                case '_':
                    stringBuffer.append(cCharAt);
                    break;
                case '`':
                    stringBuffer.append("%60");
                    break;
                case 'a':
                    stringBuffer.append(cCharAt);
                    break;
                case 'b':
                    stringBuffer.append(cCharAt);
                    break;
                case 'c':
                    stringBuffer.append(cCharAt);
                    break;
                case 'd':
                    stringBuffer.append(cCharAt);
                    break;
                case 'e':
                    stringBuffer.append(cCharAt);
                    break;
                case 'f':
                    stringBuffer.append(cCharAt);
                    break;
                case 'g':
                    stringBuffer.append(cCharAt);
                    break;
                case 'h':
                    stringBuffer.append(cCharAt);
                    break;
                case 'i':
                    stringBuffer.append(cCharAt);
                    break;
                case 'j':
                    stringBuffer.append(cCharAt);
                    break;
                case 'k':
                    stringBuffer.append(cCharAt);
                    break;
                case 'l':
                    stringBuffer.append(cCharAt);
                    break;
                case 'm':
                    stringBuffer.append(cCharAt);
                    break;
                case 'n':
                    stringBuffer.append(cCharAt);
                    break;
                case 'o':
                    stringBuffer.append(cCharAt);
                    break;
                case 'p':
                    stringBuffer.append(cCharAt);
                    break;
                case 'q':
                    stringBuffer.append(cCharAt);
                    break;
                case 'r':
                    stringBuffer.append(cCharAt);
                    break;
                case 's':
                    stringBuffer.append(cCharAt);
                    break;
                case 't':
                    stringBuffer.append(cCharAt);
                    break;
                case 'u':
                    stringBuffer.append(cCharAt);
                    break;
                case 'v':
                    stringBuffer.append(cCharAt);
                    break;
                case 'w':
                    stringBuffer.append(cCharAt);
                    break;
                case 'x':
                    stringBuffer.append(cCharAt);
                    break;
                case 'y':
                    stringBuffer.append(cCharAt);
                    break;
                case 'z':
                    stringBuffer.append(cCharAt);
                    break;
                case '{':
                    stringBuffer.append("%7B");
                    break;
                case '|':
                    stringBuffer.append("%7C");
                    break;
                case '}':
                    stringBuffer.append("%7D");
                    break;
                case '~':
                    stringBuffer.append(cCharAt);
                    break;
                default:
                    stringBuffer.append(percentEscape(cCharAt));
                    break;
            }
        }
        return stringBuffer.toString();
    }

    static String percentEscape(char c) {
        StringBuffer stringBuffer = new StringBuffer(3);
        try {
            for (byte b : String.valueOf(c).getBytes("UTF8")) {
                stringBuffer.append('%');
                String upperCase = Integer.toHexString(b).toUpperCase();
                if (c < 16) {
                    stringBuffer.append('0');
                    stringBuffer.append(upperCase);
                } else {
                    stringBuffer.append(upperCase.substring(upperCase.length() - 2));
                }
            }
            return stringBuffer.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Broken VM: does not recognize UTF-8 encoding");
        }
    }

    static String relativize(String str, String str2) {
        ParsedURI parsedURI = new ParsedURI(str);
        ParsedURI parsedURI2 = new ParsedURI(str2);
        parsedURI.path = removeDotSegments(parsedURI.path);
        if (!parsedURI.scheme.equals(parsedURI2.scheme) || !parsedURI.authority.equals(parsedURI2.authority)) {
            return str2;
        }
        String strSubstring = parsedURI.path;
        String str3 = parsedURI2.path;
        while (strSubstring.length() > 1) {
            strSubstring = strSubstring.substring(0, strSubstring.lastIndexOf(47));
            if (str3.startsWith(strSubstring)) {
                return str3.substring(strSubstring.length() + 1);
            }
        }
        return str3;
    }
}
