package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESConstants;
import es.gob.afirma.signvalidation.ISignatureFormatDetector;
import es.gob.afirma.standalone.CommandLineParameters;
import es.uji.crypto.xades.jxades.util.ObjectId;
import es.uji.crypto.xades.jxades.util.OccursRequirement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/XAdES.class */
public enum XAdES {
    BES(ISignatureFormatDetector.FORMAT_XADES_BES, "4.4.1", "Basic Electronic Signature"),
    EPES(ISignatureFormatDetector.FORMAT_XADES_EPES, "4.4.2", "Explicit Policy Electronic Signatures"),
    T(ISignatureFormatDetector.FORMAT_XADES_T, "4.4.3.1", "Electronic Signature with Time"),
    C(ISignatureFormatDetector.FORMAT_XADES_C, "4.4.3.2", "Electronic Signature with Complete Validation Data References"),
    X("XAdES-X", "B.1", "Extended Signatures with Time Forms"),
    X_L("XAdES-X-L", "B.2", "Extended Long Electronic Signatures with Time"),
    A(ISignatureFormatDetector.FORMAT_XADES_A, "B.3", "Archival Electronic Signatures"),
    B_LEVEL("B-B-LEVEL", "ETSI EN 319 132-1 V1.1.1 6.3", "XAdES BASELINE B-LEVEL"),
    T_LEVEL("B-T-LEVEL", "ETSI EN 319 132-1 V1.1.1 6.3", "XAdES BASELINE T-LEVEL");

    private String nickname;
    private String contentsId;
    private String title;
    public static final XadesElementsEnumeration XAdES_ELEMENTS = new XadesElementsEnumeration(Element.values());
    public static final XadesElementsEnumeration XAdES_BES_ELEMENTS = new XadesElementsEnumeration(Element.values(), BES);
    public static final XadesElementsEnumeration XAdES_EPES_ELEMENTS = new XadesElementsEnumeration(Element.values(), EPES);
    public static final XadesElementsEnumeration XAdES_T_ELEMENTS = new XadesElementsEnumeration(Element.values(), T);
    public static final XadesElementsEnumeration XAdES_C_ELEMENTS = new XadesElementsEnumeration(Element.values(), C);
    public static final XadesElementsEnumeration XAdES_X_ELEMENTS = new XadesElementsEnumeration(Element.values(), X);
    public static final XadesElementsEnumeration XAdES_X_L_ELEMENTS = new XadesElementsEnumeration(Element.values(), X_L);
    public static final XadesElementsEnumeration XAdES_A_ELEMENTS = new XadesElementsEnumeration(Element.values(), A);
    public static final XadesElementsEnumeration XAdES_B_LEVEL_ELEMENTS = new XadesElementsEnumeration(Element.values(), B_LEVEL);
    public static final XadesElementsEnumeration XAdES_T_LEVEL_ELEMENTS = new XadesElementsEnumeration(Element.values(), T_LEVEL);

