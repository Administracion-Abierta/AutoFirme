package org.mozilla.javascript.regexp;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/regexp/NativeRegExpCtor.class */
class NativeRegExpCtor extends BaseFunction {
    private static final long serialVersionUID = -5733330028285400526L;
    private static final int Id_multiline = 1;
    private static final int Id_STAR = 2;
    private static final int Id_input = 3;
    private static final int Id_UNDERSCORE = 4;
    private static final int Id_lastMatch = 5;
    private static final int Id_AMPERSAND = 6;
    private static final int Id_lastParen = 7;
    private static final int Id_PLUS = 8;
    private static final int Id_leftContext = 9;
    private static final int Id_BACK_QUOTE = 10;
    private static final int Id_rightContext = 11;
    private static final int Id_QUOTE = 12;
    private static final int DOLLAR_ID_BASE = 12;
    private static final int Id_DOLLAR_1 = 13;
    private static final int Id_DOLLAR_2 = 14;
    private static final int Id_DOLLAR_3 = 15;
    private static final int Id_DOLLAR_4 = 16;
    private static final int Id_DOLLAR_5 = 17;
    private static final int Id_DOLLAR_6 = 18;
    private static final int Id_DOLLAR_7 = 19;
    private static final int Id_DOLLAR_8 = 20;
    private static final int Id_DOLLAR_9 = 21;
    private static final int MAX_INSTANCE_ID = 21;
    private int multilineAttr = 4;
    private int starAttr = 4;
    private int inputAttr = 4;
    private int underscoreAttr = 4;

    NativeRegExpCtor() {
    }

    @Override // org.mozilla.javascript.BaseFunction
    public String getFunctionName() {
        return "RegExp";
    }

    @Override // org.mozilla.javascript.BaseFunction
    public int getLength() {
        return 2;
    }

    @Override // org.mozilla.javascript.BaseFunction
    public int getArity() {
        return 2;
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.Function, org.mozilla.javascript.Callable
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (args.length > 0 && (args[0] instanceof NativeRegExp) && (args.length == 1 || args[1] == Undefined.instance)) {
            return args[0];
        }
        return construct(cx, scope, args);
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.Function
    public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
        NativeRegExp re = new NativeRegExp();
        re.compile(cx, scope, args);
        ScriptRuntime.setBuiltinProtoAndParent(re, scope, TopLevel.Builtins.RegExp);
        return re;
    }

