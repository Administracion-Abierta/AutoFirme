package org.mozilla.javascript;

import com.ctc.wstx.api.InvalidCharHandler;
import com.sun.jna.platform.win32.WinUser;
import es.gob.afirma.core.signers.der.DerValue;
import java.io.Serializable;
import org.ini4j.Config;
import org.mozilla.javascript.xml.XMLLib;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeGlobal.class */
public class NativeGlobal implements Serializable, IdFunctionCall {
    static final long serialVersionUID = 6080442165748707530L;
    private static final String URI_DECODE_RESERVED = ";/?:@&=+$,#";
    private static final int INVALID_UTF8 = Integer.MAX_VALUE;
    private static final Object FTAG = "Global";
    private static final int Id_decodeURI = 1;
    private static final int Id_decodeURIComponent = 2;
    private static final int Id_encodeURI = 3;
    private static final int Id_encodeURIComponent = 4;
    private static final int Id_escape = 5;
    private static final int Id_eval = 6;
    private static final int Id_isFinite = 7;
    private static final int Id_isNaN = 8;
    private static final int Id_isXMLName = 9;
    private static final int Id_parseFloat = 10;
    private static final int Id_parseInt = 11;
    private static final int Id_unescape = 12;
    private static final int Id_uneval = 13;
    private static final int LAST_SCOPE_FUNCTION_ID = 13;
    private static final int Id_new_CommonError = 14;

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        String name;
        NativeGlobal obj = new NativeGlobal();
        for (int id = 1; id <= 13; id++) {
            int arity = 1;
            switch (id) {
                case 1:
                    name = "decodeURI";
                    break;
                case 2:
                    name = "decodeURIComponent";
                    break;
                case 3:
                    name = "encodeURI";
                    break;
                case 4:
                    name = "encodeURIComponent";
                    break;
                case 5:
                    name = Config.PROP_ESCAPE;
                    break;
                case 6:
                    name = "eval";
                    break;
                case 7:
                    name = "isFinite";
                    break;
                case 8:
                    name = "isNaN";
                    break;
                case 9:
                    name = "isXMLName";
                    break;
                case 10:
                    name = "parseFloat";
                    break;
                case 11:
                    name = "parseInt";
                    arity = 2;
                    break;
                case 12:
                    name = "unescape";
                    break;
                case 13:
                    name = "uneval";
                    break;
                default:
                    throw Kit.codeBug();
            }
            IdFunctionObject f = new IdFunctionObject(obj, FTAG, id, name, arity, scope);
            if (sealed) {
                f.sealObject();
            }
            f.exportAsScopeProperty();
        }
        ScriptableObject.defineProperty(scope, "NaN", ScriptRuntime.NaNobj, 7);
        ScriptableObject.defineProperty(scope, "Infinity", ScriptRuntime.wrapNumber(Double.POSITIVE_INFINITY), 7);
        ScriptableObject.defineProperty(scope, "undefined", Undefined.instance, 7);
        for (TopLevel.NativeErrors error : TopLevel.NativeErrors.values()) {
            if (error != TopLevel.NativeErrors.Error) {
                String name2 = error.name();
                ScriptableObject scriptableObject = (ScriptableObject) ScriptRuntime.newBuiltinObject(cx, scope, TopLevel.Builtins.Error, ScriptRuntime.emptyArgs);
                scriptableObject.put("name", scriptableObject, name2);
                scriptableObject.put("message", scriptableObject, "");
                IdFunctionObject idFunctionObject = new IdFunctionObject(obj, FTAG, 14, name2, 1, scope);
                idFunctionObject.markAsConstructor(scriptableObject);
                scriptableObject.put("constructor", scriptableObject, idFunctionObject);
                scriptableObject.setAttributes("constructor", 2);
                if (sealed) {
                    scriptableObject.sealObject();
                    idFunctionObject.sealObject();
                }
                idFunctionObject.exportAsScopeProperty();
            }
        }
    }

    @Override // org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        boolean result;
        if (f.hasTag(FTAG)) {
            int methodId = f.methodId();
            switch (methodId) {
                case 1:
                case 2:
                    String str = ScriptRuntime.toString(args, 0);
                    return decode(str, methodId == 1);
                case 3:
                case 4:
                    String str2 = ScriptRuntime.toString(args, 0);
                    return encode(str2, methodId == 3);
                case 5:
                    return js_escape(args);
                case 6:
                    return js_eval(cx, scope, args);
                case 7:
                    if (args.length < 1) {
                        return Boolean.FALSE;
                    }
                    return NativeNumber.isFinite(args[0]);
                case 8:
                    if (args.length < 1) {
                        result = true;
                    } else {
                        double d = ScriptRuntime.toNumber(args[0]);
                        result = Double.isNaN(d);
                    }
                    return ScriptRuntime.wrapBoolean(result);
                case 9:
                    Object name = args.length == 0 ? Undefined.instance : args[0];
                    XMLLib xmlLib = XMLLib.extractFromScope(scope);
                    return ScriptRuntime.wrapBoolean(xmlLib.isXMLName(cx, name));
                case 10:
                    return js_parseFloat(args);
                case 11:
                    return js_parseInt(args);
                case 12:
                    return js_unescape(args);
                case 13:
                    Object value = args.length != 0 ? args[0] : Undefined.instance;
                    return ScriptRuntime.uneval(cx, scope, value);
                case 14:
                    return NativeError.make(cx, scope, f, args);
            }
        }
        throw f.unknown();
    }

    /* JADX WARN: Code duplicated, block: B:20:0x0054 A[PHI: r8
      0x0054: PHI (r8v1 'negative' boolean) = (r8v0 'negative' boolean), (r8v3 'negative' boolean) binds: [B:13:0x003f, B:19:0x0051] A[DONT_GENERATE, DONT_INLINE]] */
    static Object js_parseInt(Object[] args) {
        char c;
        char c2;
        Context cx;
        String s = ScriptRuntime.toString(args, 0);
        int radix = ScriptRuntime.toInt32(args, 1);
        int len = s.length();
        if (len == 0) {
            return ScriptRuntime.NaNobj;
        }
        boolean negative = false;
        int start = 0;
        do {
            c = s.charAt(start);
            if (!ScriptRuntime.isStrWhiteSpaceChar(c)) {
                break;
            }
            start++;
        } while (start < len);
        if (c == '+') {
            start++;
        } else {
            boolean z = c == '-';
            negative = z;
            if (z) {
                start++;
            }
        }
        if (radix == 0) {
            radix = -1;
        } else {
            if (radix < 2 || radix > 36) {
                return ScriptRuntime.NaNobj;
            }
            if (radix == 16 && len - start > 1 && s.charAt(start) == '0' && ((c2 = s.charAt(start + 1)) == 'x' || c2 == 'X')) {
                start += 2;
            }
        }
        if (radix == -1) {
            radix = 10;
            if (len - start > 1 && s.charAt(start) == '0') {
                char c3 = s.charAt(start + 1);
                if (c3 == 'x' || c3 == 'X') {
                    radix = 16;
                    start += 2;
                } else if ('0' <= c3 && c3 <= '9' && ((cx = Context.getCurrentContext()) == null || cx.getLanguageVersion() < 150)) {
                    radix = 8;
                    start++;
                }
            }
        }
        double d = ScriptRuntime.stringPrefixToNumber(s, start, radix);
        return ScriptRuntime.wrapNumber(negative ? -d : d);
    }

    static Object js_parseFloat(Object[] args) {
        double d;
        if (args.length < 1) {
            return ScriptRuntime.NaNobj;
        }
        String s = ScriptRuntime.toString(args[0]);
        int len = s.length();
        for (int start = 0; start != len; start++) {
            char c = s.charAt(start);
            if (!ScriptRuntime.isStrWhiteSpaceChar(c)) {
                int i = start;
                if (c == '+' || c == '-') {
                    i++;
                    if (i == len) {
                        return ScriptRuntime.NaNobj;
                    }
                    c = s.charAt(i);
                }
                if (c == 'I') {
                    if (i + 8 <= len && s.regionMatches(i, "Infinity", 0, 8)) {
                        if (s.charAt(start) == '-') {
                            d = Double.NEGATIVE_INFINITY;
                        } else {
                            d = Double.POSITIVE_INFINITY;
                        }
                        return ScriptRuntime.wrapNumber(d);
                    }
                    return ScriptRuntime.NaNobj;
                }
                int decimal = -1;
                int exponent = -1;
                boolean exponentValid = false;
                try {
                    while (i < len) {
                        switch (s.charAt(i)) {
                            case '+':
                            case '-':
                                if (exponent == i - 1) {
                                    if (i != len - 1) {
                                        i++;
                                    } else {
                                        i--;
                                    }
                                }
                                break;
                            case '.':
                                if (decimal == -1) {
                                    decimal = i;
                                    i++;
                                }
                                break;
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                if (exponent != -1) {
                                    exponentValid = true;
                                } else {
                                    continue;
                                }
                                i++;
                                break;
                            case 'E':
                            case 'e':
                                if (exponent == -1 && i != len - 1) {
                                    exponent = i;
                                    i++;
                                }
                                break;
                        }
                        if (exponent != -1 && !exponentValid) {
                            i = exponent;
                        }
                        return Double.valueOf(s.substring(start, i));
                    }
                    return Double.valueOf(s.substring(start, i));
                } catch (NumberFormatException e) {
                    return ScriptRuntime.NaNobj;
                }
                if (exponent != -1) {
                    i = exponent;
                }
            }
        }
        return ScriptRuntime.NaNobj;
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x003a, code lost:
    
        if (0 == (r10 & (-8))) goto L12;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.lang.Object js_escape(java.lang.Object[] r5) {
        /*
            Method dump skipped, instruction units count: 397
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.NativeGlobal.js_escape(java.lang.Object[]):java.lang.Object");
    }

    private static Object js_unescape(Object[] args) {
        int start;
        int end;
        String s = ScriptRuntime.toString(args, 0);
        int firstEscapePos = s.indexOf(37);
        if (firstEscapePos >= 0) {
            int L = s.length();
            char[] buf = s.toCharArray();
            int destination = firstEscapePos;
            int k = firstEscapePos;
            while (k != L) {
                char c = buf[k];
                k++;
                if (c == '%' && k != L) {
                    if (buf[k] == 'u') {
                        start = k + 1;
                        end = k + 5;
                    } else {
                        start = k;
                        end = k + 2;
                    }
                    if (end <= L) {
                        int x = 0;
                        for (int i = start; i != end; i++) {
                            x = Kit.xDigitToInt(buf[i], x);
                        }
                        if (x >= 0) {
                            c = (char) x;
                            k = end;
                        }
                    }
                }
                buf[destination] = c;
                destination++;
            }
            s = new String(buf, 0, destination);
        }
        return s;
    }

    private static Object js_eval(Context cx, Scriptable scope, Object[] args) {
        Scriptable global = ScriptableObject.getTopLevelScope(scope);
        return ScriptRuntime.evalSpecial(cx, global, global, args, "eval code", 1);
    }

    static boolean isEvalFunction(Object functionObj) {
        if (functionObj instanceof IdFunctionObject) {
            IdFunctionObject function = (IdFunctionObject) functionObj;
            if (function.hasTag(FTAG) && function.methodId() == 6) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Deprecated
    public static EcmaError constructError(Context cx, String error, String message, Scriptable scope) {
        return ScriptRuntime.constructError(error, message);
    }

    @Deprecated
    public static EcmaError constructError(Context cx, String error, String message, Scriptable scope, String sourceName, int lineNumber, int columnNumber, String lineSource) {
        return ScriptRuntime.constructError(error, message, sourceName, lineNumber, lineSource, columnNumber);
    }

    private static String encode(String str, boolean fullUri) {
        int V;
        byte[] utf8buf = null;
        StringBuilder sb = null;
        int k = 0;
        int length = str.length();
        while (k != length) {
            char C = str.charAt(k);
            if (encodeUnescaped(C, fullUri)) {
                if (sb != null) {
                    sb.append(C);
                }
            } else {
                if (sb == null) {
                    sb = new StringBuilder(length + 3);
                    sb.append(str);
                    sb.setLength(k);
                    utf8buf = new byte[6];
                }
                if (56320 <= C && C <= 57343) {
                    throw uriError();
                }
                if (C < 55296 || 56319 < C) {
                    V = C;
                } else {
                    k++;
                    if (k == length) {
                        throw uriError();
                    }
                    char C2 = str.charAt(k);
                    if (56320 > C2 || C2 > 57343) {
                        throw uriError();
                    }
                    V = ((C - InvalidCharHandler.FailingHandler.SURR1_FIRST) << 10) + (C2 - InvalidCharHandler.FailingHandler.SURR2_FIRST) + 65536;
                }
                int L = oneUcs4ToUtf8Char(utf8buf, V);
                for (int j = 0; j < L; j++) {
                    int d = 255 & utf8buf[j];
                    sb.append('%');
                    sb.append(toHexChar(d >>> 4));
                    sb.append(toHexChar(d & 15));
                }
            }
            k++;
        }
        return sb == null ? str : sb.toString();
    }

    private static char toHexChar(int i) {
        if ((i >> 4) != 0) {
            Kit.codeBug();
        }
        return (char) (i < 10 ? i + 48 : (i - 10) + 65);
    }

    private static int unHex(char c) {
        if ('A' <= c && c <= 'F') {
            return (c - 'A') + 10;
        }
        if ('a' <= c && c <= 'f') {
            return (c - 'a') + 10;
        }
        if ('0' <= c && c <= '9') {
            return c - '0';
        }
        return -1;
    }

    private static int unHex(char c1, char c2) {
        int i1 = unHex(c1);
        int i2 = unHex(c2);
        if (i1 >= 0 && i2 >= 0) {
            return (i1 << 4) | i2;
        }
        return -1;
    }

    private static String decode(String str, boolean fullUri) {
        int utf8Tail;
        int ucs4Char;
        int minUcs4Char;
        char C;
        char[] buf = null;
        int bufTop = 0;
        int k = 0;
        int length = str.length();
        while (k != length) {
            char C2 = str.charAt(k);
            if (C2 != '%') {
                if (buf != null) {
                    int i = bufTop;
                    bufTop++;
                    buf[i] = C2;
                }
                k++;
            } else {
                if (buf == null) {
                    buf = new char[length];
                    str.getChars(0, k, buf, 0);
                    bufTop = k;
                }
                int start = k;
                if (k + 3 > length) {
                    throw uriError();
                }
                int B = unHex(str.charAt(k + 1), str.charAt(k + 2));
                if (B < 0) {
                    throw uriError();
                }
                k += 3;
                if ((B & 128) == 0) {
                    C = (char) B;
                } else {
                    if ((B & 192) == 128) {
                        throw uriError();
                    }
                    if ((B & 32) == 0) {
                        utf8Tail = 1;
                        ucs4Char = B & 31;
                        minUcs4Char = 128;
                    } else if ((B & 16) == 0) {
                        utf8Tail = 2;
                        ucs4Char = B & 15;
                        minUcs4Char = 2048;
                    } else if ((B & 8) == 0) {
                        utf8Tail = 3;
                        ucs4Char = B & 7;
                        minUcs4Char = 65536;
                    } else if ((B & 4) == 0) {
                        utf8Tail = 4;
                        ucs4Char = B & 3;
                        minUcs4Char = 2097152;
                    } else if ((B & 2) == 0) {
                        utf8Tail = 5;
                        ucs4Char = B & 1;
                        minUcs4Char = 67108864;
                    } else {
                        throw uriError();
                    }
                    if (k + (3 * utf8Tail) > length) {
                        throw uriError();
                    }
                    for (int j = 0; j != utf8Tail; j++) {
                        if (str.charAt(k) != '%') {
                            throw uriError();
                        }
                        int B2 = unHex(str.charAt(k + 1), str.charAt(k + 2));
                        if (B2 < 0 || (B2 & 192) != 128) {
                            throw uriError();
                        }
                        ucs4Char = (ucs4Char << 6) | (B2 & 63);
                        k += 3;
                    }
                    if (ucs4Char < minUcs4Char || (ucs4Char >= 55296 && ucs4Char <= 57343)) {
                        ucs4Char = Integer.MAX_VALUE;
                    } else if (ucs4Char == 65534 || ucs4Char == 65535) {
                        ucs4Char = 65533;
                    }
                    if (ucs4Char >= 65536) {
                        int ucs4Char2 = ucs4Char - 65536;
                        if (ucs4Char2 > 1048575) {
                            throw uriError();
                        }
                        char H = (char) ((ucs4Char2 >>> 10) + InvalidCharHandler.FailingHandler.SURR1_FIRST);
                        C = (char) ((ucs4Char2 & WinUser.CF_GDIOBJLAST) + InvalidCharHandler.FailingHandler.SURR2_FIRST);
                        int i2 = bufTop;
                        bufTop++;
                        buf[i2] = H;
                    } else {
                        C = (char) ucs4Char;
                    }
                }
                if (fullUri && URI_DECODE_RESERVED.indexOf(C) >= 0) {
                    for (int x = start; x != k; x++) {
                        int i3 = bufTop;
                        bufTop++;
                        buf[i3] = str.charAt(x);
                    }
                } else {
                    int i4 = bufTop;
                    bufTop++;
                    buf[i4] = C;
                }
            }
        }
        return buf == null ? str : new String(buf, 0, bufTop);
    }

    private static boolean encodeUnescaped(char c, boolean fullUri) {
        if ('A' <= c && c <= 'Z') {
            return true;
        }
        if ('a' <= c && c <= 'z') {
            return true;
        }
        if (('0' > c || c > '9') && "-_.!~*'()".indexOf(c) < 0) {
            return fullUri && URI_DECODE_RESERVED.indexOf(c) >= 0;
        }
        return true;
    }

    private static EcmaError uriError() {
        return ScriptRuntime.constructError("URIError", ScriptRuntime.getMessage0("msg.bad.uri"));
    }

    private static int oneUcs4ToUtf8Char(byte[] utf8Buffer, int ucs4Char) {
        int utf8Length = 1;
        if ((ucs4Char & DerValue.TAG_CONTEXT) == 0) {
            utf8Buffer[0] = (byte) ucs4Char;
        } else {
            int a = ucs4Char >>> 11;
            utf8Length = 2;
            while (a != 0) {
                a >>>= 5;
                utf8Length++;
            }
            int i = utf8Length;
            while (true) {
                i--;
                if (i <= 0) {
                    break;
                }
                utf8Buffer[i] = (byte) ((ucs4Char & 63) | 128);
                ucs4Char >>>= 6;
            }
            utf8Buffer[0] = (byte) ((256 - (1 << (8 - utf8Length))) + ucs4Char);
        }
        return utf8Length;
    }
}
