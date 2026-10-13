package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/CompleteRevocationRefs.class */
public interface CompleteRevocationRefs {
    List<OCSPRef> getOCSPRefs();

    List<CRLRef> getCRLRefs();

    ValidationResult getValidationResult();
}
