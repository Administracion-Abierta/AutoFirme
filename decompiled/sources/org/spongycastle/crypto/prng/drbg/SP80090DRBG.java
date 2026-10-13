package org.spongycastle.crypto.prng.drbg;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/prng/drbg/SP80090DRBG.class */
public interface SP80090DRBG {
    int getBlockSize();

    int generate(byte[] bArr, byte[] bArr2, boolean z);

    void reseed(byte[] bArr);
}
