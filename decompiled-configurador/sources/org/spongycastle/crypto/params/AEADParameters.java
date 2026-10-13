package org.spongycastle.crypto.params;

import org.spongycastle.crypto.CipherParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/params/AEADParameters.class */
public class AEADParameters implements CipherParameters {
    private byte[] associatedText;
    private byte[] nonce;
    private KeyParameter key;
    private int macSize;

    public AEADParameters(KeyParameter key, int macSize, byte[] nonce) {
        this(key, macSize, nonce, null);
    }

    public AEADParameters(KeyParameter key, int macSize, byte[] nonce, byte[] associatedText) {
        this.key = key;
        this.nonce = nonce;
        this.macSize = macSize;
        this.associatedText = associatedText;
    }

    public KeyParameter getKey() {
        return this.key;
    }

    public int getMacSize() {
        return this.macSize;
    }

    public byte[] getAssociatedText() {
        return this.associatedText;
    }

    public byte[] getNonce() {
        return this.nonce;
    }
}
