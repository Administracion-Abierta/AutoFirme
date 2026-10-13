package org.mozilla.javascript.commonjs.module.provider;

import java.net.URLConnection;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/commonjs/module/provider/UrlConnectionExpiryCalculator.class */
public interface UrlConnectionExpiryCalculator {
    long calculateExpiry(URLConnection uRLConnection);
}
