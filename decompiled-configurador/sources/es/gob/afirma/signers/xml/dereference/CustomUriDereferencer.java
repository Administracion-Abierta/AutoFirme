package es.gob.afirma.signers.xml.dereference;

import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.SecureXmlBuilder;
import es.gob.afirma.core.misc.http.SSLErrorProcessor;
import es.gob.afirma.core.misc.http.UrlHttpManagerFactory;
import es.gob.afirma.core.misc.http.UrlHttpMethod;
import es.gob.afirma.signers.xml.Utils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Logger;
import javax.xml.crypto.Data;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMURIReference;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/xml/dereference/CustomUriDereferencer.class */
public class CustomUriDereferencer implements URIDereferencer {
    private static final String ID = "Id";
    private static final String DEFAULT_SUN_XML_SIGNATURE_INPUT_CLASSNAME = "com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput";
    private static final String DEFAULT_APACHE_XML_SIGNATURE_INPUT_CLASSNAME = "org.apache.xml.security.signature.XMLSignatureInput";
    private static final String DEFAULT_SUN_OCTET_STREAM_DATA = "org.jcp.xml.dsig.internal.dom.ApacheOctetStreamData";
    private static final String DEFAULT_APACHE_OCTET_STREAM_DATA = "org.apache.jcp.xml.dsig.internal.dom.ApacheOctetStreamData";
    private static final String DEFAULT_SUN_NODESET_DATA = "org.jcp.xml.dsig.internal.dom.ApacheNodeSetData";
    private static final String DEFAULT_APACHE_NODESET_DATA = "org.apache.jcp.xml.dsig.internal.dom.ApacheNodeSetData";
    private final URIDereferencer defaultUriDereferencer = Utils.getDOMFactory().getURIDereferencer();
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private static Class<?> getNodesetDataClass() throws ClassNotFoundException {
        try {
            return Class.forName(DEFAULT_APACHE_NODESET_DATA);
        } catch (Error | Exception e) {
            return Class.forName(DEFAULT_SUN_NODESET_DATA);
        }
    }

    private static Class<?> getOctetStreamDataClass() throws ClassNotFoundException {
        try {
            return Class.forName(DEFAULT_APACHE_OCTET_STREAM_DATA);
        } catch (Error | Exception e) {
            return Class.forName(DEFAULT_SUN_OCTET_STREAM_DATA);
        }
    }

    private static Class<?> getXmlSignatureInputClass() throws ClassNotFoundException {
        try {
            return Class.forName(DEFAULT_APACHE_XML_SIGNATURE_INPUT_CLASSNAME);
        } catch (Error | Exception e) {
            return Class.forName(DEFAULT_SUN_XML_SIGNATURE_INPUT_CLASSNAME);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.URIReferenceException */
    public Data dereference(URIReference uRIReference, XMLCryptoContext xMLCryptoContext) throws URIReferenceException {
        try {
            return this.defaultUriDereferencer.dereference(uRIReference, xMLCryptoContext);
        } catch (Exception e) {
            String uri = uRIReference.getURI();
            if (uri.startsWith("http://") || uri.startsWith("https://")) {
                LOGGER.info("Se ha pedido dereferenciar una URI externa: " + uri);
                SSLErrorProcessor sSLErrorProcessor = new SSLErrorProcessor();
                try {
                    try {
                        return getStreamData(SecureXmlBuilder.getSecureDocumentBuilder().parse(new ByteArrayInputStream(UrlHttpManagerFactory.getInstalledManager().readUrl(uri, UrlHttpMethod.GET, sSLErrorProcessor))));
                    } catch (IOException e2) {
                        throw new URIReferenceException("No se ha podido crear un XML a partir del contenido externo dereferenciado (" + e + "): " + e2, e2);
                    } catch (ParserConfigurationException e3) {
                        throw new URIReferenceException("No se ha podido crear un XML a partir del contenido externo dereferenciado por error del analizador (" + e + "): " + e3, e3);
                    } catch (SAXException e4) {
                        throw new URIReferenceException("No se ha podido crear un XML a partir del contenido externo dereferenciado por error SAX (" + e + "): " + e4, e4);
                    }
                } catch (Exception e5) {
                    if (sSLErrorProcessor.isCancelled()) {
                        LOGGER.info("El usuario no permite la importacion del certificado SSL de confianza para referenciar los datos desde el dominio " + LoggerUtil.getTrimStr(uri));
                    } else {
                        LOGGER.severe("No se han podido derreferenciar los datos desde " + LoggerUtil.getTrimStr(uri) + ": " + e5);
                    }
                    throw new URIReferenceException("No se ha podido descargar el contenido externo (" + LoggerUtil.getTrimStr(uri) + "): " + e5, e5);
                }
            }
            Attr attr = (Attr) ((DOMURIReference) uRIReference).getHere();
            Document ownerDocument = attr.getOwnerDocument();
            String nodeValue = attr.getNodeValue();
            if ("".equals(nodeValue)) {
                try {
                    return getStreamData(ownerDocument);
                } catch (IOException e6) {
                    throw new URIReferenceException("Error obteniendo los octetos del XML: " + e6, e6);
                }
            }
            Node nodeByInternalUriReference = getNodeByInternalUriReference(nodeValue, ownerDocument);
            if (nodeByInternalUriReference == null) {
                throw new URIReferenceException(e);
            }
            try {
                return getStreamData(nodeByInternalUriReference);
            } catch (IOException e7) {
                throw new URIReferenceException("Error obteniendo los octetos del XML: " + e7, e7);
            }
        }
    }

    public static Node getNodeByInternalUriReference(String str, Document document) {
        String strSubstring = str;
        if (str.length() > 0 && str.charAt(0) == '#') {
            strSubstring = str.substring(1);
        }
        return getElementById(document, strSubstring);
    }

    protected static Data getStreamData(Node node) throws IOException {
        try {
            Class<?> xmlSignatureInputClass = getXmlSignatureInputClass();
            Object objNewInstance = xmlSignatureInputClass.getConstructor(Node.class).newInstance(node);
            if (((Boolean) xmlSignatureInputClass.getMethod("isOctetStream", new Class[0]).invoke(objNewInstance, new Object[0])).booleanValue()) {
                return (Data) getOctetStreamDataClass().getConstructor(objNewInstance.getClass()).newInstance(objNewInstance);
            }
            return (Data) getNodesetDataClass().getConstructor(objNewInstance.getClass()).newInstance(objNewInstance);
        } catch (Error e) {
            throw new IOException(e);
        } catch (Exception e2) {
            throw new IOException(e2);
        }
    }

    public static Element getElementById(Document document, String str) {
        if (document == null || str == null) {
            return null;
        }
        NodeList elementsByTagName = document.getElementsByTagName("*");
        int length = elementsByTagName.getLength();
        for (int i = 0; i < length; i++) {
            Node nodeItem = elementsByTagName.item(i);
            if (nodeItem.getNodeType() == 1) {
                NamedNodeMap attributes = nodeItem.getAttributes();
                for (int i2 = 0; i2 < attributes.getLength(); i2++) {
                    Node nodeItem2 = attributes.item(i2);
                    if ("Id".equalsIgnoreCase(nodeItem2.getNodeName()) && str.equals(nodeItem2.getNodeValue()) && (nodeItem instanceof Element)) {
                        return (Element) nodeItem;
                    }
                }
            }
        }
        return null;
    }
}
