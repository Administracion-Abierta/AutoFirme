package org.apache.xml.security.stax.ext;

import java.security.Key;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/XMLSecurityProperties.class */
public class XMLSecurityProperties {
    private final List<InputProcessor> inputProcessorList;
    private boolean skipDocumentEvents;
    private boolean disableSchemaValidation;
    private List<XMLSecurityConstants.Action> actions;
    private X509Certificate encryptionUseThisCertificate;
    private String encryptionSymAlgorithm;
    private String encryptionKeyTransportAlgorithm;
    private String encryptionKeyTransportDigestAlgorithm;
    private String encryptionKeyTransportMGFAlgorithm;
    private byte[] encryptionKeyTransportOAEPParams;
    private final List<SecurePart> encryptionParts;
    private Key encryptionKey;
    private Key encryptionTransportKey;
    private SecurityTokenConstants.KeyIdentifier encryptionKeyIdentifier;
    private String encryptionKeyName;
    private Key decryptionKey;
    private final List<SecurePart> signatureParts;
    private String signatureAlgorithm;
    private String signatureDigestAlgorithm;
    private String signatureCanonicalizationAlgorithm;
    private Key signatureKey;
    private X509Certificate[] signatureCerts;
    private boolean addExcC14NInclusivePrefixes;
    private List<SecurityTokenConstants.KeyIdentifier> signatureKeyIdentifiers;
    private String signatureKeyName;
    private boolean useSingleCert;
    private Key signatureVerificationKey;
    private int signaturePosition;
    private QName idAttributeNS;
    private final Map<String, Key> keyNameMap;
    private boolean signatureGenerateIds;
    private boolean signatureIncludeDigestTransform;
    private QName signaturePositionQName;
    private boolean signaturePositionStart;
    private AlgorithmParameterSpec algorithmParameterSpec;

    public XMLSecurityProperties() {
        this.inputProcessorList = new ArrayList();
        this.skipDocumentEvents = false;
        this.disableSchemaValidation = false;
        this.actions = new ArrayList();
        this.encryptionParts = new LinkedList();
        this.signatureParts = new LinkedList();
        this.addExcC14NInclusivePrefixes = false;
        this.signatureKeyIdentifiers = new ArrayList();
        this.useSingleCert = true;
        this.idAttributeNS = XMLSecurityConstants.ATT_NULL_Id;
        this.keyNameMap = new HashMap();
        this.signatureGenerateIds = true;
        this.signatureIncludeDigestTransform = true;
        this.signaturePositionStart = false;
    }

