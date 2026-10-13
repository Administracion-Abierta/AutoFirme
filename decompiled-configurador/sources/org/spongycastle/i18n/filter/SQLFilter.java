package org.spongycastle.i18n.filter;

import org.spongycastle.asn1.eac.EACTags;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/i18n/filter/SQLFilter.class */
public class SQLFilter implements Filter {
    @Override // org.spongycastle.i18n.filter.Filter
    public String doFilter(String input) {
        StringBuffer buf = new StringBuffer(input);
        int i = 0;
        while (i < buf.length()) {
            char ch = buf.charAt(i);
            switch (ch) {
                case '\n':
                    buf.replace(i, i + 1, "\\n");
                    i++;
                    break;
                case '\r':
                    buf.replace(i, i + 1, "\\r");
                    i++;
                    break;
                case '\"':
                    buf.replace(i, i + 1, "\\\"");
                    i++;
                    break;
                case EACTags.INTERCHANGE_CONTROL /* 39 */:
                    buf.replace(i, i + 1, "\\'");
                    i++;
                    break;
                case '-':
                    buf.replace(i, i + 1, "\\-");
                    i++;
                    break;
                case '/':
                    buf.replace(i, i + 1, "\\/");
                    i++;
                    break;
                case ';':
                    buf.replace(i, i + 1, "\\;");
                    i++;
                    break;
                case '=':
                    buf.replace(i, i + 1, "\\=");
                    i++;
                    break;
                case '\\':
                    buf.replace(i, i + 1, "\\\\");
                    i++;
                    break;
            }
            i++;
        }
        return buf.toString();
    }

    @Override // org.spongycastle.i18n.filter.Filter
    public String doFilterUrl(String input) {
        return doFilter(input);
    }
}
