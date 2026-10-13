package es.gob.afirma.core.misc.http;

import es.gob.afirma.core.LogManager;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.core.ui.CoreMessages;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/misc/http/TrustStoreManager.class */
public class TrustStoreManager {
    private KeyStore ks;
    private final File tsPath = getJKSFile();
    private static final char[] TRUSTED_KS_PWD = "changeit".toCharArray();
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static TrustStoreManager instance = null;

    public static TrustStoreManager getInstance() {
        return getInstance(null);
    }

    public static TrustStoreManager getInstance(Object obj) {
        if (instance == null) {
            instance = new TrustStoreManager(obj);
        }
        return instance;
    }

    private TrustStoreManager(Object obj) {
        if (this.tsPath.isFile()) {
            try {
                this.ks = loadTrustedKeyStore();
            } catch (Exception e) {
                if (AOUIFactory.showConfirmDialog(obj, CoreMessages.getString("AutofirmaTrustStore.0"), CoreMessages.getString("AutofirmaTrustStore.1"), AOUIFactory.YES_NO_OPTION, AOUIFactory.WARNING_MESSAGE) == AOUIFactory.OK_OPTION) {
                    try {
                        Files.delete(this.tsPath.toPath());
                    } catch (IOException e2) {
                        LOGGER.warning("No se pudo eliminar el TrustStore corrupto: " + e2);
                    }
                    this.ks = null;
                }
            }
        }
    }

    public X509Certificate[] getCertificates() throws KeyStoreException {
        if (this.ks == null) {
            return new X509Certificate[0];
        }
        ArrayList arrayList = new ArrayList();
        Enumeration<String> enumerationAliases = this.ks.aliases();
        while (enumerationAliases.hasMoreElements()) {
            arrayList.add((X509Certificate) this.ks.getCertificate(enumerationAliases.nextElement()));
        }
        return (X509Certificate[]) arrayList.toArray(new X509Certificate[0]);
    }

    public void importCerts(X509Certificate... x509CertificateArr) throws IOException, KeyStoreException {
        if (x509CertificateArr == null) {
            return;
        }
        if (this.ks == null) {
            this.ks = createTrustedKeystore();
        }
        for (X509Certificate x509Certificate : x509CertificateArr) {
            if (searchCert(x509Certificate) == null) {
                this.ks.setCertificateEntry(generateAlias(x509Certificate), x509Certificate);
            }
        }
        store();
    }

    private String generateAlias(X509Certificate x509Certificate) throws KeyStoreException {
        String string = x509Certificate.getSubjectX500Principal().toString();
        String str = string;
        int i = 1;
        while (this.ks.containsAlias(str)) {
            str = string + " (" + i + ")";
            i++;
        }
        return str;
    }

