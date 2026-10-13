package org.mozilla.javascript;

import java.lang.reflect.Field;

/* JADX INFO: compiled from: JavaMembers.java */
/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/FieldAndMethods.class */
class FieldAndMethods extends NativeJavaMethod {
    private static final long serialVersionUID = -9222428244284796755L;
    Field field;
    Object javaObject;

    FieldAndMethods(Scriptable scope, MemberBox[] methods, Field field) {
        super(methods);
        this.field = field;
        setParentScope(scope);
        setPrototype(ScriptableObject.getFunctionPrototype(scope));
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public Object getDefaultValue(Class<?> hint) {
        if (hint == ScriptRuntime.FunctionClass) {
            return this;
        }
        try {
            Object rval = this.field.get(this.javaObject);
            Class<?> type = this.field.getType();
            Context cx = Context.getContext();
            Object rval2 = cx.getWrapFactory().wrap(cx, this, rval, type);
            if (rval2 instanceof Scriptable) {
                rval2 = ((Scriptable) rval2).getDefaultValue(hint);
            }
            return rval2;
        } catch (IllegalAccessException e) {
            throw Context.reportRuntimeError1("msg.java.internal.private", this.field.getName());
        }
    }
}
