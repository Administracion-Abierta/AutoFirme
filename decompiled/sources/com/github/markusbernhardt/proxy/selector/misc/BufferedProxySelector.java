package com.github.markusbernhardt.proxy.selector.misc;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/misc/BufferedProxySelector.class */
public class BufferedProxySelector extends ProxySelector {
    private ProxySelector delegate;
    private ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private int maxSize;
    private long ttl;
    private CacheScope cacheScope;

    /* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/misc/BufferedProxySelector$CacheScope.class */
    public enum CacheScope {
        CACHE_SCOPE_HOST,
        CACHE_SCOPE_HOST_PORT,
        CACHE_SCOPE_URL
    }

    public String toString() {
        return "BufferedProxySelector{delegate=" + this.delegate + ", cache=" + this.cache + ", maxSize=" + this.maxSize + ", ttl=" + this.ttl + ", cacheScope=" + this.cacheScope + '}';
    }

    /* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/misc/BufferedProxySelector$CacheEntry.class */
    private static class CacheEntry {
        List<Proxy> result;
        long expireAt;

        public CacheEntry(List<Proxy> r, long expireAt) {
            this.result = new ArrayList(r.size());
            this.result.addAll(r);
            this.result = Collections.unmodifiableList(this.result);
            this.expireAt = expireAt;
        }

        public boolean isExpired() {
            return System.nanoTime() >= this.expireAt;
        }
    }

    public BufferedProxySelector(int maxSize, long ttl, ProxySelector delegate, CacheScope cacheScope) {
        this.maxSize = maxSize;
        this.delegate = delegate;
        this.ttl = ttl;
        this.cacheScope = cacheScope;
    }

    @Override // java.net.ProxySelector
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        this.delegate.connectFailed(uri, sa, ioe);
    }

    @Override // java.net.ProxySelector
    public List<Proxy> select(URI uri) {
        String cacheKey;
        switch (this.cacheScope) {
            case CACHE_SCOPE_HOST:
                cacheKey = uri.getHost();
                break;
            case CACHE_SCOPE_HOST_PORT:
                cacheKey = uri.getHost() + ":" + uri.getPort();
                break;
            case CACHE_SCOPE_URL:
                cacheKey = uri.toString();
                break;
            default:
                throw new RuntimeException("FixMe: Unhandled CacheScope enum constant.");
        }
        CacheEntry entry = null;
        if (cacheKey != null) {
            entry = this.cache.get(cacheKey);
        }
        if (entry == null || entry.isExpired()) {
            List<Proxy> result = this.delegate.select(uri);
            entry = new CacheEntry(result, System.nanoTime() + (this.ttl * 1000 * 1000));
            synchronized (this.cache) {
                if (this.cache.size() >= this.maxSize) {
                    purgeCache();
                }
                if (cacheKey != null) {
                    this.cache.put(cacheKey, entry);
                }
            }
        }
        return entry.result;
    }

    private void purgeCache() {
        boolean removedOne = false;
        Map.Entry<String, CacheEntry> oldest = null;
        Set<Map.Entry<String, CacheEntry>> entries = this.cache.entrySet();
        Iterator<Map.Entry<String, CacheEntry>> it = entries.iterator();
        while (it.hasNext()) {
            Map.Entry<String, CacheEntry> entry = it.next();
            if (entry.getValue().isExpired()) {
                it.remove();
                removedOne = true;
            } else if (oldest == null || entry.getValue().expireAt < oldest.getValue().expireAt) {
                oldest = entry;
            }
        }
        if (!removedOne && oldest != null) {
            this.cache.remove(oldest.getKey());
        }
    }
}
