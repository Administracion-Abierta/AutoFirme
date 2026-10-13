package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignerRoleV2Details.class */
public class SignerRoleV2Details extends XAdESStructure {
    public SignerRoleV2Details(Document document, SignedSignatureProperties ssp, SignerRoleV2 signerRoleV2, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(document, ssp, XAdESConstants.TAG_SIGNER_ROLE_V2, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        Element claimedRoles = createElement("ClaimedRoles");
        Element certifiedRoles = createElement("CertifiedRolesV2");
        Element signedAssertions = createElement("SignedAssertions");
        for (String sr : signerRoleV2.getClaimedRoles()) {
            Element claimedRole = createElement(XAdESConstants.TAG_CLAIMED_ROLE);
            claimedRole.setTextContent(sr);
            claimedRoles.appendChild(claimedRole);
        }
        for (String sr2 : signerRoleV2.getCertifiedRolesV2()) {
            Element certifiedRole = createElement("CertifiedRole");
            certifiedRole.setTextContent(sr2);
            certifiedRoles.appendChild(certifiedRole);
        }
        for (String sr3 : signerRoleV2.getSignedAssertions()) {
            Element signedAssertion = createElement("SignedAssertion");
            signedAssertion.setTextContent(sr3);
            signedAssertions.appendChild(signedAssertion);
        }
        if (signerRoleV2.getClaimedRoles().size() > 0) {
            getNode().appendChild(claimedRoles);
        }
        if (signerRoleV2.getCertifiedRolesV2().size() > 0) {
            getNode().appendChild(certifiedRoles);
        }
        if (signerRoleV2.getSignedAssertions().size() > 0) {
            getNode().appendChild(signedAssertions);
        }
    }

    public SignerRoleV2Details(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }
}
