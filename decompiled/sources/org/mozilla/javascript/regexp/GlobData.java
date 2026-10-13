package org.mozilla.javascript.regexp;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/* JADX INFO: compiled from: RegExpImpl.java */
/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/regexp/GlobData.class */
final class GlobData {
    int mode;
    boolean global;
    String str;
    Scriptable arrayobj;
    Function lambda;
    String repstr;
    int dollar = -1;
    StringBuilder charBuf;
    int leftIndex;

    GlobData() {
    }
}
