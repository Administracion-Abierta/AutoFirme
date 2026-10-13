package es.gob.afirma.core.misc.protocol;

import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.http.DataDownloader;
import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/misc/protocol/UrlParameters.class */
public abstract class UrlParameters {
    protected static final String PROPERTIES_PARAM = "properties";
    protected static final String DATA_PARAM = "dat";
    protected static final String GZIPPED_DATA_PARAM = "gzip";
    protected static final String RETRIEVE_SERVLET_PARAM = "rtservlet";
    protected static final String STORAGE_SERVLET_PARAM = "stservlet";
    protected static final int MAX_ID_LENGTH = 20;
    private static final int CIPHER_KEY_LENGTH = 8;
    protected static final String KEY_PARAM = "key";
    protected static final String FILE_ID_PARAM = "fileid";
    protected static final String KEYSTORE_OLD_PARAM = "keystore";
    protected static final String KEYSTORE_PARAM = "ksb64";
    protected static final String ACTIVE_WAITING_PARAM = "aw";
    protected static final String MINIMUM_CLIENT_VERSION_PARAM = "mcv";
    protected static final String APP_NAME_PARAM = "appname";
    protected byte[] data = null;
    private String fileId = null;
    private byte[] desKey = null;
    private URL retrieveServletUrl = null;
    private URL storageServerUrl = null;
    private String id = null;
    private String minimumClientVersion = null;
    private boolean activeWaiting = false;
    private String defaultKeyStore = null;
    private String defaultKeyStoreLib = null;
    private Properties extraParams = null;
    private String filename = null;
    protected static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();

    public Properties getExtraParams() {
        if (this.extraParams == null) {
            this.extraParams = new Properties();
        }
        return this.extraParams;
    }

    void setExtraParams(Properties properties) {
        this.extraParams = properties != null ? properties : new Properties();
    }

    public String getFileName() {
        return this.filename;
    }

    void setFilename(String str) {
        this.filename = str;
    }

    public String getDefaultKeyStore() {
        return this.defaultKeyStore;
    }

    void setDefaultKeyStore(String str) {
        this.defaultKeyStore = str;
    }

    public String getDefaultKeyStoreLib() {
        return this.defaultKeyStoreLib;
    }

    void setDefaultKeyStoreLib(String str) {
        this.defaultKeyStoreLib = str;
    }

    public void setData(byte[] bArr) {
        this.data = bArr != null ? Arrays.copyOf(bArr, bArr.length) : null;
    }

    void setFileId(String str) {
        this.fileId = str;
    }

    private void setDesKey(byte[] bArr) {
        this.desKey = bArr != null ? Arrays.copyOf(bArr, bArr.length) : null;
    }

    void setRetrieveServletUrl(URL url) {
        this.retrieveServletUrl = url;
    }

    public byte[] getData() {
        if (this.data != null) {
            return Arrays.copyOf(this.data, this.data.length);
        }
        return null;
    }

    public String getFileId() {
        return this.fileId;
    }

    public byte[] getDesKey() {
        if (this.desKey != null) {
            return Arrays.copyOf(this.desKey, this.desKey.length);
        }
        return null;
    }

    public URL getRetrieveServletUrl() {
        return this.retrieveServletUrl;
    }

    public URL getStorageServletUrl() {
        return this.storageServerUrl;
    }

    protected void setStorageServletUrl(URL url) {
        this.storageServerUrl = url;
    }

    public boolean isActiveWaiting() {
        return this.activeWaiting;
    }

    protected void setActiveWaiting(boolean z) {
        this.activeWaiting = z;
    }

    public String getMinimumClientVersion() {
        return this.minimumClientVersion;
    }

    protected void setMinimumClientVersion(String str) {
        this.minimumClientVersion = str;
    }

    public String getId() {
        return this.id;
    }

    void setSessionId(String str) {
        this.id = str;
    }