    XAdES(String nickname, String contentsId, String title) {
        this.nickname = nickname;
        this.contentsId = contentsId;
        this.title = title;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getContentsId() {
        return this.contentsId;
    }

    public String getTitle() {
        return this.title;
    }

    /* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/XAdES$Element.class */
    public enum Element implements XadesElement {
        OBJECT(null, Constants._TAG_OBJECT),
        QUALIFYING_PROPERTIES(OBJECT, XAdESConstants.TAG_QUALIFYING_PROPERTIES),
        SIGNED_PROPERTIES(QUALIFYING_PROPERTIES, XAdESConstants.TAG_SIGNED_PROPERTIES),
        SIGNED_SIGNATURE_PROPERTIES(SIGNED_PROPERTIES, XAdESConstants.TAG_SIGNED_SIGNATURE_PROPERTIES),
        SIGNING_TIME(new XAdES[]{XAdES.BES, XAdES.B_LEVEL}, SIGNED_SIGNATURE_PROPERTIES, XAdESConstants.TAG_SIGNING_TIME, OccursRequirement.ZERO_OR_ONE),
        SIGNING_CERTIFICATE(XAdES.BES, SIGNED_SIGNATURE_PROPERTIES, XAdESConstants.TAG_SIGNING_CERTIFICATE, OccursRequirement.ZERO_OR_ONE),
        SIGNATURE_POLICY_IDENTIFIER(new XAdES[]{XAdES.EPES, XAdES.B_LEVEL}, SIGNED_SIGNATURE_PROPERTIES, XAdESConstants.TAG_SIGNATURE_POLICY_IDENTIFIER, OccursRequirement.EXACTLY_ONE),
        SIGNATURE_PRODUCTION_PLACE(XAdES.BES, SIGNED_SIGNATURE_PROPERTIES, XAdESConstants.TAG_SIGNATURE_PRODUCTION_PLACE, OccursRequirement.ZERO_OR_ONE),
        SIGNER_ROLE(XAdES.BES, SIGNED_SIGNATURE_PROPERTIES, XAdESConstants.TAG_SIGNER_ROLE, OccursRequirement.ZERO_OR_ONE),
        CLAIMED_ROLES(new XAdES[]{XAdES.BES, XAdES.B_LEVEL}, SIGNER_ROLE, "ClaimedRoles", OccursRequirement.ZERO_OR_MORE),
        CERTIFIED_ROLES(XAdES.BES, SIGNER_ROLE, "CertifiedRoles", OccursRequirement.ZERO_OR_MORE),
        SIGNER(new XAdES[]{XAdES.BES, XAdES.B_LEVEL}, SIGNED_SIGNATURE_PROPERTIES, "Signer", OccursRequirement.ZERO_OR_ONE),
        SIGNER_DETAILS(new XAdES[]{XAdES.BES, XAdES.B_LEVEL}, SIGNED_SIGNATURE_PROPERTIES, "SignerDetails", OccursRequirement.ZERO_OR_ONE),
        SIGNED_DATA_OBJECT_PROPERTIES(SIGNED_PROPERTIES, XAdESConstants.TAG_SIGNED_DATA_OBJECT_PROPERTIES),
        DATA_OBJECT_FORMATS(new XAdES[]{XAdES.BES, XAdES.B_LEVEL}, SIGNED_DATA_OBJECT_PROPERTIES, XAdESConstants.TAG_DATA_OBJECT_FORMAT, OccursRequirement.ZERO_OR_MORE),
        COMMITMENT_TYPE_INDICATIONS(new XAdES[]{XAdES.BES, XAdES.B_LEVEL}, SIGNED_DATA_OBJECT_PROPERTIES, "CommitmentTypeIndication", OccursRequirement.ZERO_OR_MORE),
        ALL_DATA_OBJECTS_TIMESTAMPS(new XAdES[]{XAdES.BES, XAdES.B_LEVEL}, SIGNED_DATA_OBJECT_PROPERTIES, "AllDataObjectsTimeStamp", OccursRequirement.ZERO_OR_MORE),
        INDIVIDUAL_DATA_OBJECTS_TIMESTAMPS(new XAdES[]{XAdES.BES, XAdES.B_LEVEL}, SIGNED_DATA_OBJECT_PROPERTIES, "IndividualDataObjectsTimeStamp", OccursRequirement.ZERO_OR_MORE),
        UNSIGNED_PROPERTIES(QUALIFYING_PROPERTIES, XAdESConstants.TAG_UNSIGNED_PROPERTIES),
        UNSIGNED_SIGNATURE_PROPERTIES(UNSIGNED_PROPERTIES, XAdESConstants.TAG_UNSIGNED_SIGNATURE_PROPERTIES),
        COUNTER_SIGNATURES(new XAdES[]{XAdES.BES, XAdES.B_LEVEL}, UNSIGNED_SIGNATURE_PROPERTIES, "CounterSignature", OccursRequirement.ZERO_OR_MORE),
        SIGNATURE_TIME_STAMP(new XAdES[]{XAdES.T, XAdES.T_LEVEL}, UNSIGNED_SIGNATURE_PROPERTIES, XAdESConstants.TAG_SIGNATURE_TIMESTAMP, OccursRequirement.ONE_OR_MORE),
        COMPLETE_CERTIFICATE_REFS(XAdES.C, UNSIGNED_SIGNATURE_PROPERTIES, XAdESConstants.TAG_COMPLETE_CERTIFICATE_REFS, OccursRequirement.EXACTLY_ONE),
        COMPLETE_REVOCATION_REFS(XAdES.C, UNSIGNED_SIGNATURE_PROPERTIES, XAdESConstants.TAG_COMPLETE_REVOCATION_REFS, OccursRequirement.EXACTLY_ONE),
        ATTRIBUTE_CERTIFICATE_REFS(XAdES.C, UNSIGNED_SIGNATURE_PROPERTIES, XAdESConstants.TAG_ATTRIBUTE_CERTIFICATE_REFS, OccursRequirement.ZERO_OR_ONE),
        ATTRIBUTE_REVOCATION_REFS(XAdES.C, UNSIGNED_SIGNATURE_PROPERTIES, XAdESConstants.TAG_COMPLETE_CERTIFICATE_REFS, OccursRequirement.ZERO_OR_ONE),
        QUALIFYING_PROPERTIES_REFERENCE(OBJECT, XAdESConstants.TAG_QUALIFYING_PROPERTIES_REFERENCE),
        SIGNING_CERTIFICATE_V2(XAdES.B_LEVEL, SIGNED_SIGNATURE_PROPERTIES, XAdESConstants.TAG_SIGNING_CERTIFICATE_V2, OccursRequirement.EXACTLY_ONE),
        SIGNATURE_PRODUCTION_PLACE_V2(XAdES.B_LEVEL, SIGNED_SIGNATURE_PROPERTIES, XAdESConstants.TAG_SIGNATURE_PRODUCTION_PLACE_V2, OccursRequirement.ZERO_OR_ONE),
        SIGNER_ROLE_V2(XAdES.B_LEVEL, SIGNED_SIGNATURE_PROPERTIES, XAdESConstants.TAG_SIGNER_ROLE_V2, OccursRequirement.ZERO_OR_ONE),
        CERTIFIED_ROLES_V2(XAdES.B_LEVEL, SIGNER_ROLE_V2, "CertifiedRolesV2", OccursRequirement.ZERO_OR_MORE),
        SIGNED_ASSERTIONS(XAdES.B_LEVEL, SIGNER_ROLE_V2, "SignedAssertions", OccursRequirement.ZERO_OR_MORE);

        private XAdES[] xades;
        private ObjectId objectId;
        private String elementName;
        private OccursRequirement occursRequirement;
        private XadesElement parent;

        Element(XadesElement parent, String elementName) {
            this((XAdES[]) null, parent, elementName, OccursRequirement.EXACTLY_ONE);
        }

        Element(XAdES xades, XadesElement parent, String elementName, OccursRequirement occursRequirement) {
            this(xades != null ? new XAdES[]{xades} : (XAdES[]) null, parent, elementName, OccursRequirement.EXACTLY_ONE);
        }

        Element(XAdES[] xades, XadesElement parent, String elementName, OccursRequirement occursRequirement) {
            this.xades = xades;
            this.parent = parent;
            this.elementName = elementName;
            this.occursRequirement = OccursRequirement.EXACTLY_ONE;
        }

        @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesElement
        public XAdES[] getXAdES() {
            if (this.xades == null) {
                return null;
            }
            return (XAdES[]) this.xades.clone();
        }

        @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesElement
        public ObjectId getObjectId() {
            int[] components;
            if (this.objectId == null) {
                if (this.parent != null) {
                    int[] parentComps = this.parent.getObjectId().getComponents();
                    int size = parentComps.length;
                    components = new int[size + 1];
                    System.arraycopy(parentComps, 0, components, 0, size);
                    components[size] = ordinal() + 1;
                } else {
                    components = new int[]{0, ordinal() + 1};
                }
                this.objectId = new ObjectId(components);
            }
            return this.objectId;
        }

        @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesElement
        public String getElementName() {
            return this.elementName;
        }

        @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesElement
        public OccursRequirement getOccursRequirement() {
            return this.occursRequirement;
        }

        @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesElement
        public XadesElement getParent() {
            return this.parent;
        }

        @Override // java.lang.Enum
        public String toString() {
            return "[" + getObjectId() + "] " + getElementName();
        }
    }

    public static XAdESBase newInstance(XAdES xades, org.w3c.dom.Element baseElement) {
        return newInstance(xades, "http://uri.etsi.org/01903/v1.3.2#", CommandLineParameters.FORMAT_XADES, "dsign", "http://www.w3.org/2000/09/xmldsig#sha1", baseElement.getOwnerDocument(), baseElement);
    }

    public static XAdESBase newInstance(XAdES xades) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            return newInstance(xades, "http://uri.etsi.org/01903/v1.3.2#", CommandLineParameters.FORMAT_XADES, "dsign", "http://www.w3.org/2000/09/xmldsig#sha1", document, null);
        } catch (ParserConfigurationException e) {
            return null;
        }
    }

    public static XAdESBase newInstance(XAdES xades, String xadesNamespace, String xadesPrefix, String xmlSignaturePrefix, String digestMethod, Document document, org.w3c.dom.Element baseElement) {
        setDefinedIdAttributesAsDOMIds(document);
        if (BES.equals(xades)) {
            return new BasicXAdESImpl(document, baseElement, false, xadesPrefix, xadesNamespace, xmlSignaturePrefix, digestMethod);
        }
        if (EPES.equals(xades)) {
            return new ExplicitPolicyXAdESImpl(document, baseElement, false, xadesPrefix, xadesNamespace, xmlSignaturePrefix, digestMethod);
        }
        if (T.equals(xades)) {
            return new TimestampXAdESImpl(document, baseElement, false, xadesPrefix, xadesNamespace, xmlSignaturePrefix, digestMethod);
        }
        if (C.equals(xades)) {
            return new CompleteValidationXAdESImpl(document, baseElement, false, xadesPrefix, xadesNamespace, xmlSignaturePrefix, digestMethod);
        }
        if (X.equals(xades)) {
            return new ExtendedXAdESImpl(document, baseElement, false, xadesPrefix, xadesNamespace, xmlSignaturePrefix, digestMethod);
        }
        if (X_L.equals(xades)) {
            return new ExtendedLongXAdESImpl(document, baseElement, false, xadesPrefix, xadesNamespace, xmlSignaturePrefix, digestMethod);
        }
        if (A.equals(xades)) {
            return new ArchivalXAdESImpl(document, baseElement, false, xadesPrefix, xadesNamespace, xmlSignaturePrefix, digestMethod);
        }
        if (B_LEVEL.equals(xades)) {
            return new BLevelXAdESImpl(document, baseElement, false, xadesPrefix, xadesNamespace, xmlSignaturePrefix, digestMethod);
        }
        if (T_LEVEL.equals(xades)) {
            return new TLevelXAdESImpl(document, baseElement, false, xadesPrefix, xadesNamespace, xmlSignaturePrefix, digestMethod);
        }
        throw new IllegalArgumentException("Unknown XAdES type: " + xades);
    }

    private static void setDefinedIdAttributesAsDOMIds(Document document) {
        NodeList nodes = document.getElementsByTagName("*");
        for (int i = 0; i < nodes.getLength(); i++) {
            org.w3c.dom.Element node = (org.w3c.dom.Element) nodes.item(i);
            if (node.getAttribute("Id") != null && !node.getAttribute("Id").isEmpty()) {
                node.setIdAttributeNS(null, "Id", true);
            }
        }
    }
}
