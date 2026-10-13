package com.github.markusbernhardt.proxy.selector.fixed;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/fixed/FixedProxySelector.class */
public class FixedProxySelector extends ProxySelector {
    private final List<Proxy> proxyList;

    public FixedProxySelector(Proxy proxy) {
        List<Proxy> list = new ArrayList<>(1);
        list.add(proxy);
        this.proxyList = Collections.unmodifiableList(list);
    }

    public FixedProxySelector(String proxyHost, int proxyPort) {
        this(new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxyHost, proxyPort)));
    }

    @Override // java.net.ProxySelector
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
    }

    @Override // java.net.ProxySelector
    public List<Proxy> select(URI uri) {
        return this.proxyList;
    }

    public String toString() {
        return "FixedProxySelector{proxyList=" + this.proxyList + '}';
    }
}
