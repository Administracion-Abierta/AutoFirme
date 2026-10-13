package com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.impl;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider;
import java.util.Enumeration;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.NamespaceSupport;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv/org_isorelax/dispatcher/impl/DispatcherImpl.class */
public class DispatcherImpl implements Dispatcher {
    protected ErrorHandler errorHandler;
    private IslandVerifier currentHandler;
    protected final SchemaProvider schema;
    private int depth = 1;
    protected Locator documentLocator = null;
    protected final NamespaceSupport nsMap = new NamespaceSupport();
    protected Context contextStack = null;
    protected final Vector unparsedEntityDecls = new Vector();
    protected final Vector notationDecls = new Vector();
    protected Transponder transponder = new Transponder();

    static /* synthetic */ int access$208(DispatcherImpl x0) {
        int i = x0.depth;
        x0.depth = i + 1;
        return i;
    }

    static /* synthetic */ int access$206(DispatcherImpl x0) {
        int i = x0.depth - 1;
        x0.depth = i;
        return i;
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher
    public SchemaProvider getSchemaProvider() {
        return this.schema;
    }

    public DispatcherImpl(SchemaProvider schema) {
        this.currentHandler = null;
        this.schema = schema;
        this.currentHandler = schema.createTopLevelVerifier();
        this.currentHandler.setDispatcher(this);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv/org_isorelax/dispatcher/impl/DispatcherImpl$Context.class */
    protected static final class Context {
        public final IslandVerifier handler;
        public final int depth;
        public final Context previous;

        public Context(IslandVerifier handler, int depth, Context previous) {
            this.handler = handler;
            this.depth = depth;
            this.previous = previous;
        }
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher
    public void attachXMLReader(XMLReader reader) {
        reader.setContentHandler(this.transponder);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher
    public void switchVerifier(IslandVerifier newVerifier) throws SAXException {
        this.contextStack = new Context(this.currentHandler, this.depth, this.contextStack);
        this.currentHandler = newVerifier;
        this.currentHandler.setDispatcher(this);
        this.currentHandler.setDocumentLocator(this.documentLocator);
        this.depth = 0;
        Enumeration e = this.nsMap.getDeclaredPrefixes();
        while (e.hasMoreElements()) {
            String prefix = (String) e.nextElement();
            this.currentHandler.startPrefixMapping(prefix, this.nsMap.getURI(prefix));
        }
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher
    public void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler;
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher
    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher
    public int countUnparsedEntityDecls() {
        return this.unparsedEntityDecls.size();
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher
    public Dispatcher.UnparsedEntityDecl getUnparsedEntityDecl(int index) {
        return (Dispatcher.UnparsedEntityDecl) this.unparsedEntityDecls.get(index);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher
    public int countNotationDecls() {
        return this.notationDecls.size();
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher
    public Dispatcher.NotationDecl getNotationDecl(int index) {
        return (Dispatcher.NotationDecl) this.notationDecls.get(index);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv/org_isorelax/dispatcher/impl/DispatcherImpl$Transponder.class */
    private class Transponder implements ContentHandler, DTDHandler {
        private Transponder() {
        }

        @Override // org.xml.sax.DTDHandler
        public void unparsedEntityDecl(String name, String systemId, String publicId, String notation) {
            DispatcherImpl.this.unparsedEntityDecls.add(new Dispatcher.UnparsedEntityDecl(name, systemId, publicId, notation));
        }

        @Override // org.xml.sax.DTDHandler
        public void notationDecl(String name, String systemId, String publicId) {
            DispatcherImpl.this.notationDecls.add(new Dispatcher.NotationDecl(name, systemId, publicId));
        }

        @Override // org.xml.sax.ContentHandler
        public void setDocumentLocator(Locator locator) {
            DispatcherImpl.this.documentLocator = locator;
            DispatcherImpl.this.currentHandler.setDocumentLocator(locator);
        }

        @Override // org.xml.sax.ContentHandler
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            DispatcherImpl.this.currentHandler.startElement(uri, localName, qName, attributes);
            DispatcherImpl.access$208(DispatcherImpl.this);
            DispatcherImpl.this.nsMap.pushContext();
        }

        @Override // org.xml.sax.ContentHandler
        public void endElement(String uri, String localName, String qName) throws SAXException {
            DispatcherImpl.this.nsMap.popContext();
            DispatcherImpl.this.currentHandler.endElement(uri, localName, qName);
            if (DispatcherImpl.access$206(DispatcherImpl.this) == 0) {
                Enumeration e = DispatcherImpl.this.nsMap.getDeclaredPrefixes();
                while (e.hasMoreElements()) {
                    DispatcherImpl.this.currentHandler.endPrefixMapping((String) e.nextElement());
                }
                ElementDecl[] results = DispatcherImpl.this.currentHandler.endIsland();
                DispatcherImpl.this.depth = DispatcherImpl.this.contextStack.depth;
                DispatcherImpl.this.currentHandler = DispatcherImpl.this.contextStack.handler;
                DispatcherImpl.this.contextStack = DispatcherImpl.this.contextStack.previous;
                DispatcherImpl.this.currentHandler.endChildIsland(uri, results);
            }
        }

        @Override // org.xml.sax.ContentHandler
        public void characters(char[] ch, int start, int length) throws SAXException {
            DispatcherImpl.this.currentHandler.characters(ch, start, length);
        }

        @Override // org.xml.sax.ContentHandler
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            DispatcherImpl.this.currentHandler.ignorableWhitespace(ch, start, length);
        }

        @Override // org.xml.sax.ContentHandler
        public void processingInstruction(String target, String data) throws SAXException {
            DispatcherImpl.this.currentHandler.processingInstruction(target, data);
        }

        @Override // org.xml.sax.ContentHandler
        public void skippedEntity(String name) throws SAXException {
            DispatcherImpl.this.currentHandler.skippedEntity(name);
        }

        @Override // org.xml.sax.ContentHandler
        public void startDocument() {
        }

        @Override // org.xml.sax.ContentHandler
        public void endDocument() {
        }

        @Override // org.xml.sax.ContentHandler
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            DispatcherImpl.this.nsMap.declarePrefix(prefix, uri);
            DispatcherImpl.this.currentHandler.startPrefixMapping(prefix, uri);
        }

        @Override // org.xml.sax.ContentHandler
        public void endPrefixMapping(String prefix) throws SAXException {
            DispatcherImpl.this.currentHandler.endPrefixMapping(prefix);
        }
    }
}
