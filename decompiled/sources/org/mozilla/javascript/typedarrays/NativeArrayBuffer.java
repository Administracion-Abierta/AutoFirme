package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/typedarrays/NativeArrayBuffer.class */
public class NativeArrayBuffer extends IdScriptableObject {
    private static final long serialVersionUID = 3110411773054879549L;
    public static final String CLASS_NAME = "ArrayBuffer";
    private static final byte[] EMPTY_BUF = new byte[0];
    final byte[] buffer;
    private static final int Id_constructor = 1;
    private static final int Id_slice = 2;
    private static final int MAX_PROTOTYPE_ID = 2;
    private static final int ConstructorId_isView = -1;
    private static final int Id_byteLength = 1;
    private static final int MAX_INSTANCE_ID = 1;

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return CLASS_NAME;
    }

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeArrayBuffer na = new NativeArrayBuffer();
        na.exportAsJSClass(2, scope, sealed);
    }

    public NativeArrayBuffer() {
        this.buffer = EMPTY_BUF;
    }

    public NativeArrayBuffer(double len) {
        if (len >= 2.147483647E9d) {
            throw ScriptRuntime.rangeError("length parameter (" + len + ") is too large ");
        }
        if (len == Double.NEGATIVE_INFINITY) {
            throw ScriptRuntime.rangeError("Negative array length " + len);
        }
        if (len <= -1.0d) {
            throw ScriptRuntime.rangeError("Negative array length " + len);
        }
        int intLen = ScriptRuntime.toInt32(len);
        if (intLen < 0) {
            throw ScriptRuntime.rangeError("Negative array length " + len);
        }
        if (intLen == 0) {
            this.buffer = EMPTY_BUF;
        } else {
            this.buffer = new byte[intLen];
        }
    }

    public int getLength() {
        return this.buffer.length;
    }

    public byte[] getBuffer() {
        return this.buffer;
    }

    public NativeArrayBuffer slice(double s, double e) {
        int end = ScriptRuntime.toInt32(Math.max(0.0d, Math.min(this.buffer.length, e < 0.0d ? ((double) this.buffer.length) + e : e)));
        int start = ScriptRuntime.toInt32(Math.min(end, Math.max(0.0d, s < 0.0d ? ((double) this.buffer.length) + s : s)));
        int len = end - start;
        NativeArrayBuffer newBuf = new NativeArrayBuffer(len);
        System.arraycopy(this.buffer, start, newBuf.buffer, 0, len);
        return newBuf;
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(CLASS_NAME)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case -1:
                return Boolean.valueOf(isArg(args, 0) && (args[0] instanceof NativeArrayBufferView));
            case 0:
            default:
                throw new IllegalArgumentException(String.valueOf(id));
            case 1:
                double length = isArg(args, 0) ? ScriptRuntime.toNumber(args[0]) : 0.0d;
                return new NativeArrayBuffer(length);
            case 2:
                NativeArrayBuffer self = realThis(thisObj, f);
                double start = isArg(args, 0) ? ScriptRuntime.toNumber(args[0]) : 0.0d;
                double end = isArg(args, 1) ? ScriptRuntime.toNumber(args[1]) : self.buffer.length;
                return self.slice(start, end);
        }
    }

    private static NativeArrayBuffer realThis(Scriptable thisObj, IdFunctionObject f) {
        if (!(thisObj instanceof NativeArrayBuffer)) {
            throw incompatibleCallError(f);
        }
        return (NativeArrayBuffer) thisObj;
    }

    private static boolean isArg(Object[] args, int i) {
        return args.length > i && !Undefined.instance.equals(args[i]);
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
                arity = 2;
                s = "slice";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(CLASS_NAME, id, s, arity);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        int id = 0;
        String X = null;
        int s_length = s.length();
        if (s_length == 5) {
            X = "slice";
            id = 2;
        } else if (s_length == 11) {
            X = "constructor";
            id = 1;
        }
        if (X != null && X != s && !X.equals(s)) {
            id = 0;
        }
        return id;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        addIdFunctionProperty(ctor, CLASS_NAME, -1, "isView", 1);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int getMaxInstanceId() {
        return 1;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected String getInstanceIdName(int id) {
        return id == 1 ? "byteLength" : super.getInstanceIdName(id);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected Object getInstanceIdValue(int id) {
        if (id == 1) {
            return ScriptRuntime.wrapInt(this.buffer.length);
        }
        return super.getInstanceIdValue(id);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findInstanceIdInfo(String s) {
        if ("byteLength".equals(s)) {
            return instanceIdInfo(5, 1);
        }
        return super.findInstanceIdInfo(s);
    }
}
