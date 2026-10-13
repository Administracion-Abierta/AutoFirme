package es.gob.afirma.core.misc.http;

import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.Platform;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import org.ini4j.Config;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/misc/http/UrlHttpManagerImpl.class */
public class UrlHttpManagerImpl implements UrlHttpManager {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    public static final String JAVA_PARAM_DISABLE_SSL_CHECKS = "disableSslChecks";
    public static final String JAVA_PARAM_SECURE_DOMAINS_LIST = "secureDomainsList";
    public static final int DEFAULT_TIMEOUT = -1;
    private static final String URN_SEPARATOR = ":";
    private static final String PROT_SEPARATOR = "://";
    private static final String ACCEPT = "Accept";

    static {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
    }

    protected UrlHttpManagerImpl() {
    }

    @Override // es.gob.afirma.core.misc.http.UrlHttpManager
    public byte[] readUrl(String str, UrlHttpMethod urlHttpMethod) throws IOException {
        return readUrl(str, -1, (String) null, (String) null, urlHttpMethod);
    }

    @Override // es.gob.afirma.core.misc.http.UrlHttpManager
    public byte[] readUrl(String str, UrlHttpMethod urlHttpMethod, HttpErrorProcessor httpErrorProcessor) throws IOException {
        return readUrl(str, -1, (String) null, (String) null, urlHttpMethod, httpErrorProcessor);
    }

    @Override // es.gob.afirma.core.misc.http.UrlHttpManager
    public byte[] readUrl(String str, UrlHttpMethod urlHttpMethod, HttpErrorProcessor httpErrorProcessor, SSLConfig sSLConfig) throws IOException {
        return readUrl(str, -1, null, null, urlHttpMethod, httpErrorProcessor, sSLConfig);
    }

    @Override // es.gob.afirma.core.misc.http.UrlHttpManager
    public byte[] readUrl(String str, int i, String str2, String str3, UrlHttpMethod urlHttpMethod) throws IOException {
        return readUrl(str, i, urlHttpMethod, buildHeaders(str2, str3));
    }

    private static Properties buildHeaders(String str, String str2) {
        Properties properties = new Properties();
        if (str != null) {
            properties.setProperty("Content-Type", str);
        }
        if (str2 != null) {
            properties.setProperty(ACCEPT, str2);
        }
        return properties;
    }

    @Override // es.gob.afirma.core.misc.http.UrlHttpManager
    public byte[] readUrl(String str, int i, String str2, String str3, UrlHttpMethod urlHttpMethod, HttpErrorProcessor httpErrorProcessor) throws IOException {
        return readUrl(str, i, urlHttpMethod, buildHeaders(str2, str3), httpErrorProcessor);
    }

    @Override // es.gob.afirma.core.misc.http.UrlHttpManager
    public byte[] readUrl(String str, int i, UrlHttpMethod urlHttpMethod, Properties properties) throws IOException {
        return readUrl(str, i, urlHttpMethod, properties, (HttpErrorProcessor) null);
    }

    @Override // es.gob.afirma.core.misc.http.UrlHttpManager
    public byte[] readUrl(String str, int i, UrlHttpMethod urlHttpMethod, Properties properties, HttpErrorProcessor httpErrorProcessor) throws IOException {
        return readUrl(str, i, urlHttpMethod, properties, httpErrorProcessor, (SSLConfig) null);
    }

    @Override // es.gob.afirma.core.misc.http.UrlHttpManager
    public byte[] readUrl(String str, int i, String str2, String str3, UrlHttpMethod urlHttpMethod, HttpErrorProcessor httpErrorProcessor, SSLConfig sSLConfig) throws IOException {
        return readUrl(str, i, urlHttpMethod, buildHeaders(str2, str3), httpErrorProcessor, sSLConfig);
    }

