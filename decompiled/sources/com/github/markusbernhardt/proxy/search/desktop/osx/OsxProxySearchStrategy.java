package com.github.markusbernhardt.proxy.search.desktop.osx;

import com.github.markusbernhardt.proxy.ProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.browser.ie.IELocalByPassFilter;
import com.github.markusbernhardt.proxy.search.wpad.WpadProxySearchStrategy;
import com.github.markusbernhardt.proxy.selector.direct.NoProxySelector;
import com.github.markusbernhardt.proxy.selector.fixed.FixedProxySelector;
import com.github.markusbernhardt.proxy.selector.fixed.FixedSocksSelector;
import com.github.markusbernhardt.proxy.selector.misc.ProtocolDispatchSelector;
import com.github.markusbernhardt.proxy.selector.whitelist.ProxyBypassListSelector;
import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.PListParser;
import com.github.markusbernhardt.proxy.util.ProxyException;
import com.github.markusbernhardt.proxy.util.ProxyUtil;
import com.github.markusbernhardt.proxy.util.UriFilter;
import es.gob.afirma.keystores.filters.PolicyIdFilter;
import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.ProxySelector;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/desktop/osx/OsxProxySearchStrategy.class */
public class OsxProxySearchStrategy implements ProxySearchStrategy {
    public static final String OVERRIDE_SETTINGS_FILE = "com.github.markusbernhardt.proxy.osx.settingsFile";
    public static final String OVERRIDE_ACCEPTED_DEVICES = "com.github.markusbernhardt.proxy.osx.acceptedDevices";
    private static final String SETTINGS_FILE = "/Library/Preferences/SystemConfiguration/preferences.plist";

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public ProxySelector getProxySelector() throws ProxyException {
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Detecting OSX proxy settings", new Object[0]);
        try {
            List<String> acceptedInterfaces = getNetworkInterfaces();
            PListParser.Dict settings = PListParser.load(getSettingsFile());
            Object currentSet = settings.getAtPath("/CurrentSet");
            if (currentSet == null) {
                throw new ProxyException("CurrentSet not defined");
            }
            PListParser.Dict networkSet = (PListParser.Dict) settings.getAtPath(String.valueOf(currentSet));
            List<?> serviceOrder = (List) networkSet.getAtPath("/Network/Global/IPv4/ServiceOrder");
            if (serviceOrder == null || serviceOrder.isEmpty()) {
                throw new ProxyException("ServiceOrder not defined");
            }
            PListParser.Dict proxySettings = null;
            for (int i = 0; i < serviceOrder.size() && proxySettings == null; i++) {
                Object candidateService = serviceOrder.get(i);
                Object networkService = networkSet.getAtPath("/Network/Service/" + candidateService + "/__LINK__");
                if (networkService == null) {
                    throw new ProxyException("NetworkService not defined.");
                }
                PListParser.Dict selectedServiceSettings = (PListParser.Dict) settings.getAtPath("" + networkService);
                if (selectedServiceSettings != null) {
                    String interfaceName = (String) selectedServiceSettings.getAtPath("/Interface/DeviceName");
                    if (acceptedInterfaces.contains(interfaceName)) {
                        Logger.log(getClass(), Logger.LogLevel.TRACE, "Looking up proxies for device " + interfaceName, new Object[0]);
                        proxySettings = (PListParser.Dict) selectedServiceSettings.getAtPath("/Proxies");
                    }
                }
            }
            if (proxySettings == null) {
                return NoProxySelector.getInstance();
            }
            return buildSelector(proxySettings);
        } catch (PListParser.XmlParseException | IOException e) {
            throw new ProxyException(e);
        }
    }

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public String getName() {
        return "osx";
    }

    private ProxySelector buildSelector(PListParser.Dict proxySettings) throws ProxyException {
        ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
        installSelectorForProtocol(proxySettings, ps, "HTTP");
        installSelectorForProtocol(proxySettings, ps, "HTTPS");
        installSelectorForProtocol(proxySettings, ps, "FTP");
        installSelectorForProtocol(proxySettings, ps, "Gopher");
        installSelectorForProtocol(proxySettings, ps, "RTSP");
        installSocksProxy(proxySettings, ps);
        ProxySelector result = autodetectProxyIfAvailable(proxySettings, installPacProxyIfAvailable(proxySettings, ps));
        if (result != null) {
            result = installSimpleHostFilter(proxySettings, installExceptionList(proxySettings, result));
        }
        return result;
    }

