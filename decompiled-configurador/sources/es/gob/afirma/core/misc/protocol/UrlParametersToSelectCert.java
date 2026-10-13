package es.gob.afirma.core.misc.protocol;

import es.gob.afirma.core.misc.AOUtil;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/misc/protocol/UrlParametersToSelectCert.class */
public final class UrlParametersToSelectCert extends UrlParameters {
    private static final String ID_PARAM = "id";
    private static final String VER_PARAM = "ver";
    private static final String STICKY_PARAM = "sticky";
    private static final String RESET_STICKY_PARAM = "resetsticky";
    private final boolean servicesRequired;
    private String minimumVerstion;
    private boolean sticky;
    private boolean resetSticky;

    public UrlParametersToSelectCert() {
        this(false);
    }

    public UrlParametersToSelectCert(boolean z) {
        this.servicesRequired = z;
        setData(null);
        setFileId(null);
        setRetrieveServletUrl(null);
    }

    public String getMinimumProtocolVersion() {
        return this.minimumVerstion;
    }

    void setMinimumProtocolVersion(String str) {
        this.minimumVerstion = str;
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

    public void setSelectCertParameters(Map<String, String> map) throws ParameterException {
        String str = null;
        if (map.containsKey(ID_PARAM)) {
            str = map.get(ID_PARAM);
        } else if (map.containsKey("fileid")) {
            str = map.get("fileid");
        }
        if (str != null) {
            if (str.length() > 20) {
                throw new ParameterException("La longitud del identificador de la operacion es mayor de 20 caracteres.");
            }
            for (char c : str.toLowerCase(Locale.ENGLISH).toCharArray()) {
                if ((c < 'a' || c > 'z') && (c < '0' || c > '9')) {
                    throw new ParameterException("El identificador de la sesion debe ser alfanumerico.");
                }
            }
            setSessionId(str);
        }
        if (map.containsKey(VER_PARAM)) {
            setMinimumProtocolVersion(map.get(VER_PARAM));
        } else {
            setMinimumProtocolVersion(Integer.toString(ProtocolVersion.VERSION_0.getVersion()));
        }
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
}
