package org.apache.xml.security.encryption;

import org.apache.xml.security.keys.KeyInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/EncryptedType.class */
public interface EncryptedType {
    String getId();

    void setId(String str);

    String getType();

    void setType(String str);

    String getMimeType();

    void setMimeType(String str);

    String getEncoding();

    void setEncoding(String str);

    EncryptionMethod getEncryptionMethod();

    void setEncryptionMethod(EncryptionMethod encryptionMethod);

    KeyInfo getKeyInfo();

    void setKeyInfo(KeyInfo keyInfo);

    CipherData getCipherData();

    EncryptionProperties getEncryptionProperties();

    void setEncryptionProperties(EncryptionProperties encryptionProperties);
}
