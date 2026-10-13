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
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv/org_jp_gr_xml/dom/UDOMVisitor.class */
public final class UDOMVisitor {
    public static void traverse(Node node, IDOMVisitor idomvisitor) throws DOMVisitorException {
        boolean flag;
        switch (node.getNodeType()) {
            case 1:
                flag = idomvisitor.enter((Element) node);
                break;
            case 2:
                flag = idomvisitor.enter((Attr) node);
                break;
            case 3:
                flag = idomvisitor.enter((Text) node);
                break;
            case 4:
                flag = idomvisitor.enter((CDATASection) node);
                break;
            case 5:
                flag = idomvisitor.enter((EntityReference) node);
                break;
            case 6:
                flag = idomvisitor.enter((Entity) node);
                break;
            case 7:
                flag = idomvisitor.enter((ProcessingInstruction) node);
                break;
            case 8:
                flag = idomvisitor.enter((Comment) node);
                break;
            case 9:
                flag = idomvisitor.enter((Document) node);
                break;
            case 10:
                flag = idomvisitor.enter((DocumentType) node);
                break;
            case 11:
                flag = idomvisitor.enter((DocumentFragment) node);
                break;
            case 12:
                flag = idomvisitor.enter((Notation) node);
                break;
            default:
                flag = idomvisitor.enter(node);
                break;
        }
        if (flag) {
            traverseChildren(node, idomvisitor);
            switch (node.getNodeType()) {
                case 1:
                    idomvisitor.leave((Element) node);
                    break;
                case 2:
                    idomvisitor.leave((Attr) node);
                    break;
                case 3:
                    idomvisitor.leave((Text) node);
                    break;
                case 4:
                    idomvisitor.leave((CDATASection) node);
                    break;
                case 5:
                    idomvisitor.leave((EntityReference) node);
                    break;
                case 6:
                    idomvisitor.leave((Entity) node);
                    break;
                case 7:
                    idomvisitor.leave((ProcessingInstruction) node);
                    break;
                case 8:
                    idomvisitor.leave((Comment) node);
                    break;
                case 9:
                    idomvisitor.leave((Document) node);
                    break;
                case 10:
                    idomvisitor.leave((DocumentType) node);
                    break;
                case 11:
                    idomvisitor.leave((DocumentFragment) node);
                    break;
                case 12:
                    idomvisitor.leave((Notation) node);
                    break;
                default:
                    idomvisitor.leave(node);
                    break;
            }
        }
    }

    public static void traverseChildren(Node node, IDOMVisitor idomvisitor) {
        NodeList nodelist = node.getChildNodes();
        int i = nodelist.getLength();
        for (int j = 0; j < i; j++) {
            traverse(nodelist.item(j), idomvisitor);
        }
    }
}