    protected XMLSecurityProperties(XMLSecurityProperties xmlSecurityProperties) {
        this.inputProcessorList = new ArrayList();
        this.skipDocumentEvents = false;
        this.disableSchemaValidation = false;
        this.actions = new ArrayList();
        this.encryptionParts = new LinkedList();
        this.signatureParts = new LinkedList();
        this.addExcC14NInclusivePrefixes = false;
        this.signatureKeyIdentifiers = new ArrayList();
        this.useSingleCert = true;
        this.idAttributeNS = XMLSecurityConstants.ATT_NULL_Id;
        this.keyNameMap = new HashMap();
        this.signatureGenerateIds = true;
        this.signatureIncludeDigestTransform = true;
        this.signaturePositionStart = false;
        this.inputProcessorList.addAll(xmlSecurityProperties.inputProcessorList);
        this.skipDocumentEvents = xmlSecurityProperties.skipDocumentEvents;
        this.disableSchemaValidation = xmlSecurityProperties.disableSchemaValidation;
        this.actions = xmlSecurityProperties.actions;
        this.encryptionUseThisCertificate = xmlSecurityProperties.encryptionUseThisCertificate;
        this.encryptionSymAlgorithm = xmlSecurityProperties.encryptionSymAlgorithm;
        this.encryptionKeyTransportAlgorithm = xmlSecurityProperties.encryptionKeyTransportAlgorithm;
        this.encryptionKeyTransportDigestAlgorithm = xmlSecurityProperties.encryptionKeyTransportDigestAlgorithm;
        this.encryptionKeyTransportMGFAlgorithm = xmlSecurityProperties.encryptionKeyTransportMGFAlgorithm;
        this.encryptionKeyTransportOAEPParams = xmlSecurityProperties.encryptionKeyTransportOAEPParams;
        this.encryptionParts.addAll(xmlSecurityProperties.encryptionParts);
        this.encryptionKey = xmlSecurityProperties.encryptionKey;
        this.encryptionTransportKey = xmlSecurityProperties.encryptionTransportKey;
        this.encryptionKeyIdentifier = xmlSecurityProperties.encryptionKeyIdentifier;
        this.decryptionKey = xmlSecurityProperties.decryptionKey;
        this.signatureParts.addAll(xmlSecurityProperties.signatureParts);
        this.signatureAlgorithm = xmlSecurityProperties.signatureAlgorithm;
        this.signatureDigestAlgorithm = xmlSecurityProperties.signatureDigestAlgorithm;
        this.signatureCanonicalizationAlgorithm = xmlSecurityProperties.signatureCanonicalizationAlgorithm;
        this.signatureKey = xmlSecurityProperties.signatureKey;
        this.signatureCerts = xmlSecurityProperties.signatureCerts;
        this.addExcC14NInclusivePrefixes = xmlSecurityProperties.addExcC14NInclusivePrefixes;
        this.signatureKeyIdentifiers.addAll(xmlSecurityProperties.signatureKeyIdentifiers);
        this.useSingleCert = xmlSecurityProperties.useSingleCert;
        this.signatureVerificationKey = xmlSecurityProperties.signatureVerificationKey;
        this.signaturePosition = xmlSecurityProperties.signaturePosition;
        this.idAttributeNS = xmlSecurityProperties.idAttributeNS;
        this.signatureKeyName = xmlSecurityProperties.signatureKeyName;
        this.encryptionKeyName = xmlSecurityProperties.encryptionKeyName;
        this.keyNameMap.putAll(xmlSecurityProperties.keyNameMap);
        this.signatureGenerateIds = xmlSecurityProperties.signatureGenerateIds;
        this.signatureIncludeDigestTransform = xmlSecurityProperties.signatureIncludeDigestTransform;
        this.signaturePositionQName = xmlSecurityProperties.signaturePositionQName;
        this.signaturePositionStart = xmlSecurityProperties.signaturePositionStart;
        this.algorithmParameterSpec = xmlSecurityProperties.algorithmParameterSpec;
    }

    public boolean isSignaturePositionStart() {
        return this.signaturePositionStart;
    }

    public void setSignaturePositionStart(boolean signaturePositionStart) {
        this.signaturePositionStart = signaturePositionStart;
    }

    @Deprecated
    public SecurityTokenConstants.KeyIdentifier getSignatureKeyIdentifier() {
        if (this.signatureKeyIdentifiers.isEmpty()) {
            return null;
        }
        return this.signatureKeyIdentifiers.get(0);
    }

    public List<SecurityTokenConstants.KeyIdentifier> getSignatureKeyIdentifiers() {
        return new ArrayList(this.signatureKeyIdentifiers);
    }

