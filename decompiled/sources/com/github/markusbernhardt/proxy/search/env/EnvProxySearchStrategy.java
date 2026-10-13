package com.github.markusbernhardt.proxy.search.env;

import com.github.markusbernhardt.proxy.ProxySearchStrategy;
import com.github.markusbernhardt.proxy.selector.misc.ProtocolDispatchSelector;
import com.github.markusbernhardt.proxy.selector.whitelist.ProxyBypassListSelector;
import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.ProxyUtil;
import java.net.ProxySelector;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/env/EnvProxySearchStrategy.class */
public class EnvProxySearchStrategy implements ProxySearchStrategy {
    private String httpEnv;
    private String httpsEnv;
    private String ftpEnv;
    private String noProxyEnv;
    private String httpProxy;
    private String httpsProxy;
    private String ftpProxy;
    private String noProxy;

    public EnvProxySearchStrategy() {
        this("http_proxy", "https_proxy", "ftp_proxy", "no_proxy");
    }

    public EnvProxySearchStrategy(String httpEnv, String httpsEnv, String ftpEnv, String noProxyEnv) {
        this.httpEnv = httpEnv;
        this.httpsEnv = httpsEnv;
        this.ftpEnv = ftpEnv;
        this.noProxyEnv = noProxyEnv;
        loadProxySettings();
    }

    private void loadProxySettings() {
        this.httpProxy = System.getenv(this.httpEnv);
        this.httpsProxy = System.getenv(this.httpsEnv);
        this.ftpProxy = System.getenv(this.ftpEnv);
        this.noProxy = System.getenv(this.noProxyEnv);
    }

    public Properties readSettings() {
        Properties result = new Properties();
        result.setProperty(this.httpEnv, this.httpProxy);
        result.setProperty(this.httpsEnv, this.httpsProxy);
        result.setProperty(this.ftpEnv, this.ftpProxy);
        result.setProperty(this.noProxyEnv, this.noProxy);
        return result;
    }

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public ProxySelector getProxySelector() {
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Inspecting environment variables.", new Object[0]);
        ProxySelector httpPS = ProxyUtil.parseProxySettings(this.httpProxy);
        if (httpPS == null) {
            return null;
        }
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Http Proxy is {}", this.httpProxy);
        ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
        ps.setSelector("http", httpPS);
        ProxySelector httpsPS = ProxyUtil.parseProxySettings(this.httpsProxy);
        Class<?> cls = getClass();
        Logger.LogLevel logLevel = Logger.LogLevel.TRACE;
        Object[] objArr = new Object[1];
        objArr[0] = httpsPS == null ? this.httpsProxy : httpsPS;
        Logger.log(cls, logLevel, "Https Proxy is {}", objArr);
        ps.setSelector("https", httpsPS != null ? httpsPS : httpPS);
        ProxySelector ftpPS = ProxyUtil.parseProxySettings(this.ftpProxy);
        if (ftpPS != null) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Ftp Proxy is {}", this.ftpProxy);
            ps.setSelector("ftp", ftpPS);
        }
        ProxySelector result = ps;
        if (this.noProxy != null && this.noProxy.trim().length() > 0) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Using proxy bypass list: {}", this.noProxy);
            result = new ProxyBypassListSelector(this.noProxy, ps);
        }
        return result;
    }

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public String getName() {
        return "env";
    }
}
