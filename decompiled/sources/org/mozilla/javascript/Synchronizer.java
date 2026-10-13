package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Synchronizer.class */
public class Synchronizer extends Delegator {
    private Object syncObject;

    public Synchronizer(Scriptable obj) {
        super(obj);
    }

    public Synchronizer(Scriptable obj, Object syncObject) {
        super(obj);
        this.syncObject = syncObject;
    }

    @Override // org.mozilla.javascript.Delegator, org.mozilla.javascript.Function, org.mozilla.javascript.Callable
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        Object sync = this.syncObject != null ? this.syncObject : thisObj;
        Object objUnwrap = sync instanceof Wrapper ? ((Wrapper) sync).unwrap() : sync;
        Object obj = objUnwrap;
        synchronized (objUnwrap) {
            Object objCall = ((Function) this.obj).call(cx, scope, thisObj, args);
            return objCall;
        }
    }
}
