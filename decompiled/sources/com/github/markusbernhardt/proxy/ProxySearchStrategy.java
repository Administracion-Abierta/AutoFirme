package com.github.markusbernhardt.proxy;

import com.github.markusbernhardt.proxy.util.ProxyException;
import java.net.ProxySelector;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/ProxySearchStrategy.class */
public interface ProxySearchStrategy {
    ProxySelector getProxySelector() throws ProxyException;

    String getName();
}
