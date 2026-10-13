package org.apache.xml.security.c14n.implementations;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.CanonicalizerSpi;
import org.apache.xml.security.c14n.helper.AttrCompare;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.signature.NodeFilter;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.XMLUtils;
import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.tls.CipherSuite;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/c14n/implementations/CanonicalizerBase.class */
public abstract class CanonicalizerBase extends CanonicalizerSpi {
    public static final String XML = "xml";
    public static final String XMLNS = "xmlns";
    public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
    public static final String XML_LANG_URI = "http://www.w3.org/XML/1998/namespace";
    protected static final int NODE_BEFORE_DOCUMENT_ELEMENT = -1;
    protected static final int NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT = 0;
    protected static final int NODE_AFTER_DOCUMENT_ELEMENT = 1;
    private boolean includeComments;
    private List<NodeFilter> nodeFilter;
    private Set<Node> xpathNodeSet;
    private Attr nullNode;
    protected static final AttrCompare COMPARE = new AttrCompare();
    private static final byte[] END_PI = {63, 62};
    private static final byte[] BEGIN_PI = {60, 63};
    private static final byte[] END_COMM = {45, 45, 62};
    private static final byte[] BEGIN_COMM = {60, 33, 45, 45};
    private static final byte[] XA = {38, 35, 120, 65, 59};
    private static final byte[] X9 = {38, 35, 120, 57, 59};
    private static final byte[] QUOT = {38, 113, 117, 111, 116, 59};
    private static final byte[] XD = {38, 35, 120, 68, 59};
    private static final byte[] GT = {38, 103, 116, 59};
    private static final byte[] LT = {38, 108, 116, 59};
    private static final byte[] END_TAG = {60, 47};
    private static final byte[] AMP = {38, 97, 109, 112, 59};
    private static final byte[] EQUALS_STR = {61, 34};

    abstract void outputAttributes(Element element, NameSpaceSymbTable nameSpaceSymbTable, Map<String, byte[]> map, OutputStream outputStream) throws CanonicalizationException, DOMException, IOException;

    abstract void outputAttributesSubtree(Element element, NameSpaceSymbTable nameSpaceSymbTable, Map<String, byte[]> map, OutputStream outputStream) throws CanonicalizationException, DOMException, IOException;

    abstract void circumventBugIfNeeded(XMLSignatureInput xMLSignatureInput) throws IOException, XMLParserException;

    protected CanonicalizerBase(boolean includeComments) {
        this.includeComments = includeComments;
    }

    @Override // org.apache.xml.security.c14n.CanonicalizerSpi
    public void engineCanonicalizeSubTree(Node rootNode, OutputStream writer) throws CanonicalizationException, CloneNotSupportedException {
        engineCanonicalizeSubTree(rootNode, (Node) null, writer);
    }

    @Override // org.apache.xml.security.c14n.CanonicalizerSpi
    public void engineCanonicalizeXPathNodeSet(Set<Node> xpathNodeSet, OutputStream writer) throws CanonicalizationException, CloneNotSupportedException {
        this.xpathNodeSet = xpathNodeSet;
        engineCanonicalizeXPathNodeSetInternal(XMLUtils.getOwnerDocument(this.xpathNodeSet), writer);
    }

    public void engineCanonicalize(XMLSignatureInput input, OutputStream writer, boolean secureValidation) throws CanonicalizationException {
        try {
            if (input.isExcludeComments()) {
                this.includeComments = false;
            }
            if (input.isOctetStream()) {
                engineCanonicalize(input.getBytes(), writer, secureValidation);
            } else if (input.isElement()) {
                engineCanonicalizeSubTree(input.getSubNode(), input.getExcludeNode(), writer);
            } else if (input.isNodeSet()) {
                this.nodeFilter = input.getNodeFilters();
                circumventBugIfNeeded(input);
                if (input.getSubNode() != null) {
                    engineCanonicalizeXPathNodeSetInternal(input.getSubNode(), writer);
                } else {
                    engineCanonicalizeXPathNodeSet(input.getNodeSet(), writer);
                }
            }
        } catch (IOException | XMLParserException ex) {
            throw new CanonicalizationException(ex);
        }
    }

