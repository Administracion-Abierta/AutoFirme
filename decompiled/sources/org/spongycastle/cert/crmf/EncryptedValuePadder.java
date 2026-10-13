package org.spongycastle.cert.crmf;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/crmf/EncryptedValuePadder.class */
public interface EncryptedValuePadder {
    byte[] getPaddedData(byte[] bArr);

    byte[] getUnpaddedData(byte[] bArr);
}
