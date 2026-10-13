package es.gob.afirma.standalone.configurator;

import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.standalone.configurator.common.ConfiguratorUtil;
import es.gob.afirma.standalone.plugins.AfirmaPlugin;
import es.gob.afirma.standalone.plugins.manager.PluginsManager;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/ConfiguratorWindows.class */
final class ConfiguratorWindows implements Configurator {
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String KS_FILENAME = "autofirma.pfx";
    private static final String KS_PASSWORD = "654321";
    private static final String CA_CERT_FILENAME = "Autofirma_ROOT.cer";
    private static final int UNINSTALL_CERT_RETRIES = 3;
    private final boolean jnlpInstance;
    private final boolean firefoxSecurityRoots;
    private final String certificatePath;
    private final String keyStorePath;

    public ConfiguratorWindows(boolean z, boolean z2, String str, String str2) {
        this.jnlpInstance = z;
        this.firefoxSecurityRoots = z2;
        this.certificatePath = str;
        this.keyStorePath = str2;
    }

    @Override // es.gob.afirma.standalone.configurator.Configurator
    public void configure(Console console) throws GeneralSecurityException, IOException {
        console.print(Messages.getString("ConfiguratorWindows.2"));
        File applicationDirectory = getApplicationDirectory(this.jnlpInstance);
        console.print(Messages.getString("ConfiguratorWindows.3") + applicationDirectory.getAbsolutePath());
        if (!checkSSLKeyStoreGenerated(applicationDirectory)) {
            if (!this.keyStorePath.isEmpty() && !this.certificatePath.isEmpty()) {
                console.print(Messages.getString("ConfiguratorWindows.25"));
                try {
                    copyFile(new File(this.keyStorePath), new File(applicationDirectory.getAbsolutePath(), KS_FILENAME));
                } catch (Exception e) {
                    console.print(Messages.getString("ConfiguratorWindows.23") + ": " + e);
                }
                try {
                    copyFile(new File(this.certificatePath), new File(applicationDirectory.getAbsolutePath(), CA_CERT_FILENAME));
                } catch (Exception e2) {
                    console.print(Messages.getString("ConfiguratorWindows.24") + ": " + e2);
                }
            } else {
                console.print(Messages.getString("ConfiguratorWindows.5"));
                CertUtil.CertPack certPackForLocalhostSsl = CertUtil.getCertPackForLocalhostSsl(ConfiguratorUtil.CERT_ALIAS, KS_PASSWORD);
                console.print(Messages.getString("ConfiguratorWindows.11"));
                ConfiguratorUtil.installFile(certPackForLocalhostSsl.getPkcs12(), new File(applicationDirectory, KS_FILENAME));
                ConfiguratorUtil.installFile(certPackForLocalhostSsl.getCaCertificate().getEncoded(), new File(applicationDirectory, CA_CERT_FILENAME));
                if (this.jnlpInstance) {
                    JOptionPane.showMessageDialog(console.getParentComponent(), Messages.getString("ConfiguratorWindows.17"));
                    console.print(Messages.getString("ConfiguratorWindows.6"));
                    importCARootOnWindowsKeyStore(certPackForLocalhostSsl.getCaCertificate(), "CN=Autofirma ROOT");
                }
            }
            console.print(Messages.getString("ConfiguratorWindows.26"));
            ConfiguratorFirefoxWindows.uninstallRootCAMozillaKeyStore(applicationDirectory, console, 3);
            console.print(Messages.getString("ConfiguratorWindows.9"));
            try {
                ConfiguratorFirefoxWindows.installCACertOnMozillaKeyStores(applicationDirectory, console);
            } catch (MozillaProfileNotFoundException e3) {
                console.print(Messages.getString("ConfiguratorWindows.12") + ": " + e3);
            }
            if (this.firefoxSecurityRoots) {
                console.print(Messages.getString("ConfiguratorWindows.22"));
                try {
                    ConfiguratorFirefoxWindows.configureUseSystemTrustStore(true, console);
                } catch (MozillaProfileNotFoundException e4) {
                    console.print(Messages.getString("ConfiguratorWindows.21") + ": " + e4);
                }
            }
        } else {
            console.print(Messages.getString("ConfiguratorWindows.14"));
        }
        console.print(Messages.getString("ConfiguratorWindows.8"));
    }

    private static File getApplicationDirectory(boolean z) {
        if (z) {
            File file = new File(System.getenv("ALLUSERSPROFILE"), "Autofirma");
            if (file.isDirectory() || file.mkdirs()) {
                return file;
            }
            return new File(System.getProperty("java.io.tmpdir"));
        }
        return ConfiguratorUtil.getApplicationDirectory();
    }

