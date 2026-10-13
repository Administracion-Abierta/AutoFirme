package es.gob.afirma.signers.xml.dereference;

import java.util.Set;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/xml/dereference/XMLUtils.class */
final class XMLUtils {
    XMLUtils() {
    }

    static void getSet(Node node, Set<Node> set, Node node2, boolean z) {
        if (node2 != null && isDescendantOrSelf(node2, node)) {
            return;
        }
        getSetRec(node, set, node2, z);
    }

    static final void getSetRec(Node node, Set<Node> set, Node node2, boolean z) {
        if (node == node2) {
            return;
        }
        switch (node.getNodeType()) {
            case 1:
                set.add(node);
                if (((Element) node).hasAttributes()) {
                    NamedNodeMap attributes = ((Element) node).getAttributes();
                    for (int i = 0; i < attributes.getLength(); i++) {
                        set.add(attributes.item(i));
                    }
                }
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            default:
                set.add(node);
                return;
            case 8:
                if (z) {
                    set.add(node);
                    return;
                }
                return;
            case 9:
                break;
            case 10:
                return;
        }
        Node firstChild = node.getFirstChild();
        while (true) {
            Node nextSibling = firstChild;
            if (nextSibling != null) {
                if (nextSibling.getNodeType() == 3) {
                    set.add(nextSibling);
                    while (nextSibling != null && nextSibling.getNodeType() == 3) {
                        nextSibling = nextSibling.getNextSibling();
                    }
                    if (nextSibling == null) {
                        return;
                    }
                }
                getSetRec(nextSibling, set, node2, z);
                firstChild = nextSibling.getNextSibling();
            } else {
                return;
            }
        }
    }

    public static Document getOwnerDocument(Node node) {
        if (node.getNodeType() == 9) {
            return (Document) node;
        }
        return node.getOwnerDocument();
    }

    public static void circumventBug2650(Document document) {
        Element documentElement = document.getDocumentElement();
        if (documentElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS) == null) {
            documentElement.setAttributeNS("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS, "");
        }
        circumventBug2650internal(document);
    }

    private static void circumventBug2650internal(Node node) {
        Node parentNode = null;
        Node nextSibling = null;
        while (true) {
            Node nextSibling2 = nextSibling;
            switch (node.getNodeType()) {
                case 1:
                    Element element = (Element) node;
                    if (element.hasChildNodes()) {
                        if (element.hasAttributes()) {
                            NamedNodeMap attributes = element.getAttributes();
                            int length = attributes.getLength();
                            Node firstChild = element.getFirstChild();
                            while (true) {
                                Node node2 = firstChild;
                                if (node2 != null) {
                                    if (node2.getNodeType() == 1) {
                                        Element element2 = (Element) node2;
                                        for (int i = 0; i < length; i++) {
                                            Attr attr = (Attr) attributes.item(i);
                                            if ("http://www.w3.org/2000/xmlns/" == attr.getNamespaceURI() && !element2.hasAttributeNS("http://www.w3.org/2000/xmlns/", attr.getLocalName())) {
                                                element2.setAttributeNS("http://www.w3.org/2000/xmlns/", attr.getName(), attr.getNodeValue());
                                            }
                                        }
                                    }
                                    firstChild = node2.getNextSibling();
                                }
                            }
                        }
                        break;
                    }
                case 5:
                case 9:
                    parentNode = node;
                    nextSibling2 = node.getFirstChild();
                    break;
            }
            while (nextSibling2 == null && parentNode != null) {
                nextSibling2 = parentNode.getNextSibling();
                parentNode = parentNode.getParentNode();
            }
            if (nextSibling2 == null) {
                return;
            }
            node = nextSibling2;
            nextSibling = node.getNextSibling();
        }
    }

    public static boolean isDescendantOrSelf(Node node, Node node2) {
        if (node == node2) {
            return true;
        }
        Node ownerElement = node2;
        while (true) {
            Node node3 = ownerElement;
            if (node3 == null) {
                return false;
            }
            if (node3 == node) {
                return true;
            }
            if (node3.getNodeType() == 2) {
                ownerElement = ((Attr) node3).getOwnerElement();
            } else {
                ownerElement = node3.getParentNode();
            }
        }
    }
}
