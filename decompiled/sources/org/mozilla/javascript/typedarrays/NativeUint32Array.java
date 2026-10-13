package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/typedarrays/NativeUint32Array.class */
public class NativeUint32Array extends NativeTypedArrayView<Long> {
    private static final long serialVersionUID = -7987831421954144244L;
    private static final String CLASS_NAME = "Uint32Array";
    private static final int BYTES_PER_ELEMENT = 4;

    public NativeUint32Array() {
    }

    public NativeUint32Array(NativeArrayBuffer ab, int off, int len) {
        super(ab, off, len, len * 4);
    }

    public NativeUint32Array(int len) {
        this(new NativeArrayBuffer(((double) len) * 4.0d), 0, len);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return CLASS_NAME;
    }

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeUint32Array a = new NativeUint32Array();
        a.exportAsJSClass(6, scope, sealed);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    /* JADX INFO: renamed from: construct */
    public NativeTypedArrayView<Long> construct2(NativeArrayBuffer ab, int off, int len) {
        return new NativeUint32Array(ab, off, len);
    }

    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    public int getBytesPerElement() {
        return 4;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    /* JADX INFO: renamed from: realThis */
    public NativeTypedArrayView<Long> realThis2(Scriptable thisObj, IdFunctionObject f) {
        if (!(thisObj instanceof NativeUint32Array)) {
            throw incompatibleCallError(f);
        }
        return (NativeUint32Array) thisObj;
    }

    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    protected Object js_get(int index) {
        if (checkIndex(index)) {
            return Undefined.instance;
        }
        return ByteIo.readUint32(this.arrayBuffer.buffer, (index * 4) + this.offset, useLittleEndian());
    }

    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    protected Object js_set(int index, Object c) {
        if (checkIndex(index)) {
            return Undefined.instance;
        }
        long val = Conversions.toUint32(c);
        ByteIo.writeUint32(this.arrayBuffer.buffer, (index * 4) + this.offset, val, useLittleEndian());
        return null;
    }

    @Override // java.util.List
    public Long get(int i) {
        if (checkIndex(i)) {
            throw new IndexOutOfBoundsException();
        }
        return (Long) js_get(i);
    }

    @Override // java.util.List
    public Long set(int i, Long aByte) {
        if (checkIndex(i)) {
            throw new IndexOutOfBoundsException();
        }
        return (Long) js_set(i, aByte);
    }
}
