package org.mozilla.javascript;

import java.io.Closeable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/IteratorLikeIterable.class */
public class IteratorLikeIterable implements Iterable<Object>, Closeable {
    private final Context cx;
    private final Scriptable scope;
    private final Callable next;
    private final Callable returnFunc;
    private final Scriptable iterator;
    private boolean closed;

    public IteratorLikeIterable(Context cx, Scriptable scope, Object target) {
        this.cx = cx;
        this.scope = scope;
        this.next = ScriptRuntime.getPropFunctionAndThis(target, ES6Iterator.NEXT_METHOD, cx, scope);
        this.iterator = ScriptRuntime.lastStoredScriptable(cx);
        Object retObj = ScriptRuntime.getObjectPropNoWarn(target, "return", cx, scope);
        if (retObj != null && !Undefined.isUndefined(retObj)) {
            if (!(retObj instanceof Callable)) {
                throw ScriptRuntime.notFunctionError(target, retObj, "return");
            }
            this.returnFunc = (Callable) retObj;
            return;
        }
        this.returnFunc = null;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        if (!this.closed) {
            this.closed = true;
            if (this.returnFunc != null) {
                this.returnFunc.call(this.cx, this.scope, this.iterator, ScriptRuntime.emptyArgs);
            }
        }
    }

    @Override // java.lang.Iterable
    /* JADX INFO: renamed from: iterator, reason: merged with bridge method [inline-methods] */
    public Iterator<Object> iterator2() {
        return new Itr();
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/IteratorLikeIterable$Itr.class */
    public final class Itr implements Iterator<Object> {
        private Object nextVal;
        private boolean isDone;

        public Itr() {
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            Object val = IteratorLikeIterable.this.next.call(IteratorLikeIterable.this.cx, IteratorLikeIterable.this.scope, IteratorLikeIterable.this.iterator, ScriptRuntime.emptyArgs);
            Object doneval = ScriptableObject.getProperty(ScriptableObject.ensureScriptable(val), ES6Iterator.DONE_PROPERTY);
            if (doneval == Scriptable.NOT_FOUND) {
                doneval = Undefined.instance;
            }
            if (ScriptRuntime.toBoolean(doneval)) {
                this.isDone = true;
                return false;
            }
            this.nextVal = ScriptRuntime.getObjectPropNoWarn(val, ES6Iterator.VALUE_PROPERTY, IteratorLikeIterable.this.cx, IteratorLikeIterable.this.scope);
            return true;
        }

        @Override // java.util.Iterator
        public Object next() {
            if (this.isDone) {
                throw new NoSuchElementException();
            }
            return this.nextVal;
        }
    }
}
