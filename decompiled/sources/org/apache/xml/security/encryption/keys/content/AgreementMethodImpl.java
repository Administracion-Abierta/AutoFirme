package org.apache.xml.security.encryption.keys.content;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.xml.security.encryption.AgreementMethod;
import org.apache.xml.security.encryption.KeyDerivationMethod;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.encryption.keys.OriginatorKeyInfo;
import org.apache.xml.security.encryption.keys.RecipientKeyInfo;
import org.apache.xml.security.encryption.keys.content.derivedKey.ConcatKDFParamsImpl;
import org.apache.xml.security.encryption.keys.content.derivedKey.HKDFParamsImpl;
import org.apache.xml.security.encryption.keys.content.derivedKey.KDFParams;
import org.apache.xml.security.encryption.keys.content.derivedKey.KeyDerivationMethodImpl;
import org.apache.xml.security.encryption.params.ConcatKDFParams;
import org.apache.xml.security.encryption.params.HKDFParams;
import org.apache.xml.security.encryption.params.KeyAgreementParameters;
import org.apache.xml.security.encryption.params.KeyDerivationParameters;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.KeyInfoContent;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.EncryptionElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/encryption/keys/content/AgreementMethodImpl.class */
public class AgreementMethodImpl extends EncryptionElementProxy implements KeyInfoContent, AgreementMethod {
    private static final Logger LOG = LoggerFactory.getLogger(AgreementMethodImpl.class.getName());
    private byte[] kaNonce;
    private List<Element> agreementMethodInformation;
    private KeyDerivationMethod keyDerivationMethod;
    private OriginatorKeyInfo originatorKeyInfo;
    private RecipientKeyInfo recipientKeyInfo;
    private String algorithmURI;

    public AgreementMethodImpl(Document doc, KeyAgreementParameters keyAgreementParameter) throws XMLEncryptionException {
        this(doc, keyAgreementParameter.getKeyAgreementAlgorithm());
        if (keyAgreementParameter.getKeyDerivationParameter() != null) {
            KeyDerivationMethod keyDerivationMethod = createKeyDerivationMethod(keyAgreementParameter);
            setKeyDerivationMethod(keyDerivationMethod);
        }
        if (keyAgreementParameter.getOriginatorPublicKey() != null) {
            setOriginatorPublicKey(keyAgreementParameter.getOriginatorPublicKey());
        }
        RecipientKeyInfo recipientKeyInfo = new RecipientKeyInfo(getDocument());
        setRecipientKeyInfo(recipientKeyInfo);
    }

    public AgreementMethodImpl(Document doc, String algorithm) {
        super(doc);
        this.agreementMethodInformation = new LinkedList();
        try {
            URI tmpAlgorithm = new URI(algorithm);
            this.algorithmURI = tmpAlgorithm.toString();
            setLocalAttribute("Algorithm", this.algorithmURI);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Algorithm [" + algorithm + "] is not URI ", ex);
        }
    }

    public AgreementMethodImpl(Element element) throws XMLSecurityException {
        super(element, "http://www.w3.org/2001/04/xmlenc#");
    }

    @Override // org.apache.xml.security.encryption.AgreementMethod
    public byte[] getKANonce() {
        return this.kaNonce;
    }

    @Override // org.apache.xml.security.encryption.AgreementMethod
    public void setKANonce(byte[] kanonce) {
        this.kaNonce = kanonce;
    }

    @Override // org.apache.xml.security.encryption.AgreementMethod
    public Iterator<Element> getAgreementMethodInformation() {
        return this.agreementMethodInformation.iterator();
    }

    @Override // org.apache.xml.security.encryption.AgreementMethod
    public void addAgreementMethodInformation(Element info) {
        this.agreementMethodInformation.add(info);
    }

    @Override // org.apache.xml.security.encryption.AgreementMethod
    public void removeAgreementMethodInformation(Element info) {
        this.agreementMethodInformation.remove(info);
    }

