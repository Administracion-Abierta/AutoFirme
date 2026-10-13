package org.apache.jcp.xml.dsig.internal.dom;

import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/jcp/xml/dsig/internal/dom/DOMStructure.class */
public abstract class DOMStructure implements XMLStructure {
    public abstract void marshal(Node node, String str, DOMCryptoContext dOMCryptoContext) throws MarshalException;

    public final boolean isFeatureSupported(String feature) {
        if (feature == null) {
            throw new NullPointerException();
        }
        return false;
    }

    protected boolean equalsContent(List<XMLStructure> content, List<XMLStructure> otherContent) {
        int size = content.size();
        if (size != otherContent.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            javax.xml.crypto.dom.DOMStructure dOMStructure = (XMLStructure) otherContent.get(i);
            javax.xml.crypto.dom.DOMStructure dOMStructure2 = (XMLStructure) content.get(i);
            if (dOMStructure instanceof javax.xml.crypto.dom.DOMStructure) {
                if (!(dOMStructure2 instanceof javax.xml.crypto.dom.DOMStructure)) {
                    return false;
                }
                Node otherNode = dOMStructure.getNode();
                Node node = dOMStructure2.getNode();
                if (!DOMUtils.nodesEqual(node, otherNode)) {
                    return false;
                }
            } else if (!dOMStructure2.equals(dOMStructure)) {
                return false;
            }
        }
        return true;
    }
}
