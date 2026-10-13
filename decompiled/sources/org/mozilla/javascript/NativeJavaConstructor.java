package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeJavaConstructor.class */
public class NativeJavaConstructor extends BaseFunction {
    private static final long serialVersionUID = -8149253217482668463L;
    MemberBox ctor;

    public NativeJavaConstructor(MemberBox ctor) {
        this.ctor = ctor;
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.Function, org.mozilla.javascript.Callable
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return NativeJavaClass.constructSpecific(cx, scope, args, this.ctor);
    }

    @Override // org.mozilla.javascript.BaseFunction
    public String getFunctionName() {
        String sig = JavaMembers.liveConnectSignature(this.ctor.argTypes);
        return "<init>".concat(sig);
    }

    public String toString() {
        return "[JavaConstructor " + this.ctor.getName() + "]";
    }
}
