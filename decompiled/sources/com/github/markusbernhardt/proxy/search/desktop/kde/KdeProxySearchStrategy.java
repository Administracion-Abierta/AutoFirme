package com.github.markusbernhardt.proxy.search.desktop.kde;

import com.github.markusbernhardt.proxy.ProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.env.EnvProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.wpad.WpadProxySearchStrategy;
import com.github.markusbernhardt.proxy.selector.direct.NoProxySelector;
import com.github.markusbernhardt.proxy.selector.fixed.FixedProxySelector;
import com.github.markusbernhardt.proxy.selector.misc.ProtocolDispatchSelector;
import com.github.markusbernhardt.proxy.selector.whitelist.ProxyBypassListSelector;
import com.github.markusbernhardt.proxy.selector.whitelist.UseProxyWhiteListSelector;
import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.ProxyException;
import com.github.markusbernhardt.proxy.util.ProxyUtil;
import java.io.IOException;
import java.net.ProxySelector;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/desktop/kde/KdeProxySearchStrategy.class */
public class KdeProxySearchStrategy implements ProxySearchStrategy {
    private KdeSettingsParser settingsParser;

    public KdeProxySearchStrategy() {
        this(new KdeSettingsParser());
    }

    public KdeProxySearchStrategy(KdeSettingsParser settingsParser) {
        this.settingsParser = settingsParser;
    }

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public ProxySelector getProxySelector() throws ProxyException {
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Detecting Kde proxy settings", new Object[0]);
        Properties settings = readSettings();
        if (settings == null) {
            return null;
        }
        ProxySelector result = null;
        int type = Integer.parseInt(settings.getProperty("ProxyType", "-1"));
        switch (type) {
            case 0:
                Logger.log(getClass(), Logger.LogLevel.TRACE, "Kde uses no proxy", new Object[0]);
                result = NoProxySelector.getInstance();
                break;
            case 1:
                Logger.log(getClass(), Logger.LogLevel.TRACE, "Kde uses manual proxy settings", new Object[0]);
                result = setupFixedProxySelector(settings);
                break;
            case 2:
                String pacScriptUrl = settings.getProperty("Proxy Config Script", "");
                Logger.log(getClass(), Logger.LogLevel.TRACE, "Kde uses autodetect script {}", pacScriptUrl);
                result = ProxyUtil.buildPacSelectorForUrl(pacScriptUrl);
                break;
            case 3:
                Logger.log(getClass(), Logger.LogLevel.TRACE, "Kde uses WPAD to detect the proxy", new Object[0]);
                result = new WpadProxySearchStrategy().getProxySelector();
                break;
            case 4:
                Logger.log(getClass(), Logger.LogLevel.TRACE, "Kde reads proxy from environment", new Object[0]);
                result = setupEnvVarSelector(settings);
                break;
        }
        return result;
    }

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public String getName() {
        return "kde";
    }

    private Properties readSettings() throws ProxyException {
        try {
            return this.settingsParser.parseSettings();
        } catch (IOException e) {
            Logger.log(getClass(), Logger.LogLevel.ERROR, "Can't parse settings.", e);
            throw new ProxyException(e);
        }
    }

    private ProxySelector setupEnvVarSelector(Properties settings) {
        ProxySelector result = new EnvProxySearchStrategy(settings.getProperty("httpProxy"), settings.getProperty("httpsProxy"), settings.getProperty("ftpProxy"), settings.getProperty("NoProxyFor")).getProxySelector();
        return result;
    }

    private ProxySelector setupFixedProxySelector(Properties settings) {
        String proxyVar = settings.getProperty("httpProxy", null);
        FixedProxySelector httpPS = ProxyUtil.parseProxySettings(proxyVar);
        if (httpPS == null) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Kde http proxy is {}", proxyVar);
            return null;
        }
        ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
        ps.setSelector("http", httpPS);
        String proxyVar2 = settings.getProperty("httpsProxy", null);
        FixedProxySelector httpsPS = ProxyUtil.parseProxySettings(proxyVar2);
        if (httpsPS != null) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Kde https proxy is {}", proxyVar2);
            ps.setSelector("https", httpsPS);
        }
        String proxyVar3 = settings.getProperty("ftpProxy", null);
        FixedProxySelector ftpPS = ProxyUtil.parseProxySettings(proxyVar3);
        if (ftpPS != null) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Kde ftp proxy is {}", proxyVar3);
            ps.setSelector("ftp", ftpPS);
        }
        String noProxyList = settings.getProperty("NoProxyFor", null);
        if (noProxyList != null && noProxyList.trim().length() > 0) {
            boolean reverse = "true".equals(settings.getProperty("ReversedException", "false"));
            if (reverse) {
                Logger.log(getClass(), Logger.LogLevel.TRACE, "Kde proxy blacklist is {}", noProxyList);
                return new UseProxyWhiteListSelector(noProxyList, ps);
            }
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Kde proxy whitelist is {}", noProxyList);
            return new ProxyBypassListSelector(noProxyList, ps);
        }
        return ps;
    }
}
