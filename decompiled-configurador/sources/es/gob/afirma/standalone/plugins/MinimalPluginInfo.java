package es.gob.afirma.standalone.plugins;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/plugins/MinimalPluginInfo.class */
public class MinimalPluginInfo {
    protected final String internalName;
    protected int versionCode;

    public MinimalPluginInfo(String str, int i) {
        if (str == null) {
            throw new NullPointerException("El nombre interno del plugin no puede ser nulo");
        }
        this.internalName = str;
        this.versionCode = i;
    }

    public String getInternalName() {
        return this.internalName;
    }

    public int getVersionCode() {
        return this.versionCode;
    }
}