    @Override // es.gob.afirma.core.misc.http.UrlHttpManager
    public byte[] readUrl(String str, int i, UrlHttpMethod urlHttpMethod, Properties properties, HttpErrorProcessor httpErrorProcessor, SSLConfig sSLConfig) throws IOException {
        String password;
        String strEncode;
        String str2;
        HttpURLConnection httpURLConnection;
        if (str == null) {
            throw new IllegalArgumentException("La URL a leer no puede ser nula");
        }
        URLName uRLName = new URLName(str);
        if (uRLName.getUsername() != null || uRLName.getPassword() != null) {
            if (uRLName.getUsername() != null && uRLName.getPassword() != null) {
                password = uRLName.getUsername() + URN_SEPARATOR + uRLName.getPassword();
            } else if (uRLName.getUsername() != null) {
                password = uRLName.getUsername();
            } else {
                password = uRLName.getPassword();
            }
            strEncode = Base64.encode(password.getBytes());
            str2 = uRLName.getProtocol() + PROT_SEPARATOR + uRLName.getHost() + (uRLName.getPort() != -1 ? URN_SEPARATOR + Integer.toString(uRLName.getPort()) : "") + "/" + (uRLName.getFile() != null ? uRLName.getFile() : "");
        } else {
            str2 = str;
            strEncode = null;
        }
        String strNextToken = null;
        String strNextToken2 = null;
        if (UrlHttpMethod.POST.equals(urlHttpMethod) || UrlHttpMethod.PUT.equals(urlHttpMethod)) {
            StringTokenizer stringTokenizer = new StringTokenizer(str2, Config.DEFAULT_GLOBAL_SECTION_NAME);
            strNextToken2 = stringTokenizer.nextToken();
            if (str2.contains(Config.DEFAULT_GLOBAL_SECTION_NAME)) {
                strNextToken = stringTokenizer.nextToken();
            }
        }
        URL url = new URL(strNextToken2 != null ? strNextToken2 : str2);
        try {
            if (Platform.OS.ANDROID.equals(Platform.getOS()) || isLocal(url)) {
                httpURLConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
            } else {
                httpURLConnection = (HttpURLConnection) url.openConnection();
            }
            boolean z = sSLConfig == null && Boolean.parseBoolean(System.getProperty(JAVA_PARAM_DISABLE_SSL_CHECKS, "false"));
            boolean zCheckIsSecureDomain = checkIsSecureDomain(url);
            if (httpURLConnection instanceof HttpsURLConnection) {
                if (z || zCheckIsSecureDomain) {
                    try {
                        SslSecurityManager.disableSslChecks((HttpsURLConnection) httpURLConnection);
                        LOGGER.info("Deshabilitada la comprobacion SSL para el acceso al dominio: " + url.getHost());
                    } catch (Exception e) {
                        LOGGER.warning("No se ha podido ajustar la confianza SSL, es posible que no se pueda completar la conexion: " + e);
                    }
                } else if (sSLConfig != null) {
                    SslSecurityManager.setSslSecurity((HttpsURLConnection) httpURLConnection, sSLConfig);
                }
            }
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDefaultUseCaches(false);
            httpURLConnection.setRequestMethod(urlHttpMethod.toString());
            Properties properties2 = new Properties();
            if (properties != null) {
                properties2.putAll(properties);
            }
            if (strEncode != null && !properties2.containsKey("Authorization")) {
                httpURLConnection.addRequestProperty("Authorization", "Basic " + strEncode);
            }
            if (!properties2.containsKey(ACCEPT)) {
                httpURLConnection.addRequestProperty(ACCEPT, "*/*");
            }
            if (!properties2.containsKey("Connection")) {
                httpURLConnection.addRequestProperty("Connection", "keep-alive");
            }
            if (!properties2.containsKey("Host")) {
                httpURLConnection.addRequestProperty("Host", url.getHost());
            }
            if (!properties2.containsKey("Origin")) {
                httpURLConnection.addRequestProperty("Origin", url.getProtocol() + PROT_SEPARATOR + url.getHost());
            }
            for (Map.Entry entry : properties2.entrySet()) {
                httpURLConnection.addRequestProperty((String) entry.getKey(), (String) entry.getValue());
            }
            if (strNextToken != null) {
                httpURLConnection.setRequestProperty("Content-Length", String.valueOf(strNextToken.getBytes(StandardCharsets.UTF_8).length));
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                Throwable th = null;
                try {
                    try {
                        outputStream.write(strNextToken.getBytes(StandardCharsets.UTF_8));
                        if (outputStream != null) {
                            if (0 != 0) {
                                try {
                                    outputStream.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                outputStream.close();
                            }
                        }
                    } catch (Throwable th3) {
                        if (outputStream != null) {
                            if (th != null) {
                                try {
                                    outputStream.close();
                                } catch (Throwable th4) {
                                    th.addSuppressed(th4);
                                }
                            } else {
                                outputStream.close();
                            }
                        }
                        throw th3;
                    }
                } catch (Throwable th5) {
                    th = th5;
                    throw th5;
                }
            }
            if (i != -1) {
                httpURLConnection.setConnectTimeout(i);
            }
            httpURLConnection.connect();
            int responseCode = httpURLConnection.getResponseCode();
            String string = Integer.toString(responseCode);
            if (string.startsWith(TlbConst.TYPELIB_MINOR_VERSION_WORD) || string.startsWith(TlbConst.TYPELIB_MINOR_VERSION_OFFICE)) {
                throw new HttpError(responseCode, httpURLConnection.getResponseMessage(), AOUtil.getDataFromInputStream(httpURLConnection.getErrorStream()), str2);
            }
            InputStream inputStream = httpURLConnection.getInputStream();
            Throwable th6 = null;
            try {
                try {
                    byte[] dataFromInputStream = AOUtil.getDataFromInputStream(inputStream);
                    if (inputStream != null) {
                        if (0 != 0) {
                            try {
                                inputStream.close();
                            } catch (Throwable th7) {
                                th6.addSuppressed(th7);
                            }
                        } else {
                            inputStream.close();
                        }
                    }
                    return dataFromInputStream;
                } catch (Throwable th8) {
                    if (inputStream != null) {
                        if (th6 != null) {
                            try {
                                inputStream.close();
                            } catch (Throwable th9) {
                                th6.addSuppressed(th9);
                            }
                        } else {
                            inputStream.close();
                        }
                    }
                    throw th8;
                }
            } catch (Throwable th10) {
                th6 = th10;
                throw th10;
            }
        } catch (IOException e2) {
            if (httpErrorProcessor != null) {
                LOGGER.log(Level.WARNING, "Fallo la conexion pero intentamos recuperarla: " + e2);
                return httpErrorProcessor.processHttpError(e2, this, str2, i, urlHttpMethod, properties);
            }
            throw e2;
        }
    }

    private static boolean isLocal(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("La URL no puede ser nula");
        }
        try {
            return InetAddress.getByName(url.getHost()).isLoopbackAddress();
        } catch (Exception e) {
            LOGGER.warning("Error comprobando si una URL es el bucle local: " + e);
            return false;
        }
    }

    private static boolean checkIsSecureDomain(URL url) {
        String property = System.getProperty("secureDomainsList");
        if (property != null && !property.isEmpty()) {
            String host = url.getHost();
            for (String str : property.split(",")) {
                String strReplace = str.replace("*", "");
                if (str.startsWith("*") && str.endsWith("*")) {
                    if (host.contains(strReplace)) {
                        return true;
                    }
                } else if (str.startsWith("*")) {
                    if (host.endsWith(strReplace)) {
                        return true;
                    }
                } else if (str.endsWith("*")) {
                    if (host.startsWith(strReplace)) {
                        return true;
                    }
                } else if (host.equals(strReplace)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
