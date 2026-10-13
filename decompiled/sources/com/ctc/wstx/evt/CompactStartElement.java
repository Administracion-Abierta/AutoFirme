package com.ctc.wstx.evt;

import com.ctc.wstx.io.TextEscaper;
import com.ctc.wstx.sr.ElemAttrs;
import com.ctc.wstx.util.BaseNsContext;
import com.ctc.wstx.util.DataUtil;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.evt.AttributeEventImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/evt/CompactStartElement.class */
public class CompactStartElement extends BaseStartElement {
    private static final int OFFSET_NS_URI = 1;
    private static final int OFFSET_NS_PREFIX = 2;
    private static final int OFFSET_VALUE = 3;
    final ElemAttrs mAttrs;
    final String[] mRawAttrs;
    private ArrayList<Attribute> mAttrList;

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

    protected CompactStartElement(Location loc, QName name, BaseNsContext nsCtxt, ElemAttrs attrs) {
        super(loc, name, nsCtxt);
        this.mAttrList = null;
        this.mAttrs = attrs;
        this.mRawAttrs = attrs == null ? null : attrs.getRawAttrs();
    }

    @Override // com.ctc.wstx.evt.BaseStartElement
    public Attribute getAttributeByName(QName name) {
        int ix;
        if (this.mAttrs == null || (ix = this.mAttrs.findIndex(name)) < 0) {
            return null;
        }
        return constructAttr(this.mRawAttrs, ix, this.mAttrs.isDefault(ix));
    }

    @Override // com.ctc.wstx.evt.BaseStartElement
    public Iterator<Attribute> getAttributes() {
        if (this.mAttrList == null) {
            if (this.mAttrs == null) {
                return DataUtil.emptyIterator();
            }
            String[] rawAttrs = this.mRawAttrs;
            int rawLen = rawAttrs.length;
            int defOffset = this.mAttrs.getFirstDefaultOffset();
            if (rawLen == 4) {
                return DataUtil.singletonIterator(constructAttr(rawAttrs, 0, defOffset == 0));
            }
            ArrayList<Attribute> l = new ArrayList<>(rawLen >> 2);
            int i = 0;
            while (i < rawLen) {
                l.add(constructAttr(rawAttrs, i, i >= defOffset));
                i += 4;
            }
            this.mAttrList = l;
        }
        return this.mAttrList.iterator();
    }

    @Override // com.ctc.wstx.evt.BaseStartElement
    protected void outputNsAndAttr(Writer w) throws IOException {
        if (this.mNsCtxt != null) {
            this.mNsCtxt.outputNamespaceDeclarations(w);
        }
        String[] raw = this.mRawAttrs;
        if (raw != null) {
            int len = raw.length;
            for (int i = 0; i < len; i += 4) {
                w.write(32);
                String prefix = raw[i + 2];
                if (prefix != null && prefix.length() > 0) {
                    w.write(prefix);
                    w.write(58);
                }
                w.write(raw[i]);
                w.write("=\"");
                TextEscaper.writeEscapedAttrValue(w, raw[i + 3]);
                w.write(34);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.evt.BaseStartElement
    protected void outputNsAndAttr(XMLStreamWriter w) throws XMLStreamException {
        if (this.mNsCtxt != null) {
            this.mNsCtxt.outputNamespaceDeclarations(w);
        }
        String[] raw = this.mRawAttrs;
        if (raw != null) {
            int len = raw.length;
            for (int i = 0; i < len; i += 4) {
                String ln = raw[i];
                String prefix = raw[i + 2];
                String nsURI = raw[i + 1];
                w.writeAttribute(prefix, nsURI, ln, raw[i + 3]);
            }
        }
    }

    protected Attribute constructAttr(String[] raw, int rawIndex, boolean isDef) {
        return new AttributeEventImpl(getLocation(), raw[rawIndex], raw[rawIndex + 1], raw[rawIndex + 2], raw[rawIndex + 3], !isDef);
    }
}
