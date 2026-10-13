package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/JavaScriptException.class */
public class JavaScriptException extends RhinoException {
    private static final long serialVersionUID = -7666130513694669293L;
    private Object value;

    @Deprecated
    public JavaScriptException(Object value) {
        this(value, "", 0);
    }

    public JavaScriptException(Object value, String sourceName, int lineNumber) {
        recordErrorOrigin(sourceName, lineNumber, null, 0);
        this.value = value;
        if ((value instanceof NativeError) && Context.getContext().hasFeature(10)) {
            NativeError nativeError = (NativeError) value;
            if (!nativeError.has("fileName", nativeError)) {
                nativeError.put("fileName", nativeError, sourceName);
            }
            if (!nativeError.has("lineNumber", nativeError)) {
                nativeError.put("lineNumber", nativeError, Integer.valueOf(lineNumber));
            }
            nativeError.setStackProvider(this);
        }
    }

    @Override // org.mozilla.javascript.RhinoException
    public String details() {
        if (this.value == null) {
            return "null";
        }
        if (this.value instanceof NativeError) {
            return this.value.toString();
        }
        try {
            return ScriptRuntime.toString(this.value);
        } catch (RuntimeException e) {
            if (this.value instanceof Scriptable) {
                return ScriptRuntime.defaultObjectToString((Scriptable) this.value);
            }
            return this.value.toString();
        }
    }

    public Object getValue() {
        return this.value;
    }

    @Deprecated
    public String getSourceName() {
        return sourceName();
    }

    @Deprecated
    public int getLineNumber() {
        return lineNumber();
    }
}
