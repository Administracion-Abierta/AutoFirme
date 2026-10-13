package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/EcmaError.class */
public class EcmaError extends RhinoException {
    private static final long serialVersionUID = -6261226256957286699L;
    private String errorName;
    private String errorMessage;

    EcmaError(String errorName, String errorMessage, String sourceName, int lineNumber, String lineSource, int columnNumber) {
        recordErrorOrigin(sourceName, lineNumber, lineSource, columnNumber);
        this.errorName = errorName;
        this.errorMessage = errorMessage;
    }

    @Deprecated
    public EcmaError(Scriptable nativeError, String sourceName, int lineNumber, int columnNumber, String lineSource) {
        this("InternalError", ScriptRuntime.toString(nativeError), sourceName, lineNumber, lineSource, columnNumber);
    }

    @Override // org.mozilla.javascript.RhinoException
    public String details() {
        return this.errorName + ": " + this.errorMessage;
    }

    public String getName() {
        return this.errorName;
    }

    public String getErrorMessage() {
        return this.errorMessage;
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

    @Deprecated
    public Scriptable getErrorObject() {
        return null;
    }
}
