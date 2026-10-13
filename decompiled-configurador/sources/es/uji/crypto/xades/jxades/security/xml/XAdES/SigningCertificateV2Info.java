package es.uji.crypto.xades.jxades.security.xml.XAdES;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SigningCertificateV2Info.class */
public class SigningCertificateV2Info implements SigningCertificateInfo {
    private String issuerSerialV2;

    public SigningCertificateV2Info(String issuerSerialEncoded) {
        this.issuerSerialV2 = issuerSerialEncoded;
    }

    public String getIssuerSerialV2() {
        return this.issuerSerialV2;
    }
}
