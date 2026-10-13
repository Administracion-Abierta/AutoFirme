package delight.rhinosandox.internal;

import java.lang.reflect.Member;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;

/* JADX INFO: loaded from: autofirma.jar:delight/rhinosandox/internal/RhinoEval.class */
public class RhinoEval extends FunctionObject {
    public RhinoEval(String name, Member methodOrConstructor, Scriptable scope) {
        super(name, methodOrConstructor, scope);
    }

    @Override // org.mozilla.javascript.FunctionObject, org.mozilla.javascript.BaseFunction, org.mozilla.javascript.Function, org.mozilla.javascript.Callable
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        String script = args[0].toString();
        int idx = script.lastIndexOf("//# sourceURL=");
        String scriptUrl = null;
        if (idx != -1) {
            int _length = "//# sourceURL=".length();
            int _plus = idx + _length;
            String scriptUrl2 = script.substring(_plus);
            scriptUrl = scriptUrl2.replace("\n", "").replace(" ", "");
        }
        return cx.evaluateString(scope, script, scriptUrl, 1, null);
    }
}
