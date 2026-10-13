package org.apache.xml.security.c14n.implementations;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.helper.C14nHelper;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/c14n/implementations/Canonicalizer20010315Excl.class */
public abstract class Canonicalizer20010315Excl extends CanonicalizerBase {
    private SortedSet<String> inclusiveNSSet;
    private boolean propagateDefaultNamespace;

    public Canonicalizer20010315Excl(boolean includeComments) {
        super(includeComments);
        this.inclusiveNSSet = Collections.emptySortedSet();
        this.propagateDefaultNamespace = false;
    }

    @Override // org.apache.xml.security.c14n.implementations.CanonicalizerBase, org.apache.xml.security.c14n.CanonicalizerSpi
    public void engineCanonicalizeSubTree(Node rootNode, OutputStream writer) throws CanonicalizationException, CloneNotSupportedException {
        engineCanonicalizeSubTree(rootNode, "", (Node) null, writer);
    }

    @Override // org.apache.xml.security.c14n.CanonicalizerSpi
    public void engineCanonicalizeSubTree(Node rootNode, String inclusiveNamespaces, OutputStream writer) throws CanonicalizationException, CloneNotSupportedException {
        engineCanonicalizeSubTree(rootNode, inclusiveNamespaces, (Node) null, writer);
    }

    @Override // org.apache.xml.security.c14n.CanonicalizerSpi
    public void engineCanonicalizeSubTree(Node rootNode, String inclusiveNamespaces, boolean propagateDefaultNamespace, OutputStream writer) throws CanonicalizationException, CloneNotSupportedException {
        this.propagateDefaultNamespace = propagateDefaultNamespace;
        engineCanonicalizeSubTree(rootNode, inclusiveNamespaces, (Node) null, writer);
    }

    public void engineCanonicalizeSubTree(Node rootNode, String inclusiveNamespaces, Node excl, OutputStream writer) throws CanonicalizationException, CloneNotSupportedException {
        this.inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(inclusiveNamespaces);
        super.engineCanonicalizeSubTree(rootNode, excl, writer);
    }

    public void engineCanonicalize(XMLSignatureInput rootNode, String inclusiveNamespaces, OutputStream writer, boolean secureValidation) throws CanonicalizationException {
        this.inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(inclusiveNamespaces);
        super.engineCanonicalize(rootNode, writer, secureValidation);
    }

    @Override // org.apache.xml.security.c14n.CanonicalizerSpi
    public void engineCanonicalizeXPathNodeSet(Set<Node> xpathNodeSet, String inclusiveNamespaces, OutputStream writer) throws CanonicalizationException, CloneNotSupportedException {
        this.inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(inclusiveNamespaces);
        super.engineCanonicalizeXPathNodeSet(xpathNodeSet, writer);
    }

    @Override // org.apache.xml.security.c14n.implementations.CanonicalizerBase
    protected void outputAttributesSubtree(Element element, NameSpaceSymbTable ns, Map<String, byte[]> cache, OutputStream writer) throws CanonicalizationException, DOMException, IOException {
        String prefix;
        SortedSet<Attr> result = new TreeSet<>(COMPARE);
        SortedSet<String> visiblyUtilized = new TreeSet<>();
        if (!this.inclusiveNSSet.isEmpty()) {
            visiblyUtilized.addAll(this.inclusiveNSSet);
        }
        if (element.hasAttributes()) {
            NamedNodeMap attrs = element.getAttributes();
            int attrsLength = attrs.getLength();
            for (int i = 0; i < attrsLength; i++) {
                Attr attribute = (Attr) attrs.item(i);
                String NName = attribute.getLocalName();
                String NNodeValue = attribute.getNodeValue();
                if (!"http://www.w3.org/2000/xmlns/".equals(attribute.getNamespaceURI())) {
                    String prefix2 = attribute.getPrefix();
                    if (prefix2 != null && !prefix2.equals(CanonicalizerBase.XML) && !prefix2.equals(CanonicalizerBase.XMLNS)) {
                        visiblyUtilized.add(prefix2);
                    }
                    result.add(attribute);
                } else if ((!CanonicalizerBase.XML.equals(NName) || !"http://www.w3.org/XML/1998/namespace".equals(NNodeValue)) && ns.addMapping(NName, NNodeValue, attribute) && C14nHelper.namespaceIsRelative(NNodeValue)) {
                    Object[] exArgs = {element.getTagName(), NName, attribute.getNodeValue()};
                    throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", exArgs);
                }
            }
        }
        if (this.propagateDefaultNamespace && ns.getLevel() == 1 && this.inclusiveNSSet.contains(CanonicalizerBase.XMLNS) && ns.getMappingWithoutRendered(CanonicalizerBase.XMLNS) == null) {
            ns.removeMapping(CanonicalizerBase.XMLNS);
            ns.addMapping(CanonicalizerBase.XMLNS, "", getNullNode(element.getOwnerDocument()));
        }
        if (element.getNamespaceURI() != null && element.getPrefix() != null && element.getPrefix().length() != 0) {
            prefix = element.getPrefix();
        } else {
            prefix = CanonicalizerBase.XMLNS;
        }
        visiblyUtilized.add(prefix);
        for (String s : visiblyUtilized) {
            Attr key = ns.getMapping(s);
            if (key != null) {
                result.add(key);
            }
        }
        for (Attr attr : result) {
            outputAttrToWriter(attr.getNodeName(), attr.getNodeValue(), writer, cache);
        }
    }

