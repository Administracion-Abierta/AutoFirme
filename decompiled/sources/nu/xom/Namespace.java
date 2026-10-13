package nu.xom;

import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/Namespace.class */
public final class Namespace extends Node {
    private final String prefix;
    private final String uri;
    public static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";

    public Namespace(String str, String str2, Element element) {
        if (str == null) {
            str = "";
        } else {
            if (CanonicalizerBase.XMLNS.equals(str)) {
                throw new IllegalNameException("The xmlns prefix may not be bound to a URI.");
            }
            if ("xml".equals(str) && !"http://www.w3.org/XML/1998/namespace".equals(str2)) {
                throw new NamespaceConflictException("The prefix xml can only be bound to the URI http://www.w3.org/XML/1998/namespace");
            }
        }
        if (str.length() != 0) {
            Verifier.checkNCName(str);
        }
        if (str2 == null) {
            str2 = "";
        } else if (str2.equals("http://www.w3.org/XML/1998/namespace") && !"xml".equals(str)) {
            throw new NamespaceConflictException("The URI http://www.w3.org/XML/1998/namespace can only be bound to the prefix xml");
        }
        if (str2.length() != 0) {
            Verifier.checkAbsoluteURIReference(str2);
        } else if (str.length() != 0) {
            throw new NamespaceConflictException("Prefixed elements must have namespace URIs.");
        }
        this.prefix = str;
        this.uri = str2;
        super.setParent(element);
    }

    public String getPrefix() {
        return this.prefix;
    }

    @Override // nu.xom.Node
    public String getValue() {
        return this.uri;
    }

    @Override // nu.xom.Node
    public Node getChild(int i) {
        throw new IndexOutOfBoundsException("Namespaces do not have children");
    }

    @Override // nu.xom.Node
    public int getChildCount() {
        return 0;
    }

    @Override // nu.xom.Node
    public Node copy() {
        return new Namespace(this.prefix, this.uri, null);
    }

    @Override // nu.xom.Node
    public void detach() {
        super.setParent(null);
    }

    @Override // nu.xom.Node
    public String toXML() {
        return new StringBuffer().append(CanonicalizerBase.XMLNS).append(this.prefix.equals("") ? "" : ":").append(this.prefix).append("=\"").append(this.uri).append(OperatorName.SHOW_TEXT_LINE_AND_SPACE).toString();
    }

    public String toString() {
        return new StringBuffer().append("[Namespace: ").append(toXML()).append("]").toString();
    }
}
