package es.gob.afirma.keystores;

import es.gob.afirma.core.MissingLibraryException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.BadPaddingException;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/AOKeyStoreManagerHelperSingle.class */
final class AOKeyStoreManagerHelperSingle {
    private AOKeyStoreManagerHelperSingle() {
    }

    static KeyStore initSingle(InputStream inputStream, PasswordCallback passwordCallback) throws AOKeyStoreManagerException, IOException {
        char[] password;
        if (inputStream == null) {
            throw new AOKeyStoreManagerException("Es necesario proporcionar el fichero X.509 o PKCS#7");
        }
        try {
            Provider provider = (Provider) Class.forName("es.gob.afirma.keystores.single.SingleCertKeyStoreProvider").getConstructor(new Class[0]).newInstance(new Object[0]);
            Security.addProvider(provider);
            try {
                KeyStore keyStore = KeyStore.getInstance(AOKeyStore.SINGLE.getProviderName(), provider);
                if (passwordCallback != null) {
                    try {
                        password = passwordCallback.getPassword();
                    } catch (IOException e) {
                        if ((e.getCause() instanceof UnrecoverableKeyException) || (e.getCause() instanceof BadPaddingException)) {
                            throw new IOException("Contrasena invalida: " + e, e);
                        }
                        throw new AOKeyStoreManagerException("No se ha podido abrir el almacen PKCS#7 / X.509 solicitado", e);
                    } catch (NoSuchAlgorithmException e2) {
                        throw new AOKeyStoreManagerException("No se ha podido verificar la integridad del almacen PKCS#7 / X.509 solicitado", e2);
                    } catch (CertificateException e3) {
                        throw new AOKeyStoreManagerException("No se han podido cargar los certificados del almacen PKCS#7 / X.509 solicitado", e3);
                    }
                } else {
                    password = null;
                }
                keyStore.load(inputStream, password);
                return keyStore;
            } catch (Exception e4) {
                throw new AOKeyStoreManagerException("No se ha podido obtener el almacen PKCS#7 / X.509", e4);
            }
        } catch (Exception e5) {
            throw new MissingLibraryException("No se ha podido instanciar el proveedor SingleCertKeyStoreProvider: " + e5, e5);
        }
    }
}
