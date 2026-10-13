package org.bouncycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/CryptoServiceProperties.class */
public interface CryptoServiceProperties {
    int bitsOfSecurity();

    String getServiceName();

    CryptoServicePurpose getPurpose();

    Object getParams();
}
