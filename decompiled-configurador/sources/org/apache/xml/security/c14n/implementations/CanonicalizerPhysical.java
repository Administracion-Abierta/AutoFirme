package org.apache.xml.security.c14n.implementations;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/c14n/implementations/CanonicalizerPhysical.class */
public class CanonicalizerPhysical extends CanonicalizerBase {
    public CanonicalizerPhysical() {
        super(true);
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
        if (element.hasAttributes()) {
            SortedSet<Attr> result = new TreeSet<>(COMPARE);
            NamedNodeMap attrs = element.getAttributes();
            int attrsLength = attrs.getLength();
            for (int i = 0; i < attrsLength; i++) {
                Attr attribute = (Attr) attrs.item(i);
                result.add(attribute);
            }
            for (Attr attr : result) {
                outputAttrToWriter(attr.getNodeName(), attr.getNodeValue(), writer, cache);
            }
        }
    }

    @Override // org.apache.xml.security.c14n.implementations.CanonicalizerBase
    protected void outputAttributes(Element element, NameSpaceSymbTable ns, Map<String, byte[]> cache, OutputStream writer) throws CanonicalizationException, DOMException, IOException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }

    @Override // org.apache.xml.security.c14n.implementations.CanonicalizerBase
    protected void circumventBugIfNeeded(XMLSignatureInput input) throws IOException, XMLParserException {
    }

    @Override // org.apache.xml.security.c14n.implementations.CanonicalizerBase
    protected void handleParent(Element e, NameSpaceSymbTable ns) {
    }

    @Override // org.apache.xml.security.c14n.CanonicalizerSpi
    public final String engineGetURI() {
        return "http://santuario.apache.org/c14n/physical";
    }

    @Override // org.apache.xml.security.c14n.implementations.CanonicalizerBase
    protected void outputPItoWriter(ProcessingInstruction currentPI, OutputStream writer, int position) throws IOException {
        super.outputPItoWriter(currentPI, writer, 0);
    }

    @Override // org.apache.xml.security.c14n.implementations.CanonicalizerBase
    protected void outputCommentToWriter(Comment currentComment, OutputStream writer, int position) throws IOException {
        super.outputCommentToWriter(currentComment, writer, 0);
    }
}
