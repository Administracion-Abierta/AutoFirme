package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.security.cert.X509Certificate;
import java.util.Collection;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/XAdES_X.class */
public interface XAdES_X extends XAdES_T {
    CompleteCertificateRefs getCompleteCertificateRefs();

    void setCompleteCertificateRefs(Collection<X509Certificate> collection);

    CompleteRevocationRefs getCompleteRevocationRefs();
}
