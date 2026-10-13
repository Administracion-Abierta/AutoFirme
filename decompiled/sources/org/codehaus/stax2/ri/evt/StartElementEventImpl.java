package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.EmptyIterator;
import org.codehaus.stax2.ri.EmptyNamespaceContext;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/evt/StartElementEventImpl.class */
public class StartElementEventImpl extends BaseEventImpl implements StartElement {
    protected final QName _name;
    protected final ArrayList<Attribute> _attrs;
    protected final ArrayList<Namespace> _nsDecls;
    protected NamespaceContext _parentNsCtxt;
    NamespaceContext _actualNsCtxt;

    protected StartElementEventImpl(Location loc, QName name, ArrayList<Attribute> attrs, ArrayList<Namespace> nsDecls, NamespaceContext parentNsCtxt) {
        super(loc);
        this._actualNsCtxt = null;
        this._name = name;
        this._attrs = attrs;
        this._nsDecls = nsDecls;
        this._parentNsCtxt = parentNsCtxt == null ? EmptyNamespaceContext.getInstance() : parentNsCtxt;
    }

    public static StartElementEventImpl construct(Location loc, QName name, Iterator<?> attrIt, Iterator<?> nsDeclIt, NamespaceContext nsCtxt) {
        ArrayList<Attribute> attrs;
        ArrayList<Namespace> nsDecls;
        if (attrIt == null || !attrIt.hasNext()) {
            attrs = null;
        } else {
            attrs = new ArrayList<>();
            do {
                attrs.add((Attribute) attrIt.next());
            } while (attrIt.hasNext());
        }
        if (nsDeclIt == null || !nsDeclIt.hasNext()) {
            nsDecls = null;
        } else {
            nsDecls = new ArrayList<>();
            do {
                nsDecls.add((Namespace) nsDeclIt.next());
            } while (nsDeclIt.hasNext());
        }
        return new StartElementEventImpl(loc, name, attrs, nsDecls, nsCtxt);
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
            String prefix = this._name.getPrefix();
            if (prefix != null && prefix.length() > 0) {
                w.write(prefix);
                w.write(58);
            }
            w.write(this._name.getLocalPart());
            if (this._nsDecls != null) {
                int len = this._nsDecls.size();
                for (int i = 0; i < len; i++) {
                    w.write(32);
                    this._nsDecls.get(i).writeAsEncodedUnicode(w);
                }
            }
            if (this._attrs != null) {
                int len2 = this._attrs.size();
                for (int i2 = 0; i2 < len2; i2++) {
                    Attribute attr = this._attrs.get(i2);
                    if (attr.isSpecified()) {
                        w.write(32);
                        attr.writeAsEncodedUnicode(w);
                    }
                }
            }
            w.write(62);
        } catch (IOException ie) {
            throw new XMLStreamException(ie);
        }
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl, org.codehaus.stax2.evt.XMLEvent2
    public void writeUsing(XMLStreamWriter2 sw) throws XMLStreamException {
        QName n = this._name;
        sw.writeStartElement(n.getPrefix(), n.getLocalPart(), n.getNamespaceURI());
        if (this._nsDecls != null) {
            int len = this._nsDecls.size();
            for (int i = 0; i < len; i++) {
                Namespace ns = this._nsDecls.get(i);
                String prefix = ns.getPrefix();
                String uri = ns.getNamespaceURI();
                if (prefix == null || prefix.length() == 0) {
                    sw.writeDefaultNamespace(uri);
                } else {
                    sw.writeNamespace(prefix, uri);
                }
            }
        }
        if (this._attrs != null) {
            int len2 = this._attrs.size();
            for (int i2 = 0; i2 < len2; i2++) {
                Attribute attr = this._attrs.get(i2);
                if (attr.isSpecified()) {
                    QName name = attr.getName();
                    sw.writeAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attr.getValue());
                }
            }
        }
    }

    public final QName getName() {
        return this._name;
    }

    public Iterator<Namespace> getNamespaces() {
        if (this._nsDecls == null) {
            return EmptyIterator.getInstance();
        }
        return this._nsDecls.iterator();
    }

    public NamespaceContext getNamespaceContext() {
        if (this._actualNsCtxt == null) {
            if (this._nsDecls == null) {
                this._actualNsCtxt = this._parentNsCtxt;
            } else {
                this._actualNsCtxt = MergedNsContext.construct(this._parentNsCtxt, this._nsDecls);
            }
        }
        return this._actualNsCtxt;
    }

    public String getNamespaceURI(String prefix) {
        if (this._nsDecls != null) {
            if (prefix == null) {
                prefix = "";
            }
            int len = this._nsDecls.size();
            for (int i = 0; i < len; i++) {
                Namespace ns = this._nsDecls.get(i);
                String thisPrefix = ns.getPrefix();
                if (thisPrefix == null) {
                    thisPrefix = "";
                }
                if (prefix.equals(thisPrefix)) {
                    return ns.getNamespaceURI();
                }
            }
            return null;
        }
        return null;
    }

    public Attribute getAttributeByName(QName nameIn) {
        if (this._attrs == null) {
            return null;
        }
        String ln = nameIn.getLocalPart();
        String uri = nameIn.getNamespaceURI();
        int len = this._attrs.size();
        boolean notInNs = uri == null || uri.length() == 0;
        for (int i = 0; i < len; i++) {
            Attribute attr = this._attrs.get(i);
            QName name = attr.getName();
            if (name.getLocalPart().equals(ln)) {
                String thisUri = name.getNamespaceURI();
                if (notInNs) {
                    if (thisUri == null || thisUri.length() == 0) {
                        return attr;
                    }
                } else if (uri.equals(thisUri)) {
                    return attr;
                }
            }
        }
        return null;
    }

    public Iterator<Attribute> getAttributes() {
        if (this._attrs == null) {
            return EmptyIterator.getInstance();
        }
        return this._attrs.iterator();
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
        if (this._name.equals(other.getName()) && iteratedEquals(getNamespaces(), other.getNamespaces())) {
            return iteratedEquals(getAttributes(), other.getAttributes());
        }
        return false;
    }

    @Override // org.codehaus.stax2.ri.evt.BaseEventImpl
    public int hashCode() {
        int hash = this._name.hashCode();
        return addHash(getAttributes(), addHash(getNamespaces(), hash));
    }
}
