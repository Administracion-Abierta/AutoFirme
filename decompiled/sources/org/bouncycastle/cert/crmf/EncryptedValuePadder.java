package org.bouncycastle.cert.crmf;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/EncryptedValuePadder.class */
public interface EncryptedValuePadder {
    byte[] getPaddedData(byte[] bArr);

    byte[] getUnpaddedData(byte[] bArr);
}
