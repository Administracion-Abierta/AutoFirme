package org.bouncycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/Xof.class */
public interface Xof extends ExtendedDigest {
    int doFinal(byte[] bArr, int i, int i2);

    int doOutput(byte[] bArr, int i, int i2);
}
