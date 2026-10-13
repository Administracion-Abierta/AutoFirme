package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignatureProductionPlaceDetails.class */
public class SignatureProductionPlaceDetails extends XAdESStructure {
    public SignatureProductionPlaceDetails(Document document, SignedSignatureProperties ssp, SignatureProductionPlace signatureProductionPlace, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(document, ssp, XAdESConstants.TAG_SIGNATURE_PRODUCTION_PLACE, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        if (signatureProductionPlace.getCity() != null) {
            Element city = createElement(XAdESConstants.TAG_CITY);
            city.setTextContent(signatureProductionPlace.getCity());
            getNode().appendChild(city);
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

    public SignatureProductionPlaceDetails(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }
}
