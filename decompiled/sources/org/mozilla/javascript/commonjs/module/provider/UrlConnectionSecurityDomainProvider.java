package org.mozilla.javascript.commonjs.module.provider;

import java.net.URLConnection;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/commonjs/module/provider/UrlConnectionSecurityDomainProvider.class */
public interface UrlConnectionSecurityDomainProvider {
    Object getSecurityDomain(URLConnection uRLConnection);
}
