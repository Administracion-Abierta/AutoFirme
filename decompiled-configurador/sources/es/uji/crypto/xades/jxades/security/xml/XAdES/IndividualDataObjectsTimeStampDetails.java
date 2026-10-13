package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.uji.crypto.xades.jxades.util.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/IndividualDataObjectsTimeStampDetails.class */
public class IndividualDataObjectsTimeStampDetails extends XAdESStructure {
    public IndividualDataObjectsTimeStampDetails(Document document, SignedDataObjectProperties signedDataObjectProperties, IndividualDataObjectsTimeStamp individualDataObjectsTimeStampDetails, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix, String tsaURL) {
        super(document, signedDataObjectProperties, "IndividualDataObjectsTimeStamp", xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        try {
            String tsBase64Data = Base64.encodeBytes(individualDataObjectsTimeStampDetails.generateEncapsulatedTimeStamp(getDocument(), tsaURL));
            Element tsNode = createElement("EncapsulatedTimeStamp");
            tsNode.appendChild(getDocument().createTextNode(tsBase64Data));
            getNode().appendChild(tsNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
