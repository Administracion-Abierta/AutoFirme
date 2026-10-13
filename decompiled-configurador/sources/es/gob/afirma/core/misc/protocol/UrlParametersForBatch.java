package es.gob.afirma.core.misc.protocol;

import es.gob.afirma.core.misc.AOUtil;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/misc/protocol/UrlParametersForBatch.class */
public final class UrlParametersForBatch extends UrlParameters {
    private static final String ID_PARAM = "id";
    private static final String PARAM_BATCH_POSTSIGNER = "batchpostsignerurl";
    private static final String PARAM_BATCH_PRESIGNER = "batchpresignerurl";
    private static final String PARAM_VER = "ver";
    private static final String PARAM_STICKY = "sticky";
    private static final String RESET_STICKY_PARAM = "resetsticky";
    private static final String PARAM_NEED_CERT = "needcert";
    private static final String PARAM_JSON_BATCH = "jsonbatch";
    private static final String PARAM_LOCAL_BATCH_PROCESS = "localBatchProcess";
    private String batchPreSignerUrl;
    private String batchPostSignerUrl;
    private final boolean servicesRequired;
    private String minimumProtocolVersion;
    private boolean sticky;
    private boolean resetSticky;
    private boolean certNeeded;
    private boolean jsonBatch;
    private boolean localBatchProcess;
    private String appName;

    public UrlParametersForBatch() {
        this(false);
    }

    public UrlParametersForBatch(boolean z) {
        this.batchPreSignerUrl = null;
        this.batchPostSignerUrl = null;
        this.servicesRequired = z;
    }

    public boolean isJsonBatch() {
        return this.jsonBatch;
    }

    public void setJsonBatch(boolean z) {
        this.jsonBatch = z;
    }

    public boolean isLocalBatchProcess() {
        return this.localBatchProcess;
    }

    public void setLocalBatchProcess(boolean z) {
        this.localBatchProcess = z;
    }

    public String getBatchPresignerUrl() {
        return this.batchPreSignerUrl;
    }

    void setBatchPresignerUrl(String str) {
        this.batchPreSignerUrl = str;
    }

    public void setSticky(boolean z) {
        this.sticky = z;
    }

    public boolean getSticky() {
        return this.sticky;
    }

    public void setResetSticky(boolean z) {
        this.resetSticky = z;
    }

    public boolean getResetSticky() {
        return this.resetSticky;
    }

    public boolean isCertNeeded() {
        return this.certNeeded;
    }

    public void setCertNeeded(boolean z) {
        this.certNeeded = z;
    }

    public String getBatchPostSignerUrl() {
        return this.batchPostSignerUrl;
    }

    void setBatchPostsignerUrl(String str) {
        this.batchPostSignerUrl = str;
    }

    public String getAppName() {
        return this.appName;
    }

    void setAppName(String str) {
        this.appName = str;
    }

    public void setBatchParameters(Map<String, String> map) throws ParameterException {
        if (map.containsKey(ID_PARAM) || map.containsKey("fileid")) {
            String str = map.containsKey(ID_PARAM) ? map.get(ID_PARAM) : map.get("fileid");
            if (str.length() > 20) {
                throw new ParameterException("La longitud del identificador de la operacion es mayor de 20 caracteres.");
            }
            for (char c : str.toLowerCase(Locale.ENGLISH).toCharArray()) {
                if ((c < 'a' || c > 'z') && (c < '0' || c > '9')) {
                    throw new ParameterException("El identificador de la firma debe ser alfanumerico.");
                }
            }
            setSessionId(str);
        }
        if (map.containsKey(PARAM_VER)) {
            setMinimumProtocolVersion(map.get(PARAM_VER));
        } else {
            setMinimumProtocolVersion(Integer.toString(ProtocolVersion.VERSION_0.getVersion()));
        }
        if (map.containsKey("appname")) {
            this.appName = map.get("appname");
        }
        if (getFileId() != null) {
            return;
        }
        if (map.containsKey(PARAM_LOCAL_BATCH_PROCESS)) {
            setLocalBatchProcess(Boolean.parseBoolean(map.get(PARAM_LOCAL_BATCH_PROCESS)));
        }
        if (!isLocalBatchProcess()) {
            if (!map.containsKey(PARAM_BATCH_POSTSIGNER)) {
                throw new ParameterException("No se ha recibido la URL del postprocesador de lotes");
            }
            if (!map.containsKey(PARAM_BATCH_PRESIGNER)) {
                throw new ParameterException("No se ha recibido la URL del preprocesador de lotes");
            }
            setBatchPostsignerUrl(validateURL(map.get(PARAM_BATCH_POSTSIGNER)).toString());
            setBatchPresignerUrl(validateURL(map.get(PARAM_BATCH_PRESIGNER)).toString());
        }
        if (this.servicesRequired) {
            if (map.containsKey("stservlet")) {
                try {
                    setStorageServletUrl(validateURL(map.get("stservlet")));
                } catch (ParameterLocalAccessRequestedException e) {
                    throw new ParameterLocalAccessRequestedException("La URL del servicio de guardado no puede ser local", e);
                } catch (ParameterException e2) {
                    throw new ParameterException("Error al validar la URL del servicio de guardado: " + e2, e2);
                }
            } else if (map.containsKey(ID_PARAM)) {
                throw new ParameterException("No se ha recibido la direccion del servlet para el guardado del resultado de la operacion");
            }
        }
        String str2 = null;
        if (map.containsKey("properties")) {
            str2 = map.get("properties");
        }
        if (str2 != null && !str2.isEmpty()) {
            try {
                setExtraParams(AOUtil.base642Properties(str2));
            } catch (Exception e3) {
                LOGGER.severe("Las propiedades adicionales indicadas en el parametro 'properties' no se han podido cargar: " + e3);
                setExtraParams(new Properties());
            }
        } else {
            setExtraParams(new Properties());
        }
        if (map.containsKey(PARAM_STICKY)) {
            setSticky(Boolean.parseBoolean(map.get(PARAM_STICKY)));
        } else {
            setSticky(false);
        }
        if (map.containsKey(RESET_STICKY_PARAM)) {
            setResetSticky(Boolean.parseBoolean(map.get(RESET_STICKY_PARAM)));
        } else {
            setResetSticky(false);
        }
        if (map.containsKey(PARAM_NEED_CERT)) {
            setCertNeeded(Boolean.parseBoolean(map.get(PARAM_NEED_CERT)));
        } else {
            setCertNeeded(false);
        }
        if (map.containsKey(PARAM_JSON_BATCH)) {
            setJsonBatch(Boolean.parseBoolean(map.get(PARAM_JSON_BATCH)));
        }
        setDefaultKeyStore(getKeyStoreName(map));
        setDefaultKeyStoreLib(getDefaultKeyStoreLib(map));
    }

    public String getMinimumProtocolVersion() {
        return this.minimumProtocolVersion;
    }

    void setMinimumProtocolVersion(String str) {
        this.minimumProtocolVersion = str;
    }
}
