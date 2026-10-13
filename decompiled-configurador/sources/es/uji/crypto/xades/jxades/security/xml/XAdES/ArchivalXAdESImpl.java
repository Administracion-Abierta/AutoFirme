package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.Date;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/ArchivalXAdESImpl.class */
public class ArchivalXAdESImpl extends ExtendedLongXAdESImpl {
    public ArchivalXAdESImpl(Document document, Element baseElement, boolean readOnlyMode, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix, String digestMethod) {
        super(document, baseElement, readOnlyMode, xadesPrefix, xadesNamespace, xmlSignaturePrefix, digestMethod);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    protected void unmarshal() throws MarshalException {
        QualifyingProperties qp = getQualifyingProperties();
        if (qp != null) {
            try {
                Element qpElement = qp.getElement();
                for (XAdES.Element key : XAdES.Element.values()) {
                    NodeList nl = qpElement.getElementsByTagNameNS(this.xadesNamespace, key.getElementName());
                    if (nl != null && nl.getLength() > 0) {
                        if (XAdES.Element.SIGNING_TIME.equals(key)) {
                            SigningTime signingTime = new SigningTime(nl.item(0), this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
                            Date date = signingTime.getSigningTime();
                            if (date != null) {
                                this.data.put(XAdES.Element.SIGNING_TIME, date);
                            }
                        } else if (XAdES.Element.SIGNER_DETAILS.equals(key)) {
                            SignerDetails signerDetails = new SignerDetails(nl.item(0), this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
                            this.data.put(XAdES.Element.SIGNER, signerDetails.getSigner());
                        } else if (XAdES.Element.COMPLETE_CERTIFICATE_REFS.equals(key)) {
                            CompleteCertificateRefsImpl completeCertificateRefs = new CompleteCertificateRefsImpl(nl.item(0), this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
                            this.data.put(XAdES.Element.COMPLETE_CERTIFICATE_REFS, completeCertificateRefs);
                        } else if (XAdES.Element.COMPLETE_REVOCATION_REFS.equals(key)) {
                            CompleteRevocationRefsImpl completeRevocationRefs = new CompleteRevocationRefsImpl(nl.item(0), this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
                            this.data.put(XAdES.Element.COMPLETE_REVOCATION_REFS, completeRevocationRefs);
                        }
                    }
                }
            } catch (Exception ex) {
                throw new MarshalException(ex);
            }
        }
    }
}
