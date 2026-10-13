package nu.xom;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/DocumentFragment.class */
class DocumentFragment extends ParentNode {
    DocumentFragment() {
    }

    @Override // nu.xom.ParentNode
    void insertionAllowed(Node node, int i) {
    }

    @Override // nu.xom.ParentNode
    public void setBaseURI(String str) {
        throw new UnsupportedOperationException("XOM bug");
    }

    @Override // nu.xom.Node
    public String getValue() {
        throw new UnsupportedOperationException("XOM bug");
    }

    @Override // nu.xom.Node
    public Node copy() {
        throw new UnsupportedOperationException("XOM bug");
    }

    @Override // nu.xom.Node
    public String toXML() {
        throw new UnsupportedOperationException("XOM bug");
    }

    @Override // nu.xom.Node
    boolean isDocumentFragment() {
        return true;
    }
}
