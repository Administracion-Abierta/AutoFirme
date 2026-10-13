package org.mozilla.javascript.commonjs.module.provider;

import com.twelvemonkeys.lang.DateUtil;
import java.io.Serializable;
import java.net.URLConnection;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/commonjs/module/provider/DefaultUrlConnectionExpiryCalculator.class */
public class DefaultUrlConnectionExpiryCalculator implements UrlConnectionExpiryCalculator, Serializable {
    private static final long serialVersionUID = 1;
    private final long relativeExpiry;

    public DefaultUrlConnectionExpiryCalculator() {
        this(DateUtil.MINUTE);
    }

    public DefaultUrlConnectionExpiryCalculator(long relativeExpiry) {
        if (relativeExpiry < 0) {
            throw new IllegalArgumentException("relativeExpiry < 0");
        }
        this.relativeExpiry = relativeExpiry;
    }

    @Override // org.mozilla.javascript.commonjs.module.provider.UrlConnectionExpiryCalculator
    public long calculateExpiry(URLConnection urlConnection) {
        return System.currentTimeMillis() + this.relativeExpiry;
    }
}
