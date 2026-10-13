package com.ctc.wstx.shaded.msv_core.verifier.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/util/ErrorHandlerImpl.class */
public class ErrorHandlerImpl implements ErrorHandler {
    public static final ErrorHandler theInstance = new ErrorHandlerImpl();

    @Override // org.xml.sax.ErrorHandler
    public void fatalError(SAXParseException error) throws SAXParseException {
        throw error;
    }

    @Override // org.xml.sax.ErrorHandler
    public void error(SAXParseException error) throws SAXParseException {
        throw error;
    }

    @Override // org.xml.sax.ErrorHandler
    public void warning(SAXParseException warning) {
    }
}
