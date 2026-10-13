package org.codehaus.stax2;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/LocationInfo.class */
public interface LocationInfo {
    long getStartingByteOffset();

    long getStartingCharOffset();

    long getEndingByteOffset() throws XMLStreamException;

    long getEndingCharOffset() throws XMLStreamException;

    Location getLocation();

    XMLStreamLocation2 getStartLocation();

    XMLStreamLocation2 getCurrentLocation();

    XMLStreamLocation2 getEndLocation() throws XMLStreamException;
}
