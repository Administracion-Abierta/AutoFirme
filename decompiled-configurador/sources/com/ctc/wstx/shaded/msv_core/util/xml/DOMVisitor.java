package com.ctc.wstx.shaded.msv_core.util.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/util/xml/DOMVisitor.class */
public abstract class DOMVisitor {
    public void visit(Document dom) {
        visit(dom.getDocumentElement());
    }

    public void visit(Element e) {
        NodeList lst = e.getChildNodes();
        int len = lst.getLength();
        for (int i = 0; i < len; i++) {
            Node n = lst.item(i);
            if (n.getNodeType() == 1) {
                visit((Element) n);
            } else {
                visitNode(n);
            }
        }
    }

    public void visitNode(Node n) {
    }
}
