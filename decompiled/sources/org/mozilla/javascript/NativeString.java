package org.mozilla.javascript;

import com.aowagie.text.ElementTags;
import com.aowagie.text.html.Markup;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.sun.jna.platform.win32.WinUser;
import java.text.Collator;
import java.text.Normalizer;
import java.util.Locale;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;
import org.mozilla.javascript.regexp.NativeRegExp;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeString.class */
final class NativeString extends IdScriptableObject {
    private static final long serialVersionUID = 920268368584188687L;
    private static final Object STRING_TAG = "String";
    private static final int Id_length = 1;
    private static final int MAX_INSTANCE_ID = 1;
    private static final int ConstructorId_fromCharCode = -1;
    private static final int ConstructorId_fromCodePoint = -2;
    private static final int Id_constructor = 1;
    private static final int Id_toString = 2;
    private static final int Id_toSource = 3;
    private static final int Id_valueOf = 4;
    private static final int Id_charAt = 5;
    private static final int Id_charCodeAt = 6;
    private static final int Id_indexOf = 7;
    private static final int Id_lastIndexOf = 8;
    private static final int Id_split = 9;
    private static final int Id_substring = 10;
    private static final int Id_toLowerCase = 11;
    private static final int Id_toUpperCase = 12;
    private static final int Id_substr = 13;
    private static final int Id_concat = 14;
    private static final int Id_slice = 15;
    private static final int Id_bold = 16;
    private static final int Id_italics = 17;
    private static final int Id_fixed = 18;
    private static final int Id_strike = 19;
    private static final int Id_small = 20;
    private static final int Id_big = 21;
    private static final int Id_blink = 22;
    private static final int Id_sup = 23;
    private static final int Id_sub = 24;
    private static final int Id_fontsize = 25;
    private static final int Id_fontcolor = 26;
    private static final int Id_link = 27;
    private static final int Id_anchor = 28;
    private static final int Id_equals = 29;
    private static final int Id_equalsIgnoreCase = 30;
    private static final int Id_match = 31;
    private static final int Id_search = 32;
    private static final int Id_replace = 33;
    private static final int Id_localeCompare = 34;
    private static final int Id_toLocaleLowerCase = 35;
    private static final int Id_toLocaleUpperCase = 36;
    private static final int Id_trim = 37;
    private static final int Id_trimLeft = 38;
    private static final int Id_trimRight = 39;
    private static final int Id_includes = 40;
    private static final int Id_startsWith = 41;
    private static final int Id_endsWith = 42;
    private static final int Id_normalize = 43;
    private static final int Id_repeat = 44;
    private static final int Id_codePointAt = 45;
    private static final int Id_padStart = 46;
    private static final int Id_padEnd = 47;
    private static final int SymbolId_iterator = 48;
    private static final int Id_trimStart = 49;
    private static final int Id_trimEnd = 50;
    private static final int MAX_PROTOTYPE_ID = 50;
    private static final int ConstructorId_charAt = -5;
    private static final int ConstructorId_charCodeAt = -6;
    private static final int ConstructorId_indexOf = -7;
    private static final int ConstructorId_lastIndexOf = -8;
    private static final int ConstructorId_split = -9;
    private static final int ConstructorId_substring = -10;
    private static final int ConstructorId_toLowerCase = -11;
    private static final int ConstructorId_toUpperCase = -12;
    private static final int ConstructorId_substr = -13;
    private static final int ConstructorId_concat = -14;
    private static final int ConstructorId_slice = -15;
    private static final int ConstructorId_equalsIgnoreCase = -30;
    private static final int ConstructorId_match = -31;
    private static final int ConstructorId_search = -32;
    private static final int ConstructorId_replace = -33;
    private static final int ConstructorId_localeCompare = -34;
    private static final int ConstructorId_toLocaleLowerCase = -35;
    private CharSequence string;

    static void init(Scriptable scope, boolean sealed) {
        NativeString obj = new NativeString("");
        obj.exportAsJSClass(50, scope, sealed);
    }

