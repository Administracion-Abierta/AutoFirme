package org.codehaus.stax2;

import javax.xml.stream.Location;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/XMLStreamLocation2.class */
public interface XMLStreamLocation2 extends Location {
    public static final XMLStreamLocation2 NOT_AVAILABLE = new XMLStreamLocation2() { // from class: org.codehaus.stax2.XMLStreamLocation2.1
        @Override // org.codehaus.stax2.XMLStreamLocation2
        public XMLStreamLocation2 getContext() {
            return null;
        }

        public int getCharacterOffset() {
            return -1;
        }

        public int getColumnNumber() {
            return -1;
        }

        public int getLineNumber() {
            return -1;
        }

        public String getPublicId() {
            return null;
        }

        public String getSystemId() {
            return null;
        }
    };

    XMLStreamLocation2 getContext();
}
