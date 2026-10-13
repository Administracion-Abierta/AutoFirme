package nu.xom.converters;

import nu.xom.Attribute;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.XMLException;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/converters/DOMConverter.class */
public class DOMConverter {
    private static final String XMLNS_NAMESPACE = "http://www.w3.org/2000/xmlns/";

    private DOMConverter() {
    }

    public static Document convert(org.w3c.dom.Document document) {
        return convert(document, new NodeFactory());
    }

    public static Document convert(org.w3c.dom.Document document, NodeFactory nodeFactory) {
        Element documentElement = document.getDocumentElement();
        nu.xom.Element elementMakeRootElement = nodeFactory.makeRootElement(documentElement.getTagName(), documentElement.getNamespaceURI());
        Document documentStartMakingDocument = nodeFactory.startMakingDocument();
        documentStartMakingDocument.setRootElement(elementMakeRootElement);
        Nodes nodesConvert = convert(documentElement, nodeFactory);
        boolean z = true;
        int i = 0;
        for (int i2 = 0; i2 < nodesConvert.size(); i2++) {
            Node node = nodesConvert.get(i2);
            if (!z) {
                documentStartMakingDocument.appendChild(node);
            } else if (node instanceof nu.xom.Element) {
                documentStartMakingDocument.setRootElement((nu.xom.Element) node);
                z = false;
            } else {
                int i3 = i;
                i++;
                documentStartMakingDocument.insertChild(node, i3);
            }
        }
        org.w3c.dom.Node firstChild = document.getFirstChild();
        int i4 = 0;
        while (firstChild.getNodeType() != 1) {
            Nodes nodesConvert2 = convert(firstChild, nodeFactory);
            for (int i5 = 0; i5 < nodesConvert2.size(); i5++) {
                int i6 = i4;
                i4++;
                documentStartMakingDocument.insertChild(nodesConvert2.get(i5), i6);
            }
            firstChild = firstChild.getNextSibling();
        }
        org.w3c.dom.Node nextSibling = firstChild.getNextSibling();
        while (true) {
            org.w3c.dom.Node node2 = nextSibling;
            if (node2 == null) {
                return documentStartMakingDocument;
            }
            Nodes nodesConvert3 = convert(node2, nodeFactory);
            for (int i7 = 0; i7 < nodesConvert3.size(); i7++) {
                documentStartMakingDocument.appendChild(nodesConvert3.get(i7));
            }
            nextSibling = node2.getNextSibling();
        }
    }

    public static Nodes convert(DocumentFragment documentFragment) {
        return convert(documentFragment, new NodeFactory());
    }

