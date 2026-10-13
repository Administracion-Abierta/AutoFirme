package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESConstants;
import es.uji.crypto.xades.jxades.util.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignatureTimeStampDetails.class */
public class SignatureTimeStampDetails extends XAdESStructure {
    public SignatureTimeStampDetails(Document document, XAdESStructure parent, SignatureTimeStamp signatureTimeStamp, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix, String tsaURL) {
        super(document, parent, XAdESConstants.TAG_SIGNATURE_TIMESTAMP, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        try {
            String tsBase64Data = Base64.encodeBytes(signatureTimeStamp.generateEncapsulatedTimeStamp(getDocument(), tsaURL));
            Element tsNode = createElement("EncapsulatedTimeStamp");
            tsNode.appendChild(getDocument().createTextNode(tsBase64Data));
            getNode().appendChild(tsNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