    protected void engineCanonicalizeSubTree(Node rootNode, Node excludeNode, OutputStream writer) throws CanonicalizationException, CloneNotSupportedException {
        try {
            NameSpaceSymbTable ns = new NameSpaceSymbTable();
            int nodeLevel = -1;
            if (rootNode != null && 1 == rootNode.getNodeType()) {
                getParentNameSpaces((Element) rootNode, ns);
                nodeLevel = 0;
            }
            canonicalizeSubTree(rootNode, ns, rootNode, nodeLevel, excludeNode, writer);
            writer.flush();
        } catch (UnsupportedEncodingException ex) {
            throw new CanonicalizationException(ex);
        } catch (IOException ex2) {
            throw new CanonicalizationException(ex2);
        }
    }

    private void canonicalizeSubTree(Node currentNode, NameSpaceSymbTable ns, Node endnode, int documentLevel, Node excludeNode, OutputStream writer) throws CanonicalizationException, IOException, CloneNotSupportedException {
        if (currentNode == null || isVisibleInt(currentNode) == -1) {
            return;
        }
        Node sibling = null;
        Node parentNode = null;
        Map<String, byte[]> cache = new HashMap<>();
        while (true) {
            switch (currentNode.getNodeType()) {
                case 1:
                    documentLevel = 0;
                    if (currentNode != excludeNode) {
                        Element currentElement = (Element) currentNode;
                        ns.outputNodePush();
                        writer.write(60);
                        String name = currentElement.getTagName();
                        UtfHelpper.writeByte(name, writer, cache);
                        outputAttributesSubtree(currentElement, ns, cache, writer);
                        writer.write(62);
                        sibling = currentNode.getFirstChild();
                        if (sibling == null) {
                            writer.write((byte[]) END_TAG.clone());
                            UtfHelpper.writeStringToUtf8(name, writer);
                            writer.write(62);
                            ns.outputNodePop();
                            if (parentNode != null) {
                                sibling = currentNode.getNextSibling();
                            }
                        } else {
                            parentNode = currentElement;
                        }
                    }
                    break;
                case 2:
                case 6:
                case 12:
                    throw new CanonicalizationException("empty", new Object[]{"illegal node type during traversal"});
                case 3:
                case 4:
                    outputTextToWriter(currentNode.getNodeValue(), writer);
                    break;
                case 7:
                    outputPItoWriter((ProcessingInstruction) currentNode, writer, documentLevel);
                    break;
                case 8:
                    if (this.includeComments) {
                        outputCommentToWriter((Comment) currentNode, writer, documentLevel);
                    }
                    break;
                case 9:
                case 11:
                    ns.outputNodePush();
                    sibling = currentNode.getFirstChild();
                    break;
            }
            while (sibling == null && parentNode != null) {
                writer.write((byte[]) END_TAG.clone());
                UtfHelpper.writeByte(((Element) parentNode).getTagName(), writer, cache);
                writer.write(62);
                ns.outputNodePop();
                if (parentNode == endnode) {
                    return;
                }
                sibling = parentNode.getNextSibling();
                parentNode = parentNode.getParentNode();
                if (parentNode == null || 1 != parentNode.getNodeType()) {
                    documentLevel = 1;
                    parentNode = null;
                }
            }
            if (sibling == null) {
                return;
            }
            currentNode = sibling;
            sibling = currentNode.getNextSibling();
        }
    }

    private void engineCanonicalizeXPathNodeSetInternal(Node doc, OutputStream writer) throws CanonicalizationException, CloneNotSupportedException {
        try {
            canonicalizeXPathNodeSet(doc, doc, writer);
            writer.flush();
        } catch (IOException ex) {
            throw new CanonicalizationException(ex);
        }
    }

