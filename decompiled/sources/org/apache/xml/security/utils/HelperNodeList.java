package org.apache.xml.security.utils;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/HelperNodeList.class */
public class HelperNodeList implements NodeList {
    private final List<Node> nodes;
    private final boolean allNodesMustHaveSameParent;

    public HelperNodeList() {
        this(false);
    }

    public HelperNodeList(boolean allNodesMustHaveSameParent) {
        this.nodes = new ArrayList();
        this.allNodesMustHaveSameParent = allNodesMustHaveSameParent;
    }

    @Override // org.w3c.dom.NodeList
    public Node item(int index) {
        return this.nodes.get(index);
    }

    @Override // org.w3c.dom.NodeList
    public int getLength() {
        return this.nodes.size();
    }

    public void appendChild(Node node) throws IllegalArgumentException {
        if (this.allNodesMustHaveSameParent && getLength() > 0 && item(0).getParentNode() != node.getParentNode()) {
            throw new IllegalArgumentException("Nodes have not the same Parent");
        }
        this.nodes.add(node);
    }

    public Document getOwnerDocument() {
        if (getLength() == 0) {
            return null;
        }
        return XMLUtils.getOwnerDocument(item(0));
    }
}
