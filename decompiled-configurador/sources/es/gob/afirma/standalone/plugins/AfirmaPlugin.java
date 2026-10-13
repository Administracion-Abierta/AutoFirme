package es.gob.afirma.standalone.plugins;

import java.security.cert.Certificate;
import java.util.Properties;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/plugins/AfirmaPlugin.class */
public abstract class AfirmaPlugin {
    private PluginInfo info = null;
    private SignDataProcessor inlineProcessor = null;
    private ClassLoader classLoader = null;

    public final PluginInfo getInfo() {
        return this.info;
    }

    public final void setInfo(PluginInfo pluginInfo) {
        this.info = pluginInfo;
    }

    public final ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public final void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public final Properties getConfig() {
        return PluginPreferences.getInstance(this).recoverConfig();
    }

    public final ConfigurationPanel buildConfigurationPanel() throws ReflectiveOperationException {
        String configPanel = getInfo().getConfigPanel();
        if (configPanel == null) {
            return null;
        }
        try {
            return (ConfigurationPanel) Class.forName(configPanel, true, getClass().getClassLoader()).newInstance();
        } catch (Exception e) {
            throw new ReflectiveOperationException("No se pudo crear el panel de configuracion", e);
        }
    }

    public byte[] preSignProcess(byte[] bArr, String str) throws PluginControlledException {
        return bArr;
    }

    public byte[] postSignProcess(byte[] bArr, String str, Certificate[] certificateArr) throws PluginControlledException {
        return bArr;
    }

    public final SignDataProcessor getInlineProcessor(int i) throws PluginControlledException {
        String inlineProcessorClassname;
        if (this.inlineProcessor == null && (inlineProcessorClassname = this.info.getInlineProcessorClassname()) != null) {
            try {
                this.inlineProcessor = (SignDataProcessor) Class.forName(inlineProcessorClassname, true, getClass().getClassLoader()).getConstructor(Integer.TYPE).newInstance(Integer.valueOf(i));
            } catch (Exception e) {
                throw new PluginControlledException("No se ha podido cargar la clase de procesado en linea", e);
            }
        }
        return this.inlineProcessor;
    }

    public void install() throws PluginControlledException {
    }

    public void uninstall() throws PluginControlledException {
    }

    public void reset() throws PluginControlledException {
    }

    public final boolean equals(Object obj) {
        if (obj != null && (obj instanceof AfirmaPlugin)) {
            return getInfo().getInternalName().equals(((AfirmaPlugin) obj).getInfo().getInternalName());
        }
        if (obj != null && (obj instanceof PluginInfo)) {
            return getInfo().getInternalName().equals(((PluginInfo) obj).getInternalName());
        }
        return super.equals(obj);
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public final String toString() {
        return this.info != null ? this.info.toString() : super.toString();
    }
}
