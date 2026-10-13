package org.apache.xml.security.transforms.params;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.transforms.TransformParam;
import org.apache.xml.security.utils.ElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/transforms/params/InclusiveNamespaces.class */
public class InclusiveNamespaces extends ElementProxy implements TransformParam {
    public static final String _TAG_EC_INCLUSIVENAMESPACES = "InclusiveNamespaces";
    public static final String _ATT_EC_PREFIXLIST = "PrefixList";
    public static final String ExclusiveCanonicalizationNamespace = "http://www.w3.org/2001/10/xml-exc-c14n#";

    public InclusiveNamespaces(Document doc, String prefixList) {
        this(doc, prefixStr2Set(prefixList));
    }

    public InclusiveNamespaces(Document doc, Set<String> prefixes) {
        SortedSet<String> prefixList;
        super(doc);
        if (prefixes instanceof SortedSet) {
            prefixList = (SortedSet) prefixes;
        } else {
            prefixList = new TreeSet<>(prefixes);
        }
        StringBuilder sb = new StringBuilder();
        for (String prefix : prefixList) {
            if (CanonicalizerBase.XMLNS.equals(prefix)) {
                sb.append("#default ");
            } else {
                sb.append(prefix);
                sb.append(' ');
            }
        }
        setLocalAttribute(_ATT_EC_PREFIXLIST, sb.toString().trim());
    }

    public InclusiveNamespaces(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public String getInclusiveNamespaces() {
        return getLocalAttribute(_ATT_EC_PREFIXLIST);
    }

    public static SortedSet<String> prefixStr2Set(String inclusiveNamespaces) {
        SortedSet<String> prefixes = new TreeSet<>();
        if (inclusiveNamespaces == null || inclusiveNamespaces.length() == 0) {
            return prefixes;
        }
        String[] tokens = inclusiveNamespaces.split("\\s");
        for (String prefix : tokens) {
            if ("#default".equals(prefix)) {
                prefixes.add(CanonicalizerBase.XMLNS);
            } else {
                prefixes.add(prefix);
            }
        }
        return prefixes;
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseNamespace() {
        return "http://www.w3.org/2001/10/xml-exc-c14n#";
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return _TAG_EC_INCLUSIVENAMESPACES;
    }
}
