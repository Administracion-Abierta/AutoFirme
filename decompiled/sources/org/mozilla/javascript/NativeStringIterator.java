package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeStringIterator.class */
public final class NativeStringIterator extends ES6Iterator {
    private static final long serialVersionUID = 1;
    private static final String ITERATOR_TAG = "StringIterator";
    private String string;
    private int index;

    static void init(ScriptableObject scope, boolean sealed) {
        ES6Iterator.init(scope, sealed, new NativeStringIterator(), ITERATOR_TAG);
    }

    private NativeStringIterator() {
    }

    NativeStringIterator(Scriptable scope, Object stringLike) {
        super(scope, ITERATOR_TAG);
        this.index = 0;
        this.string = ScriptRuntime.toString(stringLike);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "String Iterator";
    }

    @Override // org.mozilla.javascript.ES6Iterator
    protected boolean isDone(Context cx, Scriptable scope) {
        return this.index >= this.string.length();
    }

    @Override // org.mozilla.javascript.ES6Iterator
    protected Object nextValue(Context cx, Scriptable scope) {
        int newIndex = this.string.offsetByCodePoints(this.index, 1);
        Object value = this.string.substring(this.index, newIndex);
        this.index = newIndex;
        return value;
    }

    @Override // org.mozilla.javascript.ES6Iterator
    protected String getTag() {
        return ITERATOR_TAG;
    }
}
