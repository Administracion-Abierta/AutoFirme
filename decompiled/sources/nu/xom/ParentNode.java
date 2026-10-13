package nu.xom;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/ParentNode.class */
public abstract class ParentNode extends Node {
    Node[] children;
    int childCount = 0;
    String actualBaseURI;

    ParentNode() {
    }

    @Override // nu.xom.Node
    public int getChildCount() {
        return this.childCount;
    }

    public void insertChild(Node node, int i) {
        _insertChild(node, i);
    }

    final void _insertChild(Node node, int i) {
        insertionAllowed(node, i);
        fastInsertChild(node, i);
    }

    void fastInsertChild(Node node, int i) {
        if (i > this.childCount) {
            throw new IndexOutOfBoundsException(new StringBuffer().append("Inserted node at position ").append(i).append(" after children").toString());
        }
        checkCapacity(this.childCount + 1);
        if (i < this.childCount) {
            System.arraycopy(this.children, i, this.children, i + 1, this.childCount - i);
        }
        this.children[i] = node;
        this.childCount++;
        node.setParent(this);
    }

    private void checkCapacity(int i) {
        if (this.children == null) {
            this.children = new Node[1];
        } else if (i >= this.children.length) {
            Node[] nodeArr = new Node[this.children.length * 2];
            System.arraycopy(this.children, 0, nodeArr, 0, this.children.length);
            this.children = nodeArr;
        }
    }

    abstract void insertionAllowed(Node node, int i);

    public void appendChild(Node node) {
        insertChild(node, this.childCount);
    }

    @Override // nu.xom.Node
    public Node getChild(int i) {
        if (this.children == null) {
            throw new IndexOutOfBoundsException("This node has no children");
        }
        return this.children[i];
    }

    public int indexOf(Node node) {
        if (this.children == null) {
            return -1;
        }
        for (int i = 0; i < this.childCount; i++) {
            if (node == this.children[i]) {
                return i;
            }
        }
        return -1;
    }

    public Node removeChild(int i) {
        if (this.children == null) {
            throw new IndexOutOfBoundsException("This node has no children");
        }
        Node node = this.children[i];
        if (node.isElement()) {
            fillInBaseURI((Element) node);
        }
        int i2 = (this.childCount - i) - 1;
        if (i2 > 0) {
            System.arraycopy(this.children, i + 1, this.children, i, i2);
        }
        this.childCount--;
        this.children[this.childCount] = null;
        node.setParent(null);
        return node;
    }

    void fillInBaseURI(Element element) {
        String actualBaseURI = "";
        for (ParentNode parent = element; parent != null && actualBaseURI.equals(""); parent = parent.getParent()) {
            actualBaseURI = parent.getActualBaseURI();
        }
        element.setActualBaseURI(actualBaseURI);
    }

    public Node removeChild(Node node) {
        if (this.children == null) {
            throw new NoSuchChildException("Child does not belong to this node");
        }
        int iIndexOf = indexOf(node);
        if (iIndexOf == -1) {
            throw new NoSuchChildException("Child does not belong to this node");
        }
        if (node.isElement()) {
            fillInBaseURI((Element) node);
        }
        removeChild(iIndexOf);
        node.setParent(null);
        return node;
    }

    public void replaceChild(Node node, Node node2) {
        if (node == null) {
            throw new NullPointerException("Tried to replace null child");
        }
        if (node2 == null) {
            throw new NullPointerException("Tried to replace child with null");
        }
        if (this.children == null) {
            throw new NoSuchChildException("Reference node is not a child of this node.");
        }
        int iIndexOf = indexOf(node);
        if (iIndexOf == -1) {
            throw new NoSuchChildException("Reference node is not a child of this node.");
        }
        if (node == node2) {
            return;
        }
        insertionAllowed(node2, iIndexOf);
        removeChild(iIndexOf);
        insertChild(node2, iIndexOf);
    }

    public abstract void setBaseURI(String str);

    String getActualBaseURI() {
        return this.actualBaseURI == null ? "" : this.actualBaseURI;
    }

    void setActualBaseURI(String str) {
        if (str == null) {
            str = "";
        }
        if (!"".equals(str)) {
            Verifier.checkAbsoluteURI(str);
        }
        this.actualBaseURI = str;
    }

    final String findActualBaseURI() {
        ParentNode parentNode = this;
        while (true) {
            ParentNode parentNode2 = parentNode;
            String actualBaseURI = parentNode2.getActualBaseURI();
            ParentNode parent = parentNode2.getParent();
            if (parent != null && "".equals(actualBaseURI)) {
                parentNode = parent;
            }
            return actualBaseURI;
        }
    }
}
