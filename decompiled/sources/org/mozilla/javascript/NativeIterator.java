package org.mozilla.javascript;

import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeIterator.class */
public final class NativeIterator extends IdScriptableObject {
    private static final long serialVersionUID = -4136968203581667681L;
    private static final Object ITERATOR_TAG = "Iterator";
    private static final String STOP_ITERATION = "StopIteration";
    public static final String ITERATOR_PROPERTY_NAME = "__iterator__";
    private static final int Id_constructor = 1;
    private static final int Id_next = 2;
    private static final int Id___iterator__ = 3;
    private static final int MAX_PROTOTYPE_ID = 3;
    private Object objectIterator;

    static void init(Context cx, ScriptableObject scope, boolean sealed) {
        NativeIterator iterator = new NativeIterator();
        iterator.exportAsJSClass(3, scope, sealed);
        if (cx.getLanguageVersion() >= 200) {
            ES6Generator.init(scope, sealed);
        } else {
            NativeGenerator.init(scope, sealed);
        }
        NativeObject obj = new StopIteration();
        obj.setPrototype(getObjectPrototype(scope));
        obj.setParentScope(scope);
        if (sealed) {
            obj.sealObject();
        }
        ScriptableObject.defineProperty(scope, STOP_ITERATION, obj, 2);
        scope.associateValue(ITERATOR_TAG, obj);
    }

    private NativeIterator() {
    }

    private NativeIterator(Object objectIterator) {
        this.objectIterator = objectIterator;
    }

    public static Object getStopIterationObject(Scriptable scope) {
        Scriptable top = ScriptableObject.getTopLevelScope(scope);
        return ScriptableObject.getTopScopeValue(top, ITERATOR_TAG);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeIterator$StopIteration.class */
    public static class StopIteration extends NativeObject {
        private static final long serialVersionUID = 2485151085722377663L;
        private Object value;

        public StopIteration() {
            this.value = Undefined.instance;
        }

        public StopIteration(Object val) {
            this.value = Undefined.instance;
            this.value = val;
        }

        public Object getValue() {
            return this.value;
        }

        @Override // org.mozilla.javascript.NativeObject, org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
        public String getClassName() {
            return NativeIterator.STOP_ITERATION;
        }

        @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
        public boolean hasInstance(Scriptable instance) {
            return instance instanceof StopIteration;
        }
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "Iterator";
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        int arity;
        String s;
        switch (id) {
            case 1:
                arity = 2;
                s = "constructor";
                break;
            case 2:
                arity = 0;
                s = ES6Iterator.NEXT_METHOD;
                break;
            case 3:
                arity = 1;
                s = ITERATOR_PROPERTY_NAME;
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(ITERATOR_TAG, id, s, arity);
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(ITERATOR_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        if (id == 1) {
            return jsConstructor(cx, scope, thisObj, args);
        }
        if (!(thisObj instanceof NativeIterator)) {
            throw incompatibleCallError(f);
        }
        NativeIterator iterator = (NativeIterator) thisObj;
        switch (id) {
            case 2:
                return iterator.next(cx, scope);
            case 3:
                return thisObj;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    private static Object jsConstructor(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (args.length == 0 || args[0] == null || args[0] == Undefined.instance) {
            Object argument = args.length == 0 ? Undefined.instance : args[0];
            throw ScriptRuntime.typeError1("msg.no.properties", ScriptRuntime.toString(argument));
        }
        Scriptable obj = ScriptRuntime.toObject(cx, scope, args[0]);
        boolean keyOnly = args.length > 1 && ScriptRuntime.toBoolean(args[1]);
        if (thisObj != null) {
            Iterator<?> iterator = getJavaIterator(obj);
            if (iterator != null) {
                Scriptable scope2 = ScriptableObject.getTopLevelScope(scope);
                return cx.getWrapFactory().wrap(cx, scope2, new WrappedJavaIterator(iterator, scope2), WrappedJavaIterator.class);
            }
            Scriptable jsIterator = ScriptRuntime.toIterator(cx, scope, obj, keyOnly);
            if (jsIterator != null) {
                return jsIterator;
            }
        }
        Object objectIterator = ScriptRuntime.enumInit(obj, cx, scope, keyOnly ? 3 : 5);
        ScriptRuntime.setEnumNumbers(objectIterator, true);
        NativeIterator result = new NativeIterator(objectIterator);
        result.setPrototype(ScriptableObject.getClassPrototype(scope, result.getClassName()));
        result.setParentScope(scope);
        return result;
    }

    private Object next(Context cx, Scriptable scope) {
        Boolean b = ScriptRuntime.enumNext(this.objectIterator);
        if (!b.booleanValue()) {
            throw new JavaScriptException(getStopIterationObject(scope), null, 0);
        }
        return ScriptRuntime.enumId(this.objectIterator, cx);
    }

    private static Iterator<?> getJavaIterator(Object obj) {
        if (obj instanceof Wrapper) {
            Object unwrapped = ((Wrapper) obj).unwrap();
            Iterator<?> iterator = null;
            if (unwrapped instanceof Iterator) {
                iterator = (Iterator) unwrapped;
            }
            if (unwrapped instanceof Iterable) {
                iterator = ((Iterable) unwrapped).iterator();
            }
            return iterator;
        }
        return null;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeIterator$WrappedJavaIterator.class */
    public static class WrappedJavaIterator {
        private Iterator<?> iterator;
        private Scriptable scope;

        WrappedJavaIterator(Iterator<?> iterator, Scriptable scope) {
            this.iterator = iterator;
            this.scope = scope;
        }

        public Object next() {
            if (!this.iterator.hasNext()) {
                throw new JavaScriptException(NativeIterator.getStopIterationObject(this.scope), null, 0);
            }
            return this.iterator.next();
        }

        public Object __iterator__(boolean b) {
            return this;
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        int id = 0;
        String X = null;
        int s_length = s.length();
        if (s_length == 4) {
            X = ES6Iterator.NEXT_METHOD;
            id = 2;
        } else if (s_length == 11) {
            X = "constructor";
            id = 1;
        } else if (s_length == 12) {
            X = ITERATOR_PROPERTY_NAME;
            id = 3;
        }
        if (X != null && X != s && !X.equals(s)) {
            id = 0;
        }
        return id;
    }
}
