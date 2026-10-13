package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecCharacters;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/stax/XMLSecCharactersImpl.class */
public class XMLSecCharactersImpl extends XMLSecEventBaseImpl implements XMLSecCharacters {
    private String data;
    private char[] text;
    private final boolean isCData;
    private final boolean isIgnorableWhiteSpace;
    private final boolean isWhiteSpace;

    public XMLSecCharactersImpl(String data, boolean isCData, boolean isIgnorableWhiteSpace, boolean isWhiteSpace, XMLSecStartElement parentXmlSecStartElement) {
        this.data = data;
        this.isCData = isCData;
        this.isIgnorableWhiteSpace = isIgnorableWhiteSpace;
        this.isWhiteSpace = isWhiteSpace;
        setParentXMLSecStartElement(parentXmlSecStartElement);
    }

    public XMLSecCharactersImpl(char[] text, boolean isCData, boolean isIgnorableWhiteSpace, boolean isWhiteSpace, XMLSecStartElement parentXmlSecStartElement) {
        this.text = text;
        this.isCData = isCData;
        this.isIgnorableWhiteSpace = isIgnorableWhiteSpace;
        this.isWhiteSpace = isWhiteSpace;
        setParentXMLSecStartElement(parentXmlSecStartElement);
    }

    public String getData() {
        if (this.data == null) {
            this.data = new String(this.text);
        }
        return this.data;
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecCharacters
    public char[] getText() {
        if (this.text == null) {
            this.text = this.data.toCharArray();
        }
        return this.text;
    }

    public boolean isWhiteSpace() {
        return this.isWhiteSpace;
    }

    public boolean isCData() {
        return this.isCData;
    }

    public boolean isIgnorableWhiteSpace() {
        return this.isIgnorableWhiteSpace;
    }

    public int getEventType() {
        if (this.isCData) {
            return 12;
        }
        return 4;
    }

    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public boolean isCharacters() {
        return true;
    }

    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl, org.apache.xml.security.stax.ext.stax.XMLSecEvent
    /* JADX INFO: renamed from: asCharacters */
    public XMLSecCharacters mo1374asCharacters() {
        return this;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            if (this.isCData) {
                writer.write("<![CDATA[");
                writer.write(getText());
                writer.write("]]>");
            } else {
                writeEncoded(writer, getText());
            }
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    private void writeEncoded(Writer writer, char[] text) throws IOException {
        int length = text.length;
        int idx = 0;
        for (int i = 0; i < length; i++) {
            char c = text[i];
            switch (c) {
                case '&':
                    writer.write(text, idx, i - idx);
                    writer.write("&amp;");
                    idx = i + 1;
                    break;
                case '<':
                    writer.write(text, idx, i - idx);
                    writer.write("&lt;");
                    idx = i + 1;
                    break;
                case '>':
                    writer.write(text, idx, i - idx);
                    writer.write("&gt;");
                    idx = i + 1;
                    break;
            }
        }
        writer.write(text, idx, length - idx);
    }
}
