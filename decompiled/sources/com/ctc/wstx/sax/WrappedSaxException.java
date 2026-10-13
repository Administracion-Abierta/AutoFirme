package com.ctc.wstx.sax;

import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sax/WrappedSaxException.class */
public final class WrappedSaxException extends RuntimeException {
    final SAXException mCause;

    public WrappedSaxException(SAXException cause) {
        this.mCause = cause;
    }

    public SAXException getSaxException() {
        return this.mCause;
    }

    @Override // java.lang.Throwable
    public String toString() {
        return this.mCause.toString();
    }
}