    private List<String> getNetworkInterfaces() throws SocketException {
        String override = System.getProperty(OVERRIDE_ACCEPTED_DEVICES);
        if (override != null && override.length() > 0) {
            return Arrays.asList(override.split(PolicyIdFilter.OID_SEPARATOR));
        }
        List<String> acceptedInterfaces = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (isInterfaceAllowed(ni)) {
                acceptedInterfaces.add(ni.getName());
            }
        }
        return acceptedInterfaces;
    }

    private boolean isInterfaceAllowed(NetworkInterface ni) throws SocketException {
        return (ni.isLoopback() || ni.isPointToPoint() || ni.isVirtual() || !ni.isUp()) ? false : true;
    }

    private File getSettingsFile() {
        File result = new File(SETTINGS_FILE);
        String overrideFile = System.getProperty(OVERRIDE_SETTINGS_FILE);
        if (overrideFile != null) {
            return new File(overrideFile);
        }
        return result;
    }

    private ProxySelector installSimpleHostFilter(PListParser.Dict proxySettings, ProxySelector result) {
        if (isActive(proxySettings.get("ExcludeSimpleHostnames"))) {
            List<UriFilter> localBypassFilter = new ArrayList<>();
            localBypassFilter.add(new IELocalByPassFilter());
            result = new ProxyBypassListSelector(localBypassFilter, result);
        }
        return result;
    }

    private ProxySelector installExceptionList(PListParser.Dict proxySettings, ProxySelector result) {
        List<?> proxyExceptions = (List) proxySettings.get("ExceptionsList");
        if (proxyExceptions != null && !proxyExceptions.isEmpty()) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "OSX uses proxy bypass list: {}", proxyExceptions);
            String noProxyList = toCommaSeparatedString(proxyExceptions);
            result = new ProxyBypassListSelector(noProxyList, result);
        }
        return result;
    }

    private String toCommaSeparatedString(List<?> proxyExceptions) {
        StringBuilder result = new StringBuilder();
        for (Object object : proxyExceptions) {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append(object);
        }
        return result.toString();
    }

    private ProxySelector autodetectProxyIfAvailable(PListParser.Dict proxySettings, ProxySelector result) throws ProxyException {
        ProxySelector wp;
        if (isActive(proxySettings.get("ProxyAutoDiscoveryEnable")) && (wp = new WpadProxySearchStrategy().getProxySelector()) != null) {
            result = wp;
        }
        return result;
    }

    private ProxySelector installPacProxyIfAvailable(PListParser.Dict proxySettings, ProxySelector result) {
        if (isActive(proxySettings.get("ProxyAutoConfigEnable"))) {
            String url = (String) proxySettings.get("ProxyAutoConfigURLString");
            result = ProxyUtil.buildPacSelectorForUrl(url);
        }
        return result;
    }

    private void installSocksProxy(PListParser.Dict proxySettings, ProtocolDispatchSelector ps) {
        if (isActive(proxySettings.get("SOCKSEnable"))) {
            String proxyHost = (String) proxySettings.get("SOCKSProxy");
            int proxyPort = ((Integer) proxySettings.get("SOCKSPort")).intValue();
            ps.setSelector("socks", new FixedSocksSelector(proxyHost, proxyPort));
            Logger.log(getClass(), Logger.LogLevel.TRACE, "OSX socks proxy is {}:{}", proxyHost, Integer.valueOf(proxyPort));
        }
    }

    private void installSelectorForProtocol(PListParser.Dict proxySettings, ProtocolDispatchSelector ps, String protocol) {
        String prefix = protocol.trim();
        if (isActive(proxySettings.get(prefix + "Enable"))) {
            String proxyHost = (String) proxySettings.get(prefix + "Proxy");
            int proxyPort = ((Integer) proxySettings.get(prefix + "Port")).intValue();
            FixedProxySelector fp = new FixedProxySelector(proxyHost, proxyPort);
            ps.setSelector(protocol.toLowerCase(), fp);
            Logger.log(getClass(), Logger.LogLevel.TRACE, "OSX uses for {} the proxy {}:{}", protocol, proxyHost, Integer.valueOf(proxyPort));
        }
    }

    private boolean isActive(Object value) {
        Integer num = 1;
        return num.equals(value);
    }
}
