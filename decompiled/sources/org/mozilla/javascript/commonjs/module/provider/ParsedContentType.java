package org.mozilla.javascript.commonjs.module.provider;

import es.gob.afirma.keystores.filters.PolicyIdFilter;
import java.io.Serializable;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/commonjs/module/provider/ParsedContentType.class */
public final class ParsedContentType implements Serializable {
    private static final long serialVersionUID = 1;
    private final String contentType;
    private final String encoding;

    public ParsedContentType(String mimeType) {
        String contentType = null;
        String encoding = null;
        if (mimeType != null) {
            StringTokenizer tok = new StringTokenizer(mimeType, PolicyIdFilter.OID_SEPARATOR);
            if (tok.hasMoreTokens()) {
                contentType = tok.nextToken().trim();
                while (tok.hasMoreTokens()) {
                    String param = tok.nextToken().trim();
                    if (param.startsWith("charset=")) {
                        encoding = param.substring(8).trim();
                        int l = encoding.length();
                        if (l <= 0) {
                            break;
                        }
                        encoding = encoding.charAt(0) == '\"' ? encoding.substring(1) : encoding;
                        if (encoding.charAt(l - 1) != '\"') {
                            break;
                        }
                        encoding = encoding.substring(0, l - 1);
                        break;
                    }
                }
            }
        }
        this.contentType = contentType;
        this.encoding = encoding;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getEncoding() {
        return this.encoding;
    }
}
