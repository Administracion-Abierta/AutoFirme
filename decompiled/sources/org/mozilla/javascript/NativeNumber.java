package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeNumber.class */
final class NativeNumber extends IdScriptableObject {
    private static final long serialVersionUID = 3504516769741512101L;
    public static final double MAX_SAFE_INTEGER = 9.007199254740991E15d;
    private static final Object NUMBER_TAG = "Number";
    private static final int MAX_PRECISION = 100;
    private static final double MIN_SAFE_INTEGER = -9.007199254740991E15d;
    private static final int ConstructorId_isFinite = -1;
    private static final int ConstructorId_isNaN = -2;
    private static final int ConstructorId_isInteger = -3;
    private static final int ConstructorId_isSafeInteger = -4;
    private static final int ConstructorId_parseFloat = -5;
    private static final int ConstructorId_parseInt = -6;
    private static final int Id_constructor = 1;
    private static final int Id_toString = 2;
    private static final int Id_toLocaleString = 3;
    private static final int Id_toSource = 4;
    private static final int Id_valueOf = 5;
    private static final int Id_toFixed = 6;
    private static final int Id_toExponential = 7;
    private static final int Id_toPrecision = 8;
    private static final int MAX_PROTOTYPE_ID = 8;
    private double doubleValue;

    static void init(Scriptable scope, boolean sealed) {
        NativeNumber obj = new NativeNumber(0.0d);
        obj.exportAsJSClass(8, scope, sealed);
    }

