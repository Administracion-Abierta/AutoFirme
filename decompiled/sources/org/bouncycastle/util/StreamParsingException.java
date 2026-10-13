package org.bouncycastle.util;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/StreamParsingException.class */
public class StreamParsingException extends Exception {
    Throwable _e;

    public StreamParsingException(String str, Throwable th) {
        super(str);
        this._e = th;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this._e;
    }
}
