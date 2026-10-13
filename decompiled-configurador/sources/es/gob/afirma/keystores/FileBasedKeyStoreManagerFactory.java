package es.gob.afirma.keystores;

import java.io.File;
import java.io.IOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/FileBasedKeyStoreManagerFactory.class */
final class FileBasedKeyStoreManagerFactory {
    private FileBasedKeyStoreManagerFactory() {
    }

    static AOKeyStoreManager getKeyStoreManager(File file, Object obj) throws AOKeystoreAlternativeException, IOException {
        AOKeyStore aOKeyStore;
        if (file == null) {
            throw new IllegalArgumentException("El fichero de almacen no puede ser nulo");
        }
        if (!file.exists() || !file.isFile()) {
            throw new IOException("El fichero de almacen no existe");
        }
        if (!file.canRead()) {
            throw new IOException("El fichero de almacen no se puede leer");
        }
        String lowerCase = file.getName().toLowerCase();
        if (lowerCase.endsWith(".pfx") || lowerCase.endsWith(".p12")) {
            aOKeyStore = AOKeyStore.PKCS12;
        } else if (lowerCase.endsWith(".jks")) {
            aOKeyStore = AOKeyStore.JAVA;
        } else if (lowerCase.endsWith(".dll") || lowerCase.endsWith(".dylib") || lowerCase.endsWith(".so")) {
            aOKeyStore = AOKeyStore.PKCS11;
        } else {
            throw new IllegalArgumentException("No se soporta el tipo de fichero: " + file.getAbsolutePath());
        }
        return AOKeyStoreManagerFactory.getAOKeyStoreManager(aOKeyStore, file.getAbsolutePath(), "File", aOKeyStore.getStorePasswordCallback(obj), obj);
    }
}
