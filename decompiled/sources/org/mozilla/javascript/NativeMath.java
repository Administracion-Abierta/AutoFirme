package org.mozilla.javascript;

import es.gob.afirma.standalone.CommandLineParameters;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeMath.class */
final class NativeMath extends IdScriptableObject {
    private static final long serialVersionUID = -8838847185801131569L;
    private static final double LOG2E = 1.4426950408889634d;
    private static final int Id_toSource = 1;
    private static final int Id_abs = 2;
    private static final int Id_acos = 3;
    private static final int Id_asin = 4;
    private static final int Id_atan = 5;
    private static final int Id_atan2 = 6;
    private static final int Id_ceil = 7;
    private static final int Id_cos = 8;
    private static final int Id_exp = 9;
    private static final int Id_floor = 10;
    private static final int Id_log = 11;
    private static final int Id_max = 12;
    private static final int Id_min = 13;
    private static final int Id_pow = 14;
    private static final int Id_random = 15;
    private static final int Id_round = 16;
    private static final int Id_sin = 17;
    private static final int Id_sqrt = 18;
    private static final int Id_tan = 19;
    private static final int Id_cbrt = 20;
    private static final int Id_cosh = 21;
    private static final int Id_expm1 = 22;
    private static final int Id_hypot = 23;
    private static final int Id_log1p = 24;
    private static final int Id_log10 = 25;
    private static final int Id_sinh = 26;
    private static final int Id_tanh = 27;
    private static final int Id_imul = 28;
    private static final int Id_trunc = 29;
    private static final int Id_acosh = 30;
    private static final int Id_asinh = 31;
    private static final int Id_atanh = 32;
    private static final int Id_sign = 33;
    private static final int Id_log2 = 34;
    private static final int Id_fround = 35;
    private static final int Id_clz32 = 36;
    private static final int LAST_METHOD_ID = 36;
    private static final int Id_E = 37;
    private static final int Id_PI = 38;
    private static final int Id_LN10 = 39;
    private static final int Id_LN2 = 40;
    private static final int Id_LOG2E = 41;
    private static final int Id_LOG10E = 42;
    private static final int Id_SQRT1_2 = 43;
    private static final int Id_SQRT2 = 44;
    private static final int MAX_ID = 44;
    private static final Object MATH_TAG = "Math";
    private static final Double Double32 = Double.valueOf(32.0d);

    static void init(Scriptable scope, boolean sealed) {
        NativeMath obj = new NativeMath();
        obj.activatePrototypeMap(44);
        obj.setPrototype(getObjectPrototype(scope));
        obj.setParentScope(scope);
        if (sealed) {
            obj.sealObject();
        }
        ScriptableObject.defineProperty(scope, "Math", obj, 2);
    }