    /* JADX WARN: Not initialized variable reg: 7, insn: 0x00af: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r7 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:40:0x00af */
    /* JADX WARN: Not initialized variable reg: 8, insn: 0x00b3: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r8 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:42:0x00b3 */
    private void store() throws IOException, KeyStoreException {
        OutputStream outputStream;
        Throwable th;
        synchronized (instance) {
            try {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(this.tsPath);
                    Throwable th2 = null;
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                    Throwable th3 = null;
                    try {
                        try {
                            this.ks.store(bufferedOutputStream, TRUSTED_KS_PWD);
                            if (bufferedOutputStream != null) {
                                if (0 != 0) {
                                    try {
                                        bufferedOutputStream.close();
                                    } catch (Throwable th4) {
                                        th3.addSuppressed(th4);
                                    }
                                } else {
                                    bufferedOutputStream.close();
                                }
                            }
                            if (fileOutputStream != null) {
                                if (0 != 0) {
                                    try {
                                        fileOutputStream.close();
                                    } catch (Throwable th5) {
                                        th2.addSuppressed(th5);
                                    }
                                } else {
                                    fileOutputStream.close();
                                }
                            }
                        } catch (Throwable th6) {
                            th3 = th6;
                            throw th6;
                        }
                    } catch (Throwable th7) {
                        if (bufferedOutputStream != null) {
                            if (th3 != null) {
                                try {
                                    bufferedOutputStream.close();
                                } catch (Throwable th8) {
                                    th3.addSuppressed(th8);
                                }
                            } else {
                                bufferedOutputStream.close();
                            }
                        }
                        throw th7;
                    }
                } catch (Throwable th9) {
                    if (outputStream != null) {
                        if (th != null) {
                            try {
                                outputStream.close();
                            } catch (Throwable th10) {
                                th.addSuppressed(th10);
                            }
                        } else {
                            outputStream.close();
                        }
                    }
                    throw th9;
                }
            } catch (IOException e) {
                throw e;
            } catch (Exception e2) {
                throw new KeyStoreException("No se ha podido guardar el almacen", e2);
            }
        }
    }

    public void deleteCert(X509Certificate x509Certificate) throws IOException, KeyStoreException {
        if (this.ks == null) {
            return;
        }
        String strSearchCert = searchCert(x509Certificate);
        if (strSearchCert == null) {
            LOGGER.warning("No se encontro en el almacen el certificado que desea eliminar");
        } else {
            this.ks.deleteEntry(strSearchCert);
            store();
        }
    }

    private String searchCert(X509Certificate x509Certificate) throws KeyStoreException {
        Enumeration<String> enumerationAliases = this.ks.aliases();
        while (enumerationAliases.hasMoreElements()) {
            String strNextElement = enumerationAliases.nextElement();
            if (((X509Certificate) this.ks.getCertificate(strNextElement)).equals(x509Certificate)) {
                return strNextElement;
            }
        }
        return null;
    }

    private static KeyStore createTrustedKeystore() throws KeyStoreException {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null, TRUSTED_KS_PWD);
            return keyStore;
        } catch (Exception e) {
            throw new KeyStoreException("No se ha podido crear un almacen vacio", e);
        }
    }

    private KeyStore loadTrustedKeyStore() throws IOException, KeyStoreException {
        FileInputStream fileInputStream = new FileInputStream(this.tsPath);
        Throwable th = null;
        try {
            try {
                byte[] dataFromInputStream = AOUtil.getDataFromInputStream(fileInputStream);
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
                try {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataFromInputStream);
                    Throwable th3 = null;
                    try {
                        try {
                            KeyStore keyStore = KeyStore.getInstance("JKS");
                            keyStore.load(byteArrayInputStream, TRUSTED_KS_PWD);
                            if (byteArrayInputStream != null) {
                                if (0 != 0) {
                                    try {
                                        byteArrayInputStream.close();
                                    } catch (Throwable th4) {
                                        th3.addSuppressed(th4);
                                    }
                                } else {
                                    byteArrayInputStream.close();
                                }
                            }
                            return keyStore;
                        } catch (Throwable th5) {
                            th3 = th5;
                            throw th5;
                        }
                    } catch (Throwable th6) {
                        if (byteArrayInputStream != null) {
                            if (th3 != null) {
                                try {
                                    byteArrayInputStream.close();
                                } catch (Throwable th7) {
                                    th3.addSuppressed(th7);
                                }
                            } else {
                                byteArrayInputStream.close();
                            }
                        }
                        throw th6;
                    }
                } catch (Exception e) {
                    throw new KeyStoreException("No se ha podido cargar el almacen de confianza de la aplicacion", e);
                }
            } catch (Throwable th8) {
                th = th8;
                throw th8;
            }
        } catch (Throwable th9) {
            if (fileInputStream != null) {
                if (th != null) {
                    try {
                        fileInputStream.close();
                    } catch (Throwable th10) {
                        th.addSuppressed(th10);
                    }
                } else {
                    fileInputStream.close();
                }
            }
            throw th9;
        }
    }

    public static File getJKSFile() {
        return new File(Platform.getUserHome(), LogManager.SUBDIR + File.separator + "TrustedCertsKeystore.jks");
    }
}
