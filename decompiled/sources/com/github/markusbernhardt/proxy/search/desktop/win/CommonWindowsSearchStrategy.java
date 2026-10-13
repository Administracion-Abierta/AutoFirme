package com.github.markusbernhardt.proxy.search.desktop.win;

import com.github.markusbernhardt.proxy.ProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.browser.ie.IELocalByPassFilter;
import com.github.markusbernhardt.proxy.selector.fixed.FixedProxySelector;
import com.github.markusbernhardt.proxy.selector.fixed.FixedSocksSelector;
import com.github.markusbernhardt.proxy.selector.misc.ProtocolDispatchSelector;
import com.github.markusbernhardt.proxy.selector.whitelist.ProxyBypassListSelector;
import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.ProxyException;
import com.github.markusbernhardt.proxy.util.ProxyUtil;
import com.github.markusbernhardt.proxy.util.UriFilter;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/desktop/win/CommonWindowsSearchStrategy.class */
public abstract class CommonWindowsSearchStrategy implements ProxySearchStrategy {
    protected ProxySelector setByPassListOnSelector(String bypassList, ProtocolDispatchSelector ps) {
        ProxyBypassListSelector result;
        if (bypassList != null && bypassList.trim().length() > 0) {
            if ("<local>".equals(bypassList.trim())) {
                result = buildLocalBypassSelector(ps);
            } else {
                result = new ProxyBypassListSelector(bypassList.replace(';', ','), ps);
            }
            return result;
        }
        return ps;
    }

    private ProxyBypassListSelector buildLocalBypassSelector(ProtocolDispatchSelector ps) {
        List<UriFilter> localBypassFilter = new ArrayList<>();
        localBypassFilter.add(new IELocalByPassFilter());
        return new ProxyBypassListSelector(localBypassFilter, ps);
    }

    private void addFallbackSelector(Properties settings, ProtocolDispatchSelector ps) {
        String proxy = settings.getProperty(PreferencesManager.VALUE_KEYSTORE_DEFAULT);
        if (proxy != null) {
            ps.setFallbackSelector(ProxyUtil.parseProxySettings(proxy));
        }
    }

    private void addSelectorForProtocol(Properties settings, String protocol, ProtocolDispatchSelector ps) {
        String proxy = settings.getProperty(protocol);
        if (proxy != null) {
            FixedProxySelector protocolSelector = ProxyUtil.parseProxySettings(proxy);
            ps.setSelector(protocol, protocolSelector);
        }
    }

    protected Properties parseProxyList(String proxyString) throws ProxyException {
        Properties p = new Properties();
        if (proxyString.indexOf(61) == -1) {
            p.setProperty(PreferencesManager.VALUE_KEYSTORE_DEFAULT, proxyString);
        } else {
            try {
                p.load(new ByteArrayInputStream(proxyString.replace(';', '\n').getBytes("ISO-8859-1")));
            } catch (IOException e) {
                Logger.log(getClass(), Logger.LogLevel.ERROR, "Error reading IE settings as properties: {}", e);
                throw new ProxyException(e);
            }
        }
        return p;
    }

    protected ProtocolDispatchSelector buildProtocolDispatchSelector(Properties properties) {
        ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
        addSelectorForProtocol(properties, "http", ps);
        addSelectorForProtocol(properties, "https", ps);
        addSelectorForProtocol(properties, "ftp", ps);
        addSelectorForProtocol(properties, "gopher", ps);
        addFallbackSelector(properties, ps);
        String socksProperties = properties.getProperty("socks");
        if (socksProperties != null) {
            String[] hostAndPort = socksProperties.split(":");
            String host = "";
            int port = 0;
            if (hostAndPort.length > 0) {
                host = hostAndPort[0];
            }
            if (hostAndPort.length > 1) {
                try {
                    port = Integer.parseInt(hostAndPort[1]);
                } catch (NumberFormatException e) {
                    Logger.log(CommonWindowsSearchStrategy.class, Logger.LogLevel.WARNING, "Cannot parse SOCKS proxy port {}", hostAndPort[1]);
                }
            }
            ps.setFallbackSelector(new FixedSocksSelector(host, port));
        }
        return ps;
    }
}
