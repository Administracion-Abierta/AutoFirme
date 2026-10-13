package es.gob.afirma.signers.pades;

import com.aowagie.text.DocumentException;
import com.aowagie.text.pdf.PdfDate;
import com.aowagie.text.pdf.PdfReader;
import com.aowagie.text.pdf.PdfStamper;
import com.aowagie.text.xml.xmp.XmpArray;
import es.gob.afirma.core.misc.SecureXmlBuilder;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/XmpHelper.class */
final class XmpHelper {
    private static final String TAG_UUID = "$$UUID$$";
    private static final String TAG_DATE = "$$DATE$$";
    private static final String XML_TAG_MODIFYDATE_START = "<xmp:ModifyDate>";
    private static final String XML_TAG_MODIFYDATE_END = "</xmp:ModifyDate>";
    private static final String XML_TAG_CREATEDATE_START = "<xmp:CreateDate>";
    private static final String XML_TAG_CREATEDATE_END = "</xmp:CreateDate>";
    private static final String PROCESSING_INSTRUCTION_SUFFIX = "?>";
    private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
    private static final String NEW_HISTORY_LIST_ITEM = "<rdf:li rdf:parseType=\"Resource\">\n  <stEvt:action>signed</stEvt:action>\n  <stEvt:instanceID>uuid:$$UUID$$</stEvt:instanceID>\n  <stEvt:parameters>Firmado por el Cliente @firma</stEvt:parameters>\n  <stEvt:softwareAgent>Cliente @firma</stEvt:softwareAgent>\n  <stEvt:when>$$DATE$$</stEvt:when>\n</rdf:li>";

    XmpHelper() {
    }

    private static String getOriginalCreationDateAsW3C(PdfReader pdfReader) {
        String str = (String) pdfReader.getInfo().get("CreationDate");
        if (str != null) {
            return PdfDate.getW3CDate(str);
        }
        return null;
    }

