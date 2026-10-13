package com.github.markusbernhardt.proxy.selector.misc;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/misc/ProxyListFallbackSelector.class */
public class ProxyListFallbackSelector extends ProxySelector {
    private static final int DEFAULT_RETRY_DELAY = 600000;
    private ProxySelector delegate;
    private ConcurrentHashMap<SocketAddress, Long> failedDelayCache;
    private long retryAfterMs;

    public ProxyListFallbackSelector(ProxySelector delegate) {
        this(600000L, delegate);
    }

    public ProxyListFallbackSelector(long retryAfterMs, ProxySelector delegate) {
        this.failedDelayCache = new ConcurrentHashMap<>();
        this.delegate = delegate;
        this.retryAfterMs = retryAfterMs;
    }

    @Override // java.net.ProxySelector
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        this.failedDelayCache.put(sa, Long.valueOf(System.currentTimeMillis()));
    }

    @Override // java.net.ProxySelector
    public List<Proxy> select(URI uri) {
        cleanupCache();
        List<Proxy> proxyList = this.delegate.select(uri);
        List<Proxy> result = filterUnresponsiveProxiesFromList(proxyList);
        return result;
    }

    private void cleanupCache() {
        Iterator<Map.Entry<SocketAddress, Long>> it = this.failedDelayCache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<SocketAddress, Long> e = it.next();
            Long lastFailTime = e.getValue();
            if (retryDelayHasPassedBy(lastFailTime)) {
                it.remove();
            }
        }
    }

    private List<Proxy> filterUnresponsiveProxiesFromList(List<Proxy> proxyList) {
        if (this.failedDelayCache.isEmpty()) {
            return proxyList;
        }
        List<Proxy> result = new ArrayList<>(proxyList.size());
        for (Proxy proxy : proxyList) {
            if (isDirect(proxy) || isNotUnresponsive(proxy)) {
                result.add(proxy);
            }
        }
        if (result.isEmpty()) {
            result.add(Proxy.NO_PROXY);
        }
        return result;
    }

    private boolean isDirect(Proxy proxy) {
        return Proxy.NO_PROXY.equals(proxy);
    }

    private boolean isNotUnresponsive(Proxy proxy) {
        Long lastFailTime = this.failedDelayCache.get(proxy.address());
        return retryDelayHasPassedBy(lastFailTime);
    }

    private boolean retryDelayHasPassedBy(Long lastFailTime) {
        return lastFailTime == null || lastFailTime.longValue() + this.retryAfterMs < System.currentTimeMillis();
    }

    final void setRetryAfterMs(long retryAfterMs) {
        this.retryAfterMs = retryAfterMs;
    }

    public String toString() {
        return "ProxyListFallbackSelector{delegate=" + this.delegate + ", failedDelayCache=" + this.failedDelayCache + ", retryAfterMs=" + this.retryAfterMs + '}';
    }
}
