package es.gob.afirma.core.misc;

import com.aowagie.text.xml.XMLConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/misc/SecureXmlBuilder.class */
public class SecureXmlBuilder {
    private static DocumentBuilderFactory SECURE_BUILDER_FACTORY = null;
    private static SAXParserFactory SAX_FACTORY = null;

    public static DocumentBuilder getSecureDocumentBuilder() throws ParserConfigurationException {
        if (SECURE_BUILDER_FACTORY == null) {
            SECURE_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
            try {
                SECURE_BUILDER_FACTORY.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE.booleanValue());
            } catch (Exception e) {
                Logger.getLogger("es.gob.afirma").log(Level.WARNING, "No se ha podido establecer el procesado seguro en la factoria XML: " + e);
            }
            for (String str : new String[]{XMLConstants.ACCESS_EXTERNAL_DTD, XMLConstants.ACCESS_EXTERNAL_SCHEMA, XMLConstants.ACCESS_EXTERNAL_STYLESHEET}) {
                try {
                    SECURE_BUILDER_FACTORY.setAttribute(str, "");
                } catch (Exception e2) {
                    Logger.getLogger("es.gob.afirma").log(Level.FINE, "No se ha podido establecer una propiedad de seguridad '" + str + "' en la factoria XML");
                }
            }
            SECURE_BUILDER_FACTORY.setValidating(false);
            SECURE_BUILDER_FACTORY.setNamespaceAware(true);
        }
        return SECURE_BUILDER_FACTORY.newDocumentBuilder();
    }

    public static SAXParser getSecureSAXParser() throws ParserConfigurationException, SAXException {
        if (SAX_FACTORY == null) {
            SAX_FACTORY = SAXParserFactory.newInstance();
            try {
                SAX_FACTORY.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE.booleanValue());
            } catch (Exception e) {
                Logger.getLogger("es.gob.afirma").log(Level.SEVERE, "No se ha podido establecer una caracteristica de seguridad en la factoria XML: " + e);
            }
            try {
                SAX_FACTORY.setFeature("http://xml.org/sax/features/external-general-entities", false);
                SAX_FACTORY.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            } catch (Exception e2) {
                Logger.getLogger("es.gob.afirma").log(Level.FINE, "No se ha podido establecer una caracteristica de seguridad en la factoria SAX XML: " + e2);
            }
            SAX_FACTORY.setValidating(false);
            SAX_FACTORY.setNamespaceAware(true);
        }
        return SAX_FACTORY.newSAXParser();
    }
}
