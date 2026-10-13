package com.ctc.wstx.shaded.msv_core.relaxns.verifier;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/relaxns/verifier/ErrorHandlerAdaptor.class */
public class ErrorHandlerAdaptor implements ErrorHandler {
    private final Dispatcher core;

    public ErrorHandlerAdaptor(Dispatcher core) {
        this.core = core;
    }

    @Override // org.xml.sax.ErrorHandler
    public void fatalError(SAXParseException error) throws SAXException {
        this.core.getErrorHandler().fatalError(error);
    }

    @Override // org.xml.sax.ErrorHandler
    public void error(SAXParseException error) throws SAXException {
        this.core.getErrorHandler().error(error);
    }

    @Override // org.xml.sax.ErrorHandler
    public void warning(SAXParseException error) throws SAXException {
        this.core.getErrorHandler().warning(error);
    }
}
