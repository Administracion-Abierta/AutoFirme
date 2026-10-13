package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ScriptRuntimeES6.class */
public class ScriptRuntimeES6 {
    public static Object requireObjectCoercible(Context cx, Object val, IdFunctionObject idFuncObj) {
        if (val == null || Undefined.isUndefined(val)) {
            throw ScriptRuntime.typeError2("msg.called.null.or.undefined", idFuncObj.getTag(), idFuncObj.getFunctionName());
        }
        return val;
    }
}
