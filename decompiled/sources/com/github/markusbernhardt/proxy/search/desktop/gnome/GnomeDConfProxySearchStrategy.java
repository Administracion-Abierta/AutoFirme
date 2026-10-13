package com.github.markusbernhardt.proxy.search.desktop.gnome;

import com.github.markusbernhardt.proxy.ProxySearchStrategy;
import com.github.markusbernhardt.proxy.selector.direct.NoProxySelector;
import com.github.markusbernhardt.proxy.selector.fixed.FixedProxySelector;
import com.github.markusbernhardt.proxy.selector.misc.ProtocolDispatchSelector;
import com.github.markusbernhardt.proxy.selector.whitelist.ProxyBypassListSelector;
import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.ProxyException;
import com.github.markusbernhardt.proxy.util.ProxyUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProxySelector;
import java.util.Properties;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/desktop/gnome/GnomeDConfProxySearchStrategy.class */
public class GnomeDConfProxySearchStrategy implements ProxySearchStrategy {
    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public ProxySelector getProxySelector() throws ProxyException {
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Detecting Gnome proxy settings", new Object[0]);
        Properties settings = readSettings();
        String type = settings.getProperty("org.gnome.system.proxy mode");
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Mode is :{}", type);
        ProxySelector result = null;
        if (type == null) {
            String useProxy = settings.getProperty("org.gnome.system.proxy.http enabled");
            if (useProxy == null) {
                return null;
            }
            type = Boolean.parseBoolean(useProxy) ? "manual" : "none";
        }
        if ("none".equals(type)) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Gnome uses no proxy", new Object[0]);
            result = NoProxySelector.getInstance();
        }
        if ("manual".equals(type)) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Gnome uses manual proxy settings", new Object[0]);
            result = setupFixedProxySelector(settings);
        }
        if ("auto".equals(type)) {
            String pacScriptUrl = settings.getProperty("org.gnome.system.proxy autoconfig-url", "");
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Gnome uses autodetect script {}", pacScriptUrl);
            result = ProxyUtil.buildPacSelectorForUrl(pacScriptUrl);
        }
        String noProxyList = settings.getProperty("org.gnome.system.proxy ignore-hosts", null);
        if (result != null && noProxyList != null && noProxyList.trim().length() > 0) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Gnome uses proxy bypass list: {}", noProxyList);
            result = new ProxyBypassListSelector(noProxyList, result);
        }
        return result;
    }

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public String getName() {
        return "gnome";
    }

    public Properties readSettings() throws ProxyException {
        Properties settings = new Properties();
        try {
            parseSettings(settings);
            return settings;
        } catch (IOException e) {
            Logger.log(getClass(), Logger.LogLevel.ERROR, "Gnome settings read error.", e);
            throw new ProxyException(e);
        }
    }

    private ProxySelector setupFixedProxySelector(Properties settings) {
        if (!hasProxySettings(settings)) {
            return null;
        }
        ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
        installHttpSelector(settings, ps);
        if (useForAllProtocols(settings)) {
            ps.setFallbackSelector(ps.getSelector("http"));
        } else {
            installSecureSelector(settings, ps);
            installFtpSelector(settings, ps);
            installSocksSelector(settings, ps);
        }
        return ps;
    }

    private boolean useForAllProtocols(Properties settings) {
        return Boolean.parseBoolean(settings.getProperty("org.gnome.system.proxy use-same-proxy", "false"));
    }

    private boolean hasProxySettings(Properties settings) {
        String proxyHost = settings.getProperty("org.gnome.system.proxy.http host", null);
        return proxyHost != null && proxyHost.length() > 0;
    }

    private void installHttpSelector(Properties settings, ProtocolDispatchSelector ps) throws NumberFormatException {
        String proxyHost = settings.getProperty("org.gnome.system.proxy.http host", null);
        int proxyPort = Integer.parseInt(settings.getProperty("org.gnome.system.proxy.http port", "0").trim());
        if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Gnome http proxy is {}:{}", proxyHost, Integer.valueOf(proxyPort));
            ps.setSelector("http", new FixedProxySelector(proxyHost.trim(), proxyPort));
        }
    }

    private void installSocksSelector(Properties settings, ProtocolDispatchSelector ps) throws NumberFormatException {
        String proxyHost = settings.getProperty("org.gnome.system.proxy.socks host", null);
        int proxyPort = Integer.parseInt(settings.getProperty("org.gnome.system.proxy.socks port", "0").trim());
        if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Gnome socks proxy is {}:{}", proxyHost, Integer.valueOf(proxyPort));
            ps.setSelector("socks", new FixedProxySelector(proxyHost.trim(), proxyPort));
        }
    }

    private void installFtpSelector(Properties settings, ProtocolDispatchSelector ps) throws NumberFormatException {
        String proxyHost = settings.getProperty("org.gnome.system.proxy.ftp host", null);
        int proxyPort = Integer.parseInt(settings.getProperty("org.gnome.system.proxy.ftp port", "0").trim());
        if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Gnome ftp proxy is {}:{}", proxyHost, Integer.valueOf(proxyPort));
            ps.setSelector("ftp", new FixedProxySelector(proxyHost.trim(), proxyPort));
        }
    }

    private void installSecureSelector(Properties settings, ProtocolDispatchSelector ps) throws NumberFormatException {
        String proxyHost = settings.getProperty("org.gnome.system.proxy.https host", null);
        int proxyPort = Integer.parseInt(settings.getProperty("org.gnome.system.proxy.https port", "0").trim());
        if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Gnome secure proxy is {}:{}", proxyHost, Integer.valueOf(proxyPort));
            ps.setSelector("https", new FixedProxySelector(proxyHost.trim(), proxyPort));
            ps.setSelector("sftp", new FixedProxySelector(proxyHost.trim(), proxyPort));
        }
    }

    private Properties parseSettings(Properties settings) throws IOException {
        Logger.log(getClass(), Logger.LogLevel.TRACE, "exec gsettings list-recursively org.gnome.system.proxy", new Object[0]);
        Process p = Runtime.getRuntime().exec("gsettings list-recursively org.gnome.system.proxy");
        BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while (true) {
            String line = bri.readLine();
            if (line != null) {
                Logger.log(getClass(), Logger.LogLevel.TRACE, line, new Object[0]);
                int schemaSep = line.indexOf(" ");
                int keySep = line.indexOf(" ", schemaSep + 1);
                String entry = line.substring(0, keySep);
                String value = line.substring(keySep + 1).replaceAll(OperatorName.SHOW_TEXT_LINE, "");
                if (value.matches("\\[.*\\]")) {
                    value = value.replaceAll("\\[|\\]| ", "");
                }
                Logger.log(getClass(), Logger.LogLevel.TRACE, "prop is: {}|{}", entry, value);
                settings.setProperty(entry, value);
            } else {
                try {
                    break;
                } catch (InterruptedException ex) {
                    Logger.log(getClass(), Logger.LogLevel.ERROR, ex.getMessage(), new Object[0]);
                }
            }
        }
        p.waitFor();
        return settings;
    }
}
