package com.ctc.wstx.shaded.msv_core.writer;

import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributeListImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/writer/XMLWriter.class */
public class XMLWriter {
    protected DocumentHandler handler;

    public void setDocumentHandler(DocumentHandler handler) {
        this.handler = handler;
    }

    public DocumentHandler getDocumentHandler() {
        return this.handler;
    }

    public void element(String name) {
        element(name, new String[0]);
    }

    public void element(String name, String[] attributes) {
        start(name, attributes);
        end(name);
    }

    public void start(String name) {
        start(name, new String[0]);
    }

    public void start(String name, String[] attributes) {
        AttributeListImpl as = new AttributeListImpl();
        for (int i = 0; i < attributes.length; i += 2) {
            as.addAttribute(attributes[i], "", attributes[i + 1]);
        }
        try {
            this.handler.startElement(name, as);
        } catch (SAXException e) {
            throw new SAXRuntimeException(e);
        }
    }

    public void end(String name) {
        try {
            this.handler.endElement(name);
        } catch (SAXException e) {
            throw new SAXRuntimeException(e);
        }
    }

    public void characters(String str) {
        try {
            this.handler.characters(str.toCharArray(), 0, str.length());
        } catch (SAXException e) {
            throw new SAXRuntimeException(e);
        }
    }
}
