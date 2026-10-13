package com.sun.java.help.impl;

import java.util.EventListener;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/ParserListener.class */
public interface ParserListener extends EventListener {
    void tagFound(ParserEvent parserEvent);

    void piFound(ParserEvent parserEvent);

    void doctypeFound(ParserEvent parserEvent);

    void textFound(ParserEvent parserEvent);

    void commentFound(ParserEvent parserEvent);

    void errorFound(ParserEvent parserEvent);
}
