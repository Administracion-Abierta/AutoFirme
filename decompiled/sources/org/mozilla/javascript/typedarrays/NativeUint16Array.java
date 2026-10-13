package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/typedarrays/NativeUint16Array.class */
public class NativeUint16Array extends NativeTypedArrayView<Integer> {
    private static final long serialVersionUID = 7700018949434240321L;
    private static final String CLASS_NAME = "Uint16Array";
    private static final int BYTES_PER_ELEMENT = 2;

    public NativeUint16Array() {
    }

    public NativeUint16Array(NativeArrayBuffer ab, int off, int len) {
        super(ab, off, len, len * 2);
    }

    public NativeUint16Array(int len) {
        this(new NativeArrayBuffer(((double) len) * 2.0d), 0, len);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return CLASS_NAME;
    }

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeUint16Array a = new NativeUint16Array();
        a.exportAsJSClass(6, scope, sealed);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    /* JADX INFO: renamed from: construct */
    public NativeTypedArrayView<Integer> construct2(NativeArrayBuffer ab, int off, int len) {
        return new NativeUint16Array(ab, off, len);
    }

    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    public int getBytesPerElement() {
        return 2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    /* JADX INFO: renamed from: realThis */
    public NativeTypedArrayView<Integer> realThis2(Scriptable thisObj, IdFunctionObject f) {
        if (!(thisObj instanceof NativeUint16Array)) {
            throw incompatibleCallError(f);
        }
        return (NativeUint16Array) thisObj;
    }

    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    protected Object js_get(int index) {
        if (checkIndex(index)) {
            return Undefined.instance;
        }
        return ByteIo.readUint16(this.arrayBuffer.buffer, (index * 2) + this.offset, useLittleEndian());
    }

    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    protected Object js_set(int index, Object c) {
        if (checkIndex(index)) {
            return Undefined.instance;
        }
        int val = Conversions.toUint16(c);
        ByteIo.writeUint16(this.arrayBuffer.buffer, (index * 2) + this.offset, val, useLittleEndian());
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
