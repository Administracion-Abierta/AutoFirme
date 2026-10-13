package org.mozilla.javascript.typedarrays;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ExternalArrayData;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeArrayIterator;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/typedarrays/NativeTypedArrayView.class */
public abstract class NativeTypedArrayView<T> extends NativeArrayBufferView implements List<T>, RandomAccess, ExternalArrayData {
    private static final long serialVersionUID = -4963053773152251274L;
    protected final int length;
    private static final int Id_constructor = 1;
    private static final int Id_toString = 2;
    private static final int Id_get = 3;
    private static final int Id_set = 4;
    private static final int Id_subarray = 5;
    private static final int SymbolId_iterator = 6;
    protected static final int MAX_PROTOTYPE_ID = 6;
    private static final int Id_length = 4;
    private static final int Id_BYTES_PER_ELEMENT = 5;
    private static final int MAX_INSTANCE_ID = 5;

    public abstract int getBytesPerElement();

    protected abstract NativeTypedArrayView<T> construct(NativeArrayBuffer nativeArrayBuffer, int i, int i2);

    protected abstract Object js_get(int i);

    protected abstract Object js_set(int i, Object obj);

    protected abstract NativeTypedArrayView<T> realThis(Scriptable scriptable, IdFunctionObject idFunctionObject);

    protected NativeTypedArrayView() {
        this.length = 0;
    }

