package org.codehaus.stax2.evt;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.codehaus.stax2.XMLStreamWriter2;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/evt/XMLEvent2.class */
public interface XMLEvent2 extends XMLEvent {
    void writeUsing(XMLStreamWriter2 xMLStreamWriter2) throws XMLStreamException;
}
