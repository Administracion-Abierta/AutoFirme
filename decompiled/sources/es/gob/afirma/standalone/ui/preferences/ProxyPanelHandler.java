package es.gob.afirma.standalone.ui.preferences;

import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.ProxyConfig;
import es.gob.afirma.standalone.ProxyUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/ProxyPanelHandler.class */
public class ProxyPanelHandler {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    static final String URL_CHECK_CONNECTION = "https://www.google.com";
    private final ProxyPanel view;

    public ProxyPanelHandler(ProxyPanel proxyPanel) {
        this.view = proxyPanel;
    }

    ProxyConfig getProxyConfig() {
        ProxyConfig.ConfigType configType;
        if (this.view.getNoProxyRb().isSelected()) {
            configType = ProxyConfig.ConfigType.NONE;
        } else if (this.view.getSystemProxyRb().isSelected()) {
            configType = ProxyConfig.ConfigType.SYSTEM;
        } else {
            configType = ProxyConfig.ConfigType.CUSTOM;
        }
        ProxyConfig proxyConfig = new ProxyConfig(configType);
        proxyConfig.setHost(this.view.getHostField().getText());
        proxyConfig.setPort(this.view.getPortField().getText());
        proxyConfig.setUsername(this.view.getUsernameField().getText());
        proxyConfig.setPassword(this.view.getPasswordField().getText().toCharArray());
        proxyConfig.setExcludedUrls(this.view.getExcludedUrlsField().getText());
        return proxyConfig;
    }

