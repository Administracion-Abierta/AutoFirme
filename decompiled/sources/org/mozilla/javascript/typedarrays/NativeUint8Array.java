package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/typedarrays/NativeUint8Array.class */
public class NativeUint8Array extends NativeTypedArrayView<Integer> {
    private static final long serialVersionUID = -3349419704390398895L;
    private static final String CLASS_NAME = "Uint8Array";

    public NativeUint8Array() {
    }

    public NativeUint8Array(NativeArrayBuffer ab, int off, int len) {
        super(ab, off, len, len);
    }

    public NativeUint8Array(int len) {
        this(new NativeArrayBuffer(len), 0, len);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return CLASS_NAME;
    }

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeUint8Array a = new NativeUint8Array();
        a.exportAsJSClass(6, scope, sealed);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    /* JADX INFO: renamed from: construct */
    public NativeTypedArrayView<Integer> construct2(NativeArrayBuffer ab, int off, int len) {
        return new NativeUint8Array(ab, off, len);
    }

    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    public int getBytesPerElement() {
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    /* JADX INFO: renamed from: realThis */
    public NativeTypedArrayView<Integer> realThis2(Scriptable thisObj, IdFunctionObject f) {
        if (!(thisObj instanceof NativeUint8Array)) {
            throw incompatibleCallError(f);
        }
        return (NativeUint8Array) thisObj;
    }

    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    protected Object js_get(int index) {
        if (checkIndex(index)) {
            return Undefined.instance;
        }
        return ByteIo.readUint8(this.arrayBuffer.buffer, index + this.offset);
    }

    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    protected Object js_set(int index, Object c) {
        if (checkIndex(index)) {
            return Undefined.instance;
        }
        int val = Conversions.toUint8(c);
        ByteIo.writeUint8(this.arrayBuffer.buffer, index + this.offset, val);
        return null;
    }

    @Override // java.util.List
    public Integer get(int i) {
        if (checkIndex(i)) {
            throw new IndexOutOfBoundsException();
        }
        return (Integer) js_get(i);
    }

    @Override // java.util.List
    public Integer set(int i, Integer aByte) {
        if (checkIndex(i)) {
            throw new IndexOutOfBoundsException();
        }
        return (Integer) js_set(i, aByte);
    }
}
