package org.bouncycastle.its.operator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/operator/ETSIDataDecryptor.class */
public interface ETSIDataDecryptor {
    byte[] decrypt(byte[] bArr, byte[] bArr2, byte[] bArr3);

    byte[] getKey();
}
