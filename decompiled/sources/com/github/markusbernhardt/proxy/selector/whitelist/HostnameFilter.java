package com.github.markusbernhardt.proxy.selector.whitelist;

import com.github.markusbernhardt.proxy.util.UriFilter;
import java.net.URI;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/whitelist/HostnameFilter.class */
public class HostnameFilter implements UriFilter {
    private static final String PROTOCOL_ENDING = "://";
    private String matchTo;
    private String protocolFilter;
    private Mode mode;

    /* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/selector/whitelist/HostnameFilter$Mode.class */
    public enum Mode {
        BEGINS_WITH,
        ENDS_WITH,
        REGEX
    }

    public HostnameFilter(Mode mode, String matchTo) {
        this.mode = mode;
        this.matchTo = matchTo.toLowerCase();
        extractProtocolFilter();
    }

    private void extractProtocolFilter() {
        int protocolIndex = this.matchTo.indexOf(PROTOCOL_ENDING);
        if (protocolIndex != -1) {
            this.protocolFilter = this.matchTo.substring(0, protocolIndex);
            this.matchTo = this.matchTo.substring(protocolIndex + PROTOCOL_ENDING.length());
        }
    }

    @Override // com.github.markusbernhardt.proxy.util.UriFilter
    public boolean accept(URI uri) {
        if (uri == null || uri.getAuthority() == null || !isProtocolMatching(uri)) {
            return false;
        }
        String host = uri.getAuthority();
        int index = host.indexOf(58);
        int index2 = host.lastIndexOf(93);
        if (index != -1 && index2 < index) {
            host = host.substring(0, index);
        }
        switch (this.mode) {
            case BEGINS_WITH:
                return host.toLowerCase().startsWith(this.matchTo);
            case ENDS_WITH:
                return host.toLowerCase().endsWith(this.matchTo);
            case REGEX:
                return host.toLowerCase().matches(this.matchTo);
            default:
                return false;
        }
    }

    private boolean isProtocolMatching(URI uri) {
        return this.protocolFilter == null || uri.getScheme() == null || uri.getScheme().equalsIgnoreCase(this.protocolFilter);
    }
}
