package org.bouncycastle.crypto.hpke;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/hpke/KEM.class */
public abstract class KEM {
    abstract AsymmetricCipherKeyPair GeneratePrivateKey();

    abstract AsymmetricCipherKeyPair DeriveKeyPair(byte[] bArr);

    abstract byte[][] Encap(AsymmetricKeyParameter asymmetricKeyParameter);

    abstract byte[][] Encap(AsymmetricKeyParameter asymmetricKeyParameter, AsymmetricCipherKeyPair asymmetricCipherKeyPair);

    abstract byte[][] AuthEncap(AsymmetricKeyParameter asymmetricKeyParameter, AsymmetricCipherKeyPair asymmetricCipherKeyPair);

    abstract byte[] Decap(byte[] bArr, AsymmetricCipherKeyPair asymmetricCipherKeyPair);

    abstract byte[] AuthDecap(byte[] bArr, AsymmetricCipherKeyPair asymmetricCipherKeyPair, AsymmetricKeyParameter asymmetricKeyParameter);

    abstract byte[] SerializePublicKey(AsymmetricKeyParameter asymmetricKeyParameter);

    abstract byte[] SerializePrivateKey(AsymmetricKeyParameter asymmetricKeyParameter);

    abstract AsymmetricKeyParameter DeserializePublicKey(byte[] bArr);

    abstract AsymmetricCipherKeyPair DeserializePrivateKey(byte[] bArr, byte[] bArr2);

    abstract int getEncryptionSize();
}
