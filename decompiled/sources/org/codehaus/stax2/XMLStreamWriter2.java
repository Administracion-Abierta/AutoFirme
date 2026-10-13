package org.codehaus.stax2;

import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.typed.TypedXMLStreamWriter;
import org.codehaus.stax2.validation.Validatable;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/XMLStreamWriter2.class */
public interface XMLStreamWriter2 extends TypedXMLStreamWriter, Validatable {
    boolean isPropertySupported(String str);

    boolean setProperty(String str, Object obj);

    XMLStreamLocation2 getLocation();

    String getEncoding();

    void writeCData(char[] cArr, int i, int i2) throws XMLStreamException;

    void writeDTD(String str, String str2, String str3, String str4) throws XMLStreamException;

    void writeFullEndElement() throws XMLStreamException;

    void writeStartDocument(String str, String str2, boolean z) throws XMLStreamException;

    void writeSpace(String str) throws XMLStreamException;

    void writeSpace(char[] cArr, int i, int i2) throws XMLStreamException;

    void writeRaw(String str) throws XMLStreamException;

    void writeRaw(String str, int i, int i2) throws XMLStreamException;

    void writeRaw(char[] cArr, int i, int i2) throws XMLStreamException;

    void copyEventFromReader(XMLStreamReader2 xMLStreamReader2, boolean z) throws XMLStreamException;

    void closeCompletely() throws XMLStreamException;
}