    private NativeMath() {
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "Math";
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        double x;
        String name;
        int arity;
        String name2;
        if (id <= 36) {
            switch (id) {
                case 1:
                    arity = 0;
                    name2 = "toSource";
                    break;
                case 2:
                    arity = 1;
                    name2 = "abs";
                    break;
                case 3:
                    arity = 1;
                    name2 = "acos";
                    break;
                case 4:
                    arity = 1;
                    name2 = "asin";
                    break;
                case 5:
                    arity = 1;
                    name2 = "atan";
                    break;
                case 6:
                    arity = 2;
                    name2 = "atan2";
                    break;
                case 7:
                    arity = 1;
                    name2 = "ceil";
                    break;
                case 8:
                    arity = 1;
                    name2 = "cos";
                    break;
                case 9:
                    arity = 1;
                    name2 = "exp";
                    break;
                case 10:
                    arity = 1;
                    name2 = "floor";
                    break;
                case 11:
                    arity = 1;
                    name2 = "log";
                    break;
                case 12:
                    arity = 2;
                    name2 = "max";
                    break;
                case 13:
                    arity = 2;
                    name2 = "min";
                    break;
                case 14:
                    arity = 2;
                    name2 = "pow";
                    break;
                case 15:
                    arity = 0;
                    name2 = "random";
                    break;
                case 16:
                    arity = 1;
                    name2 = "round";
                    break;
                case 17:
                    arity = 1;
                    name2 = "sin";
                    break;
                case 18:
                    arity = 1;
                    name2 = "sqrt";
                    break;
                case 19:
                    arity = 1;
                    name2 = "tan";
                    break;
                case 20:
                    arity = 1;
                    name2 = "cbrt";
                    break;
                case 21:
                    arity = 1;
                    name2 = "cosh";
                    break;
                case 22:
                    arity = 1;
                    name2 = "expm1";
                    break;
                case 23:
                    arity = 2;
                    name2 = "hypot";
                    break;
                case 24:
                    arity = 1;
                    name2 = "log1p";
                    break;
                case 25:
                    arity = 1;
                    name2 = "log10";
                    break;
                case 26:
                    arity = 1;
                    name2 = "sinh";
                    break;
                case 27:
                    arity = 1;
                    name2 = "tanh";
                    break;
                case 28:
                    arity = 2;
                    name2 = "imul";
                    break;
                case 29:
                    arity = 1;
                    name2 = "trunc";
                    break;
                case 30:
                    arity = 1;
                    name2 = "acosh";
                    break;
                case 31:
                    arity = 1;
                    name2 = "asinh";
                    break;
                case 32:
                    arity = 1;
                    name2 = "atanh";
                    break;
                case 33:
                    arity = 1;
                    name2 = CommandLineParameters.MASSIVE_OP_SIGN;
                    break;
                case 34:
                    arity = 1;
                    name2 = "log2";
                    break;
                case 35:
                    arity = 1;
                    name2 = "fround";
                    break;
                case 36:
                    arity = 1;
                    name2 = "clz32";
                    break;
                default:
                    throw new IllegalStateException(String.valueOf(id));
            }
            initPrototypeMethod(MATH_TAG, id, name2, arity);
            return;
        }
        switch (id) {
            case 37:
                x = 2.718281828459045d;
                name = "E";
                break;
            case 38:
                x = 3.141592653589793d;
                name = "PI";
                break;
            case 39:
                x = 2.302585092994046d;
                name = "LN10";
                break;
            case 40:
                x = 0.6931471805599453d;
                name = "LN2";
                break;
            case 41:
                x = 1.4426950408889634d;
                name = "LOG2E";
                break;
            case 42:
                x = 0.4342944819032518d;
                name = "LOG10E";
                break;
            case 43:
                x = 0.7071067811865476d;
                name = "SQRT1_2";
                break;
            case 44:
                x = 1.4142135623730951d;
                name = "SQRT2";
                break;
            default:
                throw new IllegalStateException(String.valueOf(id));
        }
        initPrototypeValue(id, name, ScriptRuntime.wrapNumber(x), 7);
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        double x;
        double dMin;
        double dExp;
        if (!f.hasTag(MATH_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int methodId = f.methodId();
        switch (methodId) {
            case 1:
                return "Math";
            case 2:
                double x2 = ScriptRuntime.toNumber(args, 0);
                x = x2 == 0.0d ? 0.0d : x2 < 0.0d ? -x2 : x2;
                break;
            case 3:
            case 4:
                double x3 = ScriptRuntime.toNumber(args, 0);
                x = (!Double.isNaN(x3) && -1.0d <= x3 && x3 <= 1.0d) ? methodId == 3 ? Math.acos(x3) : Math.asin(x3) : Double.NaN;
                break;
            case 5:
                double x4 = ScriptRuntime.toNumber(args, 0);
                x = Math.atan(x4);
                break;
            case 6:
                double x5 = ScriptRuntime.toNumber(args, 0);
                x = Math.atan2(x5, ScriptRuntime.toNumber(args, 1));
                break;
            case 7:
                double x6 = ScriptRuntime.toNumber(args, 0);
                x = Math.ceil(x6);
                break;
            case 8:
                double x7 = ScriptRuntime.toNumber(args, 0);
                x = Double.isInfinite(x7) ? Double.NaN : Math.cos(x7);
                break;
            case 9:
                double x8 = ScriptRuntime.toNumber(args, 0);
                if (x8 == Double.POSITIVE_INFINITY) {
                    dExp = x8;
                } else {
                    dExp = x8 == Double.NEGATIVE_INFINITY ? 0.0d : Math.exp(x8);
                }
                x = dExp;
                break;
            case 10:
                double x9 = ScriptRuntime.toNumber(args, 0);
                x = Math.floor(x9);
                break;
            case 11:
                double x10 = ScriptRuntime.toNumber(args, 0);
                x = x10 < 0.0d ? Double.NaN : Math.log(x10);
                break;
            case 12:
            case 13:
                x = methodId == 12 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
                for (int i = 0; i != args.length; i++) {
                    double d = ScriptRuntime.toNumber(args[i]);
                    if (Double.isNaN(d)) {
                        x = d;
                    } else {
                        if (methodId == 12) {
                            dMin = Math.max(x, d);
                        } else {
                            dMin = Math.min(x, d);
                        }
                        x = dMin;
                    }
                    break;
                }
                break;
            case 14:
                double x11 = ScriptRuntime.toNumber(args, 0);
                x = js_pow(x11, ScriptRuntime.toNumber(args, 1));
                break;
            case 15:
                x = Math.random();
                break;
            case 16:
                x = ScriptRuntime.toNumber(args, 0);
                if (!Double.isNaN(x) && !Double.isInfinite(x)) {
                    long l = Math.round(x);
                    if (l != 0) {
                        x = l;
                    } else if (x < 0.0d) {
                        x = ScriptRuntime.negativeZero;
                    } else if (x != 0.0d) {
                        x = 0.0d;
                    }
                }
                break;
            case 17:
                double x12 = ScriptRuntime.toNumber(args, 0);
                x = Double.isInfinite(x12) ? Double.NaN : Math.sin(x12);
                break;
            case 18:
                double x13 = ScriptRuntime.toNumber(args, 0);
                x = Math.sqrt(x13);
                break;
            case 19:
                double x14 = ScriptRuntime.toNumber(args, 0);
                x = Math.tan(x14);
                break;
            case 20:
                double x15 = ScriptRuntime.toNumber(args, 0);
                x = Math.cbrt(x15);
                break;
            case 21:
                double x16 = ScriptRuntime.toNumber(args, 0);
                x = Math.cosh(x16);
                break;
            case 22:
                double x17 = ScriptRuntime.toNumber(args, 0);
                x = Math.expm1(x17);
                break;
            case 23:
                x = js_hypot(args);
                break;
            case 24:
                double x18 = ScriptRuntime.toNumber(args, 0);
                x = Math.log1p(x18);
                break;
            case 25:
                double x19 = ScriptRuntime.toNumber(args, 0);
                x = Math.log10(x19);
                break;
            case 26:
                double x20 = ScriptRuntime.toNumber(args, 0);
                x = Math.sinh(x20);
                break;
            case 27:
                double x21 = ScriptRuntime.toNumber(args, 0);
                x = Math.tanh(x21);
                break;
            case 28:
                x = js_imul(args);
                break;
            case 29:
                double x22 = ScriptRuntime.toNumber(args, 0);
                x = js_trunc(x22);
                break;
            case 30:
                double x23 = ScriptRuntime.toNumber(args, 0);
                if (!Double.isNaN(x23)) {
                    return Double.valueOf(Math.log(x23 + Math.sqrt((x23 * x23) - 1.0d)));
                }
                return ScriptRuntime.NaNobj;
            case 31:
                double x24 = ScriptRuntime.toNumber(args, 0);
                if (Double.isInfinite(x24)) {
                    return Double.valueOf(x24);
                }
                if (Double.isNaN(x24)) {
                    return ScriptRuntime.NaNobj;
                }
                if (x24 != 0.0d) {
                    return Double.valueOf(Math.log(x24 + Math.sqrt((x24 * x24) + 1.0d)));
                }
                if (1.0d / x24 > 0.0d) {
                    return ScriptRuntime.zeroObj;
                }
                return ScriptRuntime.negativeZeroObj;
            case 32:
                double x25 = ScriptRuntime.toNumber(args, 0);
                if (Double.isNaN(x25) || -1.0d > x25 || x25 > 1.0d) {
                    return ScriptRuntime.NaNobj;
                }
                if (x25 != 0.0d) {
                    return Double.valueOf(0.5d * Math.log((x25 + 1.0d) / (x25 - 1.0d)));
                }
                if (1.0d / x25 > 0.0d) {
                    return ScriptRuntime.zeroObj;
                }
                return ScriptRuntime.negativeZeroObj;
            case 33:
                double x26 = ScriptRuntime.toNumber(args, 0);
                if (Double.isNaN(x26)) {
                    return ScriptRuntime.NaNobj;
                }
                if (x26 != 0.0d) {
                    return Double.valueOf(Math.signum(x26));
                }
                if (1.0d / x26 > 0.0d) {
                    return ScriptRuntime.zeroObj;
                }
                return ScriptRuntime.negativeZeroObj;
            case 34:
                double x27 = ScriptRuntime.toNumber(args, 0);
                x = x27 < 0.0d ? Double.NaN : Math.log(x27) * LOG2E;
                break;
            case 35:
                double x28 = ScriptRuntime.toNumber(args, 0);
                x = (float) x28;
                break;
            case 36:
                double x29 = ScriptRuntime.toNumber(args, 0);
                if (x29 == 0.0d || Double.isNaN(x29) || Double.isInfinite(x29)) {
                    return Double32;
                }
                long n = ScriptRuntime.toUint32(x29);
                if (n == 0) {
                    return Double32;
                }
                return Double.valueOf(31.0d - Math.floor(Math.log(n >>> 0) * LOG2E));
            default:
                throw new IllegalStateException(String.valueOf(methodId));
        }
        return ScriptRuntime.wrapNumber(x);
    }

    private static double js_pow(double x, double y) {
        double result;
        if (Double.isNaN(y)) {
            result = y;
        } else if (y == 0.0d) {
            result = 1.0d;
        } else if (x != 0.0d) {
            result = Math.pow(x, y);
            if (Double.isNaN(result)) {
                if (y == Double.POSITIVE_INFINITY) {
                    if (x < -1.0d || 1.0d < x) {
                        result = Double.POSITIVE_INFINITY;
                    } else if (-1.0d < x && x < 1.0d) {
                        result = 0.0d;
                    }
                } else if (y == Double.NEGATIVE_INFINITY) {
                    if (x < -1.0d || 1.0d < x) {
                        result = 0.0d;
                    } else if (-1.0d < x && x < 1.0d) {
                        result = Double.POSITIVE_INFINITY;
                    }
                } else if (x == Double.POSITIVE_INFINITY) {
                    result = y > 0.0d ? Double.POSITIVE_INFINITY : 0.0d;
                } else if (x == Double.NEGATIVE_INFINITY) {
                    long y_long = (long) y;
                    if (y_long != y || (y_long & 1) == 0) {
                        result = y > 0.0d ? Double.POSITIVE_INFINITY : 0.0d;
                    } else {
                        result = y > 0.0d ? Double.NEGATIVE_INFINITY : -0.0d;
                    }
                }
            }
        } else if (1.0d / x > 0.0d) {
            result = y > 0.0d ? 0.0d : Double.POSITIVE_INFINITY;
        } else {
            long y_long2 = (long) y;
            if (y_long2 != y || (y_long2 & 1) == 0) {
                result = y > 0.0d ? 0.0d : Double.POSITIVE_INFINITY;
            } else {
                result = y > 0.0d ? -0.0d : Double.NEGATIVE_INFINITY;
            }
        }
        return result;
    }

    private static double js_hypot(Object[] args) {
        if (args == null) {
            return 0.0d;
        }
        double y = 0.0d;
        boolean hasNaN = false;
        boolean hasInfinity = false;
        for (Object o : args) {
            double d = ScriptRuntime.toNumber(o);
            if (Double.isNaN(d)) {
                hasNaN = true;
            } else if (Double.isInfinite(d)) {
                hasInfinity = true;
            } else {
                y += d * d;
            }
        }
        if (hasInfinity) {
            return Double.POSITIVE_INFINITY;
        }
        if (hasNaN) {
            return Double.NaN;
        }
        return Math.sqrt(y);
    }

    private static double js_trunc(double d) {
        return d < 0.0d ? Math.ceil(d) : Math.floor(d);
    }

    private static int js_imul(Object[] args) {
        if (args == null) {
            return 0;
        }
        int x = ScriptRuntime.toInt32(args, 0);
        int y = ScriptRuntime.toInt32(args, 1);
        return x * y;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:155:0x04e5 A[PHI: r5 r6
      0x04e5: PHI (r5v1 'id' int) = 
      (r5v0 'id' int)
      (r5v3 'id' int)
      (r5v4 'id' int)
      (r5v0 'id' int)
      (r5v5 'id' int)
      (r5v6 'id' int)
      (r5v7 'id' int)
      (r5v0 'id' int)
      (r5v8 'id' int)
      (r5v9 'id' int)
      (r5v0 'id' int)
      (r5v10 'id' int)
      (r5v11 'id' int)
      (r5v12 'id' int)
      (r5v13 'id' int)
      (r5v14 'id' int)
      (r5v15 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v18 'id' int)
      (r5v19 'id' int)
      (r5v20 'id' int)
      (r5v21 'id' int)
      (r5v0 'id' int)
      (r5v22 'id' int)
      (r5v23 'id' int)
      (r5v24 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v27 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v30 'id' int)
      (r5v31 'id' int)
      (r5v32 'id' int)
      (r5v33 'id' int)
      (r5v34 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
      (r5v0 'id' int)
     binds: [B:3:0x0008, B:154:0x04dd, B:153:0x04d4, B:151:0x04c8, B:152:0x04cb, B:149:0x04bb, B:146:0x04ab, B:106:0x0358, B:142:0x0491, B:141:0x0488, B:139:0x047c, B:140:0x047f, B:137:0x046f, B:134:0x0458, B:133:0x044f, B:132:0x0446, B:131:0x043d, B:116:0x03f1, B:125:0x0420, B:127:0x042a, B:129:0x0434, B:120:0x0409, B:122:0x0413, B:114:0x03e4, B:111:0x03d4, B:108:0x03bd, B:107:0x03b4, B:66:0x01c4, B:103:0x0348, B:102:0x0340, B:101:0x0337, B:95:0x031a, B:97:0x0324, B:99:0x032e, B:90:0x0303, B:92:0x030d, B:86:0x02e5, B:80:0x02c8, B:82:0x02d2, B:84:0x02dc, B:75:0x02b1, B:77:0x02bb, B:71:0x0293, B:70:0x028b, B:69:0x0282, B:68:0x0279, B:67:0x0270, B:13:0x0067, B:60:0x01a9, B:62:0x01b3, B:55:0x018f, B:57:0x0199, B:50:0x0175, B:52:0x017f, B:45:0x015b, B:47:0x0165, B:42:0x014e, B:35:0x0126, B:37:0x0130, B:30:0x010c, B:32:0x0116, B:25:0x00f2, B:27:0x00fc, B:20:0x00d9, B:22:0x00e3, B:15:0x00bf, B:17:0x00c9, B:8:0x004f, B:10:0x0059, B:5:0x003f] A[DONT_GENERATE, DONT_INLINE]
      0x04e5: PHI (r6v1 'X' java.lang.String) = 
      (r6v0 'X' java.lang.String)
      (r6v2 'X' java.lang.String)
      (r6v3 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v4 'X' java.lang.String)
      (r6v5 'X' java.lang.String)
      (r6v6 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v7 'X' java.lang.String)
      (r6v8 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v9 'X' java.lang.String)
      (r6v10 'X' java.lang.String)
      (r6v11 'X' java.lang.String)
      (r6v12 'X' java.lang.String)
      (r6v13 'X' java.lang.String)
      (r6v14 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v15 'X' java.lang.String)
      (r6v16 'X' java.lang.String)
      (r6v17 'X' java.lang.String)
      (r6v18 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v19 'X' java.lang.String)
      (r6v20 'X' java.lang.String)
      (r6v21 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v22 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v23 'X' java.lang.String)
      (r6v24 'X' java.lang.String)
      (r6v25 'X' java.lang.String)
      (r6v26 'X' java.lang.String)
      (r6v27 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
      (r6v0 'X' java.lang.String)
     binds: [B:3:0x0008, B:154:0x04dd, B:153:0x04d4, B:151:0x04c8, B:152:0x04cb, B:149:0x04bb, B:146:0x04ab, B:106:0x0358, B:142:0x0491, B:141:0x0488, B:139:0x047c, B:140:0x047f, B:137:0x046f, B:134:0x0458, B:133:0x044f, B:132:0x0446, B:131:0x043d, B:116:0x03f1, B:125:0x0420, B:127:0x042a, B:129:0x0434, B:120:0x0409, B:122:0x0413, B:114:0x03e4, B:111:0x03d4, B:108:0x03bd, B:107:0x03b4, B:66:0x01c4, B:103:0x0348, B:102:0x0340, B:101:0x0337, B:95:0x031a, B:97:0x0324, B:99:0x032e, B:90:0x0303, B:92:0x030d, B:86:0x02e5, B:80:0x02c8, B:82:0x02d2, B:84:0x02dc, B:75:0x02b1, B:77:0x02bb, B:71:0x0293, B:70:0x028b, B:69:0x0282, B:68:0x0279, B:67:0x0270, B:13:0x0067, B:60:0x01a9, B:62:0x01b3, B:55:0x018f, B:57:0x0199, B:50:0x0175, B:52:0x017f, B:45:0x015b, B:47:0x0165, B:42:0x014e, B:35:0x0126, B:37:0x0130, B:30:0x010c, B:32:0x0116, B:25:0x00f2, B:27:0x00fc, B:20:0x00d9, B:22:0x00e3, B:15:0x00bf, B:17:0x00c9, B:8:0x004f, B:10:0x0059, B:5:0x003f] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:157:0x04e9  */
    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        int id = 0;
        String X = null;
        switch (s.length()) {
            case 1:
                if (s.charAt(0) == 'E') {
                    id = 37;
                } else if (X != null && X != s && !X.equals(s)) {
                    id = 0;
                }
                break;
            case 2:
                if (s.charAt(0) == 'P' && s.charAt(1) == 'I') {
                    id = 38;
                } else if (X != null) {
                    id = 0;
                }
                break;
            case 3:
                switch (s.charAt(0)) {
                    case 'L':
                        if (s.charAt(2) == '2' && s.charAt(1) == 'N') {
                            id = 40;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'a':
                        if (s.charAt(2) == 's' && s.charAt(1) == 'b') {
                            id = 2;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'c':
                        if (s.charAt(2) == 's' && s.charAt(1) == 'o') {
                            id = 8;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'e':
                        if (s.charAt(2) == 'p' && s.charAt(1) == 'x') {
                            id = 9;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'l':
                        if (s.charAt(2) == 'g' && s.charAt(1) == 'o') {
                            id = 11;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'm':
                        int c = s.charAt(2);
                        if (c != 110) {
                            if (c == 120 && s.charAt(1) == 'a') {
                                id = 12;
                            } else if (X != null) {
                                id = 0;
                            }
                        } else if (s.charAt(1) == 'i') {
                            id = 13;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'p':
                        if (s.charAt(2) == 'w' && s.charAt(1) == 'o') {
                            id = 14;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 's':
                        if (s.charAt(2) == 'n' && s.charAt(1) == 'i') {
                            id = 17;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 't':
                        if (s.charAt(2) == 'n' && s.charAt(1) == 'a') {
                            id = 19;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    default:
                        if (X != null) {
                            id = 0;
                        }
                        break;
                }
                break;
            case 4:
                switch (s.charAt(1)) {
                    case 'N':
                        X = "LN10";
                        id = 39;
                        if (X != null) {
                            id = 0;
                        }
                        break;
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
                    case '_':
                    case '`':
                    case 'd':
                    case 'f':
                    case 'g':
                    case 'h':
                    case 'j':
                    case 'k':
                    case 'l':
                    case 'n':
                    case 'p':
                    case 'r':
                    default:
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'a':
                        X = "tanh";
                        id = 27;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'b':
                        X = "cbrt";
                        id = 20;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'c':
                        X = "acos";
                        id = 3;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'e':
                        X = "ceil";
                        id = 7;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'i':
                        int c2 = s.charAt(3);
                        if (c2 != 104) {
                            if (c2 == 110 && s.charAt(0) == 's' && s.charAt(2) == 'g') {
                                id = 33;
                            } else if (X != null) {
                                id = 0;
                            }
                        } else if (s.charAt(0) == 's' && s.charAt(2) == 'n') {
                            id = 26;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'm':
                        X = "imul";
                        id = 28;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'o':
                        int c3 = s.charAt(0);
                        if (c3 != 99) {
                            if (c3 == 108 && s.charAt(2) == 'g' && s.charAt(3) == '2') {
                                id = 34;
                            } else if (X != null) {
                                id = 0;
                            }
                        } else if (s.charAt(2) == 's' && s.charAt(3) == 'h') {
                            id = 21;
                        } else if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'q':
                        X = "sqrt";
                        id = 18;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 's':
                        X = "asin";
                        id = 4;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 't':
                        X = "atan";
                        id = 5;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                }
                break;
            case 5:
                switch (s.charAt(0)) {
                    case 'L':
                        X = "LOG2E";
                        id = 41;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'S':
                        X = "SQRT2";
                        id = 44;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'a':
                        int c4 = s.charAt(1);
                        if (c4 == 99) {
                            X = "acosh";
                            id = 30;
                        } else if (c4 == 115) {
                            X = "asinh";
                            id = 31;
                        } else if (c4 == 116) {
                            int c5 = s.charAt(4);
                            if (c5 != 50) {
                                if (c5 == 104 && s.charAt(2) == 'a' && s.charAt(3) == 'n') {
                                    id = 32;
                                    break;
                                }
                            } else if (s.charAt(2) == 'a' && s.charAt(3) == 'n') {
                                id = 6;
                                break;
                            }
                        }
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'c':
                        X = "clz32";
                        id = 36;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'e':
                        X = "expm1";
                        id = 22;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'f':
                        X = "floor";
                        id = 10;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'h':
                        X = "hypot";
                        id = 23;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'l':
                        int c6 = s.charAt(4);
                        if (c6 == 48) {
                            X = "log10";
                            id = 25;
                        } else if (c6 == 112) {
                            X = "log1p";
                            id = 24;
                        }
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 'r':
                        X = "round";
                        id = 16;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    case 't':
                        X = "trunc";
                        id = 29;
                        if (X != null) {
                            id = 0;
                        }
                        break;
                    default:
                        if (X != null) {
                            id = 0;
                        }
                        break;
                }
                break;
            case 6:
                int c7 = s.charAt(0);
                if (c7 == 76) {
                    X = "LOG10E";
                    id = 42;
                } else if (c7 == 102) {
                    X = "fround";
                    id = 35;
                } else if (c7 == 114) {
                    X = "random";
                    id = 15;
                }
                if (X != null) {
                    id = 0;
                }
                break;
            case 7:
                X = "SQRT1_2";
                id = 43;
                if (X != null) {
                    id = 0;
                }
                break;
            case 8:
                X = "toSource";
                id = 1;
                if (X != null) {
                    id = 0;
                }
                break;
            default:
                if (X != null) {
                    id = 0;
                }
                break;
        }
        return id;
    }
}
