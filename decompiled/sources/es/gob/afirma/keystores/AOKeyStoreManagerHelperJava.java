package es.gob.afirma.keystores;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.BadPaddingException;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/AOKeyStoreManagerHelperJava.class */
final class AOKeyStoreManagerHelperJava {
    private AOKeyStoreManagerHelperJava() {
    }

    static KeyStore initJava(InputStream inputStream, PasswordCallback passwordCallback, AOKeyStore aOKeyStore) throws AOKeyStoreManagerException, IOException {
        char[] password;
        if (inputStream == null) {
            throw new IOException("Es necesario proporcionar el fichero KeyStore");
        }
        try {
            KeyStore keyStore = KeyStore.getInstance(aOKeyStore.getProviderName());
            if (passwordCallback != null) {
                try {
                    password = passwordCallback.getPassword();
                } catch (IOException e) {
                    if ((e.getCause() instanceof UnrecoverableKeyException) || (e.getCause() instanceof BadPaddingException)) {
                        throw new IOException("Contrasena invalida: " + e, e);
                    }
                    throw e;
                } catch (NoSuchAlgorithmException e2) {
                    throw new AOKeyStoreManagerException("No se ha podido verificar la integridad del almacen JavaKeyStore solicitado", e2);
                } catch (CertificateException e3) {
                    throw new AOKeyStoreManagerException("No se han podido cargar los certificados del almacen JavaKeyStore solicitado", e3);
                }
            } else {
                password = null;
            }
            keyStore.load(inputStream, password);
            return keyStore;
        } catch (Exception e4) {
            throw new AOKeyStoreManagerException("No se ha podido obtener el almacen JavaKeyStore", e4);
        }
    }
}
