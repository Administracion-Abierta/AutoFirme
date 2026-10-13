package org.spongycastle.crypto.generators;

import org.spongycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/generators/KDF2BytesGenerator.class */
public class KDF2BytesGenerator extends BaseKDFBytesGenerator {
    public KDF2BytesGenerator(Digest digest) {
        super(1, digest);
    }
}
