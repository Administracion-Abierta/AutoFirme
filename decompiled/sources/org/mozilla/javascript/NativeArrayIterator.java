package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeArrayIterator.class */
public final class NativeArrayIterator extends ES6Iterator {
    private static final long serialVersionUID = 1;
    private static final String ITERATOR_TAG = "ArrayIterator";
    private ARRAY_ITERATOR_TYPE type;
    private Scriptable arrayLike;
    private int index;

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeArrayIterator$ARRAY_ITERATOR_TYPE.class */
    public enum ARRAY_ITERATOR_TYPE {
        ENTRIES,
        KEYS,
        VALUES
    }

    static void init(ScriptableObject scope, boolean sealed) {
        ES6Iterator.init(scope, sealed, new NativeArrayIterator(), ITERATOR_TAG);
    }

    private NativeArrayIterator() {
    }

    public NativeArrayIterator(Scriptable scope, Scriptable arrayLike, ARRAY_ITERATOR_TYPE type) {
        super(scope, ITERATOR_TAG);
        this.index = 0;
        this.arrayLike = arrayLike;
        this.type = type;
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "Array Iterator";
    }

    @Override // org.mozilla.javascript.ES6Iterator
    protected boolean isDone(Context cx, Scriptable scope) {
        return ((long) this.index) >= NativeArray.getLengthProperty(cx, this.arrayLike, false);
    }

    @Override // org.mozilla.javascript.ES6Iterator
    protected Object nextValue(Context cx, Scriptable scope) {
        if (this.type == ARRAY_ITERATOR_TYPE.KEYS) {
            int i = this.index;
            this.index = i + 1;
            return Integer.valueOf(i);
        }
        Object value = this.arrayLike.get(this.index, this.arrayLike);
        if (value == Scriptable.NOT_FOUND) {
            value = Undefined.instance;
        }
        if (this.type == ARRAY_ITERATOR_TYPE.ENTRIES) {
            value = cx.newArray(scope, new Object[]{Integer.valueOf(this.index), value});
        }
        this.index++;
        return value;
    }

    @Override // org.mozilla.javascript.ES6Iterator
    protected String getTag() {
        return ITERATOR_TAG;
    }
}
