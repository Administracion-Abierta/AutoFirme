package com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom;

import com.ctc.wstx.shaded.msv.org_jp_gr_xml.xml.UXML;
import es.gob.afirma.keystores.filters.PolicyIdFilter;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/org_jp_gr_xml/dom/XMLMaker.class */
public class XMLMaker implements IDOMVisitor {
    protected String encoding_ = "UTF-8";
    protected boolean dom2_ = false;
    protected boolean expandEntityReference_ = false;
    protected boolean emptyElementTag_ = false;
    protected StringBuffer buffer_ = new StringBuffer();

    public void setEncoding(String s) {
        this.encoding_ = s;
    }

    public void setDOM2(boolean flag) {
        this.dom2_ = flag;
    }

    public void setExpandEntityReference(boolean flag) {
        this.expandEntityReference_ = flag;
    }

    public void setEmptyElementTag(boolean flag) {
        this.emptyElementTag_ = flag;
    }

    public String getText() {
        return new String(this.buffer_);
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public boolean enter(Element element) {
        String s = element.getTagName();
        this.buffer_.append("<");
        this.buffer_.append(s);
        NamedNodeMap namednodemap = element.getAttributes();
        int i = namednodemap.getLength();
        for (int j = 0; j < i; j++) {
            Attr attr = (Attr) namednodemap.item(j);
            if (attr.getSpecified()) {
                this.buffer_.append(' ');
                enter(attr);
                leave(attr);
            }
        }
        this.buffer_.append(">");
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public void leave(Element element) {
        String s = element.getTagName();
        this.buffer_.append("</" + s + ">");
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public boolean enter(Attr attr) {
        this.buffer_.append(attr.getName());
        this.buffer_.append("=\"");
        this.buffer_.append(UXML.escapeAttrQuot(attr.getValue()));
        this.buffer_.append('\"');
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public void leave(Attr attr) {
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public boolean enter(Text text) {
        this.buffer_.append(UXML.escapeCharData(text.getData()));
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public void leave(Text text) {
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public boolean enter(CDATASection cdatasection) {
        this.buffer_.append("<![CDATA[");
        this.buffer_.append(cdatasection.getData());
        this.buffer_.append("]]>");
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public void leave(CDATASection cdatasection) {
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public boolean enter(EntityReference entityreference) {
        this.buffer_.append("&");
        this.buffer_.append(entityreference.getNodeName());
        this.buffer_.append(PolicyIdFilter.OID_SEPARATOR);
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public void leave(EntityReference entityreference) {
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public boolean enter(Entity entity) {
        String s = entity.getNodeName();
        String s1 = entity.getPublicId();
        String s2 = entity.getSystemId();
        String s3 = entity.getNotationName();
        this.buffer_.append("<!ENTITY ");
        this.buffer_.append(s);
        if (s2 != null) {
            if (s1 != null) {
                this.buffer_.append(" PUBLIC \"");
                this.buffer_.append(s1);
                this.buffer_.append("\" \"");
                this.buffer_.append(UXML.escapeSystemQuot(s2));
                this.buffer_.append("\">");
            } else {
                this.buffer_.append(" SYSTEM \"");
                this.buffer_.append(UXML.escapeSystemQuot(s2));
                this.buffer_.append("\">");
            }
            if (s3 != null) {
                this.buffer_.append(" NDATA ");
                this.buffer_.append(s3);
                this.buffer_.append(">");
                return false;
            }
            return false;
        }
        this.buffer_.append(" \"");
        XMLMaker xmlmaker = new XMLMaker();
        UDOMVisitor.traverseChildren(entity, xmlmaker);
        this.buffer_.append(UXML.escapeEntityQuot(xmlmaker.getText()));
        this.buffer_.append(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
        this.buffer_.append(">");
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public void leave(Entity entity) {
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public boolean enter(ProcessingInstruction processinginstruction) {
        this.buffer_.append("<?");
        this.buffer_.append(processinginstruction.getTarget());
        this.buffer_.append(" ");
        this.buffer_.append(processinginstruction.getData());
        this.buffer_.append("?>");
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public void leave(ProcessingInstruction processinginstruction) {
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public boolean enter(Comment comment) {
        this.buffer_.append("<!--");
        this.buffer_.append(comment.getData());
        this.buffer_.append("-->");
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public void leave(Comment comment) {
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public boolean enter(Document document) {
        this.buffer_.append("<?xml version=\"1.0\" encoding=\"");
        this.buffer_.append(this.encoding_);
        this.buffer_.append("\" ?>\n");
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public void leave(Document document) {
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public boolean enter(DocumentType documenttype) {
        if (this.dom2_) {
            String s = documenttype.getName();
            String s2 = documenttype.getPublicId();
            String s3 = documenttype.getSystemId();
            String s4 = documenttype.getInternalSubset();
            this.buffer_.append("<!DOCTYPE ");
            this.buffer_.append(s);
            if (s2 != null) {
                this.buffer_.append(" PUBLIC \"");
                this.buffer_.append(s2);
                this.buffer_.append(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
            }
            if (s3 != null) {
                this.buffer_.append(" SYSTEM \"");
                this.buffer_.append(s3);
                this.buffer_.append(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
            }
            if (s4 != null) {
                this.buffer_.append(" [");
                this.buffer_.append(s4);
                this.buffer_.append("]");
            }
            this.buffer_.append(">\n");
            return true;
        }
        String s1 = documenttype.getName();
        NamedNodeMap namednodemap = documenttype.getEntities();
        NamedNodeMap namednodemap1 = documenttype.getNotations();
        this.buffer_.append("<!DOCTYPE ");
        this.buffer_.append(s1);
        if ((namednodemap != null && namednodemap.getLength() > 0) || (namednodemap1 != null && namednodemap1.getLength() > 0)) {
            this.buffer_.append(" [");
            int i = namednodemap.getLength();
            for (int j = 0; j < i; j++) {
                XMLMaker xmlmaker = new XMLMaker();
                UDOMVisitor.traverse(namednodemap.item(j), xmlmaker);
                this.buffer_.append(xmlmaker.getText());
            }
            int k = namednodemap1.getLength();
            for (int l = 0; l < k; l++) {
                enter((Notation) namednodemap1.item(l));
                leave((Notation) namednodemap1.item(l));
            }
            this.buffer_.append("]");
        }
        this.buffer_.append(">\n");
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public void leave(DocumentType documenttype) {
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public boolean enter(DocumentFragment documentfragment) {
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public void leave(DocumentFragment documentfragment) {
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public boolean enter(Notation notation) {
        String s = notation.getNodeName();
        String s1 = notation.getPublicId();
        String s2 = notation.getSystemId();
        this.buffer_.append("<!NOTATION ");
        this.buffer_.append(s);
        if (s1 != null) {
            this.buffer_.append(" PUBLIC \"");
            this.buffer_.append(s1);
            this.buffer_.append(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
            if (s2 != null) {
                this.buffer_.append(" \"");
                this.buffer_.append(UXML.escapeSystemQuot(s2));
                this.buffer_.append(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
            }
        } else if (s2 != null) {
            this.buffer_.append(" SYSTEM \"");
            this.buffer_.append(UXML.escapeSystemQuot(s2));
            this.buffer_.append(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
        }
        this.buffer_.append(">");
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public void leave(Notation notation) {
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public boolean enter(Node node) {
        throw new InternalError(node.toString());
    }

    @Override // com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor
    public void leave(Node node) {
        throw new InternalError(node.toString());
    }

    public boolean isParsedEntity(EntityReference entityreference) {
        String s = entityreference.getNodeName();
        Document document = entityreference.getOwnerDocument();
        DocumentType documenttype = document.getDoctype();
        if (documenttype == null) {
            return false;
        }
        NamedNodeMap namednodemap = documenttype.getEntities();
        Entity entity = (Entity) namednodemap.getNamedItem(s);
        return entity != null && entity.getNotationName() == null;
    }
}
