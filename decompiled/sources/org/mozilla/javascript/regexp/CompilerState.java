package org.mozilla.javascript.regexp;

import com.sun.jna.platform.win32.WinNT;
import org.mozilla.javascript.Context;

/* JADX INFO: compiled from: NativeRegExp.java */
/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/regexp/CompilerState.class */
class CompilerState {
    Context cx;
    char[] cpbegin;
    int cpend;
    int flags;
    int parenNesting;
    RENode result;
    int cp = 0;
    int backReferenceLimit = WinNT.MAXLONG;
    int maxBackReference = 0;
    int parenCount = 0;
    int classCount = 0;
    int progLength = 0;

    CompilerState(Context cx, char[] source, int length, int flags) {
        this.cx = cx;
        this.cpbegin = source;
        this.cpend = length;
        this.flags = flags;
    }
}
