package es.uji.crypto.xades.jxades.security.xml.XAdES;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignerDetails.class */
public class SignerDetails extends XAdESStructure {
    public static final String USERNAME_ATTRIBUTE = "Username";

    public SignerDetails(Document document, SignedSignatureProperties ssp, Signer signer, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(document, ssp, "SignerDetails", xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        String value = signer.getPersonName();
        if (value == null) {
            throw new IllegalArgumentException("The Signer personal name can not be NULL.");
        }
        setTextContent(value);
        String value2 = signer.getUserId();
        if (value2 != null) {
            setAttributeNS(null, "Id", value2);
        }
        String value3 = signer.getUsername();
        if (value3 != null) {
            setAttributeNS(null, USERNAME_ATTRIBUTE, value3);
        }
    }

    public SignerDetails(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public Signer getSigner() {
        SignerImpl signer = new SignerImpl();
        signer.setPersonName(getTextContent());
        signer.setUserId(getAttribute("Id"));
        signer.setUsername(getAttribute(USERNAME_ATTRIBUTE));
        return signer;
    }
}
