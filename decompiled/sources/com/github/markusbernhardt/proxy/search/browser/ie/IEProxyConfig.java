package com.github.markusbernhardt.proxy.search.browser.ie;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/browser/ie/IEProxyConfig.class */
public class IEProxyConfig {
    private boolean autoDetect;
    private String autoConfigUrl;
    private String proxy;
    private String proxyBypass;

    public IEProxyConfig(boolean autoDetect, String autoConfigUrl, String proxy, String proxyBypass) {
        this.autoDetect = autoDetect;
        this.autoConfigUrl = autoConfigUrl;
        this.proxy = proxy;
        this.proxyBypass = proxyBypass;
    }

    public boolean isAutoDetect() {
        return this.autoDetect;
    }

    public String getAutoConfigUrl() {
        return this.autoConfigUrl;
    }

    public String getProxy() {
        return this.proxy;
    }

    public String getProxyBypass() {
        return this.proxyBypass;
    }
}
