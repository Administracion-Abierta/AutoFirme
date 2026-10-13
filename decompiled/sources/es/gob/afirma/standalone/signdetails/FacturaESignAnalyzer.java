package es.gob.afirma.standalone.signdetails;

import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AdESPolicy;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.signers.xades.AOFacturaESigner;
import es.gob.afirma.signers.xades.XAdESConstants;
import es.gob.afirma.signers.xades.XAdESUtil;
import es.gob.afirma.signers.xml.Utils;
import es.gob.afirma.signvalidation.SignatureFormatDetectorXades;
import es.gob.afirma.signvalidation.ValidateXMLSignature;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/signdetails/FacturaESignAnalyzer.class */
public class FacturaESignAnalyzer implements SignAnalyzer {
    List<SignDetails> signDetailsList;
    List<CertificateDetails> certDetailsList;
    Document signDocument;
    AOTreeModel signersTree;
    public static final String FACTURAE = "Factura Electr&oacute;nica";
    public static final String URL_SHA1_RSA = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    private static final String URL_SHA256_RSA = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    private static final String URL_SHA384_RSA = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
    private static final String URL_SHA512_RSA = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
    public static final String URL_SHA1_ECDSA = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
    private static final String URL_SHA256_ECDSA = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
    private static final String URL_SHA384_ECDSA = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
    private static final String URL_SHA512_ECDSA = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
    public static final Map<String, String> FACTURAE_CLAIMED_ROLES;
    static final String[] SUPPORTED_XADES_NAMESPACE_URIS;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    public static final Map<String, String> SIGN_ALGOS_URI = new HashMap();

    static {
        SIGN_ALGOS_URI.put("http://www.w3.org/2000/09/xmldsig#rsa-sha1", AOSignConstants.SIGN_ALGORITHM_SHA1WITHRSA);
        SIGN_ALGOS_URI.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", AOSignConstants.SIGN_ALGORITHM_SHA256WITHRSA);
        SIGN_ALGOS_URI.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384", AOSignConstants.SIGN_ALGORITHM_SHA384WITHRSA);
        SIGN_ALGOS_URI.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512", "SHA512withRSA");
        SIGN_ALGOS_URI.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1", AOSignConstants.SIGN_ALGORITHM_SHA1WITHECDSA);
        SIGN_ALGOS_URI.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256", AOSignConstants.SIGN_ALGORITHM_SHA256WITHECDSA);
        SIGN_ALGOS_URI.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384", AOSignConstants.SIGN_ALGORITHM_SHA384WITHECDSA);
        SIGN_ALGOS_URI.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512", AOSignConstants.SIGN_ALGORITHM_SHA512WITHECDSA);
        FACTURAE_CLAIMED_ROLES = new HashMap();
        FACTURAE_CLAIMED_ROLES.put("emisor", "Emisor");
        FACTURAE_CLAIMED_ROLES.put("supplier", "Emisor");
        FACTURAE_CLAIMED_ROLES.put("receptor", "Receptor");
        FACTURAE_CLAIMED_ROLES.put("customer", "Receptor");
        FACTURAE_CLAIMED_ROLES.put("tercero", "Tercero");
        FACTURAE_CLAIMED_ROLES.put("third party", "Tercero");
        SUPPORTED_XADES_NAMESPACE_URIS = new String[]{XAdESConstants.NAMESPACE_XADES_NO_VERSION, XAdESConstants.NAMESPACE_XADES_1_2_2, "http://uri.etsi.org/01903/v1.3.2#", "http://uri.etsi.org/01903/v1.4.1#"};
    }