    public void setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier signatureKeyIdentifier) {
        this.signatureKeyIdentifiers.clear();
        this.signatureKeyIdentifiers.add(signatureKeyIdentifier);
    }

    public void setSignatureKeyIdentifiers(List<SecurityTokenConstants.KeyIdentifier> signatureKeyIdentifiers) {
        this.signatureKeyIdentifiers.clear();
        this.signatureKeyIdentifiers.addAll(signatureKeyIdentifiers);
    }

    public int getSignaturePosition() {
        return this.signaturePosition;
    }

    public void setSignaturePosition(int signaturePosition) {
        this.signaturePosition = signaturePosition;
    }

    public QName getIdAttributeNS() {
        return this.idAttributeNS;
    }

    public void setIdAttributeNS(QName idAttributeNS) {
        this.idAttributeNS = idAttributeNS;
    }

    public SecurityTokenConstants.KeyIdentifier getEncryptionKeyIdentifier() {
        return this.encryptionKeyIdentifier;
    }

    public void setEncryptionKeyIdentifier(SecurityTokenConstants.KeyIdentifier encryptionKeyIdentifier) {
        this.encryptionKeyIdentifier = encryptionKeyIdentifier;
    }

    public void addInputProcessor(InputProcessor inputProcessor) {
        this.inputProcessorList.add(inputProcessor);
    }

    public List<InputProcessor> getInputProcessorList() {
        return this.inputProcessorList;
    }

    public void setDecryptionKey(Key decryptionKey) {
        this.decryptionKey = decryptionKey;
    }

    public Key getDecryptionKey() {
        return this.decryptionKey;
    }

    public void setEncryptionTransportKey(Key encryptionTransportKey) {
        this.encryptionTransportKey = encryptionTransportKey;
    }

    public Key getEncryptionTransportKey() {
        return this.encryptionTransportKey;
    }

    public void setEncryptionKey(Key encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public Key getEncryptionKey() {
        return this.encryptionKey;
    }

    public void addEncryptionPart(SecurePart securePart) {
        this.encryptionParts.add(securePart);
    }

    public List<SecurePart> getEncryptionSecureParts() {
        return this.encryptionParts;
    }

    public String getEncryptionSymAlgorithm() {
        return this.encryptionSymAlgorithm;
    }

    public void setEncryptionSymAlgorithm(String encryptionSymAlgorithm) {
        this.encryptionSymAlgorithm = encryptionSymAlgorithm;
    }

    public String getEncryptionKeyTransportAlgorithm() {
        return this.encryptionKeyTransportAlgorithm;
    }

    public void setEncryptionKeyTransportAlgorithm(String encryptionKeyTransportAlgorithm) {
        this.encryptionKeyTransportAlgorithm = encryptionKeyTransportAlgorithm;
    }

    public String getEncryptionKeyTransportDigestAlgorithm() {
        return this.encryptionKeyTransportDigestAlgorithm;
    }

    public void setEncryptionKeyTransportDigestAlgorithm(String encryptionKeyTransportDigestAlgorithm) {
        this.encryptionKeyTransportDigestAlgorithm = encryptionKeyTransportDigestAlgorithm;
    }

    public String getEncryptionKeyTransportMGFAlgorithm() {
        return this.encryptionKeyTransportMGFAlgorithm;
    }

    public void setEncryptionKeyTransportMGFAlgorithm(String encryptionKeyTransportMGFAlgorithm) {
        this.encryptionKeyTransportMGFAlgorithm = encryptionKeyTransportMGFAlgorithm;
    }

    public byte[] getEncryptionKeyTransportOAEPParams() {
        return this.encryptionKeyTransportOAEPParams;
    }

    public void setEncryptionKeyTransportOAEPParams(byte[] encryptionKeyTransportOAEPParams) {
        this.encryptionKeyTransportOAEPParams = encryptionKeyTransportOAEPParams;
    }

    public X509Certificate getEncryptionUseThisCertificate() {
        return this.encryptionUseThisCertificate;
    }

    public void setEncryptionUseThisCertificate(X509Certificate encryptionUseThisCertificate) {
        this.encryptionUseThisCertificate = encryptionUseThisCertificate;
    }

    public X509Certificate[] getSignatureCerts() {
        return this.signatureCerts;
    }

    public void setSignatureCerts(X509Certificate[] signatureCerts) {
        this.signatureCerts = signatureCerts;
    }

    public void addSignaturePart(SecurePart securePart) {
        this.signatureParts.add(securePart);
    }

    public List<SecurePart> getSignatureSecureParts() {
        return this.signatureParts;
    }

    public String getSignatureAlgorithm() {
        return this.signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public String getSignatureDigestAlgorithm() {
        return this.signatureDigestAlgorithm;
    }

    public void setSignatureDigestAlgorithm(String signatureDigestAlgorithm) {
        this.signatureDigestAlgorithm = signatureDigestAlgorithm;
    }

    public void setSignatureKey(Key signatureKey) {
        this.signatureKey = signatureKey;
    }

    public Key getSignatureKey() {
        return this.signatureKey;
    }

    public boolean isUseSingleCert() {
        return this.useSingleCert;
    }

    public void setUseSingleCert(boolean useSingleCert) {
        this.useSingleCert = useSingleCert;
    }

    public boolean isAddExcC14NInclusivePrefixes() {
        return this.addExcC14NInclusivePrefixes;
    }

    public void setAddExcC14NInclusivePrefixes(boolean addExcC14NInclusivePrefixes) {
        this.addExcC14NInclusivePrefixes = addExcC14NInclusivePrefixes;
    }

    public List<XMLSecurityConstants.Action> getActions() {
        return this.actions;
    }

    public void setActions(List<XMLSecurityConstants.Action> actions) {
        this.actions = actions;
    }

    public void addAction(XMLSecurityConstants.Action action) {
        if (this.actions == null) {
            this.actions = new ArrayList();
        }
        this.actions.add(action);
    }

    public String getSignatureCanonicalizationAlgorithm() {
        return this.signatureCanonicalizationAlgorithm;
    }

    public void setSignatureCanonicalizationAlgorithm(String signatureCanonicalizationAlgorithm) {
        this.signatureCanonicalizationAlgorithm = signatureCanonicalizationAlgorithm;
    }

    public Key getSignatureVerificationKey() {
        return this.signatureVerificationKey;
    }

    public void setSignatureVerificationKey(Key signatureVerificationKey) {
        this.signatureVerificationKey = signatureVerificationKey;
    }

    public boolean isSkipDocumentEvents() {
        return this.skipDocumentEvents;
    }

    public void setSkipDocumentEvents(boolean skipDocumentEvents) {
        this.skipDocumentEvents = skipDocumentEvents;
    }

    public boolean isDisableSchemaValidation() {
        return this.disableSchemaValidation;
    }

    public void setDisableSchemaValidation(boolean disableSchemaValidation) {
        this.disableSchemaValidation = disableSchemaValidation;
    }

    public String getSignatureKeyName() {
        return this.signatureKeyName;
    }

    public void setSignatureKeyName(String signatureKeyName) {
        this.signatureKeyName = signatureKeyName;
    }

    public String getEncryptionKeyName() {
        return this.encryptionKeyName;
    }

    public void setEncryptionKeyName(String encryptionKeyName) {
        this.encryptionKeyName = encryptionKeyName;
    }

    public Map<String, Key> getKeyNameMap() {
        return Collections.unmodifiableMap(this.keyNameMap);
    }

    public void addKeyNameMapping(String keyname, Key key) {
        this.keyNameMap.put(keyname, key);
    }

    public boolean isSignatureGenerateIds() {
        return this.signatureGenerateIds;
    }

    public void setSignatureGenerateIds(boolean signatureGenerateIds) {
        this.signatureGenerateIds = signatureGenerateIds;
    }

    public boolean isSignatureIncludeDigestTransform() {
        return this.signatureIncludeDigestTransform;
    }

    public void setSignatureIncludeDigestTransform(boolean signatureIncludeDigestTransform) {
        this.signatureIncludeDigestTransform = signatureIncludeDigestTransform;
    }

    public QName getSignaturePositionQName() {
        return this.signaturePositionQName;
    }

    public void setSignaturePositionQName(QName signaturePositionQName) {
        this.signaturePositionQName = signaturePositionQName;
    }

    public AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return this.algorithmParameterSpec;
    }

    public void setAlgorithmParameterSpec(AlgorithmParameterSpec algorithmParameterSpec) {
        this.algorithmParameterSpec = algorithmParameterSpec;
    }
}
