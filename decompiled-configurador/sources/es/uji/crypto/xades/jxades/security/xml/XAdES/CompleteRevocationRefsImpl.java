package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.Collections;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/CompleteRevocationRefsImpl.class */
public class CompleteRevocationRefsImpl extends XAdESStructure implements CompleteRevocationRefs {
    private List<OCSPRef> ocspRefs;
    private List<CRLRef> crlRefs;
    private ValidationResult validationResult;

    public CompleteRevocationRefsImpl(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.CompleteRevocationRefs
    public List<OCSPRef> getOCSPRefs() {
        if (this.ocspRefs == null) {
            Element element = getChildElementNS("OCSPRefs");
            if (element != null) {
                OCSPRefs refs = new OCSPRefs(element, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
                this.ocspRefs = refs.getOCSPRefs();
            } else {
                this.ocspRefs = Collections.emptyList();
            }
        }
        return this.ocspRefs;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.CompleteRevocationRefs
    public List<CRLRef> getCRLRefs() {
        if (this.crlRefs == null) {
            Element element = getChildElementNS("CRLRefs");
            if (element != null) {
                CRLRefs refs = new CRLRefs(element, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
                this.crlRefs = refs.getCRLRefs();
            } else {
                this.crlRefs = Collections.emptyList();
            }
        }
        return this.crlRefs;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.CompleteRevocationRefs
    public ValidationResult getValidationResult() {
        Element element;
        if (this.validationResult == null && (element = getChildElementNS("ValidationResult")) != null) {
            this.validationResult = new ValidationResult(element, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
        return this.validationResult;
    }
}
