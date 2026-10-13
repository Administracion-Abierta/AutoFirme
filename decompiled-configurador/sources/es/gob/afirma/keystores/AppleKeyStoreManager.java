package es.gob.afirma.keystores;

import es.gob.afirma.core.InvalidOSException;
import es.gob.afirma.core.misc.Platform;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/AppleKeyStoreManager.class */
final class AppleKeyStoreManager extends AOKeyStoreManager {
    AppleKeyStoreManager() {
        setKeyStoreType(AOKeyStore.APPLE);
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager
    public void init(AOKeyStore aOKeyStore, InputStream inputStream, PasswordCallback passwordCallback, Object[] objArr, boolean z) throws AOKeyStoreManagerException, IOException {
        setKeyStore(initApple(inputStream));
        getAliasesWithoutDuplicates();
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public KeyStore.PrivateKeyEntry getKeyEntry(String str) throws KeyStoreException {
        if (getKeyStore() == null) {
            throw new IllegalStateException("Se han pedido claves a un almacen no inicializado");
        }
        if (str == null) {
            throw new IllegalArgumentException("El alias no puede ser nulo");
        }
        if (getKeyStore().containsAlias(str)) {
            PrivateKey privateKey = null;
            try {
                LOGGER.info("Llavero de Mac OS X, se tratan directamente las claves privadas");
                privateKey = (PrivateKey) getKeyStore().getKey(str, "dummy".toCharArray());
            } catch (Exception e) {
                LOGGER.warning("Error recuperando directamente la clave privada en Mac OS X: " + e);
            }
            if (privateKey == null) {
                throw new UnsupportedOperationException("La entrada no tiene clave privada");
            }
            return new KeyStore.PrivateKeyEntry(privateKey, getCertificateChain(str));
        }
        LOGGER.warning("El almacen no contiene ninguna clave con el alias especificado, se devolvera null");
        return null;
    }

    private static KeyStore initApple(InputStream inputStream) throws AOKeyStoreManagerException, IOException {
        if (!Platform.OS.MACOSX.equals(Platform.getOS())) {
            throw new InvalidOSException("Apple Mac OS X");
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

    private void getAliasesWithoutDuplicates() throws AOKeyStoreManagerException {
        try {
            Enumeration<String> enumerationAliases = getKeyStore().aliases();
            ArrayList<String> arrayList = new ArrayList();
            while (enumerationAliases.hasMoreElements()) {
                String strNextElement = enumerationAliases.nextElement();
                try {
                    if (getKeyStore().isKeyEntry(strNextElement)) {
                        arrayList.add(strNextElement);
                    }
                } catch (KeyStoreException e) {
                    LOGGER.info("Se ignora uno de los certificados por no poderse operar con su clave privada: " + e);
                }
            }
            ArrayList arrayList2 = new ArrayList();
            HashSet hashSet = new HashSet();
            for (String str : arrayList) {
                X509Certificate certificate = getCertificate(str);
                if (certificate != null) {
                    if (!hashSet.contains(certificate.getSerialNumber())) {
                        hashSet.add(certificate.getSerialNumber());
                        arrayList2.add(str);
                    } else {
                        LOGGER.info(String.format("Se retira el certificado con numero de serie=%1s por estar duplicado", certificate.getSerialNumber()));
                    }
                } else {
                    LOGGER.warning(String.format("No se pudo cargar el certificado con alias '%1s'", str));
                }
            }
            setCachedAliases((String[]) arrayList2.toArray(new String[arrayList2.size()]));
        } catch (KeyStoreException e2) {
            throw new AOKeyStoreManagerException("Error obteniendo los alias: " + e2, e2);
        }
    }
}
