package org.codehaus.stax2.ri.evt;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.util.XMLEventConsumer;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.ri.EmptyIterator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/evt/Stax2EventAllocatorImpl.class */
public class Stax2EventAllocatorImpl implements XMLEventAllocator, XMLStreamConstants {
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public XMLEvent allocate(XMLStreamReader r) throws XMLStreamException {
        Location loc = getLocation(r);
        switch (r.getEventType()) {
            case 1:
                return createStartElement(r, loc);
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
                return createEntityReference(r, loc);
            case 10:
            case 13:
            case 14:
            case 15:
            default:
                throw new XMLStreamException("Unrecognized event type " + r.getEventType() + ".");
            case 11:
                return createDTD(r, loc);
            case 12:
                return new CharactersEventImpl(loc, r.getText(), true);
        }
    }

    public void allocate(XMLStreamReader r, XMLEventConsumer consumer) throws XMLStreamException {
        consumer.add(allocate(r));
    }

    public XMLEventAllocator newInstance() {
        return new Stax2EventAllocatorImpl();
    }

    protected Location getLocation(XMLStreamReader r) {
        return r.getLocation();
    }

    protected EntityReference createEntityReference(XMLStreamReader r, Location loc) throws XMLStreamException {
        return new EntityReferenceEventImpl(loc, r.getLocalName());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected DTD createDTD(XMLStreamReader r, Location loc) throws XMLStreamException {
        if (r instanceof XMLStreamReader2) {
            XMLStreamReader2 sr2 = (XMLStreamReader2) r;
            DTDInfo dtd = sr2.getDTDInfo();
            return new DTDEventImpl(loc, dtd.getDTDRootName(), dtd.getDTDSystemId(), dtd.getDTDPublicId(), dtd.getDTDInternalSubset(), dtd.getProcessedDTD());
        }
        return new DTDEventImpl(loc, null, r.getText());
    }

    protected StartElement createStartElement(XMLStreamReader r, Location loc) throws XMLStreamException {
        List<Attribute> attrs;
        List<Namespace> ns;
        NamespaceContext nsCtxt = null;
        if (r instanceof XMLStreamReader2) {
            nsCtxt = ((XMLStreamReader2) r).getNonTransientNamespaceContext();
        }
        int attrCount = r.getAttributeCount();
        if (attrCount < 1) {
            attrs = null;
        } else {
            attrs = new ArrayList<>(attrCount);
            for (int i = 0; i < attrCount; i++) {
                QName aname = r.getAttributeName(i);
                attrs.add(new AttributeEventImpl(loc, aname, r.getAttributeValue(i), r.isAttributeSpecified(i)));
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
        return StartElementEventImpl.construct(loc, r.getName(), attrs == null ? EmptyIterator.getInstance() : attrs.iterator(), ns == null ? EmptyIterator.getInstance() : ns.iterator(), nsCtxt);
    }
}
