package es.gob.afirma.keystores;

import es.gob.afirma.keystores.jmulticard.ui.DnieCacheCallbackHandler;
import es.gob.afirma.keystores.jmulticard.ui.PasswordCallbackManager;
import es.gob.afirma.keystores.jmulticard.ui.SmartcardCacheCallbackHandler;
import es.gob.afirma.keystores.jmulticard.ui.SmartcardCallbackHandler;
import es.gob.jmulticard.jse.provider.DnieProvider;
import es.gob.jmulticard.jse.provider.ceres.CeresProvider;
import es.gob.jmulticard.jse.provider.gide.SmartCafeProvider;
import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.callback.CallbackHandler;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/AOKeyStoreManagerHelperFullJava.class */
final class AOKeyStoreManagerHelperFullJava {
    protected static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private AOKeyStoreManagerHelperFullJava() {
    }

    static KeyStore initCeresJava(Object obj) throws AOKeyStoreManagerException, IOException {
        return init(AOKeyStore.CERES, buildLoadStoreParameter(new SmartcardCacheCallbackHandler()), new CeresProvider(), obj);
    }

    static KeyStore initSmartCafeJava(Object obj) throws AOKeyStoreManagerException, IOException {
        return init(AOKeyStore.SMARTCAFE, buildLoadStoreParameter(new SmartcardCallbackHandler()), new SmartCafeProvider(), obj);
    }

    static KeyStore initDnieJava(Object obj) throws AOKeyStoreManagerException, IOException {
        return init(AOKeyStore.DNIEJAVA, buildLoadStoreParameter(new DnieCacheCallbackHandler()), new DnieProvider(), obj);
    }

    static KeyStore initJMulticard(Object obj) throws AOKeyStoreManagerException, IOException {
        Provider dnieProvider;
        try {
            dnieProvider = (Provider) DnieProvider.class.getMethod("configure", String.class).invoke(new DnieProvider(), "name=JMulticard\nallowAnotherCards=true");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se ha podido configurar el proveedor de DNIe con la configuracion indicada", (Throwable) e);
            dnieProvider = new DnieProvider();
        }
        return init(AOKeyStore.CERES_430, buildLoadStoreParameter(new SmartcardCacheCallbackHandler()), dnieProvider, obj);
    }

    private static KeyStore init(AOKeyStore aOKeyStore, KeyStore.LoadStoreParameter loadStoreParameter, Provider provider, Object obj) throws AOKeyStoreManagerException, IOException {
        if (Security.getProvider(provider.getName()) == null) {
            Security.addProvider(provider);
        }
        PasswordCallbackManager.setDialogOwner(obj);
        try {
            KeyStore keyStore = KeyStore.getInstance(aOKeyStore.getProviderName());
            LOGGER.info("Cargando KeyStore 100% Java para " + aOKeyStore.toString());
            try {
                keyStore.load(loadStoreParameter);
                return keyStore;
            } catch (NoSuchAlgorithmException e) {
                throw new AOKeyStoreManagerException("Error de algoritmo al obtener el almacen 100% Java para " + aOKeyStore.toString() + ": " + e, e);
            } catch (CertificateException e2) {
                throw new AOKeyStoreManagerException("Error de certificado al obtener el almacen 100% Java para " + aOKeyStore.toString() + ": " + e2, e2);
            }
        } catch (Exception e3) {
            throw new AOKeyStoreManagerException("No se ha podido obtener el almacen 100% Java para " + aOKeyStore.toString() + ": " + e3, e3);
        }
    }

    private static KeyStore.LoadStoreParameter buildLoadStoreParameter(final CallbackHandler callbackHandler) {
        return new KeyStore.LoadStoreParameter() { // from class: es.gob.afirma.keystores.AOKeyStoreManagerHelperFullJava.1
            @Override // java.security.KeyStore.LoadStoreParameter
            public KeyStore.ProtectionParameter getProtectionParameter() {
                return new KeyStore.CallbackHandlerProtection(callbackHandler);
            }
        };
    }
}
