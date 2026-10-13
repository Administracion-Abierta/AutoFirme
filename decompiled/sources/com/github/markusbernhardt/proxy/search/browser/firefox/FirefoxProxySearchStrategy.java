package com.github.markusbernhardt.proxy.search.browser.firefox;

import com.github.markusbernhardt.proxy.ProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.desktop.DesktopProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.wpad.WpadProxySearchStrategy;
import com.github.markusbernhardt.proxy.selector.direct.NoProxySelector;
import com.github.markusbernhardt.proxy.selector.fixed.FixedProxySelector;
import com.github.markusbernhardt.proxy.selector.fixed.FixedSocksSelector;
import com.github.markusbernhardt.proxy.selector.misc.ProtocolDispatchSelector;
import com.github.markusbernhardt.proxy.selector.whitelist.ProxyBypassListSelector;
import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.PlatformUtil;
import com.github.markusbernhardt.proxy.util.ProxyException;
import com.github.markusbernhardt.proxy.util.ProxyUtil;
import java.io.IOException;
import java.net.ProxySelector;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/browser/firefox/FirefoxProxySearchStrategy.class */
public class FirefoxProxySearchStrategy implements ProxySearchStrategy {
    private FirefoxProfileSource profileScanner;
    private FirefoxSettingParser settingsParser;

    public FirefoxProxySearchStrategy() {
        if (PlatformUtil.getCurrentPlattform() == PlatformUtil.Platform.WIN) {
            this.profileScanner = new WinFirefoxProfileSource();
        } else if (PlatformUtil.getCurrentPlattform() == PlatformUtil.Platform.MAC_OS) {
            this.profileScanner = new OsxFirefoxProfileSource();
        } else {
            this.profileScanner = new LinuxFirefoxProfileSource();
        }
        this.settingsParser = new FirefoxSettingParser();
    }

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public ProxySelector getProxySelector() throws ProxyException {
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Detecting Firefox settings.", new Object[0]);
        Properties settings = readSettings();
        ProxySelector result = null;
        int type = Integer.parseInt(settings.getProperty("network.proxy.type", "-1"));
        switch (type) {
            case -1:
                Logger.log(getClass(), Logger.LogLevel.TRACE, "Firefox uses system settings", new Object[0]);
                result = new DesktopProxySearchStrategy().getProxySelector();
                break;
            case 0:
                Logger.log(getClass(), Logger.LogLevel.TRACE, "Firefox uses no proxy", new Object[0]);
                result = NoProxySelector.getInstance();
                break;
            case 1:
                Logger.log(getClass(), Logger.LogLevel.TRACE, "Firefox uses manual settings", new Object[0]);
                result = setupFixedProxySelector(settings);
                break;
            case 2:
                String pacScriptUrl = settings.getProperty("network.proxy.autoconfig_url", "");
                Logger.log(getClass(), Logger.LogLevel.TRACE, "Firefox uses script (PAC) {}", pacScriptUrl);
                result = ProxyUtil.buildPacSelectorForUrl(pacScriptUrl);
                break;
            case 3:
                Logger.log(getClass(), Logger.LogLevel.TRACE, "Netscape compability mode -> uses no proxy", new Object[0]);
                result = NoProxySelector.getInstance();
                break;
            case 4:
                Logger.log(getClass(), Logger.LogLevel.TRACE, "Firefox uses automatic detection (WPAD)", new Object[0]);
                result = new WpadProxySearchStrategy().getProxySelector();
                break;
        }
        String noProxyList = settings.getProperty("network.proxy.no_proxies_on", null);
        if (result != null && noProxyList != null && noProxyList.trim().length() > 0) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Firefox uses proxy bypass list for: {}", noProxyList);
            result = new ProxyBypassListSelector(noProxyList, result);
        }
        return result;
    }

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public String getName() {
        return "firefox";
    }

    public Properties readSettings() throws ProxyException {
        try {
            Properties settings = this.settingsParser.parseSettings(this.profileScanner);
            return settings;
        } catch (IOException e) {
            throw new ProxyException("No Firefox installation found");
        }
    }

    private ProxySelector setupFixedProxySelector(Properties settings) {
        ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
        installHttpProxy(ps, settings);
        if (isProxyShared(settings)) {
            installSharedProxy(ps);
        } else {
            installFtpProxy(ps, settings);
            installSecureProxy(ps, settings);
            installSocksProxy(ps, settings);
        }
        return ps;
    }

    private void installFtpProxy(ProtocolDispatchSelector ps, Properties settings) throws NumberFormatException {
        installSelectorForProtocol(ps, settings, "ftp");
    }

    private void installHttpProxy(ProtocolDispatchSelector ps, Properties settings) throws NumberFormatException {
        installSelectorForProtocol(ps, settings, "http");
    }

    private boolean isProxyShared(Properties settings) {
        return Boolean.TRUE.toString().equalsIgnoreCase(settings.getProperty("network.proxy.share_proxy_settings", "false"));
    }

    private void installSharedProxy(ProtocolDispatchSelector ps) {
        ProxySelector httpProxy = ps.getSelector("http");
        if (httpProxy != null) {
            ps.setFallbackSelector(httpProxy);
        }
    }

    private void installSocksProxy(ProtocolDispatchSelector ps, Properties settings) throws NumberFormatException {
        String proxyHost = settings.getProperty("network.proxy.socks", null);
        int proxyPort = Integer.parseInt(settings.getProperty("network.proxy.socks_port", "0"));
        if (proxyHost != null && proxyPort != 0) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Firefox socks proxy is {}:{}", proxyHost, Integer.valueOf(proxyPort));
            ps.setSelector("socks", new FixedSocksSelector(proxyHost, proxyPort));
        }
    }

    private void installSecureProxy(ProtocolDispatchSelector ps, Properties settings) throws NumberFormatException {
        String proxyHost = settings.getProperty("network.proxy.ssl", null);
        int proxyPort = Integer.parseInt(settings.getProperty("network.proxy.ssl_port", "0"));
        if (proxyHost != null && proxyPort != 0) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Firefox secure proxy is {}:{}", proxyHost, Integer.valueOf(proxyPort));
            ps.setSelector("https", new FixedProxySelector(proxyHost, proxyPort));
            ps.setSelector("sftp", new FixedProxySelector(proxyHost, proxyPort));
        }
    }

    private void installSelectorForProtocol(ProtocolDispatchSelector ps, Properties settings, String protocol) throws NumberFormatException {
        String proxyHost = settings.getProperty("network.proxy." + protocol, null);
        int proxyPort = Integer.parseInt(settings.getProperty("network.proxy." + protocol + "_port", "0"));
        if (proxyHost != null && proxyPort != 0) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Firefox " + protocol + " proxy is {}:{}", proxyHost, Integer.valueOf(proxyPort));
            ps.setSelector(protocol, new FixedProxySelector(proxyHost, proxyPort));
        }
    }
}
