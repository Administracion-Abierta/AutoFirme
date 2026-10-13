package es.gob.afirma.core.misc.protocol;

import java.util.Locale;
import java.util.Map;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/misc/protocol/UrlParametersToSave.class */
public final class UrlParametersToSave extends UrlParameters {
    private static final String TITLE_PARAM = "title";
    private static final String FILETYPE_DESCRIPTION_PARAM = "desc";
    private static final String FILENAME_PARAM = "filename";
    private static final String FILENAME_EXTS_PARAM = "exts";
    private static final String ID_PARAM = "id";
    private static final String VER_PARAM = "ver";
    private String title;
    private String filename;
    private String extensions;
    private String fileTypeDescription;
    private final boolean servicesRequired;
    private String minimumProtocolVersion;

    public UrlParametersToSave() {
        this(false);
    }

    public UrlParametersToSave(boolean z) {
        this.title = null;
        this.filename = null;
        this.extensions = null;
        this.fileTypeDescription = null;
        this.minimumProtocolVersion = null;
        this.servicesRequired = z;
    }

    void setFileTypeDescription(String str) {
        this.fileTypeDescription = str;
    }

    void setExtensions(String str) {
        this.extensions = str;
    }

    @Override // es.gob.afirma.core.misc.protocol.UrlParameters
    void setFilename(String str) {
        this.filename = str;
    }

    void setTitle(String str) {
        this.title = str;
    }

    void setMinimumProtocolVersion(String str) {
        this.minimumProtocolVersion = str;
    }

    public String getFileTypeDescription() {
        return this.fileTypeDescription;
    }

    public String getExtensions() {
        return this.extensions;
    }

    @Override // es.gob.afirma.core.misc.protocol.UrlParameters
    public String getFileName() {
        return this.filename;
    }

    public String getTitle() {
        return this.title;
    }

    public String getMinimumProtocolVersion() {
        return this.minimumProtocolVersion;
    }

    public void setSaveParameters(Map<String, String> map) throws ParameterException {
        if (!map.containsKey("fileid") && !map.containsKey("dat")) {
            throw new ParameterException("No se ha proporcionado el identificador de fichero ni los datos a guardar");
        }
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
        setTitle(verifyTitle(map));
        setFilename(verifyFilename(map));
        setExtensions(verifyExtensions(map));
        setFileTypeDescription(verifyFileTypeDescription(map));
    }

    private static String verifyFilename(Map<String, String> map) throws ParameterException {
        String str = null;
        if (map.containsKey(FILENAME_PARAM)) {
            str = map.get(FILENAME_PARAM);
            for (char c : "\\/:*?\"<>|".toCharArray()) {
                if (str.indexOf(c) != -1) {
                    throw new ParameterException("Se ha indicado un nombre de fichero con el caracter invalido: " + c);
                }
            }
        }
        return str;
    }

    private static String verifyExtensions(Map<String, String> map) throws ParameterException {
        String str = null;
        if (map.containsKey(FILENAME_EXTS_PARAM)) {
            str = map.get(FILENAME_EXTS_PARAM);
            for (char c : "\\/:*?\"<>|; ".toCharArray()) {
                if (str.indexOf(c) != -1) {
                    throw new ParameterException("Se ha indicado una lista de extensiones de nombre de fichero con caracteres invalidos: " + c);
                }
            }
        }
        return str;
    }

    private static String verifyTitle(Map<String, String> map) {
        if (map.containsKey("title")) {
            return map.get("title");
        }
        return ProtocoloMessages.getString("ProtocolInvocationUriParser.1");
    }

    private static String verifyFileTypeDescription(Map<String, String> map) {
        String string = null;
        if (map.containsKey(FILETYPE_DESCRIPTION_PARAM)) {
            string = map.get(FILETYPE_DESCRIPTION_PARAM);
            if (map.containsKey(FILENAME_EXTS_PARAM) && !string.endsWith(")")) {
                StringBuilder sbAppend = new StringBuilder(string).append(" (");
                for (String str : map.get(FILENAME_EXTS_PARAM).split(",")) {
                    sbAppend.append("*.");
                    sbAppend.append(str);
                }
                sbAppend.append(")");
                string = sbAppend.toString();
            }
        }
        return string;
    }
}
