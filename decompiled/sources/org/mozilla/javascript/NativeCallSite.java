package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeCallSite.class */
public class NativeCallSite extends IdScriptableObject {
    private static final long serialVersionUID = 2688372752566593594L;
    private static final String CALLSITE_TAG = "CallSite";
    private ScriptStackElement element;
    private static final int Id_constructor = 1;
    private static final int Id_getThis = 2;
    private static final int Id_getTypeName = 3;
    private static final int Id_getFunction = 4;
    private static final int Id_getFunctionName = 5;
    private static final int Id_getMethodName = 6;
    private static final int Id_getFileName = 7;
    private static final int Id_getLineNumber = 8;
    private static final int Id_getColumnNumber = 9;
    private static final int Id_getEvalOrigin = 10;
    private static final int Id_isToplevel = 11;
    private static final int Id_isEval = 12;
    private static final int Id_isNative = 13;
    private static final int Id_isConstructor = 14;
    private static final int Id_toString = 15;
    private static final int MAX_PROTOTYPE_ID = 15;

    static void init(Scriptable scope, boolean sealed) {
        NativeCallSite cs = new NativeCallSite();
        cs.exportAsJSClass(15, scope, sealed);
    }

    static NativeCallSite make(Scriptable scope, Scriptable ctorObj) {
        NativeCallSite cs = new NativeCallSite();
        Scriptable proto = (Scriptable) ctorObj.get("prototype", ctorObj);
        cs.setParentScope(scope);
        cs.setPrototype(proto);
        return cs;
    }

    private NativeCallSite() {
    }

    void setElement(ScriptStackElement elt) {
        this.element = elt;
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return CALLSITE_TAG;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        int arity;
        String s;
        switch (id) {
            case 1:
                arity = 0;
                s = "constructor";
                break;
            case 2:
                arity = 0;
                s = "getThis";
                break;
            case 3:
                arity = 0;
                s = "getTypeName";
                break;
            case 4:
                arity = 0;
                s = "getFunction";
                break;
            case 5:
                arity = 0;
                s = "getFunctionName";
                break;
            case 6:
                arity = 0;
                s = "getMethodName";
                break;
            case 7:
                arity = 0;
                s = "getFileName";
                break;
            case 8:
                arity = 0;
                s = "getLineNumber";
                break;
            case 9:
                arity = 0;
                s = "getColumnNumber";
                break;
            case 10:
                arity = 0;
                s = "getEvalOrigin";
                break;
            case 11:
                arity = 0;
                s = "isToplevel";
                break;
            case 12:
                arity = 0;
                s = "isEval";
                break;
            case 13:
                arity = 0;
                s = "isNative";
                break;
            case 14:
                arity = 0;
                s = "isConstructor";
                break;
            case 15:
                arity = 0;
                s = "toString";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(CALLSITE_TAG, id, s, arity);
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(CALLSITE_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case 1:
                return make(scope, f);
            case 2:
            case 3:
            case 4:
            case 9:
                return Undefined.instance;
            case 5:
                return getFunctionName(thisObj);
            case 6:
                return null;
            case 7:
                return getFileName(thisObj);
            case 8:
                return getLineNumber(thisObj);
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                return Boolean.FALSE;
            case 15:
                return js_toString(thisObj);
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    public String toString() {
        if (this.element == null) {
            return "";
        }
        return this.element.toString();
    }

    private static Object js_toString(Scriptable obj) {
        while (obj != null && !(obj instanceof NativeCallSite)) {
            obj = obj.getPrototype();
        }
        if (obj == null) {
            return NOT_FOUND;
        }
        NativeCallSite cs = (NativeCallSite) obj;
        StringBuilder sb = new StringBuilder();
        cs.element.renderJavaStyle(sb);
        return sb.toString();
    }

    private static Object getFunctionName(Scriptable obj) {
        while (obj != null && !(obj instanceof NativeCallSite)) {
            obj = obj.getPrototype();
        }
        if (obj == null) {
            return NOT_FOUND;
        }
        NativeCallSite cs = (NativeCallSite) obj;
        if (cs.element == null) {
            return null;
        }
        return cs.element.functionName;
    }

    private static Object getFileName(Scriptable obj) {
        while (obj != null && !(obj instanceof NativeCallSite)) {
            obj = obj.getPrototype();
        }
        if (obj == null) {
            return NOT_FOUND;
        }
        NativeCallSite cs = (NativeCallSite) obj;
        if (cs.element == null) {
            return null;
        }
        return cs.element.fileName;
    }

    private static Object getLineNumber(Scriptable obj) {
        while (obj != null && !(obj instanceof NativeCallSite)) {
            obj = obj.getPrototype();
        }
        if (obj == null) {
            return NOT_FOUND;
        }
        NativeCallSite cs = (NativeCallSite) obj;
        if (cs.element == null || cs.element.lineNumber < 0) {
            return Undefined.instance;
        }
        return Integer.valueOf(cs.element.lineNumber);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        int id = 0;
        String X = null;
        switch (s.length()) {
            case 6:
                X = "isEval";
                id = 12;
                break;
            case 7:
                X = "getThis";
                id = 2;
                break;
            case 8:
                int c = s.charAt(0);
                if (c == 105) {
                    X = "isNative";
                    id = 13;
                } else if (c == 116) {
                    X = "toString";
                    id = 15;
                }
                break;
            case 10:
                X = "isToplevel";
                id = 11;
                break;
            case 11:
                switch (s.charAt(4)) {
                    case 'i':
                        X = "getFileName";
                        id = 7;
                        break;
                    case 't':
                        X = "constructor";
                        id = 1;
                        break;
                    case 'u':
                        X = "getFunction";
                        id = 4;
                        break;
                    case 'y':
                        X = "getTypeName";
                        id = 3;
                        break;
                }
                break;
            case 13:
                switch (s.charAt(3)) {
                    case 'E':
                        X = "getEvalOrigin";
                        id = 10;
                        break;
                    case 'L':
                        X = "getLineNumber";
                        id = 8;
                        break;
                    case 'M':
                        X = "getMethodName";
                        id = 6;
                        break;
                    case 'o':
                        X = "isConstructor";
                        id = 14;
                        break;
                }
                break;
            case 15:
                int c2 = s.charAt(3);
                if (c2 == 67) {
                    X = "getColumnNumber";
                    id = 9;
                } else if (c2 == 70) {
                    X = "getFunctionName";
                    id = 5;
                }
                break;
        }
        if (X != null && X != s && !X.equals(s)) {
            id = 0;
        }
        return id;
    }
}
