package org.apache.xml.security.encryption;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/EncryptedKey.class */
public interface EncryptedKey extends EncryptedType {
    String getRecipient();

    void setRecipient(String str);

    ReferenceList getReferenceList();

    void setReferenceList(ReferenceList referenceList);

    String getCarriedName();

    void setCarriedName(String str);
}
