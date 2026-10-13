package org.apache.xml.security.utils.resolver.implementations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/utils/resolver/implementations/ResolverDirectHTTP.class */
public class ResolverDirectHTTP extends ResourceResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) ResolverDirectHTTP.class);
    private static final String[] properties = {"http.proxy.host", "http.proxy.port", "http.proxy.username", "http.proxy.password", "http.basic.username", "http.basic.password"};
    private static final int HttpProxyHost = 0;
    private static final int HttpProxyPort = 1;
    private static final int HttpProxyUser = 2;
    private static final int HttpProxyPass = 3;
    private static final int HttpBasicUser = 4;
    private static final int HttpBasicPass = 5;
    private final Map<String, String> resolverProperties;

    public ResolverDirectHTTP() {
        this.resolverProperties = Collections.emptyMap();
    }

    public ResolverDirectHTTP(Map<String, String> resolverProperties) {
        this.resolverProperties = Collections.unmodifiableMap(resolverProperties != null ? resolverProperties : Collections.emptyMap());
    }

    /* JADX WARN: Code duplicated, block: B:29:0x014f A[Catch: Throwable -> 0x0159, all -> 0x0162, IOException | IllegalArgumentException | URISyntaxException -> 0x016e, TryCatch #1 {all -> 0x0162, blocks: (B:12:0x00b3, B:13:0x00bd, B:14:0x00ca, B:16:0x00d7, B:17:0x00eb, B:19:0x012e, B:25:0x0147, B:29:0x014f, B:31:0x0158, B:34:0x0161), top: B:43:0x00b3, outer: #0 }] */
    @Override // org.apache.xml.security.utils.resolver.ResourceResolverSpi
    public XMLSignatureInput engineResolveURI(ResourceResolverContext context) throws ResourceResolverException {
        try {
            URI uriNew = getNewURI(context.uriToResolve, context.baseUri);
            URL url = uriNew.toURL();
            URLConnection urlConnection = openConnection(url, context);
            String auth = urlConnection.getHeaderField("WWW-Authenticate");
            if (auth != null && auth.startsWith("Basic")) {
                String user = getProperty(context, properties[4]);
                String pass = getProperty(context, properties[5]);
                if (user != null && pass != null) {
                    urlConnection = openConnection(url, context);
                    String password = user + ":" + pass;
                    String encodedPassword = XMLUtils.encodeToString(password.getBytes(StandardCharsets.ISO_8859_1));
                    urlConnection.setRequestProperty("Authorization", "Basic " + encodedPassword);
                }
            }
            String mimeType = urlConnection.getHeaderField("Content-Type");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                InputStream inputStream = urlConnection.getInputStream();
                Throwable th = null;
                try {
                    try {
                        byte[] buf = new byte[4096];
                        int summarized = 0;
                        while (true) {
                            int read = inputStream.read(buf);
                            if (read < 0) {
                                break;
                            }
                            baos.write(buf, 0, read);
                            summarized += read;
                            if (inputStream != null) {
                                $closeResource(th, inputStream);
                            }
                            throw th;
                        }
                        LOG.debug("Fetched {} bytes from URI {}", Integer.valueOf(summarized), uriNew.toString());
                        XMLSignatureInput result = new XMLSignatureInput(baos.toByteArray());
                        result.setSecureValidation(context.secureValidation);
                        result.setSourceURI(uriNew.toString());
                        result.setMIMEType(mimeType);
                        if (inputStream != null) {
                            $closeResource(null, inputStream);
                        }
                        $closeResource(null, baos);
                        return result;
                    } catch (Throwable th2) {
                        th = th2;
                        throw th2;
                    }
                } catch (Throwable th3) {
                    if (inputStream != null) {
                        $closeResource(th, inputStream);
                    }
                    throw th3;
                }
            } catch (Throwable th4) {
                $closeResource(null, baos);
                throw th4;
            }
        } catch (IOException | IllegalArgumentException | URISyntaxException ex) {
            throw new ResourceResolverException(ex, context.uriToResolve, context.baseUri, "generic.EmptyMessage");
        }
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) throws Exception {
        if (x0 == null) {
            x1.close();
            return;
        }
        try {
            x1.close();
        } catch (Throwable th) {
            x0.addSuppressed(th);
        }
    }

    private URLConnection openConnection(URL url, ResourceResolverContext context) throws IOException {
        URLConnection urlConnection;
        String proxyHostProp = getProperty(context, properties[0]);
        String proxyPortProp = getProperty(context, properties[1]);
        String proxyUser = getProperty(context, properties[2]);
        String proxyPass = getProperty(context, properties[3]);
        Proxy proxy = null;
        if (proxyHostProp != null && proxyPortProp != null) {
            int port = Integer.parseInt(proxyPortProp);
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHostProp, port));
        }
        if (proxy != null) {
            urlConnection = url.openConnection(proxy);
            if (proxyUser != null && proxyPass != null) {
                String password = proxyUser + ":" + proxyPass;
                String authString = "Basic " + XMLUtils.encodeToString(password.getBytes(StandardCharsets.ISO_8859_1));
                urlConnection.setRequestProperty("Proxy-Authorization", authString);
            }
        } else {
            urlConnection = url.openConnection();
        }
        return urlConnection;
    }

    @Override // org.apache.xml.security.utils.resolver.ResourceResolverSpi
    public boolean engineCanResolveURI(ResourceResolverContext context) {
        if (context.uriToResolve == null) {
            LOG.debug("quick fail, uri == null");
            return false;
        }
        if (context.uriToResolve.isEmpty() || context.uriToResolve.charAt(0) == '#') {
            LOG.debug("quick fail for empty URIs and local ones");
            return false;
        }
        LOG.debug("I was asked whether I can resolve {}", context.uriToResolve);
        if (context.uriToResolve.startsWith("http:") || (context.baseUri != null && context.baseUri.startsWith("http:"))) {
            LOG.debug("I state that I can resolve {}", context.uriToResolve);
            return true;
        }
        LOG.debug("I state that I can't resolve {}", context.uriToResolve);
        return false;
    }

    private static URI getNewURI(String uri, String baseURI) throws URISyntaxException {
        URI newUri;
        if (baseURI == null || baseURI.length() == 0) {
            newUri = new URI(uri);
        } else {
            newUri = new URI(baseURI).resolve(uri);
        }
        if (newUri.getFragment() != null) {
            return new URI(newUri.getScheme(), newUri.getSchemeSpecificPart(), null);
        }
        return newUri;
    }

    private String getProperty(ResourceResolverContext context, String propertyName) {
        if (this.resolverProperties.containsKey(propertyName)) {
            return this.resolverProperties.get(propertyName);
        }
        return context.getProperties().get(propertyName);
    }
}
