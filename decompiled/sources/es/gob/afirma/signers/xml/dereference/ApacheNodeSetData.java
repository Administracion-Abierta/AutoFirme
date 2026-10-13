package es.gob.afirma.signers.xml.dereference;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.xml.crypto.NodeSetData;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xml/dereference/ApacheNodeSetData.class */
public final class ApacheNodeSetData implements NodeSetData {
    private final XMLSignatureInput xi;

    ApacheNodeSetData(XMLSignatureInput xMLSignatureInput) {
        this.xi = xMLSignatureInput;
    }

    public Iterator<Node> iterator() {
        if (this.xi.getNodeFilters() != null && !this.xi.getNodeFilters().isEmpty()) {
            return Collections.unmodifiableSet(getNodeSet(this.xi.getNodeFilters())).iterator();
        }
        try {
            return Collections.unmodifiableSet(this.xi.getNodeSet()).iterator();
        } catch (Exception e) {
            throw new RuntimeException("unrecoverable error retrieving nodeset: " + e, e);
        }
    }

    private Set<Node> getNodeSet(List<NodeFilter> list) {
        if (this.xi.isNeedsToBeExpanded()) {
            XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(this.xi.getSubNode()));
        }
        LinkedHashSet<Node> linkedHashSet = new LinkedHashSet();
        XMLUtils.getSet(this.xi.getSubNode(), linkedHashSet, null, !this.xi.isExcludeComments());
        LinkedHashSet linkedHashSet2 = new LinkedHashSet();
        for (Node node : linkedHashSet) {
            Iterator<NodeFilter> it = list.iterator();
            boolean z = false;
            while (it.hasNext() && !z) {
                if (it.next().isNodeInclude(node) != 1) {
                    z = true;
                }
            }
            if (!z) {
                linkedHashSet2.add(node);
            }
        }
        return linkedHashSet2;
    }
}
