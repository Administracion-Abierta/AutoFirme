package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.uji.crypto.xades.jxades.util.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/AllDataObjectsTimeStampDetails.class */
public class AllDataObjectsTimeStampDetails extends XAdESStructure {
    public AllDataObjectsTimeStampDetails(Document document, SignedDataObjectProperties signedDataObjectProperties, AllDataObjectsTimeStamp allDataObjectsTimeStamp, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix, String tsaURL) {
        super(document, signedDataObjectProperties, "AllDataObjectsTimeStamp", xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        try {
            String tsBase64Data = Base64.encodeBytes(allDataObjectsTimeStamp.generateEncapsulatedTimeStamp(getDocument(), tsaURL));
            Element tsNode = createElement("EncapsulatedTimeStamp");
            tsNode.appendChild(getDocument().createTextNode(tsBase64Data));
            getNode().appendChild(tsNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
