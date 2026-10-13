package es.gob.afirma.standalone;

import com.github.markusbernhardt.proxy.ProxySearch;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import es.gob.afirma.standalone.crypto.CypherDataManager;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ProxyUtil.class */
public final class ProxyUtil {
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static ProxySelector defaultProxySelector = null;
    private static final char[] PWD_CIPHER_KEY = {'8', 'W', '{', 't', '2', 'r', ',', 'B'};

    private ProxyUtil() {
    }

    public static void setProxySettings() {
        if (defaultProxySelector == null) {
            defaultProxySelector = ProxySelector.getDefault();
        }
        ProxySelector proxySelector = getProxySelector(getProxyType());
        if (proxySelector == null) {
            LOGGER.info("No se usara proxy para las conexiones de red");
            return;
        }
        ProxySelector.setDefault(proxySelector);
        try {
            List<Proxy> listSelect = proxySelector.select(new URI("http://www.theregister.co.uk"));
            if (listSelect.isEmpty() || listSelect.get(0).address() == null) {
                LOGGER.info("No se usara proxy para las conexiones HTTP");
            } else {
                InetSocketAddress inetSocketAddress = (InetSocketAddress) listSelect.get(0).address();
                LOGGER.info("Se usara proxy para las conexiones HTTP: " + inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort());
            }
            List<Proxy> listSelect2 = proxySelector.select(new URI("https://www.google.com"));
            if (listSelect2.isEmpty() || listSelect2.get(0).address() == null) {
                LOGGER.info("No se usara proxy para las conexiones HTTPS");
            } else {
                InetSocketAddress inetSocketAddress2 = (InetSocketAddress) listSelect2.get(0).address();
                LOGGER.info("Se usara proxy para las conexiones HTTPS: " + inetSocketAddress2.getHostName() + ":" + inetSocketAddress2.getPort());
            }
        } catch (URISyntaxException e) {
            throw new IllegalStateException("La URI de pruebas del proxy es invalida: " + e, e);
        }
    }

