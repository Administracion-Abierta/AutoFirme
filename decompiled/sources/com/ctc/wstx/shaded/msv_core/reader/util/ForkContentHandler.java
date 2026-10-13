package com.ctc.wstx.shaded.msv_core.reader.util;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/util/ForkContentHandler.class */
public class ForkContentHandler implements ContentHandler {
    protected ContentHandler lhs;
    protected ContentHandler rhs;

    public ForkContentHandler(ContentHandler lhs, ContentHandler rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override // org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator locator) {
        this.lhs.setDocumentLocator(locator);
        this.rhs.setDocumentLocator(locator);
    }

    @Override // org.xml.sax.ContentHandler
    public void startDocument() throws SAXException {
        this.lhs.startDocument();
        this.rhs.startDocument();
    }

    @Override // org.xml.sax.ContentHandler
    public void endDocument() throws SAXException {
        this.lhs.endDocument();
        this.rhs.endDocument();
    }

    @Override // org.xml.sax.ContentHandler
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.lhs.startPrefixMapping(prefix, uri);
        this.rhs.startPrefixMapping(prefix, uri);
    }

    @Override // org.xml.sax.ContentHandler
    public void endPrefixMapping(String prefix) throws SAXException {
        this.lhs.endPrefixMapping(prefix);
        this.rhs.endPrefixMapping(prefix);
    }

    @Override // org.xml.sax.ContentHandler
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        this.lhs.startElement(uri, localName, qName, attributes);
        this.rhs.startElement(uri, localName, qName, attributes);
    }

    @Override // org.xml.sax.ContentHandler
    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.lhs.endElement(uri, localName, qName);
        this.rhs.endElement(uri, localName, qName);
    }

    @Override // org.xml.sax.ContentHandler
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.lhs.characters(ch, start, length);
        this.rhs.characters(ch, start, length);
    }

    @Override // org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.lhs.ignorableWhitespace(ch, start, length);
        this.rhs.ignorableWhitespace(ch, start, length);
    }

    @Override // org.xml.sax.ContentHandler
    public void processingInstruction(String target, String data) throws SAXException {
        this.lhs.processingInstruction(target, data);
        this.rhs.processingInstruction(target, data);
    }

    @Override // org.xml.sax.ContentHandler
    public void skippedEntity(String name) throws SAXException {
        this.lhs.skippedEntity(name);
        this.rhs.skippedEntity(name);
    }
}
