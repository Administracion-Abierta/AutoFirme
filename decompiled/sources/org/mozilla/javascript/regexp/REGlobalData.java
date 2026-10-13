package org.mozilla.javascript.regexp;

/* JADX INFO: compiled from: NativeRegExp.java */
/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/regexp/REGlobalData.class */
class REGlobalData {
    boolean multiline;
    RECompiled regexp;
    int skipped;
    int cp;
    long[] parens;
    REProgState stateStackTop;
    REBackTrackData backTrackStackTop;

    REGlobalData() {
    }

    int parensIndex(int i) {
        return (int) this.parens[i];
    }

    int parensLength(int i) {
        return (int) (this.parens[i] >>> 32);
    }

    void setParens(int i, int index, int length) {
        if (this.backTrackStackTop != null && this.backTrackStackTop.parens == this.parens) {
            this.parens = (long[]) this.parens.clone();
        }
        this.parens[i] = (((long) index) & 4294967295L) | (((long) length) << 32);
    }
}
