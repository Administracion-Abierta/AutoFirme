package org.spongycastle.jce.provider;

import org.spongycastle.jce.exception.ExtException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/provider/AnnotatedException.class */
public class AnnotatedException extends Exception implements ExtException {
    private Throwable _underlyingException;

    public AnnotatedException(String string, Throwable e) {
        super(string);
        this._underlyingException = e;
    }

    public AnnotatedException(String string) {
        this(string, null);
    }

    Throwable getUnderlyingException() {
        return this._underlyingException;
    }

    @Override // java.lang.Throwable, org.spongycastle.jce.exception.ExtException
    public Throwable getCause() {
        return this._underlyingException;
    }
}
