package org.spongycastle.crypto.params;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/CCMParameters.class */
public class CCMParameters extends AEADParameters {
    public CCMParameters(KeyParameter key, int macSize, byte[] nonce, byte[] associatedText) {
        super(key, macSize, nonce, associatedText);
    }
}
