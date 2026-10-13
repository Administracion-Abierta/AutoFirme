package org.bouncycastle.openssl;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMDecryptor.class */
public interface PEMDecryptor {
    byte[] decrypt(byte[] bArr, byte[] bArr2) throws PEMException;
}
