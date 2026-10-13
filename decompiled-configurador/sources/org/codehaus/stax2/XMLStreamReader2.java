package org.codehaus.stax2;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.typed.TypedXMLStreamReader;
import org.codehaus.stax2.validation.Validatable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/XMLStreamReader2.class */
public interface XMLStreamReader2 extends TypedXMLStreamReader, Validatable {

    @Deprecated
    public static final String FEATURE_DTD_OVERRIDE = "org.codehaus.stax2.propDtdOverride";

    boolean isPropertySupported(String str);

    boolean setProperty(String str, Object obj);

    @Deprecated
    Object getFeature(String str);

    @Deprecated
    void setFeature(String str, Object obj);

    void skipElement() throws XMLStreamException;

    DTDInfo getDTDInfo() throws XMLStreamException;

    AttributeInfo getAttributeInfo() throws XMLStreamException;

    LocationInfo getLocationInfo();

    int getText(Writer writer, boolean z) throws XMLStreamException, IOException;

    boolean isEmptyElement() throws XMLStreamException;

    int getDepth();

    NamespaceContext getNonTransientNamespaceContext();

    String getPrefixedName();

    void closeCompletely() throws XMLStreamException;
}
