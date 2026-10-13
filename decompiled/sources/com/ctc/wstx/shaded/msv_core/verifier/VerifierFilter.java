package com.ctc.wstx.shaded.msv_core.verifier;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/VerifierFilter.class */
public class VerifierFilter extends XMLFilterImpl implements IVerifier {
    private final IVerifier verifier;

    public VerifierFilter(IVerifier verifier) {
        this.verifier = verifier;
    }

    public VerifierFilter(DocumentDeclaration documentDecl, ErrorHandler errorHandler) {
        this(new Verifier(documentDecl, errorHandler));
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.IVerifier, com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierHandler
    public boolean isValid() {
        return this.verifier.isValid();
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.IVerifier
    public Object getCurrentElementType() {
        return this.verifier.getCurrentElementType();
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.IVerifier
    public Datatype[] getLastCharacterType() {
        return this.verifier.getLastCharacterType();
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.IVerifier
    public final Locator getLocator() {
        return this.verifier.getLocator();
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.XMLReader, com.ctc.wstx.shaded.msv_core.verifier.IVerifier
    public final ErrorHandler getErrorHandler() {
        return this.verifier.getErrorHandler();
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.XMLReader, com.ctc.wstx.shaded.msv_core.verifier.IVerifier
    public final void setErrorHandler(ErrorHandler handler) {
        super.setErrorHandler(handler);
        this.verifier.setErrorHandler(handler);
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.IVerifier
    public final void setPanicMode(boolean usePanicMode) {
        this.verifier.setPanicMode(usePanicMode);
    }

    public IVerifier getVerifier() {
        return this.verifier;
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator locator) {
        this.verifier.setDocumentLocator(locator);
        super.setDocumentLocator(locator);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void startDocument() throws SAXException {
        this.verifier.startDocument();
        super.startDocument();
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void endDocument() throws SAXException {
        this.verifier.endDocument();
        super.endDocument();
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.verifier.startPrefixMapping(prefix, uri);
        super.startPrefixMapping(prefix, uri);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void endPrefixMapping(String prefix) throws SAXException {
        this.verifier.endPrefixMapping(prefix);
        super.endPrefixMapping(prefix);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        this.verifier.startElement(namespaceURI, localName, qName, atts);
        super.startElement(namespaceURI, localName, qName, atts);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        this.verifier.endElement(namespaceURI, localName, qName);
        super.endElement(namespaceURI, localName, qName);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.verifier.characters(ch, start, length);
        super.characters(ch, start, length);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.verifier.ignorableWhitespace(ch, start, length);
        super.ignorableWhitespace(ch, start, length);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void processingInstruction(String target, String data) throws SAXException {
        this.verifier.processingInstruction(target, data);
        super.processingInstruction(target, data);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void skippedEntity(String name) throws SAXException {
        this.verifier.skippedEntity(name);
        super.skippedEntity(name);
    }
}