    private void canonicalizeXPathNodeSet(Node currentNode, Node endnode, OutputStream writer) throws CanonicalizationException, IOException, CloneNotSupportedException {
        if (isVisibleInt(currentNode) == -1) {
            return;
        }
        NameSpaceSymbTable ns = new NameSpaceSymbTable();
        if (currentNode != null && 1 == currentNode.getNodeType()) {
            getParentNameSpaces((Element) currentNode, ns);
        }
        if (currentNode == null) {
            return;
        }
        Node sibling = null;
        Node parentNode = null;
        int documentLevel = -1;
        Map<String, byte[]> cache = new HashMap<>();
        while (true) {
            switch (currentNode.getNodeType()) {
                case 1:
                    documentLevel = 0;
                    Element currentElement = (Element) currentNode;
                    String name = null;
                    int i = isVisibleDO(currentNode, ns.getLevel());
                    if (i == -1) {
                        sibling = currentNode.getNextSibling();
                    } else {
                        boolean currentNodeIsVisible = i == 1;
                        if (currentNodeIsVisible) {
                            ns.outputNodePush();
                            writer.write(60);
                            name = currentElement.getTagName();
                            UtfHelpper.writeByte(name, writer, cache);
                        } else {
                            ns.push();
                        }
                        outputAttributes(currentElement, ns, cache, writer);
                        if (currentNodeIsVisible) {
                            writer.write(62);
                        }
                        sibling = currentNode.getFirstChild();
                        if (sibling == null) {
                            if (currentNodeIsVisible) {
                                writer.write((byte[]) END_TAG.clone());
                                UtfHelpper.writeByte(name, writer, cache);
                                writer.write(62);
                                ns.outputNodePop();
                            } else {
                                ns.pop();
                            }
                            if (parentNode != null) {
                                sibling = currentNode.getNextSibling();
                            }
                        } else {
                            parentNode = currentElement;
                        }
                    }
                    break;
                case 2:
                case 6:
                case 12:
                    throw new CanonicalizationException("empty", new Object[]{"illegal node type during traversal"});
                case 3:
                case 4:
                    if (isVisible(currentNode)) {
                        outputTextToWriter(currentNode.getNodeValue(), writer);
                        Node nextSibling = currentNode.getNextSibling();
                        while (true) {
                            Node nextSibling2 = nextSibling;
                            if (nextSibling2 != null && (nextSibling2.getNodeType() == 3 || nextSibling2.getNodeType() == 4)) {
                                outputTextToWriter(nextSibling2.getNodeValue(), writer);
                                sibling = nextSibling2.getNextSibling();
                                nextSibling = nextSibling2.getNextSibling();
                            }
                        }
                    }
                    break;
                case 7:
                    if (isVisible(currentNode)) {
                        outputPItoWriter((ProcessingInstruction) currentNode, writer, documentLevel);
                    }
                    break;
                case 8:
                    if (this.includeComments && isVisibleDO(currentNode, ns.getLevel()) == 1) {
                        outputCommentToWriter((Comment) currentNode, writer, documentLevel);
                    }
                    break;
                case 9:
                case 11:
                    ns.outputNodePush();
                    sibling = currentNode.getFirstChild();
                    break;
            }
            while (sibling == null && parentNode != null) {
                if (isVisible(parentNode)) {
                    writer.write((byte[]) END_TAG.clone());
                    UtfHelpper.writeByte(((Element) parentNode).getTagName(), writer, cache);
                    writer.write(62);
                    ns.outputNodePop();
                } else {
                    ns.pop();
                }
                if (parentNode == endnode) {
                    return;
                }
                sibling = parentNode.getNextSibling();
                parentNode = parentNode.getParentNode();
                if (parentNode == null || 1 != parentNode.getNodeType()) {
                    parentNode = null;
                    documentLevel = 1;
                }
            }
            if (sibling == null) {
                return;
            }
            currentNode = sibling;
            sibling = currentNode.getNextSibling();
        }
    }

