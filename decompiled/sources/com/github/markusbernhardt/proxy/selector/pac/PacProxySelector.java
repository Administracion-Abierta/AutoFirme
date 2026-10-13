package com.github.markusbernhardt.proxy.selector.pac;

import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.ProxyUtil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/pac/PacProxySelector.class */
public class PacProxySelector extends ProxySelector {
    private static final String PAC_SOCKS = "SOCKS";
    private static final String PAC_DIRECT = "DIRECT";
    private PacScriptParser pacScriptParser;
    private static volatile boolean enabled = true;

    public PacProxySelector(PacScriptSource pacSource) {
        selectEngine(pacSource);
    }

    public static void setEnabled(boolean enable) {
        enabled = enable;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    private void selectEngine(PacScriptSource pacSource) {
        try {
            Logger.log(getClass(), Logger.LogLevel.INFO, "Using javax.script JavaScript engine.", new Object[0]);
            this.pacScriptParser = new JavaxPacScriptParser(pacSource);
        } catch (Exception e) {
            Logger.log(getClass(), Logger.LogLevel.ERROR, "PAC parser error.", e);
        }
    }

    @Override // java.net.ProxySelector
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
    }

    @Override // java.net.ProxySelector
    public List<Proxy> select(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("URI must not be null.");
        }
        if (!enabled) {
            return ProxyUtil.noProxyList();
        }
        return findProxy(uri);
    }

    private List<Proxy> findProxy(URI uri) {
        try {
            if (this.pacScriptParser == null) {
                return ProxyUtil.noProxyList();
            }
            String parseResult = this.pacScriptParser.evaluate(uri.toString(), uri.getHost());
            if (parseResult == null) {
                return ProxyUtil.noProxyList();
            }
            List<Proxy> proxies = new ArrayList<>();
            String[] proxyDefinitions = parseResult.split("[;]");
            for (String proxyDef : proxyDefinitions) {
                if (proxyDef.trim().length() > 0) {
                    proxies.add(buildProxyFromPacResult(proxyDef));
                }
            }
            return proxies;
        } catch (ProxyEvaluationException e) {
            Logger.log(getClass(), Logger.LogLevel.ERROR, "PAC resolving error.", e);
            return ProxyUtil.noProxyList();
        }
    }

    private Proxy buildProxyFromPacResult(String pacResult) {
        String[] words = pacResult.trim().split("\\s+");
        if (words.length == 0) {
            return Proxy.NO_PROXY;
        }
        if (words.length == 1) {
            return Proxy.NO_PROXY;
        }
        String proxyType = words[0];
        String host = concat(words, 1);
        Proxy.Type type = Proxy.Type.HTTP;
        if (proxyType.toUpperCase().startsWith(PAC_SOCKS)) {
            type = Proxy.Type.SOCKS;
        }
        Integer port = 80;
        int indexOfPort = host.indexOf(58);
        int index2 = host.lastIndexOf(93);
        if (indexOfPort != -1 && index2 < indexOfPort) {
            port = Integer.valueOf(Integer.parseInt(host.substring(indexOfPort + 1).trim()));
            host = host.substring(0, indexOfPort).trim();
        }
        SocketAddress adr = InetSocketAddress.createUnresolved(host, port.intValue());
        return new Proxy(type, adr);
    }

    private static String concat(String[] strings, int startIndex) {
        StringBuilder b = new StringBuilder();
        for (int i = startIndex; i < strings.length; i++) {
            b.append(strings[i]);
        }
        return b.toString();
    }
}