    NativeNumber(double number) {
        this.doubleValue = number;
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "Number";
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        ctor.defineProperty("NaN", ScriptRuntime.NaNobj, 7);
        ctor.defineProperty("POSITIVE_INFINITY", ScriptRuntime.wrapNumber(Double.POSITIVE_INFINITY), 7);
        ctor.defineProperty("NEGATIVE_INFINITY", ScriptRuntime.wrapNumber(Double.NEGATIVE_INFINITY), 7);
        ctor.defineProperty("MAX_VALUE", ScriptRuntime.wrapNumber(Double.MAX_VALUE), 7);
        ctor.defineProperty("MIN_VALUE", ScriptRuntime.wrapNumber(Double.MIN_VALUE), 7);
        ctor.defineProperty("MAX_SAFE_INTEGER", ScriptRuntime.wrapNumber(9.007199254740991E15d), 7);
        ctor.defineProperty("MIN_SAFE_INTEGER", ScriptRuntime.wrapNumber(MIN_SAFE_INTEGER), 7);
        addIdFunctionProperty(ctor, NUMBER_TAG, -1, "isFinite", 1);
        addIdFunctionProperty(ctor, NUMBER_TAG, -2, "isNaN", 1);
        addIdFunctionProperty(ctor, NUMBER_TAG, -3, "isInteger", 1);
        addIdFunctionProperty(ctor, NUMBER_TAG, -4, "isSafeInteger", 1);
        addIdFunctionProperty(ctor, NUMBER_TAG, -5, "parseFloat", 1);
        addIdFunctionProperty(ctor, NUMBER_TAG, -6, "parseInt", 1);
        super.fillConstructorProperties(ctor);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        int arity;
        String s;
        switch (id) {
            case 1:
                arity = 1;
                s = "constructor";
                break;
            case 2:
                arity = 1;
                s = "toString";
                break;
            case 3:
                arity = 1;
                s = "toLocaleString";
                break;
            case 4:
                arity = 0;
                s = "toSource";
                break;
            case 5:
                arity = 0;
                s = "valueOf";
                break;
            case 6:
                arity = 1;
                s = "toFixed";
                break;
            case 7:
                arity = 1;
                s = "toExponential";
                break;
            case 8:
                arity = 1;
                s = "toPrecision";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(NUMBER_TAG, id, s, arity);
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(NUMBER_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        if (id == 1) {
            double val = args.length >= 1 ? ScriptRuntime.toNumber(args[0]) : 0.0d;
            if (thisObj == null) {
                return new NativeNumber(val);
            }
            return ScriptRuntime.wrapNumber(val);
        }
        if (id < 1) {
            return execConstructorCall(id, args);
        }
        if (!(thisObj instanceof NativeNumber)) {
            throw incompatibleCallError(f);
        }
        double value = ((NativeNumber) thisObj).doubleValue;
        switch (id) {
            case 2:
            case 3:
                int base = (args.length == 0 || args[0] == Undefined.instance) ? 10 : ScriptRuntime.toInt32(args[0]);
                return ScriptRuntime.numberToString(value, base);
            case 4:
                return "(new Number(" + ScriptRuntime.toString(value) + "))";
            case 5:
                return ScriptRuntime.wrapNumber(value);
            case 6:
                int precisionMin = cx.version < 200 ? -20 : 0;
                return num_to(value, args, 2, 2, precisionMin, 0);
            case 7:
                if (Double.isNaN(value)) {
                    return "NaN";
                }
                if (!Double.isInfinite(value)) {
                    return num_to(value, args, 1, 3, 0, 1);
                }
                if (value >= 0.0d) {
                    return "Infinity";
                }
                return "-Infinity";
            case 8:
                if (args.length == 0 || args[0] == Undefined.instance) {
                    return ScriptRuntime.numberToString(value, 10);
                }
                if (Double.isNaN(value)) {
                    return "NaN";
                }
                if (!Double.isInfinite(value)) {
                    return num_to(value, args, 0, 4, 1, 0);
                }
                if (value >= 0.0d) {
                    return "Infinity";
                }
                return "-Infinity";
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    private static Object execConstructorCall(int id, Object[] args) {
        switch (id) {
            case -6:
                return NativeGlobal.js_parseInt(args);
            case -5:
                return NativeGlobal.js_parseFloat(args);
            case -4:
                if (args.length == 0 || Undefined.instance == args[0]) {
                    return Boolean.FALSE;
                }
                if (args[0] instanceof Number) {
                    return Boolean.valueOf(isSafeInteger((Number) args[0]));
                }
                return Boolean.FALSE;
            case -3:
                if (args.length == 0 || Undefined.instance == args[0]) {
                    return Boolean.FALSE;
                }
                if (args[0] instanceof Number) {
                    return Boolean.valueOf(isInteger((Number) args[0]));
                }
                return Boolean.FALSE;
            case -2:
                if (args.length == 0 || Undefined.instance == args[0]) {
                    return Boolean.FALSE;
                }
                if (args[0] instanceof Number) {
                    return isNaN((Number) args[0]);
                }
                return Boolean.FALSE;
            case -1:
                if (args.length == 0 || Undefined.instance == args[0]) {
                    return Boolean.FALSE;
                }
                if (args[0] instanceof Number) {
                    return isFinite(args[0]);
                }
                return Boolean.FALSE;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    public String toString() {
        return ScriptRuntime.numberToString(this.doubleValue, 10);
    }

    private static String num_to(double val, Object[] args, int zeroArgMode, int oneArgMode, int precisionMin, int precisionOffset) {
        int precision;
        if (args.length == 0) {
            precision = 0;
            oneArgMode = zeroArgMode;
        } else {
            double p = ScriptRuntime.toInteger(args[0]);
            if (p < precisionMin || p > 100.0d) {
                String msg = ScriptRuntime.getMessage1("msg.bad.precision", ScriptRuntime.toString(args[0]));
                throw ScriptRuntime.rangeError(msg);
            }
            precision = ScriptRuntime.toInt32(p);
        }
        StringBuilder sb = new StringBuilder();
        DToA.JS_dtostr(sb, oneArgMode, precision + precisionOffset, val);
        return sb.toString();
    }

    static Object isFinite(Object val) {
        double d = ScriptRuntime.toNumber(val);
        Double nd = Double.valueOf(d);
        return ScriptRuntime.wrapBoolean((nd.isInfinite() || nd.isNaN()) ? false : true);
    }

    private static Boolean isNaN(Number val) {
        if (val instanceof Double) {
            return Boolean.valueOf(((Double) val).isNaN());
        }
        double d = val.doubleValue();
        return Boolean.valueOf(Double.isNaN(d));
    }

    private static boolean isInteger(Number val) {
        if (val instanceof Double) {
            return isDoubleInteger((Double) val);
        }
        return isDoubleInteger(val.doubleValue());
    }

    private static boolean isDoubleInteger(Double d) {
        return (d.isInfinite() || d.isNaN() || Math.floor(d.doubleValue()) != d.doubleValue()) ? false : true;
    }

    private static boolean isDoubleInteger(double d) {
        return (Double.isInfinite(d) || Double.isNaN(d) || Math.floor(d) != d) ? false : true;
    }

    private static boolean isSafeInteger(Number val) {
        if (val instanceof Double) {
            return isDoubleSafeInteger((Double) val);
        }
        return isDoubleSafeInteger(val.doubleValue());
    }

    private static boolean isDoubleSafeInteger(Double d) {
        return isDoubleInteger(d) && d.doubleValue() <= 9.007199254740991E15d && d.doubleValue() >= MIN_SAFE_INTEGER;
    }

    private static boolean isDoubleSafeInteger(double d) {
        return isDoubleInteger(d) && d <= 9.007199254740991E15d && d >= MIN_SAFE_INTEGER;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        int id = 0;
        String X = null;
        switch (s.length()) {
            case 7:
                int c = s.charAt(0);
                if (c == 116) {
                    X = "toFixed";
                    id = 6;
                } else if (c == 118) {
                    X = "valueOf";
                    id = 5;
                }
                break;
            case 8:
                int c2 = s.charAt(3);
                if (c2 == 111) {
                    X = "toSource";
                    id = 4;
                } else if (c2 == 116) {
                    X = "toString";
                    id = 2;
                }
                break;
            case 11:
                int c3 = s.charAt(0);
                if (c3 == 99) {
                    X = "constructor";
                    id = 1;
                } else if (c3 == 116) {
                    X = "toPrecision";
                    id = 8;
                }
                break;
            case 13:
                X = "toExponential";
                id = 7;
                break;
            case 14:
                X = "toLocaleString";
                id = 3;
                break;
        }
        if (X != null && X != s && !X.equals(s)) {
            id = 0;
        }
        return id;
    }
}
