package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/stax/XMLSecAttributeImpl.class */
public class XMLSecAttributeImpl extends XMLSecEventBaseImpl implements XMLSecAttribute {
    private final QName name;
    private final String value;
    private XMLSecNamespace attributeNamespace;

    public XMLSecAttributeImpl(QName name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override // java.lang.Comparable
    public int compareTo(XMLSecAttribute o) {
        int namespacePartCompare = this.name.getNamespaceURI().compareTo(o.getName().getNamespaceURI());
        if (namespacePartCompare != 0) {
            return namespacePartCompare;
        }
        return this.name.getLocalPart().compareTo(o.getName().getLocalPart());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof XMLSecAttribute)) {
            return false;
        }
        XMLSecAttribute comparableAttribute = (XMLSecAttribute) obj;
        if (comparableAttribute.hashCode() != hashCode()) {
            return false;
        }
        return comparableAttribute.getName().getLocalPart().equals(this.name.getLocalPart());
    }

    public int hashCode() {
        return this.name.getLocalPart().hashCode();
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecAttribute
    public XMLSecNamespace getAttributeNamespace() {
        if (this.attributeNamespace == null) {
            this.attributeNamespace = XMLSecNamespaceImpl.getInstance(this.name.getPrefix(), this.name.getNamespaceURI());
        }
        return this.attributeNamespace;
    }

    public QName getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public String getDTDType() {
        return "CDATA";
    }

    public boolean isSpecified() {
        return true;
    }

    public int getEventType() {
        return 10;
    }

    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public boolean isAttribute() {
        return true;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            String prefix = getName().getPrefix();
            if (prefix != null && !prefix.isEmpty()) {
                writer.write(prefix);
                writer.write(58);
            }
            writer.write(getName().getLocalPart());
            writer.write("=\"");
            writeEncoded(writer, getValue());
            writer.write(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
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
}
