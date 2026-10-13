package es.gob.afirma.standalone.plugins;

import java.util.Properties;
import javax.swing.JPanel;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/plugins/ConfigurationPanel.class */
public abstract class ConfigurationPanel extends JPanel {
    private static final long serialVersionUID = -1281422505557803637L;

    public abstract void init(Properties properties);

    public abstract Properties getConfiguration();
}
