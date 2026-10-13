package org.apache.xml.security.stax.impl.resourceResolvers;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.ResourceResolver;
import org.apache.xml.security.stax.ext.ResourceResolverLookup;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/resourceResolvers/ResolverHttp.class */
public class ResolverHttp implements ResourceResolver, ResourceResolverLookup {
    private static Proxy proxy;
    private String uri;
    private String baseURI;
    private Pattern pattern = Pattern.compile("^http[s]?://.*");

    public ResolverHttp() {
    }

    public ResolverHttp(String uri, String baseURI) {
        this.uri = uri;
        this.baseURI = baseURI;
    }

    public static void setProxy(Proxy proxy2) {
        proxy = proxy2;
    }

    @Override // org.apache.xml.security.stax.ext.ResourceResolverLookup
    public ResourceResolverLookup canResolve(String uri, String baseURI) {
        if (uri == null) {
            return null;
        }
        if (this.pattern.matcher(uri).matches() || (baseURI != null && this.pattern.matcher(baseURI).matches())) {
            return this;
        }
        return null;
    }

    @Override // org.apache.xml.security.stax.ext.ResourceResolverLookup
    public ResourceResolver newInstance(String uri, String baseURI) {
        return new ResolverHttp(uri, baseURI);
    }

    @Override // org.apache.xml.security.stax.ext.ResourceResolver
    public boolean isSameDocumentReference() {
        return false;
    }

    @Override // org.apache.xml.security.stax.ext.ResourceResolver
    public boolean matches(XMLSecStartElement xmlSecStartElement) {
        return false;
    }

    @Override // org.apache.xml.security.stax.ext.ResourceResolver
    public InputStream getInputStreamFromExternalReference() throws XMLSecurityException {
        URI tmp;
        HttpURLConnection urlConnection;
        try {
            if (this.baseURI == null || this.baseURI.length() == 0) {
                tmp = new URI(this.uri);
            } else {
                tmp = new URI(this.baseURI).resolve(this.uri);
            }
            if (tmp.getFragment() != null) {
                tmp = new URI(tmp.getScheme(), tmp.getSchemeSpecificPart(), null);
            }
            URL url = tmp.toURL();
            if (proxy != null) {
                urlConnection = (HttpURLConnection) url.openConnection(proxy);
            } else {
                urlConnection = (HttpURLConnection) url.openConnection();
            }
            return urlConnection.getInputStream();
        } catch (IOException | URISyntaxException e) {
            throw new XMLSecurityException(e);
        }
    }
}
