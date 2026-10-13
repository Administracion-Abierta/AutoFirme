package es.uji.crypto.xades.jxades.security.xml.XAdES;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignatureProductionPlaceImpl.class */
public class SignatureProductionPlaceImpl implements SignatureProductionPlace {
    private String city;
    private String stateOrProvince;
    private String postalCode;
    private String countryName;

    public SignatureProductionPlaceImpl() {
    }

    public SignatureProductionPlaceImpl(String city, String stateOrProvince, String postalCode, String countryName) {
        this.city = city;
        this.stateOrProvince = stateOrProvince;
        this.postalCode = postalCode;
        this.countryName = countryName;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignatureProductionPlace
    public String getCity() {
        return this.city;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignatureProductionPlace
    public void setCity(String city) {
        this.city = city;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignatureProductionPlace
    public String getStateOrProvince() {
        return this.stateOrProvince;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignatureProductionPlace
    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignatureProductionPlace
    public String getPostalCode() {
        return this.postalCode;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignatureProductionPlace
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignatureProductionPlace
    public String getCountryName() {
        return this.countryName;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignatureProductionPlace
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