    private static RegExpImpl getImpl() {
        Context cx = Context.getCurrentContext();
        return (RegExpImpl) ScriptRuntime.getRegExpProxy(cx);
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.IdScriptableObject
    protected int getMaxInstanceId() {
        return super.getMaxInstanceId() + 21;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:64:0x0284 A[PHI: r6 r7
      0x0284: PHI (r6v1 'id' int) = 
      (r6v0 'id' int)
      (r6v3 'id' int)
      (r6v4 'id' int)
      (r6v0 'id' int)
      (r6v5 'id' int)
      (r6v6 'id' int)
      (r6v7 'id' int)
      (r6v8 'id' int)
      (r6v0 'id' int)
      (r6v0 'id' int)
      (r6v0 'id' int)
      (r6v0 'id' int)
      (r6v0 'id' int)
      (r6v0 'id' int)
      (r6v0 'id' int)
      (r6v0 'id' int)
      (r6v0 'id' int)
      (r6v0 'id' int)
      (r6v0 'id' int)
      (r6v0 'id' int)
      (r6v0 'id' int)
      (r6v0 'id' int)
      (r6v0 'id' int)
      (r6v0 'id' int)
     binds: [B:3:0x0008, B:63:0x027b, B:62:0x0272, B:60:0x0267, B:61:0x026a, B:58:0x025a, B:55:0x024b, B:52:0x0235, B:5:0x0049, B:49:0x0229, B:46:0x021a, B:43:0x020a, B:40:0x01fa, B:37:0x01ea, B:34:0x01da, B:31:0x01ca, B:28:0x01ba, B:25:0x01aa, B:22:0x019a, B:19:0x018a, B:16:0x017a, B:13:0x016b, B:10:0x015b, B:7:0x014b] A[DONT_GENERATE, DONT_INLINE]
      0x0284: PHI (r7v1 'X' java.lang.String) = 
      (r7v0 'X' java.lang.String)
      (r7v2 'X' java.lang.String)
      (r7v3 'X' java.lang.String)
      (r7v0 'X' java.lang.String)
      (r7v4 'X' java.lang.String)
      (r7v5 'X' java.lang.String)
      (r7v6 'X' java.lang.String)
      (r7v7 'X' java.lang.String)
      (r7v0 'X' java.lang.String)
      (r7v0 'X' java.lang.String)
      (r7v0 'X' java.lang.String)
      (r7v0 'X' java.lang.String)
      (r7v0 'X' java.lang.String)
      (r7v0 'X' java.lang.String)
      (r7v0 'X' java.lang.String)
      (r7v0 'X' java.lang.String)
      (r7v0 'X' java.lang.String)
      (r7v0 'X' java.lang.String)
      (r7v0 'X' java.lang.String)
      (r7v0 'X' java.lang.String)
      (r7v0 'X' java.lang.String)
      (r7v0 'X' java.lang.String)
      (r7v0 'X' java.lang.String)
      (r7v0 'X' java.lang.String)
     binds: [B:3:0x0008, B:63:0x027b, B:62:0x0272, B:60:0x0267, B:61:0x026a, B:58:0x025a, B:55:0x024b, B:52:0x0235, B:5:0x0049, B:49:0x0229, B:46:0x021a, B:43:0x020a, B:40:0x01fa, B:37:0x01ea, B:34:0x01da, B:31:0x01ca, B:28:0x01ba, B:25:0x01aa, B:22:0x019a, B:19:0x018a, B:16:0x017a, B:13:0x016b, B:10:0x015b, B:7:0x014b] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:66:0x0288  */
    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.IdScriptableObject
    protected int findInstanceIdInfo(String s) {
        int attr;
        int id = 0;
        String X = null;
        switch (s.length()) {
            case 2:
                switch (s.charAt(1)) {
                    case '&':
                        if (s.charAt(0) == '$') {
                            id = 6;
                        } else if (X != null && X != s && !X.equals(s)) {
                            id = 0;
                        }
                        break;
                    case '\'':
                        if (s.charAt(0) == '$') {
                            id = 12;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case '(':
                    case ')':
                    case ',':
                    case '-':
                    case '.':
                    case '/':
                    case '0':
                    case ':':
                    case ';':
                    case '<':
                    case '=':
                    case '>':
                    case '?':
                    case '@':
                    case 'A':
                    case 'B':
                    case 'C':
                    case 'D':
                    case 'E':
                    case 'F':
                    case 'G':
                    case 'H':
                    case 'I':
                    case 'J':
                    case 'K':
                    case 'L':
                    case 'M':
                    case 'N':
                    case 'O':
                    case 'P':
                    case 'Q':
                    case 'R':
                    case 'S':
                    case 'T':
                    case 'U':
                    case 'V':
                    case 'W':
                    case 'X':
                    case 'Y':
                    case 'Z':
                    case '[':
                    case '\\':
                    case ']':
                    case '^':
                    default:
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case '*':
                        if (s.charAt(0) == '$') {
                            id = 2;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case '+':
                        if (s.charAt(0) == '$') {
                            id = 8;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case '1':
                        if (s.charAt(0) == '$') {
                            id = 13;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case '2':
                        if (s.charAt(0) == '$') {
                            id = 14;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case '3':
                        if (s.charAt(0) == '$') {
                            id = 15;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case '4':
                        if (s.charAt(0) == '$') {
                            id = 16;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case '5':
                        if (s.charAt(0) == '$') {
                            id = 17;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case '6':
                        if (s.charAt(0) == '$') {
                            id = 18;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case '7':
                        if (s.charAt(0) == '$') {
                            id = 19;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case '8':
                        if (s.charAt(0) == '$') {
                            id = 20;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case '9':
                        if (s.charAt(0) == '$') {
                            id = 21;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case '_':
                        if (s.charAt(0) == '$') {
                            id = 4;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case '`':
                        if (s.charAt(0) == '$') {
                            id = 10;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                }
                break;
            case 3:
            case 4:
            case 6:
            case 7:
            case 8:
            case 10:
            default:
                if (X != null) {
                    id = 0;
                }
                break;
            case 5:
                X = "input";
                id = 3;
                if (X != null) {
                    id = 0;
                }
                break;
            case 9:
                int c = s.charAt(4);
                if (c == 77) {
                    X = "lastMatch";
                    id = 5;
                } else if (c == 80) {
                    X = "lastParen";
                    id = 7;
                } else if (c == 105) {
                    X = "multiline";
                    id = 1;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 11:
                X = "leftContext";
                id = 9;
                if (X != null) {
                    id = 0;
                }
                break;
            case 12:
                X = "rightContext";
                id = 11;
                if (X != null) {
                    id = 0;
                }
                break;
        }
        if (id == 0) {
            return super.findInstanceIdInfo(s);
        }
        switch (id) {
            case 1:
                attr = this.multilineAttr;
                break;
            case 2:
                attr = this.starAttr;
                break;
            case 3:
                attr = this.inputAttr;
                break;
            case 4:
                attr = this.underscoreAttr;
                break;
            default:
                attr = 5;
                break;
        }
        return instanceIdInfo(attr, super.getMaxInstanceId() + id);
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.IdScriptableObject
    protected String getInstanceIdName(int id) {
        int shifted = id - super.getMaxInstanceId();
        if (1 <= shifted && shifted <= 21) {
            switch (shifted) {
                case 1:
                    return "multiline";
                case 2:
                    return "$*";
                case 3:
                    return "input";
                case 4:
                    return "$_";
                case 5:
                    return "lastMatch";
                case 6:
                    return "$&";
                case 7:
                    return "lastParen";
                case 8:
                    return "$+";
                case 9:
                    return "leftContext";
                case 10:
                    return "$`";
                case 11:
                    return "rightContext";
                case 12:
                    return "$'";
                default:
                    int substring_number = (shifted - 12) - 1;
                    char[] buf = {'$', (char) (49 + substring_number)};
                    return new String(buf);
            }
        }
        return super.getInstanceIdName(id);
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.IdScriptableObject
    protected Object getInstanceIdValue(int id) {
        Object stringResult;
        int shifted = id - super.getMaxInstanceId();
        if (1 <= shifted && shifted <= 21) {
            RegExpImpl impl = getImpl();
            switch (shifted) {
                case 1:
                case 2:
                    return ScriptRuntime.wrapBoolean(impl.multiline);
                case 3:
                case 4:
                    stringResult = impl.input;
                    break;
                case 5:
                case 6:
                    stringResult = impl.lastMatch;
                    break;
                case 7:
                case 8:
                    stringResult = impl.lastParen;
                    break;
                case 9:
                case 10:
                    stringResult = impl.leftContext;
                    break;
                case 11:
                case 12:
                    stringResult = impl.rightContext;
                    break;
                default:
                    int substring_number = (shifted - 12) - 1;
                    stringResult = impl.getParenSubString(substring_number);
                    break;
            }
            return stringResult == null ? "" : stringResult.toString();
        }
        return super.getInstanceIdValue(id);
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.IdScriptableObject
    protected void setInstanceIdValue(int id, Object value) {
        int shifted = id - super.getMaxInstanceId();
        switch (shifted) {
            case 1:
            case 2:
                getImpl().multiline = ScriptRuntime.toBoolean(value);
                break;
            case 3:
            case 4:
                getImpl().input = ScriptRuntime.toString(value);
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                break;
            default:
                int substring_number = (shifted - 12) - 1;
                if (0 > substring_number || substring_number > 8) {
                    super.setInstanceIdValue(id, value);
                }
                break;
        }
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.IdScriptableObject
    protected void setInstanceIdAttributes(int id, int attr) {
        int shifted = id - super.getMaxInstanceId();
        switch (shifted) {
            case 1:
                this.multilineAttr = attr;
                break;
            case 2:
                this.starAttr = attr;
                break;
            case 3:
                this.inputAttr = attr;
                break;
            case 4:
                this.underscoreAttr = attr;
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                break;
            default:
                int substring_number = (shifted - 12) - 1;
                if (0 > substring_number || substring_number > 8) {
                    super.setInstanceIdAttributes(id, attr);
                }
                break;
        }
    }
}
