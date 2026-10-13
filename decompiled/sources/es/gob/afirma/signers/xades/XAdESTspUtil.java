package es.gob.afirma.signers.xades;

import com.aowagie.text.xml.XMLConstants;
import com.ctc.wstx.cfg.XmlConsts;
import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.signers.tsp.pkcs7.CMSTimestamper;
import es.gob.afirma.signers.tsp.pkcs7.TsaParams;
import es.gob.afirma.signers.xml.Utils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import nu.xom.canonical.Canonicalizer;
import nu.xom.converters.DOMConverter;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xades/XAdESTspUtil.class */
public final class XAdESTspUtil {
    private static final String DEFAULT_CANONICAL_ALGO = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static DocumentBuilderFactory D_FACTORY = DocumentBuilderFactory.newInstance();

    static {
        try {
            D_FACTORY.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE.booleanValue());
        } catch (ParserConfigurationException e) {
            LOGGER.warning("No se ha podido habilitar el proceso seguro en la factoria DOM: " + e);
        }
        D_FACTORY.setValidating(false);
        D_FACTORY.setNamespaceAware(true);
    }

    private XAdESTspUtil() {
    }

    public static byte[] timestampXAdES(byte[] bArr, Properties properties) throws AOException {
        if (properties == null) {
            return bArr;
        }
        try {
            TsaParams tsaParams = new TsaParams(properties);
            try {
                Document document = D_FACTORY.newDocumentBuilder().parse(new ByteArrayInputStream(bArr));
                NodeList elementsByTagNameNS = document.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_QUALIFYING_PROPERTIES);
                if (elementsByTagNameNS.getLength() < 1) {
                    throw new AOException("La firma no tiene 'QualifyingProperties', no se puede aplicar el sello de tiempo");
                }
                if (elementsByTagNameNS.getLength() > 1) {
                    LOGGER.warning("El XML tiene mas de una firma, solo se sellara la primera");
                }
                Node nodeItem = elementsByTagNameNS.item(0);
                try {
                    try {
                        nodeItem.appendChild(document.importNode(D_FACTORY.newDocumentBuilder().parse(new ByteArrayInputStream(("<xades:UnsignedProperties xmlns:xades=\"http://uri.etsi.org/01903/v1.3.2#\">\n <xades:UnsignedSignatureProperties>\n  <xades:SignatureTimeStamp>\n   <ds:CanonicalizationMethod xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"/>\n   <xades:EncapsulatedTimeStamp>\n" + getBase64XAdESTimestampToken(getSigningTime(nodeItem), document, tsaParams) + "</xades:EncapsulatedTimeStamp>\n  </xades:SignatureTimeStamp>\n </xades:UnsignedSignatureProperties>\n</xades:UnsignedProperties>\n").getBytes(StandardCharsets.UTF_8))).getDocumentElement(), true));
                        return Utils.writeXML(document.getDocumentElement(), getOriginalXMLProperties(document, null), null, null);
                    } catch (IOException | ParserConfigurationException | SAXException e) {
                        throw new AOException("Error creando el nodo XML de sello de tiempo: " + e, e);
                    }
                } catch (IOException | NoSuchAlgorithmException e2) {
                    throw new AOException("Error obteniendo el sello de la TSA: " + e2, e2);
                }
            } catch (IOException | ParserConfigurationException | SAXException e3) {
                throw new AOException("No se puede analizar la firma para agregar el sello de tiempo: " + e3, e3);
            }
        } catch (Exception e4) {
            return bArr;
        }
    }

    private static String getBase64XAdESTimestampToken(Calendar calendar, Document document, TsaParams tsaParams) throws NoSuchAlgorithmException, IOException, AOException {
        return Base64.encode(new CMSTimestamper(tsaParams).getTimeStampToken(getSignatureNodeDigest(document, tsaParams.getTsaHashAlgorithm()), tsaParams.getTsaHashAlgorithm(), calendar));
    }

    private static Calendar getSigningTime(Node node) {
        if (!(node instanceof Element)) {
            return new GregorianCalendar();
        }
        NodeList elementsByTagNameNS = ((Element) node).getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", XAdESConstants.TAG_SIGNING_TIME);
        if (elementsByTagNameNS.getLength() > 1) {
            return new GregorianCalendar();
        }
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(elementsByTagNameNS.item(0).getTextContent().trim()).toGregorianCalendar();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo instanciar la factoria para le parseo de una fecha del XML", e);
        }
    }

    private static byte[] getSignatureNodeDigest(Document document, String str) throws NoSuchAlgorithmException, IOException {
        NodeList elementsByTagNameNS = document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "SignatureValue");
        if (elementsByTagNameNS.getLength() < 1) {
            throw new IOException("El XML no tiene nodo de firma");
        }
        if (elementsByTagNameNS.getLength() > 1) {
            LOGGER.warning("El XML tiene mas de un nodo de firma, se tratara solo el primero");
        }
        return MessageDigest.getInstance(str).digest(canonicalizeXml((Element) elementsByTagNameNS.item(0), "http://www.w3.org/TR/2001/REC-xml-c14n-20010315"));
    }

    private static byte[] canonicalizeXml(Element element, String str) throws IOException {
        nu.xom.Element elementConvert = DOMConverter.convert(element);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new Canonicalizer(byteArrayOutputStream, str).write(elementConvert);
        return byteArrayOutputStream.toByteArray();
    }

    private static Map<String, String> getOriginalXMLProperties(Document document, String str) {
        String systemId;
        Hashtable hashtable = new Hashtable();
        if (document != null) {
            if (str != null) {
                hashtable.put("encoding", str);
            } else if (document.getXmlEncoding() != null) {
                hashtable.put("encoding", document.getXmlEncoding());
            }
            String xmlVersion = document.getXmlVersion();
            if (xmlVersion != null) {
                hashtable.put(XmlConsts.XML_DECL_KW_VERSION, xmlVersion);
            }
            DocumentType doctype = document.getDoctype();
            if (doctype != null && (systemId = doctype.getSystemId()) != null) {
                hashtable.put("doctype-system", systemId);
            }
        }
        return hashtable;
    }
}
