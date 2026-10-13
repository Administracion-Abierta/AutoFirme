package es.gob.afirma.keystores;

import es.gob.afirma.core.InvalidOSException;
import es.gob.afirma.core.MissingLibraryException;
import es.gob.afirma.core.misc.Platform;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/AOKeyStoreManagerHelperCapiAddressBook.class */
final class AOKeyStoreManagerHelperCapiAddressBook {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private AOKeyStoreManagerHelperCapiAddressBook() {
    }

    static KeyStore initCAPIAddressBook(AOKeyStore aOKeyStore) throws AOKeyStoreManagerException {
        if (!Platform.getOS().equals(Platform.OS.WINDOWS)) {
            throw new InvalidOSException("Microsoft Windows");
        }
        if (Security.getProvider("SunMSCAPI") == null) {
            try {
                Security.addProvider((Provider) Class.forName("sun.security.mscapi.SunMSCAPI").getConstructor(new Class[0]).newInstance(new Object[0]));
            } catch (Exception e) {
                LOGGER.severe("No se ha podido instanciar 'sun.security.mscapi.SunMSCAPI': " + e);
                throw new MissingSunMSCAPIException(e);
            }
        }
        Provider provider = Security.getProvider("MSCAPIAddressBook");
        if (provider == null) {
            try {
                provider = (Provider) Class.forName("es.gob.afirma.keystores.capiaddressbook.MSCAPIAddressBook").getConstructor(new Class[0]).newInstance(new Object[0]);
                Security.addProvider(provider);
            } catch (Exception e2) {
                throw new MissingLibraryException("No se ha podido instanciar el proveedor MSCAPIAddressBook", e2);
            }
        }
        try {
            KeyStore keyStore = KeyStore.getInstance(aOKeyStore.getProviderName(), provider);
            try {
                keyStore.load(null, null);
                return keyStore;
            } catch (Exception e3) {
                throw new AOKeyStoreManagerException("No se ha podido abrir el almacen " + aOKeyStore.getProviderName() + ": " + e3, e3);
            }
        } catch (Exception e4) {
            throw new AOKeyStoreManagerException("No se ha podido obtener el almacen " + aOKeyStore.getProviderName() + ": " + e4, e4);
        }
    }
}
