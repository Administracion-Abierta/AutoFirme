package org.apache.xml.security.encryption;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/encryption/CipherData.class */
public interface CipherData {
    public static final int VALUE_TYPE = 1;
    public static final int REFERENCE_TYPE = 2;

    int getDataType();

    CipherValue getCipherValue();

    void setCipherValue(CipherValue cipherValue) throws XMLEncryptionException;

    CipherReference getCipherReference();

    void setCipherReference(CipherReference cipherReference) throws XMLEncryptionException;
}