    NativeString(CharSequence s) {
        this.string = s;
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "String";
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int getMaxInstanceId() {
        return 1;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findInstanceIdInfo(String s) {
        if (s.equals(XSDatatype.FACET_LENGTH)) {
            return instanceIdInfo(7, 1);
        }
        return super.findInstanceIdInfo(s);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected String getInstanceIdName(int id) {
        return id == 1 ? XSDatatype.FACET_LENGTH : super.getInstanceIdName(id);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected Object getInstanceIdValue(int id) {
        if (id == 1) {
            return ScriptRuntime.wrapInt(this.string.length());
        }
        return super.getInstanceIdValue(id);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        addIdFunctionProperty(ctor, STRING_TAG, -1, "fromCharCode", 1);
        addIdFunctionProperty(ctor, STRING_TAG, -2, "fromCodePoint", 1);
        addIdFunctionProperty(ctor, STRING_TAG, -5, "charAt", 2);
        addIdFunctionProperty(ctor, STRING_TAG, -6, "charCodeAt", 2);
        addIdFunctionProperty(ctor, STRING_TAG, -7, "indexOf", 2);
        addIdFunctionProperty(ctor, STRING_TAG, -8, "lastIndexOf", 2);
        addIdFunctionProperty(ctor, STRING_TAG, ConstructorId_split, "split", 3);
        addIdFunctionProperty(ctor, STRING_TAG, -10, "substring", 3);
        addIdFunctionProperty(ctor, STRING_TAG, -11, "toLowerCase", 1);
        addIdFunctionProperty(ctor, STRING_TAG, -12, "toUpperCase", 1);
        addIdFunctionProperty(ctor, STRING_TAG, ConstructorId_substr, "substr", 3);
        addIdFunctionProperty(ctor, STRING_TAG, -14, "concat", 2);
        addIdFunctionProperty(ctor, STRING_TAG, ConstructorId_slice, "slice", 3);
        addIdFunctionProperty(ctor, STRING_TAG, ConstructorId_equalsIgnoreCase, "equalsIgnoreCase", 2);
        addIdFunctionProperty(ctor, STRING_TAG, ConstructorId_match, "match", 2);
        addIdFunctionProperty(ctor, STRING_TAG, -32, "search", 2);
        addIdFunctionProperty(ctor, STRING_TAG, ConstructorId_replace, "replace", 2);
        addIdFunctionProperty(ctor, STRING_TAG, -34, "localeCompare", 2);
        addIdFunctionProperty(ctor, STRING_TAG, ConstructorId_toLocaleLowerCase, "toLocaleLowerCase", 1);
        super.fillConstructorProperties(ctor);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        int arity;
        String s;
        if (id == 48) {
            initPrototypeMethod(STRING_TAG, id, SymbolKey.ITERATOR, "[Symbol.iterator]", 0);
            return;
        }
        switch (id) {
            case 1:
                arity = 1;
                s = "constructor";
                break;
            case 2:
                arity = 0;
                s = "toString";
                break;
            case 3:
                arity = 0;
                s = "toSource";
                break;
            case 4:
                arity = 0;
                s = "valueOf";
                break;
            case 5:
                arity = 1;
                s = "charAt";
                break;
            case 6:
                arity = 1;
                s = "charCodeAt";
                break;
            case 7:
                arity = 1;
                s = "indexOf";
                break;
            case 8:
                arity = 1;
                s = "lastIndexOf";
                break;
            case 9:
                arity = 2;
                s = "split";
                break;
            case 10:
                arity = 2;
                s = "substring";
                break;
            case 11:
                arity = 0;
                s = "toLowerCase";
                break;
            case 12:
                arity = 0;
                s = "toUpperCase";
                break;
            case 13:
                arity = 2;
                s = "substr";
                break;
            case 14:
                arity = 1;
                s = "concat";
                break;
            case 15:
                arity = 2;
                s = "slice";
                break;
            case 16:
                arity = 0;
                s = Markup.CSS_VALUE_BOLD;
                break;
            case 17:
                arity = 0;
                s = "italics";
                break;
            case 18:
                arity = 0;
                s = "fixed";
                break;
            case 19:
                arity = 0;
                s = "strike";
                break;
            case 20:
                arity = 0;
                s = "small";
                break;
            case 21:
                arity = 0;
                s = "big";
                break;
            case 22:
                arity = 0;
                s = "blink";
                break;
            case 23:
                arity = 0;
                s = "sup";
                break;
            case 24:
                arity = 0;
                s = "sub";
                break;
            case 25:
                arity = 0;
                s = "fontsize";
                break;
            case 26:
                arity = 0;
                s = "fontcolor";
                break;
            case 27:
                arity = 0;
                s = "link";
                break;
            case 28:
                arity = 0;
                s = ElementTags.ANCHOR;
                break;
            case 29:
                arity = 1;
                s = "equals";
                break;
            case 30:
                arity = 1;
                s = "equalsIgnoreCase";
                break;
            case 31:
                arity = 1;
                s = "match";
                break;
            case 32:
                arity = 1;
                s = "search";
                break;
            case 33:
                arity = 2;
                s = "replace";
                break;
            case 34:
                arity = 1;
                s = "localeCompare";
                break;
            case 35:
                arity = 0;
                s = "toLocaleLowerCase";
                break;
            case 36:
                arity = 0;
                s = "toLocaleUpperCase";
                break;
            case 37:
                arity = 0;
                s = "trim";
                break;
            case 38:
                arity = 0;
                s = "trimLeft";
                break;
            case 39:
                arity = 0;
                s = "trimRight";
                break;
            case 40:
                arity = 1;
                s = "includes";
                break;
            case 41:
                arity = 1;
                s = "startsWith";
                break;
            case 42:
                arity = 1;
                s = "endsWith";
                break;
            case 43:
                arity = 0;
                s = "normalize";
                break;
            case 44:
                arity = 1;
                s = "repeat";
                break;
            case 45:
                arity = 1;
                s = "codePointAt";
                break;
            case 46:
                arity = 1;
                s = "padStart";
                break;
            case 47:
                arity = 1;
                s = "padEnd";
                break;
            case 48:
            default:
                throw new IllegalArgumentException(String.valueOf(id));
            case 49:
                arity = 0;
                s = "trimStart";
                break;
            case 50:
                arity = 0;
                s = "trimEnd";
                break;
        }
        initPrototypeMethod(STRING_TAG, id, s, (String) null, arity);
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        int id;
        Normalizer.Form form;
        int actionType;
        boolean zEqualsIgnoreCase;
        CharSequence s;
        if (!f.hasTag(STRING_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int iMethodId = f.methodId();
        while (true) {
            id = iMethodId;
            switch (id) {
                case ConstructorId_toLocaleLowerCase /* -35 */:
                case -34:
                case ConstructorId_replace /* -33 */:
                case -32:
                case ConstructorId_match /* -31 */:
                case ConstructorId_equalsIgnoreCase /* -30 */:
                case ConstructorId_slice /* -15 */:
                case -14:
                case ConstructorId_substr /* -13 */:
                case -12:
                case -11:
                case -10:
                case ConstructorId_split /* -9 */:
                case -8:
                case -7:
                case -6:
                case -5:
                    if (args.length > 0) {
                        thisObj = ScriptRuntime.toObject(cx, scope, ScriptRuntime.toCharSequence(args[0]));
                        Object[] newArgs = new Object[args.length - 1];
                        for (int i = 0; i < newArgs.length; i++) {
                            newArgs[i] = args[i + 1];
                        }
                        args = newArgs;
                    } else {
                        thisObj = ScriptRuntime.toObject(cx, scope, ScriptRuntime.toCharSequence(thisObj));
                    }
                    iMethodId = -id;
                    break;
                case -29:
                case -28:
                case -27:
                case WinUser.GCL_STYLE /* -26 */:
                case -25:
                case WinUser.GCLP_WNDPROC /* -24 */:
                case -23:
                case -22:
                case WinUser.GWL_USERDATA /* -21 */:
                case -20:
                case -19:
                case WinUser.GCL_CBWNDEXTRA /* -18 */:
                case -17:
                case -16:
                case -4:
                case -3:
                case 0:
                default:
                    throw new IllegalArgumentException("String.prototype has no method: " + f.getFunctionName());
                case -2:
                    int n = args.length;
                    if (n < 1) {
                        return "";
                    }
                    int[] codePoints = new int[n];
                    for (int i2 = 0; i2 != n; i2++) {
                        Object arg = args[i2];
                        int codePoint = ScriptRuntime.toInt32(arg);
                        double num = ScriptRuntime.toNumber(arg);
                        if (!ScriptRuntime.eqNumber(num, Integer.valueOf(codePoint)) || !Character.isValidCodePoint(codePoint)) {
                            throw ScriptRuntime.rangeError("Invalid code point " + ScriptRuntime.toString(arg));
                        }
                        codePoints[i2] = codePoint;
                    }
                    return new String(codePoints, 0, n);
                case -1:
                    int n2 = args.length;
                    if (n2 < 1) {
                        return "";
                    }
                    char[] chars = new char[n2];
                    for (int i3 = 0; i3 != n2; i3++) {
                        chars[i3] = ScriptRuntime.toUint16(args[i3]);
                    }
                    return new String(chars);
                case 1:
                    if (args.length == 0) {
                        s = "";
                    } else if (ScriptRuntime.isSymbol(args[0]) && thisObj != null) {
                        s = args[0].toString();
                    } else {
                        s = ScriptRuntime.toCharSequence(args[0]);
                    }
                    if (thisObj == null) {
                        return new NativeString(s);
                    }
                    return s instanceof String ? s : s.toString();
                case 2:
                case 4:
                    CharSequence cs = realThis(thisObj, f).string;
                    return cs instanceof String ? cs : cs.toString();
                case 3:
                    CharSequence s2 = realThis(thisObj, f).string;
                    return "(new String(\"" + ScriptRuntime.escapeString(s2.toString()) + "\"))";
                case 5:
                case 6:
                    CharSequence target = ScriptRuntime.toCharSequence(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    double pos = ScriptRuntime.toInteger(args, 0);
                    if (pos < 0.0d || pos >= target.length()) {
                        return id == 5 ? "" : ScriptRuntime.NaNobj;
                    }
                    char c = target.charAt((int) pos);
                    return id == 5 ? String.valueOf(c) : ScriptRuntime.wrapInt(c);
                case 7:
                    String thisString = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return ScriptRuntime.wrapInt(js_indexOf(7, thisString, args));
                case 8:
                    String thisStr = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return ScriptRuntime.wrapInt(js_lastIndexOf(thisStr, args));
                case 9:
                    String thisStr2 = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return ScriptRuntime.checkRegExpProxy(cx).js_split(cx, scope, thisStr2, args);
                case 10:
                    CharSequence target2 = ScriptRuntime.toCharSequence(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return js_substring(cx, target2, args);
                case 11:
                    String thisStr3 = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return thisStr3.toLowerCase(Locale.ROOT);
                case 12:
                    String thisStr4 = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return thisStr4.toUpperCase(Locale.ROOT);
                case 13:
                    CharSequence target3 = ScriptRuntime.toCharSequence(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return js_substr(target3, args);
                case 14:
                    String thisStr5 = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return js_concat(thisStr5, args);
                case 15:
                    CharSequence target4 = ScriptRuntime.toCharSequence(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return js_slice(target4, args);
                case 16:
                    return tagify(thisObj, OperatorName.CLOSE_FILL_NON_ZERO_AND_STROKE, null, null);
                case 17:
                    return tagify(thisObj, OperatorName.SET_FLATNESS, null, null);
                case 18:
                    return tagify(thisObj, "tt", null, null);
                case 19:
                    return tagify(thisObj, "strike", null, null);
                case 20:
                    return tagify(thisObj, "small", null, null);
                case 21:
                    return tagify(thisObj, "big", null, null);
                case 22:
                    return tagify(thisObj, "blink", null, null);
                case 23:
                    return tagify(thisObj, "sup", null, null);
                case 24:
                    return tagify(thisObj, "sub", null, null);
                case 25:
                    return tagify(thisObj, ElementTags.FONT, ElementTags.SIZE, args);
                case 26:
                    return tagify(thisObj, ElementTags.FONT, "color", args);
                case 27:
                    return tagify(thisObj, PDPageLabelRange.STYLE_LETTERS_LOWER, "href", args);
                case 28:
                    return tagify(thisObj, PDPageLabelRange.STYLE_LETTERS_LOWER, "name", args);
                case 29:
                case 30:
                    String s1 = ScriptRuntime.toString(thisObj);
                    String s3 = ScriptRuntime.toString(args, 0);
                    if (id == 29) {
                        zEqualsIgnoreCase = s1.equals(s3);
                    } else {
                        zEqualsIgnoreCase = s1.equalsIgnoreCase(s3);
                    }
                    return ScriptRuntime.wrapBoolean(zEqualsIgnoreCase);
                case 31:
                case 32:
                case 33:
                    if (id == 31) {
                        actionType = 1;
                    } else if (id == 32) {
                        actionType = 3;
                    } else {
                        actionType = 2;
                    }
                    ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f);
                    return ScriptRuntime.checkRegExpProxy(cx).action(cx, scope, thisObj, args, actionType);
                case 34:
                    String thisStr6 = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    Collator collator = Collator.getInstance(cx.getLocale());
                    collator.setStrength(3);
                    collator.setDecomposition(1);
                    return ScriptRuntime.wrapNumber(collator.compare(thisStr6, ScriptRuntime.toString(args, 0)));
                case 35:
                    String thisStr7 = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return thisStr7.toLowerCase(cx.getLocale());
                case 36:
                    String thisStr8 = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return thisStr8.toUpperCase(cx.getLocale());
                case 37:
                    String str = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    char[] chars2 = str.toCharArray();
                    int start = 0;
                    while (start < chars2.length && ScriptRuntime.isJSWhitespaceOrLineTerminator(chars2[start])) {
                        start++;
                    }
                    int end = chars2.length;
                    while (end > start && ScriptRuntime.isJSWhitespaceOrLineTerminator(chars2[end - 1])) {
                        end--;
                    }
                    return str.substring(start, end);
                case 38:
                case 49:
                    String str2 = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    char[] chars3 = str2.toCharArray();
                    int start2 = 0;
                    while (start2 < chars3.length && ScriptRuntime.isJSWhitespaceOrLineTerminator(chars3[start2])) {
                        start2++;
                    }
                    int end2 = chars3.length;
                    return str2.substring(start2, end2);
                case 39:
                case 50:
                    String str3 = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    char[] chars4 = str3.toCharArray();
                    int end3 = chars4.length;
                    while (end3 > 0 && ScriptRuntime.isJSWhitespaceOrLineTerminator(chars4[end3 - 1])) {
                        end3--;
                    }
                    return str3.substring(0, end3);
                case 40:
                case 41:
                case 42:
                    String thisString2 = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    if (args.length > 0 && (args[0] instanceof NativeRegExp)) {
                        throw ScriptRuntime.typeError2("msg.first.arg.not.regexp", String.class.getSimpleName(), f.getFunctionName());
                    }
                    int idx = js_indexOf(id, thisString2, args);
                    if (id == 40) {
                        return Boolean.valueOf(idx != -1);
                    }
                    if (id == 41) {
                        return Boolean.valueOf(idx == 0);
                    }
                    if (id == 42) {
                        return Boolean.valueOf(idx != -1);
                    }
                    break;
                case 43:
                    if (args.length == 0 || Undefined.isUndefined(args[0])) {
                        return Normalizer.normalize(ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f)), Normalizer.Form.NFC);
                    }
                    String formStr = ScriptRuntime.toString(args, 0);
                    if (Normalizer.Form.NFD.name().equals(formStr)) {
                        form = Normalizer.Form.NFD;
                    } else if (Normalizer.Form.NFKC.name().equals(formStr)) {
                        form = Normalizer.Form.NFKC;
                    } else if (Normalizer.Form.NFKD.name().equals(formStr)) {
                        form = Normalizer.Form.NFKD;
                    } else {
                        if (!Normalizer.Form.NFC.name().equals(formStr)) {
                            throw ScriptRuntime.rangeError("The normalization form should be one of 'NFC', 'NFD', 'NFKC', 'NFKD'.");
                        }
                        form = Normalizer.Form.NFC;
                    }
                    return Normalizer.normalize(ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f)), form);
                case 44:
                    return js_repeat(cx, thisObj, f, args);
                case 45:
                    String str4 = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    double cnt = ScriptRuntime.toInteger(args, 0);
                    return (cnt < 0.0d || cnt >= ((double) str4.length())) ? Undefined.instance : Integer.valueOf(str4.codePointAt((int) cnt));
                case 46:
                case 47:
                    break;
                case 48:
                    return new NativeStringIterator(scope, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
            }
        }
        return js_pad(cx, thisObj, f, args, id == 46);
    }

    private static NativeString realThis(Scriptable thisObj, IdFunctionObject f) {
        if (!(thisObj instanceof NativeString)) {
            throw incompatibleCallError(f);
        }
        return (NativeString) thisObj;
    }

    private static String tagify(Scriptable thisObj, String tag, String attribute, Object[] args) {
        String str = ScriptRuntime.toString(thisObj);
        StringBuilder result = new StringBuilder();
        result.append('<').append(tag);
        if (attribute != null) {
            result.append(' ').append(attribute).append("=\"").append(ScriptRuntime.toString(args, 0)).append('\"');
        }
        result.append('>').append(str).append("</").append(tag).append('>');
        return result.toString();
    }

    public CharSequence toCharSequence() {
        return this.string;
    }

    public String toString() {
        return this.string instanceof String ? (String) this.string : this.string.toString();
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public Object get(int index, Scriptable start) {
        if (0 <= index && index < this.string.length()) {
            return String.valueOf(this.string.charAt(index));
        }
        return super.get(index, start);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public void put(int index, Scriptable start, Object value) {
        if (0 <= index && index < this.string.length()) {
            return;
        }
        super.put(index, start, value);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public boolean has(int index, Scriptable start) {
        if (0 <= index && index < this.string.length()) {
            return true;
        }
        return super.has(index, start);
    }

    @Override // org.mozilla.javascript.ScriptableObject
    public int getAttributes(int index) {
        if (0 <= index && index < this.string.length()) {
            int attribs = 5;
            if (Context.getContext().getLanguageVersion() < 200) {
                attribs = 5 | 2;
            }
            return attribs;
        }
        return super.getAttributes(index);
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.ScriptableObject
    protected Object[] getIds(boolean nonEnumerable, boolean getSymbols) {
        Context cx = Context.getCurrentContext();
        if (cx != null && cx.getLanguageVersion() >= 200) {
            Object[] sids = super.getIds(nonEnumerable, getSymbols);
            Object[] a = new Object[sids.length + this.string.length()];
            int i = 0;
            while (i < this.string.length()) {
                a[i] = Integer.valueOf(i);
                i++;
            }
            System.arraycopy(sids, 0, a, i, sids.length);
            return a;
        }
        return super.getIds(nonEnumerable, getSymbols);
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.ScriptableObject
    protected ScriptableObject getOwnPropertyDescriptor(Context cx, Object id) {
        if (!(id instanceof Symbol) && cx != null && cx.getLanguageVersion() >= 200) {
            ScriptRuntime.StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, id);
            if (s.stringId == null && 0 <= s.index && s.index < this.string.length()) {
                String value = String.valueOf(this.string.charAt(s.index));
                return defaultIndexPropertyDescriptor(value);
            }
        }
        return super.getOwnPropertyDescriptor(cx, id);
    }

    private ScriptableObject defaultIndexPropertyDescriptor(Object value) {
        Scriptable scope = getParentScope();
        if (scope == null) {
            scope = this;
        }
        ScriptableObject desc = new NativeObject();
        ScriptRuntime.setBuiltinProtoAndParent(desc, scope, TopLevel.Builtins.Object);
        desc.defineProperty(ES6Iterator.VALUE_PROPERTY, value, 0);
        desc.defineProperty("writable", Boolean.FALSE, 0);
        desc.defineProperty("enumerable", Boolean.TRUE, 0);
        desc.defineProperty("configurable", Boolean.FALSE, 0);
        return desc;
    }

    private static int js_indexOf(int methodId, String target, Object[] args) {
        String searchStr = ScriptRuntime.toString(args, 0);
        double position = ScriptRuntime.toInteger(args, 1);
        if (methodId != 41 && methodId != 42 && searchStr.length() == 0) {
            return position > ((double) target.length()) ? target.length() : (int) position;
        }
        if (methodId != 41 && methodId != 42 && position > target.length()) {
            return -1;
        }
        if (position < 0.0d) {
            position = 0.0d;
        } else if (position > target.length()) {
            position = target.length();
        } else if (methodId == 42 && (Double.isNaN(position) || position > target.length())) {
            position = target.length();
        }
        if (42 == methodId) {
            if (args.length == 0 || args.length == 1 || (args.length == 2 && args[1] == Undefined.instance)) {
                position = target.length();
            }
            return target.substring(0, (int) position).endsWith(searchStr) ? 0 : -1;
        }
        if (methodId == 41) {
            return target.startsWith(searchStr, (int) position) ? 0 : -1;
        }
        return target.indexOf(searchStr, (int) position);
    }

    private static int js_lastIndexOf(String target, Object[] args) {
        String search = ScriptRuntime.toString(args, 0);
        double end = ScriptRuntime.toNumber(args, 1);
        if (Double.isNaN(end) || end > target.length()) {
            end = target.length();
        } else if (end < 0.0d) {
            end = 0.0d;
        }
        return target.lastIndexOf(search, (int) end);
    }

    private static CharSequence js_substring(Context cx, CharSequence target, Object[] args) {
        double end;
        int length = target.length();
        double start = ScriptRuntime.toInteger(args, 0);
        if (start < 0.0d) {
            start = 0.0d;
        } else if (start > length) {
            start = length;
        }
        if (args.length <= 1 || args[1] == Undefined.instance) {
            end = length;
        } else {
            end = ScriptRuntime.toInteger(args[1]);
            if (end < 0.0d) {
                end = 0.0d;
            } else if (end > length) {
                end = length;
            }
            if (end < start) {
                if (cx.getLanguageVersion() != 120) {
                    double temp = start;
                    start = end;
                    end = temp;
                } else {
                    end = start;
                }
            }
        }
        return target.subSequence((int) start, (int) end);
    }

    int getLength() {
        return this.string.length();
    }

    private static CharSequence js_substr(CharSequence target, Object[] args) {
        if (args.length < 1) {
            return target;
        }
        double begin = ScriptRuntime.toInteger(args[0]);
        int length = target.length();
        if (begin < 0.0d) {
            begin += (double) length;
            if (begin < 0.0d) {
                begin = 0.0d;
            }
        } else if (begin > length) {
            begin = length;
        }
        double end = length;
        if (args.length > 1) {
            Object lengthArg = args[1];
            if (!Undefined.isUndefined(lengthArg)) {
                double end2 = ScriptRuntime.toInteger(lengthArg);
                if (end2 < 0.0d) {
                    end2 = 0.0d;
                }
                end = end2 + begin;
                if (end > length) {
                    end = length;
                }
            }
        }
        return target.subSequence((int) begin, (int) end);
    }

    private static String js_concat(String target, Object[] args) {
        int N = args.length;
        if (N == 0) {
            return target;
        }
        if (N == 1) {
            String arg = ScriptRuntime.toString(args[0]);
            return target.concat(arg);
        }
        int size = target.length();
        String[] argsAsStrings = new String[N];
        for (int i = 0; i != N; i++) {
            String s = ScriptRuntime.toString(args[i]);
            argsAsStrings[i] = s;
            size += s.length();
        }
        StringBuilder result = new StringBuilder(size);
        result.append(target);
        for (int i2 = 0; i2 != N; i2++) {
            result.append(argsAsStrings[i2]);
        }
        return result.toString();
    }

    private static CharSequence js_slice(CharSequence target, Object[] args) {
        double end;
        double begin = args.length < 1 ? 0.0d : ScriptRuntime.toInteger(args[0]);
        int length = target.length();
        if (begin < 0.0d) {
            begin += (double) length;
            if (begin < 0.0d) {
                begin = 0.0d;
            }
        } else if (begin > length) {
            begin = length;
        }
        if (args.length < 2 || args[1] == Undefined.instance) {
            end = length;
        } else {
            end = ScriptRuntime.toInteger(args[1]);
            if (end < 0.0d) {
                end += (double) length;
                if (end < 0.0d) {
                    end = 0.0d;
                }
            } else if (end > length) {
                end = length;
            }
            if (end < begin) {
                end = begin;
            }
        }
        return target.subSequence((int) begin, (int) end);
    }

    private static String js_repeat(Context cx, Scriptable thisObj, IdFunctionObject f, Object[] args) {
        String str = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
        double cnt = ScriptRuntime.toInteger(args, 0);
        if (cnt < 0.0d || cnt == Double.POSITIVE_INFINITY) {
            throw ScriptRuntime.rangeError("Invalid count value");
        }
        if (cnt == 0.0d || str.length() == 0) {
            return "";
        }
        long size = ((long) str.length()) * ((long) cnt);
        if (cnt > 2.147483647E9d || size > 2147483647L) {
            throw ScriptRuntime.rangeError("Invalid size or count value");
        }
        StringBuilder sb = new StringBuilder((int) size);
        sb.append(str);
        int i = 1;
        int icnt = (int) cnt;
        while (i <= icnt / 2) {
            sb.append((CharSequence) sb);
            i *= 2;
        }
        if (i < icnt) {
            sb.append(sb.substring(0, str.length() * (icnt - i)));
        }
        return sb.toString();
    }

    private static String js_pad(Context cx, Scriptable thisObj, IdFunctionObject f, Object[] args, boolean atStart) {
        String pad = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
        long intMaxLength = ScriptRuntime.toLength(args, 0);
        if (intMaxLength <= pad.length()) {
            return pad;
        }
        String filler = " ";
        if (args.length >= 2 && !Undefined.isUndefined(args[1])) {
            filler = ScriptRuntime.toString(args[1]);
            if (filler.length() < 1) {
                return pad;
            }
        }
        int fillLen = (int) (intMaxLength - ((long) pad.length()));
        StringBuilder concat = new StringBuilder();
        do {
            concat.append(filler);
        } while (concat.length() < fillLen);
        concat.setLength(fillLen);
        if (atStart) {
            return concat.append(pad).toString();
        }
        return concat.insert(0, pad).toString();
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(Symbol k) {
        if (SymbolKey.ITERATOR.equals(k)) {
            return 48;
        }
        return 0;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:107:0x04a1 A[PHI: r5 r6
      0x04a1: PHI (r5v1 'id' int) = 
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v3 'id' int)
      (r5v4 'id' int)
      (r5v5 'id' int)
      (r5v6 'id' int)
      (r5v0 'id' int)
      (r5v7 'id' int)
      (r5v8 'id' int)
      (r5v9 'id' int)
      (r5v10 'id' int)
      (r5v11 'id' int)
      (r5v0 'id' int)
      (r5v12 'id' int)
      (r5v13 'id' int)
      (r5v0 'id' int)
      (r5v14 'id' int)
      (r5v15 'id' int)
      (r5v16 'id' int)
      (r5v17 'id' int)
      (r5v18 'id' int)
      (r5v0 'id' int)
      (r5v19 'id' int)
      (r5v20 'id' int)
      (r5v21 'id' int)
      (r5v22 'id' int)
      (r5v23 'id' int)
      (r5v24 'id' int)
      (r5v25 'id' int)
      (r5v0 'id' int)
      (r5v26 'id' int)
      (r5v27 'id' int)
      (r5v28 'id' int)
      (r5v29 'id' int)
      (r5v30 'id' int)
      (r5v0 'id' int)
      (r5v31 'id' int)
      (r5v32 'id' int)
      (r5v33 'id' int)
      (r5v34 'id' int)
      (r5v35 'id' int)
      (r5v36 'id' int)
      (r5v0 'id' int)
      (r5v37 'id' int)
      (r5v38 'id' int)
      (r5v39 'id' int)
      (r5v0 'id' int)
      (r5v40 'id' int)
      (r5v41 'id' int)
      (r5v42 'id' int)
      (r5v43 'id' int)
      (r5v44 'id' int)
      (r5v45 'id' int)
      (r5v0 'id' int)
      (r5v46 'id' int)
      (r5v47 'id' int)
      (r5v48 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
     binds: [B:3:0x0008, B:105:0x0495, B:106:0x0498, B:103:0x0488, B:100:0x0470, B:99:0x0467, B:92:0x0404, B:97:0x045b, B:96:0x0453, B:95:0x044a, B:94:0x0441, B:93:0x0438, B:89:0x03f3, B:90:0x03f6, B:87:0x03e6, B:78:0x0375, B:83:0x03cc, B:82:0x03c3, B:81:0x03ba, B:80:0x03b1, B:79:0x03a8, B:68:0x02c1, B:75:0x0364, B:74:0x035b, B:73:0x0352, B:72:0x034a, B:71:0x0341, B:70:0x0338, B:69:0x0330, B:60:0x0259, B:65:0x02af, B:64:0x02a6, B:63:0x029d, B:62:0x0294, B:61:0x028c, B:44:0x018a, B:57:0x0248, B:56:0x023f, B:55:0x0236, B:54:0x022d, B:53:0x0224, B:52:0x021c, B:50:0x0210, B:51:0x0213, B:48:0x0203, B:45:0x01ec, B:35:0x00fa, B:41:0x0179, B:40:0x0170, B:39:0x0167, B:38:0x015e, B:37:0x0155, B:36:0x014c, B:32:0x00e9, B:33:0x00ec, B:30:0x00dc, B:27:0x00cc, B:19:0x00a1, B:21:0x00ab, B:23:0x00b5, B:14:0x008a, B:16:0x0094, B:7:0x0069, B:9:0x0073] A[DONT_GENERATE, DONT_INLINE]
      0x04a1: PHI (r6v1 'X' java.lang.String) = 
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v2 'X' java.lang.String)
      (r6v3 'X' java.lang.String)
      (r6v4 'X' java.lang.String)
      (r6v5 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v6 'X' java.lang.String)
      (r6v7 'X' java.lang.String)
      (r6v8 'X' java.lang.String)
      (r6v9 'X' java.lang.String)
      (r6v10 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v11 'X' java.lang.String)
      (r6v12 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v13 'X' java.lang.String)
      (r6v14 'X' java.lang.String)
      (r6v15 'X' java.lang.String)
      (r6v16 'X' java.lang.String)
      (r6v17 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v18 'X' java.lang.String)
      (r6v19 'X' java.lang.String)
      (r6v20 'X' java.lang.String)
      (r6v21 'X' java.lang.String)
      (r6v22 'X' java.lang.String)
      (r6v23 'X' java.lang.String)
      (r6v24 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v25 'X' java.lang.String)
      (r6v26 'X' java.lang.String)
      (r6v27 'X' java.lang.String)
      (r6v28 'X' java.lang.String)
      (r6v29 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v30 'X' java.lang.String)
      (r6v31 'X' java.lang.String)
      (r6v32 'X' java.lang.String)
      (r6v33 'X' java.lang.String)
      (r6v34 'X' java.lang.String)
      (r6v35 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v36 'X' java.lang.String)
      (r6v37 'X' java.lang.String)
      (r6v38 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v39 'X' java.lang.String)
      (r6v40 'X' java.lang.String)
      (r6v41 'X' java.lang.String)
      (r6v42 'X' java.lang.String)
      (r6v43 'X' java.lang.String)
      (r6v44 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v45 'X' java.lang.String)
      (r6v46 'X' java.lang.String)
      (r6v47 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
     binds: [B:3:0x0008, B:105:0x0495, B:106:0x0498, B:103:0x0488, B:100:0x0470, B:99:0x0467, B:92:0x0404, B:97:0x045b, B:96:0x0453, B:95:0x044a, B:94:0x0441, B:93:0x0438, B:89:0x03f3, B:90:0x03f6, B:87:0x03e6, B:78:0x0375, B:83:0x03cc, B:82:0x03c3, B:81:0x03ba, B:80:0x03b1, B:79:0x03a8, B:68:0x02c1, B:75:0x0364, B:74:0x035b, B:73:0x0352, B:72:0x034a, B:71:0x0341, B:70:0x0338, B:69:0x0330, B:60:0x0259, B:65:0x02af, B:64:0x02a6, B:63:0x029d, B:62:0x0294, B:61:0x028c, B:44:0x018a, B:57:0x0248, B:56:0x023f, B:55:0x0236, B:54:0x022d, B:53:0x0224, B:52:0x021c, B:50:0x0210, B:51:0x0213, B:48:0x0203, B:45:0x01ec, B:35:0x00fa, B:41:0x0179, B:40:0x0170, B:39:0x0167, B:38:0x015e, B:37:0x0155, B:36:0x014c, B:32:0x00e9, B:33:0x00ec, B:30:0x00dc, B:27:0x00cc, B:19:0x00a1, B:21:0x00ab, B:23:0x00b5, B:14:0x008a, B:16:0x0094, B:7:0x0069, B:9:0x0073] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:109:0x04a5  */
    /* JADX WARN: Switch 'out' block B:107:0x04a1 for B:35:0x00fa already processed. Defaulting to fallback option. */
    /* JADX WARN: Switch 'out' block B:107:0x04a1 for B:44:0x018a already processed. Defaulting to fallback option. */
    /* JADX WARN: Switch 'out' block B:107:0x04a1 for B:60:0x0259 already processed. Defaulting to fallback option. */
    /* JADX WARN: Switch 'out' block B:107:0x04a1 for B:68:0x02c1 already processed. Defaulting to fallback option. */
    /* JADX WARN: Switch 'out' block B:107:0x04a1 for B:78:0x0375 already processed. Defaulting to fallback option. */
    /* JADX WARN: Switch 'out' block B:107:0x04a1 for B:92:0x0404 already processed. Defaulting to fallback option. */
    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        int id = 0;
        String X = null;
        switch (s.length()) {
            case 3:
                int c = s.charAt(2);
                if (c != 98) {
                    if (c != 103) {
                        if (c == 112 && s.charAt(0) == 's' && s.charAt(1) == 'u') {
                            id = 23;
                        } else if (X != null) {
                            id = 0;
                        }
                    } else if (s.charAt(0) == 'b' && s.charAt(1) == 'i') {
                        id = 21;
                    } else if (X != null) {
                        id = 0;
                    }
                } else if (s.charAt(0) == 's' && s.charAt(1) == 'u') {
                    id = 24;
                } else if (X != null && X != s && !X.equals(s)) {
                    id = 0;
                }
                break;
            case 4:
                int c2 = s.charAt(0);
                if (c2 == 98) {
                    X = Markup.CSS_VALUE_BOLD;
                    id = 16;
                } else if (c2 == 108) {
                    X = "link";
                    id = 27;
                } else if (c2 == 116) {
                    X = "trim";
                    id = 37;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 5:
                switch (s.charAt(4)) {
                    case 'd':
                        X = "fixed";
                        id = 18;
                        break;
                    case 'e':
                        X = "slice";
                        id = 15;
                        break;
                    case 'h':
                        X = "match";
                        id = 31;
                        break;
                    case 'k':
                        X = "blink";
                        id = 22;
                        break;
                    case 'l':
                        X = "small";
                        id = 20;
                        break;
                    case 't':
                        X = "split";
                        id = 9;
                        break;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 6:
                switch (s.charAt(1)) {
                    case 'a':
                        X = "padEnd";
                        id = 47;
                        break;
                    case 'e':
                        int c3 = s.charAt(0);
                        if (c3 == 114) {
                            X = "repeat";
                            id = 44;
                        } else if (c3 == 115) {
                            X = "search";
                            id = 32;
                        }
                        break;
                    case 'h':
                        X = "charAt";
                        id = 5;
                        break;
                    case 'n':
                        X = ElementTags.ANCHOR;
                        id = 28;
                        break;
                    case 'o':
                        X = "concat";
                        id = 14;
                        break;
                    case 'q':
                        X = "equals";
                        id = 29;
                        break;
                    case 't':
                        X = "strike";
                        id = 19;
                        break;
                    case 'u':
                        X = "substr";
                        id = 13;
                        break;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 7:
                switch (s.charAt(1)) {
                    case 'a':
                        X = "valueOf";
                        id = 4;
                        break;
                    case 'e':
                        X = "replace";
                        id = 33;
                        break;
                    case 'n':
                        X = "indexOf";
                        id = 7;
                        break;
                    case 'r':
                        X = "trimEnd";
                        id = 50;
                        break;
                    case 't':
                        X = "italics";
                        id = 17;
                        break;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 8:
                switch (s.charAt(6)) {
                    case 'c':
                        X = "toSource";
                        id = 3;
                        break;
                    case 'e':
                        X = "includes";
                        id = 40;
                        break;
                    case 'f':
                        X = "trimLeft";
                        id = 38;
                        break;
                    case 'n':
                        X = "toString";
                        id = 2;
                        break;
                    case 'r':
                        X = "padStart";
                        id = 46;
                        break;
                    case 't':
                        X = "endsWith";
                        id = 42;
                        break;
                    case 'z':
                        X = "fontsize";
                        id = 25;
                        break;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 9:
                switch (s.charAt(4)) {
                    case 'R':
                        X = "trimRight";
                        id = 39;
                        break;
                    case 'S':
                        X = "trimStart";
                        id = 49;
                        break;
                    case 'a':
                        X = "normalize";
                        id = 43;
                        break;
                    case 'c':
                        X = "fontcolor";
                        id = 26;
                        break;
                    case 't':
                        X = "substring";
                        id = 10;
                        break;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 10:
                int c4 = s.charAt(0);
                if (c4 == 99) {
                    X = "charCodeAt";
                    id = 6;
                } else if (c4 == 115) {
                    X = "startsWith";
                    id = 41;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 11:
                switch (s.charAt(2)) {
                    case 'L':
                        X = "toLowerCase";
                        id = 11;
                        break;
                    case 'U':
                        X = "toUpperCase";
                        id = 12;
                        break;
                    case 'd':
                        X = "codePointAt";
                        id = 45;
                        break;
                    case 'n':
                        X = "constructor";
                        id = 1;
                        break;
                    case 's':
                        X = "lastIndexOf";
                        id = 8;
                        break;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 12:
            case 14:
            case 15:
            default:
                if (X != null) {
                    id = 0;
                }
                break;
            case 13:
                X = "localeCompare";
                id = 34;
                if (X != null) {
                    id = 0;
                }
                break;
            case 16:
                X = "equalsIgnoreCase";
                id = 30;
                if (X != null) {
                    id = 0;
                }
                break;
            case 17:
                int c5 = s.charAt(8);
                if (c5 == 76) {
                    X = "toLocaleLowerCase";
                    id = 35;
                } else if (c5 == 85) {
                    X = "toLocaleUpperCase";
                    id = 36;
                }
                if (X != null) {
                    id = 0;
                }
                break;
        }
        return id;
    }
}
