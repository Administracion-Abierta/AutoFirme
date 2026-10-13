package org.codehaus.stax2;

import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/XMLOutputFactory2.class */
public abstract class XMLOutputFactory2 extends XMLOutputFactory implements XMLStreamProperties {
    public static final String P_AUTOMATIC_EMPTY_ELEMENTS = "org.codehaus.stax2.automaticEmptyElements";
    public static final String P_AUTO_CLOSE_OUTPUT = "org.codehaus.stax2.autoCloseOutput";
    public static final String P_AUTOMATIC_NS_PREFIX = "org.codehaus.stax2.automaticNsPrefix";
    public static final String P_TEXT_ESCAPER = "org.codehaus.stax2.textEscaper";
    public static final String P_ATTR_VALUE_ESCAPER = "org.codehaus.stax2.attrValueEscaper";

    public abstract XMLEventWriter createXMLEventWriter(Writer writer, String str) throws XMLStreamException;

    public abstract XMLEventWriter createXMLEventWriter(XMLStreamWriter xMLStreamWriter) throws XMLStreamException;

    public abstract XMLStreamWriter2 createXMLStreamWriter(Writer writer, String str) throws XMLStreamException;

    public abstract void configureForXmlConformance();

    public abstract void configureForRobustness();

    public abstract void configureForSpeed();

    protected XMLOutputFactory2() {
    }
}
