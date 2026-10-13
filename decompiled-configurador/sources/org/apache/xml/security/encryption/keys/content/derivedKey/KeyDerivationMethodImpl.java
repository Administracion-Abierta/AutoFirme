package org.apache.xml.security.encryption.keys.content.derivedKey;

import java.util.StringJoiner;
import org.apache.xml.security.encryption.KeyDerivationMethod;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.Encryption11ElementProxy;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/keys/content/derivedKey/KeyDerivationMethodImpl.class */
public class KeyDerivationMethodImpl extends Encryption11ElementProxy implements KeyDerivationMethod {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) KeyDerivationMethodImpl.class);
    private KDFParams kdfParams;

    public KeyDerivationMethodImpl(Document doc) {
        super(doc);
    }

    public KeyDerivationMethodImpl(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public void setAlgorithm(String algorithm) {
        if (algorithm != null) {
            setLocalIdAttribute("Algorithm", algorithm);
        }
    }

    @Override // org.apache.xml.security.encryption.KeyDerivationMethod
    public String getAlgorithm() {
        return getLocalAttribute("Algorithm");
    }

    @Override // org.apache.xml.security.encryption.KeyDerivationMethod
    public KDFParams getKDFParams() throws XMLSecurityException {
        if (this.kdfParams != null) {
            LOG.debug("Returning cached KDFParams");
            return this.kdfParams;
        }
        String kdfAlgorithm = getAlgorithm();
        if (EncryptionConstants.ALGO_ID_KEYDERIVATION_CONCATKDF.equals(kdfAlgorithm)) {
            Element concatKDFParamsElement = XMLUtils.selectXenc11Node(getElement().getFirstChild(), EncryptionConstants._TAG_CONCATKDFPARAMS, 0);
            this.kdfParams = new ConcatKDFParamsImpl(concatKDFParamsElement, getBaseURI());
        } else if (EncryptionConstants.ALGO_ID_KEYDERIVATION_HKDF.equals(kdfAlgorithm)) {
            Element hkdfParamsElement = XMLUtils.selectNode(getElement().getFirstChild(), Constants.XML_DSIG_NS_MORE_21_04, EncryptionConstants._TAG_HKDFPARAMS, 0);
            this.kdfParams = new HKDFParamsImpl(hkdfParamsElement, "http://www.w3.org/2007/05/xmldsig-more#");
        } else {
            throw new XMLSecurityException("KeyDerivation.NotSupportedParameter", new Object[]{kdfAlgorithm});
        }
        return this.kdfParams;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void setKDFParams(KDFParams kDFParams) {
        this.kdfParams = kDFParams;
        if (kDFParams instanceof ElementProxy) {
            appendSelf((ElementProxy) kDFParams);
            addReturnToSelf();
        } else {
            LOG.debug("Could not append KDFParams because it does not implement ElementProxy");
        }
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return EncryptionConstants._TAG_KEYDERIVATIONMETHOD;
    }

    public String toString() {
        return new StringJoiner(", ", KeyDerivationMethodImpl.class.getSimpleName() + "[", "]").add("kdfParams=" + this.kdfParams).toString();
    }
}
