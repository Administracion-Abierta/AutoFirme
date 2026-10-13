package es.gob.afirma.keystores;

import es.gob.afirma.keystores.callbacks.CachePasswordCallback;
import es.gob.afirma.keystores.callbacks.NullPasswordCallback;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.BadPaddingException;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/FileKeyStoreManager.class */
abstract class FileKeyStoreManager extends AOKeyStoreManager {
    private PasswordCallback cachePasswordCallback;
    private String keystoreFile = null;

    FileKeyStoreManager() {
    }

    protected KeyStore init(InputStream inputStream, PasswordCallback passwordCallback) throws AOKeyStoreManagerException, IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Es necesario proporcionar el fichero de almacen");
        }
        try {
            KeyStore keyStore = KeyStore.getInstance(getType().getProviderName());
            this.cachePasswordCallback = passwordCallback != null ? new CachePasswordCallback(passwordCallback.getPassword()) : NullPasswordCallback.getInstance();
            try {
                keyStore.load(inputStream, this.cachePasswordCallback.getPassword());
                try {
                    inputStream.close();
                } catch (Exception e) {
                    LOGGER.warning("Error cerrando el almacen: " + e);
                }
                return keyStore;
            } catch (IOException e2) {
                if ((e2.getCause() instanceof UnrecoverableKeyException) || (e2.getCause() instanceof BadPaddingException) || (e2.getCause() instanceof ArithmeticException)) {
                    throw new IOException("La contrasena del almacen PKCS#12 / PFX no es valida", e2);
                }
                throw new IOException("El almacen PKCS#12 / PFX no es valido o no pudo cargarse", e2);
            } catch (NoSuchAlgorithmException e3) {
                throw new AOKeyStoreManagerException("No se ha podido verificar la integridad del almacen PKCS#12 / PFX solicitado", e3);
            } catch (CertificateException e4) {
                throw new AOKeyStoreManagerException("No se han podido cargar los certificados del almacen PKCS#12 / PFX solicitado", e4);
            }
        } catch (Exception e5) {
            throw new AOKeyStoreManagerException("No se ha podido obtener el almacen de tipo " + getType(), e5);
        }
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public KeyStore.PrivateKeyEntry getKeyEntry(String str) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableEntryException {
        if (lacksKeyStores()) {
            throw new IllegalStateException("Se han pedido claves a un almacen no inicializado");
        }
        PasswordCallback entryPasswordCallBack = getEntryPasswordCallBack();
        setEntryPasswordCallBack(this.cachePasswordCallback);
        try {
            return super.getKeyEntry(str);
        } catch (Exception e) {
            LOGGER.warning("La contrasena del certificado no coincide con la del almacen: " + e);
            setEntryPasswordCallBack(NullPasswordCallback.getInstance());
            try {
                return super.getKeyEntry(str);
            } catch (Exception e2) {
                LOGGER.warning("La contrasena del certificado no es nula: " + e2);
                setEntryPasswordCallBack(entryPasswordCallBack);
                return super.getKeyEntry(str);
            }
        }
    }

    protected void setKeyStoreFile(String str) {
        this.keystoreFile = str;
    }

    protected String getKeyStoreFile() {
        return this.keystoreFile;
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public void refresh() throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream(this.keystoreFile);
            Throwable th = null;
            try {
                try {
                    setKeyStore(init(fileInputStream, this.cachePasswordCallback));
                    if (fileInputStream != null) {
                        if (0 != 0) {
                            try {
                                fileInputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            fileInputStream.close();
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (fileInputStream != null) {
                    if (th != null) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
                throw th4;
            }
        } catch (AOKeyStoreManagerException e) {
            throw new IOException("Error al recargar el almacen PKCS#12", e);
        }
    }
}
