package org.slf4j.impl;

import java.io.PrintStream;

/* JADX INFO: loaded from: autofirma.jar:org/slf4j/impl/OutputChoice.class */
class OutputChoice {
    final OutputChoiceType outputChoiceType;
    final PrintStream targetPrintStream;

    /* JADX INFO: loaded from: autofirma.jar:org/slf4j/impl/OutputChoice$OutputChoiceType.class */
    enum OutputChoiceType {
        SYS_OUT,
        CACHED_SYS_OUT,
        SYS_ERR,
        CACHED_SYS_ERR,
        FILE
    }

    OutputChoice(OutputChoiceType outputChoiceType) {
        if (outputChoiceType == OutputChoiceType.FILE) {
            throw new IllegalArgumentException();
        }
        this.outputChoiceType = outputChoiceType;
        if (outputChoiceType == OutputChoiceType.CACHED_SYS_OUT) {
            this.targetPrintStream = System.out;
        } else if (outputChoiceType == OutputChoiceType.CACHED_SYS_ERR) {
            this.targetPrintStream = System.err;
        } else {
            this.targetPrintStream = null;
        }
    }

    OutputChoice(PrintStream printStream) {
        this.outputChoiceType = OutputChoiceType.FILE;
        this.targetPrintStream = printStream;
    }

    PrintStream getTargetPrintStream() {
        switch (this.outputChoiceType) {
            case SYS_OUT:
                return System.out;
            case SYS_ERR:
                return System.err;
            case CACHED_SYS_ERR:
            case CACHED_SYS_OUT:
            case FILE:
                return this.targetPrintStream;
            default:
                throw new IllegalArgumentException();
        }
    }
}
