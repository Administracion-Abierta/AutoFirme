package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/EvaluatorException.class */
public class EvaluatorException extends RhinoException {
    private static final long serialVersionUID = -8743165779676009808L;

    public EvaluatorException(String detail) {
        super(detail);
    }

    public EvaluatorException(String detail, String sourceName, int lineNumber) {
        this(detail, sourceName, lineNumber, null, 0);
    }

    public EvaluatorException(String detail, String sourceName, int lineNumber, String lineSource, int columnNumber) {
        super(detail);
        recordErrorOrigin(sourceName, lineNumber, lineSource, columnNumber);
    }

    @Deprecated
    public String getSourceName() {
        return sourceName();
    }

    @Deprecated
    public int getLineNumber() {
        return lineNumber();
    }

    @Deprecated
    public int getColumnNumber() {
        return columnNumber();
    }

    @Deprecated
    public String getLineSource() {
        return lineSource();
    }
}