    protected NativeTypedArrayView(NativeArrayBuffer ab, int off, int len, int byteLen) {
        super(ab, off, byteLen);
        this.length = len;
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public Object get(int index, Scriptable start) {
        return js_get(index);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public boolean has(int index, Scriptable start) {
        return !checkIndex(index);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public void put(int index, Scriptable start, Object val) {
        js_set(index, val);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public void delete(int index) {
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public Object[] getIds() {
        Object[] ret = new Object[this.length];
        for (int i = 0; i < this.length; i++) {
            ret[i] = Integer.valueOf(i);
        }
        return ret;
    }

    protected boolean checkIndex(int index) {
        return index < 0 || index >= this.length;
    }

    private NativeArrayBuffer makeArrayBuffer(Context cx, Scriptable scope, int length) {
        return (NativeArrayBuffer) cx.newObject(scope, NativeArrayBuffer.CLASS_NAME, new Object[]{Double.valueOf(((double) length) * ((double) getBytesPerElement()))});
    }

    private NativeTypedArrayView<T> js_constructor(Context cx, Scriptable scope, Object[] args) {
        int byteLen;
        if (!isArg(args, 0)) {
            return construct(new NativeArrayBuffer(), 0, 0);
        }
        Object arg0 = args[0];
        if (arg0 == null) {
            return construct(new NativeArrayBuffer(), 0, 0);
        }
        if ((arg0 instanceof Number) || (arg0 instanceof String)) {
            int length = ScriptRuntime.toInt32(arg0);
            NativeArrayBuffer buffer = makeArrayBuffer(cx, scope, length);
            return construct(buffer, 0, length);
        }
        if (arg0 instanceof NativeTypedArrayView) {
            NativeTypedArrayView<T> src = (NativeTypedArrayView) arg0;
            NativeArrayBuffer na = makeArrayBuffer(cx, scope, src.length);
            NativeTypedArrayView<T> v = construct(na, 0, src.length);
            for (int i = 0; i < src.length; i++) {
                v.js_set(i, src.js_get(i));
            }
            return v;
        }
        if (arg0 instanceof NativeArrayBuffer) {
            NativeArrayBuffer na2 = (NativeArrayBuffer) arg0;
            int byteOff = isArg(args, 1) ? ScriptRuntime.toInt32(args[1]) : 0;
            if (isArg(args, 2)) {
                byteLen = ScriptRuntime.toInt32(args[2]) * getBytesPerElement();
            } else {
                byteLen = na2.getLength() - byteOff;
            }
            if (byteOff < 0 || byteOff > na2.buffer.length) {
                throw ScriptRuntime.rangeError("offset out of range");
            }
            if (byteLen < 0 || byteOff + byteLen > na2.buffer.length) {
                throw ScriptRuntime.rangeError("length out of range");
            }
            if (byteOff % getBytesPerElement() != 0) {
                throw ScriptRuntime.rangeError("offset must be a multiple of the byte size");
            }
            if (byteLen % getBytesPerElement() != 0) {
                throw ScriptRuntime.rangeError("offset and buffer must be a multiple of the byte size");
            }
            return construct(na2, byteOff, byteLen / getBytesPerElement());
        }
        if (arg0 instanceof NativeArray) {
            NativeArray nativeArray = (NativeArray) arg0;
            NativeArrayBuffer na3 = makeArrayBuffer(cx, scope, nativeArray.size());
            NativeTypedArrayView<T> v2 = construct(na3, 0, nativeArray.size());
            for (int i2 = 0; i2 < nativeArray.size(); i2++) {
                Object value = nativeArray.get(i2, nativeArray);
                if (value == Scriptable.NOT_FOUND || value == Undefined.instance) {
                    v2.js_set(i2, ScriptRuntime.NaNobj);
                } else if (value instanceof Wrapper) {
                    v2.js_set(i2, ((Wrapper) value).unwrap());
                } else {
                    v2.js_set(i2, value);
                }
            }
            return v2;
        }
        if (ScriptRuntime.isArrayObject(arg0)) {
            Object[] arrayElements = ScriptRuntime.getArrayElements((Scriptable) arg0);
            NativeArrayBuffer na4 = makeArrayBuffer(cx, scope, arrayElements.length);
            NativeTypedArrayView<T> v3 = construct(na4, 0, arrayElements.length);
            for (int i3 = 0; i3 < arrayElements.length; i3++) {
                v3.js_set(i3, arrayElements[i3]);
            }
            return v3;
        }
        throw ScriptRuntime.constructError("Error", "invalid argument");
    }

    private void setRange(NativeTypedArrayView<T> v, int off) {
        if (off >= this.length) {
            throw ScriptRuntime.rangeError("offset out of range");
        }
        if (v.length > this.length - off) {
            throw ScriptRuntime.rangeError("source array too long");
        }
        if (v.arrayBuffer == this.arrayBuffer) {
            Object[] tmp = new Object[v.length];
            for (int i = 0; i < v.length; i++) {
                tmp[i] = v.js_get(i);
            }
            for (int i2 = 0; i2 < v.length; i2++) {
                js_set(i2 + off, tmp[i2]);
            }
            return;
        }
        for (int i3 = 0; i3 < v.length; i3++) {
            js_set(i3 + off, v.js_get(i3));
        }
    }

    private void setRange(NativeArray a, int off) {
        if (off > this.length) {
            throw ScriptRuntime.rangeError("offset out of range");
        }
        if (off + a.size() > this.length) {
            throw ScriptRuntime.rangeError("offset + length out of range");
        }
        int pos = off;
        for (Object val : a) {
            js_set(pos, val);
            pos++;
        }
    }

    private Object js_subarray(Context cx, Scriptable scope, int s, int e) {
        int start = s < 0 ? this.length + s : s;
        int end = e < 0 ? this.length + e : e;
        int start2 = Math.max(0, start);
        int len = Math.max(0, Math.min(this.length, end) - start2);
        int byteOff = Math.min(start2 * getBytesPerElement(), this.arrayBuffer.getLength());
        return cx.newObject(scope, getClassName(), new Object[]{this.arrayBuffer, Integer.valueOf(byteOff), Integer.valueOf(len)});
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(getClassName())) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case 1:
                if (thisObj != null && cx.getLanguageVersion() >= 200) {
                    throw ScriptRuntime.typeError1("msg.only.from.new", getClassName());
                }
                return js_constructor(cx, scope, args);
            case 2:
                NativeTypedArrayView<T> realThis = realThis(thisObj, f);
                int arrayLength = realThis.getArrayLength();
                StringBuilder builder = new StringBuilder();
                if (arrayLength > 0) {
                    builder.append(ScriptRuntime.toString(realThis.js_get(0)));
                }
                for (int i = 1; i < arrayLength; i++) {
                    builder.append(',');
                    builder.append(ScriptRuntime.toString(realThis.js_get(i)));
                }
                return builder.toString();
            case 3:
                if (args.length > 0) {
                    return realThis(thisObj, f).js_get(ScriptRuntime.toInt32(args[0]));
                }
                throw ScriptRuntime.constructError("Error", "invalid arguments");
            case 4:
                if (args.length > 0) {
                    NativeTypedArrayView<T> self = realThis(thisObj, f);
                    if (args[0] instanceof NativeTypedArrayView) {
                        int offset = isArg(args, 1) ? ScriptRuntime.toInt32(args[1]) : 0;
                        NativeTypedArrayView<T> nativeView = (NativeTypedArrayView) args[0];
                        self.setRange(nativeView, offset);
                        return Undefined.instance;
                    }
                    if (args[0] instanceof NativeArray) {
                        int offset2 = isArg(args, 1) ? ScriptRuntime.toInt32(args[1]) : 0;
                        self.setRange((NativeArray) args[0], offset2);
                        return Undefined.instance;
                    }
                    if (args[0] instanceof Scriptable) {
                        return Undefined.instance;
                    }
                    if (isArg(args, 2)) {
                        return self.js_set(ScriptRuntime.toInt32(args[0]), args[1]);
                    }
                }
                throw ScriptRuntime.constructError("Error", "invalid arguments");
            case 5:
                if (args.length > 0) {
                    NativeTypedArrayView<T> self2 = realThis(thisObj, f);
                    int start = ScriptRuntime.toInt32(args[0]);
                    int end = isArg(args, 1) ? ScriptRuntime.toInt32(args[1]) : self2.length;
                    return self2.js_subarray(cx, scope, start, end);
                }
                throw ScriptRuntime.constructError("Error", "invalid arguments");
            case 6:
                return new NativeArrayIterator(scope, thisObj, NativeArrayIterator.ARRAY_ITERATOR_TYPE.VALUES);
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        int arity;
        String s;
        if (id == 6) {
            initPrototypeMethod(getClassName(), id, SymbolKey.ITERATOR, "[Symbol.iterator]", 0);
            return;
        }
        switch (id) {
            case 1:
                arity = 3;
                s = "constructor";
                break;
            case 2:
                arity = 0;
                s = "toString";
                break;
            case 3:
                arity = 1;
                s = "get";
                break;
            case 4:
                arity = 2;
                s = "set";
                break;
            case 5:
                arity = 2;
                s = "subarray";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(getClassName(), id, s, (String) null, arity);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(Symbol k) {
        if (SymbolKey.ITERATOR.equals(k)) {
            return 6;
        }
        return 0;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        int id = 0;
        String X = null;
        int s_length = s.length();
        if (s_length == 3) {
            int c = s.charAt(0);
            if (c == 103) {
                if (s.charAt(2) == 't' && s.charAt(1) == 'e') {
                    id = 3;
                }
            } else if (c == 115 && s.charAt(2) == 't' && s.charAt(1) == 'e') {
                id = 4;
            }
            return id;
        }
        if (s_length == 8) {
            int c2 = s.charAt(0);
            if (c2 == 115) {
                X = "subarray";
                id = 5;
            } else if (c2 == 116) {
                X = "toString";
                id = 2;
            }
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
        ctor.defineProperty("BYTES_PER_ELEMENT", ScriptRuntime.wrapInt(getBytesPerElement()), 7);
        super.fillConstructorProperties(ctor);
    }

    @Override // org.mozilla.javascript.typedarrays.NativeArrayBufferView, org.mozilla.javascript.IdScriptableObject
    protected int getMaxInstanceId() {
        return 5;
    }

    @Override // org.mozilla.javascript.typedarrays.NativeArrayBufferView, org.mozilla.javascript.IdScriptableObject
    protected String getInstanceIdName(int id) {
        switch (id) {
            case 4:
                return XSDatatype.FACET_LENGTH;
            case 5:
                return "BYTES_PER_ELEMENT";
            default:
                return super.getInstanceIdName(id);
        }
    }

    @Override // org.mozilla.javascript.typedarrays.NativeArrayBufferView, org.mozilla.javascript.IdScriptableObject
    protected Object getInstanceIdValue(int id) {
        switch (id) {
            case 4:
                return ScriptRuntime.wrapInt(this.length);
            case 5:
                return ScriptRuntime.wrapInt(getBytesPerElement());
            default:
                return super.getInstanceIdValue(id);
        }
    }

    @Override // org.mozilla.javascript.typedarrays.NativeArrayBufferView, org.mozilla.javascript.IdScriptableObject
    protected int findInstanceIdInfo(String s) {
        int id = 0;
        String X = null;
        int s_length = s.length();
        if (s_length == 6) {
            X = XSDatatype.FACET_LENGTH;
            id = 4;
        } else if (s_length == 17) {
            X = "BYTES_PER_ELEMENT";
            id = 5;
        }
        if (X != null && X != s && !X.equals(s)) {
            id = 0;
        }
        if (id == 0) {
            return super.findInstanceIdInfo(s);
        }
        if (id == 5) {
            return instanceIdInfo(7, id);
        }
        return instanceIdInfo(5, id);
    }

    @Override // org.mozilla.javascript.ExternalArrayData
    public Object getArrayElement(int index) {
        return js_get(index);
    }

    @Override // org.mozilla.javascript.ExternalArrayData
    public void setArrayElement(int index, Object value) {
        js_set(index, value);
    }

    @Override // org.mozilla.javascript.ExternalArrayData
    public int getArrayLength() {
        return this.length;
    }

    @Override // java.util.List, java.util.Collection
    public boolean containsAll(Collection<?> objects) {
        for (Object o : objects) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override // java.util.List
    public int indexOf(Object o) {
        for (int i = 0; i < this.length; i++) {
            if (o.equals(js_get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override // java.util.List
    public int lastIndexOf(Object o) {
        for (int i = this.length - 1; i >= 0; i--) {
            if (o.equals(js_get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override // java.util.List, java.util.Collection
    public Object[] toArray() {
        Object[] a = new Object[this.length];
        for (int i = 0; i < this.length; i++) {
            a[i] = js_get(i);
        }
        return a;
    }

    @Override // java.util.List, java.util.Collection
    public <U> U[] toArray(U[] uArr) {
        Object[] objArr;
        if (uArr.length >= this.length) {
            objArr = uArr;
        } else {
            objArr = (Object[]) Array.newInstance(uArr.getClass().getComponentType(), this.length);
        }
        for (int i = 0; i < this.length; i++) {
            try {
                objArr[i] = js_get(i);
            } catch (ClassCastException e) {
                throw new ArrayStoreException();
            }
        }
        return (U[]) objArr;
    }

    @Override // org.mozilla.javascript.ScriptableObject, java.util.List, java.util.Collection
    public int size() {
        return this.length;
    }

    @Override // org.mozilla.javascript.ScriptableObject, java.util.List, java.util.Collection
    public boolean isEmpty() {
        return this.length == 0;
    }

    @Override // java.util.List, java.util.Collection
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override // java.util.List, java.util.Collection
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        try {
            NativeTypedArrayView<T> v = (NativeTypedArrayView) o;
            if (this.length != v.length) {
                return false;
            }
            for (int i = 0; i < this.length; i++) {
                if (!js_get(i).equals(v.js_get(i))) {
                    return false;
                }
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override // java.util.List, java.util.Collection
    public int hashCode() {
        int hc = 0;
        for (int i = 0; i < this.length; i++) {
            hc += js_get(i).hashCode();
        }
        return hc;
    }

    @Override // java.util.List, java.util.Collection, java.lang.Iterable
    public Iterator<T> iterator() {
        return new NativeTypedArrayIterator(this, 0);
    }

    @Override // java.util.List
    public ListIterator<T> listIterator() {
        return new NativeTypedArrayIterator(this, 0);
    }

    @Override // java.util.List
    public ListIterator<T> listIterator(int start) {
        if (checkIndex(start)) {
            throw new IndexOutOfBoundsException();
        }
        return new NativeTypedArrayIterator(this, start);
    }

    @Override // java.util.List
    public List<T> subList(int i, int i2) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.List, java.util.Collection
    public boolean add(T aByte) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.List
    public void add(int i, T aByte) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.List, java.util.Collection
    public boolean addAll(Collection<? extends T> bytes) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.List
    public boolean addAll(int i, Collection<? extends T> bytes) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.List, java.util.Collection
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.List
    public T remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.List, java.util.Collection
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.List, java.util.Collection
    public boolean removeAll(Collection<?> objects) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.List, java.util.Collection
    public boolean retainAll(Collection<?> objects) {
        throw new UnsupportedOperationException();
    }
}