    public FacturaESignAnalyzer(byte[] bArr) throws Exception {
        try {
            this.signersTree = SignAnalyzer.getSignInfo(bArr).getSignsTree();
            this.signDetailsList = new ArrayList();
            this.certDetailsList = new ArrayList();
            this.signDocument = Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr));
            createSignDetails(this.signDocument.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature"));
        } catch (Exception e) {
            throw new AOInvalidFormatException("No se ha podido cargar el documento XML de firmas", e);
        }
    }

    @Override // es.gob.afirma.standalone.signdetails.SignAnalyzer
    public AOTreeModel getSignersTree() {
        return this.signersTree;
    }

    @Override // es.gob.afirma.standalone.signdetails.SignAnalyzer
    public List<SignDetails> getAllSignDetails() {
        return this.signDetailsList;
    }

    @Override // es.gob.afirma.standalone.signdetails.SignAnalyzer
    public String getSignFormat() {
        return FACTURAE;
    }

    @Override // es.gob.afirma.standalone.signdetails.SignAnalyzer
    public String getDataLocation() {
        return null;
    }

    private void createSignDetails(NodeList nodeList) throws AOInvalidFormatException {
        for (int i = 0; i < nodeList.getLength(); i++) {
            try {
                Element element = (Element) nodeList.item(i);
                String strResolveSignerXAdESFormat = SignatureFormatDetectorXades.resolveSignerXAdESFormat(element);
                SignDetails signDetailsBuildSignDetails = buildSignDetails(element, strResolveSignerXAdESFormat);
                signDetailsBuildSignDetails.setValidityResult(ValidateXMLSignature.validateSign(element, strResolveSignerXAdESFormat, false));
                this.signDetailsList.add(signDetailsBuildSignDetails);
            } catch (Exception e) {
                throw new AOInvalidFormatException("No se ha podido cargar el documento XML de firmas", e);
            }
        }
    }

    private static SignDetails buildSignDetails(Element element, String str) throws AOInvalidFormatException {
        String[] claimedRoleMetadata;
        SignDetails signDetails = new SignDetails();
        signDetails.setSignProfile(str);
        Element signatureMethodElement = XAdESUtil.getSignatureMethodElement(element);
        if (signatureMethodElement == null) {
            throw new AOInvalidFormatException("El elemento SignatureMethod no existe");
        }
        String attribute = SIGN_ALGOS_URI.get(signatureMethodElement.getAttribute("Algorithm"));
        if (attribute == null) {
            attribute = signatureMethodElement.getAttribute("Algorithm");
        }
        signDetails.setAlgorithm(attribute);
        signDetails.setSigner(buildCertDetails(element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_X509DATA).item(0)));
        NodeList elementsByTagNameNS = element.getElementsByTagNameNS("*", XAdESConstants.TAG_QUALIFYING_PROPERTIES);
        boolean z = false;
        if (elementsByTagNameNS.getLength() > 0) {
            Element element2 = (Element) elementsByTagNameNS.item(0);
            String namespaceURI = element2.getNamespaceURI();
            for (String str2 : SUPPORTED_XADES_NAMESPACE_URIS) {
                if (str2.equals(namespaceURI)) {
                    z = true;
                }
            }
            if (!z) {
                throw new AOInvalidFormatException("Una de las firmas encontradas en el documento contiene una version inexistente de XAdES");
            }
            signDetails.setSigningTime(obtainSigningTime(element2, namespaceURI));
            NodeList elementsByTagNameNS2 = element2.getElementsByTagNameNS(namespaceURI, XAdESConstants.TAG_SIGNATURE_POLICY_IDENTIFIER);
            if (elementsByTagNameNS2 != null && elementsByTagNameNS2.getLength() > 0) {
                Element element3 = (Element) elementsByTagNameNS2.item(0);
                String nodeValue = element3.getElementsByTagNameNS(namespaceURI, "Identifier").item(0).getFirstChild().getNodeValue();
                Node nodeItem = element3.getElementsByTagNameNS(namespaceURI, "Description").item(0);
                String strTrim = "";
                if (nodeItem != null) {
                    strTrim = nodeItem.getTextContent().trim();
                }
                if (AOFacturaESigner.POLICY_FACTURAE_30.getPolicyIdentifier().equals(nodeValue)) {
                    signDetails.setPolicy(new SignaturePolicy(SimpleAfirmaMessages.getString("PreferencesPanelFacturaE.1"), AOFacturaESigner.POLICY_FACTURAE_30));
                } else if (AOFacturaESigner.POLICY_FACTURAE_31.getPolicyIdentifier().equals(nodeValue)) {
                    signDetails.setPolicy(new SignaturePolicy(SimpleAfirmaMessages.getString("PreferencesPanelFacturaE.2"), AOFacturaESigner.POLICY_FACTURAE_31));
                } else {
                    String namespaceURI2 = element.getNamespaceURI();
                    String strTrim2 = element3.getElementsByTagNameNS(namespaceURI2, "DigestValue").item(0).getTextContent().trim();
                    String attribute2 = ((Element) element3.getElementsByTagNameNS(namespaceURI2, "DigestMethod").item(0)).getAttribute("Algorithm");
                    String strTrim3 = "";
                    if (element3.getElementsByTagNameNS(namespaceURI, XAdESConstants.TAG_SPURI).getLength() > 0) {
                        strTrim3 = element3.getElementsByTagNameNS(namespaceURI, XAdESConstants.TAG_SPURI).item(0).getTextContent().trim();
                    }
                    signDetails.setPolicy(new SignaturePolicy(strTrim, new AdESPolicy(nodeValue, strTrim2, attribute2, strTrim3)));
                }
            }
            NodeList elementsByTagNameNS3 = element2.getElementsByTagNameNS(namespaceURI, XAdESConstants.TAG_SIGNATURE_PRODUCTION_PLACE);
            Map<String, String> map = new HashMap();
            if (elementsByTagNameNS3.getLength() > 0) {
                map = getProductionPlaceMetadata(elementsByTagNameNS3, namespaceURI);
            } else {
                NodeList elementsByTagNameNS4 = element2.getElementsByTagNameNS(namespaceURI, XAdESConstants.TAG_SIGNATURE_PRODUCTION_PLACE_V2);
                if (elementsByTagNameNS4.getLength() > 0) {
                    map = getProductionPlaceMetadata(elementsByTagNameNS4, namespaceURI);
                }
            }
            NodeList elementsByTagNameNS5 = element2.getElementsByTagNameNS(namespaceURI, XAdESConstants.TAG_SIGNER_ROLE);
            if (elementsByTagNameNS5.getLength() > 0) {
                String[] claimedRoleMetadata2 = getClaimedRoleMetadata((Element) elementsByTagNameNS5.item(0), namespaceURI);
                if (claimedRoleMetadata2 != null) {
                    for (int i = 0; i < claimedRoleMetadata2.length; i++) {
                        map.put("claimedRole" + i, claimedRoleMetadata2[i]);
                    }
                }
            } else {
                NodeList elementsByTagNameNS6 = element2.getElementsByTagNameNS(namespaceURI, XAdESConstants.TAG_SIGNER_ROLE_V2);
                if (elementsByTagNameNS6.getLength() > 0 && (claimedRoleMetadata = getClaimedRoleMetadata((Element) elementsByTagNameNS6.item(0), namespaceURI)) != null) {
                    for (int i2 = 0; i2 < claimedRoleMetadata.length; i2++) {
                        map.put("claimedRole" + i2, claimedRoleMetadata[i2]);
                    }
                }
            }
            signDetails.setMetadata(map);
        }
        return signDetails;
    }

    private static CertificateDetails buildCertDetails(Node node) {
        X509Certificate certificate;
        if (node != null && (certificate = Utils.getCertificate((Element) ((Element) node).getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "X509Certificate").item(0))) != null) {
            return new CertificateDetails(certificate);
        }
        return null;
    }

    private static Map<String, String> getProductionPlaceMetadata(NodeList nodeList, String str) {
        HashMap map = new HashMap();
        Element element = (Element) nodeList.item(0);
        NodeList elementsByTagNameNS = element.getElementsByTagNameNS(str, XAdESConstants.TAG_STREET_ADDRESS);
        if (elementsByTagNameNS.getLength() > 0) {
            map.put(SimpleAfirmaMessages.getString("ValidationInfoDialog.11"), elementsByTagNameNS.item(0).getTextContent().trim());
        }
        NodeList elementsByTagNameNS2 = element.getElementsByTagNameNS(str, XAdESConstants.TAG_POSTAL_CODE);
        if (elementsByTagNameNS2.getLength() > 0) {
            map.put(SimpleAfirmaMessages.getString("ValidationInfoDialog.8"), elementsByTagNameNS2.item(0).getTextContent().trim());
        }
        NodeList elementsByTagNameNS3 = element.getElementsByTagNameNS(str, XAdESConstants.TAG_CITY);
        if (elementsByTagNameNS3.getLength() > 0) {
            map.put(SimpleAfirmaMessages.getString("ValidationInfoDialog.6"), elementsByTagNameNS3.item(0).getTextContent().trim());
        }
        NodeList elementsByTagNameNS4 = element.getElementsByTagNameNS(str, XAdESConstants.TAG_STATE_OR_PROVINCE);
        if (elementsByTagNameNS4.getLength() > 0) {
            map.put(SimpleAfirmaMessages.getString("ValidationInfoDialog.7"), elementsByTagNameNS4.item(0).getTextContent().trim());
        }
        NodeList elementsByTagNameNS5 = element.getElementsByTagNameNS(str, XAdESConstants.TAG_COUNTRY_NAME);
        if (elementsByTagNameNS5.getLength() > 0) {
            map.put(SimpleAfirmaMessages.getString("ValidationInfoDialog.9"), elementsByTagNameNS5.item(0).getTextContent().trim());
        }
        return map;
    }

    private static String[] getClaimedRoleMetadata(Element element, String str) {
        String[] strArr = null;
        NodeList elementsByTagNameNS = element.getElementsByTagNameNS(str, XAdESConstants.TAG_CLAIMED_ROLE);
        if (elementsByTagNameNS.getLength() > 0) {
            strArr = new String[elementsByTagNameNS.getLength()];
            for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
                String strTrim = elementsByTagNameNS.item(i).getTextContent().trim();
                strArr[i] = FACTURAE_CLAIMED_ROLES.getOrDefault(strTrim, strTrim);
            }
        }
        return strArr;
    }

    private static Date obtainSigningTime(Element element, String str) {
        Element element2 = (Element) element.getElementsByTagNameNS(str, XAdESConstants.TAG_SIGNING_TIME).item(0);
        if (element2 != null) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(element2.getTextContent());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "No se ha podido recuperar la fecha de firma", (Throwable) e);
                return null;
            }
        }
        LOGGER.info("No se ha encontrado la hora de firma de una firma");
        return null;
    }
}
