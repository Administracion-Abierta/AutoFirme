package es.uji.crypto.xades.jxades.security.xml.XAdES;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignatureProductionPlaceV2Impl.class */
public class SignatureProductionPlaceV2Impl extends SignatureProductionPlaceImpl implements SignatureProductionPlaceV2 {
    private String streetAddress;

    public SignatureProductionPlaceV2Impl() {
    }

    public SignatureProductionPlaceV2Impl(String city, String streetAddress, String stateOrProvince, String postalCode, String countryName) {
        super(city, stateOrProvince, postalCode, countryName);
        this.streetAddress = streetAddress;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignatureProductionPlaceV2
    public String getStreetAddress() {
        return this.streetAddress;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.SignatureProductionPlaceV2
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }
}
