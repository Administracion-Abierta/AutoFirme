package com.github.markusbernhardt.proxy.selector.whitelist;

import com.github.markusbernhardt.proxy.util.ProxyUtil;
import com.github.markusbernhardt.proxy.util.UriFilter;
import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/whitelist/UseProxyWhiteListSelector.class */
public class UseProxyWhiteListSelector extends ProxySelector {
    private ProxySelector delegate;
    private List<UriFilter> whiteListFilter;

    public UseProxyWhiteListSelector(String whiteList, ProxySelector proxySelector) {
        if (whiteList == null) {
            throw new NullPointerException("Whitelist must not be null.");
        }
        if (proxySelector == null) {
            throw new NullPointerException("ProxySelector must not be null.");
        }
        this.delegate = proxySelector;
        WhiteListParser parser = new DefaultWhiteListParser();
        this.whiteListFilter = parser.parseWhiteList(whiteList);
    }

    @Override // java.net.ProxySelector
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        this.delegate.connectFailed(uri, sa, ioe);
    }

    @Override // java.net.ProxySelector
    public List<Proxy> select(URI uri) {
        for (UriFilter filter : this.whiteListFilter) {
            if (filter.accept(uri)) {
                return this.delegate.select(uri);
            }
        }
        return ProxyUtil.noProxyList();
    }

    public String toString() {
        return "UseProxyWhiteListSelector{delegate=" + this.delegate + ", whiteListFilter=" + this.whiteListFilter + '}';
    }
}
