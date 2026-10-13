package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESConstants;
import es.uji.crypto.xades.jxades.util.SystemUtils;
import java.text.ParseException;
import java.util.Date;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SigningTime.class */
public class SigningTime extends XAdESStructure {
    public SigningTime(Document document, SignedSignatureProperties ssp, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        this(document, ssp, new Date(), xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public SigningTime(Document document, SignedSignatureProperties ssp, Date signingTime, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(document, ssp, XAdESConstants.TAG_SIGNING_TIME, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        getElement().setTextContent(SystemUtils.formatDate(signingTime));
    }

    public SigningTime(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public Date getSigningTime() throws ParseException {
        String value = getTextContent();
        if (value != null) {
            return SystemUtils.parseDate(value);
        }
        return null;
    }
}
