package nu.xom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Element.class */
public class Element extends ParentNode {
    private String localName;
    private String prefix;
    private String URI;
    private Attribute[] attributes;
    private int numAttributes;
    Namespaces namespaces;

    /* JADX INFO: renamed from: nu.xom.Element$1, reason: invalid class name */
    /* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Element$1.class */
    static class AnonymousClass1 {
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Element$AttributeIterator.class */
    private class AttributeIterator implements Iterator {
        private int next;

        private AttributeIterator() {
            this.next = 0;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.next < Element.this.numAttributes;
        }

        @Override // java.util.Iterator
        public Object next() throws NoSuchElementException {
            if (!hasNext()) {
                throw new NoSuchElementException("No such attribute");
            }
            Attribute attribute = Element.this.attributes[this.next];
            this.next++;
            return attribute;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /* synthetic */ AttributeIterator(Element element, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public Element(String str) {
        this(str, "");
    }

    public Element(String str, String str2) {
        this.attributes = null;
        this.numAttributes = 0;
        this.namespaces = null;
        String strSubstring = "";
        String strSubstring2 = str;
        int iIndexOf = str.indexOf(58);
        if (iIndexOf > 0) {
            strSubstring = str.substring(0, iIndexOf);
            strSubstring2 = str.substring(iIndexOf + 1);
        }
        _setNamespacePrefix(strSubstring);
        _setNamespaceURI(str2);
        try {
            _setLocalName(strSubstring2);
        } catch (IllegalNameException e) {
            e.setData(str);
            throw e;
        }
    }

    private Element() {
        this.attributes = null;
        this.numAttributes = 0;
        this.namespaces = null;
    }

    static Element build(String str, String str2, String str3) {
        Element element = new Element();
        int iIndexOf = str.indexOf(58);
        element.prefix = iIndexOf >= 0 ? str.substring(0, iIndexOf) : "";
        element.localName = str3;
        if (!"".equals(str2)) {
            Verifier.checkAbsoluteURIReference(str2);
        }
        element.URI = str2;
        return element;
    }

    public Element(Element element) {
        this.attributes = null;
        this.numAttributes = 0;
        this.namespaces = null;
        this.prefix = element.prefix;
        this.localName = element.localName;
        this.URI = element.URI;
        if (element.namespaces != null) {
            this.namespaces = element.namespaces.copy();
        }
        if (element.attributes != null) {
            this.attributes = element.copyAttributes(this);
            this.numAttributes = element.numAttributes;
        }
        this.actualBaseURI = element.findActualBaseURI();
        copyChildren(element, this);
    }

    private Attribute[] copyAttributes(Element element) {
        if (this.numAttributes == 0) {
            return null;
        }
        Attribute[] attributeArr = new Attribute[this.numAttributes];
        for (int i = 0; i < this.numAttributes; i++) {
            attributeArr[i] = (Attribute) this.attributes[i].copy();
            attributeArr[i].setParent(element);
        }
        return attributeArr;
    }

    private static Element copyTag(Element element) {
        Element elementShallowCopy = element.shallowCopy();
        if (element.namespaces != null) {
            elementShallowCopy.namespaces = element.namespaces.copy();
        }
        if (element.attributes != null) {
            elementShallowCopy.attributes = element.copyAttributes(elementShallowCopy);
            elementShallowCopy.numAttributes = element.numAttributes;
        }
        elementShallowCopy.actualBaseURI = element.findActualBaseURI();
        return elementShallowCopy;
    }

    private static void copyChildren(Element element, Element element2) {
        if (element.getChildCount() == 0) {
            return;
        }
        Element element3 = element2;
        Node child = element;
        int i = 0;
        int[] iArrGrow = new int[10];
        int i2 = 0;
        iArrGrow[0] = 0;
        boolean z = false;
        while (true) {
            if (z || child.getChildCount() <= 0) {
                z = false;
                ParentNode parent = child.getParent();
                if (parent.getChildCount() - 1 == i) {
                    child = parent;
                    i2--;
                    if (child == element) {
                        return;
                    }
                    element3 = (Element) element3.getParent();
                    i = iArrGrow[i2];
                    z = true;
                } else {
                    i++;
                    iArrGrow[i2] = i;
                    child = parent.getChild(i);
                }
            } else {
                child = child.getChild(0);
                i = 0;
                i2++;
                iArrGrow = grow(iArrGrow, i2);
                iArrGrow[i2] = 0;
            }
            if (child.isElement()) {
                Element elementCopyTag = copyTag((Element) child);
                element3.appendChild(elementCopyTag);
                if (child.getChildCount() > 0) {
                    element3 = elementCopyTag;
                }
            } else {
                element3.appendChild(child.copy());
            }
        }
    }

    private static int[] grow(int[] iArr, int i) {
        if (i < iArr.length) {
            return iArr;
        }
        int[] iArr2 = new int[iArr.length * 2];
        System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
        return iArr2;
    }

    public final Elements getChildElements(String str) {
        return getChildElements(str, "");
    }

    public final Elements getChildElements(String str, String str2) {
        if (str2 == null) {
            str2 = "";
        }
        if (str == null) {
            str = "";
        }
        Elements elements = new Elements();
        for (int i = 0; i < getChildCount(); i++) {
            Node child = getChild(i);
            if (child.isElement()) {
                Element element = (Element) child;
                if ((str.equals(element.getLocalName()) || str.length() == 0) && str2.equals(element.getNamespaceURI())) {
                    elements.add(element);
                }
            }
        }
        return elements;
    }

    public final Elements getChildElements() {
        Elements elements = new Elements();
        for (int i = 0; i < getChildCount(); i++) {
            Node child = getChild(i);
            if (child.isElement()) {
                elements.add((Element) child);
            }
        }
        return elements;
    }

    public final Element getFirstChildElement(String str) {
        return getFirstChildElement(str, "");
    }

    public final Element getFirstChildElement(String str, String str2) {
        for (int i = 0; i < getChildCount(); i++) {
            Node child = getChild(i);
            if (child.isElement()) {
                Element element = (Element) child;
                if (str.equals(element.getLocalName()) && str2.equals(element.getNamespaceURI())) {
                    return element;
                }
            }
        }
        return null;
    }

    public void addAttribute(Attribute attribute) {
        String uri;
        if (attribute.getParent() != null) {
            throw new MultipleParentException("Attribute already has a parent");
        }
        String namespacePrefix = attribute.getNamespacePrefix();
        if (namespacePrefix.length() != 0 && !CanonicalizerBase.XML.equals(namespacePrefix)) {
            if (this.prefix.equals(attribute.getNamespacePrefix()) && !getNamespaceURI().equals(attribute.getNamespaceURI())) {
                throw new NamespaceConflictException(new StringBuffer().append("Prefix of ").append(attribute.getQualifiedName()).append(" conflicts with element prefix ").append(this.prefix).toString());
            }
            if (this.namespaces != null && (uri = this.namespaces.getURI(attribute.getNamespacePrefix())) != null && !uri.equals(attribute.getNamespaceURI())) {
                throw new NamespaceConflictException(new StringBuffer().append("Attribute prefix  ").append(namespacePrefix).append(" conflicts with namespace declaration.").toString());
            }
        }
        if (this.attributes == null) {
            this.attributes = new Attribute[1];
        }
        checkPrefixConflict(attribute);
        Attribute attribute2 = getAttribute(attribute.getLocalName(), attribute.getNamespaceURI());
        if (attribute2 != null) {
            remove(attribute2);
        }
        add(attribute);
        attribute.setParent(this);
    }

    private void add(Attribute attribute) {
        if (this.numAttributes == this.attributes.length) {
            Attribute[] attributeArr = new Attribute[this.attributes.length * 2];
            System.arraycopy(this.attributes, 0, attributeArr, 0, this.numAttributes);
            this.attributes = attributeArr;
        }
        this.attributes[this.numAttributes] = attribute;
        this.numAttributes++;
    }

    private boolean remove(Attribute attribute) {
        int i = -1;
        for (int i2 = 0; i2 < this.attributes.length; i2++) {
            if (this.attributes[i2] == attribute) {
                i = i2;
                break;
            }
        }
        if (i == -1) {
            return false;
        }
        int i3 = (this.numAttributes - i) - 1;
        if (i3 > 0) {
            System.arraycopy(this.attributes, i + 1, this.attributes, i, i3);
        }
        this.numAttributes--;
        this.attributes[this.numAttributes] = null;
        return true;
    }

    void fastAddAttribute(Attribute attribute) {
        if (this.attributes == null) {
            this.attributes = new Attribute[1];
        }
        add(attribute);
        attribute.setParent(this);
    }

    public Attribute removeAttribute(Attribute attribute) {
        if (this.attributes == null) {
            throw new NoSuchAttributeException(new StringBuffer().append("Tried to remove attribute ").append(attribute.getQualifiedName()).append(" from non-parent element").toString());
        }
        if (attribute == null) {
            throw new NullPointerException("Tried to remove null attribute");
        }
        if (!remove(attribute)) {
            throw new NoSuchAttributeException(new StringBuffer().append("Tried to remove attribute ").append(attribute.getQualifiedName()).append(" from non-parent element").toString());
        }
        attribute.setParent(null);
        return attribute;
    }

    public final Attribute getAttribute(String str) {
        return getAttribute(str, "");
    }

    public final Attribute getAttribute(String str, String str2) {
        if (this.attributes == null) {
            return null;
        }
        for (int i = 0; i < this.numAttributes; i++) {
            Attribute attribute = this.attributes[i];
            if (attribute.getLocalName().equals(str) && attribute.getNamespaceURI().equals(str2)) {
                return attribute;
            }
        }
        return null;
    }

    public final String getAttributeValue(String str) {
        return getAttributeValue(str, "");
    }

    public final int getAttributeCount() {
        return this.numAttributes;
    }

    public final Attribute getAttribute(int i) {
        if (this.attributes == null) {
            throw new IndexOutOfBoundsException("Element does not have any attributes");
        }
        return this.attributes[i];
    }

    public final String getAttributeValue(String str, String str2) {
        Attribute attribute = getAttribute(str, str2);
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    public final String getLocalName() {
        return this.localName;
    }

    public final String getQualifiedName() {
        return this.prefix.length() == 0 ? this.localName : new StringBuffer().append(this.prefix).append(":").append(this.localName).toString();
    }

    public final String getNamespacePrefix() {
        return this.prefix;
    }

    public final String getNamespaceURI() {
        return this.URI;
    }

    public final String getNamespaceURI(String str) {
        String str2;
        ParentNode parent;
        Element element = this;
        String localNamespaceURI = getLocalNamespaceURI(str);
        while (true) {
            str2 = localNamespaceURI;
            if (str2 != null || (parent = element.getParent()) == null || parent.isDocument()) {
                break;
            }
            element = (Element) parent;
            localNamespaceURI = element.getLocalNamespaceURI(str);
        }
        if (str2 == null && "".equals(str)) {
            str2 = "";
        }
        return str2;
    }

    final String getLocalNamespaceURI(String str) {
        String uri;
        if (str.equals(this.prefix)) {
            return this.URI;
        }
        if (CanonicalizerBase.XML.equals(str)) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if (CanonicalizerBase.XMLNS.equals(str)) {
            return "";
        }
        if (this.namespaces != null && (uri = this.namespaces.getURI(str)) != null) {
            return uri;
        }
        if (str.length() == 0 || this.attributes == null) {
            return null;
        }
        for (int i = 0; i < this.numAttributes; i++) {
            Attribute attribute = this.attributes[i];
            if (attribute.getNamespacePrefix().equals(str)) {
                return attribute.getNamespaceURI();
            }
        }
        return null;
    }

    public void setLocalName(String str) {
        _setLocalName(str);
    }

    private void _setLocalName(String str) {
        Verifier.checkNCName(str);
        this.localName = str;
    }

    public void setNamespaceURI(String str) {
        _setNamespaceURI(str);
    }

    private void _setNamespaceURI(String str) {
        if (str == null) {
            str = "";
        }
        if (str.equals(this.URI)) {
            return;
        }
        if (str.length() != 0) {
            Verifier.checkAbsoluteURIReference(str);
        } else if (this.prefix.length() != 0) {
            throw new NamespaceConflictException("Prefixed elements must have namespace URIs.");
        }
        if (this.namespaces != null && this.namespaces.getURI(this.prefix) != null) {
            throw new NamespaceConflictException("new URI conflicts with existing prefix");
        }
        if (str.length() > 0 && this.attributes != null) {
            for (int i = 0; i < this.numAttributes; i++) {
                Attribute attribute = this.attributes[i];
                if (attribute.getNamespacePrefix().length() != 0 && attribute.getNamespacePrefix().equals(this.prefix)) {
                    throw new NamespaceConflictException(new StringBuffer().append("new element URI ").append(str).append(" conflicts with attribute ").append(attribute.getQualifiedName()).toString());
                }
            }
        }
        if ("http://www.w3.org/XML/1998/namespace".equals(str) && !CanonicalizerBase.XML.equals(this.prefix)) {
            throw new NamespaceConflictException(new StringBuffer().append("Wrong prefix ").append(this.prefix).append(" for the http://www.w3.org/XML/1998/namespace namespace URI").toString());
        }
        if (CanonicalizerBase.XML.equals(this.prefix) && !"http://www.w3.org/XML/1998/namespace".equals(str)) {
            throw new NamespaceConflictException(new StringBuffer().append("Wrong namespace URI ").append(str).append(" for the xml prefix").toString());
        }
        this.URI = str;
    }

    public void setNamespacePrefix(String str) {
        _setNamespacePrefix(str);
    }

    private void _setNamespacePrefix(String str) {
        if (str == null) {
            str = "";
        }
        if (str.length() != 0) {
            Verifier.checkNCName(str);
        }
        String localNamespaceURI = getLocalNamespaceURI(str);
        if (localNamespaceURI != null) {
            if (!localNamespaceURI.equals(this.URI) && !CanonicalizerBase.XML.equals(str)) {
                throw new NamespaceConflictException(new StringBuffer().append(str).append(" conflicts with existing prefix").toString());
            }
        } else if ("".equals(this.URI) && !"".equals(str)) {
            throw new NamespaceConflictException("Cannot assign prefix to element in no namespace");
        }
        this.prefix = str;
    }

    @Override // nu.xom.ParentNode
    void insertionAllowed(Node node, int i) {
        if (node == null) {
            throw new NullPointerException("Tried to insert a null child in the tree");
        }
        if (node.getParent() != null) {
            throw new MultipleParentException(new StringBuffer().append(node.toString()).append(" child already has a parent.").toString());
        }
        if (node.isElement()) {
            checkCycle(node, this);
        } else if (!node.isText() && !node.isProcessingInstruction() && !node.isComment()) {
            throw new IllegalAddException(new StringBuffer().append("Cannot add a ").append(node.getClass().getName()).append(" to an Element.").toString());
        }
    }

    private static void checkCycle(Node node, ParentNode parentNode) {
        if (node == parentNode) {
            throw new CycleException("Cannot add a node to itself");
        }
        if (node.getChildCount() == 0) {
            return;
        }
        do {
            ParentNode parent = parentNode.getParent();
            parentNode = parent;
            if (parent == null) {
                return;
            }
        } while (parentNode != node);
        throw new CycleException("Cannot add an ancestor as a child");
    }

    public void insertChild(String str, int i) {
        if (str == null) {
            throw new NullPointerException("Inserted null string");
        }
        super.fastInsertChild(new Text(str), i);
    }

    public void appendChild(String str) {
        insertChild(new Text(str), getChildCount());
    }

    public Nodes removeChildren() {
        int childCount = getChildCount();
        Nodes nodes = new Nodes();
        for (int i = 0; i < childCount; i++) {
            Node child = getChild(i);
            if (child.isElement()) {
                fillInBaseURI((Element) child);
            }
            child.setParent(null);
            nodes.append(child);
        }
        this.children = null;
        this.childCount = 0;
        return nodes;
    }

    public void addNamespaceDeclaration(String str, String str2) {
        if (str == null) {
            str = "";
        }
        if (str2 == null) {
            str2 = "";
        }
        if (str.equals(CanonicalizerBase.XMLNS)) {
            if (!str2.equals("")) {
                throw new NamespaceConflictException("The xmlns prefix cannot bound to any URI");
            }
            return;
        }
        if (str.equals(CanonicalizerBase.XML)) {
            if (!str2.equals("http://www.w3.org/XML/1998/namespace")) {
                throw new NamespaceConflictException(new StringBuffer().append("Wrong namespace URI for xml prefix: ").append(str2).toString());
            }
            return;
        }
        if (str2.equals("http://www.w3.org/XML/1998/namespace")) {
            throw new NamespaceConflictException(new StringBuffer().append("Wrong prefix for http://www.w3.org/XML/1998/namespace namespace: ").append(str).toString());
        }
        if (str.length() != 0) {
            Verifier.checkNCName(str);
            Verifier.checkAbsoluteURIReference(str2);
        } else if (str2.length() != 0) {
            Verifier.checkAbsoluteURIReference(str2);
        }
        String localNamespaceURI = getLocalNamespaceURI(str);
        if (localNamespaceURI != null && !localNamespaceURI.equals(str2)) {
            throw new NamespaceConflictException(str.equals("") ? new StringBuffer().append("Additional namespace ").append(str2).append(" conflicts with existing default namespace ").append(localNamespaceURI).toString() : new StringBuffer().append("Additional namespace ").append(str2).append(" for the prefix ").append(str).append(" conflicts with existing namespace binding ").append(localNamespaceURI).toString());
        }
        if (this.namespaces == null) {
            this.namespaces = new Namespaces();
        }
        this.namespaces.put(str, str2);
    }

    public void removeNamespaceDeclaration(String str) {
        if (this.namespaces != null) {
            this.namespaces.remove(str);
        }
    }

    public final int getNamespaceDeclarationCount() {
        HashSet hashSet = null;
        if (this.namespaces != null) {
            hashSet = new HashSet(this.namespaces.getPrefixes());
            hashSet.add(this.prefix);
        }
        if (CanonicalizerBase.XML.equals(this.prefix)) {
            hashSet = new HashSet();
        }
        int attributeCount = getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            String namespacePrefix = this.attributes[i].getNamespacePrefix();
            if (namespacePrefix.length() != 0 && !CanonicalizerBase.XML.equals(namespacePrefix)) {
                if (hashSet == null) {
                    hashSet = new HashSet();
                    hashSet.add(this.prefix);
                }
                hashSet.add(namespacePrefix);
            }
        }
        if (hashSet == null) {
            return 1;
        }
        return hashSet.size();
    }

    Map getNamespacePrefixesInScope() {
        HashMap map = new HashMap();
        Element element = this;
        while (true) {
            Element element2 = element;
            if (!CanonicalizerBase.XML.equals(element2.prefix)) {
                addPrefixIfNotAlreadyPresent(map, element2, element2.prefix);
            }
            if (element2.attributes != null) {
                int i = element2.numAttributes;
                for (int i2 = 0; i2 < i; i2++) {
                    String namespacePrefix = element2.attributes[i2].getNamespacePrefix();
                    if (namespacePrefix.length() != 0 && !CanonicalizerBase.XML.equals(namespacePrefix)) {
                        addPrefixIfNotAlreadyPresent(map, element2, namespacePrefix);
                    }
                }
            }
            if (element2.namespaces != null) {
                int size = element2.namespaces.size();
                for (int i3 = 0; i3 < size; i3++) {
                    addPrefixIfNotAlreadyPresent(map, element2, element2.namespaces.getPrefix(i3));
                }
            }
            ParentNode parent = element2.getParent();
            if (parent == null || parent.isDocument() || parent.isDocumentFragment()) {
                break;
            }
            element = (Element) parent;
        }
        return map;
    }

    private void addPrefixIfNotAlreadyPresent(HashMap map, Element element, String str) {
        if (map.containsKey(str)) {
            return;
        }
        map.put(str, element.getLocalNamespaceURI(str));
    }

    public final String getNamespacePrefix(int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException(new StringBuffer().append("Negative prefix number ").append(i).toString());
        }
        if (i == 0 && !CanonicalizerBase.XML.equals(this.prefix)) {
            return this.prefix;
        }
        Iterator it = getNamespacePrefixes().iterator();
        for (int i2 = 0; i2 < i; i2++) {
            try {
                it.next();
            } catch (NoSuchElementException e) {
                throw new IndexOutOfBoundsException(new StringBuffer().append("No ").append(i).append("th namespace").toString());
            }
        }
        return (String) it.next();
    }

    private Set getNamespacePrefixes() {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        if (!CanonicalizerBase.XML.equals(this.prefix)) {
            linkedHashSet.add(this.prefix);
        }
        if (this.namespaces != null) {
            linkedHashSet.addAll(this.namespaces.getPrefixes());
        }
        if (this.attributes != null) {
            int attributeCount = getAttributeCount();
            for (int i = 0; i < attributeCount; i++) {
                String namespacePrefix = this.attributes[i].getNamespacePrefix();
                if (namespacePrefix.length() != 0 && !CanonicalizerBase.XML.equals(namespacePrefix)) {
                    linkedHashSet.add(namespacePrefix);
                }
            }
        }
        return linkedHashSet;
    }

    @Override // nu.xom.ParentNode
    public void setBaseURI(String str) {
        setActualBaseURI(str);
    }

    /* JADX WARN: Code duplicated, block: B:28:0x00a3 A[PHI: r5
      0x00a3: PHI (r5v3 java.lang.String) = (r5v1 java.lang.String), (r5v1 java.lang.String), (r5v7 java.lang.String) binds: [B:12:0x004b, B:17:0x0074, B:26:0x009d] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:43:0x00ac A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:44:? A[LOOP:0: B:3:0x000a->B:44:?, LOOP_END, SYNTHETIC] */
    @Override // nu.xom.Node
    public String getBaseURI() {
        String strAbsolutize = "";
        String actualBaseURI = getActualBaseURI();
        ParentNode parent = this;
        while (true) {
            String actualBaseURI2 = parent.getActualBaseURI();
            if (actualBaseURI.length() != 0 && !actualBaseURI.equals(actualBaseURI2)) {
                strAbsolutize = URIUtil.absolutize(actualBaseURI, strAbsolutize);
                break;
            }
            if (parent.isDocument()) {
                strAbsolutize = URIUtil.absolutize(actualBaseURI2, strAbsolutize);
                break;
            }
            Attribute attribute = ((Element) parent).getAttribute("base", "http://www.w3.org/XML/1998/namespace");
            if (attribute != null) {
                String uri = URIUtil.toURI(attribute.getValue());
                if ("".equals(uri)) {
                    strAbsolutize = getEntityURI();
                    break;
                }
                if (legalURI(uri)) {
                    if ("".equals(strAbsolutize)) {
                        strAbsolutize = uri;
                    } else {
                        if (URIUtil.isOpaque(uri)) {
                            break;
                        }
                        strAbsolutize = URIUtil.absolutize(uri, strAbsolutize);
                    }
                    if (URIUtil.isAbsolute(uri)) {
                        break;
                    }
                    parent = parent.getParent();
                    if (parent == null) {
                        strAbsolutize = URIUtil.absolutize(actualBaseURI2, strAbsolutize);
                        break;
                    }
                } else {
                    parent = parent.getParent();
                    if (parent == null) {
                        strAbsolutize = URIUtil.absolutize(actualBaseURI2, strAbsolutize);
                        break;
                    }
                }
            } else {
                parent = parent.getParent();
                if (parent == null) {
                    strAbsolutize = URIUtil.absolutize(actualBaseURI2, strAbsolutize);
                    break;
                }
            }
        }
        return URIUtil.isAbsolute(strAbsolutize) ? strAbsolutize : "";
    }

    private String getEntityURI() {
        ParentNode parent = this;
        while (true) {
            ParentNode parentNode = parent;
            if (parentNode == null) {
                return "";
            }
            if (parentNode.actualBaseURI != null && parentNode.actualBaseURI.length() != 0) {
                return parentNode.actualBaseURI;
            }
            parent = parentNode.getParent();
        }
    }

    private boolean legalURI(String str) {
        try {
            Verifier.checkURIReference(str);
            return true;
        } catch (MalformedURIException e) {
            return false;
        }
    }

    /* JADX WARN: Code duplicated, block: B:23:0x00a4  */
    /* JADX WARN: Code duplicated, block: B:34:0x00b0 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:35:0x0099 A[SYNTHETIC] */
    @Override // nu.xom.Node
    public final String toXML() {
        ParentNode parent;
        StringBuffer stringBuffer = new StringBuffer(1024);
        Node child = this;
        boolean z = false;
        int i = -1;
        int[] iArrGrow = new int[10];
        int i2 = 0;
        iArrGrow[0] = -1;
        while (true) {
            if (!z && child.getChildCount() > 0) {
                writeStartTag((Element) child, stringBuffer);
                child = child.getChild(0);
                i = 0;
                i2++;
                iArrGrow = grow(iArrGrow, i2);
                iArrGrow[i2] = 0;
            } else if (z) {
                writeEndTag((Element) child, stringBuffer);
                if (child == this) {
                    break;
                }
                z = false;
                parent = child.getParent();
                if (parent.getChildCount() - 1 == i) {
                    child = parent;
                    i2--;
                    if (child != this) {
                        i = iArrGrow[i2];
                    }
                    z = true;
                } else {
                    i++;
                    iArrGrow[i2] = i;
                    child = parent.getChild(i);
                }
            } else {
                if (child.isElement()) {
                    writeStartTag((Element) child, stringBuffer);
                    if (child == this) {
                        break;
                    }
                } else {
                    stringBuffer.append(child.toXML());
                }
                z = false;
                parent = child.getParent();
                if (parent.getChildCount() - 1 == i) {
                    child = parent;
                    i2--;
                    if (child != this) {
                        i = iArrGrow[i2];
                    }
                    z = true;
                } else {
                    i++;
                    iArrGrow[i2] = i;
                    child = parent.getChild(i);
                }
            }
        }
        return stringBuffer.toString();
    }

    /* JADX WARN: Code duplicated, block: B:16:0x005f  */
    /* JADX WARN: Code duplicated, block: B:18:0x006f  */
    private static void writeStartTag(Element element, StringBuffer stringBuffer) {
        stringBuffer.append('<');
        stringBuffer.append(element.getQualifiedName());
        ParentNode parent = element.getParent();
        for (int i = 0; i < element.getNamespaceDeclarationCount(); i++) {
            String namespacePrefix = element.getNamespacePrefix(i);
            String namespaceURI = element.getNamespaceURI(namespacePrefix);
            if (parent == null || !parent.isElement()) {
                if (namespaceURI.length() != 0) {
                    stringBuffer.append(" xmlns");
                    if (namespacePrefix.length() > 0) {
                        stringBuffer.append(':');
                        stringBuffer.append(namespacePrefix);
                    }
                    stringBuffer.append("=\"");
                    stringBuffer.append(escape(namespaceURI));
                    stringBuffer.append('\"');
                }
            } else if (!namespaceURI.equals(((Element) parent).getNamespaceURI(namespacePrefix))) {
                stringBuffer.append(" xmlns");
                if (namespacePrefix.length() > 0) {
                    stringBuffer.append(':');
                    stringBuffer.append(namespacePrefix);
                }
                stringBuffer.append("=\"");
                stringBuffer.append(escape(namespaceURI));
                stringBuffer.append('\"');
            }
        }
        if (element.attributes != null) {
            for (int i2 = 0; i2 < element.numAttributes; i2++) {
                Attribute attribute = element.attributes[i2];
                stringBuffer.append(' ');
                stringBuffer.append(attribute.toXML());
            }
        }
        if (element.getChildCount() > 0) {
            stringBuffer.append('>');
        } else {
            stringBuffer.append(" />");
        }
    }

    private static String escape(String str) {
        int length = str.length();
        StringBuffer stringBuffer = new StringBuffer(length + 12);
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt == '&') {
                stringBuffer.append("&amp;");
            } else {
                stringBuffer.append(cCharAt);
            }
        }
        return stringBuffer.toString();
    }

