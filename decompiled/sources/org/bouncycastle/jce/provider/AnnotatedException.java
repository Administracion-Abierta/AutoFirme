package org.bouncycastle.jce.provider;

import org.bouncycastle.jce.exception.ExtException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/provider/AnnotatedException.class */
public class AnnotatedException extends Exception implements ExtException {
    private Throwable _underlyingException;

    public AnnotatedException(String str, Throwable th) {
        super(str);
        this._underlyingException = th;
    }

    public AnnotatedException(String str) {
        this(str, null);
    }

    Throwable getUnderlyingException() {
        return this._underlyingException;
    }

    @Override // java.lang.Throwable, org.bouncycastle.jce.exception.ExtException
    public Throwable getCause() {
        return this._underlyingException;
    }
}
