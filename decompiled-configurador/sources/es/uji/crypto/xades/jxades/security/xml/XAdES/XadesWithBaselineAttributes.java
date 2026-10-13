package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/XadesWithBaselineAttributes.class */
public interface XadesWithBaselineAttributes {
    SigningCertificateV2 getSigningCertificateV2();

    void setSigningCertificateV2(X509Certificate x509Certificate, SigningCertificateV2Info signingCertificateV2Info);

    SignatureProductionPlace getSignatureProductionPlaceV2();

    void setSignatureProductionPlaceV2(SignatureProductionPlaceV2 signatureProductionPlaceV2);

    SignerRoleV2 getSignerRoleV2();

    void setSignerRoleV2(SignerRoleV2 signerRoleV2);
}
