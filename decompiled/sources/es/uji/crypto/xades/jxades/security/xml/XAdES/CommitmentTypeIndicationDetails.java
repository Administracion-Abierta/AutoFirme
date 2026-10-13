package es.uji.crypto.xades.jxades.security.xml.XAdES;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/CommitmentTypeIndicationDetails.class */
public class CommitmentTypeIndicationDetails extends XAdESStructure {
    public CommitmentTypeIndicationDetails(Document document, SignedDataObjectProperties signedDataObjectProperties, CommitmentTypeIndication commitmentTypeIndication, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        Element objectReference;
        super(document, signedDataObjectProperties, "CommitmentTypeIndication", xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        CommitmentTypeId commitmentTypeId = commitmentTypeIndication.getCommitmentTypeId();
        if (commitmentTypeId != null) {
            new CommitmentTypeIdDetails(document, this, commitmentTypeId, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        }
        if (commitmentTypeIndication.getObjectReference() != null) {
            objectReference = createElement("ObjectReference");
            objectReference.setTextContent(commitmentTypeIndication.getObjectReference());
        } else {
            objectReference = createElement("AllSignedDataObjects");
        }
        Element commitmentTypeQualifiers = createElement("CommitmentTypeQualifiers");
        for (String qualifier : commitmentTypeIndication.getCommitmentTypeQualifiers()) {
            Element commitmentTypeQualifier = createElement("CommitmentTypeQualifier");
            commitmentTypeQualifier.setTextContent(qualifier);
            commitmentTypeQualifiers.appendChild(commitmentTypeQualifier);
        }
        getNode().appendChild(objectReference);
        getNode().appendChild(commitmentTypeQualifiers);
    }
}
