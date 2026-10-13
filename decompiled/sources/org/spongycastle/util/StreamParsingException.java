package org.spongycastle.util;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/util/StreamParsingException.class */
public class StreamParsingException extends Exception {
    Throwable _e;

    public StreamParsingException(String message, Throwable e) {
        super(message);
        this._e = e;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this._e;
    }
}
