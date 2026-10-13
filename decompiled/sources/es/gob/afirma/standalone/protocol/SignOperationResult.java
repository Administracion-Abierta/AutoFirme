package es.gob.afirma.standalone.protocol;

import java.security.KeyStore;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/SignOperationResult.class */
class SignOperationResult {
    byte[] result;
    KeyStore.PrivateKeyEntry pke;

    public SignOperationResult(byte[] bArr, KeyStore.PrivateKeyEntry privateKeyEntry) {
        this.result = bArr;
        this.pke = privateKeyEntry;
    }

    public byte[] getResult() {
        return this.result;
    }

    public KeyStore.PrivateKeyEntry getPke() {
        return this.pke;
    }
}
