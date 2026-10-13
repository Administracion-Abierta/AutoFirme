package com.github.markusbernhardt.proxy.search.wpad;

import com.github.markusbernhardt.proxy.ProxySearchStrategy;
import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.ProxyException;
import com.github.markusbernhardt.proxy.util.ProxyUtil;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/wpad/WpadProxySearchStrategy.class */
public class WpadProxySearchStrategy implements ProxySearchStrategy {
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
        String fqdn = InetAddress.getLocalHost().getCanonicalHostName();
        if (fqdn.equalsIgnoreCase("localhost") || fqdn.length() == 0 || Character.isDigit(fqdn.charAt(0))) {
            return null;
        }
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Searching per DNS guessing.", new Object[0]);
        String[] fqdnParts = fqdn.split("\\.");
        for (int i = 0; i < fqdnParts.length; i++) {
            if (i != fqdnParts.length - 2) {
                StringBuilder stringBuilder = new StringBuilder("http://wpad");
                for (int j = i + 1; j < fqdnParts.length; j++) {
                    stringBuilder.append('.');
                    stringBuilder.append(fqdnParts[j]);
                }
                stringBuilder.append("/wpad.dat");
                try {
                    URL lookupURL = new URL(stringBuilder.toString());
                    Logger.log(getClass(), Logger.LogLevel.TRACE, "Trying url: {}", lookupURL);
                    HttpURLConnection con = (HttpURLConnection) lookupURL.openConnection(Proxy.NO_PROXY);
                    con.setInstanceFollowRedirects(true);
                    con.setRequestProperty("accept", "application/x-ns-proxy-autoconfig");
                    if (con.getResponseCode() == 200) {
                        return lookupURL.toString();
                    }
                    con.disconnect();
                } catch (UnknownHostException e) {
                    Logger.log(getClass(), Logger.LogLevel.DEBUG, "Not available!", new Object[0]);
                }
            }
        }
        return null;
    }

    private String detectScriptUrlPerDHCP() throws IOException {
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Searching per DHCP.", new Object[0]);
        String url = null;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface network = interfaces.nextElement();
                if (!network.getName().equals("lo")) {
                    byte[] mac = network.getHardwareAddress();
                    Enumeration<InetAddress> ips = network.getInetAddresses();
                    while (ips.hasMoreElements()) {
                        InetAddress ip = ips.nextElement();
                        if (ip.getAddress().length == 4) {
                            url = checkDhcpAckForPAC(ip, mac);
                        }
                        if (url != null) {
                            return url;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return url;
    }

    private String getMacString(byte[] mac) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < 6) {
            Object[] objArr = new Object[2];
            objArr[0] = Byte.valueOf(mac[i]);
            objArr[1] = i < 5 ? ":" : "";
            sb.append(String.format("%02X%s", objArr));
            i++;
        }
        return sb.toString();
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x01bb */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String checkDhcpAckForPAC(java.net.InetAddress r8, byte[] r9) throws java.io.IOException {
        /*
            Method dump skipped, instruction units count: 482
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.github.markusbernhardt.proxy.search.wpad.WpadProxySearchStrategy.checkDhcpAckForPAC(java.net.InetAddress, byte[]):java.lang.String");
    }
}
