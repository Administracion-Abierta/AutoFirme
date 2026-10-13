package com.ctc.wstx.shaded.msv.org_isorelax.verifier.impl;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFilter;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierHandler;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/org_isorelax/verifier/impl/VerifierFilterImpl.class */
public class VerifierFilterImpl extends XMLFilterImpl implements VerifierFilter {
    private final Verifier verifier;
    private final VerifierHandler core;

    public VerifierFilterImpl(Verifier verifier1) throws SAXException {
        this.verifier = verifier1;
        this.core = this.verifier.getVerifierHandler();
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFilter
    public boolean isValid() {
        return this.core.isValid();
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.XMLReader
    public void setErrorHandler(ErrorHandler errorhandler) {
        super.setErrorHandler(errorhandler);
        this.verifier.setErrorHandler(errorhandler);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.XMLReader
    public void setEntityResolver(EntityResolver entityresolver) {
        super.setEntityResolver(entityresolver);
        this.verifier.setEntityResolver(entityresolver);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator locator) {
        this.core.setDocumentLocator(locator);
        super.setDocumentLocator(locator);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void startDocument() throws SAXException {
        this.core.startDocument();
        super.startDocument();
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void endDocument() throws SAXException {
        this.core.endDocument();
        super.endDocument();
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void startPrefixMapping(String s, String s1) throws SAXException {
        this.core.startPrefixMapping(s, s1);
        super.startPrefixMapping(s, s1);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void endPrefixMapping(String s) throws SAXException {
        this.core.endPrefixMapping(s);
        super.endPrefixMapping(s);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void startElement(String s, String s1, String s2, Attributes attributes) throws SAXException {
        this.core.startElement(s, s1, s2, attributes);
        super.startElement(s, s1, s2, attributes);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void endElement(String s, String s1, String s2) throws SAXException {
        this.core.endElement(s, s1, s2);
        super.endElement(s, s1, s2);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void characters(char[] ac, int i, int j) throws SAXException {
        this.core.characters(ac, i, j);
        super.characters(ac, i, j);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] ac, int i, int j) throws SAXException {
        this.core.ignorableWhitespace(ac, i, j);
        super.ignorableWhitespace(ac, i, j);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void processingInstruction(String s, String s1) throws SAXException {
        this.core.processingInstruction(s, s1);
        super.processingInstruction(s, s1);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void skippedEntity(String s) throws SAXException {
        this.core.skippedEntity(s);
        super.skippedEntity(s);
    }
}
