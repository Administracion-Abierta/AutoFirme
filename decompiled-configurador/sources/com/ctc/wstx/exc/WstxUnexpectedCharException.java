package com.ctc.wstx.exc;

import javax.xml.stream.Location;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/exc/WstxUnexpectedCharException.class */
public class WstxUnexpectedCharException extends WstxParsingException {
    final char mChar;

    public WstxUnexpectedCharException(String msg, Location loc, char c) {
        super(msg, loc);
        this.mChar = c;
    }

    public char getChar() {
        return this.mChar;
    }
}
