package org.spongycastle.operator;

import java.io.IOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/OperatorStreamException.class */
public class OperatorStreamException extends IOException {
    private Throwable cause;

    public OperatorStreamException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