    protected int isVisibleDO(Node currentNode, int level) throws CanonicalizationException {
        if (this.nodeFilter != null) {
            for (NodeFilter filter : this.nodeFilter) {
                try {
                    int i = filter.isNodeIncludeDO(currentNode, level);
                    if (i != 1) {
                        return i;
                    }
                } catch (Exception e) {
                    throw new CanonicalizationException(e);
                }
            }
        }
        if (this.xpathNodeSet != null && !this.xpathNodeSet.contains(currentNode)) {
            return 0;
        }
        return 1;
    }

    protected int isVisibleInt(Node currentNode) throws CanonicalizationException {
        if (this.nodeFilter != null) {
            for (NodeFilter filter : this.nodeFilter) {
                try {
                    int i = filter.isNodeInclude(currentNode);
                    if (i != 1) {
                        return i;
                    }
                } catch (Exception e) {
                    throw new CanonicalizationException(e);
                }
            }
        }
        if (this.xpathNodeSet != null && !this.xpathNodeSet.contains(currentNode)) {
            return 0;
        }
        return 1;
    }

    protected boolean isVisible(Node currentNode) throws CanonicalizationException {
        if (this.nodeFilter != null) {
            for (NodeFilter filter : this.nodeFilter) {
                try {
                    if (filter.isNodeInclude(currentNode) != 1) {
                        return false;
                    }
                } catch (Exception e) {
                    throw new CanonicalizationException(e);
                }
            }
        }
        return this.xpathNodeSet == null || this.xpathNodeSet.contains(currentNode);
    }

    protected void handleParent(Element e, NameSpaceSymbTable ns) {
        String Name;
        if (!e.hasAttributes() && e.getNamespaceURI() == null) {
            return;
        }
        NamedNodeMap attrs = e.getAttributes();
        int attrsLength = attrs.getLength();
        for (int i = 0; i < attrsLength; i++) {
            Attr attribute = (Attr) attrs.item(i);
            String NName = attribute.getLocalName();
            String NValue = attribute.getNodeValue();
            if ("http://www.w3.org/2000/xmlns/".equals(attribute.getNamespaceURI()) && (!XML.equals(NName) || !"http://www.w3.org/XML/1998/namespace".equals(NValue))) {
                ns.addMapping(NName, NValue, attribute);
            }
        }
        if (e.getNamespaceURI() != null) {
            String NName2 = e.getPrefix();
            String NValue2 = e.getNamespaceURI();
            if (NName2 == null || NName2.isEmpty()) {
                NName2 = XMLNS;
                Name = XMLNS;
            } else {
                Name = "xmlns:" + NName2;
            }
            Attr n = e.getOwnerDocument().createAttributeNS("http://www.w3.org/2000/xmlns/", Name);
            n.setValue(NValue2);
            ns.addMapping(NName2, NValue2, n);
        }
    }

    private void getParentNameSpaces(Element el, NameSpaceSymbTable ns) {
        Node n1 = el.getParentNode();
        if (n1 == null || 1 != n1.getNodeType()) {
            return;
        }
        List<Element> parents = new ArrayList<>();
        Node parentNode = n1;
        while (true) {
            Node parent = parentNode;
            if (parent == null || 1 != parent.getNodeType()) {
                break;
            }
            parents.add((Element) parent);
            parentNode = parent.getParentNode();
        }
        ListIterator<Element> it = parents.listIterator(parents.size());
        while (it.hasPrevious()) {
            Element ele = it.previous();
            handleParent(ele, ns);
        }
        parents.clear();
        Attr nsprefix = ns.getMappingWithoutRendered(XMLNS);
        if (nsprefix != null && nsprefix.getValue().length() == 0) {
            ns.addMappingAndRender(XMLNS, "", getNullNode(nsprefix.getOwnerDocument()));
        }
    }

