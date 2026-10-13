package com.ctc.wstx.evt;

import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.util.BaseNsContext;
import com.ctc.wstx.util.DataUtil;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.evt.BaseEventImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/evt/BaseStartElement.class */
abstract class BaseStartElement extends BaseEventImpl implements StartElement {
    protected final QName mName;
    protected final BaseNsContext mNsCtxt;

    public abstract Attribute getAttributeByName(QName qName);

    public abstract Iterator<Attribute> getAttributes();

    protected abstract void outputNsAndAttr(Writer writer) throws IOException;

    protected abstract void outputNsAndAttr(XMLStreamWriter xMLStreamWriter) throws XMLStreamException;

    protected BaseStartElement(Location loc, QName name, BaseNsContext nsCtxt) {
        super(loc);
        this.mName = name;
        this.mNsCtxt = nsCtxt;
    }

    public final QName getName() {
        return this.mName;
    }

    public Iterator<Namespace> getNamespaces() {
        if (this.mNsCtxt == null) {
            return DataUtil.emptyIterator();
        }
        return this.mNsCtxt.getNamespaces();
    }

    public NamespaceContext getNamespaceContext() {
        return this.mNsCtxt;
    }

    public String getNamespaceURI(String prefix) {
        if (this.mNsCtxt == null) {
            return null;
        }
        return this.mNsCtxt.getNamespaceURI(prefix);
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public StartElement asStartElement() {
        return this;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int getEventType() {
        return 1;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public boolean isStartElement() {
        return true;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            w.write(60);
            String prefix = this.mName.getPrefix();
            if (prefix != null && prefix.length() > 0) {
                w.write(prefix);
                w.write(58);
            }
            w.write(this.mName.getLocalPart());
            outputNsAndAttr(w);
            w.write(62);
        } catch (IOException ie) {
            throw new WstxIOException(ie);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl, org.codehaus.stax2.evt.XMLEvent2
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        QName n = this.mName;
        w.writeStartElement(n.getPrefix(), n.getLocalPart(), n.getNamespaceURI());
        outputNsAndAttr(w);
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof StartElement)) {
            return false;
        }
        StartElement other = (StartElement) o;
        if (this.mName.equals(other.getName()) && iteratedEquals(getNamespaces(), other.getNamespaces())) {
            return iteratedEquals(getAttributes(), other.getAttributes());
        }
        return false;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int hashCode() {
        int hash = this.mName.hashCode();
        return addHash(getAttributes(), addHash(getNamespaces(), hash));
    }
}
