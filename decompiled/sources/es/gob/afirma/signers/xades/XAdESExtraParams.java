package es.gob.afirma.signers.xades;

import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xades/XAdESExtraParams.class */
public final class XAdESExtraParams {
    public static final String KEEP_KEYINFO_UNSIGNED = "keepKeyInfoUnsigned";
    public static final String URI = "uri";
    public static final String URI_PREFIX = "uri";
    public static final String MD_PREFIX = "md";
    public static final String AVOID_XPATH_EXTRA_TRANSFORMS_ON_ENVELOPED = "avoidXpathExtraTransformsOnEnveloped";
    public static final String INCLUDE_ONLY_SIGNNING_CERTIFICATE = "includeOnlySignningCertificate";
    public static final String USE_MANIFEST = "useManifest";
    public static final String INSERT_ENVELOPED_SIGNATURE_ON_NODE_BY_XPATH = "insertEnvelopedSignatureOnNodeByXPath";
    public static final String NODE_TOSIGN = "nodeToSign";
    public static final String AVOID_ENVELOPED_TRANSFORM_WHEN_SIGNING_NODE = "avoidEnvelopedTransformWhenSigningNode";
    public static final String FORMAT = "format";
    public static final String PROFILE = "profile";
    public static final String REFERENCES_DIGEST_METHOD = "referencesDigestMethod";
    public static final String CANONICALIZATION_ALGORITHM = "canonicalizationAlgorithm";
    public static final String XADES_NAMESPACE = "xadesNamespace";
    public static final String SIGNED_PROPERTIES_TYPE_URL = "signedPropertiesTypeUrl";
    public static final String IGNORE_STYLE_SHEETS = "ignoreStyleSheets";
    public static final String AVOID_BASE64_TRANSFORMS = "avoidBase64Transforms";
    public static final String HEADLESS = "headless";
    public static final String ADD_KEY_INFO_KEY_VALUE = "addKeyInfoKeyValue";
    public static final String ADD_KEY_INFO_KEY_NAME = "addKeyInfoKeyName";
    public static final String ADD_KEY_INFO_X509_ISSUER_SERIAL = "addKeyInfoX509IssuerSerial";
    public static final String PRECALCULATED_HASH_ALGORITHM = "precalculatedHashAlgorithm";
    public static final String FACTURAE_SIGN = "facturaeSign";
    public static final String CONTENT_MIME_TYPE = "mimeType";
    public static final String CONTENT_TYPE_OID = "contentTypeOid";
    public static final String CONTENT_ENCODING = "encoding";
    public static final String CONTENT_MIME_TYPE_PREFIX = "mimeType";
    static final String CONTENT_TYPE_OID_PREFIX = "contentTypeOid";
    public static final String CONTENT_ENCODING_PREFIX = "encoding";
    public static final String OUTPUT_XML_ENCODING = "outputXmlEncoding";
    public static final String POLICY_IDENTIFIER = "policyIdentifier";
    public static final String POLICY_IDENTIFIER_HASH = "policyIdentifierHash";
    public static final String POLICY_IDENTIFIER_HASH_ALGORITHM = "policyIdentifierHashAlgorithm";
    public static final String POLICY_DESCRIPTION = "policyDescription";
    public static final String POLICY_QUALIFIER = "policyQualifier";
    public static final String SIGNER_CLAIMED_ROLES = "signerClaimedRoles";
    public static final String SIGNATURE_PRODUCTION_CITY = "signatureProductionCity";
    public static final String SIGNATURE_PRODUCCTION_STREET_ADDRESS = "signatureProductionStreetAddress";
    public static final String SIGNATURE_PRODUCTION_PROVINCE = "signatureProductionProvince";
    public static final String SIGNATURE_PRODUCTION_POSTAL_CODE = "signatureProductionPostalCode";
    public static final String SIGNATURE_PRODUCTION_COUNTRY = "signatureProductionCountry";
    public static final String COMMITMENT_TYPE_INDICATIONS = "commitmentTypeIndications";
    public static final String COMMITMENT_TYPE_INDICATION_PREFIX = "commitmentTypeIndication";
    public static final String COMMITMENT_TYPE_INDICATION_IDENTIFIER = "Identifier";
    public static final String COMMITMENT_TYPE_INDICATION_DESCRIPTION = "Description";
    public static final String COMMITMENT_TYPE_INDICATION_DOCUMENTATION_REFERENCE = "DocumentationReferences";
    public static final String COMMITMENT_TYPE_INDICATION_QUALIFIERS = "CommitmentTypeQualifiers";
    private static final String COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_ORIGIN = "http://uri.etsi.org/01903/v1.2.2#ProofOfOrigin";
    private static final String COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_RECEIPT = "http://uri.etsi.org/01903/v1.2.2#ProofOfReceipt";
    private static final String COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_DELIVERY = "http://uri.etsi.org/01903/v1.2.2#ProofOfDelivery";
    private static final String COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_SENDER = "http://uri.etsi.org/01903/v1.2.2#ProofOfSender";
    private static final String COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_APPROVAL = "http://uri.etsi.org/01903/v1.2.2#ProofOfApproval";
    private static final String COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_CREATION = "http://uri.etsi.org/01903/v1.2.2#ProofOfCreation";
    static final Map<String, String> COMMITMENT_TYPE_IDENTIFIERS = new HashMap(6);

    @Deprecated
    static final String MODE = "mode";
    static final String BATCH_SIGNATURE_ID = "SignatureId";
    public static final String ROOT_XML_NODE_NAME = "RootXmlNodeName";
    public static final String ROOT_XML_NODE_NAMESPACE = "RootXmlNodeNamespace";
    public static final String ROOT_XML_NODE_NAMESPACE_PREFIX = "RootXmlNodeNamespacePrefix";
    public static final String INTERNAL_VALIDATE_PKCS1 = "validatePkcs1";
    public static final String CONFIRM_DIFFERENT_PROFILE = "confirmDifferentProfile";
    public static final String ALLOW_SIGN_LTS_SIGNATURES = "allowSignLTSignature";
    public static final String AVOID_AGE_POLICY_INCOMPATIBILITIES = "avoidAGEPolicyIncompatibilities";

    static {
        COMMITMENT_TYPE_IDENTIFIERS.put("1", COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_ORIGIN);
        COMMITMENT_TYPE_IDENTIFIERS.put("2", COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_RECEIPT);
        COMMITMENT_TYPE_IDENTIFIERS.put("3", COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_DELIVERY);
        COMMITMENT_TYPE_IDENTIFIERS.put(TlbConst.TYPELIB_MINOR_VERSION_WORD, COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_SENDER);
        COMMITMENT_TYPE_IDENTIFIERS.put(TlbConst.TYPELIB_MINOR_VERSION_OFFICE, COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_APPROVAL);
        COMMITMENT_TYPE_IDENTIFIERS.put("6", COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_CREATION);
    }

    private XAdESExtraParams() {
    }
}
