package com.ctc.wstx.shaded.msv.org_jp_gr_xml.sax;

import com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.DOMVisitorException;
import com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.UDOMVisitor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/org_jp_gr_xml/sax/DOMSAXProducer.class */
public class DOMSAXProducer {
    private boolean needDocumentEmulation_ = true;
    private Node root_;
    private String systemID_;
    private String publicID_;
    private DTDHandler dtd_;
    private ContentHandler content_;
    private DeclHandler decl_;
    private LexicalHandler lexical_;
    private ErrorHandler error_;

    public DOMSAXProducer(Node node) {
        this.root_ = node;
    }

    public void setDocumentEmulation(boolean flag) {
        this.needDocumentEmulation_ = flag;
    }

    public void setDTDHandler(DTDHandler dtdhandler) {
        this.dtd_ = dtdhandler;
    }

    public void setContentHandler(ContentHandler contenthandler) {
        this.content_ = contenthandler;
    }

    public void setLexicalHandler(LexicalHandler lexicalhandler) {
        this.lexical_ = lexicalhandler;
    }

    public void setDeclHandler(DeclHandler declhandler) {
        this.decl_ = declhandler;
    }

    public void setErrorHandler(ErrorHandler errorhandler) {
        this.error_ = errorhandler;
    }

    public void makeEvent() throws SAXException {
        try {
            DOMSAXProducerVisitor domsaxproducervisitor = new DOMSAXProducerVisitor();
            domsaxproducervisitor.setSystemID(this.systemID_);
            domsaxproducervisitor.setPublicID(this.publicID_);
            domsaxproducervisitor.setDTDHandler(this.dtd_);
            domsaxproducervisitor.setContentHandler(this.content_);
            domsaxproducervisitor.setLexicalHandler(this.lexical_);
            domsaxproducervisitor.setDeclHandler(this.decl_);
            domsaxproducervisitor.setErrorHandler(this.error_);
            if (!(this.root_ instanceof Document) && this.needDocumentEmulation_) {
                domsaxproducervisitor.emulateStartDocument();
                UDOMVisitor.traverse(this.root_, domsaxproducervisitor);
                domsaxproducervisitor.emulateEndDocument();
            } else {
                UDOMVisitor.traverse(this.root_, domsaxproducervisitor);
            }
        } catch (DOMVisitorException domvisitorexception) {
            Exception exception = domvisitorexception.getCauseException();
            if (exception == null) {
                throw new SAXException(domvisitorexception.getMessage());
            }
            if (exception instanceof SAXException) {
                throw ((SAXException) exception);
            }
            throw new SAXException(domvisitorexception.getMessage());
        }
    }

    public void makeEvent(ContentHandler contenthandler) throws SAXException {
        setContentHandler(contenthandler);
        makeEvent();
    }
}
