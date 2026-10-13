package org.codehaus.stax2.ri;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/Stax2WriterImpl.class */
public abstract class Stax2WriterImpl implements XMLStreamWriter2, XMLStreamConstants {
    @Override // org.codehaus.stax2.XMLStreamWriter2
    public abstract XMLStreamLocation2 getLocation();

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public abstract String getEncoding();

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public abstract void writeStartDocument(String str, String str2, boolean z) throws XMLStreamException;

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public abstract void writeRaw(String str, int i, int i2) throws XMLStreamException;

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public abstract void writeRaw(char[] cArr, int i, int i2) throws XMLStreamException;

    protected Stax2WriterImpl() {
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public boolean isPropertySupported(String name) {
        return false;
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public boolean setProperty(String name, Object value) {
        throw new IllegalArgumentException("No settable property '" + name + "'");
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeCData(char[] text, int start, int len) throws XMLStreamException {
        writeCData(new String(text, start, len));
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeDTD(String rootName, String systemId, String publicId, String internalSubset) throws XMLStreamException {
        StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE");
        sb.append(rootName);
        if (systemId != null) {
            if (publicId != null) {
                sb.append(" PUBLIC \"");
                sb.append(publicId);
                sb.append("\" \"");
            } else {
                sb.append(" SYSTEM \"");
            }
            sb.append(systemId);
            sb.append('\"');
        }
        if (internalSubset != null && internalSubset.length() > 0) {
            sb.append(" [");
            sb.append(internalSubset);
            sb.append(']');
        }
        sb.append('>');
        writeDTD(sb.toString());
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeFullEndElement() throws XMLStreamException {
        writeCharacters("");
        writeEndElement();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeSpace(String text) throws XMLStreamException {
        writeRaw(text);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeSpace(char[] text, int offset, int length) throws XMLStreamException {
        writeRaw(text, offset, length);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeRaw(String text) throws XMLStreamException {
        writeRaw(text, 0, text.length());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void copyEventFromReader(XMLStreamReader2 sr, boolean preserveEventData) throws XMLStreamException {
        switch (sr.getEventType()) {
            case 1:
                copyStartElement(sr);
                return;
            case 2:
                writeEndElement();
                return;
            case 3:
                writeProcessingInstruction(sr.getPITarget(), sr.getPIData());
                return;
            case 4:
                writeCharacters(sr.getTextCharacters(), sr.getTextStart(), sr.getTextLength());
                return;
            case 5:
                writeComment(sr.getText());
                return;
            case 6:
                writeSpace(sr.getTextCharacters(), sr.getTextStart(), sr.getTextLength());
                return;
            case 7:
                String version = sr.getVersion();
                if (version != null && version.length() != 0) {
                    if (sr.standaloneSet()) {
                        writeStartDocument(sr.getVersion(), sr.getCharacterEncodingScheme(), sr.isStandalone());
                        return;
                    } else {
                        writeStartDocument(sr.getCharacterEncodingScheme(), sr.getVersion());
                        return;
                    }
                }
                return;
            case 8:
                writeEndDocument();
                return;
            case 9:
                writeEntityRef(sr.getLocalName());
                return;
            case 10:
            case 13:
            case 14:
            case 15:
            default:
                throw new XMLStreamException("Unrecognized event type (" + sr.getEventType() + "); not sure how to copy");
            case 11:
                DTDInfo info = sr.getDTDInfo();
                if (info == null) {
                    throw new XMLStreamException("Current state DOCTYPE, but not DTDInfo Object returned -- reader doesn't support DTDs?");
                }
                writeDTD(info.getDTDRootName(), info.getDTDSystemId(), info.getDTDPublicId(), info.getDTDInternalSubset());
                return;
            case 12:
                writeCData(sr.getTextCharacters(), sr.getTextStart(), sr.getTextLength());
                return;
        }
    }

    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return null;
    }

    public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
        return null;
    }

    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler h) {
        return null;
    }

    protected void copyStartElement(XMLStreamReader sr) throws XMLStreamException {
        int nsCount = sr.getNamespaceCount();
        if (nsCount > 0) {
            for (int i = 0; i < nsCount; i++) {
                String prefix = sr.getNamespacePrefix(i);
                String uri = sr.getNamespaceURI(i);
                if (prefix == null || prefix.length() == 0) {
                    setDefaultNamespace(uri);
                } else {
                    setPrefix(prefix, uri);
                }
            }
        }
        writeStartElement(sr.getPrefix(), sr.getLocalName(), sr.getNamespaceURI());
        if (nsCount > 0) {
            for (int i2 = 0; i2 < nsCount; i2++) {
                String prefix2 = sr.getNamespacePrefix(i2);
                String uri2 = sr.getNamespaceURI(i2);
                if (prefix2 == null || prefix2.length() == 0) {
                    writeDefaultNamespace(uri2);
                } else {
                    writeNamespace(prefix2, uri2);
                }
            }
        }
        int attrCount = sr.getAttributeCount();
        if (attrCount > 0) {
            for (int i3 = 0; i3 < attrCount; i3++) {
                writeAttribute(sr.getAttributePrefix(i3), sr.getAttributeNamespace(i3), sr.getAttributeLocalName(i3), sr.getAttributeValue(i3));
            }
        }
    }
}
