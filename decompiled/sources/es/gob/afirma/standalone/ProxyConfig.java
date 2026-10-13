package es.gob.afirma.standalone;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ProxyConfig.class */
public class ProxyConfig {
    private final ConfigType configType;
    private String host = null;
    private String port = null;
    private String username = null;
    private char[] password = null;
    private String excludedUrls = null;

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ProxyConfig$ConfigType.class */
    public enum ConfigType {
        NONE,
        SYSTEM,
        CUSTOM
    }

    public ProxyConfig(ConfigType configType) {
        this.configType = configType;
    }

    public ConfigType getConfigType() {
        return this.configType;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String str) {
        this.host = str;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String str) {
        this.port = str;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String str) {
        this.username = str;
    }

    public char[] getPassword() {
        if (this.password != null) {
            return (char[]) this.password.clone();
        }
        return null;
    }

    public void setPassword(char[] cArr) {
        this.password = cArr != null ? (char[]) cArr.clone() : null;
    }

    public String getExcludedUrls() {
        return this.excludedUrls;
    }

    public void setExcludedUrls(String str) {
        this.excludedUrls = str;
    }
}
