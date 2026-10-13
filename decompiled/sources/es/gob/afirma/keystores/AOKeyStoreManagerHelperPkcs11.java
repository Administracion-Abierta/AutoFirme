package es.gob.afirma.keystores;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.keystores.callbacks.UIPasswordCallback;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/AOKeyStoreManagerHelperPkcs11.class */
final class AOKeyStoreManagerHelperPkcs11 {
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private AOKeyStoreManagerHelperPkcs11() {
    }

    static KeyStore initPKCS11(PasswordCallback passwordCallback, Object[] objArr, boolean z, Object obj) throws AOKeyStoreManagerException, IOException {
        if (objArr == null || objArr.length < 2) {
            throw new IOException("No se puede acceder al KeyStore PKCS#11 si no se especifica la biblioteca");
        }
        if (objArr[0] != null) {
            String string = objArr[0].toString();
            Integer num = null;
            if (objArr.length >= 3 && (objArr[2] instanceof Integer)) {
                num = (Integer) objArr[2];
            }
            String strReplace = new File(string).getName().replace('.', '_').replace(' ', '_');
            Provider provider = Security.getProvider("SunPKCS11-" + strReplace);
            if (provider != null && (z || Boolean.getBoolean("es.gob.afirma.keystores.DoNotReusePkcs11Provider"))) {
                LOGGER.info("Se retira el proveedor " + provider);
                Security.removeProvider(provider.getName());
                provider = null;
            }
            if (provider == null) {
                byte[] bytes = KeyStoreUtilities.createPKCS11ConfigFile(string, strReplace, num).getBytes();
                try {
                    provider = getP11Provider(bytes);
                } catch (Exception e) {
                    LOGGER.warning("Ha fallado el primer intento de inicializacion del PKCS#11 para la la biblioteca '" + string + "', se reintentara: " + e);
                    try {
                        provider = getP11Provider(bytes);
                    } catch (Exception e2) {
                        LOGGER.log(Level.WARNING, "Ha fallado el segundo intento de inicializacion del PKCS#11 para la la biblioteca " + string, (Throwable) e);
                        throw new AOKeyStoreManagerException("No se ha podido instanciar el proveedor SunPKCS11 para la la biblioteca '" + string + "': " + e2, e2);
                    }
                }
            } else {
                LOGGER.info("El proveedor SunPKCS11 solicitado ya estaba instanciado, se reutilizara esa instancia: " + provider.getName());
            }
            if (passwordCallback == null) {
                return getKeyStoreWithNullPassword(provider);
            }
            if (passwordCallback instanceof UIPasswordCallback) {
                ((UIPasswordCallback) passwordCallback).setPrompt(KeyStoreMessages.getString("AOKeyStore.15", AOKeyStore.PKCS11.getName()));
            }
            try {
                return KeyStoreUtilities.getKeyStoreWithPasswordCallbackHandler(AOKeyStore.PKCS11, passwordCallback, provider, obj);
            } catch (AOCancelledOperationException e3) {
                Security.removeProvider("SunPKCS11-" + strReplace);
                throw e3;
            } catch (Exception e4) {
                Security.removeProvider("SunPKCS11-" + strReplace);
                throw new AOKeyStoreManagerException("Error construyendo el KeyStore PKCS#11 para la biblioteca '" + string + "': " + e4, e4);
            }
        }
        throw new IllegalArgumentException("No se puede acceder al KeyStore PKCS#11 si se especifica una biblioteca nula");
    }

    private static KeyStore getKeyStoreWithNullPassword(Provider provider) throws AOKeyStoreManagerException {
        try {
            KeyStore keyStore = KeyStore.getInstance(AOKeyStore.PKCS11.getProviderName(), provider);
            try {
                keyStore.load(null, null);
                return keyStore;
            } catch (IOException e) {
                throw new AOKeyStoreManagerException("No se ha podido obtener el almacen PKCS#11 solicitado: " + e, e);
            } catch (NoSuchAlgorithmException e2) {
                Security.removeProvider(provider.getName());
                throw new AOKeyStoreManagerException("No se ha podido verificar la integridad del almacen PKCS#11 solicitado: " + e2, e2);
            } catch (CertificateException e3) {
                Security.removeProvider(provider.getName());
                throw new AOKeyStoreManagerException("No se han podido cargar los certificados del almacen PKCS#11 solicitado: " + e3, e3);
            }
        } catch (Exception e4) {
            Security.removeProvider(provider.getName());
            throw new AOKeyStoreManagerException("No se ha podido obtener el almacen PKCS#11: " + e4, e4);
        }
    }

    private static Provider getP11Provider(byte[] bArr) throws IllegalAccessException, NoSuchMethodException, InstantiationException, SecurityException, ClassNotFoundException, IOException, IllegalArgumentException, InvocationTargetException {
        if (AOUtil.isJava9orNewer()) {
            return getP11ProviderJava9(bArr);
        }
        return getP11ProviderJava8(bArr);
    }

    private static Provider getP11ProviderJava9(byte[] bArr) throws IllegalAccessException, NoSuchMethodException, IOException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Provider provider = Security.getProvider("SunPKCS11");
        File fileCreateTempFile = File.createTempFile("pkcs11_", ".cfg");
        FileOutputStream fileOutputStream = new FileOutputStream(fileCreateTempFile);
        Throwable th = null;
        try {
            try {
                fileOutputStream.write(bArr);
                fileOutputStream.close();
                if (fileOutputStream != null) {
                    if (0 != 0) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        fileOutputStream.close();
                    }
                }
                Provider provider2 = (Provider) Provider.class.getMethod("configure", String.class).invoke(provider, fileCreateTempFile.getAbsolutePath());
                fileCreateTempFile.deleteOnExit();
                Security.addProvider(provider2);
                return provider2;
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (fileOutputStream != null) {
                if (th != null) {
                    try {
                        fileOutputStream.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    fileOutputStream.close();
                }
            }
            throw th4;
        }
    }

    private static Provider getP11ProviderJava8(byte[] bArr) throws IllegalAccessException, NoSuchMethodException, InstantiationException, SecurityException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException {
        Provider provider = (Provider) Class.forName("sun.security.pkcs11.SunPKCS11").getConstructor(InputStream.class).newInstance(new ByteArrayInputStream(bArr));
        Security.addProvider(provider);
        return provider;
    }
}
