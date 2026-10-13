package es.gob.afirma.core.misc;

import com.aowagie.text.xml.XMLConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/misc/SecureXmlTransformer.class */
public class SecureXmlTransformer {
    private static TransformerFactory TRANSFORMER_FACTORY = null;

    public static Transformer getSecureTransformer() throws TransformerConfigurationException {
        if (TRANSFORMER_FACTORY == null) {
            TRANSFORMER_FACTORY = TransformerFactory.newInstance();
            try {
                TRANSFORMER_FACTORY.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE.booleanValue());
                TRANSFORMER_FACTORY.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                TRANSFORMER_FACTORY.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            } catch (Exception e) {
                Logger.getLogger("es.gob.afirma").log(Level.WARNING, "No se ha podido establecer el procesado seguro en la factoria XML: " + e);
            }
        }
        return TRANSFORMER_FACTORY.newTransformer();
    }
}
