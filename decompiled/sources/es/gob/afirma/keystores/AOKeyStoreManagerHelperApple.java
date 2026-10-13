package es.gob.afirma.keystores;

import es.gob.afirma.core.InvalidOSException;
import es.gob.afirma.core.misc.Platform;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/AOKeyStoreManagerHelperApple.class */
final class AOKeyStoreManagerHelperApple {
    private AOKeyStoreManagerHelperApple() {
    }

    static KeyStore initApple(InputStream inputStream) throws AOKeyStoreManagerException, IOException {
        if (!Platform.OS.MACOSX.equals(Platform.getOS())) {
            throw new InvalidOSException("Apple macOS");
        }
        try {
            KeyStore keyStore = KeyStore.getInstance(AOKeyStore.APPLE.getProviderName());
            try {
                keyStore.load(inputStream, null);
                return keyStore;
            } catch (NoSuchAlgorithmException e) {
                throw new AOKeyStoreManagerException("No se ha podido verificar la integridad del almacen Apple.KeychainStore", e);
            } catch (CertificateException e2) {
                throw new AOKeyStoreManagerException("No se han podido cargar los certificados del almacen Apple.KeychainStore", e2);
            }
        } catch (Exception e3) {
            throw new AOKeyStoreManagerException("No se ha podido obtener el almacen Apple.KeychainStore", e3);
        }
    }
}
