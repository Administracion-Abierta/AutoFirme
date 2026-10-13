package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SigningCertificate.class */
public interface SigningCertificate extends SigningCertificateBase {
    String getIssuerName();

    BigInteger getX509SerialNumber();
}
