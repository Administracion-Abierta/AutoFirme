package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESExtraParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/CommitmentTypeIdDetails.class */
public class CommitmentTypeIdDetails extends XAdESStructure {
    public CommitmentTypeIdDetails(Document document, CommitmentTypeIndicationDetails commitmentTypeIndicationDetails, CommitmentTypeId commitmentTypeId, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(document, commitmentTypeIndicationDetails, "CommitmentTypeId", xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        Element identifier = createElement("Identifier");
        identifier.setTextContent(commitmentTypeId.getIdentifier());
        if (commitmentTypeId.getQualifier() != null) {
            identifier.setAttributeNS(xadesNamespace, "Qualifier", commitmentTypeId.getQualifier());
        }
        getNode().appendChild(identifier);
        Element description = createElement("Description");
        description.setTextContent(commitmentTypeId.getDescription());
        getNode().appendChild(description);
        if (commitmentTypeId.getDocumentationReferences().size() > 0) {
            Element documentationReferences = createElement(XAdESExtraParams.COMMITMENT_TYPE_INDICATION_DOCUMENTATION_REFERENCE);
            for (String reference : commitmentTypeId.getDocumentationReferences()) {
                Element documentationReference = createElement("DocumentationReference");
                documentationReference.setTextContent(reference);
                documentationReferences.appendChild(documentationReference);
            }
            getNode().appendChild(documentationReferences);
        }
    }
}
