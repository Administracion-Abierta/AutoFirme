package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/typedarrays/NativeFloat64Array.class */
public class NativeFloat64Array extends NativeTypedArrayView<Double> {
    private static final long serialVersionUID = -1255405650050639335L;
    private static final String CLASS_NAME = "Float64Array";
    private static final int BYTES_PER_ELEMENT = 8;

    public NativeFloat64Array() {
    }

    public NativeFloat64Array(NativeArrayBuffer ab, int off, int len) {
        super(ab, off, len, len * 8);
    }

    public NativeFloat64Array(int len) {
        this(new NativeArrayBuffer(((double) len) * 8.0d), 0, len);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return CLASS_NAME;
    }

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeFloat64Array a = new NativeFloat64Array();
        a.exportAsJSClass(6, scope, sealed);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    /* JADX INFO: renamed from: construct */
    public NativeTypedArrayView<Double> construct2(NativeArrayBuffer ab, int off, int len) {
        return new NativeFloat64Array(ab, off, len);
    }

    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    public int getBytesPerElement() {
        return 8;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    /* JADX INFO: renamed from: realThis */
    public NativeTypedArrayView<Double> realThis2(Scriptable thisObj, IdFunctionObject f) {
        if (!(thisObj instanceof NativeFloat64Array)) {
            throw incompatibleCallError(f);
        }
        return (NativeFloat64Array) thisObj;
    }

    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    protected Object js_get(int index) {
        if (checkIndex(index)) {
            return Undefined.instance;
        }
        long base = ByteIo.readUint64Primitive(this.arrayBuffer.buffer, (index * 8) + this.offset, useLittleEndian());
        return Double.valueOf(Double.longBitsToDouble(base));
    }

    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    protected Object js_set(int index, Object c) {
        if (checkIndex(index)) {
            return Undefined.instance;
        }
        double val = ScriptRuntime.toNumber(c);
        long base = Double.doubleToLongBits(val);
        ByteIo.writeUint64(this.arrayBuffer.buffer, (index * 8) + this.offset, base, useLittleEndian());
        return null;
    }

    @Override // java.util.List
    public Double get(int i) {
        if (checkIndex(i)) {
            throw new IndexOutOfBoundsException();
        }
        return (Double) js_get(i);
    }

    @Override // java.util.List
    public Double set(int i, Double aByte) {
        if (checkIndex(i)) {
            throw new IndexOutOfBoundsException();
        }
        return (Double) js_set(i, aByte);
    }
}