    @Override // org.apache.xml.security.c14n.implementations.CanonicalizerBase
    protected void outputAttributes(Element element, NameSpaceSymbTable ns, Map<String, byte[]> cache, OutputStream writer) throws CanonicalizationException, DOMException, IOException {
        String prefix;
        Node n;
        SortedSet<Attr> result = new TreeSet<>(COMPARE);
        Set<String> visiblyUtilized = null;
        boolean isOutputElement = isVisibleDO(element, ns.getLevel()) == 1;
        if (isOutputElement) {
            visiblyUtilized = new TreeSet<>();
            if (!this.inclusiveNSSet.isEmpty()) {
                visiblyUtilized.addAll(this.inclusiveNSSet);
            }
        }
        if (element.hasAttributes()) {
            NamedNodeMap attrs = element.getAttributes();
            int attrsLength = attrs.getLength();
            for (int i = 0; i < attrsLength; i++) {
                Attr attribute = (Attr) attrs.item(i);
                String NName = attribute.getLocalName();
                String NNodeValue = attribute.getNodeValue();
                if (!"http://www.w3.org/2000/xmlns/".equals(attribute.getNamespaceURI())) {
                    if (isVisible(attribute) && isOutputElement) {
                        String prefix2 = attribute.getPrefix();
                        if (prefix2 != null && !prefix2.equals(CanonicalizerBase.XML) && !prefix2.equals(CanonicalizerBase.XMLNS)) {
                            visiblyUtilized.add(prefix2);
                        }
                        result.add(attribute);
                    }
                } else if (isOutputElement && !isVisible(attribute) && !CanonicalizerBase.XMLNS.equals(NName)) {
                    ns.removeMappingIfNotRender(NName);
                } else {
                    if (!isOutputElement && isVisible(attribute) && this.inclusiveNSSet.contains(NName) && !ns.removeMappingIfRender(NName) && (n = ns.addMappingAndRender(NName, NNodeValue, attribute)) != null) {
                        result.add((Attr) n);
                        if (C14nHelper.namespaceIsRelative(attribute)) {
                            Object[] exArgs = {element.getTagName(), NName, attribute.getNodeValue()};
                            throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", exArgs);
                        }
                    }
                    if (ns.addMapping(NName, NNodeValue, attribute) && C14nHelper.namespaceIsRelative(NNodeValue)) {
                        Object[] exArgs2 = {element.getTagName(), NName, attribute.getNodeValue()};
                        throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", exArgs2);
                    }
                }
            }
        }
        if (isOutputElement) {
            Attr xmlns = element.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS);
            if (xmlns != null && !isVisible(xmlns)) {
                ns.addMapping(CanonicalizerBase.XMLNS, "", getNullNode(xmlns.getOwnerDocument()));
            }
            if (element.getNamespaceURI() != null && element.getPrefix() != null && element.getPrefix().length() != 0) {
                prefix = element.getPrefix();
            } else {
                prefix = CanonicalizerBase.XMLNS;
            }
            visiblyUtilized.add(prefix);
            for (String s : visiblyUtilized) {
                Attr key = ns.getMapping(s);
                if (key != null) {
                    result.add(key);
                }
            }
        }
        for (Attr attr : result) {
            outputAttrToWriter(attr.getNodeName(), attr.getNodeValue(), writer, cache);
        }
    }

    @Override // org.apache.xml.security.c14n.implementations.CanonicalizerBase
    protected void circumventBugIfNeeded(XMLSignatureInput input) throws IOException, XMLParserException {
        Document doc;
        if (!input.isNeedsToBeExpanded() || this.inclusiveNSSet.isEmpty()) {
            return;
        }
        if (input.getSubNode() != null) {
            doc = XMLUtils.getOwnerDocument(input.getSubNode());
        } else {
            doc = XMLUtils.getOwnerDocument(input.getNodeSet());
        }
        XMLUtils.circumventBug2650(doc);
    }
}
