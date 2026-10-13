package org.apache.xml.security.encryption.keys.content.derivedKey;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/keys/content/derivedKey/HKDFParamsImpl.class */
public class HKDFParamsImpl extends ElementProxy implements KDFParams {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) HKDFParamsImpl.class);

    public HKDFParamsImpl(Document doc) {
        super(doc);
    }

    public HKDFParamsImpl(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public void setPRFAlgorithm(String hmacHashAlgorithm) {
        Element targetElement = XMLUtils.selectNode(getElement().getFirstChild(), getBaseNamespace(), EncryptionConstants._TAG_PRF, 0);
        if (hmacHashAlgorithm == null) {
            LOG.debug("HMAC Hash Method is null!");
            if (targetElement != null) {
                LOG.debug("HMAC Hash Method is null, removing PRF element");
                targetElement.getParentNode().removeChild(targetElement);
                return;
            }
            return;
        }
        if (targetElement == null) {
            targetElement = createElementForFamilyLocal(getBaseNamespace(), EncryptionConstants._TAG_PRF);
            appendSelf(targetElement);
        }
        targetElement.setAttributeNS(null, "Algorithm", hmacHashAlgorithm);
    }

    public String getPRFAlgorithm() {
        Element prfElement = XMLUtils.selectXenc11Node(getElement().getFirstChild(), EncryptionConstants._TAG_PRF, 0);
        if (prfElement != null) {
            return prfElement.getAttributeNS(null, "Algorithm");
        }
        return null;
    }

    public void setInfo(String info) {
        setLocalElementValue(info, EncryptionConstants._TAG_INFO);
    }

    public String getInfo() {
        return getLocalElementValue(EncryptionConstants._TAG_INFO);
    }

    public void setKeyLength(Integer keyLength) {
        setLocalElementValue(keyLength != null ? keyLength.toString() : null, EncryptionConstants._TAG_KEYLENGTH);
    }

    public Integer getKeyLength() {
        String keyLengthStr = getLocalElementValue(EncryptionConstants._TAG_KEYLENGTH);
        Integer keyLength = null;
        if (keyLengthStr != null) {
            try {
                keyLength = Integer.valueOf(Integer.parseInt(keyLengthStr));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid key length: " + keyLengthStr);
            }
        }
        return keyLength;
    }

    public void setSalt(String salt) {
        setLocalElementValue(salt, Constants._TAG_SALT);
    }

    public String getSalt() {
        return getLocalElementValue(Constants._TAG_SALT);
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return EncryptionConstants._TAG_HKDFPARAMS;
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseNamespace() {
        return Constants.XML_DSIG_NS_MORE_21_04;
    }

    public void setLocalElementValue(String value, String elementName) {
        Element targetElement = XMLUtils.selectNode(getElement().getFirstChild(), getBaseNamespace(), elementName, 0);
        if (value == null) {
            LOG.debug("Element value: [%s] is null!", elementName);
            if (targetElement != null) {
                LOG.debug("Element value: [%s] is null. Remove element!", elementName);
                targetElement.getParentNode().removeChild(targetElement);
                return;
            }
            return;
        }
        if (targetElement == null) {
            targetElement = createElementForFamilyLocal(getBaseNamespace(), elementName);
            appendSelf(targetElement);
        }
        targetElement.setTextContent(value);
    }

    public String getLocalElementValue(String elementName) {
        Element targetElement = XMLUtils.selectNode(getElement().getFirstChild(), getBaseNamespace(), elementName, 0);
        if (targetElement != null) {
            return XMLUtils.getFullTextChildrenFromNode(targetElement);
        }
        return null;
    }
}
