package org.mozilla.javascript.commonjs.module.provider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/commonjs/module/provider/UrlModuleSourceProvider.class */
public class UrlModuleSourceProvider extends ModuleSourceProviderBase {
    private static final long serialVersionUID = 1;
    private final Iterable<URI> privilegedUris;
    private final Iterable<URI> fallbackUris;
    private final UrlConnectionSecurityDomainProvider urlConnectionSecurityDomainProvider;
    private final UrlConnectionExpiryCalculator urlConnectionExpiryCalculator;

    public UrlModuleSourceProvider(Iterable<URI> privilegedUris, Iterable<URI> fallbackUris) {
        this(privilegedUris, fallbackUris, new DefaultUrlConnectionExpiryCalculator(), null);
    }

    public UrlModuleSourceProvider(Iterable<URI> privilegedUris, Iterable<URI> fallbackUris, UrlConnectionExpiryCalculator urlConnectionExpiryCalculator, UrlConnectionSecurityDomainProvider urlConnectionSecurityDomainProvider) {
        this.privilegedUris = privilegedUris;
        this.fallbackUris = fallbackUris;
        this.urlConnectionExpiryCalculator = urlConnectionExpiryCalculator;
        this.urlConnectionSecurityDomainProvider = urlConnectionSecurityDomainProvider;
    }

    @Override // org.mozilla.javascript.commonjs.module.provider.ModuleSourceProviderBase
    protected ModuleSource loadFromPrivilegedLocations(String moduleId, Object validator) throws URISyntaxException, IOException {
        return loadFromPathList(moduleId, validator, this.privilegedUris);
    }

    @Override // org.mozilla.javascript.commonjs.module.provider.ModuleSourceProviderBase
    protected ModuleSource loadFromFallbackLocations(String moduleId, Object validator) throws URISyntaxException, IOException {
        return loadFromPathList(moduleId, validator, this.fallbackUris);
    }

    private ModuleSource loadFromPathList(String moduleId, Object validator, Iterable<URI> paths) throws URISyntaxException, IOException {
        if (paths == null) {
            return null;
        }
        for (URI path : paths) {
            ModuleSource moduleSource = loadFromUri(path.resolve(moduleId), path, validator);
            if (moduleSource != null) {
                return moduleSource;
            }
        }
        return null;
    }

    @Override // org.mozilla.javascript.commonjs.module.provider.ModuleSourceProviderBase
    protected ModuleSource loadFromUri(URI uri, URI base, Object validator) throws URISyntaxException, IOException {
        URI fullUri = new URI(uri + ".js");
        ModuleSource source = loadFromActualUri(fullUri, base, validator);
        return source != null ? source : loadFromActualUri(uri, base, validator);
    }

    protected ModuleSource loadFromActualUri(URI uri, URI base, Object validator) throws IOException {
        URLValidator applicableValidator;
        URL url = new URL(base == null ? null : base.toURL(), uri.toString());
        long request_time = System.currentTimeMillis();
        URLConnection urlConnection = openUrlConnection(url);
        if (validator instanceof URLValidator) {
            URLValidator uriValidator = (URLValidator) validator;
            applicableValidator = uriValidator.appliesTo(uri) ? uriValidator : null;
        } else {
            applicableValidator = null;
        }
        if (applicableValidator != null) {
            applicableValidator.applyConditionals(urlConnection);
        }
        try {
            urlConnection.connect();
            if (applicableValidator != null && !applicableValidator.updateValidator(urlConnection, request_time, this.urlConnectionExpiryCalculator)) {
                close(urlConnection);
                return NOT_MODIFIED;
            }
            return new ModuleSource(getReader(urlConnection), getSecurityDomain(urlConnection), uri, base, new URLValidator(uri, urlConnection, request_time, this.urlConnectionExpiryCalculator));
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e2) {
            close(urlConnection);
            throw e2;
        } catch (RuntimeException e3) {
            close(urlConnection);
            throw e3;
        }
    }

    private static Reader getReader(URLConnection urlConnection) throws IOException {
        return new InputStreamReader(urlConnection.getInputStream(), getCharacterEncoding(urlConnection));
    }

    private static String getCharacterEncoding(URLConnection urlConnection) {
        ParsedContentType pct = new ParsedContentType(urlConnection.getContentType());
        String encoding = pct.getEncoding();
        if (encoding != null) {
            return encoding;
        }
        String contentType = pct.getContentType();
        if (contentType != null && contentType.startsWith("text/")) {
            return "8859_1";
        }
        return "utf-8";
    }

    private Object getSecurityDomain(URLConnection urlConnection) {
        if (this.urlConnectionSecurityDomainProvider == null) {
            return null;
        }
        return this.urlConnectionSecurityDomainProvider.getSecurityDomain(urlConnection);
    }

