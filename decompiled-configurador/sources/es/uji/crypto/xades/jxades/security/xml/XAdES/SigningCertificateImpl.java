package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.uji.crypto.xades.jxades.util.Base64;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SigningCertificateImpl.class */
public class SigningCertificateImpl implements SigningCertificate {
    private X509Certificate certificate;
    private String digestMethod;

    public SigningCertificateImpl(X509Certificate certificate, String digestMethod) {
        this.certificate = certificate;
        this.digestMethod = digestMethod;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SigningCertificateBase
    public String getDigestMethodAlgorithm() {
        return this.digestMethod;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SigningCertificateBase
    public String getDigestValue() throws GeneralSecurityException {
        try {
            String algorithm = "SHA-1";
            if ("http://www.w3.org/2001/04/xmlenc#sha256".equals(this.digestMethod)) {
                algorithm = "SHA-256";
            } else if ("http://www.w3.org/2001/04/xmlenc#sha512".equals(this.digestMethod)) {
                algorithm = "SHA-512";
            }
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(this.certificate.getEncoded());
            String result = Base64.encodeBytes(md.digest());
            return result;
        } catch (Exception e) {
            throw new GeneralSecurityException(e);
        }
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SigningCertificate
    public String getIssuerName() {
        return this.certificate.getIssuerX500Principal().getName();
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SigningCertificate
    public BigInteger getX509SerialNumber() {
        return this.certificate.getSerialNumber();
    }
}
