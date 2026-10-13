package org.codehaus.stax2;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/XMLEventReader2.class */
public interface XMLEventReader2 extends XMLEventReader {
    boolean hasNextEvent() throws XMLStreamException;

    boolean isPropertySupported(String str);

    boolean setProperty(String str, Object obj);
}
