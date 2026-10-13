package com.github.markusbernhardt.proxy.search.browser.ie;

import com.github.markusbernhardt.proxy.jna.win.WinHttp;
import com.github.markusbernhardt.proxy.jna.win.WinHttpCurrentUserIEProxyConfig;
import com.github.markusbernhardt.proxy.jna.win.WinHttpHelpers;
import com.github.markusbernhardt.proxy.search.desktop.win.CommonWindowsSearchStrategy;
import com.github.markusbernhardt.proxy.selector.misc.ProtocolDispatchSelector;
import com.github.markusbernhardt.proxy.selector.pac.PacProxySelector;
import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.ProxyException;
import com.github.markusbernhardt.proxy.util.ProxyUtil;
import com.sun.jna.platform.win32.WinDef;
import java.net.ProxySelector;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/browser/ie/IEProxySearchStrategy.class */
public class IEProxySearchStrategy extends CommonWindowsSearchStrategy {
    private static final int WINHTTP_AUTO_DETECT_TYPE_DHCP = 1;
    private static final int WINHTTP_AUTO_DETECT_TYPE_DNS_A = 2;

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public ProxySelector getProxySelector() throws ProxyException {
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Detecting IE proxy settings", new Object[0]);
        IEProxyConfig ieProxyConfig = readIEProxyConfig();
        ProxySelector result = createPacSelector(ieProxyConfig);
        if (result == null) {
            result = createFixedProxySelector(ieProxyConfig);
        }
        return result;
    }

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public String getName() {
        return "IE";
    }

    public IEProxyConfig readIEProxyConfig() {
        WinHttpCurrentUserIEProxyConfig winHttpCurrentUserIeProxyConfig = new WinHttpCurrentUserIEProxyConfig();
        boolean result = WinHttp.INSTANCE.WinHttpGetIEProxyConfigForCurrentUser(winHttpCurrentUserIeProxyConfig);
        if (!result) {
            return null;
        }
        return new IEProxyConfig(winHttpCurrentUserIeProxyConfig.fAutoDetect, WinHttpHelpers.getAndFreeGlobalString(winHttpCurrentUserIeProxyConfig.lpszAutoConfigUrl), WinHttpHelpers.getAndFreeGlobalString(winHttpCurrentUserIeProxyConfig.lpszProxy), WinHttpHelpers.getAndFreeGlobalString(winHttpCurrentUserIeProxyConfig.lpszProxyBypass));
    }

    private PacProxySelector createPacSelector(IEProxyConfig ieProxyConfig) {
        String pacUrl = null;
        if (ieProxyConfig.isAutoDetect()) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Autodetecting script URL.", new Object[0]);
            WinDef.DWORD dwAutoDetectFlags = new WinDef.DWORD(3L);
            pacUrl = WinHttpHelpers.detectAutoProxyConfigUrl(dwAutoDetectFlags);
        }
        if (pacUrl == null) {
            pacUrl = ieProxyConfig.getAutoConfigUrl();
        }
        if (pacUrl != null && pacUrl.trim().length() > 0) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "IE uses script: " + pacUrl, new Object[0]);
            if (pacUrl.startsWith("file://") && !pacUrl.startsWith("file:///")) {
                pacUrl = "file:///" + pacUrl.substring(7);
            }
            return ProxyUtil.buildPacSelectorForUrl(pacUrl);
        }
        return null;
    }

    private ProxySelector createFixedProxySelector(IEProxyConfig ieProxyConfig) throws ProxyException {
        String proxyString = ieProxyConfig.getProxy();
        String bypassList = ieProxyConfig.getProxyBypass();
        if (proxyString == null) {
            return null;
        }
        Logger.log(getClass(), Logger.LogLevel.TRACE, "IE uses manual settings: {} with bypass list: {}", proxyString, bypassList);
        Properties p = parseProxyList(proxyString);
        ProtocolDispatchSelector ps = buildProtocolDispatchSelector(p);
        ProxySelector result = setByPassListOnSelector(bypassList, ps);
        return result;
    }
}
