package com.github.markusbernhardt.proxy.util;

import com.github.markusbernhardt.proxy.selector.fixed.FixedProxySelector;
import com.github.markusbernhardt.proxy.selector.pac.PacProxySelector;
import com.github.markusbernhardt.proxy.selector.pac.PacScriptSource;
import com.github.markusbernhardt.proxy.selector.pac.UrlPacScriptSource;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/util/ProxyUtil.class */
public class ProxyUtil {
    public static final int DEFAULT_PROXY_PORT = 80;
    private static List<Proxy> noProxyList;

    public static FixedProxySelector parseProxySettings(String proxyVar) {
        if (proxyVar == null || proxyVar.trim().length() == 0) {
            return null;
        }
        try {
            if (proxyVar.indexOf(":/") == -1) {
                proxyVar = "http://" + proxyVar;
            }
            URL url = new URL(proxyVar);
            String host = cleanIPv6(url.getHost());
            int port = url.getPort();
            if (port == -1) {
                port = 80;
            }
            return new FixedProxySelector(host.trim(), port);
        } catch (MalformedURLException e) {
            Logger.log(ProxyUtil.class, Logger.LogLevel.WARNING, "Cannot parse Proxy Settings {}", proxyVar);
            return null;
        }
    }

    public static synchronized List<Proxy> noProxyList() {
        if (noProxyList == null) {
            ArrayList<Proxy> list = new ArrayList<>(1);
            list.add(Proxy.NO_PROXY);
            noProxyList = Collections.unmodifiableList(list);
        }
        return noProxyList;
    }

    public static PacProxySelector buildPacSelectorForUrl(String url) {
        PacProxySelector result = null;
        PacScriptSource pacSource = new UrlPacScriptSource(url);
        if (pacSource.isScriptValid()) {
            result = new PacProxySelector(pacSource);
        }
        return result;
    }

    public static String cleanIPv6(String hostOrIP) {
        if (hostOrIP == null) {
            return null;
        }
        String hostOrIP2 = hostOrIP.trim();
        if (hostOrIP2.startsWith("[")) {
            hostOrIP2 = hostOrIP2.substring(1);
        }
        if (hostOrIP2.endsWith("]")) {
            hostOrIP2 = hostOrIP2.substring(0, hostOrIP2.length() - 1);
        }
        return hostOrIP2;
    }
}