    private static ProxyConfig.ConfigType getProxyType() {
        ProxyConfig.ConfigType configTypeValueOf;
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_PROXY_TYPE);
        if (str == null) {
            if (!PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_PROXY_SELECTED)) {
                configTypeValueOf = ProxyConfig.ConfigType.SYSTEM;
            } else {
                String str2 = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_PROXY_HOST);
                String str3 = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_PROXY_PORT);
                if (str2 != null && !str2.trim().isEmpty() && str3 != null && !str3.trim().isEmpty()) {
                    configTypeValueOf = ProxyConfig.ConfigType.CUSTOM;
                } else {
                    configTypeValueOf = ProxyConfig.ConfigType.NONE;
                }
            }
        } else {
            try {
                configTypeValueOf = ProxyConfig.ConfigType.valueOf(str);
            } catch (Exception e) {
                LOGGER.warning("El tipo de proxy configurado no era valido. No se usara proxy: " + e);
                configTypeValueOf = ProxyConfig.ConfigType.NONE;
            }
        }
        return configTypeValueOf;
    }

    private static ProxySelector getProxySelector(ProxyConfig.ConfigType configType) {
        ProxySelector noProxySelector;
        switch (configType) {
            case SYSTEM:
                noProxySelector = getSystemProxySelector();
                break;
            case CUSTOM:
                noProxySelector = getCustomProxySelector();
                break;
            case NONE:
            default:
                noProxySelector = getNoProxySelector();
                break;
        }
        return noProxySelector;
    }

    private static ProxySelector getSystemProxySelector() {
        ProxySelector proxySelector = ProxySelector.getDefault();
        ProxySelector.setDefault(getNoProxySelector());
        ProxySearch proxySearch = new ProxySearch();
        proxySearch.addStrategy(ProxySearch.Strategy.OS_DEFAULT);
        proxySearch.addStrategy(ProxySearch.Strategy.BROWSER);
        ProxySelector proxySelector2 = proxySearch.getProxySelector();
        ProxySelector.setDefault(proxySelector);
        return proxySelector2;
    }

    private static ProxySelector getCustomProxySelector() {
        char[] cArrDecipherPassword;
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_PROXY_HOST);
        String str2 = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_PROXY_PORT);
        String str3 = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_PROXY_USERNAME);
        String str4 = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_PROXY_PASSWORD);
        String str5 = PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_PROXY_EXCLUDED_URLS);
        System.setProperty("http.proxyHost", str);
        System.setProperty("http.proxyPort", str2);
        System.setProperty("https.proxyHost", str);
        System.setProperty("https.proxyPort", str2);
        System.setProperty("ftp.proxHost", str);
        System.setProperty("ftp.proxyPort", str2);
        System.setProperty("socksProxyHost", str);
        System.setProperty("socksProxyPort", str2);
        if (str5 != null && !str5.trim().isEmpty()) {
            String strTrim = str5.trim();
            System.setProperty("http.nonProxyHosts", strTrim);
            System.setProperty("https.nonProxyHosts", strTrim);
            System.setProperty("ftp.nonProxyHosts", strTrim);
            System.setProperty("socksNonProxyHosts", strTrim);
        }
        if (str3 != null && !str3.trim().isEmpty() && str4 != null && !str4.trim().isEmpty()) {
            try {
                cArrDecipherPassword = decipherPassword(str4);
            } catch (Exception e) {
                LOGGER.warning("No se pudo descifrar la contrasena del proxy. No se configurara el usuario y contrasena: " + e);
                cArrDecipherPassword = null;
            }
            Authenticator passwordAuthentication = null;
            if (cArrDecipherPassword != null) {
                passwordAuthentication = getPasswordAuthentication(str3, cArrDecipherPassword);
            }
            Authenticator.setDefault(passwordAuthentication);
        }
        return defaultProxySelector;
    }

    private static ProxySelector getNoProxySelector() {
        return new NoProxySelector();
    }

    public static String cipherPassword(char[] cArr) throws GeneralSecurityException {
        if (cArr == null || cArr.length < 1) {
            return null;
        }
        return CypherDataManager.cipherData(String.valueOf(cArr).getBytes(StandardCharsets.UTF_8), String.valueOf(PWD_CIPHER_KEY).getBytes(StandardCharsets.UTF_8));
    }

    public static char[] decipherPassword(String str) throws GeneralSecurityException, IOException {
        if (str == null || str.isEmpty()) {
            return null;
        }
        return new String(CypherDataManager.decipherData(str.getBytes(StandardCharsets.UTF_8), String.valueOf(PWD_CIPHER_KEY).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8).toCharArray();
    }

    public static Proxy getDefaultProxyToUrl(String str) {
        List<Proxy> listSelect;
        Proxy proxy = null;
        ProxySelector systemProxySelector = getSystemProxySelector();
        if (systemProxySelector != null && (listSelect = systemProxySelector.select(URI.create(str))) != null && !listSelect.isEmpty()) {
            proxy = listSelect.get(0);
        }
        return proxy;
    }

    private static Authenticator getPasswordAuthentication(final String str, final char[] cArr) {
        return new Authenticator() { // from class: es.gob.afirma.standalone.ProxyUtil.1
            @Override // java.net.Authenticator
            protected PasswordAuthentication getPasswordAuthentication() {
                if (getRequestorType() == Authenticator.RequestorType.PROXY) {
                    return new PasswordAuthentication(str, cArr);
                }
                return super.getPasswordAuthentication();
            }
        };
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ProxyUtil$NoProxySelector.class */
    public static class NoProxySelector extends ProxySelector {
        @Override // java.net.ProxySelector
        public List<Proxy> select(URI uri) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(Proxy.NO_PROXY);
            return arrayList;
        }

        @Override // java.net.ProxySelector
        public void connectFailed(URI uri, SocketAddress socketAddress, IOException iOException) {
            ProxyUtil.LOGGER.warning("No se puede acceder a la URL sin proxy: " + iOException);
        }
    }
}
