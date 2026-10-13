package com.ctc.wstx.shaded.msv_core.verifier.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/util/IgnoreErrorHandler.class */
public class IgnoreErrorHandler implements ErrorHandler {
    @Override // org.xml.sax.ErrorHandler
    public void fatalError(SAXParseException e) throws SAXParseException {
        throw e;
    }

    @Override // org.xml.sax.ErrorHandler
    public void error(SAXParseException error) {
    }

    @Override // org.xml.sax.ErrorHandler
    public void warning(SAXParseException warning) {
    }
}
