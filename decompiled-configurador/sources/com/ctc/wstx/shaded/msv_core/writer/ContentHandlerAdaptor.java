package com.ctc.wstx.shaded.msv_core.writer;

import java.util.Enumeration;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/writer/ContentHandlerAdaptor.class */
public class ContentHandlerAdaptor implements DocumentHandler {
    private final ContentHandler contentHandler;
    private final NamespaceSupport nsSupport = new NamespaceSupport();
    private final AttributeListAdapter attAdapter = new AttributeListAdapter();
    private final AttributesImpl atts = new AttributesImpl();
    private final boolean namespaces = true;
    private final boolean prefixes = false;
    private final String[] nameParts = new String[3];

    public ContentHandlerAdaptor(ContentHandler handler) {
        this.contentHandler = handler;
    }

    @Override // org.xml.sax.DocumentHandler
    public void setDocumentLocator(Locator locator) {
        if (this.contentHandler != null) {
            this.contentHandler.setDocumentLocator(locator);
        }
    }

    @Override // org.xml.sax.DocumentHandler
    public void startDocument() throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.startDocument();
        }
    }

    @Override // org.xml.sax.DocumentHandler
    public void endDocument() throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.endDocument();
        }
    }

    @Override // org.xml.sax.DocumentHandler
    public void startElement(String qName, AttributeList qAtts) throws SAXException {
        String prefix;
        this.nsSupport.pushContext();
        boolean seenDecl = false;
        this.atts.clear();
        int length = qAtts.getLength();
        for (int i = 0; i < length; i++) {
            String attQName = qAtts.getName(i);
            String type = qAtts.getType(i);
            String value = qAtts.getValue(i);
            if (attQName.startsWith(CanonicalizerBase.XMLNS)) {
                int n = attQName.indexOf(58);
                if (n == -1) {
                    prefix = "";
                } else {
                    prefix = attQName.substring(n + 1);
                }
                if (!this.nsSupport.declarePrefix(prefix, value)) {
                    reportError("Illegal Namespace prefix: " + prefix);
                }
                if (this.contentHandler != null) {
                    this.contentHandler.startPrefixMapping(prefix, value);
                }
                seenDecl = true;
            } else {
                String[] attName = processName(attQName, true);
                this.atts.addAttribute(attName[0], attName[1], attName[2], type, value);
            }
        }
        if (seenDecl) {
            int length2 = this.atts.getLength();
            for (int i2 = 0; i2 < length2; i2++) {
                String attQName2 = this.atts.getQName(i2);
                if (!attQName2.startsWith(CanonicalizerBase.XMLNS)) {
                    String[] attName2 = processName(attQName2, true);
                    this.atts.setURI(i2, attName2[0]);
                    this.atts.setLocalName(i2, attName2[1]);
                }
            }
        }
        if (this.contentHandler != null) {
            String[] name = processName(qName, false);
            this.contentHandler.startElement(name[0], name[1], name[2], this.atts);
        }
    }

    @Override // org.xml.sax.DocumentHandler
    public void endElement(String qName) throws SAXException {
        String[] names = processName(qName, false);
        if (this.contentHandler != null) {
            this.contentHandler.endElement(names[0], names[1], names[2]);
            Enumeration prefixes = this.nsSupport.getDeclaredPrefixes();
            while (prefixes.hasMoreElements()) {
                String prefix = (String) prefixes.nextElement();
                this.contentHandler.endPrefixMapping(prefix);
            }
        }
        this.nsSupport.popContext();
    }

    @Override // org.xml.sax.DocumentHandler
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.characters(ch, start, length);
        }
    }

    @Override // org.xml.sax.DocumentHandler
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.ignorableWhitespace(ch, start, length);
        }
    }

    @Override // org.xml.sax.DocumentHandler
    public void processingInstruction(String target, String data) throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.processingInstruction(target, data);
        }
    }

    private String[] processName(String qName, boolean isAttribute) throws SAXException {
        String[] parts = this.nsSupport.processName(qName, this.nameParts, isAttribute);
        if (parts == null) {
            parts = new String[3];
            parts[2] = qName.intern();
            reportError("Undeclared prefix: " + qName);
        }
        return parts;
    }

    void reportError(String message) throws SAXException {
        throw new SAXParseException(message, null, null, -1, -1);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/writer/ContentHandlerAdaptor$AttributeListAdapter.class */
    final class AttributeListAdapter implements Attributes {
        private AttributeList qAtts;

        AttributeListAdapter() {
        }

        void setAttributeList(AttributeList qAtts) {
            this.qAtts = qAtts;
        }

        @Override // org.xml.sax.Attributes
        public int getLength() {
            return this.qAtts.getLength();
        }

        @Override // org.xml.sax.Attributes
        public String getURI(int i) {
            return "";
        }

        @Override // org.xml.sax.Attributes
        public String getLocalName(int i) {
            return "";
        }

        @Override // org.xml.sax.Attributes
        public String getQName(int i) {
            return this.qAtts.getName(i).intern();
        }

        @Override // org.xml.sax.Attributes
        public String getType(int i) {
            return this.qAtts.getType(i).intern();
        }

        @Override // org.xml.sax.Attributes
        public String getValue(int i) {
            return this.qAtts.getValue(i);
        }

        @Override // org.xml.sax.Attributes
        public int getIndex(String uri, String localName) {
            return -1;
        }

        @Override // org.xml.sax.Attributes
        public int getIndex(String qName) {
            int max = ContentHandlerAdaptor.this.atts.getLength();
            for (int i = 0; i < max; i++) {
                if (this.qAtts.getName(i).equals(qName)) {
                    return i;
                }
            }
            return -1;
        }

        @Override // org.xml.sax.Attributes
        public String getType(String uri, String localName) {
            return null;
        }

        @Override // org.xml.sax.Attributes
        public String getType(String qName) {
            return this.qAtts.getType(qName).intern();
        }

        @Override // org.xml.sax.Attributes
        public String getValue(String uri, String localName) {
            return null;
        }

        @Override // org.xml.sax.Attributes
        public String getValue(String qName) {
            return this.qAtts.getValue(qName);
        }
    }
}
