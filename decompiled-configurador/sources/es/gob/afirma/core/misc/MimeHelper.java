package es.gob.afirma.core.misc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/misc/MimeHelper.class */
public final class MimeHelper {
    public static final String UNKNOWN_JMIMEMAGIC_VALUE = "???";
    public static final String DEFAULT_MIMETYPE = "application/octet-stream";
    public static final String DEFAULT_CONTENT_DESCRIPTION = "binary";
    public static final String DEFAULT_CONTENT_OID_DATA = "1.2.840.113549.1.7.1";
    private static final String ZIP_MIMETYPE = "application/zip";
    private static final String XML_EXTENSION = "xml";
    private static final String XML_DESCRIPTION = "Documento XML";
    private static final String XML_MIMETYPE = "text/xml";
    private static final String DOC_OFFICE_MIMETYPE = "application/msword";
    private MimeInfo mimeInfo;
    private final byte[] data;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static Properties oidMimetypeProp = null;
    private static Properties mimetypeOidProp = null;

    public MimeHelper(byte[] bArr) {
        this.mimeInfo = null;
        if (bArr == null) {
            throw new IllegalArgumentException("No se han indicado los datos que se desean analizar");
        }
        this.data = bArr;
        this.mimeInfo = new MimeInfo();
        try {
            Object objInvoke = Class.forName("net.sf.jmimemagic.Magic").getMethod("getMagicMatch", byte[].class).invoke(null, this.data);
            Class<?> cls = Class.forName("net.sf.jmimemagic.MagicMatch");
            String str = (String) cls.getMethod("getMimeType", (Class[]) null).invoke(objInvoke, (Object[]) null);
            this.mimeInfo.setMimeType(UNKNOWN_JMIMEMAGIC_VALUE.equals(str) ? null : str);
            String str2 = (String) cls.getMethod("getExtension", (Class[]) null).invoke(objInvoke, (Object[]) null);
            this.mimeInfo.setExtension(UNKNOWN_JMIMEMAGIC_VALUE.equals(str2) ? null : str2);
            String str3 = (String) cls.getMethod("getDescription", (Class[]) null).invoke(objInvoke, (Object[]) null);
            this.mimeInfo.setDescription(UNKNOWN_JMIMEMAGIC_VALUE.equals(str3) ? null : str3);
        } catch (ClassNotFoundException e) {
            LOGGER.warning("No se encontro la biblioteca JMimeMagic para la deteccion del tipo de dato: " + e);
        } catch (Exception e2) {
            try {
                Class<?> cls2 = Class.forName("net.sf.jmimemagic.MagicMatchNotFoundException");
                if (e2.getCause() != null && cls2.isInstance(e2.getCause())) {
                    LOGGER.log(Level.WARNING, "No se pudo detectar el formato de los datos: " + e2);
                } else {
                    LOGGER.log(Level.WARNING, "Error durante el analisis de la cabecera de los datos: " + e2, (Throwable) e2);
                }
            } catch (Exception e3) {
                LOGGER.warning("Error al cargar las bibliotecas de deteccion del tipo de dato: " + e3);
            }
        }
    }

    public static String transformMimeTypeToOid(String str) throws IOException {
        if (mimetypeOidProp == null) {
            loadMimetypeOidProperties();
        }
        return str == null ? DEFAULT_CONTENT_OID_DATA : mimetypeOidProp.getProperty(str, DEFAULT_CONTENT_OID_DATA);
    }

    public static String transformOidToMimeType(String str) throws IOException {
        if (oidMimetypeProp == null) {
            loadOidMimetypeProperties();
        }
        return str == null ? "application/octet-stream" : oidMimetypeProp.getProperty(str, "application/octet-stream");
    }

