package org.apache.jcp.xml.dsig.internal.dom;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.xml.crypto.NodeSetData;
import org.apache.xml.security.signature.NodeFilter;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/ApacheNodeSetData.class */
public class ApacheNodeSetData implements ApacheData, NodeSetData {
    private XMLSignatureInput xi;

    public ApacheNodeSetData(XMLSignatureInput xi) {
        this.xi = xi;
    }

    public Iterator<Node> iterator() {
        try {
            if (this.xi.getNodeFilters() != null && !this.xi.getNodeFilters().isEmpty()) {
                return Collections.unmodifiableSet(getNodeSet(this.xi.getNodeFilters())).iterator();
            }
            return Collections.unmodifiableSet(this.xi.getNodeSet()).iterator();
        } catch (Exception e) {
            throw new RuntimeException("unrecoverable error retrieving nodeset", e);
        }
    }

    @Override // org.apache.jcp.xml.dsig.internal.dom.ApacheData
    public XMLSignatureInput getXMLSignatureInput() {
        return this.xi;
    }

    private Set<Node> getNodeSet(List<NodeFilter> nodeFilters) throws TransformationException {
        if (this.xi.isNeedsToBeExpanded()) {
            XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(this.xi.getSubNode()));
        }
        Set<Node> inputSet = new LinkedHashSet<>();
        XMLUtils.getSet(this.xi.getSubNode(), inputSet, null, !this.xi.isExcludeComments());
        Set<Node> nodeSet = new LinkedHashSet<>();
        for (Node currentNode : inputSet) {
            Iterator<NodeFilter> it = nodeFilters.iterator();
            boolean skipNode = false;
            while (it.hasNext() && !skipNode) {
                NodeFilter nf = it.next();
                if (nf.isNodeInclude(currentNode) != 1) {
                    skipNode = true;
                }
            }
            if (!skipNode) {
                nodeSet.add(currentNode);
            }
        }
        return nodeSet;
    }
}
