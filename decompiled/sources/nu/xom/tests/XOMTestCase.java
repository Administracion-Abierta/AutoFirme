package nu.xom.tests;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;
import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Namespace;
import nu.xom.Node;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/tests/XOMTestCase.class */
public class XOMTestCase extends TestCase {
    public XOMTestCase(String str) {
        super(str);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    public static void assertEquals(Text text, Text text2) throws ComparisonFailure {
        assertEquals((String) null, text, text2);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    public static void assertEquals(String str, Text text, Text text2) throws ComparisonFailure {
        if (text2 == text) {
            return;
        }
        nullCheck(str, text, text2);
        assertEquals(str, text.getValue(), text2.getValue());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    private static void nullCheck(String str, Node node, Node node2) throws ComparisonFailure {
        if (node == null) {
            throw new ComparisonFailure(str, (String) null, node2.toXML());
        }
        if (node2 == null) {
            throw new ComparisonFailure(str, node.toXML(), (String) null);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    public static void assertEquals(Attribute attribute, Attribute attribute2) throws ComparisonFailure {
        assertEquals((String) null, attribute, attribute2);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    public static void assertEquals(String str, Attribute attribute, Attribute attribute2) throws ComparisonFailure {
        if (attribute2 == attribute) {
            return;
        }
        nullCheck(str, attribute, attribute2);
        String value = attribute.getValue();
        String value2 = attribute2.getValue();
        if (!"xml:base".equals(attribute.getQualifiedName())) {
            assertEquals(str, value, value2);
            assertEquals(str, attribute.getLocalName(), attribute2.getLocalName());
            assertEquals(str, attribute.getQualifiedName(), attribute2.getQualifiedName());
            assertEquals(str, attribute.getNamespaceURI(), attribute2.getNamespaceURI());
            return;
        }
        if (value.equals(value2)) {
            return;
        }
        if (value.startsWith("../")) {
            assertTrue(str, value2.endsWith(value.substring(2)));
        } else {
            assertTrue(str, value.endsWith(new StringBuffer().append('/').append(value2).toString()) || value2.endsWith(new StringBuffer().append('/').append(value).toString()));
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    public static void assertEquals(DocType docType, DocType docType2) throws ComparisonFailure {
        assertEquals((String) null, docType, docType2);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    public static void assertEquals(String str, DocType docType, DocType docType2) throws ComparisonFailure {
        if (docType2 == docType) {
            return;
        }
        nullCheck(str, docType, docType2);
        assertEquals(str, docType.getPublicID(), docType2.getPublicID());
        assertEquals(str, docType.getSystemID(), docType2.getSystemID());
        assertEquals(str, docType.getRootElementName(), docType2.getRootElementName());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    public static void assertEquals(Element element, Element element2) throws ComparisonFailure {
        assertEquals((String) null, element, element2);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    public static void assertEquals(String str, Element element, Element element2) throws ComparisonFailure {
        if (element2 == element) {
            return;
        }
        nullCheck(str, element, element2);
        assertEquals(str, element.getLocalName(), element2.getLocalName());
        assertEquals(str, element.getNamespacePrefix(), element2.getNamespacePrefix());
        assertEquals(str, element.getNamespaceURI(), element2.getNamespaceURI());
        assertEquals(str, element.getAttributeCount(), element2.getAttributeCount());
        for (int i = 0; i < element.getAttributeCount(); i++) {
            Attribute attribute = element.getAttribute(i);
            Attribute attribute2 = element2.getAttribute(attribute.getLocalName(), attribute.getNamespaceURI());
            assertNotNull(str, attribute2);
            assertEquals(str, attribute, attribute2);
        }
        for (int i2 = 0; i2 < element.getNamespaceDeclarationCount(); i2++) {
            String namespacePrefix = element.getNamespacePrefix(i2);
            String namespaceURI = element.getNamespaceURI(namespacePrefix);
            assertNotNull(str, element2.getNamespaceURI(namespacePrefix));
            assertEquals(str, namespaceURI, element2.getNamespaceURI(namespacePrefix));
        }
        for (int i3 = 0; i3 < element2.getNamespaceDeclarationCount(); i3++) {
            String namespacePrefix2 = element2.getNamespacePrefix(i3);
            String namespaceURI2 = element2.getNamespaceURI(namespacePrefix2);
            assertNotNull(str, element.getNamespaceURI(namespacePrefix2));
            assertEquals(str, namespaceURI2, element.getNamespaceURI(namespacePrefix2));
        }
        compareChildren(str, element, element2);
    }

    private static boolean hasAdjacentTextNodes(Element element) {
        boolean z;
        boolean z2 = false;
        int childCount = element.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Node child = element.getChild(i);
            if (!(child instanceof Text)) {
                z = false;
            } else {
                if (z2 || "".equals(child.getValue())) {
                    return true;
                }
                z = true;
            }
            z2 = z;
        }
        return false;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    private static void compareChildren(String str, Element element, Element element2) throws ComparisonFailure {
        Node node;
        Element elementCombineTextNodes = element;
        Element elementCombineTextNodes2 = element2;
        if (hasAdjacentTextNodes(element)) {
            elementCombineTextNodes = combineTextNodes(element);
        }
        if (hasAdjacentTextNodes(element2)) {
            elementCombineTextNodes2 = combineTextNodes(element2);
        }
        int childCount = elementCombineTextNodes.getChildCount();
        assertEquals(str, childCount, elementCombineTextNodes2.getChildCount());
        int i = childCount;
        for (int i2 = 0; i2 < childCount; i2++) {
            Node child = elementCombineTextNodes.getChild(i2);
            if (child instanceof Text) {
                i--;
                assertEquals(str, child, elementCombineTextNodes2.getChild(i2));
            }
        }
        int i3 = 0;
        int childCount2 = element.getChildCount();
        for (int i4 = 0; i4 < childCount2; i4++) {
            Node child2 = element.getChild(i4);
            if (!(child2 instanceof Text)) {
                Node child3 = element2.getChild(i3);
                while (true) {
                    node = child3;
                    i3++;
                    if (!(node instanceof Text)) {
                        break;
                    } else {
                        child3 = element2.getChild(i3);
                    }
                }
                assertEquals(str, child2, node);
            }
        }
    }

    private static Element combineTextNodes(Element element) {
        Element element2 = new Element(PDPageLabelRange.STYLE_LETTERS_LOWER);
        Comment comment = new Comment("c");
        StringBuffer stringBuffer = new StringBuffer();
        int childCount = element.getChildCount();
        int i = 0;
        while (i < childCount) {
            Node child = element.getChild(i);
            if (!(child instanceof Text)) {
                element2.appendChild(comment.copy());
            } else if (!"".equals(child.getValue())) {
                stringBuffer.setLength(0);
                do {
                    stringBuffer.append(child.getValue());
                    i++;
                    if (i == childCount) {
                        break;
                    }
                    child = element.getChild(i);
                } while (child instanceof Text);
                i--;
                element2.appendChild(stringBuffer.toString());
            }
            i++;
        }
        return element2;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    public static void assertEquals(Document document, Document document2) throws ComparisonFailure {
        assertEquals((String) null, document, document2);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    public static void assertEquals(String str, Document document, Document document2) throws ComparisonFailure {
        if (document2 == document) {
            return;
        }
        nullCheck(str, document, document2);
        assertEquals(str, document.getChildCount(), document2.getChildCount());
        for (int i = 0; i < document2.getChildCount(); i++) {
            assertEquals(str, document.getChild(i), document2.getChild(i));
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    public static void assertEquals(Comment comment, Comment comment2) throws ComparisonFailure {
        assertEquals((String) null, comment, comment2);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    public static void assertEquals(String str, Comment comment, Comment comment2) throws ComparisonFailure {
        if (comment2 == comment) {
            return;
        }
        nullCheck(str, comment, comment2);
        assertEquals(str, comment.getValue(), comment2.getValue());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    public static void assertEquals(ProcessingInstruction processingInstruction, ProcessingInstruction processingInstruction2) throws ComparisonFailure {
        assertEquals((String) null, processingInstruction, processingInstruction2);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    public static void assertEquals(String str, ProcessingInstruction processingInstruction, ProcessingInstruction processingInstruction2) throws ComparisonFailure {
        if (processingInstruction2 == processingInstruction) {
            return;
        }
        nullCheck(str, processingInstruction, processingInstruction2);
        assertEquals(str, processingInstruction.getValue(), processingInstruction2.getValue());
        assertEquals(str, processingInstruction.getTarget(), processingInstruction2.getTarget());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    public static void assertEquals(String str, Namespace namespace, Namespace namespace2) throws ComparisonFailure {
        if (namespace2 == namespace) {
            return;
        }
        nullCheck(str, namespace, namespace2);
        assertEquals(str, namespace.getValue(), namespace2.getValue());
        assertEquals(str, namespace.getPrefix(), namespace2.getPrefix());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    public static void assertEquals(Node node, Node node2) throws ComparisonFailure {
        assertEquals((String) null, node, node2);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: junit.framework.ComparisonFailure */
    public static void assertEquals(String str, Node node, Node node2) throws ComparisonFailure {
        if (node2 == node) {
            return;
        }
        nullCheck(str, node, node2);
        try {
            if (node instanceof Document) {
                assertEquals(str, (Document) node, (Document) node2);
            } else if (node instanceof Element) {
                assertEquals(str, (Element) node, (Element) node2);
            } else if (node instanceof Text) {
                assertEquals(str, (Text) node, (Text) node2);
            } else if (node instanceof DocType) {
                assertEquals(str, (DocType) node, (DocType) node2);
            } else if (node instanceof Comment) {
                assertEquals(str, (Comment) node, (Comment) node2);
            } else if (node instanceof ProcessingInstruction) {
                assertEquals(str, (ProcessingInstruction) node, (ProcessingInstruction) node2);
            } else if (node instanceof Attribute) {
                assertEquals(str, (Attribute) node, (Attribute) node2);
            } else {
                if (!(node instanceof Namespace)) {
                    throw new IllegalArgumentException(new StringBuffer().append("Unexpected node type ").append(node.getClass().getName()).toString());
                }
                assertEquals(str, (Namespace) node, (Namespace) node2);
            }
        } catch (ClassCastException e) {
            throw new ComparisonFailure(new StringBuffer().append(str).append("; Mismatched node types: ").append(node.getClass().getName()).append(" != ").append(node2.getClass().getName()).toString(), node.toXML(), node2.toXML());
        }
    }
}
