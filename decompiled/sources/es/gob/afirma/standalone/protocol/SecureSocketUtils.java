package es.gob.afirma.standalone.protocol;

import es.gob.afirma.standalone.DesktopUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/SecureSocketUtils.class */
class SecureSocketUtils {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String KSPASS = "654321";
    private static final String CTPASS = "654321";
    private static final String KEYSTORE_NAME = "autofirma.pfx";
    private static final String PKCS12 = "PKCS12";
    private static final String SSLCONTEXT = "TLSv1";

    SecureSocketUtils() {
    }

    static SSLContext getSecureSSLContext() throws GeneralSecurityException, IOException {
        File keyStoreFile = getKeyStoreFile();
        if (keyStoreFile == null) {
            throw new KeyStoreException("No se encuentra el almacen para el cifrado de la comunicacion SSL");
        }
        LOGGER.info("Se utilizara el siguiente almacen para establecer el socket SSL: " + keyStoreFile.getAbsolutePath());
        char[] charArray = "654321".toCharArray();
        char[] charArray2 = "654321".toCharArray();
        KeyStore keyStore = KeyStore.getInstance(PKCS12);
        FileInputStream fileInputStream = new FileInputStream(keyStoreFile);
        Throwable th = null;
        try {
            try {
                keyStore.load(fileInputStream, charArray);
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
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(keyStore, charArray2);
                SSLContext sSLContext = SSLContext.getInstance(SSLCONTEXT);
                sSLContext.init(keyManagerFactory.getKeyManagers(), null, null);
                return sSLContext;
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
    }

    private static File getKeyStoreFile() {
        File applicationDirectory = DesktopUtil.getApplicationDirectory();
        if (applicationDirectory != null && new File(applicationDirectory, KEYSTORE_NAME).exists()) {
            return new File(applicationDirectory, KEYSTORE_NAME);
        }
        File alternativeDirectory = DesktopUtil.getAlternativeDirectory();
        if (alternativeDirectory != null && new File(alternativeDirectory, KEYSTORE_NAME).exists()) {
            return new File(alternativeDirectory, KEYSTORE_NAME);
        }
        return null;
    }
}
