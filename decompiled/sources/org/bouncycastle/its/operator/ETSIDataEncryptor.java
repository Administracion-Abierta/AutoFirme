package org.bouncycastle.its.operator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/operator/ETSIDataEncryptor.class */
public interface ETSIDataEncryptor {
    byte[] encrypt(byte[] bArr);

    byte[] getKey();

    byte[] getNonce();
}
