package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignatureProductionPlaceV2Details.class */
public class SignatureProductionPlaceV2Details extends XAdESStructure {
    public SignatureProductionPlaceV2Details(Document document, SignedSignatureProperties ssp, SignatureProductionPlaceV2 signatureProductionPlace, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(document, ssp, XAdESConstants.TAG_SIGNATURE_PRODUCTION_PLACE_V2, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        if (signatureProductionPlace.getCity() != null) {
            Element city = createElement(XAdESConstants.TAG_CITY);
            city.setTextContent(signatureProductionPlace.getCity());
            getNode().appendChild(city);
        }
        if (signatureProductionPlace.getStreetAddress() != null) {
            Element streetAddress = createElement(XAdESConstants.TAG_STREET_ADDRESS);
            streetAddress.setTextContent(signatureProductionPlace.getStreetAddress());
            getNode().appendChild(streetAddress);
        }
        if (signatureProductionPlace.getStateOrProvince() != null) {
            Element stateOrProvince = createElement(XAdESConstants.TAG_STATE_OR_PROVINCE);
            stateOrProvince.setTextContent(signatureProductionPlace.getStateOrProvince());
            getNode().appendChild(stateOrProvince);
        }
        if (signatureProductionPlace.getPostalCode() != null) {
            Element postalCode = createElement(XAdESConstants.TAG_POSTAL_CODE);
            postalCode.setTextContent(signatureProductionPlace.getPostalCode());
            getNode().appendChild(postalCode);
        }
        if (signatureProductionPlace.getCountryName() != null) {
            Element countryName = createElement(XAdESConstants.TAG_COUNTRY_NAME);
            countryName.setTextContent(signatureProductionPlace.getCountryName());
            getNode().appendChild(countryName);
        }
    }

    public SignatureProductionPlaceV2Details(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }
}
