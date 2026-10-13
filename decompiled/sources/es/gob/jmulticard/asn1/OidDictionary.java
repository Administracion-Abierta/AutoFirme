package es.gob.jmulticard.asn1;

import es.gob.jmulticard.HexUtils;
import java.util.Properties;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/OidDictionary.class */
public final class OidDictionary {
    private static Properties dic = new Properties();

    private OidDictionary() {
    }

    static {
        dic.put("55-04-06", "C");
        dic.put("55-04-05", "SERIALNUMBER");
        dic.put("55-04-04", "SURNAME");
        dic.put("55-04-2A", "GIVENNAME");
        dic.put("55-04-03", "CN");
        dic.put("55-04-0A", PDAnnotationLink.HIGHLIGHT_MODE_OUTLINE);
        dic.put("55-04-0B", "OU");
        dic.put("2B-0E-03-02-1A", "SHA-1");
    }

    public static String getOidDescription(byte[] rawOid) {
        if (rawOid == null) {
            throw new IllegalArgumentException("No hay descripcion para un OID nulo");
        }
        String key = HexUtils.hexify(rawOid, true);
        return dic.getProperty(key) != null ? dic.getProperty(key) : key;
    }
}
