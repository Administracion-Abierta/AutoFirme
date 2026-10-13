package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.security.GeneralSecurityException;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SigningCertificateBase.class */
public interface SigningCertificateBase {
    String getDigestMethodAlgorithm();

    String getDigestValue() throws GeneralSecurityException;
}
