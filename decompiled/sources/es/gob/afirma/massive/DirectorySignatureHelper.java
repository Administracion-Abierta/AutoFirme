package es.gob.afirma.massive;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOUnsupportedSignFormatException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.MimeHelper;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.AOSignerFactory;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.standalone.CommandLineParameters;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/massive/DirectorySignatureHelper.class */
public class DirectorySignatureHelper {
    private static final String MODE_KEY = "mode";
    private static final String FORMAT_KEY = "format";
    private static final String URI_STR = "uri";
    private static final String CADES_SIGNER = "es.gob.afirma.signers.cades.AOCAdESSigner";
    private static final String XADES_SIGNER = "es.gob.afirma.signers.xades.AOXAdESSigner";
    private static final String XMLDSIG_SIGNER = "es.gob.afirma.signers.xmldsig.AOXMLDSigSigner";
    private static final String PDF_SIGNER = "es.gob.afirma.signers.pades.AOPDFSigner";
    private static final String ODF_SIGNER = "es.gob.afirma.signers.odf.AOODFSSigner";
    private static final String OOXML_SIGNER = "es.gob.afirma.signers.ooxml.AOOOXMLSigner";
    private static final String REG_FIELD_SEPARATOR = " - ";
    private static final String DEFAULT_LOG_FILE = "result.log";
    private static final String DEFAULT_MESSAGE_DIGEST_ALGORITHM = "SHA-512";
    private String algorithm;
    private String format;
    private String mode;
    private AOSigner defaultSigner;
    private LogHandler logHandler;
    private int errorCount;
    private int warnCount;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static MessageDigest md = null;
    private FileFilter fileFilter = null;
    private String outputDir = null;
    private boolean overwriteFiles = false;
    private String inDir = null;
    private List<String> signedFilenames = new ArrayList();
    private boolean activeLog = true;
    private String logPath = null;

    public DirectorySignatureHelper(String str, String str2, String str3) throws AOUnsupportedSignFormatException {
        this.algorithm = null;
        this.format = null;
        this.mode = null;
        this.defaultSigner = null;
        if (str == null || str2 == null || str3 == null) {
            throw new IllegalArgumentException("No se ha indicado una configuracion de algoritmo de firma valida");
        }
        this.algorithm = str;
        this.format = str2;
        this.mode = str3;
        this.defaultSigner = AOSignerFactory.getSigner(this.format);
        if (this.defaultSigner == null) {
            throw new AOUnsupportedSignFormatException("El formato de firma '" + str2 + "' seleccionado para la firma masiva no esta soportado");
        }
    }

