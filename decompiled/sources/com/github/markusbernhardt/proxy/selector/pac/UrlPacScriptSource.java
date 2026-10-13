package com.github.markusbernhardt.proxy.selector.pac;

import com.github.markusbernhardt.proxy.util.Logger;
import es.gob.afirma.keystores.filters.PolicyIdFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/pac/UrlPacScriptSource.class */
public class UrlPacScriptSource implements PacScriptSource {
    private static final int DEFAULT_CONNECT_TIMEOUT = 15000;
    private static final int DEFAULT_READ_TIMEOUT = 20000;
    public static final String OVERRIDE_CONNECT_TIMEOUT = "com.btr.proxy.url.connectTimeout";
    public static final String OVERRIDE_READ_TIMEOUT = "com.btr.proxy.url.readTimeout";
    private final String scriptUrl;
    private String scriptContent;
    private long expireAtMillis = 0;

    public UrlPacScriptSource(String url) {
        this.scriptUrl = url;
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.PacScriptSource
    public synchronized String getScriptContent() throws IOException {
        if (this.scriptContent == null || (this.expireAtMillis > 0 && this.expireAtMillis < System.currentTimeMillis())) {
            try {
                this.expireAtMillis = 0L;
                if (this.scriptUrl.startsWith("file:/") || this.scriptUrl.indexOf(":/") == -1) {
                    this.scriptContent = readPacFileContent(this.scriptUrl);
                } else {
                    this.scriptContent = downloadPacContent(this.scriptUrl);
                }
            } catch (IOException e) {
                Logger.log(getClass(), Logger.LogLevel.ERROR, "Loading script failed from: {} with error {}", this.scriptUrl, e);
                this.scriptContent = "";
                throw e;
            }
        }
        return this.scriptContent;
    }

    /* JADX WARN: Code duplicated, block: B:24:0x0092  */
    /* JADX WARN: Code duplicated, block: B:29:0x00ab A[Catch: Exception -> 0x00b8, TryCatch #2 {Exception -> 0x00b8, blocks: (B:2:0x0000, B:4:0x000c, B:6:0x002b, B:7:0x0042, B:9:0x004d, B:14:0x0066, B:17:0x007a, B:16:0x0070, B:32:0x00b3, B:20:0x008a, B:26:0x0097, B:29:0x00ab, B:28:0x00a1, B:31:0x00b2, B:5:0x0018), top: B:41:0x0000, inners: #0, #1, #4 }] */
    /* JADX WARN: Code duplicated, block: B:37:0x0097 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private String readPacFileContent(String scriptUrl) throws IOException {
        File file;
        try {
            if (scriptUrl.indexOf(":/") == -1) {
                file = new File(scriptUrl);
            } else {
                file = new File(new URL(scriptUrl).toURI());
            }
            StringBuilder result = new StringBuilder();
            BufferedReader r = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
            Throwable th = null;
            while (true) {
                try {
                    try {
                        String line = r.readLine();
                        if (line == null) {
                            break;
                        }
                        result.append(line).append("\n");
                    } catch (Throwable th2) {
                        th = th2;
                        throw th2;
                    }
                } catch (Throwable th3) {
                    if (r != null) {
                        if (th != null) {
                            try {
                                r.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            r.close();
                        }
                    }
                    throw th3;
                }
                if (r != null) {
                    if (th != null) {
                        r.close();
                    } else {
                        r.close();
                    }
                }
                throw th3;
            }
            if (r != null) {
                if (0 != 0) {
                    try {
                        r.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    r.close();
                }
            }
            return result.toString();
        } catch (Exception e) {
            System.out.println(System.getProperty("user.dir"));
            Logger.log(getClass(), Logger.LogLevel.ERROR, "File reading error.", e);
            throw new IOException(e.getMessage());
        }
    }

    private String downloadPacContent(String url) throws IOException {
        if (url == null) {
            throw new IOException("Invalid PAC script URL: null");
        }
        setPacProxySelectorEnabled(false);
        HttpURLConnection con = null;
        try {
            HttpURLConnection con2 = setupHTTPConnection(url);
            if (con2.getResponseCode() != 200) {
                throw new IOException("Server returned: " + con2.getResponseCode() + " " + con2.getResponseMessage());
            }
            this.expireAtMillis = con2.getExpiration();
            BufferedReader r = getReader(con2);
            String result = readAllContent(r);
            r.close();
            setPacProxySelectorEnabled(true);
            if (con2 != null) {
                con2.disconnect();
            }
            return result;
        } catch (Throwable th) {
            setPacProxySelectorEnabled(true);
            if (0 != 0) {
                con.disconnect();
            }
            throw th;
        }
    }

    private void setPacProxySelectorEnabled(boolean enable) {
        PacProxySelector.setEnabled(enable);
    }

    private String readAllContent(BufferedReader r) throws IOException {
        StringBuilder result = new StringBuilder();
        while (true) {
            String line = r.readLine();
            if (line != null) {
                result.append(line).append("\n");
            } else {
                return result.toString();
            }
        }
    }

    private BufferedReader getReader(HttpURLConnection con) throws IOException {
        String charsetName = parseCharsetFromHeader(con.getContentType());
        BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream(), charsetName));
        return r;
    }

    private HttpURLConnection setupHTTPConnection(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection(Proxy.NO_PROXY);
        con.setConnectTimeout(getTimeOut(OVERRIDE_CONNECT_TIMEOUT, 15000));
        con.setReadTimeout(getTimeOut(OVERRIDE_READ_TIMEOUT, DEFAULT_READ_TIMEOUT));
        con.setInstanceFollowRedirects(true);
        con.setRequestProperty("accept", "application/x-ns-proxy-autoconfig, */*;q=0.8");
        return con;
    }

    protected int getTimeOut(String overrideProperty, int defaultValue) {
        int timeout = defaultValue;
        String prop = System.getProperty(overrideProperty);
        if (prop != null && prop.trim().length() > 0) {
            try {
                timeout = Integer.parseInt(prop.trim());
            } catch (NumberFormatException e) {
                Logger.log(getClass(), Logger.LogLevel.DEBUG, "Invalid override property : {}={}", overrideProperty, prop);
            }
        }
        return timeout;
    }

    String parseCharsetFromHeader(String contentType) {
        String result = "UTF-8";
        if (contentType != null) {
            String[] paramList = contentType.split(PolicyIdFilter.OID_SEPARATOR);
            for (String param : paramList) {
                if (param.toLowerCase().trim().startsWith("charset") && param.indexOf("=") != -1) {
                    result = param.substring(param.indexOf("=") + 1).trim();
                }
            }
        }
        return result;
    }

    public String toString() {
        return this.scriptUrl;
    }

    @Override // com.github.markusbernhardt.proxy.selector.pac.PacScriptSource
    public boolean isScriptValid() {
        try {
            String script = getScriptContent();
            if (script == null || script.trim().length() == 0) {
                Logger.log(getClass(), Logger.LogLevel.DEBUG, "PAC script is empty. Skipping script!", new Object[0]);
                return false;
            }
            if (script.indexOf("FindProxyForURL") == -1) {
                Logger.log(getClass(), Logger.LogLevel.DEBUG, "PAC script entry point FindProxyForURL not found. Skipping script!", new Object[0]);
                return false;
            }
            return true;
        } catch (IOException e) {
            Logger.log(getClass(), Logger.LogLevel.DEBUG, "File reading error: {}", e);
            return false;
        }
    }
}
