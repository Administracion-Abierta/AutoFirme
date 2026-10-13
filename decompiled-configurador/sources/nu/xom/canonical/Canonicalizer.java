package nu.xom.canonical;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Namespace;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.ProcessingInstruction;
import nu.xom.Serializer;
import nu.xom.Text;
import nu.xom.XPathContext;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.xml.sax.helpers.NamespaceSupport;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/canonical/Canonicalizer.class */
public class Canonicalizer {
    private boolean withComments;
    private boolean exclusive;
    private boolean v11;
    private CanonicalXMLSerializer serializer;
    private List inclusiveNamespacePrefixes;
    private static Comparator comparator = new AttributeComparator(null);
    public static final String CANONICAL_XML = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    public static final String CANONICAL_XML_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
    public static final String EXCLUSIVE_XML_CANONICALIZATION = "http://www.w3.org/2001/10/xml-exc-c14n#";
    public static final String EXCLUSIVE_XML_CANONICALIZATION_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    public static final String CANONICAL_XML_11 = "http://www.w3.org/2006/12/xml-c14n11";
    public static final String CANONICAL_XML_11_WITH_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11#WithComments";

    /* JADX INFO: renamed from: nu.xom.canonical.Canonicalizer$1, reason: invalid class name */
    /* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/canonical/Canonicalizer$1.class */
    static class AnonymousClass1 {
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/canonical/Canonicalizer$AttributeComparator.class */
    private static class AttributeComparator implements Comparator {
        private AttributeComparator() {
        }

        @Override // java.util.Comparator
        public int compare(Object obj, Object obj2) {
            Attribute attribute = (Attribute) obj;
            Attribute attribute2 = (Attribute) obj2;
            String namespaceURI = attribute.getNamespaceURI();
            String namespaceURI2 = attribute2.getNamespaceURI();
            if (namespaceURI.equals(namespaceURI2)) {
                return attribute.getLocalName().compareTo(attribute2.getLocalName());
            }
            if (namespaceURI.equals("")) {
                return -1;
            }
            if (namespaceURI2.equals("")) {
                return 1;
            }
            return namespaceURI.compareTo(namespaceURI2);
        }

