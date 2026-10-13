package org.bouncycastle.openssl;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMEncryptor.class */
public interface PEMEncryptor {
    String getAlgorithm();

    byte[] getIV();

    byte[] encrypt(byte[] bArr) throws PEMException;
}
