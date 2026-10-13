package org.bouncycastle.crypto.threshold;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/threshold/SplitSecret.class */
public interface SplitSecret {
    SecretShare[] getSecretShares();

    byte[] getSecret() throws IOException;
}
