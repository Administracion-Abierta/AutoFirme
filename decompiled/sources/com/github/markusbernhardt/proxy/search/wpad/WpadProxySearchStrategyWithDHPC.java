package com.github.markusbernhardt.proxy.search.wpad;

import com.github.markusbernhardt.proxy.ProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.wpad.dhcp.DHCPMessage;
import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.ProxyException;
import com.github.markusbernhardt.proxy.util.ProxyUtil;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/wpad/WpadProxySearchStrategyWithDHPC.class */
public class WpadProxySearchStrategyWithDHPC implements ProxySearchStrategy {
    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public ProxySelector getProxySelector() throws ProxyException {
        try {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Using WPAD to find a proxy", new Object[0]);
            String pacScriptUrl = detectScriptUrlPerDHCP();
            if (pacScriptUrl == null) {
                pacScriptUrl = detectScriptUrlPerDNS();
            }
            if (pacScriptUrl == null) {
                return null;
            }
            Logger.log(getClass(), Logger.LogLevel.TRACE, "PAC script url found: {}", pacScriptUrl);
            return ProxyUtil.buildPacSelectorForUrl(pacScriptUrl);
        } catch (IOException e) {
            Logger.log(getClass(), Logger.LogLevel.ERROR, "Error during WPAD search.", e);
            throw new ProxyException(e);
        }
    }

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public String getName() {
        return "wpad";
    }

    public Properties readSettings() {
        try {
            String pacScriptUrl = detectScriptUrlPerDHCP();
            if (pacScriptUrl == null) {
                pacScriptUrl = detectScriptUrlPerDNS();
            }
            if (pacScriptUrl == null) {
                return null;
            }
            Properties result = new Properties();
            result.setProperty("url", pacScriptUrl);
            return result;
        } catch (IOException e) {
            return new Properties();
        }
    }

    private String detectScriptUrlPerDNS() throws IOException {
        String result = null;
        String fqdn = InetAddress.getLocalHost().getCanonicalHostName();
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Searching per DNS guessing.", new Object[0]);
        int iIndexOf = fqdn.indexOf(46);
        while (true) {
            int index = iIndexOf;
            if (index == -1 || result != null) {
                break;
            }
            fqdn = fqdn.substring(index + 1);
            if (fqdn.indexOf(46) == -1) {
                break;
            }
            try {
                URL lookupURL = new URL("http://wpad." + fqdn + "/wpad.dat");
                Logger.log(getClass(), Logger.LogLevel.TRACE, "Trying url: {}", lookupURL);
                HttpURLConnection con = (HttpURLConnection) lookupURL.openConnection(Proxy.NO_PROXY);
                con.setInstanceFollowRedirects(true);
                con.setRequestProperty("accept", "application/x-ns-proxy-autoconfig");
                if (con.getResponseCode() == 200) {
                    result = lookupURL.toString();
                }
                con.disconnect();
            } catch (UnknownHostException e) {
                Logger.log(getClass(), Logger.LogLevel.DEBUG, "Not available!", new Object[0]);
            }
            iIndexOf = fqdn.indexOf(46);
        }
        return result;
    }

    private String detectScriptUrlPerDHCP() {
        Logger.log(getClass(), Logger.LogLevel.DEBUG, "Searching per DHCP not supported yet.", new Object[0]);
        return null;
    }

    private DHCPMessage SendRequest(DHCPMessage offerMessageIn) {
        return null;
    }
}
