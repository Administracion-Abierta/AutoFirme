package org.apache.xml.security.c14n.implementations;

import com.aowagie.text.ElementTags;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.helper.C14nHelper;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/c14n/implementations/Canonicalizer20010315.class */
public abstract class Canonicalizer20010315 extends CanonicalizerBase {
    private boolean firstCall;
    private final XmlAttrStack xmlattrStack;
    private final boolean c14n11;

    public Canonicalizer20010315(boolean includeComments) {
        this(includeComments, false);
    }

    public Canonicalizer20010315(boolean includeComments, boolean c14n11) {
        super(includeComments);
        this.firstCall = true;
        this.xmlattrStack = new XmlAttrStack(c14n11);
        this.c14n11 = c14n11;
    }

    @Override // org.apache.xml.security.c14n.CanonicalizerSpi
    public void engineCanonicalizeXPathNodeSet(Set<Node> xpathNodeSet, String inclusiveNamespaces, OutputStream writer) throws CanonicalizationException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }

    @Override // org.apache.xml.security.c14n.CanonicalizerSpi
    public void engineCanonicalizeSubTree(Node rootNode, String inclusiveNamespaces, OutputStream writer) throws CanonicalizationException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }

    @Override // org.apache.xml.security.c14n.CanonicalizerSpi
    public void engineCanonicalizeSubTree(Node rootNode, String inclusiveNamespaces, boolean propagateDefaultNamespace, OutputStream writer) throws CanonicalizationException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }

    @Override // org.apache.xml.security.c14n.implementations.CanonicalizerBase
    protected void outputAttributesSubtree(Element element, NameSpaceSymbTable ns, Map<String, byte[]> cache, OutputStream writer) throws CanonicalizationException, DOMException, IOException {
        Node n;
        if (!element.hasAttributes() && !this.firstCall) {
            return;
        }
        SortedSet<Attr> result = new TreeSet<>(COMPARE);
        if (element.hasAttributes()) {
            NamedNodeMap attrs = element.getAttributes();
            int attrsLength = attrs.getLength();
            for (int i = 0; i < attrsLength; i++) {
                Attr attribute = (Attr) attrs.item(i);
                String NUri = attribute.getNamespaceURI();
                String NName = attribute.getLocalName();
                String NValue = attribute.getValue();
                if (!"http://www.w3.org/2000/xmlns/".equals(NUri)) {
                    result.add(attribute);
                } else if ((!"xml".equals(NName) || !"http://www.w3.org/XML/1998/namespace".equals(NValue)) && (n = ns.addMappingAndRender(NName, NValue, attribute)) != null) {
                    result.add((Attr) n);
                    if (C14nHelper.namespaceIsRelative(attribute)) {
                        Object[] exArgs = {element.getTagName(), NName, attribute.getNodeValue()};
                        throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", exArgs);
                    }
                }
            }
        }
        if (this.firstCall) {
            ns.getUnrenderedNodes(result);
            this.xmlattrStack.getXmlnsAttr(result);
            this.firstCall = false;
        }
        for (Attr attr : result) {
            outputAttrToWriter(attr.getNodeName(), attr.getNodeValue(), writer, cache);
        }
    }

    @Override // org.apache.xml.security.c14n.implementations.CanonicalizerBase
    protected void outputAttributes(Element element, NameSpaceSymbTable ns, Map<String, byte[]> cache, OutputStream writer) throws CanonicalizationException, DOMException, IOException {
        Node n;
        this.xmlattrStack.push(ns.getLevel());
        boolean isRealVisible = isVisibleDO(element, ns.getLevel()) == 1;
        SortedSet<Attr> result = new TreeSet<>(COMPARE);
        if (element.hasAttributes()) {
            NamedNodeMap attrs = element.getAttributes();
            int attrsLength = attrs.getLength();
            for (int i = 0; i < attrsLength; i++) {
                Attr attribute = (Attr) attrs.item(i);
                String NUri = attribute.getNamespaceURI();
                String NName = attribute.getLocalName();
                String NValue = attribute.getValue();
                if (!"http://www.w3.org/2000/xmlns/".equals(NUri)) {
                    if ("http://www.w3.org/XML/1998/namespace".equals(NUri)) {
                        if (this.c14n11 && ElementTags.ID.equals(NName)) {
                            if (isRealVisible) {
                                result.add(attribute);
                            }
                        } else {
                            this.xmlattrStack.addXmlnsAttr(attribute);
                        }
                    } else if (isRealVisible) {
                        result.add(attribute);
                    }
                } else if (!"xml".equals(NName) || !"http://www.w3.org/XML/1998/namespace".equals(NValue)) {
                    if (isVisible(attribute)) {
                        if ((isRealVisible || !ns.removeMappingIfRender(NName)) && (n = ns.addMappingAndRender(NName, NValue, attribute)) != null) {
                            result.add((Attr) n);
                            if (C14nHelper.namespaceIsRelative(attribute)) {
                                Object[] exArgs = {element.getTagName(), NName, attribute.getNodeValue()};
                                throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", exArgs);
                            }
                        }
                    } else if (isRealVisible && !CanonicalizerBase.XMLNS.equals(NName)) {
                        ns.removeMapping(NName);
                    } else {
                        ns.addMapping(NName, NValue, attribute);
                    }
                }
            }
        }
        if (isRealVisible) {
            Attr xmlns = element.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS);
            Node n2 = null;
            if (xmlns == null) {
                n2 = ns.getMapping(CanonicalizerBase.XMLNS);
            } else if (!isVisible(xmlns)) {
                n2 = ns.addMappingAndRender(CanonicalizerBase.XMLNS, "", getNullNode(xmlns.getOwnerDocument()));
            }
            if (n2 != null) {
                result.add((Attr) n2);
            }
            this.xmlattrStack.getXmlnsAttr(result);
            ns.getUnrenderedNodes(result);
        }
        for (Attr attr : result) {
            outputAttrToWriter(attr.getNodeName(), attr.getNodeValue(), writer, cache);
        }
    }

    @Override // org.apache.xml.security.c14n.implementations.CanonicalizerBase
    protected void circumventBugIfNeeded(XMLSignatureInput input) throws IOException, XMLParserException {
        Document doc;
        if (!input.isNeedsToBeExpanded()) {
            return;
        }
        if (input.getSubNode() != null) {
            doc = XMLUtils.getOwnerDocument(input.getSubNode());
        } else {
            doc = XMLUtils.getOwnerDocument(input.getNodeSet());
        }
        XMLUtils.circumventBug2650(doc);
    }

    @Override // org.apache.xml.security.c14n.implementations.CanonicalizerBase
    protected void handleParent(Element e, NameSpaceSymbTable ns) {
        String Name;
        if (!e.hasAttributes() && e.getNamespaceURI() == null) {
            return;
        }
        this.xmlattrStack.push(-1);
        NamedNodeMap attrs = e.getAttributes();
        int attrsLength = attrs.getLength();
        for (int i = 0; i < attrsLength; i++) {
            Attr attribute = (Attr) attrs.item(i);
            String NName = attribute.getLocalName();
            String NValue = attribute.getNodeValue();
            if ("http://www.w3.org/2000/xmlns/".equals(attribute.getNamespaceURI())) {
                if (!"xml".equals(NName) || !"http://www.w3.org/XML/1998/namespace".equals(NValue)) {
                    ns.addMapping(NName, NValue, attribute);
                }
            } else if ("http://www.w3.org/XML/1998/namespace".equals(attribute.getNamespaceURI()) && (!this.c14n11 || !ElementTags.ID.equals(NName))) {
                this.xmlattrStack.addXmlnsAttr(attribute);
            }
        }
        if (e.getNamespaceURI() != null) {
            String NName2 = e.getPrefix();
            String NValue2 = e.getNamespaceURI();
            if (NName2 == null || NName2.isEmpty()) {
                NName2 = CanonicalizerBase.XMLNS;
                Name = CanonicalizerBase.XMLNS;
            } else {
                Name = "xmlns:" + NName2;
            }
            Attr n = e.getOwnerDocument().createAttributeNS("http://www.w3.org/2000/xmlns/", Name);
            n.setValue(NValue2);
            ns.addMapping(NName2, NValue2, n);
        }
    }
}