    protected static final void outputAttrToWriter(String name, String value, OutputStream writer, Map<String, byte[]> cache) throws IOException, CloneNotSupportedException {
        Object objClone;
        writer.write(32);
        UtfHelpper.writeByte(name, writer, cache);
        writer.write((byte[]) EQUALS_STR.clone());
        int length = value.length();
        int i = 0;
        while (i < length) {
            int c = value.codePointAt(i);
            i += Character.charCount(c);
            switch (c) {
                case 9:
                    objClone = X9.clone();
                    break;
                case 10:
                    objClone = XA.clone();
                    break;
                case 13:
                    objClone = XD.clone();
                    break;
                case 34:
                    objClone = QUOT.clone();
                    break;
                case EACTags.CARD_EFFECTIVE_DATE /* 38 */:
                    objClone = AMP.clone();
                    break;
                case 60:
                    objClone = LT.clone();
                    break;
                default:
                    if (c < 128) {
                        writer.write(c);
                    } else {
                        UtfHelpper.writeCodePointToUtf8(c, writer);
                        continue;
                    }
                    break;
            }
            byte[] toWrite = (byte[]) objClone;
            writer.write(toWrite);
        }
        writer.write(34);
    }

    protected void outputPItoWriter(ProcessingInstruction currentPI, OutputStream writer, int position) throws IOException {
        if (position == 1) {
            writer.write(10);
        }
        writer.write((byte[]) BEGIN_PI.clone());
        String target = currentPI.getTarget();
        int length = target.length();
        int i = 0;
        while (i < length) {
            int c = target.codePointAt(i);
            i += Character.charCount(c);
            if (c == 13) {
                writer.write((byte[]) XD.clone());
            } else if (c < 128) {
                writer.write(c);
            } else {
                UtfHelpper.writeCodePointToUtf8(c, writer);
            }
        }
        String data = currentPI.getData();
        int length2 = data.length();
        if (length2 > 0) {
            writer.write(32);
            int i2 = 0;
            while (i2 < length2) {
                int c2 = data.codePointAt(i2);
                i2 += Character.charCount(c2);
                if (c2 == 13) {
                    writer.write((byte[]) XD.clone());
                } else {
                    UtfHelpper.writeCodePointToUtf8(c2, writer);
                }
            }
        }
        writer.write((byte[]) END_PI.clone());
        if (position == -1) {
            writer.write(10);
        }
    }

    protected void outputCommentToWriter(Comment currentComment, OutputStream writer, int position) throws IOException {
        if (position == 1) {
            writer.write(10);
        }
        writer.write((byte[]) BEGIN_COMM.clone());
        String data = currentComment.getData();
        int length = data.length();
        int i = 0;
        while (i < length) {
            int c = data.codePointAt(i);
            i += Character.charCount(c);
            if (c == 13) {
                writer.write((byte[]) XD.clone());
            } else if (c < 128) {
                writer.write(c);
            } else {
                UtfHelpper.writeCodePointToUtf8(c, writer);
            }
        }
        writer.write((byte[]) END_COMM.clone());
        if (position == -1) {
            writer.write(10);
        }
    }

    private static final void outputTextToWriter(String text, OutputStream writer) throws IOException, CloneNotSupportedException {
        Object objClone;
        int length = text.length();
        int i = 0;
        while (i < length) {
            int c = text.codePointAt(i);
            i += Character.charCount(c);
            switch (c) {
                case 13:
                    objClone = XD.clone();
                    break;
                case EACTags.CARD_EFFECTIVE_DATE /* 38 */:
                    objClone = AMP.clone();
                    break;
                case 60:
                    objClone = LT.clone();
                    break;
                case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
                    objClone = GT.clone();
                    break;
                default:
                    if (c < 128) {
                        writer.write(c);
                    } else {
                        UtfHelpper.writeCodePointToUtf8(c, writer);
                        continue;
                    }
                    break;
            }
            byte[] toWrite = (byte[]) objClone;
            writer.write(toWrite);
        }
    }

    protected Attr getNullNode(Document ownerDocument) {
        if (this.nullNode == null) {
            try {
                this.nullNode = ownerDocument.createAttributeNS("http://www.w3.org/2000/xmlns/", XMLNS);
                this.nullNode.setValue("");
            } catch (Exception e) {
                throw new RuntimeException("Unable to create nullNode: " + e);
            }
        }
        return this.nullNode;
    }
}