    private void close(URLConnection urlConnection) {
        try {
            urlConnection.getInputStream().close();
        } catch (IOException e) {
            onFailedClosingUrlConnection(urlConnection, e);
        }
    }

    protected void onFailedClosingUrlConnection(URLConnection urlConnection, IOException cause) {
    }

    protected URLConnection openUrlConnection(URL url) throws IOException {
        return url.openConnection();
    }

    @Override // org.mozilla.javascript.commonjs.module.provider.ModuleSourceProviderBase
    protected boolean entityNeedsRevalidation(Object validator) {
        return !(validator instanceof URLValidator) || ((URLValidator) validator).entityNeedsRevalidation();
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/commonjs/module/provider/UrlModuleSourceProvider$URLValidator.class */
    private static class URLValidator implements Serializable {
        private static final long serialVersionUID = 1;
        private final URI uri;
        private final long lastModified;
        private final String entityTags;
        private long expiry;

        public URLValidator(URI uri, URLConnection urlConnection, long request_time, UrlConnectionExpiryCalculator urlConnectionExpiryCalculator) {
            this.uri = uri;
            this.lastModified = urlConnection.getLastModified();
            this.entityTags = getEntityTags(urlConnection);
            this.expiry = calculateExpiry(urlConnection, request_time, urlConnectionExpiryCalculator);
        }

        boolean updateValidator(URLConnection urlConnection, long request_time, UrlConnectionExpiryCalculator urlConnectionExpiryCalculator) throws IOException {
            boolean isResourceChanged = isResourceChanged(urlConnection);
            if (!isResourceChanged) {
                this.expiry = calculateExpiry(urlConnection, request_time, urlConnectionExpiryCalculator);
            }
            return isResourceChanged;
        }

        private boolean isResourceChanged(URLConnection urlConnection) throws IOException {
            if (urlConnection instanceof HttpURLConnection) {
                return ((HttpURLConnection) urlConnection).getResponseCode() == 304;
            }
            return this.lastModified != urlConnection.getLastModified();
        }

        private static long calculateExpiry(URLConnection urlConnection, long request_time, UrlConnectionExpiryCalculator urlConnectionExpiryCalculator) {
            if ("no-cache".equals(urlConnection.getHeaderField("Pragma"))) {
                return 0L;
            }
            String cacheControl = urlConnection.getHeaderField("Cache-Control");
            if (cacheControl != null) {
                if (cacheControl.indexOf("no-cache") != -1) {
                    return 0L;
                }
                int max_age = getMaxAge(cacheControl);
                if (-1 != max_age) {
                    long response_time = System.currentTimeMillis();
                    long apparent_age = Math.max(0L, response_time - urlConnection.getDate());
                    long corrected_received_age = Math.max(apparent_age, ((long) urlConnection.getHeaderFieldInt("Age", 0)) * 1000);
                    long response_delay = response_time - request_time;
                    long corrected_initial_age = corrected_received_age + response_delay;
                    long creation_time = response_time - corrected_initial_age;
                    return (((long) max_age) * 1000) + creation_time;
                }
            }
            long explicitExpiry = urlConnection.getHeaderFieldDate("Expires", -1L);
            if (explicitExpiry != -1) {
                return explicitExpiry;
            }
            if (urlConnectionExpiryCalculator == null) {
                return 0L;
            }
            return urlConnectionExpiryCalculator.calculateExpiry(urlConnection);
        }

        private static int getMaxAge(String cacheControl) {
            int eq;
            String strAge;
            int maxAgeIndex = cacheControl.indexOf("max-age");
            if (maxAgeIndex == -1 || (eq = cacheControl.indexOf(61, maxAgeIndex + 7)) == -1) {
                return -1;
            }
            int comma = cacheControl.indexOf(44, eq + 1);
            if (comma == -1) {
                strAge = cacheControl.substring(eq + 1);
            } else {
                strAge = cacheControl.substring(eq + 1, comma);
            }
            try {
                return Integer.parseInt(strAge);
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        private static String getEntityTags(URLConnection urlConnection) {
            List<String> etags = urlConnection.getHeaderFields().get("ETag");
            if (etags == null || etags.isEmpty()) {
                return null;
            }
            StringBuilder b = new StringBuilder();
            Iterator<String> it = etags.iterator();
            b.append(it.next());
            while (it.hasNext()) {
                b.append(", ").append(it.next());
            }
            return b.toString();
        }

        boolean appliesTo(URI uri) {
            return this.uri.equals(uri);
        }

        void applyConditionals(URLConnection urlConnection) {
            if (this.lastModified != 0) {
                urlConnection.setIfModifiedSince(this.lastModified);
            }
            if (this.entityTags != null && this.entityTags.length() > 0) {
                urlConnection.addRequestProperty("If-None-Match", this.entityTags);
            }
        }

        boolean entityNeedsRevalidation() {
            return System.currentTimeMillis() > this.expiry;
        }
    }
}
