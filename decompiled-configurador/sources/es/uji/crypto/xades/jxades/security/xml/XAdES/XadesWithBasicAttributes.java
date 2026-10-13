package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/XadesWithBasicAttributes.class */
public interface XadesWithBasicAttributes {
    SigningCertificate getSigningCertificate();

    void setSigningCertificate(X509Certificate x509Certificate);

    SignatureProductionPlace getSignatureProductionPlace();

    void setSignatureProductionPlace(SignatureProductionPlace signatureProductionPlace);

    SignerRole getSignerRole();

    void setSignerRole(SignerRole signerRole);
}