    @Override // es.gob.afirma.standalone.configurator.Configurator
    public File getAplicationDirectory() {
        return getApplicationDirectory(false);
    }

    @Override // es.gob.afirma.standalone.configurator.Configurator
    public File getAlternativeApplicationDirectory() {
        return new File(System.getenv("ALLUSERSPROFILE"), "Autofirma");
    }

    private static boolean checkSSLKeyStoreGenerated(File file) {
        return new File(file, KS_FILENAME).exists();
    }

    @Override // es.gob.afirma.standalone.configurator.Configurator
    public void uninstall(Console console, PluginsManager pluginsManager) {
        LOGGER.info("Desinstalamos el certificado raiz del almacen de Windows");
        uninstallRootCAWindowsKeyStore();
        LOGGER.info("Desinstalamos el certificado raiz del almacen de Firefox");
        ConfiguratorFirefoxWindows.uninstallRootCAMozillaKeyStore(getApplicationDirectory(this.jnlpInstance), console, 3);
        List<AfirmaPlugin> pluginsLoadedList = null;
        try {
            pluginsLoadedList = pluginsManager.getPluginsLoadedList();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se pudo obtener el listado de plugins de Autofirma", (Throwable) e);
        }
        if (pluginsLoadedList != null && !pluginsLoadedList.isEmpty()) {
            LOGGER.info("Desinstalamos los plugins instalados");
            for (AfirmaPlugin afirmaPlugin : pluginsLoadedList) {
                try {
                    pluginsManager.uninstallPlugin(afirmaPlugin);
                } catch (Exception e2) {
                    LOGGER.log(Level.WARNING, "No se pudo desinstalar el plugin: " + afirmaPlugin.getInfo().getName(), (Throwable) e2);
                }
            }
        }
        File alternativeApplicationDirectory = getAlternativeApplicationDirectory();
        if (alternativeApplicationDirectory.isDirectory()) {
            try {
                Files.walkFileTree(alternativeApplicationDirectory.toPath(), new HashSet(), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() { // from class: es.gob.afirma.standalone.configurator.ConfiguratorWindows.1
                    @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
                    public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) {
                        try {
                            Files.delete(path);
                        } catch (Exception e3) {
                            ConfiguratorWindows.LOGGER.warning("No se pudo eliminar el fichero: " + LoggerUtil.getCleanUserHomePath(path.toAbsolutePath().toString()));
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
                    public FileVisitResult postVisitDirectory(Path path, IOException iOException) {
                        try {
                            Files.delete(path);
                        } catch (IOException e3) {
                            ConfiguratorWindows.LOGGER.warning("No se pudo eliminar el directorio: " + LoggerUtil.getCleanUserHomePath(path.toAbsolutePath().toString()));
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (Exception e3) {
                LOGGER.log(Level.WARNING, "No se ha podido eliminar por completo el directorio alternativo para el certificado SSL", (Throwable) e3);
            }
        }
    }

    private static void importCARootOnWindowsKeyStore(Certificate certificate, String str) throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance("Windows-ROOT");
        keyStore.load(null, null);
        boolean z = false;
        boolean z2 = false;
        do {
            try {
                keyStore.setCertificateEntry(str, certificate);
                z = true;
            } catch (KeyStoreException e) {
                LOGGER.warning("No se pudo instalar la CA del certificado SSL para el socket en el almacen de Windows: " + e);
                if (JOptionPane.showConfirmDialog((Component) null, Messages.getString("ConfiguratorWindows.0"), Messages.getString("ConfiguratorWindows.1"), 2, 2) == 2) {
                    z2 = true;
                    LOGGER.severe("El usuario cancelo la instalacion del certificado SSL para el socket: " + e);
                }
            }
            if (z) {
                return;
            }
        } while (!z2);
    }

    /* JADX WARN: Not initialized variable reg: 8, insn: 0x00c8: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r8 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:27:0x00c8 */
    /* JADX WARN: Not initialized variable reg: 9, insn: 0x00cc: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r9 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:29:0x00cc */
    private static void uninstallRootCAWindowsKeyStore() {
        String certificateAlias;
        InputStream inputStream;
        Throwable th;
        try {
            KeyStore keyStore = KeyStore.getInstance("Windows-ROOT");
            keyStore.load(null, null);
            File file = new File(getApplicationDirectory(false), CA_CERT_FILENAME);
            if (file.isFile() && file.canRead()) {
                try {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        Throwable th2 = null;
                        X509Certificate x509Certificate = (X509Certificate) CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID).generateCertificate(fileInputStream);
                        certificateAlias = keyStore.getCertificateAlias(x509Certificate);
                        if (certificateAlias != null) {
                            LOGGER.info("Se ha encontrado el certificado con el numero de serie: " + x509Certificate.getSerialNumber());
                        } else {
                            certificateAlias = "CN=Autofirma ROOT";
                            LOGGER.info("No se encontro el certificado de CA del directorio de instalacion en el almacen");
                        }
                        if (fileInputStream != null) {
                            if (0 != 0) {
                                try {
                                    fileInputStream.close();
                                } catch (Throwable th3) {
                                    th2.addSuppressed(th3);
                                }
                            } else {
                                fileInputStream.close();
                            }
                        }
                    } catch (Throwable th4) {
                        if (inputStream != null) {
                            if (th != null) {
                                try {
                                    inputStream.close();
                                } catch (Throwable th5) {
                                    th.addSuppressed(th5);
                                }
                            } else {
                                inputStream.close();
                            }
                        }
                        throw th4;
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "No se pudo cargar el certificado de CA para identificar cual eliminar del almacen de confianza. Se eliminara en base al alias por defecto: CN=Autofirma ROOT", (Throwable) e);
                    certificateAlias = "CN=Autofirma ROOT";
                }
            } else {
                certificateAlias = "CN=Autofirma ROOT";
            }
            try {
                if (keyStore.containsAlias(certificateAlias)) {
                    keyStore.deleteEntry(certificateAlias);
                } else {
                    LOGGER.info("El certificado de CA parece ya haber sido eliminado del almacen");
                }
            } catch (Exception e2) {
                LOGGER.warning("No se pudo desinstalar el certificado SSL raiz del almacen de confianza de Windows: " + e2);
            }
        } catch (Exception e3) {
            LOGGER.warning("No se pudo acceder al almacen de confianza de Windows: " + e3);
        }
    }

    /* JADX WARN: Code duplicated, block: B:22:0x006e  */
    /* JADX WARN: Code duplicated, block: B:27:0x0087 A[Catch: Throwable -> 0x00b0, all -> 0x00b8, IOException -> 0x00de, TryCatch #5 {, blocks: (B:3:0x000b, B:4:0x0018, B:5:0x001f, B:7:0x002b, B:12:0x0042, B:15:0x0056, B:14:0x004c, B:18:0x0066, B:24:0x0073, B:27:0x0087, B:26:0x007d, B:29:0x008e), top: B:65:0x000b }] */
    /* JADX WARN: Code duplicated, block: B:56:0x0073 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Not initialized variable reg: 7, insn: 0x00ba: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r7 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:42:0x00ba */
    /* JADX WARN: Not initialized variable reg: 8, insn: 0x00be: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r8 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:44:0x00be */
    private static void copyFile(File file, File file2) {
        InputStream inputStream;
        Throwable th;
        try {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Throwable th2 = null;
                FileOutputStream fileOutputStream = new FileOutputStream(file2);
                Throwable th3 = null;
                try {
                    try {
                        byte[] bArr = new byte[1024];
                        while (true) {
                            int i = fileInputStream.read(bArr);
                            if (i <= 0) {
                                break;
                            } else {
                                fileOutputStream.write(bArr, 0, i);
                            }
                            if (fileOutputStream != null) {
                                if (th3 != null) {
                                    try {
                                        fileOutputStream.close();
                                    } catch (Throwable th4) {
                                        th3.addSuppressed(th4);
                                    }
                                } else {
                                    fileOutputStream.close();
                                }
                            }
                            throw th;
                        }
                        if (fileOutputStream != null) {
                            if (0 != 0) {
                                try {
                                    fileOutputStream.close();
                                } catch (Throwable th5) {
                                    th3.addSuppressed(th5);
                                }
                            } else {
                                fileOutputStream.close();
                            }
                        }
                        if (fileInputStream != null) {
                            if (0 != 0) {
                                try {
                                    fileInputStream.close();
                                } catch (Throwable th6) {
                                    th2.addSuppressed(th6);
                                }
                            } else {
                                fileInputStream.close();
                            }
                        }
                    } catch (Throwable th7) {
                        th3 = th7;
                        throw th7;
                    }
                } catch (Throwable th8) {
                    if (fileOutputStream != null) {
                        if (th3 != null) {
                            fileOutputStream.close();
                        } else {
                            fileOutputStream.close();
                        }
                    }
                    throw th8;
                }
            } catch (Throwable th9) {
                if (inputStream != null) {
                    if (th != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable th10) {
                            th.addSuppressed(th10);
                        }
                    } else {
                        inputStream.close();
                    }
                }
                throw th9;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al copiar el fichero", (Throwable) e);
        }
    }
}
