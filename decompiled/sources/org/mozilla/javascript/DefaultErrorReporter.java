package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/DefaultErrorReporter.class */
class DefaultErrorReporter implements ErrorReporter {
    static final DefaultErrorReporter instance = new DefaultErrorReporter();
    private boolean forEval;
    private ErrorReporter chainedReporter;

    private DefaultErrorReporter() {
    }

    static ErrorReporter forEval(ErrorReporter reporter) {
        DefaultErrorReporter r = new DefaultErrorReporter();
        r.forEval = true;
        r.chainedReporter = reporter;
        return r;
    }

    @Override // org.mozilla.javascript.ErrorReporter
    public void warning(String message, String sourceURI, int line, String lineText, int lineOffset) {
        if (this.chainedReporter != null) {
            this.chainedReporter.warning(message, sourceURI, line, lineText, lineOffset);
        }
    }

    @Override // org.mozilla.javascript.ErrorReporter
    public void error(String message, String sourceURI, int line, String lineText, int lineOffset) {
        if (this.forEval) {
            String error = "SyntaxError";
            if (message.startsWith("TypeError: ")) {
                error = "TypeError";
                message = message.substring("TypeError: ".length());
            }
            throw ScriptRuntime.constructError(error, message, sourceURI, line, lineText, lineOffset);
        }
        if (this.chainedReporter != null) {
            this.chainedReporter.error(message, sourceURI, line, lineText, lineOffset);
            return;
        }
        throw runtimeError(message, sourceURI, line, lineText, lineOffset);
    }

    @Override // org.mozilla.javascript.ErrorReporter
    public EvaluatorException runtimeError(String message, String sourceURI, int line, String lineText, int lineOffset) {
        if (this.chainedReporter != null) {
            return this.chainedReporter.runtimeError(message, sourceURI, line, lineText, lineOffset);
        }
        return new EvaluatorException(message, sourceURI, line, lineText, lineOffset);
    }
}