    static byte[] addSignHistoryToXmp(byte[] bArr, Calendar calendar, Properties properties) throws ParserConfigurationException, SAXException, DOMException, IOException {
        Node nodeItem;
        byte[] pdfPassword = getPdfPassword(properties);
        PdfReader pdfReader = pdfPassword != null ? new PdfReader(bArr, pdfPassword) : new PdfReader(bArr);
        if (pdfReader.isEncrypted()) {
            pdfReader.close();
            return bArr;
        }
        byte[] metadata = pdfReader.getMetadata();
        String originalCreationDateAsW3C = getOriginalCreationDateAsW3C(pdfReader);
        if (!PdfUtil.isPdfAx(metadata) || new AOPDFSigner().isSign(bArr, properties)) {
            pdfReader.close();
            return bArr;
        }
        String str = new String(metadata, DEFAULT_ENCODING);
        String strSubstring = str.substring(0, str.indexOf(PROCESSING_INSTRUCTION_SUFFIX) + PROCESSING_INSTRUCTION_SUFFIX.length());
        String strSubstring2 = str.substring(str.lastIndexOf("<?"));
        DocumentBuilderFactory.newInstance().setNamespaceAware(true);
        DocumentBuilder secureDocumentBuilder = SecureXmlBuilder.getSecureDocumentBuilder();
        Document document = secureDocumentBuilder.parse(new ByteArrayInputStream(metadata));
        String strReplace = NEW_HISTORY_LIST_ITEM.replace(TAG_UUID, UUID.nameUUIDFromBytes(BigInteger.valueOf(calendar.getTimeInMillis()).toByteArray()).toString()).replace(TAG_DATE, new PdfDate(calendar).getW3CDate());
        NodeList elementsByTagName = document.getElementsByTagName("xmpMM:History");
        if (elementsByTagName.getLength() != 1) {
            NodeList elementsByTagName2 = document.getElementsByTagName("pdfaid:conformance");
            if (elementsByTagName2.getLength() != 1) {
                pdfReader.close();
                throw new IllegalStateException("El PDF no tiene una entrada RDF XMP valida");
            }
            nodeItem = elementsByTagName2.item(0).getParentNode();
            strReplace = "<xmpMM:History xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:xmpMM=\"http://ns.adobe.com/xap/1.0/mm/\" xmlns:stEvt=\"http://ns.adobe.com/xap/1.0/sType/ResourceEvent#\"><rdf:Seq>" + strReplace + "</rdf:Seq></xmpMM:History>";
        } else {
            NodeList elementsByTagName3 = ((Element) elementsByTagName.item(0)).getElementsByTagName(XmpArray.ORDERED);
            if (elementsByTagName3.getLength() != 1) {
                elementsByTagName3 = document.getElementsByTagName("xmpMM:History");
                strReplace = "<rdf:Seq xmlns:stEvt=\"http://ns.adobe.com/xap/1.0/sType/ResourceEvent#\">" + strReplace + "</rdf:Seq>";
            }
            nodeItem = elementsByTagName3.item(0);
        }
        try {
            nodeItem.appendChild(document.importNode(secureDocumentBuilder.parse(new InputSource(new StringReader(strReplace))).getDocumentElement(), true));
            new ConcurrentHashMap(1).put("encoding", DEFAULT_ENCODING.name());
            String str2 = new String(writeXml(document.getDocumentElement()), DEFAULT_ENCODING);
            String strConcat = str2.replace(str2.substring(0, str2.indexOf(PROCESSING_INSTRUCTION_SUFFIX) + PROCESSING_INSTRUCTION_SUFFIX.length()), strSubstring).concat(strSubstring2);
            int iIndexOf = strConcat.indexOf(XML_TAG_MODIFYDATE_START);
            if (iIndexOf != -1) {
                strConcat = strConcat.replace(strConcat.substring(iIndexOf + XML_TAG_MODIFYDATE_START.length(), strConcat.indexOf(XML_TAG_MODIFYDATE_END)), new PdfDate(calendar).getW3CDate());
            }
            if (originalCreationDateAsW3C != null) {
                strConcat = strConcat.replace(strConcat.substring(strConcat.indexOf(XML_TAG_CREATEDATE_START) + XML_TAG_CREATEDATE_START.length(), strConcat.indexOf(XML_TAG_CREATEDATE_END)), originalCreationDateAsW3C);
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                PdfStamper pdfStamper = new PdfStamper(pdfReader, byteArrayOutputStream, calendar);
                try {
                    pdfStamper.setXmpMetadata(strConcat.getBytes(DEFAULT_ENCODING));
                    pdfStamper.close(calendar);
                    pdfReader.close();
                    Logger.getLogger("es.gob.afirma").info("Se ha registrado la firma en el historico XMP del PDF");
                    return byteArrayOutputStream.toByteArray();
                } catch (DocumentException e) {
                    pdfReader.close();
                    throw new IOException(e);
                }
            } catch (DocumentException e2) {
                pdfReader.close();
                throw new IOException(e2);
            }
        } catch (IOException | DOMException | SAXException e3) {
            pdfReader.close();
            throw e3;
        }
    }

    private static byte[] getPdfPassword(Properties properties) {
        String property = properties.getProperty(PdfExtraParams.OWNER_PASSWORD_STRING);
        String property2 = properties.getProperty(PdfExtraParams.USER_PASSWORD_STRING);
        byte[] bytes = null;
        if (property != null) {
            bytes = property.getBytes(DEFAULT_ENCODING);
        } else if (property2 != null) {
            bytes = property2.getBytes(DEFAULT_ENCODING);
        }
        return bytes;
    }

    private static byte[] writeXml(Node node) {
        DOMImplementationLS dOMImplementationLS = (DOMImplementationLS) node.getOwnerDocument().getImplementation();
        LSSerializer lSSerializerCreateLSSerializer = dOMImplementationLS.createLSSerializer();
        DOMConfiguration domConfig = lSSerializerCreateLSSerializer.getDomConfig();
        if (domConfig.canSetParameter("namespaces", Boolean.FALSE)) {
            domConfig.setParameter("namespaces", Boolean.FALSE);
        }
        if (domConfig.canSetParameter("canonical-form", Boolean.TRUE)) {
            lSSerializerCreateLSSerializer.getDomConfig().setParameter("canonical-form", Boolean.TRUE);
        }
        LSOutput lSOutputCreateLSOutput = dOMImplementationLS.createLSOutput();
        lSOutputCreateLSOutput.setEncoding(DEFAULT_ENCODING.name());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        lSOutputCreateLSOutput.setByteStream(byteArrayOutputStream);
        lSSerializerCreateLSSerializer.write(node, lSOutputCreateLSOutput);
        return byteArrayOutputStream.toByteArray();
    }
}
