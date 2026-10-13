package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESConstants;
import java.security.GeneralSecurityException;
import java.util.Date;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignedSignatureProperties.class */
public class SignedSignatureProperties extends XAdESStructure {
    private Document document;

    public SignedSignatureProperties(Document document, SignedProperties sp, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(document, sp, XAdESConstants.TAG_SIGNED_SIGNATURE_PROPERTIES, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        this.document = document;
    }

    public SignedSignatureProperties(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public void setSigningTime() {
        setSigningTime(new Date());
    }

    public void setSigningTime(Date signingTime) {
        new SigningTime(this.document, this, signingTime, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
    }

    public void setSigner(Signer signer) {
        new SignerDetails(this.document, this, signer, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
    }

    public void setSigningCertificate(SigningCertificate signingCertificate) throws GeneralSecurityException {
        if (signingCertificate != null) {
            new SigningCertificateDetails(this.document, this, signingCertificate, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
    }

    public void setSigningCertificateV2(SigningCertificateV2 signingCertificateV2) throws GeneralSecurityException {
        if (signingCertificateV2 != null) {
            new SigningCertificateV2Details(this.document, this, signingCertificateV2, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
    }

    public void setSignerRole(SignerRole signerRole) {
        if (signerRole != null) {
            if (signerRole.getClaimedRole().size() > 0 || signerRole.getCertifiedRole().size() > 0) {
                new SignerRoleDetails(this.document, this, signerRole, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
            }
        }
    }

    public void setSignerRoleV2(SignerRoleV2 signerRole) {
        if (signerRole != null) {
            if (signerRole.getClaimedRoles().size() > 0 || signerRole.getCertifiedRolesV2().size() > 0 || signerRole.getSignedAssertions().size() > 0) {
                new SignerRoleV2Details(this.document, this, signerRole, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
            }
        }
    }

    public Signer getSigner() {
        SignerDetails details = getSignerDetails();
        if (details != null) {
            Signer signer = details.getSigner();
            return signer;
        }
        return null;
    }

    protected SignerDetails getSignerDetails() {
        Element element = getChildElementNS("SignerDetails");
        if (element != null) {
            return new SignerDetails(element, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
        return null;
    }

    public void setSignatureProductionPlace(SignatureProductionPlace signatureProductionPlace) {
        if (signatureProductionPlace != null) {
            new SignatureProductionPlaceDetails(this.document, this, signatureProductionPlace, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
    }

    public void setSignatureProductionPlaceV2(SignatureProductionPlaceV2 signatureProductionPlace) {
        if (signatureProductionPlace != null) {
            new SignatureProductionPlaceV2Details(this.document, this, signatureProductionPlace, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
    }

    public void setSignaturePolicyIdentifier(SignaturePolicyIdentifier signaturePolicyIdentifier) {
        if (signaturePolicyIdentifier != null) {
            new SignaturePolicyIdentifierDetails(this.document, this, signaturePolicyIdentifier, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
    }
}
