package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecStartDocument;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/stax/XMLSecStartDocumentImpl.class */
public class XMLSecStartDocumentImpl extends XMLSecEventBaseImpl implements XMLSecStartDocument {
    private final String systemId;
    private final String characterEncodingScheme;
    private final Boolean isStandAlone;
    private final String version;

    public XMLSecStartDocumentImpl(String systemId, String characterEncodingScheme, Boolean standAlone, String version) {
        this.systemId = systemId;
        this.characterEncodingScheme = characterEncodingScheme;
        this.isStandAlone = standAlone;
        this.version = version != null ? version : "1.0";
    }

    public int getEventType() {
        return 7;
    }

    public String getSystemId() {
        return this.systemId != null ? this.systemId : "";
    }

    public String getCharacterEncodingScheme() {
        return this.characterEncodingScheme != null ? this.characterEncodingScheme : StandardCharsets.UTF_8.name();
    }

    public boolean encodingSet() {
        return this.characterEncodingScheme != null;
    }

    public boolean isStandalone() {
        return this.isStandAlone != null && this.isStandAlone.booleanValue();
    }

    public boolean standaloneSet() {
        return this.isStandAlone != null;
    }

    public String getVersion() {
        return this.version;
    }

    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public boolean isStartDocument() {
        return true;
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecStartDocument
    public XMLSecStartDocument asStartDocument() {
        return this;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<?xml version=\"");
            if (getVersion() == null || getVersion().isEmpty()) {
                writer.write("1.0");
            } else {
                writer.write(getVersion());
            }
            writer.write(34);
            if (encodingSet()) {
                writer.write(" encoding=\"");
                writer.write(getCharacterEncodingScheme());
                writer.write(34);
            }
            if (standaloneSet()) {
                if (isStandalone()) {
                    writer.write(" standalone=\"yes\"");
                } else {
                    writer.write(" standalone=\"no\"");
                }
            }
            writer.write(" ?>");
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}
