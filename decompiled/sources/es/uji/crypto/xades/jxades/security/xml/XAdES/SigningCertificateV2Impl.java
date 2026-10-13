package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.uji.crypto.xades.jxades.util.Base64;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SigningCertificateV2Impl.class */
public class SigningCertificateV2Impl implements SigningCertificateV2 {
    private X509Certificate certificate;
    private String digestMethod;
    private String issuerSerial = null;

    public SigningCertificateV2Impl(X509Certificate certificate, String digestMethod) {
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
            String algorithm = "SHA-256";
            if ("http://www.w3.org/2001/04/xmlenc#sha512".equals(this.digestMethod)) {
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

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SigningCertificateV2
    public void setIssuerSerialV2(String issuerSerial) {
        this.issuerSerial = issuerSerial;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SigningCertificateV2
    public String getIssuerSerialV2() {
        return this.issuerSerial;
    }
}
