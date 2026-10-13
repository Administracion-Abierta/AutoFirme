package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.uji.crypto.xades.jxades.util.Base64;
import es.uji.crypto.xades.jxades.util.SystemUtils;
import es.uji.crypto.xades.jxades.util.XMLUtils;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/OCSPIdentifier.class */
public class OCSPIdentifier extends XAdESStructure {
    private X500Principal responderName;
    private byte[] responderKey;
    private Date producedAt;

    public OCSPIdentifier(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public X500Principal getResponderName() {
        Element element;
        Element element2;
        String value;
        if (this.responderName == null && (element = getChildElementNS("ResponderID")) != null && (element2 = XMLUtils.getChildElementByTagName(element, "ByName")) != null && (value = element2.getTextContent()) != null) {
            String value2 = value.trim();
            if (value2.length() > 0) {
                this.responderName = new X500Principal(value2);
            }
        }
        return this.responderName;
    }

    public byte[] getResponderKey() throws IOException {
        Element element;
        Element element2;
        String value;
        if (this.responderKey == null && (element = getChildElementNS("ResponderID")) != null && (element2 = XMLUtils.getChildElementByTagName(element, "ByKey")) != null && (value = element2.getTextContent()) != null) {
            String value2 = value.trim();
            if (value2.length() > 0) {
                this.responderKey = Base64.decode(value2);
            }
        }
        return this.responderKey;
    }

    public Date getProducedAt() throws ParseException {
        String value;
        if (this.producedAt == null && (value = getChildElementTextContent("ProducedAt")) != null) {
            this.producedAt = SystemUtils.parseDate(value);
        }
        return this.producedAt;
    }
}
