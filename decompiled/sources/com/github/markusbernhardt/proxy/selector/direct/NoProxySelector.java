package com.github.markusbernhardt.proxy.selector.direct;

import com.github.markusbernhardt.proxy.util.ProxyUtil;
import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/direct/NoProxySelector.class */
public class NoProxySelector extends ProxySelector {
    private static NoProxySelector instance;

    private NoProxySelector() {
    }

    public static synchronized NoProxySelector getInstance() {
        if (instance == null) {
            instance = new NoProxySelector();
        }
        return instance;
    }

    @Override // java.net.ProxySelector
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
    }

    @Override // java.net.ProxySelector
    public List<Proxy> select(URI uri) {
        return ProxyUtil.noProxyList();
    }

    public String toString() {
        return "NoProxySelector{}";
    }
}
