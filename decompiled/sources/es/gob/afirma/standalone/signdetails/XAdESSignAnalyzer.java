package es.gob.afirma.standalone.signdetails;

import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AdESPolicy;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.signers.xades.AOXAdESSigner;
import es.gob.afirma.signers.xades.XAdESConstants;
import es.gob.afirma.signers.xades.XAdESUtil;
import es.gob.afirma.signers.xml.Utils;
import es.gob.afirma.signvalidation.ISignatureFormatDetector;
import es.gob.afirma.signvalidation.SignatureFormatDetectorXades;
import es.gob.afirma.signvalidation.ValidateXMLSignature;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.io.ByteArrayInputStream;
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

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/signdetails/XAdESSignAnalyzer.class */
public class XAdESSignAnalyzer implements SignAnalyzer {
    public static final String URL_SHA1_RSA = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    private static final String URL_SHA256_RSA = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    private static final String URL_SHA384_RSA = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
    private static final String URL_SHA512_RSA = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
    public static final String URL_SHA1_ECDSA = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
    private static final String URL_SHA256_ECDSA = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
    private static final String URL_SHA384_ECDSA = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
    private static final String URL_SHA512_ECDSA = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
    List<SignDetails> signDetailsList;
    List<CertificateDetails> certDetailsList;
    Document signDocument;
    AOTreeModel signersTree;
    String dataLocation;
    private static final String FORMAT_XADES = "XAdES";
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    static final String[] SUPPORTED_XADES_NAMESPACE_URIS = {XAdESConstants.NAMESPACE_XADES_NO_VERSION, XAdESConstants.NAMESPACE_XADES_1_2_2, "http://uri.etsi.org/01903/v1.3.2#", "http://uri.etsi.org/01903/v1.4.1#"};
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
    }

    public XAdESSignAnalyzer(byte[] bArr) throws Exception {
        try {
            this.signersTree = SignAnalyzer.getSignInfo(bArr).getSignsTree();
            this.signDetailsList = new ArrayList();
            this.certDetailsList = new ArrayList();
            this.signDocument = Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr));
            this.dataLocation = obtainDataLocation();
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
        return "XAdES";
    }

    @Override // es.gob.afirma.standalone.signdetails.SignAnalyzer
    public String getDataLocation() {
        return this.dataLocation;
    }

    private void createSignDetails(NodeList nodeList) throws AOInvalidFormatException {
        for (int i = 0; i < nodeList.getLength(); i++) {
            try {
                Element element = (Element) nodeList.item(i);
                Node parentNode = element.getParentNode();
                if (parentNode == null || !AOXAdESSigner.TIMESTAMP_TAG.equals(parentNode.getLocalName())) {
                    String strResolveSignerXAdESFormat = SignatureFormatDetectorXades.resolveSignerXAdESFormat(element);
                    SignDetails signDetailsBuildSignDetails = buildSignDetails(element, strResolveSignerXAdESFormat);
                    boolean z = false;
                    if (SimpleAfirmaMessages.getString("ValidationInfoDialog.22").equals(this.dataLocation)) {
                        z = true;
                    }
                    signDetailsBuildSignDetails.setValidityResult(ValidateXMLSignature.validateSign(element, strResolveSignerXAdESFormat, z));
                    this.signDetailsList.add(signDetailsBuildSignDetails);
                }
            } catch (Exception e) {
                throw new AOInvalidFormatException("No se ha podido cargar el documento XML de firmas", e);
            }
        }
    }

    private static SignDetails buildSignDetails(Element element, String str) throws AOInvalidFormatException {
        String[] claimedRoleMetadata;
        Element element2;
        SignDetails signDetails = new SignDetails();
        signDetails.setSignProfile(str);
        Element signatureMethodElement = XAdESUtil.getSignatureMethodElement(element);
        if (signatureMethodElement != null) {
            String attribute = SIGN_ALGOS_URI.get(signatureMethodElement.getAttribute("Algorithm"));
            if (attribute == null) {
                attribute = signatureMethodElement.getAttribute("Algorithm");
            }
            signDetails.setAlgorithm(attribute);
            signDetails.setSigner(buildCertDetails(element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_X509DATA).item(0)));
            NodeList elementsByTagNameNS = element.getElementsByTagNameNS("*", XAdESConstants.TAG_QUALIFYING_PROPERTIES);
            boolean z = false;
            if (elementsByTagNameNS.getLength() > 0) {
                Element element3 = (Element) elementsByTagNameNS.item(0);
                String namespaceURI = element3.getNamespaceURI();
                for (String str2 : SUPPORTED_XADES_NAMESPACE_URIS) {
                    if (str2.equals(namespaceURI)) {
                        z = true;
                    }
                }
                if (!z) {
                    throw new AOInvalidFormatException("Una de las firmas encontradas en el documento contiene una version inexistente de XAdES");
                }
                signDetails.setSigningTime(obtainSigningTime(element3, namespaceURI));
                NodeList elementsByTagNameNS2 = element3.getElementsByTagNameNS(namespaceURI, XAdESConstants.TAG_SIGNED_DATA_OBJECT_PROPERTIES);
                if (elementsByTagNameNS2.getLength() > 0) {
                    NodeList elementsByTagNameNS3 = ((Element) elementsByTagNameNS2.item(0)).getElementsByTagNameNS(namespaceURI, XAdESConstants.TAG_DATA_OBJECT_FORMAT);
                    ArrayList arrayList = new ArrayList();
                    for (int i = 0; i < elementsByTagNameNS3.getLength(); i++) {
                        DataObjectFormat dataObjectFormat = new DataObjectFormat();
                        Element element4 = (Element) elementsByTagNameNS3.item(i);
                        dataObjectFormat.setIdentifier(element4.getAttribute("ObjectReference"));
                        NodeList elementsByTagNameNS4 = element4.getElementsByTagNameNS(namespaceURI, "Description");
                        if (elementsByTagNameNS4.getLength() > 0) {
                            dataObjectFormat.setDescription(elementsByTagNameNS4.item(0).getTextContent().trim());
                        }
                        dataObjectFormat.setMimeType(element4.getElementsByTagNameNS(namespaceURI, "MimeType").item(0).getTextContent().trim());
                        arrayList.add(dataObjectFormat);
                    }
                    signDetails.setDataObjectFormats(arrayList);
                }
                NodeList elementsByTagNameNS5 = element3.getElementsByTagNameNS(namespaceURI, XAdESConstants.TAG_SIGNATURE_POLICY_IDENTIFIER);
                if (elementsByTagNameNS5 != null && elementsByTagNameNS5.getLength() > 0 && (element2 = (Element) elementsByTagNameNS5.item(0)) != null) {
                    String strTrim = element2.getElementsByTagNameNS(namespaceURI, "Identifier").item(0).getTextContent().trim();
                    Node nodeItem = element2.getElementsByTagNameNS(namespaceURI, "Description").item(0);
                    String strTrim2 = "";
                    if (nodeItem != null) {
                        strTrim2 = nodeItem.getTextContent().trim();
                    }
                    if (SignDetails.POLICY_XADES_AGE_1_9.getPolicyIdentifier().equals(strTrim)) {
                        signDetails.setPolicy(new SignaturePolicy(SimpleAfirmaMessages.getString("PreferencesPanel.73"), SignDetails.POLICY_XADES_AGE_1_9));
                    } else if (SignDetails.POLICY_XADES_AGE_1_8.getPolicyIdentifier().equals(strTrim)) {
                        signDetails.setPolicy(new SignaturePolicy(SimpleAfirmaMessages.getString("PreferencesPanel.25"), SignDetails.POLICY_XADES_AGE_1_8));
                    } else {
                        String namespaceURI2 = element.getNamespaceURI();
                        String strTrim3 = element2.getElementsByTagNameNS(namespaceURI2, "DigestValue").item(0).getTextContent().trim();
                        String attribute2 = ((Element) element2.getElementsByTagNameNS(namespaceURI2, "DigestMethod").item(0)).getAttribute("Algorithm");
                        String strTrim4 = "";
                        if (element2.getElementsByTagNameNS(namespaceURI, XAdESConstants.TAG_SPURI).getLength() > 0) {
                            strTrim4 = element2.getElementsByTagNameNS(namespaceURI, XAdESConstants.TAG_SPURI).item(0).getTextContent().trim();
                        }
                        signDetails.setPolicy(new SignaturePolicy(strTrim2, new AdESPolicy(strTrim, strTrim3, attribute2, strTrim4)));
                    }
                }
                NodeList elementsByTagNameNS6 = element3.getElementsByTagNameNS(namespaceURI, XAdESConstants.TAG_SIGNATURE_PRODUCTION_PLACE);
                Map<String, String> map = new HashMap();
                if (elementsByTagNameNS6.getLength() > 0) {
                    map = getProductionPlaceMetadata(elementsByTagNameNS6, namespaceURI);
                } else {
                    NodeList elementsByTagNameNS7 = element3.getElementsByTagNameNS(namespaceURI, XAdESConstants.TAG_SIGNATURE_PRODUCTION_PLACE_V2);
                    if (elementsByTagNameNS7.getLength() > 0) {
                        map = getProductionPlaceMetadata(elementsByTagNameNS7, namespaceURI);
                    }
                }
                NodeList elementsByTagNameNS8 = element3.getElementsByTagNameNS(namespaceURI, XAdESConstants.TAG_SIGNER_ROLE);
                if (elementsByTagNameNS8.getLength() > 0) {
                    String[] claimedRoleMetadata2 = getClaimedRoleMetadata((Element) elementsByTagNameNS8.item(0), namespaceURI);
                    if (claimedRoleMetadata2 != null) {
                        for (int i2 = 0; i2 < claimedRoleMetadata2.length; i2++) {
                            map.put("claimedRole" + i2, claimedRoleMetadata2[i2]);
                        }
                    }
                } else {
                    NodeList elementsByTagNameNS9 = element3.getElementsByTagNameNS(namespaceURI, XAdESConstants.TAG_SIGNER_ROLE_V2);
                    if (elementsByTagNameNS9.getLength() > 0 && (claimedRoleMetadata = getClaimedRoleMetadata((Element) elementsByTagNameNS9.item(0), namespaceURI)) != null) {
                        for (int i3 = 0; i3 < claimedRoleMetadata.length; i3++) {
                            map.put("claimedRole" + i3, claimedRoleMetadata[i3]);
                        }
                    }
                }
                signDetails.setMetadata(map);
            }
            return signDetails;
        }
        throw new AOInvalidFormatException("El elemento SignatureMethod no existe");
    }

    private static CertificateDetails buildCertDetails(Node node) {
        if (node != null) {
            return new CertificateDetails(Utils.getCertificate((Element) ((Element) node).getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "X509Certificate").item(0)));
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
                strArr[i] = ((Element) elementsByTagNameNS.item(i)).getTextContent().trim();
            }
        }
        return strArr;
    }

    private String obtainDataLocation() {
        Element documentElement = this.signDocument.getDocumentElement();
        Element firstSignatureElement = XAdESUtil.getFirstSignatureElement(this.signDocument.getDocumentElement());
        List<Element> signatureDataReferenceList = XAdESUtil.getSignatureDataReferenceList(firstSignatureElement);
        if (XAdESUtil.isSignatureElementEnveloped(firstSignatureElement, signatureDataReferenceList)) {
            return SimpleAfirmaMessages.getString("ValidationInfoDialog.12");
        }
        if (XAdESUtil.isSignatureWithManifest(signatureDataReferenceList)) {
            return SimpleAfirmaMessages.getString("ValidationInfoDialog.16");
        }
        if (XAdESUtil.isSignatureElementExternallyDetached(signatureDataReferenceList)) {
            return SimpleAfirmaMessages.getString("ValidationInfoDialog.22");
        }
        if (XAdESUtil.isSignatureElementInternallyDetached(documentElement, signatureDataReferenceList)) {
            return SimpleAfirmaMessages.getString("ValidationInfoDialog.14");
        }
        if (XAdESUtil.isSignatureElementEnveloping(firstSignatureElement, signatureDataReferenceList)) {
            return SimpleAfirmaMessages.getString("ValidationInfoDialog.21");
        }
        return ISignatureFormatDetector.FORMAT_UNRECOGNIZED;
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
