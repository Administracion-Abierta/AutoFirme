package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/typedarrays/NativeDataView.class */
public class NativeDataView extends NativeArrayBufferView {
    private static final long serialVersionUID = 1427967607557438968L;
    public static final String CLASS_NAME = "DataView";
    private static final int Id_constructor = 1;
    private static final int Id_getInt8 = 2;
    private static final int Id_getUint8 = 3;
    private static final int Id_getInt16 = 4;
    private static final int Id_getUint16 = 5;
    private static final int Id_getInt32 = 6;
    private static final int Id_getUint32 = 7;
    private static final int Id_getFloat32 = 8;
    private static final int Id_getFloat64 = 9;
    private static final int Id_setInt8 = 10;
    private static final int Id_setUint8 = 11;
    private static final int Id_setInt16 = 12;
    private static final int Id_setUint16 = 13;
    private static final int Id_setInt32 = 14;
    private static final int Id_setUint32 = 15;
    private static final int Id_setFloat32 = 16;
    private static final int Id_setFloat64 = 17;
    private static final int MAX_PROTOTYPE_ID = 17;

    public NativeDataView() {
    }

    public NativeDataView(NativeArrayBuffer ab, int offset, int length) {
        super(ab, offset, length);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return CLASS_NAME;
    }

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeDataView dv = new NativeDataView();
        dv.exportAsJSClass(17, scope, sealed);
    }

    private static int determinePos(Object[] args) {
        if (isArg(args, 0)) {
            double doublePos = ScriptRuntime.toNumber(args[0]);
            if (Double.isInfinite(doublePos)) {
                throw ScriptRuntime.rangeError("offset out of range");
            }
            return ScriptRuntime.toInt32(doublePos);
        }
        return 0;
    }

    private void rangeCheck(int pos, int len) {
        if (pos < 0 || pos + len > this.byteLength) {
            throw ScriptRuntime.rangeError("offset out of range");
        }
    }

    private static NativeDataView realThis(Scriptable thisObj, IdFunctionObject f) {
        if (!(thisObj instanceof NativeDataView)) {
            throw incompatibleCallError(f);
        }
        return (NativeDataView) thisObj;
    }

    private static NativeDataView js_constructor(Object[] args) {
        int pos;
        int len;
        if (!isArg(args, 0) || !(args[0] instanceof NativeArrayBuffer)) {
            throw ScriptRuntime.constructError("TypeError", "Missing parameters");
        }
        NativeArrayBuffer ab = (NativeArrayBuffer) args[0];
        if (isArg(args, 1)) {
            double doublePos = ScriptRuntime.toNumber(args[1]);
            if (Double.isInfinite(doublePos)) {
                throw ScriptRuntime.rangeError("offset out of range");
            }
            pos = ScriptRuntime.toInt32(doublePos);
        } else {
            pos = 0;
        }
        if (isArg(args, 2)) {
            double doublePos2 = ScriptRuntime.toNumber(args[2]);
            if (Double.isInfinite(doublePos2)) {
                throw ScriptRuntime.rangeError("offset out of range");
            }
            len = ScriptRuntime.toInt32(doublePos2);
        } else {
            len = ab.getLength() - pos;
        }
        if (len < 0) {
            throw ScriptRuntime.rangeError("length out of range");
        }
        if (pos < 0 || pos + len > ab.getLength()) {
            throw ScriptRuntime.rangeError("offset out of range");
        }
        return new NativeDataView(ab, pos, len);
    }

    private Object js_getInt(int bytes, boolean signed, Object[] args) {
        int pos = determinePos(args);
        rangeCheck(pos, bytes);
        boolean littleEndian = isArg(args, 1) && bytes > 1 && ScriptRuntime.toBoolean(args[1]);
        switch (bytes) {
            case 1:
                if (signed) {
                    return ByteIo.readInt8(this.arrayBuffer.buffer, this.offset + pos);
                }
                return ByteIo.readUint8(this.arrayBuffer.buffer, this.offset + pos);
            case 2:
                if (signed) {
                    return ByteIo.readInt16(this.arrayBuffer.buffer, this.offset + pos, littleEndian);
                }
                return ByteIo.readUint16(this.arrayBuffer.buffer, this.offset + pos, littleEndian);
            case 3:
            default:
                throw new AssertionError();
            case 4:
                return signed ? ByteIo.readInt32(this.arrayBuffer.buffer, this.offset + pos, littleEndian) : ByteIo.readUint32(this.arrayBuffer.buffer, this.offset + pos, littleEndian);
        }
    }

    private Object js_getFloat(int bytes, Object[] args) {
        int pos = determinePos(args);
        rangeCheck(pos, bytes);
        boolean littleEndian = isArg(args, 1) && bytes > 1 && ScriptRuntime.toBoolean(args[1]);
        switch (bytes) {
            case 4:
                return ByteIo.readFloat32(this.arrayBuffer.buffer, this.offset + pos, littleEndian);
            case 8:
                return ByteIo.readFloat64(this.arrayBuffer.buffer, this.offset + pos, littleEndian);
            default:
                throw new AssertionError();
        }
    }

    private void js_setInt(int bytes, boolean signed, Object[] args) {
        int pos = determinePos(args);
        if (pos < 0) {
            throw ScriptRuntime.rangeError("offset out of range");
        }
        boolean littleEndian = isArg(args, 2) && bytes > 1 && ScriptRuntime.toBoolean(args[2]);
        Object val = ScriptRuntime.zeroObj;
        if (args.length > 1) {
            val = args[1];
        }
        switch (bytes) {
            case 1:
                if (signed) {
                    int value = Conversions.toInt8(val);
                    if (pos + bytes > this.byteLength) {
                        throw ScriptRuntime.rangeError("offset out of range");
                    }
                    ByteIo.writeInt8(this.arrayBuffer.buffer, this.offset + pos, value);
                    return;
                }
                int value2 = Conversions.toUint8(val);
                if (pos + bytes > this.byteLength) {
                    throw ScriptRuntime.rangeError("offset out of range");
                }
                ByteIo.writeUint8(this.arrayBuffer.buffer, this.offset + pos, value2);
                return;
            case 2:
                if (signed) {
                    int value3 = Conversions.toInt16(val);
                    if (pos + bytes > this.byteLength) {
                        throw ScriptRuntime.rangeError("offset out of range");
                    }
                    ByteIo.writeInt16(this.arrayBuffer.buffer, this.offset + pos, value3, littleEndian);
                    return;
                }
                int value4 = Conversions.toUint16(val);
                if (pos + bytes > this.byteLength) {
                    throw ScriptRuntime.rangeError("offset out of range");
                }
                ByteIo.writeUint16(this.arrayBuffer.buffer, this.offset + pos, value4, littleEndian);
                return;
            case 3:
            default:
                throw new AssertionError();
            case 4:
                if (signed) {
                    int value5 = Conversions.toInt32(val);
                    if (pos + bytes > this.byteLength) {
                        throw ScriptRuntime.rangeError("offset out of range");
                    }
                    ByteIo.writeInt32(this.arrayBuffer.buffer, this.offset + pos, value5, littleEndian);
                    return;
                }
                long value6 = Conversions.toUint32(val);
                if (pos + bytes > this.byteLength) {
                    throw ScriptRuntime.rangeError("offset out of range");
                }
                ByteIo.writeUint32(this.arrayBuffer.buffer, this.offset + pos, value6, littleEndian);
                return;
        }
    }

    private void js_setFloat(int bytes, Object[] args) {
        int pos = determinePos(args);
        if (pos < 0) {
            throw ScriptRuntime.rangeError("offset out of range");
        }
        boolean littleEndian = isArg(args, 2) && bytes > 1 && ScriptRuntime.toBoolean(args[2]);
        double val = Double.NaN;
        if (args.length > 1) {
            val = ScriptRuntime.toNumber(args[1]);
        }
        if (pos + bytes > this.byteLength) {
            throw ScriptRuntime.rangeError("offset out of range");
        }
        switch (bytes) {
            case 4:
                ByteIo.writeFloat32(this.arrayBuffer.buffer, this.offset + pos, val, littleEndian);
                return;
            case 8:
                ByteIo.writeFloat64(this.arrayBuffer.buffer, this.offset + pos, val, littleEndian);
                return;
            default:
                throw new AssertionError();
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(getClassName())) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case 1:
                return js_constructor(args);
            case 2:
                return realThis(thisObj, f).js_getInt(1, true, args);
            case 3:
                return realThis(thisObj, f).js_getInt(1, false, args);
            case 4:
                return realThis(thisObj, f).js_getInt(2, true, args);
            case 5:
                return realThis(thisObj, f).js_getInt(2, false, args);
            case 6:
                return realThis(thisObj, f).js_getInt(4, true, args);
            case 7:
                return realThis(thisObj, f).js_getInt(4, false, args);
            case 8:
                return realThis(thisObj, f).js_getFloat(4, args);
            case 9:
                return realThis(thisObj, f).js_getFloat(8, args);
            case 10:
                realThis(thisObj, f).js_setInt(1, true, args);
                return Undefined.instance;
            case 11:
                realThis(thisObj, f).js_setInt(1, false, args);
                return Undefined.instance;
            case 12:
                realThis(thisObj, f).js_setInt(2, true, args);
                return Undefined.instance;
            case 13:
                realThis(thisObj, f).js_setInt(2, false, args);
                return Undefined.instance;
            case 14:
                realThis(thisObj, f).js_setInt(4, true, args);
                return Undefined.instance;
            case 15:
                realThis(thisObj, f).js_setInt(4, false, args);
                return Undefined.instance;
            case 16:
                realThis(thisObj, f).js_setFloat(4, args);
                return Undefined.instance;
            case 17:
                realThis(thisObj, f).js_setFloat(8, args);
                return Undefined.instance;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        int arity;
        String s;
        switch (id) {
            case 1:
                arity = 3;
                s = "constructor";
                break;
            case 2:
                arity = 1;
                s = "getInt8";
                break;
            case 3:
                arity = 1;
                s = "getUint8";
                break;
            case 4:
                arity = 1;
                s = "getInt16";
                break;
            case 5:
                arity = 1;
                s = "getUint16";
                break;
            case 6:
                arity = 1;
                s = "getInt32";
                break;
            case 7:
                arity = 1;
                s = "getUint32";
                break;
            case 8:
                arity = 1;
                s = "getFloat32";
                break;
            case 9:
                arity = 1;
                s = "getFloat64";
                break;
            case 10:
                arity = 2;
                s = "setInt8";
                break;
            case 11:
                arity = 2;
                s = "setUint8";
                break;
            case 12:
                arity = 2;
                s = "setInt16";
                break;
            case 13:
                arity = 2;
                s = "setUint16";
                break;
            case 14:
                arity = 2;
                s = "setInt32";
                break;
            case 15:
                arity = 2;
                s = "setUint32";
                break;
            case 16:
                arity = 2;
                s = "setFloat32";
                break;
            case 17:
                arity = 2;
                s = "setFloat64";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(getClassName(), id, s, arity);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        int id = 0;
        String X = null;
        switch (s.length()) {
            case 7:
                int c = s.charAt(0);
                if (c == 103) {
                    X = "getInt8";
                    id = 2;
                } else if (c == 115) {
                    X = "setInt8";
                    id = 10;
                }
                break;
            case 8:
                int c2 = s.charAt(6);
                if (c2 == 49) {
                    int c3 = s.charAt(0);
                    if (c3 == 103) {
                        X = "getInt16";
                        id = 4;
                    } else if (c3 == 115) {
                        X = "setInt16";
                        id = 12;
                    }
                } else if (c2 == 51) {
                    int c4 = s.charAt(0);
                    if (c4 == 103) {
                        X = "getInt32";
                        id = 6;
                    } else if (c4 == 115) {
                        X = "setInt32";
                        id = 14;
                    }
                } else if (c2 == 116) {
                    int c5 = s.charAt(0);
                    if (c5 == 103) {
                        X = "getUint8";
                        id = 3;
                    } else if (c5 == 115) {
                        X = "setUint8";
                        id = 11;
                    }
                }
                break;
            case 9:
                int c6 = s.charAt(0);
                if (c6 == 103) {
                    int c7 = s.charAt(8);
                    if (c7 == 50) {
                        X = "getUint32";
                        id = 7;
                    } else if (c7 == 54) {
                        X = "getUint16";
                        id = 5;
                    }
                } else if (c6 == 115) {
                    int c8 = s.charAt(8);
                    if (c8 == 50) {
                        X = "setUint32";
                        id = 15;
                    } else if (c8 == 54) {
                        X = "setUint16";
                        id = 13;
                    }
                }
                break;
            case 10:
                int c9 = s.charAt(0);
                if (c9 == 103) {
                    int c10 = s.charAt(9);
                    if (c10 == 50) {
                        X = "getFloat32";
                        id = 8;
                    } else if (c10 == 52) {
                        X = "getFloat64";
                        id = 9;
                    }
                } else if (c9 == 115) {
                    int c11 = s.charAt(9);
                    if (c11 == 50) {
                        X = "setFloat32";
                        id = 16;
                    } else if (c11 == 52) {
                        X = "setFloat64";
                        id = 17;
                    }
                }
                break;
            case 11:
                X = "constructor";
                id = 1;
                break;
        }
        if (X != null && X != s && !X.equals(s)) {
            id = 0;
        }
        return id;
    }
}
