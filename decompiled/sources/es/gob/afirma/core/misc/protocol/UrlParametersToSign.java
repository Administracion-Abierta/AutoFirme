package es.gob.afirma.core.misc.protocol;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.ExtraParamsProcessor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/misc/protocol/UrlParametersToSign.class */
public final class UrlParametersToSign extends UrlParameters {
    private static final String FORMAT_PARAM = "format";
    private static final String ID_PARAM = "id";
    private String operation;
    private String signFormat;
    private String signAlgorithm;
    private final boolean servicesRequired;
    private String minimumProtocolVersion;
    private boolean sticky;
    private boolean resetSticky;
    private String appName;
    private final Map<String, String> anotherParams;
    private static final String ALGORITHM_PARAM = "algorithm";
    private static final String VER_PARAM = "ver";
    private static final String STICKY_PARAM = "sticky";
    private static final String RESET_STICKY_PARAM = "resetsticky";
    private static final String[] KNOWN_PARAMETERS = {"format", ALGORITHM_PARAM, "id", VER_PARAM, STICKY_PARAM, RESET_STICKY_PARAM, "properties", "dat", "gzip", "rtservlet", "stservlet", "key", "fileid", "keystore", "ksb64", "aw", "mcv", "appname"};
    private static final Set<String> SUPPORTED_SIGNATURE_ALGORITHMS = new HashSet();

    static {
        SUPPORTED_SIGNATURE_ALGORITHMS.add(AOSignConstants.DIGEST_ALGORITHM_SHA1);
        SUPPORTED_SIGNATURE_ALGORITHMS.add("SHA256");
        SUPPORTED_SIGNATURE_ALGORITHMS.add(AOSignConstants.DIGEST_ALGORITHM_SHA384);
        SUPPORTED_SIGNATURE_ALGORITHMS.add("SHA512");
        SUPPORTED_SIGNATURE_ALGORITHMS.add(AOSignConstants.SIGN_ALGORITHM_SHA1WITHRSA);
        SUPPORTED_SIGNATURE_ALGORITHMS.add(AOSignConstants.SIGN_ALGORITHM_SHA256WITHRSA);
        SUPPORTED_SIGNATURE_ALGORITHMS.add(AOSignConstants.SIGN_ALGORITHM_SHA384WITHRSA);
        SUPPORTED_SIGNATURE_ALGORITHMS.add("SHA512withRSA");
        SUPPORTED_SIGNATURE_ALGORITHMS.add(AOSignConstants.SIGN_ALGORITHM_SHA1WITHECDSA);
        SUPPORTED_SIGNATURE_ALGORITHMS.add(AOSignConstants.SIGN_ALGORITHM_SHA256WITHECDSA);
        SUPPORTED_SIGNATURE_ALGORITHMS.add(AOSignConstants.SIGN_ALGORITHM_SHA384WITHECDSA);
        SUPPORTED_SIGNATURE_ALGORITHMS.add(AOSignConstants.SIGN_ALGORITHM_SHA512WITHECDSA);
    }

    public UrlParametersToSign() {
        this(false);
    }

    public UrlParametersToSign(boolean z) {
        this.anotherParams = new HashMap();
        this.servicesRequired = z;
        setData(null);
        setFileId(null);
        setRetrieveServletUrl(null);
    }

    public String getMinimumProtocolVersion() {
        return this.minimumProtocolVersion;
    }

    public String getOperation() {
        return this.operation;
    }

    public String getSignatureFormat() {
        return this.signFormat;
    }

    public String getSignatureAlgorithm() {
        return this.signAlgorithm;
    }

    void setOperation(String str) {
        this.operation = str;
    }

    public void setSignFormat(String str) {
        this.signFormat = str;
    }

    void setSignAlgorithm(String str) {
        this.signAlgorithm = str;
    }

    void setMinimumProtocolVersion(String str) {
        this.minimumProtocolVersion = str;
    }

    public boolean getSticky() {
        return this.sticky;
    }

    public void setSticky(boolean z) {
        this.sticky = z;
    }

    public void setResetSticky(boolean z) {
        this.resetSticky = z;
    }

    public boolean getResetSticky() {
        return this.resetSticky;
    }

    public void setAppName(String str) {
        this.appName = str;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setSignParameters(Map<String, String> map) throws ParameterException {
        String str = null;
        if (map.containsKey("id")) {
            str = map.get("id");
        } else if (map.containsKey("fileid")) {
            str = map.get("fileid");
        }
        if (str != null) {
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
        if (map.containsKey(VER_PARAM)) {
            setMinimumProtocolVersion(map.get(VER_PARAM));
        } else {
            setMinimumProtocolVersion(Integer.toString(ProtocolVersion.VERSION_0.getVersion()));
        }
        if (map.containsKey("appname")) {
            this.appName = map.get("appname");
        }
        setOperation(map.get(ProtocolConstants.OPERATION_PARAM));
        if (getFileId() != null) {
            return;
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
            } else if (map.containsKey("id")) {
                throw new ParameterException("No se ha recibido la direccion del servlet para el guardado del resultado de la operacion");
            }
        }
        if (!map.containsKey("format")) {
            throw new ParameterException("No se ha recibido el formato de firma");
        }
        setSignFormat(map.get("format"));
        if (!map.containsKey(ALGORITHM_PARAM)) {
            throw new ParameterException("No se ha recibido el algoritmo de firma");
        }
        String str2 = map.get(ALGORITHM_PARAM);
        if (!SUPPORTED_SIGNATURE_ALGORITHMS.contains(str2)) {
            throw new ParameterException("Algoritmo de firma no soportado: " + str2);
        }
        setSignAlgorithm(str2);
        String str3 = null;
        if (map.containsKey("properties")) {
            str3 = map.get("properties");
        }
        if (str3 != null && !str3.isEmpty()) {
            try {
                setExtraParams(AOUtil.base642Properties(str3));
            } catch (Exception e3) {
                LOGGER.severe("Las propiedades adicionales indicadas en el parametro 'properties' no se han podido cargar: " + e3);
                setExtraParams(new Properties());
            }
        } else {
            setExtraParams(new Properties());
        }
        if (map.containsKey(STICKY_PARAM)) {
            setSticky(Boolean.parseBoolean(map.get(STICKY_PARAM)));
        } else {
            setSticky(false);
        }
        if (map.containsKey(RESET_STICKY_PARAM)) {
            setResetSticky(Boolean.parseBoolean(map.get(RESET_STICKY_PARAM)));
        } else {
            setResetSticky(false);
        }
        setDefaultKeyStore(getKeyStoreName(map));
        setDefaultKeyStoreLib(getDefaultKeyStoreLib(map));
    }

    public void expandExtraParams() throws ExtraParamsProcessor.IncompatiblePolicyException {
        setExtraParams(ExtraParamsProcessor.expandProperties(getExtraParams(), this.data, getSignatureFormat()));
    }

    public Map<String, String> getAnotherParams() {
        return this.anotherParams;
    }

    public void setAnotherParams(Map<String, String> map) {
        for (String str : (String[]) map.keySet().toArray(new String[0])) {
            if (!contains(KNOWN_PARAMETERS, str)) {
                this.anotherParams.put(str, map.get(str));
            }
        }
    }

    private static boolean contains(String[] strArr, String str) {
        for (String str2 : strArr) {
            if (str.equals(str2)) {
                return true;
            }
        }
        return false;
    }
}
