package com.github.markusbernhardt.proxy.search.desktop.win;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/desktop/win/WinProxyConfig.class */
public class WinProxyConfig {
    private int accessType;
    private String proxy;
    private String proxyBypass;

    public WinProxyConfig(int accessType, String proxy, String proxyBypass) {
        this.accessType = accessType;
        this.proxy = proxy;
        this.proxyBypass = proxyBypass;
    }

    public int getAccessType() {
        return this.accessType;
    }

    public String getProxy() {
        return this.proxy;
    }

    public String getProxyBypass() {
        return this.proxyBypass;
    }
}
