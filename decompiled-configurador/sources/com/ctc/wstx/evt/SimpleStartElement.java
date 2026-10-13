package com.ctc.wstx.evt;

import com.ctc.wstx.io.TextEscaper;
import com.ctc.wstx.util.BaseNsContext;
import com.ctc.wstx.util.DataUtil;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.XMLStreamWriter2;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/evt/SimpleStartElement.class */
public class SimpleStartElement extends BaseStartElement {
    final Map<QName, Attribute> mAttrs;

    @Override // com.ctc.wstx.evt.BaseStartElement, org.codehaus.stax2.ri.evt.BaseEventImpl
    public /* bridge */ /* synthetic */ int hashCode() {
        return super.hashCode();
    }

    @Override // com.ctc.wstx.evt.BaseStartElement, org.codehaus.stax2.ri.evt.BaseEventImpl
    public /* bridge */ /* synthetic */ boolean equals(Object obj) {
        return super.equals(obj);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.evt.BaseStartElement, org.codehaus.stax2.ri.evt.BaseEventImpl, org.codehaus.stax2.evt.XMLEvent2
    public /* bridge */ /* synthetic */ void writeUsing(XMLStreamWriter2 xMLStreamWriter2) throws XMLStreamException {
        super.writeUsing(xMLStreamWriter2);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.evt.BaseStartElement, org.codehaus.stax2.ri.evt.BaseEventImpl
    public /* bridge */ /* synthetic */ void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        super.writeAsEncodedUnicode(writer);
    }

    @Override // com.ctc.wstx.evt.BaseStartElement, org.codehaus.stax2.ri.evt.BaseEventImpl
    public /* bridge */ /* synthetic */ boolean isStartElement() {
        return super.isStartElement();
    }

    @Override // com.ctc.wstx.evt.BaseStartElement, org.codehaus.stax2.ri.evt.BaseEventImpl
    public /* bridge */ /* synthetic */ int getEventType() {
        return super.getEventType();
    }

    @Override // com.ctc.wstx.evt.BaseStartElement, org.codehaus.stax2.ri.evt.BaseEventImpl
    public /* bridge */ /* synthetic */ StartElement asStartElement() {
        return super.asStartElement();
    }

    @Override // com.ctc.wstx.evt.BaseStartElement
    public /* bridge */ /* synthetic */ String getNamespaceURI(String str) {
        return super.getNamespaceURI(str);
    }

    @Override // com.ctc.wstx.evt.BaseStartElement
    public /* bridge */ /* synthetic */ NamespaceContext getNamespaceContext() {
        return super.getNamespaceContext();
    }

    @Override // com.ctc.wstx.evt.BaseStartElement
    public /* bridge */ /* synthetic */ Iterator getNamespaces() {
        return super.getNamespaces();
    }

    protected SimpleStartElement(Location loc, QName name, BaseNsContext nsCtxt, Map<QName, Attribute> attr) {
        super(loc, name, nsCtxt);
        this.mAttrs = attr;
    }

    public static SimpleStartElement construct(Location loc, QName name, Map<QName, Attribute> attrs, List<Namespace> ns, NamespaceContext nsCtxt) {
        BaseNsContext myCtxt = MergedNsContext.construct(nsCtxt, ns);
        return new SimpleStartElement(loc, name, myCtxt, attrs);
    }

    public static SimpleStartElement construct(Location loc, QName name, Iterator<Attribute> attrs, Iterator<Namespace> ns, NamespaceContext nsCtxt) {
        Map<QName, Attribute> attrMap;
        BaseNsContext myCtxt;
        if (attrs == null || !attrs.hasNext()) {
            attrMap = null;
        } else {
            attrMap = new LinkedHashMap<>();
            do {
                Attribute attr = attrs.next();
                attrMap.put(attr.getName(), attr);
            } while (attrs.hasNext());
        }
        if (ns != null && ns.hasNext()) {
            ArrayList<Namespace> l = new ArrayList<>();
            do {
                l.add(ns.next());
            } while (ns.hasNext());
            myCtxt = MergedNsContext.construct(nsCtxt, l);
        } else if (nsCtxt == null) {
            myCtxt = null;
        } else if (nsCtxt instanceof BaseNsContext) {
            myCtxt = (BaseNsContext) nsCtxt;
        } else {
            myCtxt = MergedNsContext.construct(nsCtxt, null);
        }
        return new SimpleStartElement(loc, name, myCtxt, attrMap);
    }

    @Override // com.ctc.wstx.evt.BaseStartElement
    public Attribute getAttributeByName(QName name) {
        if (this.mAttrs == null) {
            return null;
        }
        return this.mAttrs.get(name);
    }

    @Override // com.ctc.wstx.evt.BaseStartElement
    public Iterator<Attribute> getAttributes() {
        if (this.mAttrs == null) {
            return DataUtil.emptyIterator();
        }
        return this.mAttrs.values().iterator();
    }

    @Override // com.ctc.wstx.evt.BaseStartElement
    protected void outputNsAndAttr(Writer w) throws IOException {
        if (this.mNsCtxt != null) {
            this.mNsCtxt.outputNamespaceDeclarations(w);
        }
        if (this.mAttrs != null && this.mAttrs.size() > 0) {
            for (Attribute attr : this.mAttrs.values()) {
                if (attr.isSpecified()) {
                    w.write(32);
                    QName name = attr.getName();
                    String prefix = name.getPrefix();
                    if (prefix != null && prefix.length() > 0) {
                        w.write(prefix);
                        w.write(58);
                    }
                    w.write(name.getLocalPart());
                    w.write("=\"");
                    String val = attr.getValue();
                    if (val != null && val.length() > 0) {
                        TextEscaper.writeEscapedAttrValue(w, val);
                    }
                    w.write(34);
                }
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.evt.BaseStartElement
    protected void outputNsAndAttr(XMLStreamWriter w) throws XMLStreamException {
        if (this.mNsCtxt != null) {
            this.mNsCtxt.outputNamespaceDeclarations(w);
        }
        if (this.mAttrs != null && this.mAttrs.size() > 0) {
            for (Attribute attr : this.mAttrs.values()) {
                if (attr.isSpecified()) {
                    QName name = attr.getName();
                    String prefix = name.getPrefix();
                    String ln = name.getLocalPart();
                    String nsURI = name.getNamespaceURI();
                    w.writeAttribute(prefix, nsURI, ln, attr.getValue());
                }
            }
        }
    }
}
