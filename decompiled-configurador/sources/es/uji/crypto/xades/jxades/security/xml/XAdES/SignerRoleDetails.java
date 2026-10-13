package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignerRoleDetails.class */
public class SignerRoleDetails extends XAdESStructure {
    public SignerRoleDetails(Document document, SignedSignatureProperties ssp, SignerRole signerRole, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(document, ssp, XAdESConstants.TAG_SIGNER_ROLE, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        Element claimedRoles = createElement("ClaimedRoles");
        Element certifiedRoles = createElement("CertifiedRoles");
        for (String sr : signerRole.getClaimedRole()) {
            Element claimedRole = createElement(XAdESConstants.TAG_CLAIMED_ROLE);
            claimedRole.setTextContent(sr);
            claimedRoles.appendChild(claimedRole);
        }
        for (String sr2 : signerRole.getCertifiedRole()) {
            Element certifiedRole = createElement("CertifiedRole");
            certifiedRole.setTextContent(sr2);
            certifiedRoles.appendChild(certifiedRole);
        }
        if (signerRole.getClaimedRole().size() > 0) {
            getNode().appendChild(claimedRoles);
        }
        if (signerRole.getCertifiedRole().size() > 0) {
            getNode().appendChild(certifiedRoles);
        }
    }

    public SignerRoleDetails(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }
}
