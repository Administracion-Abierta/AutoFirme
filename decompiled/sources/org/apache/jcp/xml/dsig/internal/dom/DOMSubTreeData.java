package org.apache.jcp.xml.dsig.internal.dom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import javax.xml.crypto.NodeSetData;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSubTreeData.class */
public class DOMSubTreeData implements NodeSetData {
    private boolean excludeComments;
    private Node root;

    public DOMSubTreeData(Node root, boolean excludeComments) {
        this.root = root;
        this.excludeComments = excludeComments;
    }

    public Iterator<Node> iterator() {
        return new DelayedNodeIterator(this.root, this.excludeComments);
    }

    public Node getRoot() {
        return this.root;
    }

    public boolean excludeComments() {
        return this.excludeComments;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSubTreeData$DelayedNodeIterator.class */
    static class DelayedNodeIterator implements Iterator<Node> {
        private Node root;
        private List<Node> nodeSet;
        private ListIterator<Node> li;
        private boolean withComments;

        DelayedNodeIterator(Node root, boolean excludeComments) {
            this.root = root;
            this.withComments = !excludeComments;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            if (this.nodeSet == null) {
                this.nodeSet = dereferenceSameDocumentURI(this.root);
                this.li = this.nodeSet.listIterator();
            }
            return this.li.hasNext();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Node next() {
            if (this.nodeSet == null) {
                this.nodeSet = dereferenceSameDocumentURI(this.root);
                this.li = this.nodeSet.listIterator();
            }
            if (this.li.hasNext()) {
                return this.li.next();
            }
            throw new NoSuchElementException();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private List<Node> dereferenceSameDocumentURI(Node node) {
            List<Node> nodes = new ArrayList<>();
            if (node != null) {
                nodeSetMinusCommentNodes(node, nodes, null);
            }
            return nodes;
        }

        private void nodeSetMinusCommentNodes(Node node, List<Node> nodeSet, Node prevSibling) {
            switch (node.getNodeType()) {
                case 1:
                    NamedNodeMap attrs = node.getAttributes();
                    if (attrs != null) {
                        int len = attrs.getLength();
                        for (int i = 0; i < len; i++) {
                            nodeSet.add(attrs.item(i));
                        }
                    }
                    nodeSet.add(node);
                    Node pSibling = null;
                    Node firstChild = node.getFirstChild();
                    while (true) {
                        Node child = firstChild;
                        if (child != null) {
                            nodeSetMinusCommentNodes(child, nodeSet, pSibling);
                            pSibling = child;
                            firstChild = child.getNextSibling();
                        }
                        break;
                    }
                    break;
                case 3:
                case 4:
                    if (prevSibling == null || (prevSibling.getNodeType() != 3 && prevSibling.getNodeType() != 4)) {
                        nodeSet.add(node);
                    }
                    break;
                case 7:
                    nodeSet.add(node);
                    break;
                case 8:
                    if (this.withComments) {
                        nodeSet.add(node);
                    }
                    break;
                case 9:
                    Node pSibling2 = null;
                    Node firstChild2 = node.getFirstChild();
                    while (true) {
                        Node child2 = firstChild2;
                        if (child2 != null) {
                            nodeSetMinusCommentNodes(child2, nodeSet, pSibling2);
                            pSibling2 = child2;
                            firstChild2 = child2.getNextSibling();
                        }
                        break;
                    }
                    break;
            }
        }
    }
}
