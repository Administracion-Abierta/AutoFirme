package es.gob.afirma.standalone.plugins;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/plugins/DataCipher.class */
public interface DataCipher {
    String cipher(byte[] bArr) throws EncryptingException;

    byte[] decipher(byte[] bArr) throws EncryptingException;
}
