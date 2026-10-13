package com.github.markusbernhardt.proxy.selector.fixed;

import java.net.InetSocketAddress;
import java.net.Proxy;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/fixed/FixedSocksSelector.class */
public class FixedSocksSelector extends FixedProxySelector {
    public FixedSocksSelector(String proxyHost, int proxyPort) {
        super(new Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(proxyHost, proxyPort)));
    }
}
