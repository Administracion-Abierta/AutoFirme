package com.ctc.wstx.shaded.msv_core.reader;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/Controller.class */
public class Controller implements GrammarReaderController2, ErrorHandler {
    private final GrammarReaderController core;
    private boolean _hadError = false;

    public GrammarReaderController getCore() {
        return this.core;
    }

    public boolean hadError() {
        return this._hadError;
    }

    public final void setErrorFlag() {
        this._hadError = true;
    }

    public Controller(GrammarReaderController _core) {
        this.core = _core;
    }

    @Override // org.xml.sax.EntityResolver
    @Deprecated
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return this.core.resolveEntity(publicId, systemId);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController
    public void warning(Locator[] locs, String errorMessage) {
        this.core.warning(locs, errorMessage);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController
    public void error(Locator[] locs, String errorMessage, Exception nestedException) {
        setErrorFlag();
        this.core.error(locs, errorMessage, nestedException);
    }

    public void error(String errorMessage, Exception nestedException) {
        error(new Locator[0], errorMessage, nestedException);
    }

    @Override // org.xml.sax.ErrorHandler
    public void fatalError(SAXParseException spe) {
        error(spe);
    }

    @Override // org.xml.sax.ErrorHandler
    public void error(SAXParseException spe) {
        error(getLocator(spe), spe.getMessage(), spe.getException());
    }

    @Override // org.xml.sax.ErrorHandler
    public void warning(SAXParseException spe) {
        warning(getLocator(spe), spe.getMessage());
    }

    public void error(IOException e, Locator source) {
        error(new Locator[]{source}, e.getMessage(), e);
    }

    public void error(SAXException e, Locator source) {
        if (e.getException() instanceof RuntimeException) {
            throw ((RuntimeException) e.getException());
        }
        if (e instanceof SAXParseException) {
            error((SAXParseException) e);
        } else {
            error(new Locator[]{source}, e.getMessage(), e);
        }
    }

    public void error(ParserConfigurationException e, Locator source) {
        error(new Locator[]{source}, e.getMessage(), e);
    }

    protected Locator[] getLocator(SAXParseException spe) {
        LocatorImpl loc = new LocatorImpl();
        loc.setColumnNumber(spe.getColumnNumber());
        loc.setLineNumber(spe.getLineNumber());
        loc.setSystemId(spe.getSystemId());
        loc.setPublicId(spe.getPublicId());
        return new Locator[]{loc};
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController2
    public LSResourceResolver getLSResourceResolver() {
        if (this.core instanceof GrammarReaderController2) {
            return ((GrammarReaderController2) this.core).getLSResourceResolver();
        }
        return null;
    }
}