    public static Nodes convert(DocumentFragment documentFragment, NodeFactory nodeFactory) {
        Nodes nodes = new Nodes();
        NodeList childNodes = documentFragment.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            appendNodes(nodes, convert(childNodes.item(i), nodeFactory));
        }
        return nodes;
    }

    private static void appendNodes(Nodes nodes, Nodes nodes2) {
        for (int i = 0; i < nodes2.size(); i++) {
            nodes.append(nodes2.get(i));
        }
    }

    private static Nodes convert(org.w3c.dom.Node node, NodeFactory nodeFactory) {
        short nodeType = node.getNodeType();
        switch (nodeType) {
            case 1:
                return convert((Element) node, nodeFactory);
            case 2:
            case 5:
            case 6:
            case 9:
            default:
                throw new XMLException(new StringBuffer().append("Unexpected DOM node type: ").append((int) nodeType).toString());
            case 3:
                return convert((Text) node, nodeFactory);
            case 4:
                return convert((Text) node, nodeFactory);
            case 7:
                return convert((ProcessingInstruction) node, nodeFactory);
            case 8:
                return convert((Comment) node, nodeFactory);
            case 10:
                return convert((DocumentType) node, nodeFactory);
        }
    }

    public static nu.xom.Comment convert(Comment comment) {
        return new nu.xom.Comment(comment.getNodeValue());
    }

    private static Nodes convert(Comment comment, NodeFactory nodeFactory) {
        return nodeFactory.makeComment(comment.getNodeValue());
    }

    public static nu.xom.Text convert(Text text) {
        return new nu.xom.Text(text.getNodeValue());
    }

    private static Nodes convert(Text text, NodeFactory nodeFactory) {
        return nodeFactory.makeText(text.getNodeValue());
    }

    public static Attribute convert(Attr attr) {
        String name = attr.getName();
        String namespaceURI = attr.getNamespaceURI();
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        return new Attribute(name, namespaceURI, attr.getNodeValue());
    }

    public static nu.xom.ProcessingInstruction convert(ProcessingInstruction processingInstruction) {
        return new nu.xom.ProcessingInstruction(processingInstruction.getTarget(), processingInstruction.getNodeValue());
    }

    private static Nodes convert(ProcessingInstruction processingInstruction, NodeFactory nodeFactory) {
        return nodeFactory.makeProcessingInstruction(processingInstruction.getTarget(), processingInstruction.getNodeValue());
    }

    public static DocType convert(DocumentType documentType) {
        DocType docType = new DocType(documentType.getName(), documentType.getPublicId(), documentType.getSystemId());
        docType.setInternalDTDSubset(documentType.getInternalSubset());
        return docType;
    }

    private static Nodes convert(DocumentType documentType, NodeFactory nodeFactory) {
        Nodes nodesMakeDocType = nodeFactory.makeDocType(documentType.getName(), documentType.getPublicId(), documentType.getSystemId());
        for (int i = 0; i < nodesMakeDocType.size(); i++) {
            Node node = nodesMakeDocType.get(i);
            if (node instanceof DocType) {
                ((DocType) node).setInternalDTDSubset(documentType.getInternalSubset());
                break;
            }
        }
        return nodesMakeDocType;
    }

    public static nu.xom.Element convert(Element element) {
        return (nu.xom.Element) convert(element, new NodeFactory()).get(0);
    }

    private static Nodes convert(Element element, NodeFactory nodeFactory) {
        org.w3c.dom.Node nextSibling = element;
        nu.xom.Element elementMakeElement = makeElement(element, nodeFactory);
        if (elementMakeElement == null) {
            Nodes nodes = new Nodes();
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Nodes nodesConvert = convert(childNodes.item(i), nodeFactory);
                for (int i2 = 0; i2 < nodesConvert.size(); i2++) {
                    nodes.append(nodesConvert.get(i2));
                }
            }
            return nodes;
        }
        nu.xom.Element parent = elementMakeElement;
        boolean z = false;
        while (true) {
            if (nextSibling.hasChildNodes() && !z) {
                nextSibling = nextSibling.getFirstChild();
                z = false;
            } else {
                if (nextSibling == element) {
                    return nodeFactory.finishMakingElement(elementMakeElement);
                }
                if (nextSibling.getNextSibling() != null) {
                    nextSibling = nextSibling.getNextSibling();
                    z = false;
                } else {
                    nextSibling = nextSibling.getParentNode();
                    z = true;
                    if (parent.getParent() != null) {
                        parent = parent.getParent();
                    }
                }
            }
            if (nextSibling.getNodeType() == 1) {
                nu.xom.Element elementMakeElement2 = makeElement((Element) nextSibling, nodeFactory);
                if (elementMakeElement2 != null) {
                    parent.appendChild(elementMakeElement2);
                    if (nextSibling.hasChildNodes()) {
                        parent = elementMakeElement2;
                    }
                }
            } else {
                Nodes nodesConvert2 = convert(nextSibling, nodeFactory);
                for (int i3 = 0; i3 < nodesConvert2.size(); i3++) {
                    parent.appendChild(nodesConvert2.get(i3));
                }
            }
        }
    }

    private static nu.xom.Element makeElement(Element element, NodeFactory nodeFactory) {
        String namespaceURI = element.getNamespaceURI();
        String tagName = element.getTagName();
        nu.xom.Element elementMakeRootElement = (element.getParentNode() == null || element.getParentNode().getNodeType() == 9) ? nodeFactory.makeRootElement(tagName, namespaceURI) : nodeFactory.startMakingElement(tagName, namespaceURI);
        if (elementMakeRootElement == null) {
            return null;
        }
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr attr = (Attr) attributes.item(i);
            String name = attr.getName();
            String namespaceURI2 = attr.getNamespaceURI();
            String value = attr.getValue();
            if (namespaceURI2 == null) {
                namespaceURI2 = "";
            }
            if (!namespaceURI2.equals("http://www.w3.org/2000/xmlns/")) {
                Nodes nodesMakeAttribute = nodeFactory.makeAttribute(name, namespaceURI2, value, Attribute.Type.UNDECLARED);
                for (int i2 = 0; i2 < nodesMakeAttribute.size(); i2++) {
                    Node node = nodesMakeAttribute.get(i2);
                    if (node instanceof Attribute) {
                        elementMakeRootElement.addAttribute((Attribute) node);
                    } else {
                        elementMakeRootElement.appendChild(node);
                    }
                }
            } else if (!name.equals(CanonicalizerBase.XMLNS)) {
                String strSubstring = name.substring(name.indexOf(58) + 1);
                if (!value.equals(elementMakeRootElement.getNamespaceURI(strSubstring))) {
                    elementMakeRootElement.addNamespaceDeclaration(strSubstring, value);
                }
            }
        }
        return elementMakeRootElement;
    }

    public static org.w3c.dom.Document convert(Document document, DOMImplementation dOMImplementation) {
        nu.xom.Element rootElement = document.getRootElement();
        String qualifiedName = rootElement.getQualifiedName();
        String namespaceURI = rootElement.getNamespaceURI();
        DocType docType = document.getDocType();
        org.w3c.dom.Document documentCreateDocument = dOMImplementation.createDocument(namespaceURI, qualifiedName, docType != null ? dOMImplementation.createDocumentType(qualifiedName, docType.getPublicID(), docType.getSystemID()) : null);
        Element documentElement = documentCreateDocument.getDocumentElement();
        boolean z = true;
        for (int i = 0; i < document.getChildCount(); i++) {
            Node child = document.getChild(i);
            if (!(child instanceof DocType)) {
                if (child instanceof nu.xom.Element) {
                    convert((nu.xom.Element) child, documentCreateDocument);
                    z = false;
                } else {
                    org.w3c.dom.Node nodeConvert = convert(child, documentCreateDocument);
                    if (z) {
                        documentCreateDocument.insertBefore(nodeConvert, documentElement);
                    } else {
                        documentCreateDocument.appendChild(nodeConvert);
                    }
                }
            }
        }
        return documentCreateDocument;
    }

    private static org.w3c.dom.Node convert(Node node, org.w3c.dom.Document document) {
        if (node instanceof nu.xom.Text) {
            return convert((nu.xom.Text) node, document);
        }
        if (node instanceof nu.xom.Comment) {
            return convert((nu.xom.Comment) node, document);
        }
        if (node instanceof nu.xom.ProcessingInstruction) {
            return convert((nu.xom.ProcessingInstruction) node, document);
        }
        throw new XMLException(new StringBuffer().append("Unexpected node type: ").append(node.getClass().getName()).toString());
    }

    private static Comment convert(nu.xom.Comment comment, org.w3c.dom.Document document) {
        return document.createComment(comment.getValue());
    }

    private static Text convert(nu.xom.Text text, org.w3c.dom.Document document) {
        return document.createTextNode(text.getValue());
    }

    private static ProcessingInstruction convert(nu.xom.ProcessingInstruction processingInstruction, org.w3c.dom.Document document) {
        return document.createProcessingInstruction(processingInstruction.getTarget(), processingInstruction.getValue());
    }

    private static Element convert(nu.xom.Element element, org.w3c.dom.Document document) {
        Element elementMakeElement = makeElement(element, document);
        Element element2 = elementMakeElement;
        Node child = element;
        int i = 0;
        int[] iArrGrow = new int[10];
        int i2 = 0;
        iArrGrow[0] = 0;
        boolean z = false;
        while (true) {
            if (z || child.getChildCount() <= 0) {
                z = false;
                ParentNode parent = child.getParent();
                org.w3c.dom.Node parentNode = element2.getParentNode();
                if (parentNode.getNodeType() == 1 && (child instanceof nu.xom.Element)) {
                    element2 = parentNode;
                }
                if (parent.getChildCount() - 1 == i) {
                    child = parent;
                    i2--;
                    if (child == element || child.getParent() == null) {
                        break;
                    }
                    i = iArrGrow[i2];
                    z = true;
                } else {
                    i++;
                    iArrGrow[i2] = i;
                    child = parent.getChild(i);
                }
            } else {
                child = child.getChild(0);
                i = 0;
                i2++;
                iArrGrow = grow(iArrGrow, i2);
                iArrGrow[i2] = 0;
            }
            if (child instanceof nu.xom.Element) {
                Element elementMakeElement2 = makeElement((nu.xom.Element) child, document);
                element2.appendChild(elementMakeElement2);
                element2 = elementMakeElement2;
            } else {
                element2.appendChild(convert(child, document));
            }
        }
        return elementMakeElement;
    }

    private static int[] grow(int[] iArr, int i) {
        if (i < iArr.length) {
            return iArr;
        }
        int[] iArr2 = new int[iArr.length * 2];
        System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
        return iArr2;
    }

    /* JADX WARN: Code duplicated, block: B:29:0x00ff  */
    /* JADX WARN: Code duplicated, block: B:31:0x0109  */
    /* JADX WARN: Code duplicated, block: B:32:0x012b  */
    private static Element makeElement(nu.xom.Element element, org.w3c.dom.Document document) {
        Attr attrCreateAttributeNS;
        String namespaceURI = element.getNamespaceURI();
        Element documentElement = element.getParent() instanceof Document ? document.getDocumentElement() : namespaceURI.equals("") ? document.createElement(element.getQualifiedName()) : document.createElementNS(namespaceURI, element.getQualifiedName());
        int attributeCount = element.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            Attribute attribute = element.getAttribute(i);
            String namespaceURI2 = attribute.getNamespaceURI();
            if (namespaceURI2.equals("")) {
                attrCreateAttributeNS = document.createAttribute(attribute.getLocalName());
                documentElement.setAttributeNode(attrCreateAttributeNS);
            } else {
                attrCreateAttributeNS = document.createAttributeNS(namespaceURI2, attribute.getQualifiedName());
                documentElement.setAttributeNodeNS(attrCreateAttributeNS);
            }
            attrCreateAttributeNS.setValue(attribute.getValue());
        }
        int namespaceDeclarationCount = element.getNamespaceDeclarationCount();
        for (int i2 = 0; i2 < namespaceDeclarationCount; i2++) {
            String namespacePrefix = element.getNamespacePrefix(i2);
            String namespaceURI3 = element.getNamespaceURI(namespacePrefix);
            ParentNode parent = element.getParent();
            if (parent instanceof nu.xom.Element) {
                if (!namespaceURI3.equals(((nu.xom.Element) parent).getNamespaceURI(namespacePrefix))) {
                    if ("".equals(namespacePrefix)) {
                        Attr attrCreateAttributeNS2 = document.createAttributeNS("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS);
                        documentElement.setAttributeNodeNS(attrCreateAttributeNS2);
                        attrCreateAttributeNS2.setValue(namespaceURI3);
                    } else {
                        Attr attrCreateAttributeNS3 = document.createAttributeNS("http://www.w3.org/2000/xmlns/", new StringBuffer().append("xmlns:").append(namespacePrefix).toString());
                        documentElement.setAttributeNodeNS(attrCreateAttributeNS3);
                        attrCreateAttributeNS3.setValue(namespaceURI3);
                    }
                }
            } else if (!namespaceURI3.equals("")) {
                if ("".equals(namespacePrefix)) {
                    Attr attrCreateAttributeNS4 = document.createAttributeNS("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS);
                    documentElement.setAttributeNodeNS(attrCreateAttributeNS4);
                    attrCreateAttributeNS4.setValue(namespaceURI3);
                } else {
                    Attr attrCreateAttributeNS5 = document.createAttributeNS("http://www.w3.org/2000/xmlns/", new StringBuffer().append("xmlns:").append(namespacePrefix).toString());
                    documentElement.setAttributeNodeNS(attrCreateAttributeNS5);
                    attrCreateAttributeNS5.setValue(namespaceURI3);
                }
            }
        }
        return documentElement;
    }
}
