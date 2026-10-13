package org.apache.xml.security.encryption.keys.content.derivedKey;

import java.util.StringJoiner;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.Encryption11ElementProxy;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/encryption/keys/content/derivedKey/ConcatKDFParamsImpl.class */
public class ConcatKDFParamsImpl extends Encryption11ElementProxy implements KDFParams {
    public ConcatKDFParamsImpl(Document doc) {
        super(doc);
    }

    public ConcatKDFParamsImpl(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public void setAlgorithmId(String algorithm) {
        if (algorithm != null) {
            setLocalAttribute(EncryptionConstants._ATT_ALGORITHM_ID, algorithm);
        }
    }

    public String getAlgorithmId() {
        return getLocalAttribute(EncryptionConstants._ATT_ALGORITHM_ID);
    }

    public void setPartyUInfo(String partyUInfo) {
        if (partyUInfo != null) {
            setLocalAttribute(EncryptionConstants._ATT_PARTYUINFO, partyUInfo);
        }
    }

    public String getPartyUInfo() {
        return getLocalAttribute(EncryptionConstants._ATT_PARTYUINFO);
    }

    public void setPartyVInfo(String partyVInfo) {
        if (partyVInfo != null) {
            setLocalAttribute(EncryptionConstants._ATT_PARTYVINFO, partyVInfo);
        }
    }

    public String getPartyVInfo() {
        return getLocalAttribute(EncryptionConstants._ATT_PARTYVINFO);
    }

    public void setSuppPubInfo(String suppPubInfo) {
        if (suppPubInfo != null) {
            setLocalAttribute(EncryptionConstants._ATT_SUPPPUBINFO, suppPubInfo);
        }
    }

    public String getSuppPubInfo() {
        return getLocalAttribute(EncryptionConstants._ATT_SUPPPUBINFO);
    }

    public void setSuppPrivInfo(String suppPrivInfo) {
        if (suppPrivInfo != null) {
            setLocalAttribute(EncryptionConstants._ATT_SUPPPRIVINFO, suppPrivInfo);
        }
    }

    public String getSuppPrivInfo() {
        return getLocalAttribute(EncryptionConstants._ATT_SUPPPRIVINFO);
    }

    public void setDigestMethod(String digestMethod) {
        if (digestMethod != null) {
            Element digestElement = XMLUtils.createElementInSignatureSpace(getDocument(), "DigestMethod");
            digestElement.setAttributeNS(null, "Algorithm", digestMethod);
            digestElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + ElementProxy.getDefaultPrefix("http://www.w3.org/2000/09/xmldsig#"), "http://www.w3.org/2000/09/xmldsig#");
            appendSelf(digestElement);
        }
    }

    public String getDigestMethod() {
        Element digestElement = XMLUtils.selectDsNode(getElement().getFirstChild(), "DigestMethod", 0);
        if (digestElement != null) {
            return digestElement.getAttributeNS(null, "Algorithm");
        }
        return null;
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return EncryptionConstants._TAG_CONCATKDFPARAMS;
    }

    public String toString() {
        return new StringJoiner(", ", ConcatKDFParamsImpl.class.getSimpleName() + "[", "]").add("baseURI='" + this.baseURI + OperatorName.SHOW_TEXT_LINE).toString();
    }
}
