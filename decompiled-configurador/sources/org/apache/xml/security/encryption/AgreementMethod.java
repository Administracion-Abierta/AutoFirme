package org.apache.xml.security.encryption;

import java.security.PublicKey;
import java.util.Iterator;
import org.apache.xml.security.encryption.keys.OriginatorKeyInfo;
import org.apache.xml.security.encryption.keys.RecipientKeyInfo;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/AgreementMethod.class */
public interface AgreementMethod {
    byte[] getKANonce();

    void setKANonce(byte[] bArr);

    KeyDerivationMethod getKeyDerivationMethod() throws XMLSecurityException;

    void setKeyDerivationMethod(KeyDerivationMethod keyDerivationMethod);

    Iterator<Element> getAgreementMethodInformation();

    void addAgreementMethodInformation(Element element);

    void removeAgreementMethodInformation(Element element);

    OriginatorKeyInfo getOriginatorKeyInfo() throws XMLSecurityException;

    void setOriginatorKeyInfo(OriginatorKeyInfo originatorKeyInfo);

    void setOriginatorPublicKey(PublicKey publicKey);

    RecipientKeyInfo getRecipientKeyInfo() throws XMLSecurityException;

    void setRecipientKeyInfo(RecipientKeyInfo recipientKeyInfo);

    String getAlgorithm();
}