        /* synthetic */ AttributeComparator(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/canonical/Canonicalizer$CanonicalXMLSerializer.class */
    private class CanonicalXMLSerializer extends Serializer {
        private Nodes nodes;
        private NamespaceSupport inScope;
        private final XPathContext xmlcontext;
        private final /* synthetic */ Canonicalizer this$0;

        CanonicalXMLSerializer(Canonicalizer canonicalizer, OutputStream outputStream) {
            super(outputStream);
            this.this$0 = canonicalizer;
            this.xmlcontext = new XPathContext(CanonicalizerBase.XML, "http://www.w3.org/XML/1998/namespace");
            setLineSeparator("\n");
        }

        @Override // nu.xom.Serializer
        public final void write(Document document) throws IOException {
            Node child;
            this.inScope = new NamespaceSupport();
            int i = 0;
            do {
                child = document.getChild(i);
                if (this.nodes == null || (child instanceof Element) || this.nodes.contains(child)) {
                    writeChild(child);
                    if (child instanceof ProcessingInstruction) {
                        breakLine();
                    } else if ((child instanceof Comment) && this.this$0.withComments) {
                        breakLine();
                    }
                }
                i++;
            } while (!(child instanceof Element));
            for (int i2 = i; i2 < document.getChildCount(); i2++) {
                Node child2 = document.getChild(i2);
                if (this.nodes == null || (child2 instanceof Element) || this.nodes.contains(child2)) {
                    if (child2 instanceof ProcessingInstruction) {
                        breakLine();
                    } else if ((child2 instanceof Comment) && this.this$0.withComments) {
                        breakLine();
                    }
                    writeChild(child2);
                }
            }
            flush();
        }

        @Override // nu.xom.Serializer
        protected final void write(Element element) throws IOException {
            if (element.getChildCount() == 0) {
                writeStartTag(element, false);
                writeEndTag(element);
                return;
            }
            Element child = element;
            boolean z = false;
            int i = -1;
            int[] iArrGrow = new int[10];
            int i2 = 0;
            iArrGrow[0] = -1;
            while (true) {
                if (z || child.getChildCount() <= 0) {
                    if (z) {
                        writeEndTag(child);
                        if (child == element) {
                            return;
                        }
                    } else {
                        writeChild(child);
                    }
                    z = false;
                    ParentNode parent = child.getParent();
                    if (parent.getChildCount() - 1 == i) {
                        child = parent;
                        i2--;
                        if (child != element) {
                            i = iArrGrow[i2];
                        }
                        z = true;
                    } else {
                        i++;
                        iArrGrow[i2] = i;
                        child = parent.getChild(i);
                    }
                } else {
                    writeStartTag(child, false);
                    child = child.getChild(0);
                    i = 0;
                    i2++;
                    iArrGrow = grow(iArrGrow, i2);
                    iArrGrow[i2] = 0;
                }
            }
        }

        private int[] grow(int[] iArr, int i) {
            if (i < iArr.length) {
                return iArr;
            }
            int[] iArr2 = new int[iArr.length * 2];
            System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
            return iArr2;
        }

        protected void writeStartTag(Element element, boolean z) throws IOException {
            ParentNode parentNode;
            boolean z2 = this.nodes == null || this.nodes.contains(element);
            if (z2) {
                this.inScope.pushContext();
                writeRaw("<");
                writeRaw(element.getQualifiedName());
            }
            TreeMap treeMap = new TreeMap();
            if (this.nodes == null) {
                ParentNode parent = element.getParent();
                Element element2 = parent instanceof Element ? (Element) parent : null;
                for (int i = 0; i < element.getNamespaceDeclarationCount(); i++) {
                    String namespacePrefix = element.getNamespacePrefix(i);
                    String namespaceURI = element.getNamespaceURI(namespacePrefix);
                    if (!namespaceURI.equals(this.inScope.getURI(namespacePrefix))) {
                        if (this.this$0.exclusive) {
                            if (needToDeclareNamespace(element, namespacePrefix, namespaceURI)) {
                                treeMap.put(namespacePrefix, namespaceURI);
                            }
                        } else if (!namespaceURI.equals("")) {
                            treeMap.put(namespacePrefix, namespaceURI);
                        } else if (element2 != null && !"".equals(element2.getNamespaceURI(""))) {
                            treeMap.put(namespacePrefix, namespaceURI);
                        }
                    }
                }
                writeNamespaceDeclarations(treeMap);
            } else {
                if (indexOf(element) != -1 && "".equals(element.getNamespaceURI())) {
                    ParentNode parent2 = element.getParent();
                    while (true) {
                        parentNode = parent2;
                        if (!(parentNode instanceof Element) || this.nodes.contains(parentNode)) {
                            break;
                        } else {
                            parent2 = parentNode.getParent();
                        }
                    }
                    if ((parentNode instanceof Element) && !"".equals(((Element) parentNode).getNamespaceURI(""))) {
                        treeMap.put("", "");
                    }
                }
                for (int i2 = r0 + 1; i2 < this.nodes.size(); i2++) {
                    Node node = this.nodes.get(i2);
                    if (!(node instanceof Namespace)) {
                        break;
                    }
                    Namespace namespace = (Namespace) node;
                    String prefix = namespace.getPrefix();
                    String value = namespace.getValue();
                    if (!value.equals(this.inScope.getURI(prefix))) {
                        if (!this.this$0.exclusive) {
                            treeMap.put(prefix, value);
                        } else if (needToDeclareNamespace(element, prefix, value)) {
                            treeMap.put(prefix, value);
                        }
                    }
                }
                writeNamespaceDeclarations(treeMap);
            }
            Attribute[] attributeArrSortAttributes = sortAttributes(element);
            for (int i3 = 0; i3 < attributeArrSortAttributes.length; i3++) {
                if (this.nodes == null || this.nodes.contains(attributeArrSortAttributes[i3]) || (attributeArrSortAttributes[i3].getNamespaceURI().equals("http://www.w3.org/XML/1998/namespace") && attributeArrSortAttributes[i3].getParent() != element)) {
                    write(attributeArrSortAttributes[i3]);
                }
            }
            if (z2) {
                writeRaw(">");
            }
        }

        private void writeNamespaceDeclarations(SortedMap sortedMap) throws IOException {
            for (Map.Entry entry : sortedMap.entrySet()) {
                String str = (String) entry.getKey();
                String str2 = (String) entry.getValue();
                writeRaw(" ");
                writeNamespaceDeclaration(str, str2);
                this.inScope.declarePrefix(str, str2);
            }
        }

        private boolean needToDeclareNamespace(Element element, String str, String str2) {
            if (visiblyUtilized(element, str, str2) || this.this$0.inclusiveNamespacePrefixes.contains(str)) {
                return noOutputAncestorUsesPrefix(element, str, str2);
            }
            return false;
        }

        private boolean visiblyUtilized(Element element, String str, String str2) {
            boolean z = false;
            String namespacePrefix = element.getNamespacePrefix();
            String namespaceURI = element.getNamespaceURI();
            if (str.equals(namespacePrefix) && namespaceURI.equals(str2)) {
                z = true;
            } else {
                for (int i = 0; i < element.getAttributeCount(); i++) {
                    Attribute attribute = element.getAttribute(i);
                    if ((this.nodes == null || this.nodes.contains(attribute)) && str.equals(attribute.getNamespacePrefix())) {
                        z = true;
                        break;
                    }
                }
            }
            return z;
        }

        private boolean noOutputAncestorUsesPrefix(Element element, String str, String str2) {
            ParentNode parent = element.getParent();
            if ((parent instanceof Document) && "".equals(str2)) {
                return false;
            }
            while (parent != null && !(parent instanceof Document)) {
                if (this.nodes == null || this.nodes.contains(parent)) {
                    Element element2 = (Element) parent;
                    if (element2.getNamespacePrefix().equals(str)) {
                        return !element2.getNamespaceURI(str).equals(str2);
                    }
                    for (int i = 0; i < element2.getAttributeCount(); i++) {
                        if (element2.getAttribute(i).getNamespacePrefix().equals(str)) {
                            return !element2.getNamespaceURI(str).equals(str2);
                        }
                    }
                }
                parent = parent.getParent();
            }
            return true;
        }

        private int indexOf(Element element) {
            for (int i = 0; i < this.nodes.size(); i++) {
                if (this.nodes.get(i) == element) {
                    return i;
                }
            }
            return -1;
        }

        @Override // nu.xom.Serializer
        protected void write(Attribute attribute) throws IOException {
            writeRaw(" ");
            writeRaw(attribute.getQualifiedName());
            writeRaw("=\"");
            writeRaw(prepareAttributeValue(attribute));
            writeRaw("\"");
        }

        @Override // nu.xom.Serializer
        protected void writeEndTag(Element element) throws IOException {
            if (this.nodes == null || this.nodes.contains(element)) {
                writeRaw("</");
                writeRaw(element.getQualifiedName());
                writeRaw(">");
                this.inScope.popContext();
            }
        }

        private Attribute[] sortAttributes(Element element) {
            TreeMap treeMap = new TreeMap();
            if (!this.this$0.exclusive && this.nodes != null && this.nodes.contains(element) && !this.nodes.contains(element.getParent())) {
                Nodes nodesQuery = element.query("ancestor::*/@xml:*", this.xmlcontext);
                if (nodesQuery.size() != 0) {
                    for (int size = nodesQuery.size() - 1; size >= 0; size--) {
                        Attribute attribute = (Attribute) nodesQuery.get(size);
                        String localName = attribute.getLocalName();
                        if (element.getAttribute(localName, "http://www.w3.org/XML/1998/namespace") == null && ((!this.this$0.v11 || !"id".equals(localName)) && !treeMap.containsKey(localName) && (!this.this$0.v11 || "lang".equals(localName) || "space".equals(localName)))) {
                            if (this.nodes.contains((Element) attribute.getParent())) {
                                treeMap.put(localName, null);
                            } else {
                                treeMap.put(localName, attribute);
                            }
                        }
                    }
                }
                Iterator it = treeMap.values().iterator();
                while (it.hasNext()) {
                    if (((Attribute) it.next()) == null) {
                        it.remove();
                    }
                }
                if (this.this$0.v11) {
                    List omittedBases = getOmittedBases(element);
                    Attribute attribute2 = element.getAttribute("base", "http://www.w3.org/XML/1998/namespace");
                    String strJoinURIReferences = "";
                    if (attribute2 != null) {
                        strJoinURIReferences = attribute2.getValue();
                        element.removeAttribute(attribute2);
                    }
                    if (!omittedBases.isEmpty()) {
                        for (int i = 0; i < omittedBases.size(); i++) {
                            strJoinURIReferences = Canonicalizer.joinURIReferences((String) omittedBases.get(i), strJoinURIReferences);
                        }
                    }
                    if (strJoinURIReferences != null && strJoinURIReferences.length() > 0) {
                        treeMap.put("base", new Attribute("xml:base", "http://www.w3.org/XML/1998/namespace", strJoinURIReferences));
                    }
                }
            }
            int attributeCount = element.getAttributeCount();
            Attribute[] attributeArr = new Attribute[attributeCount + treeMap.size()];
            for (int i2 = 0; i2 < attributeCount; i2++) {
                attributeArr[i2] = element.getAttribute(i2);
            }
            Iterator it2 = treeMap.values().iterator();
            for (int i3 = attributeCount; i3 < attributeArr.length; i3++) {
                attributeArr[i3] = (Attribute) it2.next();
            }
            Arrays.sort(attributeArr, Canonicalizer.comparator);
            return attributeArr;
        }

        private List getOmittedBases(Element element) {
            ArrayList arrayList = new ArrayList();
            ParentNode parent = element.getParent();
            while (true) {
                ParentNode parentNode = parent;
                if (parentNode == null || !(parentNode instanceof Element) || this.nodes.contains(parentNode)) {
                    break;
                }
                Element element2 = (Element) parentNode;
                String attributeValue = element2.getAttributeValue("base", "http://www.w3.org/XML/1998/namespace");
                if (attributeValue != null) {
                    arrayList.add(attributeValue);
                }
                parent = element2.getParent();
            }
            return arrayList;
        }

        private String prepareAttributeValue(Attribute attribute) {
            String value = attribute.getValue();
            StringBuffer stringBuffer = new StringBuffer(value.length());
            if (attribute.getType().equals(Attribute.Type.CDATA) || attribute.getType().equals(Attribute.Type.UNDECLARED)) {
                for (char c : value.toCharArray()) {
                    if (c == '\t') {
                        stringBuffer.append("&#x9;");
                    } else if (c == '\n') {
                        stringBuffer.append("&#xA;");
                    } else if (c == '\r') {
                        stringBuffer.append("&#xD;");
                    } else if (c == '\"') {
                        stringBuffer.append("&quot;");
                    } else if (c == '&') {
                        stringBuffer.append("&amp;");
                    } else if (c == '<') {
                        stringBuffer.append("&lt;");
                    } else {
                        stringBuffer.append(c);
                    }
                }
            } else {
                char[] charArray = value.toCharArray();
                boolean z = false;
                for (int i = 0; i < charArray.length; i++) {
                    if (charArray[i] != ' ') {
                        z = true;
                        if (charArray[i] == '\t') {
                            stringBuffer.append("&#x9;");
                        } else if (charArray[i] == '\n') {
                            stringBuffer.append("&#xA;");
                        } else if (charArray[i] == '\r') {
                            stringBuffer.append("&#xD;");
                        } else if (charArray[i] == '\"') {
                            stringBuffer.append("&quot;");
                        } else if (charArray[i] == '&') {
                            stringBuffer.append("&amp;");
                        } else if (charArray[i] == '<') {
                            stringBuffer.append("&lt;");
                        } else {
                            stringBuffer.append(charArray[i]);
                        }
                    } else if (i != charArray.length - 1 && charArray[i + 1] != ' ' && z) {
                        stringBuffer.append(charArray[i]);
                    }
                }
            }
            return stringBuffer.toString();
        }

        @Override // nu.xom.Serializer
        protected final void write(Text text) throws IOException {
            if (this.nodes == null || this.nodes.contains(text)) {
                String value = text.getValue();
                StringBuffer stringBuffer = new StringBuffer(value.length());
                for (int i = 0; i < value.length(); i++) {
                    char cCharAt = value.charAt(i);
                    if (cCharAt == '\r') {
                        stringBuffer.append("&#xD;");
                    } else if (cCharAt == '&') {
                        stringBuffer.append("&amp;");
                    } else if (cCharAt == '<') {
                        stringBuffer.append("&lt;");
                    } else if (cCharAt == '>') {
                        stringBuffer.append("&gt;");
                    } else {
                        stringBuffer.append(cCharAt);
                    }
                }
                writeRaw(stringBuffer.toString());
            }
        }

        @Override // nu.xom.Serializer
        protected final void write(Comment comment) throws IOException {
            if (this.this$0.withComments) {
                if (this.nodes == null || this.nodes.contains(comment)) {
                    super.write(comment);
                }
            }
        }

        @Override // nu.xom.Serializer
        protected final void write(ProcessingInstruction processingInstruction) throws IOException {
            if (this.nodes == null || this.nodes.contains(processingInstruction)) {
                super.write(processingInstruction);
            }
        }

        @Override // nu.xom.Serializer
        protected final void write(DocType docType) {
        }

        public void write(Node node) throws IOException {
            if (node instanceof Document) {
                write((Document) node);
                return;
            }
            if (node instanceof Attribute) {
                write((Attribute) node);
            } else if (node instanceof Namespace) {
                write((Namespace) node);
            } else {
                writeChild(node);
            }
        }

        private void write(Namespace namespace) throws IOException {
            String prefix = namespace.getPrefix();
            String value = namespace.getValue();
            writeRaw(" xmlns");
            if (!"".equals(prefix)) {
                writeRaw(":");
                writeRaw(prefix);
            }
            writeRaw("=\"");
            writeAttributeValue(value);
            writeRaw("\"");
        }
    }

    public Canonicalizer(OutputStream outputStream) {
        this(outputStream, true, false);
    }

    public Canonicalizer(OutputStream outputStream, boolean z) {
        this(outputStream, z, false);
    }

    private Canonicalizer(OutputStream outputStream, boolean z, boolean z2) {
        this.exclusive = false;
        this.v11 = false;
        this.inclusiveNamespacePrefixes = new ArrayList();
        this.serializer = new CanonicalXMLSerializer(this, outputStream);
        this.serializer.setLineSeparator("\n");
        this.withComments = z;
        this.exclusive = z2;
    }

    public Canonicalizer(OutputStream outputStream, String str) {
        this.exclusive = false;
        this.v11 = false;
        this.inclusiveNamespacePrefixes = new ArrayList();
        if (str == null) {
            throw new NullPointerException("Null algorithm");
        }
        this.serializer = new CanonicalXMLSerializer(this, outputStream);
        this.serializer.setLineSeparator("\n");
        if (str.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315")) {
            this.withComments = false;
            this.exclusive = false;
            return;
        }
        if (str.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments")) {
            this.withComments = true;
            this.exclusive = false;
            return;
        }
        if (str.equals("http://www.w3.org/2001/10/xml-exc-c14n#")) {
            this.withComments = false;
            this.exclusive = true;
            return;
        }
        if (str.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")) {
            this.withComments = true;
            this.exclusive = true;
        } else if (str.equals("http://www.w3.org/2006/12/xml-c14n11")) {
            this.withComments = false;
            this.exclusive = false;
            this.v11 = true;
        } else {
            if (!str.equals("http://www.w3.org/2006/12/xml-c14n11#WithComments")) {
                throw new CanonicalizationException(new StringBuffer().append("Unsupported canonicalization algorithm: ").append(str).toString());
            }
            this.withComments = true;
            this.exclusive = false;
            this.v11 = true;
        }
    }

    public final void write(Node node) throws IOException {
        ParentNode parentNode;
        if (node instanceof Element) {
            Element element = null;
            if (node.getDocument() == null) {
                element = new Element("pseudo");
                new Document(element);
                ParentNode parent = (ParentNode) node;
                while (true) {
                    parentNode = parent;
                    if (parentNode.getParent() == null) {
                        break;
                    } else {
                        parent = parentNode.getParent();
                    }
                }
                element.appendChild(parentNode);
            }
            try {
                Nodes nodesQuery = node.query(".//. | .//@* | .//namespace::*");
                if (this.exclusive) {
                    HashSet hashSet = new HashSet(nodesQuery.size());
                    for (int i = 0; i < nodesQuery.size(); i++) {
                        Node node2 = nodesQuery.get(i);
                        if (node2 instanceof Element) {
                            hashSet.add(((Element) node2).getNamespacePrefix());
                        } else if (node2 instanceof Attribute) {
                            String namespacePrefix = ((Attribute) node2).getNamespacePrefix();
                            if (!"".equals(namespacePrefix)) {
                                hashSet.add(namespacePrefix);
                            }
                        }
                    }
                    int i2 = 0;
                    while (i2 < nodesQuery.size()) {
                        Node node3 = nodesQuery.get(i2);
                        if ((node3 instanceof Namespace) && !hashSet.contains(((Namespace) node3).getPrefix())) {
                            nodesQuery.remove(i2);
                            i2--;
                        }
                        i2++;
                    }
                }
                write(nodesQuery);
                if (element != null) {
                    element.removeChild(0);
                }
            } catch (Throwable th) {
                if (element != null) {
                    element.removeChild(0);
                }
                throw th;
            }
        } else {
            this.serializer.nodes = null;
            this.serializer.write(node);
        }
        this.serializer.flush();
    }

    public final void write(Nodes nodes) throws IOException {
        if (nodes.size() > 0) {
            Document document = nodes.get(0).getDocument();
            if (document == null) {
                throw new CanonicalizationException("Canonicalization is not defined for detached nodes");
            }
            this.serializer.nodes = sort(nodes);
            this.serializer.write(document);
            this.serializer.flush();
        }
    }

    public final void setInclusiveNamespacePrefixList(String str) throws IOException {
        this.inclusiveNamespacePrefixes.clear();
        if (!this.exclusive || str == null) {
            return;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(str, " \t\r\n", false);
        while (stringTokenizer.hasMoreTokens()) {
            this.inclusiveNamespacePrefixes.add(stringTokenizer.nextToken());
        }
    }

    private Nodes sort(Nodes nodes) {
        Document document = nodes.get(0).getDocument();
        if (nodes.size() <= 1) {
            return new Nodes(nodes.get(0));
        }
        Nodes nodes2 = new Nodes();
        ArrayList arrayList = new ArrayList(nodes.size());
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            arrayList.add(node);
            if (node instanceof Namespace) {
                arrayList2.add(node);
            }
        }
        sort(arrayList, arrayList2, nodes2, document);
        if (!arrayList.isEmpty()) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                if (document != ((Node) it.next()).getDocument()) {
                    throw new CanonicalizationException("Cannot canonicalize subsets that contain nodes from more than one document");
                }
            }
        }
        return nodes2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String joinURIReferences(String str, String str2) {
        if ("..".equals(str)) {
            str = "../";
        }
        if ("..".equals(str2)) {
            str2 = "../";
        }
        return str2 == null ? str : URIJoiner.absolutize(str, str2);
    }