    private static void writeEndTag(Element element, StringBuffer stringBuffer) {
        stringBuffer.append("</");
        stringBuffer.append(element.getQualifiedName());
        stringBuffer.append('>');
    }

    @Override // nu.xom.Node
    public final String getValue() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return "";
        }
        Node child = getChild(0);
        if (childCount == 1 && child.isText()) {
            return child.getValue();
        }
        StringBuffer stringBuffer = new StringBuffer();
        int i = 0;
        int[] iArrGrow = new int[10];
        int i2 = 0;
        iArrGrow[0] = 0;
        boolean z = false;
        while (true) {
            if (z || child.getChildCount() <= 0) {
                z = false;
                if (child.isText()) {
                    stringBuffer.append(child.getValue());
                }
                ParentNode parent = child.getParent();
                if (parent.getChildCount() - 1 == i) {
                    child = parent;
                    i2--;
                    if (child == this) {
                        return stringBuffer.toString();
                    }
                    i = iArrGrow[i2];
                    z = true;
                } else {
                    i++;
                    iArrGrow[i2] = i;
                    child = parent.getChild(i);
                }
            } else {
                child = child.getChild(0);
                i = 0;
                i2++;
                iArrGrow = grow(iArrGrow, i2);
                iArrGrow[i2] = 0;
            }
        }
    }

    @Override // nu.xom.Node
    public Node copy() {
        Element elementCopyTag = copyTag(this);
        copyChildren(this, elementCopyTag);
        return elementCopyTag;
    }

    protected Element shallowCopy() {
        return new Element(getQualifiedName(), getNamespaceURI());
    }

    public String toString() {
        return new StringBuffer().append("[").append(getClass().getName()).append(": ").append(getQualifiedName()).append("]").toString();
    }

    @Override // nu.xom.Node
    boolean isElement() {
        return true;
    }

    private void checkPrefixConflict(Attribute attribute) {
        String namespacePrefix = attribute.getNamespacePrefix();
        String namespaceURI = attribute.getNamespaceURI();
        for (int i = 0; i < this.numAttributes; i++) {
            Attribute attribute2 = this.attributes[i];
            if (attribute2.getNamespacePrefix().equals(namespacePrefix)) {
                if (!attribute2.getNamespaceURI().equals(namespaceURI)) {
                    throw new NamespaceConflictException(new StringBuffer().append("Prefix of ").append(attribute.getQualifiedName()).append(" conflicts with ").append(attribute2.getQualifiedName()).toString());
                }
                return;
            }
        }
    }

    Iterator attributeIterator() {
        return new AttributeIterator(this, null);
    }
}
