package com.github.markusbernhardt.proxy.selector.whitelist;

import com.github.markusbernhardt.proxy.util.ProxyUtil;
import com.github.markusbernhardt.proxy.util.UriFilter;
import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/whitelist/ProxyBypassListSelector.class */
public class ProxyBypassListSelector extends ProxySelector {
    private ProxySelector delegate;
    private List<UriFilter> whiteListFilter;

    public ProxyBypassListSelector(List<UriFilter> whiteListFilter, ProxySelector proxySelector) {
        if (whiteListFilter == null) {
            throw new NullPointerException("Whitelist must not be null.");
        }
        if (proxySelector == null) {
            throw new NullPointerException("ProxySelector must not be null.");
        }
        this.delegate = proxySelector;
        this.whiteListFilter = whiteListFilter;
    }

    public ProxyBypassListSelector(String whiteList, ProxySelector proxySelector) {
        this(new DefaultWhiteListParser().parseWhiteList(whiteList), proxySelector);
    }

    @Override // java.net.ProxySelector
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        this.delegate.connectFailed(uri, sa, ioe);
    }

    public String toString() {
        return "ProxyBypassListSelector{delegate=" + this.delegate + ", whiteListFilter=" + this.whiteListFilter + '}';
    }

    @Override // java.net.ProxySelector
    public List<Proxy> select(URI uri) {
        for (UriFilter filter : this.whiteListFilter) {
            if (filter.accept(uri)) {
                return ProxyUtil.noProxyList();
            }
        }
        return this.delegate.select(uri);
    }
}