    private static void sort(List list, List list2, Nodes nodes, ParentNode parentNode) {
        if (list.isEmpty()) {
            return;
        }
        if (list.contains(parentNode)) {
            nodes.append(parentNode);
            list.remove(parentNode);
        }
        int childCount = parentNode.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Node child = parentNode.getChild(i);
            if (child instanceof Element) {
                Element element = (Element) child;
                if (list.contains(element)) {
                    nodes.append(element);
                    list.remove(element);
                }
                if (!list2.isEmpty()) {
                    Iterator it = list.iterator();
                    while (it.hasNext()) {
                        Object next = it.next();
                        if (next instanceof Namespace) {
                            Node node = (Namespace) next;
                            if (element == node.getParent()) {
                                nodes.append(node);
                                it.remove();
                            }
                        }
                    }
                }
                for (int i2 = 0; i2 < element.getAttributeCount(); i2++) {
                    Node attribute = element.getAttribute(i2);
                    if (list.contains(attribute)) {
                        nodes.append(attribute);
                        list.remove(attribute);
                        if (list.isEmpty()) {
                            return;
                        }
                    }
                }
                sort(list, list2, nodes, element);
            } else if (list.contains(child)) {
                nodes.append(child);
                list.remove(child);
                if (list.isEmpty()) {
                    return;
                }
            } else {
                continue;
            }
        }
    }
}
