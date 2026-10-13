package com.ctc.wstx.shaded.msv_core.util.xml;

import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/util/xml/DocumentFilter.class */
public class DocumentFilter implements DocumentHandler {
    public DocumentHandler next;

    public DocumentFilter(DocumentHandler next) {
        this.next = next;
    }

    @Override // org.xml.sax.DocumentHandler
    public void startDocument() throws SAXException {
        this.next.startDocument();
    }

    @Override // org.xml.sax.DocumentHandler
    public void endDocument() throws SAXException {
        this.next.endDocument();
    }

    @Override // org.xml.sax.DocumentHandler
    public void startElement(String name, AttributeList atts) throws SAXException {
        this.next.startElement(name, atts);
    }

    @Override // org.xml.sax.DocumentHandler
    public void endElement(String name) throws SAXException {
        this.next.endElement(name);
    }

    @Override // org.xml.sax.DocumentHandler
    public void characters(char[] buf, int start, int len) throws SAXException {
        this.next.characters(buf, start, len);
    }

    @Override // org.xml.sax.DocumentHandler
    public void ignorableWhitespace(char[] buf, int start, int len) throws SAXException {
        this.next.ignorableWhitespace(buf, start, len);
    }

    @Override // org.xml.sax.DocumentHandler
    public void processingInstruction(String target, String data) throws SAXException {
        this.next.processingInstruction(target, data);
    }

    @Override // org.xml.sax.DocumentHandler
    public void setDocumentLocator(Locator loc) {
        this.next.setDocumentLocator(loc);
    }
}
