package org.spongycastle.openssl;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/openssl/PEMEncryptor.class */
public interface PEMEncryptor {
    String getAlgorithm();

    byte[] getIV();

    byte[] encrypt(byte[] bArr) throws PEMException;
}
