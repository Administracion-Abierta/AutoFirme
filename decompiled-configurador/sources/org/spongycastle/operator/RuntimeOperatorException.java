package org.spongycastle.operator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/RuntimeOperatorException.class */
public class RuntimeOperatorException extends RuntimeException {
    private Throwable cause;

    public RuntimeOperatorException(String msg) {
        super(msg);
    }

    public RuntimeOperatorException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
