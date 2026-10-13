package com.github.markusbernhardt.proxy.search.desktop.gnome;

import com.aowagie.text.ElementTags;
import com.aowagie.text.xml.XMLConstants;
import com.github.markusbernhardt.proxy.ProxySearchStrategy;
import com.github.markusbernhardt.proxy.selector.direct.NoProxySelector;
import com.github.markusbernhardt.proxy.selector.fixed.FixedProxySelector;
import com.github.markusbernhardt.proxy.selector.misc.ProtocolDispatchSelector;
import com.github.markusbernhardt.proxy.selector.whitelist.ProxyBypassListSelector;
import com.github.markusbernhardt.proxy.util.EmptyXMLResolver;
import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.PlatformUtil;
import com.github.markusbernhardt.proxy.util.ProxyException;
import com.github.markusbernhardt.proxy.util.ProxyUtil;
import java.io.File;
import java.io.IOException;
import java.net.ProxySelector;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.mozilla.javascript.ES6Iterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/desktop/gnome/GnomeProxySearchStrategy.class */
public class GnomeProxySearchStrategy implements ProxySearchStrategy {
    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public ProxySelector getProxySelector() throws ProxyException {
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Detecting Gnome proxy settings", new Object[0]);
        Properties settings = readSettings();
        String type = settings.getProperty("/system/proxy/mode");
        ProxySelector result = null;
        if (type == null) {
            String useProxy = settings.getProperty("/system/http_proxy/use_http_proxy");
            if (useProxy == null) {
                return null;
            }
            type = Boolean.parseBoolean(useProxy) ? "manual" : "none";
        }
        if ("none".equals(type)) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Gnome uses no proxy", new Object[0]);
            result = NoProxySelector.getInstance();
        }
        if ("manual".equals(type)) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Gnome uses manual proxy settings", new Object[0]);
            result = setupFixedProxySelector(settings);
        }
        if ("auto".equals(type)) {
            String pacScriptUrl = settings.getProperty("/system/proxy/autoconfig_url", "");
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Gnome uses autodetect script {}", pacScriptUrl);
            result = ProxyUtil.buildPacSelectorForUrl(pacScriptUrl);
        }
        String noProxyList = settings.getProperty("/system/http_proxy/ignore_hosts", null);
        if (result != null && noProxyList != null && noProxyList.trim().length() > 0) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Gnome uses proxy bypass list: {}", noProxyList);
            result = new ProxyBypassListSelector(noProxyList, result);
        }
        return result;
    }

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public String getName() {
        return "gnome";
    }

    public Properties readSettings() throws ProxyException {
        Properties settings = new Properties();
        try {
            parseSettings("/system/proxy/", settings);
            parseSettings("/system/http_proxy/", settings);
            return settings;
        } catch (IOException e) {
            Logger.log(getClass(), Logger.LogLevel.ERROR, "Gnome settings file error.", e);
            throw new ProxyException(e);
        }
    }

    private File findSettingsFile(String context) {
        File userDir = new File(PlatformUtil.getUserHomeDir());
        StringBuilder path = new StringBuilder();
        String[] parts = context.split("/");
        for (String part : parts) {
            path.append(part);
            path.append(File.separator);
        }
        File settingsFile = new File(userDir, ".gconf" + File.separator + path.toString() + "%gconf.xml");
        if (!settingsFile.exists()) {
            Logger.log(getClass(), Logger.LogLevel.WARNING, "Gnome settings: {} not found.", settingsFile);
            return null;
        }
        return settingsFile;
    }

    private ProxySelector setupFixedProxySelector(Properties settings) {
        if (!hasProxySettings(settings)) {
            return null;
        }
        ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
        installHttpSelector(settings, ps);
        if (useForAllProtocols(settings)) {
            ps.setFallbackSelector(ps.getSelector("http"));
        } else {
            installSecureSelector(settings, ps);
            installFtpSelector(settings, ps);
            installSocksSelector(settings, ps);
        }
        return ps;
    }

    private boolean useForAllProtocols(Properties settings) {
        return Boolean.parseBoolean(settings.getProperty("/system/http_proxy/use_same_proxy", "false"));
    }

    private boolean hasProxySettings(Properties settings) {
        String proxyHost = settings.getProperty("/system/http_proxy/host", null);
        return proxyHost != null && proxyHost.length() > 0;
    }

    private void installHttpSelector(Properties settings, ProtocolDispatchSelector ps) throws NumberFormatException {
        String proxyHost = settings.getProperty("/system/http_proxy/host", null);
        int proxyPort = Integer.parseInt(settings.getProperty("/system/http_proxy/port", "0").trim());
        if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Gnome http proxy is {}:{}", proxyHost, Integer.valueOf(proxyPort));
            ps.setSelector("http", new FixedProxySelector(proxyHost.trim(), proxyPort));
        }
    }

    private void installSocksSelector(Properties settings, ProtocolDispatchSelector ps) throws NumberFormatException {
        String proxyHost = settings.getProperty("/system/proxy/socks_host", null);
        int proxyPort = Integer.parseInt(settings.getProperty("/system/proxy/socks_port", "0").trim());
        if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Gnome socks proxy is {}:{}", proxyHost, Integer.valueOf(proxyPort));
            ps.setSelector("socks", new FixedProxySelector(proxyHost.trim(), proxyPort));
        }
    }

    private void installFtpSelector(Properties settings, ProtocolDispatchSelector ps) throws NumberFormatException {
        String proxyHost = settings.getProperty("/system/proxy/ftp_host", null);
        int proxyPort = Integer.parseInt(settings.getProperty("/system/proxy/ftp_port", "0").trim());
        if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Gnome ftp proxy is {}:{}", proxyHost, Integer.valueOf(proxyPort));
            ps.setSelector("ftp", new FixedProxySelector(proxyHost.trim(), proxyPort));
        }
    }

    private void installSecureSelector(Properties settings, ProtocolDispatchSelector ps) throws NumberFormatException {
        String proxyHost = settings.getProperty("/system/proxy/secure_host", null);
        int proxyPort = Integer.parseInt(settings.getProperty("/system/proxy/secure_port", "0").trim());
        if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "Gnome secure proxy is {}:{}", proxyHost, Integer.valueOf(proxyPort));
            ps.setSelector("https", new FixedProxySelector(proxyHost.trim(), proxyPort));
            ps.setSelector("sftp", new FixedProxySelector(proxyHost.trim(), proxyPort));
        }
    }

    private Properties parseSettings(String context, Properties settings) throws IOException {
        File settingsFile = findSettingsFile(context);
        if (settingsFile == null) {
            return settings;
        }
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            if (Boolean.getBoolean(PlatformUtil.XML_FEATURE_SECURE_PROCESSING)) {
                documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            } else {
                documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            }
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setEntityResolver(new EmptyXMLResolver());
            Document doc = documentBuilder.parse(settingsFile);
            Element root = doc.getDocumentElement();
            for (Node entry = root.getFirstChild(); entry != null; entry = entry.getNextSibling()) {
                if ("entry".equals(entry.getNodeName()) && (entry instanceof Element)) {
                    String entryName = ((Element) entry).getAttribute("name");
                    settings.setProperty(context + entryName, getEntryValue((Element) entry));
                }
            }
            return settings;
        } catch (ParserConfigurationException e) {
            Logger.log(getClass(), Logger.LogLevel.ERROR, "Gnome settings parse error", e);
            throw new IOException(e.getMessage());
        } catch (SAXException e2) {
            Logger.log(getClass(), Logger.LogLevel.ERROR, "Gnome settings parse error", e2);
            throw new IOException(e2.getMessage());
        }
    }

    private String getEntryValue(Element entry) {
        String type = entry.getAttribute("type");
        if ("int".equals(type) || "bool".equals(type)) {
            return entry.getAttribute(ES6Iterator.VALUE_PROPERTY);
        }
        if ("string".equals(type)) {
            NodeList list = entry.getElementsByTagName("stringvalue");
            if (list.getLength() > 0) {
                return list.item(0).getTextContent();
            }
        }
        if (ElementTags.LIST.equals(type)) {
            StringBuilder result = new StringBuilder();
            NodeList list2 = entry.getElementsByTagName("li");
            for (int i = 0; i < list2.getLength(); i++) {
                if (result.length() > 0) {
                    result.append(",");
                }
                result.append(getEntryValue((Element) list2.item(i)));
            }
            return result.toString();
        }
        return null;
    }
}