    @Override // org.apache.xml.security.encryption.AgreementMethod
    public KeyDerivationMethod getKeyDerivationMethod() throws XMLSecurityException {
        if (this.keyDerivationMethod != null) {
            LOG.debug("Returning cached KeyDerivationMethod");
            return this.keyDerivationMethod;
        }
        Element keyDerivationMethodElement = XMLUtils.selectXenc11Node(getElement().getFirstChild(), EncryptionConstants._TAG_KEYDERIVATIONMETHOD, 0);
        if (keyDerivationMethodElement == null) {
            LOG.debug("No KeyDerivationMethod element found!");
            return null;
        }
        this.keyDerivationMethod = new KeyDerivationMethodImpl(keyDerivationMethodElement, this.baseURI);
        return this.keyDerivationMethod;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.xml.security.encryption.AgreementMethod
    public void setKeyDerivationMethod(KeyDerivationMethod keyDerivationMethod) {
        this.keyDerivationMethod = keyDerivationMethod;
        if (keyDerivationMethod instanceof ElementProxy) {
            appendSelf((ElementProxy) keyDerivationMethod);
            addReturnToSelf();
        } else {
            LOG.warn("KeyDerivationMethod [{0}] is set but is not an instance of ElementProxy. The DOM node is lost upon serialization.", keyDerivationMethod);
        }
    }

    @Override // org.apache.xml.security.encryption.AgreementMethod
    public OriginatorKeyInfo getOriginatorKeyInfo() throws XMLSecurityException {
        if (this.originatorKeyInfo != null) {
            LOG.debug("Returning cached OriginatorKeyInfo");
            return this.originatorKeyInfo;
        }
        Element originatorKeyInfoElement = XMLUtils.selectXencNode(getElement().getFirstChild(), EncryptionConstants._TAG_ORIGINATORKEYINFO, 0);
        if (originatorKeyInfoElement == null) {
            LOG.debug("No OriginatorKeyInfo element found!");
            return null;
        }
        this.originatorKeyInfo = new OriginatorKeyInfo(originatorKeyInfoElement, this.baseURI);
        return this.originatorKeyInfo;
    }

    @Override // org.apache.xml.security.encryption.AgreementMethod
    public void setOriginatorKeyInfo(OriginatorKeyInfo keyInfo) {
        this.originatorKeyInfo = keyInfo;
        appendSelf(keyInfo);
        addReturnToSelf();
    }

    @Override // org.apache.xml.security.encryption.AgreementMethod
    public void setOriginatorPublicKey(PublicKey publicKey) {
        OriginatorKeyInfo originatorKeyInfo = new OriginatorKeyInfo(getDocument());
        originatorKeyInfo.add(publicKey);
        setOriginatorKeyInfo(originatorKeyInfo);
    }

    @Override // org.apache.xml.security.encryption.AgreementMethod
    public RecipientKeyInfo getRecipientKeyInfo() throws XMLSecurityException {
        if (this.recipientKeyInfo != null) {
            LOG.debug("Returning cached RecipientKeyInfo");
            return this.recipientKeyInfo;
        }
        Element recipientKeyInfoElement = XMLUtils.selectXencNode(getElement().getFirstChild(), EncryptionConstants._TAG_RECIPIENTKEYINFO, 0);
        if (recipientKeyInfoElement == null) {
            LOG.debug("No RecipientKeyInfo element found!");
            return null;
        }
        this.recipientKeyInfo = new RecipientKeyInfo(recipientKeyInfoElement, this.baseURI);
        return this.recipientKeyInfo;
    }

    @Override // org.apache.xml.security.encryption.AgreementMethod
    public void setRecipientKeyInfo(RecipientKeyInfo keyInfo) {
        this.recipientKeyInfo = keyInfo;
        appendSelf(keyInfo);
        addReturnToSelf();
    }

    @Override // org.apache.xml.security.encryption.AgreementMethod
    public String getAlgorithm() {
        if (this.algorithmURI == null) {
            this.algorithmURI = getLocalAttribute("Algorithm");
        }
        return this.algorithmURI;
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return EncryptionConstants._TAG_AGREEMENTMETHOD;
    }

    private KeyDerivationMethod createKeyDerivationMethod(KeyAgreementParameters keyAgreementParameter) throws XMLEncryptionException {
        KDFParams kdfParams;
        KeyDerivationParameters kdfParameters = keyAgreementParameter.getKeyDerivationParameter();
        if (kdfParameters == null) {
            throw new IllegalArgumentException("KeyAgreementParameters must have KeyDerivationParameters set!");
        }
        KeyDerivationMethodImpl keyDerivationMethod = new KeyDerivationMethodImpl(getDocument());
        keyDerivationMethod.setAlgorithm(kdfParameters.getAlgorithm());
        switch (kdfParameters.getAlgorithm()) {
            case "http://www.w3.org/2009/xmlenc11#ConcatKDF":
                kdfParams = getConcatKDFParams(kdfParameters);
                break;
            case "http://www.w3.org/2021/04/xmldsig-more#hkdf":
                kdfParams = getHKDFParams(kdfParameters);
                break;
            default:
                throw new XMLEncryptionException("KeyDerivation.UnsupportedAlgorithm", kdfParameters.getAlgorithm(), kdfParameters.getClass().getName());
        }
        keyDerivationMethod.setKDFParams(kdfParams);
        return keyDerivationMethod;
    }

    private ConcatKDFParamsImpl getConcatKDFParams(KeyDerivationParameters parameter) {
        if (!(parameter instanceof ConcatKDFParams)) {
            throw new IllegalArgumentException("KDF Parameter must be instance of ConcatKDFParams");
        }
        ConcatKDFParams kdfParameters = (ConcatKDFParams) parameter;
        ConcatKDFParamsImpl concatKDFParams = new ConcatKDFParamsImpl(getDocument());
        concatKDFParams.setDigestMethod(kdfParameters.getDigestAlgorithm());
        concatKDFParams.setAlgorithmId(kdfParameters.getAlgorithmID());
        concatKDFParams.setPartyUInfo(kdfParameters.getPartyUInfo());
        concatKDFParams.setPartyVInfo(kdfParameters.getPartyVInfo());
        concatKDFParams.setSuppPubInfo(kdfParameters.getSuppPubInfo());
        concatKDFParams.setSuppPrivInfo(kdfParameters.getSuppPrivInfo());
        return concatKDFParams;
    }

    private HKDFParamsImpl getHKDFParams(KeyDerivationParameters parameter) {
        if (!(parameter instanceof HKDFParams)) {
            throw new IllegalArgumentException("KDF Parameter must be instance of HKDFParams");
        }
        HKDFParams kdfParameters = (HKDFParams) parameter;
        HKDFParamsImpl kdfParams = new HKDFParamsImpl(getDocument());
        kdfParams.setPRFAlgorithm(kdfParameters.getHmacHashAlgorithm());
        Base64.Encoder base64Encoder = Base64.getEncoder();
        if (kdfParameters.getSalt() != null) {
            kdfParams.setSalt(base64Encoder.encodeToString(kdfParameters.getSalt()));
        }
        if (kdfParameters.getInfo() != null) {
            kdfParams.setInfo(base64Encoder.encodeToString(kdfParameters.getInfo()));
        }
        kdfParams.setKeyLength(Integer.valueOf(kdfParameters.getKeyBitLength() / 8));
        return kdfParams;
    }
}
