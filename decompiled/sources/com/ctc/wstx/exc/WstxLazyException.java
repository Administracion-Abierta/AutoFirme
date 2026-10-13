package com.ctc.wstx.exc;

import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/exc/WstxLazyException.class */
public class WstxLazyException extends RuntimeException {
    final XMLStreamException mOrig;

    public WstxLazyException(XMLStreamException origEx) {
        super(origEx.getMessage(), origEx);
        this.mOrig = origEx;
    }

    public static void throwLazily(XMLStreamException ex) throws WstxLazyException {
        throw new WstxLazyException(ex);
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return "[" + getClass().getName() + "] " + this.mOrig.getMessage();
    }

    @Override // java.lang.Throwable
    public String toString() {
        return "[" + getClass().getName() + "] " + this.mOrig.toString();
    }
}
