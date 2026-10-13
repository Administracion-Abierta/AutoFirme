package nu.xom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import nu.xom.jaxen.DefaultNavigator;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.JaxenConstants;
import nu.xom.jaxen.JaxenException;
import nu.xom.jaxen.NamedAccessNavigator;
import nu.xom.jaxen.UnsupportedAxisException;
import nu.xom.jaxen.XPath;
import nu.xom.jaxen.util.SingleObjectIterator;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/JaxenNavigator.class */
class JaxenNavigator extends DefaultNavigator implements NamedAccessNavigator {
    private static final long serialVersionUID = 7008740797833836742L;

    /* JADX INFO: loaded from: autofirma.jar:nu/xom/JaxenNavigator$ChildIterator.class */
    private static class ChildIterator implements Iterator {
        private final ParentNode parent;
        private int xomIndex;
        private final int xomCount;

        ChildIterator(ParentNode parentNode) {
            this.xomIndex = 0;
            this.parent = parentNode;
            this.xomCount = parentNode.getChildCount();
        }

        ChildIterator(ParentNode parentNode, int i) {
            this.xomIndex = 0;
            this.parent = parentNode;
            this.xomIndex = i;
            this.xomCount = parentNode.getChildCount();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            for (int i = this.xomIndex; i < this.xomCount; i++) {
                Node child = this.parent.getChild(i);
                if (!child.isText() || !((Text) child).isEmpty()) {
                    return true;
                }
            }
            return false;
        }

