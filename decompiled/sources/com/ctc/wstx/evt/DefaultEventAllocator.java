package com.ctc.wstx.evt;

import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.dtd.DTDSubset;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.exc.WstxException;
import com.ctc.wstx.sr.ElemAttrs;
import com.ctc.wstx.sr.ElemCallback;
import com.ctc.wstx.sr.StreamReaderImpl;
import com.ctc.wstx.util.BaseNsContext;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.util.XMLEventConsumer;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.ri.evt.AttributeEventImpl;
import org.codehaus.stax2.ri.evt.CharactersEventImpl;
import org.codehaus.stax2.ri.evt.CommentEventImpl;
import org.codehaus.stax2.ri.evt.EndDocumentEventImpl;
import org.codehaus.stax2.ri.evt.EndElementEventImpl;
import org.codehaus.stax2.ri.evt.NamespaceEventImpl;
import org.codehaus.stax2.ri.evt.ProcInstrEventImpl;
import org.codehaus.stax2.ri.evt.StartDocumentEventImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/evt/DefaultEventAllocator.class */
public class DefaultEventAllocator extends ElemCallback implements XMLEventAllocator, XMLStreamConstants {
    static final DefaultEventAllocator sStdInstance = new DefaultEventAllocator(true);
    protected final boolean mAccurateLocation;
    protected Location mLastLocation = null;

    protected DefaultEventAllocator(boolean accurateLocation) {
        this.mAccurateLocation = accurateLocation;
    }

    public static DefaultEventAllocator getDefaultInstance() {
        return sStdInstance;
    }

    public static DefaultEventAllocator getFastInstance() {
        return new DefaultEventAllocator(false);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public XMLEvent allocate(XMLStreamReader r) throws XMLStreamException {
        Location loc;
        Map<QName, Attribute> attrs;
        List<Namespace> ns;
        if (this.mAccurateLocation) {
            loc = r.getLocation();
        } else {
            loc = this.mLastLocation;
            if (loc == null) {
                Location location = r.getLocation();
                this.mLastLocation = location;
                loc = location;
            }
        }
        switch (r.getEventType()) {
            case 1:
                if (r instanceof StreamReaderImpl) {
                    StreamReaderImpl sr = (StreamReaderImpl) r;
                    BaseStartElement be = (BaseStartElement) sr.withStartElement(this, loc);
                    if (be == null) {
                        throw new WstxException("Trying to create START_ELEMENT when current event is " + ErrorConsts.tokenTypeDesc(sr.getEventType()), loc);
                    }
                    return be;
                }
                NamespaceContext nsCtxt = null;
                if (r instanceof XMLStreamReader2) {
                    nsCtxt = ((XMLStreamReader2) r).getNonTransientNamespaceContext();
                }
                int attrCount = r.getAttributeCount();
                if (attrCount < 1) {
                    attrs = null;
                } else {
                    attrs = new LinkedHashMap<>();
                    for (int i = 0; i < attrCount; i++) {
                        QName aname = r.getAttributeName(i);
                        attrs.put(aname, new AttributeEventImpl(loc, aname, r.getAttributeValue(i), r.isAttributeSpecified(i)));
                    }
                }
                int nsCount = r.getNamespaceCount();
                if (nsCount < 1) {
                    ns = null;
                } else {
                    ns = new ArrayList<>(nsCount);
                    for (int i2 = 0; i2 < nsCount; i2++) {
                        ns.add(NamespaceEventImpl.constructNamespace(loc, r.getNamespacePrefix(i2), r.getNamespaceURI(i2)));
                    }
                }
                return SimpleStartElement.construct(loc, r.getName(), attrs, ns, nsCtxt);
            case 2:
                return new EndElementEventImpl(loc, r);
            case 3:
                return new ProcInstrEventImpl(loc, r.getPITarget(), r.getPIData());
            case 4:
                return new CharactersEventImpl(loc, r.getText(), false);
            case 5:
                return new CommentEventImpl(loc, r.getText());
            case 6:
                CharactersEventImpl ch = new CharactersEventImpl(loc, r.getText(), false);
                ch.setWhitespaceStatus(true);
                return ch;
            case 7:
                return new StartDocumentEventImpl(loc, r);
            case 8:
                return new EndDocumentEventImpl(loc);
            case 9:
                if (r instanceof StreamReaderImpl) {
                    EntityDecl ed = ((StreamReaderImpl) r).getCurrentEntityDecl();
                    if (ed == null) {
                        return new WEntityReference(loc, r.getLocalName());
                    }
                    return new WEntityReference(loc, ed);
                }
                return new WEntityReference(loc, r.getLocalName());
            case 10:
            case 13:
            case 14:
            case 15:
                throw new WstxException("Internal error: should not get " + ErrorConsts.tokenTypeDesc(r.getEventType()));
            case 11:
                if (r instanceof XMLStreamReader2) {
                    XMLStreamReader2 sr2 = (XMLStreamReader2) r;
                    DTDInfo dtd = sr2.getDTDInfo();
                    return new WDTD(loc, dtd.getDTDRootName(), dtd.getDTDSystemId(), dtd.getDTDPublicId(), dtd.getDTDInternalSubset(), (DTDSubset) dtd.getProcessedDTD());
                }
                return new WDTD(loc, null, r.getText());
            case 12:
                return new CharactersEventImpl(loc, r.getText(), true);
            default:
                throw new IllegalStateException("Unrecognized event type " + r.getEventType() + ".");
        }
    }

    public void allocate(XMLStreamReader r, XMLEventConsumer consumer) throws XMLStreamException {
        consumer.add(allocate(r));
    }

    public XMLEventAllocator newInstance() {
        return new DefaultEventAllocator(this.mAccurateLocation);
    }

    @Override // com.ctc.wstx.sr.ElemCallback
    public Object withStartElement(Location loc, QName name, BaseNsContext nsCtxt, ElemAttrs attrs, boolean wasEmpty) {
        return new CompactStartElement(loc, name, nsCtxt, attrs);
    }
}
