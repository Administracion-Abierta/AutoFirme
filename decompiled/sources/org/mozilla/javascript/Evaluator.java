package org.mozilla.javascript;

import java.util.List;
import org.mozilla.javascript.ast.ScriptNode;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Evaluator.class */
public interface Evaluator {
    Object compile(CompilerEnvirons compilerEnvirons, ScriptNode scriptNode, String str, boolean z);

    Function createFunctionObject(Context context, Scriptable scriptable, Object obj, Object obj2);

    Script createScriptObject(Object obj, Object obj2);

    void captureStackInfo(RhinoException rhinoException);

    String getSourcePositionFromStack(Context context, int[] iArr);

    String getPatchedStack(RhinoException rhinoException, String str);

    List<String> getScriptStack(RhinoException rhinoException);

    void setEvalScriptFlag(Script script);
}
