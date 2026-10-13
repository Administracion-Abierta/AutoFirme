package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/typedarrays/NativeInt16Array.class */
public class NativeInt16Array extends NativeTypedArrayView<Short> {
    private static final long serialVersionUID = -8592870435287581398L;
    private static final String CLASS_NAME = "Int16Array";
    private static final int BYTES_PER_ELEMENT = 2;

    public NativeInt16Array() {
    }

    public NativeInt16Array(NativeArrayBuffer ab, int off, int len) {
        super(ab, off, len, len * 2);
    }

    public NativeInt16Array(int len) {
        this(new NativeArrayBuffer(((double) len) * 2.0d), 0, len);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return CLASS_NAME;
    }

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeInt16Array a = new NativeInt16Array();
        a.exportAsJSClass(6, scope, sealed);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    /* JADX INFO: renamed from: construct, reason: merged with bridge method [inline-methods] */
    public NativeTypedArrayView<Short> construct2(NativeArrayBuffer ab, int off, int len) {
        return new NativeInt16Array(ab, off, len);
    }

    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    public int getBytesPerElement() {
        return 2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    /* JADX INFO: renamed from: realThis, reason: merged with bridge method [inline-methods] */
    public NativeTypedArrayView<Short> realThis2(Scriptable thisObj, IdFunctionObject f) {
        if (!(thisObj instanceof NativeInt16Array)) {
            throw incompatibleCallError(f);
        }
        return (NativeInt16Array) thisObj;
    }

    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    protected Object js_get(int index) {
        if (checkIndex(index)) {
            return Undefined.instance;
        }
        return ByteIo.readInt16(this.arrayBuffer.buffer, (index * 2) + this.offset, useLittleEndian());
    }

    @Override // org.mozilla.javascript.typedarrays.NativeTypedArrayView
    protected Object js_set(int index, Object c) {
        if (checkIndex(index)) {
            return Undefined.instance;
        }
        int val = Conversions.toInt16(c);
        ByteIo.writeInt16(this.arrayBuffer.buffer, (index * 2) + this.offset, val, useLittleEndian());
        return null;
    }

    @Override // java.util.List
    public Short get(int i) {
        if (checkIndex(i)) {
            throw new IndexOutOfBoundsException();
        }
        return (Short) js_get(i);
    }

    @Override // java.util.List
    public Short set(int i, Short aByte) {
        if (checkIndex(i)) {
            throw new IndexOutOfBoundsException();
        }
        return (Short) js_set(i, aByte);
    }
}
