package es.gob.afirma.core.misc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/misc/OfficeAnalizer.class */
public final class OfficeAnalizer {
    private static final String ZIP_MIMETYPE = "application/zip";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final Set<String> OOXML_MIMETYPES = new HashSet(17);
    private static final Set<String> ODF_MIMETYPES = new HashSet(15);
    private static final Map<String, String> FILE_EXTENSIONS = new HashMap();
    private static final Map<String, String> FILE_DESCRIPTIONS = new HashMap();
    private static final long THRESHOLD_FILE_SIZE = 1000000000;

    private OfficeAnalizer() {
    }

    static {
        OOXML_MIMETYPES.add("application/vnd.ms-word.document.macroEnabled.12");
        OOXML_MIMETYPES.add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        OOXML_MIMETYPES.add("application/vnd.ms-word.template.macroEnabled.12");
        OOXML_MIMETYPES.add("application/vnd.openxmlformats-officedocument.wordprocessingml.template");
        OOXML_MIMETYPES.add("application/vnd.ms-powerpoint.template.macroEnabled.12");
        OOXML_MIMETYPES.add("application/vnd.openxmlformats-officedocument.presentationml.template");
        OOXML_MIMETYPES.add("application/vnd.ms-powerpoint.addin.macroEnabled.12");
        OOXML_MIMETYPES.add("application/vnd.ms-powerpoint.slideshow.macroEnabled.12");
        OOXML_MIMETYPES.add("application/vnd.openxmlformats-officedocument.presentationml.slideshow");
        OOXML_MIMETYPES.add("application/vnd.ms-powerpoint.presentation.macroEnabled.12");
        OOXML_MIMETYPES.add("application/vnd.openxmlformats-officedocument.presentationml.presentation");
        OOXML_MIMETYPES.add("application/vnd.ms-excel.addin.macroEnabled.12");
        OOXML_MIMETYPES.add("application/vnd.ms-excel.sheet.binary.macroEnabled.12");
        OOXML_MIMETYPES.add("application/vnd.ms-excel.sheet.macroEnabled.12");
        OOXML_MIMETYPES.add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        OOXML_MIMETYPES.add("application/vnd.ms-excel.template.macroEnabled.12");
        OOXML_MIMETYPES.add("application/vnd.openxmlformats-officedocument.spreadsheetml.template");
        ODF_MIMETYPES.add("application/vnd.oasis.opendocument.text");
        ODF_MIMETYPES.add("application/vnd.oasis.opendocument.text-template");
        ODF_MIMETYPES.add("application/vnd.oasis.opendocument.text-web");
        ODF_MIMETYPES.add("application/vnd.oasis.opendocument.text-master");
        ODF_MIMETYPES.add("application/vnd.oasis.opendocument.graphics");
        ODF_MIMETYPES.add("application/vnd.oasis.opendocument.graphics-template");
        ODF_MIMETYPES.add("application/vnd.oasis.opendocument.presentation");
        ODF_MIMETYPES.add("application/vnd.oasis.opendocument.presentation-template");
        ODF_MIMETYPES.add("application/vnd.oasis.opendocument.spreadsheet");
        ODF_MIMETYPES.add("application/vnd.oasis.opendocument.spreadsheet-template");
        ODF_MIMETYPES.add("application/vnd.oasis.opendocument.chart");
        ODF_MIMETYPES.add("application/vnd.oasis.opendocument.formula");
        ODF_MIMETYPES.add("application/vnd.oasis.opendocument.database");
        ODF_MIMETYPES.add("application/vnd.oasis.opendocument.image");
        ODF_MIMETYPES.add("application/vnd.openofficeorg.extension");
        FILE_EXTENSIONS.put(ZIP_MIMETYPE, "zip");
        FILE_EXTENSIONS.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
        FILE_EXTENSIONS.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx");
        FILE_EXTENSIONS.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");
        FILE_EXTENSIONS.put("application/vnd.oasis.opendocument.text", "odt");
        FILE_EXTENSIONS.put("application/vnd.oasis.opendocument.presentation", "odp");
        FILE_EXTENSIONS.put("application/vnd.oasis.opendocument.spreadsheet", "ods");
        FILE_EXTENSIONS.put("application/vnd.oasis.opendocument.graphics", "odg");
        FILE_EXTENSIONS.put("application/vnd.oasis.opendocument.chart", "odc");
        FILE_EXTENSIONS.put("application/vnd.oasis.opendocument.formula", "odf");
        FILE_EXTENSIONS.put("application/vnd.oasis.opendocument.database", "odb");
        FILE_EXTENSIONS.put("application/vnd.oasis.opendocument.image", "odi");
        FILE_EXTENSIONS.put("application/vnd.oasis.opendocument.text-master", "odm");
        FILE_EXTENSIONS.put("application/msword", "doc");
        FILE_EXTENSIONS.put("application/vnd.ms-powerpoint", "ppt");
        FILE_EXTENSIONS.put("application/vnd.ms-excel", "xls");
        FILE_EXTENSIONS.put("application/vnd.ms-project", "mpp");
        FILE_DESCRIPTIONS.put(ZIP_MIMETYPE, "Archivo zip");
        FILE_DESCRIPTIONS.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "Documento de texto");
        FILE_DESCRIPTIONS.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", "Presentación");
        FILE_DESCRIPTIONS.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Hoja de cálculo");
        FILE_DESCRIPTIONS.put("application/vnd.oasis.opendocument.text", "Documento de texto");
        FILE_DESCRIPTIONS.put("application/vnd.oasis.opendocument.presentation", "Presentación");
        FILE_DESCRIPTIONS.put("application/vnd.oasis.opendocument.spreadsheet", "Hoja de cálculo");
        FILE_DESCRIPTIONS.put("application/vnd.oasis.opendocument.graphics", "Gráfico");
        FILE_DESCRIPTIONS.put("application/vnd.oasis.opendocument.chart", "Diagrama");
        FILE_DESCRIPTIONS.put("application/vnd.oasis.opendocument.formula", "Fórmula");
        FILE_DESCRIPTIONS.put("application/vnd.oasis.opendocument.database", "Base de datos");
        FILE_DESCRIPTIONS.put("application/vnd.oasis.opendocument.image", "Imagen");
        FILE_DESCRIPTIONS.put("application/vnd.oasis.opendocument.text-master", "Plantilla de documento");
        FILE_DESCRIPTIONS.put("application/msword", "Documento de texto");
        FILE_DESCRIPTIONS.put("application/vnd.ms-powerpoint", "Presentación");
        FILE_DESCRIPTIONS.put("application/vnd.ms-excel", "Hoja de cálculo");
        FILE_DESCRIPTIONS.put("application/vnd.ms-project", "Planificaci&oacute;n de proyecto");
    }

    /* JADX WARN: Not initialized variable reg: 8, insn: 0x014d: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r8 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:76:0x014d */
    /* JADX WARN: Not initialized variable reg: 9, insn: 0x0151: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r9 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:78:0x0151 */
    static String getMimeType(byte[] bArr) throws IOException {
        ZipFile zipFile;
        Throwable th;
        String mimeTypeOffice97 = null;
        if (bArr.length >= THRESHOLD_FILE_SIZE) {
            throw new IOException("El archivo tiene un tamano superior al permitido.");
        }
        File fileCreateTempFile = AOFileUtils.createTempFile(bArr);
        try {
            try {
                ZipFile zipFile2 = new ZipFile(fileCreateTempFile);
                Throwable th2 = null;
                if (isODFFile(zipFile2)) {
                    InputStream inputStream = zipFile2.getInputStream(zipFile2.getEntry("mimetype"));
                    Throwable th3 = null;
                    try {
                        try {
                            mimeTypeOffice97 = getODFMimeType(inputStream);
                            if (inputStream != null) {
                                if (0 != 0) {
                                    try {
                                        inputStream.close();
                                    } catch (Throwable th4) {
                                        th3.addSuppressed(th4);
                                    }
                                } else {
                                    inputStream.close();
                                }
                            }
                        } catch (Throwable th5) {
                            th3 = th5;
                            throw th5;
                        }
                    } catch (Throwable th6) {
                        if (inputStream != null) {
                            if (th3 != null) {
                                try {
                                    inputStream.close();
                                } catch (Throwable th7) {
                                    th3.addSuppressed(th7);
                                }
                            } else {
                                inputStream.close();
                            }
                        }
                        throw th6;
                    }
                } else if (isOOXMLFile(zipFile2)) {
                    InputStream inputStream2 = zipFile2.getInputStream(zipFile2.getEntry("[Content_Types].xml"));
                    Throwable th8 = null;
                    try {
                        try {
                            mimeTypeOffice97 = getOOXMLMimeType(inputStream2);
                            if (inputStream2 != null) {
                                if (0 != 0) {
                                    try {
                                        inputStream2.close();
                                    } catch (Throwable th9) {
                                        th8.addSuppressed(th9);
                                    }
                                } else {
                                    inputStream2.close();
                                }
                            }
                        } catch (Throwable th10) {
                            th8 = th10;
                            throw th10;
                        }
                    } catch (Throwable th11) {
                        if (inputStream2 != null) {
                            if (th8 != null) {
                                try {
                                    inputStream2.close();
                                } catch (Throwable th12) {
                                    th8.addSuppressed(th12);
                                }
                            } else {
                                inputStream2.close();
                            }
                        }
                        throw th11;
                    }
                } else {
                    mimeTypeOffice97 = getMimeTypeOffice97(bArr);
                }
                if (mimeTypeOffice97 == null) {
                    mimeTypeOffice97 = ZIP_MIMETYPE;
                }
                if (zipFile2 != null) {
                    if (0 != 0) {
                        try {
                            zipFile2.close();
                        } catch (Throwable th13) {
                            th2.addSuppressed(th13);
                        }
                    } else {
                        zipFile2.close();
                    }
                }
            } catch (Throwable th14) {
                if (zipFile != null) {
                    if (th != null) {
                        try {
                            zipFile.close();
                        } catch (Throwable th15) {
                            th.addSuppressed(th15);
                        }
                    } else {
                        zipFile.close();
                    }
                }
                throw th14;
            }
        } catch (ZipException e) {
        }
        try {
            Files.delete(fileCreateTempFile.toPath());
        } catch (IOException e2) {
            LOGGER.warning("No se ha podido eliminar el fichero temporal:  " + e2);
        }
        if (mimeTypeOffice97 == null) {
            mimeTypeOffice97 = getMimeTypeOffice97(bArr);
        }
        if (mimeTypeOffice97 == null) {
            mimeTypeOffice97 = "application/octect-stream";
        }
        return mimeTypeOffice97;
    }

    private static String getMimeTypeOffice97(byte[] bArr) {
        String str = new String(bArr);
        if (str.contains("Microsoft Excel")) {
            return "application/vnd.ms-excel";
        }
        if (str.contains("Microsoft Office Word")) {
            return "application/msword";
        }
        if (str.contains("Microsoft Office PowerPoint")) {
            return "application/vnd.ms-powerpoint";
        }
        if (str.contains("Microsoft Project")) {
            return "application/vnd.ms-project";
        }
        if (str.contains("Microsoft Visio")) {
            return "application/vnd.visio";
        }
        return null;
    }

    static String getExtension(byte[] bArr) throws IOException {
        return FILE_EXTENSIONS.get(getMimeType(bArr));
    }

    static String getExtension(String str) {
        if (str == null) {
            return null;
        }
        return FILE_EXTENSIONS.get(str);
    }

    static String getDescription(byte[] bArr) throws IOException {
        return FILE_DESCRIPTIONS.get(getMimeType(bArr));
    }

    static String getDescription(String str) {
        if (str == null) {
            return null;
        }
        return FILE_DESCRIPTIONS.get(str);
    }

    public static boolean isOOXMLDocument(byte[] bArr) {
        boolean zIsOOXMLFile;
        File fileCreateTempFile = null;
        try {
            if (bArr.length >= THRESHOLD_FILE_SIZE) {
                throw new IOException("El archivo tiene un tamano superior al permitido.");
            }
            fileCreateTempFile = AOFileUtils.createTempFile(bArr);
            ZipFile zipFile = new ZipFile(fileCreateTempFile);
            Throwable th = null;
            try {
                try {
                    zIsOOXMLFile = isOOXMLFile(zipFile);
                    if (zipFile != null) {
                        if (0 != 0) {
                            try {
                                zipFile.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            zipFile.close();
                        }
                    }
                } catch (Throwable th3) {
                    if (zipFile != null) {
                        if (th != null) {
                            try {
                                zipFile.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            zipFile.close();
                        }
                    }
                    throw th3;
                }
            } catch (Throwable th5) {
                th = th5;
                throw th5;
            }
        } catch (Exception e) {
            zIsOOXMLFile = false;
        }
        if (fileCreateTempFile != null) {
            try {
                Files.delete(fileCreateTempFile.toPath());
            } catch (IOException e2) {
                LOGGER.warning("No se ha podido eliminar el fichero temporal:  " + e2);
            }
        }
        return zIsOOXMLFile;
    }

    private static boolean isOOXMLFile(ZipFile zipFile) throws IOException {
        return (zipFile.getEntry("[Content_Types].xml") == null || zipFile.getEntry("_rels/.rels") == null || zipFile.getEntry("docProps/app.xml") == null || zipFile.getEntry("docProps/core.xml") == null) ? false : true;
    }

    public static String getOOXMLMimeType(InputStream inputStream) {
        try {
            Element documentElement = SecureXmlBuilder.getSecureDocumentBuilder().parse(inputStream).getDocumentElement();
            if (!documentElement.getNodeName().equalsIgnoreCase("Types")) {
                return null;
            }
            NodeList childNodes = documentElement.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node nodeItem = childNodes.item(i);
                if (nodeItem.getNodeName().equalsIgnoreCase("Override")) {
                    NamedNodeMap attributes = nodeItem.getAttributes();
                    Node nodeItem2 = null;
                    for (int i2 = 0; i2 < attributes.getLength(); i2++) {
                        if (attributes.item(i2).getNodeName().equalsIgnoreCase("ContentType")) {
                            nodeItem2 = attributes.item(i2);
                            break;
                        }
                    }
                    if (nodeItem2 != null) {
                        String nodeValue = nodeItem2.getNodeValue();
                        if (nodeValue.indexOf(46) != -1) {
                            nodeValue = nodeValue.substring(0, nodeValue.lastIndexOf(46));
                        }
                        if (OOXML_MIMETYPES.contains(nodeValue)) {
                            return nodeValue;
                        }
                    } else {
                        continue;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            LOGGER.warning("No se ha podido analizar el XML de ContentTypes de OOXML: " + e);
            return null;
        }
    }

    public static boolean isODFDocument(byte[] bArr) throws IOException {
        boolean zIsODFFile;
        File fileCreateTempFile = null;
        try {
            if (bArr.length >= THRESHOLD_FILE_SIZE) {
                throw new IOException("El archivo tiene un tamano superior al permitido.");
            }
            fileCreateTempFile = AOFileUtils.createTempFile(bArr);
            ZipFile zipFile = new ZipFile(fileCreateTempFile);
            Throwable th = null;
            try {
                try {
                    zIsODFFile = isODFFile(zipFile);
                    if (zipFile != null) {
                        if (0 != 0) {
                            try {
                                zipFile.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            zipFile.close();
                        }
                    }
                } catch (Throwable th3) {
                    if (zipFile != null) {
                        if (th != null) {
                            try {
                                zipFile.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            zipFile.close();
                        }
                    }
                    throw th3;
                }
            } catch (Throwable th5) {
                th = th5;
                throw th5;
            }
        } catch (Exception e) {
            zIsODFFile = false;
        }
        if (fileCreateTempFile != null) {
            try {
                Files.delete(fileCreateTempFile.toPath());
            } catch (IOException e2) {
                LOGGER.warning("No se ha podido eliminar el fichero temporal:  " + e2);
            }
        }
        return zIsODFFile;
    }

    private static boolean isODFFile(ZipFile zipFile) throws IOException {
        if (zipFile.getEntry("mimetype") == null || zipFile.getEntry("content.xml") == null || zipFile.getEntry("meta.xml") == null || zipFile.getEntry("settings.xml") == null || zipFile.getEntry("styles.xml") == null || zipFile.getEntry("META-INF/manifest.xml") == null) {
            return false;
        }
        return true;
    }

    private static String getODFMimeType(InputStream inputStream) {
        try {
            String str = new String(AOUtil.getDataFromInputStream(inputStream));
            if (ODF_MIMETYPES.contains(str)) {
                return str;
            }
            return null;
        } catch (Exception e) {
            LOGGER.warning("No se ha podido obtener el tipo de contenido ODF del flujo de entrada: " + e);
            return null;
        }
    }
}
