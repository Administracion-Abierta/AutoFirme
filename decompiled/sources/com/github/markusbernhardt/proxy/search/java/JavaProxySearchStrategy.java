package com.github.markusbernhardt.proxy.search.java;

import com.github.markusbernhardt.proxy.ProxySearchStrategy;
import com.github.markusbernhardt.proxy.selector.fixed.FixedProxySelector;
import com.github.markusbernhardt.proxy.selector.fixed.FixedSocksSelector;
import com.github.markusbernhardt.proxy.selector.misc.ProtocolDispatchSelector;
import com.github.markusbernhardt.proxy.selector.whitelist.ProxyBypassListSelector;
import com.github.markusbernhardt.proxy.util.Logger;
import java.net.ProxySelector;
import org.java_websocket.WebSocketImpl;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/java/JavaProxySearchStrategy.class */
public class JavaProxySearchStrategy implements ProxySearchStrategy {
    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public ProxySelector getProxySelector() {
        ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Using settings from Java System Properties", new Object[0]);
        setupProxyForProtocol(ps, "http", 80);
        setupProxyForProtocol(ps, "https", WebSocketImpl.DEFAULT_WSS_PORT);
        setupProxyForProtocol(ps, "ftp", 80);
        setupProxyForProtocol(ps, "ftps", 80);
        boolean socksAvailable = setupSocktProxy(ps);
        if (ps.size() == 0 && !socksAvailable) {
            return null;
        }
        return ps;
    }

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public String getName() {
        return "java";
    }

    private boolean setupSocktProxy(ProtocolDispatchSelector ps) {
        String host = System.getProperty("socksProxyHost");
        if (host == null || host.trim().length() == 0) {
            return false;
        }
        String port = System.getProperty("socksProxyPort", "1080");
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Socks proxy {}:{} found", host, port);
        ps.setFallbackSelector(new FixedSocksSelector(host, Integer.parseInt(port)));
        return true;
    }

    private void setupProxyForProtocol(ProtocolDispatchSelector ps, String protocol, int defaultPort) {
        String host = System.getProperty(protocol + ".proxyHost");
        if (host == null || host.trim().length() == 0) {
            return;
        }
        String port = System.getProperty(protocol + ".proxyPort", Integer.toString(defaultPort));
        String whiteList = System.getProperty(protocol + ".nonProxyHosts", "").replace('|', ',');
        if ("https".equalsIgnoreCase(protocol)) {
            whiteList = System.getProperty("http.nonProxyHosts", "").replace('|', ',');
        }
        Logger.log(getClass(), Logger.LogLevel.TRACE, protocol.toUpperCase() + " proxy {}:{} found using whitelist: {}", host, port, whiteList);
        ProxySelector protocolSelector = new FixedProxySelector(host, Integer.parseInt(port));
        if (whiteList.trim().length() > 0) {
            protocolSelector = new ProxyBypassListSelector(whiteList, protocolSelector);
        }
        ps.setSelector(protocol, protocolSelector);
    }
}
