package org.spongycastle.openssl;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMKeyPairParser.class */
interface PEMKeyPairParser {
    PEMKeyPair parse(byte[] bArr) throws IOException;
}
