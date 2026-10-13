package com.ctc.wstx.io;

import com.ctc.wstx.util.URLUtil;
import java.io.IOException;
import java.net.URL;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/io/SystemId.class */
public class SystemId {
    protected URL mURL;
    protected String mSystemId;

    protected SystemId(String systemId, URL url) {
        if (systemId == null && url == null) {
            throw new IllegalArgumentException("Can not pass null for both systemId and url");
        }
        this.mSystemId = systemId;
        this.mURL = url;
    }

    public static SystemId construct(String systemId) {
        if (systemId == null) {
            return null;
        }
        return new SystemId(systemId, null);
    }

    public static SystemId construct(URL url) {
        if (url == null) {
            return null;
        }
        return new SystemId(null, url);
    }

    public static SystemId construct(String systemId, URL url) {
        if (systemId == null && url == null) {
            return null;
        }
        return new SystemId(systemId, url);
    }

    public URL asURL() throws IOException {
        if (this.mURL == null) {
            this.mURL = URLUtil.urlFromSystemId(this.mSystemId);
        }
        return this.mURL;
    }

    public boolean hasResolvedURL() {
        return this.mURL != null;
    }

    public String toString() {
        if (this.mSystemId == null) {
            this.mSystemId = this.mURL.toExternalForm();
        }
        return this.mSystemId;
    }
}
