package es.gob.afirma.core.misc.protocol;

import java.util.Map;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/misc/protocol/UrlParametersToLoad.class */
public final class UrlParametersToLoad extends UrlParameters {
    private static final String VER_PARAM = "ver";
    private static final String MULTILOAD_PARAM = "multiload";
    private static final String TITLE_PARAM = "title";
    private static final String EXTENSIONS_PARAM = "exts";
    private static final String DESCRIPTION_PARAM = "desc";
    private static final String FILEPATH_PARAM = "filePath";
    private String minimumProtocolVersion;
    private boolean multiload;
    private String title;
    private String extensions;
    private String description;
    private String filepath;

    public UrlParametersToLoad() {
        setMinimumProtocolVersion(null);
        setMultiload(false);
        setTitle(null);
        setExtensions(null);
        setDescription(null);
        setFilepath(null);
    }

    public String getMinimumProtocolVersion() {
        return this.minimumProtocolVersion;
    }

    void setMinimumProtocolVersion(String str) {
        this.minimumProtocolVersion = str;
    }

    public void setMultiload(boolean z) {
        this.multiload = z;
    }

    public boolean getMultiload() {
        return this.multiload;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public String getExtensions() {
        return this.extensions;
    }

    public void setExtensions(String str) {
        this.extensions = str;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String str) {
        this.description = str;
    }

    public String getFilepath() {
        return this.filepath;
    }

    public void setFilepath(String str) {
        this.filepath = str;
    }

    public void setLoadParameters(Map<String, String> map) {
        if (map.containsKey(VER_PARAM)) {
            setMinimumProtocolVersion(map.get(VER_PARAM));
        } else {
            setMinimumProtocolVersion(Integer.toString(ProtocolVersion.VERSION_0.getVersion()));
        }
        if (map.containsKey(MULTILOAD_PARAM)) {
            setMultiload(Boolean.parseBoolean(map.get(MULTILOAD_PARAM)));
        } else {
            setMultiload(false);
        }
        if (map.containsKey("title")) {
            setTitle(map.get("title"));
        } else {
            setTitle(null);
        }
        if (map.containsKey(EXTENSIONS_PARAM)) {
            setExtensions(map.get(EXTENSIONS_PARAM));
        } else {
            setExtensions(null);
        }
        if (map.containsKey(DESCRIPTION_PARAM)) {
            setDescription(map.get(DESCRIPTION_PARAM));
        } else {
            setDescription(null);
        }
        if (map.containsKey(FILEPATH_PARAM) && !"".equals(map.get(FILEPATH_PARAM))) {
            setFilepath(map.get(FILEPATH_PARAM));
        } else {
            setFilepath(null);
        }
    }
}
