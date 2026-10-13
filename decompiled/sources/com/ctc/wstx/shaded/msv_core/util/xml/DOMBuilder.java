package com.ctc.wstx.shaded.msv_core.util.xml;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/util/xml/DOMBuilder.class */
public class DOMBuilder extends DefaultHandler {
    private final Document dom;
    private Node parent;

    public DOMBuilder(Document document) {
        this.dom = document;
        this.parent = this.dom;
    }

    public DOMBuilder() throws ParserConfigurationException {
        this(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
    }

    public Document getDocument() {
        return this.dom;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startElement(String ns, String local, String qname, Attributes atts) {
        Element e = this.dom.createElementNS(ns, qname);
        this.parent.appendChild(e);
        this.parent = e;
        for (int i = 0; i < atts.getLength(); i++) {
            e.setAttributeNS(atts.getURI(i), atts.getQName(i), atts.getValue(i));
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endElement(String ns, String local, String qname) {
        this.parent = this.parent.getParentNode();
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void characters(char[] buf, int start, int len) {
        this.parent.appendChild(this.dom.createTextNode(new String(buf, start, len)));
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] buf, int start, int len) {
        this.parent.appendChild(this.dom.createTextNode(new String(buf, start, len)));
    }
}
