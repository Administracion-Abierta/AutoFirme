package es.uji.crypto.xades.jxades.security.xml.XAdES;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignatureProductionPlace.class */
public interface SignatureProductionPlace {
    void setCity(String str);

    void setStateOrProvince(String str);

    void setPostalCode(String str);

    void setCountryName(String str);

    String getCity();

    String getStateOrProvince();

    String getPostalCode();

    String getCountryName();
}
