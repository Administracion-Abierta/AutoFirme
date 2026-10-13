package es.gob.afirma.standalone.configurator.common.xmlwise;

import es.gob.afirma.core.misc.SecureXmlBuilder;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import org.w3c.dom.Document;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/configurator/common/xmlwise/Xmlwise.class */
public class Xmlwise {
    private Xmlwise() {
    }

    public static Document loadDocument(File file) throws IOException, XmlParseException {
        return loadDocument(file, false);
    }

    public static Document loadDocument(File file, boolean z) throws IOException, XmlParseException {
        try {
            return SecureXmlBuilder.getSecureDocumentBuilder().parse(file);
        } catch (IOException e) {
            throw e;
        } catch (Exception e2) {
            throw new XmlParseException(e2);
        }
    }

    public static Document createDocument(String str, boolean z) throws XmlParseException {
        try {
            return SecureXmlBuilder.getSecureDocumentBuilder().parse(new ByteArrayInputStream(str.getBytes()));
        } catch (Exception e) {
            throw new XmlParseException(e);
        }
    }

    public static Document createDocument(byte[] bArr, boolean z) throws XmlParseException {
        try {
            return SecureXmlBuilder.getSecureDocumentBuilder().parse(new ByteArrayInputStream(bArr));
        } catch (Exception e) {
            throw new XmlParseException(e);
        }
    }

    public static Document createDocument(String str) throws XmlParseException {
        return createDocument(str, false);
    }

    public static Document createDocument(byte[] bArr) throws XmlParseException {
        return createDocument(bArr, false);
    }

    public static String escapeXML(String str) {
        int length = str.length();
        if (length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            switch (cCharAt) {
                case '\"':
                    sb.append("&quot;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                default:
                    sb.append(cCharAt);
                    break;
            }
        }
        return sb.toString();
    }

    public static XmlElement loadXml(File file) throws IOException, XmlParseException {
        return new XmlElement(loadDocument(file).getDocumentElement());
    }

    public static XmlElement loadXml(String str) throws IOException, XmlParseException {
        return loadXml(new File(str));
    }

    public static XmlElement createXml(String str) throws XmlParseException {
        return new XmlElement(createDocument(str).getDocumentElement());
    }

    public static XmlElement createXml(byte[] bArr) throws XmlParseException {
        return new XmlElement(createDocument(bArr).getDocumentElement());
    }
}
