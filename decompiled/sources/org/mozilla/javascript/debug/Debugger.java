package org.mozilla.javascript.debug;

import org.mozilla.javascript.Context;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/debug/Debugger.class */
public interface Debugger {
    void handleCompilationDone(Context context, DebuggableScript debuggableScript, String str);

    DebugFrame getFrame(Context context, DebuggableScript debuggableScript);
}