    public boolean massiveSign(MassiveType massiveType, String str, boolean z, String str2, boolean z2, boolean z3, KeyStore.PrivateKeyEntry privateKeyEntry, Properties properties) throws IOException, AOException {
        if (properties == null || !properties.containsKey("format") || !properties.containsKey("mode")) {
            throw new IllegalArgumentException("No se ha establecido el formato y modo de firma");
        }
        if (str == null || str.trim().length() < 1) {
            LOGGER.warning("No se ha indicado un directorio de inicio, se usara el actual");
        }
        File file = new File((str == null || str.trim().length() <= 0) ? "." : str.trim());
        this.inDir = file.getAbsolutePath();
        if (!file.exists() || !file.isDirectory()) {
            throw new AOException("El directorio de entrada no existe");
        }
        if (!file.canRead()) {
            throw new AOException("No se tienen permisos de lectura para el directorio de entrada");
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (File file2 : file.listFiles()) {
            arrayList2.add(file2);
        }
        for (int i = 0; i < arrayList2.size(); i++) {
            if (((File) arrayList2.get(i)).isFile()) {
                if (this.fileFilter == null) {
                    arrayList.add(((File) arrayList2.get(i)).getPath());
                } else if (this.fileFilter.accept((File) arrayList2.get(i))) {
                    arrayList.add(((File) arrayList2.get(i)).getAbsolutePath());
                }
            } else if (z) {
                if (((File) arrayList2.get(i)).canRead()) {
                    for (File file3 : ((File) arrayList2.get(i)).listFiles()) {
                        arrayList2.add(file3);
                    }
                } else {
                    LOGGER.warning("Por falta de permisos no se procesaran los ficheros del subdirectorio: " + LoggerUtil.getCleanUserHomePath(((File) arrayList2.get(i)).getAbsolutePath()));
                }
            }
        }
        return massiveSign(massiveType, (String[]) arrayList.toArray(new String[arrayList.size()]), str2, z2, z3, privateKeyEntry, properties);
    }

    public boolean massiveSign(MassiveType massiveType, String[] strArr, String str, boolean z, boolean z2, KeyStore.PrivateKeyEntry privateKeyEntry, Properties properties) throws IOException, AOException {
        if (properties == null || !properties.containsKey("format") || !properties.containsKey("mode")) {
            throw new IllegalArgumentException("No se ha establecido el formato y modo de firma");
        }
        Properties properties2 = (Properties) properties.clone();
        properties2.setProperty("headless", "true");
        if (!properties2.containsKey("mode") && this.mode != null) {
            properties2.setProperty("mode", this.mode);
        }
        if (!properties2.containsKey("format")) {
            properties2.setProperty("format", this.format);
        }
        boolean zMassiveSignOperation = true;
        this.signedFilenames = new ArrayList();
        if (strArr == null || strArr.length == 0) {
            LOGGER.warning("No se han proporcionado ficheros para firmar");
            return true;
        }
        if (str != null) {
            this.outputDir = str;
        } else {
            LOGGER.warning("No se ha especificado un directorio de salida, se usara el actual");
            this.outputDir = ".";
        }
        File file = new File(this.outputDir);
        if (!file.exists() && z) {
            file.mkdirs();
        }
        if (!file.exists() || !file.isDirectory()) {
            throw new IOException("El directorio de salida no existe o existe un fichero con el mismo nombre");
        }
        if (!file.canWrite()) {
            throw new IOException("No se tienen permisos de escritura en el directorio de salida");
        }
        FileOutputStream fileOutputStream = new FileOutputStream(this.logPath != null ? this.logPath : str + File.separator + DEFAULT_LOG_FILE);
        Throwable th = null;
        try {
            if (this.activeLog && this.logHandler == null) {
                this.logHandler = initDefaultLogHandler(fileOutputStream);
            }
            File[] files = getFiles(strArr);
            if (MassiveType.SIGN.equals(massiveType) || massiveType == null) {
                zMassiveSignOperation = massiveSignOperation(files, file, privateKeyEntry, properties2);
            } else if (MassiveType.COSIGN.equals(massiveType)) {
                zMassiveSignOperation = massiveCosignOperation(files, file, z2, privateKeyEntry, properties2);
            } else if (MassiveType.COUNTERSIGN_ALL.equals(massiveType) || MassiveType.COUNTERSIGN_LEAFS.equals(massiveType)) {
                zMassiveSignOperation = massiveCounterSignOperation(massiveType, files, file, z2, privateKeyEntry, properties2);
            } else {
                LOGGER.severe("Operacion masiva no reconocida");
            }
            closeLogRegistry();
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
            return zMassiveSignOperation;
        } catch (Throwable th3) {
            if (fileOutputStream != null) {
                if (0 != 0) {
                    try {
                        fileOutputStream.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    fileOutputStream.close();
                }
            }
            throw th3;
        }
    }

    public String[] hashesMassiveSign(String[] strArr, KeyStore.PrivateKeyEntry privateKeyEntry, AOSigner aOSigner, Properties properties) throws AOException {
        if (strArr == null || privateKeyEntry == null) {
            throw new IllegalArgumentException("Las huellas digitales a firmar y la clave privada no pueden ser nulas");
        }
        if (properties == null || !properties.containsKey("format") || !properties.containsKey("mode")) {
            throw new IllegalArgumentException("No se ha establecido el formato y modo de firma");
        }
        if (aOSigner != null && !aOSigner.getClass().equals(this.defaultSigner.getClass())) {
            throw new ClassCastException("El signer configurado para la multifirma debe ser compatible con el signer del formato indicado en el constructor");
        }
        Properties properties2 = (Properties) properties.clone();
        properties2.setProperty("headless", "true");
        AOSigner aOSigner2 = aOSigner != null ? aOSigner : this.defaultSigner;
        if (!properties2.containsKey("mode") && this.mode != null) {
            properties2.setProperty("mode", this.mode);
        }
        if (!properties2.containsKey("format")) {
            properties2.setProperty("format", this.format);
        }
        properties2.setProperty("precalculatedHashAlgorithm", AOSignConstants.getDigestAlgorithmName(this.algorithm));
        if (aOSigner2.getClass().getName().equals(XADES_SIGNER) || aOSigner2.getClass().getName().equals(XMLDSIG_SIGNER)) {
            properties2.setProperty("mimeType", "hash/" + AOSignConstants.getDigestAlgorithmName(this.algorithm).toLowerCase());
        }
        byte[][] bArr = new byte[strArr.length][];
        for (int i = 0; i < strArr.length; i++) {
            try {
                bArr[i] = aOSigner2.sign(Base64.decode(strArr[i]), this.algorithm, privateKeyEntry.getPrivateKey(), privateKeyEntry.getCertificateChain(), properties2);
            } catch (IOException e) {
                throw new AOException("El hash '" + strArr[i] + "' no es un Base64 valido", e);
            }
        }
        String[] strArr2 = new String[bArr.length];
        for (int i2 = 0; i2 < bArr.length; i2++) {
            strArr2[i2] = Base64.encode(bArr[i2]);
        }
        return strArr2;
    }

    private File[] getFiles(String[] strArr) {
        ArrayList arrayList = new ArrayList(strArr.length);
        for (String str : strArr) {
            File file = new File(str);
            if (!file.exists()) {
                LOGGER.severe("El fichero no existe: " + LoggerUtil.getCleanUserHomePath(str));
                addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.0"), LoggerUtil.getCleanUserHomePath(str), null);
            } else if (!file.isFile()) {
                LOGGER.severe("El archivo '" + LoggerUtil.getCleanUserHomePath(str) + "' es un directorio y no puede firmarse");
                addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.1"), LoggerUtil.getCleanUserHomePath(str), null);
            } else if (!file.canRead()) {
                LOGGER.severe("No se puede leer el fichero '" + LoggerUtil.getCleanUserHomePath(str) + OperatorName.SHOW_TEXT_LINE);
                addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.2"), LoggerUtil.getCleanUserHomePath(str), null);
            } else {
                arrayList.add(file);
            }
        }
        return (File[]) arrayList.toArray(new File[arrayList.size()]);
    }

    private boolean massiveSignOperation(File[] fileArr, File file, KeyStore.PrivateKeyEntry privateKeyEntry, Properties properties) throws IOException {
        String lowerCase;
        boolean z = true;
        AOSigner aOSigner = this.defaultSigner;
        for (File file2 : fileArr) {
            try {
                if (!isValidDataFile(aOSigner, file2)) {
                    LOGGER.warning("El fichero no puede ser firmado con la configuracion de firma actual: " + LoggerUtil.getCleanUserHomePath(file2.getPath()));
                    addLogRegistry(Level.WARNING, MassiveSignMessages.getString("DirectorySignatureHelper.4"), file2.getPath(), null);
                    z = false;
                } else {
                    properties.setProperty("uri", file2.toURI().toASCIIString());
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file2);
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
                                String name = aOSigner.getClass().getName();
                                if (CADES_SIGNER.equals(name) || XADES_SIGNER.equals(name) || XMLDSIG_SIGNER.equals(name)) {
                                    if ((XADES_SIGNER.equals(name) || XMLDSIG_SIGNER.equals(name)) && "explicit".equalsIgnoreCase(this.mode)) {
                                        dataFromInputStream = digest(dataFromInputStream);
                                        lowerCase = "hash/SHA-512".toLowerCase();
                                    } else {
                                        lowerCase = new MimeHelper(dataFromInputStream).getMimeType();
                                    }
                                    if (lowerCase != null) {
                                        properties.setProperty("mimeType", lowerCase);
                                        String strTransformMimeTypeToOid = MimeHelper.transformMimeTypeToOid(lowerCase);
                                        if (strTransformMimeTypeToOid != null) {
                                            properties.setProperty("contentTypeOid", strTransformMimeTypeToOid);
                                        }
                                    }
                                }
                                try {
                                    byte[] bArrSign = aOSigner.sign(dataFromInputStream, this.algorithm, privateKeyEntry.getPrivateKey(), privateKeyEntry.getCertificateChain(), properties);
                                    String str = ".signed";
                                    if ((PDF_SIGNER.equals(name) || ODF_SIGNER.equals(name) || OOXML_SIGNER.equals(name)) && aOSigner.isSign(dataFromInputStream)) {
                                        str = ".cosign";
                                    }
                                    String strSaveSignToDirectory = saveSignToDirectory(file2.getPath(), bArrSign, file, aOSigner, str);
                                    if (strSaveSignToDirectory == null) {
                                        z = false;
                                    } else {
                                        LOGGER.info("El fichero se ha firmado correctamente: " + LoggerUtil.getCleanUserHomePath(file2.getPath()));
                                        addLogRegistry(Level.INFO, MassiveSignMessages.getString("DirectorySignatureHelper.3"), file2.getPath(), strSaveSignToDirectory);
                                    }
                                } catch (OutOfMemoryError e) {
                                    LOGGER.severe("Error de falta de memoria durante la firma: " + e);
                                    addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.8"), LoggerUtil.getCleanUserHomePath(file2.getPath()), null);
                                    z = false;
                                } catch (UnsupportedOperationException e2) {
                                    LOGGER.severe("No ha sido posible firmar el fichero '" + LoggerUtil.getCleanUserHomePath(file2.getAbsolutePath()) + "': " + e2.getMessage());
                                    addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.7") + REG_FIELD_SEPARATOR + e2.getMessage(), LoggerUtil.getCleanUserHomePath(file2.getPath()), null);
                                    z = false;
                                } catch (Exception e3) {
                                    if ("es.gob.afirma.signers.xades.EFacturaAlreadySignedException".equals(e3.getClass().getName())) {
                                        LOGGER.warning("La factura ya estaba firmada y no admite firmas adicionales '" + LoggerUtil.getCleanUserHomePath(file2.getAbsolutePath()) + "': " + e3);
                                        addLogRegistry(Level.WARNING, MassiveSignMessages.getString("DirectorySignatureHelper.27"), LoggerUtil.getCleanUserHomePath(file2.getPath()), null);
                                        z = false;
                                    } else {
                                        LOGGER.severe("No ha sido posible firmar el fichero '" + LoggerUtil.getCleanUserHomePath(file2.getAbsolutePath()) + "': " + e3);
                                        addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.7"), LoggerUtil.getCleanUserHomePath(file2.getPath()), null);
                                        z = false;
                                    }
                                }
                            } catch (Throwable th3) {
                                if (fileInputStream != null) {
                                    if (th != null) {
                                        try {
                                            fileInputStream.close();
                                        } catch (Throwable th4) {
                                            th.addSuppressed(th4);
                                        }
                                    } else {
                                        fileInputStream.close();
                                    }
                                }
                                throw th3;
                            }
                        } catch (Throwable th5) {
                            th = th5;
                            throw th5;
                        }
                    } catch (Exception e4) {
                        LOGGER.warning("No se pudo leer fichero '" + LoggerUtil.getCleanUserHomePath(file2.getPath()) + "': " + e4);
                        addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.5"), file2.getPath(), null);
                        z = false;
                    }
                }
            } catch (Exception e5) {
                LOGGER.warning("No se pudo leer fichero '" + LoggerUtil.getCleanUserHomePath(file2.getPath()) + "': " + e5);
                addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.5"), file2.getPath(), null);
                z = false;
            }
        }
        return z;
    }

    private boolean massiveCosignOperation(File[] fileArr, File file, boolean z, KeyStore.PrivateKeyEntry privateKeyEntry, Properties properties) throws IOException {
        boolean zIsSign;
        AOSigner specificSigner;
        String str;
        byte[] bArrSign;
        boolean z2 = true;
        for (File file2 : fileArr) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file2);
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
                        properties.setProperty("uri", file2.toURI().toASCIIString());
                        if (this.defaultSigner.isSign(dataFromInputStream)) {
                            zIsSign = true;
                            specificSigner = this.defaultSigner;
                        } else if (z) {
                            specificSigner = getSpecificSigner(dataFromInputStream);
                            if (specificSigner != null) {
                                zIsSign = specificSigner.isSign(dataFromInputStream);
                            } else {
                                specificSigner = AOSignerFactory.getSigner(dataFromInputStream);
                                if (specificSigner != null) {
                                    zIsSign = true;
                                } else {
                                    zIsSign = false;
                                    specificSigner = this.defaultSigner;
                                }
                            }
                        } else {
                            zIsSign = false;
                            specificSigner = this.defaultSigner;
                        }
                        if (zIsSign) {
                            str = "cosign";
                            bArrSign = cosign(specificSigner, dataFromInputStream, this.algorithm, privateKeyEntry, properties);
                        } else {
                            str = CommandLineParameters.MASSIVE_OP_SIGN;
                            bArrSign = sign(specificSigner, dataFromInputStream, this.algorithm, privateKeyEntry, properties);
                        }
                        if (bArrSign == null) {
                            z2 = false;
                        } else {
                            String strSaveSignToDirectory = saveSignToDirectory(file2.getPath(), bArrSign, file, specificSigner, "." + str);
                            if (strSaveSignToDirectory == null) {
                                z2 = false;
                            } else {
                                LOGGER.info("Se ha operado (" + str + ") correctamente sobre el fichero '" + LoggerUtil.getCleanUserHomePath(file2.getPath()) + OperatorName.SHOW_TEXT_LINE);
                                addLogRegistry(Level.INFO, MassiveSignMessages.getString("DirectorySignatureHelper.10") + " (" + str + ")", file2.getPath(), strSaveSignToDirectory);
                            }
                        }
                    } catch (Throwable th3) {
                        if (fileInputStream != null) {
                            if (th != null) {
                                try {
                                    fileInputStream.close();
                                } catch (Throwable th4) {
                                    th.addSuppressed(th4);
                                }
                            } else {
                                fileInputStream.close();
                            }
                        }
                        throw th3;
                    }
                } catch (Throwable th5) {
                    th = th5;
                    throw th5;
                }
            } catch (Exception e) {
                z2 = false;
            }
        }
        return z2;
    }

    private byte[] cosign(AOSigner aOSigner, byte[] bArr, String str, KeyStore.PrivateKeyEntry privateKeyEntry, Properties properties) {
        byte[] bArrCosign;
        try {
            bArrCosign = aOSigner.cosign(bArr, str, privateKeyEntry.getPrivateKey(), privateKeyEntry.getCertificateChain(), properties);
        } catch (Exception e) {
            LOGGER.severe("No ha sido posible cofirmar el fichero '" + LoggerUtil.getCleanUserHomePath(properties.getProperty("uri")) + "': " + e);
            addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.11") + ": " + e.getMessage(), LoggerUtil.getCleanUserHomePath(properties.getProperty("uri")), null);
            bArrCosign = null;
        } catch (OutOfMemoryError e2) {
            LOGGER.severe("Error de falta de memoria durante la cofirma: " + e2);
            addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.8"), LoggerUtil.getCleanUserHomePath(properties.getProperty("uri")), null);
            bArrCosign = null;
        }
        return bArrCosign;
    }

    private byte[] sign(AOSigner aOSigner, byte[] bArr, String str, KeyStore.PrivateKeyEntry privateKeyEntry, Properties properties) throws IOException {
        String lowerCase;
        byte[] bArrDigest = bArr;
        String name = aOSigner.getClass().getName();
        if (CADES_SIGNER.equals(name) || XADES_SIGNER.equals(name) || XMLDSIG_SIGNER.equals(name)) {
            if ((XADES_SIGNER.equals(name) || XMLDSIG_SIGNER.equals(name)) && "explicit".equalsIgnoreCase(properties.getProperty("mode"))) {
                bArrDigest = digest(bArrDigest);
                lowerCase = "hash/SHA-512".toLowerCase();
            } else {
                lowerCase = new MimeHelper(bArrDigest).getMimeType();
            }
            if (lowerCase != null) {
                properties.setProperty("mimeType", lowerCase);
                String strTransformMimeTypeToOid = MimeHelper.transformMimeTypeToOid(lowerCase);
                if (strTransformMimeTypeToOid != null) {
                    properties.setProperty("contentTypeOid", strTransformMimeTypeToOid);
                }
            }
        }
        try {
            return aOSigner.sign(bArrDigest, str, privateKeyEntry.getPrivateKey(), privateKeyEntry.getCertificateChain(), properties);
        } catch (Exception e) {
            LOGGER.severe("No ha sido posible firmar el fichero de datos '" + LoggerUtil.getCleanUserHomePath(properties.getProperty("uri")) + "': " + e);
            addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.13") + ": " + e.getMessage(), LoggerUtil.getCleanUserHomePath(properties.getProperty("uri")), null);
            return null;
        }
    }

    /* JADX WARN: Code duplicated, block: B:22:0x00cb  */
    /* JADX WARN: Code duplicated, block: B:27:0x00e4 A[Catch: FileNotFoundException -> 0x0120, Exception -> 0x0164, OutOfMemoryError -> 0x01ab, TryCatch #7 {FileNotFoundException -> 0x0120, Exception -> 0x0164, OutOfMemoryError -> 0x01ab, blocks: (B:19:0x008e, B:20:0x009c, B:24:0x00d0, B:27:0x00e4, B:26:0x00da, B:30:0x00f4, B:36:0x0101, B:39:0x0115, B:38:0x010b, B:41:0x011c), top: B:66:0x008e }] */
    /* JADX WARN: Code duplicated, block: B:49:0x01e2  */
    /* JADX WARN: Code duplicated, block: B:52:0x0242  */
    /* JADX WARN: Code duplicated, block: B:53:0x0248  */
    /* JADX WARN: Code duplicated, block: B:64:0x00d0 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:66:0x008e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private boolean massiveCounterSignOperation(MassiveType massiveType, File[] fileArr, File file, boolean z, KeyStore.PrivateKeyEntry privateKeyEntry, Properties properties) throws IOException {
        FileInputStream fileInputStream;
        String strSaveSignToDirectory;
        boolean z2 = true;
        CounterSignTarget counterSignTarget = massiveType == MassiveType.COUNTERSIGN_ALL ? CounterSignTarget.TREE : CounterSignTarget.LEAFS;
        AOSigner appropiatedSigner = this.defaultSigner;
        for (File file2 : fileArr) {
            if (z) {
                try {
                    appropiatedSigner = getAppropiatedSigner(file2);
                    if (isSign(appropiatedSigner, file2)) {
                        try {
                            fileInputStream = new FileInputStream(file2);
                            Throwable th = null;
                            try {
                                try {
                                    byte[] dataFromInputStream = AOUtil.getDataFromInputStream(fileInputStream);
                                    fileInputStream.close();
                                    byte[] bArrCountersign = appropiatedSigner.countersign(dataFromInputStream, this.algorithm, counterSignTarget, null, privateKeyEntry.getPrivateKey(), privateKeyEntry.getCertificateChain(), properties);
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
                                    strSaveSignToDirectory = saveSignToDirectory(file2.getPath(), bArrCountersign, file, appropiatedSigner, ".countersign");
                                    if (strSaveSignToDirectory == null) {
                                        z2 = false;
                                    } else {
                                        LOGGER.info("El fichero se ha contrafirmado correctamente: " + LoggerUtil.getCleanUserHomePath(file2.getPath()));
                                        addLogRegistry(Level.INFO, MassiveSignMessages.getString("DirectorySignatureHelper.20"), file2.getPath(), strSaveSignToDirectory);
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
                        } catch (FileNotFoundException e) {
                            LOGGER.severe("No se ha encontrado el fichero '" + LoggerUtil.getCleanUserHomePath(file2.getPath()) + "': " + e);
                            addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.24"), file2.getPath(), null);
                            z2 = false;
                        } catch (Exception e2) {
                            LOGGER.severe("No ha sido posible contrafirmar el fichero '" + LoggerUtil.getCleanUserHomePath(file2.getPath()) + "': " + e2);
                            addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.15"), LoggerUtil.getCleanUserHomePath(file2.getPath()), null);
                            z2 = false;
                        } catch (OutOfMemoryError e3) {
                            LOGGER.severe("Error de falta de memoria durante la firma: " + e3);
                            addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.8"), LoggerUtil.getCleanUserHomePath(file2.getPath()), null);
                            z2 = false;
                        }
                    } else {
                        LOGGER.severe("El fichero '" + file2 + "' no es un fichero de firma en formato '" + properties.getProperty("format") + OperatorName.SHOW_TEXT_LINE);
                        addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.16"), LoggerUtil.getCleanUserHomePath(file2.getPath()), null);
                        z2 = false;
                    }
                } catch (Exception e4) {
                    String path = "";
                    if (file2 != null) {
                        path = file2.getPath();
                    }
                    addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.15") + ": " + e4.getMessage(), path, null);
                    z2 = false;
                }
            } else if (isSign(appropiatedSigner, file2)) {
                fileInputStream = new FileInputStream(file2);
                Throwable th6 = null;
                byte[] dataFromInputStream2 = AOUtil.getDataFromInputStream(fileInputStream);
                fileInputStream.close();
                byte[] bArrCountersign2 = appropiatedSigner.countersign(dataFromInputStream2, this.algorithm, counterSignTarget, null, privateKeyEntry.getPrivateKey(), privateKeyEntry.getCertificateChain(), properties);
                if (fileInputStream != null) {
                    if (0 != 0) {
                        fileInputStream.close();
                    } else {
                        fileInputStream.close();
                    }
                }
                strSaveSignToDirectory = saveSignToDirectory(file2.getPath(), bArrCountersign2, file, appropiatedSigner, ".countersign");
                if (strSaveSignToDirectory == null) {
                    z2 = false;
                } else {
                    LOGGER.info("El fichero se ha contrafirmado correctamente: " + LoggerUtil.getCleanUserHomePath(file2.getPath()));
                    addLogRegistry(Level.INFO, MassiveSignMessages.getString("DirectorySignatureHelper.20"), file2.getPath(), strSaveSignToDirectory);
                }
            } else {
                LOGGER.severe("El fichero '" + file2 + "' no es un fichero de firma en formato '" + properties.getProperty("format") + OperatorName.SHOW_TEXT_LINE);
                addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.16"), LoggerUtil.getCleanUserHomePath(file2.getPath()), null);
                z2 = false;
            }
        }
        return z2;
    }

    private String saveSignToDirectory(String str, byte[] bArr, File file, AOSigner aOSigner, String str2) {
        File file2;
        String relativePath = getRelativePath(str);
        String name = new File(file, relativePath).getName();
        File parentFile = new File(file, relativePath).getParentFile();
        if (!parentFile.exists()) {
            try {
                if (!parentFile.mkdirs()) {
                    LOGGER.severe("No se pudo crear la estructura de directorios del fichero '" + LoggerUtil.getCleanUserHomePath(str) + OperatorName.SHOW_TEXT_LINE);
                    addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.22"), null, str);
                    return null;
                }
            } catch (Exception e) {
                LOGGER.severe("Error al crearse la estructura de directorios del fichero '" + LoggerUtil.getCleanUserHomePath(str) + "': " + e);
                addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.21"), null, str);
                return null;
            }
        }
        int i = 0;
        File file3 = new File(parentFile, aOSigner.getSignedName(name, str2 != null ? str2 : ""));
        while (true) {
            file2 = file3;
            if (!file2.exists() || this.overwriteFiles) {
                break;
            }
            i++;
            file3 = new File(parentFile, aOSigner.getSignedName(name, (str2 != null ? str2 : "") + "(" + i + ")"));
        }
        if (!isParent(parentFile, file2)) {
            LOGGER.severe("No se pudo verificar que se fuese a guardar en un subdirectorio del directorio padre el fichero " + LoggerUtil.getCleanUserHomePath(str));
            addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.23"), null, str);
            return null;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            Throwable th = null;
            try {
                try {
                    fileOutputStream.write(bArr);
                    fileOutputStream.flush();
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
        } catch (Exception e2) {
            LOGGER.severe("No se pudo crear la estructura de directorios del fichero '" + LoggerUtil.getCleanUserHomePath(str) + "': " + e2);
            addLogRegistry(Level.SEVERE, MassiveSignMessages.getString("DirectorySignatureHelper.22"), null, file2.getPath());
        }
        this.signedFilenames.add(file2.getAbsolutePath());
        return file2.getAbsolutePath();
    }

    private static boolean isParent(File file, File file2) {
        try {
            File canonicalFile = file.getCanonicalFile();
            File canonicalFile2 = file2.getCanonicalFile();
            while (canonicalFile2 != null && !canonicalFile2.equals(canonicalFile)) {
                canonicalFile2 = canonicalFile2.getParentFile();
            }
            return canonicalFile2 != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static AOSigner getAppropiatedSigner(File file) throws IOException {
        AOSigner aOSignerDetermineType = determineType(file);
        if (aOSignerDetermineType == null) {
            throw new IllegalArgumentException("No se ha encontrado un manejador de firma valido para el fichero '" + file.getName() + OperatorName.SHOW_TEXT_LINE);
        }
        return aOSignerDetermineType;
    }

    private static boolean isSign(AOSigner aOSigner, File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        Throwable th = null;
        try {
            boolean zIsSign = aOSigner.isSign(AOUtil.getDataFromInputStream(fileInputStream));
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
            return zIsSign;
        } catch (Throwable th3) {
            if (fileInputStream != null) {
                if (0 != 0) {
                    try {
                        fileInputStream.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    fileInputStream.close();
                }
            }
            throw th3;
        }
    }

    private static boolean isValidDataFile(AOSigner aOSigner, File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
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
                    return aOSigner.isValidDataFile(dataFromInputStream);
                } catch (OutOfMemoryError e) {
                    throw new IOException("El fichero es demasiado grande: " + e, e);
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
    }

    public void setActiveLog(boolean z) {
        this.activeLog = z;
    }

    public void setLogPath(String str) {
        this.logPath = (str == null || str.trim().length() == 0) ? null : str;
    }

    public void setLogHandler(LogHandler logHandler) {
        this.logHandler = logHandler;
    }

    public String getLogPath() {
        return this.logPath;
    }

    public boolean isActiveLog() {
        return this.activeLog;
    }

    private String getRelativePath(String str) {
        if (this.inDir != null) {
            return str.substring(this.inDir.length());
        }
        int iIndexOf = str.indexOf(File.separator);
        if (iIndexOf != -1) {
            return str.substring(iIndexOf + 1);
        }
        return str;
    }

    public void setFileFilter(FileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }

    public FileFilter getFileFilter() {
        return this.fileFilter;
    }

    public AOSigner getDefaultSigner() {
        return this.defaultSigner;
    }

    public void setOverwritePreviuosFileSigns(boolean z) {
        this.overwriteFiles = z;
    }

    private void addLogRegistry(Level level, String str, String str2, String str3) {
        if (this.activeLog) {
            if (str == null) {
                LOGGER.warning("Se ha intentado insertar un registro nulo en el log");
                return;
            }
            if (this.logHandler != null) {
                try {
                    this.logHandler.addLog(level.intValue(), str, str2, str3);
                } catch (IOException e) {
                    LOGGER.warning("No se ha podido insertar un registro en el log de la operacion: " + str + ": " + e.toString());
                }
            }
            if (level == Level.WARNING) {
                this.warnCount++;
            }
            if (level == Level.SEVERE) {
                this.errorCount++;
            }
        }
    }

    protected static LogHandler initDefaultLogHandler(OutputStream outputStream) {
        return new DefaultLogHandler(outputStream);
    }

    private void closeLogRegistry() {
        if (this.logHandler != null) {
            Properties properties = new Properties();
            properties.setProperty("warningsCount", Integer.toString(this.warnCount));
            properties.setProperty("errorsCount", Integer.toString(this.errorCount));
            try {
                this.logHandler.close(properties);
            } catch (Exception e) {
                LOGGER.warning("El fichero de log no se ha cerrado correctamente: " + e);
            }
        }
        this.warnCount = 0;
        this.errorCount = 0;
    }

    private static AOSigner determineType(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("Se ha introducido un fichero de firma nulo");
        }
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("El archivo indicado no existe o no es un fichero de datos");
        }
        if (!file.canRead()) {
            throw new IOException("No tiene permisos de lectura sobre el fichero indicado");
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            Throwable th = null;
            try {
                AOSigner signer = AOSignerFactory.getSigner(AOUtil.getDataFromInputStream(fileInputStream));
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
                return signer;
            } catch (Throwable th3) {
                if (fileInputStream != null) {
                    if (0 != 0) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
                throw th3;
            }
        } catch (Exception e) {
            throw new IOException("Error al leer el fichero: " + e, e);
        }
    }

    public String[] getSignedFilenames() {
        return (String[]) this.signedFilenames.toArray(new String[0]);
    }

    private static AOSigner getSpecificSigner(byte[] bArr) throws IOException {
        for (String str : new String[]{AOSignConstants.SIGN_FORMAT_PDF, AOSignConstants.SIGN_FORMAT_ODF, AOSignConstants.SIGN_FORMAT_OOXML}) {
            AOSigner signer = AOSignerFactory.getSigner(str);
            if (signer != null && signer.isValidDataFile(bArr)) {
                return signer;
            }
        }
        return null;
    }

    private static byte[] digest(byte[] bArr) {
        if (md == null) {
            try {
                md = MessageDigest.getInstance("SHA-512");
            } catch (NoSuchAlgorithmException e) {
                LOGGER.severe("Se ha utilizado internamente un algoritmo de huella digital no soportado: " + e);
                throw new IllegalArgumentException("Algoritmo no soportado", e);
            }
        }
        return md.digest(bArr);
    }
}
