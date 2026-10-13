package org.mozilla.javascript;

import java.io.Serializable;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ScriptStackElement.class */
public final class ScriptStackElement implements Serializable {
    private static final long serialVersionUID = -6416688260860477449L;
    public final String fileName;
    public final String functionName;
    public final int lineNumber;

    public ScriptStackElement(String fileName, String functionName, int lineNumber) {
        this.fileName = fileName;
        this.functionName = functionName;
        this.lineNumber = lineNumber;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        renderMozillaStyle(sb);
        return sb.toString();
    }

    public void renderJavaStyle(StringBuilder sb) {
        sb.append("\tat ").append(this.fileName);
        if (this.lineNumber > -1) {
            sb.append(':').append(this.lineNumber);
        }
        if (this.functionName != null) {
            sb.append(" (").append(this.functionName).append(')');
        }
    }

    public void renderMozillaStyle(StringBuilder sb) {
        if (this.functionName != null) {
            sb.append(this.functionName).append("()");
        }
        sb.append('@').append(this.fileName);
        if (this.lineNumber > -1) {
            sb.append(':').append(this.lineNumber);
        }
    }

    public void renderV8Style(StringBuilder sb) {
        sb.append("    at ");
        if (this.functionName == null || "anonymous".equals(this.functionName) || "undefined".equals(this.functionName)) {
            appendV8Location(sb);
            return;
        }
        sb.append(this.functionName).append(" (");
        appendV8Location(sb);
        sb.append(')');
    }

    private void appendV8Location(StringBuilder sb) {
        sb.append(this.fileName).append(':');
        sb.append(this.lineNumber > -1 ? this.lineNumber : 0).append(":0");
    }
}
