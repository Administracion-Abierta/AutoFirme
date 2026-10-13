package org.mozilla.javascript.xml;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.NativeWith;
import org.mozilla.javascript.Ref;
import org.mozilla.javascript.Scriptable;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/xml/XMLObject.class */
public abstract class XMLObject extends IdScriptableObject {
    private static final long serialVersionUID = 8455156490438576500L;

    public abstract boolean has(Context context, Object obj);

    public abstract Object get(Context context, Object obj);

    public abstract void put(Context context, Object obj, Object obj2);

    public abstract boolean delete(Context context, Object obj);

    public abstract Object getFunctionProperty(Context context, String str);

    public abstract Object getFunctionProperty(Context context, int i);

    public abstract Scriptable getExtraMethodSource(Context context);

    public abstract Ref memberRef(Context context, Object obj, int i);

    public abstract Ref memberRef(Context context, Object obj, Object obj2, int i);

    public abstract NativeWith enterWith(Scriptable scriptable);

    public abstract NativeWith enterDotQuery(Scriptable scriptable);

    public XMLObject() {
    }

    public XMLObject(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    public Object addValues(Context cx, boolean thisIsLeft, Object value) {
        return Scriptable.NOT_FOUND;
    }

    @Override // org.mozilla.javascript.ScriptableObject
    public String getTypeOf() {
        return avoidObjectDetection() ? "undefined" : "xml";
    }
}