    void registerComponents() {
        this.view.getNoProxyRb().addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == 1) {
                enableComponentsFromPanel(false, this.view.getManualProxyPanel());
            }
        });
        this.view.getSystemProxyRb().addItemListener(itemEvent2 -> {
            if (itemEvent2.getStateChange() == 1) {
                enableComponentsFromPanel(false, this.view.getManualProxyPanel());
            }
        });
        this.view.getManualProxyRb().addItemListener(itemEvent3 -> {
            if (itemEvent3.getStateChange() == 1) {
                enableComponentsFromPanel(true, this.view.getManualProxyPanel());
            }
        });
        this.view.getDetectProxyButton().addActionListener(actionEvent -> {
            detectDefaultProxy();
        });
        this.view.getCheckConnectionButton().addActionListener(actionEvent2 -> {
            checkConnection(this.view.getHostField().getText(), this.view.getPortField().getText());
        });
        this.view.getPortField().getDocument().setDocumentFilter(new DocumentFilter() { // from class: es.gob.afirma.standalone.ui.preferences.ProxyPanelHandler.1
            public void insertString(DocumentFilter.FilterBypass filterBypass, int i, String str, AttributeSet attributeSet) throws BadLocationException {
                if (filterBypass.getDocument().getLength() + str.length() < 5 && str.matches("\\d+")) {
                    filterBypass.insertString(i, str, attributeSet);
                }
            }

            public void remove(DocumentFilter.FilterBypass filterBypass, int i, int i2) throws BadLocationException {
                filterBypass.remove(i, i2);
            }

            public void replace(DocumentFilter.FilterBypass filterBypass, int i, int i2, String str, AttributeSet attributeSet) throws BadLocationException {
                if (filterBypass.getDocument().getLength() + str.length() < 5 && str.matches("\\d+")) {
                    filterBypass.insertString(i, str, attributeSet);
                }
            }
        });
    }

    void loadViewData() {
        ProxyConfig.ConfigType configTypeValueOf;
        try {
            configTypeValueOf = ProxyConfig.ConfigType.valueOf(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_PROXY_TYPE));
        } catch (Exception e) {
            configTypeValueOf = ProxyConfig.ConfigType.NONE;
        }
        if (PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_PROXY_SELECTED, PreferencesManager.PreferencesSource.USER) != null) {
            if (!PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_PROXY_SELECTED)) {
                configTypeValueOf = ProxyConfig.ConfigType.SYSTEM;
            } else {
                String str = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_PROXY_HOST);
                if (str != null && !str.trim().isEmpty()) {
                    configTypeValueOf = ProxyConfig.ConfigType.CUSTOM;
                }
            }
        }
        switch (configTypeValueOf) {
            case NONE:
                this.view.getNoProxyRb().setSelected(true);
                enableComponentsFromPanel(false, this.view.getManualProxyPanel());
                break;
            case SYSTEM:
                this.view.getSystemProxyRb().setSelected(true);
                enableComponentsFromPanel(false, this.view.getManualProxyPanel());
                break;
            case CUSTOM:
                this.view.getManualProxyRb().setSelected(true);
                loadCustomConfig();
                break;
        }
    }

    void saveViewData() throws ConfigurationException {
        ProxyConfig proxyConfig = getProxyConfig();
        if (proxyConfig.getConfigType() == ProxyConfig.ConfigType.CUSTOM) {
            String host = proxyConfig.getHost();
            String port = proxyConfig.getPort();
            if (host == null || host.trim().isEmpty()) {
                LOGGER.warning("El host no puede ser nulo o vacio");
                throw new ConfigurationException(SimpleAfirmaMessages.getString("ProxyDialog.1"));
            }
            if (port == null || port == "") {
                LOGGER.warning("El puerto no puede ser nulo, vacio o tener mas de 4 digitos");
                throw new ConfigurationException(SimpleAfirmaMessages.getString("ProxyDialog.3"));
            }
            PreferencesManager.put(PreferencesManager.PREFERENCE_GENERAL_PROXY_HOST, host);
            PreferencesManager.put(PreferencesManager.PREFERENCE_GENERAL_PROXY_PORT, port);
            if (proxyConfig.getUsername() == null || proxyConfig.getUsername().trim().isEmpty()) {
                PreferencesManager.remove(PreferencesManager.PREFERENCE_GENERAL_PROXY_USERNAME);
            } else {
                PreferencesManager.put(PreferencesManager.PREFERENCE_GENERAL_PROXY_USERNAME, proxyConfig.getUsername().trim());
            }
            char[] password = proxyConfig.getPassword();
            if (password == null || password.length == 0) {
                PreferencesManager.remove(PreferencesManager.PREFERENCE_GENERAL_PROXY_PASSWORD);
            } else {
                try {
                    String strCipherPassword = ProxyUtil.cipherPassword(password);
                    if (strCipherPassword != null) {
                        PreferencesManager.put(PreferencesManager.PREFERENCE_GENERAL_PROXY_PASSWORD, strCipherPassword);
                    }
                } catch (GeneralSecurityException e) {
                    LOGGER.severe("Error cifrando la contrasena del Proxy: " + e);
                    JOptionPane.showMessageDialog(this.view.getParent(), SimpleAfirmaMessages.getString("ProxyDialog.19"));
                    PreferencesManager.put(PreferencesManager.PREFERENCE_GENERAL_PROXY_PASSWORD, "");
                }
            }
            if (proxyConfig.getExcludedUrls() != null && !proxyConfig.getExcludedUrls().trim().isEmpty()) {
                PreferencesManager.put(PreferencesManager.PREFERENCE_GENERAL_PROXY_EXCLUDED_URLS, proxyConfig.getExcludedUrls().trim().replace(",", "|"));
            } else {
                PreferencesManager.remove(PreferencesManager.PREFERENCE_GENERAL_PROXY_EXCLUDED_URLS);
            }
        } else {
            PreferencesManager.remove(PreferencesManager.PREFERENCE_GENERAL_PROXY_HOST);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_GENERAL_PROXY_PORT);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_GENERAL_PROXY_USERNAME);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_GENERAL_PROXY_PASSWORD);
            PreferencesManager.remove(PreferencesManager.PREFERENCE_GENERAL_PROXY_EXCLUDED_URLS);
        }
        PreferencesManager.remove(PreferencesManager.PREFERENCE_GENERAL_PROXY_SELECTED);
        PreferencesManager.put(PreferencesManager.PREFERENCE_GENERAL_PROXY_TYPE, proxyConfig.getConfigType().name());
        try {
            PreferencesManager.flush();
        } catch (Exception e2) {
            LOGGER.severe("Error al guardar las preferencias de firma: " + e2);
        }
    }

    private void loadCustomConfig() {
        char[] cArrDecipherPassword;
        this.view.getHostField().setText(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_PROXY_HOST));
        this.view.getPortField().setText(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_PROXY_PORT));
        this.view.getUsernameField().setText(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_PROXY_USERNAME));
        try {
            cArrDecipherPassword = ProxyUtil.decipherPassword(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_PROXY_PASSWORD));
        } catch (Exception e) {
            cArrDecipherPassword = null;
            LOGGER.warning("No se pudo descifrar la contrasena del proxy. Se omitira la contrasena: " + e);
            JOptionPane.showMessageDialog(this.view, SimpleAfirmaMessages.getString("ProxyDialog.18"));
            PreferencesManager.remove(PreferencesManager.PREFERENCE_GENERAL_PROXY_PASSWORD);
        }
        this.view.getPasswordField().setText(cArrDecipherPassword == null ? "" : String.valueOf(cArrDecipherPassword));
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_PROXY_EXCLUDED_URLS);
        if (str != null) {
            this.view.getExcludedUrlsField().setText(str.replace("|", ","));
        }
    }

    private static void enableComponentsFromPanel(boolean z, Container container) {
        for (Container container2 : container.getComponents()) {
            if (container2 instanceof Container) {
                container2.setEnabled(z);
                enableComponentsFromPanel(z, container2);
            } else {
                container2.setEnabled(z);
            }
        }
    }

    private void detectDefaultProxy() {
        LOGGER.info("Detectando proxies...");
        this.view.setCursor(new Cursor(3));
        Proxy defaultProxyToUrl = ProxyUtil.getDefaultProxyToUrl(URL_CHECK_CONNECTION);
        if (defaultProxyToUrl != null) {
            LOGGER.info("Proxy disponible: " + defaultProxyToUrl.toString());
            SocketAddress socketAddressAddress = defaultProxyToUrl.address();
            if (socketAddressAddress instanceof InetSocketAddress) {
                this.view.getHostField().setText(((InetSocketAddress) socketAddressAddress).getHostName());
                this.view.getPortField().setText(Integer.toString(((InetSocketAddress) socketAddressAddress).getPort()));
            } else {
                LOGGER.info("El proxy detectado no define una direccion de red");
                defaultProxyToUrl = null;
            }
        }
        this.view.setCursor(new Cursor(0));
        if (defaultProxyToUrl == null) {
            LOGGER.info("No se han detectado un proxy valido configurado en el sistema");
            AOUIFactory.showMessageDialog(this.view, SimpleAfirmaMessages.getString("ProxyDialog.14"), SimpleAfirmaMessages.getString("ProxyDialog.11"), AOUIFactory.ERROR_MESSAGE);
        }
    }

    private void checkConnection(String str, String str2) {
        new CheckConnectionThread(this.view, str, str2).start();
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/ProxyPanelHandler$CheckConnectionThread.class */
    private static class CheckConnectionThread extends Thread {
        private final Logger SUBLOGGER = Logger.getLogger("es.gob.afirma");
        private final Component parent;
        private final String proxyHost;
        private final String proxyPort;

        public CheckConnectionThread(Component component, String str, String str2) {
            this.parent = component;
            this.proxyHost = str;
            this.proxyPort = str2;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            boolean z;
            this.SUBLOGGER.info("Verificando conexion....");
            if (this.parent != null) {
                this.parent.setCursor(new Cursor(3));
            }
            try {
                ((HttpURLConnection) new URL(ProxyPanelHandler.URL_CHECK_CONNECTION).openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.proxyHost, Integer.parseInt(this.proxyPort))))).connect();
                z = true;
            } catch (Exception e) {
                this.SUBLOGGER.log(Level.WARNING, "Error conectando con el proxy: " + e, (Throwable) e);
                z = false;
            }
            if (z) {
                this.SUBLOGGER.info("Conexion proxy correcta");
                AOUIFactory.showMessageDialog(this.parent, SimpleAfirmaMessages.getString("ProxyDialog.9"), SimpleAfirmaMessages.getString("ProxyDialog.8"), AOUIFactory.INFORMATION_MESSAGE);
            } else {
                this.SUBLOGGER.info("Conexion proxy incorrecta");
                AOUIFactory.showMessageDialog(this.parent, SimpleAfirmaMessages.getString("ProxyDialog.10"), SimpleAfirmaMessages.getString("ProxyDialog.8"), AOUIFactory.ERROR_MESSAGE);
            }
            if (this.parent != null) {
                this.parent.setCursor(new Cursor(0));
            }
        }
    }
}
