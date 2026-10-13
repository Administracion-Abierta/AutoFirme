package com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv/org_jp_gr_xml/dom/IDOMVisitor.class */
public interface IDOMVisitor {
    boolean enter(Element element) throws DOMVisitorException;

    boolean enter(Attr attr) throws DOMVisitorException;

    boolean enter(Text text) throws DOMVisitorException;

    boolean enter(CDATASection cDATASection) throws DOMVisitorException;

    boolean enter(EntityReference entityReference) throws DOMVisitorException;

    boolean enter(Entity entity) throws DOMVisitorException;

    boolean enter(ProcessingInstruction processingInstruction) throws DOMVisitorException;

    boolean enter(Comment comment) throws DOMVisitorException;

    boolean enter(Document document) throws DOMVisitorException;

    boolean enter(DocumentType documentType) throws DOMVisitorException;

    boolean enter(DocumentFragment documentFragment) throws DOMVisitorException;

    boolean enter(Notation notation) throws DOMVisitorException;

    boolean enter(Node node) throws DOMVisitorException;

    void leave(Element element) throws DOMVisitorException;

    void leave(Attr attr) throws DOMVisitorException;

    void leave(Text text) throws DOMVisitorException;

    void leave(CDATASection cDATASection) throws DOMVisitorException;

    void leave(EntityReference entityReference) throws DOMVisitorException;

    void leave(Entity entity) throws DOMVisitorException;

    void leave(ProcessingInstruction processingInstruction) throws DOMVisitorException;

    void leave(Comment comment) throws DOMVisitorException;

    void leave(Document document) throws DOMVisitorException;

    void leave(DocumentType documentType) throws DOMVisitorException;

    void leave(DocumentFragment documentFragment) throws DOMVisitorException;

    void leave(Notation notation) throws DOMVisitorException;

    void leave(Node node) throws DOMVisitorException;
}
