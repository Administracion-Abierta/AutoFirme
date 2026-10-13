package nu.xom;

import nu.xom.jaxen.NamespaceContext;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Node.class */
public abstract class Node {
    private ParentNode parent = null;
    private static NamespaceContext emptyContext = new EmptyNamespaceContext(null);

    /* JADX INFO: renamed from: nu.xom.Node$1, reason: invalid class name */
    /* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Node$1.class */
    static class AnonymousClass1 {
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Node$EmptyNamespaceContext.class */
    private static class EmptyNamespaceContext implements NamespaceContext {
        private EmptyNamespaceContext() {
        }

        @Override // nu.xom.jaxen.NamespaceContext
        public String translateNamespacePrefixToUri(String str) {
            return null;
        }

        /* synthetic */ EmptyNamespaceContext(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    Node() {
    }

    public abstract String getValue();

    public final Document getDocument() {
        Node node;
        Node parent = this;
        while (true) {
            node = parent;
            if (node == null || node.isDocument()) {
                break;
            }
            parent = node.getParent();
        }
        return (Document) node;
    }

    final Node getRoot() {
        ParentNode parent = getParent();
        if (parent == null) {
            return this;
        }
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }
        return parent;
    }

    public String getBaseURI() {
        return this.parent == null ? "" : this.parent.getBaseURI();
    }

    public final ParentNode getParent() {
        return this.parent;
    }

    final void setParent(ParentNode parentNode) {
        this.parent = parentNode;
    }

    public void detach() {
        if (this.parent == null) {
            return;
        }
        if (isAttribute()) {
            ((Element) this.parent).removeAttribute((Attribute) this);
        } else {
            this.parent.removeChild(this);
        }
    }

    public abstract Node getChild(int i);

    public abstract int getChildCount();

    public abstract Node copy();

    public abstract String toXML();

    public final boolean equals(Object obj) {
        return this == obj;
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public final Nodes query(String str, XPathContext xPathContext) {
        if (isDocType()) {
            throw new XPathException("Can't use XPath on a DocType");
        }
        DocumentFragment documentFragment = null;
        Node root = getRoot();
        if (!root.isDocument()) {
            documentFragment = new DocumentFragment();
            documentFragment.appendChild(root);
        }
        try {
            try {
                JaxenConnector jaxenConnector = new JaxenConnector(str);
                if (xPathContext == null) {
                    jaxenConnector.setNamespaceContext(emptyContext);
                } else {
                    jaxenConnector.setNamespaceContext(xPathContext.getJaxenContext());
                }
                Nodes nodes = new Nodes(jaxenConnector.selectNodes(this));
                if (documentFragment != null) {
                    documentFragment.removeChild(0);
                }
                return nodes;
            } catch (XPathException e) {
                e.setXPath(str);
                throw e;
            } catch (Exception e2) {
                XPathException xPathException = new XPathException(new StringBuffer().append("XPath error: ").append(e2.getMessage()).toString(), e2);
                xPathException.setXPath(str);
                throw xPathException;
            }
        } catch (Throwable th) {
            if (documentFragment != null) {
                documentFragment.removeChild(0);
            }
            throw th;
        }
    }

    public final Nodes query(String str) {
        return query(str, null);
    }

    boolean isElement() {
        return false;
    }

    boolean isText() {
        return false;
    }

    boolean isComment() {
        return false;
    }

    boolean isProcessingInstruction() {
        return false;
    }

    boolean isAttribute() {
        return false;
    }

    boolean isDocument() {
        return false;
    }

    boolean isDocType() {
        return false;
    }

    boolean isDocumentFragment() {
        return false;
    }
}
