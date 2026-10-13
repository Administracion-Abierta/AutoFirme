package com.ctc.wstx.exc;

import javax.xml.stream.Location;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/exc/WstxEOFException.class */
public class WstxEOFException extends WstxParsingException {
    public WstxEOFException(String msg, Location loc) {
        super(msg, loc);
    }
}
