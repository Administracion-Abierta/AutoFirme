package com.ctc.wstx.exc;

import javax.xml.stream.Location;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/exc/WstxParsingException.class */
public class WstxParsingException extends WstxException {
    public WstxParsingException(String msg, Location loc) {
        super(msg, loc);
    }

    public WstxParsingException(String msg) {
        super(msg);
    }
}
