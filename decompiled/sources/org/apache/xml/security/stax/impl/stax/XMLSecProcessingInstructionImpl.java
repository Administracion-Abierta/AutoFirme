package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecProcessingInstruction;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/stax/XMLSecProcessingInstructionImpl.class */
public class XMLSecProcessingInstructionImpl extends XMLSecEventBaseImpl implements XMLSecProcessingInstruction {
    private final String data;
    private final String target;

    public XMLSecProcessingInstructionImpl(String target, String data, XMLSecStartElement parentXmlSecStartElement) {
        this.target = target;
        this.data = data;
        setParentXMLSecStartElement(parentXmlSecStartElement);
    }

    public String getTarget() {
        return this.target;
    }

    public String getData() {
        return this.data;
    }

    public int getEventType() {
        return 3;
    }

    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public boolean isProcessingInstruction() {
        return true;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<?");
            writer.write(getTarget());
            String data = getData();
            if (data != null && !data.isEmpty()) {
                writer.write(32);
                writer.write(data);
            }
            writer.write("?>");
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}
