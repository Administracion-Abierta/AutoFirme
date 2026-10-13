package org.apache.pdfbox.contentstream.operator.state;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/state/EmptyGraphicsStackException.class */
public final class EmptyGraphicsStackException extends IOException {
    private static final long serialVersionUID = 1;

    EmptyGraphicsStackException() {
        super("Cannot execute restore, the graphics stack is empty");
    }
}
