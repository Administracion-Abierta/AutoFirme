package com.github.markusbernhardt.proxy.search.browser.ie;

import com.github.markusbernhardt.proxy.util.UriFilter;
import java.net.URI;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/browser/ie/IELocalByPassFilter.class */
public class IELocalByPassFilter implements UriFilter {
    @Override // com.github.markusbernhardt.proxy.util.UriFilter
    public boolean accept(URI uri) {
        String host;
        return (uri == null || (host = uri.getAuthority()) == null || host.contains(".")) ? false : true;
    }
}
