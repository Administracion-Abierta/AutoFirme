package org.bouncycastle.openssl;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMKeyPairParser.class */
interface PEMKeyPairParser {
    PEMKeyPair parse(byte[] bArr) throws IOException;
}
