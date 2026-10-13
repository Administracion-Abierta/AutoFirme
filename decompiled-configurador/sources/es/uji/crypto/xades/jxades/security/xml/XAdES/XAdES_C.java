package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.security.cert.X509Certificate;
import java.util.Collection;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/XAdES_C.class */
public interface XAdES_C extends XAdES_T {
    CompleteCertificateRefs getCompleteCertificateRefs();

    void setCompleteCertificateRefs(Collection<X509Certificate> collection);
}
