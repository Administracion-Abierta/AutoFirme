package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ES6Iterator.class */
public abstract class ES6Iterator extends IdScriptableObject {
    private static final long serialVersionUID = 2438373029140003950L;
    protected boolean exhausted = false;
    private String tag;
    private static final int Id_next = 1;
    private static final int SymbolId_iterator = 2;
    private static final int SymbolId_toStringTag = 3;
    private static final int MAX_PROTOTYPE_ID = 3;
    public static final String NEXT_METHOD = "next";
    public static final String DONE_PROPERTY = "done";
    public static final String RETURN_PROPERTY = "return";
    public static final String VALUE_PROPERTY = "value";
    public static final String RETURN_METHOD = "return";

    protected abstract boolean isDone(Context context, Scriptable scriptable);

    protected abstract Object nextValue(Context context, Scriptable scriptable);

    protected static void init(ScriptableObject scope, boolean sealed, IdScriptableObject prototype, String tag) {
        if (scope != null) {
            prototype.setParentScope(scope);
            prototype.setPrototype(getObjectPrototype(scope));
        }
        prototype.activatePrototypeMap(3);
        if (sealed) {
            prototype.sealObject();
        }
        if (scope != null) {
            scope.associateValue(tag, prototype);
        }
    }

    protected ES6Iterator() {
    }

    protected ES6Iterator(Scriptable scope, String tag) {
        this.tag = tag;
        Scriptable top = ScriptableObject.getTopLevelScope(scope);
        setParentScope(top);
        IdScriptableObject prototype = (IdScriptableObject) ScriptableObject.getTopScopeValue(top, tag);
        setPrototype(prototype);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        switch (id) {
            case 1:
                initPrototypeMethod(getTag(), id, NEXT_METHOD, 0);
                return;
            case 2:
                initPrototypeMethod(getTag(), id, SymbolKey.ITERATOR, "[Symbol.iterator]", 3);
                return;
            case 3:
                initPrototypeValue(3, SymbolKey.TO_STRING_TAG, getClassName(), 3);
                return;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(getTag())) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        if (!(thisObj instanceof ES6Iterator)) {
            throw incompatibleCallError(f);
        }
        ES6Iterator iterator = (ES6Iterator) thisObj;
        switch (id) {
            case 1:
                return iterator.next(cx, scope);
            case 2:
                return iterator;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(Symbol k) {
        if (SymbolKey.ITERATOR.equals(k)) {
            return 2;
        }
        if (SymbolKey.TO_STRING_TAG.equals(k)) {
            return 3;
        }
        return 0;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        if (NEXT_METHOD.equals(s)) {
            return 1;
        }
        return 0;
    }

    protected Object next(Context cx, Scriptable scope) {
        Object value = Undefined.instance;
        boolean done = isDone(cx, scope) || this.exhausted;
        if (!done) {
            value = nextValue(cx, scope);
        } else {
            this.exhausted = true;
        }
        return makeIteratorResult(cx, scope, Boolean.valueOf(done), value);
    }

    protected String getTag() {
        return this.tag;
    }

    static Scriptable makeIteratorResult(Context cx, Scriptable scope, Boolean done) {
        return makeIteratorResult(cx, scope, done, Undefined.instance);
    }

    static Scriptable makeIteratorResult(Context cx, Scriptable scope, Boolean done, Object value) {
        Scriptable iteratorResult = cx.newObject(scope);
        ScriptableObject.putProperty(iteratorResult, VALUE_PROPERTY, value);
        ScriptableObject.putProperty(iteratorResult, DONE_PROPERTY, done);
        return iteratorResult;
    }
}
