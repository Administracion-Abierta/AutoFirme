package com.ctc.wstx.exc;

import java.io.IOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/exc/WstxIOException.class */
public class WstxIOException extends WstxException {
    public WstxIOException(IOException ie) {
        super(ie);
    }

    public WstxIOException(String msg) {
        super(msg);
    }
}
