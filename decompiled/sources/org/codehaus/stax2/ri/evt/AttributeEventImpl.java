package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import org.codehaus.stax2.XMLStreamWriter2;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/evt/AttributeEventImpl.class */
public class AttributeEventImpl extends BaseEventImpl implements Attribute {
    final QName mName;
    final String mValue;
    final boolean mWasSpecified;

    public AttributeEventImpl(Location loc, String localName, String uri, String prefix, String value, boolean wasSpecified) {
        super(loc);
        this.mValue = value;
        if (prefix == null) {
            if (uri == null) {
                this.mName = new QName(localName);
            } else {
                this.mName = new QName(uri, localName);
            }
        } else {
            this.mName = new QName(uri == null ? "" : uri, localName, prefix);
        }
        this.mWasSpecified = wasSpecified;
    }

    public AttributeEventImpl(Location loc, QName name, String value, boolean wasSpecified) {
        super(loc);
        this.mName = name;
        this.mValue = value;
        this.mWasSpecified = wasSpecified;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int getEventType() {
        return 10;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public boolean isAttribute() {
        return true;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        String prefix = this.mName.getPrefix();
        if (prefix != null) {
            try {
                if (prefix.length() > 0) {
                    w.write(prefix);
                    w.write(58);
                }
            } catch (IOException ie) {
                throwFromIOE(ie);
                return;
            }
        }
        w.write(this.mName.getLocalPart());
        w.write(61);
        w.write(34);
        writeEscapedAttrValue(w, this.mValue);
        w.write(34);
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl, org.codehaus.stax2.evt.XMLEvent2
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        QName n = this.mName;
        w.writeAttribute(n.getPrefix(), n.getLocalPart(), n.getNamespaceURI(), this.mValue);
    }

    public String getDTDType() {
        return "CDATA";
    }

    public QName getName() {
        return this.mName;
    }

    public String getValue() {
        return this.mValue;
    }

    public boolean isSpecified() {
        return this.mWasSpecified;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof Attribute)) {
            return false;
        }
        Attribute other = (Attribute) o;
        if (this.mName.equals(other.getName()) && this.mValue.equals(other.getValue()) && isSpecified() == other.isSpecified()) {
            return stringsWithNullsEqual(getDTDType(), other.getDTDType());
        }
        return false;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int hashCode() {
        return this.mName.hashCode() ^ this.mValue.hashCode();
    }

    protected static void writeEscapedAttrValue(Writer w, String value) throws IOException {
        int i = 0;
        int len = value.length();
        do {
            int start = i;
            char c = 0;
            while (i < len) {
                c = value.charAt(i);
                if (c == '<' || c == '&' || c == '\"') {
                    break;
                } else {
                    i++;
                }
            }
            int outLen = i - start;
            if (outLen > 0) {
                w.write(value, start, outLen);
            }
            if (i < len) {
                if (c == '<') {
                    w.write("&lt;");
                } else if (c == '&') {
                    w.write("&amp;");
                } else if (c == '\"') {
                    w.write("&quot;");
                }
            }
            i++;
        } while (i < len);
    }
}