    private static void loadOidMimetypeProperties() throws IOException {
        oidMimetypeProp = new Properties();
        InputStream resourceAsStream = MimeHelper.class.getClassLoader().getResourceAsStream("resources/mimetypes_oids.properties");
        Throwable th = null;
        try {
            try {
                if (resourceAsStream == null) {
                    throw new IOException("No se ha encontrado el fichero de recursos para la relacion entre OID y MimeType");
                }
                oidMimetypeProp.load(resourceAsStream);
                if (resourceAsStream != null) {
                    if (0 != 0) {
                        try {
                            resourceAsStream.close();
                            return;
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                            return;
                        }
                    }
                    resourceAsStream.close();
                }
            } catch (Throwable th3) {
                throw th3;
            }
        } catch (Throwable th4) {
            if (resourceAsStream != null) {
                if (0 != 0) {
                    try {
                        resourceAsStream.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    resourceAsStream.close();
                }
            }
            throw th4;
        }
    }

    private static void loadMimetypeOidProperties() throws IOException {
        if (oidMimetypeProp == null) {
            loadOidMimetypeProperties();
        }
        mimetypeOidProp = new Properties();
        for (String str : (String[]) oidMimetypeProp.keySet().toArray(new String[0])) {
            mimetypeOidProp.put(oidMimetypeProp.get(str), str);
        }
    }

    public String getMimeType() throws IOException {
        if (this.mimeInfo.isMimeTypeVerified()) {
            return this.mimeInfo.getMimeType();
        }
        String mimeType = this.mimeInfo.getMimeType();
        if (mimeType == null || XML_MIMETYPE.equals(mimeType)) {
            if (AOFileUtils.isXML(this.data)) {
                this.mimeInfo.setMimeType(XML_MIMETYPE);
                this.mimeInfo.setMimeTypeVerified(true);
            } else {
                mimeType = null;
            }
        }
        if (ZIP_MIMETYPE.equals(mimeType) || DOC_OFFICE_MIMETYPE.equals(mimeType)) {
            this.mimeInfo.setMimeType(OfficeAnalizer.getMimeType(this.data));
            this.mimeInfo.setMimeTypeVerified(true);
        }
        if (mimeType == null) {
            this.mimeInfo.setMimeType("application/octet-stream");
            this.mimeInfo.setMimeTypeVerified(true);
        }
        return this.mimeInfo.getMimeType();
    }

    public String getExtension() throws IOException {
        if (this.mimeInfo.isExtensionVerified()) {
            return this.mimeInfo.getExtension();
        }
        String strVerifyExtension = verifyExtension(getMimeType());
        if (strVerifyExtension != null) {
            this.mimeInfo.setExtension(strVerifyExtension);
        }
        this.mimeInfo.setExtensionVerified(true);
        return this.mimeInfo.getExtension();
    }

    private static String verifyExtension(String str) {
        String extension;
        if ("application/octet-stream".equals(str)) {
            return null;
        }
        if (XML_MIMETYPE.equals(str)) {
            return "xml";
        }
        if (str != null && (extension = OfficeAnalizer.getExtension(str)) != null) {
            return extension;
        }
        return null;
    }

    public String getDescription() throws IOException {
        if (this.mimeInfo.isDescriptionVerified()) {
            return this.mimeInfo.getDescription();
        }
        String strVerifyDescription = verifyDescription(getMimeType());
        if (strVerifyDescription == null) {
            if (this.mimeInfo.getDescription() != null) {
                strVerifyDescription = this.mimeInfo.getDescription();
            } else {
                strVerifyDescription = DEFAULT_CONTENT_DESCRIPTION;
            }
        }
        this.mimeInfo.setDescription(strVerifyDescription);
        this.mimeInfo.setDescriptionVerified(true);
        return this.mimeInfo.getDescription();
    }

    private static String verifyDescription(String str) {
        String description;
        if (XML_MIMETYPE.equals(str)) {
            return XML_DESCRIPTION;
        }
        if (str != null && !str.equals("application/octet-stream") && (description = OfficeAnalizer.getDescription(str)) != null) {
            return description;
        }
        return null;
    }

    public boolean isZipData() {
        if (this.mimeInfo != null) {
            return ZIP_MIMETYPE.equals(this.mimeInfo.getMimeType());
        }
        return this.data.length >= 4 && this.data[0] == 80 && this.data[1] == 75 && ((this.data[2] == 3 && this.data[3] == 4) || ((this.data[2] == 5 && this.data[3] == 6) || (this.data[2] == 7 && this.data[3] == 8)));
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/misc/MimeHelper$MimeInfo.class */
    static class MimeInfo {
        private String mType = null;
        private boolean mTypeVerified = false;
        private String extension = null;
        private boolean extensionVerified = false;
        private String description = null;
        private boolean descriptionVerified = false;

        MimeInfo() {
        }

        String getMimeType() {
            return this.mType;
        }

        void setMimeType(String str) {
            this.mType = str;
        }

        boolean isMimeTypeVerified() {
            return this.mTypeVerified;
        }

        void setMimeTypeVerified(boolean z) {
            this.mTypeVerified = z;
        }

        String getExtension() {
            return this.extension;
        }

        void setExtension(String str) {
            this.extension = str;
        }

        boolean isExtensionVerified() {
            return this.extensionVerified;
        }

        void setExtensionVerified(boolean z) {
            this.extensionVerified = z;
        }

        String getDescription() {
            return this.description;
        }

        void setDescription(String str) {
            this.description = str;
        }

        boolean isDescriptionVerified() {
            return this.descriptionVerified;
        }

        void setDescriptionVerified(boolean z) {
            this.descriptionVerified = z;
        }
    }
}