    void setCommonParameters(Map<String, String> map) throws ParameterException {
        setDesKey(verifyCipherKey(map));
        setActiveWaiting(map.containsKey(ACTIVE_WAITING_PARAM) && Boolean.parseBoolean(map.get(ACTIVE_WAITING_PARAM)));
        if (map.containsKey(MINIMUM_CLIENT_VERSION_PARAM)) {
            setMinimumClientVersion(map.get(MINIMUM_CLIENT_VERSION_PARAM));
        }
        if (!map.containsKey(DATA_PARAM)) {
            if (map.containsKey(FILE_ID_PARAM)) {
                setFileId(map.get(FILE_ID_PARAM));
                if (!map.containsKey(RETRIEVE_SERVLET_PARAM)) {
                    throw new ParameterException("No se ha recibido la direccion del servlet para la recuperacion de los datos a firmar");
                }
                try {
                    setRetrieveServletUrl(validateURL(map.get(RETRIEVE_SERVLET_PARAM)));
                    return;
                } catch (ParameterLocalAccessRequestedException e) {
                    throw new ParameterLocalAccessRequestedException("La URL del servicio de recuperacion de datos no puede ser local", e);
                } catch (ParameterException e2) {
                    throw new ParameterException("Error al validar la URL del servlet de recuperacion: " + e2, e2);
                }
            }
            return;
        }
        String str = map.get(DATA_PARAM);
        if (str.startsWith("file:/")) {
            throw new ParameterException("No se permite la lectura de ficheros locales: " + str);
        }
        try {
            setData(DataDownloader.downloadData(str, Boolean.parseBoolean(map.get(GZIPPED_DATA_PARAM))));
        } catch (Exception e3) {
            throw new ParameterException("No se han podido obtener los datos: " + e3, e3);
        }
    }

    private static byte[] verifyCipherKey(Map<String, String> map) throws ParameterException {
        String str;
        if (!map.containsKey(KEY_PARAM) || (str = map.get(KEY_PARAM)) == null || str.length() == 0) {
            return null;
        }
        if (str.length() != 8) {
            throw new ParameterException("La longitud de la clave de cifrado no es correcta");
        }
        return str.getBytes();
    }

    protected static URL validateURL(String str) throws ParameterException {
        try {
            URL url = new URL(URLDecoder.decode(str, DEFAULT_ENCODING));
            if (!"http".equals(url.getProtocol()) && !"https".equals(url.getProtocol())) {
                throw new ParameterException("El protocolo de la URL proporcionada para el servlet no esta soportado: " + url.getProtocol());
            }
            if ("localhost".equals(url.getHost()) || "127.0.0.1".equals(url.getHost())) {
                throw new ParameterLocalAccessRequestedException("El host de la URL proporcionada para el Servlet es local");
            }
            if (url.toString().indexOf(63) != -1 || url.toString().indexOf(61) != -1) {
                throw new ParameterException("Se han encontrado parametros en la URL del servlet");
            }
            return url;
        } catch (Exception e) {
            throw new ParameterException("La URL proporcionada para el servlet no es valida (" + str + "): " + e);
        }
    }

    protected static String getKeyStoreName(Map<String, String> map) {
        String str = null;
        if (map.get(KEYSTORE_OLD_PARAM) != null) {
            str = map.get(KEYSTORE_OLD_PARAM);
        } else if (map.get(KEYSTORE_PARAM) != null) {
            try {
                str = new String(Base64.decode(map.get(KEYSTORE_PARAM)));
            } catch (Exception e) {
            }
        }
        if (str != null) {
            int iIndexOf = str.indexOf(58);
            if (iIndexOf == -1) {
                return str;
            }
            if (str.length() > 1) {
                return str.substring(0, iIndexOf).trim();
            }
            LOGGER.info("El almacen indicado no es valido ('" + str + "'), se usara el por defecto del sistema operativo");
            return null;
        }
        return null;
    }

    protected static String getDefaultKeyStoreLib(Map<String, String> map) {
        int iIndexOf;
        String str = null;
        if (map.get(KEYSTORE_OLD_PARAM) != null) {
            str = map.get(KEYSTORE_OLD_PARAM);
        } else if (map.get(KEYSTORE_PARAM) != null) {
            try {
                str = new String(Base64.decode(map.get(KEYSTORE_PARAM)));
            } catch (Exception e) {
            }
        }
        if (str != null && (iIndexOf = str.indexOf(58)) != -1 && iIndexOf < str.length() - 1) {
            return cleanupPath(str.substring(iIndexOf + 1));
        }
        return null;
    }

    private static String cleanupPath(String str) {
        String canonicalPath;
        try {
            canonicalPath = new File(str.trim().replace(OperatorName.SHOW_TEXT_LINE_AND_SPACE, "").replace(OperatorName.SHOW_TEXT_LINE, "")).getCanonicalPath();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Ruta de fichero no valida", (Throwable) e);
            canonicalPath = null;
        }
        return canonicalPath;
    }
}
