package org.apache.xml.security.encryption;

import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/encryption/EncryptionProperties.class */
public interface EncryptionProperties {
    String getId();

    void setId(String str);

    Iterator<EncryptionProperty> getEncryptionProperties();

    void addEncryptionProperty(EncryptionProperty encryptionProperty);

    void removeEncryptionProperty(EncryptionProperty encryptionProperty);
}
