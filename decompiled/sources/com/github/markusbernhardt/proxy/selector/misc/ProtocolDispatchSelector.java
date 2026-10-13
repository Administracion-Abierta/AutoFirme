package com.github.markusbernhardt.proxy.selector.misc;

import com.github.markusbernhardt.proxy.selector.direct.NoProxySelector;
import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/misc/ProtocolDispatchSelector.class */
public class ProtocolDispatchSelector extends ProxySelector {
    private Map<String, ProxySelector> selectors = new ConcurrentHashMap();
    private ProxySelector fallbackSelector = NoProxySelector.getInstance();

    public void setSelector(String protocol, ProxySelector selector) {
        if (protocol == null) {
            throw new NullPointerException("Protocol must not be null.");
        }
        if (selector == null) {
            throw new NullPointerException("Selector must not be null.");
        }
        this.selectors.put(protocol, selector);
    }

    public ProxySelector removeSelector(String protocol) {
        return this.selectors.remove(protocol);
    }

    public ProxySelector getSelector(String protocol) {
        return this.selectors.get(protocol);
    }

    public void setFallbackSelector(ProxySelector selector) {
        if (selector == null) {
            throw new NullPointerException("Selector must not be null.");
        }
        this.fallbackSelector = selector;
    }

    @Override // java.net.ProxySelector
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        ProxySelector selector = this.fallbackSelector;
        String protocol = uri.getScheme();
        if (protocol != null && this.selectors.get(protocol) != null) {
            selector = this.selectors.get(protocol);
        }
        selector.connectFailed(uri, sa, ioe);
    }

    @Override // java.net.ProxySelector
    public List<Proxy> select(URI uri) {
        ProxySelector selector = this.fallbackSelector;
        String protocol = uri.getScheme();
        if (protocol != null && this.selectors.get(protocol) != null) {
            selector = this.selectors.get(protocol);
        }
        return selector.select(uri);
    }

    public int size() {
        return this.selectors.size();
    }

    public String toString() {
        return "ProtocolDispatchSelector{selectors=" + this.selectors + ", fallbackSelector=" + this.fallbackSelector + '}';
    }
}
