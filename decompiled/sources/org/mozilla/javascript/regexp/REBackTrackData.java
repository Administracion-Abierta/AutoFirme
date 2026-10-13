package org.mozilla.javascript.regexp;

/* JADX INFO: compiled from: NativeRegExp.java */
/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/regexp/REBackTrackData.class */
class REBackTrackData {
    final REBackTrackData previous;
    final int op;
    final int pc;
    final int cp;
    final int continuationOp;
    final int continuationPc;
    final long[] parens;
    final REProgState stateStackTop;

    REBackTrackData(REGlobalData gData, int op, int pc, int cp, int continuationOp, int continuationPc) {
        this.previous = gData.backTrackStackTop;
        this.op = op;
        this.pc = pc;
        this.cp = cp;
        this.continuationOp = continuationOp;
        this.continuationPc = continuationPc;
        this.parens = gData.parens;
        this.stateStackTop = gData.stateStackTop;
    }
}
