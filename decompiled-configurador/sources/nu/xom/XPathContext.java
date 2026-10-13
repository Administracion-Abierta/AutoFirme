package nu.xom;

import java.util.HashMap;
import java.util.Map;
import nu.xom.jaxen.NamespaceContext;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/XPathContext.class */
public final class XPathContext {
    private Map namespaces;

    /* JADX INFO: renamed from: nu.xom.XPathContext$1, reason: invalid class name */
    /* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/XPathContext$1.class */
    static class AnonymousClass1 {
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/XPathContext$JaxenNamespaceContext.class */
    private class JaxenNamespaceContext implements NamespaceContext {
        private JaxenNamespaceContext() {
        }

        @Override // nu.xom.jaxen.NamespaceContext
        public String translateNamespacePrefixToUri(String str) {
            return XPathContext.this.lookup(str);
        }

        /* synthetic */ JaxenNamespaceContext(XPathContext xPathContext, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public XPathContext(String str, String str2) {
        this();
        addNamespace(str, str2);
    }

    public XPathContext() {
        this.namespaces = new HashMap();
        addNamespace(CanonicalizerBase.XML, "http://www.w3.org/XML/1998/namespace");
    }

    public void addNamespace(String str, String str2) {
        if (CanonicalizerBase.XML.equals(str) && !"http://www.w3.org/XML/1998/namespace".equals(str2)) {
            throw new NamespaceConflictException(new StringBuffer().append("Wrong namespace URI for xml prefix: ").append(str2).toString());
        }
        if ("".equals(str2)) {
            str2 = null;
        }
        if (str == null) {
            throw new NullPointerException("Prefixes used in XPath expressions cannot be null");
        }
        if ("".equals(str)) {
            throw new NamespaceConflictException("XPath expressions do not use the default namespace");
        }
        Verifier.checkNCName(str);
        if (str2 == null) {
            this.namespaces.remove(str);
        } else {
            this.namespaces.put(str, str2);
        }
    }

    public static XPathContext makeNamespaceContext(Element element) {
        XPathContext xPathContext = new XPathContext();
        xPathContext.namespaces = element.getNamespacePrefixesInScope();
        return xPathContext;
    }

    NamespaceContext getJaxenContext() {
        return new JaxenNamespaceContext(this, null);
    }

    public String lookup(String str) {
        if ("".equals(str)) {
            return null;
        }
        return (String) this.namespaces.get(str);
    }
}
