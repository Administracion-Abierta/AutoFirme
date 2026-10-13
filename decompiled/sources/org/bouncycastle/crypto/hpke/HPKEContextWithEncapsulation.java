package org.bouncycastle.crypto.hpke;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/hpke/HPKEContextWithEncapsulation.class */
public class HPKEContextWithEncapsulation extends HPKEContext {
    final byte[] encapsulation;

    public HPKEContextWithEncapsulation(HPKEContext hPKEContext, byte[] bArr) {
        super(hPKEContext.aead, hPKEContext.hkdf, hPKEContext.exporterSecret, hPKEContext.suiteId);
        this.encapsulation = bArr;
    }

    public byte[] getEncapsulation() {
        return Arrays.clone(this.encapsulation);
    }
}
