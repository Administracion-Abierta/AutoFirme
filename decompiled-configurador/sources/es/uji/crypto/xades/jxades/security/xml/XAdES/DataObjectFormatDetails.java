package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/DataObjectFormatDetails.class */
public class DataObjectFormatDetails extends XAdESStructure {
    public DataObjectFormatDetails(Document document, SignedDataObjectProperties signedDataObjectProperties, DataObjectFormat dataObjectFormat, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(document, signedDataObjectProperties, XAdESConstants.TAG_DATA_OBJECT_FORMAT, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        Element description = createElement("Description");
        description.setPrefix(xadesPrefix);
        description.setTextContent(dataObjectFormat.getDescription());
        getNode().appendChild(description);
        ObjectIdentifier objectIdentifier = dataObjectFormat.getObjectIdentifier();
        if (objectIdentifier != null) {
            new ObjectIdentifierDetails(document, this, objectIdentifier, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        }
        Element mimetype = createElement("MimeType");
        mimetype.setPrefix(xadesPrefix);
        mimetype.setTextContent(dataObjectFormat.getMimeType());
        getNode().appendChild(mimetype);
        Element encoding = createElement("Encoding");
        encoding.setPrefix(xadesPrefix);
        encoding.setTextContent(dataObjectFormat.getEncoding());
        getNode().appendChild(encoding);
        setAttributeNS(xadesNamespace, "ObjectReference", dataObjectFormat.getObjectReference());
    }
}
