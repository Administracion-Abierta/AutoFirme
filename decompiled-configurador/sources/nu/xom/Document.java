package nu.xom;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Document.class */
public class Document extends ParentNode {
    public Document(Element element) {
        _insertChild(element, 0);
    }

    public Document(Document document) {
        insertChild(document.getRootElement().copy(), 0);
        int childCount = document.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Node child = document.getChild(i);
            if (!child.isElement()) {
                insertChild(child.copy(), i);
            }
        }
        this.actualBaseURI = document.actualBaseURI;
    }

    @Override // nu.xom.ParentNode
    final void insertionAllowed(Node node, int i) {
        if (node == null) {
            throw new NullPointerException("Tried to insert a null child in the document");
        }
        if (node.getParent() != null) {
            throw new MultipleParentException("Child already has a parent.");
        }
        if (node.isComment() || node.isProcessingInstruction()) {
            return;
        }
        if (node.isDocType()) {
            if (i > getRootPosition()) {
                throw new IllegalAddException("Cannot add a document type declaration after the root element");
            }
            if (getDocType() != null) {
                throw new IllegalAddException("Tried to insert a second DOCTYPE");
            }
            return;
        }
        if (!node.isElement()) {
            throw new IllegalAddException(new StringBuffer().append("Cannot add a ").append(node.getClass().getName()).append(" to a Document.").toString());
        }
        if (getChildCount() != 0) {
            throw new IllegalAddException("Cannot add a second root element to a Document.");
        }
    }

    private int getRootPosition() {
        int i = 0;
        while (!getChild(i).isElement()) {
            i++;
        }
        return i;
    }

    public final DocType getDocType() {
        for (int i = 0; i < getChildCount(); i++) {
            Node child = getChild(i);
            if (child.isDocType()) {
                return (DocType) child;
            }
        }
        return null;
    }

    public void setDocType(DocType docType) {
        DocType docType2 = getDocType();
        if (docType == null) {
            throw new NullPointerException("Null DocType");
        }
        if (docType == docType2) {
            return;
        }
        if (docType.getParent() != null) {
            throw new MultipleParentException("DocType belongs to another document");
        }
        if (docType2 == null) {
            insertChild(docType, 0);
            return;
        }
        int iIndexOf = indexOf(docType2);
        super.removeChild(iIndexOf);
        fastInsertChild(docType, iIndexOf);
        docType2.setParent(null);
        docType.setParent(this);
    }

    public final Element getRootElement() {
        int i = 0;
        while (true) {
            Node child = getChild(i);
            if (child.isElement()) {
                return (Element) child;
            }
            i++;
        }
    }

    public void setRootElement(Element element) {
        Element rootElement = getRootElement();
        if (element == rootElement) {
            return;
        }
        if (element == null) {
            throw new NullPointerException("Root element cannot be null");
        }
        if (element.getParent() != null) {
            throw new MultipleParentException(new StringBuffer().append(element.getQualifiedName()).append(" already has a parent").toString());
        }
        fillInBaseURI(rootElement);
        int iIndexOf = indexOf(rootElement);
        rootElement.setParent(null);
        this.children[iIndexOf] = element;
        element.setParent(this);
    }

    @Override // nu.xom.ParentNode
    public void setBaseURI(String str) {
        setActualBaseURI(str);
    }

    @Override // nu.xom.Node
    public final String getBaseURI() {
        return getActualBaseURI();
    }

    @Override // nu.xom.ParentNode
    public Node removeChild(int i) {
        if (i == getRootPosition()) {
            throw new WellformednessException("Cannot remove the root element");
        }
        return super.removeChild(i);
    }

    @Override // nu.xom.ParentNode
    public Node removeChild(Node node) {
        if (node == getRootElement()) {
            throw new WellformednessException("Cannot remove the root element");
        }
        return super.removeChild(node);
    }

    @Override // nu.xom.ParentNode
    public void replaceChild(Node node, Node node2) {
        if (node == getRootElement() && node2 != null && node2.isElement()) {
            setRootElement((Element) node2);
        } else if (node == getDocType() && node2 != null && node2.isDocType()) {
            setDocType((DocType) node2);
        } else {
            super.replaceChild(node, node2);
        }
    }

    @Override // nu.xom.Node
    public final String getValue() {
        return getRootElement().getValue();
    }

    @Override // nu.xom.Node
    public final String toXML() {
        StringBuffer stringBuffer = new StringBuffer(64);
        stringBuffer.append("<?xml version=\"1.0\"?>\n");
        for (int i = 0; i < getChildCount(); i++) {
            stringBuffer.append(getChild(i).toXML());
            stringBuffer.append("\n");
        }
        return stringBuffer.toString();
    }

    @Override // nu.xom.Node
    public Node copy() {
        return new Document(this);
    }

    @Override // nu.xom.Node
    boolean isDocument() {
        return true;
    }

    public final String toString() {
        return new StringBuffer().append("[").append(getClass().getName()).append(": ").append(getRootElement().getQualifiedName()).append("]").toString();
    }
}
