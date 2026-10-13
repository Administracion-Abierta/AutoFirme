package org.mozilla.classfile;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/classfile/ExceptionTableEntry.class */
final class ExceptionTableEntry {
    int itsStartLabel;
    int itsEndLabel;
    int itsHandlerLabel;
    short itsCatchType;

    ExceptionTableEntry(int startLabel, int endLabel, int handlerLabel, short catchType) {
        this.itsStartLabel = startLabel;
        this.itsEndLabel = endLabel;
        this.itsHandlerLabel = handlerLabel;
        this.itsCatchType = catchType;
    }
}