        @Override // java.util.Iterator
        public Object next() {
            Object obj;
            ParentNode parentNode = this.parent;
            int i = this.xomIndex;
            this.xomIndex = i + 1;
            Node child = parentNode.getChild(i);
            if (child.isText()) {
                Text text = (Text) child;
                boolean zIsEmpty = text.isEmpty();
                ArrayList arrayList = new ArrayList(1);
                arrayList.add(text);
                while (this.xomIndex < this.xomCount) {
                    Node child2 = this.parent.getChild(this.xomIndex);
                    if (!child2.isText()) {
                        break;
                    }
                    this.xomIndex++;
                    arrayList.add(child2);
                    if (zIsEmpty && !((Text) child2).isEmpty()) {
                        zIsEmpty = false;
                    }
                }
                if (zIsEmpty) {
                    return next();
                }
                obj = arrayList;
            } else {
                if (child.isDocType()) {
                    return next();
                }
                obj = child;
            }
            return obj;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:nu/xom/JaxenNavigator$NamedChildIterator.class */
    private static class NamedChildIterator implements Iterator {
        private final ParentNode parent;
        private int index = -1;
        private final int xomCount;
        private Element next;
        private final String localName;
        private final String URI;

        NamedChildIterator(ParentNode parentNode, String str, String str2, String str3) {
            this.parent = parentNode;
            this.xomCount = parentNode.getChildCount();
            this.localName = str;
            if (str3 == null) {
                this.URI = "";
            } else {
                this.URI = str3;
            }
            findNext();
        }

        private void findNext() {
            while (true) {
                int i = this.index + 1;
                this.index = i;
                if (i >= this.xomCount) {
                    this.next = null;
                    return;
                }
                Node child = this.parent.getChild(this.index);
                if (child.isElement()) {
                    Element element = (Element) child;
                    if (element.getNamespaceURI().equals(this.URI) && element.getLocalName().equals(this.localName)) {
                        this.next = element;
                        return;
                    }
                }
            }
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.next != null;
        }

        @Override // java.util.Iterator
        public Object next() {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            Element element = this.next;
            findNext();
            return element;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    JaxenNavigator() {
    }

    @Override // nu.xom.jaxen.DefaultNavigator, nu.xom.jaxen.Navigator
    public Iterator getSelfAxisIterator(Object obj) {
        if (obj instanceof Text) {
            Node node = (Text) obj;
            ArrayList arrayList = new ArrayList();
            ParentNode parent = node.getParent();
            int iIndexOf = parent.indexOf(node);
            int i = iIndexOf;
            int i2 = iIndexOf;
            while (i > 0 && parent.getChild(i - 1).isText()) {
                i--;
            }
            while (i2 < parent.getChildCount() - 1 && parent.getChild(i2 + 1).isText()) {
                i2++;
            }
            for (int i3 = i; i3 <= i2; i3++) {
                arrayList.add(parent.getChild(i3));
            }
            obj = arrayList;
        }
        return new SingleObjectIterator(obj);
    }

    @Override // nu.xom.jaxen.DefaultNavigator, nu.xom.jaxen.Navigator
    public Object getElementById(Object obj, String str) {
        Element rootElement;
        Node node = obj instanceof ArrayList ? (Node) ((List) obj).get(0) : (Node) obj;
        ParentNode parent = (node.isElement() || node.isDocument()) ? (ParentNode) node : node.getParent();
        ParentNode parentNode = parent;
        while (parent != null) {
            parentNode = parent;
            parent = parent.getParent();
        }
        if (parentNode.isDocument()) {
            rootElement = ((Document) parentNode).getRootElement();
        } else {
            if (!parentNode.getChild(0).isElement()) {
                return null;
            }
            rootElement = (Element) parentNode.getChild(0);
        }
        return findByID(rootElement, str);
    }

    public static Element findByID(Element element, String str) {
        if (hasID(element, str)) {
            return element;
        }
        Elements childElements = element.getChildElements();
        for (int i = 0; i < childElements.size(); i++) {
            Element elementFindByID = findByID(childElements.get(i), str);
            if (elementFindByID != null) {
                return elementFindByID;
            }
        }
        return null;
    }

    private static boolean hasID(Element element, String str) {
        int attributeCount = element.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            Attribute attribute = element.getAttribute(i);
            if (Attribute.Type.ID == attribute.getType()) {
                return attribute.getValue().trim().equals(str);
            }
        }
        return false;
    }

    @Override // nu.xom.jaxen.Navigator
    public String getNamespacePrefix(Object obj) {
        return ((Namespace) obj).getPrefix();
    }

    @Override // nu.xom.jaxen.Navigator
    public String getNamespaceStringValue(Object obj) {
        return ((Namespace) obj).getValue();
    }

    @Override // nu.xom.jaxen.DefaultNavigator, nu.xom.jaxen.Navigator
    public Iterator getNamespaceAxisIterator(Object obj) {
        try {
            Element element = (Element) obj;
            Map namespacePrefixesInScope = element.getNamespacePrefixesInScope();
            ArrayList arrayList = new ArrayList(namespacePrefixesInScope.size() + 1);
            arrayList.add(new Namespace("xml", "http://www.w3.org/XML/1998/namespace", element));
            for (Map.Entry entry : namespacePrefixesInScope.entrySet()) {
                String str = (String) entry.getKey();
                String str2 = (String) entry.getValue();
                if (!"".equals(str) || !"".equals(str2)) {
                    arrayList.add(new Namespace(str, str2, element));
                }
            }
            return arrayList.iterator();
        } catch (ClassCastException e) {
            return JaxenConstants.EMPTY_ITERATOR;
        }
    }

    @Override // nu.xom.jaxen.DefaultNavigator, nu.xom.jaxen.Navigator
    public Iterator getParentAxisIterator(Object obj) {
        Node node = (Node) getParentNode(obj);
        return node == null ? JaxenConstants.EMPTY_ITERATOR : new SingleObjectIterator(node);
    }

    @Override // nu.xom.jaxen.DefaultNavigator, nu.xom.jaxen.Navigator
    public Object getDocumentNode(Object obj) {
        return ((Node) obj).getRoot();
    }

    @Override // nu.xom.jaxen.DefaultNavigator, nu.xom.jaxen.Navigator
    public Object getDocument(String str) throws FunctionCallException {
        throw new FunctionCallException("document() function not supported");
    }

    @Override // nu.xom.jaxen.DefaultNavigator, nu.xom.jaxen.Navigator
    public Iterator getAttributeAxisIterator(Object obj) {
        try {
            return ((Element) obj).attributeIterator();
        } catch (ClassCastException e) {
            return JaxenConstants.EMPTY_ITERATOR;
        }
    }

    @Override // nu.xom.jaxen.DefaultNavigator, nu.xom.jaxen.Navigator
    public Iterator getChildAxisIterator(Object obj) {
        return obj instanceof ParentNode ? new ChildIterator((ParentNode) obj) : JaxenConstants.EMPTY_ITERATOR;
    }

    @Override // nu.xom.jaxen.DefaultNavigator, nu.xom.jaxen.Navigator
    public Iterator getFollowingSiblingAxisIterator(Object obj) {
        Node node;
        if (obj instanceof ArrayList) {
            ArrayList arrayList = (ArrayList) obj;
            node = (Node) arrayList.get(arrayList.size() - 1);
        } else {
            node = (Node) obj;
        }
        ParentNode parent = node.getParent();
        return parent == null ? JaxenConstants.EMPTY_ITERATOR : new ChildIterator(parent, parent.indexOf(node) + 1);
    }

    @Override // nu.xom.jaxen.DefaultNavigator, nu.xom.jaxen.Navigator
    public Object getParentNode(Object obj) {
        return (obj instanceof ArrayList ? (Node) ((List) obj).get(0) : (Node) obj).getParent();
    }

    @Override // nu.xom.jaxen.Navigator
    public String getTextStringValue(Object obj) {
        List list = (List) obj;
        if (list.size() == 1) {
            return ((Text) list.get(0)).getValue();
        }
        StringBuffer stringBuffer = new StringBuffer();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            stringBuffer.append(((Text) it.next()).getValue());
        }
        return stringBuffer.toString();
    }

    @Override // nu.xom.jaxen.Navigator
    public String getElementNamespaceUri(Object obj) {
        return ((Element) obj).getNamespaceURI();
    }

    @Override // nu.xom.jaxen.Navigator
    public String getElementName(Object obj) {
        return ((Element) obj).getLocalName();
    }

    @Override // nu.xom.jaxen.Navigator
    public String getElementQName(Object obj) {
        return ((Element) obj).getQualifiedName();
    }

    @Override // nu.xom.jaxen.Navigator
    public String getAttributeNamespaceUri(Object obj) {
        return ((Attribute) obj).getNamespaceURI();
    }

    @Override // nu.xom.jaxen.Navigator
    public String getAttributeName(Object obj) {
        return ((Attribute) obj).getLocalName();
    }

    @Override // nu.xom.jaxen.Navigator
    public String getAttributeQName(Object obj) {
        return ((Attribute) obj).getQualifiedName();
    }

    @Override // nu.xom.jaxen.DefaultNavigator, nu.xom.jaxen.Navigator
    public String getProcessingInstructionData(Object obj) {
        return ((ProcessingInstruction) obj).getValue();
    }

    @Override // nu.xom.jaxen.DefaultNavigator, nu.xom.jaxen.Navigator
    public String getProcessingInstructionTarget(Object obj) {
        return ((ProcessingInstruction) obj).getTarget();
    }

    @Override // nu.xom.jaxen.Navigator
    public boolean isDocument(Object obj) {
        return (obj instanceof Document) || (obj instanceof DocumentFragment);
    }

    @Override // nu.xom.jaxen.Navigator
    public boolean isElement(Object obj) {
        return obj instanceof Element;
    }

    @Override // nu.xom.jaxen.Navigator
    public boolean isAttribute(Object obj) {
        return obj instanceof Attribute;
    }

    @Override // nu.xom.jaxen.Navigator
    public boolean isNamespace(Object obj) {
        return obj instanceof Namespace;
    }

    @Override // nu.xom.jaxen.Navigator
    public boolean isComment(Object obj) {
        return obj instanceof Comment;
    }

    @Override // nu.xom.jaxen.Navigator
    public boolean isText(Object obj) {
        if (!(obj instanceof ArrayList)) {
            return false;
        }
        Iterator it = ((List) obj).iterator();
        while (it.hasNext()) {
            if (!(it.next() instanceof Text)) {
                return false;
            }
        }
        return true;
    }

    @Override // nu.xom.jaxen.Navigator
    public boolean isProcessingInstruction(Object obj) {
        return obj instanceof ProcessingInstruction;
    }

    @Override // nu.xom.jaxen.Navigator
    public String getCommentStringValue(Object obj) {
        return ((Comment) obj).getValue();
    }

    @Override // nu.xom.jaxen.Navigator
    public String getElementStringValue(Object obj) {
        return ((Element) obj).getValue();
    }

    @Override // nu.xom.jaxen.Navigator
    public String getAttributeStringValue(Object obj) {
        return ((Attribute) obj).getValue();
    }

    @Override // nu.xom.jaxen.Navigator
    public XPath parseXPath(String str) throws JaxenException {
        return new JaxenConnector(str);
    }

    @Override // nu.xom.jaxen.NamedAccessNavigator
    public Iterator getChildAxisIterator(Object obj, String str, String str2, String str3) throws UnsupportedAxisException {
        return obj instanceof ParentNode ? new NamedChildIterator((ParentNode) obj, str, str2, str3) : JaxenConstants.EMPTY_ITERATOR;
    }

    @Override // nu.xom.jaxen.NamedAccessNavigator
    public Iterator getAttributeAxisIterator(Object obj, String str, String str2, String str3) throws UnsupportedAxisException {
        try {
            Element element = (Element) obj;
            Attribute attribute = str3 == null ? element.getAttribute(str) : element.getAttribute(str, str3);
            return attribute == null ? JaxenConstants.EMPTY_ITERATOR : new SingleObjectIterator(attribute);
        } catch (ClassCastException e) {
            return JaxenConstants.EMPTY_ITERATOR;
        }
    }
}
