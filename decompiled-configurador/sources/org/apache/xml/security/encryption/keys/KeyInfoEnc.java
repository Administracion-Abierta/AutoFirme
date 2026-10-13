package org.apache.xml.security.encryption.keys;

import org.apache.xml.security.encryption.AgreementMethod;
import org.apache.xml.security.encryption.keys.content.AgreementMethodImpl;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.I18n;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/keys/KeyInfoEnc.class */
public class KeyInfoEnc extends KeyInfo {
    private static final Logger LOG = LoggerFactory.getLogger(KeyInfoEnc.class.getName());

    public KeyInfoEnc(Document doc) {
        super(doc);
    }

    public KeyInfoEnc(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void add(AgreementMethod agreementMethod) {
        if (agreementMethod instanceof ElementProxy) {
            LOG.debug("Adding agreementMethod with algorithm {0}", agreementMethod.getAlgorithm());
            appendSelf((ElementProxy) agreementMethod);
            addReturnToSelf();
            return;
        }
        Object[] exArgs = {EncryptionConstants._TAG_AGREEMENTMETHOD, agreementMethod.getClass().getName()};
        throw new IllegalArgumentException(I18n.translate("KeyValue.IllegalArgument", exArgs));
    }

    public int lengthAgreementMethod() {
        return length("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_AGREEMENTMETHOD);
    }

    public AgreementMethod itemAgreementMethod(int i) throws XMLSecurityException {
        Element e = XMLUtils.selectXencNode(getFirstChild(), EncryptionConstants._TAG_AGREEMENTMETHOD, i);
        if (e == null) {
            LOG.warn("No AgreementMethod element at position [{0}]", Integer.valueOf(i));
            return null;
        }
        return new AgreementMethodImpl(e);
    }

    public boolean containsAgreementMethod() {
        return lengthAgreementMethod() > 0;
    }
}
