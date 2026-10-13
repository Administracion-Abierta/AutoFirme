package org.apache.xml.security.stax.ext;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/Transformer.class */
public interface Transformer {
    void setOutputStream(OutputStream outputStream) throws XMLSecurityException;

    void setTransformer(Transformer transformer) throws XMLSecurityException;

    void setProperties(Map<String, Object> map) throws XMLSecurityException;

    XMLSecurityConstants.TransformMethod getPreferredTransformMethod(XMLSecurityConstants.TransformMethod transformMethod);

    void transform(XMLSecEvent xMLSecEvent) throws XMLStreamException;

    void transform(InputStream inputStream) throws XMLStreamException;

    void doFinal() throws XMLStreamException;
}
