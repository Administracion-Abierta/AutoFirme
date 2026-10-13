package org.bouncycastle.crypto;

import javax.security.auth.Destroyable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/SecretWithEncapsulation.class */
public interface SecretWithEncapsulation extends Destroyable {
    byte[] getSecret();

    byte[] getEncapsulation();
}
