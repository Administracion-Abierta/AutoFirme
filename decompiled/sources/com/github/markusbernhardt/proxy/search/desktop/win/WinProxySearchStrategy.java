package com.github.markusbernhardt.proxy.search.desktop.win;

import com.github.markusbernhardt.proxy.jna.win.WinHttp;
import com.github.markusbernhardt.proxy.jna.win.WinHttpHelpers;
import com.github.markusbernhardt.proxy.jna.win.WinHttpProxyInfo;
import com.github.markusbernhardt.proxy.selector.misc.ProtocolDispatchSelector;
import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.ProxyException;
import java.net.ProxySelector;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/desktop/win/WinProxySearchStrategy.class */
public class WinProxySearchStrategy extends CommonWindowsSearchStrategy {
    static final int WINHTTP_ACCESS_TYPE_DEFAULT_PROXY = 0;
    static final int WINHTTP_ACCESS_TYPE_NO_PROXY = 1;
    static final int WINHTTP_ACCESS_TYPE_NAMED_PROXY = 3;

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public ProxySelector getProxySelector() throws ProxyException {
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Detecting Windows proxy settings", new Object[0]);
        WinProxyConfig windowsProxyConfig = readWindowsProxyConfig();
        if (windowsProxyConfig.getAccessType() == 1) {
            return null;
        }
        return createFixedProxySelector(windowsProxyConfig);
    }

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public String getName() {
        return "windows";
    }

    public WinProxyConfig readWindowsProxyConfig() {
        WinHttpProxyInfo winHttpProxyInfo = new WinHttpProxyInfo();
        boolean result = WinHttp.INSTANCE.WinHttpGetDefaultProxyConfiguration(winHttpProxyInfo);
        if (!result) {
            return null;
        }
        return new WinProxyConfig(winHttpProxyInfo.dwAccessType != null ? winHttpProxyInfo.dwAccessType.intValue() : 0, WinHttpHelpers.getAndFreeGlobalString(winHttpProxyInfo.lpszProxy), WinHttpHelpers.getAndFreeGlobalString(winHttpProxyInfo.lpszProxyBypass));
    }

    private ProxySelector createFixedProxySelector(WinProxyConfig winProxyConfig) throws ProxyException {
        String proxyString = winProxyConfig.getProxy();
        String bypassList = winProxyConfig.getProxyBypass();
        if (proxyString == null) {
            return null;
        }
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Windows uses manual settings: {} with bypass list: {}", proxyString, bypassList);
        Properties p = parseProxyList(proxyString);
        ProtocolDispatchSelector ps = buildProtocolDispatchSelector(p);
        ProxySelector result = setByPassListOnSelector(bypassList, ps);
        return result;
    }
}
