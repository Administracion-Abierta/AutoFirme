package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/stax/XMLSecNamespaceImpl.class */
public final class XMLSecNamespaceImpl extends XMLSecEventBaseImpl implements XMLSecNamespace {
    private static final Map<String, Map<String, XMLSecNamespace>> XMLSEC_NS_MAP = Collections.synchronizedMap(new WeakHashMap());
    private String prefix;
    private final String uri;
    private QName qName;

    private XMLSecNamespaceImpl(String prefix, String uri) {
        this.prefix = prefix;
        this.uri = uri;
    }

    public static XMLSecNamespace getInstance(String prefix, String uri) {
        String prefixToUse = prefix;
        if (prefixToUse == null) {
            prefixToUse = "";
        }
        String uriToUse = uri;
        if (uriToUse == null) {
            uriToUse = "";
        }
        Map<String, XMLSecNamespace> nsMap = XMLSEC_NS_MAP.get(prefixToUse);
        if (nsMap != null) {
            XMLSecNamespace xmlSecNamespace = nsMap.get(uriToUse);
            if (xmlSecNamespace != null) {
                return xmlSecNamespace;
            }
            XMLSecNamespace xmlSecNamespace2 = new XMLSecNamespaceImpl(prefixToUse, uriToUse);
            nsMap.put(uriToUse, xmlSecNamespace2);
            return xmlSecNamespace2;
        }
        Map<String, XMLSecNamespace> nsMap2 = new WeakHashMap<>();
        XMLSecNamespace xmlSecNamespace3 = new XMLSecNamespaceImpl(prefixToUse, uriToUse);
        nsMap2.put(uriToUse, xmlSecNamespace3);
        XMLSEC_NS_MAP.put(prefixToUse, nsMap2);
        return xmlSecNamespace3;
    }

    @Override // java.lang.Comparable
    public int compareTo(XMLSecNamespace o) {
        return this.prefix.compareTo(o.getPrefix());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof XMLSecNamespace)) {
            return false;
        }
        XMLSecNamespace comparableNamespace = (XMLSecNamespace) obj;
        if (comparableNamespace.hashCode() != hashCode()) {
            return false;
        }
        return comparableNamespace.getPrefix().equals(this.prefix);
    }

    public int hashCode() {
        return this.prefix.hashCode();
    }

    public QName getName() {
        if (this.qName == null) {
            this.qName = new QName("http://www.w3.org/2000/xmlns/", this.prefix);
        }
        return this.qName;
    }

    public String getValue() {
        return this.uri;
    }

    public String getDTDType() {
        return "CDATA";
    }

    public boolean isSpecified() {
        return true;
    }

    public String getNamespaceURI() {
        return this.uri;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public boolean isDefaultNamespaceDeclaration() {
        return this.prefix.length() == 0;
    }

    public int getEventType() {
        return 13;
    }

    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public boolean isNamespace() {
        return true;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write(CanonicalizerBase.XMLNS);
            if (getPrefix() != null && !getPrefix().isEmpty()) {
                writer.write(58);
                writer.write(getPrefix());
            }
            writer.write("=\"");
            writeEncoded(writer, getValue());
            writer.write("\"");
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    private void writeEncoded(Writer writer, String text) throws IOException {
        int length = text.length();
        int idx = 0;
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (c == '&') {
                writer.write(text, idx, i - idx);
                writer.write("&amp;");
                idx = i + 1;
            } else if (c == '\"') {
                writer.write(text, idx, i - idx);
                writer.write("&quot;");
                idx = i + 1;
            }
        }
        writer.write(text, idx, length - idx);
    }

    public String toString() {
        if (this.prefix == null || this.prefix.isEmpty()) {
            return "xmlns=\"" + this.uri + "\"";
        }
        return "xmlns:" + this.prefix + "=\"" + this.uri + "\"";
    }
}
