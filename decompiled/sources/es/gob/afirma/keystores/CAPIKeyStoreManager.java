package es.gob.afirma.keystores;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.InvalidOSException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.keystores.callbacks.FirstEmptyThenPinUiPasswordCallback;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/CAPIKeyStoreManager.class */
public final class CAPIKeyStoreManager extends AOKeyStoreManager {
    private static KeyStore capiKsMy = null;

    CAPIKeyStoreManager() {
        setKeyStoreType(AOKeyStore.WINDOWS);
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public KeyStore.PrivateKeyEntry getKeyEntry(String str) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableEntryException {
        if (capiKsMy == null) {
            throw new IllegalStateException("Se han pedido claves a un almacen no inicializado");
        }
        return (KeyStore.PrivateKeyEntry) capiKsMy.getEntry(str, new KeyStore.PasswordProtection("dummy".toCharArray()));
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager
    public void init(AOKeyStore aOKeyStore, InputStream inputStream, PasswordCallback passwordCallback, Object[] objArr, boolean z) throws AOKeyStoreManagerException {
        resetCachedAliases();
        if (AOKeyStore.WINDOWS.equals(aOKeyStore)) {
            if (z) {
                capiKsMy = null;
            }
            setKeyStore(initCapi());
            return;
        }
        throw new AOKeyStoreManagerException("Tipo de almacen no soportado, este gestor es exclusivo CAPI: " + aOKeyStore.getName());
    }

    private static KeyStore initCapi() throws AOKeyStoreManagerException {
        if (capiKsMy == null) {
            if (!Platform.getOS().equals(Platform.OS.WINDOWS)) {
                throw new InvalidOSException("Microsoft Windows");
            }
            Provider provider = Security.getProvider("SunMSCAPI");
            if (provider == null) {
                try {
                    provider = (Provider) Class.forName("sun.security.mscapi.SunMSCAPI").getConstructor(new Class[0]).newInstance(new Object[0]);
                    Security.addProvider(provider);
                } catch (Exception e) {
                    LOGGER.severe("No se ha podido instanciar 'sun.security.mscapi.SunMSCAPI': " + e);
                    throw new MissingSunMSCAPIException(e);
                }
            }
            try {
                capiKsMy = KeyStoreUtilities.getKeyStoreWithPasswordCallbackHandler(AOKeyStore.WINDOWS, new FirstEmptyThenPinUiPasswordCallback(KeyStoreMessages.getString("CAPIKeyStoreManager.0")), provider, null);
                try {
                    cleanCAPIDuplicateAliases(capiKsMy);
                } catch (Exception e2) {
                    LOGGER.warning("No se han podido tratar los alias duplicados: " + e2);
                }
            } catch (AOCancelledOperationException e3) {
                throw e3;
            } catch (Exception e4) {
                throw new AOKeyStoreManagerException("No se ha podido obtener el almacen Windows.MY: " + e4, e4);
            }
        }
        return capiKsMy;
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public X509Certificate getCertificate(String str) {
        if (str == null) {
            LOGGER.warning("El alias del certificado es nulo, se devolvera null");
            return null;
        }
        if (capiKsMy == null) {
            LOGGER.warning("No se ha podido recuperar el certificado con el alias especificado porque el KeyStore no estaba inicializado, se devolvera null");
            return null;
        }
        try {
            X509Certificate x509Certificate = (X509Certificate) capiKsMy.getCertificate(str);
            if (x509Certificate == null) {
                LOGGER.warning("No se ha podido recuperar el certificado con el alias especificado, se devolvera null");
                return null;
            }
            return x509Certificate;
        } catch (Exception e) {
            LOGGER.warning("No se ha podido recuperar el certificado con el alias especificado', se devolvera null: " + e);
            return null;
        }
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public X509Certificate[] getCertificateChain(String str) {
        if (capiKsMy == null) {
            LOGGER.warning("El KeyStore actual no esta inicializado, por lo que no se pudo recuperar el certificado para el alias especificado");
            return null;
        }
        try {
            return (X509Certificate[]) capiKsMy.getCertificateChain(str);
        } catch (Exception e) {
            LOGGER.severe("Error al obtener la cadena de certificados para el alias certificado actual, se devolvera una cadena vacia: " + e);
            return new X509Certificate[0];
        }
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public String[] getAliases() {
        if (capiKsMy == null) {
            throw new IllegalStateException("Se han pedido los alias de un almacen no inicializado");
        }
        if (getCachedAliases() != null) {
            return getCachedAliases();
        }
        LOGGER.info("Solicitando los alias al KeyStore (" + capiKsMy.getProvider() + ")");
        try {
            setCachedAliases(cleanDeactivatedAliases((String[]) Collections.list(capiKsMy.aliases()).toArray(new String[0])));
            return getCachedAliases();
        } catch (Exception e) {
            LOGGER.severe("Error intentando obtener los alias del almacen de claves, se devolvera una enumeracion vacia: " + e);
            return new String[0];
        }
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager
    public String toString() {
        return "Gestor del almacen Windows.MY de CAPI via SunMSCAPI";
    }

    private static void cleanCAPIDuplicateAliases(KeyStore keyStore) throws IllegalAccessException, NoSuchFieldException {
        if (AOUtil.isJava9orNewer()) {
            return;
        }
        Field declaredField = keyStore.getClass().getDeclaredField("keyStoreSpi");
        declaredField.setAccessible(true);
        KeyStoreSpi keyStoreSpi = (KeyStoreSpi) declaredField.get(keyStore);
        if ("sun.security.mscapi.KeyStore$MY".equals(keyStoreSpi.getClass().getName())) {
            Field declaredField2 = keyStoreSpi.getClass().getEnclosingClass().getDeclaredField("entries");
            declaredField2.setAccessible(true);
            if (declaredField2.get(keyStoreSpi) instanceof Collection) {
                for (Object obj : (Collection) declaredField2.get(keyStoreSpi)) {
                    Field declaredField3 = obj.getClass().getDeclaredField("certChain");
                    declaredField3.setAccessible(true);
                    String string = Integer.toString(((X509Certificate[]) declaredField3.get(obj))[0].hashCode());
                    Field declaredField4 = obj.getClass().getDeclaredField("alias");
                    declaredField4.setAccessible(true);
                    String str = (String) declaredField4.get(obj);
                    if (!str.equals(string)) {
                        declaredField4.set(obj, str.concat(" - ").concat(string));
                    }
                }
            }
        }
    }
}
